package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: ResetINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class ResetINPUTElement extends INPUTElement
{
    protected JButton reset;

    public ResetINPUTElement()
    {
        super(INPUTElement.RESET);
        reset = new JButton();
        reset.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (form != null)
                    form.reset();
            }
        });
    }

    public ResetINPUTElement(String name, String value)
    {
        this();
        this.name = name;
        reset.setText(value);
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        reset.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.reset.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.input.reset.value"), reset.getText()
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 3, data));
    }

    public String paramString()
    {
        try
        {
            return name + "=" + java.net.URLEncoder.encode(reset.getText(), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO: Handle the exception better.
            throw new RuntimeException(e);
        }
    }

    public Component getComponent()
    {
        return reset;
    }

    public void setValue(String value)
    {
        reset.setText(value);
    }

    public String getValue()
    {
        return reset.getText();
    }

    public boolean canSubmit()
    {
        return false;
    }
}