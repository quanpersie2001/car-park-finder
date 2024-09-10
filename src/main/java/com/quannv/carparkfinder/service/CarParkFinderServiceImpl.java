package com.quannv.carparkfinder.service;

import com.quannv.carparkfinder.dto.GeneralResponse;
import com.quannv.carparkfinder.dto.custom.CarParkAvailableDTO;
import com.quannv.carparkfinder.dto.custom.CarParkNearestEntry;
import com.quannv.carparkfinder.model.CarPark;
import com.quannv.carparkfinder.repository.CarParkMetadataRepository;
import com.uber.h3core.H3Core;
import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.quannv.carparkfinder.utils.CustomUtils.convertDistanceFromRadToKm;
import static com.quannv.carparkfinder.utils.CustomUtils.haversineDistance;

@Slf4j
@Service
@AllArgsConstructor
public class CarParkFinderServiceImpl implements CarParkFinderService {

	private CarParkMetadataRepository carParkMetadataRepository;

	private CarParkAvailableCacheService cacheService;

	private GOVDataSourceUtility dataSourceUtility;

	private H3Core h3Core;

	@Override
	public GeneralResponse<List<CarParkNearestEntry>> getNearestCarParks(Double latitude,
	                                                                     Double longitude,
	                                                                     int page,
	                                                                     int perPage) {

		GeneralResponse<List<CarParkNearestEntry>> response = new GeneralResponse<>();
		response.setAcknowledge(true);

		Pageable pageable = PageRequest.of(page, perPage);
		List<CarParkAvailableDTO> carParkAvailable = cacheService.getAllCarParkAvailable();

		log.info("Retrieved {} car park available from cache", carParkAvailable.size());

		if (CollectionUtils.isEmpty(carParkAvailable)) {
			carParkAvailable = dataSourceUtility.fetchAvailableCarPark();
			log.info("Retrieved {} car park available from GOV API", carParkAvailable.size());
		}

		List<String> carParkAvailableNo = carParkAvailable.stream()
		                                                  .map(CarParkAvailableDTO::getCarParkNumber)
		                                                  .toList();

		if (CollectionUtils.isEmpty(carParkAvailableNo)) {
			response.setResponse(Collections.emptyList());
			return response;
		}
		Map<String, CarParkAvailableDTO> carParkNumberMap =
				carParkAvailable.stream()
				                .collect(Collectors.toMap(CarParkAvailableDTO::getCarParkNumber, info -> info));
		List<Tuple> carParks = carParkMetadataRepository.findNearbyCarParks(latitude,
		                                                                    longitude,
		                                                                    carParkAvailableNo.toArray(String[]::new),
		                                                                    pageable);

		List<CarParkNearestEntry> nearestEntries =
				carParks.stream()
				        .map(carPark -> {
					        String carParkNo = carPark.get(0, String.class);
					        String address = carPark.get(1, String.class);
					        double lat = carPark.get(2, BigDecimal.class).doubleValue();
					        double lon = carPark.get(3, BigDecimal.class).doubleValue();
					        double distance = convertDistanceFromRadToKm(carPark.get(4, Double.class), lat, latitude);

					        return new CarParkNearestEntry(address, lat, lon, distance,
					                                       carParkNumberMap.get(carParkNo)
					                                                       .getTotalLots(),
					                                       carParkNumberMap.get(carParkNo)
					                                                       .getAvailableLots());
				        })
				        .toList();
		response.setResponse(nearestEntries);
		return response;
	}

	@Override
	public GeneralResponse<List<CarParkNearestEntry>> getNearestCarParks(Double latitude,
	                                                                     Double longitude,
	                                                                     int h3Resolution) {
		GeneralResponse<List<CarParkNearestEntry>> response = new GeneralResponse<>();
		response.setAcknowledge(true);

		List<CarParkAvailableDTO> carParkAvailable = cacheService.getAllCarParkAvailable();

		log.info("Retrieved {} car park available from cache", carParkAvailable.size());

		if (CollectionUtils.isEmpty(carParkAvailable)) {
			carParkAvailable = dataSourceUtility.fetchAvailableCarPark();
			log.info("Retrieved {} car park available from GOV API", carParkAvailable.size());
		}

		List<String> carParkAvailableNumbers = carParkAvailable.stream()
		                                                       .map(CarParkAvailableDTO::getCarParkNumber)
		                                                       .toList();

		if (CollectionUtils.isEmpty(carParkAvailableNumbers)) {
			response.setResponse(Collections.emptyList());
			return response;
		}
		Map<String, CarParkAvailableDTO> carParkNumberMap =
				carParkAvailable.stream()
				                .collect(Collectors.toMap(CarParkAvailableDTO::getCarParkNumber, info -> info));


		String hexId = h3Core.latLngToCellAddress(latitude, longitude, h3Resolution);
		List<String> hexNeighbors = h3Core.gridRingUnsafe(hexId, 1);
		hexNeighbors.add(hexId);

		List<CarPark> carParks = switch (h3Resolution) {
			case 7 -> carParkMetadataRepository.findNearbyCarParksByRes7LocationId(hexNeighbors,
			                                                                       carParkAvailableNumbers.toArray(String[]::new));
			case 8 -> carParkMetadataRepository.findNearbyCarParksByRes8LocationId(hexNeighbors,
			                                                                       carParkAvailableNumbers.toArray(String[]::new));
			case 9 -> carParkMetadataRepository.findNearbyCarParksByRes9LocationId(hexNeighbors,
			                                                                       carParkAvailableNumbers.toArray(String[]::new));
			default -> new ArrayList<>();
		};

		List<CarParkNearestEntry> nearestEntries =
				carParks.stream()
				        .map(carPark -> new CarParkNearestEntry(carPark.getAddress(),
				                                                carPark.getLatitude(),
				                                                carPark.getLongitude(),
				                                                haversineDistance(latitude,
				                                                                  longitude,
				                                                                  carPark.getLatitude(),
				                                                                  carPark.getLongitude()),
				                                                carParkNumberMap.get(carPark.getCarParkNumber())
				                                                                .getTotalLots(),
				                                                carParkNumberMap.get(carPark.getCarParkNumber())
				                                                                .getAvailableLots()))
						.sorted(Comparator.comparingDouble(CarParkNearestEntry::getDistance))
				        .toList();
		response.setResponse(nearestEntries);
		return response;
	}
}
