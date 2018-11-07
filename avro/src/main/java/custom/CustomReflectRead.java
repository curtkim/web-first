package custom;

import java.io.File;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;

public class CustomReflectRead {

  static String OLD_FILE_NAME = "users_reflect.avro";

  public static void main(String[] argv) throws Exception{

    Schema schema = ReflectData.get().getSchema(User.class);

    ReflectDatumReader datumReader = new ReflectDatumReader(schema);
    DataFileReader<User> dataFileReader = new DataFileReader<>(new File(CustomReflectWrite.FILE_NAME), datumReader);

    User user = null;
    while (dataFileReader.hasNext()) {
      user = dataFileReader.next(user);
      System.out.println(user);
    }
  }
}
