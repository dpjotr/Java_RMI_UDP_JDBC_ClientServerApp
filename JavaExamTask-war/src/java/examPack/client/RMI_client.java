package examPack.client;
import examPack.IHistory;
import examPack.ISessionCreator_RMI;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

    class RMI_client {
    String login;
    String password;
    private GUI userGui;
    private Registry registry;
    ISessionCreator_RMI sessionCreator;
    IHistory session;

    RMI_client() throws RemoteException, NotBoundException {


        registry= LocateRegistry.getRegistry(1099);
        sessionCreator =(ISessionCreator_RMI)registry
                .lookup("Session Creator");
        System.out.println("Connection done, " +
                "sessionCreator is available");
        userGui=new GUI(this);
    }


    public static void main(String[]args) throws RemoteException, NotBoundException {
        RMI_client client=new RMI_client();
     
    }
}
