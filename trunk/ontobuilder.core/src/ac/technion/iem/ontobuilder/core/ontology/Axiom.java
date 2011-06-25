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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jdom.Element;

import com.modica.application.ApplicationUtilities;
import com.modica.application.PropertiesTableModel;
import com.modica.gui.MultilineLabel;
import com.modica.ontology.operator.StringOperator;


/**
 * <p>Title: Axiom</p>
 * Extends {@link OntologyObject}
 */
public class Axiom extends OntologyObject
{
    private static final long serialVersionUID = 1L;

    protected String axiom;
    protected OntologyClass ontologyClass;

    /**
     * Constructs a default Axiom
     */
    public Axiom()
    {
        super();
    }

    /**
     * Constructs a Axiom
     * 
     * @param name the axiom name
     * @param axiom the axiom value
     */
    public Axiom(String name, String axiom)
    {
        this();
        this.name = name;
        this.axiom = axiom;
    }

    /**
     * Set the name
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
     * Get the axiom value
     * 
     * @return the axiom value
     */
    public String getAxiom()
    {
        return axiom;
    }

    /**
     * Set the axiom value
     * 
     * @param axiom the axiom value
     */
    public void setAxiom(String axiom)
    {
        this.axiom = axiom;
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
     * Compare two axioms
     * 
     * @param o1 the first axiom
     * @param o2 the second axiom
     * @return 0 if the axioms are equal
     */
    public int compare(Object o1, Object o2)
    {
        return ((Axiom) o1).axiom.compareTo(((Axiom) o2).axiom);
    }

    /**
     * Checks if an axiom is equal to another
     * 
     * @param o the axiom to compare
     */
    public boolean equals(Object o)
    {
        if (o instanceof Axiom)
            return axiom.equals(((Axiom) o).axiom) && o.getClass() == getClass() &&
                id == ((Axiom) o).id;
        return false;
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
                ApplicationUtilities.getResourceString("ontology.axiom.name"), name
            },
            {
                ApplicationUtilities.getResourceString("ontology.axiom.axiom"), axiom
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 2, data));
        return properties;
    }

    /**
     * Clone the axiom
     * 
     * @return the cloned axiom
     */
    public Object clone()
    {
        return new Axiom(new String(name), new String(axiom));
    }

    public Axiom applyStringOperator(StringOperator operator)
    {
        return new Axiom(operator.transformString(name), operator.transformString(axiom));
    }

    /**
     * Get the XML representation of an axiom
     * 
     * @return an XML element
     */
    public Element getXMLRepresentation()
    {
        Element axiomElement = new Element("axiom");
        axiomElement.setAttribute(new org.jdom.Attribute("name", name));
        axiomElement.addContent(axiom);
        return axiomElement;
    }

    /**
     * Create an axiom from an XML element
     * 
     * @param axiomElement the XML {@link Element}
     * @param model the {@link Ontology}
     * @return an {@link Axiom}
     */
    public static Axiom getAxiomFromXML(Element axiomElement, Ontology model)
    {
        return new Axiom(axiomElement.getAttributeValue("name"), axiomElement.getText());
    }

    protected static Axiom a;

    public static Axiom createAxiomDialog()
    {
        final com.modica.gui.TextField txtAxiomName = new com.modica.gui.TextField(15);
        final com.modica.gui.TextArea txtAxiom = new com.modica.gui.TextArea(1, 4);

        final JDialog dialog = new JDialog((JFrame) null,
            ApplicationUtilities.getResourceString("ontology.axiom.dialog.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(ApplicationUtilities
            .getIntProperty("ontology.axiom.dialog.width"), ApplicationUtilities
            .getIntProperty("ontology.axiom.dialog.height")));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.axiom.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                a = new Axiom(txtAxiomName.getText(), txtAxiom.getText());
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.axiom.dialog.button.cancel")));
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
            JLabel title = new JLabel(ApplicationUtilities.getResourceString("ontology.axiom"),
                ApplicationUtilities.getImage("axiom.gif"), SwingConstants.LEFT);
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
                    .getResourceString("ontology.axiom.dialog.explanation")), gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(ApplicationUtilities.getResourceString("ontology.axiom.name") +
                ":");
            name.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont().getSize()));
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtAxiomName, gbcl);
            txtAxiomName.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtAxiomName.getText().trim().equals("") &&
                                !txtAxiom.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Axiom
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.NORTHEAST;
            JLabel axiom = new JLabel(ApplicationUtilities.getResourceString("ontology.axiom") +
                ":");
            axiom.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont()
                .getSize()));
            center.add(axiom, gbcl);

            gbcl.gridx = 1;
            gbcl.weighty = 1;
            gbcl.weightx = 1;
            gbcl.anchor = GridBagConstraints.NORTH;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(txtAxiom), gbcl);
            txtAxiom.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtAxiomName.getText().trim().equals("") &&
                                !txtAxiom.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
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
                        txtAxiomName.requestFocus();
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