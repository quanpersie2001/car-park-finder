package com.quannv.carparkfinder.dto.gov;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GOVCarParkAvailableItems {
	@JsonProperty("timestamp")
	private String timestamp;

	@JsonProperty("carpark_data")
	private List<GOVCarParkAvailableEntryDTO> carParkData;
}
