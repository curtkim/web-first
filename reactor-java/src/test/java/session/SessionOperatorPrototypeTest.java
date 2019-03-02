package session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import reactor.core.publisher.Flux;

public class SessionOperatorPrototypeTest {

  @Test
  public void test() {
    Flux<UserLog> flux = Flux.just(
        new UserLog("A", 1),
        new UserLog("A", 2),

        new UserLog("A", 201),
        new UserLog("A", 202),
        new UserLog("A", 203),

        new UserLog("A", 501),
        new UserLog("A", 502),
        new UserLog("A", 503)
    );

    long TIMEOUT = 100;
    List<Session> sessionList = new ArrayList<>();

    Map<String, Session> map = new HashMap<>();
    flux.subscribe(log -> {

      long current = log.time;
      List<String> deleteKeys = new ArrayList<>();
      for (String key : map.keySet()) {
        Session session = map.get(key);
        if (current - session.end > TIMEOUT) {
          sessionList.add(session);
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
      System.out.println(err);
    }, () -> {
      for (String key : map.keySet()) {
        Session session = map.get(key);
        sessionList.add(session);
      }
    });

    System.out.println(sessionList);
  }
}
