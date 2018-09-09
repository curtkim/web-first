package domain2;

import java.util.Objects;

public class Record{
  public int partition;
  public long timestamp;
  public String kind;
  public int value;

  public Record(int partition, long timestamp, String kind, int value) {
    this.partition = partition;
    this.timestamp = timestamp;
    this.kind = kind;
    this.value = value;
  }

  @Override
  public String toString() {
    return "Record{" +
        "partition=" + partition +
        ", timestamp=" + timestamp +
        ", kind='" + kind + '\'' +
        ", value=" + value +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Record record = (Record) o;
    return partition == record.partition &&
        timestamp == record.timestamp &&
        value == record.value &&
        Objects.equals(kind, record.kind);
  }

  @Override
  public int hashCode() {

    return Objects.hash(partition, timestamp, kind, value);
  }
}
