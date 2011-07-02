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
 * <p>Title: SubmitINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class SubmitINPUTElement extends INPUTElement
{
    protected JButton submit;
    protected boolean pressed;

    public SubmitINPUTElement()
    {
        super(INPUTElement.SUBMIT);
        submit = new JButton();
        submit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (form != null)
                    form.clearPressed();
                pressed = true;
            }
        });
    }

    public SubmitINPUTElement(String name, String value)
    {
        this();
        this.name = name;
        submit.setText(value);
    }

    public void setValue(String value)
    {
        submit.setText(value);
    }

    public String getValue()
    {
        return submit.getText();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        submit.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.submit.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.input.submit.value"), submit.getText()
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 3, data));
    }

    public String paramString()
    {
        return name + "=" + encode(submit.getText());
    }

    public String encode(String s)
    {
        try
        {
            return java.net.URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO: Handle the exception better.
            throw new RuntimeException(e);
        }
    }

    public Component getComponent()
    {
        return submit;
    }

    public boolean isPressed()
    {
        return pressed;
    }

    public void forcePressed()
    {
        pressed = true;
    }

    public void clearPressed()
    {
        pressed = false;
    }

    public boolean canSubmit()
    {
        return super.canSubmit() && pressed;
    }
}