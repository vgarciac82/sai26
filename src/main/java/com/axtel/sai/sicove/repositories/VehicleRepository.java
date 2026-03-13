package com.axtel.sai.sicove.repositories;

import java.sql.Connection;

import com.axtel.sai.sicove.entities.Vehicle;
import com.axtel.sai.sicove.exceptions.SicoveException;

public interface VehicleRepository {

	Vehicle findByInventoryId( Connection conn, int inventoryId ) throws SicoveException;

}
