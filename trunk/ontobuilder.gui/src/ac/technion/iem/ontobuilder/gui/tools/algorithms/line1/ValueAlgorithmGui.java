package ac.technion.iem.ontobuilder.gui.tools.algorithms.line1;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.ValueAlgorithm;

/**
 * <p>
 * Title: ValueAlgorithmGui
 * </p>
  * <p>Description: Implements the methods of the ValueAlgorithm used by the GUI</p>
 */
public class ValueAlgorithmGui extends TermAlgorithmGui
{
    ValueAlgorithm _valueAlgorithm;
    
    public ValueAlgorithmGui(AbstractAlgorithm abstractAlgorithm)
    {
        _valueAlgorithm = (ValueAlgorithm)abstractAlgorithm;
    }
    
    public JTable getProperties()
    {
        return super.getProperties();
    }

}
