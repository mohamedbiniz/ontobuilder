package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.elements.TextField;

/**
 * <p>Title: TextINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class TextINPUTElement extends INPUTElement
{
    protected String defaultValue;
    protected String label;
    protected int size = -1;
    protected int maxLength = -1;
    protected boolean readOnly;

    protected TextField text;

    public TextINPUTElement()
    {
        super(INPUTElement.TEXT);
        text = new TextField(ApplicationUtilities.getIntProperty("html.input.text.size"));
        text.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                if (maxLength > 0 && text.getText().length() >= maxLength)
                    e.consume();
            }
        });
    }

    public TextINPUTElement(String name, String defaultValue)
    {
        this();
        this.name = name;
        setDefaultValue(defaultValue);
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
        text.setText(defaultValue);
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
            text.setColumns(size);
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
        text.setEditable(!b);
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        text.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.text.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.input.text.defaultValue"),
                defaultValue
            },
            {
                ApplicationUtilities.getResourceString("html.input.text.value"), text.getText()
            },
            {
                ApplicationUtilities.getResourceString("html.input.text.label"), label
            },
            {
                ApplicationUtilities.getResourceString("html.input.text.size"),
                size != -1 ? new Integer(size) : null
            },
            {
                ApplicationUtilities.getResourceString("html.input.text.maxlength"),
                maxLength != -1 ? new Integer(maxLength) : null
            },
            {
                ApplicationUtilities.getResourceString("html.input.text.readonly"),
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
        return name + "=" + encode(text.getText());
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
        return text;
    }

    public void reset()
    {
        text.setText(defaultValue);
    }

    public void setValue(String value)
    {
        text.setText(value);
    }

    public String getValue()
    {
        return text.getText();
    }
}