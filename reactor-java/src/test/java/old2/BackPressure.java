package old2;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class BackPressure {
  public static void main(String[] argv) throws InterruptedException {
    Observable.interval(1, TimeUnit.NANOSECONDS)
        .observeOn(Schedulers.newThread())
        .subscribe(
            i -> {
              System.out.println(i);
              try {
                Thread.sleep(100);
              } catch (Exception e) {	}
            },
            System.out::println);

    Thread.sleep(10000);
  }
}
