package kakao;

import reactor.core.publisher.Flux;

import java.util.function.Function;

public class MyAnswer {
  public static void main(String[] args) {
    KakaoExample1.getListFlux()
        .flatMap(it -> Flux.fromIterable(it))
        .groupBy(Function.identity())
        .flatMap(grouped -> Flux.zip(Flux.just(grouped.key()), grouped.count()))
        .subscribe(System.out::println);
  }
}
