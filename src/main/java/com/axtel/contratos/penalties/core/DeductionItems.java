package com.axtel.contratos.penalties.core;

import java.util.Base64;

public class DeductionItems {

    private int nIdPenaltyDeduction;

    private int nIdItemContract;

    private int nConsecutiveItem;

    private String fItemDeliveryDate;

    private int nPiecesElements;

    private double mItemAmount;

    //A
    private double mDelayAmount;

    //B
    private float nDailyPenaltyPercentage;

    //C=A*B(mMontoConAtraso*nPorcentajePenalidadDiaria)
    private double mAmountDailyDeduction;

    //D
    private int nDailyDays;

    //E=C*D
    private double mTotalAmountDeduction;

    //F
    private float nComplianceGuaranteePercentage;

    //G=F/B
    private float nPenaltyDays;

    //H
    private double mAmountDeduction;

    public int getnIdPenaltyDeduction() {
        return nIdPenaltyDeduction;
    }

    public void setnIdPenaltyDeduction(int nIdPenaltyDeduction) {
        this.nIdPenaltyDeduction = nIdPenaltyDeduction;
    }

    public int getnIdItemContract() {
        return nIdItemContract;
    }

    public void setnIdItemContract(int nIdItemContract) {
        this.nIdItemContract = nIdItemContract;
    }

    public String getfItemDeliveryDate() {
        return fItemDeliveryDate;
    }

    public void setfItemDeliveryDate(String fItemDeliveryDate) {
        this.fItemDeliveryDate = fItemDeliveryDate;
    }

    public int getnPiecesElements() {
        return nPiecesElements;
    }

    public void setnPiecesElements(int nPiecesElements) {
        this.nPiecesElements = nPiecesElements;
    }

    public double getmItemAmount() {
        return mItemAmount;
    }

    public void setmItemAmount(double mItemAmount) {
        this.mItemAmount = mItemAmount;
    }

    public double getmDelayAmount() {
        return mDelayAmount;
    }

    public void setmDelayAmount(double mDelayAmount) {
        this.mDelayAmount = mDelayAmount;
    }

    public float getnDailyPenaltyPercentage() {
        return nDailyPenaltyPercentage;
    }

    public void setnDailyPenaltyPercentage(float nDailyPenaltyPercentage) {
        this.nDailyPenaltyPercentage = nDailyPenaltyPercentage;
    }

    public double getmAmountDailyDeduction() {
        return mAmountDailyDeduction;
    }

    public void setmAmountDailyDeduction(double mAmountDailyDeduction) {
        this.mAmountDailyDeduction = mAmountDailyDeduction;
    }

    public int getnDailyDays() {
        return nDailyDays;
    }

    public void setnDailyDays(int nDailyDays) {
        this.nDailyDays = nDailyDays;
    }

    public double getmTotalAmountDeduction() {
        return mTotalAmountDeduction;
    }

    public void setmTotalAmountDeduction(double mTotalAmountDeduction) {
        this.mTotalAmountDeduction = mTotalAmountDeduction;
    }

    public float getnComplianceGuaranteePercentage() {
        return nComplianceGuaranteePercentage;
    }

    public void setnComplianceGuaranteePercentage(float nComplianceGuaranteePercentage) {
        this.nComplianceGuaranteePercentage = nComplianceGuaranteePercentage;
    }

    public double getmAmountDeduction() {
        return mAmountDeduction;
    }

    public float getnPenaltyDays() {
        return nPenaltyDays;
    }

    public void setnPenaltyDays(float nPenaltyDays) {
        this.nPenaltyDays = nPenaltyDays;
    }

    public void setmAmountDeduction(double mAmountDeduction) {
        this.mAmountDeduction = mAmountDeduction;
    }

    public int getnConsecutiveItem() {
        return nConsecutiveItem;
    }

    public void setnConsecutiveItem(int nConsecutiveItem) {
        this.nConsecutiveItem = nConsecutiveItem;
    }
}
