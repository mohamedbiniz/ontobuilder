package ac.technion.iem.ontobuilder.io.utils.xml;

import ac.technion.iem.ontobuilder.core.util.properties.PropertiesHandler;

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
        return PropertiesHandler.getResourceString("file.xmlFilter.description");
    }
}
