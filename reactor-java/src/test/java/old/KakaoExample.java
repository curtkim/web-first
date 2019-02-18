package old;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

// http://tech.kakao.com/2018/05/29/reactor-programming/
public class KakaoExample {

  final List<String> basket1 = Arrays.asList(new String[]{"kiwi", "orange", "lemon", "orange", "lemon", "kiwi"});
  final List<String> basket2 = Arrays.asList(new String[]{"banana", "lemon", "lemon", "kiwi"});
  final List<String> basket3 = Arrays.asList(new String[]{"strawberry", "orange", "lemon", "grape", "strawberry"});
  final List<List<String>> baskets = Arrays.asList(basket1, basket2, basket3);
  final Flux<List<String>> basketFlux = Flux.fromIterable(baskets);

  @Test
  public void testFruitBaskets() {
    Flux<FruitInfo> fruitsFlux = basketFlux.concatMap(basket -> {
      final Mono<List<String>> distinctFruits = Flux.fromIterable(basket).distinct().collectList();

      final Mono<Map<String, Long>> countFruitsMono = Flux.fromIterable(basket)
          .groupBy(fruit -> fruit) // 바구니로 부터 넘어온 과일 기준으로 group을 묶는다.
          .concatMap(groupedFlux -> groupedFlux.count()
              .map(count -> {
                final Map<String, Long> fruitCount = new LinkedHashMap<>();
                fruitCount.put(groupedFlux.key(), count);
                return fruitCount;
              }) // 각 과일별로 개수를 Map으로 리턴
          ) // concatMap으로 순서보장
          .reduce((accumulatedMap, currentMap) -> new LinkedHashMap<String, Long>() {
            {
              putAll(accumulatedMap);
              putAll(currentMap);
            }
          }); // 그동안 누적된 accumulatedMap에 현재 넘어오는 currentMap을 합쳐서 새로운 Map을 만든다. // map끼리 putAll하여 하나의 Map으로 만든다.

      return Flux.zip(distinctFruits, countFruitsMono,
          (distinct, count) -> new FruitInfo(distinct, count));
    });
    assertResult(fruitsFlux);
  }

  @Test
  public void testWithPublish(){
    Flux<FruitInfo> fruitsFlux = basketFlux.concatMap(basket -> {
      final Flux<String> source = Flux.fromIterable(basket).log().publish().autoConnect(2);
      final Mono<List<String>> distinctFruits = source.distinct().collectList();
      final Mono<Map<String, Long>> countFruitsMono = source
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
          }}); // 그동안 누적된 accumulatedMap에 현재 넘어오는 currentMap을 합쳐서 새로운 Map을 만든다. // map끼리 putAll하여 하나의 Map으로 만든다.
      return Flux.zip(distinctFruits, countFruitsMono, (distinct, count) -> new FruitInfo(distinct, count));
    });
    assertResult(fruitsFlux);
  }


  public static void assertResult(Flux<FruitInfo> fruitsFlux){
    final FruitInfo expected1 = new FruitInfo(
        Arrays.asList("kiwi", "orange", "lemon"),
        new LinkedHashMap<String, Long>() { {
          put("kiwi", 2L);
          put("orange", 2L);
          put("lemon", 2L);
        }}
    );
    final FruitInfo expected2 = new FruitInfo(
        Arrays.asList("banana", "lemon", "kiwi"),
        new LinkedHashMap<String, Long>() { {
          put("banana", 1L);
          put("lemon", 2L);
          put("kiwi", 1L);
        }}
    );
    final FruitInfo expected3 = new FruitInfo(
        Arrays.asList("strawberry", "orange", "lemon", "grape"),
        new LinkedHashMap<String, Long>() { {
          put("strawberry", 2L);
          put("orange", 1L);
          put("lemon", 1L);
          put("grape", 1L);
        }}
    );

    StepVerifier.create(fruitsFlux)
        .expectNext(expected1)
        .expectNext(expected2)
        .expectNext(expected3)
        .verifyComplete();
  }
}

class FruitInfo {
  private final List<String> distinctFruits;
  private final Map<String, Long> countFruits;

  public FruitInfo(List<String> distinctFruits, Map<String, Long> countFruits) {
    this.distinctFruits = distinctFruits;
    this.countFruits = countFruits;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FruitInfo fruitInfo = (FruitInfo) o;

    if (distinctFruits != null ? !distinctFruits.equals(fruitInfo.distinctFruits) : fruitInfo.distinctFruits != null)
      return false;
    return countFruits != null ? countFruits.equals(fruitInfo.countFruits) : fruitInfo.countFruits == null;
  }

  @Override
  public int hashCode() {
    int result = distinctFruits != null ? distinctFruits.hashCode() : 0;
    result = 31 * result + (countFruits != null ? countFruits.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "FruitInfo{" +
        "distinctFruits=" + distinctFruits +
        ", countFruits=" + countFruits +
        '}';
  }
}