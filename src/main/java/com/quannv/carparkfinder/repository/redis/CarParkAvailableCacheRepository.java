package com.quannv.carparkfinder.repository.redis;

import com.quannv.carparkfinder.dto.custom.CarParkAvailableDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.quannv.carparkfinder.utils.Constants.CAR_PARK_AVAILABLE_REDIS_KEY;

@Repository
@AllArgsConstructor
public class CarParkAvailableCacheRepository {

	private RedisTemplate<String, CarParkAvailableDTO> redisTemplate;

	public void saveAll(List<CarParkAvailableDTO> cacheDTOs) {
		redisTemplate.opsForList().rightPushAll(CAR_PARK_AVAILABLE_REDIS_KEY, cacheDTOs);
	}

	public List<CarParkAvailableDTO> findAll() {
		// Some data in car park available with available lots = 0
		return Objects.requireNonNull(redisTemplate.opsForList().range(CAR_PARK_AVAILABLE_REDIS_KEY, 0, -1))
		              .stream()
		              .filter(dto -> dto.getAvailableLots() != 0)
		              .collect(Collectors.toList());
	}

	public void deleteAll() {
		redisTemplate.delete(CAR_PARK_AVAILABLE_REDIS_KEY);
	}
}
