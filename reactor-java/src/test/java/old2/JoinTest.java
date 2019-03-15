package old2;

import org.junit.Test;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JoinTest {

  @Test
  public void test() throws InterruptedException {
    Flux<String> left = Flux.interval(Duration.ofMillis(100)).map(i -> "L"+i);
    Flux<String> right = Flux.interval(Duration.ofMillis(200)).map(i -> "R"+i);

    Flux<String> joined = left.join(right, i-> Flux.never(), i-> Flux.never(), (i,j)-> i+"-"+j);
    joined.subscribe(System.out::println);

    Thread.sleep(500);
  }



  final BiFunction<Integer, Integer, Integer> add = (t1, t2) -> t1 + t2;

  <T> Function<Integer, Flux<T>> just(final Flux<T> publisher) {
    return t1 -> publisher;
  }

  @Test
  public  void normal1() {
    AssertSubscriber<Object> ts = AssertSubscriber.create();

    DirectProcessor<Integer> source1 = DirectProcessor.create();
    DirectProcessor<Integer> source2 = DirectProcessor.create();

    Flux<Integer> m =
        source1.join(source2, just(Flux.never()), just(Flux.never()), add);

    m.subscribe(ts);

    source1.onNext(1);
    source1.onNext(2);
    source1.onNext(4);

    source2.onNext(16);
    source2.onNext(32);
    source2.onNext(64);

    source1.onComplete();
    source2.onComplete();

    ts.assertValues(
        17, 18, 20,
        33, 34, 36,
        65, 66, 68)
        .assertComplete()
        .assertNoError();
  }

  @Test
  public  void normal1WithDuration() {
    AssertSubscriber<Object> ts = AssertSubscriber.create();
    DirectProcessor<Integer> source1 = DirectProcessor.create();
    DirectProcessor<Integer> source2 = DirectProcessor.create();

    DirectProcessor<Integer> duration1 = DirectProcessor.create();

    Flux<Integer> m = source1.join(source2, just(duration1), just(Flux.never()), add);
    m.subscribe(ts);

    source1.onNext(1);
    source1.onNext(2);
    source2.onNext(16);

    duration1.onNext(1);

    source1.onNext(4);
    source1.onNext(8);

    source1.onComplete();
    source2.onComplete();

    ts.assertValues(17, 18, 20, 24)
        .assertComplete()
        .assertNoError();
  }

  @Test
  public  void normal2() {
    AssertSubscriber<Object> ts = AssertSubscriber.create();
    DirectProcessor<Integer> source1 = DirectProcessor.create();
    DirectProcessor<Integer> source2 = DirectProcessor.create();

    Flux<Integer> m =
        source1.join(source2, just(Flux.never()), just(Flux.never()), add);

    m.subscribe(ts);

    source1.onNext(1);
    source1.onNext(2);
    source1.onComplete();

    source2.onNext(16);
    source2.onNext(32);
    source2.onNext(64);

    source2.onComplete();

    ts.assertValues(17, 18, 33, 34, 65, 66)
        .assertComplete()
        .assertNoError();
  }
}
