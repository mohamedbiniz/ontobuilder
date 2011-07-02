package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: ButtonINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class ButtonINPUTElement extends INPUTElement
{
    protected JButton button;

    public ButtonINPUTElement()
    {
        super(INPUTElement.BUTTON);
        button = new JButton();
    }

    public ButtonINPUTElement(String name, String value)
    {
        this();
        this.name = name;
        button.setText(value);
    }

    public void setValue(String value)
    {
        button.setText(value);
    }

    public String getValue()
    {
        return button.getText();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        button.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.button.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.input.button.value"), button.getText()
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 3, data));
    }

    public String paramString()
    {
        try
        {
            return name + "=" + java.net.URLEncoder.encode(button.getText(), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO: Handle the exception better.
            throw new RuntimeException(e);
        }
    }

    public Component getComponent()
    {
        return button;
    }

    public boolean canSubmit()
    {
        return false;
    }
}