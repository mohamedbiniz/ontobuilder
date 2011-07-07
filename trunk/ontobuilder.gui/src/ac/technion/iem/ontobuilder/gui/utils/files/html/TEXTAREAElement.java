package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.Font;
import java.io.UnsupportedEncodingException;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.elements.TextArea;

/**
 * <p>Title: TEXTAREAElement</p>
 * Extends {@link INPUTElement}
 */
public class TEXTAREAElement extends INPUTElement
{
    protected String defaultValue;
    protected String label;
    protected int rows = -1;
    protected int cols = -1;
    protected boolean readOnly;

    protected TextArea textarea;

    public TEXTAREAElement()
    {
        super(INPUTElement.TEXTAREA);
        textarea = new TextArea(ApplicationUtilities.getIntProperty("html.textarea.rows"),
            ApplicationUtilities.getIntProperty("html.textarea.cols"));
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);
        textarea.setFont(new Font("Monospaced", Font.PLAIN, textarea.getFont().getSize()));
    }

    public TEXTAREAElement(String name)
    {
        this();
        this.name = name;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
        textarea.setText(defaultValue);
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

    public void setRows(int rows)
    {
        this.rows = rows;
        if (rows > 0)
            textarea.setRows(rows);
    }

    public int getRows()
    {
        return rows;
    }

    public void setCols(int cols)
    {
        this.cols = cols;
        if (cols > 0)
            textarea.setColumns(cols);
    }

    public int getCols()
    {
        return cols;
    }

    public void setReadOnly(boolean b)
    {
        readOnly = b;
        textarea.setEditable(!b);
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        textarea.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.textarea.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.textarea.defaultValue"), defaultValue
            },
            {
                ApplicationUtilities.getResourceString("html.textarea.value"), textarea.getText()
            },
            {
                ApplicationUtilities.getResourceString("html.textarea.label"), label
            },
            {
                ApplicationUtilities.getResourceString("html.textarea.rows"),
                rows != -1 ? new Integer(rows) : null
            },
            {
                ApplicationUtilities.getResourceString("html.textarea.cols"),
                cols != -1 ? new Integer(cols) : null
            },
            {
                ApplicationUtilities.getResourceString("html.textarea.readonly"),
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
        return name + "=" + encode(textarea.getText());
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
        return new JScrollPane(textarea);
    }

    public void reset()
    {
        textarea.setText(defaultValue);
    }

    public void setValue(String value)
    {
        textarea.setText(value);
    }

    public String getValue()
    {
        return textarea.getText();
    }
}