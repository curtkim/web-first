package util;

import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class JavaSerializer implements Serializer {

  @Override
  public void configure(Map configs, boolean isKey) {

  }

  @Override
  public byte[] serialize(String topic, Object object) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(baos);
      oos.writeObject(object);
      return baos.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        oos.close();
        baos.close();
      }
      catch (IOException ex){}
    }
  }

  @Override
  public void close() {

  }
}
