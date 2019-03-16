package old2;

import domain1.Key;
import domain1.Record;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.function.Predicate;

public class BufferTest {

  @Test
  public void overlap(){
    StepVerifier.create(Flux.range(1, 10).buffer(5, 3))
        .expectNext(Arrays.asList(1,2,3,4,5))
        .expectNext(Arrays.asList(4,5,6,7,8))
        .expectNext(Arrays.asList(7,8,9,10))
        .expectNext(Arrays.asList(10))
        .expectComplete()
        .verify();
  }


  @Test
  public void test0(){
    StepVerifier.create(Flux.just(1, 2, 3, 4, 5, 6).bufferUntil((i)-> i%2 == 0))
        .expectNext(Arrays.asList(1,2))
        .expectNext(Arrays.asList(3,4))
        .expectNext(Arrays.asList(5,6))
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

