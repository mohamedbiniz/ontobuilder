package ac.technion.iem.ontobuilder.io.utils.xml;

import java.io.File;

import com.modica.text.TextFilePreviewer;
import com.modica.util.FileUtilities;

/**
 * <p>Title: XMLFilePreviewer</p>
 * Extends {@link TextFilePreviewer}
 */
public class XMLFilePreviewer extends TextFilePreviewer
{
    public boolean isFileSupported(File f)
    {
        String extension = FileUtilities.getFileExtension(f);
        return extension != null && extension.equals("xml");
    }
}
