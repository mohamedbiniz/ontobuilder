package ac.technion.iem.ontobuilder.core.ontology;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.util.properties.PropertiesHandler;

import com.modica.application.PropertiesTableModel;
import com.modica.gui.MultilineLabel;
import com.modica.ontology.operator.StringOperator;

/**
 * <p>Title: DomainEntry</p>
 * Extends {@link OntologyObject}
 */
public class DomainEntry extends OntologyObject
{
    private static final long serialVersionUID = 1L;

    Object entry;
    Domain domain;

    /**
     * Constructs a default DomainEntry
     */
    public DomainEntry()
    {
        super();
    }

    /**
     * Constructs a DomainEntry
     *
     * @param domain the {@link Domain}
     */
    public DomainEntry(Domain domain)
    {
        this();
        this.domain = domain;
    }

    /**
     * Constructs a DomainEntry
     *
     * @param domain the {@link Domain}
     * @param entry the entry
     */
    public DomainEntry(Domain domain, Object entry)
    {
        this(domain);
        this.entry = entry;
    }

    /**
     * Constructs a DomainEntry
     *
     * @param entry the entry
     */
    public DomainEntry(Object entry)
    {
        this();
        this.entry = entry;
    }

    /**
     * Get the entry
     *
     * @return the entry
     */
    public Object getEntry()
    {
        return entry;
    }

    /**
     * Set the entry
     *
     * @param entry the entry
     */
    public void setEntry(Object entry)
    {
        this.entry = entry;
    }

    /**
     * Get the domain
     *
     * @return the {@link Domain}
     */
    public Domain getDomain()
    {
        return domain;
    }

    /**
     * Set the domain
     *
     * @param domain the {@link Domain}
     */
    public void setDomain(Domain domain)
    {
        this.domain = domain;
    }

    public String toString()
    {
        return entry.toString();
    }

    public int compare(Object o1, Object o2)
    {
        return ((DomainEntry) o1).name.compareTo(((DomainEntry) o2).name);
    }

    public boolean equals(Object o)
    {
        return (o instanceof DomainEntry && o.getClass() == getClass() && id == ((DomainEntry) o).id) ||
            (o instanceof Term && entry instanceof Term && entry.equals(o));
    }

    public JTable getProperties()
    {
        if (entry instanceof Term)
            return ((Term) entry).getProperties();
        String columnNames[] =
        {
            PropertiesHandler.getResourceString("properties.attribute"),
            PropertiesHandler.getResourceString("properties.value")
        };
        Object data[][] =
        {
            {
                PropertiesHandler.getResourceString("ontology.domain.entry"), entry
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 1, data));
        return properties;
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
        if (entry instanceof Term)
        {
            DefaultMutableTreeNode termNode = ((Term) entry).getTreeBranch();
            root.add((DefaultMutableTreeNode) termNode.getChildAt(1)); // The attributes node is the
                                                                       // second child;
        }
        return root;
    }

    public NodeHyperTree getHyperTreeNode()
    {
        NodeHyperTree root = new NodeHyperTree(this, NodeHyperTree.CLASS);
        if (entry instanceof Term)
        {
            NodeHyperTree termNode = ((Term) entry).getHyperTreeNode();
            root.add(termNode.getChild(PropertiesHandler
                .getResourceString("ontology.attributes")));
        }
        return root;
    }

    public Object clone()
    {
        if (entry instanceof Term)
            return new DomainEntry(domain, ((Term) entry).clone());
        else
            return new DomainEntry(domain, entry);
    }

    public DomainEntry applyStringOperator(StringOperator operator)
    {
        if (entry instanceof Term)
            return new DomainEntry(domain, ((Term) entry).applyStringOperator(operator));
        else if (entry instanceof String)
            return new DomainEntry(domain, operator.transformString((String) entry));
        else
            return new DomainEntry(domain, entry);
    }

    /**
     * Get the XML {@link Element} representation of the domain
     */
    public Element getXMLRepresentation()
    {
        Element entryElement = new Element("entry");
        if (entry instanceof Term)
            entryElement.addContent(((Term) entry).getXMLRepresentation());
        else
            entryElement.addContent(entry.toString());
        return entryElement;
    }

    /**
     * Get the domain from an XML element
     * 
     * @param entryElement the XML {@link Element}
     * @param model the {@link Ontology}
     * @return the {@link DomainEntry}
     */
    public static DomainEntry getDomainEntryFromXML(Element entryElement, Ontology model)
    {
        Element termElement = entryElement.getChild("term");
        if (termElement != null) // the entry is a term
            return new DomainEntry(Term.getTermFromXML(termElement, model));
        else
            return new DomainEntry(entryElement.getText());
    }

    protected static DomainEntry e;

