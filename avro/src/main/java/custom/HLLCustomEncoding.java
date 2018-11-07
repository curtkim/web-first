package custom;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import net.agkn.hll.HLL;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

public class HLLCustomEncoding extends CustomEncoding<HLL> {

  public HLLCustomEncoding() {
    List<Schema> union = Arrays.asList(
        Schema.create(Schema.Type.NULL),
        Schema.create(Schema.Type.BYTES));
    //union.get(1).addProp("CustomEncoding", "UUIDAsBytesEncoding");
    schema = Schema.createUnion(union);
  }

  @Override
  protected void write(Object datum, Encoder out) throws IOException {
    if(datum != null) {
      // encode the position of the data in the union
      out.writeInt(1);
      HLL hll = (HLL)datum;
      out.writeBytes(hll.toBytes());
    } else {
      // position of null in union
      out.writeInt(0);
    }
  }

  @Override
  protected HLL read(Object reuse, Decoder in) throws IOException {
    // get index in union
    int index = in.readIndex();

    if (index == 1) {
      ByteBuffer b = in.readBytes(null);
      return HLL.fromBytes(b.array());
    } else {
      // no uuid present
      return null;
    }
  }
}
