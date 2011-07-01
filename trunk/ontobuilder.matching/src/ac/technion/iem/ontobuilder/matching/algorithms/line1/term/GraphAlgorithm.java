package ac.technion.iem.ontobuilder.matching.algorithms.line1.term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

import com.modica.application.ApplicationUtilities;
import com.modica.application.PropertiesTableModel;
import com.modica.ontobuilder.ApplicationParameters;
import com.modica.ontology.OntologyUtilities;
import com.modica.ontology.domain.GuessedDomain;
import com.modica.ontology.match.MatchInformation;
import com.modica.util.StringUtilities;

/**
 * <p>Title: GraphAlgorithm</p>
 * Extends a {@link TermValueAlgorithm}
 */
public class GraphAlgorithm extends TermValueAlgorithm
{
    protected double siblingsWeight;
    protected double parentsWeight;
    protected double graphWeight;

    protected boolean combine = true;
    protected boolean enhance = true;

    /**
     * Make a copy of the algorithm instance
     */
    public Algorithm makeCopy()
    {
        GraphAlgorithm algorithm = new GraphAlgorithm();
        algorithm.pluginName = pluginName;
        algorithm.mode = mode;
        algorithm.thesaurus = thesaurus;
        algorithm.termPreprocessor = termPreprocessor;
        algorithm.threshold = threshold;
        algorithm.effectiveness = effectiveness;
        algorithm.combine = combine;
        algorithm.enhance = enhance;
        algorithm.siblingsWeight = siblingsWeight;
        algorithm.parentsWeight = parentsWeight;
        algorithm.graphWeight = graphWeight;
        return algorithm;
    }

    /**
     * Constructs a default GraphAlgorithm
     */
    public GraphAlgorithm()
    {
        super();
    }

    /**
     * Set the siblings weight
     * 
     * @param siblingsWeight the siblings weight
     */
    public void setSiblingsWeight(double siblingsWeight)
    {
        this.siblingsWeight = siblingsWeight;
    }

    /**
     * Get the siblings weight
     * 
     * @return the siblings weight
     */
    public double getSiblingsWeight()
    {
        return siblingsWeight;
    }

    /**
     * Set the parent weight
     * 
     * @param parentsWeight the parent weight
     */
    public void setParentsWeight(double parentsWeight)
    {
        this.parentsWeight = parentsWeight;
    }

    /**
     * Get the parent weight
     * 
     * @return the parent weight
     */
    public double getParentsWeight()
    {
        return parentsWeight;
    }

    /**
     * Set to combine the matrices
     * 
     * @param b <code>true</code> if is combined
     */
    public void setCombine(boolean b)
    {
        combine = b;
    }

    /**
     * Check whether is combined
     * 
     * @return <code>true</code> if is combined
     */
    public boolean isCombine()
    {
        return combine;
    }

    /**
     * Set to enhance
     * 
     * @param b <code>true</code> if is enhanced
     */
    public void setEnhance(boolean b)
    {
        enhance = b;
    }

    /**
     * Check whether is enhanced
     * 
     * @return <code>true</code> if is enhanced
     */
    public boolean isEnhance()
    {
        return enhance;
    }

