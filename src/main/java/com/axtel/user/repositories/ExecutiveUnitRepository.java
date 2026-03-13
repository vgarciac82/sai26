package com.axtel.user.repositories;


import java.sql.Connection;

import com.axtel.user.UserException;
import com.axtel.user.entities.ExecutiveUnit;


public interface ExecutiveUnitRepository {

	ExecutiveUnit getExecutiveUnitByAU(Connection conn, String administrativeUnit) throws UserException;

}
