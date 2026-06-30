import java.sql.*;
import com.j256.ormlite.jdbc.JdbcConnectionSource;

public class Database {
   private String databaseName = null;
   private JdbcConnectionSource connection = null;
   
   public Database(String databaseName) {
       this.databaseName = databaseName;
   }    
   
   public JdbcConnectionSource getConnection() throws SQLException {
      if (databaseName == null) {
          throw new SQLException("O nome do banco de dados está nulo!");
      }
      
      if (connection == null) {
          try {
              connection = new JdbcConnectionSource("jdbc:sqlite:" + databaseName);             
          } catch (Exception e) {
              System.err.println(e.getClass().getName() + ": " + e.getMessage());
              System.exit(0); 
          }
          System.out.println("Banco de dados aberto com sucesso!");
      }
      return connection;
   }
   

   public void close() {
       if (connection != null) {
           try {
               connection.close();
               this.connection = null;
               System.out.println("Banco de dados fechado.");
           } catch (java.lang.Exception e) {
               System.err.println(e);
           }
       }
   }
}