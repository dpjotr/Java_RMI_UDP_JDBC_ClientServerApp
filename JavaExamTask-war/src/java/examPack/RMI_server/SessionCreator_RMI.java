package examPack.RMI_server;
import examPack.ByteConverter;
import static examPack.ByteConverter.convertObjectFromBytes;
import static examPack.CommonInformation.UDP_PACKAGE_SIZE;
import static examPack.CommonInformation.UDP_SERVER_PORT;
import examPack.IHistory;
import examPack.UDP_Message;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionCreator_RMI extends UnicastRemoteObject
implements examPack.ISessionCreator_RMI {    
    
    private DatagramSocket socket;
    private DatagramPacket packet;


    protected SessionCreator_RMI() throws SocketException, RemoteException {
        super();        
    }



    @Override
    public IHistory testLogin(String login, String password)
                                        throws RemoteException{        
    
        UDP_Message message=new UDP_Message();
        synchronized(message){
            try {
                socket = new DatagramSocket();
            } catch (SocketException ex) {
                Logger.getLogger(SessionCreator_RMI.class.getName()).log(Level.SEVERE, null, ex);
            }
            packet = new DatagramPacket(new byte[UDP_PACKAGE_SIZE], UDP_PACKAGE_SIZE); 
            message.setLogin(login);
            message.setPassword(password);
            message.setCommand("login");
            try {
                message=sendMessage(InetAddress.getLocalHost(),  UDP_SERVER_PORT, message);
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
            finally{socket.close(); packet=null;}
            if (message.getServerResponse().equals("Access granted")) {
                try {
                    return new Session(login,password);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    
        private  UDP_Message sendMessage(InetAddress ip, int port, UDP_Message message) {
        try {
            byte[] buf = ByteConverter.convertObjectToBytes(message);
            packet = new DatagramPacket(buf, buf.length, ip, port);
            socket.send(packet);
            System.out.print("Message from rmi server sent by session creator\n");
            packet = new DatagramPacket(new byte[UDP_PACKAGE_SIZE], UDP_PACKAGE_SIZE);
            socket.receive(packet);
            System.out.print("Message from db server received for sessionCreator received\n");
            UDP_Message answer;

            Object answerObj = (Object) convertObjectFromBytes(packet.getData());
            if( answerObj instanceof UDP_Message) {
                answer = (UDP_Message) answerObj;
                return answer;
            }
            else {
                answer=new UDP_Message();
                answer.setServerResponse("Unable to decode answer from server");
                return answer;
            }
          
        } catch (IOException e) {
            System.err.println("Error from client sendMessage: " + e.getMessage()); 
            e.printStackTrace();
            UDP_Message emptyMessage=new UDP_Message();
            emptyMessage.setServerResponse("Unable to connect to server");
            return emptyMessage;
        }
    }    
}
