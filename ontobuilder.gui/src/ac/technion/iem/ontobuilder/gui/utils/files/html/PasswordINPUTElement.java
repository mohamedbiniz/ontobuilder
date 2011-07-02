package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;

import javax.swing.JPasswordField;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: PasswordINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class PasswordINPUTElement extends INPUTElement
{
    protected String defaultValue;
    protected String label;
    protected int size = -1;
    protected int maxLength = -1;
    protected boolean readOnly;

    protected JPasswordField password;

    public PasswordINPUTElement()
    {
        super(INPUTElement.PASSWORD);
        password = new JPasswordField(
            ApplicationUtilities.getIntProperty("html.input.password.size"));
        password.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                if (maxLength > 0 && password.getPassword().length >= maxLength)
                    e.consume();
            }
        });
    }

    public PasswordINPUTElement(String name, String defaultValue)
    {
        this();
        this.name = name;
        setDefaultValue(defaultValue);
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
        password.setText(defaultValue);
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setSize(int size)
    {
        this.size = size;
        if (size >= 0)
            password.setColumns(size);
    }

    public int getSize()
    {
        return size;
    }

    public void setMaxLength(int maxLength)
    {
        this.maxLength = maxLength;
    }

    public int getMaxLength()
    {
        return maxLength;
    }

    public void setReadOnly(boolean b)
    {
        readOnly = b;
        password.setEditable(!b);
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        password.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.password.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.input.password.defaultValue"),
                defaultValue
            },
            {
                ApplicationUtilities.getResourceString("html.input.password.value"),
                new String(password.getPassword())
            },
            {
                ApplicationUtilities.getResourceString("html.input.password.label"), label
            },
            {
                ApplicationUtilities.getResourceString("html.input.password.size"),
                size != -1 ? new Integer(size) : null
            },
            {
                ApplicationUtilities.getResourceString("html.input.password.maxlength"),
                maxLength != -1 ? new Integer(maxLength) : null
            },
            {
                ApplicationUtilities.getResourceString("html.input.password.readonly"),
                new Boolean(readOnly)
            },
            {
                ApplicationUtilities.getResourceString("html.input.disabled"),
                new Boolean(disabled)
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 9, data));
    }

    public String paramString()
    {
        try
        {
            return name + "=" +
                java.net.URLEncoder.encode(new String(password.getPassword()), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO: Handle the exception better.
            throw new RuntimeException(e);
        }
    }

    public Component getComponent()
    {
        return password;
    }

    public void reset()
    {
        password.setText(defaultValue);
    }

    public void setValue(String value)
    {
        password.setText(value);
    }

    public String getValue()
    {
        return new String(password.getPassword());
    }
}