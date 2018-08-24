import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class DistinctTest {

  private static List<String> words = Arrays.asList("the",
      "quick",
      "brown",
      "fox",
      "jumped",
      "over",
      "the",
      "lazy",
      "dog");

  @Test
  public void findingMissingLetter() {
    Flux<String> manyLetters = Flux
        .fromIterable(words)
        .flatMap(word -> Flux.fromArray(word.split("")))
        .distinct()
        .sort()
        .zipWith(Flux.range(1, Integer.MAX_VALUE),
            (string, count) -> String.format("%2d. %s", count, string));

    manyLetters.subscribe(System.out::println);
  }

  @Test
  public void distinct(){
    Flux<String> flux = Flux.just("A", "A", "B", "A", "C");

    StepVerifier.create(flux.distinct())
        .expectNext("A")
        .expectNext("B")
        .expectNext("C")
        .expectComplete()
        .verify();
  }

  @Test
  public void distinctUntilChanged(){
    Flux<String> flux = Flux.just("A", "A", "B", "A", "C");

    StepVerifier.create(flux.distinctUntilChanged())
        .expectNext("A")
        .expectNext("B")
        .expectNext("A")
        .expectNext("C")
        .expectComplete()
        .verify();
  }


  @Test
  public void bufferUntil(){
    Flux<Record> flux = Flux.just(
        new Record(new Key(1, "A"), 1),
        new Record(new Key(1, "B"), 1),
        new Record(new Key(1, "A"), 2),
        new Record(new Key(2, "A"), 1),
        new Record(new Key(2, "C"), 1),
        new Record(new Key(2, "C"), 2)
    );

    Predicate<Record> predicate = new Predicate<Record>() {
      int current = 0;

      @Override
      public boolean test(Record record) {
        if( record.key.time > current){
          current = record.key.time;
          return true;
        }
        return false;
      }
    };

    StepVerifier.create(flux.bufferUntil(predicate, true))
        .expectNext(Arrays.asList(
            new Record(new Key(1, "A"), 1),
            new Record(new Key(1, "B"), 1),
            new Record(new Key(1, "A"), 2)
        ))
        .expectNext(Arrays.asList(
            new Record(new Key(2, "A"), 1),
            new Record(new Key(2, "C"), 1),
            new Record(new Key(2, "C"), 2)
        ))
        .expectComplete()
        .verify();
  }


}

class Key {
  int time;
  String name;

  public Key(int time, String name) {
    this.time = time;
    this.name = name;
  }

  @Override
  public String toString() {
    return "Key{" +
        "time=" + time +
        ", name='" + name + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Key key = (Key) o;
    return time == key.time &&
        Objects.equals(name, key.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, name);
  }
}
class Record{
  Key key;
  int value;

  public Record(Key key, int value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public String toString() {
    return "Record{" +
        "key=" + key +
        ", value=" + value +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Record record = (Record) o;
    return value == record.value &&
        Objects.equals(key, record.key);
  }

  @Override
  public int hashCode() {

    return Objects.hash(key, value);
  }
}