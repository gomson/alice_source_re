package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;




















public class Actions
{
  public AbstractAction newWorldAction;
  public AbstractAction openWorldAction;
  public AbstractAction openExampleWorldAction;
  public AbstractAction saveWorldAction;
  public AbstractAction saveWorld;
  public AbstractAction saveWorldAsAction;
  public AbstractAction saveForWebAction;
  public AbstractAction importObjectAction;
  public AbstractAction quitAction;
  public AbstractAction cutAction;
  public AbstractAction copyAction;
  public AbstractAction pasteAction;
  public AbstractAction undoAction;
  public AbstractAction redoAction;
  public AbstractAction aboutAction;
  public AbstractAction playAction;
  public AbstractAction addCharacterAction;
  public AbstractAction add3DTextAction;
  public AbstractAction exportMovieAction;
  public AbstractAction trashAction;
  public AbstractAction helpAction;
  public AbstractAction onScreenHelpAction;
  public AbstractAction preferencesAction;
  public AbstractAction makeBillboardAction;
  public AbstractAction showWorldInfoAction;
  public AbstractAction launchTutorialAction;
  public AbstractAction launchTutorialFileAction;
  public AbstractAction launchTutorialEditor;
  public AbstractAction launchSoftwareUpdate;
  public AbstractAction showStdOutDialogAction;
  public AbstractAction showStdErrDialogAction;
  public AbstractAction showPrintDialogAction;
  public AbstractAction pauseWorldAction;
  public AbstractAction resumeWorldAction;
  public AbstractAction restartWorldAction;
  public AbstractAction stopWorldAction;
  public AbstractAction takePictureAction;
  public AbstractAction restartStopWorldAction;
  public AbstractAction logInstructorIntervention;
  public AbstractAction licenseAction;
  protected AuthoringTool authoringTool;
  protected JAliceFrame jAliceFrame;
  protected LinkedList applicationActions = new LinkedList();
  public LinkedList renderActions = new LinkedList();
  
  public Actions(AuthoringTool authoringTool, JAliceFrame jAliceFrame) {
    this.authoringTool = authoringTool;
    this.jAliceFrame = jAliceFrame;
    actionInit();
    keyInit();
    undoAction.setEnabled(false);
    redoAction.setEnabled(false);
  }
  
