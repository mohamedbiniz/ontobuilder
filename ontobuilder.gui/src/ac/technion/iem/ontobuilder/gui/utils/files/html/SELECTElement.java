package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: SELECTElement</p>
 * Extends {@link INPUTElement}
 */
public class SELECTElement extends INPUTElement
{
    protected ArrayList<OPTIONElement> options;
    protected String label;
    protected boolean multiple;
    protected int size = -1;

    protected JComboBox select;
    protected ComboSelectModel comboSelectModel;
    protected JList list;
    protected ListSelectModel listSelectModel;
    protected ListSelectSelectionModel listSelectSelectionModel;
    protected JPanel component;

    public SELECTElement()
    {
        super(INPUTElement.SELECT);
        options = new ArrayList<OPTIONElement>();

        select = new JComboBox(comboSelectModel = new ComboSelectModel());
        select.setRenderer(new ComboSelectRenderer());

        list = new JList(listSelectModel = new ListSelectModel());
        list.setSelectionModel(listSelectSelectionModel = new ListSelectSelectionModel());

        component = new JPanel();
        // The default is a combo box
        component.add(select);
    }

    public SELECTElement(String name)
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

    public boolean isMultiple()
    {
        return multiple;
    }

    public void setMultiple(boolean b)
    {
        if (multiple && !b) // Going from multiple to single
        {
            transformListToCombo();
            component.removeAll();
            component.add(select);
        }
        else if (!multiple && b) // Going from single to multiple
        {
            transformComboToList();
            component.removeAll();
            component.add(new JScrollPane(list));
        }
        multiple = b;
    }

    protected void transformListToCombo()
    {
    }

    protected void transformComboToList()
    {
    }

    public void setSize(int size)
    {
        this.size = size;
        if (size > 0)
        {
            select.setMaximumRowCount(size);
            list.setVisibleRowCount(size);
        }
    }

    public int getSize()
    {
        return size;
    }

    public int getOptionsCount()
    {
        return options.size();
    }

    public void addOption(OPTIONElement option)
    {
        if (option == null)
            return;
        option.setSelect(this);
        options.add(option);
        if (multiple)
            listSelectModel.fireItemAdded(options.indexOf(option));
        else
            comboSelectModel.fireItemAdded(option);
    }

    public void removeOption(OPTIONElement option)
    {
        if (option == null)
            return;
        options.remove(option);
        select.removeItem(option);
        if (multiple)
            listSelectModel.fireItemRemoved(options.indexOf(option));
        else
            comboSelectModel.fireItemRemoved(option, options.indexOf(option));
    }

    public OPTIONElement getOption(int index)
    {
        if (index < 0 || index >= options.size())
            return null;
        return (OPTIONElement) options.get(index);
    }

    public String getSelectedValues()
    {
        if (multiple)
        {
            String value = "";
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            {
                OPTIONElement option = (OPTIONElement) i.next();
                if (option.isSelected())
                    value += option.getValue() + ",";
            }
            if (!value.equals("")) // remove last comma
                value = value.substring(0, value.length() - 1);
            return value;
        }
        else
        {
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            {
                OPTIONElement option = (OPTIONElement) i.next();
                if (option.isSelected())
                    return option.getValue();
            }
            return "";
        }
    }

