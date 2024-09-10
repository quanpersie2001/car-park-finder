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
public class CarParkDTO {
	private String carParkNo;
	private String address;
	private Double latitude;
	private Double longitude;
	private Double distance;
}
