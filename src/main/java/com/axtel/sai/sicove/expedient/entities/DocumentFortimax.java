package com.axtel.sai.sicove.expedient.entities;

import java.util.Base64;

public class DocumentFortimax {

    private String fortimax;

    private String documentName;

    private String documentPath;

    private boolean attached;

    public String getFortimax() {
        return fortimax;
    }

    public void setFortimax(String fortimax) {
        this.fortimax = fortimax;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public boolean isAttached() {
        return attached;
    }

    public void setAttached(boolean attached) {
        this.attached = attached;
    }

    @Override
    public String toString() {
        return "DocumentFortimax [fortimax=" + fortimax + ", documentName=" + documentName + ", documentPath=" + documentPath + ", attached=" + attached + "]";
    }
}
