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
}

