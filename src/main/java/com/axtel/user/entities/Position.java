package com.axtel.user.entities;

import java.util.Base64;

public class Position {

    private String description;

    private int id;

    private PositionAdditional positionAdditional;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PositionAdditional getPositionAdditional() {
        return positionAdditional;
    }

    public void setPositionAdditional(PositionAdditional positionAdditional) {
        this.positionAdditional = positionAdditional;
    }

    @Override
    public String toString() {
        return "Position [description=" + description + ", id=" + id + ", positionAdditional=" + positionAdditional + "]";
    }
}
