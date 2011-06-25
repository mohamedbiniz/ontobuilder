package ac.technion.iem.ontobuilder.core.ontology;

import java.awt.BorderLayout;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom.Element;

import com.modica.application.ApplicationUtilities;
import com.modica.application.PropertiesTableModel;
import com.modica.gui.MultilineLabel;
import com.modica.hypertree.NodeHyperTree;
import com.modica.ontology.operator.StringOperator;

/**
 * <p>Title: Relationship</p>
 * Extends {@link OntologyObject}
 */
public class Relationship extends OntologyObject
{
    private static final long serialVersionUID = 1L;

    protected Term source;
    protected Term target;

    /**
     * Constructs a default Relationship
     */
    public Relationship()
    {
        super();
    }

    /**
     * Constructs a Relationship
     * 
     * @param source the source {@link Term}
     * @param name the name
     * @param target the target {@link Term}
     */
    public Relationship(Term source, String name, Term target)
    {
        this();
        this.source = source;
        this.target = target;
        this.name = name;
    }

    /**
     * Set the name
     * 
     * @param the name
     */
    public void setName(String name)
    {
        super.setName(name);
        if (source != null && source.getOntology() != null)
            source.getOntology().fireObjectChangedEvent(this);
    }

    /**
     * Get the source term
     * 
     * @return the source
     */
    public Term getSource()
    {
        return source;
    }

    /**
     * Set the source term
     * 
     * @param the source {@link Term}
     */
    public void setSource(Term source)
    {
        this.source = source;
    }

    /**
     * Get the target term
     * 
     * @return the target {@link Term}
     */
    public Term getTarget()
    {
        return target;
    }

    /**
     * Set the target term
     * 
     * @param the target {@link Term}
     */
    public void setTarget(Term target)
    {
        this.target = target;
    }

    public String toString()
    {
        return source.getName() + " -- " + name + " --> " + target.getName();
    }

    public int compare(Object o1, Object o2)
    {
        return ((Relationship) o1).name.compareTo(((Relationship) o2).name);
    }

    public boolean equals(Object o)
    {
        if (o instanceof Relationship)
            return name.equals(((Relationship) o).name) &&
                source.equals(((Relationship) o).source) &&
                target.equals(((Relationship) o).target) && o.getClass() == getClass() &&
                id == ((Relationship) o).id;
        ;
        return false;
    }

    public Object clone()
    {
        return new Relationship(source, new String(name), target);
    }

