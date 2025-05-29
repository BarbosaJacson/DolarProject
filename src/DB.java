import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DB {

   private static Connection connection=null;

    public static Connection getConnection() {
                if(connection==null){
                    try {
                        Properties properties = loadProperties();
                        String url = properties.getProperty("dburl");
                        connection = DriverManager.getConnection(url, properties);

                    } catch (SQLException e) {
                        throw new DbException(e.getMessage());
                    }
                }
       return connection;
    }

    public static void closeConnection(){

        if (connection != null) {
            try {
                connection.close();

            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        // 1. Tenta usar variáveis de ambiente
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        String url = System.getenv("DB_URL");



        if (user != null && password != null && url != null) {
            properties.setProperty("user", user);
            properties.setProperty("password", password);
            properties.setProperty("dburl", url);

            return properties;
        }

        // 2. Se variáveis não estão disponíveis, tenta carregar do arquivo
        try (FileInputStream fs = new FileInputStream("db.properties")) {
            properties.load(fs);
            return properties;
        } catch (IOException e) {
            throw new DbException("Erro ao carregar propriedades: " + e.getMessage());
        }
    }

public static void closeStatement(Statement st){
if (st!=null){
    try {
        st.close();
    } catch (SQLException e) {
        throw new DbException(e.getMessage());
    }
}

}
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new DbException("Error closing ResultSet: " + e.getMessage());
            }
        }
    }
}
