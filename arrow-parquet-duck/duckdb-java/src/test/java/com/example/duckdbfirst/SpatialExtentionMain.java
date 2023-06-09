package com.example.duckdbfirst;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.InputStreamInStream;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.*;

public class SpatialExtentionMain {
  public static void main(String[] args) throws ClassNotFoundException, SQLException, ParseException, IOException {
    Class.forName("org.duckdb.DuckDBDriver");
    Connection conn = DriverManager.getConnection("jdbc:duckdb:");

    long startTime =System.currentTimeMillis();
    prefare(conn);
    System.out.println("prefare " + (System.currentTimeMillis() - startTime)+ "ms");

    WKBReader reader = new WKBReader();
    Statement stmt = conn.createStatement();
    String sql = "select linkid, rdcate, oneway, ST_AsText(ST_GeomFromWKB(wkb_geometry)) geom " +
        "FROM st_read('/Users/curt/Documents/rn_link_l.fgb') " +
        "limit 10";
    ResultSet rs = stmt.executeQuery(sql);
    System.out.println(rs.getMetaData().getColumnClassName(4));
    System.out.println(rs.getMetaData().getColumnType(4));
    System.out.println(rs.getMetaData().getColumnTypeName(4));

    System.out.println("query " + (System.currentTimeMillis() - startTime)+ "ms");
    while (rs.next()) {
      //ByteBuffer byteBuffer = (ByteBuffer)rs.getObject(4);
      System.out.println(String.format("%s %s %s", rs.getString(1), rs.getString(2), rs.getString(3)));
      String wkt = rs.getString(4);
      System.out.println(wkt);
    }
    rs.close();
  }

  static void prefare(Connection conn) throws SQLException {
    Statement stmt = conn.createStatement();

    String sql =
        "INSTALL spatial;" +
        "LOAD spatial;";
    stmt.execute(sql);
  }
}
