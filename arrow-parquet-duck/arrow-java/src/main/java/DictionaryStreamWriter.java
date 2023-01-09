import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.ValueVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.dictionary.Dictionary;
import org.apache.arrow.vector.dictionary.DictionaryEncoder;
import org.apache.arrow.vector.dictionary.DictionaryProvider;
import org.apache.arrow.vector.ipc.ArrowStreamReader;
import org.apache.arrow.vector.ipc.ArrowStreamWriter;
import org.apache.arrow.vector.types.pojo.*;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class DictionaryStreamWriter {

  public static void main(String[] args) throws IOException {
    final List<Person> list = asList(
        new Person("kim", 20, "seoul"),
        new Person("lee", 30, "seoul"),
        new Person("park", 25, "pusan")
    );

    BufferAllocator allocator = new RootAllocator();

    DictionaryEncoding regionEncoding = new DictionaryEncoding(1L, false, null);
    Field name = new Field("name", FieldType.nullable(new ArrowType.Utf8()), null);
    Field age = new Field("age", FieldType.nullable(new ArrowType.Int(32, true)), null);
    Field region = new Field("region", new FieldType(true, new ArrowType.Int(32, true), regionEncoding), null);

    Schema schema = new Schema(Arrays.asList(name, age,region));
    VectorSchemaRoot root = VectorSchemaRoot.create(schema, allocator);



    final VarCharVector dictVector = new VarCharVector("dict", allocator);
    dictVector.allocateNewSafe();
    dictVector.setSafe(0, "seoul".getBytes());
    dictVector.setSafe(1, "pusan".getBytes());
    dictVector.setValueCount(2);
    Dictionary dictionary = new Dictionary(dictVector, regionEncoding);

    DictionaryProvider.MapDictionaryProvider provider = new DictionaryProvider.MapDictionaryProvider();
    provider.put(dictionary);


    File file = new File("test_dict_stream.arrow");
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    ArrowStreamWriter writer = new ArrowStreamWriter(root, provider, fileOutputStream.getChannel());
    writer.start();

    List<String> regionDictionary = new ArrayList<>();

    Flux.fromIterable(list).buffer(2)
        .doOnNext((chunk)->{
          root.clear();
          VarCharVector nameVector = (VarCharVector)root.getVector("name");
          IntVector ageVector = (IntVector)root.getVector("age");
          IntVector regionVector = (IntVector)root.getVector("region");

          root.allocateNew();
          nameVector.allocateNew();
          ageVector.allocateNew();
          regionVector.allocateNew();

          for(int i =0; i < chunk.size(); i++){
            Person person = chunk.get(i);
            nameVector.set(i, person.name.getBytes());
            ageVector.set(i, person.age);

            int regionIdx = regionDictionary.indexOf(person.region);
            if(regionIdx < 0) {
              regionDictionary.add(person.region);
              regionIdx = regionDictionary.size()-1;
            }
            System.out.println(person.region + " " +regionIdx);
            regionVector.set(i, regionIdx);
          }
          nameVector.setValueCount(chunk.size());
          ageVector.setValueCount(chunk.size());
          regionVector.setValueCount(chunk.size());
          root.setRowCount(chunk.size());

          dictVector.clear();
          for(int i =0; i < regionDictionary.size(); i++){
            String strRegion = regionDictionary.get(i);
            dictVector.allocateNew();
            dictVector.set(i, strRegion.getBytes());
          }
          dictVector.setValueCount(regionDictionary.size());


          try {
            System.out.println("writeBatch");
            writer.writeBatch();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }

        }).collectList().block();
    writer.end();
    fileOutputStream.close();


    try (
        BufferAllocator rootAllocator = new RootAllocator();
        FileInputStream fileInputStreamForStream = new FileInputStream(file);
        ArrowStreamReader reader = new ArrowStreamReader(fileInputStreamForStream, rootAllocator)
    ) {

      while (reader.loadNextBatch()) {
        VectorSchemaRoot vectorSchemaRootRecover = reader.getVectorSchemaRoot();
        System.out.println(vectorSchemaRootRecover.getRowCount());

        IntVector intVector = (IntVector) vectorSchemaRootRecover.getVector("region");
        long dictionaryId = intVector.getField().getDictionary().getId();
        Dictionary dict = reader.getDictionaryVectors().get(dictionaryId);

        //dict.getVector().getVector()
        //System.out.println("dict vector type: " + dict.getVectorType());

        VarCharVector varCharVector =(VarCharVector) DictionaryEncoder.decode(intVector, dict);
        System.out.println(varCharVector);
        varCharVector.close();

        System.out.println(vectorSchemaRootRecover.getSchema().getFields());
        System.out.println(vectorSchemaRootRecover.contentToTSVString());
        vectorSchemaRootRecover.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class Person{
  String name;
  int age;
  String region;

  public Person(String name, int age, String region) {
    this.name = name;
    this.age = age;
    this.region = region;
  }
}
