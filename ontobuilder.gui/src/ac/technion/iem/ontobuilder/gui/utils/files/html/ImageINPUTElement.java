package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: ImageINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class ImageINPUTElement extends INPUTElement
{
    protected URL src;
    protected String alt;
    protected int x, y;
    protected boolean pressed;

    // protected JLabel image;
    protected JButton image;

    public ImageINPUTElement()
    {
        super(INPUTElement.IMAGE);
        image = new JButton();
        image.setBorder(null);
        image.setRequestFocusEnabled(false);
        // image=new JLabel();
        image.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                x = e.getX();
                y = e.getY();
                if (form != null)
                    form.clearPressed();
                pressed = true;
            }
        });
        image.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public ImageINPUTElement(String name, URL src, String alt)
    {
        this();
        this.name = name;
        setSrc(src);
        this.alt = alt;
    }

    public void setSrc(URL src)
    {
        this.src = src;
        image.setIcon(new ImageIcon(src));
    }

    public URL getSrc()
    {
        return src;
    }

    public void setAlt(String alt)
    {
        this.alt = alt;
    }

    public String getAlt()
    {
        return alt;
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        image.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.type"), getInputType()
            },
            {
                ApplicationUtilities.getResourceString("html.input.image.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.input.image.src"), src
            },
            {
                ApplicationUtilities.getResourceString("html.input.image.alt"), alt
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 4, data));
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public String paramString()
    {
        if (name == null || name.length() == 0)
            return "x=" + x + "&" + "y=" + y;
        try
        {
            return URLEncoder.encode(name, "UTF-8") + ".x=" + x + "&" +
                URLEncoder.encode(name, "UTF-8") + ".y=" + y;
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO: Handle the exception better.
            throw new RuntimeException(e);
        }
    }

    public Component getComponent()
    {
        return image;
    }

    public void clearPressed()
    {
        pressed = false;
    }

    public boolean canSubmit()
    {
        return super.canSubmit() && pressed;
    }
}