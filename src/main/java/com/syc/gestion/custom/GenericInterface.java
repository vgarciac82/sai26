package com.syc.gestion.custom;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.gestion.core.GestionException;

public interface GenericInterface {

	public void execute(HttpServletRequest req, HttpServletResponse resp, String jniName, String action)
			throws GestionException, IOException;
}
