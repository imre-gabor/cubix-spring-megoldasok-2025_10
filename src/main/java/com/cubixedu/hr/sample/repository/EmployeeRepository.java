package com.cubixedu.hr.sample.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.cubixedu.hr.sample.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee>{

	Page<Employee> findBySalaryGreaterThan(Integer minSalary, Pageable pageable);
	
	List<Employee> findByPositionName(String title);
	
	List<Employee> findByNameStartingWithIgnoreCase(String name);

	List<Employee> findByDateOfStartWorkBetween(LocalDateTime start, LocalDateTime end);

	@Modifying
	@Transactional
	@Query("UPDATE Employee e "
			+ "SET e.salary = :minimalSalary "
			+ "WHERE e.position.name = :positionName "
			+ "AND e.salary < :minimalSalary "
			+ "AND e.company.id = :companyId")
	void updateSalaries(String positionName, int minimalSalary, long companyId);

	Optional<Employee> findByUsername(String username);
	
}
