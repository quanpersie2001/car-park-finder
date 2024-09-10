package com.quannv.carparkfinder.dto.gov;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GOVCarParkAvailableInfoDTO {
	@JsonProperty("total_lots")
	private String totalLots;

	@JsonProperty("lot_type")
	private String lotType;

	@JsonProperty("lots_available")
	private String lotsAvailable;
}
