package com.quannv.carparkfinder.configuration.liquibase;

import jakarta.annotation.PostConstruct;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

@Component
@DependsOn("liquibase")
@Slf4j
public class LiquibaseService {

	private final DataSource datasource;
	private final SpringLiquibase liquibase;
	private final LiquibaseProperties liquibaseProperties;

	public LiquibaseService(DataSource datasource,
	                        @Qualifier("liquibase") SpringLiquibase liquibase,
	                        LiquibaseProperties liquibaseProperties) {
		this.datasource = datasource;
		this.liquibase = liquibase;
		this.liquibaseProperties = liquibaseProperties;
	}

	@PostConstruct
	public void initialize() throws Exception {
		try (Connection connection = datasource.getConnection()) {
			executeForNonIgnoredSchemas(connection);
		}
	}

	private void executeForNonIgnoredSchemas(Connection connection) throws Exception {
		List<String> ignoreSchemas = liquibaseProperties.getExcludedSchemas();
		try (ResultSet schemasRS = connection.getMetaData().getSchemas()) {
			while (schemasRS.next()) {
				String currentSchema = schemasRS.getString("TABLE_SCHEM").toLowerCase();
				if (!ignoreSchemas.contains(currentSchema)) {
					performLiquibaseUpdateForSchema(currentSchema);
				}
			}
		} catch (Exception ex) {
			log.error("Error occurred while performing Liquibase changesets", ex);
			connection.close();
			throw new Exception("Error occurred while performing Liquibase changesets");
		}
	}

	private void performLiquibaseUpdateForSchema(String schema) throws LiquibaseException {
		liquibase.setDataSource(datasource);
		liquibase.setDefaultSchema(schema);
		liquibase.setChangeLog(liquibaseProperties.getMasterFilePath());
		liquibase.setShouldRun(true);
		liquibase.afterPropertiesSet();
	}
}