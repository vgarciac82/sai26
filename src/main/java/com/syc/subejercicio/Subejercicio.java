package com.syc.subejercicio;

import java.util.ArrayList;
import java.util.List;

public class Subejercicio {
	List<UsuarioCorreo>	usuarios;
	List<String>		claves;

	public Subejercicio() {
		usuarios = new ArrayList<UsuarioCorreo>();
		claves = new ArrayList<String>();
	}

	public List<UsuarioCorreo> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<UsuarioCorreo> usuarios) {
		this.usuarios = usuarios;
	}

	public List<String> getClaves() {
		return claves;
	}

	public void setClaves(List<String> claves) {
		this.claves = claves;
	}

}