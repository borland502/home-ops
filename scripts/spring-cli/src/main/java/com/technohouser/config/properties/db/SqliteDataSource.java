package com.technohouser.config.properties.db;

import jakarta.persistence.EntityManagerFactory;
import java.net.URI;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.sqlite.SQLiteDataSource;

@Configuration
public class SqliteDataSource {

  @Value("${spring.datasource.url}")
  private String url;

  @Bean
  public DataSource dataSource() {
    SQLiteDataSource dataSource = new SQLiteDataSource();
    dataSource.setUrl(url);
    return dataSource;
  }

}
