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
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom.Element;
import org.jdom.Namespace;

import com.jgraph.graph.ConnectionSet;
import com.jgraph.graph.DefaultEdge;
import com.jgraph.graph.DefaultGraphCell;
import com.jgraph.graph.DefaultPort;
import com.jgraph.graph.GraphConstants;
import com.modica.application.ApplicationUtilities;
import com.modica.application.PropertiesTableModel;
import com.modica.biztalk.BizTalkUtilities;
import com.modica.graph.GraphUtilities;
import com.modica.graph.OrderedDefaultPort;
import com.modica.gui.MultilineLabel;
import com.modica.hypertree.NodeHyperTree;
import com.modica.ontology.domain.GuessedDomain;
import com.modica.ontology.operator.StringOperator;
import com.modica.util.Email;
import com.modica.util.StringUtilities;

/**
 * <p>Title: Term</p>
 * Extends {@link OntologyClass}
 */
public class Term extends OntologyClass
{
    private static final long serialVersionUID = 1L;

    protected Object value;
    protected Term parent;
    protected ArrayList<Relationship> relationships;
    protected ArrayList<Term> terms;

    protected Term precede;
    protected Term succeed;

    protected boolean isDecomposition = false;

    /**
     * Constructs a default Term
     */
    public Term()
    {
        super();
        relationships = new ArrayList<Relationship>();
        terms = new ArrayList<Term>();
    }

    /**
     * Constructs a Term
     * 
     * @param name the name
     */
    public Term(String name)
    {
        this();
        this.name = name;
    }

    /**
     * Constructs a Term
     * 
     * @param name the name
     * @param object the object
     */
    public Term(String name, Object value)
    {
        this(name);
        this.value = value;
    }

    /**
     * Constructs a Term
     * 
     * @param id the id
     * @param name the name
     * @param object the object
     */
    public Term(String id, String name, Object value)
    {
        this(name, value);
        if (id != null)
            this.id = Long.parseLong(id);
    }

    /**
     * Constructs a Term
     * 
     * @param ontologyClass the {@link OntologyClass}
     */
    public Term(OntologyClass ontologyClass)
    {
        this();
        setSuperClass(ontologyClass);
    }

    /**
     * Constructs a Term
     * 
     * @param ontologyClass the {@link OntologyClass}
     * @param name the name
     */
    public Term(OntologyClass ontologyClass, String name)
    {
        this(ontologyClass);
        this.name = name;
    }

    /**
     * Constructs a Term
     * 
     * @param ontologyClass the {@link OntologyClass}
     * @param name the name
     * @param value the object
     */
    public Term(OntologyClass ontologyClass, String name, Object value)
    {
        this(ontologyClass, name);
        this.value = value;
    }

    /**
     * Check if it is a decomposed term
     * 
     * @return <code>true</code> if is decomposed
     */
    public boolean isDecomposedTerm()
    {
        return isDecomposition;
    }

    /**
     * Get the term precede
     * 
     * @return the precede {@link Term}
     */
    public Term getPrecede()
    {
        return precede;
    }

    /**
     * Set the term precede
     * 
     * @param precede the precede {@link Term}
     */
    public void setPrecede(Term precede)
    {
        this.precede = precede;
    }

    /**
     * Get the term succeed
     * 
     * @return the succeed {@link Term}
     */
    public Term getSucceed()
    {
        return succeed;
    }

    /**
     * Set the term succeed
     * 
     * @param succeed the succeed {@link Term}
     */
    public void setSucceed(Term succeed)
    {
        this.succeed = succeed;
    }

    /**
     * Set the ontology
     * 
     * @param ontology the {@link Ontology}
     */
    public void setOntology(Ontology ontology)
    {
        this.ontology = ontology;
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term subTerm = (Term) i.next();
            subTerm.setOntology(ontology);
        }
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
     * Set the value
     * 
     * @param value the value
     */
    public void setValue(Object value)
    {
        this.value = value;
    }

    /**
     * Set the parent term
     * 
     * @param parent the parent {@link Term}
     */
    public void setParent(Term parent)
    {
        this.parent = parent;
    }

    /**
     * Get the parent term
     * 
     * @return the parent {@link Term}
     */
    public Term getParent()
    {
        return parent;
    }

    /**
     * Add a term
     * 
     * @param term the {@link Term} to add
     */
    public void addTerm(final Term term)
    {
        addTerm(term, true);
    }

