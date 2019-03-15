package old2;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Objects;

public class ComplexTest {

  @Test
  public void window_concat_groupby_concat(){
    Flux<Summary> flux = Flux.range(0, 3*60)
        .map(i -> new Rec(i, i%2 == 0 ? "A" : "B", 1))
        .windowUntil( rec -> rec.time % 60 == 0, true)
        .flatMap((Flux<Rec> f)->
            f.groupBy(r -> r.kind)
            .flatMap(groupedFlux ->
                groupedFlux.reduce(
                    new Summary(0, groupedFlux.key(), 0),
                    (a, b)-> a.add(b)))
        );

    StepVerifier.create(flux)
        .expectNext(new Summary(0, "A", 30), new Summary(0, "B", 30))
        .expectNext(new Summary(60, "A", 30), new Summary(60, "B", 30))
        .expectNext(new Summary(120, "A", 30), new Summary(120, "B", 30))
        .expectComplete()
        .verify();
  }
}

class Summary {
  long time;
  String kind;
  int sum;

  public Summary(long time, String kind, int sum) {
    this.time = time;
    this.kind = kind;
    this.sum = sum;
  }

  public Summary add(Rec rec){
    long truncatedTime = rec.time/60*60;
    return new Summary(truncatedTime, this.kind, this.sum + rec.value);
  }

  @Override
  public String toString() {
    return "old2.Summary{" +
        "time=" + time +
        ", kind='" + kind + '\'' +
        ", sum=" + sum +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Summary summary = (Summary) o;
    return time == summary.time &&
        sum == summary.sum &&
        Objects.equals(kind, summary.kind);
  }

  @Override
  public int hashCode() {

    return Objects.hash(time, kind, sum);
  }
}

class Rec {
  long time;
  String kind;
  int value;

  public Rec(long time, String kind, int value) {
    this.time = time;
    this.kind = kind;
    this.value = value;
  }
}