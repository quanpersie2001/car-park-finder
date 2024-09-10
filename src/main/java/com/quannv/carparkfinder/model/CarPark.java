package com.quannv.carparkfinder.model;

import jakarta.persistence.Transient;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "CAR_PARK_METADATA")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarPark {

	@Id
	@Column(name = "CAR_PARK_NO", columnDefinition = "NVARCHAR(10)")
	private String carParkNumber;

	@Column(name = "ADDRESS", columnDefinition = "NVARCHAR(200)")
	private String address;

	@Column(name = "LATITUDE", columnDefinition = "DECIMAL")
	private Double latitude;

	@Column(name = "LONGITUDE", columnDefinition = "DECIMAL")
	private Double longitude;

	@Column(name = "LOCATION", columnDefinition = "GEOMETRY(Point,4326)")
	private Point location;

	@Column(name = "RES_7_LOCATION_ID", columnDefinition = "NVARCHAR(15)")
	private String res7LocationId;

	@Column(name = "RES_8_LOCATION_ID", columnDefinition = "NVARCHAR(15)")
	private String res8LocationId;

	@Column(name = "RES_9_LOCATION_ID", columnDefinition = "NVARCHAR(15)")
	private String res9LocationId;
}
