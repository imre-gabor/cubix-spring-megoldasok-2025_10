package com.cubixedu.hr.sample.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubixedu.hr.sample.config.HrConfigProperties;
import com.cubixedu.hr.sample.config.HrConfigProperties.Smart;
import com.cubixedu.hr.sample.model.Employee;

@Service
public class SmartEmployeeService extends AbstractEmployeeService {

	@Autowired
	HrConfigProperties config;

	@Override
	public int getPayRaisePercent(Employee employee) {
		
		double yearsWorked = ChronoUnit.DAYS.between(employee.getDateOfStartWork(), LocalDateTime.now()) / 365.0;		
		Smart smartConfig = config.getSalary().getSmart();
		
//		if(yearsWorked > smartConfig.getLimit3())
//			return smartConfig.getPercent3();
//		
//		if(yearsWorked > smartConfig.getLimit2())
//			return smartConfig.getPercent2();
//		
//		if(yearsWorked > smartConfig.getLimit1())
//			return smartConfig.getPercent1();

		//opcionális feladat
		TreeMap<Double, Integer> limitsMap = smartConfig.getLimits();
		//1. megoldás
		
//		Integer maxLimit = null;
//		for(var entry: limitsMap.entrySet()) {
//			if(yearsWorked > entry.getKey()) {
//				maxLimit = entry.getValue();
//			} else {
//				break;
//			}
//		}
//		
//		return maxLimit ==  null ? 0 : maxLimit;
		
		//2. megoldás
//		Optional<Double> optionalMax = limitsMap.keySet()
//			.stream()
//			.filter(k -> yearsWorked > k)
//			.max(Double::compare);
//		
//		return optionalMax.isEmpty() ? 0 : limitsMap.get(optionalMax.get());
	
		//3. megoldás
		Entry<Double, Integer> floorEntry = limitsMap.floorEntry(yearsWorked);
		return floorEntry == null ? 0 : floorEntry.getValue();
	}

}
