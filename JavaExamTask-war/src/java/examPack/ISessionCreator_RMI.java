package examPack;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISessionCreator_RMI extends Remote {

    IHistory testLogin(String login, String password) throws RemoteException;
}
