package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JFrame;













public class FileChooser
{
  private static Properties appProperties = null;
  



  private static final String MEDIA_DIRECTORY = "mediaDirectory";
  



  private static final String PROPERTY_FILE_NAME = "SimplePictureProperties.txt";
  



  public FileChooser() {}
  



  public static String pickPath(JFileChooser fileChooser)
  {
    String path = null;
    




    JFrame frame = new JFrame();
    


    int returnVal = fileChooser.showOpenDialog(frame);
    

    if (returnVal == 0)
      path = fileChooser.getSelectedFile().getPath();
    return path;
  }
  






  public static String pickAFile()
  {
    JFileChooser fileChooser = null;
    

    String fileName = null;
    

    String mediaDir = getMediaDirectory();
    


    try
    {
      File file = new File(mediaDir);
      if (file.exists()) {
        fileChooser = new JFileChooser(file);
      }
    }
    catch (Exception localException) {}
    
    if (fileChooser == null) {
      fileChooser = new JFileChooser();
    }
    
    fileName = pickPath(fileChooser);
    
    return fileName;
  }
  





  public static String pickADirectory()
  {
    JFileChooser fileChooser = null;
    String dirName = null;
    

    String mediaDir = getMediaDirectory();
    

    if (mediaDir != null) {
      fileChooser = new JFileChooser(mediaDir);
    } else {
      fileChooser = new JFileChooser();
    }
    
    fileChooser.setFileSelectionMode(1);
    

    dirName = pickPath(fileChooser);
    
    return dirName + "/";
  }
  





  public static String getMediaPath(String fileName)
  {
    String path = null;
    String directory = getMediaDirectory();
    

    if (directory == null)
    {
      SimpleOutput.showError(
      









        Messages.getString("The_media_path__directory_") + " " + Messages.getString("has_not_been_set_yet__") + Messages.getString("Please_pick_the_directory_") + Messages.getString("that_contains_your_media_") + " " + Messages.getString("usually_called_mediasources__") + Messages.getString("with_the_following_FileChooser___") + Messages.getString("The_directory_name_will_be_stored_") + Messages.getString("in_a_file_and_remain_unchanged_unless_you_use_") + Messages.getString("FileChooser_pickMediaPath___or_") + Messages.getString("FileChooser_setMediaPath__full_path_name___") + " " + Messages.getString("ex__FileChooser_setMediaPath__c__intro_prog_java_mediasources_____") + " " + Messages.getString("to_change_it_"));
      pickMediaPath();
      directory = getMediaDirectory();
    }
    

    path = directory + fileName;
    
    return path;
  }
  




  public static String getMediaDirectory()
  {
    String directory = null;
    



    if (appProperties == null)
    {
      appProperties = new Properties();
      

      try
      {
        Class currClass = Class.forName("FileChooser");
        URL classURL = currClass.getResource("FileChooser.class");
        URL fileURL = new URL(classURL, "SimplePictureProperties.txt");
        FileInputStream in = new FileInputStream(fileURL.getPath());
        appProperties.load(in);
        in.close();
      } catch (Exception ex) {
        directory = null;
      }
    }
    

    if (appProperties != null) {
      directory = (String)appProperties.get("mediaDirectory");
    }
    return directory;
  }
  






  public static void setMediaPath(String directory)
  {
    File file = new File(directory);
    if (!file.exists())
    {
      System.out.println(Messages.getString("Sorry_but_") + directory + " " + 
        Messages.getString("doesn_t_exist__try_a_different_directory_"));
      pickMediaPath();


    }
    else
    {


      if (appProperties == null) {
        appProperties = new Properties();
      }
      
      appProperties.put("mediaDirectory", directory);
      


      try
      {
        Class currClass = Class.forName("FileChooser");
        URL classURL = currClass.getResource("FileChooser.class");
        URL fileURL = new URL(classURL, "SimplePictureProperties.txt");
        FileOutputStream out = 
          new FileOutputStream(fileURL.getPath());
        appProperties.store(out, 
          Messages.getString("Properties_for_the_Simple_Picture_class"));
        out.close();
      }
      catch (Exception ex) {
        System.err.println(Messages.getString("Couldn_t_save_media_path_to_a_file"));
      }
    }
  }
  




  public static void pickMediaPath()
  {
    String dir = pickADirectory();
    setMediaPath(dir);
  }
}
