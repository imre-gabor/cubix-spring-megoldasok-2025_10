package com.cubixedu.hr.sample.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.cubixedu.hr.sample.model.Company;
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
	@Transactional
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
	@Transactional
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
	@Transactional
	public void delete(long id) {
		employeeRepository.deleteById(id);
	}

	@Override
	public Page<Employee> findBySalaryGreaterThan(Integer minSalary, Pageable pageable) {
		return employeeRepository.findBySalaryGreaterThan(minSalary, pageable);
	}
	
	@Override
	public List<Employee> findEmployeesByExample(Employee example) {
		long id = example.getEmployeeId();
		String name = example.getName();
		String title = example.getPosition().getName();
		int salary = example.getSalary();
		LocalDateTime entryDate = example.getDateOfStartWork();
		Company company = example.getCompany();
		String companyName = company == null ? null : company.getName();

		Specification<Employee> spec = Specification.where(null);

		if (id > 0)
			spec = spec.and(EmployeeSpecifications.hasId(id));

		if (StringUtils.hasText(name))
			spec = spec.and(EmployeeSpecifications.hasName(name));

		if (StringUtils.hasText(title))
			spec = spec.and(EmployeeSpecifications.hasTitle(title));

		if (salary > 0)
			spec = spec.and(EmployeeSpecifications.hasSalary(salary));

		if (entryDate != null)
			spec = spec.and(EmployeeSpecifications.hasEntryDate(entryDate));

		if (StringUtils.hasText(companyName))
			spec = spec.and(EmployeeSpecifications.hasCompany(companyName));

		return employeeRepository.findAll(spec, Sort.by("employeeId"));
	}
	
	
}
