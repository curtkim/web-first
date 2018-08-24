import reactor.core.publisher.Flux;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.BaseStream;

public class ReadFile {

  private static Flux<String> fluxVersion(Path path) {
    final Runtime runtime = Runtime.getRuntime();

    return fromPath(path)
        .filter(s -> s.startsWith("Title: ") || s.startsWith("Author: ")
            || s.equalsIgnoreCase("##BOOKSHELF##"))
        .map(s -> s.replaceFirst("Title: ", ""))
        .map(s -> s.replaceFirst("Author: ", " by "))
        .windowWhile(s -> !s.contains("##"))
        .flatMap(bookshelf -> bookshelf
            .window(2)
            .flatMap(bookInfo -> bookInfo.reduce(String::concat))
            .collectList()
            .doOnNext(s -> System.gc())
            .flatMapMany(bookList -> Flux.just(
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
