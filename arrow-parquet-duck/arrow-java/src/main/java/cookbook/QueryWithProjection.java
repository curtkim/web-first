package cookbook;

import org.apache.arrow.dataset.file.FileFormat;
import org.apache.arrow.dataset.file.FileSystemDatasetFactory;
import org.apache.arrow.dataset.jni.NativeMemoryPool;
import org.apache.arrow.dataset.scanner.ScanOptions;
import org.apache.arrow.dataset.scanner.Scanner;
import org.apache.arrow.dataset.source.Dataset;
import org.apache.arrow.dataset.source.DatasetFactory;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowReader;

import java.util.Optional;

public class QueryWithProjection {
  public static void main(String[] args) {
    //String uri = "file:" + System.getProperty("user.dir") + "/thirdpartydeps/parquetfiles/data1.parquet";
    String uri = "file:/./nybb.geoparquet";
    String[] projection = new String[]{"BoroName"};
    ScanOptions options = new ScanOptions(/*batchSize*/ 32768, Optional.of(projection));

    try (
        BufferAllocator allocator = new RootAllocator();
        DatasetFactory datasetFactory = new FileSystemDatasetFactory(allocator, NativeMemoryPool.getDefault(), FileFormat.PARQUET, uri);
        Dataset dataset = datasetFactory.finish();
        Scanner scanner = dataset.newScan(options);
        ArrowReader reader = scanner.scanBatches()
    ) {
      while (reader.loadNextBatch()) {
        try (VectorSchemaRoot root = reader.getVectorSchemaRoot()) {
          System.out.print(root.contentToTSVString());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
