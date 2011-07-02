package ac.technion.iem.ontobuilder.core.util.network;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * <p>Title: NetworkEntityResolver</p>
 * Implements {@link EntityResolver}
 */
public class NetworkEntityResolver implements EntityResolver
{
    /**
     * @deprecated
     */
    public InputSource resolveEntity(String publicId, String systemId) throws IOException
    {
        // InputStream entityStream=getClass().getResourceAsStream("/" + systemId);
        // return new InputSource(entityStream);
        String filtered;
        if (systemId.indexOf("file:/") != -1)
        {
            filtered = systemId.substring(6, systemId.length());

        }
        else
        {
            filtered = systemId;
        }
        InputStream entityStream = new FileInputStream(filtered);
        return new InputSource(entityStream);
    }
}
