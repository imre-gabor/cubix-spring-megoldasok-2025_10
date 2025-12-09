package com.cubixedu.hr.sample.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cubixedu.hr.sample.model.Employee;
import com.cubixedu.hr.sample.model.Position;
import com.cubixedu.hr.sample.repository.EmployeeRepository;
import com.cubixedu.hr.sample.repository.PositionRepository;

@Service
public abstract class AbstractEmployeeService implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private PositionRepository positionRepository;
	
	
	@Override
	public Employee save(Employee employee) {
		processCompanyAndPosition(employee);
		
		return employeeRepository.save(employee);
	}

	private void processCompanyAndPosition(Employee employee) {
		employee.setCompany(null);
		String posName = employee.getPosition().getName();
		Position position = null;
		if(posName != null) {
			List<Position> positions = positionRepository.findByName(posName);
			if(positions.isEmpty()) {
				position = positionRepository.save(new Position(posName, null));
			} else {
				position = positions.get(0);
			}
		}
		employee.setPosition(position);
	}

	@Override
	public Employee update(Employee employee) {
		if(!employeeRepository.existsById(employee.getEmployeeId()))
			return null;
		processCompanyAndPosition(employee);
		return employeeRepository.save(employee);
	}

	@Override
	public List<Employee> findAll() {
		return employeeRepository.findAll();
	}
	
	@Override
	public Page<Employee> findAll(Pageable pageable) {
		return employeeRepository.findAll(pageable);
	}


	@Override
	public Optional<Employee> findById(long id) {
		return employeeRepository.findById(id);
	}

	@Override
	public void delete(long id) {
		employeeRepository.deleteById(id);
	}

	@Override
	public Page<Employee> findBySalaryGreaterThan(Integer minSalary, Pageable pageable) {
		return employeeRepository.findBySalaryGreaterThan(minSalary, pageable);
	}
	
	
	
}
