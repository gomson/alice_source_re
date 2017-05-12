package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.JAlice;
import edu.cmu.cs.stage3.alice.authoringtool.util.AliceTabbedPaneUI;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.awt.DynamicFlowLayout;
import edu.cmu.cs.stage3.image.ImageIO;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.InputStream;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




















public class StartUpContentPane
  extends ContentPane
{
  public static final int DO_NOT_CHANGE_TAB_ID = -1;
  public static final int OPEN_TAB_ID = 1;
  public static final int TUTORIAL_TAB_ID = 2;
  public static final int RECENT_TAB_ID = 3;
  public static final int TEMPLATE_TAB_ID = 4;
  public static final int EXAMPLE_TAB_ID = 5;
  public static final int TEXTBOOK_EXAMPLE_TAB_ID = 6;
  private static final String TUTORIAL_STRING = Messages.getString("Tutorial");
  
  private static final String EXAMPLES_STRING = Messages.getString("Examples");
  
  private static final String RECENT_STRING = Messages.getString("Recent_Worlds");
  
  private static final String TEXTBOOK_EXAMPLES_STRING = Messages.getString("Textbook_");
  
  private static final String OPEN_STRING = Messages.getString("Open_a_world");
  
  private static final String TEMPLATES_STRING = Messages.getString("Templates");
  
  private final int WIDTH = 550;
  private final int HEIGHT = 500;
  

  private static final Color SELECTED_COLOR = new Color(10, 
    10, 100);
  private static final Color SELECTED_TEXT_COLOR = new Color(
    255, 255, 255);
  private static final Color BACKGROUND_COLOR = new Color(
    0, 0, 0);
  

  private static Configuration authoringToolConfig = Configuration.getLocalConfiguration(JAlice.class
    .getPackage());
  
  private AliceWorldFilter aliceFilter;
  private javax.swing.filechooser.FileFilter worldFilter;
  private TutorialWorldFilter tutorialFilter = new TutorialWorldFilter();
  
  private StartUpIcon currentlySelected;
  
  private ImageIcon headerImage;
  
  private ImageIcon basicIcon;
  private ImageIcon directoryIcon;
  private ImageIcon upDirectoryIcon;
  private ImageIcon tutorialButtonIcon;
  private File exampleWorlds = null;
  private File templateWorlds = null;
  private File tutorialWorlds = null;
  private File textbookExampleWorlds = null;
  
  private JTabbedPane mainTabPane = new JTabbedPane();
  private JScrollPane exampleScrollPane = new JScrollPane(
    22, 
    31);
  private JScrollPane textbookExampleScrollPane = new JScrollPane(
    22, 
    31);
  private JScrollPane recentScrollPane = new JScrollPane(
    22, 
    31);
  private JScrollPane templateScrollPane = new JScrollPane(
    22, 
    31);
  private JScrollPane tutorialScrollPane = new JScrollPane(
    22, 
    31);
  
  private JPanel exampleWorldsContainer = new JPanel();
  private JPanel recentWorldsContainer = new JPanel();
  private JPanel textbookExampleWorldsContainer = new JPanel();
  private JPanel templateWorldsContainer = new JPanel();
  private JPanel tutorialWorldsContainer = new JPanel();
  private DynamicFlowLayout examplePanelLayout = new DynamicFlowLayout(
    0, null, JPanel.class, 20);
  private DynamicFlowLayout recentPanelLayout = new DynamicFlowLayout(
    0, null, JPanel.class, 20);
  private DynamicFlowLayout templatePanelLayout = new DynamicFlowLayout(
    0, null, JPanel.class, 20);
  private DynamicFlowLayout tutorialPanelLayout = new DynamicFlowLayout(
    0, null, JPanel.class, 20);
  private DynamicFlowLayout textbookPanelLayout = new DynamicFlowLayout(
    0, null, JPanel.class, 20);
  
  private JPanel exampleWorldsDirectoryContainer = new JPanel();
  private JPanel textbookExampleWorldsDirectoryContainer = new JPanel();
  private JPanel templateWorldsDirectoryContainer = new JPanel();
  private JPanel tutorialWorldsDirectoryContainer = new JPanel();
  private JPanel recentWorldsDirectoryContainer = new JPanel();
  
  private JLabel exampleWorldsDirLabel = new JLabel();
  private JLabel textbookExampleWorldsDirLabel = new JLabel();
  private JLabel templateWorldsDirLabel = new JLabel();
  private JLabel tutorialWorldsDirLabel = new JLabel();
  private JLabel recentWorldsDirLabel = new JLabel();
  
  private JButton openButton = new JButton();
  private JButton cancelButton = new JButton();
  
  private JCheckBox stopShowingCheckBox = new JCheckBox();
  
  private JLabel headerLabel = new JLabel();
  
  private JPanel tutorialButtonPanel = new JPanel();
  private JButton startTutorialButton = new JButton();
  private BorderLayout borderLayout1 = new BorderLayout();
  
  private JFileChooser fileChooser = new JFileChooser() {
    public void setSelectedFile(File file) {
      super.setSelectedFile(file);
      StartUpContentPane.this.handleFileSelectionChange(file);
    }
  };
  
  private JPanel buttonPanel = new JPanel();
  private JLabel jLabel1 = new JLabel();
  protected int currentTab = 2;
  
  public StartUpContentPane(AuthoringTool authoringTool) {
    headerImage = new ImageIcon(JAlice.class.getResource("images/startUpDialog/StartupScreen.png"));
    basicIcon = new ImageIcon(JAlice.class.getResource("images/startUpDialog/aliceIcon.png"));
    directoryIcon = new ImageIcon(JAlice.class.getResource("images/startUpDialog/directoryIcon.png"));
    upDirectoryIcon = new ImageIcon(JAlice.class.getResource("images/startUpDialog/upDirectoryIcon.png"));
    tutorialButtonIcon = new ImageIcon(JAlice.class.getResource("images/startUpDialog/tutorialButton.png"));
    
    String[] recentWorldsStrings = authoringToolConfig.getValueList("recentWorlds.worlds");
    String max = authoringToolConfig.getValue("recentWorlds.maxWorlds");
    int maxRecentWorlds = Integer.parseInt(max);
    if ((maxRecentWorlds > 0) && (maxRecentWorlds <= recentWorldsStrings.length)) {
      String[] cappedRecentWorlds = new String[maxRecentWorlds];
      System.arraycopy(recentWorldsStrings, 0, cappedRecentWorlds, 0, maxRecentWorlds);
      recentWorldsStrings = cappedRecentWorlds;
    }
    String filename = authoringToolConfig.getValue("directories.examplesDirectory");
    if (filename != null) {
      exampleWorlds = new File(filename).getAbsoluteFile();
    }
    filename = authoringToolConfig.getValue("directories.templatesDirectory");
    if (filename != null) {
      templateWorlds = new File(filename).getAbsoluteFile();
    }
    filename = authoringToolConfig.getValue("directories.textbookExamplesDirectory");
    if (filename != null) {
      textbookExampleWorlds = new File(filename).getAbsoluteFile();
    }
    
    worldFilter = authoringTool.getWorldFileFilter();
    aliceFilter = new AliceWorldFilter(worldFilter);
    
    jbInit();
    guiInit();
    
    int count = 0;
    int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
    String font = "SansSerif";
    
    tutorialWorlds = authoringTool.getTutorialDirectory();
    count = buildPanel(tutorialWorldsContainer, 
      buildVectorFromDirectory(tutorialWorlds, tutorialFilter), 
      false, null, 2);
    if (count <= 0) {
      tutorialWorldsDirectoryContainer.removeAll();
      JLabel noTutorialWorldsLabel = new JLabel();
      noTutorialWorldsLabel.setText(
        Messages.getString("No_tutorial_found_"));
      noTutorialWorldsLabel.setFont(new Font(font, 
        1, (int)(18 * fontSize / 12.0D)));
      tutorialWorldsDirectoryContainer.add(noTutorialWorldsLabel);
    }
    count = buildPanel(exampleWorldsContainer, 
      buildVectorFromDirectory(exampleWorlds, aliceFilter), false, 
      null, 1);
    if (count <= 0) {
      exampleWorldsDirectoryContainer.removeAll();
      JLabel noExampleWorldsLabel = new JLabel();
      noExampleWorldsLabel.setText(
        Messages.getString("No_example_worlds_"));
      noExampleWorldsLabel.setFont(new Font(font, 
        1, (int)(18 * fontSize / 12.0D)));
      exampleWorldsDirectoryContainer.add(noExampleWorldsLabel);
    }
    count = buildPanel(templateWorldsContainer, 
      buildVectorFromDirectory(templateWorlds, aliceFilter), false, 
      null, 1);
    if (count <= 0) {
      templateWorldsDirectoryContainer.removeAll();
      JLabel noTemplateWorldsLabel = new JLabel();
      noTemplateWorldsLabel.setText(Messages.getString("No_templates_"));
      noTemplateWorldsLabel.setFont(new Font(font, 
        1, (int)(18 * fontSize / 12.0D)));
      templateWorldsDirectoryContainer.add(noTemplateWorldsLabel);
    }
    count = buildPanel(recentWorldsContainer, 
      buildVectorFromString(recentWorldsStrings), true, null, 
      1);
    if (count <= 0) {
      recentWorldsDirectoryContainer.removeAll();
      JLabel noRecentWorldsLabel = new JLabel();
      noRecentWorldsLabel
        .setText(Messages.getString("No_recent_worlds_"));
      noRecentWorldsLabel.setFont(new Font(font, 
        1, (int)(18 * fontSize / 12.0D)));
      recentWorldsDirectoryContainer.add(noRecentWorldsLabel);
    }
    count = buildPanel(textbookExampleWorldsContainer, 
      buildVectorFromDirectory(textbookExampleWorlds, aliceFilter), 
      false, null, 1);
    if (count <= 0) {
      textbookExampleWorldsDirectoryContainer.removeAll();
      JLabel noRecentWorldsLabel = new JLabel();
      noRecentWorldsLabel
        .setText(Messages.getString("No_textbook_worlds_"));
      noRecentWorldsLabel.setFont(new Font(font, 
        1, (int)(18 * fontSize / 12.0D)));
      textbookExampleWorldsDirectoryContainer.add(noRecentWorldsLabel);
    }
    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        StartUpContentPane.this.matchSizes();
      }
    });
    tutorialPanelLayout.setHgap(35);
    tutorialPanelLayout.setVgap(10);
    examplePanelLayout.setHgap(35);
    examplePanelLayout.setVgap(10);
    recentPanelLayout.setHgap(35);
    recentPanelLayout.setVgap(10);
    templatePanelLayout.setHgap(35);
    templatePanelLayout.setVgap(10);
    textbookPanelLayout.setHgap(35);
    textbookPanelLayout.setVgap(10);
  }
  
  public void preDialogShow(JDialog dialog) {
    super.preDialogShow(dialog);
    
    mainTabPane.setSelectedComponent(getTabForID(currentTab));
  }
  
  private void handleFileSelectionChange(File file) {
    openButton.setEnabled((file != null) && (file.exists()) && 
      (!file.isDirectory()));
  }
  



  public String getTitle()
  {
    return Messages.getString("Welcome_to_Alice__");
  }
  
  public void addOKActionListener(ActionListener l) {
    openButton.addActionListener(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    openButton.removeActionListener(l);
  }
  
  public void addCancelActionListener(ActionListener l) {
    cancelButton.addActionListener(l);
  }
  
  public void removeCancelActionListener(ActionListener l) {
    cancelButton.removeActionListener(l);
  }
  
  public boolean isTutorial() {
    if (currentlySelected != null) {
      return currentlySelected.type == 2;
    }
    return getTabID() == 2;
  }
  
  public boolean isSaveNeeded()
  {
    if (currentlySelected != null) {
      return currentlySelected.needToSave;
    }
    return true;
  }
  
  public File getFile()
  {
    if (getTabID() == 1) {
      return fileChooser.getSelectedFile();
    }
    if (currentlySelected != null) {
      return new File(currentlySelected.file);
    }
    return null;
  }
  

  private int getIDForTab(Component tab)
  {
    if (tab == fileChooser)
      return 1;
    if (tab == tutorialWorldsDirectoryContainer)
      return 2;
    if (tab == recentWorldsDirectoryContainer)
      return 3;
    if (tab == exampleWorldsDirectoryContainer)
      return 5;
    if (tab == textbookExampleWorldsDirectoryContainer)
      return 6;
    if (tab == templateWorldsDirectoryContainer)
      return 4;
    return 0;
  }
  
  private Component getTabForID(int tabID) {
    switch (tabID) {
    case 1: 
      return fileChooser;
    case 2: 
      return tutorialWorldsDirectoryContainer;
    case 3: 
      return recentWorldsDirectoryContainer;
    case 5: 
      return exampleWorldsDirectoryContainer;
    case 4: 
      return templateWorldsDirectoryContainer;
    case 6: 
      return textbookExampleWorldsDirectoryContainer;
    }
    return tutorialWorldsDirectoryContainer;
  }
  
  private int getTabID()
  {
    return getIDForTab(mainTabPane.getSelectedComponent());
  }
  
  public void setTabID(int tabID) {
    if (tabID == 1)
    {
      File currentDir = new File(
        authoringToolConfig.getValue("directories.worldsDirectory"));
      if (currentDir.exists()) {
        fileChooser.setCurrentDirectory(currentDir);
      }
    }
    if (tabID != -1) {
      currentTab = tabID;
      mainTabPane.setSelectedComponent(getTabForID(currentTab));
    }
  }
  
  private String makeNameFromFilename(String filename)
  {
    String name = filename.substring(0, filename.length() - 4);
    
    int last = name.lastIndexOf(File.separator);
    if ((last >= 0) && (last < name.length())) {
      name = name.substring(last + 1);
    }
    return name;
  }
  
  private String makeDirectoryNameFromFilename(String filename) {
    String name = new String(filename);
    
    if (filename.endsWith(File.separator)) {
      filename = filename.substring(filename.length());
    }
    int last = filename.lastIndexOf(File.separator);
    if ((last >= 0) && (last < filename.length())) {
      name = filename.substring(last + 1);
    }
    return name;
  }
  
  private Vector buildVectorFromString(String[] files) {
    Vector toReturn = new Vector();
    if (files != null) {
      for (int i = 0; i < files.length; i++) {
        String name = makeNameFromFilename(files[i]);
        StringObjectPair sop = new StringObjectPair(
          name, files[i]);
        toReturn.add(sop);
      }
    }
    return toReturn;
  }
  
  private Vector buildVectorFromDirectory(File dir, java.io.FileFilter f)
  {
    Vector toReturn = null;
    if ((dir != null) && (dir.isDirectory())) {
      toReturn = new Vector();
      File[] files = dir.listFiles(f);
      for (int i = 0; i < files.length; i++) {
        String name = "";
        if (files[i].isDirectory()) {
          name = makeDirectoryNameFromFilename(files[i].getName());
        } else {
          name = makeNameFromFilename(files[i].getName());
        }
        StringObjectPair sop = new StringObjectPair(
          Messages.getString(name), files[i].getAbsolutePath());
        toReturn.add(sop);
      }
    }
    return toReturn;
  }
  
  private ImageIcon getIconFromFile(File file) {
    String filename = file.getAbsolutePath();
    ImageIcon icon = null;
    try {
      if (filename.endsWith(".stl")) {
        DocumentBuilderFactory factory = 
          DocumentBuilderFactory.newInstance();
        

        DocumentBuilder builder = factory
          .newDocumentBuilder();
        Document document = builder.parse(file);
        NodeList nl = document
          .getElementsByTagName("stencilStack");
        if ((nl != null) && (nl.getLength() > 0)) {
          Node n = nl.item(0);
          if ((n instanceof Element)) {
            String worldFileName = ((Element)n)
              .getAttribute("world");
            file = new File(worldFileName);
          }
        }
      }
      ZipFile zip = new ZipFile(file);
      ZipEntry entry = zip.getEntry("thumbnail.png");
      if (entry != null) {
        InputStream stream = zip.getInputStream(entry);
        Image image = ImageIO.load(
          "png", stream);
        if (image != null) {
          icon = new ImageIcon(image);
        }
      }
      zip.close();
    } catch (Exception e) {
      return null;
    }
    return icon;
  }
  
  protected Component getTopContainer(Component innerContainer)
  {
    if (innerContainer == tutorialWorldsContainer)
      return tutorialWorldsDirectoryContainer;
    if (innerContainer == exampleWorldsContainer)
      return exampleScrollPane;
    if (innerContainer == templateWorldsContainer)
      return templateScrollPane;
    if (innerContainer == textbookExampleWorldsContainer) {
      return textbookExampleScrollPane;
    }
    return null;
  }
  

  protected String getBaseDirString(Component topLevelOwner)
  {
    if (topLevelOwner == tutorialWorldsDirectoryContainer)
      return TUTORIAL_STRING;
    if (topLevelOwner == exampleScrollPane)
      return EXAMPLES_STRING;
    if (topLevelOwner == templateScrollPane)
      return TEMPLATES_STRING;
    if (topLevelOwner == textbookExampleScrollPane) {
      return TEXTBOOK_EXAMPLES_STRING;
    }
    return "";
  }
  




  private int buildPanel(JPanel toBuild, Vector toAdd, boolean needToSave, File parentDir, int type)
  {
    int count = 0;
    
    if ((parentDir != null) || (toAdd != null)) {
      toBuild.removeAll();
    }
    if ((parentDir != null) && 
      (parentDir.getName().compareToIgnoreCase("Required") != 0)) {
      String parentDirName = Messages.getString("Back");
      StartUpIcon parentDirIcon = new StartUpIcon(parentDirName, 
        upDirectoryIcon, parentDir.getAbsolutePath(), false, 
        3, getTopContainer(toBuild));
      toBuild.add(parentDirIcon);
      count++;
    }
    if (toAdd != null) {
      for (int i = 0; i < toAdd.size(); i++) {
        StringObjectPair sop = 
          (StringObjectPair)toAdd.get(i);
        String name = sop.getString();
        String filename = (String)sop.getObject();
        File file = new File(filename);
        ImageIcon icon = basicIcon;
        if ((file.exists()) && (file.canRead())) {
          filename = file.getAbsolutePath();
          if (file.isDirectory()) {
            StartUpIcon dirIcon = new StartUpIcon(name, 
              directoryIcon, filename, false, 
              3, getTopContainer(toBuild));
            toBuild.add(dirIcon);
            count++;
          } else {
            boolean worldIsThere = true;
            if ((file.exists()) && (file.canRead())) {
              icon = getIconFromFile(file);
              if (icon == null) {
                icon = basicIcon;
              }
            } else {
              worldIsThere = false;
            }
            if (worldIsThere) {
              StartUpIcon sui = new StartUpIcon(name, icon, 
                filename, needToSave, type, 
                getTopContainer(toBuild));
              toBuild.add(sui);
              count++;
            }
          }
        }
      }
    }
    toBuild.revalidate();
    return count;
  }
  
  private void initializeFileChooser()
  {
    AikMin.setFontSize(12);
    











    fileChooser = new JFileChooser() {
      public void setSelectedFile(File file) {
        super.setSelectedFile(file);
        StartUpContentPane.this.handleFileSelectionChange(file);
      }
    };
    for (int i = 0; i < fileChooser.getComponentCount(); i++) {
      setButtonBackgroundColors(fileChooser.getComponent(i), 
        fileChooser.getBackground());
    }
    


    File currentDir = new File(
      authoringToolConfig.getValue("directories.worldsDirectory"));
    if (currentDir.exists()) {
      fileChooser.setCurrentDirectory(currentDir);
    }
    
    fileChooser.setFileFilter(worldFilter);
    fileChooser.setBackground(Color.white);
    fileChooser.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        StartUpContentPane.this.fileChooser_actionPerformed(e);
      }
    });
    fileChooser.setFileSelectionMode(0);
    mainTabPane.add(fileChooser, "<html><body leftmargin=5 marginwidth=5>" + 
      OPEN_STRING + "</body></html>");
    try {
      AikMin.setFontSize(Integer.parseInt(authoringToolConfig
        .getValue("fontSize")));
    }
    catch (Exception localException) {}
  }
  
  private void guiInit() {
    int fontSize = Integer.parseInt(authoringToolConfig
      .getValue("fontSize"));
    if (fontSize > 12) {
      setPreferredSize(new Dimension(550, 500 + fontSize * 
        3));
    } else
      setPreferredSize(new Dimension(550, 500));
    headerLabel.setIcon(headerImage);
    startTutorialButton.setIcon(tutorialButtonIcon);
    startTutorialButton.setCursor(new Cursor(
      12));
    exampleWorldsContainer.setBorder(null);
    tutorialWorldsContainer.setBorder(null);
    recentWorldsContainer.setBorder(null);
    templateWorldsContainer.setBorder(null);
    textbookExampleWorldsContainer.setBorder(null);
    authoringToolConfig.getValue("showStartUpDialog");
    stopShowingCheckBox.setSelected(authoringToolConfig.getValue(
      "showStartUpDialog").equalsIgnoreCase("true"));
    mainTabPane.setUI(new AliceTabbedPaneUI());
    mainTabPane.setOpaque(false);
    initializeFileChooser();
    int selectedTab = Integer.parseInt(authoringToolConfig
      .getValue("showStartUpDialog_OpenTab"));
    setTabID(selectedTab);
  }
  























  private void setButtonBackgroundColors(Component c, Color color)
  {
    if (!(c instanceof Button)) {
      c.setBackground(color);
    }
    if ((c instanceof Container)) {
      Container cont = (Container)c;
      for (int i = 0; i < cont.getComponentCount(); i++) {
        setButtonBackgroundColors(cont.getComponent(i), color);
      }
    }
  }
  
  private void jbInit() {
    setLayout(new GridBagLayout());
    

    Component component2 = Box.createGlue();
    buttonPanel.setLayout(new GridBagLayout());
    setBackground(Color.white);
    

    mainTabPane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        StartUpContentPane.this.mainTabPane_stateChanged(e);
      }
      
    });
    buttonPanel.setOpaque(false);
    

    int fontSize = Integer.parseInt(authoringToolConfig
      .getValue("fontSize"));
    openButton
      .setMinimumSize(new Dimension(fontSize * 8, fontSize * 2 + 5));
    openButton.setPreferredSize(new Dimension(fontSize * 8, 
      fontSize * 2 + 5));
    openButton.setText(Messages.getString("Open"));
    openButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        StartUpContentPane.authoringToolConfig.setValue("showStartUpDialog_OpenTab", 
          Integer.toString(StartUpContentPane.this.getIDForTab(mainTabPane
          .getSelectedComponent())));
      }
      

    });
    cancelButton.setMinimumSize(new Dimension(fontSize * 8, 
      fontSize * 2 + 5));
    cancelButton.setPreferredSize(new Dimension(fontSize * 8, 
      fontSize * 2 + 5));
    cancelButton.setText(Messages.getString("Cancel"));
    








    stopShowingCheckBox.setOpaque(false);
    stopShowingCheckBox.setText(
      Messages.getString("Show_this_dialog_at_start"));
    stopShowingCheckBox
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          StartUpContentPane.this.stopShowingCheckBox_actionPerformed(e);
        }
        

      });
    exampleWorldsContainer.setLayout(examplePanelLayout);
    exampleScrollPane.setBackground(Color.white);
    exampleScrollPane.setBorder(null);
    exampleScrollPane.setOpaque(false);
    exampleScrollPane.getViewport().setBackground(Color.white);
    exampleWorldsContainer.setBackground(Color.white);
    exampleWorldsContainer.setAlignmentX(0.0F);
    exampleWorldsContainer.setAlignmentY(0.0F);
    
    exampleWorldsDirLabel.setText(" ");
    exampleWorldsDirectoryContainer.setLayout(new GridBagLayout());
    exampleWorldsDirectoryContainer.setOpaque(true);
    exampleWorldsDirectoryContainer.setBackground(Color.white);
    exampleWorldsDirectoryContainer.add(exampleWorldsDirLabel, 
      new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 
      17, 0, 
      new Insets(2, 2, 2, 0), 0, 0));
    exampleWorldsDirectoryContainer.add(exampleScrollPane, 
      new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 
      10, 1, 
      new Insets(0, 0, 0, 0), 0, 0));
    

    recentWorldsContainer.setLayout(recentPanelLayout);
    recentScrollPane.setBackground(Color.white);
    recentScrollPane.setBorder(null);
    recentScrollPane.getViewport().setBackground(Color.white);
    recentScrollPane.setOpaque(false);
    recentWorldsContainer.setBackground(Color.white);
    recentWorldsContainer.setAlignmentX(0.0F);
    recentWorldsContainer.setAlignmentY(0.0F);
    
    recentWorldsDirLabel.setText(" ");
    recentWorldsDirectoryContainer.setLayout(new GridBagLayout());
    recentWorldsDirectoryContainer.setOpaque(true);
    recentWorldsDirectoryContainer.setBackground(Color.white);
    recentWorldsDirectoryContainer.add(recentWorldsDirLabel, 
      new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 
      17, 0, 
      new Insets(2, 2, 2, 0), 0, 0));
    recentWorldsDirectoryContainer.add(recentScrollPane, 
      new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 
      10, 1, 
      new Insets(0, 0, 0, 0), 0, 0));
    

    templateWorldsContainer.setLayout(templatePanelLayout);
    templateScrollPane.getViewport().setBackground(Color.white);
    templateScrollPane.setOpaque(false);
    templateScrollPane.setBorder(null);
    templateScrollPane.setBackground(Color.white);
    templateWorldsContainer.setBackground(Color.white);
    templateWorldsContainer.setAlignmentX(0.0F);
    templateWorldsContainer.setAlignmentY(0.0F);
    
    templateWorldsDirLabel.setText(" ");
    templateWorldsDirectoryContainer.setLayout(new GridBagLayout());
    templateWorldsDirectoryContainer.setOpaque(true);
    templateWorldsDirectoryContainer.setBackground(Color.white);
    templateWorldsDirectoryContainer.add(templateWorldsDirLabel, 
      new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 
      17, 0, 
      new Insets(2, 2, 2, 0), 0, 0));
    templateWorldsDirectoryContainer.add(templateScrollPane, 
      new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 
      10, 1, 
      new Insets(0, 0, 0, 0), 0, 0));
    

    textbookExampleWorldsContainer.setLayout(textbookPanelLayout);
    textbookExampleScrollPane.getViewport().setBackground(Color.white);
    textbookExampleScrollPane.setOpaque(false);
    textbookExampleScrollPane.setBorder(null);
    textbookExampleScrollPane.setBackground(Color.white);
    textbookExampleWorldsContainer.setBackground(Color.white);
    textbookExampleWorldsContainer.setAlignmentX(0.0F);
    textbookExampleWorldsContainer.setAlignmentY(0.0F);
    
    textbookExampleWorldsDirLabel.setText(" ");
    textbookExampleWorldsDirectoryContainer.setLayout(new GridBagLayout());
    textbookExampleWorldsDirectoryContainer.setOpaque(true);
    textbookExampleWorldsDirectoryContainer.setBackground(Color.white);
    textbookExampleWorldsDirectoryContainer.add(
      textbookExampleWorldsDirLabel, new GridBagConstraints(0, 0, 1, 
      1, 0.0D, 0.0D, 17, 
      0, new Insets(2, 2, 2, 0), 0, 0));
    textbookExampleWorldsDirectoryContainer.add(textbookExampleScrollPane, 
      new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 
      10, 1, 
      new Insets(0, 0, 0, 0), 0, 0));
    

    tutorialButtonPanel.setBackground(Color.white);
    tutorialButtonPanel.setLayout(new GridBagLayout());
    startTutorialButton.setBackground(Color.white);
    startTutorialButton.setBorder(null);
    startTutorialButton.setMaximumSize(new Dimension(120, 90));
    startTutorialButton.setMinimumSize(new Dimension(120, 90));
    startTutorialButton.setPreferredSize(new Dimension(120, 90));
    startTutorialButton.setToolTipText(
      Messages.getString("Start_the_Alice_tutorial"));
    startTutorialButton
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          StartUpContentPane.this.startTutorialButton_actionPerformed(e);
        }
        
      });
    jLabel1.setText(Messages.getString("or_continue_a_tutorial_"));
    
    tutorialButtonPanel.add(startTutorialButton, new GridBagConstraints(0, 
      0, 2, 1, 0.0D, 0.0D, 10, 
      0, new Insets(2, 0, 2, 0), 0, 0));
    tutorialButtonPanel.add(jLabel1, new GridBagConstraints(0, 1, 1, 1, 
      0.0D, 0.0D, 16, 
      0, new Insets(2, 3, 2, 0), 0, 0));
    tutorialButtonPanel.add(component2, new GridBagConstraints(1, 1, 1, 1, 
      1.0D, 1.0D, 10, 1, 
      new Insets(0, 0, 0, 0), 0, 0));
    
    tutorialWorldsContainer.setLayout(tutorialPanelLayout);
    tutorialScrollPane.getViewport().setBackground(Color.white);
    tutorialScrollPane.setOpaque(false);
    tutorialScrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
    tutorialScrollPane.setBackground(Color.white);
    tutorialWorldsContainer.setBackground(Color.white);
    tutorialWorldsContainer.setAlignmentX(0.0F);
    tutorialWorldsContainer.setAlignmentY(0.0F);
    
    tutorialWorldsDirectoryContainer.setLayout(new GridBagLayout());
    tutorialWorldsDirectoryContainer.setOpaque(true);
    tutorialWorldsDirectoryContainer.setBackground(Color.white);
    tutorialWorldsDirectoryContainer.add(
      tutorialButtonPanel, new GridBagConstraints(0, 0, 1, 
      1, 0.0D, 0.0D, 17, 
      1, new Insets(2, 2, 2, 0), 0, 0));
    tutorialWorldsDirectoryContainer.add(tutorialScrollPane, 
      new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 
      10, 1, 
      new Insets(0, 0, 0, 0), 0, 0));
    

    add(buttonPanel, new GridBagConstraints(0, 2, 2, 1, 1.0D, 1.0D, 
      10, 2, 
      new Insets(0, 0, 0, 0), 0, 0));
    buttonPanel.add(openButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 
      0.0D, 12, 0, 
      new Insets(4, 0, 0, 4), 0, 0));
    buttonPanel.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 0.0D, 
      0.0D, 12, 0, 
      new Insets(4, 0, 0, 4), 0, 0));
    buttonPanel.add(Box.createGlue(), new GridBagConstraints(0, 1, 1, 2, 
      1.0D, 0.0D, 10, 
      2, new Insets(0, 0, 0, 0), 0, 0));
    add(mainTabPane, new GridBagConstraints(0, 1, 2, 1, 1.0D, 1.0D, 
      10, 1, new Insets(
      0, 0, 0, 0), 0, 0));
    mainTabPane.add(tutorialWorldsDirectoryContainer, 
      "<html><body leftmargin=5 marginwidth=5>" + TUTORIAL_STRING + 
      "</body></html>");
    mainTabPane.add(recentWorldsDirectoryContainer, 
      "<html><body leftmargin=5 marginwidth=5>" + RECENT_STRING + 
      "</body></html>");
    mainTabPane.add(templateWorldsDirectoryContainer, 
      "<html><body leftmargin=5 marginwidth=5>" + TEMPLATES_STRING + 
      "</body></html>");
    mainTabPane.add(exampleWorldsDirectoryContainer, 
      "<html><body leftmargin=5 marginwidth=5>" + EXAMPLES_STRING + 
      "</body></html>");
    mainTabPane.add(textbookExampleWorldsDirectoryContainer, 
      "<html><body leftmargin=5 marginwidth=5>" + 
      TEXTBOOK_EXAMPLES_STRING + "</body></html>");
    

    add(stopShowingCheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 
      16, 2, 
      new Insets(0, 2, 0, 0), 0, 0));
    add(headerLabel, new GridBagConstraints(0, 0, 2, 1, 0.0D, 0.0D, 
      10, 0, new Insets(
      0, 0, 0, 0), 0, 0));
    
    tutorialScrollPane.getViewport().add(tutorialWorldsContainer, null);
    templateScrollPane.getViewport().add(templateWorldsContainer, null);
    exampleScrollPane.getViewport().add(exampleWorldsContainer, null);
    recentScrollPane.getViewport().add(recentWorldsContainer, null);
    textbookExampleScrollPane.getViewport().add(textbookExampleWorldsContainer, null);
    
    templateScrollPane.getVerticalScrollBar().setUnitIncrement(50);
    exampleScrollPane.getVerticalScrollBar().setUnitIncrement(50);
    recentScrollPane.getVerticalScrollBar().setUnitIncrement(50);
    textbookExampleScrollPane.getVerticalScrollBar().setUnitIncrement(50);
  }
  
  private void matchSizes()
  {
    tutorialWorldsContainer.setSize(
      recentScrollPane.getVisibleRect().width, 
      tutorialWorldsContainer.getHeight());
    recentWorldsContainer.setSize(recentScrollPane.getVisibleRect().width, 
      recentWorldsContainer.getHeight());
    templateWorldsContainer.setSize(
      templateScrollPane.getVisibleRect().width, 
      templateWorldsContainer.getHeight());
    exampleWorldsContainer.setSize(
      exampleScrollPane.getVisibleRect().width, 
      exampleWorldsContainer.getHeight());
    textbookExampleWorldsContainer.setSize(
      textbookExampleScrollPane.getVisibleRect().width, 
      textbookExampleWorldsContainer.getHeight());
  }
  













  private void setFileChooserButtons()
  {
    remove(buttonPanel);
  }
  










  private void setRegularButtons()
  {
    add(buttonPanel, new GridBagConstraints(0, 2, 2, 1, 1.0D, 1.0D, 
      10, 2, 
      new Insets(0, 0, 0, 0), 0, 0));
  }
  
  private void mainTabPane_stateChanged(ChangeEvent e) {
    if (currentlySelected != null) {
      currentlySelected.deSelect();
      currentlySelected = null;
    }
    if (mainTabPane.getSelectedComponent() == fileChooser) {
      setFileChooserButtons();
      handleFileSelectionChange(fileChooser.getSelectedFile());
    } else {
      setRegularButtons();
      openButton.setEnabled(false);
    }
  }
  
  private void stopShowingCheckBox_actionPerformed(ActionEvent e) {
    if (stopShowingCheckBox.isSelected()) {
      authoringToolConfig.setValue("showStartUpDialog", "true");
    } else {
      authoringToolConfig.setValue("showStartUpDialog", "false");
    }
  }
  
  private void fileChooser_actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();
    if (actionCommand.equals("ApproveSelection")) {
      openButton.setEnabled(true);
      openButton.doClick();
    } else if (actionCommand.equals("CancelSelection")) {
      cancelButton.doClick();
    }
  }
  



  private void startTutorialButton_actionPerformed(ActionEvent e)
  {
    openButton.setEnabled(true);
    openButton.doClick();
  }
  
  protected class StartUpIcon extends JLabel implements MouseListener
  {
    protected static final int STANDARD = 1;
    protected static final int TUTORIAL = 2;
    protected static final int DIRECTORY = 3;
    protected boolean isSelected = false;
    protected String file;
    protected boolean needToSave = false;
    
    protected int type;
    protected Component owner;
    
    public StartUpIcon(String name, ImageIcon icon, String file, boolean needToSave, int type, Component owner)
    {
      super(icon, 0);
      this.file = file;
      this.needToSave = needToSave;
      this.type = type;
      this.owner = owner;
      
      setBackground(StartUpContentPane.BACKGROUND_COLOR);
      setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 
        2));
      setVerticalTextPosition(3);
      setHorizontalTextPosition(0);
      Dimension size = new Dimension(
        icon.getIconWidth() + 4, icon.getIconHeight() + 24);
      setPreferredSize(size);
      setMinimumSize(size);
      setMaximumSize(size);
      if (type == 3) {
        setCursor(
          Cursor.getPredefinedCursor(12));
      }
      setOpaque(false);
      addMouseListener(this);
    }
    
    protected JPanel getContainer(Component topLevelOwner)
    {
      if (topLevelOwner == tutorialWorldsDirectoryContainer)
        return tutorialWorldsContainer;
      if (topLevelOwner == exampleScrollPane)
        return exampleWorldsContainer;
      if (topLevelOwner == templateScrollPane)
        return templateWorldsContainer;
      if (topLevelOwner == textbookExampleScrollPane) {
        return textbookExampleWorldsContainer;
      }
      return null;
    }
    




































    protected void changeDirectory(String newDirectory)
    {
      File newDir = new File(newDirectory);
      File parentDir = newDir.getParentFile();
      
      String baseDir = getBaseDirString(owner);
      if ((owner instanceof JScrollPane))
      {
        ((JScrollPane)owner).getVerticalScrollBar().setValue(0);
      }
      if ((newDir.compareTo(exampleWorlds) == 0) || 
        (newDir.compareTo(templateWorlds) == 0) || 
        
        (newDir.compareTo(tutorialWorlds) == 0)) {
        StartUpContentPane.this.buildPanel(getContainer(owner), 
          StartUpContentPane.access$14(StartUpContentPane.this, newDir, aliceFilter), 
          needToSave, null, 1);
      }
      else {
        StartUpContentPane.this.buildPanel(getContainer(owner), 
          StartUpContentPane.access$14(StartUpContentPane.this, newDir, aliceFilter), 
          needToSave, parentDir, 1);
      }
    }
    

    public void deSelect()
    {
      if (isSelected) {
        currentlySelected = null;
        isSelected = false;
        setBackground(StartUpContentPane.BACKGROUND_COLOR);
        setOpaque(false);
        repaint();
        setForeground((Color)
          UIManager.get("Label.foreground"));
      }
    }
    
    public void mouseClicked(MouseEvent e) {
      if (type == 3) {
        changeDirectory(file);
      } else {
        if (!isSelected) {
          isSelected = true;
          if (currentlySelected != null) {
            currentlySelected.deSelect();
          }
          if (!openButton.isEnabled()) {
            openButton.setEnabled(true);
          }
          setBackground(StartUpContentPane.SELECTED_COLOR);
          setOpaque(true);
          setForeground(StartUpContentPane.SELECTED_TEXT_COLOR);
          currentlySelected = this;
          currentlySelected.repaint();
        }
        if (e.getClickCount() == 2) {
          openButton.doClick();
        }
      }
    }
    
    public void mouseReleased(MouseEvent e) {}
    
    public void mousePressed(MouseEvent e) {}
    
    public void mouseExited(MouseEvent e) {}
    
    public void mouseEntered(MouseEvent e) {}
  }
}
