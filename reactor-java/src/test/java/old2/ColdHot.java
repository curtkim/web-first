package old2;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ColdHot {

  @Test
  public void test0(){
    List<Integer> elements = new ArrayList<>();
    List<Integer> elements2 = new ArrayList<>();

    Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5, 6);

    Flux<Integer> flux2 = flux.filter(x -> x % 2 == 0);
    Flux<Integer> flux3 = flux.filter(x -> x % 3 == 0);
    flux2.subscribe(elements::add);
    flux3.subscribe(elements2::add);

    assertThat(elements).containsExactly(2,4,6);
    assertThat(elements2).containsExactly(3,6);
  }

  @Test
  public void test1(){
    List<Integer> elements = new ArrayList<>();
    List<Integer> elements2 = new ArrayList<>();

    Flux<Integer> flux = Flux.just(1, 2, 3, 4);

    flux.log().subscribe(elements::add);
    flux.subscribe(elements2::add);

    assertThat(elements).containsExactly(1, 2, 3, 4);
    assertThat(elements2).containsExactly(1, 2, 3, 4);
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
          public void onError(Throwable t) {}

          @Override
          public void onComplete() {}
        });

    assertThat(elements).containsExactly(1, 2, 3, 4);
  }

  @Test
  public void map(){
    List<Integer> elements = new ArrayList<>();
    Flux.just(1, 2, 3, 4)
        .log()
        .map(i -> i * 2)
        .subscribe(elements::add);
    assertThat(elements).containsExactly(2, 4, 6, 8);
  }

  @Test
  public void zip(){
    List<String> elements = new ArrayList<>();

    Flux.just(1, 2, 3, 4)
        .log()
        .map(i -> i * 2)
        .zipWith(Flux.range(10, 100),
            (two, one) -> String.format("First Flux: %d, Second Flux: %d", one, two))
        .subscribe(elements::add);

    assertThat(elements).containsExactly(
        "First Flux: 10, Second Flux: 2",
        "First Flux: 11, Second Flux: 4",
        "First Flux: 12, Second Flux: 6",
        "First Flux: 13, Second Flux: 8");
  }

  @Test
  public void multi_thread() throws InterruptedException {
    List<Integer> elements = new ArrayList<>();
    Flux.just(1, 2, 3, 4)
        .log()
        .map(i -> i * 2)
        .subscribeOn(Schedulers.parallel())
        .subscribe(elements::add);

    Thread.sleep(1000);
    System.out.println(elements);
  }

}
