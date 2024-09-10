package com.quannv.carparkfinder.configuration.liquibase;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "liquibase")
public class LiquibaseProperties {

	@NotBlank
	@Value("${liquibase.master.file.path}")
	private String masterFilePath;

	@NotEmpty
	@Value("${liquibase.ignore.schema:pg_catalog,information_schema}")
	private List<String> excludedSchemas;

}