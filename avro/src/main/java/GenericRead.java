import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;

import java.io.File;

public class GenericRead {
  public static void main(String[] argv) throws Exception {

    Schema schema = new Schema.Parser().parse(new File("src/main/avro/user.avsc"));
    DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
    DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(new File("users_reflect.avro"), datumReader);
    GenericRecord user = null;
    while (dataFileReader.hasNext()) {
      // Reuse user object by passing it to next(). This saves us from
      // allocating and garbage collecting many objects for files with
      // many items.
      user = dataFileReader.next(user);
      System.out.println(user);
    }
  }
}
