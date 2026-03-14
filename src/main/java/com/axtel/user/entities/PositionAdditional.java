package com.axtel.user.entities;

import java.util.Base64;

public class PositionAdditional {

    private int id;

    private String level;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "PositionAdditional [id=" + id + ", level=" + level + "]";
    }
}
