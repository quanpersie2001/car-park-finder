package com.quannv.carparkfinder.repository;

import com.quannv.carparkfinder.model.CarPark;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarParkMetadataRepository extends PagingAndSortingRepository<CarPark, String>, CrudRepository<CarPark, String> {
	@Query(value = "with available_car_park as (select unnest(:availableCarParkNo) as car_park_no), " +
	               "car_park_with_distance as " +
	               "(select m.CAR_PARK_NO, m.ADDRESS, m.LATITUDE, m.LONGITUDE, ST_Distance(m.LOCATION, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) as DISTANCE " +
	               "from CAR_PARK_METADATA m) " +
	               "select m1.CAR_PARK_NO, m1.ADDRESS, m1.LATITUDE, m1.LONGITUDE, m1.DISTANCE from car_park_with_distance m1 " +
	               "join available_car_park a on m1.CAR_PARK_NO = a.car_park_no " +
	               "order by m1.DISTANCE",
	       countQuery = "with available_car_park as (select unnest(:availableCarParkNo) as car_park_no) " +
	                    "select count(m.CAR_PARK_NO) from CAR_PARK_METADATA m " +
	                    "join available_car_park a on m.CAR_PARK_NO = a.car_park_no",
	       nativeQuery = true)
	List<Tuple> findNearbyCarParks(double latitude, double longitude, String[] availableCarParkNo, Pageable pageable);

	@Query(value = "with available_car_park as (select unnest(:availableCarParkNo) as car_park_no) " +
	               "select m.* from CAR_PARK_METADATA m " +
	               "join available_car_park a on m.CAR_PARK_NO = a.car_park_no " +
	               "where m.RES_7_LOCATION_ID in (:hexIds)",
	       countQuery = "with available_car_park as (select unnest(:availableCarParkNo) as car_park_no) " +
	                    "select count(m.CAR_PARK_NO) from CAR_PARK_METADATA m " +
	                    "join available_car_park a on m.CAR_PARK_NO = a.car_park_no " +
	                    "where m.RES_7_LOCATION_ID in (:hexIds)",
	       nativeQuery = true)
	List<CarPark> findNearbyCarParksByRes7LocationId(List<String> hexIds, String[] availableCarParkNo);


	@Query(value = "with available_car_park as (select unnest(:availableCarParkNo) as car_park_no) " +
	               "select m.* from CAR_PARK_METADATA m " +
	               "join available_car_park a on m.CAR_PARK_NO = a.car_park_no " +
	               "where m.RES_8_LOCATION_ID in (:hexIds)",
	       countQuery = "with available_car_park as (select unnest(:availableCarParkNo) as car_park_no) " +
	                    "select count(m.CAR_PARK_NO) from CAR_PARK_METADATA m " +
	                    "join available_car_park a on m.CAR_PARK_NO = a.car_park_no " +
	                    "where m.RES_8_LOCATION_ID in (:hexIds)",
	       nativeQuery = true)
	List<CarPark> findNearbyCarParksByRes8LocationId(List<String> hexIds, String[] availableCarParkNo);

	@Query(value = "with available_car_park as (select unnest(:availableCarParkNo) as car_park_no) " +
	               "select m.* from CAR_PARK_METADATA m " +
	               "join available_car_park a on m.CAR_PARK_NO = a.car_park_no " +
	               "where m.RES_9_LOCATION_ID in (:hexIds)",
	       countQuery = "with available_car_park as (select unnest(:availableCarParkNo) as car_park_no) " +
	                    "select count(m.CAR_PARK_NO) from CAR_PARK_METADATA m " +
	                    "join available_car_park a on m.CAR_PARK_NO = a.car_park_no " +
	                    "where m.RES_9_LOCATION_ID in (:hexIds)",
	       nativeQuery = true)
	List<CarPark> findNearbyCarParksByRes9LocationId(List<String> hexIds, String[] availableCarParkNo);
}
