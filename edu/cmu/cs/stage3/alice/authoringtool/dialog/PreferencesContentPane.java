package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.JAlice;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener;
import edu.cmu.cs.stage3.io.FileUtilities;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.Document;

public class PreferencesContentPane extends ContentPane
{
  protected HashMap checkBoxToConfigKeyMap = new HashMap();
  
  protected AuthoringTool authoringTool;
  private Package authoringToolPackage = Package.getPackage("edu.cmu.cs.stage3.alice.authoringtool");
  protected JFileChooser browseFileChooser = new JFileChooser();
  protected HashMap rendererStringMap = new HashMap();
  protected boolean restartRequired = false;
  protected boolean reloadRequired = false;
  protected boolean shouldListenToRenderBoundsChanges = true;
  protected boolean changedCaptureDirectory = false;
  protected Frame owner;
  private Vector m_okActionListeners = new Vector();
  
  private final String FOREVER_INTERVAL_STRING = Messages.getString("Forever");
  
  private final String INFINITE_BACKUPS_STRING = Messages.getString("Infinite");
  

  private static Configuration authoringToolConfig = Configuration.getLocalConfiguration(JAlice.class
    .getPackage());
  
  public PreferencesContentPane()
  {
    setPreferredSize(new Dimension(600, 500));
    setMinimumSize(new Dimension(600, 500));
    jbInit();
    actionInit();
    checkBoxMapInit();
    miscInit();
    updateGUI();
    scaleFont(this);
  }
  
  private void actionInit() {
    okayAction.putValue("Name", Messages.getString("OK"));
    okayAction.putValue("ShortDescription", 
      Messages.getString("Accept_preference_changes"));
    
    cancelAction.putValue("Name", 
      Messages.getString("Cancel"));
    cancelAction.putValue("ShortDescription", 
      Messages.getString("Close_dialog_without_accepting_changes"));
    
    okayButton.setAction(okayAction);
    cancelButton.setAction(cancelAction);
  }
  
  private void checkBoxMapInit() {
    useBorderlessWindowCheckBox.setText(
      Messages.getString("use_a_borderless_render_window"));
    watcherPanelEnabledCheckBox.setText(
      Messages.getString("show_variable_watcher_when_world_runs"));
    runtimeScratchPadEnabledCheckBox.setText(
      Messages.getString("show_scratch_pad_when_world_runs"));
    infiniteBackupsCheckBox.setText(
      Messages.getString("save_infinite_number_of_backup_scripts"));
    doProfilingCheckBox.setText(Messages.getString("profile_world"));
    enableScriptingCheckBox.setToolTipText("");
    enableScriptingCheckBox.setActionCommand("enable jython scripting");
    enableScriptingCheckBox.setText(" " + 
      Messages.getString("enable_jython_scripting"));
    saveAsSingleFileCheckBox.setText(
      Messages.getString("always_save_worlds_as_single_files"));
    
    checkBoxToConfigKeyMap.put(showStartUpDialogCheckBox, 
      "showStartUpDialog");
    checkBoxToConfigKeyMap.put(enableHighContrastCheckBox, 
      "enableHighContrastMode");
    checkBoxToConfigKeyMap.put(enableLoggingCheckBox, "enableLoggingMode");
    checkBoxToConfigKeyMap
      .put(disableTooltipCheckBox, "disableTooltipMode");
    checkBoxToConfigKeyMap
      .put(showBuilderCheckBox, "showBuilderMode");
    checkBoxToConfigKeyMap.put(showWebWarningCheckBox, 
      "showWebWarningDialog");
    checkBoxToConfigKeyMap.put(loadSavedTabsCheckBox, "loadSavedTabs");
    

    checkBoxToConfigKeyMap.put(saveThumbnailWithWorldCheckBox, 
      "saveThumbnailWithWorld");
    checkBoxToConfigKeyMap.put(forceSoftwareRenderingCheckBox, 
      "rendering.forceSoftwareRendering");
    checkBoxToConfigKeyMap.put(showFPSCheckBox, "rendering.showFPS");
    checkBoxToConfigKeyMap.put(deleteFiles, "rendering.deleteFiles");
    checkBoxToConfigKeyMap.put(useBorderlessWindowCheckBox, 
      "rendering.useBorderlessWindow");
    

    checkBoxToConfigKeyMap.put(constrainRenderDialogAspectCheckBox, 
      "rendering.constrainRenderDialogAspectRatio");
    checkBoxToConfigKeyMap.put(ensureRenderDialogIsOnScreenCheckBox, 
      "rendering.ensureRenderDialogIsOnScreen");
    checkBoxToConfigKeyMap.put(createNormalsCheckBox, 
      "importers.aseImporter.createNormalsIfNoneExist");
    checkBoxToConfigKeyMap.put(createUVsCheckBox, 
      "importers.aseImporter.createUVsIfNoneExist");
    checkBoxToConfigKeyMap.put(useSpecularCheckBox, 
      "importers.aseImporter.useSpecular");
    checkBoxToConfigKeyMap.put(groupMultipleRootObjectsCheckBox, 
      "importers.aseImporter.groupMultipleRootObjects");
    checkBoxToConfigKeyMap.put(colorToWhiteWhenTexturedCheckBox, 
      "importers.aseImporter.colorToWhiteWhenTextured");
    checkBoxToConfigKeyMap.put(watcherPanelEnabledCheckBox, 
      "watcherPanelEnabled");
    checkBoxToConfigKeyMap.put(runtimeScratchPadEnabledCheckBox, 
      "rendering.runtimeScratchPadEnabled");
    checkBoxToConfigKeyMap.put(infiniteBackupsCheckBox, 
      "saveInfiniteBackups");
    checkBoxToConfigKeyMap.put(doProfilingCheckBox, "doProfiling");
    

    checkBoxToConfigKeyMap.put(showWorldStatsCheckBox, "showWorldStats");
    checkBoxToConfigKeyMap.put(enableScriptingCheckBox, "enableScripting");
    checkBoxToConfigKeyMap.put(pickUpTilesCheckBox, "gui.pickUpTiles");
    checkBoxToConfigKeyMap.put(useAlphaTilesCheckBox, "gui.useAlphaTiles");
    
    checkBoxToConfigKeyMap.put(saveAsSingleFileCheckBox, 
      "useSingleFileLoadStore");
    checkBoxToConfigKeyMap
      .put(clearStdOutOnRunCheckBox, "clearStdOutOnRun");
    checkBoxToConfigKeyMap.put(screenCaptureInformUserCheckBox, 
      "screenCapture.informUser");
  }
  

  private void miscInit()
  {
    browseFileChooser.setApproveButtonText(
      Messages.getString("Set_Directory"));
    browseFileChooser.setDialogTitle(
      Messages.getString("Choose_Directory___"));
    browseFileChooser.setDialogType(0);
    browseFileChooser.setFileSelectionMode(1);
    

    Configuration.addConfigurationListener(new ConfigurationListener()
    {
      public void changing(ConfigurationEvent ev) {}
      
      public void changed(ConfigurationEvent ev) {
        if ((ev.getKeyName().endsWith("rendering.orderedRendererList")) || 
          (ev.getKeyName().endsWith("rendering.forceSoftwareRendering")) || 
          (ev.getKeyName().endsWith("resourceFile")) || 
          (ev.getKeyName().endsWith("language")) || 
          (ev.getKeyName().endsWith("enableScripting")) || 
          (ev.getKeyName().endsWith("enableLoggingMode"))) {
          restartRequired = true;
        }
      }
    });
  }
  
  public final AbstractAction okayAction = new AbstractAction() {
    public void actionPerformed(ActionEvent ev) {
      if (validateInput()) {
        PreferencesContentPane.this.fireOKActionListeners();
      }
    }
  };
  
  public final AbstractAction cancelAction = new AbstractAction()
  {
    public void actionPerformed(ActionEvent ev) {}
  };
  
  public final DocumentListener captureDirectoryChangeListener = new DocumentListener() {
    public void changedUpdate(DocumentEvent e) {
      changedCaptureDirectory = true;
    }
    
    public void insertUpdate(DocumentEvent e) {
      changedCaptureDirectory = true;
    }
    
    public void removeUpdate(DocumentEvent e) {
      changedCaptureDirectory = true;
    }
  };
  
  public final DocumentListener renderDialogBoundsChecker = new DocumentListener() {
    public void changedUpdate(DocumentEvent e) {
      if (shouldListenToRenderBoundsChanges) {
        checkAndUpdateRenderBounds();
      }
    }
    
    public void insertUpdate(DocumentEvent e) {
      if (shouldListenToRenderBoundsChanges) {
        checkAndUpdateRenderBounds();
      }
    }
    
    public void removeUpdate(DocumentEvent e) {
      if (shouldListenToRenderBoundsChanges) {
        checkAndUpdateRenderBounds();
      }
    }
  };
  
  public final DocumentListener renderDialogWidthChecker = new DocumentListener() {
    public void changedUpdate(DocumentEvent e) {
      if (shouldListenToRenderBoundsChanges) {
        checkAndUpdateRenderWidth();
      }
    }
    
    public void insertUpdate(DocumentEvent e) {
      if (shouldListenToRenderBoundsChanges) {
        checkAndUpdateRenderWidth();
      }
    }
    
    public void removeUpdate(DocumentEvent e) {
      if (shouldListenToRenderBoundsChanges) {
        checkAndUpdateRenderWidth();
      }
    }
  };
  
