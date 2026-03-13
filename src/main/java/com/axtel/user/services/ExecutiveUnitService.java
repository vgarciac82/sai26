package com.axtel.user.services;


import com.axtel.user.UserException;
import com.axtel.user.entities.ExecutiveUnit;


public interface ExecutiveUnitService {

	ExecutiveUnit getExecutiveUnitByAU( String administrativeUnit ) throws UserException;
}
