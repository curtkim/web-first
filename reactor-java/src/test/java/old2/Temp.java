package old2;

import io.reactivex.Observable;

import java.util.concurrent.TimeUnit;

public class Temp {
  public static void main(String[] argv) throws InterruptedException {

    Observable<String> left =
        Observable.interval(100, TimeUnit.MILLISECONDS)
            .map(i -> "L" + i)
            .take(6);
    Observable<String> right =
        Observable.interval(200, TimeUnit.MILLISECONDS)
            .map(i -> "R" + i)
            .take(3);

    left
        .groupJoin(
            right,
            i -> Observable.never(),
            i -> Observable.timer(0, TimeUnit.MILLISECONDS),
            (l, rs) -> rs.toList().subscribe(list -> System.out.println(l + ": " + list))
        )
        .subscribe();

    /*
    System.out.println(System.currentTimeMillis());
    Observable.timer(1, TimeUnit.SECONDS)
        .subscribe(aLong -> {
          System.out.println(aLong);
          System.out.println(System.currentTimeMillis());
        });
    */

    Thread.sleep(2000);
  }
}
