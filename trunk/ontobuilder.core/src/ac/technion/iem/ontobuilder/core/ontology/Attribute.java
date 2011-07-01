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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.util.properties.PropertiesHandler;

import com.modica.application.PropertiesCellEditor;
import com.modica.application.PropertiesTableModel;
import com.modica.gui.MultilineLabel;
import com.modica.hypertree.NodeHyperTree;
import com.modica.ontology.operator.StringOperator;

/**
 * <p>Title: Attribute</p>
 * Extends {@link OntologyObject}
 */
public class Attribute extends OntologyObject
{
    private static final long serialVersionUID = 1L;

    protected Object value;
    protected Domain domain;
    protected Term term;
    protected OntologyClass ontologyClass;

    /**
     * Constructs a default Attribute
     */
    public Attribute()
    {
        super();
        domain = new Domain();
        domain.setAttribute(this);
    }

    /**
     * Constructs a Attribute
     * 
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    public Attribute(String name, Object value)
    {
        this();
        this.name = name;
        this.value = value;
    }

    /**
     * Constructs a Attribute
     * 
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @param domainName the domain name of the attribute
     */
    public Attribute(String name, Object value, String domainName)
    {
        this(name, value);
        domain.setName(domainName);
    }

    /**
     * Constructs a Attribute
     * 
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @param domain the {@link Domain} of the attribute
     */
    public Attribute(String name, Object value, Domain domain)
    {
        this(name, value);
        this.domain = domain;
    }

    /**
     * Set the name of the attribute
     * 
     * @param name the name
     */
    public void setName(String name)
    {
        super.setName(name);
        if (ontologyClass != null && ontologyClass.getOntology() != null)
            ontologyClass.getOntology().fireObjectChangedEvent(this);
    }

    /**
     * Get the value
     * 
     * @return the value
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Set the value of the attribute
     * 
     * @param value the value
     */
    public void setValue(Object value)
    {
        this.value = value;
        if (ontologyClass != null && ontologyClass.getOntology() != null)
            ontologyClass.getOntology().fireObjectChangedEvent(this);
    }

    /**
     * Get the term
     * 
     * @return the {@link Term}
     */
    public Term getTerm()
    {
        return term;
    }

    /**
     * Set the term
     * 
     * @param term the {@link Term}
     */
    public void setTerm(Term term)
    {
        this.term = term;
    }

    /**
     * Get the ontology class
     * 
     * @return the {@link OntologyClass}
     */
    public OntologyClass getOntologyClass()
    {
        return ontologyClass;
    }

    /**
     * Set the ontology class
     * 
     * @param ontologyClass the {@link OntologyClass}
     */
    public void setOntologyClass(OntologyClass ontologyClass)
    {
        this.ontologyClass = ontologyClass;
    }