  public final DocumentListener renderDialogHeightChecker = new DocumentListener() {
    public void changedUpdate(DocumentEvent e) {
      if (shouldListenToRenderBoundsChanges) {
        checkAndUpdateRenderHeight();
      }
    }
    
    public void insertUpdate(DocumentEvent e) {
      if (shouldListenToRenderBoundsChanges) {
        checkAndUpdateRenderHeight();
      }
    }
    
    public void removeUpdate(DocumentEvent e) {
      if (shouldListenToRenderBoundsChanges) {
        checkAndUpdateRenderHeight();
      }
    }
  };
  
  private void scaleFont(Component currentComponent) {
    currentComponent.setFont(new Font("SansSerif", 
      1, 12));
    
    if ((currentComponent instanceof Container)) {
      for (int i = 0; i < ((Container)currentComponent)
            .getComponentCount(); i++) {
        scaleFont(((Container)currentComponent).getComponent(i));
      }
    }
  }
  
  public void setAuthoringTool(AuthoringTool authoringTool)
  {
    this.authoringTool = authoringTool;
  }
  
  public String getTitle() {
    return Messages.getString("Preferences");
  }
  
  public void preDialogShow(JDialog dialog) {
    super.preDialogShow(dialog);
    updateGUI();
    changedCaptureDirectory = false;
  }
  
  public void postDialogShow(JDialog dialog) {
    super.postDialogShow(dialog);
  }
  
