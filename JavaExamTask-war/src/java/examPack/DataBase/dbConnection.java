
package examPack.DataBase;


import examPack.EmployeeException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class dbConnection implements AutoCloseable{
    Connection connection=null;    
    Statement statement=null;  
    ResultSet result=null;
    
    
    dbConnection(){    
           
        //Open connection
        try {
            connection = DriverManager
                    .getConnection("jdbc:derby://localhost:1527/database", "adm", "adm");
            statement = connection.createStatement();
            
            System.out.println("Connection to database done");
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());            
        }
    }    
  
    
    boolean login(String login, String password) throws SQLException{
        
       
        try {
        result = statement.executeQuery("SELECT * FROM employees WHERE login="+quotate(login));            
        if(result.next()&&password.equals(result.getString("psw"))) return true;
        else return false;
        } catch (SQLException ex) {
            Logger.getLogger(dbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }   
    
    String quotate(String s){return "'"+s+"'";}
    
    
    String[] setResultHistoryUsingLastName(String name, String login, String password) 
                                                    throws SQLException, EmployeeException{
        if (this.login(login, password)){
        result = statement.executeQuery(
                    "SELECT id, code, name, last_name,"
                            + " position, manager, hire, dismiss\n" +
                    "FROM employees  NATURAL JOIN employeesHistory  \n" +
                    "WHERE UPPER(last_name)=UPPER("+quotate(name)+")");  
        
        return getHistory();
        }
        else throw new EmployeeException("Unable to login");
    }
    
    String[] setResultHistoryUsingId(int id, String login, String password) 
                                                    throws SQLException, EmployeeException{
        if (this.login(login, password)){
        result = statement.executeQuery(
                    "SELECT id, code, name, last_name,"
                            + " position, manager, hire, dismiss\n" +
                    "FROM employees  NATURAL JOIN employeesHistory  \n" +
                    "WHERE code="+id);  
        
        return getHistory();
        }
        else throw new EmployeeException("Unable to login");
    }   
    
    
    String[] getHistory(){
    
        try {             
            
            ArrayList <String>list=new ArrayList();
            while(result.next()){  
                String line="Record#"+result.getString("id")+"--"
                        +"Employee_Id:"+result.getString("code")
                        +", name:"+result.getString("name")
                        +", last name: "+result.getString("last_name")
                        +", position:"+result.getString("position")
                        +", period:"+quotate(result.getString("hire"))
                        +"-- "
                        +(result.getString("dismiss")==null?"until now":quotate(result.getString("dismiss")));                
                list.add(line);
        }
        if (list.size()>0)
            return list.toArray(new String[list.size()]);
        else return null;

        } catch (SQLException ex) {
                ex.printStackTrace();
        }                
        return null;
    }
    
        String[] getHistory(int id, String login, String password){
        
        return null;
    }

    @Override
    public void close() {
        
        statement=null;  
        result=null;
        if (connection!=null)
        try {
            connection.close();            
            System.out.println("Connection to database closed");            
        } catch (SQLException ex) {
            Logger.getLogger(dbConnection.class.getName()).log(Level.SEVERE, null, ex);           
        }    
    }
}