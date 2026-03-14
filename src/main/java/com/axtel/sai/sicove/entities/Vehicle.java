package com.axtel.sai.sicove.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Base64;

public class Vehicle {

    private int id;

    private int idInventory;

    private String inventoryCode;

    private String description;

    private String type;

    private String brand;

    private String subBrand;

    private int model;

    private String color;

    private Integer doors;

    private String cylinders;

    private String transmissionType;

    private Date registeredDate;

    private String serial;

    private BigDecimal unitCostTotal;

    private Integer agencyId;

    private Integer useId;

    private String licensePlate;

    private String currentKilometers;

    private int statusId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdInventory() {
        return idInventory;
    }

    public void setIdInventory(int idInventory) {
        this.idInventory = idInventory;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSubBrand() {
        return subBrand;
    }

    public void setSubBrand(String subBrand) {
        this.subBrand = subBrand;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getDoors() {
        return doors;
    }

    public void setDoors(Integer doors) {
        this.doors = doors;
    }

    public String getCylinders() {
        return cylinders;
    }

    public void setCylinders(String cylinders) {
        this.cylinders = cylinders;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public BigDecimal getUnitCostTotal() {
        return unitCostTotal;
    }

    public void setUnitCostTotal(BigDecimal unitCostTotal) {
        this.unitCostTotal = unitCostTotal;
    }

    public Integer getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Integer agencyId) {
        this.agencyId = agencyId;
    }

    public Integer getUseId() {
        return useId;
    }

    public void setUseId(Integer useId) {
        this.useId = useId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getCurrentKilometers() {
        return currentKilometers;
    }

    public void setCurrentKilometers(String currentKilometers) {
        this.currentKilometers = currentKilometers;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    @Override
    public String toString() {
        return "Vehicle [id=" + id + ", idInventory=" + idInventory + ", inventoryCode=" + inventoryCode + ", description=" + description + ", type=" + type + ", brand=" + brand + ", subBrand=" + subBrand + ", model=" + model + ", color=" + color + ", doors=" + doors + ", cylinders=" + cylinders + ", transmissionType=" + transmissionType + ", registeredDate=" + registeredDate + ", serial=" + serial + ", unitCostTotal=" + unitCostTotal + ", agencyId=" + agencyId + ", useId=" + useId + ", licensePlate=" + licensePlate + ", currentKilometers=" + currentKilometers + ", statusId=" + statusId + "]";
    }
}
