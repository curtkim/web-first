package doc;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.dictionary.Dictionary;
import org.apache.arrow.vector.dictionary.DictionaryEncoder;
import org.apache.arrow.vector.dictionary.DictionaryProvider;
import org.apache.arrow.vector.ipc.ArrowStreamReader;
import org.apache.arrow.vector.ipc.ArrowStreamWriter;
import org.apache.arrow.vector.types.pojo.*;

import java.io.*;
import java.nio.channels.Channels;
import java.util.Arrays;
import java.util.Map;

public class WriteStreamArrowFileWithDict {


  public static void main(String[] args) throws IOException {

    BufferAllocator allocator = new RootAllocator();

    // create dictionary and provider
    final VarCharVector dictVector = new VarCharVector("dict", allocator);
    dictVector.allocateNewSafe();
    dictVector.setSafe(0, "aa".getBytes());
    dictVector.setSafe(1, "bb".getBytes());
    dictVector.setSafe(2, "cc".getBytes());
    dictVector.setValueCount(3);

    Dictionary dictionary = new Dictionary(dictVector, new DictionaryEncoding(1L, false, /*indexType=*/null));


    // create vector and encode it
    final VarCharVector vector = new VarCharVector("vector", allocator);
    vector.allocateNewSafe();
    vector.setSafe(0, "bb".getBytes());
    vector.setSafe(1, "bb".getBytes());
    vector.setSafe(2, "cc".getBytes());
    vector.setSafe(3, "aa".getBytes());
    vector.setValueCount(4);

    // get the encoded vector
    IntVector encodedVector = (IntVector) DictionaryEncoder.encode(vector, dictionary);


    // create VectorSchemaRoot
    VectorSchemaRoot root = new VectorSchemaRoot(
        Arrays.asList(encodedVector.getField()),
        Arrays.asList(encodedVector)
    );

    DictionaryProvider.MapDictionaryProvider provider = new DictionaryProvider.MapDictionaryProvider();
    provider.put(dictionary);



    // write data
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    {
      ArrowStreamWriter writer = new ArrowStreamWriter(root, provider, Channels.newChannel(out));
      writer.start();
      writer.writeBatch();
      writer.end();
    }

    // read data
    try (ArrowStreamReader reader = new ArrowStreamReader(new ByteArrayInputStream(out.toByteArray()), allocator)) {
      reader.loadNextBatch();
      VectorSchemaRoot readRoot = reader.getVectorSchemaRoot();
      // get the encoded vector
      IntVector intVector = (IntVector) readRoot.getVector(0);

      // get dictionaries and decode the vector
      Map<Long, Dictionary> dictionaryMap = reader.getDictionaryVectors();
      long dictionaryId = intVector.getField().getDictionary().getId();
      VarCharVector varCharVector =(VarCharVector) DictionaryEncoder.decode(intVector, dictionaryMap.get(dictionaryId));
    }
  }
}
