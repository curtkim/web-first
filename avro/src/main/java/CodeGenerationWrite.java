import example.avro.User;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;

public class CodeGenerationWrite {
  public static void main(String[] argv) throws Exception{
    User user1 = new User();
    user1.setName("Alyssa");
    user1.setFavoriteNumber(256);

    // Construct via builder
    User user3 = User.newBuilder()
        .setName("Charlie")
        .setFavoriteColor("blue")
        .setFavoriteNumber(null)
        .build();

    DatumWriter<User> userDatumWriter = new SpecificDatumWriter<>(User.class);

    DataFileWriter<User> dataFileWriter = new DataFileWriter<>(userDatumWriter);
    dataFileWriter.create(user1.getSchema(), new File("users.avro"));
    dataFileWriter.append(user1);
    dataFileWriter.append(user3);
    dataFileWriter.close();
  }
}
