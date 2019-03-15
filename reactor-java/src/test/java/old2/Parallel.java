package old2;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class Parallel {

  //https://www.e4developer.com/2018/04/28/springs-webflux-reactor-parallelism-and-backpressure/
  public static void main(String[] args){
    Flux.range(1, 5)
        .parallel(8)
        .runOn(Schedulers.parallel())
        .map(it -> {
          System.out.println(Thread.currentThread().getName());
          return it*2;
        })
        .sequential()
        .subscribe(i -> System.out.println(Thread.currentThread().getName() + " " + i));
  }
}
