package com.quannv.carparkfinder.utils;

import com.quannv.carparkfinder.dto.custom.CarParkAvailableDTO;
import com.quannv.carparkfinder.dto.gov.GOVCarParkAvailableInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.locationtech.proj4j.BasicCoordinateTransform;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.ProjCoordinate;
import org.locationtech.proj4j.util.ProjectionMath;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class CustomUtils {

	public static Pair<Double, Double> convertSVY21ToWGS84(Double xCoordinate, Double yCoordinate) {
		CRSFactory crsFactory = new CRSFactory();
		CoordinateReferenceSystem svy21CRS = crsFactory.createFromName("EPSG:3414");
		CoordinateReferenceSystem wgs84CRS = crsFactory.createFromName("EPSG:4326");

		BasicCoordinateTransform transform = new BasicCoordinateTransform(svy21CRS, wgs84CRS);

		ProjCoordinate svy21Coordinate = new ProjCoordinate(xCoordinate, yCoordinate);
		ProjCoordinate wgs84Coordinate = new ProjCoordinate();

		transform.transform(svy21Coordinate, wgs84Coordinate);

		return new ImmutablePair<>(wgs84Coordinate.y, wgs84Coordinate.x);
	}

	public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
		return 6378.137 * ProjectionMath.greatCircleDistance(Math.toRadians(lon1), Math.toRadians(lat1),
		                                                    Math.toRadians(lon2), Math.toRadians(lat2));
	}

	public static double convertDistanceFromRadToKm(double radDistance, double lat1, double lat2) {
		return radDistance * 111.320 * Math.cos(Math.toRadians((lat1 + lat2) / 2));
	}

	public static CarParkAvailableDTO concatCarParkAvailableLots(String carParkNumber, List<GOVCarParkAvailableInfoDTO> govCarParkAvailableInfoList) {
		Integer totalLots = govCarParkAvailableInfoList.stream().mapToInt(info -> Integer.parseInt(info.getTotalLots())).sum();
		Integer totalAvailable = govCarParkAvailableInfoList.stream().mapToInt(info -> Integer.parseInt(info.getLotsAvailable())).sum();
		return new CarParkAvailableDTO(carParkNumber, totalLots, totalAvailable);
	}

	public static List<GOVCarParkAvailableInfoDTO> mergeGOVCarParkAvailableInfoWithDuplicateKey(List<GOVCarParkAvailableInfoDTO> x,
	                                                                                            List<GOVCarParkAvailableInfoDTO> y) {
		return Stream.concat(x.stream(), y.stream())
		             .collect(Collectors.toMap(
				             GOVCarParkAvailableInfoDTO::getLotType,
				             dto -> dto,
				             (dto1, dto2) -> Integer.parseInt(dto1.getLotsAvailable()) <
				                             Integer.parseInt(dto2.getLotsAvailable()) ? dto1 : dto2
		             ))
		             .values()
		             .stream()
		             .toList();
	}
}
