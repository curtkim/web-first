package domain2;

import java.util.function.Predicate;

public class TimePredicate implements Predicate<Record> {
  long current = 0;

  @Override
  public boolean test(Record rec) {
    long truncated = rec.timestamp/ 1000*1000;
    //System.out.println(rec.timestamp + " " + truncated + " " + rec.partition + " " + rec.value);
    if( truncated > current){
      current = truncated;
      return true;
    }
    return false;
  }
}
