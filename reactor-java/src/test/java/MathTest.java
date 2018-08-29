import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.math.MathFlux;
import reactor.test.StepVerifier;

public class MathTest {

  @Test
  public void test1(){
    StepVerifier.create(MathFlux.max(Flux.range(1, 100)))
        .expectNext(100)
        .expectComplete()
        .verify();

    StepVerifier.create(MathFlux.sumInt(Flux.range(1, 10)))
        .expectNext(55)
        .expectComplete()
        .verify();

    StepVerifier.create(MathFlux.averageDouble(Flux.range(1, 10)))
        .expectNext(5.5)
        .expectComplete()
        .verify();
  }
}
