package com.axtel.sai.sicove.repositories;

import java.sql.Connection;
import com.axtel.sai.sicove.entities.EmployeeDAO;
import com.axtel.sai.sicove.exceptions.SicoveException;
import java.util.Base64;

public interface EmployeeRepository {

    EmployeeDAO readEmployee(Connection conn, String employeeResponsible) throws SicoveException;

    EmployeeDAO readEmployeeByLogin(Connection conn, String employeeResponsible) throws SicoveException;
}
