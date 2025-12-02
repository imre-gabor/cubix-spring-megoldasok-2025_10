package com.cubixedu.hr.sample.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cubixedu.hr.sample.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>{

	List<Employee> findBySalaryGreaterThan(Integer minSalary);

}
