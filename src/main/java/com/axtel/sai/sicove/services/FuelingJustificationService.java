package com.axtel.sai.sicove.services;


import com.axtel.sai.sicove.entities.FuelingJustification;
import com.axtel.sai.sicove.exceptions.SicoveException;


public interface FuelingJustificationService {

	FuelingJustification saveFuelingJustification(  FuelingJustification fuelingJustification ) throws SicoveException;

	FuelingJustification readFuelingJustification( int id ) throws SicoveException;

	FuelingJustification updateFuelingJustification( FuelingJustification fuelingJustificationOrg ) throws SicoveException;

}
