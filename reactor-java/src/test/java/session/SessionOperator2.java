package session;

import org.reactivestreams.Publisher;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxOperator;

import java.util.*;

public class SessionOperator2 extends FluxOperator<UserLog, Flux<Session>> {

  static long TIMEOUT = 100;

  /**
   * Build a {@link FluxOperator} wrapper around the passed parent {@link Publisher}
   * @param source the {@link Publisher} to decorate
   */
  protected SessionOperator2(Flux<? extends UserLog> source) {
    super(source);
  }

  @Override
  public void subscribe(CoreSubscriber<? super Flux<Session>> actual) {

    Map<String, Session> map = new HashMap<>();

    source.subscribe(log -> {

      long current = log.time;
      List<String> deleteKeys = new ArrayList<>();
      for (String key : map.keySet()) {
        Session session = map.get(key);
        if (current - session.end > TIMEOUT) {
          deleteKeys.add(key);
        }
      }

      List<Session> list = new ArrayList<>();
      for (String key : deleteKeys) {
        list.add(map.remove(key));
      }
      actual.onNext(Flux.fromIterable(list));

      if (map.containsKey(log.userId)) {
        map.get(log.userId).end = log.time;
      } else {
        map.put(log.userId, new Session(log.userId, log.time, log.time));
      }

    }, err -> {
      actual.onError(err);
    }, () -> {
      actual.onNext(Flux.fromIterable(map.values()));
      actual.onComplete();
    }, (subscription)->{
      actual.onSubscribe(subscription);
    });

  }

}
