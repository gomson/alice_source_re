package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;









public class MoviePlayer
{
  private JFrame frame = new JFrame(Messages.getString("Movie_Player"));
  private JLabel frameLabel = new JLabel(Messages.getString("No_images_yet__"));
  private AnimationPanel animationPanel = null;
  private String dir = null;
  






  public MoviePlayer(List pictureList)
  {
    animationPanel = new AnimationPanel(pictureList);
    Picture p = (Picture)pictureList.get(0);
    String fileName = p.getFileName();
    File f = new File(fileName);
    dir = (f.getParent() + "/");
    init();
  }
  





  public MoviePlayer(String directory)
  {
    animationPanel = new AnimationPanel(directory);
    dir = directory;
    init();
  }
  





  public MoviePlayer()
  {
    SimpleOutput.showInformation(
      Messages.getString("Please_pick_a_") + Messages.getString("directory_that_contains_the_JPEG_frames"));
    String directory = FileChooser.pickADirectory();
    dir = directory;
    animationPanel = new AnimationPanel(directory);
    init();
  }
  





  public void showNext()
  {
    animationPanel.showNext();
    frameLabel.setText(Messages.getString("Frame_Number_") + 
      animationPanel.getCurrIndex());
    frame.repaint();
  }
  



  public void showPrevious()
  {
    animationPanel.showPrev();
    frameLabel.setText(Messages.getString("Frame_Number_") + 
      animationPanel.getCurrIndex());
    frame.repaint();
  }
  



  public void playMovie()
  {
    frameLabel.setText(Messages.getString("Playing_Movie"));
    frame.repaint();
    animationPanel.showAll();
    frameLabel.setText(Messages.getString("Frame_Number_") + 
      animationPanel.getCurrIndex());
    frame.repaint();
  }
  





  public void playMovie(int framesPerSecond)
  {
    animationPanel.setFramesPerSec(framesPerSecond);
    playMovie();
  }
  
  public void setFrameRate(int rate)
  {
    animationPanel.setFramesPerSec(rate);
  }
  




  public void delAllBefore()
  {
    animationPanel.removeAllBefore();
  }
  




  public void delAllAfter()
  {
    animationPanel.removeAllAfter();
  }
  





  public void writeQuicktime()
  {
    MovieWriter writer = new MovieWriter(animationPanel.getFramesPerSec(), 
      dir);
    writer.writeQuicktime();
  }
  




  public void writeAVI()
  {
    MovieWriter writer = new MovieWriter(animationPanel.getFramesPerSec(), 
      dir);
    writer.writeAVI();
  }
  




  public void addPicture(Picture picture)
  {
    animationPanel.add(picture);
    showNext();
  }
  




  private void init()
  {
    frame.setDefaultCloseOperation(3);
    Container container = frame.getContentPane();
    container.setLayout(new BorderLayout());
    JPanel buttonPanel = new JPanel();
    

    container.add(animationPanel, "Center");
    

    JPanel labelPanel = new JPanel();
    labelPanel.add(frameLabel);
    container.add(labelPanel, "North");
    

    container.add(new ButtonPanel(this), "South");
    

    frame.pack();
    

    frame.setVisible(true);
  }
  




  public void setVisible(boolean flag)
  {
    frame.setVisible(flag);
  }
  
  public static void main(String[] args)
  {
    MoviePlayer moviePlayer = 
      new MoviePlayer();
    
    moviePlayer.playMovie(16);
  }
}
