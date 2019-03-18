package session;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Function;

public class SessionOperator2Test {

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

    StepVerifier.create(new SessionOperator2(flux).flatMap(Function.identity()))
        .expectNext(new Session("A", 1, 2))
        .expectNext(new Session("A", 201, 203))
        .expectNext(new Session("A", 501, 503))
        .expectComplete()
        .verify();
  }
}
