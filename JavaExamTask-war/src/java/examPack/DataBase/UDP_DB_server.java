package examPack.DataBase;
import examPack.ByteConverter;
import examPack.EmployeeException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import static examPack.CommonInformation.UDP_PACKAGE_SIZE;
import static examPack.CommonInformation.UDP_SERVER_PORT;
import examPack.UDP_Message;
import java.io.IOException;
import java.sql.SQLException;


public class UDP_DB_server implements AutoCloseable{


    private DatagramSocket socket;
    private DatagramPacket packet;
    Boolean notBusy;

    public UDP_DB_server()  {

        notBusy= true;
    }


    public  static void main(String[]args){
        try(UDP_DB_server server=new UDP_DB_server()){
        server.run();
        }
        catch (Exception e){
            e.printStackTrace();
        }    

    }

    private void run() throws EmployeeException, SocketException  {   
        System.out.println("UDP-database server started");
        while (notBusy){
            
            synchronized(notBusy){
                notBusy=false;            
                socket=new DatagramSocket(UDP_SERVER_PORT);
                packet=new DatagramPacket(new byte[UDP_PACKAGE_SIZE], UDP_PACKAGE_SIZE);
                recieveMessage();
                socket.close();
                packet=null;
                notBusy=true;
            }
        }
    }

    private void recieveMessage() throws EmployeeException {
        
         try(dbConnection database= new dbConnection();){
            socket.receive(packet);
            UDP_Message received = (UDP_Message) ByteConverter.convertObjectFromBytes(packet.getData());
            System.out.print("Message received on the database server with command"+received.getCommand()+"\n");
            if (received.getCommand()==null||received.getPassword()==null||received.getLogin()==null){
                UDP_Message answer = new UDP_Message();
                answer.setServerResponse("Wrong message type: commmand or creditnals are missing");
                byte[] buf = ByteConverter.convertObjectToBytes(answer);
                packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                socket.send(packet);
                System.out.print("Message from db server sent");

            }
            else {

                switch (received.getCommand()) {
                    case "login": {
                        
                        
                        String response=database.login(received.getLogin(), received.getPassword())?
                                "Access granted":"Access denied";
                        UDP_Message answer = new UDP_Message();
                        answer.setServerResponse(response);
                        byte[] buf = ByteConverter.convertObjectToBytes(answer);
                        packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                        socket.send(packet);
                        break;
                    }
                    case "get history by last name": {

                        UDP_Message answer = new UDP_Message();
                        answer.setHistory(database.setResultHistoryUsingLastName(
                        received.getLastName(), received.getLogin(), received.getPassword()));
                        byte[] buf = ByteConverter.convertObjectToBytes(answer);
                        packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                        socket.send(packet);                       
                        break;
                    }
                    
                    case "get history by id": {

                        UDP_Message answer = new UDP_Message();
                        answer.setHistory(database.setResultHistoryUsingId(
                                received.getId(), received.getLogin(), received.getPassword()));
                        byte[] buf = ByteConverter.convertObjectToBytes(answer);
                        packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                        socket.send(packet);
                        break;
                    }

                }
            }

        }
        catch(SQLException|IOException e){
            e.printStackTrace();
        }
   

    }

    @Override
    public void close() throws Exception {
        if (socket!=null)
            socket.close();
    }
}


