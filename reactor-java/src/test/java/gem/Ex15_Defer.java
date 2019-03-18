package gem;

import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

//??
public class Ex15_Defer {

  @Test
  public void test() {
    Supplier<Flux<Integer>> supplier = () -> Flux.range( (int)(System.currentTimeMillis() & 1023), 5);

    defer(supplier)
        .subscribe(System.out::println);
  }

  static Flux defer(Supplier<Flux<Integer>> supplier){
    return Flux.just("whatever").flatMap(v -> supplier.get());
  }
}
