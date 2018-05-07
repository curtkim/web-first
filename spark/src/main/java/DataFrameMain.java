import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class DataFrameMain {
  public static void main(String[] args) {
    SparkSession spark = SparkSession.builder()
        .master("local[2]")
        .appName("DataFrame Application")
        .getOrCreate();

    Dataset<Row> df = spark.read().json("people.json");
    df.show();
    df.printSchema();
    df.write().parquet("people.parquet");

    spark.stop();
  }
}
