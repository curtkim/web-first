
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.ipc.ArrowFileReader;
import org.apache.arrow.vector.ipc.message.ArrowBlock;
import org.apache.arrow.vector.VectorSchemaRoot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReadArrowFile {

  public static void main(String[] args) {

    try (
        BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE);
        FileInputStream fileInputStream = new FileInputStream(new File("random_access_file.arrow"));
        ArrowFileReader reader = new ArrowFileReader(fileInputStream.getChannel(), allocator);
    ) {
      System.out.println("Record batches in file: " + reader.getRecordBlocks().size());
      for (ArrowBlock arrowBlock : reader.getRecordBlocks()) {
        reader.loadRecordBatch(arrowBlock);
        VectorSchemaRoot root = reader.getVectorSchemaRoot();
        System.out.println("VectorSchemaRoot read: \n" + root.contentToTSVString());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