    public static DomainEntry createEntryDialog(final Ontology model)
    {
        final com.modica.gui.TextField txtEntry = new com.modica.gui.TextField(15);
        txtEntry.setEnabled(false);
        final com.modica.gui.TextField txtTermName = new com.modica.gui.TextField(15);
        final com.modica.gui.TextField txtTermValue = new com.modica.gui.TextField(15);
        final com.modica.gui.ComboBox cmbTermDomain = new com.modica.gui.ComboBox(
            Domain.getPredefinedDomains());
        cmbTermDomain.setEnabled(false);
        cmbTermDomain.setEditable(true);
        cmbTermDomain.setSelectedIndex(-1);
        cmbTermDomain.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer()
        {
            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setIcon(PropertiesHandler.getImage("domain.gif"));
                return this;
            }
        });

        Vector<Object> classes = (Vector<Object>) model.getClasses();
        classes.add(0, "               ");
        final com.modica.gui.ComboBox cmbTermClass = new com.modica.gui.ComboBox(classes);
        cmbTermClass.setEnabled(false);
        cmbTermClass.setSelectedIndex(0);
        cmbTermClass.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer()
        {
            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof OntologyClass)
                    setIcon(PropertiesHandler.getImage("class.gif"));
                return this;
            }
        });

        final JDialog dialog = new JDialog((JFrame) null,
            PropertiesHandler.getResourceString("ontology.domainEntry.dialog.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(PropertiesHandler
            .getIntProperty("ontology.domainEntry.dialog.width"), PropertiesHandler
            .getIntProperty("ontology.domainEntry.dialog.height")));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        final JRadioButton entryRadio = new JRadioButton(
            PropertiesHandler.getResourceString("ontology.domainEntry.dialog.radio.entry"));
        final JRadioButton termRadio = new JRadioButton(
            PropertiesHandler.getResourceString("ontology.domainEntry.dialog.radio.term"));

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(PropertiesHandler
            .getResourceString("ontology.domainEntry.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(txtTermName.getText().trim().length() > 0);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                if (termRadio.isSelected())
                {
                    Term t;
                    if (cmbTermClass.getSelectedIndex() > 0)
                        t = new Term((OntologyClass) cmbTermClass.getSelectedItem());
                    else
                        t = new Term();
                    t.name = txtTermName.getText();
                    t.value = txtTermValue.getText();
                    t.ontology = model;
                    String domain = cmbTermDomain.getText();
                    if (domain != null && domain.trim().length() > 0)
                        t.domain.setName(domain);
                    e = new DomainEntry(t);
                }
                else
                {
                    e = new DomainEntry(txtEntry.getText());
                }
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(PropertiesHandler
            .getResourceString("ontology.domainEntry.dialog.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                e = null;
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        ButtonGroup group = new ButtonGroup();
        termRadio.setSelected(true);
        group.add(entryRadio);
        entryRadio.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                boolean selected = e.getStateChange() == ItemEvent.SELECTED;
                txtEntry.setEnabled(selected);
                txtTermName.setEnabled(!selected);
                txtTermValue.setEnabled(!selected);
                cmbTermDomain.setEnabled(!selected);
                cmbTermClass.setEnabled(!selected);
                okButton.setEnabled(selected && txtEntry.getText().trim().length() > 0);
                if (selected)
                    txtEntry.requestFocus();
            }
        });
        group.add(termRadio);
        termRadio.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                boolean selected = e.getStateChange() == ItemEvent.SELECTED;
                txtEntry.setEnabled(!selected);
                txtTermName.setEnabled(selected);
                txtTermValue.setEnabled(selected);
                cmbTermDomain.setEnabled(selected);
                cmbTermClass.setEnabled(selected);
                okButton.setEnabled(selected && txtTermName.getText().trim().length() > 0);
                if (selected)
                    txtTermName.requestFocus();
            }
        });

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(
                PropertiesHandler.getResourceString("ontology.domain.entry"),
                PropertiesHandler.getImage("domainentry.gif"), SwingConstants.LEFT);
            title.setFont(new Font(dialog.getFont().getFontName(), Font.BOLD, dialog.getFont()
                .getSize() + 6));
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.weightx = 1;
            gbcl.gridwidth = 2;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.NORTHWEST;
            center.add(title, gbcl);
        }

        {// Explanation
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 1;
            gbcl.gridwidth = 2;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 20, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(
                new MultilineLabel(PropertiesHandler
                    .getResourceString("ontology.domainEntry.dialog.explanation")), gbcl);
        }

        {// Radio
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.gridwidth = 2;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 5, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(entryRadio, gbcl);
        }

        {// Entry
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel entry = new JLabel(
                PropertiesHandler.getResourceString("ontology.domain.entry") + ":");
            entry.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont()
                .getSize()));
            center.add(entry, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtEntry, gbcl);
            txtEntry.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtEntry.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Radio
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 4;
            gbcl.gridwidth = 2;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 5, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(termRadio, gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 5;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(PropertiesHandler.getResourceString("ontology.term.name") +
                ":");
            name.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont().getSize()));
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtTermName, gbcl);
            txtTermName.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtTermName.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Value
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 6;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel value = new JLabel(
                PropertiesHandler.getResourceString("ontology.term.value") + ":");
            center.add(value, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtTermValue, gbcl);
        }

        /*
         * {// Domain GridBagConstraints gbcl=new GridBagConstraints(); gbcl.gridy=7;
         * gbcl.insets=new Insets(0,0,5,5); gbcl.anchor=GridBagConstraints.EAST; JLabel domain=new
         * JLabel(PropertiesHandler.getResourceString("ontology.domain") + ":");
         * center.add(domain,gbcl); gbcl.gridx=1; gbcl.anchor=GridBagConstraints.WEST;
         * center.add(cmbTermDomain,gbcl); } {// Class GridBagConstraints gbcl=new
         * GridBagConstraints(); gbcl.gridy=8; gbcl.insets=new Insets(0,0,0,5);
         * gbcl.anchor=GridBagConstraints.EAST; JLabel clazz=new
         * JLabel(PropertiesHandler.getResourceString("ontology.class") + ":");
         * center.add(clazz,gbcl); gbcl.gridx=1; gbcl.anchor=GridBagConstraints.WEST;
         * center.add(cmbTermClass,gbcl); }
         */

        {// Separator
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 7;
            gbc.gridwidth = 2;
            gbc.gridheight = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.VERTICAL;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            center.add(new JPanel(), gbc);
        }

        dialog.addWindowListener(new WindowAdapter()
        {
            public void windowOpened(WindowEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        txtEntry.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                e = null;
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);// show();
        return e;
    }
}