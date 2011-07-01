package ac.technion.iem.ontobuilder.matching.algorithms.common;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMatchMatrix;

/**
 * <p>Title: interface MatchAlgorithm</p>
 * <p>Description: Interface to matching algorithms</p>
 */
public interface MatchAlgorithm
{
    public AbstractMatchMatrix match(Ontology o1, Ontology o2);
}