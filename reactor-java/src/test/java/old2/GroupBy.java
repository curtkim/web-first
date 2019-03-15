package old2;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.test.StepVerifier;

import java.util.*;

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
    Flux<Pair> flux = Flux.range(1, 100)
        .groupBy(i -> i%2)
        .concatMap( groupedFlux ->
          groupedFlux.reduce(new Pair(groupedFlux.key(), 0), (a, b) -> a.add(b))
        );

    StepVerifier.create(flux)
        .expectNext(new Pair(1, 2500))
        .expectNext(new Pair(0, 2550))
        .expectComplete()
        .verify();
  }

}


class Pair {
  int group;
  int value;

  Pair(int group, int value){
    this.group = group;
    this.value = value;
  }

  Pair add(int value){
    Pair pair = new Pair(this.group, this.value);
    pair.value += value;
    return pair;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pair pair = (Pair) o;
    return group == pair.group &&
        value == pair.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(group, value);
  }

  @Override
  public String toString() {
    return "old2.Pair{" +
        "group=" + group +
        ", value=" + value +
        '}';
  }
}
