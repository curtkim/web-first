package com.example.duckdbfirst;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SpeedFragmentDao extends JdbcDaoSupport {

  @Autowired
  DataSource dataSource;

  @PostConstruct
	private void initialize(){
		setDataSource(dataSource);
	}

  public List<SpeedFragment> find(int day, long trafficId){
    long idx = trafficId % 31;
    String sql = String.format("select * from read_parquet('data/navi-speed-fragment-%d_%d_*.parquet') where traffic_id = ?", day, idx);

    return getJdbcTemplate().query(sql, new Object[]{trafficId}, new RowMapper<SpeedFragment>() {
      @Override
      public SpeedFragment mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SpeedFragment(
            rs.getLong("traffic_id"),
            rs.getLong("link_id"),
            rs.getInt("speed"),
            rs.getLong("timestamp"));
      }
    });
  }
}
