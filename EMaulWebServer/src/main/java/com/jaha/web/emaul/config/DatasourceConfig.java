package com.jaha.web.emaul.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by doring on 14. 11. 7..
 */
@Configuration
public class DatasourceConfig {
    @Bean(name = "dsMaster")
    @Primary
    @ConfigurationProperties(prefix = "spring.mysql_master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jdbcMaster")
    public JdbcTemplate masterJdbcTemplate(@Qualifier("dsMaster") DataSource dsMaster) {
        return new JdbcTemplate(dsMaster);
    }


//    @Bean(name = "dsSlave")
//    @ConfigurationProperties(prefix = "spring.mysql_slave")
//    public DataSource slaveDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "jdbcSlave")
//    public JdbcTemplate slaveJdbcTemplate(@Qualifier("dsSlave") DataSource dsSlave) {
//        return new JdbcTemplate(dsSlave);
//    }

}
