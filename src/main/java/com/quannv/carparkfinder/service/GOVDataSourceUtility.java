package com.quannv.carparkfinder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quannv.carparkfinder.dto.custom.CarParkAvailableDTO;
import com.quannv.carparkfinder.dto.gov.GOVCarParkAvailableEntryDTO;
import com.quannv.carparkfinder.dto.gov.GOVCarParkAvailableInfoDTO;
import com.quannv.carparkfinder.dto.gov.GOVCarParkAvailableResponseDTO;
import com.quannv.carparkfinder.model.CarPark;
import com.quannv.carparkfinder.repository.CarParkMetadataRepository;
import com.quannv.carparkfinder.utils.CustomUtils;
import com.uber.h3core.H3Core;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.quannv.carparkfinder.utils.Constants.GET_CAR_PARKS_AVAILABLE_URI;
import static com.quannv.carparkfinder.utils.CustomUtils.concatCarParkAvailableLots;
import static com.quannv.carparkfinder.utils.CustomUtils.convertSVY21ToWGS84;


@Component
@Slf4j
@RequiredArgsConstructor
public class GOVDataSourceUtility {

	private final CarParkMetadataRepository carParkMetadataRepository;

	@Value(value = "${gov.datasource.path}")
	private String sourceFilePath;

	private final H3Core h3Core;

	private final GeometryFactory factory;

	private final CloseableHttpClient httpClient;

	@Value(value = "${gov.datasource.dumb.batch.size}")
	private int dumbBatchSize;

	private static int STATIC_GOV_DATA_SOURCE_ROW_NUMBER = 2240;

	public List<CarParkAvailableDTO> fetchAvailableCarPark() {
		log.info("Starting fetch available car park from GOV");
		List<CarParkAvailableDTO> availableCarParks = new ArrayList<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			HttpGet request = new HttpGet(GET_CAR_PARKS_AVAILABLE_URI);
			GOVCarParkAvailableResponseDTO response =
					httpClient.execute(request, httpResponse -> mapper.readValue(httpResponse.getEntity()
					                                                                         .getContent(),
					                                                             GOVCarParkAvailableResponseDTO.class));

			Map<String, List<GOVCarParkAvailableInfoDTO>> govCarParkAvailableInfoMap =
					response.getItems()
					        .stream()
					        .flatMap(item -> item.getCarParkData().stream())
					        .collect(Collectors.toMap(GOVCarParkAvailableEntryDTO::getCarParkNumber,
					                                  GOVCarParkAvailableEntryDTO::getCarParkInfo,
					                                  CustomUtils::mergeGOVCarParkAvailableInfoWithDuplicateKey));

			availableCarParks = govCarParkAvailableInfoMap.entrySet()
			                                              .stream()
			                                              .map(entry -> concatCarParkAvailableLots(entry.getKey(), entry.getValue()))
			                                              .toList();

		} catch (Exception e) {
			log.error("Error occurred while fetching all available car park from GOV", e);
		}
		return availableCarParks;
	}

	public void dumbCarParkDataFromGOVDataSource() {
		log.info("Starting dump data from gov data source to CarPark metadata table");

		if (carParkMetadataRepository.count() >= STATIC_GOV_DATA_SOURCE_ROW_NUMBER) {
			log.info("GOV data source already dumbed");
			return;
		}

		try {
			ClassPathResource resource = new ClassPathResource(sourceFilePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(),
			                                                                 StandardCharsets.UTF_8));
			List<CarPark> carParks = new ArrayList<>();
			CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build());

			for (CSVRecord record : parser) {
				CarPark carPark = transformCSVRecord(record);
				if (Objects.nonNull(carPark)) {
					carParks.add(carPark);
					if (carParks.size() == dumbBatchSize) {
						carParkMetadataRepository.saveAll(carParks);
						carParks.clear();
					}
				}
			}

			if (!carParks.isEmpty()) {
				carParkMetadataRepository.saveAll(carParks);
			}
			log.info("Successfully to dump data from GOV data source");
		} catch (Exception e) {
			log.error("Error occurred while dumbing data from gov data source", e);
		}
	}

	private CarPark transformCSVRecord(CSVRecord csvRecord) {
		CarPark carPark = null;
		try {
			String carParkNo = csvRecord.get(0);
			String address = csvRecord.get(1);
			double xCoordinate = Double.parseDouble(csvRecord.get(2));
			double yCoordinate = Double.parseDouble(csvRecord.get(3));

			Pair<Double, Double> latLongConverted = convertSVY21ToWGS84(xCoordinate, yCoordinate);
			double latitude = latLongConverted.getLeft();
			double longitude = latLongConverted.getRight();

			String res7Hex = h3Core.latLngToCellAddress(latitude, longitude, 7);
			String res8Hex = h3Core.latLngToCellAddress(latitude, longitude, 8);
			String res9Hex = h3Core.latLngToCellAddress(latitude, longitude, 9);

			carPark = CarPark.builder()
			                 .carParkNumber(carParkNo)
			                 .address(address)
			                 .latitude(latitude)
			                 .longitude(longitude)
			                 .location(factory.createPoint(new Coordinate(longitude, latitude)))
			                 .res7LocationId(res7Hex).res8LocationId(res8Hex).res9LocationId(res9Hex)
			                 .build();
		} catch (Exception e) {
			log.error("Error while transform row data: {}", csvRecord, e);
		}
		return carPark;
	}
}
