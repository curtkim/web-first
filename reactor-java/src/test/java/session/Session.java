package session;

import java.util.Objects;

public class Session {
  String userId;
  long start;
  long end;

  public Session(String userId, long start, long end) {
    this.userId = userId;
    this.start = start;
    this.end = end;
  }

  @Override
  public String toString() {
    return "Session{" +
        "userId='" + userId + '\'' +
        ", start=" + start +
        ", end=" + end +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Session session = (Session) o;
    return start == session.start &&
        end == session.end &&
        userId.equals(session.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, start, end);
  }
}
