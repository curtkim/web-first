package domain2;

public class Summary{
  int partition;
  long timestamp;
  String kind;
  int sum;

  public Summary(int partition, String kind) {
    this.partition = partition;
    this.kind = kind;
  }

  public Summary add(Record r){
    Summary s1 = new Summary(this.partition, this.kind);
    s1.timestamp = r.timestamp / 1000 * 1000;
    s1.sum = this.sum + r.value;
    return s1;
  }

  @Override
  public String toString() {
    return "Summary{" +
        "partition=" + partition +
        ", timestamp=" + timestamp +
        ", kind='" + kind + '\'' +
        ", sum=" + sum +
        '}';
  }
}
