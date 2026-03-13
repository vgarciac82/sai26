package com.syc.contable.core;

import java.util.ArrayList;

public class AdecuacionCalendario {

	double mTotREnero = 0;
	double mTotRFebrero = 0;
	double mTotRMarzo = 0;
	double mTotRAbril = 0;
	double mTotRMayo = 0;
	double mTotRJunio = 0;
	double mTotRJulio = 0;
	double mTotRAgosto = 0;
	double mTotRSeptiembre = 0;
	double mTotROctubre = 0;
	double mTotRNoviembre = 0;
	double mTotRDiciembre = 0;
	double mTotREneroNegativo = 0;
	double mTotRFebreroNegativo = 0;
	double mTotRMarzoNegativo = 0;
	double mTotRAbrilNegativo = 0;
	double mTotRMayoNegativo = 0;
	double mTotRJunioNegativo = 0;
	double mTotRJulioNegativo = 0;
	double mTotRAgostoNegativo = 0;
	double mTotRSeptiembreNegativo = 0;
	double mTotROctubreNegativo = 0;
	double mTotRNoviembreNegativo = 0;
	double mTotRDiciembreNegativo = 0;

	double mTotAEnero = 0;
	double mTotAFebrero = 0;
	double mTotAMarzo = 0;
	double mTotAAbril = 0;
	double mTotAMayo = 0;
	double mTotAJunio = 0;
	double mTotAJulio = 0;
	double mTotAAgosto = 0;
	double mTotASeptiembre = 0;
	double mTotAOctubre = 0;
	double mTotANoviembre = 0;
	double mTotADiciembre = 0;
	double iTotAbonos = 0;
	double iTotCargos = 0;
	
	String mensaje="";
	String justificacion="";
	ArrayList<Integer> renglon = new ArrayList<Integer>();
	ArrayList<String> tipoMov= new ArrayList<String>();
	
	ArrayList<Saldo> epSaldo = new ArrayList<Saldo>();
	
