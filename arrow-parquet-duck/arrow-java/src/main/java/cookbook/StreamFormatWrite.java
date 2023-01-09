package cookbook;

import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.ipc.ArrowStreamWriter;
import org.apache.arrow.vector.types.pojo.Schema;
import org.apache.arrow.vector.VectorSchemaRoot;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class StreamFormatWrite {
  public static void main(String[] args) {
    try (BufferAllocator rootAllocator = new RootAllocator()) {
      Field name = new Field("name", FieldType.nullable(new ArrowType.Utf8()), null);
      Field age = new Field("age", FieldType.nullable(new ArrowType.Int(32, true)), null);
      Schema schemaPerson = new Schema(asList(name, age));

      try (
          VectorSchemaRoot vectorSchemaRoot = VectorSchemaRoot.create(schemaPerson, rootAllocator)
      ) {
        VarCharVector nameVector = (VarCharVector) vectorSchemaRoot.getVector("name");
        nameVector.allocateNew(3);
        nameVector.set(0, "David".getBytes());
        nameVector.set(1, "Gladis".getBytes());
        nameVector.set(2, "Juan".getBytes());

        IntVector ageVector = (IntVector) vectorSchemaRoot.getVector("age");
        ageVector.allocateNew(3);
        ageVector.set(0, 10);
        ageVector.set(1, 20);
        ageVector.set(2, 30);

        vectorSchemaRoot.setRowCount(3);

        File file = new File("streaming_to_file.arrow");
        try (
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ArrowStreamWriter writer = new ArrowStreamWriter(vectorSchemaRoot, null, fileOutputStream.getChannel())
        ) {
          writer.start();
          writer.writeBatch();
          System.out.println("Number of rows written: " + vectorSchemaRoot.getRowCount());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
