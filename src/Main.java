import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Connection connection = null;
        Statement st = null;
        ResultSet rs = null;
        Scanner scanner = new Scanner(System.in);
        Locale.setDefault(Locale.US);

        System.out.println("Enter a value for the media:  ");
        int periodAverage = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter a value for factor:  ");
        double Factor = scanner.nextDouble();

        try {
            connection = DB.getConnection();
            st = connection.createStatement();
            rs = st.executeQuery("SELECT Date, Open, Highest, Lowest, " +
                    "Closing FROM dollar_quotes_db.dolar ORDER BY Date DESC");

            DateTimeFormatter sdfInput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter sdfOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            List<OHLC> quotes = new ArrayList<>();
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                String dateStr = rs.getString("Date");
                LocalDateTime localDateTime = LocalDateTime.parse(dateStr, sdfInput);
                double Open = rs.getDouble("Open");
                double Highest = rs.getDouble("Highest");
                double Lowest = rs.getDouble("Lowest");
                double Closing = rs.getDouble("Closing");
                String formattedDate = localDateTime.format(sdfOutput);
                System.out.printf("Line %d: %s | %.2f | %.2f | %.2f | %.2f%n",
                        rowCount, formattedDate, Open, Highest, Lowest, Closing);

                OHLC ohlc = new OHLC(localDateTime, Open, Highest, Lowest, Closing);
                quotes.add(ohlc);

            }
            System.out.println("Total line processed: " + rowCount);
            OHLCManager manager = new OHLCManager(quotes);
            double movingAverage = manager.calculateMovingAverage(periodAverage);
            double trueRange = manager.calculateTrueRange(periodAverage);
            double[] limits = manager.calculateAllLimits(movingAverage, trueRange, Factor);

            System.out.printf("Moving Average (115 period): %.2f%n", movingAverage);
            System.out.printf("True Range: %.2f%n", trueRange);
            System.out.println("Limits:");
            for (int i = 0; i < limits.length; i++) {
                System.out.printf("Limit[%d]: %.2f%n", i, limits[i]);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
            DB.closeConnection();
        }
    }
}