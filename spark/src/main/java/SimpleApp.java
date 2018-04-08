import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;

public class SimpleApp {
  public static void main(String[] args) {
    String logFile = "build.gradle";

    SparkSession spark = SparkSession.builder()
        .master("local[2]")
        .appName("Simple Application")
        .getOrCreate();
    Dataset<String> logData = spark.read().textFile(logFile).cache();

    long numAs = logData.filter(new FilterFunction<String>() {
      @Override
      public boolean call(String s) throws Exception {
        return s.contains("a");
      }
    }).count();
    long numBs = logData.filter(new FilterFunction<String>() {
      @Override
      public boolean call(String s) throws Exception {
        return s.contains("b");
      }
    }).count();

    System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);

    spark.stop();
  }
}