    /**
     * Get the name
     * 
     * @return the name
     */
    public String getName()
    {
        return ApplicationUtilities.getResourceString("algorithm.graph");
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription()
    {
        return ApplicationUtilities.getResourceString("algorithm.graph.description");
    }

    /**
     * Configure the algorithm parameters
     * 
     * @param element the {@link Element} with the parameters to configure
     */
    public void configure(Element element)
    {
        super.configure(element);
        Element parametersElement = element.getChild("parameters");
        if (parametersElement == null)
            return;
        List<?> parametersList = parametersElement.getChildren("parameter");
        for (Iterator<?> i = parametersList.iterator(); i.hasNext();)
        {
            Element parameterElement = (Element) i.next();
            String name = parameterElement.getChild("name").getText();
            if (name.equals("graphWeight") || name.equals("parentsWeight") ||
                name.equals("siblingsWeight"))
            {
                double value = Double.parseDouble(parameterElement.getChild("value").getText());
                if (name.equals("parentsWeight"))
                    parentsWeight = value;
                else if (name.equals("siblingsWeight"))
                    siblingsWeight = value;
                else if (name.equals("graphWeight"))
                    graphWeight = value;
            }
        }
    }

    public void updateProperties(HashMap<?, ?> properties)
    {
        super.updateProperties(properties);
        graphWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.combined.graphWeight")).toString())
            .doubleValue();
        siblingsWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.graph.siblingsWeight")).toString())
            .doubleValue();
        parentsWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.graph.parentsWeight")).toString())
            .doubleValue();
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
                ApplicationUtilities.getResourceString("algorithm.combined.termWeight"),
                new Double(termWeight)
            },
            {
                ApplicationUtilities.getResourceString("algorithm.combined.valueWeight"),
                new Double(valueWeight)
            },
            {
                ApplicationUtilities.getResourceString("algorithm.combined.graphWeight"),
                new Double(graphWeight)
            },
            {
                ApplicationUtilities.getResourceString("algorithm.graph.parentsWeight"),
                new Double(parentsWeight)
            },
            {
                ApplicationUtilities.getResourceString("algorithm.graph.siblingsWeight"),
                new Double(siblingsWeight)
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 5, data));
        return properties;
    }

    /**
     * Get the terms to match
     * 
     * @param targetOntology the target {@link Ontology}
     * @param candidateOntology the candidate {@link Ontology}
     */
    protected void getTermsToMatch(Ontology targetOntology, Ontology candidateOntology)
    {

        if (!targetOntology.getModel().isLight())
        {
            originalTargetTerms = OntologyUtilities.getTermsOfClass(targetOntology, "input");
            originalTargetTerms = OntologyUtilities.filterTermListRemovingTermsOfClass(
                originalTargetTerms, "hidden");
            originalTargetTerms.addAll(OntologyUtilities.getTermsOfClass(targetOntology,
                "composition"));

        }
        else
        {
            originalTargetTerms = new ArrayList<Term>(targetOntology.getModel().getTerms());
        }

        if (!candidateOntology.getModel().isLight())
        {
            originalCandidateTerms = OntologyUtilities.getTermsOfClass(candidateOntology, "input");
            originalCandidateTerms = OntologyUtilities.filterTermListRemovingTermsOfClass(
                originalCandidateTerms, "hidden");
            originalCandidateTerms.addAll(OntologyUtilities.getTermsOfClass(candidateOntology,
                "composition"));
        }
        else
        {
            originalCandidateTerms = new ArrayList<Term>(candidateOntology.getModel().getTerms());
        }

    }

    /**
     * Performs a match between the target and candidate ontologies
     * 
     * @param targetOntology the target {@link Ontology}
     * @param candidateOntology the candidate {@link Ontology}
     */
    public MatchInformation match(Ontology targetOntology, Ontology candidateOntology)
    {
        if (termAlgorithm == null)
        {
            termAlgorithm = (TermAlgorithm) AlgorithmUtilities.getAlgorithm(
                ApplicationUtilities.getResourceString("algorithm.term")).makeCopy();
            if (termAlgorithm == null)
                return null;
        }
        if (valueAlgorithm == null)
        {
            valueAlgorithm = (ValueAlgorithm) AlgorithmUtilities.getAlgorithm(
                ApplicationUtilities.getResourceString("algorithm.value")).makeCopy();
            if (valueAlgorithm == null)
                return null;
        }

        // Get Terms
        getTermsToMatch(targetOntology, candidateOntology);

        // Preprocess
        preprocess();

        // old version
        // double
        // termMatchMatrix[][]=OntologyUtilities.getMatchMatrix(originalTargetTerms,targetTerms,originalCandidateTerms,candidateTerms,termAlgorithm);
        // double
        // valueMatchMatrix[][]=OntologyUtilities.getMatchMatrix(originalTargetTerms,targetTerms,originalCandidateTerms,candidateTerms,valueAlgorithm);
        // double termValueMatchMatrix[][]=combineMatrices(termMatchMatrix,valueMatchMatrix);

        // added by haggai 6/12/03
        MatchMatrix termMatchMM = OntologyUtilities.createMatchMatrix(originalTargetTerms,
            targetTerms, originalCandidateTerms, candidateTerms, termAlgorithm);
        MatchMatrix valueMatchMM = OntologyUtilities.createMatchMatrix(originalTargetTerms,
            targetTerms, originalCandidateTerms, candidateTerms, valueAlgorithm);
        // ***end haggai
        // new version - haggai 6/12/03
        double termMatchMatrix[][] = termMatchMM.transpose();/*
                                                              * OntologyUtilities.getMatchMatrix(
                                                              * originalTargetTerms
                                                              * ,targetTerms,originalCandidateTerms
                                                              * ,candidateTerms,termAlgorithm);
                                                              */
        double valueMatchMatrix[][] = valueMatchMM.transpose();/*
                                                                * OntologyUtilities.getMatchMatrix(
                                                                * originalTargetTerms
                                                                * ,targetTerms,originalCandidateTerms
                                                                * ,candidateTerms,valueAlgorithm);
                                                                */
        // end new version

        double termValueMatchMatrix[][] = combineMatrices(termMatchMatrix, valueMatchMatrix);

        GraphMatch graphMatch = new GraphMatch(termValueMatchMatrix, originalTargetTerms,
            originalCandidateTerms, targetOntology.getGraph(), candidateOntology.getGraph());
        graphMatch.setParentsWeight(parentsWeight);
        graphMatch.setSiblingsWeight(siblingsWeight);
        graphMatch.setThreshold(threshold);
        double graphMatchMatrix[][] = graphMatch.match();

        double matchMatrix[][];
        if (combine)
            matchMatrix = combineMatrices(termMatchMatrix, valueMatchMatrix, termValueMatchMatrix,
                graphMatchMatrix);
        else
            matchMatrix = graphMatchMatrix;

        // added by haggai 6/12/03
        MatchMatrix graphMM = new MatchMatrix(originalCandidateTerms.size(),
            originalTargetTerms.size(), originalCandidateTerms, originalTargetTerms);
        graphMM.setMatchConfidenceMatrix(matchMatrix);
        matchInformation = buildMatchInformation(matchMatrix);
        matchInformation.setMatrix(graphMM);
        // /end haggai
        matchInformation.setTargetOntologyTermsTotal(originalTargetTerms.size());
        matchInformation.setTargetOntology(targetOntology);
        matchInformation.setCandidateOntologyTermsTotal(originalCandidateTerms.size());
        matchInformation.setCandidateOntology(candidateOntology);
        matchInformation.setAlgorithm(this);
        return matchInformation;
    }

    /**
     * Combine the matrices
     * 
     * @param termMatchMatrix the term match matrix
     * @param valueMatchMatrix the value match matrix
     * @param termValueMatchMatrix the term value matrix
     * @param graphMatchMatrix the graph match matrix
     * @return a combined matrix
     */
    protected double[][] combineMatrices(double termMatchMatrix[][], double valueMatchMatrix[][],
        double termValueMatchMatrix[][], double graphMatchMatrix[][])
    {
        double matchMatrix[][] = new double[candidateTerms.size()][targetTerms.size()];

        for (int j = 0; j < candidateTerms.size(); j++)
        {
            Term candidateTerm = (Term) candidateTerms.get(j);
            for (int i = 0; i < targetTerms.size(); i++)
            {
                Term targetTerm = (Term) targetTerms.get(i);

                double termEffectiveness = termMatchMatrix[j][i];
                double valueEffectiveness = valueMatchMatrix[j][i];
                double graphEffectiveness = graphMatchMatrix[j][i];

                boolean print = false;// ((String)targetTerm.getAttributeValue("name")).equalsIgnoreCase("putime")
                                      // &&
                                      // ((String)candidateTerm.getAttributeValue("name")).equalsIgnoreCase("pkday");

                if (print)
                    System.out.println("****************************************************");
                if (print)
                    System.out.println("Comparing " + targetTerm + " vs. " + candidateTerm);

                double termWeightAux = termWeight;
                double valueWeightAux = valueWeight;
                double graphWeightAux = graphWeight;

                if (targetTerm.getDomain().getEntriesCount() == 0 &&
                    candidateTerm.getDomain().getEntriesCount() == 0) // this means that the value
                                                                      // for valueEffectiveness is
                                                                      // just domain similarity
                {
                    if (valueEffectiveness > 0)
                        termEffectiveness = valueEffectiveness + (1 - valueEffectiveness) *
                            termEffectiveness;
                    else if (termEffectiveness > 0)
                        termEffectiveness -= GuessedDomain.DOMAIN_PENALTY;
                    valueWeightAux = 0;
                }

                if (termEffectiveness < threshold)
                    termWeightAux = 0;
                if (valueEffectiveness < threshold)
                    valueWeightAux = 0;
                if (graphEffectiveness < threshold)
                    graphWeightAux = 0;

                /*
                 * if((termWeightAux+valueWeightAux+graphWeightAux)==0) matchMatrix[j][i]=0; else
                 * matchMatrix[j][i]=(termEffectiveness*termWeightAux +
                 * valueEffectiveness*valueWeightAux +
                 * graphEffectiveness*graphWeightAux)/(termWeightAux+valueWeightAux+graphWeightAux);
                 */

                if ((termWeight + valueWeightAux + graphWeight) == 0)
                    matchMatrix[j][i] = 0;
                else
                    matchMatrix[j][i] = (termEffectiveness * termWeightAux + valueEffectiveness *
                        valueWeightAux + graphEffectiveness * graphWeightAux) /
                        (termWeight + valueWeightAux + graphWeight);

                // Enhancing
                if (enhance && matchMatrix[j][i] < threshold &&
                    matchMatrix[j][i] < termValueMatchMatrix[j][i])
                    matchMatrix[j][i] = termValueMatchMatrix[j][i];

                if (print)
                {
                    System.out.println("\tTerm effectivity: " + termEffectiveness);
                    System.out.println("\tValue effectivity: " + valueEffectiveness);
                    System.out.println("\tGraph effectivity: " + graphEffectiveness);
                    System.out.println("\tOverall effectivity: " + matchMatrix[j][i]);
                    System.out.println("****************************************************");
                }
            }
        }

        if (ApplicationParameters.verbose)
        {
            String columnNames[] =
            {
                ApplicationUtilities.getResourceString("ontology.match.candidate"),
                ApplicationUtilities.getResourceString("ontology.match.target"),
                ApplicationUtilities.getResourceString("ontology.match.effectiveness")
            };
            Object matchTable[][] = new Object[targetTerms.size() * candidateTerms.size()][3];

            for (int j = 0; j < candidateTerms.size(); j++)
            {
                Term candidateTerm = (Term) candidateTerms.get(j);
                for (int i = 0; i < targetTerms.size(); i++)
                {
                    Term targetTerm = (Term) targetTerms.get(i);
                    int index = j * targetTerms.size() + i;
                    matchTable[index][0] = candidateTerm;
                    matchTable[index][1] = targetTerm;
                    matchTable[index][2] = new Double(matchMatrix[j][i]);
                }
            }

            System.out.println(ApplicationUtilities.getResourceString("algorithm.term") + " + " +
                ApplicationUtilities.getResourceString("algorithm.value") + " + " +
                ApplicationUtilities.getResourceString("algorithm.graph"));
            System.out.println();
            System.out.println(StringUtilities.getJTableStringRepresentation(new JTable(
                new PropertiesTableModel(columnNames, 5, matchTable))));
        }

        return matchMatrix;
    }
}