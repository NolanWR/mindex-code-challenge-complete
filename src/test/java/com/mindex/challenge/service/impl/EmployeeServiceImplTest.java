package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String numberOfReportsUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        numberOfReportsUrl = "http://localhost:" + port + "/employee/numberOfReports/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testNumberOfReportsNoSubReports() {
        Employee testEmployee1 = new Employee();
        Employee testEmployee2 = new Employee();
        Employee testEmployee3 = new Employee();
        Employee testEmployee4 = new Employee();
        Employee testEmployee5 = new Employee();

        ArrayList<Employee> directReports1 = new ArrayList<Employee>();
        ArrayList<Employee> directReports2 = new ArrayList<Employee>();
        ArrayList<Employee> directReports3 = new ArrayList<Employee>();

        testEmployee2.setEmployeeId("b");
        testEmployee3.setEmployeeId("c");
        testEmployee4.setEmployeeId("d");
        testEmployee5.setEmployeeId("e");

        //setting employees with no direct reports for the first test
        directReports1.add(testEmployee2);
        directReports1.add(testEmployee3);
        directReports1.add(testEmployee4);
        directReports1.add(testEmployee5);

        testEmployee1.setDirectReports(directReports1);

        Employee storedTestEmployee = restTemplate.postForEntity(employeeUrl, testEmployee1, Employee.class).getBody();
        assertNotNull(storedTestEmployee.getEmployeeId());

        ReportingStructure numberOfReports = restTemplate.getForEntity(numberOfReportsUrl, ReportingStructure.class, storedTestEmployee.getEmployeeId()).getBody();

        //testEmployee1 has 4 direct reports, so the numberOfReports should be 4
        assertEquals(numberOfReports.getNumberOfReports(), 4);
    }

    @Test
    public void testNumberOfReportsWithSubReports() {
        Employee testEmployee1 = new Employee();
        Employee testEmployee2 = new Employee();
        Employee testEmployee3 = new Employee();
        Employee testEmployee4 = new Employee();
        Employee testEmployee5 = new Employee();

        ArrayList<Employee> directReports1 = new ArrayList<Employee>();
        ArrayList<Employee> directReports2 = new ArrayList<Employee>();

        testEmployee2.setEmployeeId("b");
        testEmployee3.setEmployeeId("c");
        testEmployee4.setEmployeeId("d");
        testEmployee5.setEmployeeId("e");

        //making an employee with a duplicate direct report
        directReports1.add(testEmployee4);
        directReports1.add(testEmployee5);

        testEmployee2.setDirectReports(directReports1);

        directReports2.add(testEmployee2);
        directReports2.add(testEmployee3);
        directReports2.add(testEmployee4);
        directReports2.add(testEmployee5);

        //testEmployee1 should have ids 'b', 'c', 'd', and 'e' as direct reports, but id 'b' should also have 'd' and 'e' as direct reports, so they shouldn't be counted twice
        testEmployee1.setDirectReports(directReports2);

        Employee storedTestEmployee = restTemplate.postForEntity(employeeUrl, testEmployee1, Employee.class).getBody();
        assertNotNull(storedTestEmployee.getEmployeeId());

        ReportingStructure numberOfReports = restTemplate.getForEntity(numberOfReportsUrl, ReportingStructure.class, storedTestEmployee.getEmployeeId()).getBody();

        //testEmployee1 has 6 direct reports, but 2 of the direct reports repeat in the nested arrays, so the actual result should be 4
        assertEquals(numberOfReports.getNumberOfReports(), 4);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
