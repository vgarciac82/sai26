package com.axtel.gestion.core;


public class State {
    private boolean opened;
    private boolean selected;

    public State(boolean opened, boolean selected) {
        this.opened = opened;
        this.selected = selected;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
