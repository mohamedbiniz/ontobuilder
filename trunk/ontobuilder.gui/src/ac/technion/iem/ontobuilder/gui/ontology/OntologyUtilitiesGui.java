package ac.technion.iem.ontobuilder.gui.ontology;

import java.util.ArrayList;
import java.util.Iterator;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.match.Mismatch;


/**
 * <p>
 * Title: OntologyUtilitiesGui
 * </p>
  * <p>Description: Implements the methods of the OntologyUtilities used by the GUI</p>
 */
public class OntologyUtilitiesGui
{
    private OntologyUtilities ontologyUtilities;
    
    public OntologyUtilitiesGui(OntologyUtilities ontologyUtilities)
    {
        this.ontologyUtilities = ontologyUtilities;
    }
    
    public OntologyUtilities getonOntologyUtilities()
    {
        return this.ontologyUtilities;
    }
    
    /**
     * Create an ontology from MatchInformation
     * 
     * @param name the ontology name
     * @param matchInformation the {@link MatchInformation} to use
     * @return an {@link Ontology}
     */
    public static Ontology createOntology(String name, MatchInformation matchInformation)
    {
        OntologyGui ontologyGui = new OntologyGui(name);
        Ontology ontology = ontologyGui.getOntology();

        // Create all the classes
        Ontology targetOntology = matchInformation.getTargetOntology();
        Ontology targetModel = targetOntology;
        for (int i = 0; i < targetModel.getClassesCount(); i++)
        {
            OntologyClass targetOntologyClass = targetModel.getClass(i);
            OntologyClass ontologyClass = (OntologyClass) targetOntologyClass.clone();
            OntologyUtilities.buildClassHierarchy(ontologyClass, targetOntologyClass);
            ontologyClass.setOntology(ontology);
            ontology.addClass(ontologyClass);
        }

        ArrayList<?> matches = matchInformation.getMatches();
        ArrayList<Term> matchesToInclude = new ArrayList<Term>();
        for (Iterator<?> i = matches.iterator(); i.hasNext();)
        {
            Match match = (Match) i.next();
            matchesToInclude.add(match.getTargetTerm());
        }
        for (Iterator<Term> i = matchesToInclude.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            OntologyUtilities.addTermToOntology(term, ontology, matchesToInclude);
        }

        ArrayList<?> mismatchesTarget = matchInformation.getMismatchesTargetOntology();
        ArrayList<Term> mismatchesTargetToInclude = new ArrayList<Term>();
        for (Iterator<?> i = mismatchesTarget.iterator(); i.hasNext();)
        {
            Mismatch mismatch = (Mismatch) i.next();
            if (mismatch.isSelected())
                mismatchesTargetToInclude.add(mismatch.getTerm());
        }
        for (Iterator<Term> i = mismatchesTargetToInclude.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            OntologyUtilities.addTermToOntology(term, ontology, mismatchesTargetToInclude);
        }

        ArrayList<?> mismatchesCandidate = matchInformation.getMismatchesCandidateOntology();
        ArrayList<Term> mismatchesCandidateToInclude = new ArrayList<Term>();
        for (Iterator<?> i = mismatchesCandidate.iterator(); i.hasNext();)
        {
            Mismatch mismatch = (Mismatch) i.next();
            if (mismatch.isSelected())
                mismatchesCandidateToInclude.add(mismatch.getTerm());
        }
        for (Iterator<Term> i = mismatchesCandidateToInclude.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            OntologyUtilities.addTermToOntology(term, ontology, mismatchesCandidateToInclude);
        }

        return ontology;
    }
}
