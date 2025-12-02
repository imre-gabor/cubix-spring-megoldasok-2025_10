package com.cubixedu.hr.sample.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cubixedu.hr.sample.model.Company;
import com.cubixedu.hr.sample.model.Employee;
import com.cubixedu.hr.sample.repository.CompanyRepository;
import com.cubixedu.hr.sample.repository.EmployeeRepository;

import jakarta.transaction.Transactional;

@Service
public class CompanyService {

    private final EmployeeRepository employeeRepository;
	private final CompanyRepository companyRepository;
	
	public CompanyService(EmployeeRepository employeeRepository, CompanyRepository companyRepository) {
		super();
		this.employeeRepository = employeeRepository;
		this.companyRepository = companyRepository;
	}

	public Company save(Company company) {
		return companyRepository.save(company);
	}

	public Company update(Company company) {
		if(!companyRepository.existsById(company.getId()))
			return null;
		return companyRepository.save(company);
	}

	public List<Company> findAll() {
		return companyRepository.findAll();
	}

	public Optional<Company> findById(long id) {
		return companyRepository.findById(id);
	}

	public void delete(long id) {
		companyRepository.deleteById(id);
	}
	
	public Company addEmployee(long id, Employee employee) {
		Company company = companyRepository.findById(id).get();
		company.addEmployee(employee);
		employeeRepository.save(employee);
		return company;
	}
	
	public Company deleteEmployee(long id, long employeeId) {
		Company company = companyRepository.findById(id).get();
		Employee employee = employeeRepository.findById(employeeId).get();
		employee.setCompany(null);
		company.getEmployees().remove(employee);
		employeeRepository.save(employee);
		return company;
	}
	
	//@Transactional
	public Company replaceEmployees(long id, List<Employee> employees) {
		Company company = companyRepository.findById(id).get();
		
		company.getEmployees().forEach(e -> e.setCompany(null));
		company.getEmployees().clear();
		
		employees.forEach(e -> {
			//company.addEmployee(employeeRepository.save(e));		//csak @Transactional esetben helyes
			
			company.addEmployee(e);
			e.setEmployeeId(employeeRepository.save(e).getEmployeeId());			
		});
		
		return company;
	}
	
}