  public void addOKActionListener(ActionListener l) {
    m_okActionListeners.addElement(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    m_okActionListeners.removeElement(l);
  }
  
  public void addCancelActionListener(ActionListener l) {
    cancelButton.addActionListener(l);
  }
  
  public void removeCancelActionListener(ActionListener l) {
    cancelButton.removeActionListener(l);
  }
  
  private void fireOKActionListeners() {
    ActionEvent e = new ActionEvent(this, 
      1001, 
      Messages.getString("OK"));
    for (int i = 0; i < m_okActionListeners.size(); i++) {
      ActionListener l = 
        (ActionListener)m_okActionListeners.elementAt(i);
      l.actionPerformed(e);
    }
  }
  
  public void finalizeSelections() {
    setInput();
    if (restartRequired) {
      Object[] options = { Messages.getString("Restart"), 
        Messages.getString("Cancel") };
      int result = 
        DialogManager.showOptionDialog(
        Messages.getString("You_will_have_to_restart_Alice_in_order_for_these_settings_to_take_effect_"), 
        Messages.getString("Restart_Required"), 
        2, 
        3, null, 
        options, options[0]);
      restartRequired = false;
      if (result == 0) {
        authoringTool.quit(true);
      }
    } else if (reloadRequired)
    {
      DialogManager.showMessageDialog(
        Messages.getString("You_will_have_to_reload_the_current_world_in_order_for_these_settings_to_take_effect_"), 
        Messages.getString("Reload_Required"), 
        1);
      reloadRequired = false;
    }
    if ((configTabbedPane != null) && (generalPanel != null)) {
      configTabbedPane.setSelectedComponent(generalPanel);
    }
  }
  
  protected boolean isValidRenderBounds(int x, int y, int w, int h) {
    if ((x < 0) || (y < 0) || (w <= 0) || (h <= 0)) {
      return false;
    }
    return true;
  }
  
  protected void checkAndUpdateRenderWidth() {
    int w = 0;int h = 0;
    boolean isOK = true;
    try {
      w = Integer.parseInt(boundsWidthTextField.getText());
      if (w > 0) {
        boundsWidthTextField.setForeground(Color.black);
      } else {
        boundsWidthTextField.setForeground(Color.red);
        isOK = false;
      }
    } catch (NumberFormatException e) {
      boundsWidthTextField.setForeground(Color.red);
      isOK = false;
    }
    try {
      h = Integer.parseInt(boundsHeightTextField.getText());
      if (h <= 0) {
        isOK = false;
      }
    } catch (NumberFormatException e) {
      isOK = false;
    }
    if ((constrainRenderDialogAspectCheckBox.isSelected()) && (isOK) && 
      (authoringTool != null)) {
      double currentAspectRatio = authoringTool.getAspectRatio();
      h = (int)Math.round(w / currentAspectRatio);
      if (h <= 0) {
        h = 1;
      }
      shouldListenToRenderBoundsChanges = false;
      boundsHeightTextField.setText(Integer.toString(h));
      shouldListenToRenderBoundsChanges = true;
    }
    okayButton.setEnabled(isOK);
  }
  
  protected void checkAndUpdateRenderHeight() {
    int w = 0;int h = 0;
    boolean isOK = true;
    try {
      h = Integer.parseInt(boundsHeightTextField.getText());
      if (h > 0) {
        boundsHeightTextField.setForeground(Color.black);
      } else {
        boundsHeightTextField.setForeground(Color.red);
        isOK = false;
      }
    } catch (NumberFormatException e) {
      boundsHeightTextField.setForeground(Color.red);
      isOK = false;
    }
    try {
      w = Integer.parseInt(boundsWidthTextField.getText());
      if (w <= 0) {
        isOK = false;
      }
    } catch (NumberFormatException e) {
      isOK = false;
    }
    if ((constrainRenderDialogAspectCheckBox.isSelected()) && (isOK) && 
      (authoringTool != null)) {
      double currentAspectRatio = authoringTool.getAspectRatio();
      w = (int)Math.round(h * currentAspectRatio);
      if (w <= 0) {
        w = 1;
      }
      shouldListenToRenderBoundsChanges = false;
      boundsWidthTextField.setText(Integer.toString(w));
      shouldListenToRenderBoundsChanges = true;
    }
    okayButton.setEnabled(isOK);
  }
  
  protected void checkAndUpdateRenderBounds() {
    int x = 0;int y = 0;int w = 0;int h = 0;
    boolean isOK = true;
    try {
      x = Integer.parseInt(boundsXTextField.getText());
      if (x >= 0) {
        boundsXTextField.setForeground(Color.black);
      } else {
        boundsXTextField.setForeground(Color.red);
        isOK = false;
      }
    } catch (NumberFormatException e) {
      boundsXTextField.setForeground(Color.red);
      isOK = false;
    }
    try {
      y = Integer.parseInt(boundsYTextField.getText());
      if (y >= 0) {
        boundsYTextField.setForeground(Color.black);
      } else {
        boundsYTextField.setForeground(Color.red);
        isOK = false;
      }
    } catch (NumberFormatException e) {
      boundsYTextField.setForeground(Color.red);
      isOK = false;
    }
    try {
      w = Integer.parseInt(boundsWidthTextField.getText());
      if (w > 0) {
        boundsWidthTextField.setForeground(Color.black);
      } else {
        boundsWidthTextField.setForeground(Color.red);
        isOK = false;
      }
    } catch (NumberFormatException e) {
      boundsWidthTextField.setForeground(Color.red);
      isOK = false;
    }
    try {
      h = Integer.parseInt(boundsHeightTextField.getText());
      if (h > 0) {
        boundsHeightTextField.setForeground(Color.black);
      } else {
        boundsHeightTextField.setForeground(Color.red);
        isOK = false;
      }
    } catch (NumberFormatException e) {
      boundsHeightTextField.setForeground(Color.red);
      isOK = false;
    }
    if ((constrainRenderDialogAspectCheckBox.isSelected()) && (isOK) && 
      (authoringTool != null)) {
      double currentAspectRatio = authoringTool.getAspectRatio();
      if (currentAspectRatio > 1.0D) {
        w = (int)Math.round(h * currentAspectRatio);
        if (w <= 0) {
          w = 1;
        }
        shouldListenToRenderBoundsChanges = false;
        boundsWidthTextField.setText(Integer.toString(w));
        shouldListenToRenderBoundsChanges = true;
      } else {
        h = (int)Math.round(w / currentAspectRatio);
        if (h <= 0) {
          h = 1;
        }
        shouldListenToRenderBoundsChanges = false;
        boundsHeightTextField.setText(Integer.toString(h));
        shouldListenToRenderBoundsChanges = true;
      }
    }
    okayButton.setEnabled(isOK);
  }
  
  protected boolean validateInput() {
    try {
      int i = Integer.parseInt(maxRecentWorldsTextField
        .getText());
      if ((i < 0) || (i > 30)) {
        throw new NumberFormatException();
      }
    }
    catch (NumberFormatException e) {
      DialogManager.showMessageDialog(
        Messages.getString("the_maximum_number_of_recent_worlds_must_be_between_0_and_30"), 
        Messages.getString("Invalid_Clipboard_Number"), 
        1);
      return false;
    }
    try
    {
      int i = 
        Integer.parseInt(numClipboardsTextField.getText());
      if ((i < 0) || (i > 30)) {
        throw new NumberFormatException();
      }
    }
    catch (NumberFormatException e) {
      DialogManager.showMessageDialog(
        Messages.getString("the_number_of_clipboards_must_be_between_0_and_30"), 
        Messages.getString("Invalid_Clipboard_Number"), 
        1);
      return false;
    }
    try
    {
      int x = Integer.parseInt(boundsXTextField.getText());
      int y = Integer.parseInt(boundsYTextField.getText());
      int w = Integer.parseInt(boundsWidthTextField.getText());
      int h = Integer.parseInt(boundsHeightTextField.getText());
      if (!isValidRenderBounds(x, y, w, h)) {
        throw new NumberFormatException();
      }
    }
    catch (NumberFormatException e) {
      DialogManager.showMessageDialog(
        Messages.getString("all_of_the_render_window_bounds_values_must_be_integers_greater_than_0"), 
        Messages.getString("Bad_Render_Bounds"), 
        1);
      return false;
    }
    

    File worldDirectoryFile = new File(
      worldDirectoryTextField.getText());
    if ((!worldDirectoryFile.exists()) || 
      (!worldDirectoryFile.isDirectory()) || 
      (!worldDirectoryFile.canRead())) {
      int result = 
        DialogManager.showConfirmDialog(
        worldDirectoryFile.getAbsolutePath() + " " + 
        Messages.getString("is_not_valid___The_worlds_directory_must_be_a_directory_that_exists_and_can_be_read___Would_you_like_to_fix_this_now_"), 
        Messages.getString("Bad_Directory"), 
        0, 
        2);
      if (result != 1) {
        return false;
      }
    }
    

    File importDirectoryFile = new File(
      importDirectoryTextField.getText());
    if ((!importDirectoryFile.exists()) || 
      (!importDirectoryFile.isDirectory()) || 
      (!importDirectoryFile.canRead())) {
      int result = 
        DialogManager.showConfirmDialog(
        importDirectoryFile.getAbsolutePath() + " " + 
        Messages.getString("is_not_valid___The_import_directory_must_be_a_directory_that_exists_and_can_be_read___Would_you_like_to_fix_this_now_"), 
        Messages.getString("Bad_Directory"), 
        0, 
        2);
      if (result != 1) {
        return false;
      }
    }
    






















    if (changedCaptureDirectory) {
      File captureDirectoryFile = new File(
        captureDirectoryTextField.getText());
      int directoryCheck = 
        FileUtilities.isWritableDirectory(captureDirectoryFile);
      if (directoryCheck == -2)
      {
        DialogManager.showMessageDialog(
          Messages.getString("The_capture_directory_specified_can_not_be_written_to__Please_choose_another_directory_"), 
          Messages.getString("Bad_Directory"), 
          1);
        return false; }
      if ((directoryCheck == -1) || 
        (directoryCheck == -3))
      {
        DialogManager.showMessageDialog(
          Messages.getString("The_capture_directory_must_be_a_directory_that_exists_"), 
          Messages.getString("Bad_Directory"), 
          1);
        return false;
      }
    }
    
    if (baseNameTextField.getText().trim().equals("")) {
      DialogManager.showMessageDialog(
        Messages.getString("The_capture_base_name_must_not_be_empty_"), 
        Messages.getString("Bad_Base_Name"), 
        1);
      return false;
    }
    
    char[] badChars = { '\\', '/', ':', '*', '?', '"', '<', '>', '|' };
    




    String baseName = baseNameTextField.getText().trim();
    for (int i = 0; i < badChars.length; i++) {
      if (baseName.indexOf(badChars[i]) != -1) {
        StringBuffer message = new StringBuffer(
          Messages.getString("Filenames_may_not_contain_the_following_characters_"));
        for (int j = 0; j < badChars.length; j++) {
          message.append(" ");
          message.append(badChars[j]);
        }
        DialogManager.showMessageDialog(
          message.toString(), Messages.getString("Bad_Filename"), 
          1);
        return false;
      }
    }
    
    String saveIntervalString = (String)saveIntervalComboBox
      .getSelectedItem();
    if (!saveIntervalString.equalsIgnoreCase(FOREVER_INTERVAL_STRING)) {
      try {
        Integer.parseInt(saveIntervalString);
      }
      catch (Throwable t) {
        DialogManager.showMessageDialog(
          Messages.getString("You_must_enter_a_valid_number_for_the_time_to_wait_before_prompting_to_save_"), 
          Messages.getString("Bad_Prompt_To_Save_Interval"), 
          1);
        return false;
      }
    }
    
    String backupCountString = (String)backupCountComboBox
      .getSelectedItem();
    if (!backupCountString.equalsIgnoreCase(INFINITE_BACKUPS_STRING)) {
      try {
        Integer.parseInt(backupCountString);
      }
      catch (Throwable t) {
        DialogManager.showMessageDialog(
          Messages.getString("You_must_enter_a_valid_number_for_the_number_of_backups_you_want_Alice_to_save_"), 
          Messages.getString("Bad_Backup_Count_Value"), 
          1);
        return false;
      }
    }
    String fontSizeString = (String)fontSizeComboBox.getSelectedItem();
    try {
      Integer.parseInt(fontSizeString);
    }
    catch (Throwable t) {
      DialogManager.showMessageDialog(
        Messages.getString("You_must_enter_a_valid_number_for_the_font_size_"), 
        Messages.getString("Bad_Backup_Font_Size"), 
        1);
      return false;
    }
    
    return true;
  }
  
  protected void setInput() {
    boolean oldContrast = Configuration.getValue(authoringToolPackage, 
      "enableHighContrastMode").equalsIgnoreCase("true");
    Iterator iter = checkBoxToConfigKeyMap.keySet()
      .iterator(); while (iter.hasNext()) {
      JCheckBox checkBox = 
        (JCheckBox)iter.next();
      String currentValue = Configuration.getValue(authoringToolPackage, 
        (String)checkBoxToConfigKeyMap.get(checkBox));
      if (currentValue == null)
      {
        AuthoringTool.showErrorDialog(
        
          Messages.getString("Warning__no_value_found_for_preference__") + checkBoxToConfigKeyMap.get(checkBox), 
          null);
        currentValue = "false";
        Configuration.setValue(authoringToolPackage, 
          (String)checkBoxToConfigKeyMap.get(checkBox), 
          currentValue);
      }
      if (currentValue.equalsIgnoreCase("true") != checkBox.isSelected()) {
        Configuration.setValue(authoringToolPackage, 
          (String)checkBoxToConfigKeyMap.get(checkBox), 
          checkBox.isSelected() ? "true" : "false");
      }
    }
    

    if (Configuration.getValue(authoringToolPackage, "disableTooltipMode").equalsIgnoreCase("true")) {
      ToolTipManager.sharedInstance().setEnabled(false);
    } else {
      ToolTipManager.sharedInstance().setEnabled(true);
    }
    


    if (Configuration.getValue(authoringToolPackage, "showBuilderMode").equalsIgnoreCase("true")) {
      edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.showBuilder = true;
    } else {
      edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.showBuilder = false;
    }
    


    if (!Configuration.getValue(authoringToolPackage, "recentWorlds.maxWorlds").equals(maxRecentWorldsTextField.getText())) {
      Configuration.setValue(authoringToolPackage, 
        "recentWorlds.maxWorlds", 
        maxRecentWorldsTextField.getText());
    }
    
    if (!Configuration.getValue(authoringToolPackage, "numberOfClipboards").equals(numClipboardsTextField.getText())) {
      Configuration.setValue(authoringToolPackage, "numberOfClipboards", 
        numClipboardsTextField.getText());
    }
    String boundsString = boundsXTextField.getText() + ", " + 
      boundsYTextField.getText() + ", " + 
      boundsWidthTextField.getText() + ", " + 
      boundsHeightTextField.getText();
    
    if (!Configuration.getValue(authoringToolPackage, "rendering.renderWindowBounds").equals(boundsString)) {
      Configuration.setValue(authoringToolPackage, 
        "rendering.renderWindowBounds", boundsString);
    }
    

    if (!Configuration.getValue(authoringToolPackage, "directories.worldsDirectory").equals(worldDirectoryTextField.getText())) {
      Configuration.setValue(authoringToolPackage, 
        "directories.worldsDirectory", 
        worldDirectoryTextField.getText());
    }
    

    if (!Configuration.getValue(authoringToolPackage, "directories.importDirectory").equals(worldDirectoryTextField.getText())) {
      Configuration.setValue(authoringToolPackage, 
        "directories.importDirectory", 
        worldDirectoryTextField.getText());
    }
    








    if (!Configuration.getValue(authoringToolPackage, "directories.charactersDirectory").equals(worldDirectoryTextField.getText() + System.getProperty("file.separator") + "CustomGallery")) {
      Configuration.setValue(authoringToolPackage, 
        "directories.charactersDirectory", 
        worldDirectoryTextField.getText() + System.getProperty("file.separator") + "CustomGallery");
    }
    

    if (!Configuration.getValue(authoringToolPackage, "screenCapture.directory").equals(captureDirectoryTextField.getText())) {
      Configuration.setValue(authoringToolPackage, 
        "screenCapture.directory", 
        captureDirectoryTextField.getText());
    }
    
    if (!Configuration.getValue(authoringToolPackage, "screenCapture.baseName").equals(baseNameTextField.getText())) {
      Configuration.setValue(authoringToolPackage, 
        "screenCapture.baseName", baseNameTextField.getText());
    }
    

    if (!Configuration.getValue(authoringToolPackage, "screenCapture.numDigits").equals((String)numDigitsComboBox.getSelectedItem())) {
      Configuration.setValue(authoringToolPackage, 
        "screenCapture.numDigits", 
        (String)numDigitsComboBox.getSelectedItem());
    }
    

    if (!Configuration.getValue(authoringToolPackage, "screenCapture.codec").equals((String)codecComboBox.getSelectedItem())) {
      Configuration.setValue(authoringToolPackage, "screenCapture.codec", 
        (String)codecComboBox.getSelectedItem());
    }
    
    if (!Configuration.getValue(authoringToolPackage, "resourceFile").equals((String)resourceFileComboBox.getSelectedItem())) {
      Configuration.setValue(authoringToolPackage, "resourceFile", 
        (String)resourceFileComboBox.getSelectedItem());
    }
    
    if (!Configuration.getValue(authoringToolPackage, "language").equals((String)languageComboBox.getSelectedItem())) {
      Configuration.setValue(authoringToolPackage, "language", 
        (String)languageComboBox.getSelectedItem());
      Configuration.setValue(authoringToolPackage, "directories.templatesDirectory", "templateWorlds" + System.getProperty("file.separator") + (String)languageComboBox.getSelectedItem());
    }
    






    String saveIntervalString = (String)saveIntervalComboBox
      .getSelectedItem();
    if (saveIntervalString.equalsIgnoreCase(FOREVER_INTERVAL_STRING))
    {
      Configuration.setValue(authoringToolPackage, "promptToSaveInterval", 
        Integer.toString(Integer.MAX_VALUE));
    } else {
      Configuration.setValue(authoringToolPackage, 
        "promptToSaveInterval", saveIntervalString);
    }
    
    String backupCountString = (String)backupCountComboBox
      .getSelectedItem();
    if (saveIntervalString.equalsIgnoreCase(FOREVER_INTERVAL_STRING)) {
      Configuration.setValue(authoringToolPackage, 
        "maximumWorldBackupCount", 
        Integer.toString(Integer.MAX_VALUE));
    } else {
      Configuration.setValue(authoringToolPackage, 
        "maximumWorldBackupCount", backupCountString);
    }
    
    int oldFontSize = ((Font)
      UIManager.get("Label.font")).getSize();
    String fontSizeString = (String)fontSizeComboBox.getSelectedItem();
    
    Configuration.setValue(authoringToolPackage, "fontSize", fontSizeString);
    int newFontSize = Integer.valueOf(fontSizeString).intValue();
    if ((oldContrast != enableHighContrastCheckBox.isSelected()) || 
      (oldFontSize != newFontSize)) {
      restartRequired = true;
    }
    











    try
    {
      Configuration.storeConfig();
    }
    catch (IOException e) {
      AuthoringTool.showErrorDialog(
        Messages.getString("Error_storing_preferences_"), e);
    }
  }
  
  protected void updateGUI() {
    Iterator iter = checkBoxToConfigKeyMap.keySet()
      .iterator(); while (iter.hasNext()) {
      JCheckBox checkBox = 
        (JCheckBox)iter.next();
      boolean value;
      try {
        value = 
        
          Configuration.getValue(authoringToolPackage, (String)checkBoxToConfigKeyMap.get(checkBox)).equalsIgnoreCase("true");
      } catch (Exception e) { boolean value;
        value = false;
      }
      checkBox.setSelected(value);
    }
    
    setSaveIntervalValues();
    initSaveIntervalComboBox();
    setBackupCountValues();
    initBackupCountComboBox();
    setFontSizeValues();
    initFontSizeComboBox();
    
    maxRecentWorldsTextField.setText(Configuration.getValue(
      authoringToolPackage, "recentWorlds.maxWorlds"));
    numClipboardsTextField.setText(Configuration.getValue(
      authoringToolPackage, "numberOfClipboards"));
    






    String boundsString = Configuration.getValue(authoringToolPackage, 
      "rendering.renderWindowBounds");
    StringTokenizer st = new StringTokenizer(
      boundsString, " \t,");
    if (st.countTokens() == 4) {
      boundsXTextField.setText(st.nextToken());
      boundsYTextField.setText(st.nextToken());
      boundsWidthTextField.setText(st.nextToken());
      boundsHeightTextField.setText(st.nextToken());
    }
    
    String worldDirectory = Configuration.getValue(authoringToolPackage, 
      "directories.worldsDirectory");
    worldDirectoryTextField.setText(worldDirectory);
    String importDirectory = Configuration.getValue(authoringToolPackage, 
      "directories.importDirectory");
    importDirectoryTextField.setText(importDirectory);
    





    String captureDirectory = Configuration.getValue(authoringToolPackage, 
      "screenCapture.directory");
    captureDirectoryTextField.setText(captureDirectory);
    
    baseNameTextField.setText(Configuration.getValue(authoringToolPackage, 
      "screenCapture.baseName"));
    numDigitsComboBox.setSelectedItem(Configuration.getValue(
      authoringToolPackage, "screenCapture.numDigits"));
    codecComboBox.setSelectedItem(Configuration.getValue(
      authoringToolPackage, "screenCapture.codec"));
  }
  

















  public void setVisible(boolean b)
  {
    if (b) {
      updateGUI();
    }
    super.setVisible(b);
  }
  

  protected class ConfigListModel
    implements ListModel, ConfigurationListener
  {
    protected Package configPackage;
    protected String configKey;
    protected Set listenerSet = new HashSet();
    
    public ConfigListModel(Package configPackage, String configKey) {
      this.configPackage = configPackage;
      this.configKey = configKey;
      Configuration.addConfigurationListener(this);
    }
    
    public void addListDataListener(ListDataListener listener)
    {
      listenerSet.add(listener);
    }
    
    public void removeListDataListener(ListDataListener listener)
    {
      listenerSet.remove(listener);
    }
    
    public int getSize() {
      return Configuration.getValueList(configPackage, configKey).length;
    }
    
    public Object getElementAt(int index) {
      String item = Configuration.getValueList(configPackage, configKey)[index];
      return 
        AuthoringToolResources.getReprForValue(item);
    }
    
    public void moveIndexHigher(int index) {
      String[] valueList = Configuration.getValueList(configPackage, 
        configKey);
      if ((index > 0) && (index < valueList.length)) {
        String[] newValueList = new String[valueList.length];
        System.arraycopy(valueList, 0, newValueList, 0, 
          valueList.length);
        String temp = newValueList[index];
        newValueList[index] = newValueList[(index - 1)];
        newValueList[(index - 1)] = temp;
        Configuration.setValueList(configPackage, configKey, 
          newValueList);
      }
    }
    
    public void moveIndexLower(int index) {
      String[] valueList = Configuration.getValueList(configPackage, 
        configKey);
      if ((index >= 0) && (index < valueList.length - 1)) {
        String[] newValueList = new String[valueList.length];
        System.arraycopy(valueList, 0, newValueList, 0, 
          valueList.length);
        String temp = newValueList[index];
        newValueList[index] = newValueList[(index + 1)];
        newValueList[(index + 1)] = temp;
        Configuration.setValueList(configPackage, configKey, 
          newValueList);
      }
    }
    

    public void changing(ConfigurationEvent ev) {}
    

    public void changed(ConfigurationEvent ev)
    {
      if (ev.getKeyName().endsWith("rendering.orderedRendererList")) {
        int upperRange = 0;
        if (ev.getOldValueList() != null) {
          upperRange = Math.max(upperRange, 
            ev.getOldValueList().length);
        }
        if (ev.getNewValueList() != null) {
          upperRange = Math.max(upperRange, 
            ev.getNewValueList().length);
        }
        ListDataEvent listDataEvent = new ListDataEvent(
          this, 0, 
          0, upperRange);
        Iterator iter = listenerSet.iterator();
        while (iter.hasNext())
        {
          ((ListDataListener)iter.next()).contentsChanged(listDataEvent);
        }
      }
    }
  }
  
  private int getValueForString(String numString) {
    if ((numString.equalsIgnoreCase(FOREVER_INTERVAL_STRING)) || 
      (numString.equalsIgnoreCase(INFINITE_BACKUPS_STRING))) {
      return Integer.MAX_VALUE;
    }
    try {
      return Integer.parseInt(numString);
    }
    catch (NumberFormatException nfe) {}
    return -1;
  }
  
  private void setSaveIntervalValues()
  {
    saveIntervalOptions.removeAllElements();
    saveIntervalOptions.add("15");
    saveIntervalOptions.add("30");
    saveIntervalOptions.add("45");
    saveIntervalOptions.add("60");
    saveIntervalOptions.add(FOREVER_INTERVAL_STRING);
    String intervalString = Configuration.getValue(authoringToolPackage, 
      "promptToSaveInterval");
    int interval = -1;
    try {
      interval = Integer.parseInt(intervalString);
    }
    catch (Throwable localThrowable) {}
    addComboBoxValueValue(interval, saveIntervalOptions);
  }
  
  private void addComboBoxValueValue(int toAdd, Vector toAddTo) {
    if (toAdd > 0) {
      boolean isThere = false;
      int location = toAddTo.size() - 1;
      for (int i = 0; i < toAddTo.size(); i++) {
        int currentValue = getValueForString((String)toAddTo.get(i));
        if (toAdd == currentValue) {
          isThere = true;
        } else if ((toAdd < currentValue) && (location > i)) {
          location = i;
        }
      }
      if (!isThere) {
        Integer currentValue = new Integer(toAdd);
        toAddTo.insertElementAt(currentValue.toString(), location);
      }
    }
  }
  
  private void initSaveIntervalComboBox() {
    saveIntervalComboBox.removeAllItems();
    String intervalString = Configuration.getValue(authoringToolPackage, 
      "promptToSaveInterval");
    for (int i = 0; i < saveIntervalOptions.size(); i++) {
      saveIntervalComboBox.addItem(saveIntervalOptions.get(i));
      
      if (intervalString.equalsIgnoreCase(saveIntervalOptions.get(i).toString())) {
        saveIntervalComboBox.setSelectedIndex(i);
      }
      else if (intervalString.equalsIgnoreCase(Integer.toString(Integer.MAX_VALUE)))
      {
        if (((String)saveIntervalOptions.get(i)).equalsIgnoreCase(FOREVER_INTERVAL_STRING))
          saveIntervalComboBox.setSelectedIndex(i);
      }
    }
  }
  
  private void setBackupCountValues() {
    backupCountOptions.removeAllElements();
    backupCountOptions.add("0");
    backupCountOptions.add("1");
    backupCountOptions.add("2");
    backupCountOptions.add("3");
    backupCountOptions.add("4");
    backupCountOptions.add("5");
    backupCountOptions.add("10");
    backupCountOptions.add(INFINITE_BACKUPS_STRING);
    String intervalString = Configuration.getValue(authoringToolPackage, 
      "maximumWorldBackupCount");
    int interval = -1;
    try {
      interval = Integer.parseInt(intervalString);
    }
    catch (Throwable localThrowable) {}
    addComboBoxValueValue(interval, backupCountOptions);
  }
  
  private void initBackupCountComboBox() {
    backupCountComboBox.removeAllItems();
    String intervalString = Configuration.getValue(authoringToolPackage, 
      "maximumWorldBackupCount");
    for (int i = 0; i < backupCountOptions.size(); i++) {
      backupCountComboBox.addItem(backupCountOptions.get(i));
      
      if (intervalString.equalsIgnoreCase(backupCountOptions.get(i).toString())) {
        backupCountComboBox.setSelectedIndex(i);
      }
      else if (intervalString.equalsIgnoreCase(Integer.toString(Integer.MAX_VALUE)))
      {
        if (((String)backupCountOptions.get(i)).equalsIgnoreCase(INFINITE_BACKUPS_STRING))
          backupCountComboBox.setSelectedIndex(i);
      }
    }
  }
  
  private void setFontSizeValues() {
    fontSizeOptions.removeAllElements();
    int fontSize = Integer.parseInt(authoringToolConfig
      .getValue("fontSize"));
    List size = new ArrayList();
    size.add(Integer.valueOf(8));
    size.add(Integer.valueOf(10));
    size.add(Integer.valueOf(12));
    size.add(Integer.valueOf(14));
    size.add(Integer.valueOf(16));
    size.add(Integer.valueOf(20));
    if ((fontSize != 8) && (fontSize != 10) && (fontSize != 12) && (fontSize != 14) && 
      (fontSize != 16) && (fontSize != 20)) {
      size.add(Integer.valueOf(fontSize));
    }
    Collections.sort(size);
    for (int i = 0; i < size.size(); i++) {
      fontSizeOptions.add(String.valueOf(size.get(i)));
    }
  }
  
  private void initFontSizeComboBox() {
    fontSizeComboBox.removeAllItems();
    String intervalString = Configuration.getValue(authoringToolPackage, 
      "fontSize");
    for (int i = 0; i < fontSizeOptions.size(); i++) {
      fontSizeComboBox.addItem(fontSizeOptions.get(i));
      
      if (intervalString.equalsIgnoreCase(fontSizeOptions.get(i).toString())) {
        fontSizeComboBox.setSelectedIndex(i);
      }
    }
  }
  















  void worldDirectoryBrowseButton_actionPerformed(ActionEvent ev)
  {
    File parent = new File(Configuration.getValue(
      authoringToolPackage, "directories.worldsDirectory"));
    browseFileChooser.setCurrentDirectory(parent);
    int returnVal = browseFileChooser.showOpenDialog(this);
    
    if (returnVal == 0) {
      File file = browseFileChooser.getSelectedFile();
      worldDirectoryTextField.setText(file.getAbsolutePath());
    }
  }
  
  void importDirectoryBrowseButton_actionPerformed(ActionEvent ev) {
    File parent = new File(Configuration.getValue(
      authoringToolPackage, "directories.importDirectory"))
      .getParentFile();
    browseFileChooser.setCurrentDirectory(parent);
    int returnVal = browseFileChooser.showOpenDialog(this);
    
    if (returnVal == 0) {
      File file = browseFileChooser.getSelectedFile();
      importDirectoryTextField.setText(file.getAbsolutePath());
    }
  }
  
  void browseButton_actionPerformed(ActionEvent e) {
    boolean done = false;
    String finalFilePath = captureDirectoryTextField.getText();
    while (!done) {
      File parent = new File(finalFilePath);
      if (!parent.exists()) {
        parent = new File(Configuration.getValue(
          authoringToolPackage, "screenCapture.directory"));
      }
      browseFileChooser.setCurrentDirectory(parent);
      int returnVal = browseFileChooser.showOpenDialog(this);
      
      if (returnVal == 0) {
        File captureDirectoryFile = browseFileChooser
          .getSelectedFile();
        int directoryCheck = 
          FileUtilities.isWritableDirectory(captureDirectoryFile);
        if (directoryCheck == -2) {
          done = false;
          
          DialogManager.showMessageDialog(
            Messages.getString("The_capture_directory_specified_can_not_be_written_to__Please_choose_another_directory_"), 
            Messages.getString("Bad_Directory"), 
            1);
        } else if ((directoryCheck == -1) || 
          (directoryCheck == -3)) {
          done = false;
          
          DialogManager.showMessageDialog(
            Messages.getString("The_capture_directory_must_be_a_directory_that_exists_"), 
            Messages.getString("Bad_Directory"), 
            1);
        } else {
          finalFilePath = captureDirectoryFile.getAbsolutePath();
          done = true;
        }
      } else {
        finalFilePath = parent.getAbsolutePath();
        done = true;
      }
    }
    captureDirectoryTextField.setText(finalFilePath);
  }
  




















  JPanel generalPanel = new JPanel();
  JPanel renderingPanel = new JPanel();
  JPanel screenGrabPanel = new JPanel();
  JPanel seldomUsedPanel = new JPanel();
  JPanel directoriesPanel = new JPanel();
  JPanel aseImporterPanel = new JPanel();
  
  JButton okayButton = new JButton();
  JButton cancelButton = new JButton();
  
  JCheckBox useBorderlessWindowCheckBox = new JCheckBox();
  JCheckBox infiniteBackupsCheckBox = new JCheckBox();
  JTextField importDirectoryTextField = new JTextField();
  Vector saveIntervalOptions = new Vector();
  Vector backupCountOptions = new Vector();
  Vector fontSizeOptions = new Vector();
  JLabel importDirectoryLabel = new JLabel();
  JButton importDirectoryBrowseButton = new JButton();
  JCheckBox enableScriptingCheckBox = new JCheckBox();
  JCheckBox doProfilingCheckBox = new JCheckBox();
  JCheckBox runtimeScratchPadEnabledCheckBox = new JCheckBox();
  JCheckBox saveAsSingleFileCheckBox = new JCheckBox();
  JCheckBox watcherPanelEnabledCheckBox = new JCheckBox();
  JComboBox saveIntervalComboBox = new JComboBox();
  
  JTabbedPane configTabbedPane = new JTabbedPane();
  
  private void jbInit()
  {
    setLayout(new BorderLayout());
    okayButton.setText(Messages.getString("Okay"));
    cancelButton.setText(Messages.getString("Cancel"));
    
    Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BorderLayout());
    buttonPanel.setBorder(emptyBorder);
    
    configTabbedPane.setBackground(new Color(204, 204, 204));
    add(configTabbedPane, "Center");
    
    GeneralTabInit();
    RenderingTabInit();
    ScreenGrabTabInit();
    SeldomUsedTabInit();
    ASEImportTabInit();
    
    generalPanel.setBorder(emptyBorder);
    renderingPanel.setBorder(emptyBorder);
    screenGrabPanel.setBorder(emptyBorder);
    seldomUsedPanel.setBorder(emptyBorder);
    
    configTabbedPane.add(generalPanel, Messages.getString("General"));
    configTabbedPane.add(renderingPanel, Messages.getString("Rendering"));
    
    configTabbedPane.add(screenGrabPanel, Messages.getString("Screen_Grab"));
    

    configTabbedPane.add(seldomUsedPanel, Messages.getString("Seldom_Used"));
    

    Box buttonBox = Box.createHorizontalBox();
    
    Component component1 = Box.createGlue();
    
    Component component2 = Box.createHorizontalStrut(8);
    
    Component component3 = Box.createGlue();
    
    buttonBox.add(component1, null);
    buttonBox.add(okayButton, null);
    buttonBox.add(component2, null);
    buttonBox.add(cancelButton, null);
    buttonBox.add(component3, null);
    buttonPanel.add(buttonBox, "Center");
    add(buttonPanel, "South");
  }
  
