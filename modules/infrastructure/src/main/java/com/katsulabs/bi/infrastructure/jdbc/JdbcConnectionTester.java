package com.katsulabs.bi.infrastructure.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import com.katsulabs.bi.application.datasource.DatasourceConnectionTestPort;
import com.katsulabs.bi.application.support.JsonMapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

@Component
public class JdbcConnectionTester implements DatasourceConnectionTestPort {

  private static final TypeReference<Map<String, String>> MAP_TYPE = new TypeReference<>() {};

  @Override
  public void testJdbcConfig(String configJson) {
    Map<String, String> config = JsonMapper.fromJson(configJson, MAP_TYPE);
    String driver = config.get("driver");
    String url = config.get("jdbcurl");
    if (driver == null || url == null) {
      throw new IllegalArgumentException("driver 와 jdbcurl 이 필요합니다.");
    }
    String username = config.getOrDefault("username", "");
    String password = config.getOrDefault("password", "");
    try {
      Class.forName(driver);
      try (Connection ignored = DriverManager.getConnection(url, username, password)) {
        // connection ok
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("JDBC 연결 실패: " + e.getMessage(), e);
    }
  }
}
