package session;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class SessionOperatorTest {

  @Test
  public void test() {
    Flux<UserLog> flux = Flux.just(
        new UserLog("A", 1),
        new UserLog("A", 2),

        new UserLog("A", 201),
        new UserLog("A", 202),
        new UserLog("A", 203),

        new UserLog("A", 501),
        new UserLog("A", 502),
        new UserLog("A", 503)
    );

    StepVerifier.create(new SessionOperator(flux))
        .expectNext(new Session("A", 1, 2))
        .expectNext(new Session("A", 201, 203))
        .expectNext(new Session("A", 501, 503))
        .expectComplete()
        .verify();
    //new SessionOperator(flux).subscribe(s -> System.out.println(s), (err)-> {}, ()-> {System.out.println("end");});


  }
}
