package domain1;

import java.util.Objects;

public class Record{
  public Key key;
  public int value;

  public Record(Key key, int value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public String toString() {
    return "Record{" +
        "key=" + key +
        ", value=" + value +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Record record = (Record) o;
    return value == record.value &&
        Objects.equals(key, record.key);
  }

  @Override
  public int hashCode() {

    return Objects.hash(key, value);
  }
}
