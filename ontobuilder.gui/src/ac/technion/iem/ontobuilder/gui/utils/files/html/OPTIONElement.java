package ac.technion.iem.ontobuilder.gui.utils.files.html;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: OPTIONElement</p>
 * Extends {@link INPUTElement}
 */
public class OPTIONElement extends INPUTElement
{
    private boolean defaultSelected;
    private boolean selected;
    private String value;
    private String label;
    private SELECTElement select;

    public OPTIONElement()
    {
        super(INPUTElement.OPTION);
    }

    public OPTIONElement(String value)
    {
        this();
        this.value = value;
    }

    public boolean isDefaultSelected()
    {
        return defaultSelected;
    }

    public void setDefaultSelected(boolean b)
    {
        defaultSelected = b;
        selected = b;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean b)
    {
        selected = b;
        if (select != null)
            select.updateSelectionView(this);
    }

    public void setLabel(String label)
    {
        this.label = label;
        if (select != null)
            select.updateLabelView(this);
    }

    public String getLabel()
    {
        return label;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        if (value == null)
            return label;
        else
            return value;
    }

    public void setSelect(SELECTElement select)
    {
        this.select = select;
        this.name = select.name;
    }

    public SELECTElement getSelect()
    {
        return select;
    }

    public String toString()
    {
        return value + (selected ? " * " : "");
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
                ApplicationUtilities.getResourceString("html.input.type"), getInputType()
            },
            {
                ApplicationUtilities.getResourceString("html.option.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.option.value"), value
            },
            {
                ApplicationUtilities.getResourceString("html.option.label"), label
            },
            {
                ApplicationUtilities.getResourceString("html.option.defaultSelected"),
                new Boolean(defaultSelected)
            },
            {
                ApplicationUtilities.getResourceString("html.option.selected"),
                new Boolean(selected)
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 6, data));
    }

    public void reset()
    {
        setSelected(defaultSelected);
    }
}