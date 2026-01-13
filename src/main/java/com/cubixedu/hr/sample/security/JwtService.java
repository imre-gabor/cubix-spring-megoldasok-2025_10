package com.cubixedu.hr.sample.security;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cubixedu.hr.sample.config.HrConfigProperties;
import com.cubixedu.hr.sample.config.HrConfigProperties.JwtData;
import com.cubixedu.hr.sample.model.Employee;

import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

	private static final String USERNAME = "username";
	private static final String MANAGED_EMPLOYEES = "managedEmployees";
	private static final String MANAGER = "manager";
	private static final String ID = "id";
	private static final String FULLNAME = "fullname";
	private static final String AUTH = "auth";
	private static Algorithm algorithm;// = Algorithm.HMAC256("mysecret");
	private static String ISSUER;
	
	@Autowired
	HrConfigProperties hrConfig;
	
	@PostConstruct
	public void init() {
		JwtData jwtData = hrConfig.getJwtData();
		ISSUER = jwtData.getIssuer();
		try {
			algorithm = (Algorithm) Algorithm.class.getMethod(jwtData.getAlg(), String.class)
			.invoke(null, jwtData.getSecret());
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
	}
	

	public String createJwt(UserDetails userDetails) {
		Employee employee = ((HrUser)userDetails).getEmployee();
		
		Builder jwtBuilder = JWT.create()
			.withSubject(userDetails.getUsername())
			.withArrayClaim(AUTH, userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new))
			.withClaim(FULLNAME, employee.getName())
			.withClaim(ID, employee.getEmployeeId());
		
		Employee manager = employee.getManager();
		if(manager != null) {
			jwtBuilder.withClaim(MANAGER, createMapFromEmployee(manager));
		}
		
		Set<Employee> managedEmployees = employee.getManagedEmployees();
		if(managedEmployees != null && ! managedEmployees.isEmpty()) {
			jwtBuilder.withClaim(MANAGED_EMPLOYEES, managedEmployees.stream()
					.map(this::createMapFromEmployee)
					.toList());
		}
		
		
		return jwtBuilder		
			.withExpiresAt(new Date(System.currentTimeMillis() + hrConfig.getJwtData().getDuration().toMillis()))
			.withIssuer(ISSUER)
			.sign(algorithm);
	}

	private Map<String, Object> createMapFromEmployee(Employee employee) {
		
		return Map.of(
				ID, employee.getEmployeeId(),
				USERNAME, employee.getUsername()
				);
	}

	public UserDetails parseJwt(String jwtToken) {
		
		DecodedJWT decodedJwt = JWT.require(algorithm)
		.withIssuer(ISSUER)
		.build()
		.verify(jwtToken);
		
		Employee employee = new Employee();
		employee.setEmployeeId(decodedJwt.getClaim(ID).asLong());
		employee.setUsername(decodedJwt.getSubject());
		employee.setName(decodedJwt.getClaim(FULLNAME).asString());
		
		
		Map<String, Object> managerData = decodedJwt.getClaim(MANAGER).asMap();
		employee.setManager(parseEmployeeFromMap(managerData));
		
		List<HashMap> managedEmployees = decodedJwt.getClaim(MANAGED_EMPLOYEES).asList(HashMap.class);
		if(managedEmployees != null) {
		
			employee.setManagedEmployees(
				managedEmployees.stream().map(this::parseEmployeeFromMap).collect(Collectors.toSet())
			);
		}
		
		
		return new HrUser(
				decodedJwt.getSubject(), 
				"dummy", 
				decodedJwt.getClaim(AUTH).asList(String.class).stream()
					.map(SimpleGrantedAuthority::new).toList(), 
				employee);
	}

	private Employee parseEmployeeFromMap(Map<String, Object> employeeData) {
		if(employeeData == null || employeeData.isEmpty())
			return null;
		Employee employee = new Employee();
		employee.setEmployeeId(((Integer)employeeData.get(ID)).longValue());
		employee.setUsername(employeeData.get(USERNAME).toString());
		return employee;
	}

}
