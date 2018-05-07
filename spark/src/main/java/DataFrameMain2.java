import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.Function;

import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;


public class DataFrameMain2 {
  public static void main(String[] args) {
    SparkSession spark = SparkSession.builder()
        .master("local[2]")
        .appName("DataFrame2 Application")
        .getOrCreate();

    JavaRDD<String> peopleRDD = spark.sparkContext()
        .textFile("people.txt", 1)
        .toJavaRDD();

    StructType schema = DataTypes.createStructType(Arrays.asList(
        DataTypes.createStructField("name", DataTypes.StringType, true),
        DataTypes.createStructField("age", DataTypes.StringType, true)
    ));

    // Convert records of the RDD (people) to Rows
    JavaRDD<Row> rowRDD = peopleRDD.map((Function<String, Row>) record -> {
      String[] attributes = record.split(",");
      return RowFactory.create(attributes[0], attributes[1].trim());
    });

    // Apply the schema to the RDD
    Dataset<Row> peopleDataFrame = spark.createDataFrame(rowRDD, schema);
    peopleDataFrame.write().parquet("people2.parquet");

    /*
    // Creates a temporary view using the DataFrame
    peopleDataFrame.createOrReplaceTempView("people");

    // SQL can be run over a temporary view created using DataFrames
    Dataset<Row> results = spark.sql("SELECT name FROM people");

    // The results of SQL queries are DataFrames and support all the normal RDD operations
    // The columns of a row in the result can be accessed by field index or by field name
    Dataset<String> namesDS = results.map(
        (MapFunction<Row, String>) row -> "Name: " + row.getString(0),
        Encoders.STRING());
    namesDS.show();
    */

    spark.stop();
  }

}
