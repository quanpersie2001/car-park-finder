package com.quannv.carparkfinder.service;

import com.quannv.carparkfinder.dto.custom.CarParkAvailableDTO;
import com.quannv.carparkfinder.repository.redis.CarParkAvailableCacheRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class CarParkAvailableCacheService {

	private CarParkAvailableCacheRepository cacheRepository;

	private GOVDataSourceUtility govDataSourceUtility;

	@Scheduled(fixedRate = 60000) // Update every minute
	public void updateCache() {
		try {
			log.info("Updating available car park cache");
			List<CarParkAvailableDTO> updatedData = govDataSourceUtility.fetchAvailableCarPark();

			// Clear existing cache and save updated data
			cacheRepository.deleteAll();
			cacheRepository.saveAll(updatedData);
		} catch (Exception e) {
			log.error("Error while update car park available cache", e);
		}
	}

	public List<CarParkAvailableDTO> getAllCarParkAvailable() {
		return cacheRepository.findAll();
	}
}
