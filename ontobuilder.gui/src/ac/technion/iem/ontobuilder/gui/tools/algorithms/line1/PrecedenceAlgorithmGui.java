package ac.technion.iem.ontobuilder.gui.tools.algorithms.line1;

import java.util.HashMap;

import javax.annotation.PreDestroy;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence.NewPrecedenceAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence.PrecedenceAlgorithm;

/**
 * <p>
 * Title: PrecedenceAlgorithmGui
 * </p>
  * <p>Description: Implements the methods of the PrecedenceAlgorithm used by the GUI</p>
 */
public class PrecedenceAlgorithmGui
{
    PrecedenceAlgorithm _precedenceAlgorithm;
    
    public PrecedenceAlgorithmGui(AbstractAlgorithm abstractAlgorithm)
    {
        _precedenceAlgorithm = (PrecedenceAlgorithm)abstractAlgorithm;
    }
    
    public void updateProperties(HashMap<?, ?> properties)
    {
        super.updateProperties(properties);
        
        double precedeWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.precedence.precedeWeight"))
            .toString()).doubleValue();
        precedenceAlgorithm.setPrecedeWeight(precedeWeight);
        
        double succeedWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.precedence.succeedWeight"))
            .toString()).doubleValue();
        precedenceAlgorithm.setSucceedWeight(succeedWeight);
        
        double precedenceWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.combined.precedenceWeight"))
            .toString()).doubleValue();
        precedenceAlgorithm.setPrecedenceWeight(precedenceWeight);
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
                ApplicationUtilities.getResourceString("algorithm.combined.precedenceWeight"),
                new Double(precedenceWeight)
            },
            {
                ApplicationUtilities.getResourceString("algorithm.precedence.precedeWeight"),
                new Double(precedeWeight)
            },
            {
                ApplicationUtilities.getResourceString("algorithm.precedence.succeedWeight"),
                new Double(succeedWeight)
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 5, data));
        return properties;
    }
    
}
