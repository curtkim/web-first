import java.nio.file.Paths;
import reactor.core.publisher.Flux;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.BaseStream;
import java.util.List;

public class ReadFile {

  public static void main(String[] args) {
    Flux<String> flux = fluxVersion(Paths.get("bookshelf.txt"));
    flux.subscribe(it -> System.out.println(it + ";"));
    //System.out.println(flux.count().block());
  }

  private static Flux<String> fluxVersion(Path path) {
    final Runtime runtime = Runtime.getRuntime();

    return fromPath(path)
        .filter(s -> s.startsWith("Title: ") || s.startsWith("Author: ") || s.equalsIgnoreCase("##BOOKSHELF##"))
        .map(s -> s.replaceFirst("Title: ", ""))
        .map(s -> s.replaceFirst("Author: ", " by "))
        .windowWhile(s -> !s.contains("##"))
        .flatMap((Flux<String> bookshelf) -> bookshelf
            .window(2)
            .flatMap((Flux<String> bookInfo) -> bookInfo.reduce(String::concat))
            .collectList()
            .doOnNext(s -> System.gc())
            .flatMapMany((List<String> bookList) -> Flux.just(
                "\n\nFound new Bookshelf of " + bookList.size() + " books:",
                bookList.toString(),
                String.format("Memory in use while reading: %dMB\n", (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024))
            )));
  }

  private static Flux<String> fromPath(Path path) {
    return Flux.using(() -> Files.lines(path),
        Flux::fromStream,
        BaseStream::close
    );
  }

}
