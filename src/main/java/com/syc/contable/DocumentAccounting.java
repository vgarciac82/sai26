package com.syc.contable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import java.util.Base64;

public class DocumentAccounting {

    private Map<String, String> dataHeaderMap;

    private List<Map<String, String>> dataDetailMap;

    public DocumentAccounting() {
        this(100);
    }

    public DocumentAccounting(int initialCapacity) {
        dataHeaderMap = new CaseInsensitiveMap<String, String>();
        dataDetailMap = new ArrayList<Map<String, String>>(initialCapacity);
    }

    public Map<String, String> getDataHeaderMap() {
        return dataHeaderMap;
    }

    public void setDataHeaderMap(Map<String, String> dataHeaderMap) {
        this.dataHeaderMap = dataHeaderMap;
    }

    public List<Map<String, String>> getDataDetailMap() {
        return dataDetailMap;
    }

    public void setDataDetailMap(List<Map<String, String>> dataDetailMap) {
        this.dataDetailMap = dataDetailMap;
    }
}