  private JTextField maxRecentWorldsTextField = new JTextField();
  private JLabel maxRecentWorldsLabel = new JLabel();
  private JComboBox resourceFileComboBox = new JComboBox();
  private JComboBox languageComboBox = new JComboBox();
  private JComboBox fontSizeComboBox = new JComboBox();
  private JTextField worldDirectoryTextField = new JTextField();
  
  private void GeneralTabInit() {
    JPanel maxRecentWorldsPanel = new JPanel();
    JPanel resourcesPanel = new JPanel();
    JPanel languagePanel = new JPanel();
    JPanel inputDirectoriesPanel = new JPanel();
    JPanel fontSizePanel = new JPanel();
    
    maxRecentWorldsTextField.setColumns(3);
    maxRecentWorldsTextField.setMinimumSize(new Dimension(50, 22));
    maxRecentWorldsTextField.setMargin(new Insets(1, 1, 1, 1));
    maxRecentWorldsLabel
      .setText(
      Messages.getString("maximum_number_of_worlds_kept_in_the_recent_worlds_menu"));
    
    maxRecentWorldsPanel.setLayout(new GridBagLayout());
    maxRecentWorldsPanel.add(maxRecentWorldsTextField, 
      new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 
      17, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    maxRecentWorldsPanel.add(maxRecentWorldsLabel, new GridBagConstraints(
      1, 0, 1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(5, 5, 5, 5), 0, 0));
    
    JLabel resourcesLabel = new JLabel();
    resourcesPanel.setLayout(new GridBagLayout());
    resourcesPanel.add(resourcesLabel, new GridBagConstraints(0, 0, 1, 1, 
      0.0D, 0.0D, 17, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    resourcesPanel.add(resourceFileComboBox, new GridBagConstraints(1, 0, 
      1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(5, 5, 5, 5), 0, 0));
    
    resourcesLabel.setText(Messages.getString("display_my_program_"));
    
    File resourceDirectory = new File(
    
      JAlice.getAliceHomeDirectory(), 
      "resources" + System.getProperty("file.separator") + 
      AikMin.locale).getAbsoluteFile();
    File[] resourceFiles = resourceDirectory
      .listFiles(AuthoringToolResources.resourceFileFilter);
    for (int i = 0; i < resourceFiles.length; i++) {
      resourceFileComboBox.addItem(resourceFiles[i].getName());
    }
    
    resourceFileComboBox.setRenderer(new ListCellRenderer()
    {
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
      {
        JLabel toReturn = new JLabel(
          Messages.getString("No_Name"));
        toReturn.setOpaque(true);
        


        String name = value.toString();
        if (name.equals("Alice Style.py")) {
          name = Messages.getString("Alice_Style");
        } else if (name.equals("Java Style.py")) {
          name = Messages.getString("Java_Style_in_Color");
        } else if (name.equals("Java Text Style.py")) {
          name = Messages.getString("Java_Style_in_Black___White");
        } else {
          int dotIndex = name.lastIndexOf(".");
          if (dotIndex > -1) {
            name = name.substring(0, dotIndex);
          }
        }
        toReturn.setText(name);
        if (isSelected) {
          toReturn.setBackground(list.getSelectionBackground());
          toReturn.setForeground(list.getSelectionForeground());
        } else {
          toReturn.setBackground(list.getBackground());
          toReturn.setForeground(list.getForeground());
        }
        
        return toReturn;
      }
    });
    resourceFileComboBox.setSelectedItem(Configuration.getValue(
      authoringToolPackage, "resourceFile"));
    

    JLabel languageLabel = new JLabel();
    languagePanel.setLayout(new GridBagLayout());
    languagePanel.add(languageLabel, new GridBagConstraints(0, 0, 1, 1, 
      0.0D, 0.0D, 17, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    languagePanel.add(languageComboBox, new GridBagConstraints(1, 0, 1, 1, 
      0.0D, 0.0D, 17, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    
    languageLabel.setText(Messages.getString("display_language_"));
    for (int i = 0; i < AikMin.listOfLanguages.length; i++) {
      languageComboBox.addItem(AikMin.listOfLanguages[i]);
    }
    languageComboBox.setSelectedItem(Configuration.getValue(
      authoringToolPackage, "language"));
    
    JLabel worldDirectoryLabel = new JLabel();
    JButton worldDirectoryBrowseButton = new JButton();
    worldDirectoryTextField.setColumns(15);
    inputDirectoriesPanel.setLayout(new GridBagLayout());
    inputDirectoriesPanel.add(worldDirectoryLabel, new GridBagConstraints(
      0, 0, 1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(5, 5, 5, 5), 0, 0));
    inputDirectoriesPanel.add(worldDirectoryTextField, 
      new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 
      17, 1, 
      new Insets(5, 5, 5, 5), 0, 0));
    inputDirectoriesPanel.add(worldDirectoryBrowseButton, 
      new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 
      17, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    
























    worldDirectoryLabel.setText(Messages.getString("save_and_load_from_"));
    worldDirectoryBrowseButton.setText(Messages.getString("Browse___"));
    worldDirectoryBrowseButton
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          worldDirectoryBrowseButton_actionPerformed(e);
        }
        
      });
    JLabel fontSizeLabel = new JLabel();
    
    fontSizePanel.setLayout(new GridBagLayout());
    fontSizePanel.add(fontSizeComboBox, new GridBagConstraints(0, 0, 1, 1, 
      0.0D, 0.0D, 17, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    fontSizePanel.add(fontSizeLabel, new GridBagConstraints(1, 0, 1, 1, 
      0.0D, 0.0D, 17, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    
    fontSizeComboBox.setEditable(true);
    fontSizeComboBox.setPreferredSize(new Dimension(55, 25));
    fontSizeComboBox.setMaximumRowCount(9);
    
    fontSizeLabel.setText(" " + Messages.getString("general_font_size__default_value_is_12_"));
    
    Component component = Box.createGlue();
    
    generalPanel.setLayout(new GridBagLayout());
    generalPanel.add(maxRecentWorldsPanel, new GridBagConstraints(0, 2, 1, 
      1, 1.0D, 0.0D, 17, 0, 
      new Insets(0, 0, 0, 0), 0, 0));
    



    generalPanel.add(resourcesPanel, new GridBagConstraints(0, 3, 1, 1, 
      1.0D, 0.0D, 17, 0, 
      new Insets(0, 0, 0, 0), 0, 0));
    generalPanel.add(languagePanel, new GridBagConstraints(0, 4, 1, 1, 1.0D, 
      0.0D, 17, 0, 
      new Insets(0, 0, 0, 0), 0, 0));
    generalPanel.add(inputDirectoriesPanel, new GridBagConstraints(0, 5, 1, 
      1, 1.0D, 0.0D, 17, 
      2, new Insets(0, 0, 0, 0), 0, 0));
    generalPanel.add(fontSizePanel, new GridBagConstraints(0, 6, 1, 1, 1.0D, 
      0.0D, 17, 0, 
      new Insets(0, 0, 0, 0), 0, 0));
    generalPanel.add(component, new GridBagConstraints(0, 7, 1, 1, 1.0D, 
      1.0D, 17, 2, 
      new Insets(0, 0, 0, 0), 0, 0));
  }
  









  private JCheckBox forceSoftwareRenderingCheckBox = new JCheckBox();
  private JCheckBox showFPSCheckBox = new JCheckBox();
  private JCheckBox deleteFiles = new JCheckBox();
  private JTextField boundsXTextField = new JTextField();
  private JTextField boundsYTextField = new JTextField();
  private JTextField boundsWidthTextField = new JTextField();
  private JTextField boundsHeightTextField = new JTextField();
  private JCheckBox constrainRenderDialogAspectCheckBox = new JCheckBox();
  private JCheckBox ensureRenderDialogIsOnScreenCheckBox = new JCheckBox();
  private JList rendererList = new JList();
  
  private void RenderingTabInit() {
    JPanel renderWindowBoundsPanel = new JPanel();
    JLabel rendererListLabel = new JLabel();
    JButton rendererMoveUpButton = new JButton();
    JButton rendererMoveDownButton = new JButton();
    
    forceSoftwareRenderingCheckBox.setText(" " + Messages.getString("force_software_rendering__slower_and_safer_"));
    showFPSCheckBox.setText(" " + Messages.getString("show_frames_per_second"));
    deleteFiles.setText(" " + Messages.getString("delete_frames_folder_after_exporting_video"));
    




    JLabel renderWindowBoundsLabel = new JLabel();
    JLabel boundsWidthLabel = new JLabel();
    JLabel boundsHeightLabel = new JLabel();
    JLabel boundsXLabel = new JLabel();
    JLabel boundsYLabel = new JLabel();
    
    renderWindowBoundsLabel.setText(
      Messages.getString("render_window_position_and_size_"));
    
    boundsXLabel.setHorizontalAlignment(0);
    boundsXLabel.setText(Messages.getString("horizontal_position_"));
    boundsXTextField.setColumns(5);
    boundsXTextField.setMinimumSize(new Dimension(61, 22));
    boundsXTextField.setMargin(new Insets(1, 1, 1, 1));
    boundsXTextField.getDocument().addDocumentListener(
      renderDialogBoundsChecker);
    
    boundsYLabel.setHorizontalAlignment(0);
    boundsYLabel.setText(" " + Messages.getString("vertical_position_"));
    boundsYTextField.setColumns(5);
    boundsYTextField.setMinimumSize(new Dimension(61, 22));
    boundsYTextField.setMargin(new Insets(1, 1, 1, 1));
    boundsYTextField.getDocument().addDocumentListener(
      renderDialogBoundsChecker);
    
    boundsWidthLabel.setHorizontalAlignment(0);
    boundsWidthLabel.setText(" " + Messages.getString("width_"));
    boundsWidthTextField.setColumns(5);
    boundsWidthTextField.setMinimumSize(new Dimension(61, 22));
    boundsWidthTextField.setMargin(new Insets(1, 1, 1, 1));
    boundsWidthTextField.getDocument().addDocumentListener(
      renderDialogWidthChecker);
    
    boundsHeightLabel.setText(" " + Messages.getString("height_"));
    boundsHeightTextField.setColumns(5);
    boundsHeightTextField.setMinimumSize(new Dimension(61, 22));
    boundsHeightTextField.setMargin(new Insets(1, 1, 1, 1));
    boundsHeightTextField.getDocument().addDocumentListener(
      renderDialogHeightChecker);
    
    renderWindowBoundsPanel.setLayout(new GridBagLayout());
    renderWindowBoundsPanel.add(renderWindowBoundsLabel, 
      new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 
      17, 0, 
      new Insets(3, 5, 3, 0), 0, 0));
    renderWindowBoundsPanel.add(boundsXLabel, new GridBagConstraints(0, 1, 
      1, 1, 0.0D, 0.0D, 13, 
      0, new Insets(3, 5, 3, 0), 0, 0));
    renderWindowBoundsPanel.add(boundsXTextField, new GridBagConstraints(1, 
      1, 1, 1, 1.0D, 0.0D, 10, 
      0, new Insets(3, 5, 3, 0), 0, 0));
    renderWindowBoundsPanel.add(boundsYLabel, new GridBagConstraints(0, 2, 
      1, 1, 0.0D, 0.0D, 13, 
      0, new Insets(3, 5, 3, 0), 0, 0));
    renderWindowBoundsPanel.add(boundsYTextField, new GridBagConstraints(1, 
      2, 2, 1, 1.0D, 0.0D, 10, 
      0, new Insets(3, 5, 3, 0), 0, 0));
    renderWindowBoundsPanel.add(boundsWidthLabel, new GridBagConstraints(0, 
      3, 1, 1, 0.0D, 0.0D, 13, 
      0, new Insets(3, 5, 3, 0), 0, 0));
    renderWindowBoundsPanel.add(boundsWidthTextField, 
      new GridBagConstraints(1, 3, 1, 1, 1.0D, 0.0D, 
      10, 0, 
      new Insets(3, 5, 3, 0), 0, 0));
    renderWindowBoundsPanel.add(boundsHeightLabel, new GridBagConstraints(
      0, 4, 1, 1, 0.0D, 0.0D, 13, 
      0, new Insets(3, 5, 3, 0), 0, 0));
    renderWindowBoundsPanel.add(boundsHeightTextField, 
      new GridBagConstraints(1, 4, 1, 1, 1.0D, 0.0D, 
      10, 0, 
      new Insets(3, 5, 3, 0), 0, 0));
    
    constrainRenderDialogAspectCheckBox.setText(" " + Messages.getString("constrain_render_window_s_aspect_ratio"));
    constrainRenderDialogAspectCheckBox
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          checkAndUpdateRenderBounds();
        }
        

      });ensureRenderDialogIsOnScreenCheckBox
      .setText(" " + Messages.getString("make_sure_the_render_window_is_always_on_the_screen"));
    
    rendererListLabel
      .setText(Messages.getString("renderer_order__top_item_will_be_tried_first__bottom_item_will_be_tried_last__"));
    
    rendererList.setModel(new ConfigListModel(authoringToolPackage, 
      "rendering.orderedRendererList"));
    rendererList.setSelectedIndex(0);
    rendererList.setBorder(BorderFactory.createLineBorder(Color.black));
    rendererList.setSelectionMode(0);
    
    rendererMoveUpButton.setText(Messages.getString("move_up"));
    rendererMoveUpButton
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Object selectedItem = rendererList.getSelectedValue();
          ((PreferencesContentPane.ConfigListModel)rendererList.getModel())
            .moveIndexHigher(rendererList
            .getSelectedIndex());
          rendererList.setSelectedValue(selectedItem, false);
        }
      });
    rendererMoveDownButton.setText(Messages.getString("move_down"));
    rendererMoveDownButton
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Object selectedItem = rendererList.getSelectedValue();
          ((PreferencesContentPane.ConfigListModel)rendererList.getModel())
            .moveIndexLower(rendererList.getSelectedIndex());
          rendererList.setSelectedValue(selectedItem, false);
        }
        
      });
    Component component = Box.createGlue();
    renderingPanel.setLayout(new GridBagLayout());
    

    renderingPanel.add(forceSoftwareRenderingCheckBox, 
      new GridBagConstraints(0, 0, 2, 1, 0.0D, 0.0D, 
      17, 0, 
      new Insets(0, 0, 0, 0), 0, 0));
    renderingPanel.add(showFPSCheckBox, new GridBagConstraints(0, 1, 2, 1, 
      0.0D, 0.0D, 17, 0, 
      new Insets(0, 0, 0, 0), 0, 0));
    
    renderingPanel.add(deleteFiles, new GridBagConstraints(0, 2, 2, 1, 0.0D, 
      0.0D, 17, 0, 
      new Insets(0, 0, 0, 0), 0, 0));
    renderingPanel.add(renderWindowBoundsPanel, new GridBagConstraints(0, 
      3, 2, 1, 0.0D, 0.0D, 17, 
      0, new Insets(10, 0, 0, 0), 0, 0));
    renderingPanel.add(constrainRenderDialogAspectCheckBox, 
      new GridBagConstraints(0, 4, 2, 1, 0.0D, 0.0D, 
      17, 0, 
      new Insets(10, 0, 0, 0), 0, 0));
    renderingPanel.add(ensureRenderDialogIsOnScreenCheckBox, 
      new GridBagConstraints(0, 5, 2, 1, 0.0D, 0.0D, 
      17, 0, 
      new Insets(0, 0, 0, 0), 0, 0));
    renderingPanel.add(rendererListLabel, new GridBagConstraints(0, 6, 2, 
      1, 0.0D, 0.0D, 17, 0, 
      new Insets(10, 5, 0, 0), 0, 0));
    renderingPanel.add(rendererList, new GridBagConstraints(0, 7, 2, 1, 
      1.0D, 0.0D, 17, 
      2, new Insets(5, 5, 0, 5), 0, 0));
    renderingPanel.add(rendererMoveUpButton, new GridBagConstraints(0, 8, 
      1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(5, 5, 5, 5), 0, 0));
    renderingPanel.add(rendererMoveDownButton, new GridBagConstraints(1, 8, 
      1, 1, 1.0D, 0.0D, 17, 
      0, new Insets(5, 5, 5, 5), 0, 0));
    renderingPanel.add(component, new GridBagConstraints(0, 9, 2, 1, 1.0D, 
      1.0D, 10, 0, 
      new Insets(0, 0, 0, 0), 0, 0));
  }
  

  JTextField captureDirectoryTextField = new JTextField();
  private JTextField baseNameTextField = new JTextField();
  private JComboBox numDigitsComboBox = new JComboBox();
  private JComboBox codecComboBox = new JComboBox();
  private JCheckBox screenCaptureInformUserCheckBox = new JCheckBox();
  
  private void ScreenGrabTabInit() {
    JLabel captureDirectory = new JLabel();
    captureDirectory.setText(Messages.getString("directory_to_capture_to_"));
    captureDirectoryTextField.getDocument().addDocumentListener(
      captureDirectoryChangeListener);
    captureDirectoryTextField.setColumns(15);
    
    JButton browseButton = new JButton();
    browseButton.setText(Messages.getString("Browse___"));
    browseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        browseButton_actionPerformed(e);
      }
      
    });
    JLabel baseNameLabel = new JLabel();
    baseNameLabel.setText(Messages.getString("base_filename_"));
    
    baseNameTextField.setMinimumSize(new Dimension(100, 28));
    baseNameTextField.setPreferredSize(new Dimension(100, 28));
    
    JLabel numDigitsLabel = new JLabel();
    numDigitsLabel.setText(
      Messages.getString("number_of_digits_to_append_"));
    
    numDigitsComboBox.addItem("1");
    numDigitsComboBox.addItem("2");
    numDigitsComboBox.addItem("3");
    numDigitsComboBox.addItem("4");
    numDigitsComboBox.addItem("5");
    numDigitsComboBox.addItem("6");
    
    JLabel codecLabel = new JLabel();
    codecLabel.setText(Messages.getString("image_format_"));
    codecComboBox.setPreferredSize(new Dimension(60, 25));
    codecComboBox.addItem("jpeg");
    codecComboBox.addItem("png");
    
    screenCaptureInformUserCheckBox.setText(" " + Messages.getString("show_information_dialog_when_capture_is_made"));
    
    JLabel usageLabel = new JLabel();
    usageLabel.setText(Messages.getString("Note__use_Ctrl_G_to_grab_a_frame_while_the_world_is_running_"));
    
    screenGrabPanel.setLayout(new GridBagLayout());
    
    Component component = Box.createGlue();
    screenGrabPanel.add(captureDirectory, new GridBagConstraints(0, 0, 1, 
      1, 0.0D, 0.0D, 13, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    screenGrabPanel.add(captureDirectoryTextField, new GridBagConstraints(
      1, 0, 1, 1, 1.0D, 0.0D, 10, 
      1, new Insets(5, 5, 5, 5), 0, 0));
    screenGrabPanel.add(browseButton, new GridBagConstraints(2, 0, 1, 1, 
      0.0D, 0.0D, 10, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    screenGrabPanel.add(baseNameLabel, new GridBagConstraints(0, 1, 1, 1, 
      0.0D, 0.0D, 13, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    screenGrabPanel.add(baseNameTextField, new GridBagConstraints(1, 1, 1, 
      1, 0.0D, 0.0D, 17, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    screenGrabPanel.add(numDigitsLabel, new GridBagConstraints(0, 2, 1, 1, 
      0.0D, 0.0D, 13, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    screenGrabPanel.add(numDigitsComboBox, new GridBagConstraints(1, 2, 1, 
      1, 0.0D, 0.0D, 17, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    screenGrabPanel.add(codecLabel, new GridBagConstraints(0, 3, 1, 1, 0.0D, 
      0.0D, 13, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    screenGrabPanel.add(codecComboBox, new GridBagConstraints(1, 3, 1, 1, 
      0.0D, 0.0D, 17, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
    screenGrabPanel.add(screenCaptureInformUserCheckBox, 
      new GridBagConstraints(0, 4, 3, 1, 0.0D, 0.0D, 
      17, 0, 
      new Insets(0, 0, 0, 0), 0, 0));
    screenGrabPanel.add(component, new GridBagConstraints(0, 5, 3, 1, 1.0D, 
      1.0D, 10, 1, 
      new Insets(0, 0, 0, 0), 0, 0));
    screenGrabPanel.add(usageLabel, new GridBagConstraints(0, 6, 3, 1, 0.0D, 
      0.0D, 16, 0, 
      new Insets(5, 5, 5, 5), 0, 0));
  }
  
  private JCheckBox showStartUpDialogCheckBox = new JCheckBox();
  private JCheckBox showWebWarningCheckBox = new JCheckBox();
  private JCheckBox loadSavedTabsCheckBox = new JCheckBox();
  private JCheckBox pickUpTilesCheckBox = new JCheckBox();
  private JCheckBox useAlphaTilesCheckBox = new JCheckBox();
  private JCheckBox saveThumbnailWithWorldCheckBox = new JCheckBox();
  private JCheckBox showWorldStatsCheckBox = new JCheckBox();
  private JCheckBox clearStdOutOnRunCheckBox = new JCheckBox();
  private JCheckBox enableHighContrastCheckBox = new JCheckBox();
  private JCheckBox enableLoggingCheckBox = new JCheckBox();
  private JCheckBox disableTooltipCheckBox = new JCheckBox();
  private JTextField numClipboardsTextField = new JTextField();
  private JComboBox backupCountComboBox = new JComboBox();
  private JCheckBox showBuilderCheckBox = new JCheckBox();
  
  private void SeldomUsedTabInit() {
    JPanel saveIntervalPanel = new JPanel();
    JPanel backupCountPanel = new JPanel();
    
    showStartUpDialogCheckBox.setText(" " + Messages.getString("show_startup_dialog_when_Alice_launches"));
    showWebWarningCheckBox.setText(" " + Messages.getString("show_warning_when_browsing_the_web_gallery"));
    loadSavedTabsCheckBox.setText(" " + Messages.getString("open_tabs_that_were_previously_open_on_world_load"));
    pickUpTilesCheckBox.setText(" " + Messages.getString("pick_up_tiles_while_dragging_and_dropping__reduces_performance_"));
    useAlphaTilesCheckBox.setText(" " + Messages.getString("use_alpha_blending_in_picked_up_tiles__really_reduces_performance_"));
    saveThumbnailWithWorldCheckBox.setText(" " + Messages.getString("save_thumbnail_with_world"));
    showWorldStatsCheckBox.setText(" " + Messages.getString("show_world_statistics"));
    clearStdOutOnRunCheckBox.setText(" " + Messages.getString("clear_text_output_on_play"));
    enableHighContrastCheckBox.setText(" " + Messages.getString("enable_high_contrast_mode_for_projectors"));
    enableLoggingCheckBox.setText(" " + Messages.getString("enable_logging"));
    disableTooltipCheckBox.setText(" " + Messages.getString("disable_tooltip"));
    showBuilderCheckBox.setText(" " + Messages.getString("show_he_she_builder_in_gallery"));
    
    JLabel numClipboardsLabel = new JLabel();
    numClipboardsTextField.setColumns(3);
    numClipboardsTextField.setMargin(new Insets(1, 1, 1, 1));
    numClipboardsLabel.setText(Messages.getString("number_of_clipboards"));
    

    JPanel numClipboardsPanel = new JPanel();
    BorderLayout borderLayout = new BorderLayout();
    borderLayout.setHgap(8);
    numClipboardsPanel.setLayout(borderLayout);
    numClipboardsPanel.add(numClipboardsTextField, "West");
    numClipboardsPanel.add(numClipboardsLabel, "Center");
    
    JLabel saveIntervalLabelEnd = new JLabel();
    saveIntervalComboBox.setEditable(true);
    saveIntervalComboBox.setPreferredSize(new Dimension(60, 25));
    saveIntervalLabelEnd.setText(" " + Messages.getString("number_of_minutes_to_wait_before_displaying_save_reminder"));
    
    saveIntervalPanel.setOpaque(false);
    saveIntervalPanel.setBorder(null);
    saveIntervalPanel.add(saveIntervalComboBox);
    saveIntervalPanel.add(saveIntervalLabelEnd);
    
    JLabel backupCountLabel = new JLabel();
    backupCountLabel.setText(" " + Messages.getString("number_of_backups_of_each_world_to_save"));
    backupCountComboBox.setEditable(true);
    backupCountComboBox.setPreferredSize(new Dimension(60, 25));
    backupCountComboBox.setMaximumRowCount(9);
    
    backupCountPanel.setOpaque(false);
    backupCountPanel.setBorder(null);
    backupCountPanel.add(backupCountComboBox);
    backupCountPanel.add(backupCountLabel);
    

    seldomUsedPanel.setLayout(new GridBagLayout());
    
    seldomUsedPanel.add(showStartUpDialogCheckBox, new GridBagConstraints(
      0, 0, 1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(0, 0, 0, 0), 0, 0));
    seldomUsedPanel.add(showWebWarningCheckBox, new GridBagConstraints(0, 
      1, 1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(0, 0, 0, 0), 0, 0));
    seldomUsedPanel.add(loadSavedTabsCheckBox, new GridBagConstraints(0, 2, 
      1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(0, 0, 0, 0), 0, 0));
    seldomUsedPanel.add(pickUpTilesCheckBox, new GridBagConstraints(0, 3, 
      1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(0, 0, 0, 0), 0, 0));
    seldomUsedPanel.add(useAlphaTilesCheckBox, new GridBagConstraints(0, 4, 
      1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(0, 0, 0, 0), 0, 0));
    seldomUsedPanel.add(saveThumbnailWithWorldCheckBox, 
      new GridBagConstraints(0, 5, 1, 1, 0.0D, 0.0D, 
      17, 0, 
      new Insets(0, 0, 0, 0), 0, 0));
    seldomUsedPanel.add(showWorldStatsCheckBox, new GridBagConstraints(0, 
      6, 1, 1, 0.0D, 0.0D, 17, 
      2, new Insets(0, 0, 0, 0), 0, 0));
    



    seldomUsedPanel.add(clearStdOutOnRunCheckBox, new GridBagConstraints(0, 
      7, 1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(0, 0, 0, 0), 0, 0));
    seldomUsedPanel.add(enableHighContrastCheckBox, new GridBagConstraints(
      0, 8, 1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(0, 0, 0, 0), 0, 0));
    seldomUsedPanel.add(enableLoggingCheckBox, new GridBagConstraints(0, 9, 
      1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(0, 0, 0, 0), 0, 0));
    seldomUsedPanel.add(enableScriptingCheckBox, 
      new GridBagConstraints(0, 10, 1, 1, 0.0D, 0.0D, 
      17, 2, 
      new Insets(0, 0, 0, 0), 0, 0));
    
    seldomUsedPanel.add(disableTooltipCheckBox, new GridBagConstraints(0, 
      11, 1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(0, 0, 0, 0), 0, 0));
    seldomUsedPanel.add(showBuilderCheckBox, new GridBagConstraints(0, 
      12, 1, 1, 0.0D, 0.0D, 17, 
      0, new Insets(0, 0, 0, 0), 0, 0));
    
    seldomUsedPanel.add(numClipboardsPanel, new GridBagConstraints(0, 13, 
      1, 1, 0.0D, 0.0D, 17, 
      2, new Insets(5, 5, 5, 0), 0, 0));
    seldomUsedPanel.add(saveIntervalPanel, new GridBagConstraints(0, 14, 1, 
      1, 0.0D, 0.0D, 17, 0, 
      new Insets(0, 0, 0, 0), 0, 0));
    seldomUsedPanel.add(backupCountPanel, new GridBagConstraints(0, 15, 1, 
      1, 0.0D, 0.0D, 17, 0, 
      new Insets(0, 0, 0, 0), 0, 0));
    




















    seldomUsedPanel.add(Box.createVerticalGlue(), 
      new GridBagConstraints(0, 16, 1, 1, 1.0D, 1.0D, 
      10, 1, 
      new Insets(0, 0, 0, 0), 0, 0));
    

    if (Configuration.getValue(authoringToolPackage, "disableTooltipMode").equalsIgnoreCase("true")) {
      ToolTipManager.sharedInstance().setEnabled(false);
    } else {
      ToolTipManager.sharedInstance().setEnabled(true);
    }
  }
  

  JCheckBox createNormalsCheckBox = new JCheckBox();
  JCheckBox createUVsCheckBox = new JCheckBox();
  JCheckBox useSpecularCheckBox = new JCheckBox();
  JCheckBox groupMultipleRootObjectsCheckBox = new JCheckBox();
  JCheckBox colorToWhiteWhenTexturedCheckBox = new JCheckBox();
  
  private void ASEImportTabInit()
  {
    Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    
    createNormalsCheckBox.setText(Messages.getString("create_normals_if_none_exist"));
    createUVsCheckBox.setText(Messages.getString("create_uv_coordinates_if_none_exist"));
    useSpecularCheckBox.setText(Messages.getString("use_specular_information_if_given_in_ASE_file"));
    groupMultipleRootObjectsCheckBox.setText(Messages.getString("group_multiple_root_objects"));
    colorToWhiteWhenTexturedCheckBox.setText(Messages.getString("set_ambient_and_diffuse_color_to_white_if_object_is_textured"));
    

    Box aseImporterBox = Box.createVerticalBox();
    aseImporterBox.add(createNormalsCheckBox, null);
    aseImporterBox.add(createUVsCheckBox, null);
    aseImporterBox.add(useSpecularCheckBox, null);
    aseImporterBox.add(groupMultipleRootObjectsCheckBox, null);
    aseImporterBox.add(colorToWhiteWhenTexturedCheckBox, null);
    
    aseImporterPanel.setLayout(new BorderLayout());
    aseImporterPanel.setBorder(emptyBorder);
    aseImporterPanel.add(aseImporterBox, "Center");
  }
}
