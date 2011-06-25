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
import java.util.ArrayList;
import java.util.Iterator;

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
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.operator.StringOperator;

/**
 * <p>Title: OntologyClass</p>
 * Extends {@link OntologyObject}
 */
public class OntologyClass extends OntologyObject
{
    private static final long serialVersionUID = 1L;

    protected Ontology ontology;
    protected Domain domain;
    protected ArrayList<Attribute> attributes;
    protected ArrayList<Axiom> axioms;
    protected OntologyClass superClass;
    protected ArrayList<OntologyClass> instances;

    /**
     * Constructs a default OntologyClass
     */
    public OntologyClass()
    {
        super();
        instances = new ArrayList<OntologyClass>();
        domain = new Domain();
        domain.setOntologyClass(this);
        attributes = new ArrayList<Attribute>();
        axioms = new ArrayList<Axiom>();
    }

    /**
     * Constructs a OntologyClass
     *
     * @param name the class name
     */
    public OntologyClass(String name)
    {
        this();
        this.name = name;
    }

    /**
     * Constructs a OntologyClass
     *
     * @param superClass the {@link OntologyClass}
     * @param name the class name
     */
    public OntologyClass(OntologyClass superClass, String name)
    {
        this(name);
        setSuperClass(superClass);
        if (ontology != null)
            ontology.fireClassAddedEvent(superClass, this);
    }

    /**
     * Set the superClass
     *
     * @param superClass the {@link OntologyClass}
     */
    public void setSuperClass(OntologyClass superClass)
    {
        if (superClass == null)
            return;
        superClass.instances.add(this);
        this.superClass = superClass;
        ontology = superClass.ontology;
        domain = (Domain) superClass.domain.clone();
        domain.setOntologyClass(this);
        for (Iterator<Attribute> i = superClass.attributes.iterator(); i.hasNext();)
        {
            Attribute a = (Attribute) ((Attribute) i.next()).clone();
            a.setValue(null);
            a.setOntologyClass(this);
            attributes.add(a);
        }
        for (Iterator<Axiom> i = superClass.axioms.iterator(); i.hasNext();)
        {
            Axiom a = (Axiom) ((Axiom) i.next()).clone();
            a.setOntologyClass(this);
            axioms.add(a);
        }
        ontology.fireObjectChangedEvent(this);
    }

    /**
     * Get the superClass 
     *
     * @return the {@link OntologyClass} 
     */
    public OntologyClass getSuperClass()
    {
        return superClass;
    }

    /**
     * Check is a class is an instance of the Ontology class by its name
     *
     * @param className the class name
     * @return <code>true</code> if it is an instance
     */
    public boolean isInstanceOf(String className)
    {
        OntologyClass superClassAux = superClass;
        while (superClassAux != null)
        {
            if (superClassAux.getName().equals(className))
                return true;
            else
                superClassAux = superClassAux.superClass;
        }
        return false;
    }

    /**
     * Set the name
     * 
     * @param name the name
     */
    public void setName(String name)
    {
        super.setName(name);
        if (ontology != null)
            ontology.fireObjectChangedEvent(this);
    }

    /**
     * Set the ontology
     * 
     * @param ontology the {@link Ontology}
     */
    public void setOntology(Ontology ontology)
    {
        this.ontology = ontology;
        for (Iterator<OntologyClass> i = instances.iterator(); i.hasNext();)
        {
            OntologyClass oc = (OntologyClass) i.next();
            oc.setOntology(ontology);
        }
    }

    /**
     * Get the ontology 
     *
     * @return the {@link Ontology} 
     */
    public Ontology getOntology()
    {
        return ontology;
    }

