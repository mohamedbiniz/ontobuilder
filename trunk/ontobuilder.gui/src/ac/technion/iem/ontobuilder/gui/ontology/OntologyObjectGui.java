package ac.technion.iem.ontobuilder.gui.ontology;

import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.core.ontology.OntologyObject;
import ac.technion.iem.ontobuilder.gui.application.ObjectWithProperties;
import ac.technion.iem.ontobuilder.gui.utils.hypertree.NodeHyperTree;

/**
 * <p>
 * Title: OntologyObjectGui
 * </p>
  * <p>Description: Implements the methods of the OntologyObject used by the GUI</p>
  * Implements {@link ObjectWithProperties}
 */
public abstract class OntologyObjectGui implements ObjectWithProperties
{   
    @SuppressWarnings("unused")
    private OntologyObject ontologyObject;
    
    public OntologyObjectGui(OntologyObject ontologyObject)
    {
        this.ontologyObject = ontologyObject;
    }
    
    public DefaultMutableTreeNode getTreeBranch()
    {
        return new DefaultMutableTreeNode(this);
    }

    public NodeHyperTree getHyperTreeNode()
    {
        return new NodeHyperTree(this, NodeHyperTree.TERM);
    }
}
