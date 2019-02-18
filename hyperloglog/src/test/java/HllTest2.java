import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import net.agkn.hll.HLL;
import org.assertj.core.data.Offset;
import org.junit.Test;

public class HllTest2 {

  long generateUserId(){
    return (long)(Math.random()*20_000_000);
  }

  List<Long> generate(int uniq, int size){
    List<Long> uniqList = new ArrayList<>();
    for(int i = 0; i < uniq; i++)
      uniqList.add(generateUserId());

    List<Long> list = new ArrayList<>();
    for(int i = 0; i < size; i++)
      list.add( uniqList.get( (int)(Math.random()*uniq) ));

    return list;
  }

  @Test
  public void test(){
    List<Long> results = new ArrayList<>();
    for(int i = 0; i < 100; i++)
      results.add(testInner());

    Comparator<Long> comp = (a,b)-> (int)(a-b);

    long max = results.stream().max(comp).get();
    long min = results.stream().min(comp).get();
    System.out.println(min + " " + max);
  }

  long testInner(){
    int uniq = 1000;
    HashFunction hashFunction = Hashing.murmur3_128();

    //Set<Long> set = new HashSet<>();
    HLL hll = new HLL(10/*log2m*/, 4/*registerWidth*/);

    for (long id : generate(uniq, uniq*10)) {
      //long hashedValue = hashFunction.newHasher().putLong(id).hash().asLong();
      long hashedValue = hashFunction.hashBytes((id+"").getBytes()).asLong();
      hll.addRaw(hashedValue);
      //set.add(id);
    }

    long cardinality = hll.cardinality();
    System.out.println(cardinality + " " + uniq+ " " + hll.toBytes().length + " bytes");
    return cardinality - uniq;
  }
}
