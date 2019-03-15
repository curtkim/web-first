package old2;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

//https://medium.com/@cheron.antoine/reactor-java-4-how-to-take-control-over-the-execution-of-mono-and-flux-ead31dc066
public class SubscribeOnPublishOn {

  private <T> T identityWithThreadLogging(T el, String operation) {
    System.out.println(operation + " -- " + el + " -- " +
        Thread.currentThread().getName());
    return el;
  }

  @Test
  public void flatMapWithoutChangingScheduler() throws InterruptedException {
    Flux.range(1, 3)
        .map(n -> identityWithThreadLogging(n, "map1"))
        .flatMap(n -> Mono.just(n).map(nn -> identityWithThreadLogging(nn, "mono")))
        .subscribeOn(Schedulers.parallel())
        .subscribe(n -> {
          this.identityWithThreadLogging(n, "subscribe");
          System.out.println(n);
        });

    Thread.sleep(1000);
  }
}
