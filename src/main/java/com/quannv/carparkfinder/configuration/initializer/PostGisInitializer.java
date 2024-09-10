package com.quannv.carparkfinder.configuration.initializer;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PostGisInitializer {

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	@SneakyThrows
	public void init() {
		jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS postgis;");
	}
}
