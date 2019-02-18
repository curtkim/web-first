package session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxOperator;

public class SessionOperator extends FluxOperator<UserLog, Session> {

  static long TIMEOUT = 100;

  /**
   * Build a {@link FluxOperator} wrapper around the passed parent {@link Publisher}
   * @param source the {@link Publisher} to decorate
   */
  protected SessionOperator(Flux<? extends UserLog> source) {
    super(source);
  }

  @Override
  public void subscribe(CoreSubscriber<? super Session> actual) {

    Map<String, Session> map = new HashMap<>();

    source.subscribe(log -> {

      long current = log.time;
      List<String> deleteKeys = new ArrayList<>();
      for (String key : map.keySet()) {
        Session session = map.get(key);
        if (current - session.end > TIMEOUT) {
          actual.onNext(session);
          deleteKeys.add(key);
        }
      }

      for (String key : deleteKeys) {
        map.remove(key);
      }

      if (map.containsKey(log.userId)) {
        map.get(log.userId).end = log.time;
      } else {
        map.put(log.userId, new Session(log.userId, log.time, log.time));
      }
    }, err -> {
      actual.onError(err);
    }, () -> {
      for (String key : map.keySet()) {
        Session session = map.get(key);
        actual.onNext(session);
        actual.onComplete();
      }
    }, (subscription)->{
      actual.onSubscribe(subscription);
    });

  }
}
