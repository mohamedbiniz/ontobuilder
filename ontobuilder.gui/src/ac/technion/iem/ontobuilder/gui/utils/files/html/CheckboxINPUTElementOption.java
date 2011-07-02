package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: CheckboxINPUTElementOption</p>
 * Extends {@link INPUTElement}
 */
public class CheckboxINPUTElementOption extends INPUTElement
{
    protected boolean defaultChecked;
    protected String label;
    protected String value;
    protected CheckboxINPUTElement checkboxInput;

    protected JCheckBox checkbox;

    public CheckboxINPUTElementOption()
    {
        super(INPUTElement.CHECKBOXOPTION);
        checkbox = new JCheckBox();
    }

    public CheckboxINPUTElementOption(String value)
    {
        this();
        this.value = value;
    }

    public boolean isDefaultChecked()
    {
        return defaultChecked;
    }

    public void setDefaultChecked(boolean b)
    {
        defaultChecked = b;
        checkbox.setSelected(b);
    }

    public void setLabel(String label)
    {
        this.label = label;
        checkbox.setText(label);
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
        return value;
    }

    public void setCheckbox(CheckboxINPUTElement checkboxInput)
    {
        this.checkboxInput = checkboxInput;
        this.name = checkboxInput.name;
    }

    public CheckboxINPUTElement getCheckbox()
    {
        return checkboxInput;
    }

    public String toString()
    {
        return value + (checkbox.isSelected() ? " * " : "");
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        checkbox.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.checkbox.option.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.input.checkbox.option.value"), value
            },
            {
                ApplicationUtilities.getResourceString("html.input.checkbox.option.label"), label
            },
            {
                ApplicationUtilities.getResourceString("html.input.checkbox.option.defaultChecked"),
                new Boolean(defaultChecked)
            },
            {
                ApplicationUtilities.getResourceString("html.input.checkbox.option.checked"),
                new Boolean(checkbox.isSelected())
            },
            {
                ApplicationUtilities.getResourceString("html.input.disabled"),
                new Boolean(disabled)
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 7, data));
    }

    public String paramString()
    {
        return (checkbox.isSelected() ? name + " = " + value : "");
    }

    public Component getComponent()
    {
        return checkbox;
    }

    public void reset()
    {
        checkbox.setSelected(defaultChecked);
    }

    public void setChecked(boolean b)
    {
        checkbox.setSelected(b);
    }

    public boolean isChecked()
    {
        return checkbox.isSelected();
    }
}