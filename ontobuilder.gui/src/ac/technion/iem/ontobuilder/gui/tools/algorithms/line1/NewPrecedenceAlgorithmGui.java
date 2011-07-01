package ac.technion.iem.ontobuilder.gui.tools.algorithms.line1;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence.NewPrecedenceAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence.PrecedenceAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.CombinedAlgorithm;

/**
 * <p>
 * Title: NewPrecedenceAlgorithmGui
 * </p>
  * <p>Description: Implements the methods of the NewPrecedenceAlgorithm used by the GUI</p>
 */
public class NewPrecedenceAlgorithmGui
{
    NewPrecedenceAlgorithm _newPrecedenceAlgorithm;
    
    public NewPrecedenceAlgorithmGui(AbstractAlgorithm abstractAlgorithm)
    {
        _newPrecedenceAlgorithm = (NewPrecedenceAlgorithm)abstractAlgorithm;
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
                new Double(_newPrecedenceAlgorithm.getTermWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.combined.valueWeight"),
                new Double(_newPrecedenceAlgorithm.getValueWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.combined.precedenceWeight"),
                new Double(_newPrecedenceAlgorithm.getPivotWeight()) //should be pivot weight
            },
            {
                ApplicationUtilities.getResourceString("algorithm.precedence.precedeWeight"),
                new Double(precedeWeight)
            },
            {
                ApplicationUtilities.getResourceString("algorithm.precedence.succeedWeight"),
                new Double(succeedWeight)
            },
            {
                "use one to one match", new Boolean(_newPrecedenceAlgorithm.isOneToOneMatch())
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 5, data));
        return properties;
    }
}
