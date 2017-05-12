package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.alice.authoringtool.dialog.NewVariableContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.StdErrOutContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule;
import edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule;
import edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.core.clock.DefaultClock;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.DoInOrder;
import edu.cmu.cs.stage3.alice.core.response.DoTogether;
import edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation;
import edu.cmu.cs.stage3.alice.core.response.PropertyAnimation;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.Wait;
import edu.cmu.cs.stage3.alice.gallery.batch.BatchSaveWithThumbnails;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.w3c.dom.Document;

public class AuthoringTool implements java.awt.datatransfer.ClipboardOwner, edu.cmu.cs.stage3.caitlin.stencilhelp.application.StencilApplication
{
  public static final String CHARACTER_EXTENSION = "a2c";
  public static final String WORLD_EXTENSION = "a2w";
  private static org.python.core.PyFile pyStdOut;
  private static org.python.core.PyFile pyStdErr;
  private edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory renderTargetFactory;
  private World world;
  private File defaultWorld;
  private JAliceFrame jAliceFrame;
  private EditorManager editorManager;
  private edu.cmu.cs.stage3.alice.authoringtool.util.DefaultScheduler scheduler;
  private edu.cmu.cs.stage3.alice.authoringtool.util.OneShotScheduler oneShotScheduler;
  private Runnable worldScheduleRunnable;
  private DefaultClock worldClock;
  private MainUndoRedoStack undoRedoStack;
  private Actions actions;
  private Importing importing;
  public edu.cmu.cs.stage3.alice.authoringtool.dialog.OutputComponent stdOutOutputComponent;
  public edu.cmu.cs.stage3.alice.authoringtool.dialog.OutputComponent stdErrOutputComponent;
  private edu.cmu.cs.stage3.alice.authoringtool.util.WatcherPanel watcherPanel;
  private JFileChooser importFileChooser;
  private JFileChooser addCharacterFileChooser;
  private JFileChooser browseFileChooser;
  private JFileChooser saveWorldFileChooser;
  private JFileChooser saveCharacterFileDialog;
  private edu.cmu.cs.stage3.alice.authoringtool.dialog.LoadElementProgressPane worldLoadProgressPane;
  private edu.cmu.cs.stage3.alice.authoringtool.dialog.StoreElementProgressPane worldStoreProgressPane;
  private edu.cmu.cs.stage3.alice.authoringtool.dialog.LoadElementProgressPane characterLoadProgressPane;
  private edu.cmu.cs.stage3.alice.authoringtool.dialog.StoreElementProgressPane characterStoreProgressPane;
  private edu.cmu.cs.stage3.alice.authoringtool.dialog.PreferencesContentPane preferencesContentPane;
  private edu.cmu.cs.stage3.alice.authoringtool.dialog.AboutContentPane aboutContentPane;
  private edu.cmu.cs.stage3.alice.authoringtool.dialog.LicenseContentPane licenseContentPane;
  private edu.cmu.cs.stage3.alice.authoringtool.dialog.WorldInfoContentPane worldInfoContentPane;
  public StdErrOutContentPane stdErrContentPane;
  public StdErrOutContentPane stdOutContentPane;
  private edu.cmu.cs.stage3.alice.authoringtool.dialog.ExportCodeForPrintingContentPane exportCodeForPrintingContentPane;
  private edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane startUpContentPane;
  private edu.cmu.cs.stage3.alice.authoringtool.dialog.SaveForWebContentPane saveForWebContentPane;
  private NewVariableContentPane newVariableContentPane;
  private edu.cmu.cs.stage3.alice.authoringtool.dialog.RenderContentPane renderContentPane;
  private edu.cmu.cs.stage3.alice.authoringtool.dialog.CaptureContentPane captureContentPane;
  private JPanel renderPanel;
  private javax.swing.filechooser.FileFilter worldFileFilter;
  private javax.swing.filechooser.FileFilter characterFileFilter;
  private File currentWorldLocation;
  private boolean worldHasBeenModified = false;
  private long lastSaveTime;
  private movieMaker.SoundStorage soundStorage = null;
  
  private File worldDirectory;
  
  private HashMap extensionStringsToFileFilterMap;
  private Configuration authoringToolConfig;
  private edu.cmu.cs.stage3.alice.core.RenderTarget renderTarget;
  private edu.cmu.cs.stage3.alice.scripting.ScriptingFactory scriptingFactory;
  private java.awt.event.WindowListener jAliceFrameWindowListener;
  private boolean saveTabsEnabled = false;
  
  private long worldLoadedTime;
  
  private edu.cmu.cs.stage3.alice.authoringtool.util.RectangleAnimator rectangleAnimator;
  
  private boolean stdOutToConsole;
  private boolean stdErrToConsole;
  public int numEncoded = 0;
  
  private static ArrayList sound = new ArrayList();
  
  private edu.cmu.cs.stage3.alice.core.util.WorldListener userDefinedParameterListener = new edu.cmu.cs.stage3.alice.core.util.WorldListener() {
    private Object m_previousPropertyValue = null;
    
    private CallToUserDefinedResponse[] getCallsTo(final UserDefinedResponse userDefined) { Vector v = new Vector();
      getWorld().internalSearch(new edu.cmu.cs.stage3.util.Criterion() {
        public boolean accept(Object o) {
          if ((o instanceof CallToUserDefinedResponse)) {
            CallToUserDefinedResponse call = (CallToUserDefinedResponse)o;
            if (userDefinedResponse.getUserDefinedResponseValue() == userDefined) {
              return true;
            }
          }
          return false;
        }
      }, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, v);
      CallToUserDefinedResponse[] calls = new CallToUserDefinedResponse[v.size()];
      v.copyInto(calls);
      return calls;
    }
    
    private CallToUserDefinedQuestion[] getCallsTo(final edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion userDefined) { Vector v = new Vector();
      getWorld().internalSearch(new edu.cmu.cs.stage3.util.Criterion() {
        public boolean accept(Object o) {
          if ((o instanceof CallToUserDefinedQuestion)) {
            CallToUserDefinedQuestion call = (CallToUserDefinedQuestion)o;
            if (userDefinedQuestion.getUserDefinedQuestionValue() == userDefined) {
              return true;
            }
          }
          return false;
        }
      }, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, v);
      CallToUserDefinedQuestion[] calls = new CallToUserDefinedQuestion[v.size()];
      v.copyInto(calls);
      return calls;
    }
    

    protected void handleChildrenChanging(edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e) {}
    
    protected void handleChildrenChanged(edu.cmu.cs.stage3.alice.core.event.ChildrenEvent e) {}
    
    protected void handlePropertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent e) { m_previousPropertyValue = e.getProperty().get(); }
    
