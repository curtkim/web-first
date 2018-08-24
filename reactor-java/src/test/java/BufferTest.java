import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;

public class BufferTest {
  @Test
  public void test0(){
    StepVerifier.create(Flux.just(1, 2, 3, 4, 5, 6).bufferUntil((i)-> i%2 == 0))
        .expectNext(Arrays.asList(1,2))
        .expectNext(Arrays.asList(3,4))
        .expectNext(Arrays.asList(5,6))
        .expectComplete()
        .verify();
  }

}
