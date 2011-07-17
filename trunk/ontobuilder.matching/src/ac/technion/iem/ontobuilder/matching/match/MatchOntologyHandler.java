package ac.technion.iem.ontobuilder.matching.match;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;

/**
 * <p>Title: MatchOntologyHandler</p>
 * <p>Description: Handles a match between two ontologies without the GUI the GUI</p>
 */
public class MatchOntologyHandler
{
    /**
     * Match two ontologies according to an algorithm
     * 
     * @param ontology the {@link OntologyGui}
     * @param algorithm the {@link Algorithm}
     * @return MatchInformation
     */
    public static MatchInformation match(Ontology targetOntology, Ontology candOntology, Algorithm algorithm)
    {
        return algorithm.match(targetOntology, candOntology);
    }
}
