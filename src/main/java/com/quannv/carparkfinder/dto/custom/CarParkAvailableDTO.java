package com.quannv.carparkfinder.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CarParkAvailableDTO {
	private String carParkNumber;
	private Integer totalLots;
	private Integer availableLots;
}
