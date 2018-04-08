import org.apache.avro.Schema;
import org.apache.avro.SchemaCompatibility;

import org.junit.Assert;
import java.io.File;
import java.io.IOException;


public class CheckCompatibility {
  public static void main(String[] args) throws IOException {
    Schema schema = new Schema.Parser().parse(new File("src/main/avro/user.avsc"));
    Schema oldSchema = new Schema.Parser().parse(new File("src/main/user_old1.avsc"));

    SchemaCompatibility.SchemaPairCompatibility compatResult = SchemaCompatibility.checkReaderWriterCompatibility(schema, oldSchema);
    System.out.println(compatResult.getDescription());
    Assert.assertEquals(SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE, compatResult.getType());
  }
}
