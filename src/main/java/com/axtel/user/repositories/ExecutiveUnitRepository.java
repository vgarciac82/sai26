package com.axtel.user.repositories;

import java.sql.Connection;
import com.axtel.user.UserException;
import com.axtel.user.entities.ExecutiveUnit;
import java.util.Base64;

public interface ExecutiveUnitRepository {

    ExecutiveUnit getExecutiveUnitByAU(Connection conn, String administrativeUnit) throws UserException;
}
