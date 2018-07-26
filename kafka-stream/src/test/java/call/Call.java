package call;

import java.io.Serializable;

public class Call implements Serializable{
  long id;
  double lng;
  double lat;
  long distance;

  Call() {}

  public Call(long id, double lng, double lat, long distance) {
    this.id = id;
    this.lng = lng;
    this.lat = lat;
    this.distance = distance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Call call = (Call) o;

    if (id != call.id) return false;
    if (Double.compare(call.lng, lng) != 0) return false;
    if (Double.compare(call.lat, lat) != 0) return false;
    return distance == call.distance;

  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = (int) (id ^ (id >>> 32));
    temp = Double.doubleToLongBits(lng);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(lat);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (int) (distance ^ (distance >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "Call{" +
        "id=" + id +
        ", lng=" + lng +
        ", lat=" + lat +
        ", distance=" + distance +
        '}';
  }
}
