package ac.technion.iem.ontobuilder.gui.utils.files.html;

import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.gui.application.ObjectWithProperties;

/**
 * <p>Title: HTMLElement</p>
 * Implements {@link INPUTElement}
 */
public abstract class HTMLElement implements ObjectWithProperties
{
    public static final String FORM = "form";
    public static final String INPUT = "input";
    public static final String FRAME = "frame";
    public static final String META = "meta";
    public static final String A = "a";

    private String type;
    private String description;

    public HTMLElement(String type)
    {
        this.type = type;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public String getType()
    {
        return type;
    }

    public abstract JTable getProperties();

    public DefaultMutableTreeNode getTreeBranch()
    {
        return new DefaultMutableTreeNode(this);
    }

    public String toString()
    {
        return type;
    }
}