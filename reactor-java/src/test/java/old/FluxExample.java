package old;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Java6Assertions.assertThat;


public class FluxExample {

  @Test
  public void test1(){
    List<Integer> elements = new ArrayList<>();

    Flux.just(1, 2, 3, 4)
        .log()
        .subscribe(elements::add);

    assertThat(elements).containsExactly(1, 2, 3, 4);
  }

  @Test
  public void test2(){
    List<Integer> elements = new ArrayList<>();
    Flux.just(1, 2, 3, 4)
        .log()
        .subscribe(new Subscriber<Integer>() {
          @Override
          public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
          }

          @Override
          public void onNext(Integer integer) {
            elements.add(integer);
          }

          @Override
          public void onError(Throwable t) {}

          @Override
          public void onComplete() {}
        });

    assertThat(elements).containsExactly(1, 2, 3, 4);
  }

  @Test
  public void backpressure(){
    List<Integer> elements = new ArrayList<>();
    Flux.just(1, 2, 3, 4)
        .log()
        .subscribe(new Subscriber<Integer>() {
          private Subscription s;
          int onNextAmount;

          @Override
          public void onSubscribe(Subscription s) {
            this.s = s;
            s.request(2);
          }

          @Override
          public void onNext(Integer integer) {
            elements.add(integer);
            onNextAmount++;
            if (onNextAmount % 2 == 0) {
              s.request(2);
            }
          }

          @Override
          public void onError(Throwable t) {
          }

          @Override
          public void onComplete() {}
        });
    assertThat(elements).containsExactly(1, 2, 3, 4);
  }

  @Test
  public void transform(){
    List<Integer> elements = new ArrayList<>();
    Flux.just(1,2,3,4)
        .log()
        .map(i-> i*2)
        .subscribe(elements::add);
    assertThat(elements).containsExactly(2, 4, 6, 8);
  }

  @Test
  public void connectableFlux() {
    ConnectableFlux<Object> publish = Flux.create(fluxSink -> {
      while (true) {
        fluxSink.next(System.currentTimeMillis());
      }
    }).publish();

    publish.subscribe(System.out::println);
    publish.subscribe(System.out::println);

    publish.connect();
  }

  @Test
  public void throttling(){
    ConnectableFlux<Object> publish = Flux.create(fluxSink -> {
      while(true) {
        fluxSink.next(System.currentTimeMillis());
      }
    })
    .sample(ofSeconds(2))
    .publish();

    publish.subscribe(System.out::println);
    publish.connect();
  }

  @Test
  public void concurrency(){
    List<Integer> elements = new ArrayList<>();
    Flux.just(1, 2, 3, 4)
        .log()
        .map(i -> i * 2)
        .subscribeOn(Schedulers.parallel())
        .subscribe(elements::add);

    assertThat(elements).containsExactly(2, 4, 6, 8);
  }
}
