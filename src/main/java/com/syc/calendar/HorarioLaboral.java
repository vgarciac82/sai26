package com.syc.calendar;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HorarioLaboral {

	private int fin_hora;
	private int fin_minuto;

	private int ini_hora;
	private int ini_minuto;

	public HorarioLaboral() {

		Calendar c = Calendar.getInstance();

		this.ini_hora = c.get(Calendar.HOUR_OF_DAY);
		this.ini_minuto = c.get(Calendar.MINUTE);

		c.add(Calendar.HOUR_OF_DAY, 9);
		this.fin_hora = c.get(Calendar.HOUR_OF_DAY);
		this.fin_minuto = c.get(Calendar.MINUTE);
	}

	public HorarioLaboral(int ini_hora, int ini_minuto, int fin_hora, int fin_minuto) {
		this.ini_hora = ini_hora;
		this.ini_minuto = ini_minuto;

		this.fin_hora = fin_hora;
		this.fin_minuto = fin_minuto;
	}

	public List getHorarioLaboral(Timestamp time) {

		List l = new ArrayList();

		l.add(getHorarioLaboralInicial(time));
		l.add(getHorarioLaboralFinal(time));

		return l;
	}

	public Calendar getHorarioLaboralFinal(Timestamp time) {

		Calendar c = Calendar.getInstance();

		c.setTime(time);
		c.set(Calendar.HOUR_OF_DAY, fin_hora);
		c.set(Calendar.MINUTE, fin_minuto);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c;
	}

	public Calendar getHorarioLaboralInicial(Timestamp time) {

		Calendar c = Calendar.getInstance();

		c.setTime(time);
		c.set(Calendar.HOUR_OF_DAY, ini_hora);
		c.set(Calendar.MINUTE, ini_minuto);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c;
	}

	public void setHorarioLaboral(int ini_hora, int ini_minuto, int fin_hora, int fin_minuto) {
		this.ini_hora = ini_hora;
		this.ini_minuto = ini_minuto;

		this.fin_hora = fin_hora;
		this.fin_minuto = fin_minuto;
	}
}
