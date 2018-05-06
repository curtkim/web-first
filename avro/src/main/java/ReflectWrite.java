import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.reflect.AvroName;
import org.apache.avro.reflect.Nullable;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;

import java.io.File;

public class ReflectWrite {
  public static void main(String[] argv) throws Exception{

    Schema schema = ReflectData.get().getSchema(User.class);
    System.out.println(schema);

    User user1 = new User("Alyssa", 256);
    User user2 = new User("Ben", 7);
    user2.setFavoriteColor("red");

    File file = new File("users_reflect.avro");
    DatumWriter<User> datumWriter = new ReflectDatumWriter<>(User.class);
    DataFileWriter<User> dataFileWriter = new DataFileWriter<>(datumWriter);
    dataFileWriter.create(schema, file);
    dataFileWriter.append(user1);
    dataFileWriter.append(user2);
    dataFileWriter.close();
  }
}

class User {
  String name;

  @AvroName("favorite_number")
  Integer favoriteNumber;

  @Nullable
  @AvroName("favorite_color")
  String favoriteColor;

  User(){
  }

  public User(String name, Integer favoriteNumber) {
    this.name = name;
    this.favoriteNumber = favoriteNumber;
  }

  public String getFavoriteColor() {
    return favoriteColor;
  }

  public void setFavoriteColor(String favoriteColor) {
    this.favoriteColor = favoriteColor;
  }

  @Override
  public String toString() {
    return "User{" +
        "name='" + name + '\'' +
        ", favoriteNumber=" + favoriteNumber +
        ", favoriteColor='" + favoriteColor + '\'' +
        '}';
  }
}