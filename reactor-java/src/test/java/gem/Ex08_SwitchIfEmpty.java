package gem;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Ex08_SwitchIfEmpty {

  @Test
  public void test() {
    Flux source = Flux.empty();

    source
        .switchIfEmpty(Mono.just("A"))
        .subscribe(System.out::println);
  }
}
