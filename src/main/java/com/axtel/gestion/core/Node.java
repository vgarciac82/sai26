package com.axtel.gestion.core;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import java.util.Base64;

public class Node {

    private String id;

    private String text;

    private String icon;

    @SerializedName("state")
    private State nodeState;

    private List<Node> children;

    public Node(String id, String text, String icon, boolean opened, boolean selected) {
        this.id = id;
        this.text = text;
        this.icon = icon;
        this.nodeState = new State(opened, selected);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public State getNodeState() {
        return nodeState;
    }

    public void setNodeState(State nodeState) {
        this.nodeState = nodeState;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }
}
