package old2;

import java.util.stream.IntStream;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

// https://zoltanaltfatter.com/2018/08/26/subscribeOn-publishOn-in-Reactor/
public class SubscribeOnPublishOn2 {

  public static void createSubscribers(Flux<Integer> flux) {
    IntStream.range(1, 2).forEach(value ->
        flux.subscribe(integer -> System.out.println(value + " consumer processed "
            + integer + " using thread: " + Thread.currentThread().getName())));
  }

  public static void main(String[] args) throws InterruptedException {
    Flux<Integer> flux3 = Flux.range(0, 2)
        // this is influenced by subscribeOn
        .doOnNext(s -> System.out.println(s + " before publishOn using thread: " + Thread.currentThread().getName()))
        .publishOn(Schedulers.elastic())
        // the rest is influenced by publishOn
        .doOnNext(s -> System.out.println(s + " after publishOn using thread: " + Thread.currentThread().getName()))
        .subscribeOn(Schedulers.single());
    createSubscribers(flux3);

    //Thread.sleep(2*1000);
  }

}
