package com.quannv.carparkfinder.controller.v1;

import com.quannv.carparkfinder.dto.GeneralResponse;
import com.quannv.carparkfinder.dto.custom.CarParkNearestEntry;
import com.quannv.carparkfinder.service.CarParkFinderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("carParkControllerV1")
@RequestMapping("/api/v1/car-parks")
@AllArgsConstructor
@Slf4j
@Tag(name = "Car park V1", description = "the car park API V1")
public class CarParkController {

	private CarParkFinderService carParkFinderService;

	@Operation(summary = "Find nearest car parks",
	           description = "Find nearest car park with latitude and longitude provided, " +
	                         "use postgis extension to calculate distance")
	@GetMapping("/nearest")
	public ResponseEntity<GeneralResponse<List<CarParkNearestEntry>>> getNearestCarParks(
			@Parameter(description = "Latitude of the current location") @RequestParam Double latitude,
			@Parameter(description = "Longitude of the current location") @RequestParam Double longitude,
			@Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0", required = false) int page,
			@Parameter(description = "Number of items per page") @RequestParam(defaultValue = "3", required = false) int perPage) {
		return ResponseEntity.ok(carParkFinderService.getNearestCarParks(latitude, longitude, page, perPage));
	}
}
