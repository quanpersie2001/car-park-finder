package com.quannv.carparkfinder.controller.v2;

import com.quannv.carparkfinder.dto.GeneralResponse;
import com.quannv.carparkfinder.dto.custom.CarParkNearestEntry;
import com.quannv.carparkfinder.service.CarParkFinderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("carParkControllerV2")
@RequestMapping("/api/v2/car-parks")
@AllArgsConstructor
@Tag(name = "Car park V2", description = "the car park API V2")
public class CarParkController {

	private CarParkFinderService carParkFinderService;

	@Operation(summary = "Find nearest car parks",
	           description = "Find nearest car park with latitude and longitude provided, " +
	                         "use H3 (Uber's Hexagonal Hierarchical Spatial Index)")
	@GetMapping(path = "/nearest")
	public ResponseEntity<GeneralResponse<List<CarParkNearestEntry>>> getNearestCarParks(
			@Parameter(description = "Latitude of the current location") @RequestParam Double latitude,
			@Parameter(description = "Longitude of the current location") @RequestParam Double longitude,
			@Parameter(description = "Resolution of H3 grid (between 7 and 9). A higher value provides more granularity, "
			                         + "while a lower value covers larger areas.") @RequestParam @Min(7) @Max(9) int res) {
		return ResponseEntity.ok(carParkFinderService.getNearestCarParks(latitude, longitude, res));
	}
}
