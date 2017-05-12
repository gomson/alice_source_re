package edu.cmu.cs.stage3.alice.authoringtool;

import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.Application;
import com.apple.eawt.OpenFilesHandler;
import com.sun.media.codec.audio.mp3.JavaDecoder;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.SplashScreen;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;












public class JAlice
{
  private static String version = "2.4.3";
  private static String backgroundColor = new java.awt.Color(0, 78, 152).toString();
  private static String directory = null;
  private Package authoringToolPackage = Package.getPackage("edu.cmu.cs.stage3.alice.authoringtool");
  






  static AuthoringTool authoringTool;
  






  static
  {
    try
    {
      File bakFile = new File(getAliceHomeDirectory().getParent().toString() + File.separator + "Aliceold.exe");
      if (bakFile.exists()) {
        bakFile.delete();
      }
      File newFile = new File(getAliceHomeDirectory().getParent().toString() + File.separator + "Alicenew.exe");
      if (newFile.exists()) {
        JOptionPane.showMessageDialog(new JFrame(), 
          "You must restart Alice to complete the software update.", 
          "Software Update", 
          1);
        File oldFile = new File(getAliceHomeDirectory().getParent().toString() + File.separator + "Alice.exe");
        if (newFile.exists()) {
          oldFile.renameTo(new File(getAliceHomeDirectory().getParent().toString() + File.separator + "Aliceold.exe"));
          newFile.renameTo(oldFile);
        }
        newFile = new File(getAliceHomeDirectory().toString() + File.separator + "lib" + File.separator + "win32" + File.separator + "jni_directx7renderer.dll.new");
        oldFile = new File(getAliceHomeDirectory().toString() + File.separator + "lib" + File.separator + "win32" + File.separator + "jni_directx7renderer.dll");
        if (newFile.exists()) {
          oldFile.delete();
          newFile.renameTo(oldFile);
        }
        newFile = new File(getAliceHomeDirectory().toString() + File.separator + "lib" + File.separator + "win32" + File.separator + "jni_awtutilities.dll.new");
        oldFile = new File(getAliceHomeDirectory().toString() + File.separator + "lib" + File.separator + "win32" + File.separator + "jni_awtutilities.dll");
        if (newFile.exists()) {
          oldFile.delete();
          newFile.renameTo(oldFile);
        }
        try
        {
          if (AikMin.isWindows()) {
            String file = getAliceHomeDirectory().getParent().toString() + "\\Alice.exe";
            if (new File(file).exists()) {
              Runtime.getRuntime().exec(file);
            } else {
              DialogManager.showMessageDialog("Missing Alice.exe in Alice directory. Please restart Alice manually.");
            }
            
          }
          else if (AikMin.isMAC()) {
            String decodedPath = URLDecoder.decode(JAlice.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
            decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf(".app") + 4);
            String[] params = { "open", "-n", decodedPath };
            Runtime.getRuntime().exec(params);
          }
          else
          {
            String file = getAliceHomeDirectory().getParent().toString() + "/Required/run-alice";
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
        System.exit(0);
      }
      
      File versionFile = new File(getAliceHomeDirectory(), "etc/config.txt").getAbsoluteFile();
      if (versionFile.exists()) {
        if (versionFile.canRead()) {
          BufferedReader br = new BufferedReader(new FileReader(versionFile));
          String colorString = br.readLine();
          directory = br.readLine();
          br.close();
          if (colorString != null) {
            colorString = colorString.trim();
            if (colorString.length() > 0)
              try {
                if (!colorString.startsWith("0x")) {
                  String[] color = colorString.split(",");
                  double red = Integer.decode(color[0]).doubleValue() / 255.0D;
                  double green = Integer.decode(color[1]).doubleValue() / 255.0D;
                  double blue = Integer.decode(color[2]).doubleValue() / 255.0D;
                  backgroundColor = new edu.cmu.cs.stage3.alice.scenegraph.Color(red, green, blue).toString();
                } else {
                  java.awt.Color newColor = java.awt.Color.decode(colorString);
                  backgroundColor = new edu.cmu.cs.stage3.alice.scenegraph.Color(newColor).toString();
                }
              } catch (Throwable colorT) { colorT.printStackTrace();
              }
          }
        } else {
          version = Messages.getString("cannot_read_config_txt");
        }
      } else {
        version = Messages.getString("config_txt_does_not_exist");
      }
    } catch (Throwable t) {
      t.printStackTrace();
      version = Messages.getString("error_while_reading_config_txt");
    } }
  
  public JAlice() {}
  
  public static String getVersion() { return version; }
  

  static File aliceHomeDirectory = null;
  static File aliceUserDirectory = null;
  
  static SplashScreen splashScreen;
  static File defaultWorld;
  static File worldToLoad = null;
  static boolean stdOutToConsole = false;
  static boolean stdErrToConsole = false;
  static String defaultRendererClassname = null;
  

  static boolean mainHasFinished = false;
  private static boolean listenerRegistered = false;
  

  public static void main(String[] args)
  {
    try
    {
      String[] mp3args = new String[0];
      
      JavaDecoder.main(mp3args);
    }
    catch (Throwable t) {
      t.printStackTrace(System.out);
    }
    try
    {
      File firstRun = new File(getAliceHomeDirectory(), "etc/firstRun.txt").getAbsoluteFile();
      if ((firstRun.exists()) || (AikMin.target == 1)) {
        firstRun.delete();
        if (getAliceUserDirectory().exists())
          new File(getAliceUserDirectory(), "AlicePreferences.xml").getAbsoluteFile().delete();
      }
      firstRun = null;
      localInit();
      boolean useJavaBasedSplashScreen = true;
      String useSplashScreenString = System.getProperty("alice.useJavaBasedSplashScreen");
      if ((useSplashScreenString != null) && (!useSplashScreenString.equalsIgnoreCase("true"))) {
        useJavaBasedSplashScreen = false;
      }
      if (useJavaBasedSplashScreen) {
        Class.forName("edu.cmu.cs.stage3.alice.authoringtool.util.Configuration");
        splashScreen = initSplashScreen();
        splashScreen.showSplash();
      }
      if (AikMin.isMAC()) {
        Application app = Application.getApplication();
        app.setOpenFileHandler(new OpenFilesHandler() {
          public void openFiles(AppEvent.OpenFilesEvent event) {
            List<String> filenames = new ArrayList();
            for (File f : event.getFiles()) {
              filenames.add(f.getAbsolutePath());
            }
            if (!JAlice.listenerRegistered) {
              JAlice.worldToLoad = new File(((String[])filenames.toArray(new String[filenames.size()]))[0]).getAbsoluteFile();
              JAlice.listenerRegistered = true;
            }
            else {
              try {
                String decodedPath = URLDecoder.decode(JAlice.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
                decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf(".app") + 4);
                String[] params = { "open", "-n", decodedPath, ((String[])filenames.toArray(new String[filenames.size()]))[0] };
                Runtime.getRuntime().exec(params);
              }
              catch (Exception e) {
                e.printStackTrace();
              }
            }
          }
        });
      }
      parseCommandLineArgs(args);
      Class.forName("edu.cmu.cs.stage3.alice.authoringtool.util.Configuration");
      configInit();
      try {
        File aliceHasNotExitedFile = new File(getAliceUserDirectory(), "aliceHasNotExited.txt");
        if (aliceHasNotExitedFile.exists()) {
          aliceHasNotExitedFile.delete();
        }
        aliceHasNotExitedFile.createNewFile();
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(aliceHasNotExitedFile));
        writer.write("Alice_has_not_exited_propertly_yet_");
        writer.flush();
        writer.close();
      } catch (Exception localException) {}
      Class.forName("edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources");
      Class.forName("edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities");
      


      defaultWorld = new File(getAliceHomeDirectory(), "etc/default_" + AikMin.locale + ".a2w").getAbsoluteFile();
      if ((!defaultWorld.exists()) || (!defaultWorld.canRead())) {
        JOptionPane.showMessageDialog(new JFrame(), 
          defaultWorld.getAbsolutePath() + " " + Messages.getString("does_not_exist_or_cannot_be_read__No_starting_world_will_be_available_"), 
          Messages.getString("Warning"), 
          0);
        System.exit(1);
      }
      
      authoringTool = new AuthoringTool(defaultWorld, worldToLoad, stdOutToConsole, stdErrToConsole);
      if (useJavaBasedSplashScreen) {
        splashScreen.dispose();
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    
    mainHasFinished = true;
  }
  
  private static void localInit() {
    Configuration authoringtoolConfig = Configuration.getLocalConfiguration(JAlice.class.getPackage());
    if (authoringtoolConfig.getValue("language") == null) {
      authoringtoolConfig.setValue("language", AikMin.defaultLanguage);
    }
    AikMin.locale = authoringtoolConfig.getValue("language");
  }
  

  private static SplashScreen initSplashScreen()
  {
    URL url = JAlice.class
      .getResource("images/AliceSplash_" + AikMin.locale + ".png");
    if (url == null) {
      url = 
        JAlice.class.getResource("images/AliceSplash_English.png");
    }
    Image splashImage = Toolkit.getDefaultToolkit().getImage(url);
    



    return new SplashScreen(splashImage);
  }
  
  private static void configInit() {
    Configuration authoringtoolConfig = Configuration.getLocalConfiguration(JAlice.class.getPackage());
    

    authoringtoolConfig.setValue("backgroundColor", backgroundColor);
    if (authoringtoolConfig.getValue("recentWorlds.maxWorlds") == null) {
      authoringtoolConfig.setValue("recentWorlds.maxWorlds", Integer.toString(8));
    }
    if (authoringtoolConfig.getValueList("recentWorlds.worlds") == null) {
      authoringtoolConfig.setValueList("recentWorlds.worlds", new String[0]);
    }
    
    if (authoringtoolConfig.getValue("enableHighContrastMode") == null) {
      authoringtoolConfig.setValue("enableHighContrastMode", "false");
    }
    
    if (authoringtoolConfig.getValue("enableLoggingMode") == null) {
      authoringtoolConfig.setValue("enableLoggingMode", "false");
    }
    
    if (authoringtoolConfig.getValue("disableTooltipMode") == null) {
      authoringtoolConfig.setValue("disableTooltipMode", "false");
    }
    
    if (authoringtoolConfig.getValue("showBuilderMode") == null) {
      authoringtoolConfig.setValue("showBuilderMode", "false");
    }
    
    if (authoringtoolConfig.getValue("fontSize") == null) {
      authoringtoolConfig.setValue("fontSize", Integer.toString(12));
    }
    
    if (authoringtoolConfig.getValue("showObjectLoadFeedback") == null) {
      authoringtoolConfig.setValue("showObjectLoadFeedback", "true");
    }
    
    if (authoringtoolConfig.getValue("maximumWorldBackupCount") == null) {
      authoringtoolConfig.setValue("maximumWorldBackupCount", Integer.toString(5));
    }
    
    if (authoringtoolConfig.getValue("maxRecentlyUsedValues") == null) {
      authoringtoolConfig.setValue("maxRecentlyUsedValues", Integer.toString(5));
    }
    
    if (authoringtoolConfig.getValue("numberOfClipboards") == null) {
      authoringtoolConfig.setValue("numberOfClipboards", Integer.toString(1));
    }
    
    if (authoringtoolConfig.getValue("showWorldStats") == null) {
      authoringtoolConfig.setValue("showWorldStats", "false");
    }
    
    if (authoringtoolConfig.getValue("enableScripting") == null) {
      authoringtoolConfig.setValue("enableScripting", "false");
    }
    
    if (authoringtoolConfig.getValue("promptToSaveInterval") == null) {
      authoringtoolConfig.setValue("promptToSaveInterval", Integer.toString(15));
    }
    
    if (authoringtoolConfig.getValue("doNotShowUnhookedMethodWarning") == null) {
      authoringtoolConfig.setValue("doNotShowUnhookedMethodWarning", "false");
    }
    




    if (authoringtoolConfig.getValue("clearStdOutOnRun") == null) {
      authoringtoolConfig.setValue("clearStdOutOnRun", "true");
    }
    
    if (authoringtoolConfig.getValue("resourceFile") == null) {
      authoringtoolConfig.setValue("resourceFile", "Alice Style.py");
    }
    
    if (authoringtoolConfig.getValue("watcherPanelEnabled") == null) {
      authoringtoolConfig.setValue("watcherPanelEnabled", "false");
    }
    
    if (authoringtoolConfig.getValue("showStartUpDialog") == null) {
      authoringtoolConfig.setValue("showStartUpDialog", "true");
    }
    
    if (authoringtoolConfig.getValue("showWebWarningDialog") == null) {
      authoringtoolConfig.setValue("showWebWarningDialog", "true");
    }
    
    if (authoringtoolConfig.getValue("showStartUpDialog_OpenTab") == null) {
      authoringtoolConfig.setValue("showStartUpDialog_OpenTab", Integer.toString(4));
    }
    
    if (authoringtoolConfig.getValue("loadSavedTabs") == null) {
      authoringtoolConfig.setValue("loadSavedTabs", "false");
    }
    
    if (authoringtoolConfig.getValue("saveThumbnailWithWorld") == null) {
      authoringtoolConfig.setValue("saveThumbnailWithWorld", "true");
    }
    




















    if (authoringtoolConfig.getValue("mainWindowBounds") == null) {
      int screenWidth = 1280;
      int screenHeight = 720;
      int x = 0;
      int y = 0;
      authoringtoolConfig.setValue("mainWindowBounds", x + ", " + y + ", " + screenWidth + ", " + screenHeight);
    }
    
    if (authoringtoolConfig.getValueList("rendering.orderedRendererList") == null) {
      Class[] rendererClasses = DefaultRenderTargetFactory.getPotentialRendererClasses();
      String[] list = new String[rendererClasses.length];
      for (int i = 0; i < rendererClasses.length; i++) {
        list[i] = rendererClasses[i].getName();
      }
      authoringtoolConfig.setValueList("rendering.orderedRendererList", list);
    }
    
    if (authoringtoolConfig.getValue("rendering.showFPS") == null) {
      authoringtoolConfig.setValue("rendering.showFPS", "false");
    }
    
    if (authoringtoolConfig.getValue("rendering.forceSoftwareRendering") == null) {
      authoringtoolConfig.setValue("rendering.forceSoftwareRendering", "false");
    }
    
    if (authoringtoolConfig.getValue("rendering.deleteFiles") == null) {
      authoringtoolConfig.setValue("rendering.deleteFiles", "true");
    }
    
    if (authoringtoolConfig.getValue("rendering.renderWindowMatchesSceneEditor") == null) {
      authoringtoolConfig.setValue("rendering.renderWindowMatchesSceneEditor", "true");
    }
    
    if (authoringtoolConfig.getValue("rendering.ensureRenderDialogIsOnScreen") == null) {
      authoringtoolConfig.setValue("rendering.ensureRenderDialogIsOnScreen", "true");
    }
    
    if (authoringtoolConfig.getValue("rendering.renderWindowBounds") == null) {
      int screenWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
      int screenHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
      int width = 480;
      int height = 360;
      int x = (screenWidth - width) / 2;
      int y = (screenHeight - height) / 2;
      
      authoringtoolConfig.setValue("rendering.renderWindowBounds", x + ", " + y + ", " + width + ", " + height);
    }
    
    if (authoringtoolConfig.getValue("rendering.runtimeScratchPadEnabled") == null) {
      authoringtoolConfig.setValue("rendering.runtimeScratchPadEnabled", "false");
    }
    
    if (authoringtoolConfig.getValue("rendering.runtimeScratchPadHeight") == null) {
      authoringtoolConfig.setValue("rendering.runtimeScratchPadHeight", "300");
    }
    
    if (authoringtoolConfig.getValue("rendering.useBorderlessWindow") == null) {
      authoringtoolConfig.setValue("rendering.useBorderlessWindow", "false");
    }
    
    if (authoringtoolConfig.getValue("rendering.constrainRenderDialogAspectRatio") == null) {
      authoringtoolConfig.setValue("rendering.constrainRenderDialogAspectRatio", "true");
    }
    








    if (authoringtoolConfig.getValue("gui.pickUpTiles") == null) {
      authoringtoolConfig.setValue("gui.pickUpTiles", "true");
    }
    
    if (authoringtoolConfig.getValue("gui.useAlphaTiles") == null) {
      authoringtoolConfig.setValue("gui.useAlphaTiles", "false");
    }
    
    if (authoringtoolConfig.getValue("useSingleFileLoadStore") == null) {
      authoringtoolConfig.setValue("useSingleFileLoadStore", "true");
    }
    
    if (authoringtoolConfig.getValue("directories.worldsDirectory") == null)
    {
      String dir = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop";
      authoringtoolConfig.setValue("directories.worldsDirectory", dir);
    }
    
    File defaultGallery = new File("gallery").getAbsoluteFile();
    if (defaultGallery.exists()) {
      String[] list = defaultGallery.list();
      authoringtoolConfig.setValueList("directories.galleryDirectory", list);
    }
    
    if (authoringtoolConfig.getValue("directories.importDirectory") == null)
    {
      String dir = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop";
      authoringtoolConfig.setValue("directories.importDirectory", dir);
    }
    
    if (authoringtoolConfig.getValue("directories.examplesDirectory") == null) {
      authoringtoolConfig.setValue("directories.examplesDirectory", "exampleWorlds");
    }
    
    String charDir = authoringtoolConfig.getValue("directories.charactersDirectory");
    if (charDir == null) {
      charDir = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop" + System.getProperty("file.separator") + "CustomGallery";
    }
    File captureDir = new File(charDir);
    if ((!captureDir.exists()) && 
      (!captureDir.mkdir())) {
      charDir = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop";
      captureDir = new File(charDir);
    }
    
    authoringtoolConfig.setValue("directories.charactersDirectory", charDir);
    
    if (authoringtoolConfig.getValue("directories.templatesDirectory") == null) {
      authoringtoolConfig.setValue("directories.templatesDirectory", "templateWorlds" + System.getProperty("file.separator") + AikMin.locale);
    }
    
    if (authoringtoolConfig.getValue("directories.textbookExamplesDirectory") == null) {
      authoringtoolConfig.setValue("directories.textbookExamplesDirectory", "textbookExampleWorlds");
    }
    




    if (authoringtoolConfig.getValue("screenCapture.directory") == null) {
      String dir = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop";
      authoringtoolConfig.setValue("screenCapture.directory", dir);
    }
    if (authoringtoolConfig.getValue("screenCapture.baseName") == null) {
      authoringtoolConfig.setValue("screenCapture.baseName", "capture");
    }
    if (authoringtoolConfig.getValue("screenCapture.numDigits") == null) {
      authoringtoolConfig.setValue("screenCapture.numDigits", "2");
    }
    if (authoringtoolConfig.getValue("screenCapture.codec") == null) {
      authoringtoolConfig.setValue("screenCapture.codec", "jpeg");
    }
    if (authoringtoolConfig.getValue("screenCapture.codec").equalsIgnoreCase("gif")) {
      authoringtoolConfig.setValue("screenCapture.codec", "jpeg");
    }
    if (authoringtoolConfig.getValue("screenCapture.informUser") == null) {
      authoringtoolConfig.setValue("screenCapture.informUser", "true");
    }
    
    if (authoringtoolConfig.getValue("saveInfiniteBackups") == null) {
      authoringtoolConfig.setValue("saveInfiniteBackups", "false");
    }
    
    if (authoringtoolConfig.getValue("doProfiling") == null) {
      authoringtoolConfig.setValue("doProfiling", "false");
    }
  }
  

  private static void parseCommandLineArgs(String[] args)
  {
    LongOpt[] options = {
      new LongOpt("stdOutToConsole", 0, null, 111), 
      new LongOpt("stdErrToConsole", 0, null, 101), 
      new LongOpt("defaultRenderer", 1, null, 114), 
      
      new LongOpt("help", 0, null, 104) };
    

    String helpMessage = "\nUsage: JAlice <options> <world>\n\noptions:\n    --stdOutToConsole|-o:\n        directs System.stdOut to the console instead of the output text area.\n    --stdErrToConsole|-e:\n        directs System.stdOut to the console instead of the output text area.\n    --defaultRenderer|-r <classname>:\n        the Renderer specified by <classname> will be used as the default Renderer\n    --help|-h:\n        prints this help message\n\nworld:\n    a pathname to a world on disk to be loaded at startup.\n";
    























    Getopt g = new Getopt("JAlice", args, ":oeh", options);
    int c; while ((c = g.getopt()) != -1) { int c;
      switch (c) {
      case 111: 
        stdOutToConsole = true;
        break;
      case 101: 
        stdErrToConsole = true;
        break;
      case 114: 
        defaultRendererClassname = g.getOptarg();
        break;
      














      case 63: 
      case 104: 
        System.err.println(helpMessage);
        System.exit(0);
        break;
      default: 
        System.err.println("ignoring " + c + " on the command line.");
      }
      
    }
    
    int i = g.getOptind();
    if ((i >= 0) && (i < args.length)) {
      if (AikMin.isWindows()) {
        char ch = ':';
        String file = args[i].toString();
        file = file.substring(file.lastIndexOf(ch) - 1, file.length());
        worldToLoad = new File(file).getAbsoluteFile();
      }
      else if (!AikMin.isMAC())
      {

        worldToLoad = new File(args[i]).getAbsoluteFile();
      }
    }
  }
  
  public static boolean isMainFinished() {
    return mainHasFinished;
  }
  
  public static void setAliceHomeDirectory(File file) {
    aliceHomeDirectory = file;
  }
  
  public static File getAliceHomeDirectory() {
    if (aliceHomeDirectory == null) {
      if (System.getProperty("alice.home") != null) {
        setAliceHomeDirectory(new File(System.getProperty("alice.home")).getAbsoluteFile());
      } else {
        setAliceHomeDirectory(new File(System.getProperty("user.dir")).getAbsoluteFile());
      }
    }
    
    return aliceHomeDirectory;
  }
  
  public static void setAliceUserDirectory(File file) {
    if (file != null) {
      if (file.exists()) {
        aliceUserDirectory = file;
      } else if (file.mkdirs()) {
        aliceUserDirectory = file;
      }
    }
  }
  
  public static File getAliceUserDirectory() {
    if (aliceUserDirectory == null) {
      File dirFromProperties = null;
      if (System.getProperty("alice.userDir") != null) {
        dirFromProperties = new File(System.getProperty("alice.userDir")).getAbsoluteFile();
      }
      File userHome = new File(System.getProperty("user.home")).getAbsoluteFile();
      File aliceHome = getAliceHomeDirectory();
      File aliceUser = null;
      if (directory != null) {
        aliceUser = new File(directory, ".alice2");
      } else if (dirFromProperties != null) {
        aliceUser = dirFromProperties;
      } else if ((userHome.exists()) && (userHome.canRead()) && (userHome.canWrite())) {
        aliceUser = new File(userHome, ".alice2");
      } else if ((aliceHome != null) && (aliceHome.exists()) && (aliceHome.canRead()) && (aliceHome.canWrite())) {
        aliceUser = new File(aliceHome, ".alice2");
      }
      setAliceUserDirectory(aliceUser);
    }
    return aliceUserDirectory;
  }
}
