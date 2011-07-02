package ac.technion.iem.ontobuilder.gui.utils.graphs;

import java.util.*;

import ac.technion.iem.ontobuilder.core.ontology.Term;

import com.jgraph.graph.*;

/**
 * <p>Title: CellComparator</p>
 * <p>Description: Class which handles comparison of two cells</p>
 * Implements {@link Comparator}
 */
public class CellComparator implements Comparator<Object>
{
    /**
     * Compares two terms
     */
    public int compare(Object o1, Object o2)
    {
        DefaultGraphCell c1 = (DefaultGraphCell) o1;
        DefaultGraphCell c2 = (DefaultGraphCell) o2;
        Term t1 = (Term) c1.getUserObject();
        Term t2 = (Term) c2.getUserObject();
        if (t1.equals(t2))
            return 0;
        if (t1.precedes(t2))
            return -1;
        else
            return 1;
    }
}
