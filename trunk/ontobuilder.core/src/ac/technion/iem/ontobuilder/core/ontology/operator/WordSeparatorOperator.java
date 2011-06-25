package ac.technion.iem.ontobuilder.core.ontology.operator;

import com.modica.util.StringUtilities;

/**
 * <p>Title: WordSeparatorOperator</p>
 * Implements {@link StringOperator}
 */
public class WordSeparatorOperator implements StringOperator
{
    public String transformString(String text)
    {
        return StringUtilities.separateCapitalizedWords(text);
    }
}