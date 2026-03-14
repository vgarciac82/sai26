/*
    Copyright 2004 Jenkov Development

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import com.jenkov.prizetags.tree.impl.TreeNode;
import com.jenkov.prizetags.tree.impl.TreeTableMapping;
import com.jenkov.prizetags.tree.itf.IResultSetProcessor;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import java.util.Base64;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class DataBaseTreeReader {

    private TreeTableMapping mapping = null;

    private IResultSetProcessor processor = null;

    public DataBaseTreeReader() {
    }

    public DataBaseTreeReader(TreeTableMapping mapping) {
        this.mapping = mapping;
    }

    public DataBaseTreeReader(TreeTableMapping mapping, IResultSetProcessor processor) {
        this.mapping = mapping;
        this.processor = processor;
    }

    public ITreeNode readTree(Connection connection, String sql) throws SQLException {
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = connection.prepareStatement(sql);
            result = statement.executeQuery();
            return readTree(result);
        } finally {
            if (result != null) {
                result.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public ITreeNode readTree(ResultSet result) throws SQLException {
        ITreeNode root = null;
        Map nodes = new LinkedHashMap();
        Map parentIds = new LinkedHashMap();
        while (result.next()) {
            ITreeNode treeNode = readTreeNode(result);
            if (processor != null) {
                processor.process(result, treeNode);
            }
            nodes.put(treeNode.getId(), treeNode);
            if (mapping.getParentIdColumn() != null) {
                parentIds.put(treeNode.getId(), result.getString(mapping.getParentIdColumn()));
            }
        }
        Iterator nodeIds = nodes.keySet().iterator();
        while (nodeIds.hasNext()) {
            String nodeId = (String) nodeIds.next();
            ITreeNode node = (ITreeNode) nodes.get(nodeId);
            String parentId = (String) parentIds.get(node.getId());
            ITreeNode parent = (ITreeNode) nodes.get(parentId);
            if (parent != null && parent != node) {
                parent.addChild(node);
            } else {
                root = node;
            }
        }
        while (root.getParent() != null) {
            root = root.getParent();
        }
        nodes.clear();
        parentIds.clear();
        return root;
    }

    private ITreeNode readTreeNode(ResultSet result) throws SQLException {
        ITreeNode node = new TreeNode();
        if (mapping.getIdColumn() != null)
            node.setId(result.getString(mapping.getIdColumn()));
        if (mapping.getNameColumn() != null)
            node.setName(result.getString(mapping.getNameColumn()));
        if (mapping.getTypeColumn() != null)
            node.setType(result.getString(mapping.getTypeColumn()));
        if (mapping.getToolTipColumn() != null)
            node.setToolTip(result.getString(mapping.getToolTipColumn()));
        return node;
    }
}
