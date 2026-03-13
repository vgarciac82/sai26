package com.axtel.contratos.penalties.core;


/**
 * @author hfariasr
 *
 */
public class PenaltyItems {
	private int nIdPenaltyDeduction;
	private int nIdItemContract;
	private int nConsecutiveItem;
	private String fItemDeliveryDate;
	private int nPiecesElements;
	private double mItemAmount;
	private double mDelayAmount;//A 
	private float nDailyPenaltyPercentage;//B
	private double mAmountDailyPenalty;//C=A*B(mMontoConAtraso*nPorcentajePenalidadDiaria)
	private int nDailyDays;//D
	private double mTotalAmountPenalty;//E=C*D
	private float nComplianceGuaranteePercentage;//F
	private float nPenaltyDays;//G=F/B
	private double mAmountPenalty;//H
	
	public int getnIdPenaltyDeduction() {
		return nIdPenaltyDeduction;
	}
	
	public void setnIdPenaltyDeduction( int nIdPenaltyDeduction ) {
		this.nIdPenaltyDeduction = nIdPenaltyDeduction;
	}
	
	public int getnIdItemContract() {
		return nIdItemContract;
	}
	
	public void setnIdItemContract( int nIdItemContract ) {
		this.nIdItemContract = nIdItemContract;
	}
	
	public String getfItemDeliveryDate() {
		return fItemDeliveryDate;
	}
	
	public void setfItemDeliveryDate( String fItemDeliveryDate ) {
		this.fItemDeliveryDate = fItemDeliveryDate;
	}
	
	public int getnPiecesElements() {
		return nPiecesElements;
	}
	
	public void setnPiecesElements( int nPiecesElements ) {
		this.nPiecesElements = nPiecesElements;
	}
	
	public double getmItemAmount() {
		return mItemAmount;
	}
	
	public void setmItemAmount( double mItemAmount ) {
		this.mItemAmount = mItemAmount;
	}

	public double getmDelayAmount() {
		return mDelayAmount;
	}
	
	public void setmDelayAmount( double mDelayAmount ) {
		this.mDelayAmount = mDelayAmount;
	}
	
	public float getnDailyPenaltyPercentage() {
		return nDailyPenaltyPercentage;
	}
	
	public void setnDailyPenaltyPercentage( float nDailyPenaltyPercentage ) {
		this.nDailyPenaltyPercentage = nDailyPenaltyPercentage;
	}
	
	public double getmAmountDailyPenalty() {
		return mAmountDailyPenalty;
	}
	
	public void setmAmountDailyPenalty( double mAmountDailyPenalty ) {
		this.mAmountDailyPenalty = mAmountDailyPenalty;
	}
	
	public int getnDailyDays() {
		return nDailyDays;
	}
	
	public void setnDailyDays( int nDailyDays ) {
		this.nDailyDays = nDailyDays;
	}
	
	public double getmTotalAmountPenalty() {
		return mTotalAmountPenalty;
	}
	
	public void setmTotalAmountPenalty( double mTotalAmountPenalty ) {
		this.mTotalAmountPenalty = mTotalAmountPenalty;
	}
	
	public float getnComplianceGuaranteePercentage() {
		return nComplianceGuaranteePercentage;
	}
	
	public void setnComplianceGuaranteePercentage( float nComplianceGuaranteePercentage ) {
		this.nComplianceGuaranteePercentage = nComplianceGuaranteePercentage;
	}
	
	public float getnPenaltyDays() {
		return nPenaltyDays;
	}

	public void setnPenaltyDays( float nPenaltyDays ) {
		this.nPenaltyDays = nPenaltyDays;
	}

	public double getmAmountPenalty() {
		return mAmountPenalty;
	}
	
	public void setmAmountPenalty( double mAmountPenalty ) {
		this.mAmountPenalty = mAmountPenalty;
	}

	public int getnConsecutiveItem() {
		return nConsecutiveItem;
	}

	public void setnConsecutiveItem( int nConsecutiveItem ) {
		this.nConsecutiveItem = nConsecutiveItem;
	}
	
}
