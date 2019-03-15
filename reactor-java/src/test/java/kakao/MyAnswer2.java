package kakao;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.function.Function;

public class MyAnswer2 {
  public static void main(String[] args) {
    KakaoExample1.getListFlux()
        .flatMap(it ->
            Flux.fromIterable(it)
                .publishOn(Schedulers.parallel())
                .log()
                .groupBy(Function.identity())
                .flatMap(grouped -> Flux.zip(Flux.just(grouped.key()), grouped.count()))
        )
        .publishOn(Schedulers.single())
        .log()
        .groupBy(tuple -> tuple.getT1())
        .flatMap(grouped -> Flux.zip(Flux.just(grouped.key()), grouped.reduce(0L, (a, tuple)-> a+tuple.getT2())))
        .subscribe(System.out::println);

    try {
      Thread.sleep(2*1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }
}