	public double getmTotREnero() {
		return mTotREnero;
	}
	public void setmTotREnero(double mTotREnero) {
		this.mTotREnero = mTotREnero;
	}
	public double getmTotRFebrero() {
		return mTotRFebrero;
	}
	public void setmTotRFebrero(double mTotRFebrero) {
		this.mTotRFebrero = mTotRFebrero;
	}
	public double getmTotRMarzo() {
		return mTotRMarzo;
	}
	public void setmTotRMarzo(double mTotRMarzo) {
		this.mTotRMarzo = mTotRMarzo;
	}
	public double getmTotRAbril() {
		return mTotRAbril;
	}
	public void setmTotRAbril(double mTotRAbril) {
		this.mTotRAbril = mTotRAbril;
	}
	public double getmTotRMayo() {
		return mTotRMayo;
	}
	public void setmTotRMayo(double mTotRMayo) {
		this.mTotRMayo = mTotRMayo;
	}
	public double getmTotRJunio() {
		return mTotRJunio;
	}
	public void setmTotRJunio(double mTotRJunio) {
		this.mTotRJunio = mTotRJunio;
	}
	public double getmTotRJulio() {
		return mTotRJulio;
	}
	public void setmTotRJulio(double mTotRJulio) {
		this.mTotRJulio = mTotRJulio;
	}
	public double getmTotRAgosto() {
		return mTotRAgosto;
	}
	public void setmTotRAgosto(double mTotRAgosto) {
		this.mTotRAgosto = mTotRAgosto;
	}
	public double getmTotRSeptiembre() {
		return mTotRSeptiembre;
	}
	public void setmTotRSeptiembre(double mTotRSeptiembre) {
		this.mTotRSeptiembre = mTotRSeptiembre;
	}
	public double getmTotROctubre() {
		return mTotROctubre;
	}
	public void setmTotROctubre(double mTotROctubre) {
		this.mTotROctubre = mTotROctubre;
	}
	public double getmTotRNoviembre() {
		return mTotRNoviembre;
	}
	public void setmTotRNoviembre(double mTotRNoviembre) {
		this.mTotRNoviembre = mTotRNoviembre;
	}
	public double getmTotRDiciembre() {
		return mTotRDiciembre;
	}
	public void setmTotRDiciembre(double mTotRDiciembre) {
		this.mTotRDiciembre = mTotRDiciembre;
	}
	public double getmTotREneroNegativo() {
		return mTotREneroNegativo;
	}
	public void setmTotREneroNegativo(double mTotREneroNegativo) {
		this.mTotREneroNegativo = mTotREneroNegativo;
	}
	public double getmTotRFebreroNegativo() {
		return mTotRFebreroNegativo;
	}
	public void setmTotRFebreroNegativo(double mTotRFebreroNegativo) {
		this.mTotRFebreroNegativo = mTotRFebreroNegativo;
	}
	public double getmTotRMarzoNegativo() {
		return mTotRMarzoNegativo;
	}
	public void setmTotRMarzoNegativo(double mTotRMarzoNegativo) {
		this.mTotRMarzoNegativo = mTotRMarzoNegativo;
	}
	public double getmTotRAbrilNegativo() {
		return mTotRAbrilNegativo;
	}
	public void setmTotRAbrilNegativo(double mTotRAbrilNegativo) {
		this.mTotRAbrilNegativo = mTotRAbrilNegativo;
	}
	public double getmTotRMayoNegativo() {
		return mTotRMayoNegativo;
	}
	public void setmTotRMayoNegativo(double mTotRMayoNegativo) {
		this.mTotRMayoNegativo = mTotRMayoNegativo;
	}
	public double getmTotRJunioNegativo() {
		return mTotRJunioNegativo;
	}
	public void setmTotRJunioNegativo(double mTotRJunioNegativo) {
		this.mTotRJunioNegativo = mTotRJunioNegativo;
	}
	public double getmTotRJulioNegativo() {
		return mTotRJulioNegativo;
	}
	public void setmTotRJulioNegativo(double mTotRJulioNegativo) {
		this.mTotRJulioNegativo = mTotRJulioNegativo;
	}
	public double getmTotRAgostoNegativo() {
		return mTotRAgostoNegativo;
	}
	public void setmTotRAgostoNegativo(double mTotRAgostoNegativo) {
		this.mTotRAgostoNegativo = mTotRAgostoNegativo;
	}
	public double getmTotRSeptiembreNegativo() {
		return mTotRSeptiembreNegativo;
	}
	public void setmTotRSeptiembreNegativo(double mTotRSeptiembreNegativo) {
		this.mTotRSeptiembreNegativo = mTotRSeptiembreNegativo;
	}
	public double getmTotROctubreNegativo() {
		return mTotROctubreNegativo;
	}
	public void setmTotROctubreNegativo(double mTotROctubreNegativo) {
		this.mTotROctubreNegativo = mTotROctubreNegativo;
	}
	public double getmTotRNoviembreNegativo() {
		return mTotRNoviembreNegativo;
	}
	public void setmTotRNoviembreNegativo(double mTotRNoviembreNegativo) {
		this.mTotRNoviembreNegativo = mTotRNoviembreNegativo;
	}
	public double getmTotRDiciembreNegativo() {
		return mTotRDiciembreNegativo;
	}
	public void setmTotRDiciembreNegativo(double mTotRDiciembreNegativo) {
		this.mTotRDiciembreNegativo = mTotRDiciembreNegativo;
	}
	public double getmTotAEnero() {
		return mTotAEnero;
	}
	public void setmTotAEnero(double mTotAEnero) {
		this.mTotAEnero = mTotAEnero;
	}
	public double getmTotAFebrero() {
		return mTotAFebrero;
	}
	public void setmTotAFebrero(double mTotAFebrero) {
		this.mTotAFebrero = mTotAFebrero;
	}
	public double getmTotAMarzo() {
		return mTotAMarzo;
	}
	public void setmTotAMarzo(double mTotAMarzo) {
		this.mTotAMarzo = mTotAMarzo;
	}
	public double getmTotAAbril() {
		return mTotAAbril;
	}
	public void setmTotAAbril(double mTotAAbril) {
		this.mTotAAbril = mTotAAbril;
	}
	public double getmTotAMayo() {
		return mTotAMayo;
	}
	public void setmTotAMayo(double mTotAMayo) {
		this.mTotAMayo = mTotAMayo;
	}
	public double getmTotAJunio() {
		return mTotAJunio;
	}
	public void setmTotAJunio(double mTotAJunio) {
		this.mTotAJunio = mTotAJunio;
	}
	public double getmTotAJulio() {
		return mTotAJulio;
	}
	public void setmTotAJulio(double mTotAJulio) {
		this.mTotAJulio = mTotAJulio;
	}
	public double getmTotAAgosto() {
		return mTotAAgosto;
	}
	public void setmTotAAgosto(double mTotAAgosto) {
		this.mTotAAgosto = mTotAAgosto;
	}
	public double getmTotASeptiembre() {
		return mTotASeptiembre;
	}
	public void setmTotASeptiembre(double mTotASeptiembre) {
		this.mTotASeptiembre = mTotASeptiembre;
	}
	public double getmTotAOctubre() {
		return mTotAOctubre;
	}
	public void setmTotAOctubre(double mTotAOctubre) {
		this.mTotAOctubre = mTotAOctubre;
	}
	public double getmTotANoviembre() {
		return mTotANoviembre;
	}
	public void setmTotANoviembre(double mTotANoviembre) {
		this.mTotANoviembre = mTotANoviembre;
	}
	public double getmTotADiciembre() {
		return mTotADiciembre;
	}
	public void setmTotADiciembre(double mTotADiciembre) {
		this.mTotADiciembre = mTotADiciembre;
	}
	public double getiTotAbonos() {
		return iTotAbonos;
	}
	public void setiTotAbonos(double iTotAbonos) {
		this.iTotAbonos = iTotAbonos;
	}
	public double getiTotCargos() {
		return iTotCargos;
	}
	public void setiTotCargos(double iTotCargos) {
		this.iTotCargos = iTotCargos;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public ArrayList<Saldo> getEpSaldo() {
		return epSaldo;
	}
	public void setEpSaldo(ArrayList<Saldo> epSaldo) {
		this.epSaldo = epSaldo;
	}
	public void addRenglon(int r){
		renglon.add(r);
	}
	public int renglonLength(){
		return renglon.size();
	} 
	public int getRenglon(int index){
		return renglon.get(index);
	}
	public void addMovimiento(String mvto){
		tipoMov.add(mvto);
	}
	public int movimientoLength(){
		return tipoMov.size();
	}
	public String getMovimiento(int index){
		return tipoMov.get(index);
	}
	public void addSaldo(Saldo saldo){
		epSaldo.add(saldo);
	}
	public int saldoLength(){
		return epSaldo.size();
	} 
	public Saldo getSaldo(int index){
		return epSaldo.get(index);
	}
	public String getJustificacion() {
		return justificacion;
	}
	public void setJustificacion(String justificacion) {
		this.justificacion = justificacion;
	}
}
