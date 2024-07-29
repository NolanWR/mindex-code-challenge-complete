package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final Set<String> employeeSet = new HashSet<String>();

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    public ReportingStructure findNumberOfReports(Employee employee) {

        calcNumberOfReports(employee.getDirectReports());
        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setNumberOfReports(employeeSet.size());
        //reportingStructure.setNumberOfReports(calcNumberOfReports(employee));
        reportingStructure.setEmployee(employee);

        return reportingStructure;
    }

    //simple verison to count all instances of an employee
    /*private int calcNumberOfReports(Employee employee){
        int numberOfReports = 0;

        //recursive loop to go through each directReport and count the number of employees
        if (employee.getDirectReports() != null){
            for(int i = 0; i < employee.getDirectReports().size(); i++) {
                numberOfReports = numberOfReports + calcNumberOfReports(employee.getDirectReports().get(i));
            }
        }
        else if (employee.getDirectReports() == null) {
            numberOfReports++;
        }
        return numberOfReports;
    }*/

    //HashSet version to count unique employees
    private void calcNumberOfReports(List<Employee> employees) {
        if (employees != null){
            for(int i = 0; i < employees.size(); i++) {
                employeeSet.add(employees.get(i).getEmployeeId());
                calcNumberOfReports(employees.get(i).getDirectReports());
            }
        }
    }
}
