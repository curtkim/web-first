import example.avro.User;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.File;

public class CodeGenerationRead {
  public static void main(String[] argv) throws Exception{
    // Deserialize Users from disk
    DatumReader<User> userDatumReader = new SpecificDatumReader<>(User.class);
    DataFileReader<User> dataFileReader = new DataFileReader<>(new File("users.avro"), userDatumReader);
    User user = null;
    while (dataFileReader.hasNext()) {
      // Reuse user object by passing it to next(). This saves us from
      // allocating and garbage collecting many objects for files with
      // many items.
      user = dataFileReader.next(user);
      System.out.println(user);
    }
  }
}
