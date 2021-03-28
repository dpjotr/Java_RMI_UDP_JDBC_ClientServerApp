package examPack;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IHistory extends Remote {
    String[] getHistory (String name) throws RemoteException, EmployeeException ;
    String[] getHistory (int code) throws RemoteException, EmployeeException ;
    boolean login (String user, String password) throws RemoteException ;
    boolean logout () throws RemoteException ;


}
