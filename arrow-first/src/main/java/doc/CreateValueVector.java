package doc;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.VarCharVector;

import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;

import java.util.HashMap;
import java.util.Map;

import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.Schema;
import java.util.HashMap;
import java.util.Map;
import static java.util.Arrays.asList;

public class CreateValueVector {
  public static void main(String[] args) {

    try (
        BufferAllocator allocator = new RootAllocator();
        IntVector intVector = new IntVector("fixed-size-primitive-layout", allocator);
    ) {
      intVector.allocateNew(3);
      intVector.set(0, 1);
      intVector.setNull(1);
      intVector.set(2, 2);
      intVector.setValueCount(3);
      System.out.println("Vector created in memory: " + intVector);
    }

    try (
        BufferAllocator allocator = new RootAllocator();
        VarCharVector varCharVector = new VarCharVector("variable-size-primitive-layout", allocator);
    ) {
      varCharVector.allocateNew(3);
      varCharVector.set(0, "one".getBytes());
      varCharVector.set(1, "two".getBytes());
      varCharVector.set(2, "three".getBytes());
      varCharVector.setValueCount(3);
      System.out.println("Vector created in memory: " + varCharVector);
    }

    {
      Map<String, String> metadata = new HashMap<>();
      metadata.put("A", "Id card");
      metadata.put("B", "Passport");
      metadata.put("C", "Visa");
      Field document = new Field("document",
          new FieldType(true, new ArrowType.Utf8(), /*dictionary*/ null, metadata),
          /*children*/ null);
      System.out.println("Field created: " + document + ", Metadata: " + document.getMetadata());
    }

    {
      Map<String, String> metadata = new HashMap<>();
      metadata.put("K1", "V1");
      metadata.put("K2", "V2");
      Field a = new Field("A", FieldType.nullable(new ArrowType.Int(32, true)), /*children*/ null);
      Field b = new Field("B", FieldType.nullable(new ArrowType.Utf8()), /*children*/ null);
      Schema schema = new Schema(asList(a, b), metadata);
      System.out.println("Schema created: " + schema);
    }
  }
}
