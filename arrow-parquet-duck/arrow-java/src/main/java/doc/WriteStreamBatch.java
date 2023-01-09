package doc;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.*;
import org.apache.arrow.vector.dictionary.Dictionary;
import org.apache.arrow.vector.dictionary.DictionaryEncoder;
import org.apache.arrow.vector.dictionary.DictionaryProvider;
import org.apache.arrow.vector.ipc.ArrowStreamReader;
import org.apache.arrow.vector.ipc.ArrowStreamWriter;
import org.apache.arrow.vector.types.pojo.DictionaryEncoding;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.Schema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WriteStreamBatch {


  public static void main(String[] args) throws IOException {

    BufferAllocator allocator = new RootAllocator();

    BitVector bitVector = new BitVector("boolean", allocator);
    VarCharVector varCharVector = new VarCharVector("varchar", allocator);
    for (int i = 0; i < 10; i++) {
      bitVector.setSafe(i, i % 2 == 0 ? 0 : 1);
      varCharVector.setSafe(i, ("test" + i).getBytes(StandardCharsets.UTF_8));
    }
    bitVector.setValueCount(10);
    varCharVector.setValueCount(10);

    List<Field> fields = Arrays.asList(bitVector.getField(), varCharVector.getField());
    List<FieldVector> vectors = Arrays.asList(bitVector, varCharVector);
    VectorSchemaRoot root = new VectorSchemaRoot(fields, vectors);


    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ArrowStreamWriter writer = new ArrowStreamWriter(root, /*DictionaryProvider=*/null, Channels.newChannel(out));


    writer.start();
    // write the first batch
    writer.writeBatch();

    // write another four batches.
    for (int i = 0; i < 4; i++) {
      // populate VectorSchemaRoot data and write the second batch
      BitVector childVector1 = (BitVector) root.getVector(0);
      VarCharVector childVector2 = (VarCharVector) root.getVector(1);
      childVector1.reset();
      childVector2.reset();
      //... do some populate work here, could be different for each batch
      writer.writeBatch();
    }
    // end
    writer.end();


    try (ArrowStreamReader reader = new ArrowStreamReader(new ByteArrayInputStream(out.toByteArray()), allocator)) {
      Schema schema = reader.getVectorSchemaRoot().getSchema();
      for (int i = 0; i < 5; i++) {
        // This will be loaded with new values on every call to loadNextBatch
        VectorSchemaRoot readBatch = reader.getVectorSchemaRoot();
        reader.loadNextBatch();
        //... do something with readBatch
      }

    }
  }
}
