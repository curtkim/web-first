import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Function;

public class TransformCompose {

  boolean toUpper = false;

  Function<Flux<String>, Publisher<String>> statefullFun = flux -> {
    Flux<String> f = flux.filter(color -> color.equalsIgnoreCase("red"));
    if(toUpper)
      return f.map(String::toUpperCase);
    return f;
  };

  @Test
  // flux 생성시점에 적용된다.
  public void transform() {
    toUpper = false;
    Flux<String> f = Flux.just("red", "green", "blue").transform(statefullFun);

    StepVerifier.create(f)
        .expectSubscription()
        .expectNext("red")
        .verifyComplete();

    StepVerifier.create(f)
        .expectSubscription()
        .expectNext("red")
        .verifyComplete();
  }

  @Test
  // subscription 시점에 operator가 적용된다.
  public void compose() {
    toUpper = false;
    Flux<String> f = Flux.just("red", "green", "blue").compose(statefullFun);

    toUpper = true;
    StepVerifier.create(f)
        .expectSubscription()
        .expectNext("RED")
        .verifyComplete();

    toUpper = false;
    StepVerifier.create(f)
        .expectSubscription()
        .expectNext("red")
        .verifyComplete();
  }
}
