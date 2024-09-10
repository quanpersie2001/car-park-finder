package com.quannv.carparkfinder.configuration.liquibase;

import liquibase.integration.spring.SpringLiquibase;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@AllArgsConstructor
public class SpringLiquibaseConfig {

	private DataSource dataSource;

	private LiquibaseProperties liquibaseProperties;

	@Bean
	public SpringLiquibase liquibase() {
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setDataSource(dataSource);
		liquibase.setChangeLog(liquibaseProperties.getMasterFilePath());
		liquibase.setShouldRun(false);
		return liquibase;
	}

	@Bean
	public HealthIndicator dbHealthIndicator() {
		DataSourceHealthIndicator indicator = new DataSourceHealthIndicator(dataSource);
		indicator.setQuery("SELECT 999989999999.889999::numeric AS \"NumericValue\"");
		return indicator;
	}
}
