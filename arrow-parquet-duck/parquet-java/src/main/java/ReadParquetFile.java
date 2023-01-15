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

  private static void printGroup(Group group) {
    int fieldCount = group.getType().getFieldCount();
    int valueCount = group.getFieldRepetitionCount(0);

    for(int value=0; value < valueCount; value++){
      for(int field=0; field < 3; field++){
        System.out.print(group.getValueToString(field, value) + ", ");
      }
      System.out.println("--");
    }
    /*
    for( int i = 0; i< valueCount; i++){
      for(int j = 0; j < fieldCount; j++)
        System.out.print(group.getValueToString(j, i)+ ", ");
      System.out.println();
    }
    */
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
            System.out.println("group : " + i);
            printGroup(g);
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
