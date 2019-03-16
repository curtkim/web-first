package kakao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// subscribeOn(Schedulers.parallel())
public class KakaoExample2 {

  public static void main(String[] args) throws InterruptedException {
    final Flux<List<String>> basketFlux = KakaoExample1.getListFlux();

    CountDownLatch countDownLatch = new CountDownLatch(1);

    basketFlux.concatMap(basket -> {
      System.out.println(basket);
      final Mono<List<String>> distinctFruits = Flux
          .fromIterable(basket)
          .log()
          .distinct()
          .collectList()
          .subscribeOn(Schedulers.parallel());

      final Mono<Map<String, Long>> countFruitsMono = Flux.fromIterable(basket)
          .log()
          .groupBy(fruit -> fruit) // 바구니로 부터 넘어온 과일 기준으로 group을 묶는다.
          .concatMap(groupedFlux -> groupedFlux.count()
              .map(count -> {
                final Map<String, Long> fruitCount = new LinkedHashMap<>();
                fruitCount.put(groupedFlux.key(), count);
                return fruitCount;
              })
          )
          .reduce((accumulatedMap, currentMap) -> new LinkedHashMap<String, Long>() { {
            putAll(accumulatedMap);
            putAll(currentMap);
          }})
          .subscribeOn(Schedulers.parallel());

      return Flux.zip(distinctFruits, countFruitsMono, (distinct, count) -> new FruitInfo(distinct, count));
    }).subscribe(
        System.out::println,
        err -> countDownLatch.countDown(),
        () -> countDownLatch.countDown()
    );

    countDownLatch.await();
  }

}

