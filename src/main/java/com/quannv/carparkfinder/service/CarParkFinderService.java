package com.quannv.carparkfinder.service;

import com.quannv.carparkfinder.dto.GeneralResponse;
import com.quannv.carparkfinder.dto.custom.CarParkNearestEntry;

import java.util.List;

public interface CarParkFinderService {
	GeneralResponse<List<CarParkNearestEntry>> getNearestCarParks(Double latitude, Double longitude, int page, int perPage);

	GeneralResponse<List<CarParkNearestEntry>> getNearestCarParks(Double latitude, Double longitude, int res);
}
