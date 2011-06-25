package ac.technion.iem.ontobuilder.gui.ontology;

import hypertree.HyperTree;

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
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.help.CSH;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdom.DocType;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import schemamatchings.meta.algorithms.Schema;

import com.jgraph.JGraph;
import com.modica.application.Actions;
import com.modica.application.ApplicationUtilities;
import com.modica.dom.DOMUtilities;
import com.modica.dom.NetworkEntityResolver;
import com.modica.gui.MultilineLabel;
import com.modica.gui.PopupTrigger;
import com.modica.gui.event.PopupListener;
import com.modica.html.ButtonINPUTElement;
import com.modica.html.CheckboxINPUTElement;
import com.modica.html.CheckboxINPUTElementOption;
import com.modica.html.FORMElement;
import com.modica.html.FileINPUTElement;
import com.modica.html.HTMLUtilities;
import com.modica.html.HiddenINPUTElement;
import com.modica.html.INPUTElement;
import com.modica.html.ImageINPUTElement;
import com.modica.html.OPTIONElement;
import com.modica.html.PasswordINPUTElement;
import com.modica.html.RadioINPUTElement;
import com.modica.html.RadioINPUTElementOption;
import com.modica.html.ResetINPUTElement;
import com.modica.html.SELECTElement;
import com.modica.html.SubmitINPUTElement;
import com.modica.html.TEXTAREAElement;
import com.modica.html.TextINPUTElement;
import com.modica.io.StringOutputStream;
import com.modica.ontology.algorithm.Algorithm;
import com.modica.ontology.event.OntologyModelEvent;
import com.modica.ontology.event.OntologyModelListener;
import com.modica.ontology.event.OntologySelectionEvent;
import com.modica.ontology.event.OntologySelectionListener;
import com.modica.ontology.match.MatchInformation;
import com.modica.util.FileUtilities;
import com.modica.util.NetworkUtilities;
import com.modica.util.StringUtilities;

/**
 * <p>Title: Ontology</p>
 * Extends {@link JPanel}
 * Implements {@link Schema}
 */
public class OntologyPanel extends JPanel

{
    private static final long serialVersionUID = -7583434763421818827L;

    // File formats
    final public static short XML_FORMAT = 0;
    final public static short BINARY_FORMAT = 1;
    final public static short BIZTALK_FORMAT = 2;
    final public static short LIGHT_XML_FORMAT = 3;

    protected JPopupMenu popMenu;
    protected Actions actions;
    protected ArrayList<OntologyModelListener> modelListeners;
    protected ArrayList<OntologySelectionListener> selectionListeners;

    protected OntologyModel model;
    protected JTree ontologyTree;
    protected OntologyTreeCellEditor ontologyCellEditor;

    protected Object actionObject;

    protected boolean dirty;
    protected File file;

    /**
     * Constructs a default Ontology
     */
    public OntologyPanel()
    {
        super(new BorderLayout());
        model = new OntologyModel();
        init();
    }

    /**
     * Constructs an Ontology
     * 
     * @param model an {@link OntologyModel}
     */
    public OntologyPanel(OntologyModel model)
    {
        super(new BorderLayout());
        this.model = model;
        init();
    }

    /**
     * Constructs an Ontology
     * 
     * @param name the ontology name
     */
    public OntologyPanel(String name)
    {
        this(name, "");
    }

    /**
     * Constructs an Ontology
     * 
     * @param name the ontology name
     * @param title the ontology title
     */
    public OntologyPanel(String name, String title)
    {
        super(new BorderLayout());
        model = new OntologyModel(name, title);
        init();
    }

