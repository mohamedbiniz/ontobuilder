package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;

import javax.swing.JRadioButton;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: RadioINPUTElementOption</p>
 * Extends {@link INPUTElement}
 */
public class RadioINPUTElementOption extends INPUTElement
{
    protected boolean defaultChecked;
    protected String value;
    protected String label;
    protected RadioINPUTElement radioInput;

    protected JRadioButton radio;

    public RadioINPUTElementOption()
    {
        super(INPUTElement.RADIOOPTION);
        radio = new JRadioButton();
    }

    public RadioINPUTElementOption(String value)
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
        radio.setSelected(b);
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public void setLabel(String label)
    {
        this.label = label;
        radio.setText(label);
    }

    public String getLabel()
    {
        return label;
    }

    public void setRadio(RadioINPUTElement radioInput)
    {
        this.radioInput = radioInput;
        this.name = radioInput.name;
    }

    public RadioINPUTElement getRadio()
    {
        return radioInput;
    }

    public String toString()
    {
        return value + (radio.isSelected() ? " * " : "");
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        radio.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.radio.option.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.input.radio.option.value"), value
            },
            {
                ApplicationUtilities.getResourceString("html.input.radio.option.label"), label
            },
            {
                ApplicationUtilities.getResourceString("html.input.radio.option.defaultChecked"),
                new Boolean(defaultChecked)
            },
            {
                ApplicationUtilities.getResourceString("html.input.radio.option.checked"),
                new Boolean(radio.isSelected())
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
        return (radio.isSelected() ? name + "=" + value : "");
    }

    public Component getComponent()
    {
        return radio;
    }

    public void reset()
    {
        radio.setSelected(defaultChecked);
    }

    public void setChecked(boolean b)
    {
        radio.setSelected(b);
    }

    public boolean isChecked()
    {
        return radio.isSelected();
    }
}