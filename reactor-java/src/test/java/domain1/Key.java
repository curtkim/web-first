package domain1;

import java.util.Objects;

public class Key {
  public int time;
  public String name;

  public Key(int time, String name) {
    this.time = time;
    this.name = name;
  }

  @Override
  public String toString() {
    return "Key{" +
        "time=" + time +
        ", name='" + name + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Key key = (Key) o;
    return time == key.time &&
        Objects.equals(name, key.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, name);
  }
}
