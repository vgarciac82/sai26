package com.syc.gestion.core;

import com.jenkov.prizetags.tree.impl.TreeTableMapping;
import java.util.Base64;

public class TreeTableMappingFortimax extends TreeTableMapping {

    private String nombre_usuario = null;

    public TreeTableMappingFortimax(String tableName, String idColumn, String parentIdColumn, String treeIdColumn, String nameColumn, String typeColumn, String toolTipColumn, String nombre_usuario) {
        super(tableName, idColumn, parentIdColumn, treeIdColumn, nameColumn, typeColumn, toolTipColumn);
        this.nombre_usuario = nombre_usuario;
        // TODO Auto-generated constructor stub
    }
}
