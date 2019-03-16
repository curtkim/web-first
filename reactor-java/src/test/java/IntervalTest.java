import java.time.Duration;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class IntervalTest {

  @Test
  public void test(){
    Scheduler single = Schedulers.single();
    System.out.println(Thread.currentThread().getName());
    Flux.interval(Duration.ofSeconds(1), single)
        .publishOn(single)
        .doOnComplete(()-> single.dispose())
        .subscribe(System.out::println);

  }
}
