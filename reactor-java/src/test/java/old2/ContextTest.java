package old2;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ContextTest {

  @Test
  public void test(){
    String key = "message";
    Mono<String> r = Mono.just("Hello")
        .flatMap( s -> Mono.subscriberContext().map( ctx -> s + " " + ctx.get(key)))
        .subscriberContext(ctx -> ctx.put(key, "World"));

    StepVerifier.create(r)
        .expectNext("Hello World")
        .verifyComplete();
  }

  @Test
  public void testFlux(){
    String key = "message";
    Flux<String> r = Flux.just("Hello", "Hi")
        .flatMap( s -> Mono.subscriberContext().map( ctx -> s + " " + ctx.get(key)))
        .subscriberContext(ctx -> ctx.put(key, "World"));

    StepVerifier.create(r)
        .expectNext("Hello World")
        .expectNext("Hi World")
        .verifyComplete();
  }


  @Test
  public void test2(){
    String key = "message";
    Mono<String> r = Mono.just("Hello")
        .subscriberContext(ctx -> ctx.put(key, "World"))
        .flatMap( s -> Mono.subscriberContext()
            .map( ctx -> s + " " + ctx.getOrDefault(key, "Stranger")));

    StepVerifier.create(r)
        .expectNext("Hello Stranger") // not World
        .verifyComplete();
  }

  @Test
  public void testImmutable(){
    String key = "message";

    Mono<String> r = Mono.subscriberContext()
        .map( ctx -> ctx.put(key, "Hello"))
        .flatMap( ctx -> Mono.subscriberContext())
        .map( ctx -> ctx.getOrDefault(key,"Default"));

    StepVerifier.create(r)
        .expectNext("Default")
        .verifyComplete();
  }
}
