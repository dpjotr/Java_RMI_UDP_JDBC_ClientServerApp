package examPack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public abstract class ByteConverter {

        static public Object convertObjectFromBytes(byte[] bytes)  {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); 
                                ObjectInput in = new ObjectInputStream(bis)) {
                return  in.readObject();
            } catch (Exception e) {
                System.err.println("Error convertMessageFromBytes: " + e.getMessage());
                return null;
            }
        }

        static public byte[] convertObjectToBytes(Object obj)  {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();   
                                ObjectOutput out = new ObjectOutputStream(bos)) {
                out.writeObject(obj);
                return bos.toByteArray();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return  null;
        }
}
