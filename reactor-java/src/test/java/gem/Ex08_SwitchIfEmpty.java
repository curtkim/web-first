package gem;

import org.junit.Test;
import reactor.core.publisher.Flux;

public class Ex08_SwitchIfEmpty {

  @Test
  public void test() {
    Flux source = Flux.empty();

    source
        .switchIfEmpty(Flux.empty().doOnComplete(() -> System.out.println("Empty source!")))
        .subscribe(System.out::println);
  }
}