    /**
     * Add a term
     * 
     * @param term the {@link Term} to add
     * @param fixPrecedence whether to fix precedence
     */
    protected void addTerm(final Term term, boolean fixPrecedence)
    {
        if (term == null)
            return;
        if (!terms.contains(term))
        {
            term.setParent(this);
            term.setOntology(ontology);
            addRelationship(new Relationship(this,
                ApplicationUtilities.getResourceString("ontology.relationships.parent"), term));
            term.addRelationship(new Relationship(term, ApplicationUtilities
                .getResourceString("ontology.relationships.child"), this));
            if (fixPrecedence)
            {
                Term prevTerm = terms.size() > 0 ? (Term) terms.get(terms.size() - 1) : null;
                if (prevTerm != null)
                {
                    prevTerm.succeed = term;
                    term.precede = prevTerm;
                }
            }
            terms.add(term);
            if (ontology != null)
                ontology.fireTermAddedEvent(this, term);
        }
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
            if (term.precede != null)
                term.precede.succeed = term.succeed;
            if (term.succeed != null)
                ;
            term.succeed.precede = term.precede;
            terms.remove(term);
        }
        if (ontology != null)
            ontology.fireTermDeletedEvent(this, term);
    }

    /**
     * Get the number of terms
     */
    public int getTermsCount()
    {
        return terms.size();
    }

    /**
     * Get the term by a specific index
     * 
     * @param index the index
     * @return the {@link Term}
     */
    public Term getTerm(int index)
    {
        if (index < 0 || index >= terms.size())
            return null;
        return (Term) terms.get(index);
    }

    /**
     * Get the term by a specific name
     * 
     * @param name the name
     * @return the {@link Term}
     */
    public Term getTerm(String name)
    {
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term t = (Term) i.next();
            if (t.name.equalsIgnoreCase(name))
                return t;
        }
        return null;
    }

    /**
     * Add an attribute
     * 
     * @param attribute the {@link Attribute} to add
     */
    public void addAttribute(Attribute attribute)
    {
        super.addAttribute(attribute);
        attribute.setTerm(this);
    }

    /**
     * Remove an attribute
     * 
     * @param attribute the {@link Attribute} to remove
     */
    public void removeAttribute(Attribute attribute)
    {
        super.removeAttribute(attribute);
        attribute.setTerm(null);
    }

    /**
     * Add a relationship
     * 
     * @param relationship the {@link Relationship} to add
     */
    public void addRelationship(Relationship relationship)
    {
        if (relationship == null)
            return;
        if (!relationships.contains(relationship))
        {
            relationships.add(relationship);
            if (ontology != null)
                ontology.fireRelationshipAddedEvent(this, relationship);
        }
    }

    /**
     * Remove a relationship
     * 
     * @param relationship the {@link Relationship} to remove
     */
    public void removeRelationship(Relationship relationship)
    {
        if (relationships.contains(relationship))
        {
            relationships.remove(relationship);
            if (ontology != null)
                ontology.fireRelationshipDeletedEvent(this, relationship);
        }
    }

    /**
     * Get the number of relationships
     */
    public int getRelationshipsCount()
    {
        return relationships.size();
    }

    /**
     * Get the relationship by a specific index
     * 
     * @param index the index
     * @return the {@link Relationship}
     */
    public Relationship getRelationship(int index)
    {
        if (index < 0 || index >= relationships.size())
            return null;
        return (Relationship) relationships.get(index);
    }

    public void removeAssociatedRelationships()
    {
        Vector<?> relationships = ontology.getRelationships();
        for (Iterator<?> i = relationships.iterator(); i.hasNext();)
        {
            Relationship r = (Relationship) i.next();
            if (this.equals(r.getSource()) || this.equals(r.getTarget()))
            {
                r.getSource().removeRelationship(r);
                r.getTarget().removeRelationship(r);
            }
        }
    }

    public String toString()
    {
        String attName = (String) getAttributeValue("name");
        return ((superClass != null ? superClass + ": " : "") + name + " (" +
            (attName != null ? attName : "") + ")");// + ","+id;
    }

    public String toStringVs2()
    {
        String attName = (String) getAttributeValue("name");
        return name + " (" + (attName != null ? attName : "") + ")" +
            (superClass != null ? ":" + superClass : "");// + ","+id;
    }

    /**
     * Make a copy of a term
     * 
     * @return the copied term
     */
    public Term copy()
    {
        Term t = (Term) clone();
        t.setId(this.id);
        return t;
    }

    public int compare(Object o1, Object o2)
    {
        return ((Term) o1).name.compareTo(((Term) o2).name);
    }

    public boolean equals(Object o)
    {
        if (o == null)
            return false;

        if (o instanceof Term)
        {
            Term t = (Term) o;
            return t.toString().equals(this.toString()) ||
                t.toStringVs2().equals(this.toStringVs2());

        }
        // return (name != null && ((Term)o).t != null ? name.equals(((Term)o).name) : false) &&
        // (getClass() != null && o.getClass() != null ? o.getClass()==getClass() : false) &&
        // id==((Term)o).id;
        return false;
    }

    public Object clone()
    {
        Term term = new Term(new String(name), value);
        term.setDomain((Domain) domain.clone());
        term.superClass = superClass;
        term.parent = parent;
        for (Iterator<?> i = axioms.iterator(); i.hasNext();)
            term.addAxiom((Axiom) ((Axiom) i.next()).clone());
        for (Iterator<?> i = attributes.iterator(); i.hasNext();)
            term.addAttribute((Attribute) ((Attribute) i.next()).clone());
        for (Iterator<Relationship> i = relationships.iterator(); i.hasNext();)
        {
            Relationship r = (Relationship) i.next();
            term.addRelationship(new Relationship(term, new String(r.getName()), r.getTarget()));
        }
        term.ontology = ontology;
        return term;
    }

    public Object simpleClone()
    {
        Term term = new Term(new String(name), value);
        term.setDomain((Domain) domain.clone());
        term.superClass = superClass;
        for (Iterator<?> i = axioms.iterator(); i.hasNext();)
            term.addAxiom((Axiom) ((Axiom) i.next()).clone());
        for (Iterator<?> i = attributes.iterator(); i.hasNext();)
            term.addAttribute((Attribute) ((Attribute) i.next()).clone());
        term.ontology = ontology;
        return term;
    }

    /**
     * Normalize the term
     */
    public void normalize()
    {
        // If this was already normalized or it is a part of a normalized term, then obviate the
        // normalization
        Term parent = getParent();
        if ((parent != null && parent.isInstanceOf("composition")) || isInstanceOf("composition"))
            return;

        // Create the composition class if is not already created
        OntologyClass compositionClass = ontology.findClass("composition");
        if (compositionClass == null)
        {
            compositionClass = new OntologyClass("composition");
            compositionClass.addAttribute(new Attribute("name", ""));
            ontology.addClass(compositionClass);
        }

        // Create the composition subclasses if not already created
        OntologyClass decompositionClass = ontology.findClass("decomposition");
        if (decompositionClass == null)
        {
            decompositionClass = new OntologyClass(compositionClass, "decomposition");
        }
        OntologyClass groupClass = ontology.findClass("group");
        if (groupClass == null)
        {
            groupClass = new OntologyClass(compositionClass, "group");
        }

        String gd = domain.guessDomain();
        Domain d = domain;

        if (gd != null &&
            (gd.equals("date") || gd.equals("time") || gd.equals("url") || gd.equals("email")))
        {
            GuessedDomain gdv = GuessedDomain.guessDomain(value);
            Object object = gdv != null ? gdv.getObject() : null;

            OntologyClass subTermsClass = getSuperClass();
            String termName = (String) getAttributeValue("name");
            if (termName == null)
                termName = "";
            else
                termName += "_";

            if (gd.equals("date"))
            {
                Term tday = new Term(subTermsClass, name + " day");
                tday.isDecomposition = true;
                tday.setAttributeValue("name", termName + "day");
                tday.domain.setName(ApplicationUtilities
                    .getResourceString("ontology.domain.pinteger"));
                OntologyUtilities.fillDomain(domain, tday.domain,
                    OntologyUtilities.DOMAIN_DATE_DAY_PART);
                addTerm(tday);
                Term tmonth = new Term(subTermsClass, name + " month");
                tmonth.isDecomposition = true;
                tmonth.setAttributeValue("name", termName + "month");
                tmonth.domain.setName(ApplicationUtilities
                    .getResourceString("ontology.domain.text"));
                OntologyUtilities.fillDomain(domain, tmonth.domain,
                    OntologyUtilities.DOMAIN_DATE_MONTH_PART);
                addTerm(tmonth);
                Term tyear = new Term(subTermsClass, name + " year");
                tyear.isDecomposition = true;
                tyear.setAttributeValue("name", termName + "year");
                tyear.domain.setName(ApplicationUtilities
                    .getResourceString("ontology.domain.pinteger"));
                OntologyUtilities.fillDomain(domain, tyear.domain,
                    OntologyUtilities.DOMAIN_DATE_YEAR_PART);
                addTerm(tyear);

                if (object != null && (object instanceof Date))
                {
                    Date date = (Date) object;
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);

                    tday.setValue(GuessedDomain.months[calendar.get(Calendar.DAY_OF_MONTH)]);
                    tmonth.setValue(new Integer(calendar.get(Calendar.MONTH) + 1));
                    tyear.setValue(new Integer(calendar.get(Calendar.YEAR)));
                }
                setSuperClass(decompositionClass);
            }
            else if (gd.equals("time"))
            {
                Term thour = new Term(subTermsClass, name + " hour");
                thour.isDecomposition = true;
                thour.setAttributeValue("name", termName + "hour");
                thour.domain.setName(ApplicationUtilities
                    .getResourceString("ontology.domain.pinteger"));
                OntologyUtilities.fillDomain(domain, thour.domain,
                    OntologyUtilities.DOMAIN_TIME_HOUR_PART);
                addTerm(thour);
                Term tminute = new Term(subTermsClass, name + " minute");
                tminute.isDecomposition = true;
                tminute.setAttributeValue("name", termName + "minute");
                tminute.domain.setName(ApplicationUtilities
                    .getResourceString("ontology.domain.pinteger"));
                OntologyUtilities.fillDomain(domain, tminute.domain,
                    OntologyUtilities.DOMAIN_TIME_MINUTE_PART);
                addTerm(tminute);
                Term tsecond = new Term(subTermsClass, name + " second");
                tsecond.isDecomposition = true;
                tsecond.setAttributeValue("name", termName + "second");
                tsecond.domain.setName(ApplicationUtilities
                    .getResourceString("ontology.domain.pinteger"));
                OntologyUtilities.fillDomain(domain, tsecond.domain,
                    OntologyUtilities.DOMAIN_TIME_SECOND_PART);
                addTerm(tsecond);
                Term tampm = new Term(subTermsClass, name + " am pm");
                tampm.isDecomposition = true;
                tampm.setAttributeValue("name", termName + "ampm");
                tampm.domain
                    .setName(ApplicationUtilities.getResourceString("ontology.domain.text"));
                OntologyUtilities.fillDomain(domain, tampm.domain,
                    OntologyUtilities.DOMAIN_TIME_AMPM_PART);
                addTerm(tampm);

                if (object != null && (object instanceof Date))
                {
                    Date date = (Date) object;
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);

                    thour.setValue(new Integer(calendar.get(Calendar.HOUR)));
                    tminute.setValue(new Integer(calendar.get(Calendar.MINUTE)));
                    tsecond.setValue(new Integer(calendar.get(Calendar.SECOND)));
                    tampm.setValue(calendar.get(Calendar.AM_PM) == Calendar.PM ? "pm" : "am");
                }
                setSuperClass(decompositionClass);
            }
            else if (gd.equals("url"))
            {
                Term tprotocol = new Term(subTermsClass, name + " protocol");
                tprotocol.isDecomposition = true;
                tprotocol.setAttributeValue("name", termName + "protocol");
                tprotocol.domain.setName(ApplicationUtilities
                    .getResourceString("ontology.domain.text"));
                OntologyUtilities.fillDomain(domain, tprotocol.domain,
                    OntologyUtilities.DOMAIN_URL_PROTOCOL_PART);
                addTerm(tprotocol);
                Term tport = new Term(subTermsClass, name + " port");
                tport.isDecomposition = true;
                tport.setAttributeValue("name", termName + "port");
                tport.domain.setName(ApplicationUtilities
                    .getResourceString("ontology.domain.pinteger"));
                OntologyUtilities.fillDomain(domain, tport.domain,
                    OntologyUtilities.DOMAIN_URL_PORT_PART);
                addTerm(tport);
                Term thost = new Term(subTermsClass, name + " host");
                thost.isDecomposition = true;
                thost.setAttributeValue("name", termName + "host");
                thost.domain
                    .setName(ApplicationUtilities.getResourceString("ontology.domain.text"));
                OntologyUtilities.fillDomain(domain, thost.domain,
                    OntologyUtilities.DOMAIN_URL_HOST_PART);
                addTerm(thost);
                Term tfile = new Term(subTermsClass, name + " file");
                tfile.isDecomposition = true;
                tfile.setAttributeValue("name", termName + "file");
                tfile.domain
                    .setName(ApplicationUtilities.getResourceString("ontology.domain.text"));
                OntologyUtilities.fillDomain(domain, tfile.domain,
                    OntologyUtilities.DOMAIN_URL_FILE_PART);
                addTerm(tfile);

                if (object != null && (object instanceof URL))
                {
                    URL url = (URL) object;

                    tprotocol.setValue(url.getProtocol());
                    tport.setValue(url.getPort() + "");
                    thost.setValue(url.getHost());
                    tfile.setValue(url.getFile());
                }
                setSuperClass(decompositionClass);
            }
            else if (gd.equals("email"))
            {
                Term tuser = new Term(subTermsClass, name + " user");
                tuser.isDecomposition = true;
                tuser.setAttributeValue("name", termName + "user");
                tuser.domain
                    .setName(ApplicationUtilities.getResourceString("ontology.domain.text"));
                OntologyUtilities.fillDomain(domain, tuser.domain,
                    OntologyUtilities.DOMAIN_EMAIL_USER_PART);
                addTerm(tuser);
                Term tdomain = new Term(subTermsClass, name + " domain");
                tdomain.isDecomposition = true;
                tdomain.setAttributeValue("name", termName + "domain");
                tdomain.domain.setName(ApplicationUtilities
                    .getResourceString("ontology.domain.text"));
                OntologyUtilities.fillDomain(domain, tdomain.domain,
                    OntologyUtilities.DOMAIN_EMAIL_DOMAIN_PART);
                addTerm(tdomain);

                if (object != null && (object instanceof String))
                {
                    Email email = (Email) object;
                    tuser.setValue(email.getUser());
                    tdomain.setValue(email.getDomain());
                }
                setSuperClass(decompositionClass);
            }
        }

        this.domain = d;

        // Group terms with same name together

        Hashtable<String, ArrayList<Term>> listedTerms = new Hashtable<String, ArrayList<Term>>();
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            if (term.isInstanceOf("form") || term.isInstanceOf("page"))
                continue;
            ArrayList<Term> list = (ArrayList<Term>) listedTerms.get(term.getName());
            if (list == null)
                listedTerms.put(term.getName(), list = new ArrayList<Term>());
            list.add(term);
        }

        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            term.normalize();
        }

        // OntologyClass inputClass=ontology.findClass("input");
        @SuppressWarnings("unchecked")
        ArrayList<Term> termsCopy = (ArrayList<Term>) terms.clone();
        for (Enumeration<String> e = listedTerms.keys(); e.hasMoreElements();)
        {
            ArrayList<?> list = (ArrayList<?>) listedTerms.get(e.nextElement());
            if (list.size() > 1)
            {
                Term firstTerm = (Term) list.get(0);
                int firstTermIndex = termsCopy.indexOf(firstTerm);

                Term baseTerm = new Term(groupClass, firstTerm.getName());
                baseTerm.setAttributeValue("name",
                    StringUtilities.makeIdentifierString(firstTerm.getName()) + "__Composition_");

                for (int i = 0; i < list.size(); i++)
                {
                    Term term = (Term) list.get(i);
                    baseTerm.addTerm(term, false);
                }

                for (Iterator<Term> i = terms.iterator(); i.hasNext();)
                {
                    Term term = (Term) i.next();
                    if (baseTerm.terms.contains(term))
                    {
                        i.remove();
                        ontology.fireTermDeletedEvent(this, term);
                    }
                }

                if (firstTermIndex > terms.size())
                    firstTermIndex = terms.size();

                baseTerm.setParent(this);
                baseTerm.setOntology(ontology);
                terms.add(firstTermIndex, baseTerm);

                // fix the precedence links
                Term prevTerm = firstTermIndex > 0 ? (Term) terms.get(firstTermIndex - 1) : null;
                if (prevTerm != null)
                {
                    baseTerm.succeed = prevTerm.succeed;
                    baseTerm.precede = prevTerm;
                }

                addRelationship(new Relationship(this,
                    ApplicationUtilities.getResourceString("ontology.relationships.parent"),
                    baseTerm));
                baseTerm.addRelationship(new Relationship(baseTerm, ApplicationUtilities
                    .getResourceString("ontology.relationships.child"), this));
                if (ontology != null)
                    ontology.fireTermAddedEvent(this, baseTerm, firstTermIndex);
            }
        }
    }

    public OntologyClass applyStringOperator(StringOperator operator)
    {
        Term term = new Term(operator.transformString(name),
            ((value instanceof String) ? operator.transformString((String) value) : value));
        term.setDomain(domain.applyStringOperator(operator));
        term.terms = terms;
        term.superClass = superClass;
        term.parent = parent;
        for (Iterator<?> i = axioms.iterator(); i.hasNext();)
            term.addAxiom(((Axiom) i.next()).applyStringOperator(operator));
        for (Iterator<?> i = attributes.iterator(); i.hasNext();)
            term.addAttribute(((Attribute) i.next()).applyStringOperator(operator));
        for (Iterator<Relationship> i = relationships.iterator(); i.hasNext();)
        {
            Relationship r = (Relationship) i.next();
            term.addRelationship(new Relationship(term, operator.transformString(r.getName()), r
                .getTarget()));
        }
        term.ontology = ontology;
        return term;
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
                ApplicationUtilities.getResourceString("ontology.term.name"), name
            },
            {
                ApplicationUtilities.getResourceString("ontology.term.value"), value
            },
            {
                ApplicationUtilities.getResourceString("ontology.domain"), domain.getName()
            },
            {
                ApplicationUtilities.getResourceString("ontology.class"),
                superClass != null ? superClass.getName() : null
            },
            {
                ApplicationUtilities.getResourceString("ontology"),
                ontology != null ? ontology.getName() : null
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 5, data));
        return properties;
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);

        root.add(domain.getTreeBranch());

        DefaultMutableTreeNode attributesNode = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("ontology.attributes"));
        root.add(attributesNode);
        for (Iterator<?> i = attributes.iterator(); i.hasNext();)
            attributesNode.add(((Attribute) i.next()).getTreeBranch());

        DefaultMutableTreeNode axiomsNode = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("ontology.axioms"));
        root.add(axiomsNode);
        for (Iterator<?> i = axioms.iterator(); i.hasNext();)
            axiomsNode.add(((Axiom) i.next()).getTreeBranch());

        DefaultMutableTreeNode relationshipsNode = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("ontology.relationships"));
        root.add(relationshipsNode);
        for (Iterator<Relationship> i = relationships.iterator(); i.hasNext();)
            relationshipsNode.add(((Relationship) i.next()).getTreeBranch());

        DefaultMutableTreeNode termsNode = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("ontology.subterms"));
        root.add(termsNode);
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
            termsNode.add(((Term) i.next()).getTreeBranch());

        return root;
    }

    public NodeHyperTree getHyperTreeNode(boolean showRelations, boolean showClasses,
        boolean showProperties)
    {
        NodeHyperTree root = new NodeHyperTree(this, NodeHyperTree.TERM);

        if (showProperties)
        {
            root.add(domain.getHyperTreeNode());
            NodeHyperTree attributesNode = new NodeHyperTree(
                ApplicationUtilities.getResourceString("ontology.attributes"),
                NodeHyperTree.PROPERTY);
            root.add(attributesNode);
            for (Iterator<?> i = attributes.iterator(); i.hasNext();)
                attributesNode.add(((Attribute) i.next()).getHyperTreeNode());

            NodeHyperTree axiomsNode = new NodeHyperTree(
                ApplicationUtilities.getResourceString("ontology.axioms"), NodeHyperTree.PROPERTY);
            root.add(axiomsNode);
            for (Iterator<?> i = axioms.iterator(); i.hasNext();)
                axiomsNode.add(((Axiom) i.next()).getHyperTreeNode());
        }

        if (showRelations)
        {
            NodeHyperTree relationshipsNode = new NodeHyperTree(
                ApplicationUtilities.getResourceString("ontology.relationships"),
                NodeHyperTree.RELATIONSHIP);
            root.add(relationshipsNode);
            for (Iterator<Relationship> i = relationships.iterator(); i.hasNext();)
                relationshipsNode.add(((Relationship) i.next()).getHyperTreeNode());
        }

        // NodeHyperTree termsNode=new
        // NodeHyperTree(ApplicationUtilities.getResourceString("ontology.subterms"));
        // root.add(termsNode);
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
            root.add(((Term) i.next()).getHyperTreeNode(showRelations, showClasses, showProperties));

        return root;
    }

    /**
     * Get the XML {@link Element} representation of the term
     * 
     * @return an XML {@link Element}
     */
    public Element getXMLRepresentation()
    {
        Element termElement = new Element("term");
        termElement.setAttribute(new org.jdom.Attribute("id", Long.toString(id)));
        termElement.setAttribute(new org.jdom.Attribute("name", name));
        if (value != null)
            termElement.setAttribute(new org.jdom.Attribute("value", value.toString()));
        if (superClass != null)
            termElement.setAttribute(new org.jdom.Attribute("class", superClass.getName()));

        // Domain
        termElement.addContent(domain.getXMLRepresentation());

        // Attributes
        Element attributesElement = new Element("attributes");
        termElement.addContent(attributesElement);
        for (Iterator<?> i = attributes.iterator(); i.hasNext();)
            attributesElement.addContent(((Attribute) i.next()).getXMLRepresentation());

        // Axioms
        Element axiomsElement = new Element("axioms");
        termElement.addContent(axiomsElement);
        for (Iterator<?> i = axioms.iterator(); i.hasNext();)
            axiomsElement.addContent(((Axiom) i.next()).getXMLRepresentation());

        // Relationships
        Element relationshipsElement = new Element("relationships");
        termElement.addContent(relationshipsElement);
        for (Iterator<Relationship> i = relationships.iterator(); i.hasNext();)
            relationshipsElement.addContent(((Relationship) i.next()).getXMLRepresentation());

        // Subterms
        Element subtermsElement = new Element("subterms");
        termElement.addContent(subtermsElement);
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
            subtermsElement.addContent(((Term) i.next()).getXMLRepresentation());

        return termElement;
    }

    /**
     * Get the BizTalk representation of the term
     * 
     * @return an BizTalk {@link Element}
     */
    public Element getBizTalkRepresentation(Element schema, Element parent, Namespace def,
        Namespace b, Namespace d, ArrayList<Object> names)
    {
        if (this.isInstanceOf("hidden"))
            return null;

        Element e = null;

        String termName = (String) getAttributeValue("name");
        String elementName = BizTalkUtilities.makeValidBizTalkName(name, termName);
        if (elementName.length() == 0)
            elementName = "_" + id;

        // Solve duplicates
        while (names.contains(elementName))
            elementName += "_";
        names.add(elementName);

        if (terms.size() > 0) // treat this term as an element
        {
            e = new Element("ElementType", def);
            e.setAttribute(new org.jdom.Attribute("name", elementName));

            // determine type of content
            String content = "empty";
            for (Iterator<Term> i = terms.iterator(); i.hasNext();)
            {
                Term term = (Term) i.next();
                if (term.getTermsCount() > 0)
                {
                    content = "eltOnly";
                    break;
                }
            }

            e.setAttribute(new org.jdom.Attribute("content", content));
            e.setAttribute(new org.jdom.Attribute("model", "closed"));
            e.addContent(new Element("RecordInfo", b));

            Element element = new Element("element", def);
            element.setAttribute(new org.jdom.Attribute("type", elementName));
            element.setAttribute(new org.jdom.Attribute("maxOccurs", "1"));
            element.setAttribute(new org.jdom.Attribute("minOccurs", "0"));

            for (Iterator<Term> i = terms.iterator(); i.hasNext();)
            {
                Term term = (Term) i.next();
                term.getBizTalkRepresentation(schema, e, def, b, d, names);
            }

            if (e.getChildren().size() > 1)
            {
                parent.addContent(element);
                schema.addContent(e);
            }
        }
        else if (isInstanceOf("input"))// treat this term as an attribute
        {
            // determine type of this term
            String type = null;
            String ontologyClass = superClass.getName();
            if (domain.getEntriesCount() > 0)
                type = "enumeration";
            else if (ontologyClass.equals("text") || ontologyClass.equals("password") ||
                ontologyClass.equals("hidden") || ontologyClass.equals("file") ||
                ontologyClass.equals("textarea"))
                type = "string";
            else if (ontologyClass.equals("checkbox"))
                type = "boolean";
            else if (ontologyClass.equals("checkbox"))
                type = "boolean";

            e = new Element("AttributeType", def);
            e.setAttribute(new org.jdom.Attribute("name", elementName));
            if (type != null)
                e.setAttribute(new org.jdom.Attribute("type", type, d));
            if (domain.getEntriesCount() > 0)
            {
                StringBuffer enumeration = new StringBuffer();
                for (int i = 0; i < domain.getEntriesCount(); i++)
                {
                    Object de = domain.getEntryAt(i).getEntry();
                    if (de instanceof Term)
                    {
                        Term t = (Term) de;
                        String entryValue = (String) t.getValue();
                        String entryName = t.getName();
                        String enumerationName = BizTalkUtilities.makeValidBizTalkEnumerationEntry(
                            entryName, entryValue);
                        if (enumerationName.length() == 0)
                            continue;
                        enumeration.append(enumerationName + " ");
                    }
                }
                String enumr = enumeration.toString().trim();
                e.setAttribute(new org.jdom.Attribute("values", enumr.length() > 0 ? enumr : "_", d));
            }
            e.addContent(new Element("FieldInfo", b));
            parent.addContent(e);

            Element attribute = new Element("attribute", def);
            attribute.setAttribute(new org.jdom.Attribute("type", elementName));
            attribute.setAttribute(new org.jdom.Attribute("required", "no"));
            parent.addContent(attribute);
        }
        return e;
    }

    /**
     * Get the term from an XML element
     * 
     * @param termElement the XML {@link Element}
     * @return a {@link Term}
     */
    public static Term getTermFromXML(Element termElement, Ontology model)
    {
        Term term = new Term(termElement.getAttributeValue("id"),
            termElement.getAttributeValue("name"), termElement.getAttributeValue("value"));
        term.superClass = model.findClass(termElement.getAttributeValue("class"));
        if (term.superClass != null)
            term.superClass.instances.add(term);
        term.ontology = model;
        term.setDomain(Domain.getDomainFromXML(termElement.getChild("domain"), model));

        java.util.List<?> attributeElements = termElement.getChild("attributes").getChildren();
        for (Iterator<?> i = attributeElements.iterator(); i.hasNext();)
            term.addAttribute(Attribute.getAttributeFromXML((Element) i.next(), model));

        java.util.List<?> axiomElements = termElement.getChild("axioms").getChildren();
        for (Iterator<?> i = axiomElements.iterator(); i.hasNext();)
            term.addAxiom(Axiom.getAxiomFromXML((Element) i.next(), model));

        java.util.List<?> subtermsElements = termElement.getChild("subterms").getChildren();
        Term prevSubTerm = null;
        for (Iterator<?> i = subtermsElements.iterator(); i.hasNext();)
        {
            Term subTerm = Term.getTermFromXML((Element) i.next(), model);
            if (!term.terms.contains(subTerm))
            {
                subTerm.parent = term;
                subTerm.ontology = model;
                term.terms.add(subTerm);
                if (prevSubTerm != null)
                {
                    prevSubTerm.setSucceed(subTerm);
                    subTerm.setPrecede(prevSubTerm);
                }
                prevSubTerm = subTerm;
            }
        }

        return term;
    }

    public void solveRelationshipsFromXML(Element termElement)
    {
        java.util.List<?> relationshipsElements = termElement.getChild("relationships")
            .getChildren();
        for (Iterator<?> i = relationshipsElements.iterator(); i.hasNext();)
            addRelationship(Relationship.getRelationshipFromXML((Element) i.next(), this, ontology));
        java.util.List<?> subtermsElements = termElement.getChild("subterms").getChildren();
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term subTerm = (Term) i.next();
            for (Iterator<?> j = subtermsElements.iterator(); j.hasNext();)
            {
                Element subTermElement = (Element) j.next();
                if (subTermElement.getAttributeValue("name").equals(subTerm.getName()))
                {
                    subTerm.solveRelationshipsFromXML(subTermElement);
                    break;
                }
            }
        }
    }

    protected static Term t;

    public static Term createTermDialog(Ontology model)
    {
        final com.modica.gui.TextField txtTermName = new com.modica.gui.TextField(15);
        final com.modica.gui.TextField txtTermValue = new com.modica.gui.TextField(15);
        final com.modica.gui.ComboBox cmbTermDomain = new com.modica.gui.ComboBox(
            Domain.getPredefinedDomains());
        cmbTermDomain.setEditable(true);
        cmbTermDomain.setSelectedIndex(-1);
        cmbTermDomain.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer()
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

        Vector<Object> classes = model.getClasses();
        classes.add(0, "               ");
        final com.modica.gui.ComboBox cmbTermClass = new com.modica.gui.ComboBox(classes);
        cmbTermClass.setSelectedIndex(0);
        cmbTermClass.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer()
        {
            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof OntologyClass)
                    setIcon(ApplicationUtilities.getImage("class.gif"));
                return this;
            }
        });

        final JDialog dialog = new JDialog((JFrame) null,
            ApplicationUtilities.getResourceString("ontology.term.dialog.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(ApplicationUtilities
            .getIntProperty("ontology.term.dialog.width"), ApplicationUtilities
            .getIntProperty("ontology.term.dialog.height")));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.term.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(txtTermName.getText().trim().length() > 0);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (cmbTermClass.getSelectedIndex() > 0)
                    t = new Term((OntologyClass) cmbTermClass.getSelectedItem());
                else
                    t = new Term();
                t.name = txtTermName.getText();
                t.value = txtTermValue.getText();
                String domain = cmbTermDomain.getText();
                if (domain != null && domain.trim().length() > 0)
                    t.domain.setName(domain);
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.term.dialog.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                t = null;
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(ApplicationUtilities.getResourceString("ontology.term"),
                ApplicationUtilities.getImage("term.gif"), JLabel.LEFT);
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
                    .getResourceString("ontology.term.dialog.explanation")), gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(ApplicationUtilities.getResourceString("ontology.term.name") +
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
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel value = new JLabel(
                ApplicationUtilities.getResourceString("ontology.term.value") + ":");
            center.add(value, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtTermValue, gbcl);
        }

        {// Domain
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 4;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel domain = new JLabel(ApplicationUtilities.getResourceString("ontology.domain") +
                ":");
            center.add(domain, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(cmbTermDomain, gbcl);
        }

        {// Class
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 5;
            gbcl.insets = new Insets(0, 0, 0, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel clazz = new JLabel(ApplicationUtilities.getResourceString("ontology.class") +
                ":");
            center.add(clazz, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(cmbTermClass, gbcl);
        }

        {// Separator
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 6;
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
                        txtTermName.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                t = null;
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);// show();
        return t;
    }

    // public DefaultGraphCell buildGraphHierarchy(DefaultPort parentPort,ArrayList cells,Hashtable
    // attributes,ConnectionSet cs)
    // {
    // if(isInstanceOf("hidden") && !GraphUtilities.isShowHiddenElements()) return null;
    // DefaultGraphCell termVertex=new DefaultGraphCell(this);
    // cells.add(termVertex);
    // Map termMap=GraphUtilities.createDefaultAttributes();
    // GraphConstants.setIcon(termMap, ApplicationUtilities.getImage("term.gif"));
    // attributes.put(termVertex,termMap);
    //
    // if(parentPort!=null) // Connect parent with this child
    // {
    // DefaultPort toParentPort=new DefaultPort("toParent");
    // termVertex.add(toParentPort);
    // DefaultEdge edge=new DefaultEdge();
    // cs.connect(edge,parentPort, true);
    // cs.connect(edge,toParentPort,false);
    // cells.add(edge);
    // }
    //
    // if(!terms.isEmpty()) // It has children
    // {
    // DefaultPort toChildPort=new OrderedDefaultPort("toChild");
    // termVertex.add(toChildPort);
    // for(Iterator i=terms.iterator();i.hasNext();)
    // {
    // Term term=(Term)i.next();
    // DefaultGraphCell nextVertex=term.buildGraphHierarchy(toChildPort,cells,attributes,cs);
    // }
    // }

    public DefaultGraphCell buildGraphHierarchy(DefaultPort parentPort,
        ArrayList<DefaultGraphCell> cells, Hashtable<DefaultGraphCell, Map<?, ?>> attributes,
        ConnectionSet cs)
    {
        if (isInstanceOf("hidden") && !GraphUtilities.isShowHiddenElements())
            return null;
        DefaultGraphCell termVertex = new DefaultGraphCell(this);
        cells.add(termVertex);
        Map<?, ?> termMap = GraphUtilities.createDefaultAttributes();
        GraphConstants.setIcon(termMap, ApplicationUtilities.getImage("term.gif"));
        attributes.put(termVertex, termMap);

        if (parentPort != null) // Connect parent with this child
        {
            DefaultPort toParentPort = new DefaultPort("toParent");
            termVertex.add(toParentPort);
            DefaultEdge edge = new DefaultEdge();
            cs.connect(edge, parentPort, true);
            cs.connect(edge, toParentPort, false);
            cells.add(edge);
        }

        if (!terms.isEmpty()) // It has children
        {
            DefaultPort toChildPort = new OrderedDefaultPort("toChild");
            termVertex.add(toChildPort);
            for (Iterator<Term> i = terms.iterator(); i.hasNext();)
            {
                Term term = (Term) i.next();
                /* DefaultGraphCell nextVertex= */term.buildGraphHierarchy(toChildPort, cells,
                    attributes, cs);
            }
        }

        return termVertex;
    }

    //
    // return termVertex;
    // }

    // public void buildPrecedenceRelationships(ArrayList cells,Hashtable attributes,ConnectionSet
    // cs)
    // {
    // DefaultGraphCell vertex=GraphUtilities.getCellWithObject(cells,this);
    // if(vertex==null) return;
    // if(precede!=null)
    // {
    // DefaultGraphCell prevVertex=null;
    // Term auxPrecede=this;
    // do
    // {
    // auxPrecede=auxPrecede.precede;
    // prevVertex=GraphUtilities.getCellWithObject(cells,auxPrecede);
    // } while(prevVertex==null && auxPrecede!=null);
    // if(prevVertex!=null)
    // {
    // DefaultPort prevPort=new DefaultPort("precedes");
    // prevVertex.add(prevPort);
    // DefaultPort nextPort=new DefaultPort("isPreceded");
    // vertex.add(nextPort);
    // DefaultEdge edge=new DefaultEdge();
    // cs.connect(edge,prevPort,true);
    // cs.connect(edge,nextPort,false);
    // cells.add(edge);
    // }
    // }
    // if(succeed!=null)
    // {
    // DefaultGraphCell nextVertex=null;
    // Term auxSucceed=this;
    // do
    // {
    // auxSucceed=auxSucceed.succeed;
    // nextVertex=GraphUtilities.getCellWithObject(cells,auxSucceed);
    // } while (nextVertex==null && auxSucceed!=null);
    // if(nextVertex!=null)
    // {
    // DefaultPort prevPort=new DefaultPort("isSucceeded");
    // vertex.add(prevPort);
    // DefaultPort nextPort=new DefaultPort("succeeds");
    // vertex.add(nextPort);
    // DefaultEdge edge=new DefaultEdge();
    // cs.connect(edge,prevPort,false);
    // cs.connect(edge,nextPort,true);
    // cells.add(edge);
    // }
    // }
    //
    // for(Iterator i=terms.iterator();i.hasNext();)
    // {
    // Term term=(Term)i.next();
    // term.buildPrecedenceRelationships(cells,attributes,cs);
    // }
    // }

    public void buildPrecedenceRelationships(ArrayList<DefaultGraphCell> cells,
        Hashtable<?, ?> attributes, ConnectionSet cs)
    {
        DefaultGraphCell vertex = GraphUtilities.getCellWithObject(cells, this);
        if (vertex == null)
            return;
        if (precede != null)
        {
            DefaultGraphCell prevVertex = null;
            Term auxPrecede = this;
            do
            {
                auxPrecede = auxPrecede.precede;
                prevVertex = GraphUtilities.getCellWithObject(cells, auxPrecede);
            }
            while (prevVertex == null && auxPrecede != null);
            if (prevVertex != null)
            {
                DefaultPort prevPort = new DefaultPort("precedes");
                prevVertex.add(prevPort);
                DefaultPort nextPort = new DefaultPort("isPreceded");
                vertex.add(nextPort);
                DefaultEdge edge = new DefaultEdge();
                cs.connect(edge, prevPort, true);
                cs.connect(edge, nextPort, false);
                cells.add(edge);
            }
        }
        if (succeed != null)
        {
            DefaultGraphCell nextVertex = null;
            Term auxSucceed = this;
            do
            {
                auxSucceed = auxSucceed.succeed;
                nextVertex = GraphUtilities.getCellWithObject(cells, auxSucceed);
            }
            while (nextVertex == null && auxSucceed != null);
            if (nextVertex != null)
            {
                DefaultPort prevPort = new DefaultPort("isSucceeded");
                vertex.add(prevPort);
                DefaultPort nextPort = new DefaultPort("succeeds");
                vertex.add(nextPort);
                DefaultEdge edge = new DefaultEdge();
                cs.connect(edge, prevPort, false);
                cs.connect(edge, nextPort, true);
                cells.add(edge);
            }
        }

        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            term.buildPrecedenceRelationships(cells, attributes, cs);
        }
    }

    /**
     * Get all the children of the term
     * 
     * @return a list of all the child {@link Term}
     */
    public ArrayList<Term> getAllChildren()
    {
        ArrayList<Term> children = new ArrayList<Term>();
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term child = (Term) i.next();
            children.add(child);
            children.addAll(child.getAllChildren());
        }
        return children;
    }

    /**
     * Get all the precedes of the term
     * 
     * @return a list of all the precedes {@link Term}
     */
    public ArrayList<Term> getAllPrecedes()
    {
        ArrayList<Term> precedes = new ArrayList<Term>();
        if (precede != null)
        {
            precedes.add(precede);
            precedes.addAll(precede.getAllChildren());
            precedes.addAll(precede.getAllPrecedes());
        }
        else if (parent != null)
        {
            precedes.add(parent);
            precedes.addAll(parent.getAllPrecedes());
        }

        return precedes;
    }

    /**
     * Check is a term precedes the current term
     * 
     * @param t the {@link Term} to check
     * @return <code>true</code> if it precedes
     */
    public boolean precedes(Term t)
    {
        if (this.equals(t))
            return false;
        ArrayList<Term> precedes = getAllPrecedes();
        if (precedes.contains(t))
            return false;
        else
            return true;
    }

    /**
     * Check is a term succeeds the current term
     * 
     * @param t the {@link Term} to check
     * @return <code>true</code> if it succeeds
     */
    public boolean succeeds(Term t)
    {
        if (this.equals(t))
            return false;
        else
            return !precedes(t);
    }

    public String getInputFormName()
    {
        Term parentTerm = parent;
        while (parentTerm != null && !parentTerm.isInstanceOf("form"))
            parentTerm = parentTerm.parent;
        if (parentTerm != null && parentTerm.isInstanceOf("form"))
            return parentTerm.name;
        else
            return null;
    }

    public String getInputPageName()
    {
        Term parentTerm = parent;
        while (parentTerm != null && !parentTerm.isInstanceOf("page"))
            parentTerm = parentTerm.parent;
        if (parentTerm != null && parentTerm.isInstanceOf("page"))
            return parentTerm.name;
        else
            return null;
    }

    public Element getInputFullNameAsXML()
    {
        Element termElement = new Element("term");
        termElement.setText(name);
        if (isInstanceOf("input") || isInstanceOf("decomposition"))
        {
            String page = getInputPageName();
            String form = getInputFormName();
            termElement.setAttribute("page", page != null ? page : "");
            termElement.setAttribute("form", form != null ? form : "");
            if (superClass != null)
                termElement
                    .setAttribute(
                        "type",
                        superClass.name.equalsIgnoreCase("decomposition") ? (terms.size() > 0 ? ((Term) terms
                            .get(0)).superClass.name : "") : superClass.name);
        }
        termElement.setAttribute("name", (String) getAttributeValue("name"));
        return termElement;
    }
}