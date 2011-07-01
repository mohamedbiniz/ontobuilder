package ac.technion.iem.ontobuilder.io.utils.xml;

import com.modica.application.ApplicationUtilities;
import com.modica.util.ExtensionFileFilter;

/**
 * <p>Title: XMLFileFilter</p>
 * Extends {@link ExtensionFileFilter}
 */
public class XMLFileFilter extends ExtensionFileFilter
{
    public boolean isExtensionAcceptable(String extension)
    {
        return extension != null && extension.equals("xml");
    }

    public String getDescription()
    {
        return ApplicationUtilities.getResourceString("file.xmlFilter.description");
    }
}
