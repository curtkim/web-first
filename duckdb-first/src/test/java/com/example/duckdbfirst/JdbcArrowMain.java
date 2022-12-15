package com.example.duckdbfirst;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.ipc.ArrowReader;
import org.duckdb.DuckDBResultSet;


/**
 * jni 에러가 발생한다.
 * https://issues.apache.org/jira/browse/ARROW-16608
 * https://github.com/apache/arrow/pull/14472
 */
public class JdbcArrowMain {
	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException  {
		Class.forName("org.duckdb.DuckDBDriver");
		Connection conn = DriverManager.getConnection("jdbc:duckdb:");
		Statement stmt = conn.createStatement();
		DuckDBResultSet rs = (DuckDBResultSet) stmt.executeQuery("SELECT * from generate_series(2000)");

		/* by resultset
		while(rs.next()){
			System.out.println(rs.getLong(1));
		}
		rs.close();
		*/

		BufferAllocator allocator = new RootAllocator();
		ArrowReader reader = (ArrowReader) rs.arrowExportStream(allocator, 1024);
		while (reader.loadNextBatch()) {
			System.out.println(reader.getVectorSchemaRoot().contentToTSVString());
		}
		reader.close();
		allocator.close();
	}
}
