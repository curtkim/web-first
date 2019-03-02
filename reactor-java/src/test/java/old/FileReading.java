package old;

import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.BaseStream;

//https://simonbasle.github.io/2017/10/file-reading-in-reactor/
public class FileReading {
    private static Flux<String> fromPath(Path path) {
        return Flux.using(
                () -> Files.lines(path),
                Flux::fromStream,
                BaseStream::close
        );
    }

    private static void listVersion(Path path) throws IOException {
        final Runtime runtime = Runtime.getRuntime(); // <1>

        List<String> wholeData = Files.readAllLines(path);
        List<String> books = new ArrayList<>();
        Iterator<String> iter = wholeData.iterator();
        String title = null;
        while(iter.hasNext()) {
            String line = iter.next();

            if (line.startsWith("Title: ")) {
                title = line.replaceFirst("Title: ", "");
            }
            else if (line.startsWith("Author: ")) {
                String author = line.replaceFirst("Author: ", " by ");
                books.add(title + author);
                title = null;
            }
            else if (line.equalsIgnoreCase("##BOOKSHELF##")) {
                System.gc(); // <2>
                System.out.println("\n\nFound new bookshelf of " + books.size() + " books:");
                System.out.println(books);
                System.out.printf("Memory in use while reading: %dMB\n",  // <3>
                        (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024));
                books.clear();
            }
        }
    }

    private static Flux<String> fluxVersion(Path path) {
        final Runtime runtime = Runtime.getRuntime();

        return fromPath(path)
                .filter(s -> s.startsWith("Title: ") || s.startsWith("Author: ") || s.equalsIgnoreCase("##BOOKSHELF##"))
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
}
