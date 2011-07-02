package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.utils.elements.TextField;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;

/**
 * <p>Title: FileINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class FileINPUTElement extends INPUTElement
{
    protected String label;
    protected int size = -1;
    protected int maxLength = -1;
    protected boolean readOnly;

    protected JPanel component;
    protected TextField file;
    protected JButton browse;

    public FileINPUTElement()
    {
        super(INPUTElement.FILE);
        component = new JPanel();
        component.add(file = new TextField(ApplicationUtilities
            .getIntProperty("html.input.file.size")));
        component.add(browse = new JButton(ApplicationUtilities
            .getResourceString("html.input.file.browse")));
        file.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                if (maxLength > 0 && file.getText().length() >= maxLength)
                    e.consume();
            }
        });
        browse.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (!readOnly)
                {
                    File f = FileUtilities.openFileDialog(null);
                    if (f != null)
                        file.setText(f.getAbsolutePath());
                }
            }
        });
    }

    public FileINPUTElement(String name)
    {
        this();
        this.name = name;
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
            file.setColumns(size);
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
        file.setEditable(!b);
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        file.setEnabled(!b);
        browse.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.file.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.input.file.label"), label
            },
            {
                ApplicationUtilities.getResourceString("html.input.file.value"), file.getText()
            },
            {
                ApplicationUtilities.getResourceString("html.input.file.size"),
                size != -1 ? new Integer(size) : null
            },
            {
                ApplicationUtilities.getResourceString("html.input.file.maxlength"),
                maxLength != -1 ? new Integer(maxLength) : null
            },
            {
                ApplicationUtilities.getResourceString("html.input.file.readonly"),
                new Boolean(readOnly)
            },
            {
                ApplicationUtilities.getResourceString("html.input.disabled"),
                new Boolean(disabled)
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 8, data));
    }

    public String paramString()
    {
        try
        {
            return name + "=" + java.net.URLEncoder.encode(file.getText(), "UTF-8");
        }
        // TODO: Handle the exception better
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Component getComponent()
    {
        return component;
    }

    public void reset()
    {
        file.setText("");
    }

    public void setValue(String value)
    {
        file.setText(value);
    }

    public String getValue()
    {
        return file.getText();
    }
}