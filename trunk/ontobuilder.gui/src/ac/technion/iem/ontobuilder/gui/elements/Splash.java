package ac.technion.iem.ontobuilder.gui.elements;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: Splash</p>
 * Extends {@link JWindow}
 */
public class Splash extends JWindow
{
    private static final long serialVersionUID = 1L;

    public Splash()
    {
        super();
        URL url = ApplicationUtilities.class.getResource("\\images\\splash.gif");
        if (url == null)
        {
            try
            {
                url = new File("images", "splash.gif").toURI().toURL();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        JLabel splashLabel = new JLabel(new ImageIcon(url));
        getContentPane().add(splashLabel);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getSize().width / 2, screenSize.height / 2 -
            getSize().height / 2);
    }
}
