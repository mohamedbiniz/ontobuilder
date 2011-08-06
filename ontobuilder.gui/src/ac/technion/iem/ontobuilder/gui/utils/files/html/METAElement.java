package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.core.utils.network.NetworkUtilities;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: METAElement</p>
 * Extends {@link HTMLElement}
 */
public class METAElement extends HTMLElement
{
    private String httpEquiv;
    private String name;
    private String content;
    private int refreshTime;
    private URL url;

    public METAElement()
    {
        super(HTMLElement.META);
    }

    public void setHTTPEquiv(String httpEquiv)
    {
        this.httpEquiv = httpEquiv.toLowerCase();
    }

    public String getHTTEquiv()
    {
        return httpEquiv;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setContent(String content, URL documentURL)
    {
        this.content = content;
        if (httpEquiv != null && httpEquiv.equals("refresh"))
        {
            StringTokenizer st = new StringTokenizer(content, ";");
            try
            {
                refreshTime = Integer.parseInt(st.nextToken().trim());
            }
            catch (NumberFormatException e)
            {
                refreshTime = 0;
            }
            String surl = st.nextToken().trim();
            int index = surl.indexOf("=");
            if (index != -1)
            {
                try
                {
                    url = NetworkUtilities.getAbsoluteURL(surl.substring(index + 1, surl.length()),
                        documentURL, null);
                }
                catch (MalformedURLException e)
                {
                    url = null;
                }
            }
        }
    }

    public String getContent()
    {
        return content;
    }

    public void setRefreshTime(int refreshTime)
    {
        this.refreshTime = refreshTime;
    }

    public int returnRefreshTime()
    {
        return refreshTime;
    }

    public void setURL(URL url)
    {
        this.url = url;
    }

    public URL getURL()
    {
        return url;
    }

    public String toString()
    {
        return (httpEquiv != null && httpEquiv.length() > 0 ? httpEquiv : name) + " = " + content;
    }

    public JTable getProperties()
    {
        String columnNames[] =
        {
            ApplicationUtilities.getResourceString("properties.attribute"),
            ApplicationUtilities.getResourceString("properties.value")
        };
        Object data[][];
        if (httpEquiv != null && httpEquiv.length() > 0)
        {
            if (httpEquiv.equals("refresh"))
            {
                data = new Object[3][2];
                data[0][0] = "http-equiv";
                data[0][1] = httpEquiv;
                data[1][0] = ApplicationUtilities.getResourceString("html.meta.url");
                data[1][1] = url.toExternalForm();
                data[2][0] = ApplicationUtilities.getResourceString("html.meta.refresh");
                data[2][1] = new Integer(refreshTime);
            }
            else
            {
                data = new Object[2][2];
                data[0][0] = "http-equiv";
                data[0][1] = httpEquiv;
                data[1][0] = ApplicationUtilities.getResourceString("html.meta.content");
                data[1][1] = content;
            }
        }
        else
        {
            data = new Object[2][2];
            data[0][0] = ApplicationUtilities.getResourceString("html.meta.name");
            data[0][1] = name;
            data[1][0] = ApplicationUtilities.getResourceString("html.meta.content");
            data[1][1] = content;
        }
        return new JTable(new PropertiesTableModel(columnNames, 2, data));
    }
}