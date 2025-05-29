import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OHLC {

    private LocalDateTime date;
    private double Open;
    private double Highest;
    private double Lowest;
    private double Closing;

    public OHLC(LocalDateTime date, double open, double highest, double lowest, double closing) {
        this.date = date;
        Open = open;
        Highest = highest;
        Lowest = lowest;
        Closing = closing;
    }

      public LocalDateTime getDate() {
        return date;
    }

    public double getOpen() {
        return Open;
    }

    public double getHighest() {
        return Highest;
    }

    public double getLowest() {
        return Lowest;
    }

    public double getClosing() {
        return Closing;
    }

}
