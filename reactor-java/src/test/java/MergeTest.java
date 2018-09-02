import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class MergeTest {

  @Test
  public void test(){
    StepVerifier.create(Flux.mergeOrdered((a, b)-> a-b, Flux.just(3,4,5), Flux.just(1,2,3)))
      .expectNext(1,2,3,3,4,5)
      .expectComplete()
      .verify();
  }
}
