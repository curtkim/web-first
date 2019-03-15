package kakao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

// Hot
public class KakaoExample3 {

  public static void main(String[] args){
    final Flux<List<String>> basketFlux = KakaoExample1.getListFlux();

    basketFlux.concatMap(basket -> {
      System.out.println(basket);
      final Flux<String> source = Flux.fromIterable(basket).log().publish().autoConnect(2).subscribeOn(Schedulers.single());

      final Mono<List<String>> distinctFruits = source.publishOn(Schedulers.parallel()).distinct().collectList().log();
      final Mono<Map<String, Long>> countFruitsMono = source.publishOn(Schedulers.parallel())
          .groupBy(fruit -> fruit) // 바구니로 부터 넘어온 과일 기준으로 group을 묶는다.
          .concatMap(groupedFlux -> groupedFlux.count()
              .map(count -> {
                final Map<String, Long> fruitCount = new LinkedHashMap<>();
                fruitCount.put(groupedFlux.key(), count);
                return fruitCount;
              }) // 각 과일별로 개수를 Map으로 리턴
          ) // concatMap으로 순서보장
          .reduce((accumulatedMap, currentMap) -> new LinkedHashMap<String, Long>() { {
            putAll(accumulatedMap);
            putAll(currentMap);
          }}) // 그동안 누적된 accumulatedMap에 현재 넘어오는 currentMap을 합쳐서 새로운 Map을 만든다. // map끼리 putAll하여 하나의 Map으로 만든다.
          .log();
      return Flux.zip(distinctFruits, countFruitsMono, (distinct, count) -> new FruitInfo(distinct, count));
    }).subscribe(
        System.out::println
    );

    try {
      Thread.sleep(2*1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}

