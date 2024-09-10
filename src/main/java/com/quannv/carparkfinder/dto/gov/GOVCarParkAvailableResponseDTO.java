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
public class GOVCarParkAvailableResponseDTO {
	@JsonProperty("items")
	private List<GOVCarParkAvailableItems> items;
}

