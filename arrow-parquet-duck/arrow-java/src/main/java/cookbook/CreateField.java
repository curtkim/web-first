package cookbook;

import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;

import java.util.ArrayList;
import java.util.List;

public class CreateField {
  public static void main(String[] args) {
    Field name = new Field("name", FieldType.nullable(new ArrowType.Utf8()), null);
    System.out.println(name);

    Field age = new Field("age", FieldType.nullable(new ArrowType.Int(32, true)), null);
    System.out.println(age);

    {
      FieldType intType = new FieldType(true, new ArrowType.Int(32, true), null);
      FieldType listType = new FieldType(true, new ArrowType.List(), null);
      Field childField = new Field("intCol", intType, null);
      List<Field> childFields = new ArrayList<>();
      childFields.add(childField);
      Field points = new Field("points", listType, childFields);

      System.out.println(points);
    }
  }
}
