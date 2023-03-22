package com.example.duckdbfirst;

import java.sql.*;

public class DuckdbJdbcMain {
  public static void main(String[] args) throws ClassNotFoundException, SQLException {
    Class.forName("org.duckdb.DuckDBDriver");
    Connection conn = DriverManager.getConnection("jdbc:duckdb:");
//    Properties ro_prop = new Properties();
//    ro_prop.setProperty("duckdb.read_only", "true");
//    Connection conn_ro = DriverManager.getConnection("jdbc:duckdb:/tmp/my_database", ro_prop);

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM read_parquet('userdata1.parquet')");
    while (rs.next()) {
      System.out.println(String.format("%s %s %s", rs.getString(1), rs.getString(2), rs.getString(3)));
    }
    rs.close();
  }
}
