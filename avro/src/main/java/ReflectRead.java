import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;

import java.io.File;

public class ReflectRead {
  public static void main(String[] argv) throws Exception{

    Schema schema = ReflectData.get().getSchema(User.class);

    ReflectDatumReader datumReader = new ReflectDatumReader(schema);
    DataFileReader<User> dataFileReader = new DataFileReader<>(new File("users_reflect.avro"), datumReader);

    User user = null;
    while (dataFileReader.hasNext()) {
      user = dataFileReader.next(user);
      System.out.println(user);
    }
  }
}