    protected int[] getSelectedIndexes()
    {
        if (multiple)
        {
            ArrayList<Integer> indexes = new ArrayList<Integer>();
            int j = 0;
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext(); j++)
            {
                if (((OPTIONElement) i.next()).isSelected())
                    indexes.add(new Integer(j));
            }
            int selectedIndexes[] = new int[indexes.size()];
            for (int i = 0; i < indexes.size(); i++)
                selectedIndexes[i] = ((Integer) indexes.get(i)).intValue();
            return selectedIndexes;
        }
        else
        {
            int j = 0;
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext(); j++)
            {
                if (((OPTIONElement) i.next()).isSelected())
                    return new int[]
                    {
                        j
                    };
            }
        }
        return new int[0];
    }

    public String getValue()
    {
        return getSelectedValues();
    }

    public void select(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        if (!multiple)
            deselectAll();
        ((OPTIONElement) options.get(index)).setSelected(true);
    }

    public void select(int indexes[])
    {
        if (multiple)
            for (int i = 0; i < indexes.length; i++)
                select(indexes[i]);
        else
            select(indexes[0]);
    }

    public void deselect(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        ((OPTIONElement) options.get(index)).setSelected(false);
    }

    public void deselect(int indexes[])
    {
        for (int i = 0; i < indexes.length; i++)
            deselect(indexes[i]);
    }

    public void deselectAll()
    {
        for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            ((OPTIONElement) i.next()).setSelected(false);
    }

    // This updates the view
    public void updateSelectionView(OPTIONElement option)
    {
        int index = options.indexOf(option);
        if (!multiple)
            comboSelectModel.fireItemChanged();
        else
            listSelectSelectionModel.fireValueChanged(index);
    }

    // This updates the view
    public void updateLabelView(OPTIONElement option)
    {
        int index = options.indexOf(option);
        if (!multiple)
            comboSelectModel.fireItemChanged();
        else
            listSelectModel.fireItemChanged(index);
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
        for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            node.add(((OPTIONElement) i.next()).getTreeBranch());
        return node;
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        select.setEnabled(!b);
        list.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.select.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.select.label"), label
            },
            {
                ApplicationUtilities.getResourceString("html.select.multiple"),
                new Boolean(multiple)
            },
            {
                ApplicationUtilities.getResourceString("html.select.size"),
                size != -1 ? new Integer(size) : null
            },
            {
                ApplicationUtilities.getResourceString("html.select.options"),
                new Integer(options.size())
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
        StringBuffer param = new StringBuffer();
        if (multiple)
        {
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            {
                OPTIONElement option = (OPTIONElement) i.next();
                if (option.isSelected())
                    param.append(name).append("=").append(encode(option.getValue())).append("&");
            }
            String paramText = param.toString();
            if (paramText.length() > 0)
                paramText = paramText.substring(0, paramText.length() - 1);
            return paramText;
        }
        else
        {
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            {
                OPTIONElement option = (OPTIONElement) i.next();
                if (option.isSelected())
                    return name + "=" + encode(option.getValue());
            }
            return "";
        }
    }

    private String encode(String s)
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
        return component;
    }

    public void reset()
    {
        for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            ((OPTIONElement) i.next()).reset();
    }

    public boolean canSubmit()
    {
        return super.canSubmit() && getSelectedIndexes().length > 0;
    }

    protected class ComboSelectRenderer extends javax.swing.plaf.basic.BasicComboBoxRenderer
    {
        private static final long serialVersionUID = 1L;

        public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus)
        {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value != null)
                setText(((OPTIONElement) value).getLabel());
            return this;
        }
    }

    protected class ComboSelectModel extends AbstractListModel implements ComboBoxModel
    {
        private static final long serialVersionUID = 1L;

        protected Object selectedItem;

        public void setSelectedItem(Object anItem)
        {
            selectedItem = anItem;
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
                ((OPTIONElement) i.next()).setSelected(false);
            if (anItem != null)
                ((OPTIONElement) anItem).setSelected(true);
            fireContentsChanged(this, -1, -1);
        }

        public Object getSelectedItem()
        {
            return selectedItem;
        }

        public int getSize()
        {
            return options.size();
        }

        public Object getElementAt(int index)
        {
            if (index >= 0 && index < options.size())
                return options.get(index);
            return null;
        }

        public void fireItemAdded(OPTIONElement option)
        {
            if (option.isSelected())
                setSelectedItem(option);
            if (selectedItem == null)
                setSelectedItem(options.get(0));
            fireIntervalAdded(this, options.size() - 1, options.size() - 1);
        }

        public void fireItemRemoved(OPTIONElement option, int index)
        {
            if (option == selectedItem && options.size() > 0) // select the previous/next
            {
                if (index == options.size())
                    setSelectedItem(options.get(index - 1));
                else
                    setSelectedItem(options.get(index));
            }
            fireIntervalRemoved(this, index, index);
        }

        public void fireItemChanged()
        {
            selectedItem = null;
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            {
                OPTIONElement option = (OPTIONElement) i.next();
                if (option.isSelected())
                {
                    selectedItem = option;
                    break;
                }
            }
            fireContentsChanged(this, -1, -1);
        }
    }

    protected class ListSelectModel extends AbstractListModel
    {
        private static final long serialVersionUID = 1L;

        public int getSize()
        {
            return options.size();
        }

        public Object getElementAt(int index)
        {
            return ((OPTIONElement) options.get(index)).getLabel();
        }

        public void fireItemChanged(int index)
        {
            fireContentsChanged(this, index, index);
        }

        public void fireItemAdded(int index)
        {
            fireIntervalAdded(this, index, index);
        }

        public void fireItemRemoved(int index)
        {
            fireIntervalRemoved(this, index, index);
        }
    }

    protected class ListSelectSelectionModel implements ListSelectionModel
    {
        protected EventListenerList listenerList = new EventListenerList();
        protected int selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
        protected int anchorIndex = -1;
        protected int leadIndex = -1;
        protected boolean isAdjusting = false;

        public void addListSelectionListener(ListSelectionListener l)
        {
            listenerList.add(ListSelectionListener.class, l);
        }

        public void removeListSelectionListener(ListSelectionListener l)
        {
            listenerList.remove(ListSelectionListener.class, l);
        }

        public void fireValueChanged(int index)
        {
            fireValueChanged(index, index);
        }

        protected void fireValueChanged(int firstIndex, int lastIndex)
        {
            Object[] listeners = listenerList.getListenerList();
            ListSelectionEvent e = null;
            for (int i = listeners.length - 2; i >= 0; i -= 2)
            {
                if (listeners[i] == ListSelectionListener.class)
                {
                    if (e == null)
                        e = new ListSelectionEvent(this, firstIndex, lastIndex, isAdjusting);
                    ((ListSelectionListener) listeners[i + 1]).valueChanged(e);
                }
            }
        }

        public int getSelectionMode()
        {
            return selectionMode;
        }

        public void setSelectionMode(int selectionMode)
        {
            switch (selectionMode)
            {
            case SINGLE_SELECTION:
            case SINGLE_INTERVAL_SELECTION:
            case MULTIPLE_INTERVAL_SELECTION:
                this.selectionMode = selectionMode;
                break;
            default:
                throw new IllegalArgumentException("Invalid selectionMode");
            }
        }

        public int getAnchorSelectionIndex()
        {
            return anchorIndex;
        }

        public void setAnchorSelectionIndex(int anchorIndex)
        {
            this.anchorIndex = anchorIndex;
        }

        public int getLeadSelectionIndex()
        {
            return leadIndex;
        }

        public void setLeadSelectionIndex(int leadIndex)
        {
            this.leadIndex = leadIndex;
        }

        public int getMaxSelectionIndex()
        {
            int max = -1;
            for (int i = 0; i < options.size(); i++)
                if (((OPTIONElement) options.get(i)).isSelected())
                    max = i;
            return max;
        }

        public int getMinSelectionIndex()
        {
            for (int i = 0; i < options.size(); i++)
                if (((OPTIONElement) options.get(i)).isSelected())
                    return i;
            return -1;
        }

        public void setValueIsAdjusting(boolean valueIsAdjusting)
        {
            isAdjusting = valueIsAdjusting;
        }

        public boolean getValueIsAdjusting()
        {
            return isAdjusting;
        }

        public boolean isSelectedIndex(int index)
        {
            return ((index < 0) || (index >= options.size())) ? false : ((OPTIONElement) options
                .get(index)).isSelected();
        }

        public boolean isSelectionEmpty()
        {
            return getSelectedIndexes().length == 0;
        }

        public void setSelectionInterval(int index0, int index1)
        {
            if (index0 == -1 || index1 == -1)
                return;
            if (selectionMode == SINGLE_SELECTION)
                index0 = index1;
            anchorIndex = index0;
            leadIndex = index1;

            for (int i = 0; i < options.size(); i++)
            {
                OPTIONElement option = (OPTIONElement) options.get(i);
                if (i >= index0 && i <= index1)
                    option.setSelected(true);
                else
                    option.setSelected(false);
            }
            fireValueChanged(index0, index1);
        }

        public void addSelectionInterval(int index0, int index1)
        {
            if (index0 == -1 || index1 == -1)
                return;
            if (selectionMode != MULTIPLE_INTERVAL_SELECTION)
            {
                setSelectionInterval(index0, index1);
                return;
            }
            anchorIndex = index0;
            leadIndex = index1;
            for (int i = 0; i < options.size(); i++)
            {
                OPTIONElement option = (OPTIONElement) options.get(i);
                if (i >= index0 && i <= index1)
                    option.setSelected(true);
            }
            fireValueChanged(index0, index1);
        }

        public void clearSelection()
        {
            removeSelectionInterval(0, options.size());
        }

        public void removeSelectionInterval(int index0, int index1)
        {
            if (index0 == -1 || index1 == -1)
                return;
            anchorIndex = index0;
            leadIndex = index1;
            for (int i = 0; i < options.size(); i++)
            {
                OPTIONElement option = (OPTIONElement) options.get(i);
                if (i >= index0 && i <= index1)
                    option.setSelected(false);
            }
            fireValueChanged(index0, index1);
        }

        public void insertIndexInterval(int index, int length, boolean before)
        {
        }

        public void removeIndexInterval(int index0, int index1)
        {
        }
    }
}