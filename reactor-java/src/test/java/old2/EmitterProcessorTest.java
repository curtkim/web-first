package old2;

import org.junit.Test;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.FluxSink;

public class EmitterProcessorTest {

  @Test
  public void test(){
    EmitterProcessor<Integer> emitter = EmitterProcessor.create();
    FluxSink sink = emitter.sink();
    sink.next(1);
    sink.next(2);
    emitter.subscribe(System.out::println);
    emitter.subscribe(System.err::println);
    sink.next(3);
    sink.complete();
  }
}
