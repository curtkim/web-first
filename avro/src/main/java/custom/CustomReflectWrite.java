package custom;

import java.io.File;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.reflect.AvroName;
import org.apache.avro.reflect.Nullable;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;

public class CustomReflectWrite {

  static String FILE_NAME = "custom_users_reflect.avro";

  public static void main(String[] argv) throws Exception{

    Schema schema = ReflectData.get().getSchema(User.class);
    System.out.println(schema);

    User user1 = new User("Alyssa", 256);
    user1.friendHll.addRaw(1);
    user1.friendHll.addRaw(2);
    System.out.println(user1);

    User user2 = new User("Ben", 7);
    user2.setFavoriteColor("red");
    user2.friendHll.addRaw(3);
    System.out.println(user2);

    File file = new File(FILE_NAME);
    DatumWriter<User> datumWriter = new ReflectDatumWriter<>(User.class);
    DataFileWriter<User> dataFileWriter = new DataFileWriter<>(datumWriter);
    dataFileWriter.create(schema, file);
    dataFileWriter.append(user1);
    dataFileWriter.append(user2);
    dataFileWriter.close();
  }
}