    protected void handlePropertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent e) {
      Property property = e.getProperty();
      edu.cmu.cs.stage3.alice.core.Element owner = property.getOwner();
      if ((owner instanceof Variable)) {
        Variable variable = (Variable)owner;
        if (property.getName().equals("name")) {
          edu.cmu.cs.stage3.alice.core.Element parent = variable.getParent();
          if ((parent instanceof UserDefinedResponse)) {
            UserDefinedResponse userDefined = (UserDefinedResponse)parent;
            CallToUserDefinedResponse[] calls = getCallsTo(userDefined);
            for (int i = 0; i < calls.length; i++) {
              for (int j = 0; j < requiredActualParameters.size(); j++) {
                Variable actualParameterJ = (Variable)requiredActualParameters.get(j);
                String nameJ = name.getStringValue();
                if ((nameJ != null) && (nameJ.equals(m_previousPropertyValue))) {
                  name.set(e.getValue());
                }
              }
            }
          } else if ((parent instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)) {
            edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion userDefined = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)parent;
            CallToUserDefinedQuestion[] calls = getCallsTo(userDefined);
            for (int i = 0; i < calls.length; i++) {
              for (int j = 0; j < requiredActualParameters.size(); j++) {
                Variable actualParameterJ = (Variable)requiredActualParameters.get(j);
                String nameJ = name.getStringValue();
                if ((nameJ != null) && (nameJ.equals(m_previousPropertyValue)))
                  name.set(e.getValue());
              }
            }
          }
        }
      }
    }
    
    protected void handleObjectArrayPropertyChanging(ObjectArrayPropertyEvent e) {}
    
    protected void handleObjectArrayPropertyChanged(ObjectArrayPropertyEvent e) {
      ObjectArrayProperty oap = e.getObjectArrayProperty();
      edu.cmu.cs.stage3.alice.core.Element owner = oap.getOwner();
      if ((owner instanceof UserDefinedResponse)) {
        UserDefinedResponse userDefined = (UserDefinedResponse)owner;
        if (oap.getName().equals("requiredFormalParameters")) {
          Object item = e.getItem();
          if ((item instanceof Variable)) {
            Variable formalParameter = (Variable)item;
            String formalParameterName = name.getStringValue();
            CallToUserDefinedResponse[] calls = getCallsTo(userDefined);
            switch (e.getChangeType()) {
            case 1: 
              for (int i = 0; i < calls.length; i++) {
                Variable actualParameter = new Variable();
                name.set(name.get());
                Class cls = valueClass.getClassValue();
                valueClass.set(cls);
                value.set(AuthoringToolResources.getDefaultValueForClass(cls));
                boolean tempListening = getUndoRedoStack().getIsListening();
                getUndoRedoStack().setIsListening(false);
                calls[i].addChild(actualParameter);
                requiredActualParameters.add(actualParameter);
                getUndoRedoStack().setIsListening(tempListening);
              }
              break;
            case 3: 
              for (int i = 0; i < calls.length; i++) {
                for (int j = 0; j < requiredActualParameters.size(); j++) {
                  Variable actualParameterJ = (Variable)requiredActualParameters.get(j);
                  String nameJ = name.getStringValue();
                  if ((nameJ != null) && (nameJ.equals(formalParameterName))) {
                    boolean tempListening = getUndoRedoStack().getIsListening();
                    getUndoRedoStack().setIsListening(false);
                    actualParameterJ.removeFromParent();
                    getUndoRedoStack().setIsListening(tempListening);
                  }
                }
              }
              break;
            case 2: 
              for (int i = 0; i < calls.length; i++) {
                requiredActualParameters.shift(e.getOldIndex(), e.getNewIndex());
              }
            }
          }
        }
      }
      else if ((owner instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)) {
        edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion userDefined = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)owner;
        if (oap.getName().equals("requiredFormalParameters")) {
          Object item = e.getItem();
          if ((item instanceof Variable)) {
            Variable formalParameter = (Variable)item;
            String formalParameterName = name.getStringValue();
            CallToUserDefinedQuestion[] calls = getCallsTo(userDefined);
            switch (e.getChangeType()) {
            case 1: 
              for (int i = 0; i < calls.length; i++) {
                Variable actualParameter = new Variable();
                name.set(name.get());
                Class cls = valueClass.getClassValue();
                valueClass.set(cls);
                value.set(AuthoringToolResources.getDefaultValueForClass(cls));
                calls[i].addChild(actualParameter);
                requiredActualParameters.add(actualParameter);
              }
              break;
            case 3: 
              for (int i = 0; i < calls.length; i++) {
                for (int j = 0; j < requiredActualParameters.size(); j++) {
                  Variable actualParameterJ = (Variable)requiredActualParameters.get(j);
                  String nameJ = name.getStringValue();
                  if ((nameJ != null) && (nameJ.equals(formalParameterName))) {
                    actualParameterJ.removeFromParent();
                  }
                }
              }
              break;
            case 2: 
              for (int i = 0; i < calls.length; i++) {
                requiredActualParameters.shift(e.getOldIndex(), e.getNewIndex());
              }
            }
          }
        }
      }
    }
    
    protected boolean isPropertyListeningRequired(Property property) {
      return true;
    }
    
    protected boolean isObjectArrayPropertyListeningRequired(ObjectArrayProperty oap) { return true; }
  };
  

  private edu.cmu.cs.stage3.alice.core.Element selectedElement;
  
  private HashSet selectionListeners = new HashSet();
  

  private HashSet stateListeners = new HashSet();
  

  public boolean instructorInControl = false;
  
  public static org.python.core.PyFile getPyStdOut() { return pyStdOut; }
  
  public static org.python.core.PyFile getPyStdErr()
  {
    return pyStdErr;
  }
  
  public static AuthoringTool getHack()
  {
    return hack;
  }
  
  public AuthoringTool(File defaultWorld, File worldToLoad, boolean stdOutToConsole, boolean stdErrToConsole)
  {
    String font = "SansSerif";
    try {
      javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      

































      javax.swing.UIManager.put("Button.border", new javax.swing.plaf.BorderUIResource.CompoundBorderUIResource(new javax.swing.border.AbstractBorder()new javax.swing.plaf.basic.BasicBorders.MarginBorder
      {
        protected java.awt.Insets insets = new java.awt.Insets(3, 3, 3, 3);
        protected javax.swing.border.Border line = javax.swing.BorderFactory.createLineBorder(java.awt.Color.black, 1);
        protected javax.swing.border.Border spacer = javax.swing.BorderFactory.createEmptyBorder(2, 4, 2, 4);
        protected javax.swing.border.Border raisedBevel = javax.swing.BorderFactory.createBevelBorder(0);
        protected javax.swing.border.Border loweredBevel = javax.swing.BorderFactory.createBevelBorder(1);
        protected javax.swing.border.Border raisedBorder = javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createCompoundBorder(line, raisedBevel), spacer);
        protected javax.swing.border.Border loweredBorder = javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createCompoundBorder(line, loweredBevel), spacer);
        

        public void paintBorder(Component c, java.awt.Graphics g, int x, int y, int w, int h)
        {
          JButton button = (JButton)c;
          javax.swing.ButtonModel model = button.getModel();
          
          if (model.isEnabled()) {
            if ((model.isPressed()) && (model.isArmed())) {
              loweredBorder.paintBorder(button, g, x, y, w, h);
            } else {
              raisedBorder.paintBorder(button, g, x, y, w, h);
            }
          } else {
            raisedBorder.paintBorder(button, g, x, y, w, h);
          }
        }
        
        public java.awt.Insets getBorderInsets(Component c) {
          return insets;
        }
        
      }, new javax.swing.plaf.basic.BasicBorders.MarginBorder()));
      

      javax.swing.UIManager.put("Label.font", new java.awt.Font(font, 1, 12));
      javax.swing.UIManager.put("Label.foreground", AuthoringToolResources.getColor("mainFontColor"));
      javax.swing.UIManager.put("TabbedPane.selected", new java.awt.Color(255, 255, 255, 0));
      javax.swing.UIManager.put("TabbedPane.tabInsets", new java.awt.Insets(1, 4, 1, 3));
      if (AikMin.isWindows()) {
        javax.swing.UIManager.put("FileChooserUI", "com.sun.java.swing.plaf.windows.WindowsFileChooserUI");
      }
    }
    catch (Exception e)
    {
      showErrorDialog(Messages.getString("Error_configuring_Look_and_Feel_"), e);
    }
    
    hack = this;
    this.defaultWorld = defaultWorld;
    




    filterInit();
    configInit();
    try {
      AikMin.setFontSize(Integer.parseInt(authoringToolConfig.getValue("fontSize")));
      if (authoringToolConfig.getValue("enableHighContrastMode").equalsIgnoreCase("true")) {
        javax.swing.UIManager.put("Label.foreground", java.awt.Color.black);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    mainInit();
    this.stdOutToConsole = stdOutToConsole;
    this.stdErrToConsole = stdErrToConsole;
    initializeOutput(stdOutToConsole, stdErrToConsole);
    pyInit();
    
    dialogInit();
    
    undoRedoInit();
    miscInit();
    importInit();
    
    worldInit(null);
    stencilInit();
    edu.cmu.cs.stage3.scheduler.Scheduler s = new edu.cmu.cs.stage3.scheduler.AbstractScheduler() {
      protected void handleCaughtThowable(Runnable source, Throwable t) {
        markEachFrameRunnableForRemoval(source);
        AuthoringTool.showErrorDialog(source.toString(), t);

      }
      

    };
    s.addEachFrameRunnable(scheduler);
    s.addEachFrameRunnable(oneShotScheduler);
    
    edu.cmu.cs.stage3.scheduler.SchedulerThread schedulerThread = new edu.cmu.cs.stage3.scheduler.SchedulerThread(s);
    

    schedulerThread.start();
    
    jAliceFrame.setVisible(true);
    worldInit(worldToLoad);
    if ((worldToLoad == null) && 
      (authoringToolConfig.getValue("showStartUpDialog").equalsIgnoreCase("true"))) {
      showStartUpDialog(4);
    }
  }
  
  private void filterInit()
  {
    worldFileFilter = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter("a2w", "a2w".toUpperCase() + " " + Messages.getString("_Alice_World_Files_"));
    characterFileFilter = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter("a2c", "a2c".toUpperCase() + " " + Messages.getString("_Alice_Object_Files_"));
  }
  
  private void mainInit() {
    editorManager = new EditorManager(this);
    scheduler = new edu.cmu.cs.stage3.alice.authoringtool.util.DefaultScheduler();
    undoRedoStack = new MainUndoRedoStack(this);
    oneShotScheduler = new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotScheduler();
    jAliceFrame = new JAliceFrame(this);
    actions = new Actions(this, jAliceFrame);
    jAliceFrame.actionInit(actions);
    DialogManager.initialize(jAliceFrame);
    importing = new Importing();
    stdOutOutputComponent = new edu.cmu.cs.stage3.alice.authoringtool.dialog.OutputComponent();
    stdErrOutputComponent = new edu.cmu.cs.stage3.alice.authoringtool.dialog.OutputComponent();
    watcherPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.WatcherPanel();
  }
  
  private void pyInit() {
    scriptingFactory = new edu.cmu.cs.stage3.alice.scripting.jython.ScriptingFactory();
    scriptingFactory.setStdOut(System.out);
    scriptingFactory.setStdErr(System.err);
  }
  
  private void worldsDirectoryChanged() {
    String worldsDirPath = authoringToolConfig.getValue("directories.worldsDirectory");
    if (worldsDirPath != null) {
      File worldsDir = new File(worldsDirPath);
      if ((worldsDir != null) && (worldsDir.exists()) && (worldsDir.isDirectory())) {
        try {
          saveWorldFileChooser.setCurrentDirectory(worldsDir);
        }
        catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {}
      }
    }
  }
  



  private void importDirectoryChanged()
  {
    String importDirPath = authoringToolConfig.getValue("directories.importDirectory");
    if (importDirPath != null) {
      File importDir = new File(importDirPath);
      if ((importDir != null) && (importDir.exists()) && (importDir.isDirectory())) {
        try {
          importFileChooser.setCurrentDirectory(importDir);
        }
        catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {}
      }
    }
  }
  



  private void charactersDirectoryChanged()
  {
    String charactersDirPath = authoringToolConfig.getValue("directories.charactersDirectory");
    if (charactersDirPath != null) {
      File charactersDir = new File(charactersDirPath);
      if (charactersDir != null) {
        if (!charactersDir.exists())
        {
          charactersDir.mkdir();
        }
        try {
          addCharacterFileChooser.setCurrentDirectory(charactersDir);
          saveCharacterFileDialog.setCurrentDirectory(charactersDir);
        }
        catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {}
      }
    }
  }
  




  private void configInit()
  {
    authoringToolConfig = Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());
    Configuration.addConfigurationListener(new edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener()
    {
      public void changing(edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev) {}
      
      public void changed(edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev) {
        if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.recentWorlds.maxWorlds")) {
          jAliceFrame.updateRecentWorlds();
        } else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.numberOfClipboards")) {
          jAliceFrame.updateClipboards();
        } else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.showWorldStats")) {
          jAliceFrame.showStatusPanel(ev.getNewValue().equalsIgnoreCase("true"));
        } else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.directories.worldsDirectory")) {
          AuthoringTool.this.worldsDirectoryChanged();
        } else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.directories.importDirectory")) {
          AuthoringTool.this.importDirectoryChanged();
        } else if (ev.getKeyName().equals("edu.cmu.cs.stage3.alice.authoringtool.directories.charactersDirectory")) {
          AuthoringTool.this.charactersDirectoryChanged();
        }
      }
    });
  }
  
  private void dialogInit()
  {
    preferencesContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.PreferencesContentPane();
    preferencesContentPane.setAuthoringTool(this);
    
    captureContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.CaptureContentPane(this);
    renderContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.RenderContentPane(this);
    try
    {
      if (AikMin.isMAC()) {
        javax.swing.UIManager.setLookAndFeel("apple.laf.AquaLookAndFeel");
      }
    }
    catch (Exception localException) {}
    
    importFileChooser = new JFileChooser();
    saveWorldFileChooser = new JFileChooser() {
      public void approveSelection() {
        File desiredFile = getSelectedFile();
        if ((currentWorldLocation == null) || (currentWorldLocation.equals(desiredFile)) || (!desiredFile.exists())) {
          if (shouldAllowOverwrite(desiredFile)) {
            super.approveSelection();
          } else {
            DialogManager.showMessageDialog(Messages.getString("That_is_protected_Alice_file_and_you_can_not_overwrite_it__Please_choose_another_file_"));
          }
        } else if (desiredFile.exists()) {
          if (shouldAllowOverwrite(desiredFile)) {
            int n = DialogManager.showConfirmDialog(Messages.getString("You_are_about_to_save_over_an_existing_file__Are_you_sure_you_want_to_"), Messages.getString("Save_Over_Warning"), 0);
            if (n == 0) {
              super.approveSelection();
            }
          } else {
            DialogManager.showMessageDialog(Messages.getString("That_is_protected_Alice_file_and_you_can_not_overwrite_it__Please_choose_another_file_"));
          }
          
        }
      }
    };
    addCharacterFileChooser = new JFileChooser();
    saveCharacterFileDialog = new JFileChooser();
    
    browseFileChooser = new JFileChooser();
    


















    int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
    int x = 800 + (fontSize - 12) * 25;
    int y = 405 + (fontSize - 12);
    
    saveCharacterFileDialog.setDialogTitle(Messages.getString("Save_Object___"));
    saveCharacterFileDialog.setDialogType(1);
    saveCharacterFileDialog.setFileSelectionMode(0);
    saveCharacterFileDialog.setFileFilter(characterFileFilter);
    saveCharacterFileDialog.setPreferredSize(new Dimension(x, y));
    
    worldLoadProgressPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.LoadElementProgressPane(Messages.getString("Loading_World___"), Messages.getString("Loading__"));
    worldStoreProgressPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.StoreElementProgressPane(Messages.getString("Saving_World___"), Messages.getString("Saving__"));
    characterLoadProgressPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.LoadElementProgressPane(Messages.getString("Loading_Object___"), Messages.getString("Loading__"));
    characterStoreProgressPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.StoreElementProgressPane(Messages.getString("Saving_Object___"), Messages.getString("Saving__"));
    

    renderPanel = new JPanel();
    



    renderPanel.setLayout(new java.awt.BorderLayout());
    
    importFileChooser.setApproveButtonText(Messages.getString("Import"));
    importFileChooser.setDialogTitle(Messages.getString("Import___"));
    importFileChooser.setDialogType(0);
    importFileChooser.setPreferredSize(new Dimension(x, y));
    
    saveWorldFileChooser.setApproveButtonText(Messages.getString("Save_World_As"));
    saveWorldFileChooser.setDialogTitle(Messages.getString("Save_World_As___"));
    saveWorldFileChooser.setDialogType(1);
    saveWorldFileChooser.setFileSelectionMode(0);
    saveWorldFileChooser.setFileFilter(worldFileFilter);
    saveWorldFileChooser.setPreferredSize(new Dimension(x, y));
    
    addCharacterFileChooser.setApproveButtonText(Messages.getString("Add_Object"));
    addCharacterFileChooser.setDialogTitle(Messages.getString("Add_Object___"));
    addCharacterFileChooser.setDialogType(0);
    addCharacterFileChooser.setFileSelectionMode(0);
    addCharacterFileChooser.setFileFilter(characterFileFilter);
    addCharacterFileChooser.setPreferredSize(new Dimension(x, y));
    
    worldsDirectoryChanged();
    importDirectoryChanged();
    charactersDirectoryChanged();
    
    startUpContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane(this);
    
    worldInfoContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.WorldInfoContentPane();
    
    exportCodeForPrintingContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.ExportCodeForPrintingContentPane(this);
    


    newVariableContentPane = new NewVariableContentPane();
    
    stdErrContentPane = new StdErrOutContentPane(this, true);
    stdOutContentPane = new StdErrOutContentPane(this, false);
  }
  
  private void undoRedoInit() {
    addAuthoringToolStateListener(undoRedoStack);
    
    undoRedoStack.addUndoRedoListener(new edu.cmu.cs.stage3.alice.authoringtool.event.UndoRedoListener() {
      public void onChange() {
        int currentIndex = undoRedoStack.getCurrentUndoableRedoableIndex();
        if (currentIndex == -1) {
          actions.undoAction.setEnabled(false);
        } else {
          actions.undoAction.setEnabled(true);
        }
        
        if (currentIndex == undoRedoStack.size() - 1) {
          actions.redoAction.setEnabled(false);
        } else {
          actions.redoAction.setEnabled(true);
        }
        
        worldHasBeenModified = ((currentIndex != undoRedoStack.getUnmodifiedIndex()) || (undoRedoStack.isScriptDirty()));
        AuthoringTool.this.updateTitle();
      }
    });
  }
  
  private void miscInit() {
    extensionStringsToFileFilterMap = new HashMap();
    scheduler.addEachFrameRunnable(oneShotScheduler);
    

    jAliceFrameWindowListener = new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent e) {
        quit(false);
      }
    };
    jAliceFrame.addWindowListener(jAliceFrameWindowListener);
    
    worldClock = new DefaultClock();
    
    worldScheduleRunnable = new Runnable() {
      public void run() {
        try {
          worldClock.schedule();
        }
        catch (Throwable t)
        {
          AuthoringTool.this.stopWorldAndShowDialog(t);














        }
        














      }
      















    };
    Timer fpsTimer = new Timer(500, new java.awt.event.ActionListener() {
      DecimalFormat formater = new DecimalFormat("#0.00");
      
      public void actionPerformed(java.awt.event.ActionEvent ev) { String fps = formater.format(scheduler.getSimulationFPS()) + " " + Messages.getString("fps");
        if (authoringToolConfig.getValue("rendering.showFPS").equalsIgnoreCase("true")) {
          renderContentPane.setTitle(Messages.getString("World_Running_____") + fps);
        }
      }
    });
    fpsTimer.start();
    

    lastSaveTime = System.currentTimeMillis();
    Timer promptToSaveTimer = new Timer(60000, new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent ev) {
        boolean modalShowing = false;
        java.awt.Window[] ownedWindows = jAliceFrame.getOwnedWindows();
        for (int i = 0; i < ownedWindows.length; i++)
        {
          if (ownedWindows[i].isShowing()) {
            modalShowing = true;
            break;
          }
        }
        

        boolean skipThisWorld = false;
        if ((currentWorldLocation != null) && 
          (currentWorldLocation.getAbsolutePath().startsWith(getTutorialDirectory().getAbsolutePath()))) {
          skipThisWorld = true;
        }
        


        if (!worldHasBeenModified) {
          skipThisWorld = true;
        }
        
        if ((!modalShowing) && (!skipThisWorld)) {
          long time = System.currentTimeMillis();
          long dt = time - lastSaveTime;
          int interval = Integer.parseInt(authoringToolConfig.getValue("promptToSaveInterval"));
          long intervalMillis = interval * 60000L;
          if (dt > intervalMillis)
          {
            int result = 
              DialogManager.showOptionDialog(
              Messages.getString("You_have_not_saved_in_more_than_") + interval + " " + Messages.getString("minutes__nIt_is_recommended_that_you_save_early_and_often_to_avoid_losing_work_"), 
              Messages.getString("Save_"), 
              0, 
              2, 
              null, 
              new Object[] { Messages.getString("Save_right_now"), Messages.getString("Remind_me_later") }, 
              Messages.getString("Save_right_now"));
            if (result == 0) {
              getActions().saveWorldAction.actionPerformed(null);
            }
            lastSaveTime = System.currentTimeMillis();
          }
        }
      }
    });
    promptToSaveTimer.start();
    addAuthoringToolStateListener(new edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateAdapter() {
      public void worldLoaded(AuthoringToolStateChangedEvent ev) {
        lastSaveTime = System.currentTimeMillis();
      }
      
      public void worldSaved(AuthoringToolStateChangedEvent ev) { lastSaveTime = System.currentTimeMillis(); }
    });
    


    if ((edu.cmu.cs.stage3.awt.AWTUtilities.mouseListenersAreSupported()) || (edu.cmu.cs.stage3.awt.AWTUtilities.mouseMotionListenersAreSupported())) {
      scheduler.addEachFrameRunnable(new Runnable()
      {
        public void run() {}
      });
    }
    











    AikMin.isMAC();
    



    rectangleAnimator = new edu.cmu.cs.stage3.alice.authoringtool.util.RectangleAnimator(this);
    
    watcherPanel.setMinimumSize(new Dimension(0, 0));
    

    javax.swing.ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
    javax.swing.ToolTipManager.sharedInstance().setInitialDelay(1000);
    javax.swing.ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
  }
  



















  private static AuthoringTool hack;
  

















  public static int id = 0;
  public static int temp = -1;
  
  private void importInit() { java.util.List importers = importing.getImporters();
    edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter imageFiles = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter(Messages.getString("Image_Files"));
    extensionStringsToFileFilterMap.put("Image Files", imageFiles);
    edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter soundFiles = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter(Messages.getString("Sound_Files"));
    extensionStringsToFileFilterMap.put("Sound Files", soundFiles);
    java.util.TreeSet extensions = new java.util.TreeSet();
    Iterator jter; for (Iterator iter = importers.iterator(); iter.hasNext(); 
        

        jter.hasNext())
    {
      Importer importer = (Importer)iter.next();
      java.util.Map map = importer.getExtensionMap();
      jter = map.keySet().iterator(); continue;
      String extension = (String)jter.next();
      String description = extension + " (" + map.get(extension) + ")";
      edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter ext = new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter(extension, description);
      extensions.add(ext);
      extensionStringsToFileFilterMap.put(extension, ext);
      if ((importer instanceof edu.cmu.cs.stage3.alice.authoringtool.importers.ImageImporter)) {
        imageFiles.addExtensionFileFilter(ext);
      } else if ((importer instanceof edu.cmu.cs.stage3.alice.authoringtool.importers.MediaImporter)) {
        soundFiles.addExtensionFileFilter(ext);
      }
    }
    
    importFileChooser.addChoosableFileFilter(characterFileFilter);
    importFileChooser.addChoosableFileFilter(imageFiles);
    importFileChooser.addChoosableFileFilter(soundFiles);
    for (Iterator iter = extensions.iterator(); iter.hasNext();) {
      edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter ext = (edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter)iter.next();
      importFileChooser.addChoosableFileFilter(ext);
    }
    importFileChooser.setFileFilter(importFileChooser.getAcceptAllFileFilter());
  }
  








  private void worldInit(File worldToLoad)
  {
    if (worldToLoad != null)
    {
      String oldWorld = worldToLoad.getAbsolutePath();
      oldWorld = oldWorld.substring(0, oldWorld.lastIndexOf(".")) + ".a2w";
      File world = new File(oldWorld);
      
      if (world.exists()) {
        if (world.canRead()) {
          int retVal = loadWorld(world, false);
          if (retVal != 1) {}
        }
        else
        {
          showErrorDialog(Messages.getString("cannot_read_world__") + world, null, false);
        }
      } else {
        showErrorDialog(Messages.getString("world_doesn_t_exist__") + world, null, false);
      }
    }
    

    loadWorld(defaultWorld, false);
  }
  
  private void initializeOutput(boolean stdOutToConsole, boolean stdErrToConsole) {
    if (stdOutToConsole) {
      pyStdOut = new org.python.core.PyFile(System.out);
    } else {
      java.io.PrintStream stdOutStream = stdOutOutputComponent.getStdOutStream();
      System.setOut(stdOutStream);
      pyStdOut = new org.python.core.PyFile(stdOutStream);
    }
    if (stdErrToConsole) {
      pyStdErr = new org.python.core.PyFile(System.err);
    } else {
      java.io.PrintStream stdErrStream = stdErrOutputComponent.getStdErrStream();
      System.setErr(stdErrStream);
      pyStdErr = new org.python.core.PyFile(stdErrStream);
    }
  }
  
  public JAliceFrame getJAliceFrame() {
    return jAliceFrame;
  }
  
  public edu.cmu.cs.stage3.alice.authoringtool.util.DefaultScheduler getScheduler() {
    return scheduler;
  }
  
  public edu.cmu.cs.stage3.alice.authoringtool.util.OneShotScheduler getOneShotScheduler() {
    return oneShotScheduler;
  }
  
  public MainUndoRedoStack getUndoRedoStack() {
    return undoRedoStack;
  }
  
  public Actions getActions() {
    return actions;
  }
  
  public Configuration getConfig() {
    return authoringToolConfig;
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory getRenderTargetFactory() {
    if (renderTargetFactory == null) {
      Class rendererClass = null;
      boolean isSoftwareEmulationForced = false;
      try {
        String[] renderers = authoringToolConfig.getValueList("rendering.orderedRendererList");
        rendererClass = Class.forName(renderers[0]);
      }
      catch (Throwable localThrowable) {}
      
      try
      {
        String s = authoringToolConfig.getValue("rendering.forceSoftwareRendering");
        if (s != null) {
          isSoftwareEmulationForced = s.equals("true");
        }
      }
      catch (Throwable localThrowable1) {}
      

      String commandLineOption = System.getProperty("alice.forceSoftwareRendering");
      if ((commandLineOption != null) && (commandLineOption.equalsIgnoreCase("true"))) {
        isSoftwareEmulationForced = true;
      }
      renderTargetFactory = new edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory(rendererClass);
      renderTargetFactory.setIsSoftwareEmulationForced(isSoftwareEmulationForced);
    }
    return renderTargetFactory;
  }
  
  public Object getContext()
  {
    return null;
  }
  

  public void setContext(Object context) {}
  
  public edu.cmu.cs.stage3.alice.authoringtool.dialog.OutputComponent getStdOutOutputComponent()
  {
    return stdOutOutputComponent;
  }
  
  public edu.cmu.cs.stage3.alice.authoringtool.dialog.OutputComponent getStdErrOutputComponent() {
    return stdErrOutputComponent;
  }
  
  public boolean isStdOutToConsole() {
    return stdOutToConsole;
  }
  
  public boolean isStdErrToConsole() {
    return stdErrToConsole;
  }
  
  public edu.cmu.cs.stage3.alice.authoringtool.util.WatcherPanel getWatcherPanel() {
    return watcherPanel;
  }
  
  public EditorManager getEditorManager() {
    return editorManager;
  }
  



  public void setSelectedElement(edu.cmu.cs.stage3.alice.core.Element element)
  {
    if (element == null) {
      element = getWorld();
    }
    if (selectedElement != element) {
      selectedElement = element;
      fireElementSelected(element);
    }
  }
  
  public edu.cmu.cs.stage3.alice.core.Element getSelectedElement() {
    return selectedElement;
  }
  
  public void addElementSelectionListener(edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener listener) {
    selectionListeners.add(listener);
  }
  
  public void removeElementSelectionListener(edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener listener) {
    selectionListeners.remove(listener);
  }
  
  protected void fireElementSelected(edu.cmu.cs.stage3.alice.core.Element element) {
    for (Iterator iter = selectionListeners.iterator(); iter.hasNext();) {
      ((edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener)iter.next()).elementSelected(element);
    }
  }
  



  public void addAuthoringToolStateListener(AuthoringToolStateListener listener)
  {
    stateListeners.add(listener);
  }
  
  public void removeAuthoringToolStateListener(AuthoringToolStateListener listener) {
    stateListeners.remove(listener);
  }
  
  protected void fireStateChanging(int previousState, int currentState) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.stateChanging(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_an_authoring_tool_state_change_"), t);
      }
    }
  }
  
  protected void fireWorldLoading(World world) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(1, 1, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.worldLoading(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_load_"), t);
      }
    }
  }
  
  protected void fireWorldUnLoading(World world) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(1, 1, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.worldUnLoading(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_unload_"), t);
      }
    }
  }
  
  protected void fireWorldStarting(int previousState, int currentState, World world) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.worldStarting(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_starting_"), t);
      }
    }
  }
  
  protected void fireWorldStopping(int previousState, int currentState, World world) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.worldStopping(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_stopping_"), t);
      }
    }
  }
  
  protected void fireWorldPausing(int previousState, int currentState, World world) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.worldPausing(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_pausing_"), t);
      }
    }
  }
  
  protected void fireWorldSaving(World world) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(1, 1, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.worldSaving(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_saving_"), t);
      }
    }
  }
  
  protected void fireStateChanged(int previousState, int currentState) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.stateChanged(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_authoring_tool_state_changed_"), t);
      }
    }
  }
  
  protected void fireWorldLoaded(World world) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(1, 1, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.worldLoaded(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_loaded_"), t);
      }
    }
  }
  
  protected void fireWorldUnLoaded(World world) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(1, 1, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.worldUnLoaded(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_unloaded_"), t);
      }
    }
  }
  
  protected void fireWorldStarted(int previousState, int currentState, World world) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.worldStarted(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_started_"), t);
      }
    }
  }
  
  protected void fireWorldStopped(int previousState, int currentState, World world) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.worldStopped(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_stopped_"), t);
      }
    }
  }
  
  protected void fireWorldPaused(int previousState, int currentState, World world) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(previousState, currentState, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.worldPaused(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_paused_"), t);
      }
    }
  }
  
  protected void fireWorldSaved(World world) {
    AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(1, 1, world);
    for (Iterator iter = stateListeners.iterator(); iter.hasNext();) {
      AuthoringToolStateListener listener = (AuthoringToolStateListener)iter.next();
      try {
        listener.worldSaved(ev);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_in_listener_responding_to_world_saved_"), t);
      }
    }
  }
  



  public void editObject(Object object)
  {
    editObject(object, true);
  }
  
  public void editObject(Object object, boolean switchToNewTab) {
    Class editorClass = null;
    if (object != null) {
      editorClass = edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.getBestEditor(object.getClass());
    }
    editObject(object, editorClass, switchToNewTab);
  }
  
  public void editObject(Object object, Class editorClass, boolean switchToNewTab) {
    jAliceFrame.getTabbedEditorComponent().editObject(object, editorClass, switchToNewTab);
    saveTabs();
    if ((switchToNewTab) && (getJAliceFrame().getGuiMode() != JAliceFrame.SCENE_EDITOR_SMALL_MODE)) {
      getJAliceFrame().setGuiMode(JAliceFrame.SCENE_EDITOR_SMALL_MODE);
    }
  }
  
  public void editObject(Object object, JComponent componentToAnimateFrom) {
    if (!isObjectBeingEdited(object)) {
      animateEditOpen(componentToAnimateFrom);
    }
    editObject(object);
  }
  






  public void editObject(Object object, boolean switchToNewTab, JComponent componentToAnimateFrom)
  {
    if (!isObjectBeingEdited(object)) {
      animateEditOpen(componentToAnimateFrom);
    }
    editObject(object, switchToNewTab);
  }
  






  public void editObject(Object object, Class editorClass, boolean switchToNewTab, JComponent componentToAnimateFrom)
  {
    if (!isObjectBeingEdited(object)) {
      animateEditOpen(componentToAnimateFrom);
    }
    editObject(object, editorClass, switchToNewTab);
  }
  






  protected void animateEditOpen(JComponent componentToAnimateFrom)
  {
    Rectangle sourceBounds = componentToAnimateFrom.getBounds();
    Point sourceLocation = sourceBounds.getLocation();
    SwingUtilities.convertPointToScreen(sourceLocation, componentToAnimateFrom);
    sourceBounds.setLocation(sourceLocation);
    Rectangle targetBounds = jAliceFrame.getTabbedEditorComponent().getBounds();
    Point targetLocation = targetBounds.getLocation();
    SwingUtilities.convertPointToScreen(targetLocation, jAliceFrame.getTabbedEditorComponent());
    targetBounds.setLocation(targetLocation);
    java.awt.Color color = componentToAnimateFrom.getBackground();
    rectangleAnimator.animate(sourceBounds, targetBounds, color);
  }
  
  public Object getObjectBeingEdited() {
    return jAliceFrame.getTabbedEditorComponent().getObjectBeingEdited();
  }
  
  public Object[] getObjectsBeingEdited() {
    return jAliceFrame.getTabbedEditorComponent().getObjectsBeingEdited();
  }
  
  public boolean isObjectBeingEdited(Object object) {
    return jAliceFrame.getTabbedEditorComponent().isObjectBeingEdited(object);
  }
  



  private int showStartUpDialog(int tabID)
  {
    int retVal = askForSaveIfNecessary();
    if (retVal != 3) {
      startUpContentPane.setTabID(tabID);
      if (DialogManager.showDialog(startUpContentPane) == 0) {
        File file = startUpContentPane.getFile();
        if (startUpContentPane.isTutorial()) {
          launchTutorialFile(file);
        }
        else {
          loadWorld(file, false);
        }
        resetClipboards();
        watcherPanel.clear();
      }
      return 1;
    }
    return retVal;
  }
  
  public void resetClipboards()
  {
    try {
      int numClipboards = Integer.parseInt(authoringToolConfig.getValue("numberOfClipboards"));
      jAliceFrame.clipboardPanel.removeAll();
      for (int i = 0; i < numClipboards; i++) {
        jAliceFrame.clipboardPanel.add(new edu.cmu.cs.stage3.alice.authoringtool.util.DnDClipboard());
      }
      jAliceFrame.clipboardPanel.revalidate();
      jAliceFrame.clipboardPanel.repaint();
    } catch (NumberFormatException e) {
      showErrorDialog(Messages.getString("illegal_number_of_clipboards__") + authoringToolConfig.getValue("numberOfClipboards"), null);
    }
  }
  
  public int newWorld() {
    return showStartUpDialog(4);
  }
  
  public int openWorld() { return showStartUpDialog(1); }
  
  public int openExampleWorld() {
    return showStartUpDialog(5);
  }
  
  public int openTutorialWorld() { return showStartUpDialog(2); }
  
  public int saveWorld()
  {
    if ((currentWorldLocation != null) && (shouldAllowOverwrite(currentWorldLocation)) && (currentWorldLocation.canWrite())) {
      try {
        return saveWorldToFile(currentWorldLocation, false);
      } catch (IOException e) {
        showErrorDialog(Messages.getString("Unable_to_save_world") + ": " + currentWorldLocation.getAbsolutePath(), e);
        return 2;
      }
    }
    return saveWorldAs();
  }
  
  public int saveWorldAs()
  {
    int returnVal = DialogManager.showSaveDialog(saveWorldFileChooser);
    if (returnVal == 0) {
      File file = saveWorldFileChooser.getSelectedFile();
      




      if (!file.getName().endsWith(".a2w")) {
        file = new File(file.getParent(), file.getName() + "." + "a2w");
      }
      try {
        return saveWorldToFile(file, false);
      } catch (IOException e) {
        showErrorDialog(Messages.getString("Unable_to_save_world") + ": " + file, e);
        file.delete();
        return 2;
      } }
    if (returnVal == 0) {
      return 3;
    }
    return 2;
  }
  
  private void finalCleanUp()
  {
    try {
      Rectangle bounds = jAliceFrame.getBounds();
      authoringToolConfig.setValue("mainWindowBounds", x + ", " + y + ", " + width + ", " + height);
      Configuration.storeConfig();
      renderTargetFactory.release();
    } catch (Throwable t) {
      showErrorDialog(Messages.getString("Error_encountered_during_final_cleanup_"), t);
    }
  }
  
  public int quit(boolean condition) {
    try {
      int retVal = leaveWorld(true);
      if (retVal == 1) {
        finalCleanUp();
        File temp = new File(authoringToolConfig.getValue("directories.charactersDirectory"));
        if ((temp.exists()) && (temp.list().length <= 0)) {
          temp.delete();
        }
        File aliceHasNotExitedFile = new File(JAlice.getAliceUserDirectory(), "aliceHasNotExited.txt");
        aliceHasNotExitedFile.delete();
        java.io.BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        
        File aliceJAR = new File(JAlice.getAliceHomeDirectory().toString() + File.separator + "lib" + File.separator + "aliceupdate.jar");
        if (aliceJAR.exists()) {
          try {
            bis = new java.io.BufferedInputStream(new java.io.FileInputStream(aliceJAR));
            bos = new BufferedOutputStream(new java.io.FileOutputStream(new File(JAlice.getAliceHomeDirectory().toString() + File.separator + "lib" + File.separator + "alice.jar")));
            int i;
            while ((i = bis.read()) != -1) { int i;
              bos.write(i);
            }
          } catch (Exception e1) {
            e1.printStackTrace();
            
            if (bis != null)
              try {
                bis.close();
                aliceJAR.delete();
              } catch (IOException ioe) {
                ioe.printStackTrace();
              }
            if (bos != null) {
              try {
                bos.close();
              } catch (IOException ioe) {
                ioe.printStackTrace();
              }
            }
          }
          finally
          {
            if (bis != null)
              try {
                bis.close();
                aliceJAR.delete();
              } catch (IOException ioe) {
                ioe.printStackTrace();
              }
            if (bos != null) {
              try {
                bos.close();
              } catch (IOException ioe) {
                ioe.printStackTrace();
              }
            }
          }
        }
        if (condition) {
          try
          {
            if (AikMin.isWindows()) {
              String file = JAlice.getAliceHomeDirectory().getParent().toString() + "\\Alice.exe";
              if (new File(file).exists()) {
                Runtime.getRuntime().exec(file);
              } else {
                DialogManager.showMessageDialog("Missing Alice.exe in Alice directory. Please restart Alice manually.");
              }
              
            }
            else if (AikMin.isMAC()) {
              String decodedPath = java.net.URLDecoder.decode(JAlice.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
              decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf(".app") + 4);
              String[] params = { "open", "-n", decodedPath };
              Runtime.getRuntime().exec(params);
            }
            else
            {
              String file = JAlice.getAliceHomeDirectory().getParent().toString() + "/Required/run-alice";
              if (new File(file).exists()) {
                try {
                  Runtime.getRuntime().exec(file);
                } catch (Exception e) {
                  e.printStackTrace();
                }
              } else {
                DialogManager.showMessageDialog("Missing Alice executable in Alice directory. Please restart Alice manually.");
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        System.exit(0);
        return 1; }
      if (retVal == 3)
        return 3;
      if (retVal == 2) {
        int result = DialogManager.showConfirmDialog(Messages.getString("Alice_failed_to_correctly_save_and_or_close_the_current_world___Would_you_still_like_to_quit_"));
        if (result == 0) {
          finalCleanUp();
          System.exit(1);
        }
      }
    } catch (Throwable t) {
      try {
        int result = DialogManager.showConfirmDialog(Messages.getString("Error_encountered_while_attempting_to_close_Alice___Would_you_like_to_force_the_close_"));
        if (result == 0) {
          finalCleanUp();
          System.exit(1);
        }
      } catch (Throwable t2) {
        finalCleanUp();
        System.exit(1);
      }
    }
    return 2;
  }
  
  public int askForSaveIfNecessary() {
    if (worldHasBeenModified) {
      if ((currentWorldLocation != null) && (currentWorldLocation.getAbsolutePath().startsWith(getTutorialDirectory().getAbsolutePath())))
        return 1;
      if ((currentWorldLocation == null) || (!shouldAllowOverwrite(currentWorldLocation))) {
        String question = Messages.getString("The_world_has_not_been_saved___Would_you_like_to_save_it_");
        int n = DialogManager.showConfirmDialog(question, Messages.getString("Save_World_"), 1);
        if (n == 0) {
          int retVal = saveWorldAs();
          if (retVal == 3)
            return askForSaveIfNecessary();
          if (retVal == 2)
            return 2;
        } else {
          if (n == 1)
            return 1;
          if (n == 2)
            return 3;
          if (n == -1)
            return 3;
        }
      } else {
        String question = Messages.getString("The_world_has_been_modified___Would_you_like_to_save_it_");
        int n = DialogManager.showConfirmDialog(question, Messages.getString("Save_World_"), 1);
        if (n == 0) {
          int retVal = saveWorld();
          if (retVal == 3)
            return 3;
          if (retVal == 2)
            return 2;
        } else {
          if (n == 1)
            return 1;
          if (n == 2)
            return 3;
          if (n == -1)
            return 3;
        }
      }
      worldHasBeenModified = false;
      undoRedoStack.setUnmodified();
    }
    return 1;
  }
  







































  public int leaveWorld(boolean askForSaveIfNecessary)
  {
    try
    {
      if (askForSaveIfNecessary) {
        int retVal = askForSaveIfNecessary();
        if (retVal == 3)
          return 3;
        if (retVal == 2) {
          return 2;
        }
      }
      
      fireWorldUnLoading(world);
      
      saveTabsEnabled = false;
      undoRedoStack.clear();
      jAliceFrame.setWorld(null);
      userDefinedParameterListener.setWorld(null);
      setCurrentWorldLocation(null);
      editObject(null);
      edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.clearRecentlyUsedValues();
      if (world != null) {
        world.release();
        fireWorldUnLoaded(world);
      }
      
      world = null;
      
      return 1;
    } catch (Exception e) {
      showErrorDialog(Messages.getString("Error_encountered_while_leaving_current_world_"), e); }
    return 2;
  }
  






































































  public void waitForBackupToFinishIfNecessary(File file) {}
  





































































  public void backupWorld(final File src, final int maxBackups)
  {
    new Thread()
    {
      public void run()
      {
        File parentDir = src.getParentFile();
        String name = src.getName();
        if (name.endsWith(".a2w")) {
          name = name.substring(0, name.length() - 4);
        }
        File dstDir = new File(parentDir, Messages.getString("Backups_of_") + name);
        
        StringBuffer sb = new StringBuffer();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        sb.append(name);
        sb.append(" ");
        sb.append(Messages.getString("backed_up_on_"));
        switch (calendar.get(2)) {
        case 0: 
          sb.append(Messages.getString("Jan_"));
          break;
        case 1: 
          sb.append(Messages.getString("Feb_"));
          break;
        case 2: 
          sb.append(Messages.getString("Mar_"));
          break;
        case 3: 
          sb.append(Messages.getString("Apr_"));
          break;
        case 4: 
          sb.append(Messages.getString("May_"));
          break;
        case 5: 
          sb.append(Messages.getString("Jun_"));
          break;
        case 6: 
          sb.append(Messages.getString("Jul_"));
          break;
        case 7: 
          sb.append(Messages.getString("Aug_"));
          break;
        case 8: 
          sb.append(Messages.getString("Sep_"));
          break;
        case 9: 
          sb.append(Messages.getString("Oct_"));
          break;
        case 10: 
          sb.append(Messages.getString("Nov_"));
          break;
        case 11: 
          sb.append(Messages.getString("Dec_"));
        }
        
        sb.append(calendar.get(5));
        sb.append(" ");
        sb.append(calendar.get(1));
        sb.append(" ");
        sb.append(Messages.getString("at_"));
        sb.append(calendar.get(10));
        sb.append("h");
        sb.append(calendar.get(12));
        sb.append("m");
        sb.append(calendar.get(13));
        switch (calendar.get(9)) {
        case 0: 
          sb.append("s AM.a2w");
          break;
        case 1: 
          sb.append("s PM.a2w");
        }
        
        
        File dst = new File(dstDir, sb.toString());
        
        if (edu.cmu.cs.stage3.io.FileUtilities.isFileCopySupported())
        {
          edu.cmu.cs.stage3.io.FileUtilities.copy(src, dst, true);
          File[] siblings = dstDir.listFiles(new java.io.FilenameFilter() {
            public boolean accept(File dir, String name) {
              return name.endsWith(".a2w");
            }
          });
          if (siblings.length > maxBackups) {
            File fileToDelete = siblings[0];
            long fileToDeleteLastModified = fileToDelete.lastModified();
            for (int i = 1; i < siblings.length; i++) {
              long lastModified = siblings[i].lastModified();
              if (lastModified < fileToDeleteLastModified) {
                fileToDelete = siblings[i];
                fileToDeleteLastModified = lastModified;
              }
            }
            fileToDelete.delete();
          }
          

        }
        else
        {
          if (!dst.exists()) {
            dst.getParentFile().mkdirs();
          }
          try {
            java.io.InputStream in = new java.io.FileInputStream(src);
            java.io.OutputStream out = new java.io.FileOutputStream(dst);
            
            byte[] buffer = new byte['Ѐ'];
            
            int length;
            
            while ((length = in.read(buffer)) > 0) { int length;
              out.write(buffer, 0, length);
            }
            
            in.close();
            out.close();
          }
          catch (Exception localException) {}
          
          File[] siblings = dstDir.listFiles(new java.io.FilenameFilter() {
            public boolean accept(File dir, String name) {
              return name.endsWith(".a2w");
            }
          });
          if (siblings.length > maxBackups) {
            File fileToDelete = siblings[0];
            long fileToDeleteLastModified = fileToDelete.lastModified();
            for (int i = 1; i < siblings.length; i++) {
              long lastModified = siblings[i].lastModified();
              if (lastModified < fileToDeleteLastModified) {
                fileToDelete = siblings[i];
                fileToDeleteLastModified = lastModified;
              }
            }
            fileToDelete.delete();
          }
        }
      }
    }.start();
  }
  
  public int saveWorldToFile(File file, boolean saveForWeb)
    throws IOException
  {
    if ((file.exists()) && (!file.canWrite())) {
      DialogManager.showMessageDialog(Messages.getString("Cannot_save_world___") + file.getAbsolutePath() + " " + Messages.getString("is_read_only_"), Messages.getString("Cannot_Save_World"), 0);
      return 2;
    }
    if ((saveForWeb) && (file.exists())) {
      file.delete();
    }
    if (file.exists()) {
      worldStoreProgressPane.setIsCancelEnabled(false);
    } else {
      worldStoreProgressPane.setIsCancelEnabled(true);
      waitForBackupToFinishIfNecessary(file);
    }
    fireWorldSaving(world);
    
    worldDirectory = null;
    boolean tempListening = getUndoRedoStack().getIsListening();
    undoRedoStack.setIsListening(false);
    try
    {
      saveTabsEnabled = true;
      saveTabs();
      

      countSomething("edu.cmu.cs.stage3.alice.authoringtool.saveCount");
      

      updateWorldOpenTime();
      

      java.util.Dictionary map = new java.util.Hashtable();
      if (authoringToolConfig.getValue("saveThumbnailWithWorld").equalsIgnoreCase("true")) {
        try {
          edu.cmu.cs.stage3.alice.core.Camera[] cameras = (edu.cmu.cs.stage3.alice.core.Camera[])world.getDescendants(edu.cmu.cs.stage3.alice.core.Camera.class);
          if (cameras.length > 0) {
            edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget rt = getRenderTargetFactory().createOffscreenRenderTarget();
            rt.setSize(120, 90);
            rt.addCamera(cameras[0].getSceneGraphCamera());
            rt.clearAndRenderOffscreen();
            Image image = rt.getOffscreenImage();
            if (image != null) {
              java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
              edu.cmu.cs.stage3.image.ImageIO.store("png", baos, image);
              map.put("thumbnail.png", baos.toByteArray());
            }
            rt.release();
          }
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }
      worldStoreProgressPane.setElement(world);
      worldStoreProgressPane.setFile(file);
      worldStoreProgressPane.setFilnameToByteArrayMap(map);
      int result = DialogManager.showDialog(worldStoreProgressPane);
      if (result == 0) {
        if (worldStoreProgressPane.wasSuccessful()) {
          jAliceFrame.HACK_standDownFromRedAlert();
          worldHasBeenModified = false;
          undoRedoStack.setUnmodified();
          if ((!file.equals(defaultWorld)) && (!isTemplateWorld(file.getAbsolutePath()))) {
            setCurrentWorldLocation(file);
            
            if (file.isDirectory()) {
              worldDirectory = file;
            }
            if (!saveForWeb)
            {

              jAliceFrame.updateRecentWorlds(file.getAbsolutePath());
              int backupCount = 0;
              try {
                backupCount = Integer.parseInt(authoringToolConfig.getValue("maximumWorldBackupCount"));
              } catch (Throwable t) {
                t.printStackTrace();
              }
              if (backupCount > 0) {
                backupWorld(file, backupCount);
              }
            }
          } else {
            setCurrentWorldLocation(null);
          }
          fireWorldSaved(world);
          return 1;
        }
        return 2;
      }
      
      file.delete();
      return 3;
    }
    catch (Throwable t) {
      showErrorDialog(Messages.getString("Unable_to_store_world_to_file__") + file, t);
      
      return 2;
    } finally {
      undoRedoStack.setIsListening(tempListening);
    }
  }
  
  public void saveForWeb() {
    if (DialogManager.showDialog(saveForWebContentPane) == 0) {
      File directory = saveForWebContentPane.getExportDirectory();
      if (!directory.exists()) {
        directory.mkdir();
      }
      String fileName = saveForWebContentPane.getExportFileName();
      File file = new File(directory, fileName);
      int width = saveForWebContentPane.getExportWidth();
      int height = saveForWebContentPane.getExportHeight();
      String authorName = saveForWebContentPane.getExportAuthorName();
      try
      {
        String htmlCode = null;
        if (saveForWebContentPane.isCodeToBeExported()) {
          StringBuffer buffer = new StringBuffer();
          exportCodeForPrintingContentPane.initialize(authorName);
          exportCodeForPrintingContentPane.getHTML(buffer, file, false, true, null);
          htmlCode = buffer.toString();
        }
        saveWorldForWeb(file, width, height, authorName, htmlCode);
      } catch (Throwable t) {
        showErrorDialog(Messages.getString("Error_saving_for_the_web_"), t);
      }
    }
  }
  
  public int saveWorldForWeb(File htmlFile, int width, int height, String authorName, String code) throws IOException {
    String baseName = htmlFile.getName();
    int dotIndex = htmlFile.getName().lastIndexOf(".");
    if (dotIndex > 0) {
      baseName = htmlFile.getName().substring(0, dotIndex);
    }
    
    File worldFile = new File(htmlFile.getParentFile(), baseName + "." + "a2w");
    
    if ((htmlFile.exists()) && (!htmlFile.canWrite())) {
      DialogManager.showMessageDialog(Messages.getString("Cannot_save_web_page___") + htmlFile.getAbsolutePath() + " " + Messages.getString("is_read_only_"), Messages.getString("Cannot_Save"), 0);
      return 2;
    }
    if ((worldFile.exists()) && (!worldFile.canWrite())) {
      DialogManager.showMessageDialog(Messages.getString("Cannot_save_world___") + worldFile.getAbsolutePath() + " " + Messages.getString("is_read_only_"), Messages.getString("Cannot_Save"), 0);
      return 2;
    }
    if (authorName != null) {
      authorName = "<h2>" + Messages.getString("Created_by_") + authorName + "</h2>\n";
    } else {
      authorName = " ";
    }
    if (code == null) {
      code = " ";
    }
    HashMap replacements = new HashMap();
    replacements.put("__worldname__", baseName);
    replacements.put("__code__", code);
    replacements.put("__authorname__", authorName);
    replacements.put("__worldfile__", worldFile.getName());
    replacements.put("__width__", Integer.toString(width));
    replacements.put("__height__", Integer.toString(height));
    


    File templateFile = new File(JAlice.getAliceHomeDirectory(), "etc/appletTemplate.html");
    if (!templateFile.exists()) {
      templateFile.createNewFile();
    }
    java.io.BufferedReader templateReader = new java.io.BufferedReader(new java.io.FileReader(templateFile));
    java.io.PrintWriter webPageWriter = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(htmlFile)));
    
    String line = templateReader.readLine();
    while (line != null) { String from;
      for (Iterator iter = replacements.keySet().iterator(); iter.hasNext(); 
          

          line.indexOf(from) > 0)
      {
        from = (String)iter.next();
        String to = (String)replacements.get(from);
        continue;
        line = line.substring(0, line.indexOf(from)) + to + line.substring(line.indexOf(from) + from.length());
      }
      
      webPageWriter.println(line);
      line = templateReader.readLine();
    }
    templateReader.close();
    webPageWriter.flush();
    webPageWriter.close();
    

    int saveWorldResult = saveWorldToFile(worldFile, true);
    if (saveWorldResult != 1) {
      return saveWorldResult;
    }
    

    File appletSourceFile = new File(JAlice.getAliceHomeDirectory(), "etc/aliceapplet.jar");
    File appletDestinationFile = new File(htmlFile.getParentFile(), "aliceapplet.jar");
    AuthoringToolResources.copyFile(appletSourceFile, appletDestinationFile);
    
    return 1;
  }
  

  public int loadWorld(String filename, boolean askForSaveIfNecessary) { return loadWorld(new File(filename), askForSaveIfNecessary); }
  
  public int loadWorld(File file, boolean askForSaveIfNecessary) {
    int result;
    int result;
    if (file.isFile()) {
      result = loadWorld(new edu.cmu.cs.stage3.io.ZipFileTreeLoader(), file, askForSaveIfNecessary); } else { int result;
      if (file.isDirectory()) {
        result = loadWorld(new edu.cmu.cs.stage3.io.FileSystemTreeLoader(), file, askForSaveIfNecessary);
      } else {
        showErrorDialog(Messages.getString("The_file_or_directory_is_not_valid__") + file, null);
        result = 2;
      } }
    return result;
  }
  
  public boolean isTemplateWorld(String filename) {
    return filename.startsWith(getTemplateWorldsDirectory().getAbsolutePath());
  }
  
  public int loadWorld(edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, Object path, boolean askForSaveIfNecessary) {
    if (askForSaveIfNecessary) {
      int retVal = askForSaveIfNecessary();
      if (retVal == 3)
        return 3;
      if (retVal == 2) {
        result = DialogManager.showConfirmDialog(Messages.getString("Alice_failed_to_correctly_save_the_current_world___Would_you_still_like_to_load_a_new_world_"));
        if (result != 0) {
          return 3;
        }
      }
    }
    
    fireWorldLoading(null);
    
    worldDirectory = null;
    
    World tempWorld = null;
    try {
      loader.open(path);
      try {
        if (path.equals(defaultWorld)) {
          tempWorld = (World)edu.cmu.cs.stage3.alice.core.Element.load(loader, null, null);
        } else {
          worldLoadProgressPane.setLoader(loader);
          worldLoadProgressPane.setExternalRoot(null);
          DialogManager.showDialog(worldLoadProgressPane);
          tempWorld = (World)worldLoadProgressPane.getLoadedElement();
        }
      } finally {
        loader.close();
      }
    } catch (Throwable t) {
      showErrorDialog(Messages.getString("Unable_to_load_world__") + path, t);
    }
    



























    if (tempWorld != null) {
      final java.awt.Cursor prevCursor = getJAliceFrame().getCursor();
      getJAliceFrame().setCursor(java.awt.Cursor.getPredefinedCursor(3));
      try {
        if (world != null) {
          for (int i = 0; i < world.getChildCount(); i++) {
            world.removeChild(world.getChildAt(0));
          }
          world.release();
          world = null;
          System.gc();
        }
        world = tempWorld;
        worldClock.setWorld(world);
        world.setClock(worldClock);
        world.setScriptingFactory(scriptingFactory);
        worldLoadedTime = System.currentTimeMillis();
        jAliceFrame.setWorld(world);
        userDefinedParameterListener.setWorld(world);
        world.setRenderTargetFactory(getRenderTargetFactory());
        

        edu.cmu.cs.stage3.alice.core.Element[] elements = world.getDescendants(edu.cmu.cs.stage3.alice.core.RenderTarget.class);
        if (elements.length > 0) {
          renderPanel.removeAll();
          renderTarget = ((edu.cmu.cs.stage3.alice.core.RenderTarget)elements[0]);
          renderPanel.add(renderTarget.getAWTComponent(), "Center");
          renderPanel.revalidate();
          renderPanel.repaint();
        }
        
        setSelectedElement(world);
        
        loadTabs();
        if (!world.responses.isEmpty()) {
          editObject(world.responses.get(0), true);
        }
        
        if ((!path.equals(defaultWorld)) && ((path instanceof File)) && (!isTemplateWorld(((File)path).getAbsolutePath()))) {
          setCurrentWorldLocation((File)path);
          
          if (((File)path).isDirectory()) {
            worldDirectory = ((File)path);
          }
          jAliceFrame.updateRecentWorlds(((File)path).getAbsolutePath());
        } else {
          setCurrentWorldLocation(null);
        }
        
        undoRedoStack.setUnmodified();
        fireWorldLoaded(world);
      } finally {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            getJAliceFrame().setCursor(prevCursor);
          }
        });
      }
      return 1;
    }
    return 2;
  }
  
  protected boolean shouldAllowOverwrite(File file)
  {
    if (file != null) {
      if (file.getAbsolutePath().startsWith(getTutorialDirectory().getAbsolutePath()))
        return false;
      if (file.getAbsolutePath().startsWith(getExampleWorldsDirectory().getAbsolutePath()))
        return false;
      if (isTemplateWorld(file.getAbsolutePath())) {
        return false;
      }
    }
    

    return true;
  }
  









  public edu.cmu.cs.stage3.alice.core.Element loadAndAddCharacter()
  {
    edu.cmu.cs.stage3.alice.core.Element character = null;
    
    addCharacterFileChooser.rescanCurrentDirectory();
    int returnVal = DialogManager.showDialog(addCharacterFileChooser, null);
    
    if (returnVal == 0) {
      File file = addCharacterFileChooser.getSelectedFile();
      character = loadAndAddCharacter(file);
    }
    
    return character;
  }
  
  public int add3DText() {
    edu.cmu.cs.stage3.alice.authoringtool.dialog.Add3DTextPanel add3DTextPanel = new edu.cmu.cs.stage3.alice.authoringtool.dialog.Add3DTextPanel();
    

    if (DialogManager.showConfirmDialog(add3DTextPanel, Messages.getString("Add_3D_Text"), 2, -1) == 0) {
      edu.cmu.cs.stage3.alice.core.Text3D text3D = add3DTextPanel.createText3D();
      if (text3D != null) {
        undoRedoStack.startCompound();
        
        name.set(AuthoringToolResources.getNameForNewChild(name.getStringValue(), world));
        
        world.addChild(text3D);
        world.sandboxes.add(text3D);
        
        if ((getCurrentCamera() instanceof SymmetricPerspectiveCamera)) {
          animateAddModel(text3D, world, (SymmetricPerspectiveCamera)getCurrentCamera());
        } else {
          vehicle.set(world);
        }
        
        undoRedoStack.stopCompound();
        
        return 1;
      }
      return 3;
    }
    
    return 3;
  }
  















  public int exportMovie()
  {
    if ((currentWorldLocation == null) && 
      (saveWorldAs() != 1))
      return 3;
    String directory = currentWorldLocation.getParent();
    directory = directory.replace('\\', '/');
    File dir = new File(directory + "/frames");
    if ((!dir.exists()) && (!dir.mkdir())) {
      showErrorDialog("Error_creating_temporary_folder_", 
        "Cannot_create_the_frames_folder. You don't have permission. Please save your world in a different location.");
      return 3;
    }
    
    soundStorage = new movieMaker.SoundStorage();
    playWhileEncoding(directory);
    soundStorage = null;
    if (authoringToolConfig.getValue("rendering.deleteFiles").equalsIgnoreCase("true")) {
      captureContentPane.removeFiles(directory + "/frames/");
      dir.delete();
      if (dir.exists()) {
        showErrorDialog(Messages.getString("Error_removing_temporary_folder_"), 
          Messages.getString("Cannot_delete_the_frames_folder___nOne_or_more_files_in_the_folder_is_being_used_") + "\n" + 
          Messages.getString("Try_deleting_the_") + dir + " " + Messages.getString("folder_manually_"));
      }
    }
    return 1;
  }
  
  public edu.cmu.cs.stage3.alice.core.Element loadAndAddCharacter(URL url) {
    return loadAndAddCharacter(new edu.cmu.cs.stage3.io.ZipTreeLoader(), url, null);
  }
  
  public edu.cmu.cs.stage3.alice.core.Element loadAndAddCharacter(File file) {
    edu.cmu.cs.stage3.alice.core.Element character = null;
    if (file.isFile()) {
      character = loadAndAddCharacter(new edu.cmu.cs.stage3.io.ZipFileTreeLoader(), file, null);
    } else if (file.isDirectory()) {
      character = loadAndAddCharacter(new edu.cmu.cs.stage3.io.FileSystemTreeLoader(), file, null);
    } else {
      showErrorDialog(Messages.getString("The_file_or_directory_is_not_valid__") + file, null);
    }
    return character;
  }
  
  public edu.cmu.cs.stage3.alice.core.Element loadAndAddCharacter(URL url, Matrix44 targetTransformation) {
    return loadAndAddCharacter(new edu.cmu.cs.stage3.io.ZipTreeLoader(), url, targetTransformation);
  }
  
  public edu.cmu.cs.stage3.alice.core.Element loadAndAddCharacter(File file, Matrix44 targetTransformation) {
    edu.cmu.cs.stage3.alice.core.Element character = null;
    if (file.isFile()) {
      character = loadAndAddCharacter(new edu.cmu.cs.stage3.io.ZipFileTreeLoader(), file, targetTransformation);
    } else if (file.isDirectory()) {
      character = loadAndAddCharacter(new edu.cmu.cs.stage3.io.FileSystemTreeLoader(), file, targetTransformation);
    } else {
      showErrorDialog(Messages.getString("The_file_or_directory_is_not_valid__") + file, null);
    }
    return character;
  }
  
  public edu.cmu.cs.stage3.alice.core.Element loadAndAddCharacter(edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, Object pathname, Matrix44 targetTransformation) {
    undoRedoStack.startCompound();
    
    edu.cmu.cs.stage3.alice.core.Element character = null;
    try
    {
      loader.open(pathname);
      try {
        characterLoadProgressPane.setLoader(loader);
        characterLoadProgressPane.setExternalRoot(world);
        DialogManager.showDialog(characterLoadProgressPane);
        character = characterLoadProgressPane.getLoadedElement();
      } finally {
        loader.close();
      }
      if (character != null) {
        addCharacter(character, targetTransformation);
      }
    } catch (java.util.zip.ZipException e) {
      showErrorDialog(Messages.getString("File_is_not_a_valid_") + "a2c" + ": " + pathname, e, false);
    } catch (Exception e) {
      showErrorDialog(Messages.getString("Unable_to_load_object__") + pathname, e);
    } finally {
      undoRedoStack.stopCompound();
    }
    return character;
  }
  
  public void addCharacter(edu.cmu.cs.stage3.alice.core.Element element, Matrix44 targetTransformation, edu.cmu.cs.stage3.alice.core.ReferenceFrame asSeenBy) {
    if (element != null) {
      name.set(AuthoringToolResources.getNameForNewChild(name.getStringValue(), world));
      
      world.addChild(element);
      world.sandboxes.add(element);
      
      if ((element instanceof Transformable)) {
        int animateStyle = 0;
        Transformable model = (Transformable)element;
        if (targetTransformation != null) {
          try {
            boolean tempListening = getUndoRedoStack().getIsListening();
            getUndoRedoStack().setIsListening(false);
            vehicle.set(world);
            edu.cmu.cs.stage3.math.Box boundingBox = model.getBoundingBox();
            
            model.setAbsoluteTransformationRightNow(targetTransformation);
            model.moveRightNow(edu.cmu.cs.stage3.math.MathUtilities.negate(boundingBox.getCenterOfBottomFace()));
            
            animateStyle = 2;
            getUndoRedoStack().setIsListening(tempListening);
          } catch (Exception e) {
            animateStyle = 1;
          }
        } else {
          animateStyle = 1;
        }
        
        promptForVisualizationInfo(element);
        if (animateStyle > 0) {
          if ((animateStyle == 1) && ((getCurrentCamera() instanceof SymmetricPerspectiveCamera))) {
            animateAddModel(model, world, (SymmetricPerspectiveCamera)getCurrentCamera());
          } else if ((animateStyle == 2) || (!(getCurrentCamera() instanceof SymmetricPerspectiveCamera))) {
            animateAddModel(model, world, null);
          }
        }
      }
    } else {
      showErrorDialog(Messages.getString("null_Element_encountered"), null);
    }
  }
  
  public void addCharacter(edu.cmu.cs.stage3.alice.core.Element element, Matrix44 targetTransformation) {
    addCharacter(element, targetTransformation, null);
  }
  
  public void addCharacter(edu.cmu.cs.stage3.alice.core.Element element) {
    addCharacter(element, null, null);
  }
  
  public void promptForVisualizationInfo(edu.cmu.cs.stage3.alice.core.Element element) {
    if ((element instanceof edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization)) {
      String typeString = Messages.getString("array");
      if ((element instanceof edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization)) {
        typeString = Messages.getString("list");
      } else if ((element instanceof edu.cmu.cs.stage3.alice.core.visualization.ArrayOfModelsVisualization)) {
        typeString = Messages.getString("array");
      }
      edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization visualization = (edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization)element;
      
      edu.cmu.cs.stage3.alice.authoringtool.util.CollectionEditorPanel collectionEditorPanel = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getCollectionEditorPanel();
      collectionEditorPanel.setCollection(visualization.getItemsCollection());
      DialogManager.showMessageDialog(collectionEditorPanel, Messages.getString("Initialize_") + typeString, -1);
    }
  }
  
  private int[] makePixmap(Image img) {
    int w = img.getWidth(null);
    int h = img.getHeight(null);
    int[] pixels = new int[w * h];
    java.awt.image.PixelGrabber pg = new java.awt.image.PixelGrabber(img, 0, 0, w, h, pixels, 0, w);
    try {
      pg.grabPixels();
    } catch (InterruptedException e) {
      return null;
    }
    if ((pg.getStatus() & 0x80) != 0) {
      return null;
    }
    
    return pixels;
  }
  
  private Document createCharacterXML(Transformable model) {
    DecimalFormat numberFormatter = new DecimalFormat("#0.##");
    javax.xml.parsers.DocumentBuilderFactory factory = null;
    javax.xml.parsers.DocumentBuilder builder = null;
    Document xmlDocument = null;
    try {
      factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
      builder = factory.newDocumentBuilder();
      xmlDocument = builder.newDocument();
    } catch (javax.xml.parsers.ParserConfigurationException pce) {
      pce.printStackTrace();
      return null;
    }
    org.w3c.dom.Element xmlModel = null;
    
    edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter itaCounter = new edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter();
    edu.cmu.cs.stage3.alice.core.util.TextureMapCounter textureMapCounter = new edu.cmu.cs.stage3.alice.core.util.TextureMapCounter();
    
    model.visit(itaCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
    model.visit(textureMapCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
    
    xmlModel = xmlDocument.createElement("model");
    org.w3c.dom.Element xmlElement = xmlDocument.createElement("name");
    xmlElement.appendChild(xmlDocument.createTextNode(name.getStringValue()));
    xmlModel.appendChild(xmlElement);
    if ((data.get("modeled by") != null) && (!data.get("modeled by").equals(""))) {
      xmlElement = xmlDocument.createElement("modeledby");
      xmlElement.appendChild(xmlDocument.createTextNode(data.get("modeled by").toString()));
      xmlModel.appendChild(xmlElement);
    }
    if ((data.get("painted by") != null) && (!data.get("painted by").equals(""))) {
      xmlElement = xmlDocument.createElement("paintedby");
      xmlElement.appendChild(xmlDocument.createTextNode(data.get("painted by").toString()));
      xmlModel.appendChild(xmlElement);
    }
    if ((data.get("programmed by") != null) && (!data.get("programmed by").equals(""))) {
      xmlElement = xmlDocument.createElement("programmedby");
      xmlElement.appendChild(xmlDocument.createTextNode(data.get("programmed by").toString()));
      xmlModel.appendChild(xmlElement);
    }
    xmlElement = xmlDocument.createElement("parts");
    xmlElement.appendChild(xmlDocument.createTextNode(String.valueOf(itaCounter.getIndexedTriangleArrayCount())));
    xmlModel.appendChild(xmlElement);
    xmlElement = xmlDocument.createElement("physicalsize");
    xmlElement.appendChild(xmlDocument.createTextNode(numberFormatter.format(getSizex) + "m x " + numberFormatter.format(getSizey) + "m x " + numberFormatter.format(getSizez) + "m"));
    xmlModel.appendChild(xmlElement);
    
    org.w3c.dom.Element xmlGroup = xmlDocument.createElement("methods");
    edu.cmu.cs.stage3.alice.core.Element[] listElements = responses.getElementArrayValue();
    for (int i = 0; i < listElements.length; i++) {
      xmlElement = xmlDocument.createElement("method");
      xmlElement.appendChild(xmlDocument.createTextNode(name.getStringValue()));
      xmlGroup.appendChild(xmlElement);
    }
    xmlModel.appendChild(xmlGroup);
    
    xmlGroup = xmlDocument.createElement("questions");
    listElements = questions.getElementArrayValue();
    for (int i = 0; i < listElements.length; i++) {
      xmlElement = xmlDocument.createElement("question");
      xmlElement.appendChild(xmlDocument.createTextNode(name.getStringValue()));
      xmlGroup.appendChild(xmlElement);
    }
    xmlModel.appendChild(xmlGroup);
    
    xmlGroup = xmlDocument.createElement("sounds");
    listElements = sounds.getElementArrayValue();
    for (int i = 0; i < listElements.length; i++) {
      xmlElement = xmlDocument.createElement("sound");
      xmlElement.appendChild(xmlDocument.createTextNode(name.getStringValue()));
      xmlGroup.appendChild(xmlElement);
    }
    xmlModel.appendChild(xmlGroup);
    
    xmlDocument.appendChild(xmlModel);
    xmlDocument.getDocumentElement().normalize();
    
    return xmlDocument;
  }
  
  public void saveCharacter(edu.cmu.cs.stage3.alice.core.Element element) {
    String characterFilename = name.getStringValue() + "." + "a2c";
    characterFilename = characterFilename.substring(0, 1).toUpperCase() + characterFilename.substring(1);
    
    saveCharacterFileDialog.setSelectedFile(new File(characterFilename));
    saveCharacterFileDialog.setVisible(true);
    AuthoringToolResources.centerComponentOnScreen(saveCharacterFileDialog);
    int returnVal = DialogManager.showDialog(saveCharacterFileDialog, null);
    
    if (returnVal == 0) {
      File file = saveCharacterFileDialog.getSelectedFile();
      saveCharacter(element, file);
    }
  }
  
  public void saveCharacter(edu.cmu.cs.stage3.alice.core.Element element, File file)
  {
    BatchSaveWithThumbnails batch = new BatchSaveWithThumbnails();
    Transformable trans = (Transformable)element;
    


    File dst = file;
    Object def = vehicle.get();
    vehicle.set(batch.getWorld());
    
    java.util.Dictionary map = new java.util.Hashtable();
    m_camera.getAGoodLookAtRightNow(trans);
    
    edu.cmu.cs.stage3.math.Sphere bs = trans.getBoundingSphere();
    if ((bs != null) && (bs.getCenter() != null) && (bs.getRadius() > 0.0D)) {
      double radius = bs.getRadius();
      double theta = Math.min(m_camera.horizontalViewingAngle.doubleValue(), m_camera.verticalViewingAngle.doubleValue());
      double farDist = radius / Math.sin(theta / 2.0D) + radius;
      m_camera.farClippingPlaneDistance.set(new Double(farDist));
    }
    
    m_rt.clearAndRenderOffscreen();
    Image image = m_rt.getOffscreenImage();
    


    Image zBufferImage = m_rt.getZBufferImage();
    
    int clear = 65280;
    if (zBufferImage != null) {
      int width = zBufferImage.getWidth(null);
      int height = zBufferImage.getHeight(null);
      int[] zBuffer = makePixmap(zBufferImage);
      int[] imageBuffer = makePixmap(image);
      double[] shadow = new double[zBuffer.length];
      
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          if (zBuffer[(x + y * width)] != clear) {
            for (int i = 1; i <= 6; i++) {
              double shade = (6 - i + 1) / 10.0D;
              if ((x + i + (y + i) * width < zBuffer.length) && (zBuffer[(x + i + (y + i) * width)] == clear) && (shadow[(x + i + (y + i) * width)] < shade)) {
                shadow[(x + i + (y + i) * width)] = shade;
              }
            }
          }
        }
      }
      
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          int r = imageBuffer[(x + y * width)] >> 16 & 0xFF;
          int g = imageBuffer[(x + y * width)] >> 8 & 0xFF;
          int b = imageBuffer[(x + y * width)] & 0xFF;
          r = (int)(r * (1.0D - shadow[(x + y * width)]));
          g = (int)(g * (1.0D - shadow[(x + y * width)]));
          b = (int)(b * (1.0D - shadow[(x + y * width)]));
          imageBuffer[(x + y * width)] = (-16777216 + (r << 16) + (g << 8) + b);
        }
      }
      image = new java.awt.image.BufferedImage(width, height, 2);
      ((java.awt.image.BufferedImage)image).setRGB(0, 0, width, height, imageBuffer, 0, width);
    }
    


    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    try {
      edu.cmu.cs.stage3.image.ImageIO.store("png", baos, image);
      map.put("thumbnail.png", baos.toByteArray());
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }
    
    Document xmlDocument = createCharacterXML(trans);
    
    baos = new java.io.ByteArrayOutputStream();
    try {
      edu.cmu.cs.stage3.xml.Encoder.write(xmlDocument, baos);
      map.put("galleryData.xml", baos.toByteArray());
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    
    vehicle.set(def);
    try
    {
      Thread.sleep(100L);
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }
    
    if ((m_rt instanceof edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget)) {
      ((edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer.RenderTarget)m_rt).commitAnyPendingChanges();
    }
    try
    {
      trans.store(dst, null, map);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
  




  public edu.cmu.cs.stage3.alice.core.Element importElement()
  {
    return importElement(null);
  }
  
  public edu.cmu.cs.stage3.alice.core.Element importElement(Object path) {
    return importElement(path, null);
  }
  
  public edu.cmu.cs.stage3.alice.core.Element importElement(Object path, edu.cmu.cs.stage3.alice.core.Element parent) {
    return importElement(path, parent, null);
  }
  
  public edu.cmu.cs.stage3.alice.core.Element importElement(Object path, edu.cmu.cs.stage3.alice.core.Element parent, edu.cmu.cs.stage3.alice.authoringtool.util.PostImportRunnable postImportRunnable) {
    return importElement(path, parent, postImportRunnable, true);
  }
  
  public edu.cmu.cs.stage3.alice.core.Element importElement(Object path, edu.cmu.cs.stage3.alice.core.Element parent, edu.cmu.cs.stage3.alice.authoringtool.util.PostImportRunnable postImportRunnable, boolean animateOnAdd) {
    edu.cmu.cs.stage3.alice.core.Element element = null;
    
    if (path == null)
    {
      if (importFileChooser.getFileFilter().getDescription().compareToIgnoreCase(Messages.getString("Image_Files__BMP_JPG_JPEG_PNG_GIF_TIF_TIFF_")) == 0) {
        importFileChooser.setAccessory(new edu.cmu.cs.stage3.alice.authoringtool.dialog.ImagePreview(importFileChooser));
        importFileChooser.setCurrentDirectory(new File(JAlice.getAliceHomeDirectory(), "textureMap"));
        File file = new File(JAlice.getAliceHomeDirectory(), "textureMap/GrassTexture.png");
        if (file.exists()) {
          importFileChooser.setSelectedFile(file);
        }
      } else {
        importFileChooser.setAccessory(null);
        importFileChooser.setSelectedFile(new File(""));
        Configuration authoringToolConfig = Configuration.getLocalConfiguration(JAlice.class.getPackage());
        importFileChooser.setCurrentDirectory(new File(authoringToolConfig.getValue("directories.worldsDirectory")));
      }
      importFileChooser.rescanCurrentDirectory();
      int returnVal = DialogManager.showDialog(importFileChooser, null);
      
      if (returnVal == 0) {
        File file = importFileChooser.getSelectedFile();
        if (file.getAbsolutePath().toLowerCase().endsWith("a2c")) {
          return loadAndAddCharacter(file);
        }
        path = file;
      }
    }
    
    importFileChooser.setSelectedFile(null);
    if (path != null) {
      String ext = null;
      if ((path instanceof String)) {
        String s = (String)path;
        ext = s.substring(s.lastIndexOf('.') + 1).toUpperCase();
      } else if ((path instanceof File)) {
        String s = ((File)path).getAbsolutePath();
        ext = s.substring(s.lastIndexOf('.') + 1).toUpperCase();
        path = ((File)path).getAbsoluteFile();
      } else if ((path instanceof URL)) {
        String s = ((URL)path).getPath();
        ext = s.substring(s.lastIndexOf('.') + 1).toUpperCase();
      } else {
        throw new IllegalArgumentException(Messages.getString("path_must_be_a_String__java_io_File__or_java_net_URL"));
      }
      String s;
      if (parent == null) {
        parent = world;
      }
      
      Importer importerToUse = null;
      for (Iterator iter = importing.getImporters().iterator(); iter.hasNext();) {
        Importer importer = (Importer)iter.next();
        
        if (importer.getExtensionMap().get(ext) != null) {
          importerToUse = importer;
          break;
        }
      }
      
      if (importerToUse != null) {
        undoRedoStack.startCompound();
        try
        {
          if ((path instanceof String)) {
            element = importerToUse.load((String)path);
          } else if ((path instanceof File)) {
            element = importerToUse.load((File)path);
          } else if ((path instanceof URL)) {
            element = importerToUse.load((URL)path);
          }
          
          if (element != null) {
            if (element.getParent() != parent) {
              String name = name.getStringValue();
              name.set(AuthoringToolResources.getNameForNewChild(name, parent));
              if (parent != null) {
                parent.addChild(element);
                AuthoringToolResources.addElementToAppropriateProperty(element, parent);
              }
              if (animateOnAdd) {
                animateAddModelIfPossible(element, parent);
              }
              

              if (postImportRunnable != null) {
                postImportRunnable.setImportedElement(element);
                postImportRunnable.run();
              }
            }
          } else {
            showErrorDialog(Messages.getString("Corrupted_file_or_incorrect_file_type_"), null, false);
          }
        } catch (IOException e) {
          showErrorDialog(Messages.getString("Error_while_importing_object_"), e);
        }
        undoRedoStack.stopCompound();
      } else {
        DialogManager.showMessageDialog(Messages.getString("No_importer_found_to_load_given_file_type_"), Messages.getString("Import_error"), 0);
      }
    } else {
      return null;
    }
    
    return element;
  }
  
  private void animateAddModelIfPossible(edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.core.Element parent) {
    if ((element instanceof Transformable)) {
      if ((parent instanceof edu.cmu.cs.stage3.alice.core.ReferenceFrame)) {
        if ((getCurrentCamera() instanceof SymmetricPerspectiveCamera)) {
          animateAddModel(
            (Transformable)element, 
            (edu.cmu.cs.stage3.alice.core.ReferenceFrame)parent, 
            (SymmetricPerspectiveCamera)getCurrentCamera());
        } else {
          vehicle.set((edu.cmu.cs.stage3.alice.core.ReferenceFrame)parent);
        }
      }
      else if ((getCurrentCamera() instanceof SymmetricPerspectiveCamera)) {
        animateAddModel((Transformable)element, world, (SymmetricPerspectiveCamera)getCurrentCamera());
      } else {
        vehicle.set(world);
      }
    }
  }
  
  public void animateAddModel(Transformable transformable, edu.cmu.cs.stage3.alice.core.ReferenceFrame vehicle, SymmetricPerspectiveCamera camera)
  {
    if ((transformable instanceof Model)) {
      Model model = (Model)transformable;
      
      HashMap opacityMap = new HashMap();
      Vector properties = new Vector();
      if (camera != null) {
        properties.add(localTransformation);
        properties.add(farClippingPlaneDistance);
      }
      properties.add(vehicle);
      edu.cmu.cs.stage3.alice.core.Element[] descendants = model.getDescendants(Model.class, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      for (int i = 0; i < descendants.length; i++) {
        opacityMap.put(descendants[i], opacity.get());
        properties.add(opacity);
      }
      Property[] affectedProperties = (Property[])properties.toArray(new Property[0]);
      boolean tempListening = getUndoRedoStack().getIsListening();
      undoRedoStack.setIsListening(false);
      opacity.set(new Double(0.0D), edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      
      javax.vecmath.Matrix4d goodLook = null;
      if (camera != null) {
        vehicle.set(vehicle);
        goodLook = camera.calculateGoodLookAt(model);
      }
      vehicle.set(null);
      undoRedoStack.setIsListening(tempListening);
      
      double distanceToBackOfObject = 0.0D;
      boolean needToChangeFarClipping = false;
      if (camera != null) {
        distanceToBackOfObject = AuthoringToolResources.distanceToBackAfterGetAGoodLookAt(model, camera);
        needToChangeFarClipping = distanceToBackOfObject > farClippingPlaneDistance.doubleValue();
      }
      

      PropertyAnimation setupOpacity = new PropertyAnimation();
      element.set(model);
      propertyName.set("opacity");
      value.set(new Double(0.0D));
      duration.set(new Double(0.0D));
      howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      PropertyAnimation vehicleAnimation = new PropertyAnimation();
      element.set(model);
      propertyName.set("vehicle");
      value.set(vehicle);
      duration.set(new Double(0.0D));
      howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
      PointOfViewAnimation getAGoodLook = new PointOfViewAnimation();
      if (camera != null) {
        subject.set(camera);
        pointOfView.set(goodLook);
      }
      
      PointOfViewAnimation cameraGoBack = new PointOfViewAnimation();
      if (camera != null) {
        subject.set(camera);
        pointOfView.set(camera.getLocalTransformation());
        duration.set(new Double(0.5D));
      }
      Wait wait = new Wait();
      duration.set(new Double(0.7D));
      Wait wait2 = new Wait();
      duration.set(new Double(0.2D));
      PropertyAnimation farClipping = new PropertyAnimation();
      if (camera != null) {
        element.set(camera);
        propertyName.set("farClippingPlaneDistance");
        value.set(new Double(distanceToBackOfObject));
      }
      PropertyAnimation farClipping2 = new PropertyAnimation();
      if (camera != null) {
        element.set(camera);
        propertyName.set("farClippingPlaneDistance");
        value.set(farClippingPlaneDistance.get());
      }
      DoTogether opacityDoTogether = new DoTogether();
      for (Iterator iter = opacityMap.keySet().iterator(); iter.hasNext();) {
        Model m = (Model)iter.next();
        Object opacity = opacityMap.get(m);
        PropertyAnimation opacityAnimation = new PropertyAnimation();
        element.set(m);
        propertyName.set("opacity");
        value.set(opacity);
        howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
        componentResponses.add(opacityAnimation);
      }
      DoInOrder waitOpacityDoInOrder = new DoInOrder();
      componentResponses.add(wait);
      componentResponses.add(opacityDoTogether);
      componentResponses.add(wait2);
      DoTogether cameraOpacityDoTogether = new DoTogether();
      if (needToChangeFarClipping) {
        componentResponses.add(farClipping);
      }
      componentResponses.add(getAGoodLook);
      componentResponses.add(waitOpacityDoInOrder);
      DoInOrder response = new DoInOrder();
      componentResponses.add(setupOpacity);
      componentResponses.add(vehicleAnimation);
      if (camera != null) {
        componentResponses.add(cameraOpacityDoTogether);
        componentResponses.add(cameraGoBack);
        componentResponses.add(farClipping2);
      } else {
        componentResponses.add(opacityDoTogether);
      }
      
      PropertyAnimation undoVehicleAnimation = new PropertyAnimation();
      element.set(model);
      propertyName.set("vehicle");
      value.set(null);
      duration.set(new Double(0.0D));
      howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
      PointOfViewAnimation undoGetAGoodLook = new PointOfViewAnimation();
      if (camera != null) {
        subject.set(camera);
        pointOfView.set(goodLook);
        duration.set(new Double(0.5D));
      }
      PointOfViewAnimation undoCameraGoBack = new PointOfViewAnimation();
      if (camera != null) {
        subject.set(camera);
        pointOfView.set(camera.getLocalTransformation());
      }
      Wait undoWait = new Wait();
      duration.set(new Double(0.9D));
      Wait undoWait2 = new Wait();
      duration.set(new Double(0.2D));
      PropertyAnimation undoFarClipping = new PropertyAnimation();
      if (camera != null) {
        element.set(camera);
        propertyName.set("farClippingPlaneDistance");
        value.set(new Double(distanceToBackOfObject));
        duration.set(new Double(0.2D));
      }
      PropertyAnimation undoFarClipping2 = new PropertyAnimation();
      if (camera != null) {
        element.set(camera);
        propertyName.set("farClippingPlaneDistance");
        value.set(farClippingPlaneDistance.get());
        duration.set(new Double(0.2D));
      }
      DoInOrder undoCameraGoBackWaitDoInOrder = new DoInOrder();
      componentResponses.add(undoWait);
      componentResponses.add(undoCameraGoBack);
      DoTogether undoOpacityDoTogether = new DoTogether();
      for (Iterator iter = opacityMap.keySet().iterator(); iter.hasNext();) {
        Model m = (Model)iter.next();
        PropertyAnimation opacityAnimation = new PropertyAnimation();
        element.set(m);
        propertyName.set("opacity");
        value.set(new Double(0.0D));
        howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
        componentResponses.add(opacityAnimation);
      }
      DoInOrder undoOpacityWaitDoInOrder = new DoInOrder();
      componentResponses.add(undoWait2);
      componentResponses.add(undoOpacityDoTogether);
      DoTogether undoCameraOpacityDoTogether = new DoTogether();
      componentResponses.add(undoOpacityWaitDoInOrder);
      componentResponses.add(undoCameraGoBackWaitDoInOrder);
      if (needToChangeFarClipping) {
        componentResponses.add(undoFarClipping);
      }
      DoInOrder undoResponse = new DoInOrder();
      if (camera != null) {
        componentResponses.add(undoGetAGoodLook);
        componentResponses.add(undoCameraOpacityDoTogether);
        if (needToChangeFarClipping) {
          componentResponses.add(undoFarClipping2);
        }
      } else {
        componentResponses.add(undoOpacityWaitDoInOrder);
      }
      componentResponses.add(undoVehicleAnimation);
      
      performOneShot(response, undoResponse, affectedProperties);
    } else {
      vehicle.set(vehicle);
    }
  }
  
  private void displayDurations(edu.cmu.cs.stage3.alice.core.Response r) {
    if ((r instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse)) {
      edu.cmu.cs.stage3.alice.core.response.CompositeResponse c = (edu.cmu.cs.stage3.alice.core.response.CompositeResponse)r;
      System.out.println("COMPOSITE(");
      for (int i = 0; i < componentResponses.size(); i++) {
        displayDurations((edu.cmu.cs.stage3.alice.core.Response)componentResponses.get(i));
      }
      System.out.println(")");
    }
    else {
      System.out.println(duration.doubleValue());
    }
  }
  
  public void getAGoodLookAt(Transformable transformable, final SymmetricPerspectiveCamera camera) {
    Property[] affectedProperties = { localTransformation };
    

    javax.vecmath.Matrix4d goodLook = camera.calculateGoodLookAt(transformable);
    
    PointOfViewAnimation getAGoodLook = new PointOfViewAnimation();
    subject.set(camera);
    pointOfView.set(goodLook);
    
    PointOfViewAnimation undoResponse = new PointOfViewAnimation();
    subject.set(camera);
    pointOfView.set(camera.getLocalTransformation());
    
    performOneShot(getAGoodLook, undoResponse, affectedProperties);
    
    final double distanceToBackOfObject = AuthoringToolResources.distanceToBackAfterGetAGoodLookAt(transformable, camera);
    
    if (distanceToBackOfObject > farClippingPlaneDistance.doubleValue()) {
      Runnable runnable = new Runnable() {
        public void run() {
          try {
            Thread.sleep(1000L);
          } catch (InterruptedException e) {
            AuthoringTool.showErrorDialog(Messages.getString("Interrupted_during_clipping_plane_operation_"), e);
          }
          int result = 
            DialogManager.showConfirmDialog(
            Messages.getString("The_camera_s_far_clipping_plane_is_too_close_to_see_all_of_the_object___Would_you_like_to_move_it_back_"), 
            Messages.getString("Alter_camera_s_far_clipping_plane_"), 
            0, 
            1);
          if (result == 0) {
            Property[] affectedProperties = { camerafarClippingPlaneDistance };
            
            PropertyAnimation farClippingAnimation = new PropertyAnimation();
            element.set(camera);
            propertyName.set("farClippingPlaneDistance");
            value.set(new Double(distanceToBackOfObject));
            
            PropertyAnimation undoResponse = new PropertyAnimation();
            element.set(camera);
            propertyName.set("farClippingPlaneDistance");
            value.set(camerafarClippingPlaneDistance.get());
            
            performOneShot(farClippingAnimation, undoResponse, affectedProperties);
          }
        }
      };
      SwingUtilities.invokeLater(runnable);
    }
  }
  
  public void saveTabs() {
    if ((saveTabsEnabled) && (world != null)) {
      Object[] objects = jAliceFrame.getTabbedEditorComponent().getObjectsBeingEdited();
      String tabObjectsString = "";
      for (int i = 0; i < objects.length; i++) {
        if ((objects[i] instanceof edu.cmu.cs.stage3.alice.core.Element)) {
          tabObjectsString = tabObjectsString + ((edu.cmu.cs.stage3.alice.core.Element)objects[i]).getKey() + ":";
        }
      }
      world.data.put("edu.cmu.cs.stage3.alice.authoringtool.tabObjects", tabObjectsString);
    }
  }
  
  public void loadTabs() {
    if ((authoringToolConfig.getValue("loadSavedTabs").equalsIgnoreCase("true")) && 
      (world != null)) {
      String tabObjectsString = (String)world.data.get("edu.cmu.cs.stage3.alice.authoringtool.tabObjects");
      if (tabObjectsString != null) {
        StringTokenizer st = new StringTokenizer(tabObjectsString, ":");
        getJAliceFrame().setCursor(java.awt.Cursor.getPredefinedCursor(3));
        while (st.hasMoreTokens()) {
          String key = st.nextToken();
          key = key.substring(world.getKey().length() + (key.equals(world.getKey()) ? 0 : 1));
          edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(key);
          if (element != null) {
            editObject(element, false);
          }
        }
        getJAliceFrame().setCursor(java.awt.Cursor.getDefaultCursor());
      }
    }
  }
  
  public boolean isImportable(String extension)
  {
    for (Iterator iter = importing.getImporters().iterator(); iter.hasNext();) {
      Importer importer = (Importer)iter.next();
      java.util.Map map = importer.getExtensionMap();
      if (map.get(extension.toUpperCase()) != null) {
        return true;
      }
    }
    
    return false;
  }
  
  public void setImportFileFilter(String extensionString) {
    javax.swing.filechooser.FileFilter filter = (javax.swing.filechooser.FileFilter)extensionStringsToFileFilterMap.get(extensionString);
    if (filter != null) {
      importFileChooser.setFileFilter(filter);
    }
  }
  
  public void showWorldInfoDialog() {
    updateWorldOpenTime();
    worldInfoContentPane.setWorld(world);
    DialogManager.showDialog(worldInfoContentPane);
  }
  
  private DecimalFormat captureFormatter = new DecimalFormat();
  
  public void storeCapturedImage(Image image) {
    File dir = new File(authoringToolConfig.getValue("screenCapture.directory"));
    
    int numDigits = Integer.parseInt(authoringToolConfig.getValue("screenCapture.numDigits"));
    StringBuffer pattern = new StringBuffer(authoringToolConfig.getValue("screenCapture.baseName"));
    String codec = authoringToolConfig.getValue("screenCapture.codec");
    pattern.append("#");
    for (int i = 0; i < numDigits; i++) {
      pattern.append("0");
    }
    pattern.append(".");
    pattern.append(codec);
    captureFormatter.applyPattern(pattern.toString());
    
    int i = 0;
    File file = new File(dir, captureFormatter.format(i));
    boolean writable;
    boolean writable; if (file.exists()) {
      writable = file.canWrite();
    } else {
      try {
        boolean success = file.createNewFile();
        boolean writable = success;
        if (success) {
          file.delete();
        }
      } catch (Throwable e) {
        writable = false;
      }
    }
    
    if (!writable) {
      Object[] options = { Messages.getString("Yes__let_me_select_a_directory"), Messages.getString("No__I_don_t_want_to_take_a_picture_anymore") };
      int dialogVal = 
        DialogManager.showOptionDialog(
        Messages.getString("Alice_can_not_save_the_captured_image__Do_you_want_to_select_a_new_directory_"), 
        Messages.getString("Alice_can_t_save_the_file"), 
        0, 
        3, 
        null, 
        options, 
        options[0]);
      if (dialogVal == 0) {
        File parent = dir.getParentFile();
        try {
          browseFileChooser.setCurrentDirectory(parent);
        }
        catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
        
        boolean done = false;
        while (!done) {
          int returnVal = DialogManager.showOpenDialog(browseFileChooser);
          if (returnVal == 0) {
            File selectedFile = browseFileChooser.getSelectedFile();
            File testCaptureFile = new File(selectedFile, "test.jpg");
            if (testCaptureFile.exists()) {
              writable = testCaptureFile.canWrite();
            } else {
              try {
                boolean success = testCaptureFile.createNewFile();
                writable = success;
                if (success) {
                  testCaptureFile.delete();
                }
              } catch (Exception e) {
                writable = false;
              }
            }
            if (!writable) {
              DialogManager.showMessageDialog(Messages.getString("The_capture_directory_specified_can_not_be_written_to__Please_choose_another_directory_"));
            } else {
              done = true;
              dir = selectedFile;
            }
          } else {
            DialogManager.showMessageDialog(
              Messages.getString("You_have_not_selected_a_writable_directory_to_save_pictures_to_") + "\n" + Messages.getString("Alice_will_not_be_able_to_take_pictures_until_you_do_so__You_can_go_to_Preferences__Screen_Grab_to_set_a_directory_"), 
              Messages.getString("No_directory_set"), 
              2);
            return;
          }
        }
      } else {
        DialogManager.showMessageDialog(
          Messages.getString("You_have_not_selected_a_writable_directory_to_save_pictures_to_") + "\n" + Messages.getString("Alice_will_not_be_able_to_take_pictures_until_you_do_so__You_can_go_to_Preferences__Screen_Grab_to_set_a_directory_"), 
          Messages.getString("No_directory_set"), 
          2);
        return;
      }
      file = new File(dir, captureFormatter.format(i));
    }
    while (file.exists()) {
      i++;
      file = new File(dir, captureFormatter.format(i));
    }
    try {
      file.createNewFile();
      java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      java.io.DataOutputStream dos = new java.io.DataOutputStream(bos);
      edu.cmu.cs.stage3.image.ImageIO.store(codec, dos, image);
      dos.flush();
      fos.close();
      
      if (authoringToolConfig.getValue("screenCapture.informUser").equalsIgnoreCase("true")) {
        java.awt.image.BufferedImage scaledImage = edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects.getImageScaledToLongestDimension(image, 128);
        edu.cmu.cs.stage3.alice.authoringtool.dialog.CapturedImageContentPane capturedImageContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.CapturedImageContentPane();
        capturedImageContentPane.setStoreLocation(file.getCanonicalPath());
        capturedImageContentPane.setImage(scaledImage);
        DialogManager.showDialog(capturedImageContentPane);
      }
    }
    catch (Throwable t)
    {
      showErrorDialog(Messages.getString("Error_while_storing_screen_capture_"), t);
    }
  }
  
  public void performPointOfViewOneShot(Transformable transformable, Matrix44 newTransformation) {
    Matrix44 oldTransformation = localTransformation.getMatrix44Value();
    
    PointOfViewAnimation povAnimation = new PointOfViewAnimation();
    subject.set(transformable);
    pointOfView.set(newTransformation);
    
    edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior oneShotBehavior = new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior();
    oneShotBehavior.setResponse(povAnimation);
    oneShotBehavior.setAffectedProperties(new Property[] { localTransformation });
    oneShotBehavior.start(oneShotScheduler);
    
    edu.cmu.cs.stage3.alice.authoringtool.util.PointOfViewUndoableRedoable undo = new edu.cmu.cs.stage3.alice.authoringtool.util.PointOfViewUndoableRedoable(transformable, oldTransformation, newTransformation, oneShotScheduler);
    undoRedoStack.push(undo);
  }
  
  public void performOneShot(edu.cmu.cs.stage3.alice.core.Response response, edu.cmu.cs.stage3.alice.core.Response undoResponse, Property[] affectedProperties) {
    edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior oneShotBehavior = new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior();
    oneShotBehavior.setResponse(response);
    oneShotBehavior.setAffectedProperties(affectedProperties);
    oneShotBehavior.start(oneShotScheduler);
    
    edu.cmu.cs.stage3.alice.authoringtool.util.OneShotUndoableRedoable undo = new edu.cmu.cs.stage3.alice.authoringtool.util.OneShotUndoableRedoable(response, undoResponse, oneShotBehavior, oneShotScheduler);
    undoRedoStack.push(undo);
  }
  





  public void lostOwnership(java.awt.datatransfer.Clipboard clipboard, java.awt.datatransfer.Transferable contents) {}
  





  public void makeBillboard()
  {
    setImportFileFilter("Image Files");
    importElement(null, null, new edu.cmu.cs.stage3.alice.authoringtool.util.PostImportRunnable() {
      public void run() {
        edu.cmu.cs.stage3.alice.core.TextureMap textureMap = (edu.cmu.cs.stage3.alice.core.TextureMap)getImportedElement();
        if (textureMap != null) {
          textureMap.removeFromParent();
          name.set(AuthoringToolResources.getNameForNewChild(name.getStringValue(), world));
          edu.cmu.cs.stage3.alice.core.Billboard billboard = AuthoringToolResources.makeBillboard(textureMap, true);
          AuthoringTool.this.animateAddModelIfPossible(billboard, world);
          world.addChild(billboard);
          vehicle.set(world);
          world.sandboxes.add(billboard);
        }
      }
    }, true);
  }
  
  public void setElementScope(edu.cmu.cs.stage3.alice.core.Element element)
  {
    jAliceFrame.getWorldTreeComponent().setCurrentScope(element);
    if (element != null) {
      if (selectedElement != null) {
        if (!selectedElement.isDescendantOf(element)) {
          setSelectedElement(element);
        }
      } else {
        setSelectedElement(element);
      }
    }
  }
  

  public edu.cmu.cs.stage3.alice.core.Camera getCurrentCamera()
  {
    edu.cmu.cs.stage3.alice.core.Element[] e = world.getDescendants(edu.cmu.cs.stage3.alice.core.Camera.class);
    if (e != null) {
      return (edu.cmu.cs.stage3.alice.core.Camera)e[0];
    }
    return null;
  }
  
  public void showAbout()
  {
    aboutContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.AboutContentPane();
    DialogManager.showDialog(aboutContentPane);
  }
  
  public void showLicense() {
    licenseContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.LicenseContentPane();
    DialogManager.showDialog(licenseContentPane);
  }
  
  public void showPreferences() {
    int result = DialogManager.showDialog(preferencesContentPane);
    if (result == 0) {
      preferencesContentPane.finalizeSelections();
    }
  }
  











  public static void showErrorDialog(String message, Object t)
  {
    showErrorDialog(message, t, false);
  }
  
  public static void showErrorDialog(String message, Object t, boolean showSubmitBugButton) {
    edu.cmu.cs.stage3.alice.authoringtool.dialog.ErrorContentPane errorPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.ErrorContentPane();
    errorPane.setMessage(message);
    
    if ((t instanceof Throwable)) {
      errorPane.setThrowable((Throwable)t);
    } else
      errorPane.setDetails((String)t);
    DialogManager.showDialog(errorPane);
    if (((t instanceof Throwable)) && 
      (getHack().isStdErrToConsole()) && (t != null)) {
      Throwable tt = (Throwable)t;
      tt.printStackTrace(System.err);
    }
  }
  














  private void stopWorldAndShowDialog(Throwable throwable)
  {
    stopWorld();
    if ((throwable instanceof edu.cmu.cs.stage3.alice.core.ExceptionWrapper)) {
      throwable = ((edu.cmu.cs.stage3.alice.core.ExceptionWrapper)throwable).getWrappedException();
    }
    final Throwable t = throwable;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if ((t instanceof edu.cmu.cs.stage3.alice.core.SimulationException)) {
          showSimulationExceptionDialog((edu.cmu.cs.stage3.alice.core.SimulationException)t);
        } else {
          AuthoringTool.showErrorDialog(Messages.getString("Error_during_simulation_"), t);
        }
      }
    });
  }
  
  public void showSimulationExceptionDialog(edu.cmu.cs.stage3.alice.core.SimulationException simulationException) {
    edu.cmu.cs.stage3.alice.authoringtool.dialog.SimulationExceptionPanel simulationExceptionPanel = new edu.cmu.cs.stage3.alice.authoringtool.dialog.SimulationExceptionPanel(this);
    simulationExceptionPanel.setSimulationException(simulationException);
    simulationExceptionPanel.setErrorHighlightingEnabled(true);
    DialogManager.showMessageDialog(simulationExceptionPanel, Messages.getString("Problem_Detected"), 0, AuthoringToolResources.getAliceSystemIcon());
    simulationExceptionPanel.setErrorHighlightingEnabled(false);
  }
  
  public JFileChooser getImportFileChooser() {
    return importFileChooser;
  }
  
  public World getWorld() {
    return world;
  }
  
  public void showOnScreenHelp() {
    showStencils();
  }
  
  public void showStdErrDialog() {
    stdErrContentPane.setMode(2);
    stdErrContentPane.showStdErrDialog();
  }
  
  public void showStdOutDialog() {
    stdOutContentPane.setMode(2);
    stdOutContentPane.showStdOutDialog();
  }
  
  public void updateWorldOpenTime() {
    if (world != null) {
      String worldOpenTimeString = (String)world.data.get("edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime");
      long worldOpenTime = 0L;
      if (worldOpenTimeString != null) {
        worldOpenTime = Long.parseLong(worldOpenTimeString);
      }
      worldOpenTime += System.currentTimeMillis() - worldLoadedTime;
      worldLoadedTime = System.currentTimeMillis();
      world.data.put("edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime", Long.toString(worldOpenTime));
    }
  }
  
  public void countSomething(String dataKey) {
    String countString = (String)world.data.get(dataKey);
    int count = 0;
    if (countString != null) {
      count = Integer.parseInt(countString);
    }
    count++;
    world.data.put(dataKey, Integer.toString(count));
  }
  
  public void showPrintDialog() {
    int result = DialogManager.showDialog(exportCodeForPrintingContentPane);
    if (result == 0) {
      final File fileToExportTo = exportCodeForPrintingContentPane.getFileToExportTo();
      edu.cmu.cs.stage3.progress.ProgressPane progressPane = new edu.cmu.cs.stage3.progress.ProgressPane(Messages.getString("Saving_HTML___"), Messages.getString("Saving__")) {
        protected void construct() throws edu.cmu.cs.stage3.progress.ProgressCancelException {
          try {
            StringBuffer htmlOutput = new StringBuffer();
            exportCodeForPrintingContentPane.getHTML(htmlOutput, fileToExportTo, true, true, this);
            java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(new java.io.FileOutputStream(fileToExportTo));
            writer.write(htmlOutput.toString());
            writer.flush();
            writer.close();
          } catch (edu.cmu.cs.stage3.progress.ProgressCancelException pce) {
            fileToExportTo.delete();
            throw pce;
          } catch (Throwable t) {
            AuthoringTool.showErrorDialog(Messages.getString("Unable_to_store_world_to_file__") + fileToExportTo, t);
            fileToExportTo.delete();
          }
        }
      };
      DialogManager.showDialog(progressPane);
    }
  }
  



  private void setCurrentWorldLocation(File file)
  {
    currentWorldLocation = file;
    updateTitle();
  }
  
  private void updateTitle() {
    String path = "";
    if (currentWorldLocation != null) {
      try {
        path = currentWorldLocation.getCanonicalPath();
      } catch (IOException e) {
        path = currentWorldLocation.getAbsolutePath();
      }
    }
    
    String modifiedStatus = "";
    if (worldHasBeenModified) {
      modifiedStatus = Messages.getString("____Modified_");
    }
    
    jAliceFrame.setTitle("Alice (" + JAlice.getVersion() + ") " + path + modifiedStatus);
  }
  
  public Component getEditorForElement(edu.cmu.cs.stage3.alice.core.Element elementToEdit) {
    int index = jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementToEdit);
    if (index > -1) {
      return jAliceFrame.tabbedEditorComponent.getEditorAt(index).getJComponent();
    }
    Class editorClass = edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.getBestEditor(elementToEdit.getClass());
    Editor editor = editorManager.getEditorInstance(editorClass);
    edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.editObject(editor, elementToEdit);
    return editor.getJComponent();
  }
  




  protected HashMap componentMap = new HashMap();
  protected edu.cmu.cs.stage3.caitlin.stencilhelp.client.StencilManager stencilManager;
  protected HashSet classesToStopOn = new HashSet();
  protected Timer updateTimer;
  protected boolean stencilDragging = false;
  protected Component dragStartSource;
  protected File tutorialOne;
  protected File tutorialDirectory = new File(JAlice.getAliceHomeDirectory(), "tutorial").getAbsoluteFile();
  protected ArrayList wayPoints = new ArrayList();
  protected Dimension oldDimension;
  
  private void stencilInit() { stencilManager = new edu.cmu.cs.stage3.caitlin.stencilhelp.client.StencilManager(this);
    jAliceFrame.setGlassPane(stencilManager.getStencilComponent());
    ((JComponent)stencilManager.getStencilComponent()).setOpaque(false);
    
    classesToStopOn.add(javax.swing.JMenu.class);
    classesToStopOn.add(javax.swing.AbstractButton.class);
    classesToStopOn.add(javax.swing.JComboBox.class);
    classesToStopOn.add(javax.swing.JList.class);
    classesToStopOn.add(javax.swing.JMenu.class);
    classesToStopOn.add(javax.swing.JSlider.class);
    classesToStopOn.add(javax.swing.text.JTextComponent.class);
    classesToStopOn.add(JTabbedPane.class);
    classesToStopOn.add(JTree.class);
    classesToStopOn.add(javax.swing.JTable.class);
    classesToStopOn.add(DragFromComponent.class);
    classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel.class);
    classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.util.TrashComponent.class);
    classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController.class);
    classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BehaviorGroupsEditor.class);
    classesToStopOn.add(SceneEditor.class);
    classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.class);
    classesToStopOn.add(edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject.class);
    

    componentMap.put("fileMenu", jAliceFrame.fileMenu);
    componentMap.put("editMenu", jAliceFrame.editMenu);
    componentMap.put("toolsMenu", jAliceFrame.toolsMenu);
    componentMap.put("helpMenu", jAliceFrame.helpMenu);
    componentMap.put("playButton", jAliceFrame.playButton);
    componentMap.put("addObjectButton", jAliceFrame.addObjectButton);
    componentMap.put("undoButton", jAliceFrame.undoButton);
    componentMap.put("redoButton", jAliceFrame.redoButton);
    componentMap.put("trashComponent", jAliceFrame.trashComponent);
    componentMap.put("clipboardPanel", jAliceFrame.clipboardPanel);
    componentMap.put("objectTree", jAliceFrame.worldTreeComponent);
    componentMap.put("sceneEditor", jAliceFrame.sceneEditor);
    componentMap.put("details", jAliceFrame.dragFromComponent);
    componentMap.put("behaviors", jAliceFrame.behaviorGroupsEditor);
    componentMap.put("editors", jAliceFrame.tabbedEditorComponent);
    
    updateTimer = new Timer(100, new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent ev) {
        stencilManager.update();
      }
      
    });
    updateTimer.setRepeats(false);
    java.awt.event.AWTEventListener updateListener = new java.awt.event.AWTEventListener() {
      public void eventDispatched(java.awt.AWTEvent ev) {
        if (((ev.getSource() instanceof Component)) && (
          (ev.getSource() == jAliceFrame) || (jAliceFrame.isAncestorOf((Component)ev.getSource())))) {
          updateTimer.restart();
        }
        
      }
    };
    java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(updateListener, 
      98306L);
    

    jAliceFrame.worldTreeComponent.worldTree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
      public void treeCollapsed(javax.swing.event.TreeExpansionEvent ev) {
        updateTimer.restart();
      }
      
      public void treeExpanded(javax.swing.event.TreeExpansionEvent ev) { updateTimer.restart();
      }

    });
    tutorialOne = new File(JAlice.getAliceHomeDirectory(), "tutorial" + File.separator + "Tutorial1.stl");
  }
  
  public void hackStencilUpdate() {
    if (updateTimer != null) {
      updateTimer.restart();
    }
  }
  

  protected Point oldPosition;
  protected int oldLeftRightSplitPaneLocation;
  protected int oldWorldTreeDragFromSplitPaneLocation;
  protected int oldEditorBehaviorSplitPaneLocation;
  protected int oldSmallSceneBehaviorSplitPaneLocation;
  protected Dimension oldRenderWindowSize;
  protected Point oldRenderWindowPosition;
  protected boolean oldShouldConstrain;
  protected Rectangle oldRenderBounds;
  public void showStencils()
  {
    setLayout();
    jAliceFrame.dragFromComponent.selectTab(1);
    jAliceFrame.removeKeyListener(stencilManager);
    jAliceFrame.addKeyListener(stencilManager);
    jAliceFrame.requestFocus();
    stencilManager.showStencils(true);
    authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "true");
  }
  
  protected void restoreLayout() {
    jAliceFrame.setResizable(true);
    if (oldShouldConstrain) {
      authoringToolConfig.setValue("rendering.constrainRenderDialogAspectRatio", "true");
    } else {
      authoringToolConfig.setValue("rendering.constrainRenderDialogAspectRatio", "false");
    }
    renderContentPane.saveRenderBounds(oldRenderBounds);
    jAliceFrame.setSize(oldDimension);
    jAliceFrame.setLocation(oldPosition);
    jAliceFrame.validate();
    jAliceFrame.leftRightSplitPane.setDividerLocation(oldLeftRightSplitPaneLocation);
    jAliceFrame.worldTreeDragFromSplitPane.setDividerLocation(oldWorldTreeDragFromSplitPaneLocation);
    jAliceFrame.editorBehaviorSplitPane.setDividerLocation(oldEditorBehaviorSplitPaneLocation);
    jAliceFrame.validate();
    jAliceFrame.smallSceneBehaviorSplitPane.setDividerLocation(oldSmallSceneBehaviorSplitPaneLocation);
  }
  
  protected void setLayout() {
    int screenWidth = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    int screenHeight = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    

    Dimension d = getScreenSize();
    oldDimension = new Dimension(width, height);
    oldPosition = jAliceFrame.getLocation();
    Point newPosition = new Point(oldPosition.x, oldPosition.y);
    int newWidth = width;
    int newHeight = height;
    if (width > 1032) {
      newWidth = 1032;
    } else if ((width < 1024) && (screenWidth >= 1024)) {
      newWidth = 1032;
    }
    if (height > 776) {
      newHeight = 740;
    } else if ((height < 740) && (screenWidth >= 768)) {
      newHeight = 740;
    }
    if (oldPosition.x + newWidth > screenWidth + 4) {
      x = ((screenWidth - newWidth) / 2);
    }
    if (oldPosition.y + newHeight > screenHeight + 4) {
      y = ((screenHeight - 28 - newHeight) / 2);
    }
    boolean shouldModifyStuff = true;
    if ((screenWidth < 1024) || (screenHeight < 740)) {
      DialogManager.showMessageDialog(
        Messages.getString("Your_screen_resolution_is_lower_than_what_we_recommend_for_running_the_tutorial__n") + Messages.getString("Alice_will_still_run_the_tutorial__but_some_of_the_objects_may_not_line_up_well_"), 
        Messages.getString("Low_Resolution_Warning"), 
        2, 
        null);
      shouldModifyStuff = false;
    }
    oldSmallSceneBehaviorSplitPaneLocation = jAliceFrame.smallSceneBehaviorSplitPane.getDividerLocation();
    oldLeftRightSplitPaneLocation = jAliceFrame.leftRightSplitPane.getDividerLocation();
    oldWorldTreeDragFromSplitPaneLocation = jAliceFrame.worldTreeDragFromSplitPane.getDividerLocation();
    oldEditorBehaviorSplitPaneLocation = jAliceFrame.editorBehaviorSplitPane.getDividerLocation();
    oldRenderWindowSize = renderContentPane.getSize();
    oldRenderWindowPosition = renderContentPane.getLocation();
    
    oldShouldConstrain = authoringToolConfig.getValue("rendering.constrainRenderDialogAspectRatio").equalsIgnoreCase("true");
    authoringToolConfig.setValue("rendering.constrainRenderDialogAspectRatio", "false");
    
    oldRenderBounds = renderContentPane.getRenderBounds();
    

    if (((newHeight != oldDimension.height) || (newWidth != oldDimension.width)) && (shouldModifyStuff)) {
      DialogManager.showMessageDialog(
        Messages.getString("Alice_is_going_to_adjust_your_screen_size_to_make_the_tutorial_fit_on_the_screen_better__n") + Messages.getString("When_you_exit_the_tutorial_your_original_screen_size_will_be_restored_"), 
        Messages.getString("Different_Resolution_Warning"), 
        2, 
        null);
    }
    
    if (shouldModifyStuff) {
      jAliceFrame.smallSceneBehaviorSplitPane.setDividerLocation(224);
      jAliceFrame.leftRightSplitPane.setDividerLocation(300);
      jAliceFrame.worldTreeDragFromSplitPane.setDividerLocation(237);
      jAliceFrame.editorBehaviorSplitPane.setDividerLocation(204);
      try {
        Thread.sleep(100L);
      } catch (Exception localException) {}
      jAliceFrame.setExtendedState(0);
      jAliceFrame.setSize(newWidth, newHeight);
      jAliceFrame.setPreferredSize(new Dimension(newWidth, newHeight));
      jAliceFrame.setLocation(newPosition);
    }
    

    jAliceFrame.doLayout();
    jAliceFrame.setResizable(false);
  }
  
  public void hideStencils() {
    restoreLayout();
    stencilManager.showStencils(false);
    authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "false");
    jAliceFrame.removeKeyListener(stencilManager);
    jAliceFrame.requestFocus();
  }
  
  public void setGlassPane(Component c) {
    jAliceFrame.setGlassPane(c);
  }
  
  public void setVisible(boolean visible) {
    if (visible) {
      setLayout();
      authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "true");
      jAliceFrame.removeKeyListener(stencilManager);
      jAliceFrame.addKeyListener(stencilManager);
      jAliceFrame.requestFocus();
    } else {
      restoreLayout();
      authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "false");
      jAliceFrame.removeKeyListener(stencilManager);
      jAliceFrame.requestFocus();
    }
  }
  

  public edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule getStateCapsuleFromString(String capsuleString)
  {
    StencilStateCapsule sc = new StencilStateCapsule();
    sc.parse(capsuleString);
    return sc;
  }
  
  public edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule getCurrentState() {
    if (wayPoints.size() > 0) {
      WorldDifferencesCapsule currentWayPoint = (WorldDifferencesCapsule)wayPoints.get(0);
      StencilStateCapsule capsule = currentWayPoint.getStateCapsule();
      

      return capsule;
    }
    return null;
  }
  


  protected Component getValidComponent(Component c)
  {
    while (c != null)
    {
      if (((c instanceof JButton)) && ((c.getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController))) {
        c = c.getParent();
      } else if ((c instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementNamePropertyViewController)) {
        c = c.getParent();
      } else if (((c instanceof javax.swing.AbstractButton)) && ((c.getParent() instanceof javax.swing.JComboBox))) {
        c = c.getParent();
      } else if (((c instanceof javax.swing.JTextField)) && ((c.getParent() instanceof javax.swing.JComboBox))) {
        c = c.getParent();
      } else { if (((c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ImagePanel)) && ((c.getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator)))
          return c;
        if (((c instanceof JLabel)) && (Messages.getString("more___").equals(((JLabel)c).getText())))
          return c;
        if ((c instanceof SceneEditor)) {
          return ((SceneEditor)c).getRenderPanel();
        }
      }
      








      for (Iterator iter = classesToStopOn.iterator(); iter.hasNext();) {
        Class stopClass = (Class)iter.next();
        if (stopClass.isAssignableFrom(c.getClass())) {
          return c;
        }
      }
      

      c = c.getParent();
    }
    
    return null;
  }
  
  public String getIDForPoint(Point p, boolean dropSite) {
    p = SwingUtilities.convertPoint(jAliceFrame.getGlassPane(), p, jAliceFrame.getLayeredPane());
    Component c = jAliceFrame.getRootPane().getLayeredPane().findComponentAt(p);
    c = getValidComponent(c);
    if (c == null) {
      return null;
    }
    Point localPoint = SwingUtilities.convertPoint(jAliceFrame.getRootPane().getLayeredPane(), p, c);
    
    String key = null;
    if (((c instanceof JTree)) && (jAliceFrame.worldTreeComponent.isAncestorOf(c))) {
      JTree tree = (JTree)c;
      javax.swing.tree.TreePath treePath = tree.getClosestPathForLocation(x, y);
      Rectangle bounds = tree.getPathBounds(treePath);
      key = "objectTree";
      if (bounds.contains(localPoint)) {
        String elementKey = ((edu.cmu.cs.stage3.alice.core.Element)treePath.getLastPathComponent()).getKey(world);
        key = key + "<" + elementKey + ">";
      }
    } else if ((c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDClipboard)) {
      key = "clipboard";
      Component[] components = c.getParent().getComponents();
      int index = -1;
      for (int i = 0; i < components.length; i++) {
        if (components[i] == c) {
          index = i;
        }
      }
      key = key + "<" + Integer.toString(index) + ">";
    }
    else if ((jAliceFrame.sceneEditor.isAncestorOf(c)) || (c == jAliceFrame.sceneEditor)) {
      if (jAliceFrame.sceneEditor.getGuiMode() == SceneEditor.LARGE_MODE) {
        key = "sceneEditor<large>";
      } else {
        key = "sceneEditor<small>";
      }
      if (jAliceFrame.sceneEditor.getViewMode() == 1) {
        key = key + ":quadView";
      } else {
        key = key + ":singleView";
      }
      
      if ((jAliceFrame.sceneEditor.getGalleryViewer().isAncestorOf(c)) || (c == jAliceFrame.sceneEditor.getGalleryViewer())) {
        key = key + ":galleryViewer<" + jAliceFrame.sceneEditor.getGalleryViewer().getDirectory() + ">";
        
        if ((c instanceof JButton)) {
          key = key + ":button<" + ((JButton)c).getText() + ">";
        } else if ((c instanceof edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject)) {
          key = key + ":galleryObject<" + ((edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject)c).getUniqueIdentifier() + ">";
        } else if (c != jAliceFrame.sceneEditor.getGalleryViewer()) {
          key = null;
        }
      } else {
        String id = jAliceFrame.sceneEditor.getIdForComponent(c);
        
        if (id != null) {
          key = key + ":" + id;
        } else if (c != jAliceFrame.sceneEditor) {
          key = null;
        }
      }
    } else if ((jAliceFrame.dragFromComponent.isAncestorOf(c)) || (c == jAliceFrame.dragFromComponent)) {
      if (jAliceFrame.dragFromComponent.getElement() == null) {
        key = "details";
      } else {
        key = "details<" + jAliceFrame.dragFromComponent.getElement().getKey(world) + ">";
        if ((c instanceof JTabbedPane)) {
          int whichTab = ((JTabbedPane)c).getUI().tabForCoordinate((JTabbedPane)c, x, y);
          if (whichTab > -1) {
            key = key + ":tab<" + Integer.toString(whichTab) + ">";
          }
        } else if (c != jAliceFrame.dragFromComponent) {
          key = key + ":" + jAliceFrame.dragFromComponent.getKeyForComponent(c);
        }
      }
    } else if (jAliceFrame.behaviorGroupsEditor.isAncestorOf(c)) {
      key = "behaviors";
      if ((c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel)) {
        try {
          java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel)c).getTransferable();
          if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
            edu.cmu.cs.stage3.alice.core.Element e = (edu.cmu.cs.stage3.alice.core.Element)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
            key = key + ":elementTile<" + e.getKey(world) + ">";
          } else {
            key = null;
          }
        } catch (Exception e) {
          showErrorDialog("Error while examining DnDGroupingPanel.", e);
          key = null;
        }
      } else if ((c instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController)) {
        Property property = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController)c).getProperty();
        edu.cmu.cs.stage3.alice.core.Element element = property.getOwner();
        key = key + ":elementTile<" + element.getKey(world) + ">";
        key = key + ":property<" + property.getName() + ">";
      }
      else if (((c instanceof JLabel)) && (((JLabel)c).getText().equals("more..."))) {
        if ((c.getParent().getParent().getParent() instanceof FormattedElementViewController)) {
          FormattedElementViewController vc = (FormattedElementViewController)c.getParent().getParent().getParent();
          edu.cmu.cs.stage3.alice.core.Element element = vc.getElement();
          key = key + ":elementTile<" + element.getKey(world) + ">";
          key = key + ":more";
        } else {
          key = null;
        }
      } else if ((c instanceof JButton)) {
        if (((JButton)c).getText().equals("create new event")) {
          key = key + ":createNewEventButton";
        } else {
          key = null;
        }
      }
    } else if (jAliceFrame.tabbedEditorComponent.isAncestorOf(c)) {
      key = "editors";
      if ((c instanceof JTabbedPane)) {
        int whichTab = ((JTabbedPane)c).getUI().tabForCoordinate((JTabbedPane)c, x, y);
        if (whichTab > -1) {
          Object o = jAliceFrame.tabbedEditorComponent.getObjectBeingEditedAt(whichTab);
          if ((o instanceof edu.cmu.cs.stage3.alice.core.Element)) {
            key = key + ":element<" + ((edu.cmu.cs.stage3.alice.core.Element)o).getKey(world) + ">";
          } else {
            key = null;
          }
        }
      } else {
        Object o = jAliceFrame.tabbedEditorComponent.getObjectBeingEdited();
        if ((o instanceof edu.cmu.cs.stage3.alice.core.Element)) {
          key = key + ":element<" + ((edu.cmu.cs.stage3.alice.core.Element)o).getKey(world) + ">";
          











          if ((c instanceof JButton)) {
            key = key + ":button<" + ((JButton)c).getText() + ">";
          } else if ((c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel)) {
            try {
              java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel)c).getTransferable();
              if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
                edu.cmu.cs.stage3.alice.core.Element e = 
                  (edu.cmu.cs.stage3.alice.core.Element)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
                key = key + ":elementTile<" + e.getKey(world) + ">";
              } else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
                edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep = 
                  (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)transferable.getTransferData(
                  edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
                key = key + ":elementPrototypeTile<" + ep.getElementClass().getName() + ">";
              } else {
                key = null;
              }
            }
            catch (Exception e) {
              showErrorDialog(Messages.getString("Error_while_examining_DnDGroupingPanel_"), e);
              key = null;
            }
          } else if ((c instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController)) {
            Property property = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController)c).getProperty();
            edu.cmu.cs.stage3.alice.core.Element element = property.getOwner();
            key = key + ":elementTile<" + element.getKey(world) + ">";
            key = key + ":property<" + property.getName() + ">";
          }
          else if (((c instanceof JLabel)) && (((JLabel)c).getText().equals("more..."))) {
            if ((c.getParent().getParent().getParent() instanceof FormattedElementViewController)) {
              FormattedElementViewController vc = 
                (FormattedElementViewController)c.getParent().getParent().getParent();
              edu.cmu.cs.stage3.alice.core.Element element = vc.getElement();
              key = key + ":elementTile<" + element.getKey(world) + ">";
              key = key + ":more";
            } else {
              key = null;
            }
          }
        } else {
          key = null;
        }
      }
    } else if (componentMap.containsValue(c)) {
      for (Iterator iter = componentMap.keySet().iterator(); iter.hasNext();) {
        String k = (String)iter.next();
        if (c.equals(componentMap.get(k))) {
          key = k;
          break;
        }
      }
    }
    
    return key;
  }
  
  private Image getComponentImage(Component c) {
    Rectangle bounds = c.getBounds();
    java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(width, height, 2);
    java.awt.Graphics2D g = image.createGraphics();
    c.paintAll(g);
    return image;
  }
  
  public Image getImageForID(String id) {
    Rectangle r = null;
    Image image = null;
    StringTokenizer st = new StringTokenizer(id, ":", false);
    if (st.hasMoreTokens()) {
      String token = st.nextToken();
      String prefix = AuthoringToolResources.getPrefix(token);
      String spec = AuthoringToolResources.getSpecifier(token);
      if (prefix.equals("objectTree")) {
        if (spec != null) {
          edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
          if (element != null) {
            JTree tree = jAliceFrame.worldTreeComponent.worldTree;
            WorldTreeModel worldTreeModel = (WorldTreeModel)tree.getModel();
            r = tree.getPathBounds(new javax.swing.tree.TreePath(worldTreeModel.getPath(element)));
            if ((r != null) && (!worldTreeModel.isLeaf(element))) {
              x -= 15;
              width += 15;
            }
            if (r != null) {
              r = SwingUtilities.convertRectangle(tree, r, jAliceFrame.getGlassPane());
            }
          }
        } else {
          r = jAliceFrame.worldTreeComponent.getBounds();
          r = SwingUtilities.convertRectangle(jAliceFrame.worldTreeComponent.getParent(), r, jAliceFrame.getGlassPane());
        }
      } else if (prefix.equals("clipboard")) {
        if (spec != null) {
          try {
            int index = Integer.parseInt(spec);
            if (index <= -1) return image;
            Component c = jAliceFrame.clipboardPanel.getComponent(index);
            if (c == null) return image;
            image = getComponentImage(c);

          }
          catch (Exception localException1) {}
        }
      }
      else if (prefix.equals("sceneEditor")) {
        if (st.hasMoreTokens()) {
          token = st.nextToken();
          if (st.hasMoreTokens()) {
            token = st.nextToken();
            prefix = AuthoringToolResources.getPrefix(token);
            spec = AuthoringToolResources.getSpecifier(token);
            if (prefix.equals("galleryViewer")) {
              jAliceFrame.sceneEditor.getGalleryViewer().setDirectory(spec);
              if (st.hasMoreTokens()) {
                token = st.nextToken();
                prefix = AuthoringToolResources.getPrefix(token);
                spec = AuthoringToolResources.getSpecifier(token);
                if (prefix.equals("button")) {
                  Component c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
                  if (c != null) {
                    image = getComponentImage(c);
                  }
                } else if (prefix.equals("galleryObject")) {
                  Component c = AuthoringToolResources.findGalleryObject(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
                  if (c != null) {
                    image = getComponentImage(c);
                  }
                }
              }
            } else {
              Component c = jAliceFrame.sceneEditor.getComponentForId(token);
              if ((c != null) && (isComponentVisible((JComponent)c))) {
                image = getComponentImage(c);
              }
            }
          }
        }
      } else if (prefix.equals("details")) {
        if (spec != null) {
          edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
          if (jAliceFrame.dragFromComponent.getElement().equals(element)) {
            if (st.hasMoreTokens()) {
              token = st.nextToken();
              prefix = AuthoringToolResources.getPrefix(token);
              spec = AuthoringToolResources.getSpecifier(token);
              if (prefix.equals("viewController")) {
                if (st.hasMoreTokens()) {
                  token = st.nextToken();
                  prefix = AuthoringToolResources.getPrefix(token);
                  spec = AuthoringToolResources.getSpecifier(token);
                  Component c = jAliceFrame.dragFromComponent.getPropertyViewComponentForKey(token);
                  if ((c != null) && (isComponentVisible((JComponent)c))) {
                    image = getComponentImage(c);
                  }
                }
              } else {
                Component c = jAliceFrame.dragFromComponent.getComponentForKey(token);
                if ((c != null) && (isComponentVisible((JComponent)c))) {
                  image = getComponentImage(c);
                }
              }
            } else {
              image = getComponentImage(jAliceFrame.dragFromComponent);
            }
          }
        } else {
          image = getComponentImage(jAliceFrame.dragFromComponent);
        }
      } else if (prefix.equals("behaviors")) {
        if (st.hasMoreTokens()) {
          token = st.nextToken();
          prefix = AuthoringToolResources.getPrefix(token);
          spec = AuthoringToolResources.getSpecifier(token);
          if (prefix.equals("createNewEventButton")) {
            Component c = AuthoringToolResources.findButton(jAliceFrame.behaviorGroupsEditor, "create new event");
            if (c != null) {
              image = getComponentImage(c);
            }
          } else if ((prefix.equals("elementTile")) && (spec != null)) {
            edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
            if (element != null) {
              if (st.hasMoreTokens()) {
                token = st.nextToken();
                prefix = AuthoringToolResources.getPrefix(token);
                spec = AuthoringToolResources.getSpecifier(token);
                if ((prefix.equals("property")) && (spec != null)) {
                  Component c = AuthoringToolResources.findPropertyViewController(jAliceFrame.behaviorGroupsEditor, element, spec);
                  if (c != null) {
                    image = getComponentImage(c);
                  }
                } else if (prefix.equals("more")) {
                  Component dndPanel = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
                  if ((dndPanel instanceof FormattedElementViewController)) {
                    Component moreTile = ((FormattedElementViewController)dndPanel).getMoreTile();
                    if ((moreTile != null) && (moreTile.isShowing())) {
                      image = getComponentImage(moreTile);
                    }
                  }
                }
              } else {
                Component c = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
                if (c != null) {
                  image = getComponentImage(c);
                }
              }
            }
          }
        }
      } else if (prefix.equals("editors")) {
        if (st.hasMoreTokens()) {
          token = st.nextToken();
          prefix = AuthoringToolResources.getPrefix(token);
          spec = AuthoringToolResources.getSpecifier(token);
          if (prefix.equals("element")) {
            edu.cmu.cs.stage3.alice.core.Element elementBeingEdited = world.getDescendantKeyed(spec);
            if (st.hasMoreTokens()) {
              if ((jAliceFrame.tabbedEditorComponent.getObjectBeingEdited() != null) && (jAliceFrame.tabbedEditorComponent.getObjectBeingEdited().equals(elementBeingEdited)))
              {
                Container container = (Container)jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentAt(jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited));
                token = st.nextToken();
                prefix = AuthoringToolResources.getPrefix(token);
                spec = AuthoringToolResources.getSpecifier(token);
                if (prefix.equals("button")) {
                  Component c = AuthoringToolResources.findButton(container, spec);
                  if (c != null) {
                    image = getComponentImage(c);
                  }
                } else if ((prefix.equals("elementTile")) && (spec != null)) {
                  edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
                  if (element != null) {
                    if (st.hasMoreTokens()) {
                      token = st.nextToken();
                      prefix = AuthoringToolResources.getPrefix(token);
                      spec = AuthoringToolResources.getSpecifier(token);
                      if ((prefix.equals("property")) && (spec != null)) {
                        Component c = AuthoringToolResources.findPropertyViewController(container, element, spec);
                        if (c != null) {
                          image = getComponentImage(c);
                        }
                      } else if (prefix.equals("more")) {
                        Component dndPanel = AuthoringToolResources.findElementDnDPanel(container, element);
                        if ((dndPanel instanceof FormattedElementViewController)) {
                          Component moreTile = ((FormattedElementViewController)dndPanel).getMoreTile();
                          if ((moreTile != null) && (moreTile.isShowing())) {
                            image = getComponentImage(moreTile);
                          }
                        }
                      }
                    } else {
                      Component c = AuthoringToolResources.findElementDnDPanel(container, element);
                      if (c != null) {
                        image = getComponentImage(c);
                      }
                    }
                  }
                } else if ((prefix.equals("elementPrototypeTile")) && (spec != null)) {
                  try {
                    Class elementClass = Class.forName(spec);
                    if (elementClass == null) return image;
                    Component c = AuthoringToolResources.findPrototypeDnDPanel(container, elementClass);
                    if (c == null) return image;
                    image = getComponentImage(c);
                  }
                  catch (Exception e)
                  {
                    showErrorDialog(Messages.getString("Error_while_looking_for_ProtoypeDnDPanel_using_class_") + spec, e);
                  }
                }
              }
            } else {
              int tabIndex = jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited);
              if ((tabIndex >= 0) && (tabIndex < jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentCount())) {
                r = jAliceFrame.tabbedEditorComponent.tabbedPane.getUI().getTabBounds(jAliceFrame.tabbedEditorComponent.tabbedPane, tabIndex);
                r = SwingUtilities.convertRectangle(jAliceFrame.tabbedEditorComponent.tabbedPane, r, jAliceFrame.getGlassPane());
              }
            }
          }
        } else {
          r = jAliceFrame.tabbedEditorComponent.getBounds();
          r = SwingUtilities.convertRectangle(jAliceFrame.tabbedEditorComponent.getParent(), r, jAliceFrame.getGlassPane());
        }
      } else if (componentMap.containsKey(prefix)) {
        Component c = (Component)componentMap.get(prefix);
        if (c != null) {
          image = getComponentImage(c);
        }
      }
    }
    return image;
  }
  
  public Rectangle getBoxForID(String id) throws edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException {
    Rectangle r = null;
    StringTokenizer st = new StringTokenizer(id, ":", false);
    if (st.hasMoreTokens()) {
      String token = st.nextToken();
      String prefix = AuthoringToolResources.getPrefix(token);
      String spec = AuthoringToolResources.getSpecifier(token);
      if (prefix.equals("objectTree")) {
        if (spec != null) {
          edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
          if (element != null) {
            JTree tree = jAliceFrame.worldTreeComponent.worldTree;
            WorldTreeModel worldTreeModel = (WorldTreeModel)tree.getModel();
            r = tree.getPathBounds(new javax.swing.tree.TreePath(worldTreeModel.getPath(element)));
            if ((r != null) && (!worldTreeModel.isLeaf(element))) {
              x -= 15;
              width += 15;
            }
            if (r != null) {
              r = SwingUtilities.convertRectangle(tree, r, jAliceFrame.getGlassPane());
            }
          }
        } else {
          r = jAliceFrame.worldTreeComponent.getBounds();
          r = SwingUtilities.convertRectangle(jAliceFrame.worldTreeComponent.getParent(), r, jAliceFrame.getGlassPane());
        }
      } else if (prefix.equals("clipboard")) {
        if (spec != null) {
          try {
            int index = Integer.parseInt(spec);
            if (index <= -1) break label2283;
            Component c = jAliceFrame.clipboardPanel.getComponent(index);
            if (c == null) break label2283;
            r = c.getBounds();
            r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());

          }
          catch (Exception localException1) {}
        }
        
      }
      else if (prefix.equals("sceneEditor")) {
        if (st.hasMoreTokens()) {
          token = st.nextToken();
          if (st.hasMoreTokens()) {
            token = st.nextToken();
            prefix = AuthoringToolResources.getPrefix(token);
            spec = AuthoringToolResources.getSpecifier(token);
            if (prefix.equals("galleryViewer")) {
              jAliceFrame.sceneEditor.getGalleryViewer().setDirectory(spec);
              if (st.hasMoreTokens()) {
                token = st.nextToken();
                prefix = AuthoringToolResources.getPrefix(token);
                spec = AuthoringToolResources.getSpecifier(token);
                if (prefix.equals("button")) {
                  Component c = null;
                  if ((spec.contains("(Core)")) || (spec.contains("(" + AikMin.locale + ")"))) {
                    c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
                  } else {
                    c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec + " (Core)");
                  }
                  if (c == null) c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec + " (" + AikMin.locale + ")");
                  if (c != null) {
                    r = c.getBounds();
                    r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
                  }
                } else if (prefix.equals("galleryObject")) {
                  Component c = AuthoringToolResources.findGalleryObject(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
                  if (c != null) {
                    r = c.getBounds();
                    r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
                  }
                }
              } else {
                r = jAliceFrame.sceneEditor.getGalleryViewer().getBounds();
                r = SwingUtilities.convertRectangle(jAliceFrame.sceneEditor.getGalleryViewer().getParent(), r, jAliceFrame.getGlassPane());
              }
            } else {
              Component c = jAliceFrame.sceneEditor.getComponentForId(token);
              if ((c != null) && (isComponentVisible((JComponent)c))) {
                r = c.getBounds();
                r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
              }
            }
          } else {
            r = jAliceFrame.sceneEditor.getBounds();
            r = SwingUtilities.convertRectangle(jAliceFrame.sceneEditor.getParent(), r, jAliceFrame.getGlassPane());
          }
        } else {
          r = jAliceFrame.sceneEditor.getBounds();
          r = SwingUtilities.convertRectangle(jAliceFrame.sceneEditor.getParent(), r, jAliceFrame.getGlassPane());
        }
      } else if (prefix.equals("details")) {
        if (spec != null) {
          edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
          if (jAliceFrame.dragFromComponent.getElement().equals(element)) {
            if (st.hasMoreTokens()) {
              token = st.nextToken();
              prefix = AuthoringToolResources.getPrefix(token);
              spec = AuthoringToolResources.getSpecifier(token);
              if (prefix.equals("tab")) {
                int tabIndex = Integer.parseInt(spec);
                r = jAliceFrame.dragFromComponent.tabbedPane.getUI().getTabBounds(jAliceFrame.dragFromComponent.tabbedPane, tabIndex);
                r = SwingUtilities.convertRectangle(jAliceFrame.dragFromComponent.tabbedPane, r, jAliceFrame.getGlassPane());
                return r; }
              if (prefix.equals("viewController")) {
                if (st.hasMoreTokens()) {
                  token = st.nextToken();
                  prefix = AuthoringToolResources.getPrefix(token);
                  spec = AuthoringToolResources.getSpecifier(token);
                  Component c = jAliceFrame.dragFromComponent.getPropertyViewComponentForKey(token);
                  if ((c != null) && (isComponentVisible((JComponent)c))) {
                    r = c.getBounds();
                    r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
                  }
                }
              } else {
                Component c = jAliceFrame.dragFromComponent.getComponentForKey(token);
                if ((c != null) && (isComponentVisible((JComponent)c))) {
                  r = c.getBounds();
                  r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
                }
              }
            } else {
              r = jAliceFrame.dragFromComponent.getBounds();
              r = SwingUtilities.convertRectangle(jAliceFrame.dragFromComponent.getParent(), r, jAliceFrame.getGlassPane());
            }
          }
        } else {
          r = jAliceFrame.dragFromComponent.getBounds();
          r = SwingUtilities.convertRectangle(jAliceFrame.dragFromComponent.getParent(), r, jAliceFrame.getGlassPane());
        }
      } else if (prefix.equals("behaviors")) {
        if (st.hasMoreTokens()) {
          token = st.nextToken();
          prefix = AuthoringToolResources.getPrefix(token);
          spec = AuthoringToolResources.getSpecifier(token);
          if (prefix.equals("createNewEventButton")) {
            Component c = AuthoringToolResources.findButton(jAliceFrame.behaviorGroupsEditor, "create new event");
            if (c != null) {
              r = c.getBounds();
              r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
            }
          } else if ((prefix.equals("elementTile")) && (spec != null)) {
            edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
            if (element != null) {
              if (st.hasMoreTokens()) {
                token = st.nextToken();
                prefix = AuthoringToolResources.getPrefix(token);
                spec = AuthoringToolResources.getSpecifier(token);
                if ((prefix.equals("property")) && (spec != null)) {
                  Component c = AuthoringToolResources.findPropertyViewController(jAliceFrame.behaviorGroupsEditor, element, spec);
                  if (c != null) {
                    r = c.getBounds();
                    r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
                  }
                } else if (prefix.equals("more")) {
                  Component dndPanel = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
                  if ((dndPanel instanceof FormattedElementViewController)) {
                    Component moreTile = ((FormattedElementViewController)dndPanel).getMoreTile();
                    if ((moreTile != null) && (moreTile.isShowing())) {
                      r = moreTile.getBounds();
                      r = SwingUtilities.convertRectangle(moreTile.getParent(), r, jAliceFrame.getGlassPane());
                    }
                  }
                }
              } else {
                Component c = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
                if (c != null) {
                  r = c.getBounds();
                  r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
                }
              }
            }
          }
        } else {
          r = jAliceFrame.behaviorGroupsEditor.getBounds();
          r = SwingUtilities.convertRectangle(jAliceFrame.behaviorGroupsEditor.getParent(), r, jAliceFrame.getGlassPane());
        }
      } else if (prefix.equals("editors")) {
        if (st.hasMoreTokens()) {
          token = st.nextToken();
          prefix = AuthoringToolResources.getPrefix(token);
          spec = AuthoringToolResources.getSpecifier(token);
          if (prefix.equals("element")) {
            edu.cmu.cs.stage3.alice.core.Element elementBeingEdited = world.getDescendantKeyed(spec);
            if (st.hasMoreTokens()) {
              if ((jAliceFrame.tabbedEditorComponent.getObjectBeingEdited() != null) && (jAliceFrame.tabbedEditorComponent.getObjectBeingEdited().equals(elementBeingEdited)))
              {
                Container container = (Container)jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentAt(jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited));
                token = st.nextToken();
                prefix = AuthoringToolResources.getPrefix(token);
                spec = AuthoringToolResources.getSpecifier(token);
                if (prefix.equals("button")) {
                  Component c = AuthoringToolResources.findButton(container, spec);
                  if (c != null) {
                    r = c.getBounds();
                    r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
                  }
                } else if ((prefix.equals("elementTile")) && (spec != null)) {
                  edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
                  if (element != null) {
                    if (st.hasMoreTokens()) {
                      token = st.nextToken();
                      prefix = AuthoringToolResources.getPrefix(token);
                      spec = AuthoringToolResources.getSpecifier(token);
                      if ((prefix.equals("property")) && (spec != null)) {
                        Component c = AuthoringToolResources.findPropertyViewController(container, element, spec);
                        if (c != null) {
                          r = c.getBounds();
                          r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
                        }
                      } else if (prefix.equals("more")) {
                        Component dndPanel = AuthoringToolResources.findElementDnDPanel(container, element);
                        if ((dndPanel instanceof FormattedElementViewController)) {
                          Component moreTile = ((FormattedElementViewController)dndPanel).getMoreTile();
                          if ((moreTile != null) && (moreTile.isShowing())) {
                            r = moreTile.getBounds();
                            r = SwingUtilities.convertRectangle(moreTile.getParent(), r, jAliceFrame.getGlassPane());
                          }
                        }
                      }
                    } else {
                      Component c = AuthoringToolResources.findElementDnDPanel(container, element);
                      if (c != null) {
                        r = c.getBounds();
                        r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
                      }
                    }
                  }
                } else if ((prefix.equals("elementPrototypeTile")) && (spec != null)) {
                  try {
                    Class elementClass = Class.forName(spec);
                    if (elementClass == null) break label2283;
                    Component c = AuthoringToolResources.findPrototypeDnDPanel(container, elementClass);
                    if (c == null) break label2283;
                    r = c.getBounds();
                    r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
                  }
                  catch (Exception e)
                  {
                    showErrorDialog(Messages.getString("Error_while_looking_for_ProtoypeDnDPanel_using_class_") + spec, e);
                  }
                }
              }
            } else {
              int tabIndex = jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited);
              if ((tabIndex >= 0) && (tabIndex < jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentCount())) {
                r = jAliceFrame.tabbedEditorComponent.tabbedPane.getUI().getTabBounds(jAliceFrame.tabbedEditorComponent.tabbedPane, tabIndex);
                r = SwingUtilities.convertRectangle(jAliceFrame.tabbedEditorComponent.tabbedPane, r, jAliceFrame.getGlassPane());
              }
            }
          }
        } else {
          r = jAliceFrame.tabbedEditorComponent.getBounds();
          r = SwingUtilities.convertRectangle(jAliceFrame.tabbedEditorComponent.getParent(), r, jAliceFrame.getGlassPane());
        }
      } else if (componentMap.containsKey(prefix)) {
        Component c = (Component)componentMap.get(prefix);
        if (c != null) {
          r = c.getBounds();
          r = SwingUtilities.convertRectangle(c.getParent(), r, jAliceFrame.getGlassPane());
        }
      }
    }
    label2283:
    if (r == null) {
      throw new edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException(id);
    }
    
    return r;
  }
  
  public boolean isComponentVisible(JComponent c) {
    if (c == null) {
      return false;
    }
    Rectangle visibleR = c.getVisibleRect();
    Rectangle ourRect = c.getBounds();
    return (width == width) && (height == height);
  }
  








  public boolean isIDVisible(String id)
    throws edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException
  {
    StringTokenizer st = new StringTokenizer(id, ":", false);
    
    if (st.hasMoreTokens()) {
      String token = st.nextToken();
      String prefix = AuthoringToolResources.getPrefix(token);
      String spec = AuthoringToolResources.getSpecifier(token);
      if (prefix.equals("objectTree")) {
        if (spec != null) {
          edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
          if (element != null) {
            JTree tree = jAliceFrame.worldTreeComponent.worldTree;
            WorldTreeModel worldTreeModel = (WorldTreeModel)tree.getModel();
            Rectangle r = tree.getPathBounds(new javax.swing.tree.TreePath(worldTreeModel.getPath(element)));
            if ((r != null) && (tree.getVisibleRect().contains(r))) {
              return true;
            }
            return false;
          }
        }
      } else {
        if (prefix.equals("clipboard")) {
          if (spec != null) {
            try {
              int index = Integer.parseInt(spec);
              if (index > -1) {
                Component c = jAliceFrame.clipboardPanel.getComponent(index);
                if ((c != null) && (isComponentVisible((JComponent)c))) {
                  return true;
                }
                return false;
              }
              
              return false;
            }
            catch (Exception e) {
              return false;
            }
          }
          return false;
        }
        
        if (prefix.equals("sceneEditor")) {
          if (st.hasMoreTokens()) {
            token = st.nextToken();
            if (st.hasMoreTokens()) {
              token = st.nextToken();
              prefix = AuthoringToolResources.getPrefix(token);
              spec = AuthoringToolResources.getSpecifier(token);
              if (prefix.equals("galleryViewer")) {
                if ((jAliceFrame.sceneEditor.getGalleryViewer().getDirectory().equals(spec)) || 
                  (jAliceFrame.sceneEditor.getGalleryViewer().getDirectory().replace(" (Core)", "").equals(spec)) || 
                  (jAliceFrame.sceneEditor.getGalleryViewer().getDirectory().replace(" (" + AikMin.locale + ")", "").equals(spec))) {
                  if (st.hasMoreTokens()) {
                    token = st.nextToken();
                    prefix = AuthoringToolResources.getPrefix(token);
                    spec = AuthoringToolResources.getSpecifier(token);
                    if (prefix.equals("button")) {
                      Component c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec + " (Core)");
                      if (c == null) c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec + " (" + AikMin.locale + ")");
                      if ((c != null) && (isComponentVisible((JComponent)c))) {
                        return true;
                      }
                      return false;
                    }
                    if (prefix.equals("galleryObject")) {
                      Component c = AuthoringToolResources.findGalleryObject(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
                      if ((c != null) && (isComponentVisible((JComponent)c))) {
                        return true;
                      }
                      return false;
                    }
                  }
                  else {
                    if (jAliceFrame.sceneEditor.getGalleryViewer().isShowing()) {
                      return true;
                    }
                    return false;
                  }
                }
                else {
                  return false;
                }
              } else {
                Component c = jAliceFrame.sceneEditor.getComponentForId(token);
                if ((c != null) && (isComponentVisible((JComponent)c))) {
                  return true;
                }
                return false;
              }
            }
          }
        } else {
          if (prefix.equals("details")) {
            if (spec == null) {
              spec = "";
            }
            edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
            if (jAliceFrame.dragFromComponent.getElement().equals(element)) {
              if (st.hasMoreTokens()) {
                token = st.nextToken();
                prefix = AuthoringToolResources.getPrefix(token);
                spec = AuthoringToolResources.getSpecifier(token);
                if (prefix.equals("tab")) {
                  return true;
                }
                Component c = jAliceFrame.dragFromComponent.getComponentForKey(token);
                
                if (c != null) {
                  Rectangle boundss = c.getBounds();
                  boundss = SwingUtilities.convertRectangle(c.getParent(), boundss, jAliceFrame);
                }
                

                if ((c != null) && (isComponentVisible((JComponent)c))) {
                  Rectangle bounds = c.getBounds();
                  SwingUtilities.convertRectangle(c.getParent(), bounds, jAliceFrame.dragFromComponent);
                  if (jAliceFrame.dragFromComponent.getVisibleRect().contains(bounds))
                  {
                    return true;
                  }
                  return false;
                }
                
                return false;
              }
              

              return false;
            }
            

            return false;
          }
          if (prefix.equals("behaviors")) {
            if (st.hasMoreTokens()) {
              token = st.nextToken();
              prefix = AuthoringToolResources.getPrefix(token);
              spec = AuthoringToolResources.getSpecifier(token);
              if (prefix.equals("createNewBehaviorButton")) {
                Component c = AuthoringToolResources.findButton(jAliceFrame.behaviorGroupsEditor, "create new behavior");
                if ((c != null) && (c.isShowing())) {
                  return true;
                }
                return false;
              }
              if ((prefix.equals("elementTile")) && (spec != null)) {
                edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
                if (element != null) {
                  if (st.hasMoreTokens()) {
                    token = st.nextToken();
                    prefix = AuthoringToolResources.getPrefix(token);
                    spec = AuthoringToolResources.getSpecifier(token);
                    if ((prefix.equals("property")) && (spec != null)) {
                      Component c = AuthoringToolResources.findPropertyViewController(jAliceFrame.behaviorGroupsEditor, element, spec);
                      if ((c != null) && (c.isShowing())) {
                        Rectangle bounds = c.getBounds();
                        bounds = SwingUtilities.convertRectangle(c.getParent(), bounds, jAliceFrame.behaviorGroupsEditor.getScrollPane());
                        if (jAliceFrame.behaviorGroupsEditor.getScrollPaneVisibleRect().contains(bounds)) {
                          return true;
                        }
                        return false;
                      }
                      



                      return false;
                    }
                    if (prefix.equals("more")) {
                      Component dndPanel = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
                      if ((dndPanel instanceof FormattedElementViewController)) {
                        Component moreTile = ((FormattedElementViewController)dndPanel).getMoreTile();
                        if ((moreTile != null) && (moreTile.isShowing())) {
                          Rectangle bounds = moreTile.getBounds();
                          SwingUtilities.convertRectangle(moreTile.getParent(), bounds, jAliceFrame.behaviorGroupsEditor.getScrollPane());
                          if (jAliceFrame.behaviorGroupsEditor.getScrollPaneVisibleRect().contains(bounds)) {
                            return true;
                          }
                          return false;
                        }
                        



                        return false;
                      }
                    }
                  }
                  else {
                    Component c = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
                    boolean visibleRectNotEmpty = true;
                    if ((c instanceof JComponent)) {
                      visibleRectNotEmpty = !((JComponent)c).getVisibleRect().isEmpty();
                    }
                    if ((c != null) && (c.isShowing()) && (visibleRectNotEmpty)) {
                      Rectangle bounds = c.getBounds();
                      SwingUtilities.convertRectangle(c.getParent(), bounds, jAliceFrame.behaviorGroupsEditor.getScrollPane());
                      if (jAliceFrame.behaviorGroupsEditor.getScrollPaneVisibleRect().contains(bounds)) {
                        return true;
                      }
                      return false;
                    }
                    

                    return false;
                  }
                }
              }
            }
          }
          else if (prefix.equals("editors")) {
            if (st.hasMoreTokens()) {
              token = st.nextToken();
              prefix = AuthoringToolResources.getPrefix(token);
              spec = AuthoringToolResources.getSpecifier(token);
              if (prefix.equals("element")) {
                edu.cmu.cs.stage3.alice.core.Element elementBeingEdited = world.getDescendantKeyed(spec);
                if (st.hasMoreTokens()) {
                  if ((jAliceFrame.tabbedEditorComponent.getObjectBeingEdited() != null) && (jAliceFrame.tabbedEditorComponent.getObjectBeingEdited().equals(elementBeingEdited)))
                  {
                    Container container = (Container)jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentAt(jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited));
                    token = st.nextToken();
                    prefix = AuthoringToolResources.getPrefix(token);
                    spec = AuthoringToolResources.getSpecifier(token);
                    if (prefix.equals("button")) {
                      Component c = AuthoringToolResources.findButton(container, spec);
                      if ((c != null) && (c.isShowing())) {
                        return true;
                      }
                      return false;
                    }
                    if ((prefix.equals("elementTile")) && (spec != null)) {
                      edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
                      if (element != null) {
                        if (st.hasMoreTokens()) {
                          token = st.nextToken();
                          prefix = AuthoringToolResources.getPrefix(token);
                          spec = AuthoringToolResources.getSpecifier(token);
                          if ((prefix.equals("property")) && (spec != null)) {
                            Component c = AuthoringToolResources.findPropertyViewController(container, element, spec);
                            if ((c != null) && (isComponentVisible((JComponent)c))) {
                              return true;
                            }
                            return false;
                          }
                          if (prefix.equals("more")) {
                            Component dndPanel = AuthoringToolResources.findElementDnDPanel(container, element);
                            if ((dndPanel instanceof FormattedElementViewController)) {
                              Component moreTile = ((FormattedElementViewController)dndPanel).getMoreTile();
                              if ((moreTile != null) && (moreTile.isShowing())) {
                                return true;
                              }
                              return false;
                            }
                          }
                        }
                        else {
                          Component c = AuthoringToolResources.findElementDnDPanel(container, element);
                          if ((c != null) && (isComponentVisible((JComponent)c))) {
                            return true;
                          }
                          return false;
                        }
                      }
                    }
                    else if ((prefix.equals("elementPrototypeTile")) && (spec != null)) {
                      try {
                        Class elementClass = Class.forName(spec);
                        if (elementClass == null) break label1813;
                        Component c = AuthoringToolResources.findPrototypeDnDPanel(container, elementClass);
                        if ((c != null) && (c.isShowing())) {
                          return true;
                        }
                        return false;
                      }
                      catch (Exception e)
                      {
                        showErrorDialog(Messages.getString("Error_while_looking_for_ProtoypeDnDPanel_using_class_") + spec, e);
                      }
                    }
                  } else {
                    return false;
                  }
                } else {
                  int tabIndex = jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited);
                  if ((tabIndex >= 0) && (tabIndex < jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentCount())) {
                    return true;
                  }
                  return false;
                }
              }
            }
          }
          else if (componentMap.containsKey(prefix)) {
            Component c = (Component)componentMap.get(prefix);
            if ((c != null) && (c.isShowing())) {
              return true;
            }
            return false;
          }
        }
      } }
    label1813:
    return true;
  }
  
  public void makeIDVisible(String id) throws edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException {
    StringTokenizer st = new StringTokenizer(id, ":", false);
    
    if (st.hasMoreTokens()) {
      String token = st.nextToken();
      String prefix = AuthoringToolResources.getPrefix(token);
      String spec = AuthoringToolResources.getSpecifier(token);
      
      if (prefix.equals("objectTree")) {
        if (spec != null) {
          edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
          if (element != null) {
            JTree tree = jAliceFrame.worldTreeComponent.worldTree;
            WorldTreeModel worldTreeModel = (WorldTreeModel)tree.getModel();
            tree.scrollPathToVisible(new javax.swing.tree.TreePath(worldTreeModel.getPath(element)));
          }
        }
      } else if (!prefix.equals("clipboard"))
      {

        if (prefix.equals("sceneEditor")) {
          if (spec.equals("large")) {
            jAliceFrame.sceneEditor.setGuiMode(SceneEditor.LARGE_MODE);
          }
          else if (spec.equals("small")) {
            jAliceFrame.sceneEditor.setGuiMode(SceneEditor.SMALL_MODE);
          }
          
          if (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.equals("singleView")) {
              jAliceFrame.sceneEditor.setViewMode(0);
            }
            else if (token.equals("quadView")) {
              jAliceFrame.sceneEditor.setViewMode(1);
            }
            

            if (st.hasMoreTokens()) {
              token = st.nextToken();
              prefix = AuthoringToolResources.getPrefix(token);
              spec = AuthoringToolResources.getSpecifier(token);
              if (prefix.equals("galleryViewer"))
              {

                if (st.hasMoreTokens()) {
                  token = st.nextToken();
                  prefix = AuthoringToolResources.getPrefix(token);
                  spec = AuthoringToolResources.getSpecifier(token);
                  if (prefix.equals("button")) {
                    Component c = AuthoringToolResources.findButton(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
                    if ((c != null) && (c.isShowing()) && ((c instanceof JComponent))) {
                      ((JComponent)c).scrollRectToVisible(c.getBounds());
                    }
                  } else if (prefix.equals("galleryObject")) {
                    Component c = AuthoringToolResources.findGalleryObject(jAliceFrame.sceneEditor.getGalleryViewer(), spec);
                    if ((c != null) && (c.isShowing()) && ((c instanceof JComponent))) {
                      ((JComponent)c).scrollRectToVisible(c.getBounds());
                    }
                  }
                }
              }
              else
              {
                Component c = jAliceFrame.sceneEditor.getComponentForId(token);
                if ((c != null) && (c.isShowing()) && ((c instanceof JComponent))) {
                  ((JComponent)c).scrollRectToVisible(c.getBounds());
                }
              }
            }
          }
        } else if (prefix.equals("details")) {
          if (spec == null) {
            spec = "";
          }
          edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
          if (!jAliceFrame.dragFromComponent.getElement().equals(element)) {
            jAliceFrame.dragFromComponent.setElement(element);
          }
          if ((jAliceFrame.dragFromComponent.getElement().equals(element)) && 
            (st.hasMoreTokens())) {
            token = st.nextToken();
            prefix = AuthoringToolResources.getPrefix(token);
            spec = AuthoringToolResources.getSpecifier(token);
            
            if (!prefix.equals("tab")) {
              boolean isViewController = false;
              if ((prefix.equals("viewController")) && 
                (st.hasMoreTokens())) {
                token = st.nextToken();
                prefix = AuthoringToolResources.getPrefix(token);
                spec = AuthoringToolResources.getSpecifier(token);
                isViewController = true;
              }
              
              if ((prefix.equals("property")) || (prefix.equals("variable")) || (prefix.equals("textureMap")) || (prefix.equals("sound")) || (prefix.equals("other"))) {
                jAliceFrame.dragFromComponent.selectTab(0);
              }
              if ((prefix.equals("userDefinedResponse")) || (prefix.equals("responsePrototype"))) {
                jAliceFrame.dragFromComponent.selectTab(1);
              }
              if ((prefix.equals("userDefinedQuestion")) || (prefix.equals("questionPrototype"))) {
                jAliceFrame.dragFromComponent.selectTab(2);
              }
              Component c = null;
              if (isViewController) {
                c = jAliceFrame.dragFromComponent.getPropertyViewComponentForKey(token);
              } else {
                c = jAliceFrame.dragFromComponent.getComponentForKey(token);
              }
              if ((c != null) && ((c.getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel))) {
                edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel ep = (edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel)c.getParent();
                ep.setExpanded(true);
              }
              if ((c != null) && (c.isShowing()) && ((c instanceof JComponent))) {
                ((JComponent)c).scrollRectToVisible(c.getBounds());
              }
            }
          }
        }
        else if (prefix.equals("behaviors")) {
          if (st.hasMoreTokens()) {
            token = st.nextToken();
            prefix = AuthoringToolResources.getPrefix(token);
            spec = AuthoringToolResources.getSpecifier(token);
            if ((prefix.equals("elementTile")) && (spec != null)) {
              edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
              if (element != null) {
                if (st.hasMoreTokens()) {
                  token = st.nextToken();
                  prefix = AuthoringToolResources.getPrefix(token);
                  spec = AuthoringToolResources.getSpecifier(token);
                  if ((prefix.equals("property")) && (spec != null)) {
                    Component c = AuthoringToolResources.findPropertyViewController(jAliceFrame.behaviorGroupsEditor, element, spec);
                    if ((c != null) && (c.isShowing()) && ((c instanceof JComponent))) {
                      ((JComponent)c).scrollRectToVisible(c.getBounds());
                    }
                  } else if (prefix.equals("more")) {
                    Component dndPanel = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
                    if ((dndPanel instanceof FormattedElementViewController)) {
                      Component moreTile = ((FormattedElementViewController)dndPanel).getMoreTile();
                      if ((moreTile != null) && (moreTile.isShowing()) && ((moreTile instanceof JComponent))) {
                        ((JComponent)moreTile).scrollRectToVisible(moreTile.getBounds());
                      }
                    }
                  }
                } else {
                  Component c = AuthoringToolResources.findElementDnDPanel(jAliceFrame.behaviorGroupsEditor, element);
                  if ((c != null) && (c.isShowing()) && ((c instanceof JComponent)))
                  {
                    ((JComponent)c).scrollRectToVisible(new Rectangle(0, 0, c.getWidth(), c.getHeight()));
                  }
                }
              }
            }
          }
        } else if (prefix.equals("editors")) {
          if (st.hasMoreTokens()) {
            token = st.nextToken();
            prefix = AuthoringToolResources.getPrefix(token);
            spec = AuthoringToolResources.getSpecifier(token);
            if (prefix.equals("element")) {
              edu.cmu.cs.stage3.alice.core.Element elementBeingEdited = world.getDescendantKeyed(spec);
              if (elementBeingEdited == null) {
                throw new edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException(spec);
              }
              if (st.hasMoreTokens()) {
                if (jAliceFrame.tabbedEditorComponent.getObjectBeingEdited() != elementBeingEdited) {
                  editObject(elementBeingEdited);
                }
                

                Container container = (Container)jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentAt(jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited));
                token = st.nextToken();
                prefix = AuthoringToolResources.getPrefix(token);
                spec = AuthoringToolResources.getSpecifier(token);
                if (prefix.equals("button")) {
                  Component c = AuthoringToolResources.findButton(container, spec);
                  if ((c != null) && (c.isShowing()) && ((c instanceof JComponent))) {
                    ((JComponent)c).scrollRectToVisible(c.getBounds());
                  }
                } else if ((prefix.equals("elementTile")) && (spec != null)) {
                  edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(spec);
                  if (element != null) {
                    if (st.hasMoreTokens()) {
                      token = st.nextToken();
                      prefix = AuthoringToolResources.getPrefix(token);
                      spec = AuthoringToolResources.getSpecifier(token);
                      if ((prefix.equals("property")) && (spec != null)) {
                        Component c = AuthoringToolResources.findPropertyViewController(container, element, spec);
                        if ((c != null) && (c.isShowing()) && ((c instanceof JComponent))) {
                          ((JComponent)c).scrollRectToVisible(c.getBounds());
                        }
                      } else if (prefix.equals("more")) {
                        Component dndPanel = AuthoringToolResources.findElementDnDPanel(container, element);
                        if ((dndPanel instanceof FormattedElementViewController)) {
                          Component moreTile = ((FormattedElementViewController)dndPanel).getMoreTile();
                          if ((moreTile != null) && (moreTile.isShowing()) && ((moreTile instanceof JComponent))) {
                            ((JComponent)moreTile).scrollRectToVisible(moreTile.getBounds());
                          }
                        }
                      }
                    } else {
                      Component c = AuthoringToolResources.findElementDnDPanel(container, element);
                      if ((c != null) && (c.isShowing()) && ((c instanceof JComponent))) {
                        ((JComponent)c).scrollRectToVisible(c.getBounds());
                      }
                    }
                  }
                } else if ((prefix.equals("elementPrototypeTile")) && (spec != null)) {
                  try {
                    Class elementClass = Class.forName(spec);
                    if (elementClass == null) return;
                    Component c = AuthoringToolResources.findPrototypeDnDPanel(container, elementClass);
                    if ((c == null) || (!c.isShowing()) || (!(c instanceof JComponent))) return;
                    ((JComponent)c).scrollRectToVisible(c.getBounds());
                  }
                  catch (Exception e)
                  {
                    showErrorDialog(Messages.getString("Error_while_looking_for_ProtoypeDnDPanel_using_class_") + spec, e);
                  }
                }
              } else {
                int tabIndex = jAliceFrame.tabbedEditorComponent.getIndexOfObject(elementBeingEdited);
                if ((tabIndex < 0) || (tabIndex >= jAliceFrame.tabbedEditorComponent.tabbedPane.getComponentCount())) {
                  editObject(elementBeingEdited);
                }
              }
            }
          }
        } else if (componentMap.containsKey(prefix)) {
          Component c = (Component)componentMap.get(prefix);
          if ((c != null) && (c.isShowing()) && ((c instanceof JComponent)))
            ((JComponent)c).scrollRectToVisible(c.getBounds());
        }
      }
    }
  }
  
  public synchronized void makeWayPoint() {
    if (wayPoints.size() > 0) {
      WorldDifferencesCapsule currentWayPoint = (WorldDifferencesCapsule)wayPoints.get(0);
      currentWayPoint.stopListening();
    }
    
    WorldDifferencesCapsule wayPoint = new WorldDifferencesCapsule(this, world);
    wayPoints.add(0, wayPoint);
  }
  
  public synchronized void goToPreviousWayPoint() {
    if (wayPoints.size() > 0) {
      WorldDifferencesCapsule currentWayPoint = (WorldDifferencesCapsule)wayPoints.get(0);
      currentWayPoint.restoreWorld();
      currentWayPoint.dispose();
      wayPoints.remove(0);
    }
    
    if (wayPoints.size() > 0) {
      WorldDifferencesCapsule previousWayPoint = (WorldDifferencesCapsule)wayPoints.get(0);
      previousWayPoint.restoreWorld();
      previousWayPoint.startListening();
    }
  }
  
  public synchronized void clearWayPoints() {
    for (Iterator iter = wayPoints.iterator(); iter.hasNext();) {
      WorldDifferencesCapsule wayPoint = (WorldDifferencesCapsule)iter.next();
      wayPoint.dispose();
    }
    wayPoints.clear();
  }
  
  public boolean doesStateMatch(edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule capsule) {
    if ((capsule instanceof StencilStateCapsule)) {
      StencilStateCapsule stencilStateCapsule = (StencilStateCapsule)capsule;
      
      String[] existantElements = stencilStateCapsule.getExistantElements();
      String[] nonExistantElements = stencilStateCapsule.getNonExistantElements();
      java.util.Set propertyValueKeys = stencilStateCapsule.getPropertyValueKeySet();
      java.util.Set elementPositions = stencilStateCapsule.getElementPositionKeySet();
      

      for (int i = 0; i < existantElements.length; i++) {
        if (world.getDescendantKeyed(existantElements[i]) == null) {
          return false;
        }
      }
      



      for (int i = 0; i < nonExistantElements.length; i++) {
        if (world.getDescendantKeyed(nonExistantElements[i]) != null) {
          return false;
        }
      }
      



      for (Iterator iter = elementPositions.iterator(); iter.hasNext();) {
        String elementKey = (String)iter.next();
        int position = stencilStateCapsule.getElementPosition(elementKey);
        
        edu.cmu.cs.stage3.alice.core.Element element = world.getDescendantKeyed(elementKey);
        if (element != null) {
          int actualPosition = element.getParent().getIndexOfChild(element);
          if ((element instanceof UserDefinedResponse)) {
            Property resp = element.getParent().getPropertyNamed("responses");
            if ((resp instanceof ObjectArrayProperty)) {
              actualPosition = ((ObjectArrayProperty)resp).indexOf(element);
            }
          }
          else if ((element instanceof edu.cmu.cs.stage3.alice.core.Response)) {
            Property resp = element.getParent().getPropertyNamed("componentResponses");
            if ((resp instanceof ObjectArrayProperty)) {
              actualPosition = ((ObjectArrayProperty)resp).indexOf(element);
            }
          }
          else if ((element instanceof edu.cmu.cs.stage3.alice.core.Behavior)) {
            Property resp = element.getParent().getPropertyNamed("behaviors");
            if ((resp instanceof ObjectArrayProperty)) {
              actualPosition = ((ObjectArrayProperty)resp).indexOf(element);
            }
          }
          


          if (position != actualPosition)
          {
            return false;
          }
        }
        else {
          return false;
        }
      }
      


      for (Iterator iter = propertyValueKeys.iterator(); iter.hasNext();) {
        String propertyKey = (String)iter.next();
        String valueRepr = stencilStateCapsule.getPropertyValue(propertyKey);
        int dotIndex = propertyKey.lastIndexOf(".");
        String elementKey = propertyKey.substring(0, dotIndex);
        String propertyName = propertyKey.substring(dotIndex + 1);
        
        edu.cmu.cs.stage3.alice.core.Element propertyOwner = world.getDescendantKeyed(elementKey);
        if (propertyOwner != null)
        {

          if ((propertyOwner instanceof CallToUserDefinedResponse)) {
            Property requiredParams = propertyOwner.getPropertyNamed("requiredActualParameters");
            
            Object udobj = requiredParams.getValue();
            if ((udobj instanceof Variable[])) {
              Variable[] vars = (Variable[])udobj;
              if (vars != null) {
                for (int i = 0; i < vars.length; i++) {
                  if (vars[i].getKey(world).equals(propertyKey)) {
                    String actualValueRepr = AuthoringToolResources.getReprForValue(vars[i].getValue(), true);
                    if (!actualValueRepr.equals(valueRepr)) {
                      return false;
                    }
                  }
                }
              }
            }
          } else {
            Object value = propertyOwner.getPropertyNamed(propertyName).get();
            String actualValueRepr = AuthoringToolResources.getReprForValue(value, true);
            if (actualValueRepr != null) {
              if (!actualValueRepr.equals(valueRepr)) {
                return false;
              }
            } else {
              return false;
            }
          }
        } else {
          return false;
        }
      }
      





      WorldDifferencesCapsule currentWayPoint = (WorldDifferencesCapsule)wayPoints.get(0);
      
      if ((currentWayPoint.otherPropertyChangesMade(propertyValueKeys)) || (currentWayPoint.otherElementsInsertedOrDeleted(existantElements, nonExistantElements)) || (currentWayPoint.otherElementsShifted(elementPositions))) {
        return false;
      }
      
      return true;
    }
    
    return true;
  }
  
  public void handleMouseEvent(MouseEvent ev) {
    Point p = ev.getPoint();
    p = SwingUtilities.convertPoint((Component)ev.getSource(), p, jAliceFrame.getLayeredPane());
    Component newSource = jAliceFrame.getLayeredPane().findComponentAt(p);
    
    if (((newSource instanceof JLabel)) || ((newSource instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPrototypeDnDPanel.Tile))) {
      newSource = newSource.getParent();
    }
    
    switch (ev.getID()) {
    case 506: 
      if (stencilDragging) {
        newSource = dragStartSource;
      }
      break;
    case 501: 
      stencilDragging = true;
      dragStartSource = newSource;
      break;
    case 502: 
      if (stencilDragging) {
        newSource = dragStartSource;
        dragStartSource = null;
      }
      stencilDragging = false;
      break;
    }
    
    




    if (newSource != null) {
      p = SwingUtilities.convertPoint(jAliceFrame.getLayeredPane(), p, newSource);
      MouseEvent newEv = new MouseEvent(newSource, ev.getID(), ev.getWhen(), ev.getModifiers(), x, y, ev.getClickCount(), ev.isPopupTrigger());
      ev.consume();
      newSource.dispatchEvent(newEv);
    }
  }
  
  public void deFocus()
  {
    jAliceFrame.getContentPane().requestFocus();
  }
  
  public void performTask(String task) {
    String prefix = AuthoringToolResources.getPrefix(task);
    String spec = AuthoringToolResources.getSpecifier(task);
    
    if (prefix.equals("loadWorld"))
    {
      boolean askForSave = false;
      loadWorld(spec, askForSave);
    }
  }
  
  public Dimension getScreenSize() {
    return jAliceFrame.getSize();
  }
  

  public void launchTutorial() { launchTutorialFile(null); }
  
  public void launchTutorialFile(File tutorialFile) {
    if (Integer.parseInt(authoringToolConfig.getValue("fontSize")) > 12) {
      DialogManager.showMessageDialog(
        Messages.getString("Alice_is_not_able_to_load_the_tutorial_") + Messages.getString("Your_font_size_is_too_large__You_can_go_to_Edit__Preferences_to_change_the_font_size_"), 
        Messages.getString("Font_size_too_large"), 
        2);
    } else {
      if (tutorialFile == null) {
        tutorialFile = tutorialOne;
      }
      tutorialFile = tutorialFile.getAbsoluteFile();
      showStencils();
      stencilManager.loadStencilTutorial(tutorialFile);
    }
  }
  
  public File getTutorialDirectory() { return tutorialDirectory; }
  
  public File getExampleWorldsDirectory() {
    return new File(authoringToolConfig.getValue("directories.examplesDirectory")).getAbsoluteFile();
  }
  
  public File getTemplateWorldsDirectory() { return new File(authoringToolConfig.getValue("directories.templatesDirectory")).getAbsoluteFile(); }
  
  public javax.swing.filechooser.FileFilter getWorldFileFilter()
  {
    return worldFileFilter;
  }
  
  public javax.swing.filechooser.FileFilter getCharacterFileFilter() {
    return characterFileFilter;
  }
  

  public Variable showNewVariableDialog(String title, edu.cmu.cs.stage3.alice.core.Element context)
  {
    newVariableContentPane.reset(context);
    newVariableContentPane.setTitle(title);
    newVariableContentPane.setListsOnly(false);
    newVariableContentPane.setShowValue(true);
    return showNewVariableDialog(newVariableContentPane, context);
  }
  
  public Variable showNewVariableDialog(String title, edu.cmu.cs.stage3.alice.core.Element context, boolean listsOnly, boolean showValue) {
    newVariableContentPane.reset(context);
    newVariableContentPane.setListsOnly(listsOnly);
    newVariableContentPane.setShowValue(showValue);
    newVariableContentPane.setTitle(title);
    return showNewVariableDialog(newVariableContentPane, context);
  }
  
  protected Variable showNewVariableDialog(NewVariableContentPane newVariablePaneToShow, edu.cmu.cs.stage3.alice.core.Element context) {
    int result = DialogManager.showDialog(newVariablePaneToShow);
    switch (result) {
    case 0: 
      return newVariablePaneToShow.getVariable();
    case 2: 
      return null;
    }
    return null;
  }
  
  public edu.cmu.cs.stage3.alice.core.Sound promptUserForRecordedSound(edu.cmu.cs.stage3.alice.core.Sandbox parent)
  {
    if ((currentWorldLocation == null) && 
      (saveWorldAs() != 1)) {
      return null;
    }
    String directory = currentWorldLocation.getParent();
    directory = directory.replace('\\', '/');
    File dir = new File(directory + "/sound");
    dir.mkdir();
    
    edu.cmu.cs.stage3.alice.authoringtool.dialog.SoundRecorder soundRecorder = new edu.cmu.cs.stage3.alice.authoringtool.dialog.SoundRecorder(dir);
    soundRecorder.setParentToCheckForNameValidity(parent);
    
    int result = DialogManager.showDialog(soundRecorder);
    edu.cmu.cs.stage3.alice.core.Sound sound = null;
    
    switch (result) {
    case 0: 
      sound = soundRecorder.getSound();
      if (sound != null) {
        getUndoRedoStack().startCompound();
        try {
          parent.addChild(sound);
          sounds.add(sound);
        } finally {
          getUndoRedoStack().stopCompound();
        }
      }
      break;
    }
    return sound;
  }
  
  public File getCurrentWorldLocation() {
    return currentWorldLocation;
  }
  
  public String getCurrentRendererText() {
    return renderTargetFactory.toString();
  }
  

  public void logInstructorIntervention()
  {
    String comment = "";
    if (!instructorInControl) {
      comment = javax.swing.JOptionPane.showInputDialog(Messages.getString("What_are_you_demonstrating_"));
      if (comment != null) instructorInControl = true;
    } else {
      instructorInControl = false;
    }
    
    if (undoRedoStack != null) {
      if (instructorInControl) {
        undoRedoStack.logInstructorIntervention(Messages.getString("start"), comment);
      }
      else if (comment != null) { undoRedoStack.logInstructorIntervention(Messages.getString("end"), comment);
      }
    }
  }
  


  private boolean worldRun()
  {
    undoRedoStack.setIsListening(false);
    
    fireStateChanging(1, 3);
    fireWorldStarting(1, 3, world);
    











    countSomething("edu.cmu.cs.stage3.alice.authoringtool.playCount");
    
    world.preserve();
    






    try
    {
      worldClock.start();
      scheduler.addEachFrameRunnable(worldScheduleRunnable);
      actions.pauseWorldAction.setEnabled(true);
      actions.resumeWorldAction.setEnabled(false);
      fireStateChanged(1, 3);
      fireWorldStarted(1, 3, world);
    } catch (org.python.core.PyException e) {
      world.restore();
      showErrorDialog(Messages.getString("Error_during_world_start_"), null);
      if (!org.python.core.Py.matchException(e, org.python.core.Py.SystemExit))
      {

        org.python.core.Py.printException(e, null, pyStdErr);
      }
      return false;
    } catch (edu.cmu.cs.stage3.alice.core.SimulationException e) {
      world.restore();
      showSimulationExceptionDialog(e);
      return false;
    } catch (edu.cmu.cs.stage3.alice.core.ExceptionWrapper e) {
      world.restore();
      Exception wrappedException = e.getWrappedException();
      if ((wrappedException instanceof edu.cmu.cs.stage3.alice.core.SimulationException)) {
        showSimulationExceptionDialog((edu.cmu.cs.stage3.alice.core.SimulationException)wrappedException);
      } else {
        showErrorDialog(Messages.getString("Error_during_world_start_"), wrappedException);
      }
      return false;
    } catch (Throwable t) {
      world.restore();
      showErrorDialog(Messages.getString("Error_during_world_start_"), t);
      return false;
    }
    return true;
  }
  
  public void worldStopRunning() {
    fireStateChanging(3, 1);
    fireWorldStopping(3, 1, world);
    
    scheduler.removeEachFrameRunnable(worldScheduleRunnable);
    try
    {
      worldClock.stop();
    } catch (org.python.core.PyException e) {
      showErrorDialog(Messages.getString("Error_during_world_stop_"), null);
      if (!org.python.core.Py.matchException(e, org.python.core.Py.SystemExit))
      {

        org.python.core.Py.printException(e, null, pyStdErr);
      }
    } catch (Throwable t) {
      showErrorDialog(Messages.getString("Error_during_world_stop_"), t);
    }
    world.restore();
    undoRedoStack.setIsListening(true);
    fireStateChanged(3, 1);
    fireWorldStopped(3, 1, world);
  }
  
  public double getAspectRatio()
  {
    double aspectRatio = 0.0D;
    if ((getCurrentCamera() instanceof SymmetricPerspectiveCamera)) {
      SymmetricPerspectiveCamera cam = (SymmetricPerspectiveCamera)getCurrentCamera();
      Number hAngle = horizontalViewingAngle.getNumberValue();
      Number vAngle = verticalViewingAngle.getNumberValue();
      if ((hAngle != null) && (vAngle != null)) {
        aspectRatio = hAngle.doubleValue() / vAngle.doubleValue();
      }
    }
    return aspectRatio;
  }
  
  private void checkForUnreferencedCurrentMethod() {
    Object object = getObjectBeingEdited();
    if (((object instanceof UserDefinedResponse)) && 
      (!AuthoringToolResources.isMethodHookedUp((UserDefinedResponse)object, world)) && (!authoringToolConfig.getValue("doNotShowUnhookedMethodWarning").equalsIgnoreCase("true"))) {
      String objectRepr = AuthoringToolResources.getReprForValue(object, true);
      DialogManager.showMessageDialog(
        Messages.getString("The_current_method__") + objectRepr + Messages.getString("__is_not_called_by_any_events_or_by_any_other_methods_which_might_be_called_by_any_events_"), 
        Messages.getString("Warning__Current_method_will_not_be_called_"), 
        2);
    }
  }
  
  public void play()
  {
    jAliceFrame.playButton.setEnabled(false);
    
    checkForUnreferencedCurrentMethod();
    
    if (worldRun()) {
      double aspectRatio = getAspectRatio();
      stdErrContentPane.stopReactingToPrint();
      stdOutContentPane.stopReactingToPrint();
      renderContentPane.setAspectRatio(aspectRatio);
      renderContentPane.getRenderPanel().add(renderPanel, "Center");
      
      DialogManager.showDialog(renderContentPane);
      stdErrContentPane.startReactingToPrint();
      stdOutContentPane.startReactingToPrint();
    }
    jAliceFrame.playButton.setEnabled(true);
  }
  
  public JPanel playWhileEncoding(String directory) {
    jAliceFrame.playButton.setEnabled(false);
    
    checkForUnreferencedCurrentMethod();
    
    if (worldRun()) {
      double aspectRatio = getAspectRatio();
      stdErrContentPane.stopReactingToPrint();
      stdOutContentPane.stopReactingToPrint();
      captureContentPane.setExportDirectory(directory);
      captureContentPane.captureInit();
      captureContentPane.setAspectRatio(aspectRatio);
      captureContentPane.getRenderPanel().add(renderPanel, "Center");
      jAliceFrame.sceneEditor.makeDirty();
      DialogManager.setResize(false);
      DialogManager.showDialog(captureContentPane);
      stdErrContentPane.startReactingToPrint();
      stdOutContentPane.startReactingToPrint();
    }
    jAliceFrame.playButton.setEnabled(true);
    DialogManager.setResize(true);
    return captureContentPane;
  }
  
  public void pause() {
    new Thread(new PauseSound(true)).start();
    worldClock.pause();
    actions.pauseWorldAction.setEnabled(false);
    actions.resumeWorldAction.setEnabled(true);
    renderTarget.getAWTComponent().requestFocus();
  }
  
  public DefaultClock getWorldClock() {
    return worldClock;
  }
  
  public void resume() {
    new Thread(new PauseSound(false)).start();
    worldClock.resume();
    actions.pauseWorldAction.setEnabled(true);
    actions.resumeWorldAction.setEnabled(false);
    renderTarget.getAWTComponent().requestFocus();
  }
  
  public void restartWorld() {
    try {
      worldClock.stop();
      sound.clear();
      world.restore();
      actions.pauseWorldAction.setEnabled(true);
      actions.resumeWorldAction.setEnabled(false);
      
      Thread.sleep(100L);
      worldClock.start();
    } catch (org.python.core.PyException e) {
      showErrorDialog(Messages.getString("Error_while_restarting_world_"), null);
      if (!org.python.core.Py.matchException(e, org.python.core.Py.SystemExit))
      {

        org.python.core.Py.printException(e, null, pyStdErr);
      }
    } catch (edu.cmu.cs.stage3.alice.core.SimulationException e) {
      showSimulationExceptionDialog(e);
    } catch (edu.cmu.cs.stage3.alice.core.ExceptionWrapper e) {
      Exception wrappedException = e.getWrappedException();
      if ((wrappedException instanceof edu.cmu.cs.stage3.alice.core.SimulationException)) {
        showSimulationExceptionDialog((edu.cmu.cs.stage3.alice.core.SimulationException)wrappedException);
      } else {
        showErrorDialog(Messages.getString("Error_while_restarting_world_"), wrappedException);
      }
    } catch (Throwable t) {
      showErrorDialog(Messages.getString("Error_while_restarting_world_"), t);
    }
    renderTarget.getAWTComponent().requestFocus();
  }
  
  public void stopWorld() {
    sound.clear();
    renderContentPane.stopWorld();
  }
  
  public void setWorldSpeed(double newSpeed) {
    worldClock.setSpeed(newSpeed);
  }
  
  public void takePicture() {
    try {
      storeCapturedImage(renderTarget.getOffscreenImage());
    } catch (Throwable t) {
      showErrorDialog(Messages.getString("Error_capturing_image_"), t);
    }
    renderTarget.getAWTComponent().requestFocus();
  }
  
  public movieMaker.SoundStorage getSoundStorage() {
    return soundStorage;
  }
  
  public void setSoundStorage(movieMaker.SoundStorage myS) {
    soundStorage = myS;
  }
  
  public static void pauseSound(edu.cmu.cs.stage3.media.Player m_player) {
    sound.add(m_player);
    m_player.startFromBeginning();
  }
  
  private class PauseSound implements Runnable {
    boolean pause = false;
    
    public PauseSound(boolean pause) { this.pause = pause; }
    
    public void run()
    {
      if (pause) {
        for (int i = 0; i < AuthoringTool.sound.size(); i++)
          ((edu.cmu.cs.stage3.media.Player)AuthoringTool.sound.get(i)).stop();
      } else {
        for (int i = 0; i < AuthoringTool.sound.size(); i++) {
          ((edu.cmu.cs.stage3.media.Player)AuthoringTool.sound.get(i)).start();
        }
      }
    }
  }
  
  public void openTutorialEditor()
  {
    if (currentWorldLocation != null)
      stencilManager.setTutorialWorld(currentWorldLocation.getAbsolutePath());
    stencilManager.setInstructorMode(true);
    showStencils();
  }
  
  public void stencilManagerReFocus() { jAliceFrame.removeKeyListener(stencilManager);
    jAliceFrame.addKeyListener(stencilManager);
    jAliceFrame.requestFocus();
  }
  

  private static boolean pulsing = true;
  private static JLabel statusLabel = new JLabel(Messages.getString("Ready"));
  private static javax.swing.JFrame statusFrame;
  private int numUpdate = 0;
  
  private boolean checkForUpdate() {
    java.net.URLConnection urlc = null;
    try {
      URL url = new URL(AuthoringToolResources.getMainUpdateURL().toString() + "alice.jar");
      urlc = url.openConnection();
      File old = new File(JAlice.getAliceHomeDirectory().toString() + System.getProperty("file.separator") + "lib" + System.getProperty("file.separator") + "alice.jar");
      long date = urlc.getLastModified();
      long oldDate = old.lastModified();
      if (date > oldDate) {
        return true;
      }
    } catch (Exception e) {
      javax.swing.JOptionPane.showMessageDialog(null, 
        Messages.getString("Update_failed"), Messages.getString("Cannot access required file."), 0);
    }
    return false;
  }
  
  public void updateAlice()
  {
    Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    
    final JDialog dlg = new JDialog(new javax.swing.JFrame(), Messages.getString("Checking_for_update"));
    dlg.setPreferredSize(new Dimension(330, 250));
    dlg.setSize(new Dimension(330, 250));
    dlg.setLocation(getSizewidth / 2 - 150, getSizeheight / 2 - 50);
    dlg.setAlwaysOnTop(true);
    dlg.setResizable(false);
    dlg.setVisible(true);
    
    JPanel updateDialog = new JPanel();
    updateDialog.setLayout(new java.awt.BorderLayout());
    
    javax.swing.Box left = javax.swing.Box.createVerticalBox();
    left.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    final JCheckBox UpdateJAR = new JCheckBox("");
    


    UpdateJAR.setText(Messages.getString("Update_Software"));
    UpdateJAR.setSelected(true);
    




    left.add(UpdateJAR);
    
    final JCheckBox CoreGallery = new JCheckBox(Messages.getString("Download_Core_Gallery"));
    left.add(CoreGallery);
    
    final JCheckBox EnglishGallery = new JCheckBox(Messages.getString("Download_English_Gallery"));
    left.add(EnglishGallery);
    
    final JCheckBox SpanishGallery = new JCheckBox(Messages.getString("Download_Spanish_Gallery"));
    left.add(SpanishGallery);
    
    updateDialog.add(left, "Before");
    
    javax.swing.Box bottom = javax.swing.Box.createVerticalBox();
    bottom.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 20, 10));
    JButton ConfirmUpdate = new JButton(Messages.getString("Update"));
    ConfirmUpdate.setAlignmentX(0.5F);
    ConfirmUpdate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        dlg.dispose();
        if (UpdateJAR.isSelected()) {
          numUpdate += 1;
          new Thread(new AuthoringTool.StartUpdating(AuthoringTool.this, "AliceUpdate.zip", 
            JAlice.getAliceHomeDirectory().getParent().toString(), false)).start();
        }
        if (CoreGallery.isSelected()) {
          numUpdate += 1;
          new Thread(new AuthoringTool.StartUpdating(AuthoringTool.this, "CoreGallery.zip", 
            JAlice.getAliceHomeDirectory().toString() + System.getProperty("file.separator") + "gallery", true)).start();
        }
        if (EnglishGallery.isSelected()) {
          numUpdate += 1;
          new Thread(new AuthoringTool.StartUpdating(AuthoringTool.this, "EnglishGallery.zip", 
            JAlice.getAliceHomeDirectory().toString() + System.getProperty("file.separator") + "gallery", true)).start();
        }
        if (SpanishGallery.isSelected()) {
          numUpdate += 1;
          new Thread(new AuthoringTool.StartUpdating(AuthoringTool.this, "SpanishGallery.zip", 
            JAlice.getAliceHomeDirectory().toString() + System.getProperty("file.separator") + "gallery", true)).start();
        }
        if (numUpdate == 0) {
          DialogManager.showMessageDialog(Messages.getString("No_update_selected_"));
        }
      }
    });
    bottom.add(ConfirmUpdate);
    
    updateDialog.add(bottom, "Last");
    dlg.add(updateDialog);
    dlg.pack();
    dlg.repaint();
  }
  
  public class StartUpdating implements Runnable
  {
    private boolean isGallery;
    private String file;
    private String dest;
    private ProgressMonitor monitor = new ProgressMonitor(null, Messages.getString("Updating"), Messages.getString("Getting_Started___"), 0, 0);
    
    public StartUpdating(String filename, String location, boolean dir) {
      file = filename;
      dest = location;
      isGallery = dir;
    }
    
    public void run()
    {
      java.io.BufferedInputStream bis = null;
      BufferedOutputStream bos = null;
      java.net.URLConnection urlc = null;
      File temp;
      Object[] options; int result; try { URL url = new URL(AuthoringToolResources.getMainUpdateURL().toString() + this.file);
        urlc = url.openConnection();
        monitor.setMaximum(urlc.getContentLength());
        
        File aliceHome = new File(dest);
        bis = new java.io.BufferedInputStream(urlc.getInputStream());
        bos = new BufferedOutputStream(new java.io.FileOutputStream(aliceHome + System.getProperty("file.separator") + this.file));
        
        if (isGallery) {
          if (!aliceHome.exists()) {
            aliceHome.mkdir();
          }
          monitor.setNote(Messages.getString("Downloading_gallery"));
        } else {
          monitor.setNote(Messages.getString("Downloading_AliceUpdate_zip_file"));
        }
        
        int progress = 0;
        int i; while ((i = bis.read()) != -1) {
          int i;
          bos.write(i);
          if (monitor.isCanceled()) {
            break;
          }
          monitor.setProgress(progress++);
        }
        

        monitor.setProgress(monitor.getMaximum());
        bis.close();
        bos.close();
        
        if (!monitor.isCanceled())
        {


          java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(aliceHome + System.getProperty("file.separator") + this.file);
          java.util.Enumeration entries = zipFile.entries();
          
          monitor.setNote("Copying models to Alice gallery");
          monitor.setProgress(0);progress = 0;
          monitor.setMaximum(zipFile.size());
          
          while (entries.hasMoreElements()) {
            java.util.zip.ZipEntry entry = (java.util.zip.ZipEntry)entries.nextElement();
            File file = new File(aliceHome + System.getProperty("file.separator") + entry.getName());
            if (entry.isDirectory()) {
              file.mkdir();
            }
            else {
              file.getParentFile().mkdirs();
              
              java.io.InputStream in = zipFile.getInputStream(entry);
              java.io.OutputStream out = new BufferedOutputStream(new java.io.FileOutputStream(aliceHome + System.getProperty("file.separator") + entry.getName()));
              byte[] buffer = new byte['Ѐ'];
              int len;
              while ((len = in.read(buffer)) >= 0) { int len;
                out.write(buffer, 0, len); }
              in.close();
              out.flush();
              out.close();
              monitor.setProgress(progress++);
            } }
          monitor.setProgress(monitor.getMaximum());
          zipFile.close();
        }
      } catch (java.io.FileNotFoundException e) {
        AuthoringTool.showErrorDialog("Error encountered during update", e); } catch (Exception e1) { File temp;
        Object[] options;
        int result; javax.swing.JOptionPane.showMessageDialog(null, " " + Messages.getString("Update_failed__Please_check_your_internet_connection__"), Messages.getString("Update_failed"), 0); } finally { File temp;
        Object[] options;
        int result; if (bis != null)
          try {
            bis.close();
          } catch (IOException ioe) {
            ioe.printStackTrace();
          }
        if (bos != null)
          try {
            bos.close();
          } catch (IOException ioe) {
            ioe.printStackTrace();
          }
        numUpdate -= 1;
        
        File temp = new File(dest + System.getProperty("file.separator") + this.file);
        if (temp.exists())
          temp.delete();
        if (numUpdate == 0) {
          Object[] options = { Messages.getString("Restart"), Messages.getString("Cancel") };
          int result = DialogManager.showOptionDialog(" " + Messages.getString("You_must_restart_Alice_for_the_updates_to_take_effect__"), Messages.getString("Update_completed"), 2, 3, null, options, options[0]);
          if (result == 0) {
            monitor.close();
            quit(true);
          }
        }
        monitor.close();
      }
    }
  }
}
