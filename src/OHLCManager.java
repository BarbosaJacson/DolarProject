import java.util.ArrayList;
import java.util.List;

public class OHLCManager {
    private double movingAverage;
    private double trueRange;
    private int window;
    private double factor;

    public OHLCManager(int window, double factor) {
        this.window = window;
        this.factor = factor;
    }

    private List<OHLC> list;

    public OHLCManager(List<OHLC> list) {
        this.list = list;
    }

    public OHLCManager() {
    }

    public List<OHLC> getList() {
        return new ArrayList<>(list); // Defensive copy;
    }

    public double calculateMovingAverage(int window) {
        if (list.size() < window) {
            return 0.0;
        } else {
            double sum = 0.0;
            for (int i = 0; i < window; i++) {
                sum += list.get(i).getClosing();
            }
            return sum / window;
        }
    }

    public double calculateTrueRange(int window) {
        if (list.size() <= window) {
            throw new IllegalArgumentException("The list has fewer elements than specified");
        } else {

            double sumTrueRange = 0.0;

            for (int i = 1; i < window; i++) {

                OHLC current = list.get(i);
                OHLC previous = list.get(i - 1);

                double highLowRange = current.getHighest() - current.getLowest();
                double highPrevCloseRange = Math.abs(current.getHighest() - previous.getClosing());
                double lowPrevCloseRange = Math.abs(current.getLowest() - previous.getClosing());

                double trueRange = Math.max(highLowRange, Math.max(highPrevCloseRange, lowPrevCloseRange));

                sumTrueRange += trueRange;
            }
            return sumTrueRange / window;
        }
    }

    public void setParameters(int window, double factor) {
        this.window = window;
        this.factor = factor;
        this.movingAverage = calculateMovingAverage(window);
        this.trueRange = calculateTrueRange(window);
    }

    public double[] calculateAllLimits(double movingAverage, double trueRange, double factor) {
        double Lsc = movingAverage + (trueRange * factor);
        double Lic = movingAverage + trueRange;
        double Lsv = movingAverage - (trueRange * factor);
        double Liv = movingAverage - trueRange;


        return new double[]{Lsc, Lic, Lsv, Liv};
    }

    public List<Integer> calculateFilter(int firstValue, int secondValue, double movingAverage, double[] limits) {
        double MM = movingAverage;
        int limite = list.size() - (firstValue + secondValue);
        double lic = limits[1];
        double liv = limits[3];
        double lsc = limits[0];
        double lsv = limits[2];
        List<Integer> indicesComFiltroValido = new ArrayList<>();

        if (list.size() <= window) {
            throw new IllegalArgumentException("The list has fewer elements than specified");
        } else {
            for (int i = 0; i <= limite; i++) {
                int countB = 0;
                int countS = 0;
                int bFb = 0, bFs = 0; // ← corrigido

                OHLC current = list.get(i);
                double closed = current.getClosing();

                for (int j = 0; j < firstValue; j++) {
                    double prevOneClosed = list.get(i + j).getClosing();
                    if ((prevOneClosed > MM) && (closed > MM)) {
                        countB++;
                    }
                    if ((prevOneClosed < MM) && (closed < MM)) {
                        countS++;
                    }
                }

                if ((countB >= firstValue) || (countS >= firstValue)) {
                    for (int k = 0; k < secondValue; k++) {
                        double prevTwoClosed = list.get(i + firstValue + k).getClosing();
                        if ((lic < prevTwoClosed) && (prevTwoClosed < lsc)) bFb++;
                        if ((liv > prevTwoClosed) && (prevTwoClosed > lsv)) bFs++;
                    }

                    if (bFb >= secondValue || bFs >= secondValue) {
                        indicesComFiltroValido.add(i);
                    }
                }
            }
        }

        return indicesComFiltroValido;
    }

}





