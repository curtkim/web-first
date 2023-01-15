import java.io.IOException;
import java.nio.file.Paths;

//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.Path;
import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter;
import org.apache.parquet.format.converter.ParquetMetadataConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.io.ColumnIOFactory;
import org.apache.parquet.io.MessageColumnIO;
import org.apache.parquet.io.RecordReader;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;

public class ReadParquetFile {

  //private static Path path = new Path("userdata1.parquet");

  private static void printGroup(Group g) {
    int fieldCount = g.getType().getFieldCount();
    for (int field = 0; field < fieldCount; field++) {
      int valueCount = g.getFieldRepetitionCount(field);

      Type fieldType = g.getType().getType(field);
      String fieldName = fieldType.getName();

      for (int index = 0; index < valueCount; index++) {
        if (fieldType.isPrimitive()) {
          //System.out.println(fieldName + " " + g.getValueToString(field, index));
        }
      }
    }
    //System.out.println("");
  }

  public static void main(String[] args) throws IllegalArgumentException {

    //Configuration conf = new Configuration();

    try {
      ParquetFileReader r = new ParquetFileReader(new LocalInputFile(Paths.get("userdata1.parquet")), ParquetReadOptions.builder().build());
      ParquetMetadata readFooter = r.getFooter();
      System.out.println("meta key/value");
      for(String key : readFooter.getFileMetaData().getKeyValueMetaData().keySet()) {
        System.out.println(key +" : " + readFooter.getFileMetaData().getKeyValueMetaData().get(key));
      }

      MessageType schema = readFooter.getFileMetaData().getSchema();

      System.out.println("schema");
      System.out.println(schema);

      PageReadStore pages = null;
      try {
        while (null != (pages = r.readNextRowGroup())) {
          final long rows = pages.getRowCount();
          System.out.println("Number of rows: " + rows);

          final MessageColumnIO columnIO = new ColumnIOFactory().getColumnIO(schema);
          final RecordReader recordReader = columnIO.getRecordReader(pages, new GroupRecordConverter(schema));
          for (int i = 0; i < rows; i++) {
            final Group g = (Group)recordReader.read();
            System.out.println(i);
            printGroup(g);

            // TODO Compare to System.out.println(g);
          }
        }
      } finally {
        r.close();
      }
    } catch (IOException e) {
      System.out.println("Error reading parquet file.");
      e.printStackTrace();
    }
  }
}
