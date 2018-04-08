package dzone;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;



public class AvroExampleWithoutCodeGeneration {

  public void serialize() throws IOException {

    InputStream in = new FileInputStream("src/main/resources/dzone/StudentActivity.json");

    Schema schema = new Schema.Parser().parse(new File("src/main/resources/dzone/StudentActivity.avsc"));
    GenericRecord AvroRec = new GenericData.Record(schema);
    GenericRecord CourseRec = new GenericData.Record(schema.getField("course_details").schema());
    File AvroFile = new File("StudentActivity.avro");

    // Create a writer to serialize the record
    DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
    DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);
    dataFileWriter.create(schema, AvroFile);


    // iterate over JSONs present in input file and write to Avro output file
    for (Iterator it = new ObjectMapper().readValues(
        new JsonFactory().createJsonParser(in), JSONObject.class); it.hasNext();) {

      JSONObject JsonRec = (JSONObject) it.next();
      AvroRec.put("id", JsonRec.get("id"));
      AvroRec.put("student_id", JsonRec.get("student_id"));
      AvroRec.put("university_id", JsonRec.get("university_id"));

      LinkedHashMap CourseDetails = (LinkedHashMap) JsonRec.get("course_details");
      CourseRec.put("course_id", CourseDetails.get("course_id"));
      CourseRec.put("enroll_date", CourseDetails.get("enroll_date"));
      CourseRec.put("verb", CourseDetails.get("verb"));
      CourseRec.put("result_score", CourseDetails.get("result_score"));

      AvroRec.put("course_details", CourseRec);

      dataFileWriter.append(AvroRec);
    }  // end of for loop

    in.close();
    dataFileWriter.close();
  } // end of serialize method


  public void deserialize () throws IOException {
    // create a schema
    Schema schema = new Schema.Parser().parse(new File("src/main/resources/dzone/StudentActivity.avsc"));
    // create a record using schema
    GenericRecord AvroRec = new GenericData.Record(schema);
    File AvroFile = new File("StudentActivity.avro");
    DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
    DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(AvroFile, datumReader);
    System.out.println("Deserialized data is :");
    while (dataFileReader.hasNext()) {
      AvroRec = dataFileReader.next(AvroRec);
      System.out.println(AvroRec);
    }
  }

  public static void main(String[] args) throws IOException {
    AvroExampleWithoutCodeGeneration AvroEx = new AvroExampleWithoutCodeGeneration();
    AvroEx.serialize();
    AvroEx.deserialize();
  }
}