package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.util.HashMap;

import javax.swing.JTable;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.thesaurus.Thesaurus;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermPreprocessor;

import com.modica.ontology.match.MatchInformation;

/**
 * <p>Title: interface Algorithm</p>
 */
public interface Algorithm
{
    public static final String MATCH_THRESHOLD_PROPERTY = "matchThreshold";

    public MatchInformation match(Ontology targetOntology, Ontology candidateOntology);

    public void setMode(int mode);

    public int getMode();

    public String getPluginName();

    public void setPluginName(String pluginName);

    public String getName();

    public String getDescription();

    public void configure(Element element);

    public void updateProperties(HashMap<?, ?> properties);

    public JTable getProperties();

    public void setThreshold(double threshold);

    public double getThreshold();

    public boolean usesThesaurus();

    public void setThesaurus(Thesaurus thesaurus);

    public Thesaurus getThesaurus();

    public void setTermPreprocessor(TermPreprocessor termPreprocessor);

    public TermPreprocessor getTermPreprocessor();

    public boolean implementsEffectiveness();

    public double getEffectiveness();

    public Algorithm makeCopy();
}
