package com.axtel.user.entities;

import java.util.Base64;

public class ExecutingUnit {

    private String budgetExecutingUnit;

    private String description;

    private String id;

    private WorkCenter workCenter;

    public String getBudgetExecutingUnit() {
        return budgetExecutingUnit;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public WorkCenter getWorkCenter() {
        return workCenter;
    }

    public void setBudgetExecutingUnit(String budgetExecutingUnit) {
        this.budgetExecutingUnit = budgetExecutingUnit;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setWorkCenter(WorkCenter workCenter) {
        this.workCenter = workCenter;
    }

    @Override
    public String toString() {
        return "ExecutingUnit [id=" + id + ", description=" + description + ", budgetExecutingUnit=" + budgetExecutingUnit + ", workCenter=" + workCenter + "]";
    }
}
