package old2;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RefereneceExamples {

  @Test
  public void test1(){
    Flux<Integer> ints = Flux.range(1, 4)
        .map(i -> {
          if (i <= 3) return i;
          throw new RuntimeException("Got to 4");
        });

    StepVerifier.create(ints)
        .expectNext(1, 2, 3)
        .expectError(RuntimeException.class)
        .verify();
  }

  @Test
  public void test2(){
    StepVerifier.create(Flux.range(1, 4))
        .expectNext(1, 2, 3, 4)
        .expectComplete()
        .verify();
  }

  @Test
  public void generate(){
    Flux<String> flux = Flux.generate(
        () -> 0,
        (state, sink) -> {
          sink.next("3 x " + state + " = " + 3*state);
          if (state == 2) sink.complete();
          return state + 1;
        });

    StepVerifier.create(flux)
        .expectNext("3 x 0 = 0")
        .expectNext("3 x 1 = 3")
        .expectNext("3 x 2 = 6")
        .expectComplete()
        .verify();
  }

  @Test
  public void generateMutable(){
    Flux<String> flux = Flux.generate(
        AtomicLong::new,
        (state, sink) -> {
          long i = state.getAndIncrement();
          sink.next("3 x " + i + " = " + 3*i);
          if (i == 2) sink.complete();
          return state;
        });

    StepVerifier.create(flux)
        .expectNext("3 x 0 = 0")
        .expectNext("3 x 1 = 3")
        .expectNext("3 x 2 = 6")
        .expectComplete()
        .verify();
  }

  @Test
  public void handle(){
    Function<Integer, String> alphabet = (Integer letterNumber)-> {
      if (letterNumber < 1 || letterNumber > 26) {
        return null;
      }
      int letterIndexAscii = 'A' + letterNumber - 1;
      return "" + (char) letterIndexAscii;
    };

    Flux<String> flux = Flux.just(-1, 30, 13, 9, 20)
        .handle((i, sink) -> {
          String letter = alphabet.apply(i);
          if (letter != null)
            sink.next(letter); // 0 or 1
        });

    StepVerifier.create(flux)
        .expectNext("M", "I", "T")
        .expectComplete()
        .verify();
  }

  @Test
  public void reduce(){
    Mono<Integer> mono = Flux.just(1, 2, 3, 4)
        .log()
        .reduce(0, (a, b)-> a+b);

    StepVerifier.create(mono)
        .expectNext(10)
        .expectComplete()
        .verify();
  }


  @Test
  public void flatMap(){
    Flux<Integer> flux = Flux.just(1,2,3).flatMap(i -> Flux.range(1, i));
    StepVerifier.create(flux)
        .expectNext(1, 1, 2, 1, 2, 3)
        .expectComplete()
        .verify();
  }

}
