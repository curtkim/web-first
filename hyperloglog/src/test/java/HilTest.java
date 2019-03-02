import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import net.agkn.hll.HLL;
import org.assertj.core.data.Offset;
import org.junit.Test;

import java.util.stream.LongStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class HilTest {

  @Test
  public void test(){
    HashFunction hashFunction = Hashing.murmur3_128();
    long numberOfElements = 100_000;
    long toleratedDifference = 1_000;
    HLL hll = new HLL(13, 5);
    System.out.println("init bytes=" + hll.toBytes().length);

    LongStream.range(0, numberOfElements).forEach(element -> {
          long hashedValue = hashFunction.newHasher().putLong(element).hash().asLong();
          hll.addRaw(hashedValue);
        }
    );

    System.out.println("end bytes=" + hll.toBytes().length);

    long cardinality = hll.cardinality();
    assertThat(cardinality)
        .isCloseTo(numberOfElements, Offset.offset(toleratedDifference));

  }

}
