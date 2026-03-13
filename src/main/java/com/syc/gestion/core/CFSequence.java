package com.syc.gestion.core;

public class CFSequence {

	public String seq_name;
	public int seq_value;

	public CFSequence() {
		this.seq_name = null;
		this.seq_value = -1;
	}

	public CFSequence(String seq_name, int seq_value) {
		this.seq_name = seq_name;
		this.seq_value = seq_value;
	}

	public String getName() {
		return seq_name;
	}

	public void setName(String seq_name) {
		this.seq_name = seq_name;
	}

	public int getValue() {
		return seq_value;
	}

	public void setValue(int seq_value) {
		this.seq_value = seq_value;
	}
}
