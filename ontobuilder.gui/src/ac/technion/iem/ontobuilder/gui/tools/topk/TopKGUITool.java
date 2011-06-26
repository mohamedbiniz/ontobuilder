package ac.technion.iem.ontobuilder.gui.tools.topk;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.gui.tools.utils.ExceptionsHandler;
import ac.technion.iem.ontobuilder.gui.tools.utils.FileChoosingUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsWrapper;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;
import ac.technion.iem.ontobuilder.matching.utils.DoublePrecision;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

import com.modica.ontology.match.MatchInformation;

/**
 * <p>Title: TopKGUITool</p>
 * <p>Description: The Top-K GUI</p>
 * Extends {@link JFrame}
 * @author Haggai Roitman
 */
public class TopKGUITool extends JFrame
{

    private static final long serialVersionUID = 4758252722444566693L;

    /** Creates new form SchemaMappings */
    public TopKGUITool()
    {
        sm = new SMSplash();
        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    // sm.show();
                    sm.setVisible(true); // changed for java 1.6
                }
            });
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        // GUIThemeAdaptor.setGUI_Theme(new Theme());
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialise the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    private void initComponents()
    {
        setTitle("Top K Framework Graphic Tool - created by: Haggai Roitman Oct 2003");
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel111 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        // targetURL = new javax.swing.JTextField();
        // cadidateURL = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        matchTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        algorithmComboBox = new javax.swing.JComboBox();
        targetXMLFile = new javax.swing.JComboBox();
        cadidateXMLFile = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        messages = new javax.swing.JLabel();
        threshold = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        prinButton = new javax.swing.JButton();
        pickCandButton = new javax.swing.JButton();
        pickTargetButton = new javax.swing.JButton();
        matchIndexLabel = new javax.swing.JLabel();
        bestMatchButton = new javax.swing.JButton();
        nextMatchButton = new javax.swing.JButton();
        previousMatchButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                exitForm(evt);
            }
        });

        setResizable(false);
        jPanel1.setLayout(new AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        // jPanel1.setBorder(new javax.swing.border.MatteBorder(new
        // javax.swing.ImageIcon("images/SPACE.JPG")));
        jLabel1.setForeground(new java.awt.Color(204, 255, 0));
        jLabel1.setIcon(new javax.swing.ImageIcon(""));
        jLabel1.setText("Pick Target Ontology:");
        jLabel1.setFont(new java.awt.Font("David", java.awt.Font.BOLD, 14));
        jLabel1.setToolTipText("");
        jPanel1.add(jLabel1, new AbsoluteConstraints(60, 150, 170, 20));

        jLabel11.setForeground(new java.awt.Color(204, 255, 0));
        jLabel11.setIcon(new javax.swing.ImageIcon(""));
        jLabel11.setText("Pick Candidate Ontology:");
        jLabel11.setFont(new java.awt.Font("David", java.awt.Font.BOLD, 14));
        jLabel11.setToolTipText("");
        jPanel1.add(jLabel11, new AbsoluteConstraints(60, 120, -1, 20));

        targetXMLFile.setBackground(new java.awt.Color(255, 255, 204));
        targetXMLFile.setFont(new java.awt.Font("Dialog", 1, 12));
        targetXMLFile.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.add(targetXMLFile, new AbsoluteConstraints(225, 150, 315, -1));

        cadidateXMLFile.setBackground(new java.awt.Color(255, 255, 204));
        cadidateXMLFile.setFont(new java.awt.Font("Dialog", 1, 12));
        cadidateXMLFile.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.add(cadidateXMLFile, new AbsoluteConstraints(225, 120, 315, -1));

        pickCandButton.setIcon(new ImageIcon("images/openontology.gif"));
        pickCandButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pickCandXMLFileButtonPressed();
            }
        });
        jPanel1.add(pickCandButton, new AbsoluteConstraints(545, 120, 40, -1));

        pickTargetButton.setIcon(new ImageIcon("images/openontology.gif"));
        pickTargetButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pickTargetXMLFileButtonPressed();
            }
        });
        jPanel1.add(pickTargetButton, new AbsoluteConstraints(545, 150, 40, -1));

        jScrollPane1.setBackground(new java.awt.Color(0, 0, 0));
        jScrollPane1.setBorder(new javax.swing.border.TitledBorder(null, "Match Information",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 3, 24),
            new java.awt.Color(0, 255, 204)));
        jScrollPane1.setToolTipText("");
        matchTable.setRowHeight(20);
        matchTable.setBackground(new java.awt.Color(255, 255, 204));
        matchTable.setFont(new java.awt.Font("David", 1, 14));
        matchTable.setForeground(new java.awt.Color(0, 0, 0));
        matchTable.getTableHeader().setFont(new java.awt.Font("Arial", 1, 14));
        matchTable.getTableHeader().setBackground(Color.YELLOW);
        matchTable.setModel(new SchemaMappingTabelModel());
        jScrollPane1.setViewportView(matchTable);
        matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel rowSM = matchTable.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                if (e.getValueIsAdjusting())
                {
                    return;
                }
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty())
                {
                    return;
                }
                int selectedRow = matchTable.getSelectedRow();
                MatchedAttributePair pair = (MatchedAttributePair) ((SchemaMappingTabelModel) matchTable
                    .getModel()).getRecordAtRow(selectedRow);
                // Term candidateTerm =
                // smw.getMatchMatrix().getTermByName(pair.getAttribute1(),smw.getMatchMatrix().getCandidateTerms());
                // Term targetTerm =
                // smw.getMatchMatrix().getTermByName(pair.getAttribute2(),smw.getMatchMatrix().getTargetTerms());
                String infoMessage = "<html><font color=red><b>Candidate Term:</b></font>" +
                    pair.getAttribute1() +
                    " <font color=red><b><br>matched to:<br>TargetTerm:</b></font>" +
                    pair.getAttribute2() + "<br>" +
                    "<font color=red><b>Match Effectiveness:</b></font>" +
                    DoublePrecision.getDoubleP(pair.getMatchedPairWeight(), 5) + "</html>";
                JOptionPane.showMessageDialog(null, infoMessage, "Term Match Information",
                    JOptionPane.INFORMATION_MESSAGE);

            }
        });

        jPanel1.add(jScrollPane1, new AbsoluteConstraints(60, 240, 680, 260));

        jLabel2.setForeground(new java.awt.Color(255, 204, 0));
        jLabel2.setText("Match Algorithm:");
        jLabel2.setFont(new java.awt.Font("David", java.awt.Font.BOLD, 14));
        jPanel1.add(jLabel2, new AbsoluteConstraints(60, 190, 110, 20));

        algorithmComboBox.setBackground(new java.awt.Color(255, 255, 204));
        algorithmComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]
        {
            MatchingAlgorithmsNamesEnum.TERM.getName(), MatchingAlgorithmsNamesEnum.VALUE.getName(),
            MatchingAlgorithmsNamesEnum.TERM_VALUE_COMBINED.getName(), MatchingAlgorithmsNamesEnum.PRECEDENCE.getName(),
            MatchingAlgorithmsNamesEnum.TERM_VALUE_PRECEDENCE_COMPOSITION_COMBINED.getName(),
            MatchingAlgorithmsNamesEnum.COMPOSITION.getName()
        }));
        jPanel1.add(algorithmComboBox, new AbsoluteConstraints(180, 190, 230, 20));

        jLabel3.setForeground(new java.awt.Color(255, 204, 0));
        jLabel3.setText("Threshold:");
        jLabel3.setFont(new java.awt.Font("David", java.awt.Font.BOLD, 14));
        jPanel1.add(jLabel3, new AbsoluteConstraints(435, 190, 70, 20));

        threshold.setBackground(new java.awt.Color(255, 255, 204));
        threshold.setFont(new java.awt.Font("David", java.awt.Font.BOLD, 14));
        threshold.setText("      0.0");
        jPanel1.add(threshold, new AbsoluteConstraints(515, 190, 70, -1));

        matchIndexLabel.setForeground(Color.WHITE);
        matchIndexLabel.setFont(new java.awt.Font("David", java.awt.Font.BOLD, 16));
        jPanel1.add(matchIndexLabel, new AbsoluteConstraints(600, 190, -1, -1));

        jPanel2.setLayout(new AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setBorder(new javax.swing.border.MatteBorder(new java.awt.Insets(1, 1, 1, 1),
            new java.awt.Color(204, 255, 204)));

        previousMatchButton.setIcon(new ImageIcon("images/back.gif"));
        previousMatchButton.setToolTipText("");
        previousMatchButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                previousMatchButtonActionPerformed(evt);
            }
        });

        jPanel2.add(previousMatchButton, new AbsoluteConstraints(140, 20, 40, 30));

        saveButton.setIcon(new ImageIcon("images/savematch.gif"));
        saveButton.setToolTipText("");
        saveButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                saveButtonPressed();
            }
        });

        jPanel2.add(saveButton, new AbsoluteConstraints(200, 20, 40, 30));

        prinButton.setIcon(new ImageIcon("images/print.gif"));
        prinButton.setToolTipText("");
        prinButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                printButtonPressed();
            }
        });

        jPanel2.add(prinButton, new AbsoluteConstraints(260, 20, 40, 30));

        messages.setForeground(new java.awt.Color(255, 204, 0));
        messages.setFont(new java.awt.Font("David", java.awt.Font.BOLD, 16));
        jPanel2.add(messages, new AbsoluteConstraints(500, 30, -1, -1));

        bestMatchButton.setIcon(new ImageIcon("images/go.gif"));
        bestMatchButton.setToolTipText("");
        bestMatchButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bestMatchButtonActionPerformed(evt);
            }
        });

        jPanel2.add(bestMatchButton, new AbsoluteConstraints(20, 20, 40, 30));

        nextMatchButton.setIcon(new ImageIcon("images/forward.gif"));
        nextMatchButton.setToolTipText("");
        nextMatchButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                nextMatchButtonActionPerformed(evt);
            }
        });

        jPanel2.add(nextMatchButton, new AbsoluteConstraints(80, 20, 40, 30));
        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.add(jPanel2, new AbsoluteConstraints(60, 530, 690, 70));
        // jLabel4.setIcon(new javax.swing.ImageIcon("images/SMDemo.gif"));
        jLabel4.setFont(new Font("David", Font.BOLD, 40));
        jLabel4.setForeground(Color.YELLOW);
        jLabel111.setIcon(new ImageIcon("images/TopKLogo.gif"));
        jLabel4.setText("Top K Framework - Graphic Tool");
        jPanel1.add(jLabel4, new AbsoluteConstraints(150, 40, -1, -1));
        jPanel1.add(jLabel111, new AbsoluteConstraints(20, 10, -1, -1));

        jLabel5.setForeground(new java.awt.Color(204, 255, 204));
        jLabel5.setFont(new java.awt.Font("David", java.awt.Font.BOLD, 14));
        jLabel5.setText("Options:");
        jPanel1.add(jLabel5, new AbsoluteConstraints(60, 510, -1, -1));

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new java.awt.Dimension(800, 640));
        setLocation((screenSize.width - 800) / 2, (screenSize.height - 640) / 2);
        try
        {
            Thread.currentThread();
            Thread.sleep(5000);

        }
        catch (InterruptedException e)
        {
        }
        sm.dispose();
    }

    /**
     * Handles a previous match button action
     * 
     * @param evt the event performed
     */
    private void previousMatchButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        try
        {
            if (mapIndex == 0)
            {
                eHandler.displayErrorMessage(false, this, "You must first press on GO", "Error");
                return;
            }
            try
            {
                double t = Double.parseDouble(threshold.getText());
                if (t < 0 || t > 1)
                {
                    eHandler.displayErrorMessage(false, this,
                        "You entered iliigal thershold (must be [0,1])", "Error");
                    enableButtons(true);
                    return;
                }
            }
            catch (NumberFormatException e)
            {
                eHandler.displayErrorMessage(false, this,
                    "You entered iliigal thershold (must be [0,1])", "Error");
                enableButtons(true);
                return;
            }
            mapIndex = (mapIndex == 1) ? mapIndex : --mapIndex;
            enableButtons(false);
            SchemaTranslator st = smw.getKthBestMatching(mapIndex);
            MatchedAttributePair[] pairs = st.getMatchedPairs();
            LinkedList<MatchedAttributePair> toDisplay = new LinkedList<MatchedAttributePair>();
            for (int i = 0; i < pairs.length; i++)
                toDisplay.add(pairs[i]);
            if (!"".equals(threshold.getText()))
                toDisplay = filterByThreshold(Double.parseDouble(threshold.getText()), toDisplay);
            setTableData(toDisplay, smw);
        }
        catch (Throwable e)
        {
            eHandler.displayErrorMessage(false, this, "Error:" + e.getMessage(), "Error");
            enableButtons(true);
            mapIndex++;

        }
    }

    /**
     * Handles a press on the Save button
     */
    private void saveButtonPressed()
    {
        if (stRemember != null)
        {
            try
            {
                String filename = ((String) cadidateXMLFile.getSelectedItem()).substring(0,
                    ((String) cadidateXMLFile.getSelectedItem()).indexOf(".xml") - 1) +
                    "-" +
                    ((String) targetXMLFile.getSelectedItem()).substring(0,
                        ((String) targetXMLFile.getSelectedItem()).indexOf(".xml") - 1) +
                    mapIndex +
                    ".xml";
                stRemember.saveMatchToXML(mapIndex, (String) cadidateXMLFile.getSelectedItem(),
                    (String) targetXMLFile.getSelectedItem(), filename);
                JOptionPane.showMessageDialog(this, mapIndex + " best match saved into file: " +
                    filename, "Save Best Match", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (SchemaMatchingsException e)
            {
                eHandler.displayErrorMessage(false, this, "Error: " + e.getMessage(), "Error");
                return;
            }
        }
        else
        {
            eHandler.displayErrorMessage(false, this, "No match to save", "Error");
        }
    }

    /**
     * Handles a next match button action
     * 
     * @param evt the event performed
     */
    private void nextMatchButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        try
        {
            if (mapIndex == 0)
            {
                eHandler.displayErrorMessage(false, this, "You must first press on GO", "Error");
                return;
            }
            try
            {
                double t = Double.parseDouble(threshold.getText());
                if (t < 0 || t > 1)
                {
                    eHandler.displayErrorMessage(false, this,
                        "You entered iliigal thershold (must be [0,1])", "Error");
                    enableButtons(true);
                    return;
                }
            }
            catch (NumberFormatException e)
            {
                eHandler.displayErrorMessage(false, this,
                    "You entered iliigal thershold (must be [0,1])", "Error");
                enableButtons(true);
                return;
            }
            mapIndex++;
            enableButtons(false);
            SchemaTranslator st = smw.getKthBestMatching(mapIndex);
            // debug
            printDiff(st.getMatchedPairs());
            stRemember = st;
            System.out.println("Total mapping weight:" + st.getTotalMatchWeight());
            // /
            MatchedAttributePair[] pairs = st.getMatchedPairs();
            LinkedList<MatchedAttributePair> toDisplay = new LinkedList<MatchedAttributePair>();
            for (int i = 0; i < pairs.length; i++)
                toDisplay.add(pairs[i]);
            if (!"".equals(threshold.getText()))
                toDisplay = filterByThreshold(Double.parseDouble(threshold.getText()), toDisplay);
            setTableData(toDisplay, smw);
            // processWindow .destroy();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            eHandler.displayErrorMessage(false, this, "Error:" + e.getMessage(), "Error");
            enableButtons(true);
            mapIndex--;

        }
    }

    /**
     * Handles a best match button action
     * 
     * @param evt the event performed
     */
    private void bestMatchButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        try
        {
            enableButtons(false);
            if (cadidateXMLFile.getItemCount() == 0 || targetXMLFile.getItemCount() == 0)
            {
                eHandler.displayErrorMessage(false, this,
                    "Please enter candidate and target Ontology XML files", "Error");
                enableButtons(true);
            }
            else
            {
                try
                {
                    double t = Double.parseDouble(threshold.getText());
                    if (t < 0 || t > 1)
                    {
                        eHandler.displayErrorMessage(false, this,
                            "You entered iliigal thershold (must be [0,1])", "Error");
                        enableButtons(true);
                        return;
                    }
                }
                catch (NumberFormatException e)
                {
                    eHandler.displayErrorMessage(false, this,
                        "You entered iliigal thershold (must be [0,1])", "Error");
                    enableButtons(true);
                    return;
                }
                mapIndex = 1;
                if (!lastCandidateURL.equals((String) cadidateXMLFile.getSelectedItem()) ||
                    !lastTargetURL.equals((String) targetXMLFile.getSelectedItem()) ||
                    !lastAlgorithm.equals(algorithmComboBox.getSelectedItem()))
                {
                    lastCandidateURL = (String) cadidateXMLFile.getSelectedItem();
                    lastTargetURL = (String) targetXMLFile.getSelectedItem();
                    lastAlgorithm = (String) algorithmComboBox.getSelectedItem();
                    Ontology oTarget = ob.readOntologyXMLFile(((File) targetXMLFiles
                        .get(lastTargetURL)).getPath());
                    Ontology oCandidate = ob.readOntologyXMLFile(((File) candidateXMLFiles
                        .get(lastCandidateURL)).getPath());
                    MatchInformation match = ob.matchOntologies(oCandidate, oTarget,
                        (String) algorithmComboBox.getSelectedItem());

                    if (smw != null)
                    {
                        smw = null;
                    }
                    smw = new SchemaMatchingsWrapper(match.getMatrix());
                    SchemaTranslator st = smw.getBestMatching();
                    // debug
                    stRemember = st;
                    System.out.println("Total mapping weight:" + st.getTotalMatchWeight());
                    // /
                    MatchedAttributePair[] pairs = st.getMatchedPairs();
                    LinkedList<MatchedAttributePair> toDisplay = new LinkedList<MatchedAttributePair>();
                    for (int i = 0; i < pairs.length; i++)
                        toDisplay.add(pairs[i]);
                    if (!"".equals(threshold.getText()))
                        toDisplay = filterByThreshold(Double.parseDouble(threshold.getText()),
                            toDisplay);
                    setTableData(toDisplay, smw);
                }
                else
                {
                    // Retrieve best mapping
                    SchemaTranslator st = smw.getBestMatching();
                    MatchedAttributePair[] pairs = st.getMatchedPairs();
                    LinkedList<MatchedAttributePair> toDisplay = new LinkedList<MatchedAttributePair>();
                    for (int i = 0; i < pairs.length; i++)
                        toDisplay.add(pairs[i]);
                    setTableData(toDisplay, smw);
                }
                // processWindow.destroy();
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            // processWindow.destroy();
            eHandler.displayErrorMessage(false, this, "Error:" + e.getMessage(), "Error");
            enableButtons(true);

        }
    }

    /**
     * Handles a press on the print button
     */
    public void printButtonPressed()
    {
        // System.out.println("print sent...");
        // /* Construct the print request specification.
        // * The print data is Postscript which will be
        // * supplied as a stream. The media size
        // * required is A4, and 2 copies are to be printed
        // */
        // String filename =
        // ((String)cadidateXMLFile.getSelectedItem()).substring(0,((String)cadidateXMLFile.getSelectedItem()).indexOf(".xml")-1)+"-"+((String)targetXMLFile.getSelectedItem()).substring(0,((String)targetXMLFile.getSelectedItem()).indexOf(".xml")-1)+mapIndex+".xml";
        // try{
        // stRemember.saveMatchToXML(mapIndex,(String)cadidateXMLFile.getSelectedItem(),(String)targetXMLFile.getSelectedItem(),filename);
        // }catch(SchemaMatchingsException e){
        // return;
        // }
        // DocFlavor flavor = DocFlavor.INPUT_STREAM.GIF;
        // PrintRequestAttributeSet aset =
        // new HashPrintRequestAttributeSet();
        // aset.add(MediaSizeName.ISO_A4);
        // aset.add(new Copies(1));
        // aset.add(Sides.ONE_SIDED);
        // aset.add(Finishings.STAPLE);
        // /* locate a print service that can handle it */
        // PrintService[] pservices =
        // PrintServiceLookup.lookupPrintServices(flavor, aset);
        // if (true) {
        // System.out.println("selected printer " + pservices[0].getName());
        //
        // /* create a print job for the chosen service */
        // DocPrintJob pj =
        // /*pservices[0]*/PrintServiceLookup.lookupDefaultPrintService().createPrintJob();
        // try {
        // /*
        // * Create a Doc object to hold the print data.
        // * Since the data is postscript located in a disk file,
        // * an input stream needs to be obtained
        // * BasicDoc is a useful implementation that will if requested
        // * close the stream when printing is completed.
        // */
        // FileInputStream fis = new FileInputStream("back.gif");
        // Doc doc = new SimpleDoc(fis, flavor, null);
        //
        // /* print the doc as specified */
        // pj.print(doc, aset);
        //
        // /*
        // * Do not explicitly call System.exit() when print returns.
        // * Printing can be asynchronous so may be executing in a
        // * separate thread.
        // * If you want to explicitly exit the VM, use a print job
        // * listener to be notified when it is safe to do so.
        // */
        //
        // } catch (IOException ie) {
        // System.out.println(ie);
        // } catch (PrintException e) {
        // System.out.println(e);
        // }
        // }
        // else{
        // System.out.println("didn't find printing service");
        // }

    }

    /**
     * Handles a press on the pick candidate XML file button
     */
    public void pickCandXMLFileButtonPressed()
    {
        FileChoosingUtilities.openFileChoser(this, "Pick up Candidate Ontology XML file");
        if (FileChoosingUtilities.isFileChosed())
        {
            candidateXMLFiles.put(FileChoosingUtilities.getChosenFile().getName(),
                FileChoosingUtilities.getChosenFile());
            tempCandidateXMLFiles.addFirst(FileChoosingUtilities.getChosenFile().getName());
            refreshCandFilesListView();
        }
    }

    /**
     * Handles a press on the pick target XML file button
     */
    public void pickTargetXMLFileButtonPressed()
    {
        FileChoosingUtilities.openFileChoser(this, "Pick up Target Ontology XML file");
        if (FileChoosingUtilities.isFileChosed())
        {
            targetXMLFiles.put(FileChoosingUtilities.getChosenFile().getName(),
                FileChoosingUtilities.getChosenFile());
            tempTargetXMLFiles.addFirst(FileChoosingUtilities.getChosenFile().getName());
            refreshTargetFilesListView();
        }
    }

    /**
     * Prints the different between each of the two match attributes, used for debug
     */
    public void printDiff(MatchedAttributePair[] pairs)
    {
        if (stRemember == null)
            return;
        else
        {
            for (int i = 0; i < pairs.length; i++)
            {
                if (!stRemember.isExist(pairs[i]))
                    System.out.println("new pair: " + pairs[i].getAttribute1() + " -> " +
                        pairs[i].getAttribute2() + "weight:" + pairs[i].getMatchedPairWeight());
            }
        }
    }

    /**
     * Handles refresh of the candidate files list
     */
    public void refreshCandFilesListView()
    {
        cadidateXMLFile.removeAllItems(); // remove old values
        Iterator<String> it = tempCandidateXMLFiles.iterator();
        while (it.hasNext())
        {
            cadidateXMLFile.addItem(it.next());
        }
    }

    /**
     * Handles refresh of the target files list
     */
    public void refreshTargetFilesListView()
    {
        targetXMLFile.removeAllItems(); // remove old values
        Iterator<String> it = tempTargetXMLFiles.iterator();
        while (it.hasNext())
        {
            targetXMLFile.addItem(it.next());
        }
    }

    /**
     * Filters the matched attributes that obey the threshold from the input list
     * 
     * @param tsh the threshold
     * @param data the data to filter
     * @return a filtered list of MatchedAttributePair
     */
    public LinkedList<MatchedAttributePair> filterByThreshold(double tsh,
        LinkedList<MatchedAttributePair> data)
    {
        LinkedList<MatchedAttributePair> filtered = new LinkedList<MatchedAttributePair>();
        Iterator<MatchedAttributePair> it = data.iterator();
        while (it.hasNext())
        {
            MatchedAttributePair pair = it.next();
            if (pair.getMatchedPairWeight() >= tsh)
                filtered.add(pair);
        }
        return filtered;
    }

    /**
     * Enables the bestMatchButton, previousMatchButton, nextMatchButton
     * 
     * @param flag true is the buttons are to be enabled, false if they should be disabled
     */
    public void enableButtons(boolean flag)
    {
        bestMatchButton.setEnabled(flag);
        previousMatchButton.setEnabled(flag);
        nextMatchButton.setEnabled(flag);
    }

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt)
    {
        System.exit(0);
    }

    /**
     * This function fills allergiesTable with patient allergies details
     * 
     * @param allergies list of allergies
     */
    public void setTableData(final LinkedList<MatchedAttributePair> matches,
        final SchemaMatchingsWrapper smw)
    {
        Runnable op = new Runnable()
        {
            public void run()
            {
                if (matches != null)
                {
                    matchIndexLabel.setText(Integer.toString(mapIndex) + " best mapping");
                    ((SchemaMappingTabelModel) matchTable.getModel()).setTableData(matches, smw);
                    enableButtons(true);
                }
            }
        };
        SwingUtilities.invokeLater(op);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        TopKGUITool sm = null;
        try
        {
            sm = new TopKGUITool();
            // sm.show();
            sm.setVisible(true); // changed for java 1.6
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            sm.getExceptionsHandler().displayErrorMessage(true, sm,
                "Unknown Exception occured:" + e.getMessage(), "Fatal Error");
        }
    }

    public ExceptionsHandler getExceptionsHandler()
    {
        return eHandler;
    }

    // Variables declaration - do not modify
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable matchTable;
    public javax.swing.JComboBox algorithmComboBox;
    private javax.swing.JTextField threshold;
    private javax.swing.JButton nextMatchButton;
    private javax.swing.JButton previousMatchButton;
    // private javax.swing.JTextField cadidateURL;
    // private javax.swing.JTextField targetURL;
    private javax.swing.JButton bestMatchButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton prinButton;
    private javax.swing.JButton pickCandButton;
    private javax.swing.JButton pickTargetButton;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel messages;
    private javax.swing.JComboBox cadidateXMLFile;
    private javax.swing.JComboBox targetXMLFile;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel matchIndexLabel;
    private ExceptionsHandler eHandler = new ExceptionsHandler();
    private OntoBuilderWrapper ob = new OntoBuilderWrapper();
    private SchemaMatchingsWrapper smw;
    private String lastCandidateURL = "";
    private String lastTargetURL = "";
    private String lastAlgorithm = "";
    // private String lastThreshold = "";
    private Hashtable<String, File> candidateXMLFiles = new Hashtable<String, File>();
    private Hashtable<String, File> targetXMLFiles = new Hashtable<String, File>();
    private LinkedList<String> tempCandidateXMLFiles = new LinkedList<String>();
    private LinkedList<String> tempTargetXMLFiles = new LinkedList<String>();
    private int mapIndex = 0;
    private SMSplash sm;
    // private Process processWindow;
    // for debug
    private SchemaTranslator stRemember;
    // End of variables declaration

}
