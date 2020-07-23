/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Conexion;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import java.sql.*;
public class ClsConexion {
    
    private static Connection conection=null;
    public Connection getConection(){
        try{
            MysqlConnectionPoolDataSource ds=new MysqlConnectionPoolDataSource();
            ds.setServerName("localhost");
            ds.setPort(3306);
            ds.setDatabaseName("dbferreteriakerly?serverTimezone=UTC");
            conection=ds.getConnection("root","1754429361f");
        }catch(Exception ex){
           ex.printStackTrace();
        }
        return conection;
    }
    
}