    /**
     * Set the domain
     * 
     * @param domain the {@link Domain}
     */
    public void setDomain(Domain domain)
    {
        this.domain = domain;
        domain.setOntologyClass(this);
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
     * Add an axion
     *
     * @param axiom the {@link Axiom} to add
     */
    public void addAxiom(Axiom axiom)
    {
        if (axiom == null)
            return;
        if (!axioms.contains(axiom))
        {
            axioms.add(axiom);
            axiom.setOntologyClass(this);
            for (Iterator<OntologyClass> i = instances.iterator(); i.hasNext();)
                ((OntologyClass) i.next()).addAxiom((Axiom) axiom.clone());
            if (ontology != null)
                ontology.fireAxiomAddedEvent(this, axiom);
        }
    }

    /**
     * Remove an axion
     *
     * @param axiom the {@link Axiom} to remove
     */
    public void removeAxiom(Axiom axiom)
    {
        if (axioms.contains(axiom))
        {
            axioms.remove(axiom);
            if (ontology != null)
                ontology.fireAxiomDeletedEvent(this, axiom);
        }
    }

    /**
     * Get the number of axioms
     */
    public int getAxiomsCount()
    {
        return axioms.size();
    }

    /**
     * Get an axiom according to its index
     *
     * @param index the index
     * @return the {@link Axiom}
     */
    public Axiom getAxiom(int index)
    {
        if (index < 0 || index >= axioms.size())
            return null;
        return (Axiom) axioms.get(index);
    }

    /**
     * Remove a certain sub-class 
     *
     * @param subClass the subClass {@link OntologyClass} to remove
     */
    public void removeSubClass(OntologyClass subClass)
    {
        if (instances.contains(subClass))
        {
            subClass.superClass = null;
            instances.remove(subClass);
            if (ontology != null)
                ontology.fireClassDeletedEvent(this, subClass);
        }
    }

    /**
     * Get the number of sub classes
     */
    public int getSubClassesCount()
    {
        return instances.size();
    }

    /**
     * Get a sub-class according to its index
     *
     * @param index the index
     * @return the sub-class {@link OntologyClass}
     */
    public OntologyClass getSubClass(int index)
    {
        if (index < 0 || index >= instances.size())
            return null;
        return (OntologyClass) instances.get(index);
    }

    /**
     * Add an attribute
     *
     * @param attribute the {@link Attribute} to add
     */
    public void addAttribute(Attribute attribute)
    {
        if (attribute == null)
            return;
        if (!attributes.contains(attribute))
        {
            attributes.add(attribute);
            attribute.setOntologyClass(this);
            for (Iterator<OntologyClass> i = instances.iterator(); i.hasNext();)
            {
                Attribute a = (Attribute) attribute.clone();
                a.setOntologyClass(this);
                ((OntologyClass) i.next()).addAttribute(a);
            }
            if (ontology != null)
                ontology.fireAttributeAddedEvent(this, attribute);
        }
    }

    /**
     * Remove an attribute
     *
     * @param attribute the {@link Attribute} to remove
     */
    public void removeAttribute(Attribute attribute)
    {
        if (attributes.contains(attribute))
        {
            attribute.setOntologyClass(null);
            attributes.remove(attribute);
            if (ontology != null)
                ontology.fireAttributeDeletedEvent(this, attribute);
        }
    }

    /**
     * Get the number of attributes
     */
    public int getAttributesCount()
    {
        return attributes.size();
    }

    /**
     * Get a attribute according to its index
     *
     * @param index the index
     * @return the {@link Attribute}
     */
    public Attribute getAttribute(int index)
    {
        if (index < 0 || index >= attributes.size())
            return null;
        return (Attribute) attributes.get(index);
    }

    public void setAttributeValue(String name, Object value)
    {
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
        {
            Attribute a = (Attribute) i.next();
            if (a.getName().equals(name))
                a.setValue(value);
        }
    }

    public void setAttributeValue(String name, Object value, Domain domain)
    {
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
        {
            Attribute a = (Attribute) i.next();
            if (a.getName().equals(name))
            {
                a.setDomain(domain);
                a.setValue(value);
            }
        }
    }

    public Object getAttributeValue(String name)
    {
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
        {
            Attribute a = (Attribute) i.next();
            if (a.getName().equals(name))
            {
                if (a.getValue() != null)
                    return a.getValue();
                else if (superClass != null)
                    return superClass.getAttributeValue(name);
                else
                    return null;
            }
        }
        return null;
    }

    public int compare(Object o1, Object o2)
    {
        return ((OntologyClass) o1).name.compareTo(((OntologyClass) o2).name);
    }

    public boolean equals(Object o)
    {
        if (o instanceof OntologyClass)
            return name.equals(((OntologyClass) o).name) && o.getClass() == getClass() &&
                id == ((OntologyClass) o).id;
        return false;
    }

    public Object clone()
    {
        OntologyClass ontologyClass = new OntologyClass(new String(name));
        ontologyClass.setDomain((Domain) domain.clone());
        ontologyClass.superClass = superClass;
        for (Iterator<Axiom> i = axioms.iterator(); i.hasNext();)
            ontologyClass.addAxiom((Axiom) ((Axiom) i.next()).clone());
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
            ontologyClass.addAttribute((Attribute) ((Attribute) i.next()).clone());
        ontologyClass.ontology = ontology;
        return ontologyClass;
    }

    public OntologyClass applyStringOperator(StringOperator operator)
    {
        OntologyClass ontologyClass = new OntologyClass(operator.transformString(name));
        ontologyClass.setDomain(domain.applyStringOperator(operator));
        ontologyClass.superClass = superClass;
        for (Iterator<Axiom> i = axioms.iterator(); i.hasNext();)
            ontologyClass.addAxiom(((Axiom) i.next()).applyStringOperator(operator));
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
            ontologyClass.addAttribute(((Attribute) i.next()).applyStringOperator(operator));
        ontologyClass.ontology = ontology;
        return ontologyClass;
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
                ApplicationUtilities.getResourceString("ontology.class.name"), name
            },
            {
                ApplicationUtilities.getResourceString("ontology.class.superclass"),
                superClass != null ? superClass.getName() : null
            },
            {
                ApplicationUtilities.getResourceString("ontology.domain"), domain.getName()
            },
            {
                ApplicationUtilities.getResourceString("ontology"), ontology
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 4, data));
        return properties;
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);

        root.add(domain.getTreeBranch());

        DefaultMutableTreeNode attributesNode = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("ontology.attributes"));
        root.add(attributesNode);
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
            attributesNode.add(((Attribute) i.next()).getTreeBranch());

        DefaultMutableTreeNode axiomsNode = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("ontology.axioms"));
        root.add(axiomsNode);
        for (Iterator<Axiom> i = axioms.iterator(); i.hasNext();)
            axiomsNode.add(((Axiom) i.next()).getTreeBranch());

        DefaultMutableTreeNode subClassesNode = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("ontology.class.subclasses"));
        root.add(subClassesNode);
        for (Iterator<OntologyClass> i = instances.iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (!(o instanceof Term))
                subClassesNode.add(((OntologyClass) o).getTreeBranch());
        }
        return root;
    }

    public NodeHyperTree getHyperTreeNode(boolean showProperties)
    {
        NodeHyperTree root = new NodeHyperTree(this, NodeHyperTree.CLASS);

        if (showProperties)
        {
            root.add(domain.getHyperTreeNode());

            NodeHyperTree attributesNode = new NodeHyperTree(
                ApplicationUtilities.getResourceString("ontology.attributes"),
                NodeHyperTree.PROPERTY);
            root.add(attributesNode);
            for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
                attributesNode.add(((Attribute) i.next()).getHyperTreeNode());

            NodeHyperTree axiomsNode = new NodeHyperTree(
                ApplicationUtilities.getResourceString("ontology.axioms"), NodeHyperTree.PROPERTY);
            root.add(axiomsNode);
            for (Iterator<Axiom> i = axioms.iterator(); i.hasNext();)
                axiomsNode.add(((Axiom) i.next()).getHyperTreeNode());
        }

        NodeHyperTree subClassesNode = new NodeHyperTree(
            ApplicationUtilities.getResourceString("ontology.class.subclasses"),
            NodeHyperTree.CLASS);
        root.add(subClassesNode);
        for (Iterator<OntologyClass> i = instances.iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (!(o instanceof Term))
                subClassesNode.add(((OntologyClass) o).getHyperTreeNode(showProperties));
        }
        return root;
    }

    /**
     * Get the XML {@link Element} representation of the ontology class
     */
    public Element getXMLRepresentation()
    {
        Element classElement = new Element("class");
        classElement.setAttribute("name", name);

        // Domain
        classElement.addContent(domain.getXMLRepresentation());

        // Attributes
        Element attributesElement = new Element("attributes");
        classElement.addContent(attributesElement);
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
            attributesElement.addContent(((Attribute) i.next()).getXMLRepresentation());

        // Axioms
        Element axiomsElement = new Element("axioms");
        classElement.addContent(axiomsElement);
        for (Iterator<Axiom> i = axioms.iterator(); i.hasNext();)
            axiomsElement.addContent(((Axiom) i.next()).getXMLRepresentation());

        // Subclasses
        Element subclassesElement = new Element("subclasses");
        classElement.addContent(subclassesElement);
        for (Iterator<OntologyClass> i = instances.iterator(); i.hasNext();)
        {
            OntologyClass instance = (OntologyClass) i.next();
            if (!(instance instanceof Term))
                subclassesElement.addContent(instance.getXMLRepresentation());
        }

        return classElement;
    }

    /**
     * Get the classElement from an XML element
     *
     * @param domainElement the XML {@link Element}
     * @param model the {@link Ontology}
     * @return the {@link OntologyClass}
     */
    public static OntologyClass getClassFromXML(Element classElement, Ontology model)
    {
        OntologyClass ontologyClass = new OntologyClass(classElement.getAttributeValue("name"));
        ontologyClass.setDomain(Domain.getDomainFromXML(classElement.getChild("domain"), model));
        ontologyClass.ontology = model;

        java.util.List<?> attributeElements = classElement.getChild("attributes").getChildren();
        for (Iterator<?> i = attributeElements.iterator(); i.hasNext();)
            ontologyClass.addAttribute(Attribute.getAttributeFromXML((Element) i.next(), model));

        java.util.List<?> axiomElements = classElement.getChild("axioms").getChildren();
        for (Iterator<?> i = axiomElements.iterator(); i.hasNext();)
            ontologyClass.addAxiom(Axiom.getAxiomFromXML((Element) i.next(), model));

        java.util.List<?> subclassesElements = classElement.getChild("subclasses").getChildren();
        for (Iterator<?> i = subclassesElements.iterator(); i.hasNext();)
        {
            OntologyClass subClass = OntologyClass.getClassFromXML((Element) i.next(), model);
            subClass.superClass = ontologyClass;
            subClass.ontology = model;
            ontologyClass.instances.add(subClass);
        }

        return ontologyClass;
    }

    protected static OntologyClass c;

    public static OntologyClass createClassDialog(final OntologyClass parent)
    {
        final com.modica.gui.TextField txtClassName = new com.modica.gui.TextField(15);
        final com.modica.gui.ComboBox cmbClassDomain = new com.modica.gui.ComboBox(
            Domain.getPredefinedDomains());
        cmbClassDomain.setEditable(true);
        cmbClassDomain.setSelectedIndex(-1);
        cmbClassDomain.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer()
        {
            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setIcon(ApplicationUtilities.getImage("domain.gif"));
                return this;
            }
        });

        final JDialog dialog = new JDialog((JFrame) null,
            ApplicationUtilities.getResourceString("ontology.class.dialog.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(ApplicationUtilities
            .getIntProperty("ontology.class.dialog.width"), ApplicationUtilities
            .getIntProperty("ontology.class.dialog.height")));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.class.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(txtClassName.getText().trim().length() > 0);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (parent != null)
                    c = new OntologyClass(parent, txtClassName.getText());
                else
                    c = new OntologyClass(txtClassName.getText());
                String domain = cmbClassDomain.getText();
                if (domain != null && domain.trim().length() > 0)
                    c.domain.setName(domain);
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.class.dialog.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                c = null;
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(ApplicationUtilities.getResourceString("ontology.class"),
                ApplicationUtilities.getImage("class.gif"), SwingConstants.LEFT);
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
                new MultilineLabel(ApplicationUtilities
                    .getResourceString("ontology.class.dialog.explanation")), gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(ApplicationUtilities.getResourceString("ontology.class.name") +
                ":");
            name.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont().getSize()));
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtClassName, gbcl);
            txtClassName.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtClassName.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Domain
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel domain = new JLabel(ApplicationUtilities.getResourceString("ontology.domain") +
                ":");
            center.add(domain, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(cmbClassDomain, gbcl);
        }

        {// Separator
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 4;
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
                        txtClassName.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                c = null;
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);// show();
        return c;
    }
}