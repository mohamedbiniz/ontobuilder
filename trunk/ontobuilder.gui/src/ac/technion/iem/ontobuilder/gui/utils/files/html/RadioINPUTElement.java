package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.GridLayout;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: RadioINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class RadioINPUTElement extends INPUTElement
{
    protected ArrayList<RadioINPUTElementOption> options;
    protected String label;

    protected ButtonGroup radioGroup;
    protected JPanel component;

    public RadioINPUTElement()
    {
        super(INPUTElement.RADIO);
        options = new ArrayList<RadioINPUTElementOption>();
        component = new JPanel(new GridLayout(0, 1));
        radioGroup = new ButtonGroup();
    }

    public RadioINPUTElement(String name)
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

    public int getOptionsCount()
    {
        return options.size();
    }

    public void addOption(RadioINPUTElementOption option)
    {
        if (option == null)
            return;
        option.setRadio(this);
        options.add(option);
        if (!option.isDisabled())
            disabled = false;
        JRadioButton radio = (JRadioButton) option.getComponent();
        component.add(radio);
        radioGroup.add(radio);
    }

    public void removeOption(RadioINPUTElementOption option)
    {
        options.remove(option);
        component.removeAll();
        for (Iterator<RadioINPUTElementOption> i = options.iterator(); i.hasNext();)
        {
            RadioINPUTElementOption o = (RadioINPUTElementOption) i.next();
            JRadioButton radio = (JRadioButton) o.getComponent();
            component.add(radio);
            radioGroup.add(radio);
        }
    }

    public RadioINPUTElementOption getOption(int index)
    {
        if (index < 0 || index >= options.size())
            return null;
        return (RadioINPUTElementOption) options.get(index);
    }

    public String getCheckedValue()
    {
        for (Iterator<RadioINPUTElementOption> i = options.iterator(); i.hasNext();)
        {
            RadioINPUTElementOption option = (RadioINPUTElementOption) i.next();
            if (!option.isDisabled() && option.isChecked())
                return option.getValue();
        }
        return "";
    }

    public String getValue()
    {
        return getCheckedValue();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        for (Iterator<RadioINPUTElementOption> i = options.iterator(); i.hasNext();)
            ((RadioINPUTElementOption) i.next()).setDisabled(b);
    }

    public void check(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        for (Iterator<RadioINPUTElementOption> i = options.iterator(); i.hasNext();)
            ((RadioINPUTElementOption) i.next()).setChecked(false);
        ((RadioINPUTElementOption) options.get(index)).setChecked(true);
    }

    public void uncheck(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        ((RadioINPUTElementOption) options.get(index)).setChecked(false);
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
        for (Iterator<RadioINPUTElementOption> i = options.iterator(); i.hasNext();)
            node.add(((RadioINPUTElementOption) i.next()).getTreeBranch());
        return node;
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
                ApplicationUtilities.getResourceString("html.input.radio.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.input.radio.label"), label
            },
            {
                ApplicationUtilities.getResourceString("html.input.radio.options"),
                new Integer(options.size())
            },
            {
                ApplicationUtilities.getResourceString("html.input.disabled"),
                new Boolean(disabled)
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 5, data));
    }

    public String paramString()
    {
        try
        {
            return name + "=" + java.net.URLEncoder.encode(getCheckedValue(), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO: Handle the exception better.
            throw new RuntimeException(e);
        }
    }

    public Component getComponent()
    {
        return component;
    }

    public void reset()
    {
        for (Iterator<RadioINPUTElementOption> i = options.iterator(); i.hasNext();)
            ((RadioINPUTElementOption) i.next()).reset();
    }

    public boolean canSubmit()
    {
        return super.canSubmit() && getCheckedValue().length() > 0;
    }
}