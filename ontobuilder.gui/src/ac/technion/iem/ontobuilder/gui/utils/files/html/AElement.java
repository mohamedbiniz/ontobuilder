package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.net.URL;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: AElement</p>
 * Extends {@link HTMLElement}
 */
public class AElement extends HTMLElement
{
    private String name;
    private String target;
    private String description;
    private URL url;

    public AElement(String description, URL url)
    {
        super(HTMLElement.A);
        this.description = description;
        this.url = url;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public URL getURL()
    {
        return url;
    }

    public void setURL(URL url)
    {
        this.url = url;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getTarget()
    {
        return target;
    }

    public String toString()
    {
        return description + " : " + (url != null ? url.toExternalForm() : "");
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
                ApplicationUtilities.getResourceString("html.a.description"), description
            },
            {
                ApplicationUtilities.getResourceString("html.a.url"), url
            },
            {
                ApplicationUtilities.getResourceString("html.a.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.a.target"), target
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 4, data));
    }
}