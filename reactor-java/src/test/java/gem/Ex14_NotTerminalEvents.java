package gem;

import org.junit.Test;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Signal;

public class Ex14_NotTerminalEvents {

  @Test
  public void test() {
    EmitterProcessor<Signal<Integer>> bus = EmitterProcessor.create();

    bus.materialize() // Signal로 변환한다.
        .subscribe(
            System.out::println,
            Throwable::printStackTrace
        );
    //bus.subscribe(System.out::println, Throwable::printStackTrace);

    bus.onNext(Signal.next(1));
    bus.onNext(Signal.error(new RuntimeException()));
    bus.onNext(Signal.next(2));
    bus.onNext(Signal.next(3));

  }
}
