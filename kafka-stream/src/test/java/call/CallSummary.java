package call;

import java.io.Serializable;

public class CallSummary implements Serializable{
  long count=0;
  long distance=0;

  CallSummary() {
  }

  public CallSummary(long count, long distance) {
    this.count = count;
    this.distance = distance;
  }

  public CallSummary add(Call call){
    System.out.println("add: " + this + " + " + call);
    count++;
    distance += call.distance;
    return this;
  }

  @Override
  public String toString() {
    return "CallSummary{" +
        "count=" + count +
        ", distance=" + distance +
        '}';
  }

  public static CallSummary reduce(CallSummary a, CallSummary b){
    System.out.println("reduce: " + a + " " + b);
    CallSummary result = new CallSummary();
    result.count = a.count + b.count;
    result.distance = a.distance + b.distance;
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CallSummary that = (CallSummary) o;

    if (count != that.count) return false;
    return distance == that.distance;

  }

  @Override
  public int hashCode() {
    int result = (int) (count ^ (count >>> 32));
    result = 31 * result + (int) (distance ^ (distance >>> 32));
    return result;
  }
}
