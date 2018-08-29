import domain.Key;
import domain.Record;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.function.Predicate;

public class GroupBy {

  @Test
  public void test() {
    List<String> basket1 = Arrays.asList("kiwi", "orange", "lemon", "orange", "lemon", "kiwi");
    Flux<Map<String, Long>> flux1 = Flux.fromIterable(basket1)
        .groupBy(fruit -> fruit)
        .concatMap((GroupedFlux<String, String> groupedFlux) -> groupedFlux.count()
            .map(count -> {
              final Map<String, Long> fruitCount = new LinkedHashMap<>();
              fruitCount.put(groupedFlux.key(), count);
              return fruitCount;
            })
        );

    StepVerifier.create(flux1)
        .expectNext(new HashMap<String, Long>(){{ put("kiwi", 2l);}})
        .expectNext(new HashMap<String, Long>(){{ put("orange", 2l);}})
        .expectNext(new HashMap<String, Long>(){{ put("lemon", 2l);}})
        .expectComplete()
        .verify();
  }

  @Test
  public void test2(){
    Flux<Integer> flux = Flux.range(1, 100)
        .groupBy(i -> i%2)
        .concatMap( groupedFlux -> groupedFlux.reduce(0, (a,b)-> a+b) );

    StepVerifier.create(flux)
        .expectNext(2500)
        .expectNext(2550)
        .expectComplete()
        .verify();
  }

}
