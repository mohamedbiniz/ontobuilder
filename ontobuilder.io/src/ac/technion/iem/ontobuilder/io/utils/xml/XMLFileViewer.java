package ac.technion.iem.ontobuilder.io.utils.xml;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import com.modica.util.FileUtilities;

/**
 * <p>Title: XMLFileViewer</p>
 * Extends {@link FileView}
 */
public class XMLFileViewer extends FileView
{
    public Icon getIcon(File f)
    {
        String extension = FileUtilities.getFileExtension(f);
        if (extension != null && extension.equals("xml"))
            return PropertiesHandler.getImage("xmlfile.gif");
        return null;
    }
}
