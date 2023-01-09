package com.example.duckdbfirst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

@SpringBootApplication
public class DuckdbFirstApplication {

  @Bean
  public DataSource dataSource(){
    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.duckdb.DuckDBDriver");
        dataSourceBuilder.url("jdbc:duckdb:");
        return dataSourceBuilder.build();
  }

  @Bean
  public SpeedFragmentDao speedFragmentDao(){
    return new SpeedFragmentDao();
  }

  public static void main(String[] args) {
    SpringApplication.run(DuckdbFirstApplication.class, args);
  }

}