    /**
     * Initialize the ontology
     */
    protected void init()
    {
        modelListeners = new ArrayList<OntologyModelListener>();
        selectionListeners = new ArrayList<OntologySelectionListener>();
        initializeActions();
        createPopupMenu();
        dirty = true;

        // Initialize the model
        model.addOntologyModelListener(new OntologyModelListener()
        {
            public void modelChanged(OntologyModelEvent e)
            {
                fireModelChangedEvent(e);
            }

            public void objectChanged(OntologyModelEvent e)
            {
                updateTree(e.getObject());
                fireObjectChangedEvent(e);
                dirty = true;
            }

            public void classAdded(OntologyModelEvent e)
            {
                addClassToTree(e.getSuperClass(), e.getOntologyClass());
                fireClassAddedEvent(e);
                dirty = true;
            }

            public void classDeleted(OntologyModelEvent e)
            {
                deleteClassFromTree(e.getSuperClass(), e.getOntologyClass());
                fireClassDeletedEvent(e);
                dirty = true;
            }

            public void termAdded(OntologyModelEvent e)
            {
                addTermToTree(e.getParent(), e.getTerm(), e.getPosition());
                fireTermAddedEvent(e);
                dirty = true;
            }

            public void termDeleted(OntologyModelEvent e)
            {
                deleteTermFromTree(e.getParent(), e.getTerm());
                fireTermDeletedEvent(e);
                dirty = true;
            }

            public void attributeAdded(OntologyModelEvent e)
            {
                addAttributeToTree(e.getOntologyClass(), e.getAttribute());
                fireAttributeAddedEvent(e);
                dirty = true;
            }

            public void attributeDeleted(OntologyModelEvent e)
            {
                deleteAttributeFromTree(e.getOntologyClass(), e.getAttribute());
                fireAttributeDeletedEvent(e);
                dirty = true;
            }

            public void axiomAdded(OntologyModelEvent e)
            {
                addAxiomToTree(e.getOntologyClass(), e.getAxiom());
                fireAxiomAddedEvent(e);
                dirty = true;
            }

            public void axiomDeleted(OntologyModelEvent e)
            {
                deleteAxiomFromTree(e.getOntologyClass(), e.getAxiom());
                fireAxiomDeletedEvent(e);
                dirty = true;
            }

            public void relationshipAdded(OntologyModelEvent e)
            {
                addRelationshipToTree(e.getTerm(), e.getRelationship());
                fireRelationshipAddedEvent(e);
                dirty = true;
            }

            public void relationshipDeleted(OntologyModelEvent e)
            {
                deleteRelationshipFromTree(e.getTerm(), e.getRelationship());
                fireRelationshipDeletedEvent(e);
                dirty = true;
            }

            public void domainEntryAdded(OntologyModelEvent e)
            {
                addDomainEntryToTree(e.getDomain(), e.getEntry());
                fireDomainEntryAddedEvent(e);
                dirty = true;
            }

            public void domainEntryDeleted(OntologyModelEvent e)
            {
                deleteDomainEntryFromTree(e.getDomain(), e.getEntry());
                fireDomainEntryDeletedEvent(e);
                dirty = true;
            }
        });

        // Initialize the view
        ontologyTree = new JTree(new OntologyTreeModel(model));
        ontologyTree.setCellRenderer(new OntologyTreeRenderer());
        ontologyTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ToolTipManager.sharedInstance().registerComponent(ontologyTree);
        ontologyTree.putClientProperty("JTree.lineStyle", "Angled");
        PopupTrigger pt = new PopupTrigger();
        pt.addPopupListener(new PopupListener()
        {
            public void popup(MouseEvent e)
            {
                JTree ontologyTree = (JTree) e.getSource();
                TreePath path = ontologyTree.getPathForLocation(e.getX(), e.getY());
                if (path == null)
                    return;
                ontologyTree.setSelectionPath(path);
                showPopupForObject((DefaultMutableTreeNode) path.getLastPathComponent(), e);
            }
        });
        ontologyTree.addMouseListener(pt);
        ontologyTree.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent e)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) e.getSource())
                    .getLastSelectedPathComponent();
                if (node != null)
                {
                    Object object = node.getUserObject();
                    fireOntologySelectionEvent(object);
                }
            }
        });
        ontologyTree.setEditable(true);
        ontologyCellEditor = new OntologyTreeCellEditor(ontologyTree);
        ontologyTree.setCellEditor(new OntologyDefaultTreeCellEditor(ontologyTree,
            (DefaultTreeCellRenderer) ontologyTree.getCellRenderer(), ontologyCellEditor));
        ontologyTree.getCellEditor().addCellEditorListener(new CellEditorListener()
        {
            public void editingStopped(ChangeEvent e)
            {
                OntologyTreeCellEditor editor = (OntologyTreeCellEditor) e.getSource();
                OntologyObject o = editor.getObjectBeingEdited();
                if (o != null)
                    o.setName(editor.getChangedValue());
            }

            public void editingCanceled(ChangeEvent e)
            {
            }
        });
        add(BorderLayout.CENTER, ontologyTree);
    }

    public String toString()
    {
        return model.toString();
    }

    /**
     * Check if the ontology is dirty
     * 
     * @return <code>true</code> if is dirty
     */
    public boolean isDirty()
    {
        return dirty;
    }

    /**
     * Set the ontology to dirty
     * 
     * @param b <code>true</code> if is dirty
     */
    public void setDirty(boolean b)
    {
        dirty = b;
    }

    /**
     * Get the OntologyModel
     * 
     * @return the {@link OntologyModel}
     */
    public OntologyModel getModel()
    {
        return model;
    }

    /**
     * Get the ontology name
     * 
     * @return the ontology name
     */
    public String getName()
    {
        return model.getName();
    }

    /**
     * Set the ontology name
     * 
     * @param name the ontology name
     */
    public void setName(String name)
    {
        model.setName(name);
    }

    /**
     * Get the ontology title
     * 
     * @return the ontology title
     */
    public String getTitle()
    {
        return model.getTitle();
    }

    /**
     * Set the ontology title
     * 
     * @param title the ontology title
     */
    public void setTitle(String title)
    {
        model.setTitle(title);
    }

    /**
     * Set the site URL
     * 
     * @param siteURL the site {@link URL}
     */
    public void setSiteURL(URL siteURL)
    {
        model.setSiteURL(siteURL);
    }

    /**
     * Get the site URL
     * 
     * @return the site {@link URL}
     */
    public URL getSiteURL()
    {
        return model.getSiteURL();
    }

    /**
     * Add a term
     * 
     * @param term the {@link Term} to add
     */
    public void addTerm(Term term)
    {
        model.addTerm(term);
    }

    /**
     * Remove a term
     * 
     * @param term the {@link Term} to remove
     */
    public void removeTerm(Term term)
    {
        model.removeTerm(term);
    }

    /**
     * Get the number of terms
     */
    public int getTermsCount()
    {
        return model.getTermsCount();
    }

    /**
     * Gets term according to ordinal index of term in term list. Not very useful unless you know
     * what you are looking for. Consider using getTermByID(id) if you know the Term ID
     * 
     * @param index
     * @return matching {@link Term} or Null if not found
     */
    public Term getTerm(int index)
    {
        return model.getTerm(index);
    }

    /**
     * Gets term according to term ID. Term ID is randomly generated when ontology is created and
     * saved to the ontology file Subsequent matching of this ontology with others will use this id
     * as well inside the MatchInformation object
     * 
     * @param id termID to find
     * @return {@link Term} if found, Null otherwise
     */
    public Term getTermByID(long id)
    {
        return model.getTermByID(id);
    }

    public void addClass(OntologyClass ontologyClass)
    {
        model.addClass(ontologyClass);
    }

    public void removeClass(OntologyClass ontologyClass)
    {
        model.removeClass(ontologyClass);
    }

    public int getClassesCount()
    {
        return model.getClassesCount();
    }

    public OntologyClass getClass(int index)
    {
        return model.getClass(index);
    }

    public void addOntologySelectionListener(OntologySelectionListener l)
    {
        selectionListeners.add(l);
    }

    public void removeOntologySelectionListener(OntologySelectionListener l)
    {
        selectionListeners.remove(l);
    }

    protected void fireOntologySelectionEvent(Object object)
    {
        OntologySelectionEvent event = new OntologySelectionEvent(this, object);
        for (Iterator<OntologySelectionListener> i = selectionListeners.iterator(); i.hasNext();)
        {
            OntologySelectionListener l = (OntologySelectionListener) i.next();
            l.valueChanged(event);
        }
    }

    public void addOntologyModelListener(OntologyModelListener l)
    {
        modelListeners.add(l);
    }

    public void removeOntologyModelListener(OntologyModelListener l)
    {
        modelListeners.remove(l);
    }

    protected void fireModelChangedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getObject());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.modelChanged(event);
        }
    }

    protected void fireObjectChangedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getObject());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.objectChanged(event);
        }
    }

    protected void fireTermAddedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getParent(), e.getTerm());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.termAdded(event);
        }
    }

    protected void fireTermDeletedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getParent(), e.getTerm());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.termDeleted(event);
        }
    }

    protected void fireClassAddedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getSuperClass(),
            e.getOntologyClass());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.classAdded(event);
        }
    }

    protected void fireClassDeletedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getSuperClass(),
            e.getOntologyClass());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.classDeleted(event);
        }
    }

    protected void fireAttributeAddedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getOntologyClass(),
            e.getAttribute());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.attributeAdded(event);
        }
    }

    protected void fireAttributeDeletedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getOntologyClass(),
            e.getAttribute());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.attributeDeleted(event);
        }
    }

    protected void fireAxiomAddedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getOntologyClass(), e.getAxiom());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.axiomAdded(event);
        }
    }

    protected void fireAxiomDeletedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getOntologyClass(), e.getAxiom());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.axiomDeleted(event);
        }
    }

    protected void fireRelationshipAddedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getTerm(), e.getRelationship());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.relationshipAdded(event);
        }
    }

    protected void fireRelationshipDeletedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getTerm(), e.getRelationship());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.relationshipDeleted(event);
        }
    }

    protected void fireDomainEntryAddedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getDomain(), e.getEntry());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.domainEntryAdded(event);
        }
    }

    protected void fireDomainEntryDeletedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getDomain(), e.getEntry());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.domainEntryDeleted(event);
        }
    }

    /**
     * Initialize all the relevant actions
     */
    protected void initializeActions()
    {
        actions = new Actions();

        // Add class
        Action action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.addClass"),
            ApplicationUtilities.getImage("addclass.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = -365323134917080608L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddClass();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addClass.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addClass.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.addClass.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.addClass.accelerator")));
        actions.addAction("addClass", action);

        // Add term
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.addTerm"),
            ApplicationUtilities.getImage("addterm.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = -6798374443298269596L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddTerm();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addTerm.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addTerm.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.addTerm.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.addTerm.accelerator")));
        actions.addAction("addTerm", action);

        // Add Attribute
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.addAttribute"),
            ApplicationUtilities.getImage("addattribute.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = -5782247577102612427L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddAttribute();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addAttribute.longDescription"));
        action
            .putValue(Action.SHORT_DESCRIPTION, ApplicationUtilities
                .getResourceString("ontology.action.addAttribute.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.addAttribute.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.addAttribute.accelerator")));
        actions.addAction("addAttribute", action);

        // Add Axiom
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.addAxiom"),
            ApplicationUtilities.getImage("addaxiom.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = -1429484172124654728L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddAxiom();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addAxiom.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addAxiom.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.addAxiom.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.addAxiom.accelerator")));
        actions.addAction("addAxiom", action);

        // Add Relationship
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.addRelationship"),
            ApplicationUtilities.getImage("addrelationship.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = 6838465638619976106L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddRelationship();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION, ApplicationUtilities
            .getResourceString("ontology.action.addRelationship.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION, ApplicationUtilities
            .getResourceString("ontology.action.addRelationship.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.addRelationship.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.addRelationship.accelerator")));
        actions.addAction("addRelationship", action);

        // Add Domain Entry
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.addDomainEntry"),
            ApplicationUtilities.getImage("adddomainentry.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = 2742783500475014344L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddDomainEntry();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION, ApplicationUtilities
            .getResourceString("ontology.action.addDomainEntry.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION, ApplicationUtilities
            .getResourceString("ontology.action.addDomainEntry.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.addDomainEntry.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.addDomainEntry.accelerator")));
        actions.addAction("addDomainEntry", action);

        // Delete
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.delete"),
            ApplicationUtilities.getImage("delete.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = 4629467236841994609L;

            public void actionPerformed(ActionEvent e)
            {
                commandDelete();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.delete.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.delete.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.delete.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.delete.accelerator")));
        actions.addAction("delete", action);

        // Rename
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.rename"),
            ApplicationUtilities.getImage("rename.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = 3193573320053147756L;

            public void actionPerformed(ActionEvent e)
            {
                commandRenameObject();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.rename.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.rename.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.rename.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.rename.accelerator")));
        actions.addAction("rename", action);
    }

    /**
     * Execute a "Rename" command
     */
    protected void commandRenameObject()
    {
        ontologyTree.startEditingAtPath(ontologyTree.getSelectionModel().getSelectionPath());
        ontologyCellEditor.getEditorComponent().requestFocus();
    }

    /**
     * Execute a "AddClass" command
     */
    protected void commandAddClass()
    {
        OntologyClass parent = (OntologyClass) actionObject;
        OntologyClass c = OntologyClass.createClassDialog(parent);
        if (c == null)
            return;
        if (parent == null)
            addClass(c);
    }

    /**
     * Execute a "AddTerm" command
     */
    protected void commandAddTerm()
    {
        Term t = Term.createTermDialog(model);
        if (t == null)
            return;
        Term parent = (Term) actionObject;
        if (parent != null)
            parent.addTerm(t);
        else
            addTerm(t);
    }

    /**
     * Execute a "AddAttribute" command
     */
    protected void commandAddAttribute()
    {
        Attribute a = Attribute.createAttributeDialog();
        if (a == null)
            return;
        if (actionObject instanceof DomainEntry)
            actionObject = ((DomainEntry) actionObject).getEntry();
        OntologyClass parent = (OntologyClass) actionObject;
        if (parent != null)
            parent.addAttribute(a);
    }

    /**
     * Execute a "AddAxiom" command
     */
    public void commandAddAxiom()
    {
        Axiom a = Axiom.createAxiomDialog();
        if (a == null)
            return;
        OntologyClass parent = (OntologyClass) actionObject;
        if (parent != null)
            parent.addAxiom(a);
    }

    /**
     * Execute a "AddRelationship" command
     */
    public void commandAddRelationship()
    {
        Term source = (Term) actionObject;
        Relationship r = Relationship.createRelationshipDialog(source, model);
        if (r != null)
            source.addRelationship(r);
    }

    /**
     * Execute a "AddDomainEntr" command
     */
    public void commandAddDomainEntry()
    {
        Domain domain = (Domain) actionObject;
        DomainEntry entry = DomainEntry.createEntryDialog(model);
        if (entry != null)
            domain.addEntry(entry);
    }

    /**
     * Execute a "Delete" command
     */
    protected void commandDelete()
    {
        if (actionObject instanceof Term)
        {
            Term t = (Term) actionObject;
            Vector<?> relationships = model.getRelationships();
            for (Iterator<?> i = relationships.iterator(); i.hasNext();)
            {
                Relationship r = (Relationship) i.next();
                if (t.equals(r.getSource()) || t.equals(r.getTarget()))
                {
                    if (JOptionPane.showConfirmDialog(this, StringUtilities.getReplacedString(
                        ApplicationUtilities.getResourceString("warning.ontology.deleteTerm"),
                        new String[]
                        {
                            t.getName()
                        }), ApplicationUtilities.getResourceString("ontology"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
                        return;
                }
            }
            Term pt = t.getParent();
            if (pt != null)
                pt.removeTerm(t);
            else
                removeTerm(t);
        }
        if (actionObject instanceof OntologyClass)
        {
            OntologyClass c = (OntologyClass) actionObject;
            Vector<?> terms = model.getTerms();
            for (Iterator<?> i = terms.iterator(); i.hasNext();)
                if (c.equals(((Term) i.next()).getSuperClass()))
                {
                    JOptionPane.showMessageDialog(null, StringUtilities.getReplacedString(
                        ApplicationUtilities.getResourceString("warning.ontology.deleteClass"),
                        new String[]
                        {
                            c.getName()
                        }), ApplicationUtilities.getResourceString("warning"),
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            OntologyClass sc = c.getSuperClass();
            if (sc != null)
                sc.removeSubClass(c);
            else
                removeClass(c);
        }
        else if (actionObject instanceof Attribute)
        {
            Attribute a = (Attribute) actionObject;
            OntologyClass c = a.getOntologyClass();
            c.removeAttribute(a);
        }
        else if (actionObject instanceof Axiom)
        {
            Axiom a = (Axiom) actionObject;
            OntologyClass c = a.getOntologyClass();
            c.removeAxiom(a);
        }
        else if (actionObject instanceof Relationship)
        {
            Relationship r = (Relationship) actionObject;
            Term t = r.getSource();
            t.removeRelationship(r);
        }
        else if (actionObject instanceof DomainEntry)
        {
            DomainEntry de = (DomainEntry) actionObject;
            Domain domain = de.getDomain();
            domain.removeEntry(de);
        }
    }

    protected void createPopupMenu()
    {
        // Sets the Popup Menu
        popMenu = new JPopupMenu(ApplicationUtilities.getResourceString("ontology"));
        addMenuItem(popMenu, actions.getAction("addClass"));
        addMenuItem(popMenu, actions.getAction("addTerm"));
        addMenuItem(popMenu, actions.getAction("addAttribute"));
        addMenuItem(popMenu, actions.getAction("addAxiom"));
        addMenuItem(popMenu, actions.getAction("addRelationship"));
        popMenu.addSeparator();
        addMenuItem(popMenu, actions.getAction("addDomainEntry"));
        popMenu.addSeparator();
        addMenuItem(popMenu, actions.getAction("delete"));
        addMenuItem(popMenu, actions.getAction("rename"));
    }

    protected JMenuItem addMenuItem(JPopupMenu menu, Action action)
    {
        JMenuItem menuItem = menu.add(action);
        menuItem.setAccelerator((KeyStroke) action.getValue(Action.ACCELERATOR_KEY));
        if (action.getValue("helpID") != null)
            CSH.setHelpIDString(menuItem, (String) action.getValue("helpID"));
        return menuItem;
    }

    protected void showPopupForObject(DefaultMutableTreeNode node, MouseEvent e)
    {
        Set<?> actionNames = actions.getActions();
        for (Iterator<?> i = actionNames.iterator(); i.hasNext();)
            actions.getAction((String) i.next()).setEnabled(false);

        actionObject = null;
        if (node == null)
            return;
        Object object = node.getUserObject();
        if (object instanceof String)
        {
            String nodeString = (String) object;
            if (nodeString.equals(ApplicationUtilities.getResourceString("ontology.terms")) ||
                nodeString.equals(ApplicationUtilities.getResourceString("ontology.subterms")))
            {
                actions.getAction("addTerm").setEnabled(true);
            }
            else if (nodeString.equals(ApplicationUtilities.getResourceString("ontology.classes")) ||
                nodeString.equals(ApplicationUtilities
                    .getResourceString("ontology.class.subclasses")))
            {
                actions.getAction("addClass").setEnabled(true);
            }
            else if (nodeString.equals(ApplicationUtilities
                .getResourceString("ontology.attributes")))
            {
                actions.getAction("addAttribute").setEnabled(true);
            }
            else if (nodeString.equals(ApplicationUtilities.getResourceString("ontology.axioms")))
            {
                actions.getAction("addAxiom").setEnabled(true);
            }
            else if (nodeString.equals(ApplicationUtilities
                .getResourceString("ontology.relationships")))
            {
                actions.getAction("addRelationship").setEnabled(true);
            }
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            if (parentNode != null && !parentNode.isRoot())
                actionObject = parentNode.getUserObject();
        }
        else
        {
            if (object instanceof Term)
            {
                actions.getAction("delete").setEnabled(true);
                actions.getAction("rename").setEnabled(true);
            }
            else if (object instanceof OntologyClass)
            {
                actions.getAction("delete").setEnabled(true);
                actions.getAction("rename").setEnabled(true);
            }
            else if (object instanceof Attribute)
            {
                actions.getAction("delete").setEnabled(true);
                actions.getAction("rename").setEnabled(true);
            }
            else if (object instanceof Axiom)
            {
                actions.getAction("delete").setEnabled(true);
                actions.getAction("rename").setEnabled(true);
            }
            else if (object instanceof Relationship)
            {
                actions.getAction("delete").setEnabled(true);
                actions.getAction("rename").setEnabled(true);
            }
            else if (object instanceof Domain)
            {
                // if(((Domain)object).getEntriesCount()==0)
                actions.getAction("rename").setEnabled(true);
                // if(((Domain)object).getName().equals(ApplicationUtilities.getResourceString("ontology.domain.choice")))
                actions.getAction("addDomainEntry").setEnabled(true);
            }
            else if (object instanceof DomainEntry)
            {
                actions.getAction("delete").setEnabled(true);
            }
            actionObject = object;
        }
        popMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    protected void addClassToTree(OntologyClass superClass, OntologyClass ontologyClass)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode classesNode;
            if (superClass == null) // add it to the root
                classesNode = treeModel.findChildNodeWithUserObject(ApplicationUtilities
                    .getResourceString("ontology.classes"));
            else
            {
                DefaultMutableTreeNode parentClassNode = treeModel
                    .findNodeWithUserObject(superClass);
                classesNode = treeModel.findChildNodeWithUserObject(parentClassNode,
                    ApplicationUtilities.getResourceString("ontology.class.subclasses"));
            }
            treeModel.insertNodeInto(ontologyClass.getTreeBranch(), classesNode,
                classesNode.getChildCount());
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void deleteClassFromTree(OntologyClass superClass, OntologyClass ontologyClass)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode classNode = treeModel.findNodeWithUserObject(ontologyClass);
            treeModel.removeNodeFromParent(classNode);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void addTermToTree(Term parent, Term term)
    {
        addTermToTree(parent, term, -1);
    }

    protected void addTermToTree(Term parent, Term term, int position)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode termsNode;
            if (parent == null) // add it to the root
                termsNode = treeModel.findChildNodeWithUserObject(ApplicationUtilities
                    .getResourceString("ontology.terms"));
            else
            {
                DefaultMutableTreeNode parentTermNode = treeModel.findNodeWithUserObject(parent);
                termsNode = treeModel.findChildNodeWithUserObject(parentTermNode,
                    ApplicationUtilities.getResourceString("ontology.subterms"));
            }
            treeModel.insertNodeInto(term.getTreeBranch(), termsNode,
                position == -1 ? termsNode.getChildCount() : position);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void deleteTermFromTree(Term parent, Term term)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode termNode = treeModel.findNodeWithUserObject(term);
            treeModel.removeNodeFromParent(termNode);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void addAttributeToTree(OntologyClass ontologyClass, Attribute attribute)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode parentNode = treeModel.findNodeWithUserObject(ontologyClass);
            DefaultMutableTreeNode attributesNode = treeModel.findChildNodeWithUserObject(
                parentNode, ApplicationUtilities.getResourceString("ontology.attributes"));
            treeModel.insertNodeInto(attribute.getTreeBranch(), attributesNode,
                attributesNode.getChildCount());
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void deleteAttributeFromTree(OntologyClass ontologyClass, Attribute attribute)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode attributeNode = treeModel.findNodeWithUserObject(attribute);
            treeModel.removeNodeFromParent(attributeNode);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void addAxiomToTree(OntologyClass ontologyClass, Axiom axiom)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode parentNode = treeModel.findNodeWithUserObject(ontologyClass);
            DefaultMutableTreeNode axiomsNode = treeModel.findChildNodeWithUserObject(parentNode,
                ApplicationUtilities.getResourceString("ontology.axioms"));
            treeModel.insertNodeInto(axiom.getTreeBranch(), axiomsNode, axiomsNode.getChildCount());
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void deleteAxiomFromTree(OntologyClass ontologyClass, Axiom axiom)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode axiomNode = treeModel.findNodeWithUserObject(axiom);
            treeModel.removeNodeFromParent(axiomNode);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void addRelationshipToTree(Term term, Relationship relationship)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode parentNode = treeModel.findNodeWithUserObject(term);
            DefaultMutableTreeNode relationshipsNode = treeModel.findChildNodeWithUserObject(
                parentNode, ApplicationUtilities.getResourceString("ontology.relationships"));
            treeModel.insertNodeInto(relationship.getTreeBranch(), relationshipsNode,
                relationshipsNode.getChildCount());
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void deleteRelationshipFromTree(Term term, Relationship relationship)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode relationshipNode = treeModel
                .findNodeWithUserObject(relationship);
            treeModel.removeNodeFromParent(relationshipNode);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void addDomainEntryToTree(Domain domain, DomainEntry entry)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode domainNode = treeModel.findNodeWithUserObject(domain);
            treeModel.insertNodeInto(entry.getTreeBranch(), domainNode, domainNode.getChildCount());
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void deleteDomainEntryFromTree(Domain domain, DomainEntry entry)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode entryNode = treeModel.findNodeWithUserObject(entry);
            treeModel.removeNodeFromParent(entryNode);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void updateTree(Object object)
    {
        OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
        for (int i = 0; i < ontologyTree.getRowCount(); i++)
        {
            TreePath path = ontologyTree.getPathForRow(i);
            if (((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject().equals(
                object))
                treeModel.valueForPathChanged(path, object);
        }
    }

    protected void refreshTree()
    {
        OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
        treeModel.updateTree();
    }

    protected static OntologyPanel ontology;

    public static OntologyPanel createOntologyDialog()
    {
        final com.modica.gui.TextField txtOntologyName = new com.modica.gui.TextField(10);
        final com.modica.gui.TextField txtOntologyTitle = new com.modica.gui.TextField(15);
        final com.modica.gui.TextField txtOntologySite = new com.modica.gui.TextField(15);

        final JDialog dialog = new JDialog((JFrame) null,
            ApplicationUtilities.getResourceString("ontology.dialog.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(ApplicationUtilities.getIntProperty("ontology.dialog.width"),
            ApplicationUtilities.getIntProperty("ontology.dialog.height")));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(txtOntologyTitle.getText().trim().length() > 0 &&
            txtOntologyName.getText().trim().length() > 0);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                ontology = new OntologyPanel(txtOntologyName.getText(), txtOntologyTitle.getText());
                try
                {
                    ontology.setSiteURL(NetworkUtilities.makeURL(txtOntologySite.getText()));
                    dialog.dispose();
                }
                catch (MalformedURLException ex)
                {
                    JOptionPane.showMessageDialog(dialog,
                        ApplicationUtilities.getResourceString("error") + ": " + ex.getMessage(),
                        ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.dialog.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                ontology = null;
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(ApplicationUtilities.getResourceString("ontology"),
                ApplicationUtilities.getImage("ontology.gif"), JLabel.LEFT);
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
                    .getResourceString("ontology.dialog.explanation")), gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(ApplicationUtilities.getResourceString("ontology.name") + ":");
            name.setFont(new Font(name.getFont().getName(), Font.BOLD, name.getFont().getSize()));
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtOntologyName, gbcl);
            txtOntologyName.addKeyListener(new KeyAdapter()
            {
                public void keyPressed(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtOntologyTitle.getText().trim().equals("") &&
                                !txtOntologyName.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Title
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel title = new JLabel(ApplicationUtilities.getResourceString("ontology.title") +
                ":");
            title
                .setFont(new Font(title.getFont().getName(), Font.BOLD, title.getFont().getSize()));
            center.add(title, gbcl);

            gbcl.gridx = 1;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            center.add(txtOntologyTitle, gbcl);
            txtOntologyTitle.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtOntologyTitle.getText().trim().equals("") &&
                                !txtOntologyName.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Site URL
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 4;
            gbcl.insets = new Insets(0, 0, 0, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(ApplicationUtilities.getResourceString("ontology.site") + ":");
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            center.add(txtOntologySite, gbcl);
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
                        txtOntologyName.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                ontology = null;
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);
        return ontology;
    }

    /**
     * Set the file
     * 
     * @param file the {@link File}
     */
    public void setFile(File file)
    {
        this.file = file;
    }

    /**
     * Get the file
     * 
     * @return the {@link File}
     */
    public File getFile()
    {
        return file;
    }

    /**
     * Save the ontology to a {@link File}
     */
    public void save(File file) throws IOException
    {
        save(file, XML_FORMAT);
    }

    /**
     * Save the ontology to a {@link File}.
     * <br>Available formats:
     * <br><code>BIZTALK_FORMAT</code>, <code>XML_FORMAT</code>, <code>BINARY_FORMAT</code>, <code>LIGHT_XML_FORMAT</code> 
     */
    public void save(File file, short format) throws IOException
    {
        switch (format)
        {
        case BIZTALK_FORMAT:
            saveToBizTalk(file);
            break;
        case XML_FORMAT:
            saveToXML(file);
            break;
        case BINARY_FORMAT:
            saveToBinary(file);
            break;
        case LIGHT_XML_FORMAT:
            saveToLightXML(file);
            break;
        default:
            throw new IOException(StringUtilities.getReplacedString(
                ApplicationUtilities.getResourceString("error.ontology.fileFormat"), new String[]
                {
                    model.getName()
                }));
        }

        dirty = false;
        this.file = file;
        model.fireModelChangedEvent(model);
    }

    /**
     * Save the ontology to a binary {@link File}
     */
    public void saveToBinary(File file) throws IOException
    {
        FileOutputStream out = new FileOutputStream(file);
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(model);
        os.flush();
        os.close();
    }

    /**
     * Save the ontology to an XML {@link File}
     */
    public void saveToXML(File file) throws IOException
    {
        org.jdom.Element ontologyElement = model.getXMLRepresentation();
        DocType ontologyDocType = new DocType("ontology", "dtds/ontology.dtd");
        org.jdom.Document ontologyDocument = new org.jdom.Document(ontologyElement, ontologyDocType);

        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        XMLOutputter fmt = new XMLOutputter("    ", true);
        fmt.output(ontologyDocument, out);
        out.close();
    }

    /**
     * Save the ontology to a light XML {@link File}
     */
    public void saveToLightXML(File file) throws IOException
    {
        org.jdom.Element ontologyElement = model.getLightXMLRepresentation();
        DocType ontologyDocType = new DocType("ontology", "dtds/ontology.dtd");
        org.jdom.Document ontologyDocument = new org.jdom.Document(ontologyElement, ontologyDocType);

        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        XMLOutputter fmt = new XMLOutputter("    ", true);
        fmt.output(ontologyDocument, out);
        out.close();
    }

    /**
     * Save the ontology to a BizTalk {@link File}
     */
    public void saveToBizTalk(File file) throws IOException
    {
        org.jdom.Element schemaElement = model.getBizTalkRepresentation();
        org.jdom.Document ontologyDocument = new org.jdom.Document(schemaElement);

        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        XMLOutputter fmt = new XMLOutputter("    ", true);
        fmt.output(ontologyDocument, out);
        out.close();
    }

    /**
     * Open an ontology from a file
     * 
     * @param file the {@link File} to read from
     * @return an {@link OntologyPanel}
     * @throws IOException
     */
    public static OntologyPanel open(File file) throws IOException
    {
        if (!file.exists())
            throw new IOException(StringUtilities.getReplacedString(
                ApplicationUtilities.getResourceString("error.ontology.file"), new String[]
                {
                    file.getAbsolutePath()
                }));
        try
        {
            String ext = FileUtilities.getFileExtension(file);
            if (ext != null && ext.equalsIgnoreCase("xml"))
                return openFromXML(file);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            OntologyModel model = (OntologyModel) ois.readObject();
            ois.close();
            model.listeners = new ArrayList<OntologyModelListener>();
            OntologyPanel ontology = new OntologyPanel(model);
            ontology.setFile(file);
            ontology.setDirty(false);
            return ontology;
        }
        catch (ClassNotFoundException e)
        {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Open an ontology from an XML file
     * 
     * @param file the {@link File} to read from
     * @return an {@link OntologyPanel}
     * @throws IOException
     */
    public static OntologyPanel openFromXML(File file) throws IOException
    {
        if (!file.exists())
            throw new IOException(StringUtilities.getReplacedString(
                ApplicationUtilities.getResourceString("error.ontology.file"), new String[]
                {
                    file.getAbsolutePath()
                }));
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            SAXBuilder builder = new SAXBuilder(true);
            builder.setEntityResolver(new NetworkEntityResolver());
            org.jdom.Document ontologyDocument = builder.build(reader);

            OntologyModel model = OntologyModel.getModelFromXML(ontologyDocument.getRootElement());
            OntologyPanel ontology = new OntologyPanel(model);
            ontology.setFile(file);
            ontology.setDirty(false);
            return ontology;
        }
        catch (JDOMException e)
        {
            throw new IOException(e.getMessage());
        }
    }

    public HyperTree getHyperTree(boolean showClasses, boolean showRelations, boolean showProperties)
    {
        return new HyperTree(model.getHyperTreeNode(showClasses, showRelations, showProperties));
    }

    public JGraph getGraph()
    {
        return model.getGraph();
    }

    /**
     * Match an ontology according to an algorithm
     * 
     * @param ontology the {@link OntologyPanel}
     * @param algorithm the {@link Algorithm}
     * @return MatchInformation
     */
    public MatchInformation match(OntologyPanel ontology, Algorithm algorithm)
    {
        return algorithm.match(this, ontology);
    }

    /**
     * Normalize the model
     */
    public void normalize()
    {
        model.normalize();
    }

    /**
     * Generate an ontology from a URL
     * 
     * @param url the {@link OntologyPanel}
     * @return an {@link OntologyPanel}
     * @throws IOException
     */
    public static OntologyPanel generateOntology(URL url) throws IOException
    {
        org.w3c.dom.Document document = DOMUtilities.getDOM(url,
            new PrintWriter(new StringWriter()));

        String ontologyTitle = "";
        String ontologyName = url.getHost();

        NodeList titles = document.getElementsByTagName("title");
        for (int i = 0; i < titles.getLength(); i++)
        {
            Node titleNode = (((org.w3c.dom.Element) titles.item(i)).getFirstChild());
            if (titleNode != null)
            {
                ontologyTitle = titleNode.getNodeValue();
                break;
            }
        }

        OntologyPanel ontology = new OntologyPanel(ontologyName, ontologyTitle);
        ontology.setSiteURL(url);

        // Predefined domains
        Domain formMethodDomain = new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.choice"), "choice");
        formMethodDomain.addEntry(new DomainEntry("post"));
        formMethodDomain.addEntry(new DomainEntry("get"));

        // Classes
        OntologyClass pageClass = new OntologyClass("page");
        ontology.addClass(pageClass);
        OntologyClass formClass = new OntologyClass("form");
        formClass.addAttribute(new Attribute("method", "get", formMethodDomain));
        formClass.addAttribute(new Attribute("action", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.url"), "url")));
        ontology.addClass(formClass);
        OntologyClass inputClass = new OntologyClass("input");
        inputClass.addAttribute(new Attribute("name", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.text"), "text")));
        inputClass.addAttribute(new Attribute("disabled", Boolean.FALSE, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.boolean"), "boolean")));
        ontology.addClass(inputClass);

        // Text Class
        OntologyClass textInputClass = new OntologyClass(inputClass, "text");
        textInputClass.addAttribute(new Attribute("type", "text"));
        textInputClass.addAttribute(new Attribute("defaultValue", null, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.text"), "text")));
        textInputClass.addAttribute(new Attribute("value", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.text"), "text")));
        textInputClass.addAttribute(new Attribute("size", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        textInputClass.addAttribute(new Attribute("maxLength", null, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.pinteger"), "pinteger")));
        textInputClass.addAttribute(new Attribute("readOnly", Boolean.FALSE, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.boolean"), "boolean")));

        // Password Class
        OntologyClass passwordInputClass = new OntologyClass(inputClass, "password");
        passwordInputClass.addAttribute(new Attribute("type", "password"));
        passwordInputClass.addAttribute(new Attribute("defaultValue", null, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.text"), "text")));
        passwordInputClass.addAttribute(new Attribute("size", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.pinteger"), "text")));
        passwordInputClass.addAttribute(new Attribute("maxLength", null, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.pinteger"), "pinteger")));
        passwordInputClass.addAttribute(new Attribute("readOnly", Boolean.FALSE, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.boolean"), "boolean")));

        // File Class
        OntologyClass fileInputClass = new OntologyClass(inputClass, "file");
        fileInputClass.addAttribute(new Attribute("type", "file"));
        fileInputClass.addAttribute(new Attribute("size", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        fileInputClass.addAttribute(new Attribute("maxLength", null, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.pinteger"), "pinteger")));
        fileInputClass.addAttribute(new Attribute("readOnly", Boolean.FALSE, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.boolean"), "boolean")));

        // Hidden Class
        OntologyClass hiddenInputClass = new OntologyClass(inputClass, "hidden");
        hiddenInputClass.addAttribute(new Attribute("type", "hidden"));

        // Checkbox Class
        OntologyClass checkboxInputClass = new OntologyClass(inputClass, "checkbox");
        checkboxInputClass.addAttribute(new Attribute("type", "checkbox"));

        // Radio Class
        OntologyClass radioInputClass = new OntologyClass(inputClass, "radio");
        radioInputClass.addAttribute(new Attribute("type", "radio"));

        // Select Class
        OntologyClass selectInputClass = new OntologyClass(inputClass, "select");
        selectInputClass.addAttribute(new Attribute("type", "select"));
        selectInputClass.addAttribute(new Attribute("size", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        selectInputClass.addAttribute(new Attribute("multiple", Boolean.FALSE, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.boolean"), "boolean")));

        // Textarea Class
        OntologyClass textareaInputClass = new OntologyClass(inputClass, "textarea");
        textareaInputClass.addAttribute(new Attribute("type", "textarea"));
        textareaInputClass.addAttribute(new Attribute("defaultValue", null, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.text"), "text")));
        textareaInputClass.addAttribute(new Attribute("rows", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        textareaInputClass.addAttribute(new Attribute("cols", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        textareaInputClass.addAttribute(new Attribute("readOnly", Boolean.FALSE, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.boolean"), "boolean")));

        // Button Class
        OntologyClass buttonInputClass = new OntologyClass(inputClass, "button");
        hiddenInputClass.addAttribute(new Attribute("type", "button"));

        // Submit Class
        OntologyClass submitInputClass = new OntologyClass(inputClass, "submit");
        submitInputClass.addAttribute(new Attribute("type", "submit"));

        // Reset Class
        OntologyClass resetInputClass = new OntologyClass(inputClass, "reset");
        resetInputClass.addAttribute(new Attribute("type", "reset"));

        // Image Class
        OntologyClass imageInputClass = new OntologyClass(inputClass, "image");
        imageInputClass.addAttribute(new Attribute("type", "image"));
        imageInputClass.addAttribute(new Attribute("src", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.url"), "url")));

        Term pageTerm = new Term(pageClass, url.toExternalForm());
        ontology.addTerm(pageTerm);
        JTree elementsTree = HTMLUtilities.getFORMElementsHierarchy(document, url);
        ArrayList<?> f = HTMLUtilities.extractFormsFromTree((DefaultMutableTreeNode) elementsTree
            .getModel().getRoot());

        Term prevFormTerm = null;
        for (Iterator<?> j = f.iterator(); j.hasNext();)
        {
            FORMElement form = (FORMElement) j.next();
            Term formTerm = new Term(formClass, form.getName());
            if (prevFormTerm != null)
            {
                prevFormTerm.setSucceed(formTerm);
                formTerm.setPrecede(prevFormTerm);
            }
            prevFormTerm = formTerm;
            formTerm.setAttributeValue("method", form.getMethod());
            formTerm.setAttributeValue("action", form.getAction());
            pageTerm.addTerm(formTerm);
            Term prevInputTerm = null;
            for (int k = 0; k < form.getInputsCount(); k++)
            {
                INPUTElement input = form.getInput(k);
                Term inputTerm = null;
                if (input.getInputType().equals(INPUTElement.TEXT))
                {
                    TextINPUTElement textInput = (TextINPUTElement) input;
                    inputTerm = new Term(textInputClass, textInput.getLabel(), textInput.getValue());
                    inputTerm.setAttributeValue("name", textInput.getName());
                    inputTerm.setAttributeValue("defaultValue", textInput.getDefaultValue());
                    if (textInput.getSize() != -1)
                        inputTerm.setAttributeValue("size", new Integer(textInput.getSize()));
                    if (textInput.getMaxLength() != -1)
                        inputTerm.setAttributeValue("maxLength",
                            new Integer(textInput.getMaxLength()));
                    inputTerm.setAttributeValue("readOnly", new Boolean(textInput.isReadOnly()));
                }
                else if (input.getInputType().equals(INPUTElement.PASSWORD))
                {
                    PasswordINPUTElement passwordInput = (PasswordINPUTElement) input;
                    inputTerm = new Term(passwordInputClass, passwordInput.getLabel(),
                        passwordInput.getValue());
                    inputTerm.setAttributeValue("name", passwordInput.getName());
                    inputTerm.setAttributeValue("defaultValue", passwordInput.getDefaultValue());
                    if (passwordInput.getSize() != -1)
                        inputTerm.setAttributeValue("size", new Integer(passwordInput.getSize()));
                    if (passwordInput.getMaxLength() != -1)
                        inputTerm.setAttributeValue("maxLength",
                            new Integer(passwordInput.getMaxLength()));
                    inputTerm
                        .setAttributeValue("readOnly", new Boolean(passwordInput.isReadOnly()));
                }
                else if (input.getInputType().equals(INPUTElement.FILE))
                {
                    FileINPUTElement fileInput = (FileINPUTElement) input;
                    inputTerm = new Term(fileInputClass, fileInput.getLabel(), fileInput.getValue());
                    inputTerm.setAttributeValue("name", fileInput.getName());
                    if (fileInput.getSize() != -1)
                        inputTerm.setAttributeValue("size", new Integer(fileInput.getSize()));
                    if (fileInput.getMaxLength() != -1)
                        inputTerm.setAttributeValue("maxLength",
                            new Integer(fileInput.getMaxLength()));
                    inputTerm.setAttributeValue("readOnly", new Boolean(fileInput.isReadOnly()));
                }
                else if (input.getInputType().equals(INPUTElement.HIDDEN))
                {
                    HiddenINPUTElement hiddenInput = (HiddenINPUTElement) input;
                    inputTerm = new Term(hiddenInputClass, input.getName(), hiddenInput.getValue());
                    inputTerm.setAttributeValue("name", hiddenInput.getName());
                }
                else if (input.getInputType().equals(INPUTElement.CHECKBOX))
                {
                    CheckboxINPUTElement checkboxInput = (CheckboxINPUTElement) input;
                    inputTerm = new Term(checkboxInputClass, checkboxInput.getLabel(),
                        checkboxInput.getValue());
                    inputTerm.setAttributeValue("name", checkboxInput.getName());
                    Domain checkboxDomain = new Domain(
                        ApplicationUtilities.getResourceString("ontology.domain.choice"), "choice");
                    for (int o = 0; o < checkboxInput.getOptionsCount(); o++)
                    {
                        CheckboxINPUTElementOption option = checkboxInput.getOption(o);
                        Term optionTerm = new Term(option.getLabel(), option.getValue());
                        optionTerm.addAttribute(new Attribute("checked", new Boolean(option
                            .isChecked())));
                        optionTerm.addAttribute(new Attribute("defaultChecked", new Boolean(option
                            .isDefaultChecked())));
                        checkboxDomain.addEntry(new DomainEntry(optionTerm));
                    }
                    inputTerm.setDomain(checkboxDomain);
                }
                else if (input.getInputType().equals(INPUTElement.RADIO))
                {
                    RadioINPUTElement radioInput = (RadioINPUTElement) input;
                    inputTerm = new Term(radioInputClass, radioInput.getLabel(),
                        radioInput.getValue());
                    inputTerm.setAttributeValue("name", radioInput.getName());
                    Domain radioDomain = new Domain(
                        ApplicationUtilities.getResourceString("ontology.domain.choice"), "choice");
                    for (int o = 0; o < radioInput.getOptionsCount(); o++)
                    {
                        RadioINPUTElementOption option = radioInput.getOption(o);
                        Term optionTerm = new Term(option.getLabel(), option.getValue());
                        optionTerm.addAttribute(new Attribute("checked", new Boolean(option
                            .isChecked())));
                        optionTerm.addAttribute(new Attribute("defaultChecked", new Boolean(option
                            .isDefaultChecked())));
                        radioDomain.addEntry(new DomainEntry(optionTerm));
                    }
                    inputTerm.setDomain(radioDomain);
                }
                else if (input.getInputType().equals(INPUTElement.SELECT))
                {
                    SELECTElement selectInput = (SELECTElement) input;
                    inputTerm = new Term(selectInputClass, selectInput.getLabel(),
                        selectInput.getValue());
                    inputTerm.setAttributeValue("name", selectInput.getName());
                    Domain selectDomain = new Domain(
                        ApplicationUtilities.getResourceString("ontology.domain.choice"), "choice");
                    for (int o = 0; o < selectInput.getOptionsCount(); o++)
                    {
                        OPTIONElement option = selectInput.getOption(o);
                        Term optionTerm = new Term(option.getLabel(), option.getValue());
                        optionTerm.addAttribute(new Attribute("selected", new Boolean(option
                            .isSelected())));
                        optionTerm.addAttribute(new Attribute("defaultSelected", new Boolean(option
                            .isDefaultSelected())));
                        selectDomain.addEntry(new DomainEntry(optionTerm));
                    }
                    inputTerm.setDomain(selectDomain);
                }
                else if (input.getInputType().equals(INPUTElement.TEXTAREA))
                {
                    TEXTAREAElement textareaInput = (TEXTAREAElement) input;
                    inputTerm = new Term(textareaInputClass, textareaInput.getLabel(),
                        textareaInput.getValue());
                    inputTerm.setAttributeValue("name", textareaInput.getName());
                    inputTerm.setAttributeValue("defaultValue", textareaInput.getDefaultValue());
                    if (textareaInput.getRows() != -1)
                        inputTerm.setAttributeValue("rows", new Integer(textareaInput.getRows()));
                    if (textareaInput.getCols() != -1)
                        inputTerm.setAttributeValue("cols", new Integer(textareaInput.getCols()));
                    inputTerm
                        .setAttributeValue("readOnly", new Boolean(textareaInput.isReadOnly()));
                }
                else if (input.getInputType().equals(INPUTElement.BUTTON))
                {
                    ButtonINPUTElement buttonInput = (ButtonINPUTElement) input;
                    inputTerm = new Term(buttonInputClass, buttonInput.getValue());
                    inputTerm.setAttributeValue("name", buttonInput.getName());
                }
                else if (input.getInputType().equals(INPUTElement.SUBMIT))
                {
                    SubmitINPUTElement submitInput = (SubmitINPUTElement) input;
                    inputTerm = new Term(submitInputClass, submitInput.getValue());
                    inputTerm.setAttributeValue("name", submitInput.getName());
                }
                else if (input.getInputType().equals(INPUTElement.RESET))
                {
                    ResetINPUTElement resetInput = (ResetINPUTElement) input;
                    inputTerm = new Term(resetInputClass, resetInput.getValue());
                    inputTerm.setAttributeValue("name", resetInput.getName());
                }
                else if (input.getInputType().equals(INPUTElement.IMAGE))
                {
                    ImageINPUTElement imageInput = (ImageINPUTElement) input;
                    inputTerm = new Term(imageInputClass, imageInput.getAlt());
                    inputTerm.setAttributeValue("name", imageInput.getName());
                    inputTerm.setAttributeValue("src", imageInput.getSrc());
                }
                if (inputTerm != null)
                {
                    inputTerm.setAttributeValue("disabled", new Boolean(input.isDisabled()));
                    Hashtable<?, ?> events = input.getEvents();
                    for (Enumeration<?> e = events.keys(); e.hasMoreElements();)
                    {
                        String event = (String) e.nextElement();
                        String script = (String) events.get(event);
                        inputTerm.addAxiom(new Axiom(event, script));
                    }
                    if (prevInputTerm != null)
                    {
                        prevInputTerm.setSucceed(inputTerm);
                        inputTerm.setPrecede(prevInputTerm);
                    }
                    prevInputTerm = inputTerm;
                    formTerm.addTerm(inputTerm);
                }
            }
        }

        return ontology;
    }

    public String getXMLRepresentationAsString() throws IOException
    {
        org.jdom.Element ontologyElement = model.getXMLRepresentation();
        DocType ontologyDocType = new DocType("ontology");
        org.jdom.Document ontologyDocument = new org.jdom.Document(ontologyElement, ontologyDocType);

        StringOutputStream xmlRepresentation = new StringOutputStream();
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(xmlRepresentation));
        XMLOutputter fmt = new XMLOutputter("    ", true);
        fmt.output(ontologyDocument, out);
        out.close();
        return xmlRepresentation.toString();
    }

}