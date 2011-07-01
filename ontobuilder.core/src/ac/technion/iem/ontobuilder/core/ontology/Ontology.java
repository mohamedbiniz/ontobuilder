package ac.technion.iem.ontobuilder.core.ontology;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom.Element;
import org.jdom.Namespace;

import com.jgraph.JGraph;
import com.jgraph.graph.ConnectionSet;
import com.jgraph.graph.DefaultGraphCell;
import com.jgraph.graph.DefaultGraphModel;
import com.jgraph.graph.DefaultPort;
import com.jgraph.graph.GraphConstants;
import com.modica.application.ApplicationUtilities;
import com.modica.application.PropertiesCellEditor;
import com.modica.application.PropertiesTableModel;
import com.modica.graph.GraphUtilities;
import com.modica.graph.OrderedDefaultPort;
import com.modica.hypertree.NodeHyperTree;
import com.modica.ontology.event.OntologyModelEvent;
import com.modica.ontology.event.OntologyModelListener;
import com.modica.util.NetworkUtilities;
import com.modica.util.StringUtilities;

/**
 * <p>Title: Ontology</p>
 * Extends {@link OntologyObject}
 */
public class Ontology extends OntologyObject
{
    private static final long serialVersionUID = -6672012851686868915L;

    protected String title;
    protected URL siteURL;

    protected ArrayList<Term> terms;
    protected ArrayList<OntologyClass> classes;

    protected boolean isLight = false;

    transient protected ArrayList<OntologyModelListener> listeners;

    /**
     * Constructs a default OntologyModel
     */
    public Ontology()
    {
        super();
        terms = new ArrayList<Term>();
        if (!isLight)
        {
            classes = new ArrayList<OntologyClass>();
        }
        listeners = new ArrayList<OntologyModelListener>();
    }

    /**
     * Constructs a OntologyModel
     * 
     * @param name the OntologyModel name
     * @param title the OntologyModel title
     */
    public Ontology(String name, String title)
    {
        this();
        this.name = name;
        this.title = title;
    }

    public void addOntologyModelListener(OntologyModelListener l)
    {
        listeners.add(l);
    }

    public void removeOntologyModelListener(OntologyModelListener l)
    {
        listeners.remove(l);
    }

