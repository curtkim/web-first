package old2;

import java.util.concurrent.Callable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class AsyncMain {

  public static void main(String[] args) throws InterruptedException {

    long starTime = System.currentTimeMillis();

    Scheduler single = Schedulers.newSingle("single");

    Flux.just(1,3,5,7,9)
        .flatMap(it -> {
          System.out.println(it + " " + Thread.currentThread().getName());
          return Mono.fromCallable(new MyCallable()).publishOn(Schedulers.elastic());
        })
        .publishOn(single)
        .subscribe(
            it -> System.out.println(it),
            err -> System.out.println(err),
            ()-> {
              System.out.println("done " + Thread.currentThread().getName() + " " + (System.currentTimeMillis() - starTime));
              single.dispose();
            });
  }
}

class MyCallable implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {
    int random = (int)(Math.random() * 1000);
    Thread.sleep(random);
    System.out.println(Thread.currentThread().getName() + " " + random);
    return random;
  }
}
