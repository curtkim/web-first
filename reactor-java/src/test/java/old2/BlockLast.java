package old2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import reactor.core.publisher.Flux;

public class BlockLast {

  @Test
  public void test(){
    int result = Flux.range(1, 10).blockLast();
    assertEquals(10, result);

    result = Flux.range(1, 10).blockFirst();
    assertEquals(1, result);
  }

}
