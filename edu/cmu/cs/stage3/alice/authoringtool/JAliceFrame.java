package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BehaviorGroupsEditor;
import edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor;
import edu.cmu.cs.stage3.alice.authoringtool.util.AlicePopupMenu;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDClipboard;
import edu.cmu.cs.stage3.alice.authoringtool.util.InvisibleSplitPaneUI;
import edu.cmu.cs.stage3.alice.authoringtool.util.TrashComponent;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;



public class JAliceFrame
  extends JFrame
{
  protected AuthoringTool authoringTool;
  public static boolean isLogging = false;
  
  public static int SCENE_EDITOR_SMALL_MODE = 1;
  public static int SCENE_EDITOR_LARGE_MODE = 2;
  protected int guiMode = SCENE_EDITOR_SMALL_MODE;
  
  WorldTreeComponent worldTreeComponent;
  
  DragFromComponent dragFromComponent;
  
  TabbedEditorComponent tabbedEditorComponent;
  public SceneEditor sceneEditor;
  BehaviorGroupsEditor behaviorGroupsEditor;
  TrashComponent trashComponent;
  protected ComponentImageFactory componentImageFactory = new ComponentImageFactory();
  
  StatusBar statusBar;
  
  protected Configuration authoringToolConfig = Configuration.getLocalConfiguration(getClass().getPackage());
  

  protected int recentWorldsPosition = -1;
  protected ArrayList recentWorlds = new ArrayList();
  
  protected int maxRecentWorlds;
  
  protected ActionListener recentWorldsListener;
  
  public JAliceFrame(AuthoringTool authoringTool)
  {
    this.authoringTool = authoringTool;
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    jbInit();
    guiInit();
    setGuiMode(SCENE_EDITOR_SMALL_MODE);
    recentWorldsInit();
    
    setIconImage(AuthoringToolResources.getAliceSystemIconImage());
  }
  
  protected class ComponentImageFactory
    extends JPanel {
    public ComponentImageFactory() { setPreferredSize(new Dimension(0, 0)); }
    
    public BufferedImage manufactureImage(JComponent component) {
      boolean isShowing = component.isShowing();
      Container parent = component.getParent();
      if (!isShowing)
      {

        if (parent != null) {
          parent.remove(component);
        }
        add(component);
      }
      doLayout();
      Dimension d = component.getPreferredSize();
      BufferedImage image = new BufferedImage(width, height, 2);
      Graphics2D g = image.createGraphics();
      component.paintAll(g);
      if (!isShowing)
      {

        remove(component);
        if (parent != null) {
          parent.add(component);
        }
      }
      return image;
    }
  }
  
  public Image getImageForComponent(JComponent c) {
    BufferedImage image = componentImageFactory.manufactureImage(c);
    return image;
  }
  
  public void HACK_goToRedAlert() {
    getContentPane().setBackground(new java.awt.Color(192, 0, 0));
  }
  
  public void HACK_standDownFromRedAlert() { getContentPane().setBackground(edu.cmu.cs.stage3.alice.scenegraph.Color.valueOf(authoringToolConfig.getValue("backgroundColor")).createAWTColor()); }
  
  private void guiInit()
  {
    setDefaultCloseOperation(0);
    
    if (authoringToolConfig.getValue("useSingleFileLoadStore").equalsIgnoreCase("true")) {
      fileMenu.remove(openZippedWorldItem);
    }
    String t = authoringToolConfig.getValue("backgroundColor");
    if (t == null) {
      getContentPane().setBackground(new java.awt.Color(0, 78, 152));
    } else {
      getContentPane().setBackground(edu.cmu.cs.stage3.alice.scenegraph.Color.valueOf(authoringToolConfig.getValue("backgroundColor")).createAWTColor());
    }
    Configuration.addConfigurationListener(
      new ConfigurationListener() {
        public void changing(ConfigurationEvent ev) {}
        
        public void changed(ConfigurationEvent ev) { if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.backgroundColor")) {
            getContentPane().setBackground(edu.cmu.cs.stage3.alice.scenegraph.Color.valueOf(authoringToolConfig.getValue("backgroundColor")).createAWTColor());
          } else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.useSingleFileLoadStore")) {
            if (ev.getNewValue().equalsIgnoreCase("true")) {
              fileMenu.remove(openZippedWorldItem);
            }
            else if (!fileMenu.isMenuComponent(openZippedWorldItem)) {
              fileMenu.add(openZippedWorldItem, 2);
            }
            
          }
          
        }
        

      });
    worldTreeComponent = new WorldTreeComponent(authoringTool);
    dragFromComponent = new DragFromComponent(authoringTool);
    
    tabbedEditorComponent = new TabbedEditorComponent(authoringTool);
    behaviorGroupsEditor = new BehaviorGroupsEditor();
    sceneEditor = new SceneEditor();
    sceneEditor.setAuthoringTool(authoringTool);
    trashComponent = new TrashComponent(authoringTool);
    statusBar = new StatusBar(authoringTool);
    

    worldTreePanel.add(worldTreeComponent, "Center");
    dragFromPanel.add(dragFromComponent, "Center");
    
    editorPanel.add(tabbedEditorComponent, "Center");
    scenePanel.add(sceneEditor, "Center");
    behaviorPanel.add(behaviorGroupsEditor, "Center");
    
    trashPanel.add(trashComponent, "South");
    if (authoringToolConfig.getValue("showWorldStats").equalsIgnoreCase("true")) {
      getContentPane().add(statusBar, "South");
    }
    

    int screenWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    int screenHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    
    String boundsString = authoringToolConfig.getValue("mainWindowBounds");
    StringTokenizer st = new StringTokenizer(boundsString, " \t,");
    
    int x = 0;
    int y = 0;
    int width = screenWidth;
    int height = screenHeight;
    if (st.countTokens() == 4) {
      try {
        x = Integer.parseInt(st.nextToken());
        y = Integer.parseInt(st.nextToken());
        width = Integer.parseInt(st.nextToken());
        height = Integer.parseInt(st.nextToken());
      } catch (NumberFormatException e) {
        AuthoringTool.showErrorDialog(Messages.getString("Parse_error_in_config_value__mainWindowBounds"), null);
      }
    } else {
      AuthoringTool.showErrorDialog(Messages.getString("Incorrect_number_of_tokens_in_config_value__mainWindowBounds"), null);
    }
    if ((width > screenWidth) || (height > screenHeight)) {
      setExtendedState(6);
    } else {
      setBounds(x, y, width, height);
    }
    



    leftRightSplitPane.setDividerLocation(300);
    leftRightSplitPane.setResizeWeight(0.0D);
    worldTreeDragFromSplitPane.setDividerLocation((int)(0.32D * height));
    worldTreeDragFromSplitPane.setResizeWeight(0.0D);
    editorBehaviorSplitPane.setDividerLocation((int)(0.275D * height));
    editorBehaviorSplitPane.setResizeWeight(0.0D);
    smallSceneBehaviorSplitPane.setDividerLocation((int)(1.3333333333333333D * (editorBehaviorSplitPane.getDividerLocation() - 36)));
    smallSceneBehaviorSplitPane.setResizeWeight(0.0D);
    


    leftRightSplitPane.setUI(new InvisibleSplitPaneUI());
    worldTreeDragFromSplitPane.setUI(new InvisibleSplitPaneUI());
    editorBehaviorSplitPane.setUI(new InvisibleSplitPaneUI());
    smallSceneBehaviorSplitPane.setUI(new InvisibleSplitPaneUI());
    

    leftRightSplitPane.setBorder(null);
    worldTreeDragFromSplitPane.setBorder(null);
    editorBehaviorSplitPane.setBorder(null);
    smallSceneBehaviorSplitPane.setBorder(null);
    

    worldTreePanel.setMinimumSize(new Dimension(0, 0));
    dragFromPanel.setMinimumSize(new Dimension(0, 0));
    editorPanel.setMinimumSize(new Dimension(0, 0));
    smallSceneBehaviorPanel.setMinimumSize(new Dimension(0, 0));
    scenePanel.setMinimumSize(new Dimension(0, 0));
    behaviorPanel.setMinimumSize(new Dimension(0, 0));
    rightPanel.setMinimumSize(new Dimension(0, 0));
    


    authoringTool.addElementSelectionListener(dragFromComponent);
    

    clipboardPanel.setLayout(new BoxLayout(clipboardPanel, 0));
    updateClipboards();
    
    worldTreeComponent.startListening(authoringTool);
    



























    if (AuthoringToolResources.areExperimentalFeaturesEnabled()) {
      helpMenu.add(onScreenHelpItem, 1);
      buttonPanel.add(teachMeButton, new GridBagConstraints(4, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(1, 16, 0, 0), 0, 0));
    }
    getContentPane().add(componentImageFactory, "West");
    

    if (AikMin.isMAC()) {
      addWindowFocusListener(new WindowFocusListener() {
        Boolean flag = Boolean.valueOf(ToolTipManager.sharedInstance().isEnabled());
        
        public void windowGainedFocus(WindowEvent arg0) {
          if (flag.booleanValue())
            ToolTipManager.sharedInstance().setEnabled(true);
          setEnabled(true);
          AlicePopupMenu.showPopup();
        }
        
        public void windowLostFocus(WindowEvent arg0)
        {
          if (flag.booleanValue()) {
            ToolTipManager.sharedInstance().setEnabled(false);
          }
        }
      });
    }
  }
  

  private void recentWorldsInit()
  {
    recentWorldsListener = new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        JMenuItem item = (JMenuItem)ev.getSource();
        if (authoringTool.loadWorld(item.getText(), true) == 2) {
          recentWorlds.remove(item.getText());
          updateRecentWorlds();
        }
        
      }
    };
    String[] recentWorldsStrings = authoringToolConfig.getValueList("recentWorlds.worlds");
    for (int i = 0; i < recentWorldsStrings.length; i++) {
      recentWorlds.add(recentWorldsStrings[i]);
    }
    
    String max = authoringToolConfig.getValue("recentWorlds.maxWorlds");
    maxRecentWorlds = Integer.parseInt(max);
    
    recentWorldsPosition = (fileMenu.getMenuComponentCount() - 2);
    fileMenu.insertSeparator(recentWorldsPosition++);
    
    updateRecentWorlds();
  }
  

















  public void actionInit(Actions actions)
  {
    newWorldItem.setAction(newWorldAction);
    openWorldItem.setAction(openWorldAction);
    saveWorldItem.setAction(saveWorldAction);
    saveWorldAsItem.setAction(saveWorldAsAction);
    saveForWebItem.setAction(saveForWebAction);
    addCharacterItem.setAction(addCharacterAction);
    add3DTextItem.setAction(add3DTextAction);
    exportMovieItem.setAction(exportMovieAction);
    importItem.setAction(importObjectAction);
    makeBillboardItem.setAction(makeBillboardAction);
    exitItem.setAction(quitAction);
    preferencesItem.setAction(preferencesAction);
    worldInfoItem.setAction(showWorldInfoAction);
    exampleWorldsItem.setAction(openExampleWorldAction);
    aboutItem.setAction(aboutAction);
    onScreenHelpItem.setAction(onScreenHelpAction);
    playButton.setAction(playAction);
    undoButton.setAction(undoAction);
    redoButton.setAction(redoAction);
    addObjectButton.setAction(addCharacterAction);
    teachMeButton.setAction(launchTutorialAction);
    selectTutorialMenuItem.setAction(launchTutorialFileAction);
    tutorialEditor.setAction(launchTutorialEditor);
    softwareUpdate.setAction(launchSoftwareUpdate);
    license.setAction(licenseAction);
    showStdOutItem.setAction(showStdOutDialogAction);
    showStdErrItem.setAction(showStdErrDialogAction);
    printItem.setAction(showPrintDialogAction);
    
    instructorDemoItem.setAction(logInstructorIntervention);
  }
  





  public void setWorld(World world)
  {
    worldTreeComponent.setWorld(world);
    behaviorGroupsEditor.setObject(world);
    sceneEditor.setObject(world);
    tabbedEditorComponent.setWorld(world);
  }
  
  public void setGuiMode(int guiMode) {
    this.guiMode = guiMode;
    int dividerLocation1 = leftRightSplitPane.getDividerLocation();
    int dividerLocation2 = smallSceneBehaviorSplitPane.getDividerLocation();
    rightPanel.removeAll();
    scenePanel.removeAll();
    if (guiMode == SCENE_EDITOR_SMALL_MODE) {
      scenePanel.add(sceneEditor, "Center");
      rightPanel.add(editorBehaviorSplitPane, "Center");
      sceneEditor.setGuiMode(SceneEditor.SMALL_MODE);
    } else if (guiMode == SCENE_EDITOR_LARGE_MODE) {
      rightPanel.add(sceneEditor, "Center");
      sceneEditor.setGuiMode(SceneEditor.LARGE_MODE);
    }
    leftRightSplitPane.setDividerLocation(dividerLocation1);
    smallSceneBehaviorSplitPane.setDividerLocation(dividerLocation2);
  }
  













  public int getGuiMode()
  {
    return guiMode;
  }
  
  public void showStatusPanel(boolean b) {
    if (b) {
      getContentPane().add(statusBar, "South");
    } else {
      getContentPane().remove(statusBar);
    }
    getContentPane().validate();
    getContentPane().repaint();
  }
  
  public void registerKeyboardAction(ActionListener actionListener, String command, KeyStroke keyStroke, int condition) {
    mainPanel.registerKeyboardAction(actionListener, command, keyStroke, condition);
  }
  
  public void updateRecentWorlds(String pathname) {
    int pos = recentWorlds.indexOf(pathname);
    if (pos > -1) {
      recentWorlds.remove(pos);
    }
    recentWorlds.add(0, pathname);
    updateRecentWorlds();
  }
  
  public void updateRecentWorlds()
  {
    int numExtra = recentWorlds.size() - maxRecentWorlds;
    for (int i = 0; i < numExtra; i++) {
      recentWorlds.remove(maxRecentWorlds);
    }
    

    int recentWorldsEndPosition = fileMenu.getMenuComponentCount() - 2;
    int numEntries = recentWorldsEndPosition - recentWorldsPosition;
    for (int i = 0; i < numEntries; i++) {
      fileMenu.remove(recentWorldsPosition);
    }
    

    if (recentWorlds.size() > 0)
    {

      int i = 0; for (Iterator iter = recentWorlds.iterator(); iter.hasNext(); i++) {
        String pathname = (String)iter.next();
        JMenuItem item = new JMenuItem(pathname);
        item.addActionListener(recentWorldsListener);
        fileMenu.insert(item, recentWorldsPosition + i);
        if (pathname.length() > 60) {
          int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
          item.setToolTipText(pathname);
          item.setPreferredSize(new Dimension(500, fontSize + 10));
        }
      }
    } else {
      JMenuItem item = new JMenuItem(Messages.getString("_No_Recent_Worlds_"));
      item.setEnabled(false);
      fileMenu.insert(item, recentWorldsPosition);
    }
    

    authoringToolConfig.setValueList("recentWorlds.worlds", (String[])recentWorlds.toArray(new String[0]));
  }
  
  public void updateClipboards() {
    try {
      int numClipboards = Integer.parseInt(authoringToolConfig.getValue("numberOfClipboards"));
      int current = clipboardPanel.getComponentCount();
      if (numClipboards > current) {
        for (int i = current; i < numClipboards; i++) {
          clipboardPanel.add(new DnDClipboard());
        }
      } else if ((numClipboards < current) && (numClipboards >= 0)) {
        for (int i = numClipboards; i < current; i++) {
          clipboardPanel.remove(0);
        }
      }
      clipboardPanel.revalidate();
      clipboardPanel.repaint();
    } catch (NumberFormatException e) {
      AuthoringTool.showErrorDialog(Messages.getString("illegal_number_of_clipboards__") + authoringToolConfig.getValue("numberOfClipboards"), null);
    }
  }
  
  public WorldTreeComponent getWorldTreeComponent() {
    return worldTreeComponent;
  }
  
  public TabbedEditorComponent getTabbedEditorComponent() {
    return tabbedEditorComponent;
  }
  




  JMenuBar menuBar = new JMenuBar();
  JMenu fileMenu = new JMenu();
  JMenuItem newWorldItem = new JMenuItem();
  JMenuItem openWorldItem = new JMenuItem();
  JMenuItem openZippedWorldItem = new JMenuItem();
  JMenuItem saveWorldItem = new JMenuItem();
  JMenuItem saveWorldAsItem = new JMenuItem();
  JMenuItem saveForWebItem = new JMenuItem();
  JMenuItem addCharacterItem = new JMenuItem();
  JMenuItem importItem = new JMenuItem();
  JMenuItem makeBillboardItem = new JMenuItem();
  JMenuItem exitItem = new JMenuItem();
  JMenu editMenu = new JMenu();
  JMenuItem preferencesItem = new JMenuItem();
  JMenu helpMenu = new JMenu();
  JMenuItem exampleWorldsItem = new JMenuItem();
  JMenuItem aboutItem = new JMenuItem();
  JMenuItem worldInfoItem = new JMenuItem();
  JMenuItem instructorDemoItem = new JMenuItem();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel toolBarPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel buttonPanel = new JPanel();
  JButton playButton = new JButton();
  JPanel clipboardPanel = new JPanel();
  JPanel trashPanel = new JPanel();
  JPanel mainPanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  JButton undoButton = new JButton();
  JButton redoButton = new JButton();
  
  JPanel authoringPanel = new JPanel();
  
  BorderLayout borderLayout3 = new BorderLayout();
  BorderLayout borderLayout4 = new BorderLayout();
  JSplitPane leftRightSplitPane = new JSplitPane();
  JPanel leftPanel = new JPanel();
  JPanel rightPanel = new JPanel();
  JPanel scenePanel = new JPanel();
  JSplitPane worldTreeDragFromSplitPane = new JSplitPane();
  JPanel smallSceneBehaviorPanel = new JPanel();
  JSplitPane smallSceneBehaviorSplitPane = new JSplitPane();
  BorderLayout borderLayout5 = new BorderLayout();
  JPanel worldTreePanel = new JPanel();
  JPanel dragFromPanel = new JPanel();
  BorderLayout borderLayout6 = new BorderLayout();
  BorderLayout borderLayout7 = new BorderLayout();
  BorderLayout borderLayout8 = new BorderLayout();
  JSplitPane editorBehaviorSplitPane = new JSplitPane();
  JPanel editorPanel = new JPanel();
  JPanel behaviorPanel = new JPanel();
  BorderLayout borderLayout9 = new BorderLayout();
  BorderLayout borderLayout10 = new BorderLayout();
  BorderLayout borderLayout11 = new BorderLayout();
  Component glue;
  JMenuItem onScreenHelpItem = new JMenuItem();
  JMenu toolsMenu = new JMenu();
  BorderLayout borderLayout12 = new BorderLayout();
  BorderLayout borderLayout13 = new BorderLayout();
  Border border1;
  Border border2;
  Border border3;
  Border border4;
  Border border5;
  Border border6;
  Border border7;
  Border border8;
  JButton addObjectButton = new JButton();
  JMenuItem add3DTextItem = new JMenuItem();
  JMenuItem exportMovieItem = new JMenuItem();
  JButton teachMeButton = new JButton();
  JMenuItem selectTutorialMenuItem = new JMenuItem();
  JMenuItem tutorialEditor = new JMenuItem();
  JMenuItem softwareUpdate = new JMenuItem();
  JMenuItem license = new JMenuItem();
  JMenuItem showStdOutItem = new JMenuItem();
  JMenuItem showStdErrItem = new JMenuItem();
  JMenuItem printItem = new JMenuItem();
  
  private void jbInit() {
    glue = Box.createGlue();
    border1 = BorderFactory.createEmptyBorder(10, 10, 0, 10);
    border2 = BorderFactory.createEmptyBorder(0, 10, 10, 10);
    border3 = BorderFactory.createEmptyBorder(2, 10, 10, 10);
    border4 = BorderFactory.createLineBorder(java.awt.Color.black, 1);
    border5 = BorderFactory.createLineBorder(java.awt.Color.black, 1);
    border6 = BorderFactory.createLineBorder(java.awt.Color.black, 1);
    border7 = BorderFactory.createLineBorder(java.awt.Color.black, 1);
    border8 = BorderFactory.createLineBorder(java.awt.Color.black, 1);
    fileMenu.setMnemonic('F');
    fileMenu.setText(Messages.getString("File"));
    newWorldItem.setText(Messages.getString("New_World"));
    openWorldItem.setText(Messages.getString("Open_World"));
    openZippedWorldItem.setText(Messages.getString("Open_Single_File_World"));
    saveWorldItem.setText(Messages.getString("Save_World"));
    saveWorldAsItem.setText(Messages.getString("Save_World_As___"));
    saveForWebItem.setText(Messages.getString("Export_As_A_Web_Page___"));
    addCharacterItem.setText(Messages.getString("Add_Character"));
    importItem.setText(Messages.getString("Import___"));
    makeBillboardItem.setText(Messages.getString("Make_Billboard"));
    exitItem.setText(Messages.getString("Exit"));
    editMenu.setMnemonic('E');
    editMenu.setText(Messages.getString("Edit"));
    preferencesItem.setText(Messages.getString("Preferences___"));
    helpMenu.setMnemonic('H');
    helpMenu.setText(Messages.getString("Help"));
    exampleWorldsItem.setText(Messages.getString("Example_Worlds___"));
    aboutItem.setText(Messages.getString("About_Alice"));
    getContentPane().setLayout(borderLayout1);
    toolBarPanel.setLayout(gridBagLayout1);
    playButton.setText(Messages.getString("Play"));
    trashPanel.setAlignmentY(0.0F);
    trashPanel.setOpaque(false);
    trashPanel.setLayout(borderLayout11);
    clipboardPanel.setLayout(gridBagLayout2);
    mainPanel.setLayout(borderLayout2);
    buttonPanel.setLayout(gridBagLayout4);
    undoButton.setText(Messages.getString("Undo"));
    redoButton.setText(Messages.getString("Redo"));
    


    authoringPanel.setLayout(borderLayout3);
    
    leftPanel.setLayout(borderLayout5);
    worldTreeDragFromSplitPane.setOrientation(0);
    worldTreeDragFromSplitPane.setOpaque(false);
    rightPanel.setLayout(borderLayout6);
    worldTreePanel.setLayout(borderLayout7);
    dragFromPanel.setLayout(borderLayout8);
    editorBehaviorSplitPane.setOrientation(0);
    editorBehaviorSplitPane.setOpaque(false);
    behaviorPanel.setLayout(borderLayout9);
    editorPanel.setLayout(borderLayout10);
    leftRightSplitPane.setDoubleBuffered(true);
    leftRightSplitPane.setOpaque(false);
    
    setJMenuBar(menuBar);
    onScreenHelpItem.setToolTipText("");
    onScreenHelpItem.setActionCommand("OnScreenHelp");
    onScreenHelpItem.setText(Messages.getString("On_Screen_Help"));
    toolsMenu.setMnemonic('T');
    toolsMenu.setText(Messages.getString("Tools"));
    worldInfoItem.setActionCommand("worldInfo");
    worldInfoItem.setText(Messages.getString("World_Statistics___"));
    smallSceneBehaviorPanel.setLayout(borderLayout12);
    scenePanel.setLayout(borderLayout13);
    worldTreePanel.setBorder(border4);
    worldTreePanel.setOpaque(false);
    dragFromPanel.setOpaque(false);
    scenePanel.setBorder(border6);
    scenePanel.setOpaque(false);
    behaviorPanel.setBorder(border7);
    behaviorPanel.setOpaque(false);
    editorPanel.setOpaque(false);
    toolBarPanel.setBorder(border3);
    toolBarPanel.setOpaque(false);
    buttonPanel.setOpaque(false);
    clipboardPanel.setOpaque(false);
    mainPanel.setBorder(border2);
    mainPanel.setOpaque(false);
    authoringPanel.setOpaque(false);
    leftPanel.setOpaque(false);
    rightPanel.setOpaque(false);
    smallSceneBehaviorPanel.setOpaque(false);
    
    smallSceneBehaviorSplitPane.setOpaque(false);
    addObjectButton.setText(Messages.getString("Add_Object"));
    add3DTextItem.setText(Messages.getString("Add_3D_Text"));
    exportMovieItem.setText(Messages.getString("Export_Video"));
    teachMeButton.setText(Messages.getString("Teach_Me"));
    selectTutorialMenuItem.setText(Messages.getString("Select_a_Tutorial"));
    tutorialEditor.setText("Create or edit a tutorial");
    softwareUpdate.setText(Messages.getString("Update_Software"));
    license.setText(Messages.getString("View_License"));
    showStdOutItem.setText(Messages.getString("Text_Output___"));
    showStdErrItem.setText(Messages.getString("Standard_Error___"));
    printItem.setText(Messages.getString("Print___"));
    menuBar.add(fileMenu);
    menuBar.add(editMenu);
    menuBar.add(toolsMenu);
    menuBar.add(helpMenu);
    fileMenu.add(newWorldItem);
    fileMenu.add(openWorldItem);
    fileMenu.add(openZippedWorldItem);
    fileMenu.add(saveWorldItem);
    fileMenu.add(saveWorldAsItem);
    
    fileMenu.add(exportMovieItem);
    fileMenu.add(printItem);
    
    fileMenu.add(importItem);
    fileMenu.add(add3DTextItem);
    fileMenu.add(makeBillboardItem);
    fileMenu.addSeparator();
    fileMenu.add(exitItem);
    editMenu.add(preferencesItem);
    toolsMenu.add(worldInfoItem);
    toolsMenu.add(showStdOutItem);
    toolsMenu.add(showStdErrItem);
    

    if (authoringTool.getConfig().getValue("enableLoggingMode").equalsIgnoreCase("true")) {
      toolsMenu.add(instructorDemoItem);
      isLogging = true;
    }
    
    helpMenu.add(exampleWorldsItem);
    helpMenu.add(selectTutorialMenuItem);
    
    helpMenu.add(softwareUpdate);
    helpMenu.add(license);
    helpMenu.add(aboutItem);
    getContentPane().add(toolBarPanel, "North");
    toolBarPanel.add(buttonPanel, new GridBagConstraints(0, 0, 1, 2, 0.0D, 0.0D, 
      16, 0, new Insets(0, 0, 0, 0), 0, 0));
    buttonPanel.add(playButton, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 
      10, 0, new Insets(0, 0, 0, 0), 0, 0));
    buttonPanel.add(undoButton, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 
      10, 0, new Insets(0, 16, 0, 0), 0, 0));
    buttonPanel.add(redoButton, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 
      10, 0, new Insets(0, 4, 0, 0), 0, 0));
    



    toolBarPanel.add(clipboardPanel, new GridBagConstraints(4, 0, 1, 2, 0.0D, 0.0D, 
      14, 0, new Insets(0, 0, 0, 0), 0, 0));
    toolBarPanel.add(trashPanel, new GridBagConstraints(1, 0, 1, 2, 0.0D, 0.0D, 
      16, 3, new Insets(0, 14, 0, 0), 0, 0));
    toolBarPanel.add(glue, new GridBagConstraints(2, 0, 1, 1, 1.0D, 0.0D, 
      10, 2, new Insets(0, 0, 0, 0), 0, 0));
    getContentPane().add(mainPanel, "Center");
    
    worldTreeDragFromSplitPane.add(worldTreePanel, "top");
    worldTreeDragFromSplitPane.add(dragFromPanel, "bottom");
    leftPanel.add(worldTreeDragFromSplitPane, "Center");
    
    smallSceneBehaviorSplitPane.add(scenePanel, "left");
    smallSceneBehaviorSplitPane.add(behaviorPanel, "right");
    smallSceneBehaviorPanel.add(smallSceneBehaviorSplitPane, "Center");
    editorBehaviorSplitPane.add(smallSceneBehaviorPanel, "top");
    editorBehaviorSplitPane.add(editorPanel, "bottom");
    rightPanel.add(editorBehaviorSplitPane, "Center");
    
    leftRightSplitPane.add(leftPanel, "left");
    leftRightSplitPane.add(rightPanel, "right");
    
    authoringPanel.add(leftRightSplitPane, "Center");
    
    mainPanel.add(authoringPanel, "Center");
  }
}
