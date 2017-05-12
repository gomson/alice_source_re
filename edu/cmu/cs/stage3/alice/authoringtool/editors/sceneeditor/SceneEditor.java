package edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.Editor;
import edu.cmu.cs.stage3.alice.authoringtool.JAliceFrame;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator;
import edu.cmu.cs.stage3.alice.authoringtool.util.ScriptComboWidget;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener;
import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public class SceneEditor
  extends JPanel implements Editor
{
  private Package authoringToolPackage = Package.getPackage("edu.cmu.cs.stage3.alice.authoringtool");
  public String editorName = Messages.getString("Scene_Editor");
  
  public static int LARGE_MODE = 1;
  public static int SMALL_MODE = 2;
  protected int guiMode = LARGE_MODE;
  
  protected World world;
  protected Camera renderCamera = null;
  protected ScriptComboWidget scriptComboWidget = new ScriptComboWidget();
  
  protected AuthoringTool authoringTool;
  protected Image makeSceneEditorBigImage = AuthoringToolResources.getImageForString("makeSceneEditorBig");
  protected Image makeSceneEditorSmallImage = AuthoringToolResources.getImageForString("makeSceneEditorSmall");
  
  protected JButton makeSceneEditorBigButton;
  
  protected JButton makeSceneEditorSmallButton;
  

  public SceneEditor()
  {
    configInit();
    jbInit();
    guiInit();
  }
  
  private void configInit() {
    Configuration.addConfigurationListener(
      new ConfigurationListener() {
        public void changing(ConfigurationEvent ev) {}
        
        public void changed(ConfigurationEvent ev) { if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.enableScripting")) {
            if (Configuration.getValue(authoringToolPackage, "enableScripting").equalsIgnoreCase("true")) {
              topPanel.add(scriptComboWidget, "Center");
            } else {
              topPanel.remove(scriptComboWidget);
            }
            revalidate();
            repaint();
          }
        }
      });
  }
  


  private void guiInit()
  {
    mainPanel.setMinimumSize(new Dimension(0, 0));
    
    if (Configuration.getValue(authoringToolPackage, "enableScripting").equalsIgnoreCase("true")) {
      topPanel.add(scriptComboWidget, "Center");
    }
    
    if (AikMin.locale.equalsIgnoreCase("english")) {
      makeSceneEditorBigButton = new JButton(new ImageIcon(makeSceneEditorBigImage));
      makeSceneEditorSmallButton = new JButton(new ImageIcon(makeSceneEditorSmallImage));
      makeSceneEditorBigButton.setMargin(new Insets(2, 2, 2, 2));
      makeSceneEditorSmallButton.setMargin(new Insets(0, 0, 0, 0));
    } else {
      makeSceneEditorBigButton = new JButton(Messages.getString("Add_Object"));
      makeSceneEditorBigButton.setMargin(new Insets(4, 5, 4, 5));
      makeSceneEditorBigButton.setForeground(Color.white);
      makeSceneEditorSmallButton = new JButton(Messages.getString("Done"));
      makeSceneEditorSmallButton.setMargin(new Insets(5, 10, 5, 10));
      makeSceneEditorSmallButton.setForeground(Color.white);
    }
    
    makeSceneEditorBigButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        authoringTool.getJAliceFrame().setGuiMode(JAliceFrame.SCENE_EDITOR_LARGE_MODE);
      }
    });
    makeSceneEditorSmallButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        authoringTool.getJAliceFrame().setGuiMode(JAliceFrame.SCENE_EDITOR_SMALL_MODE);
      }
    });
    makeSceneEditorBigButton.setBackground(AuthoringToolResources.getColor("makeSceneEditorBigBackground"));
    makeSceneEditorSmallButton.setBackground(AuthoringToolResources.getColor("makeSceneEditorSmallBackground"));
    
    cameraViewPanel.navPanel.add(makeSceneEditorBigButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 14, 0, new Insets(0, 0, 2, 2), 0, 0));
    cameraViewPanel.controlPanel.add(makeSceneEditorSmallButton, new GridBagConstraints(0, 9, 1, 1, 0.0D, 0.0D, 14, 0, new Insets(0, 0, 8, 8), 0, 0));
    

    makeSceneEditorBigButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Open_the_Object_Gallery_and_Layout_Tool__p__p_Objects_are_added_to_the_world_from_the_Gallery__p_The_Layout_Tool_has_tools_that_will_help_you_position_objects_in_the_world__p_You_will_not_be_able_to_edit_Methods_or_Events_while_the_Gallery_is_open_") + "</font></html>");
    makeSceneEditorSmallButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Close_the_Gallery_and_Layout_Tool__p__p_Closes_the_gallery_and_returns_p_to_the_Method_and_Event_editors_") + "</font></html>");
  }
  

  public void setGuiMode(int guiMode)
  {
    if (this.guiMode != guiMode) {
      this.guiMode = guiMode;
      cameraViewPanel.setTargetsDirty();
      cameraViewPanel.updateMoveCameraCombo();
      mainPanel.removeAll();
      if (guiMode == SMALL_MODE)
      {
        cameraViewPanel.setViewMode(0);
        cameraViewPanel.navPanel.add(makeSceneEditorBigButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 14, 0, new Insets(0, 0, 2, 2), 0, 0));
        cameraViewPanel.guiNavigator.setImageSize(1);
        cameraViewPanel.defaultMoveModeButton.doClick();
        if (cameraViewPanel.affectSubpartsCheckBox.isSelected()) {
          cameraViewPanel.affectSubpartsCheckBox.doClick();
        }
        mainPanel.add(cameraViewPanel.superRenderPanel, "Center");
      } else if (guiMode == LARGE_MODE)
      {
        cameraViewPanel.navPanel.remove(makeSceneEditorBigButton);
        cameraViewPanel.singleViewButton.doClick();
        cameraViewPanel.guiNavigator.setImageSize(2);
        





        cameraViewPanel.add(cameraViewPanel.superRenderPanel, "Center");
        cameraViewPanel.defaultMoveModeButton.doClick();
        mainPanel.add(cameraViewPanel, "Center");
      }
    }
  }
  
  public int getGuiMode()
  {
    return guiMode;
  }
  
  public void setViewMode(int mode) {
    cameraViewPanel.setViewMode(mode);
  }
  
  public int getViewMode() {
    return cameraViewPanel.getViewMode();
  }
  
  public Dimension getRenderSize() {
    return cameraViewPanel.getRenderSize();
  }
  
  public JPanel getSuperRenderPanel() {
    return cameraViewPanel.superRenderPanel;
  }
  
  public JPanel getRenderPanel() {
    return cameraViewPanel.renderPanel;
  }
  
  public void makeDirty() {
    cameraViewPanel.setTargetsDirty();
  }
  
  public GalleryViewer getGalleryViewer() {
    return cameraViewPanel.galleryViewer;
  }
  



  public JComponent getJComponent()
  {
    return this;
  }
  
  public Object getObject() {
    return world;
  }
  
  public void setObject(World world) {
    this.world = world;
    scriptComboWidget.setSandbox(world);
    cameraViewPanel.setWorld(world);
    
    if (world != null)
    {
      Camera[] cameras = (Camera[])world.getDescendants(Camera.class);
      if (cameras.length > 0) {
        renderCamera = cameras[0];
      }
    } else {
      renderCamera = null;
    }
  }
  
  public void setAuthoringTool(AuthoringTool authoringTool) {
    if (this.authoringTool != null) {
      this.authoringTool.removeAuthoringToolStateListener(this);
    }
    
    this.authoringTool = authoringTool;
    

    cameraViewPanel.setAuthoringTool(authoringTool);
    cameraViewPanel.renderInit(authoringTool);
    stencilInit();
    
    if (authoringTool != null) {
      authoringTool.addAuthoringToolStateListener(this);
    }
  }
  




  protected HashMap idsToComponents = new HashMap();
  protected HashMap componentsToIds = new HashMap();
  
  protected void stencilInit() {
    idsToComponents.put("makeSceneEditorBigButton", makeSceneEditorBigButton);
    idsToComponents.put("makeSceneEditorSmallButton", makeSceneEditorSmallButton);
    idsToComponents.put("guiNavigatorSlidePanel", cameraViewPanel.guiNavigator.getSlidePanel());
    idsToComponents.put("guiNavigatorDrivePanel", cameraViewPanel.guiNavigator.getDrivePanel());
    idsToComponents.put("guiNavigatorTiltPanel", cameraViewPanel.guiNavigator.getTiltPanel());
    idsToComponents.put("affectSubpartsCheckBox", cameraViewPanel.affectSubpartsCheckBox);
    idsToComponents.put("aspectRatioComboBox", cameraViewPanel.aspectRatioComboBox);
    idsToComponents.put("lensAngleSlider", cameraViewPanel.lensAngleSlider);
    idsToComponents.put("singleViewButton", cameraViewPanel.singleViewButton);
    idsToComponents.put("quadViewButton", cameraViewPanel.quadViewButton);
    idsToComponents.put("cameraDummyButton", cameraViewPanel.cameraDummyButton);
    idsToComponents.put("objectDummyButton", cameraViewPanel.objectDummyButton);
    idsToComponents.put("moveCameraCombo", cameraViewPanel.moveCameraCombo);
    idsToComponents.put("defaultMoveModeButton", cameraViewPanel.defaultMoveModeButton);
    idsToComponents.put("moveUpDownModeButton", cameraViewPanel.moveUpDownModeButton);
    idsToComponents.put("turnLeftRightModeButton", cameraViewPanel.turnLeftRightModeButton);
    idsToComponents.put("turnForwardBackwardModeButton", cameraViewPanel.turnForwardBackwardModeButton);
    idsToComponents.put("tumbleModeButton", cameraViewPanel.tumbleModeButton);
    idsToComponents.put("copyModeButton", cameraViewPanel.copyModeButton);
    idsToComponents.put("orthoScrollModeButton", cameraViewPanel.orthoScrollModeButton);
    idsToComponents.put("orthoZoomInModeButton", cameraViewPanel.orthoZoomInModeButton);
    
    idsToComponents.put("superRenderPanel", cameraViewPanel.superRenderPanel);
    idsToComponents.put("renderPanel", cameraViewPanel.renderPanel);
    
    for (Iterator iter = idsToComponents.keySet().iterator(); iter.hasNext();) {
      Object key = iter.next();
      componentsToIds.put(idsToComponents.get(key), key);
    }
  }
  
  public String getIdForComponent(Component c) {
    return (String)componentsToIds.get(c);
  }
  
  public Component getComponentForId(String id) {
    return (Component)idsToComponents.get(id);
  }
  



  public void stateChanged(AuthoringToolStateChangedEvent ev)
  {
    cameraViewPanel.setTargetsDirty();
    if (ev.getCurrentState() == 3) {
      cameraViewPanel.deactivate();
    } else if (isShowing()) {
      cameraViewPanel.activate();
    }
  }
  
  public void worldLoaded(AuthoringToolStateChangedEvent ev) {
    cameraViewPanel.activate();
  }
  
  public void stateChanging(AuthoringToolStateChangedEvent ev) { cameraViewPanel.setTargetsDirty(); }
  public void worldLoading(AuthoringToolStateChangedEvent ev) { cameraViewPanel.setTargetsDirty(); }
  public void worldUnLoading(AuthoringToolStateChangedEvent ev) { cameraViewPanel.setTargetsDirty(); }
  public void worldStarting(AuthoringToolStateChangedEvent ev) { cameraViewPanel.setTargetsDirty(); }
  public void worldStopping(AuthoringToolStateChangedEvent ev) { cameraViewPanel.setTargetsDirty(); }
  public void worldPausing(AuthoringToolStateChangedEvent ev) { cameraViewPanel.setTargetsDirty(); }
  public void worldSaving(AuthoringToolStateChangedEvent ev) { cameraViewPanel.setTargetsDirty(); }
  public void worldUnLoaded(AuthoringToolStateChangedEvent ev) { cameraViewPanel.setTargetsDirty(); }
  public void worldStarted(AuthoringToolStateChangedEvent ev) { cameraViewPanel.setTargetsDirty(); }
  public void worldStopped(AuthoringToolStateChangedEvent ev) { cameraViewPanel.setTargetsDirty(); }
  public void worldPaused(AuthoringToolStateChangedEvent ev) { cameraViewPanel.setTargetsDirty(); }
  public void worldSaved(AuthoringToolStateChangedEvent ev) { cameraViewPanel.setTargetsDirty(); }
  














  void this_componentShown(ComponentEvent e)
  {
    cameraViewPanel.setTargetsDirty();
    cameraViewPanel.activate();
  }
  




  BorderLayout borderLayout1 = new BorderLayout();
  JPanel mainPanel = new JPanel();
  
  BorderLayout borderLayout4 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  BevelBorder bevelBorder1 = new BevelBorder(1);
  FlowLayout flowLayout4 = new FlowLayout();
  BorderLayout borderLayout3 = new BorderLayout();
  FlowLayout flowLayout6 = new FlowLayout();
  FlowLayout flowLayout7 = new FlowLayout();
  FlowLayout flowLayout8 = new FlowLayout();
  FlowLayout flowLayout9 = new FlowLayout();
  BorderLayout borderLayout7 = new BorderLayout();
  

  BorderLayout borderLayout8 = new BorderLayout();
  

  BorderLayout borderLayout6 = new BorderLayout();
  Border border1;
  Border border2;
  Border border3;
  FlowLayout flowLayout3 = new FlowLayout();
  JPanel topPanel = new JPanel();
  BorderLayout borderLayout5 = new BorderLayout();
  BorderLayout borderLayout9 = new BorderLayout();
  CameraViewPanel cameraViewPanel = new CameraViewPanel(this);
  
  private void jbInit() {
    border1 = BorderFactory.createEmptyBorder(0, 10, 0, 10);
    border2 = BorderFactory.createMatteBorder(0, 10, 0, 10, new Color(236, 235, 235));
    border3 = BorderFactory.createEmptyBorder(0, 10, 0, 10);
    setBackground(new Color(126, 159, 197));
    addComponentListener(new ComponentAdapter() {
      public void componentShown(ComponentEvent e) {
        this_componentShown(e);
      }
    });
    setLayout(borderLayout1);
    mainPanel.setBackground(new Color(126, 159, 197));
    mainPanel.setMinimumSize(new Dimension(10, 100));
    mainPanel.setLayout(borderLayout9);
    



    flowLayout4.setHgap(0);
    flowLayout4.setVgap(0);
    







    flowLayout3.setAlignment(0);
    flowLayout3.setHgap(0);
    flowLayout3.setVgap(0);
    topPanel.setLayout(borderLayout5);
    mainPanel.add(cameraViewPanel);
    add(mainPanel, "Center");
    add(topPanel, "North");
  }
}
