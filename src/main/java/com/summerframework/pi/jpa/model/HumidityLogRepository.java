package com.summerframework.pi.jpa.model;

import org.springframework.data.repository.CrudRepository;

public interface HumidityLogRepository extends CrudRepository<HumidityLog, Long> {

	Iterable<HumidityLog> findByTimestampGreaterThanEqual(long timestampOneHourAgo);
}