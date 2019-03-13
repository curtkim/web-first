package job;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.function.Function;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class Job implements Runnable {

  int interval;
  int duration;

  Function<Long, Object> converter;
  String filename;

  public Job(int interval, int duration, Function<Long, Object> converter, String filename) {
    this.interval = interval;
    this.duration = duration;
    this.filename = filename;
    this.converter = converter;
  }

  FileWriter newWriter() {
    return newWriter(filename);
  }

  static FileWriter newWriter(String filename) {
    try {
      return new FileWriter(filename);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    Scheduler single = Schedulers.newSingle(filename);
    Flux.interval(Duration.ofSeconds(interval), single)
        .take(Duration.ofSeconds(duration), single)
        .map(converter)
        .subscribe(new FileWriterSubscriber(newWriter(), single));
  }

  public static void main(String[] args) {
    Job job = new Job(1, 10, it -> it*2, "job1.txt");
    new Thread(job).start();

    Job job2 = new Job(2, 15, it -> it, "job2.txt");
    new Thread(job2).start();

    System.out.println(Thread.currentThread().getName() + " start");
  }
}
