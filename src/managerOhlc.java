import java.util.ArrayList;
import java.util.List;

public class managerOhlc {
    private double movingAverage;
    private double trueRange;
    private int window;
    private double factor;

    public managerOhlc(int window, double factor) {
        this.window = window;
        this.factor = factor;
    }

    private List<OHLC> list;

    public managerOhlc(List<OHLC> list) {
        this.list = list;
    }

    public managerOhlc() {
    }

    public List<OHLC> getList() {
        return new ArrayList<>(list); // Defensive copy;
    }
    public List<Integer> calculateFilter(int firstValue, int secondValue, int window, double factor) {
        int limite = list.size() - (firstValue + secondValue);
        List<Integer> indicesComFiltroValido = new ArrayList<>();
        List<double[]> calculatedData = new ArrayList<>();

        if (list.size() <= window) {
            throw new IllegalArgumentException("The list has fewer elements than specified");
        }

        for (int i = 0; i <= limite; i++) {
            double MM = 0.0, trueRange = 0.0;
            if (i >= window - 1) {
                double sumMM = 0.0, sumTrueRange = 0.0;
                for (int j = i - (window - 1); j <= i; j++) {
                    sumMM += list.get(j).getClosing();
                    if (j > i - (window - 1)) {
                        OHLC current = list.get(j);
                        OHLC previous = list.get(j - 1);
                        double highLowRange = current.getHighest() - current.getLowest();
                        double highPrevCloseRange = Math.abs(current.getHighest() - previous.getClosing());
                        double lowPrevCloseRange = Math.abs(current.getLowest() - previous.getClosing());
                        sumTrueRange += Math.max(highLowRange, Math.max(highPrevCloseRange, lowPrevCloseRange));
                    }
                }
                MM = sumMM / window;
                trueRange = sumTrueRange / (window - 1);
            } else {
                continue;
            }

            double Lsc = MM + (trueRange * factor);
            double Lic = MM + trueRange;
            double Lsv = MM - (trueRange * factor);
            double Liv = MM - trueRange;

            int countB = 0, countS = 0, bFb = 0, bFs = 0;
            OHLC current = list.get(i);
            double closed = current.getClosing();

            for (int j = 0; j < firstValue; j++) {
                double prevOneClosed = list.get(i + j).getClosing();
                if (prevOneClosed > MM && closed > MM) {
                    countB++;
                }
                if (prevOneClosed < MM && closed < MM) {
                    countS++;
                }
            }

            if (countB >= firstValue || countS >= firstValue) {
                for (int k = 0; k < secondValue; k++) {
                    double prevTwoClosed = list.get(i + firstValue + k).getClosing();
                    if (Lic < prevTwoClosed && prevTwoClosed < Lsc) bFb++;
                    if (Liv > prevTwoClosed && prevTwoClosed > Lsv) bFs++;
                }

                if (bFb >= secondValue || bFs >= secondValue) {
                    indicesComFiltroValido.add(i);
                    calculatedData.add(new double[]{MM, trueRange, Lsc, Lic, Lsv, Liv});
                }
            }
        }

        this.filteredData = calculatedData;
        this.filteredIndices = indicesComFiltroValido;
        return indicesComFiltroValido;
    }

    private List<double[]> filteredData = new ArrayList<>();
    private List<Integer> filteredIndices = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Filtered OHLC Data:\n");
        sb.append("Index |      Date      | Closing | MMAverage | TR |   Lsc   |   Lic   |   Lsv   |   Liv\n");
        sb.append("---------------------------------------------------------------\n");

        for (int i = 0; i < filteredIndices.size(); i++) {
            int index = filteredIndices.get(i);
            OHLC ohlc = list.get(index);
            double[] data = filteredData.get(i);
            sb.append(String.format("%d | %s | %.2f | %.2f | %.2f | %.2f | %.2f | %.2f | %.2f\n",
                    index, ohlc.getDate(), ohlc.getClosing(), data[0], data[1], data[2], data[3], data[4], data[5]));
        }

        return sb.toString();
    }
}