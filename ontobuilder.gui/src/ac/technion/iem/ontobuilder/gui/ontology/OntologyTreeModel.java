package ac.technion.iem.ontobuilder.gui.ontology;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * <p>Title: OntologyTreeModel</p>
 * Extends {@link DefaultTreeModel}
 */
public class OntologyTreeModel extends DefaultTreeModel
{
    private static final long serialVersionUID = 1L;

    protected OntologyModel model;

    public OntologyTreeModel(OntologyModel model)
    {
        super(model.getTreeBranch());
        this.model = model;
        updateTree();
    }

    public void updateTree()
    {
        setRoot(model.getTreeBranch());
    }

    public DefaultMutableTreeNode findNodeWithUserObject(Object object)
    {
        return findNodeWithUserObject((DefaultMutableTreeNode) root, object);
    }

    public DefaultMutableTreeNode findNodeWithUserObject(DefaultMutableTreeNode root, Object object)
    {
        for (Enumeration<?> children = root.breadthFirstEnumeration(); children.hasMoreElements();)
        {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            if (child.getUserObject().equals(object))
                return child;
        }
        return null;
    }

    public DefaultMutableTreeNode findChildNodeWithUserObject(Object object)
    {
        return findChildNodeWithUserObject((DefaultMutableTreeNode) root, object);
    }

    public DefaultMutableTreeNode findChildNodeWithUserObject(DefaultMutableTreeNode root,
        Object object)
    {
        for (Enumeration<?> children = root.children(); children.hasMoreElements();)
        {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            if (child.getUserObject().equals(object))
                return child;
        }
        return null;
    }

    public ArrayList<DefaultMutableTreeNode> findNodesWithUserObject(Object object)
    {
        return findNodesWithUserObject((DefaultMutableTreeNode) root, object);
    }

    // Breadth first search for nodes with a given userobject
    public ArrayList<DefaultMutableTreeNode> findNodesWithUserObject(DefaultMutableTreeNode root,
        Object object)
    {
        ArrayList<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>();
        for (Enumeration<?> allNodes = root.breadthFirstEnumeration(); allNodes.hasMoreElements();)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) allNodes.nextElement();
            if (node.getUserObject().equals(object))
                nodes.add(node);
        }
        return nodes;
    }
}