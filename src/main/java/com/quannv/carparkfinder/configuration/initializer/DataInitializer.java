package com.quannv.carparkfinder.configuration.initializer;

import com.quannv.carparkfinder.service.GOVDataSourceUtility;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
@DependsOn({"postGisInitializer", "liquibaseService"})
public class DataInitializer {

	private GOVDataSourceUtility dataSourceUtility;

	@PostConstruct
	public void initializer() {
		dataSourceUtility.dumbCarParkDataFromGOVDataSource();

	}
}