  private void actionInit() {
    newWorldAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.newWorld();
      }
      
    };
    openWorldAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.openWorld();
      }
      
    };
    openExampleWorldAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.openExampleWorld();
      }
      
    };
    saveWorldAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.saveWorld();
      }
      
    };
    saveWorld = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.saveWorld();
      }
      
    };
    saveWorldAsAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.saveWorldAs();
      }
      
    };
    saveForWebAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.saveForWeb();
      }
      
    };
    importObjectAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.getImportFileChooser().setFileFilter(authoringTool.getImportFileChooser().getAcceptAllFileFilter());
        authoringTool.importElement();
      }
      
    };
    quitAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.quit(false);
      }
      
    };
    cutAction = new AbstractAction()
    {

      public void actionPerformed(ActionEvent e) {}

    };
    copyAction = new AbstractAction()
    {

      public void actionPerformed(ActionEvent e) {}

    };
    pasteAction = new AbstractAction()
    {

      public void actionPerformed(ActionEvent e) {}

    };
    undoAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.getUndoRedoStack().undo();
      }
      
    };
    redoAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.getUndoRedoStack().redo();
      }
      
    };
    aboutAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.showAbout();
      }
      
    };
    playAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.play();
      }
      
    };
    addCharacterAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.loadAndAddCharacter();
      }
      
    };
    add3DTextAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.add3DText();
      }
      
    };
    exportMovieAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.exportMovie();
      }
      
    };
    trashAction = new AbstractAction()
    {

      public void actionPerformed(ActionEvent e) {}

    };
    helpAction = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e) {}

    };
    onScreenHelpAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.showOnScreenHelp();
      }
      
    };
    preferencesAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.showPreferences();
      }
      
    };
    makeBillboardAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.makeBillboard();
      }
      
    };
    showWorldInfoAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.showWorldInfoDialog();
      }
      
    };
    launchTutorialAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.launchTutorial();
      }
      
    };
    launchTutorialFileAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.openTutorialWorld();
      }
      
    };
    launchTutorialEditor = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.openTutorialEditor();
      }
      
    };
    launchSoftwareUpdate = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.updateAlice();
      }
      
    };
    showStdOutDialogAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.showStdOutDialog();
      }
      
    };
    showStdErrDialogAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.showStdErrDialog();
      }
      
    };
    showPrintDialogAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.showPrintDialog();
      }
      
    };
    pauseWorldAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.pause();
      }
      
    };
    resumeWorldAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.resume();
      }
      
    };
    restartWorldAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.restartWorld();
      }
      
    };
    restartStopWorldAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.restartWorld();
        authoringTool.pause();
      }
      

    };
    stopWorldAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.stopWorld();
      }
      
    };
    takePictureAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        takePictureAction.setEnabled(false);
        authoringTool.takePicture();
        takePictureAction.setEnabled(true);
      }
      

    };
    logInstructorIntervention = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.logInstructorIntervention();
      }
      
    };
    licenseAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        authoringTool.showLicense();
      }
      

    };
    newWorldAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(78, 2));
    newWorldAction.putValue("ActionCommandKey", "newWorld");
    newWorldAction.putValue("MnemonicKey", new Integer(78));
    newWorldAction.putValue("Name", Messages.getString("New_World"));
    newWorldAction.putValue("ShortDescription", Messages.getString("Create_a_new_world"));
    newWorldAction.putValue("SmallIcon", AuthoringToolResources.getIconForString("new"));
    applicationActions.add(newWorldAction);
    
    openWorldAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(79, 2));
    openWorldAction.putValue("ActionCommandKey", "openWorld");
    openWorldAction.putValue("MnemonicKey", new Integer(79));
    openWorldAction.putValue("Name", Messages.getString("Open_World___"));
    openWorldAction.putValue("ShortDescription", Messages.getString("Open_an_existing_world"));
    openWorldAction.putValue("SmallIcon", AuthoringToolResources.getIconForString("open"));
    applicationActions.add(openWorldAction);
    
    saveWorldAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(83, 2));
    saveWorldAction.putValue("ActionCommandKey", "saveWorld");
    saveWorldAction.putValue("MnemonicKey", new Integer(83));
    saveWorldAction.putValue("Name", Messages.getString("Save_World"));
    saveWorldAction.putValue("ShortDescription", Messages.getString("Save_the_current_world"));
    saveWorldAction.putValue("SmallIcon", AuthoringToolResources.getIconForString("save"));
    applicationActions.add(saveWorldAction);
    
    saveWorld.putValue("AcceleratorKey", KeyStroke.getKeyStroke(113, 0));
    saveWorld.putValue("ActionCommandKey", "saveWorld");
    saveWorld.putValue("Name", Messages.getString("Save_World"));
    applicationActions.add(saveWorld);
    

    saveWorldAsAction.putValue("ActionCommandKey", "saveWorldAs");
    saveWorldAsAction.putValue("MnemonicKey", new Integer(118));
    saveWorldAsAction.putValue("Name", Messages.getString("Save_World_As___"));
    saveWorldAsAction.putValue("ShortDescription", Messages.getString("Save_the_current_world"));
    
    applicationActions.add(saveWorldAsAction);
    

    saveForWebAction.putValue("ActionCommandKey", "saveForWeb");
    saveForWebAction.putValue("MnemonicKey", new Integer(119));
    saveForWebAction.putValue("Name", Messages.getString("Export_As_A_Web_Page___"));
    saveForWebAction.putValue("ShortDescription", Messages.getString("Export_as_a_web_page"));
    
    applicationActions.add(saveForWebAction);
    

    importObjectAction.putValue("ActionCommandKey", "importObject");
    importObjectAction.putValue("MnemonicKey", new Integer(73));
    importObjectAction.putValue("Name", Messages.getString("Import___"));
    importObjectAction.putValue("ShortDescription", Messages.getString("Import"));
    importObjectAction.putValue("SmallIcon", AuthoringToolResources.getIconForString("import"));
    applicationActions.add(importObjectAction);
    

    quitAction.putValue("ActionCommandKey", "quit");
    quitAction.putValue("MnemonicKey", new Integer(120));
    quitAction.putValue("Name", Messages.getString("Exit"));
    quitAction.putValue("ShortDescription", Messages.getString("Exit_Alice"));
    
    applicationActions.add(quitAction);
    
    cutAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(88, 2));
    cutAction.putValue("ActionCommandKey", "cut");
    cutAction.putValue("MnemonicKey", new Integer(116));
    cutAction.putValue("Name", Messages.getString("Cut"));
    cutAction.putValue("ShortDescription", Messages.getString("Cut"));
    cutAction.putValue("SmallIcon", AuthoringToolResources.getIconForString("cut"));
    applicationActions.add(cutAction);
    
    copyAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(67, 2));
    copyAction.putValue("ActionCommandKey", "copy");
    copyAction.putValue("MnemonicKey", new Integer(67));
    copyAction.putValue("Name", Messages.getString("Copy"));
    copyAction.putValue("ShortDescription", Messages.getString("Copy"));
    copyAction.putValue("SmallIcon", AuthoringToolResources.getIconForString("copy"));
    applicationActions.add(copyAction);
    
    pasteAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(86, 2));
    pasteAction.putValue("ActionCommandKey", "paste");
    pasteAction.putValue("MnemonicKey", new Integer(80));
    pasteAction.putValue("Name", Messages.getString("Paste"));
    pasteAction.putValue("ShortDescription", Messages.getString("Paste"));
    pasteAction.putValue("SmallIcon", AuthoringToolResources.getIconForString("paste"));
    applicationActions.add(pasteAction);
    
    undoAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(90, 2));
    undoAction.putValue("ActionCommandKey", "undo");
    undoAction.putValue("MnemonicKey", new Integer(85));
    undoAction.putValue("Name", Messages.getString("Undo"));
    undoAction.putValue("ShortDescription", Messages.getString("Undo_the_Last_Action"));
    undoAction.putValue("SmallIcon", AuthoringToolResources.getIconForString("undo"));
    applicationActions.add(undoAction);
    
    redoAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(89, 2));
    redoAction.putValue("ActionCommandKey", "redo");
    redoAction.putValue("MnemonicKey", new Integer(82));
    redoAction.putValue("Name", Messages.getString("Redo"));
    redoAction.putValue("ShortDescription", Messages.getString("Redo"));
    redoAction.putValue("SmallIcon", AuthoringToolResources.getIconForString("redo"));
    applicationActions.add(redoAction);
    

    aboutAction.putValue("ActionCommandKey", "about");
    aboutAction.putValue("MnemonicKey", new Integer(65));
    aboutAction.putValue("Name", Messages.getString("About_Alice"));
    aboutAction.putValue("ShortDescription", Messages.getString("About_Alice"));
    aboutAction.putValue("SmallIcon", AuthoringToolResources.getIconForString("about"));
    applicationActions.add(aboutAction);
    

    onScreenHelpAction.putValue("ActionCommandKey", "onScreenHelp");
    onScreenHelpAction.putValue("MnemonicKey", new Integer(79));
    onScreenHelpAction.putValue("Name", Messages.getString("On_Screen_Help__experimental_"));
    onScreenHelpAction.putValue("ShortDescription", Messages.getString("Experimental_Tutorial_Editor"));
    
    applicationActions.add(onScreenHelpAction);
    
    playAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(116, 0));
    playAction.putValue("ActionCommandKey", "play");
    
    playAction.putValue("Name", Messages.getString("Play"));
    playAction.putValue("ShortDescription", "<html><font face=arial size=-1>" + Messages.getString("Play_the_world_") + "<p><p>" + Messages.getString("Opens_the_play_window_and_p_starts_the_world_running_") + "</font></html>");
    playAction.putValue("SmallIcon", AuthoringToolResources.getIconForString("play"));
    applicationActions.add(playAction);
    

    addCharacterAction.putValue("ActionCommandKey", "addObject");
    
    addCharacterAction.putValue("Name", Messages.getString("Add_Object___"));
    addCharacterAction.putValue("ShortDescription", Messages.getString("Add_a_previously_stored_Object"));
    
    applicationActions.add(addCharacterAction);
    

    add3DTextAction.putValue("ActionCommandKey", "add3DText");
    
    add3DTextAction.putValue("Name", Messages.getString("Add_3D_Text___"));
    add3DTextAction.putValue("ShortDescription", Messages.getString("Add_Text_extruded_into_3D"));
    
    applicationActions.add(add3DTextAction);
    
    exportMovieAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(117, 0));
    exportMovieAction.putValue("ActionCommandKey", "exportVideo");
    exportMovieAction.putValue("Name", Messages.getString("Export_Video___"));
    exportMovieAction.putValue("ShortDescription", Messages.getString("Export_the_current_world_as_a_video"));
    applicationActions.add(exportMovieAction);
    

    trashAction.putValue("ActionCommandKey", "trash");
    

    trashAction.putValue("ShortDescription", "<html><font face=arial size=-1>" + Messages.getString("Trash") + "<p><p>" + Messages.getString("Drag_and_drop_tiles_here_to_delete_them_") + "</font></html>");
    
    applicationActions.add(trashAction);
    

    openExampleWorldAction.putValue("ActionCommandKey", "openExampleWorld");
    
    openExampleWorldAction.putValue("Name", Messages.getString("Example_Worlds"));
    openExampleWorldAction.putValue("ShortDescription", Messages.getString("Open_an_Example_World"));
    
    applicationActions.add(openExampleWorldAction);
    

    helpAction.putValue("ActionCommandKey", "help");
    helpAction.putValue("MnemonicKey", new Integer(72));
    helpAction.putValue("Name", Messages.getString("Help_Topics"));
    helpAction.putValue("ShortDescription", Messages.getString("Alice_Documentation"));
    
    applicationActions.add(helpAction);
    
    preferencesAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(119, 0));
    preferencesAction.putValue("ActionCommandKey", "preferences");
    preferencesAction.putValue("MnemonicKey", new Integer(80));
    preferencesAction.putValue("Name", Messages.getString("Preferences"));
    preferencesAction.putValue("ShortDescription", Messages.getString("Set_Preferences"));
    
    applicationActions.add(preferencesAction);
    
    makeBillboardAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(66, 2));
    makeBillboardAction.putValue("ActionCommandKey", "makeBillboard");
    makeBillboardAction.putValue("MnemonicKey", new Integer(66));
    makeBillboardAction.putValue("Name", Messages.getString("Make_Billboard___"));
    makeBillboardAction.putValue("ShortDescription", Messages.getString("Make_a_billboard_object_from_an_image"));
    
    applicationActions.add(makeBillboardAction);
    

    showWorldInfoAction.putValue("ActionCommandKey", "showWorldInfo");
    showWorldInfoAction.putValue("MnemonicKey", new Integer(73));
    showWorldInfoAction.putValue("Name", Messages.getString("World_Statistics"));
    showWorldInfoAction.putValue("ShortDescription", Messages.getString("Show_information_about_the_current_world"));
    
    applicationActions.add(showWorldInfoAction);
    

    launchTutorialAction.putValue("ActionCommandKey", "launchTutorial");
    launchTutorialAction.putValue("MnemonicKey", new Integer(84));
    launchTutorialAction.putValue("Name", Messages.getString("Teach_Me"));
    launchTutorialAction.putValue("ShortDescription", Messages.getString("Launch_the_Tutorial"));
    
    applicationActions.add(launchTutorialAction);
    
    launchTutorialFileAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(112, 0));
    launchTutorialFileAction.putValue("ActionCommandKey", "launchTutorialFile");
    launchTutorialFileAction.putValue("MnemonicKey", new Integer(84));
    launchTutorialFileAction.putValue("Name", Messages.getString("Tutorial"));
    launchTutorialFileAction.putValue("ShortDescription", Messages.getString("Open_a_tutorial"));
    
    applicationActions.add(launchTutorialFileAction);
    

    launchTutorialEditor.putValue("ActionCommandKey", "launchTutorialEditor");
    
    launchTutorialEditor.putValue("Name", "Tutorial Editor");
    launchTutorialEditor.putValue("ShortDescription", "Create or edit a tutorial");
    
    applicationActions.add(launchTutorialEditor);
    

    launchSoftwareUpdate.putValue("ActionCommandKey", "launchSoftwareUpdate");
    
    launchSoftwareUpdate.putValue("Name", Messages.getString("Update_Software"));
    launchSoftwareUpdate.putValue("ShortDescription", Messages.getString("Update_Software"));
    
    applicationActions.add(launchSoftwareUpdate);
    

    showStdOutDialogAction.putValue("ActionCommandKey", "showStdOutDialog");
    showStdOutDialogAction.putValue("MnemonicKey", new Integer(79));
    showStdOutDialogAction.putValue("Name", Messages.getString("Text_Output"));
    showStdOutDialogAction.putValue("ShortDescription", Messages.getString("Show_text_output_window"));
    
    applicationActions.add(showStdOutDialogAction);
    

    showStdErrDialogAction.putValue("ActionCommandKey", "showStdErrDialog");
    showStdErrDialogAction.putValue("MnemonicKey", new Integer(69));
    showStdErrDialogAction.putValue("Name", Messages.getString("Error_Console"));
    showStdErrDialogAction.putValue("ShortDescription", Messages.getString("Show_error_console_window"));
    
    applicationActions.add(showStdErrDialogAction);
    
    showPrintDialogAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(80, 2));
    showPrintDialogAction.putValue("ActionCommandKey", "showPrintDialog");
    
    showPrintDialogAction.putValue("MnemonicKey", new Integer(80));
    showPrintDialogAction.putValue("Name", Messages.getString("Export_Code_For_Printing___"));
    showPrintDialogAction.putValue("ShortDescription", Messages.getString("Export_user_defined_methods_and_questions_for_printing"));
    
    applicationActions.add(showPrintDialogAction);
    
    pauseWorldAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(19, 0));
    pauseWorldAction.putValue("ActionCommandKey", "pauseWorld");
    
    pauseWorldAction.putValue("Name", Messages.getString("Pause"));
    pauseWorldAction.putValue("ShortDescription", Messages.getString("Pause_the_running_of_the_world__Pause_Break_"));
    
    renderActions.add(pauseWorldAction);
    
    resumeWorldAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(33, 0));
    resumeWorldAction.putValue("ActionCommandKey", "resumeWorld");
    
    resumeWorldAction.putValue("Name", "  " + Messages.getString("Play__"));
    resumeWorldAction.putValue("ShortDescription", Messages.getString("Resume_the_running_of_the_world__Page_Up_"));
    
    renderActions.add(resumeWorldAction);
    
    restartWorldAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(8, 0));
    restartWorldAction.putValue("ActionCommandKey", "restartWorld");
    
    restartWorldAction.putValue("Name", Messages.getString("Restart"));
    restartWorldAction.putValue("ShortDescription", Messages.getString("Restart_the_world__Backspace_"));
    
    renderActions.add(restartWorldAction);
    
    restartStopWorldAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(8, 0));
    restartStopWorldAction.putValue("ActionCommandKey", "restartWorld");
    
    restartStopWorldAction.putValue("Name", Messages.getString("Restart"));
    restartStopWorldAction.putValue("ShortDescription", Messages.getString("Restart_the_world__Backspace_"));
    
    renderActions.add(restartStopWorldAction);
    

    stopWorldAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(27, 0));
    stopWorldAction.putValue("ActionCommandKey", "stopWorld");
    
    stopWorldAction.putValue("Name", Messages.getString("Stop"));
    stopWorldAction.putValue("ShortDescription", Messages.getString("Stop_the_running_of_the_world__Esc_"));
    
    renderActions.add(stopWorldAction);
    
    takePictureAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(71, 2));
    takePictureAction.putValue("ActionCommandKey", "takePicture");
    
    takePictureAction.putValue("Name", Messages.getString("Take_Picture"));
    takePictureAction.putValue("ShortDescription", Messages.getString("Take_a_screenshot_of_the_current_scene__Ctrl_G_"));
    
    renderActions.add(takePictureAction);
    

    logInstructorIntervention.putValue("ActionCommandKey", "logInstructor");
    logInstructorIntervention.putValue("MnemonicKey", new Integer(76));
    logInstructorIntervention.putValue("Name", Messages.getString("Log_Instructor_Actions"));
    logInstructorIntervention.putValue("ShortDescription", Messages.getString("Add_record_of_instructor_actions_to_log"));
    logInstructorIntervention.putValue("SmallIcon", AuthoringToolResources.getIconForString("log"));
    applicationActions.add(logInstructorIntervention);
    

    licenseAction.putValue("ActionCommandKey", "license");
    licenseAction.putValue("Name", Messages.getString("View_License"));
    licenseAction.putValue("ShortDescription", Messages.getString("View_License"));
    applicationActions.add(licenseAction);
  }
  


  private void keyInit()
  {
    for (Iterator iter = applicationActions.iterator(); iter.hasNext();) {
      Action action = (Action)iter.next();
      try
      {
        KeyStroke keyStroke = (KeyStroke)action.getValue("AcceleratorKey");
        commandKey = (String)action.getValue("ActionCommandKey");
      } catch (ClassCastException e) { String commandKey;
        continue; }
      String commandKey;
      KeyStroke keyStroke;
      if ((keyStroke != null) && (commandKey != null)) {
        jAliceFrame.registerKeyboardAction(action, commandKey, keyStroke, 2);
      }
    }
  }
}