    public Relationship applyStringOperator(StringOperator operator)
    {
        return new Relationship(source, operator.transformString(name), target);
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
                ApplicationUtilities.getResourceString("ontology.relationship.name"), name
            },
            {
                ApplicationUtilities.getResourceString("ontology.relationship.source"), source
            },
            {
                ApplicationUtilities.getResourceString("ontology.relationship.target"), target
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 3, data));
        return properties;
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
        DefaultMutableTreeNode sources = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("ontology.relationship.source"));
        sources.add(new DefaultMutableTreeNode(source));
        root.add(sources);
        DefaultMutableTreeNode targets = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("ontology.relationship.targets"));
        targets.add(new DefaultMutableTreeNode(target));
        root.add(targets);
        return root;
    }

    public NodeHyperTree getHyperTreeNode()
    {
        NodeHyperTree root = new NodeHyperTree(this, NodeHyperTree.RELATIONSHIP);
        NodeHyperTree sources = new NodeHyperTree(
            ApplicationUtilities.getResourceString("ontology.relationship.source"),
            NodeHyperTree.TERM);
        sources.add(new NodeHyperTree(source, NodeHyperTree.TERM));
        root.add(sources);
        NodeHyperTree targets = new NodeHyperTree(
            ApplicationUtilities.getResourceString("ontology.relationship.targets"),
            NodeHyperTree.TERM);
        targets.add(new NodeHyperTree(target, NodeHyperTree.TERM));
        root.add(targets);
        return root;
    }

    /**
     * Get the XML {@link Element} representation of the relationship
     * 
     * @return an XML element
     */
    public Element getXMLRepresentation()
    {
        Element relationshipElement = new Element("relationship");
        relationshipElement.setAttribute("name", name);
        Element sourceElement = new Element("source");
        relationshipElement.addContent(sourceElement.addContent(source.getName()));
        Element targetsElement = new Element("targets");
        relationshipElement.addContent(targetsElement.addContent(target.getName()));
        return relationshipElement;
    }

    /**
     * Get the relationship from an XML element
     * 
     * @param relationshipElement the XML {@link Element}
     * @param source the source {@link Term}
     * @param model an {@link Ontology}
     * @return a {@link Relationship}
     */
    public static Relationship getRelationshipFromXML(Element relationshipElement, Term source,
        Ontology model)
    {
        Term target = model.findTerm(relationshipElement.getChild("targets").getText());
        Relationship relationship = new Relationship(source,
            relationshipElement.getAttributeValue("name"), target);
        return relationship;
    }

    protected static Relationship r;
    protected static Term aTarget;

    public static Relationship createRelationshipDialog(final Term source, Ontology model)
    {
        final com.modica.gui.TextField txtRelationshipName = new com.modica.gui.TextField(15);

        final JDialog dialog = new JDialog((JFrame) null,
            ApplicationUtilities.getResourceString("ontology.relationship.dialog.windowTitle"),
            true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(ApplicationUtilities
            .getIntProperty("ontology.relationship.dialog.width"), ApplicationUtilities
            .getIntProperty("ontology.relationship.dialog.height")));
        dialog.setLocationRelativeTo(null);
        // dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.relationship.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                r = new Relationship(source, txtRelationshipName.getText(), aTarget);
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.relationship.dialog.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                r = null;
                aTarget = null;
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(
                ApplicationUtilities.getResourceString("ontology.relationship"),
                ApplicationUtilities.getImage("relationship.gif"), SwingConstants.LEFT);
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
                    .getResourceString("ontology.relationship.dialog.explanation")), gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(
                ApplicationUtilities.getResourceString("ontology.relationship.name") + ":");
            name.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont().getSize()));
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            center.add(txtRelationshipName, gbcl);
            txtRelationshipName.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtRelationshipName.getText().trim().equals("") && aTarget != null)
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Source
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel sourceLabel = new JLabel(
                ApplicationUtilities.getResourceString("ontology.relationship.source") + ":");
            sourceLabel.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont()
                .getSize()));
            center.add(sourceLabel, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(new JLabel(source.getName(), ApplicationUtilities.getImage("term.gif"),
                SwingConstants.LEFT), gbcl);
        }

        {// Target
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 4;
            gbcl.insets = new Insets(0, 0, 0, 5);
            gbcl.anchor = GridBagConstraints.NORTHEAST;
            JLabel targetLabel = new JLabel(
                ApplicationUtilities.getResourceString("ontology.relationship.target") + ":");
            targetLabel.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont()
                .getSize()));
            center.add(targetLabel, gbcl);

            gbcl.gridx = 1;
            gbcl.weighty = 1;
            gbcl.weightx = 1;
            gbcl.anchor = GridBagConstraints.NORTH;
            gbcl.fill = GridBagConstraints.BOTH;
            JTree terms = model.getTermsHierarchy();
            center.add(new JScrollPane(terms), gbcl);
            terms.addTreeSelectionListener(new TreeSelectionListener()
            {
                public void valueChanged(TreeSelectionEvent event)
                {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) event
                        .getSource()).getLastSelectedPathComponent();
                    if (node != null)
                    {
                        if (node.getUserObject() instanceof Term)
                            aTarget = (Term) node.getUserObject();
                        else
                            aTarget = null;
                        if (!txtRelationshipName.getText().trim().equals("") && aTarget != null)
                            okButton.setEnabled(true);
                        else
                            okButton.setEnabled(false);
                    }
                }
            });
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
                        txtRelationshipName.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                r = null;
                aTarget = null;
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);// show();
        return r;
    }
}