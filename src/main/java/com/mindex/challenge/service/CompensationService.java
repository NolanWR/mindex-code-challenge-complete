package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;

import java.util.List;

public interface CompensationService {
    Compensation create(Compensation compensation);
    Compensation read(String id);
}
