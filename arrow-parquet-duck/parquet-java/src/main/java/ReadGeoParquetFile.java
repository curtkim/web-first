import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.io.ColumnIOFactory;
import org.apache.parquet.io.MessageColumnIO;
import org.apache.parquet.io.RecordReader;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class ReadGeoParquetFile {

  //private static Path path = new Path("nybb.geoparquet");

  private static void printGroup(Group g) {
    WKBReader wkbReader = new WKBReader();

    int fieldCount = g.getType().getFieldCount();
    int valueCount = g.getFieldRepetitionCount(0);
    System.out.println("valueCount:" + valueCount);

    for (int field = 0; field < fieldCount; field++) {

      Type fieldType = g.getType().getType(field);
      String fieldName = fieldType.getName();

      for (int index = 0; index < valueCount; index++) {
        if (fieldType.isPrimitive()) {
          if( fieldName.equals("geometry")){
            Geometry geometry = null;
            try {
              geometry = wkbReader.read(g.getBinary(field, index).getBytes());
            } catch (ParseException e) {
              throw new RuntimeException(e);
            }
            System.out.println(fieldName + " " + geometry.getNumPoints());
          }
          else {
            System.out.println(fieldName + " " + g.getValueToString(field, index));
          }
        }
      }
    }
    System.out.println("");
  }

  public static void main(String[] args) throws IllegalArgumentException {

    //Configuration conf = new Configuration();

    try {
      ParquetFileReader r = ParquetFileReader.open(new LocalInputFile(Paths.get("nybb.geoparquet")), ParquetReadOptions.builder().build());
      ParquetMetadata readFooter = r.getFooter();
      Map<String, String> map = readFooter.getFileMetaData().getKeyValueMetaData();

      System.out.println("meta key/value");
      for(String key : map.keySet()) {
        System.out.println(key +" : " + map.get(key));
      }
      if(map.containsKey("geo")){
        JSONObject json = new JSONObject(map.get("geo"));
        System.out.println(json.toString(4));
      }

      MessageType schema = readFooter.getFileMetaData().getSchema();

      System.out.println("schema");
      System.out.println(schema);
      //ParquetFileReader r = new ParquetFileReader(conf, path, readFooter);


      PageReadStore pages = null;
      try {
        while (null != (pages = r.readNextRowGroup())) {
          final long rows = pages.getRowCount();
          System.out.println("Number of rows: " + rows);

          final MessageColumnIO columnIO = new ColumnIOFactory().getColumnIO(schema);
          final RecordReader recordReader = columnIO.getRecordReader(pages, new GroupRecordConverter(schema));
          for (int i = 0; i < rows; i++) {
            final Group g = (Group)recordReader.read();
            System.out.println("row : " + i);
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