    /**
     * Set the domain
     * 
     * @param domain the {@link Domain}
     */
    public void setDomain(Domain domain)
    {
        this.domain = domain;
        domain.setAttribute(this);
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
     * Compare two attributes
     * 
     * @param o1 the first attribute
     * @param o2 the second attribute
     * @return 0 if the attributes are equal
     */
    public int compare(Object o1, Object o2)
    {
        return ((Attribute) o1).name.compareTo(((Attribute) o2).name);
    }

    /**
     * Checks if an attribute is equal to another
     * 
     * @param o the attibute to compare
     */
    public boolean equals(Object o)
    {
        if (o instanceof Attribute)
            return name.equals(((Attribute) o).name) && o.getClass() == getClass() &&
                id == ((Attribute) o).id;
        return false;
    }

    public JTable getProperties()
    {
        String columnNames[] =
        {
            PropertiesHandler.getResourceString("properties.attribute"),
            PropertiesHandler.getResourceString("properties.value")
        };
        Object data[][] = new Object[4][2];
        data[0][0] = PropertiesHandler.getResourceString("ontology.attribute.name");
        data[0][1] = name;
        data[1][0] = PropertiesHandler.getResourceString("ontology.attribute.value");
        data[1][1] = (term != null ? term.getAttributeValue(name) : (ontologyClass != null ? ontologyClass
            .getAttributeValue(name) : value));
        data[2][0] = PropertiesHandler.getResourceString("ontology.domain");
        data[2][1] = domain.getName();
        if (term != null)
        {
            data[3][0] = PropertiesHandler.getResourceString("ontology.term");
            data[3][1] = term.getName();
        }
        else
        {
            data[3][0] = PropertiesHandler.getResourceString("ontology.class");
            data[3][1] = ontologyClass.getName();
        }
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 4, data))
        {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int col)
            {
                return col == 1 && (row == 0 || row == 1);
            }
        };
        TableColumn valueColumn = properties.getColumn(columnNames[1]);
        valueColumn.setCellEditor(new PropertiesCellEditor());
        properties.getModel().addTableModelListener(new TableModelListener()
        {
            public void tableChanged(TableModelEvent e)
            {
                int row = e.getFirstRow();
                int column = e.getColumn();
                Object data = ((TableModel) e.getSource()).getValueAt(row, column);
                String label = (String) data;
                switch (row)
                // name
                {
                case 0:
                    if (!name.equals(label))
                        setName(label);
                    break;
                case 1:
                    if (value == null || (value != null && !value.equals(label)))
                        setValue(label);
                    break;
                }
            }
        });
        return properties;
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
        root.add(domain.getTreeBranch());
        return root;
    }

    public NodeHyperTree getHyperTreeNode()
    {
        NodeHyperTree root = new NodeHyperTree(this, NodeHyperTree.PROPERTY);
        root.add(domain.getHyperTreeNode());
        return root;
    }

    /**
     * Clone the attribute
     * 
     * @return the cloned attribute
     */
    public Object clone()
    {
        Attribute a = new Attribute(new String(name), value, (Domain) domain.clone());
        a.term = term;
        a.ontologyClass = ontologyClass;
        return a;
    }

    public Attribute applyStringOperator(StringOperator operator)
    {
        Attribute a = new Attribute(operator.transformString(name),
            ((value instanceof String) ? operator.transformString((String) value) : value),
            domain.applyStringOperator(operator));
        a.term = term;
        a.ontologyClass = ontologyClass;
        return a;
    }

    /**
     * Get the XML representation of an attribute
     * 
     * @return an XML {@link Element}
     */
    public Element getXMLRepresentation()
    {
        Element attributeElement = new Element("attribute");
        attributeElement.setAttribute(new org.jdom.Attribute("name", name));
        if (value != null)
            attributeElement.setAttribute(new org.jdom.Attribute("value", value.toString()));
        attributeElement.addContent(domain.getXMLRepresentation());
        return attributeElement;
    }

    /**
     * Create an attribute from an XML element
     * 
     * @param attributeElement the XML {@link Element}
     * @param model the {@link Ontology}
     * @return an {@link Attribute}
     */
    public static Attribute getAttributeFromXML(Element attributeElement, Ontology model)
    {
        Attribute attribute = new Attribute(attributeElement.getAttributeValue("name"),
            attributeElement.getAttributeValue("value"));
        attribute.setDomain(Domain.getDomainFromXML(attributeElement.getChild("domain"), model));
        return attribute;
    }

    protected static Attribute a;

    /**
     * Create an attribute dialog
     * 
     * @return an {@link Attribute}
     */
    public static Attribute createAttributeDialog()
    {
        final com.modica.gui.TextField txtAttributeName = new com.modica.gui.TextField(15);
        final com.modica.gui.TextField txtAttributeValue = new com.modica.gui.TextField(15);
        final com.modica.gui.ComboBox cmbAttributeDomain = new com.modica.gui.ComboBox(
            Domain.getPredefinedDomains());
        cmbAttributeDomain.setEditable(true);
        cmbAttributeDomain.setSelectedIndex(-1);
        cmbAttributeDomain.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer()
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

        final JDialog dialog = new JDialog((JFrame) null,
            PropertiesHandler.getResourceString("ontology.attribute.dialog.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(PropertiesHandler
            .getIntProperty("ontology.attribute.dialog.width"), PropertiesHandler
            .getIntProperty("ontology.attribute.dialog.height")));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(PropertiesHandler
            .getResourceString("ontology.attribute.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(txtAttributeName.getText().trim().length() > 0);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                a = new Attribute(txtAttributeName.getText(), txtAttributeValue.getText());
                String domain = cmbAttributeDomain.getText();
                if (domain != null && domain.trim().length() > 0)
                    a.domain.setName(domain);
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(PropertiesHandler
            .getResourceString("ontology.attribute.dialog.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                a = null;
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(PropertiesHandler.getResourceString("ontology.attribute"),
                PropertiesHandler.getImage("attribute.gif"), SwingConstants.LEFT);
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
                    .getResourceString("ontology.attribute.dialog.explanation")), gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(
                PropertiesHandler.getResourceString("ontology.attribute.name") + ":");
            name.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont().getSize()));
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtAttributeName, gbcl);
            txtAttributeName.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtAttributeName.getText().trim().equals(""))
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
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel value = new JLabel(
                PropertiesHandler.getResourceString("ontology.attribute.value") + ":");
            center.add(value, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtAttributeValue, gbcl);
        }

        {// Domain
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 4;
            gbcl.insets = new Insets(0, 0, 0, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel domain = new JLabel(PropertiesHandler.getResourceString("ontology.domain") +
                ":");
            center.add(domain, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(cmbAttributeDomain, gbcl);
        }

        {// Separator
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 5;
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
                        txtAttributeName.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                a = null;
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);// show();
        return a;
    }
}