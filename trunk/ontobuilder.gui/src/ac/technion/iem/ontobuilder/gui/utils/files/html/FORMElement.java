package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/*
 * Fix: default FROM::method to be GET
 */
/**
 * <p>Title: FORMElement</p>
 * Extends {@link HTMLElement}
 */
public class FORMElement extends HTMLElement
{

    static final Character EQUALS = new Character('=');

    static final String POST = "post";
    static final String GET = "get";

    private URL action;
    private String method;
    private String name;
    private ArrayList<INPUTElement> inputs;

    private JPanel component;

    private String lastQueryString = "";
    private int lastRes = -1;

    public FORMElement()
    {
        super(HTMLElement.FORM);
        inputs = new ArrayList<INPUTElement>();
    }

    public FORMElement(String name, URL action, String method)
    {
        super(HTMLElement.FORM);
        this.name = name;
        this.action = action;
        this.method = method;
        inputs = new ArrayList<INPUTElement>();
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setMethod(String methods)
    {
        this.method = methods;
    }

    public String getMethod()
    {
        return method;
    }

    public void setAction(URL action)
    {
        this.action = action;
    }

    public URL getAction()
    {
        return action;
    }

    public int getInputsCount()
    {
        return inputs.size();
    }

    public String toString()
    {
        return name;
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
                ApplicationUtilities.getResourceString("html.form.name"), name
            },
            {
                ApplicationUtilities.getResourceString("html.form.method"), method
            },
            {
                ApplicationUtilities.getResourceString("html.form.action"), action
            },
            {
                ApplicationUtilities.getResourceString("html.form.inputs"),
                new Integer(inputs.size())
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 4, data));
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
        for (Iterator<INPUTElement> i = inputs.iterator(); i.hasNext();)
            node.add(((INPUTElement) i.next()).getTreeBranch());
        return node;
    }

    public void setInputs(ArrayList<INPUTElement> inputs)
    {
        this.inputs = inputs;
        for (Iterator<INPUTElement> i = this.inputs.iterator(); i.hasNext();)
            ((INPUTElement) i.next()).setForm(this);
    }

    public INPUTElement getInput(int index)
    {
        if (index < 0 || index >= inputs.size())
            return null;
        return (INPUTElement) inputs.get(index);
    }

    public void addInput(INPUTElement input)
    {
        if (input == null)
            return;
        input.setForm(this);
        inputs.add(input);
    }

    public void flat()
    {
        flatRadios();
        flatCheckboxes();
    }

    private void flatRadios()
    {
        HashMap<String, RadioINPUTElement> hm = new HashMap<String, RadioINPUTElement>();
        for (Iterator<INPUTElement> i = inputs.iterator(); i.hasNext();)
        {
            INPUTElement input = (INPUTElement) i.next();
            if (input.getInputType().equals(INPUTElement.RADIO))
            {
                RadioINPUTElement radioInput = (RadioINPUTElement) input;
                String name = radioInput.getName();
                if (hm.containsKey(name))
                {
                    RadioINPUTElement radio = (RadioINPUTElement) hm.get(input.getName());
                    for (int j = 0; j < radioInput.getOptionsCount(); j++)
                        radio.addOption(radioInput.getOption(j));
                    i.remove();
                }
                else
                    hm.put(name, radioInput);
            }
        }
    }

    private void flatCheckboxes()
    {
        HashMap<String, CheckboxINPUTElement> hm = new HashMap<String, CheckboxINPUTElement>();
        for (Iterator<INPUTElement> i = inputs.iterator(); i.hasNext();)
        {
            INPUTElement input = (INPUTElement) i.next();
            if (input.getInputType().equals(INPUTElement.CHECKBOX))
            {
                CheckboxINPUTElement checkboxInput = (CheckboxINPUTElement) input;
                String name = checkboxInput.getName();
                if (hm.containsKey(name))
                {
                    CheckboxINPUTElement checkbox = (CheckboxINPUTElement) hm.get(input.getName());
                    for (int j = 0; j < checkboxInput.getOptionsCount(); j++)
                        checkbox.addOption(checkboxInput.getOption(j));
                    i.remove();
                }
                else
                    hm.put(name, checkboxInput);
            }
        }
    }

    public InputStream submit() throws IOException
    {
        return submit(new StringBuffer());
    }

    public InputStream submit(StringBuffer info) throws IOException
    {
        // Build the query string
        info.append("Query string:\n");
        StringBuffer queryString = new StringBuffer();
        for (Iterator<INPUTElement> i = inputs.iterator(); i.hasNext();)
        {
            INPUTElement input = (INPUTElement) i.next();
            if (input.canSubmit())
            {
                String param = input.paramString();
                queryString.append(param).append("&");
                info.append("\t- " + param + "\n");
            }
        }
        String queryStringText = queryString.toString();
        // Remove last &
        if (queryStringText.length() > 0)
            queryStringText = queryStringText.substring(0, queryStringText.length() - 1);
        URL url = action;

        // fix for default GET (HTTP specifications) .
        // amir 08/2004
        if (method.length() == 0)
            method = GET;

        if (method.equalsIgnoreCase(GET))
        {
            try
            {

                // added by haggai 5/1/04
                lastQueryString = url.getFile() + "?" + queryStringText;
                //

                url = new URL(url, url.getFile() + "?" + queryStringText);
            }
            catch (MalformedURLException e)
            {
                return null;
            }
        }
        try
        {
            HttpURLConnection.setFollowRedirects(true);// was false
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; Q312461)");
            // those headers were commented out
            // connection.setRequestProperty("connection","Keep-Alive");
            // connection.setRequestProperty("accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            // connection.setRequestProperty("accept-language","en-us");
            // connection.setRequestProperty("host","OntoBuilder");

            // Get information about headers
            info.append("Headers:\n");
            Map<?, ?> headers = connection.getRequestProperties();
            Set<?> headersSet = headers.keySet();
            for (Iterator<?> i = headersSet.iterator(); i.hasNext();)
            {
                String key = (String) i.next();
                info.append("\t- " + key + "=" + headers.get(key) + "\n");
            }

            if (method.equalsIgnoreCase(POST))
            {
                connection.setDoOutput(true);
                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.print(queryStringText);
                out.close();
            }

            // Handle server redirections
            /*
             * just printouts to check the connection print("URL is "+ url.getProtocol()
             * +";"+url.getHost()+";"+url.getPort()+";"+url.getFile()); print("Properties = " +
             * connection.getRequestProperties().toString()); print("Headers = " +
             * connection.getHeaderFields().toString()); print("Length = " +
             * connection.getContentLength()); print("Use Caches = " +
             * connection.getDefaultUseCaches()); print("Expiration = " +
             * connection.getExpiration()); print("Type = " + connection.getContentType());
             * print("Encoding =  " + connection.getContentEncoding()); // print("Content = " +
             * connection.getContent());
             */

            // Handle server redirections
            if (connection instanceof HttpURLConnection)
            {
                URL redirectURL = null;
                int res = ((HttpURLConnection) connection).getResponseCode();
                lastRes = res;

                while (res > 299 && res < 400)
                {
                    String location = connection.getHeaderField("Location");
                    try
                    {
                        redirectURL = new URL(URLDecoder.decode(location, "UTF-8"));
                    }
                    catch (MalformedURLException e)
                    {
                        break;
                    }
                    connection = redirectURL.openConnection();
                    res = ((HttpURLConnection) connection).getResponseCode();
                    lastRes = res;

                }
            }

            return connection.getInputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public int getLastRes()
    {
        return lastRes;
    }

    public String getLastQueryString()
    {
        return lastQueryString;
    }

    public void reset()
    {
        for (Iterator<INPUTElement> i = inputs.iterator(); i.hasNext();)
            ((INPUTElement) i.next()).reset();
    }

    public void clearPressed()
    {
        for (Iterator<INPUTElement> i = inputs.iterator(); i.hasNext();)
        {
            INPUTElement input = (INPUTElement) i.next();
            if (input.getInputType().equals(INPUTElement.IMAGE))
                ((ImageINPUTElement) input).clearPressed();
            else if (input.getInputType().equals(INPUTElement.SUBMIT))
                ((SubmitINPUTElement) input).clearPressed();
        }
    }

    public Component getComponent()
    {
        if (component != null)
            return component;
        component = new JPanel(new GridBagLayout());
        int ypos = 1;
        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            component.add(new JLabel(ApplicationUtilities.getResourceString("html.form.name") +
                ": " + name, SwingConstants.LEFT), gbc);
            gbc.gridy = ypos++;
            component.add(new JLabel(ApplicationUtilities.getResourceString("html.form.action") +
                ": " + action, SwingConstants.LEFT), gbc);
            gbc.gridy = ypos++;
            gbc.insets = new Insets(0, 0, 10, 0);
            component.add(new JLabel(ApplicationUtilities.getResourceString("html.form.method") +
                ": " + method, SwingConstants.LEFT), gbc);
        }

        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(5, 10, 0, 0);
            for (Iterator<INPUTElement> i = inputs.iterator(); i.hasNext(); ypos++)
            {
                INPUTElement inputElement = (INPUTElement) i.next();
                String type = inputElement.getInputType();
                if (type.equals(INPUTElement.TEXT))
                {
                    TextINPUTElement textInput = (TextINPUTElement) inputElement;
                    JLabel label = new JLabel(textInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(textInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.PASSWORD))
                {
                    PasswordINPUTElement passwordInput = (PasswordINPUTElement) inputElement;
                    JLabel label = new JLabel(passwordInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(passwordInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.FILE))
                {
                    FileINPUTElement fileInput = (FileINPUTElement) inputElement;
                    JLabel label = new JLabel(fileInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(fileInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.HIDDEN))
                {
                    HiddenINPUTElement hiddenInput = (HiddenINPUTElement) inputElement;
                    JLabel label = new JLabel(hiddenInput.getName(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(hiddenInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.CHECKBOX))
                {
                    CheckboxINPUTElement checkboxInput = (CheckboxINPUTElement) inputElement;
                    JLabel label = new JLabel(checkboxInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(checkboxInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.RADIO))
                {
                    RadioINPUTElement radioInput = (RadioINPUTElement) inputElement;
                    JLabel label = new JLabel(radioInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(radioInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.SELECT))
                {
                    SELECTElement selectInput = (SELECTElement) inputElement;
                    JLabel label = new JLabel(selectInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(selectInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.BUTTON))
                {
                    ButtonINPUTElement buttonInput = (ButtonINPUTElement) inputElement;
                    gbc.gridx = 1;
                    gbc.gridy = ypos;
                    component.add(buttonInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.RESET))
                {
                    ResetINPUTElement resetInput = (ResetINPUTElement) inputElement;
                    gbc.gridx = 1;
                    gbc.gridy = ypos;
                    component.add(resetInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.SUBMIT))
                {
                    SubmitINPUTElement submitInput = (SubmitINPUTElement) inputElement;
                    gbc.gridx = 1;
                    gbc.gridy = ypos;
                    component.add(submitInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.IMAGE))
                {
                    ImageINPUTElement imageInput = (ImageINPUTElement) inputElement;
                    JLabel label = new JLabel(imageInput.getAlt(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    gbc.gridy = ypos;
                    component.add(imageInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.TEXTAREA))
                {
                    TEXTAREAElement textareaInput = (TEXTAREAElement) inputElement;
                    JLabel label = new JLabel(textareaInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    gbc.gridy = ypos;
                    component.add(textareaInput.getComponent(), gbc);
                }
            }
        }

        {// Separator
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = ypos;
            gbcl.gridwidth = 2;
            gbcl.gridheight = GridBagConstraints.REMAINDER;
            gbcl.fill = GridBagConstraints.VERTICAL;
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            component.add(new JPanel(), gbcl);
        }

        return component;
    }

    // private void print(String s) {
    // System.out.println(s);
    // }

}