package job;

import java.io.FileWriter;
import java.io.IOException;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.scheduler.Scheduler;

public class FileWriterSubscriber implements Subscriber<Object> {

  private FileWriter writer;
  private Scheduler scheduler;
  private Subscription subscription;

  public FileWriterSubscriber(FileWriter writer, Scheduler scheduler) {
    this.writer = writer;
    this.scheduler = scheduler;
  }

  @Override
  public void onSubscribe(Subscription s) {
    this.subscription = s;
    subscription.request(1);
  }

  @Override
  public void onNext(Object aLong) {
    System.out.println(Thread.currentThread().getName() + " " + aLong);
    try {
      writer.write(aLong + "\n");
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    subscription.request(1);
  }

  @Override
  public void onError(Throwable t) {
    try {
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onComplete() {
    System.out.println(Thread.currentThread().getName() + " complete");
    try {
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    scheduler.dispose();
  }
}