    protected void fireModelChangedEvent(OntologyObject object)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, object);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.modelChanged(event);
        }
    }

    protected void fireObjectChangedEvent(OntologyObject object)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, object);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.objectChanged(event);
            l.modelChanged(event);
        }
    }

    protected void fireTermAddedEvent(Term parent, Term term)
    {
        fireTermAddedEvent(parent, term, -1);
    }

    protected void fireTermAddedEvent(Term parent, Term term, int position)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, parent, term);
        event.setObject(parent);
        event.setPosition(position);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.termAdded(event);
            l.modelChanged(event);
        }
    }

    protected void fireTermDeletedEvent(Term parent, Term term)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, parent, term);
        event.setObject(parent);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.termDeleted(event);
            l.modelChanged(event);
        }
    }

    protected void fireClassAddedEvent(OntologyClass superClass, OntologyClass ontologyClass)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, superClass, ontologyClass);
        event.setObject(superClass);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.classAdded(event);
            l.modelChanged(event);
        }
    }

    protected void fireClassDeletedEvent(OntologyClass superClass, OntologyClass ontologyClass)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, superClass, ontologyClass);
        event.setObject(superClass);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.classDeleted(event);
            l.modelChanged(event);
        }
    }

    protected void fireAttributeAddedEvent(OntologyClass ontologyClass, Attribute attribute)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, ontologyClass, attribute);
        event.setObject(ontologyClass);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.attributeAdded(event);
            l.modelChanged(event);
        }
    }

    protected void fireAttributeDeletedEvent(OntologyClass ontologyClass, Attribute attribute)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, ontologyClass, attribute);
        event.setObject(ontologyClass);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.attributeDeleted(event);
            l.modelChanged(event);
        }
    }

    protected void fireAxiomAddedEvent(OntologyClass ontologyClass, Axiom axiom)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, ontologyClass, axiom);
        event.setObject(ontologyClass);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.axiomAdded(event);
            l.modelChanged(event);
        }
    }

    protected void fireAxiomDeletedEvent(OntologyClass ontologyClass, Axiom axiom)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, ontologyClass, axiom);
        event.setObject(ontologyClass);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.axiomDeleted(event);
            l.modelChanged(event);
        }
    }

    protected void fireRelationshipAddedEvent(Term term, Relationship relationship)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, term, relationship);
        event.setObject(term);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.relationshipAdded(event);
            l.modelChanged(event);
        }
    }

    protected void fireRelationshipDeletedEvent(Term term, Relationship relationship)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, term, relationship);
        event.setObject(term);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.relationshipDeleted(event);
            l.modelChanged(event);
        }
    }

    protected void fireDomaiEntryAddedEvent(Domain domain, DomainEntry entry)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, domain, entry);
        event.setObject(domain);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.domainEntryAdded(event);
            l.modelChanged(event);
        }
    }

    protected void fireDomaiEntryDeletedEvent(Domain domain, DomainEntry entry)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, domain, entry);
        event.setObject(domain);
        for (Iterator<OntologyModelListener> i = listeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.domainEntryDeleted(event);
            l.modelChanged(event);
        }
    }

    /**
     * Set the name
     * 
     * @param name the name
     */
    public void setName(String name)
    {
        super.setName(name);
        fireObjectChangedEvent(this);
        fireModelChangedEvent(this);
    }

    /**
     * Get the ontology title
     * 
     * @return the ontology title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Set the title
     * 
     * @param title the title
     */
    public void setTitle(String title)
    {
        this.title = title;
        fireModelChangedEvent(this);
    }

    /**
     * Set the site URL
     * 
     * @param siteURL the site {@link URL}
     */
    public void setSiteURL(URL siteURL)
    {
        this.siteURL = siteURL;
        fireModelChangedEvent(this);
    }

    /**
     * Set the site URL
     * 
     * @param siteURL the site {@link URL} string
     */
    public void setSiteURL(String siteURL)
    {
        try
        {
            this.siteURL = NetworkUtilities.makeURL(siteURL);
            fireModelChangedEvent(this);
        }
        catch (MalformedURLException e)
        {
        }
    }

    /**
     * Get the site URL
     * 
     * @return the site {@link URL}
     */
    public URL getSiteURL()
    {
        return siteURL;
    }

    /**
     * Add a term
     * 
     * @param term the {@link Term} to add
     */
    public void addTerm(final Term term)
    {
        if (!terms.contains(term))
        {
            terms.add(term);
            term.setOntology(this);
            fireTermAddedEvent(null, term);
        }
    }

    /**
     * Resolves a term - generates a unique term
     * 
     * @param t the {@link Term}
     * @return the unique {@link Term}
     */
    public Term resolveTerm(Term t)
    {
        int cnt = 0;
        for (Iterator<Term> it = terms.iterator(); it.hasNext();)
        {
            Term _t = (Term) it.next();
            if (t.getName().equals(_t.getName()))
                cnt++;
        }

        String head = "";
        for (int i = 0; i < cnt; i++)
        {
            head += "_";
        }

        t.setName(head + t.getName());

        return t;
    }

    /**
     * Remove a term
     * 
     * @param term the {@link Term} to remove
     */
    public void removeTerm(Term term)
    {
        if (terms.contains(term))
        {
            term.removeAssociatedRelationships();
            term.setOntology(null);
            terms.remove(term);
            fireTermDeletedEvent(null, term);
        }
    }

    /**
     * Return the number of terms in model (excluding subterms)
     * 
     * @return num of terms in model
     */
    public int getTermsCount()
    {
        return terms.size();
    }

    /**
     * Travel over ontology model and return no. of terms in model
     * 
     * @return num of terms in model (including sub terms, sub-sub-terms, etc.)
     */
    public int getAllTermsCount()
    {
        return getTerms().size();
    }

    /**
     * Gets term according to ordinal index of term in term list. Not very useful unless you know
     * what you are looking for. Consider using getTermByID(id) if you know the Term ID
     * 
     * @param index
     * @return matching Term or Null if not found
     */
    public Term getTerm(int index)
    {
        if (index < 0 || index >= terms.size())
            return null;
        return (Term) terms.get(index);
    }

    public void addClass(final OntologyClass ontologyClass)
    {

        if (isLight)
            return;

        if (!classes.contains(ontologyClass))
        {
            classes.add(ontologyClass);
            ontologyClass.setOntology(this);
            fireClassAddedEvent(null, ontologyClass);
        }
    }

    public void removeClass(OntologyClass ontologyClass)
    {
        if (isLight)
            return;

        if (classes.contains(ontologyClass))
        {
            ontologyClass.setOntology(null);
            classes.remove(ontologyClass);
            fireClassDeletedEvent(null, ontologyClass);
        }
    }

    public int getClassesCount()
    {
        return classes.size();
    }

    public OntologyClass getClass(int index)
    {
        if (isLight || index < 0 || index >= classes.size())
            return null;
        return (OntologyClass) classes.get(index);
    }

    public int compare(Object o1, Object o2)
    {
        return ((Ontology) o1).name.compareTo(((Ontology) o2).name);
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof Ontology))
            return false;
        Ontology d = (Ontology) o;
        if (terms.size() != d.terms.size())
            return false;
        for (int i = 0; i < terms.size(); i++)
            if (!terms.get(i).equals(d.terms.get(i)))
                return false;
        return true;
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
                ApplicationUtilities.getResourceString("ontology.name"), name
            },
            {
                ApplicationUtilities.getResourceString("ontology.title"), title
            },
            {
                ApplicationUtilities.getResourceString("ontology.site"),
                siteURL != null ? siteURL.toExternalForm() : null
            },
            {
                ApplicationUtilities.getResourceString("ontology.terms"), new Integer(terms.size())
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 4, data)
        {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int col)
            {
                return col == 1 && (row == 0 || row == 1 || row == 2);
            }
        });
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
                    if (!title.equals(label))
                        setTitle(label);
                    break;
                case 2:
                    if (!siteURL.toExternalForm().equals(label))
                        setSiteURL(label);
                }
            }
        });
        return properties;
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);

        if (!isLight)
        {
            DefaultMutableTreeNode classesNode = new DefaultMutableTreeNode(
                ApplicationUtilities.getResourceString("ontology.classes"));
            root.add(classesNode);
            for (Iterator<OntologyClass> i = classes.iterator(); i.hasNext();)
                classesNode.add(((OntologyClass) i.next()).getTreeBranch());
        }

        DefaultMutableTreeNode termsNode = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("ontology.terms"));
        root.add(termsNode);
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
            termsNode.add(((Term) i.next()).getTreeBranch());

        return root;
    }

    public NodeHyperTree getHyperTreeNode(boolean showClasses, boolean showRelations,
        boolean showProperties)
    {
        NodeHyperTree root = new NodeHyperTree(this, NodeHyperTree.CLASS);

        if (showClasses && !isLight)
        {
            NodeHyperTree classesNode = new NodeHyperTree(
                ApplicationUtilities.getResourceString("ontology.classes"), NodeHyperTree.CLASS);
            root.add(classesNode);
            for (Iterator<OntologyClass> i = classes.iterator(); i.hasNext();)
                classesNode.add(((OntologyClass) i.next()).getHyperTreeNode(showProperties));
        }

        NodeHyperTree termsNode = new NodeHyperTree(
            ApplicationUtilities.getResourceString("ontology.terms"), NodeHyperTree.TERM);
        root.add(termsNode);
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
            termsNode.add(((Term) i.next()).getHyperTreeNode(showRelations, showClasses,
                showProperties));

        return root;
    }

    public JGraph getGraph()
    {
        JGraph graph = new JGraph(new DefaultGraphModel());
        graph.setEditable(false);
        ArrayList<DefaultGraphCell> cells = new ArrayList<DefaultGraphCell>();
        // ConnectionSet for the Insert method
        ConnectionSet cs = new ConnectionSet();
        // Hashtable for Attributes (Vertex to Map)
        Hashtable<DefaultGraphCell, Map<?, ?>> attributes = new Hashtable<DefaultGraphCell, Map<?, ?>>();

        DefaultGraphCell vertex = new DefaultGraphCell(name);
        cells.add(vertex);
        Map<?, ?> map = GraphUtilities.createDefaultAttributes();
        GraphConstants.setIcon(map, ApplicationUtilities.getImage("ontology.gif"));
        attributes.put(vertex, map);

        if (!terms.isEmpty())
        {
            DefaultPort toChildPort = new OrderedDefaultPort("toChild");
            vertex.add(toChildPort);
            for (Iterator<Term> i = terms.iterator(); i.hasNext();)
            {
                Term term = (Term) i.next();
                term.buildGraphHierarchy(toChildPort, cells, attributes, cs);
            }
            if (GraphUtilities.getShowPrecedenceLinks())
            {
                for (Iterator<Term> i = terms.iterator(); i.hasNext();)
                {
                    Term term = (Term) i.next();
                    term.buildPrecedenceRelationships(cells, attributes, cs);
                }
            }
        }

        // Insert the cells (View stores attributes)
        graph.getModel().insert(cells.toArray(), cs, null, attributes);
        GraphUtilities.alignHierarchy(graph, SwingConstants.LEFT, 10, 10);
        return graph;
    }

    public DefaultGraphCell addToGraph(ArrayList<DefaultGraphCell> cells,
        Hashtable<DefaultGraphCell, Map<?, ?>> attributes, ConnectionSet cs)
    {
        DefaultGraphCell vertex = new DefaultGraphCell(name);
        cells.add(vertex);
        Map<?, ?> map = GraphUtilities.createDefaultAttributes();
        GraphConstants.setIcon(map, ApplicationUtilities.getImage("ontology.gif"));
        attributes.put(vertex, map);

        if (!terms.isEmpty())
        {
            DefaultPort toChildPort = new DefaultPort("toChild");
            vertex.add(toChildPort);
            for (Iterator<Term> i = terms.iterator(); i.hasNext();)
            {
                Term term = (Term) i.next();
                term.buildGraphHierarchy(toChildPort, cells, attributes, cs);
            }
            if (GraphUtilities.getShowPrecedenceLinks())
            {
                for (Iterator<Term> i = terms.iterator(); i.hasNext();)
                {
                    Term term = (Term) i.next();
                    term.buildPrecedenceRelationships(cells, attributes, cs);
                }
            }
        }

        return vertex;
    }

    // public DefaultGraphCell addToGraph(ArrayList cells,Hashtable attributes,ConnectionSet cs)
    // {
    // DefaultGraphCell vertex=new DefaultGraphCell(name);
    // cells.add(vertex);
    // Map map=GraphUtilities.createDefaultAttributes();
    // GraphConstants.setIcon(map, ApplicationUtilities.getImage("ontology.gif"));
    // attributes.put(vertex,map);
    //
    // if(!terms.isEmpty())
    // {
    // DefaultPort toChildPort=new DefaultPort("toChild");
    // vertex.add(toChildPort);
    // for(Iterator i=terms.iterator();i.hasNext();)
    // {
    // Term term=(Term)i.next();
    // term.buildGraphHierarchy(toChildPort,cells,attributes,cs);
    // }
    // if(GraphUtilities.getShowPrecedenceLinks())
    // {
    // for(Iterator i=terms.iterator();i.hasNext();)
    // {
    // Term term=(Term)i.next();
    // term.buildPrecedenceRelationships(cells,attributes,cs);
    // }
    // }
    // }
    //
    // return vertex;
    // }

    /**
     * Get the XML {@link Element} representation of the Ontology Model
     * 
     * @return an XML {@link Element}
     */
    public Element getXMLRepresentation()
    {
        Element ontologyElement = new Element("ontology");
        ontologyElement.setAttribute(new org.jdom.Attribute("name", name));
        ontologyElement.setAttribute(new org.jdom.Attribute("title", title));
        ontologyElement.setAttribute(new org.jdom.Attribute("type", (isLight ? "light" : "full")));
        if (siteURL != null)
            ontologyElement.setAttribute(new org.jdom.Attribute("site", siteURL.toExternalForm()));

        if (!isLight)
        {
            Element classesElement = new Element("classes");
            for (Iterator<OntologyClass> i = classes.iterator(); i.hasNext();)
                classesElement.addContent(((OntologyClass) i.next()).getXMLRepresentation());
            ontologyElement.addContent(classesElement);
        }

        Element termsElement = new Element("terms");
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
            termsElement.addContent(((Term) i.next()).getXMLRepresentation());
        ontologyElement.addContent(termsElement);

        return ontologyElement;
    }

    /**
     * Get the light XML {@link Element} representation of the Ontology Model
     * 
     * @return an light XML {@link Element}
     */
    public Element getLightXMLRepresentation()
    {
        Element ontologyElement = new Element("ontology");
        ontologyElement.setAttribute(new org.jdom.Attribute("name", name));
        ontologyElement.setAttribute(new org.jdom.Attribute("title", title));
        ontologyElement.setAttribute(new org.jdom.Attribute("type", "light"));
        if (siteURL != null)
            ontologyElement.setAttribute(new org.jdom.Attribute("site", siteURL.toExternalForm()));

        Element termsElement = new Element("terms");
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
            termsElement.addContent(((Term) i.next()).getXMLRepresentation());
        ontologyElement.addContent(termsElement);

        return ontologyElement;
    }

    /**
     * Get the BizTalk {@link Element}representation of the Ontology Model
     * 
     * @return an BizTalk {@link Element}
     */
    public Element getBizTalkRepresentation()
    {
        Element schemaElement = new Element("Schema");

        Namespace b = Namespace.getNamespace("b", "urn:schemas-microsoft-com:BizTalkServer");
        Namespace d = Namespace.getNamespace("d", "urn:schemas-microsoft-com:datatypes");
        Namespace def = Namespace.getNamespace("urn:schemas-microsoft-com:xml-data");
        schemaElement.addNamespaceDeclaration(b);
        schemaElement.addNamespaceDeclaration(d);
        schemaElement.addNamespaceDeclaration(def);

        schemaElement.setAttribute(new org.jdom.Attribute("name", StringUtilities
            .makeIdentifierString(name)));
        schemaElement.setAttribute(new org.jdom.Attribute("BizTalkServerEditorTool_Version", "1.5",
            b));
        schemaElement.setAttribute(new org.jdom.Attribute("standard", "XML", b));
        schemaElement.setAttribute(new org.jdom.Attribute("root_reference", StringUtilities
            .makeIdentifierString(name), b));

        schemaElement.addContent(new Element("SelectionFields", b));

        Element e = new Element("ElementType", def);
        e.setAttribute(new org.jdom.Attribute("name", StringUtilities.makeIdentifierString(name)));
        e.setAttribute(new org.jdom.Attribute("content", terms.size() > 0 ? "eltOnly" : "empty"));
        e.setAttribute(new org.jdom.Attribute("model", "closed"));
        e.addContent(new Element("RecordInfo", b));

        ArrayList<Object> names = new ArrayList<Object>();
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            term.getBizTalkRepresentation(schemaElement, e, def, b, d, names);
        }

        if (e.getChildren().size() > 1)
            schemaElement.addContent(e);

        return schemaElement;
    }

    /**
     * Get the model from an XML element
     * 
     * @param ontologyElement the XML {@link Element}
     * @return an {@link Ontology}
     */
    public static Ontology getModelFromXML(Element ontologyElement)
    {
        Ontology model = new Ontology(ontologyElement.getAttributeValue("name"),
            ontologyElement.getAttributeValue("title"));
        try
        {
            model.setSiteURL(NetworkUtilities.makeURL(ontologyElement.getAttributeValue("site")));
        }
        catch (MalformedURLException e)
        {
        }

        if (ontologyElement.getAttribute("type").getValue().equals("full"))
        {
            model.setLight(false);
            List<?> classElements = ontologyElement.getChild("classes").getChildren();
            for (Iterator<?> i = classElements.iterator(); i.hasNext();)
                model.addClass(OntologyClass.getClassFromXML((Element) i.next(), model));
        }
        else
        {
            model.setLight(true);
        }

        List<?> termsElements = ontologyElement.getChild("terms").getChildren();
        Term prevTerm = null;
        for (Iterator<?> i = termsElements.iterator(); i.hasNext();)
        {
            Term t = Term.getTermFromXML((Element) i.next(), model);
            if (prevTerm != null)
            {
                prevTerm.setSucceed(t);
                t.setPrecede(prevTerm);
            }
            model.addTerm(t);
            prevTerm = t;
        }

        for (Iterator<Term> i = model.terms.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            for (Iterator<?> j = termsElements.iterator(); j.hasNext();)
            {
                Element termElement = (Element) j.next();
                if (termElement.getAttributeValue("name").equals(term.getName()))
                    term.solveRelationshipsFromXML(termElement);
            }
        }

        return model;
    }

    public OntologyClass findClass(String name)
    {
        if (name == null || isLight)
            return null;
        Vector<Object> classes = getClasses();
        for (Iterator<Object> i = classes.iterator(); i.hasNext();)
        {
            OntologyClass ontologyClass = (OntologyClass) i.next();
            if (ontologyClass.getName().equals(name))
                return ontologyClass;
        }
        return null;
    }

    public Vector<Object> getClasses()
    {
        Vector<Object> cs = new Vector<Object>();
        for (Iterator<OntologyClass> i = classes.iterator(); i.hasNext();)
        {
            OntologyClass c = (OntologyClass) i.next();
            cs.add(c);
            getClassesRec(c, cs);
        }
        return cs;
    }

    protected void getClassesRec(OntologyClass c, Vector<Object> cs)
    {
        for (int i = 0; i < c.getSubClassesCount(); i++)
        {
            OntologyClass sc = c.getSubClass(i);
            if (!(sc instanceof Term))
            {
                cs.add(sc);
                getClassesRec(sc, cs);
            }
        }
    }

    public JTree getClassesHierarchy()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("ontology.classes"));
        for (Iterator<OntologyClass> i = classes.iterator(); i.hasNext();)
        {
            OntologyClass c = (OntologyClass) i.next();
            DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(c);
            getClassesHierarchyRec(c, classNode);
        }
        JTree tree = new JTree(root);
        tree.setCellRenderer(new OntologyTreeRenderer());
        return tree;
    }

    protected void getClassesHierarchyRec(OntologyClass c, DefaultMutableTreeNode root)
    {
        for (int i = 0; i < c.getSubClassesCount(); i++)
        {
            OntologyClass sc = c.getSubClass(i);
            if (!(sc instanceof Term))
            {
                DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(sc);
                root.add(subNode);
                getClassesHierarchyRec(sc, subNode);
            }
        }
    }

    /**
     * Find a term according to its name
     * 
     * @param name the name
     * @return the {@link Term}
     */
    public Term findTerm(String name)
    {

        if (name == null)
            return null;
        Vector<Term> terms = getTerms();
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            if (term.getName().equals(name))
                return term;
        }
        return null;
    }

    /**
     * Find a term according to its id
     * 
     * @param id the id
     * @return the {@link Term}
     */
    public Term getTermByID(long id)
    {
        Vector<Term> tmpTerms = this.getTerms();
        for (int i = 0; i < tmpTerms.size(); i++)
            if (((Term) tmpTerms.get(i)).getId() == id)
                return (Term) tmpTerms.get(i);
        return null;
    }

    /**
     * Search for a term according to its string representation
     * 
     * @param str the string
     * @return a {@link Term}
     */
    public Term searchTerm(String str)
    {

        if (name == null)
            return null;
        Vector<Term> terms = getTerms();
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            String termStr = OntologyUtilities.oneIdRemoval(term.toString());
            if (termStr.equals(str))
            {
                return (Term) term.simpleClone();
            }
            termStr = OntologyUtilities.oneIdRemoval(term.toStringVs2());
            if (termStr.equals(str))
            {
                return (Term) term.simpleClone();
            }
        }
        return null;
    }

    /**
     * Get a list of all the {@link Term}
     */
    public Vector<Term> getTerms()
    {
        Vector<Term> ts = new Vector<Term>();
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term t = (Term) i.next();
            ts.add(t);
            getTermsRec(t, ts);
        }
        return ts;
    }

    protected void getTermsRec(Term t, Vector<Term> ts)
    {
        for (int i = 0; i < t.getTermsCount(); i++)
        {
            Term st = t.getTerm(i);
            ts.add(st);
            getTermsRec(st, ts);
        }
    }

    public JTree getTermsHierarchy()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("ontology.terms"));
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term t = (Term) i.next();
            DefaultMutableTreeNode termNode = new DefaultMutableTreeNode(t);
            root.add(termNode);
            getTermsHierarchyRec(t, termNode);
        }
        JTree tree = new JTree(root);
        tree.setCellRenderer(new OntologyTreeRenderer());
        return tree;
    }

    protected void getTermsHierarchyRec(Term t, DefaultMutableTreeNode root)
    {
        for (int i = 0; i < t.getTermsCount(); i++)
        {
            Term st = t.getTerm(i);
            DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(st);
            root.add(subNode);
            getTermsHierarchyRec(st, subNode);
        }
    }

    public Vector<Relationship> getRelationships()
    {
        Vector<Relationship> rs = new Vector<Relationship>();
        Vector<Term> terms = getTerms();
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term t = (Term) i.next();
            for (int j = 0; j < t.getRelationshipsCount(); j++)
                rs.add(t.getRelationship(j));
        }
        return rs;
    }

    /**
     * Check if the model is light
     * 
     * @return true if is light
     */
    public boolean isLight()
    {
        return isLight;
    }

    /**
     * Set the model to be light
     * 
     * @param isLight is light
     */
    public void setLight(boolean isLight)
    {
        this.isLight = isLight;
    }

    /**
     * Normalise the model
     */
    public void normalize()
    {
        if (isLight)
            return;

        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
            ((Term) i.next()).normalize();
    }
}