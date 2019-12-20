import org.junit.Test;
import reactor.core.publisher.Flux;

public class UnicastProblem {

  @Test
  // window가 UnicastProcessor를 사용하고
  // window.all에서 한번 소비했기 때문에
  // 에러가 발생한다.
  public void test() {
    try {
      Flux.just(4, 2, 6, 4, 5, 6, 7, 8, 9)
          .window(3)
          .filterWhen(window -> window.all(n -> n % 2 == 0))
          .flatMap(window -> window)
          .subscribe(System.out::println);
    } catch (Exception ex){
      return;
    }
  }

}