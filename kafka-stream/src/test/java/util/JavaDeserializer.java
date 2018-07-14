package util;

import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class JavaDeserializer implements Deserializer {

  @Override
  public void configure(Map configs, boolean isKey) {

  }

  @Override
  public Object deserialize(String topic, byte[] data) {
    ByteArrayInputStream bais = null;
    ObjectInputStream ois = null;
    try {
      bais = new ByteArrayInputStream(data);
      ois = new ObjectInputStream(bais);
      return ois.readObject();
    }
    catch (IOException e){
      throw new RuntimeException(e);
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    finally {
      try {
        bais.close();
        ois.close();
      }
      catch (IOException ex){}
    }
  }

  @Override
  public void close() {

  }
}
