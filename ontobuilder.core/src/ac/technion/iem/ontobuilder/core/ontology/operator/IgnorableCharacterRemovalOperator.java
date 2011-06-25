package ac.technion.iem.ontobuilder.core.ontology.operator;

import com.modica.util.StringUtilities;

/**
 * <p>Title: IgnorableCharacterRemovalOperator</p>
 * Implements {@link StringOperator}
 */
public class IgnorableCharacterRemovalOperator implements StringOperator
{
    public String transformString(String text)
    {
        return StringUtilities.removeIgnorableCharacters(text);
    }
}