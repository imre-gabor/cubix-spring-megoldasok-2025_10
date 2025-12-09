package com.cubixedu.hr.sample.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cubixedu.hr.sample.model.Position;

public interface PositionRepository extends JpaRepository<Position, Integer> {

	public List<Position> findByName(String name);

}
