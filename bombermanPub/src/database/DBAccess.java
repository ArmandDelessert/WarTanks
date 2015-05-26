package database;

import game.DBData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.sqlite.SQLiteConfig;

/**
 * Cette classe met à disposition les méthodes qui permettent d'acceder à la base de données.
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 */
public class DBAccess {
	
	static String dataBaseToOpen = "";
	static int numMap = 1;

   /**
    * Cette méthode static permet de charger une carte depuis de la base de données.
    * @param args
    */
	public static ArrayList<DBData> getMap(String dbName, int numMapGiven)
	{
		dataBaseToOpen = dbName;
		numMap = numMapGiven;

		ArrayList<DBData> map = new ArrayList<DBData>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String dbPath = "jdbc:sqlite:" + dataBaseToOpen;
        String sqlString = "SELECT X, Y, Type FROM Element WHERE idCarte = " + numMap; 
      
      try {
          Class.forName("org.sqlite.JDBC"); // charger le driver pour SQLite
        
          SQLiteConfig config = new SQLiteConfig();
          config.enforceForeignKeys(true);
          connection = DriverManager.getConnection(dbPath, config.toProperties());
        
          preparedStatement = connection.prepareStatement(sqlString);
          preparedStatement.setQueryTimeout(30);
          
          // pour un select
          ResultSet result = preparedStatement.executeQuery();
          while (result.next()) {
      	     DBData dbData = new DBData();
        	 dbData.setX(result.getInt(1));
        	 dbData.setY(result.getInt(2));
        	 dbData.setData(result.getInt(3));
             map.add(dbData);
          }
              
      }
      catch (Exception e) {
           e.printStackTrace();
      }
      finally {
         try {
            preparedStatement.getConnection().close();
         } catch (SQLException e) {
            e.printStackTrace();
         }
      }
      return map;
   }
}