package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D.Float;
import java.io.PrintStream;
import java.util.Vector;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;









public class SoundExplorer
  implements MouseMotionListener, ActionListener, MouseListener, LineListener
{
  private static final String zoomInHint = Messages.getString("Click_to_see_all_the_samples__the_number_of_samples_between_pixels_is_1_");
  
  private boolean DEBUG = false;
  

  private JFrame soundFrame;
  

  private JPanel playPanel;
  

  private JScrollPane scrollSound;
  

  private JPanel soundPanel;
  

  private SimpleSound sound;
  

  private boolean inStereo;
  

  private JLabel startIndexLabel;
  

  private JLabel stopIndexLabel;
  

  private JPanel buttonPanel;
  

  private JButton playEntireButton;
  

  private JButton playSelectionButton;
  

  private JButton playBeforeButton;
  

  private JButton playAfterButton;
  

  private JButton clearSelectionButton;
  

  private JButton stopButton;
  

  private boolean selectionPrevState;
  

  private JPanel leftSoundPanel;
  

  private JPanel rightSoundPanel;
  

  private JPanel leftSampleWrapper;
  

  private JPanel rightSampleWrapper;
  

  private SamplingPanel leftSamplePanel;
  

  private SamplingPanel rightSamplePanel;
  

  private JPanel infoPanel;
  

  private JLabel indexLabel;
  

  private JTextField numSamplesPerPixelField;
  
  private JTextField indexValue;
  
  private JLabel leftSampleLabel;
  
  private JTextField leftSampleValue;
  
  private JLabel rightSampleLabel;
  
  private JTextField rightSampleValue;
  
  private JPanel zoomButtonPanel;
  
  private JButton zoomButton;
  
  private JButton prevButton;
  
  private JButton nextButton;
  
  private JButton lastButton;
  
  private JButton firstButton;
  
  private int zoomOutWidth;
  
  private int zoomInWidth;
  
  private int sampleWidth;
  
  private int sampleHeight;
  
  private int soundPanelHeight;
  
  private float framesPerPixel;
  
  private int currentPixelPosition;
  
  private int base = 0;
  
  private int mousePressed;
  
  private int mouseReleased;
  
  private int mousePressedX;
  
  private int mouseReleasedX;
  private boolean mouseDragged;
  private int startFrame;
  private int stopFrame;
  private int selectionStart;
  private int selectionStop;
  private static final String currentIndexText = Messages.getString("Current_Index__");
  private static final String startIndexText = Messages.getString("Start_Index__");
  private static final String stopIndexText = Messages.getString("Stop_Index__");
  private static final Color selectionColor = Color.gray;
  private static final Color backgroundColor = Color.black;
  private static final Color waveColor = Color.white;
  private static final Color barColor = Color.cyan;
  

  private static String leftSampleText = Messages.getString("Sample_Value__");
  private static String rightSampleText = Messages.getString("Right__Bottom__Sample_Value__");
  





  public SoundExplorer(SimpleSound sound, boolean inStereo)
  {
    this.sound = sound;
    this.inStereo = inStereo;
    
    if (inStereo) {
      leftSampleText = Messages.getString("Left__Top__Sample_Value__");
    }
    



    sound.setSoundExplorer(this);
    




    mouseDragged = false;
    selectionStart = -1;
    selectionStop = -1;
    

    zoomOutWidth = 640;
    zoomInWidth = sound.getLengthInFrames();
    sampleWidth = zoomOutWidth;
    framesPerPixel = (sound.getLengthInFrames() / sampleWidth);
    sampleHeight = 201;
    






    currentPixelPosition = 0;
    

    createWindow();
  }
  




  private void catchException(Exception ex)
  {
    System.err.println(ex.getMessage());
  }
  




  public void setTitle(String s)
  {
    soundFrame.setTitle(s);
  }
  



  private void createWindow()
  {
    String fileName = sound.getFileName();
    if (fileName == null) {
      fileName = Messages.getString("no_file_name");
    }
    soundFrame = new JFrame(fileName);
    















    Container frameContainer = soundFrame.getContentPane();
    
    frameContainer.setLayout(new BorderLayout());
    soundFrame.setDefaultCloseOperation(2);
    


    createPlayPanel();
    frameContainer.add(playPanel, "North");
    

    createSoundPanel();
    

    scrollSound = new JScrollPane();
    scrollSound.setViewportView(soundPanel);
    frameContainer.add(scrollSound, "Center");
    





    createInfoPanel();
    
    frameContainer.add(infoPanel, "South");
    

    soundFrame.pack();
    soundFrame.setResizable(false);
    soundFrame.setVisible(true);
  }
  








  private JButton makeButton(String name, boolean enabled, JPanel panel)
  {
    JButton j = new JButton(name);
    j.addActionListener(this);
    j.setEnabled(enabled);
    panel.add(j);
    return j;
  }
  



  private void clearSelection()
  {
    selectionStart = -1;
    selectionStop = -1;
    startIndexLabel.setText(startIndexText + "N/A");
    stopIndexLabel.setText(stopIndexText + "N/A");
    soundFrame.getContentPane().repaint();
    playSelectionButton.setEnabled(false);
    clearSelectionButton.setEnabled(false);
  }
  






  private void createPlayPanel()
  {
    playPanel = new JPanel();
    
    playPanel.setLayout(new BorderLayout());
    

    JPanel selectionPanel = new JPanel();
    startIndexLabel = new JLabel(startIndexText + "N/A");
    stopIndexLabel = new JLabel(stopIndexText + "N/A");
    playSelectionButton = makeButton(Messages.getString("Play_Selection"), false, selectionPanel);
    clearSelectionButton = makeButton(Messages.getString("Clear_Selection"), false, selectionPanel);
    clearSelectionButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SoundExplorer.this.clearSelection();
      }
    });
    selectionPanel.add(startIndexLabel);
    selectionPanel.add(stopIndexLabel);
    

    buttonPanel = new JPanel();
    playEntireButton = makeButton(Messages.getString("Play_Entire_Sound"), true, buttonPanel);
    selectionPrevState = false;
    playBeforeButton = makeButton(Messages.getString("Play_Before"), false, buttonPanel);
    playAfterButton = makeButton(Messages.getString("Play_After"), true, buttonPanel);
    stopButton = makeButton(Messages.getString("Stop"), false, buttonPanel);
    

    playBeforeButton.setToolTipText(Messages.getString("Play_sound_up_to_the_current_index"));
    playAfterButton.setToolTipText(Messages.getString("Play_sound_starting_at_the_current_index"));
    playEntireButton.setToolTipText(Messages.getString("Play_the_entire_sound"));
    playSelectionButton.setToolTipText(Messages.getString("Play_sound_between_start_and_stop_index"));
    stopButton.setToolTipText(Messages.getString("Stop_playing_the_sound"));
    clearSelectionButton.setToolTipText(Messages.getString("Click_to_clear__remove__the_selection"));
    

    playPanel.add(buttonPanel, "North");
    playPanel.add(selectionPanel, "South");
  }
  





  private void createSoundPanel()
  {
    soundPanel = new JPanel();
    if (inStereo) {
      soundPanel.setLayout(new GridLayout(2, 1));
    } else {
      soundPanel.setLayout(new GridLayout(1, 1));
    }
    





    leftSoundPanel = new JPanel();
    leftSoundPanel.setLayout(new BorderLayout());
    leftSoundPanel.setPreferredSize(
    
      new Dimension(sampleWidth, sampleHeight));
    





    leftSampleWrapper = new JPanel();
    leftSamplePanel = new SamplingPanel(true);
    leftSamplePanel.addMouseMotionListener(this);
    leftSamplePanel.addMouseListener(this);
    leftSampleWrapper.add(leftSamplePanel);
    leftSampleWrapper
    
      .setPreferredSize(new Dimension(sampleWidth, sampleHeight));
    








    leftSoundPanel.add(leftSampleWrapper, "North");
    
    soundPanel.add(leftSoundPanel);
    

    soundPanelHeight = sampleHeight;
    
    if (inStereo)
    {
      rightSoundPanel = new JPanel();
      rightSoundPanel.setLayout(new BorderLayout());
      rightSoundPanel.setPreferredSize(
      
        new Dimension(sampleWidth, sampleHeight));
      
      rightSampleWrapper = new JPanel();
      rightSamplePanel = new SamplingPanel(false);
      rightSamplePanel.addMouseMotionListener(this);
      rightSamplePanel.addMouseListener(this);
      rightSampleWrapper.add(rightSamplePanel);
      rightSampleWrapper.setPreferredSize(
      
        new Dimension(sampleWidth, sampleHeight));
      

      rightSoundPanel.add(rightSampleWrapper, "North");
      
      soundPanel.add(rightSoundPanel);
      

      soundPanelHeight = (2 * sampleHeight);
    }
    
    soundPanel.setPreferredSize(new Dimension(zoomOutWidth, soundPanelHeight));
    soundPanel.setSize(soundPanel.getPreferredSize());
  }
  




  private void updateIndexValues()
  {
    int curFrame = (int)(currentPixelPosition * framesPerPixel);
    

    indexValue.setText(Integer.toString(curFrame + base));
    

    if (numSamplesPerPixelField != null) {
      numSamplesPerPixelField.setText(Integer.toString((int)framesPerPixel));
    }
    
    try
    {
      leftSampleValue.setText(Integer.toString(sound.getLeftSample(curFrame)));
      if (inStereo) {
        rightSampleValue.setText(Integer.toString(sound.getRightSample(curFrame)));
      }
    }
    catch (Exception ex) {
      catchException(ex);
    }
  }
  






  private void setUpIndexPanel(JPanel indexPanel)
  {
    JPanel topPanel = new JPanel();
    Box vertBox = Box.createVerticalBox();
    

    Icon prevIcon = new ImageIcon(SoundExplorer.class.getResource("leftArrow.gif"), 
      Messages.getString("previous_index"));
    Icon nextIcon = new ImageIcon(SoundExplorer.class.getResource("rightArrow.gif"), 
      Messages.getString("next_index"));
    Icon firstIcon = new ImageIcon(SoundExplorer.class.getResource("endLeft.gif"), 
      Messages.getString("first_index"));
    Icon lastIcon = new ImageIcon(SoundExplorer.class.getResource("endRight.gif"), 
      Messages.getString("last_index"));
    

    prevButton = new JButton(prevIcon);
    firstButton = new JButton(firstIcon);
    nextButton = new JButton(nextIcon);
    lastButton = new JButton(lastIcon);
    

    prevButton.setToolTipText(Messages.getString("Click_to_view_previous_index__sample_at_previous_pixel_"));
    firstButton.setToolTipText(Messages.getString("Click_to_view_first_index__sample_at_first_pixel_"));
    nextButton.setToolTipText(Messages.getString("Click_to_view_next_index__sample_at_next_pixel_"));
    lastButton.setToolTipText(Messages.getString("Click_to_view_last_index__sample_at_last_pixel_"));
    

    prevButton.setPreferredSize(new Dimension(prevIcon.getIconWidth() + 2, 
      prevIcon.getIconHeight() + 2));
    firstButton.setPreferredSize(new Dimension(firstIcon.getIconWidth() + 2, 
      firstIcon.getIconHeight() + 2));
    nextButton.setPreferredSize(new Dimension(nextIcon.getIconWidth() + 2, 
      nextIcon.getIconHeight() + 2));
    lastButton.setPreferredSize(new Dimension(lastIcon.getIconWidth() + 2, 
      lastIcon.getIconHeight() + 2));
    


    prevButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        currentPixelPosition -= 1;
        if (currentPixelPosition < 0)
          currentPixelPosition = 0;
        SoundExplorer.this.updateIndexValues();
        checkScroll();
        soundFrame.getContentPane().repaint();
      }
      

    });
    nextButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        currentPixelPosition += 1;
        if (currentPixelPosition * framesPerPixel >= sound.getNumSamples())
          currentPixelPosition = ((int)((sound.getNumSamples() - 1) / framesPerPixel));
        SoundExplorer.this.updateIndexValues();
        checkScroll();
        soundFrame.getContentPane().repaint();
      }
      

    });
    firstButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        currentPixelPosition = 0;
        SoundExplorer.this.updateIndexValues();
        checkScroll();
        soundFrame.getContentPane().repaint();
      }
      

    });
    lastButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        currentPixelPosition = ((int)((sound.getNumSamples() - 1) / framesPerPixel));
        SoundExplorer.this.updateIndexValues();
        checkScroll();
        soundFrame.getContentPane().repaint();
      }
      

    });
    indexValue = new JTextField(Integer.toString(0), 8);
    indexValue.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        SoundExplorer.this.handleZoomIn(Integer.parseInt(indexValue.getText()));
        

        SoundExplorer.this.updateIndexValues();
      }
      

    });
    leftSampleValue = new JTextField(8);
    leftSampleValue.setEditable(false);
    rightSampleValue = new JTextField(8);
    rightSampleValue.setEditable(false);
    

    indexLabel = new JLabel(currentIndexText);
    leftSampleLabel = new JLabel(leftSampleText);
    rightSampleLabel = new JLabel(rightSampleText);
    updateIndexValues();
    

    topPanel.add(firstButton);
    topPanel.add(prevButton);
    topPanel.add(indexLabel);
    topPanel.add(indexValue);
    topPanel.add(leftSampleLabel);
    topPanel.add(leftSampleValue);
    if (inStereo) {
      topPanel.add(rightSampleLabel);
      topPanel.add(rightSampleValue);
    }
    topPanel.add(nextButton);
    topPanel.add(lastButton);
    

    JPanel bottomPanel = new JPanel();
    bottomPanel.add(new JLabel(Messages.getString("The_number_of_samples_between_pixels__")));
    numSamplesPerPixelField = new JTextField(Integer.toString((int)framesPerPixel), 8);
    numSamplesPerPixelField.setToolTipText(Messages.getString("Click_here_to_zoom_in__decrease__or_out__increase__"));
    numSamplesPerPixelField.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        SoundExplorer.this.handleFramesPerPixel(Integer.parseInt(numSamplesPerPixelField.getText()));
        

        SoundExplorer.this.updateIndexValues();
      }
    });
    bottomPanel.add(numSamplesPerPixelField);
    


    vertBox.add(topPanel);
    vertBox.add(bottomPanel);
    

    indexPanel.add(vertBox);
  }
  






  private void createInfoPanel()
  {
    infoPanel = new JPanel();
    infoPanel.setLayout(new BorderLayout());
    

    JPanel indexPanel = new JPanel();
    indexPanel.setLayout(new FlowLayout());
    setUpIndexPanel(indexPanel);
    

    zoomButtonPanel = new JPanel();
    zoomButton = makeButton(Messages.getString("Zoom_In"), true, zoomButtonPanel);
    zoomButton.setToolTipText(zoomInHint);
    
    infoPanel.add("North", indexPanel);
    infoPanel.add("South", zoomButtonPanel);
  }
  





  public void mouseClicked(MouseEvent e)
  {
    currentPixelPosition = e.getX();
    
    if (currentPixelPosition == 0)
    {
      playBeforeButton.setEnabled(false);
      playAfterButton.setEnabled(true);
    }
    else if (currentPixelPosition < sampleWidth)
    {
      playBeforeButton.setEnabled(true);
      playAfterButton.setEnabled(true);
    }
    else if (currentPixelPosition == sampleWidth)
    {
      playBeforeButton.setEnabled(true);
      playAfterButton.setEnabled(false);
    }
    
    updateIndexValues();
    soundPanel.repaint();
  }
  




  public void mousePressed(MouseEvent e)
  {
    mousePressedX = e.getX();
  }
  




  public void mouseReleased(MouseEvent e)
  {
    mouseReleasedX = e.getX();
    
    if (mouseDragged)
    {

      mousePressed = mousePressedX;
      mouseReleased = mouseReleasedX;
      
      if (mousePressed > mouseReleased)
      {
        int temp = mousePressed;
        mousePressed = mouseReleased;
        mouseReleased = temp;
      }
      
      startFrame = ((int)(mousePressed * framesPerPixel));
      stopFrame = ((int)(mouseReleased * framesPerPixel));
      

      if (stopFrame >= sound.getLengthInFrames()) {
        stopFrame = sound.getLengthInFrames();
      }
      
      if (startFrame < 0) {
        startFrame = 0;
      }
      
      startIndexLabel.setText(startIndexText + startFrame);
      stopIndexLabel.setText(stopIndexText + stopFrame);
      

      selectionStart = mousePressed;
      selectionStop = mouseReleased;
      
      soundPanel.repaint();
      playSelectionButton.setEnabled(true);
      clearSelectionButton.setEnabled(true);
      mouseDragged = false;
    }
  }
  





  public void mouseEntered(MouseEvent e) {}
  





  public void mouseExited(MouseEvent e) {}
  





  public void mouseDragged(MouseEvent e)
  {
    mouseDragged = true;
    


    mouseReleased(e);
  }
  





  public void mouseMoved(MouseEvent e) {}
  




  public void update(LineEvent e)
  {
    if (e.getType().equals(LineEvent.Type.OPEN))
    {
      playEntireButton.setEnabled(false);
      playBeforeButton.setEnabled(false);
      playAfterButton.setEnabled(false);
      selectionPrevState = playSelectionButton.isEnabled();
      playSelectionButton.setEnabled(false);
      clearSelectionButton.setEnabled(false);
      stopButton.setEnabled(true);
    }
    if (e.getType().equals(LineEvent.Type.CLOSE))
    {
      playEntireButton.setEnabled(true);
      playSelectionButton.setEnabled(selectionPrevState);
      clearSelectionButton.setEnabled(selectionPrevState);
      stopButton.setEnabled(false);
      if (currentPixelPosition == 0)
      {
        playBeforeButton.setEnabled(false);
        playAfterButton.setEnabled(true);
      }
      else if (currentPixelPosition < sampleWidth)
      {
        playBeforeButton.setEnabled(true);
        playAfterButton.setEnabled(true);
      }
      else if (currentPixelPosition == sampleWidth)
      {
        playBeforeButton.setEnabled(true);
        playAfterButton.setEnabled(false);
      }
    }
  }
  





  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand() == Messages.getString("Play_Entire_Sound"))
    {
      try
      {
        sound.play();
      }
      catch (Exception ex)
      {
        catchException(ex);
      }
      
    } else if (e.getActionCommand() == Messages.getString("Play_Selection"))
    {
      try
      {
        sound.playAtRateInRange(1.0F, startFrame, stopFrame);
      }
      catch (Exception ex)
      {
        catchException(ex);
      }
      
    } else if (e.getActionCommand().equals(Messages.getString("Stop")))
    {

      for (int i = 0; i < sound.getPlaybacks().size(); i++)
      {

        ((Playback)sound.getPlaybacks().elementAt(i)).stopPlaying();
      }
      
    } else if (e.getActionCommand().equals(Messages.getString("Zoom_In")))
    {
      handleZoomIn(true);
    }
    else if (e.getActionCommand().equals(Messages.getString("Zoom_Out")))
    {
      handleZoomOut();
    }
    else if (e.getActionCommand().equals(Messages.getString("Play_Before")))
    {
      try
      {
        sound.playAtRateInRange(
          1.0F, 0, (int)(currentPixelPosition * framesPerPixel));
      }
      catch (Exception ex)
      {
        catchException(ex);
      }
      
    } else if (e.getActionCommand().equals(Messages.getString("Play_After")))
    {
      try
      {
        sound.playAtRateInRange(
          1.0F, (int)(currentPixelPosition * framesPerPixel), 
          sound.getLengthInFrames() - 1);
      }
      catch (Exception ex)
      {
        catchException(ex);
      }
    }
  }
  











  public void checkScroll()
  {
    if (sampleWidth != zoomOutWidth)
    {

      JViewport viewport = scrollSound.getViewport();
      Rectangle rect = viewport.getViewRect();
      int rectMinX = (int)rect.getX();
      int rectWidth = (int)rect.getWidth();
      int rectMaxX = rectMinX + rectWidth - 1;
      

      int maxIndex = sound.getLength() - rectWidth - 1;
      

      if ((currentPixelPosition < rectMinX) || 
        (currentPixelPosition > rectMaxX))
      {


        int barXPos = currentPixelPosition - rectWidth / 2;
        int barYPos = (int)(sampleHeight - rect.getHeight()) / 2;
        

        if (barXPos < 0) {
          barXPos = 0;
        } else if (barXPos > maxIndex) {
          barXPos = maxIndex;
        }
        
        viewport.setViewPosition(new Point(barXPos, barYPos));
      }
    }
  }
  




  private void handleZoomIn(boolean checkScrollFlag)
  {
    zoomButton.setText(Messages.getString("Zoom_Out"));
    zoomButton.setToolTipText(Messages.getString("Click_to_zoom_out__see_the_whole_sound_"));
    

    currentPixelPosition = ((int)(currentPixelPosition * framesPerPixel));
    selectionStart = ((int)(selectionStart * framesPerPixel));
    selectionStop = ((int)(selectionStop * framesPerPixel));
    
    sampleWidth = zoomInWidth;
    framesPerPixel = (sound.getLengthInFrames() / sampleWidth);
    
    soundPanel.setPreferredSize(new Dimension(zoomInWidth, 
      soundPanel.getHeight()));
    soundPanel.setSize(soundPanel.getPreferredSize());
    
    leftSoundPanel.setPreferredSize(new Dimension(zoomInWidth, 
      leftSoundPanel.getHeight()));
    leftSoundPanel.setSize(leftSoundPanel.getPreferredSize());
    
    leftSampleWrapper.setPreferredSize(new Dimension(zoomInWidth, 
      leftSampleWrapper.getHeight()));
    leftSampleWrapper.setSize(leftSampleWrapper.getPreferredSize());
    leftSamplePanel.setPreferredSize(new Dimension(sampleWidth, 
      sampleHeight));
    leftSamplePanel.setSize(leftSamplePanel.getPreferredSize());
    
    leftSamplePanel.createWaveForm(true);
    
    if (inStereo)
    {
      rightSoundPanel.setPreferredSize(
        new Dimension(zoomInWidth, 
        rightSoundPanel.getHeight()));
      rightSoundPanel.setSize(
        rightSoundPanel.getPreferredSize());
      
      rightSampleWrapper.setPreferredSize(
        new Dimension(zoomInWidth, 
        rightSampleWrapper.getHeight()));
      rightSampleWrapper.setSize(
        rightSampleWrapper.getPreferredSize());
      
      rightSamplePanel.setPreferredSize(
        new Dimension(zoomInWidth, 
        rightSamplePanel.getHeight()));
      rightSamplePanel.setSize(
        rightSamplePanel.getPreferredSize());
      
      rightSamplePanel.createWaveForm(false);
    }
    


    scrollSound.revalidate();
    

    updateIndexValues();
    

    if (checkScrollFlag) {
      checkScroll();
    }
  }
  



  private void handleZoomIn(int index)
  {
    if (index % framesPerPixel != 0.0F)
    {
      handleZoomIn(false);
    }
    

    currentPixelPosition = ((int)(index / framesPerPixel));
    

    checkScroll();
    

    soundPanel.repaint();
  }
  




  private void handleZoomOut()
  {
    zoomButton.setText(Messages.getString("Zoom_In"));
    zoomButton.setToolTipText(zoomInHint);
    
    sampleWidth = zoomOutWidth;
    framesPerPixel = (sound.getLengthInFrames() / sampleWidth);
    
    int divisor = sound.getLengthInFrames() / sampleWidth;
    currentPixelPosition /= divisor;
    selectionStart /= divisor;
    selectionStop /= divisor;
    
    soundPanel.setPreferredSize(
      new Dimension(zoomOutWidth, 
      soundPanel.getHeight()));
    soundPanel.setSize(soundPanel.getPreferredSize());
    
    leftSoundPanel.setPreferredSize(
      new Dimension(zoomOutWidth, 
      leftSoundPanel.getHeight()));
    leftSoundPanel.setSize(leftSoundPanel.getPreferredSize());
    
    leftSampleWrapper.setPreferredSize(
      new Dimension(zoomOutWidth, 
      leftSampleWrapper.getHeight()));
    leftSampleWrapper.setSize(
      leftSampleWrapper.getPreferredSize());
    
    leftSamplePanel.setPreferredSize(
      new Dimension(sampleWidth, sampleHeight));
    leftSamplePanel.setSize(leftSamplePanel.getPreferredSize());
    
    leftSamplePanel.createWaveForm(true);
    
    if (inStereo)
    {

      rightSoundPanel.setPreferredSize(
        new Dimension(zoomOutWidth, 
        rightSoundPanel.getHeight()));
      rightSoundPanel.setSize(
        rightSoundPanel.getPreferredSize());
      
      rightSampleWrapper.setPreferredSize(
        new Dimension(zoomOutWidth, 
        rightSampleWrapper.getHeight()));
      rightSampleWrapper.setSize(
        rightSampleWrapper.getPreferredSize());
      
      rightSamplePanel.setPreferredSize(
        new Dimension(sampleWidth, sampleHeight));
      rightSamplePanel.setSize(
        rightSamplePanel.getPreferredSize());
      
      rightSamplePanel.createWaveForm(false);
    }
    
    updateIndexValues();
    
    soundPanel.repaint();
  }
  







  private void handleFramesPerPixel(int numFrames)
  {
    int currIndex = (int)(currentPixelPosition * framesPerPixel);
    sampleWidth = (sound.getLengthInFrames() / numFrames);
    framesPerPixel = numFrames;
    
    int divisor = sound.getLengthInFrames() / sampleWidth;
    currentPixelPosition = (currIndex / divisor);
    selectionStart /= divisor;
    selectionStop /= divisor;
    
    soundPanel.setPreferredSize(
      new Dimension(sampleWidth, 
      soundPanel.getHeight()));
    soundPanel.setSize(soundPanel.getPreferredSize());
    
    leftSoundPanel.setPreferredSize(
      new Dimension(sampleWidth, 
      leftSoundPanel.getHeight()));
    leftSoundPanel.setSize(leftSoundPanel.getPreferredSize());
    
    leftSampleWrapper.setPreferredSize(
      new Dimension(sampleWidth, 
      leftSampleWrapper.getHeight()));
    leftSampleWrapper.setSize(
      leftSampleWrapper.getPreferredSize());
    
    leftSamplePanel.setPreferredSize(
      new Dimension(sampleWidth, sampleHeight));
    leftSamplePanel.setSize(leftSamplePanel.getPreferredSize());
    
    leftSamplePanel.createWaveForm(true);
    
    if (inStereo)
    {

      rightSoundPanel.setPreferredSize(
        new Dimension(sampleWidth, 
        rightSoundPanel.getHeight()));
      rightSoundPanel.setSize(
        rightSoundPanel.getPreferredSize());
      
      rightSampleWrapper.setPreferredSize(
        new Dimension(sampleWidth, 
        rightSampleWrapper.getHeight()));
      rightSampleWrapper.setSize(
        rightSampleWrapper.getPreferredSize());
      
      rightSamplePanel.setPreferredSize(
        new Dimension(sampleWidth, sampleHeight));
      rightSamplePanel.setSize(
        rightSamplePanel.getPreferredSize());
      
      rightSamplePanel.createWaveForm(false);
    }
    

    scrollSound.revalidate();
    

    updateIndexValues();
    

    checkScroll();
    
    soundPanel.repaint();
  }
  





  public void setBase(int base)
  {
    this.base = base;
  }
  



  private class SamplingPanel
    extends JPanel
  {
    private boolean forLeftSample;
    

    private Vector points;
    


    public SamplingPanel(boolean inputForLeftSample)
    {
      forLeftSample = inputForLeftSample;
      
      if (DEBUG) {
        System.out.println("creating_new_sampling_panel___n_tfor_left_sample__" + 
          forLeftSample + 
          "_n_tsampleWidth__" + sampleWidth + 
          "_n_tsampleHeight__" + sampleHeight);
      }
      setBackground(SoundExplorer.backgroundColor);
      setPreferredSize(new Dimension(sampleWidth, sampleHeight));
      setSize(getPreferredSize());
      if (DEBUG) {
        System.out.println("_tSample_panel_preferred_size__" + 
          getPreferredSize() + "_n_tSample_panel_size__" + getSize());
      }
      points = new Vector();
      createWaveForm(forLeftSample);
    }
    






    public void createWaveForm(boolean forLeftSample)
    {
      AudioFormat format = sound.getAudioFileFormat().getFormat();
      
      float maxValue;
      if (format.getSampleSizeInBits() == 8)
      {
        maxValue = (float)Math.pow(2.0D, 7.0D);
      } else { float maxValue;
        if (format.getSampleSizeInBits() == 16)
        {
          maxValue = (float)Math.pow(2.0D, 15.0D);
        } else { float maxValue;
          if (format.getSampleSizeInBits() == 24)
          {
            maxValue = (float)Math.pow(2.0D, 23.0D);
          } else { float maxValue;
            if (format.getSampleSizeInBits() == 32)
            {
              maxValue = (float)Math.pow(2.0D, 31.0D);
            }
            else
            {
              try
              {
                sound.printError(Messages.getString("InvalidSampleSize"));
              }
              catch (Exception ex)
              {
                SoundExplorer.this.catchException(ex);
              }
              return;
            } } } }
      float maxValue;
      points.clear();
      
      for (int pixel = 0; pixel < sampleWidth; pixel++)
      {




        if (forLeftSample)
        {
          try
          {
            sampleValue = 
              sound.getLeftSample((int)(pixel * framesPerPixel));
          }
          catch (Exception ex) {
            float sampleValue;
            SoundExplorer.this.catchException(ex); return;
          }
          
        }
        else
        {
          try
          {
            sampleValue = 
              sound.getRightSample((int)(pixel * framesPerPixel));
          }
          catch (Exception ex) {
            float sampleValue;
            SoundExplorer.this.catchException(ex); return;
          }
        }
        
        float sampleValue;
        float y = (float)Math.floor(sampleHeight / 2) - 
          sampleValue * ((float)Math.floor(sampleHeight / 2) / 
          maxValue);
        
        points.add(new Point2D.Float(pixel, y));
      }
      
      if (DEBUG)
        System.out.println("number of points: " + points.size());
      repaint();
    }
    







    public void paintComponent(Graphics g)
    {
      Rectangle rectToPaint = g.getClipBounds();
      
      if (DEBUG)
      {
        System.out.println("Repainting: " + rectToPaint);
        System.out.println("SampleWidth: " + sampleWidth);
        System.out.println("framesPerPixel: " + framesPerPixel);
        System.out.println("Sample_panel_size: " + getSize());
        System.out.println("SamplePanel_Width: " + getWidth());
        System.out.println("SamplePanel_Height: " + getHeight());
      }
      

      Graphics2D g2 = (Graphics2D)g;
      g2.setBackground(SoundExplorer.backgroundColor);
      g2.clearRect((int)rectToPaint.getX(), (int)rectToPaint.getY(), 
        (int)rectToPaint.getWidth(), (int)rectToPaint.getHeight());
      

      if ((selectionStart != -1) && (selectionStop != -1))
      {
        g2.setBackground(SoundExplorer.selectionColor);
        g2.clearRect(selectionStart, 0, 
          selectionStop - selectionStart + 1, sampleHeight);
      }
      

      g2.setColor(SoundExplorer.waveColor);
      for (int i = (int)rectToPaint.getX(); 
            i < rectToPaint.getX() + rectToPaint.getWidth() - 1.0D; i++)
      {
        g2.draw(
          new Line2D.Float((Point2D.Float)points.elementAt(i), 
          (Point2D.Float)points.elementAt(i + 1)));
      }
      

      g2.setColor(SoundExplorer.barColor);
      g2.setStroke(new BasicStroke(1.0F));
      g2.draw(new Line2D.Double(rectToPaint.getX(), 
        Math.floor(sampleHeight / 2), 
        rectToPaint.getX() + rectToPaint.getWidth() - 1.0D, 
        Math.floor(sampleHeight / 2)));
      

      if ((rectToPaint.getX() < currentPixelPosition) && 
        (currentPixelPosition < rectToPaint.getX() + rectToPaint.getWidth() - 1.0D))
      {
        g2.setColor(SoundExplorer.barColor);
        g2.setStroke(new BasicStroke(1.0F));
        g2.draw(new Line2D.Double(currentPixelPosition, 0.0D, 
          currentPixelPosition, sampleHeight));
      }
    }
  }
}
