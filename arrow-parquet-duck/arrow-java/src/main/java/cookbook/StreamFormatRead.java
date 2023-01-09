package cookbook;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowStreamReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class StreamFormatRead {
  public static void main(String[] args) {
    File file = new File("streaming_to_file.arrow");
    try (
        BufferAllocator rootAllocator = new RootAllocator();
        FileInputStream fileInputStreamForStream = new FileInputStream(file);
        ArrowStreamReader reader = new ArrowStreamReader(fileInputStreamForStream, rootAllocator)
    ) {
      VectorSchemaRoot vectorSchemaRootRecover = reader.getVectorSchemaRoot();
      System.out.println(vectorSchemaRootRecover.getSchema().getFields());

      while (reader.loadNextBatch())
        System.out.print(vectorSchemaRootRecover.contentToTSVString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
