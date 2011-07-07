package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.io.UnsupportedEncodingException;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.elements.TextField;

/**
 * <p>Title: HiddenINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class HiddenINPUTElement extends INPUTElement
{
    protected String value;

    protected TextField hidden;

    public HiddenINPUTElement()
    {
        super(INPUTElement.HIDDEN);
        hidden = new TextField(ApplicationUtilities.getIntProperty("html.input.hidden.size"));
        // hidden.setEditable(false);
    }

    public HiddenINPUTElement(String name, String value)
    {
        this();
        this.name = name;
        setValue(value);
    }

    public void setValue(String value)
    {
        this.value = value;
        hidden.setText(value);
    }

    public String getValue()
    {
        return value;
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
                ApplicationUtilities.getResourceString("html.input.hidden.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.input.hidden.value"), value
            },
            {
                ApplicationUtilities.getResourceString("html.input.disabled"),
                new Boolean(disabled)
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 4, data));
    }

    public String paramString()
    {
        try
        {
            return name + "=" + java.net.URLEncoder.encode(hidden.getText(), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO: Handle the exception better.
            throw new RuntimeException(e);
        }
    }

    public Component getComponent()
    {
        return hidden;
    }
}