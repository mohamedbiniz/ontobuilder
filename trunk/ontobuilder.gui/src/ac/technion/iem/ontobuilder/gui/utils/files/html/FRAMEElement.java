package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.net.URL;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: FRAMEElement</p>
 * Extends {@link HTMLElement}
 */
public class FRAMEElement extends HTMLElement
{
    private URL src;
    private String name;
    private boolean internal;

    public FRAMEElement(URL src, String name)
    {
        super(HTMLElement.FRAME);
        this.src = src;
        this.name = name;
        internal = false;
    }

    public boolean isInternal()
    {
        return internal;
    }

    public void setInternal(boolean b)
    {
        internal = b;
    }

    public void setSrc(URL src)
    {
        this.src = src;
    }

    public URL getSrc()
    {
        return src;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return name + " : " + src.toExternalForm();
    }

    public JTable getProperties()
    {
        String columnNames[] =
        {
            ApplicationUtilities.getResourceString("properties.attribute"),
            ApplicationUtilities.getResourceString("properties.value")
        };
        Object data[][] =
        {
            {
                ApplicationUtilities.getResourceString("html.frame.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.frame.source"), src
            },
            {
                ApplicationUtilities.getResourceString("html.frame.internal"),
                new Boolean(internal)
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 3, data));
    }
}