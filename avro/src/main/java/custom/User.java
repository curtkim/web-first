package custom;

import net.agkn.hll.HLL;
import org.apache.avro.reflect.AvroEncode;
import org.apache.avro.reflect.AvroName;
import org.apache.avro.reflect.Nullable;

public class User {
  String name;

  @AvroName("favorite_number")
  Integer favoriteNumber;

  @Nullable
  @AvroName("favorite_color")
  String favoriteColor;

  @AvroName("friend_hll")
  @AvroEncode(using = HLLCustomEncoding.class)
  HLL friendHll = new HLL(14, 5);


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
    String strFriendHll = ", friendHll.cardinality='" + (friendHll != null ? friendHll.cardinality() : "null")+ '\'';

    return "User{" +
        "name='" + name + '\'' +
        ", favoriteNumber=" + favoriteNumber +
        ", favoriteColor='" + favoriteColor + '\'' +
        strFriendHll +
        '}';
  }
}
