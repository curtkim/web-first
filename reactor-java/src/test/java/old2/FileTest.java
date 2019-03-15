package old2;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileTest {

  @Test
  public void test() throws IOException {
    Stream<String> lines = Files.lines(Paths.get("src/test/resources/dummy.txt"));
    List<String> results = lines.collect(Collectors.toList());
    System.out.println(results);
  }
}
