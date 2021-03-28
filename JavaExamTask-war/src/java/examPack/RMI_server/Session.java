package examPack.RMI_server;

import examPack.ByteConverter;
import static examPack.ByteConverter.convertObjectFromBytes;
import static examPack.CommonInformation.UDP_PACKAGE_SIZE;
import static examPack.CommonInformation.UDP_SERVER_PORT;
import examPack.EmployeeException;
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


public class Session extends UnicastRemoteObject
        implements IHistory, AutoCloseable {

    private String login;
    private String password;
    private DatagramSocket socket;
    private DatagramPacket packet;

    public Session(String l, String p) throws RemoteException, SocketException {
        super();
        login=l;
        password=p;
    }

    @Override
    public String[] getHistory(String name) throws RemoteException, EmployeeException {

            UDP_Message message=new UDP_Message();
            message.setLogin(login);
            message.setPassword(password);
            message.setLastName(name);
            message.setCommand("get history by last name");
            UDP_Message answer=null;
            try {
                answer=sendMessage(InetAddress.getLocalHost(),  UDP_SERVER_PORT, message);
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }

            if(answer.getHistory()==null||answer.getHistory().length<1)            
            throw new EmployeeException("No employee with last name "+name+" can be found");
            
            return answer.getHistory();

    }

    @Override
    public String[] getHistory(int code) throws RemoteException, EmployeeException {
        
            UDP_Message message=new UDP_Message();
            message.setLogin(login);
            message.setPassword(password);
            message.setId(code);
            message.setCommand("get history by id");
            UDP_Message answer=null;

            try {
                answer=sendMessage(InetAddress.getLocalHost(),  UDP_SERVER_PORT, message);
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
            String[] result=answer.getHistory();
            if(result==null||result.length<1)            
            throw new EmployeeException("No employee with id "+code+" can be found");
            
            return answer.getHistory();

    }

    @Override
    public boolean login(String user, String password) throws RemoteException {
        UDP_Message message=new UDP_Message();
        message.setLogin(login);
        message.setPassword(password);
        message.setCommand("login");
        try {
            message=sendMessage(InetAddress.getLocalHost(),  UDP_SERVER_PORT, message);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }

        if (message.getServerResponse().equals("Access granted")) return true;     
        else return false;
    }

    @Override
    public boolean logout() throws RemoteException {
        try {
            this.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void close() throws Exception {
        if (socket !=null && !socket.isClosed()) socket.close();
    }
    
    public  UDP_Message sendMessage(InetAddress ip, int port, UDP_Message message) {
        try {
            byte[] buf = ByteConverter.convertObjectToBytes(message);
            packet = new DatagramPacket(buf, buf.length, ip, port);
            socket=new DatagramSocket();
            socket.send(packet);
            System.out.print("Message from rmi server sent with command "+message.getCommand()+"\n");
            packet = new DatagramPacket(new byte[UDP_PACKAGE_SIZE], UDP_PACKAGE_SIZE);
            socket.receive(packet);
            System.out.print("Message from db server received");
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
                UDP_Message emptyMessage=new UDP_Message();
                emptyMessage.setServerResponse("Unable to connect to server");
                e.printStackTrace();
                return emptyMessage;
        }
        finally{socket.close(); packet=null;}
    }    
    
}
