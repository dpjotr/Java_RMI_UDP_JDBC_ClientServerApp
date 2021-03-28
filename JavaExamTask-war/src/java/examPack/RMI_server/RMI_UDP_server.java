package examPack.RMI_server;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMI_UDP_server {
    Registry registry;
    String SessionCreatorName="Session Creator";
    SessionCreator_RMI sessionCreator_RMI;

    RMI_UDP_server() throws AlreadyBoundException, RemoteException, SocketException {
        sessionCreator_RMI = new SessionCreator_RMI();
        registry = LocateRegistry.createRegistry(1099);
        registry.bind(SessionCreatorName, sessionCreator_RMI);
        System.out.println("RMI-UDP server started");
    }
    public static void main(String[]args) throws RemoteException,  AlreadyBoundException,   SocketException {

        RMI_UDP_server server=new RMI_UDP_server();
    }
}
