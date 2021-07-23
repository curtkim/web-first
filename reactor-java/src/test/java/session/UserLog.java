package session;

public class UserLog {
  String userId;
  long time;

  public UserLog(String userId, long time) {
    this.userId = userId;
    this.time = time;
  }

  @Override
  public String toString() {
    return "UserLog{" +
        "userId='" + userId + '\'' +
        ", time=" + time +
        '}';
  }
}
