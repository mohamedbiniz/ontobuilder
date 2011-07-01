package ac.technion.iem.ontobuilder.matching.wrapper;

import java.io.File;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import ac.technion.iem.ontobuilder.core.thesaurus.Thesaurus;

import com.modica.application.Application;
import com.modica.application.ApplicationOptions;
import com.modica.application.ApplicationUtilities;
import com.modica.application.PropertyException;
import com.modica.application.ResourceException;
import com.modica.ontology.algorithm.AbstractAlgorithm;
import com.modica.ontology.algorithm.Algorithm;
import com.modica.ontology.algorithm.AlgorithmException;
import com.modica.ontology.algorithm.AlgorithmUtilities;
import com.modica.ontology.domain.DomainSimilarity;
import com.modica.util.NetworkUtilities;

/**
 * <p>Title: OntoBuilder</p>
 * <p>Description: Initialises application - parameters, Thesaurus, algorithms</p>
 */
class OntoBuilder
{

    protected Locale locale = Locale.getDefault();
    protected ApplicationOptions options;
    protected Thesaurus thesaurus;
    protected Vector<AbstractAlgorithm> algorithms;

    /**
     * Constructs a default OntoBuilder (regular version)
     */
    protected OntoBuilder()
    {
        init(false);
    }

    /**
     * Constructs a OntoBuilder
     * 
     * @param light the version of the Onto Builder to create (<code>true</code>-light, <code>false</code>-regular)
     */
    protected OntoBuilder(boolean light)
    {
        init(light);
    }

    /**
     * Initialises an OntoBuilder
     * 
     * @param light the version of the Onto Builder to create (<code>true</code>-light, <code>false</code>-regular)
     */
    private void init(boolean light)
    {
        initializeParameters();
        initializeOptions();
        buildThesaurus();
        initializeAlgorithms();
        DomainSimilarity.buildDomainMatrix(ApplicationUtilities
            .getStringProperty("domain.domainMatrix"));
        if (!light)
        {
            NetworkUtilities.initializeHTTPSProtocol();
            setProxy();
            setConnectionTimeout();
        }
    }

    /**
     * Sets the proxy according to NetworkUtilities.USE_PROXY_PROPERTY
     */
    protected void setProxy()
    {
        boolean proxy = Boolean.valueOf(
            (String) options.getOptionValue(NetworkUtilities.USE_PROXY_PROPERTY)).booleanValue();
        if (proxy)
            NetworkUtilities.setProxy(
                (String) options.getOptionValue(NetworkUtilities.PROXY_HOST_PROPERTY),
                (String) options.getOptionValue(NetworkUtilities.PROXY_PORT_PROPERTY));
        else
            NetworkUtilities.disableProxy();
    }

    /**
     * Sets the proxy according to the input host and port
     * 
     * @param proxyHost the proxy host to set
     * @param proxyPort the proxy port to set
     */
    public void setProxy(String proxyHost, String proxyPort)
    {
        NetworkUtilities.setProxy(proxyHost, proxyPort);
    }

    /**
     * Set the connection timeout according to NetworkUtilities.CONNECTION_TIMEOUT_PROPERTY
     */
    protected void setConnectionTimeout()
    {
        try
        {
            long millis = Long.parseLong((String) options
                .getOptionValue(NetworkUtilities.CONNECTION_TIMEOUT_PROPERTY));
            NetworkUtilities.setConnectionTimeout(millis > 0 ? millis * 1000 : -1);
        }
        catch (NumberFormatException e)
        {
            // NetworkUtilities.setConnectionTimeout(-1);
        }
    }

    /**
     * Builds to Thesaurus of the Onto Builder
     */
    private void buildThesaurus()
    {
        try
        {
            File thesaurusFile = new File(ApplicationUtilities.getCurrentDirectory() +
                File.separator + ApplicationUtilities.getStringProperty("thesaurus.file"));
            if (thesaurusFile.exists())
                thesaurus = new Thesaurus(thesaurusFile);
            else
                thesaurus = new Thesaurus("/" +
                    ApplicationUtilities.getStringProperty("thesaurus.file"));
        }
        catch (ThesaurusException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get the Thesaurus
     * 
     * @return the {@link Thesaurus}
     */
    public Thesaurus getThesaurus()
    {
        return thesaurus;
    }

    /**
     * Load the application's options file
     */
    private void initializeOptions()
    {
        options = new ApplicationOptions();
        File optionFile = new File(ApplicationUtilities.getCurrentDirectory() + File.separator +
            ApplicationUtilities.getStringProperty("application.configurationFile"));
        if (optionFile.exists())
            options.loadOptions(optionFile);
        else
            options.loadOptions("/" +
                ApplicationUtilities.getStringProperty("application.configurationFile"));
    }

    /**
     * Initialise the matching algorithms instances
     */
    private void initializeAlgorithms()
    {
        try
        {
            File algorithmsFile = new File(ApplicationUtilities.getCurrentDirectory() +
                ApplicationUtilities.getStringProperty("algorithms.file"));
            if (algorithmsFile.exists())
                algorithms = AlgorithmUtilities.getAlgorithmsInstances(algorithmsFile);
            else
                algorithms = AlgorithmUtilities.getAlgorithmsInstances("/" +
                    ApplicationUtilities.getStringProperty("algorithms.file"));
            if (algorithms == null)
                return;
            double threshold = Double.parseDouble((String) options
                .getOptionValue(Algorithm.MATCH_THRESHOLD_PROPERTY));
            for (Iterator<AbstractAlgorithm> i = algorithms.iterator(); i.hasNext();)
            {
                Algorithm algorithm = i.next();
                algorithm.setThreshold(threshold / 100);
                if (algorithm.usesThesaurus())
                    algorithm.setThesaurus(thesaurus);
            }
        }
        catch (AlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Initialise the parameters
     */
    private void initializeParameters()
    {
        // Initialise the properties
        try
        {
            ApplicationUtilities.initializeProperties(Application.PROPERTIES_FILE);
        }
        catch (PropertyException e)
        {
            e.printStackTrace();
            return;
        }

        // Initialise the resource bundle
        try
        {
            ApplicationUtilities.initializeResources(
                ApplicationUtilities.getCurrentDirectory() + File.separator +
                    ApplicationUtilities.getStringProperty("application.resourceBundle"), locale);
        }
        catch (ResourceException e)
        {
            e.printStackTrace();
            return;
        }
    }
}