import reactor.core.publisher.Flux;

public class MultipleSubscribe {
  public static void main(String[] args) {
    Flux<Integer> flux = Flux.range(1, 10).log();
    flux.map(it -> it*2).subscribe(System.out::println);
    flux.subscribe(System.out::println);
  }
}
