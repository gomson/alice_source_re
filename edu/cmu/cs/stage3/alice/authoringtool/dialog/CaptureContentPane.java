package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.Actions;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.StyleStream;
import edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.response.SoundResponse;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import movieMaker.FrameSequencer;
import movieMaker.MovieCapturer;
import movieMaker.MovieWriter;
import movieMaker.SoundStorage;

public class CaptureContentPane extends ContentPane
{
  protected JPanel renderPanel = new JPanel(
    new java.awt.BorderLayout());
  protected JPanel buttonPanel = new JPanel(
    new GridBagLayout());
  protected JSplitPane stdOutSplitPane = new JSplitPane(
    0);
  
  protected Configuration config = Configuration.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class
    .getPackage());
  protected double aspectRatio;
  protected AuthoringTool authoringTool;
  protected boolean showStdOut = false;
  protected int stdOutHeight = 100;
  protected int watcherWidth = 200;
  protected OutputComponent stdOutOutputComponent;
  protected edu.cmu.cs.stage3.alice.authoringtool.util.WatcherPanel watcherPanel;
  protected JScrollPane watcherScrollPane;
  protected JSplitPane watcherSplitPane = new JSplitPane(
    1);
  protected JButton pauseButton;
  protected JButton resumeButton;
  protected JButton restartButton;
  protected JButton stopButton;
  protected JButton takePictureButton;
  protected JSlider speedSlider;
  protected JLabel speedLabel;
  protected RenderCanvasFocusListener renderCanvasFocusListener = new RenderCanvasFocusListener();
  protected boolean shiftIsDown = false;
  
  protected JScrollPane textScrollPane;
  protected StyledStreamTextPane detailTextPane = new StyledStreamTextPane();
  
  protected boolean doNotListenToSpeedSlider = false;
  protected boolean doNotListenToResize = false;
  protected final int dividerSize = 8;
  
  protected String title;
  
  protected ActionListener okActionListener;
  
  protected JPanel captureBar = new JPanel(
    new GridBagLayout());
  protected JPanel exportBar = new JPanel(
    new GridBagLayout());
  
  protected JFrame statusFrame;
  
  protected JLabel recordLabel;
  
  protected JButton startCaptureButton;
  
  protected JButton stopCaptureButton;
  protected JButton clearButton;
  protected JLabel timeLabel;
  protected JButton encodeButton;
  protected JTextField fileName;
  protected JComboBox fileType;
  protected JLabel statusLabel;
  protected movieMaker.StartMovieCapture movieCapture = null;
  
  private Thread capturing;
  private Thread pulse;
  private Thread timerThread;
  private Thread writing;
  private Thread compressing;
  private boolean pulsing = false;
  private boolean running = false;
  private boolean endCapturing = false;
  private MovieCapturer videoHandler;
  private String exportDirectory;
  private FrameSequencer frameSequencer;
  
  protected class TextOutputDocumentListener implements javax.swing.event.DocumentListener
  {
    protected TextOutputDocumentListener() {}
    
    public void insertUpdate(final DocumentEvent ev) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            String textUpdate = ev.getDocument().getText(
              ev.getOffset(), ev.getLength());
            detailTextPane.getDocument().insertString(
              detailTextPane.getDocument().getLength(), 
              textUpdate, detailTextPane.stdOutStyle);
          }
          catch (Exception localException) {}
          CaptureContentPane.TextOutputDocumentListener.this.update();
        }
      });
    }
    
    public void removeUpdate(DocumentEvent ev) {
      update();
    }
    
    public void changedUpdate(DocumentEvent ev) {
      update();
    }
    
    private void update() {
      if (!showStdOut) {
        saveRenderBounds();
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            updateGUI();
            showStdOut = true;
            Component renderCanvas = getRenderCanvas();
            if (renderCanvas != null) {
              renderCanvas.requestFocus();
            }
          }
        });
      }
    }
  }
  
  protected class RenderComponentListener extends java.awt.event.ComponentAdapter {
    protected RenderComponentListener() {}
    
    public void componentResized(java.awt.event.ComponentEvent ev) {
      if ((shouldConstrainAspectOnResize()) && 
        (!doNotListenToResize) && (!endCapturing)) {
        doNotListenToResize = true;
        Rectangle oldBounds = getRenderBounds();
        Dimension newSize = renderPanel
          .getSize();
        Rectangle newBounds = new Rectangle(
          oldBounds.getLocation(), newSize);
        
        int deltaWidth = Math.abs(width - width);
        int deltaHeight = Math.abs(height - height);
        
        constrainToAspectRatio(newBounds, deltaWidth < deltaHeight);
        
        config
          .setValue(
          "rendering.renderWindowBounds", x + ", " + y + ", " + width + ", " + height);
        renderPanel.setPreferredSize(new Dimension(
          width, height));
        buttonPanel.setPreferredSize(new Dimension(
          width, buttonPanel.getHeight()));
        

        captureBar.setPreferredSize(new Dimension(
          width, captureBar.getHeight()));
        exportBar.setPreferredSize(new Dimension(
          width, exportBar.getHeight()));
        
        packDialog();
        doNotListenToResize = false;
      }
    }
  }
  
  protected TextOutputDocumentListener textListener = new TextOutputDocumentListener();
  
  protected RenderComponentListener renderResizeListener = new RenderComponentListener();
  
  public CaptureContentPane(AuthoringTool authoringTool)
  {
    this.authoringTool = authoringTool;
    stdOutOutputComponent = authoringTool.getStdOutOutputComponent();
    watcherPanel = authoringTool.getWatcherPanel();
    guiInit();
  }
  
  public void setAspectRatio(double aspectRatio) {
    this.aspectRatio = aspectRatio;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String newTitle) {
    title = newTitle;
  }
  
  protected void setRenderWindowSizeBasedOnSavedBounds() {
    Rectangle bounds = getRenderBounds();
    renderPanel.setPreferredSize(new Dimension(width, 
      height));
  }
  
  public void preDialogShow(JDialog parentDialog) {
    super.preDialogShow(parentDialog);
    final Component renderCanvas = getRenderCanvas();
    
    if (renderCanvas != null) {
      renderCanvas.addFocusListener(renderCanvasFocusListener);
      javax.swing.Timer focusTimer = new javax.swing.Timer(100, 
        new ActionListener()
        {
          public void actionPerformed(ActionEvent ev) {
            renderCanvas.requestFocus();
          }
        });
      focusTimer.setRepeats(false);
      focusTimer.start();
    }
    stdOutOutputComponent.stdOutStream.println(
      Messages.getString("Running_World"));
    stdOutOutputComponent.stdOutStream.flush();
    stdOutOutputComponent.getTextPane().getDocument()
      .addDocumentListener(textListener);
    Rectangle bounds = getRenderBounds();
    renderPanel.setPreferredSize(new Dimension(width, 
      height));
    parentDialog.setLocation(bounds.getLocation());
    showStdOut = false;
    keyMapInit();
    updateGUI();
    
    if (config.getValue("rendering.ensureRenderDialogIsOnScreen").equalsIgnoreCase("true")) {
      Dimension screenSize = 
        java.awt.Toolkit.getDefaultToolkit().getScreenSize();
      Dimension dialogSize = parentDialog.getSize();
      Point dialogLocation = parentDialog.getLocation();
      Rectangle screenRect = new Rectangle(0, 0, 
        width, height);
      
      if (!SwingUtilities.isRectangleContainingRectangle(screenRect, parentDialog.getBounds())) {
        if (x + width > width) {
          x = (width - width);
        }
        if (x < 0) {
          x = 0;
        }
        if (y + height > height) {
          y = (height - height);
        }
        if (y < 0) {
          y = 0;
        }
        

        if (config.getValue("rendering.constrainRenderDialogAspectRatio").equalsIgnoreCase("false")) {
          if (width > width) {
            Dimension renderSize = renderPanel
              .getPreferredSize();
            width -= 8;
            renderPanel.setPreferredSize(renderSize);
          }
          if (height > height) {
            Dimension renderSize = renderPanel
              .getPreferredSize();
            height -= 27;
            renderPanel.setPreferredSize(renderSize);
          }
        }
        else if ((height > height) || 
          (width > width)) {
          double windowAspect = width / 
            height;
          if (aspectRatio > windowAspect) {
            Dimension renderSize = renderPanel
              .getPreferredSize();
            width -= 8;
            height = 
              ((int)Math.round(width / aspectRatio));
            renderPanel.setPreferredSize(renderSize);
          } else {
            Dimension renderSize = renderPanel
              .getPreferredSize();
            height -= 27;
            width = 
              ((int)Math.round(height * aspectRatio));
            renderPanel.setPreferredSize(renderSize);
          }
        }
        
        parentDialog.setLocation(dialogLocation);
        renderPanel.invalidate();
        parentDialog.pack();
      }
    }
  }
  
  public void postDialogShow(JDialog parentDialog) {
    super.postDialogShow(parentDialog);
    
    authoringTool.stopWorld();
    statusFrame.setVisible(false);
    timeLabel.setBackground(Color.GREEN);
    setClear(false);
    setButtonsCapturing(false);
    
    endCapturing = false;
    running = false;
    pulsing = false;
    try
    {
      if (timerThread != null)
        timerThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    timeLabel.setText("0:00  ");
    try {
      if (capturing != null)
        capturing.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    authoringTool.worldStopRunning();
    
    speedSlider.setValue(0);
    saveRenderBounds();
    showStdOut = false;
    if (config.getValue("clearStdOutOnRun").equalsIgnoreCase("true")) {
      detailTextPane.setText("");
    }
    Component renderCanvas = getRenderCanvas();
    if (renderCanvas != null) {
      renderCanvas.removeFocusListener(renderCanvasFocusListener);
    }
    
    stdOutOutputComponent.getTextPane().getDocument().removeDocumentListener(textListener);
    stdOutOutputComponent.stdOutStream.println(
      Messages.getString("Stopping_World") + "\n");
    stdOutOutputComponent.stdOutStream.flush();
  }
  
  public void addOKActionListener(ActionListener l)
  {
    okActionListener = l;
    stopButton.addActionListener(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    okActionListener = null;
    stopButton.removeActionListener(l);
  }
  
  protected void checkFileName() {
    String nameValue = fileName.getText();
    if (edu.cmu.cs.stage3.alice.authoringtool.AikMin.isValidName(nameValue)) {
      fileName.setForeground(Color.black);
      startCaptureButton.setEnabled(true);
    } else {
      fileName.setForeground(Color.red);
      startCaptureButton.setEnabled(false);
    }
  }
  
  private void guiInit()
  {
    title = Messages.getString("Alice_World");
    
    setRenderWindowSizeBasedOnSavedBounds();
    

    watcherScrollPane = new JScrollPane(watcherPanel, 
      20, 
      30);
    textScrollPane = new JScrollPane(detailTextPane, 
      20, 
      30);
    detailTextPane.setEditable(false);
    

    pauseButton = new JButton(
      authoringTool.getActions().pauseWorldAction);
    resumeButton = new JButton(
      authoringTool.getActions().resumeWorldAction);
    restartButton = new JButton(
      authoringTool.getActions().restartStopWorldAction);
    stopButton = new JButton(
      authoringTool.getActions().stopWorldAction);
    takePictureButton = new JButton(
      authoringTool.getActions().takePictureAction);
    takePictureButton.setEnabled(true);
    
    speedLabel = new JLabel(Messages.getString("Speed__1x"));
    int fontSize = Integer.parseInt(config.getValue("fontSize"));
    speedLabel.setFont(new java.awt.Font("SansSerif", 1, 
      (int)(12 * fontSize / 12.0D)));
    speedLabel.setPreferredSize(new Dimension(80, 12));
    speedLabel.setMinimumSize(new Dimension(20, 12));
    speedLabel.setMaximumSize(new Dimension(80, 12));
    
    pauseButton.setMargin(new Insets(3, 2, 3, 2));
    resumeButton.setMargin(new Insets(3, 2, 3, 2));
    restartButton.setMargin(new Insets(3, 2, 3, 2));
    stopButton.setMargin(new Insets(3, 2, 3, 2));
    takePictureButton.setMargin(new Insets(3, 2, 3, 2));
    

    stopCaptureButton = new JButton(Messages.getString("Stop_Recording"));
    stopCaptureButton.setToolTipText(
      Messages.getString("Click_here_to_stop_the_video_capture"));
    
    startCaptureButton = new JButton(Messages.getString("Record"));
    startCaptureButton.setToolTipText(
      Messages.getString("Click_here_to_start_the_video_capture"));
    
    encodeButton = new JButton(Messages.getString("Export_Video"));
    encodeButton.setToolTipText(
      Messages.getString("Click_here_to_stop_filming_and_encode_the_video"));
    
    clearButton = new JButton(Messages.getString("Clear"));
    clearButton.setToolTipText(
      Messages.getString("Click_here_to_clear_captured_video"));
    clearButton.setMargin(new Insets(3, 2, 3, 2));
    
    stopCaptureButton.setMargin(new Insets(3, 2, 3, 2));
    startCaptureButton.setMargin(new Insets(3, 2, 3, 2));
    encodeButton.setMargin(new Insets(3, 2, 3, 2));
    
    String[] types = { "MOV" };
    fileType = new JComboBox(types);
    
    fileName = new JTextField(Messages.getString("MyVideo"));
    fileName.setFont(new java.awt.Font("SansSerif", 1, 
      (int)(12 * fontSize / 12.0D)));
    fileName.setPreferredSize(new Dimension(124, 24));
    fileName.setMinimumSize(new Dimension(60, 24));
    fileName.setMaximumSize(new Dimension(124, 24));
    fileName.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        checkFileName();
      }
      
      public void removeUpdate(DocumentEvent e) { checkFileName(); }
      
      public void insertUpdate(DocumentEvent e) {
        checkFileName();
      }
      
    });
    statusLabel = new JLabel(Messages.getString("Ready"));
    timeLabel = new JLabel("0:00  ");
    
    captureBar = new JPanel();
    statusLabel.setHorizontalAlignment(0);
    captureBar.setLayout(new GridBagLayout());
    exportBar = new JPanel();
    exportBar.setLayout(new GridBagLayout());
    recordLabel = new JLabel();
    
    captureBar.add(startCaptureButton, new GridBagConstraints(1, 
      0, 1, 2, 0.0D, 0.0D, 17, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    captureBar.add(stopCaptureButton, new GridBagConstraints(2, 0, 
      1, 2, 0.0D, 0.0D, 17, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    captureBar.add(clearButton, new GridBagConstraints(3, 0, 1, 2, 
      0.0D, 0.0D, 17, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    captureBar.add(new JLabel(), new GridBagConstraints(4, 0, 1, 
      2, 0.5D, 0.0D, 17, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    captureBar.add(recordLabel, new GridBagConstraints(5, 0, 1, 2, 
      0.0D, 0.0D, 17, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    captureBar.add(timeLabel, new GridBagConstraints(6, 0, 1, 2, 
      0.0D, 0.0D, 17, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    
    exportBar.add(new JLabel("   " + Messages.getString("File_name_") + " "), 
      new GridBagConstraints(1, 0, 1, 2, 0.0D, 0.0D, 
      17, 
      0, new Insets(
      2, 2, 2, 2), 0, 0));
    exportBar.add(fileName, new GridBagConstraints(2, 0, 1, 2, 
      0.0D, 0.0D, 17, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    exportBar.add(fileType, new GridBagConstraints(3, 0, 1, 2, 
      0.0D, 0.0D, 17, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    exportBar.add(new JLabel(), new GridBagConstraints(4, 0, 1, 2, 
      0.5D, 0.0D, 13, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    exportBar.add(encodeButton, new GridBagConstraints(5, 0, 1, 2, 
      0.0D, 0.0D, 13, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    
    timeLabel.setBackground(Color.GREEN);
    
    setButtonsCapturing(false);
    encodeButton.setEnabled(false);
    setClear(false);
    
    captureActionListeners();
    
    speedSlider = new JSlider(0, 9, 0);
    





    speedSlider.setPreferredSize(new Dimension(100, 16));
    speedSlider.setMinimumSize(new Dimension(40, 16));
    speedSlider.setMaximumSize(new Dimension(100, 16));
    speedSlider.setSnapToTicks(true);
    speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent ce) {
        JSlider s = (JSlider)ce.getSource();
        if (!doNotListenToSpeedSlider) {
          int value = s.getValue();
          if (value >= 0) {
            updateSpeed(value + 1.0D);
          } else if (value < 0) {
            updateSpeed(1.0D / (1 + value * -1));
          }
        }
        Component renderCanvas = getRenderCanvas();
        if ((renderCanvas != null) && (renderCanvas.isShowing())) {
          renderCanvas.requestFocus();
        }
        
      }
    });
    buttonPanel.add(speedSlider, new GridBagConstraints(1, 1, 1, 
      1, 1.0D, 1.0D, 17, 
      2, new Insets(2, 
      2, 2, 2), 0, 0));
    buttonPanel.add(pauseButton, new GridBagConstraints(2, 0, 1, 
      2, 0.0D, 0.0D, 17, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    buttonPanel.add(speedLabel, new GridBagConstraints(1, 0, 1, 1, 
      0.0D, 0.0D, 10, 
      0, new Insets(0, 2, 0, 
      2), 0, 0));
    
    buttonPanel.add(resumeButton, new GridBagConstraints(5, 0, 1, 
      2, 0.0D, 0.0D, 17, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    buttonPanel.add(restartButton, new GridBagConstraints(6, 0, 1, 
      2, 0.0D, 0.0D, 17, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    buttonPanel.add(stopButton, new GridBagConstraints(7, 0, 1, 2, 
      0.0D, 0.0D, 17, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    buttonPanel.add(takePictureButton, new GridBagConstraints(8, 
      0, 1, 2, 1.0D, 0.0D, 13, 
      0, new Insets(2, 2, 2, 
      2), 0, 0));
    
    watcherSplitPane = new JSplitPane(
      1);
    watcherSplitPane.setContinuousLayout(true);
    watcherSplitPane.setDividerSize(0);
    watcherSplitPane.setResizeWeight(1.0D);
    watcherSplitPane.setLeftComponent(renderPanel);
    
    stdOutSplitPane = new JSplitPane(
      0);
    stdOutSplitPane.setContinuousLayout(true);
    stdOutSplitPane.setDividerSize(0);
    stdOutSplitPane.setResizeWeight(1.0D);
    stdOutSplitPane.setTopComponent(watcherSplitPane);
    setLayout(new java.awt.BorderLayout());
    

    JPanel menu = new JPanel();
    menu.setLayout(new java.awt.GridLayout(3, 1));
    menu.add(captureBar);
    menu.add(exportBar);
    menu.add(buttonPanel);
    add(menu, "North");
    add(stdOutSplitPane, "Center");
    
    createStatusFrame();
    
    updateGUI();
  }
  

  private void keyMapInit()
  {
    java.util.Iterator iter = authoringTool.getActions().renderActions
      .iterator(); while (iter.hasNext()) {
      javax.swing.Action action = (javax.swing.Action)iter.next();
      try
      {
        javax.swing.KeyStroke keyStroke = (javax.swing.KeyStroke)action
          .getValue("AcceleratorKey");
        commandKey = (String)action
          .getValue("ActionCommandKey");
      } catch (ClassCastException e) { String commandKey;
        continue; }
      String commandKey;
      javax.swing.KeyStroke keyStroke; if ((keyStroke != null) && (commandKey != null)) {
        Component root = 
          SwingUtilities.getRoot(this);
        if ((root instanceof JDialog))
        {

          ((JDialog)root).getRootPane().registerKeyboardAction(
            action, 
            commandKey, 
            keyStroke, 
            2);
        }
      }
    }
  }
  

  public void saveRenderBounds()
  {
    Point pos = new Point(0, 0);
    Dimension size = renderPanel.getSize();
    Component root = SwingUtilities.getRoot(this);
    if ((root instanceof JDialog)) {
      SwingUtilities.convertPointToScreen(pos, root);
    } else {
      SwingUtilities.convertPointToScreen(pos, renderPanel);
    }
    config.setValue(
      "rendering.renderWindowBounds", x + ", " + y + ", " + width + ", " + height);
  }
  
  public void saveRenderBounds(Rectangle newBounds) {
    config.setValue(
      "rendering.renderWindowBounds", x + ", " + y + ", " + width + ", " + height);
  }
  
  protected boolean shouldConstrainAspectOnResize() {
    if (!showStdOut)
    {


      if (config.getValue("rendering.constrainRenderDialogAspectRatio").equalsIgnoreCase("true"))
        return true; } return false;
  }
  



  public JPanel getRenderPanel()
  {
    return renderPanel;
  }
  
  public Rectangle getRenderBounds() {
    String boundsString = config.getValue("rendering.renderWindowBounds");
    StringTokenizer st = new StringTokenizer(
      boundsString, " \t,");
    if (st.countTokens() == 4) {
      try {
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        int width = Integer.parseInt(st.nextToken());
        int height = Integer.parseInt(st.nextToken());
        Rectangle bounds = new Rectangle(x, y, width, 
          height);
        double currentAspectRatio = width / height;
        if (shouldConstrainAspectOnResize()) {
          constrainToAspectRatio(bounds, currentAspectRatio > 1.0D);
        }
        return bounds;
      }
      catch (NumberFormatException e) {
        AuthoringTool.showErrorDialog(
          Messages.getString("Parse_error_in_config_value__rendering_renderWindowBounds"), 
          e);
      }
      
    } else {
      AuthoringTool.showErrorDialog(
        Messages.getString("Incorrect_number_of_tokens_in_config_value__rendering_renderWindowBounds"), 
        null);
    }
    
    return null;
  }
  
  public void constrainToAspectRatio(Rectangle bounds, boolean stretchHorizontally)
  {
    if (aspectRatio > 0.0D) {
      if (stretchHorizontally) {
        width = ((int)Math.round(height * aspectRatio));
      } else {
        height = ((int)Math.round(width / aspectRatio));
      }
    }
  }
  
  public void updateSpeed(double newSpeed) {
    authoringTool.setWorldSpeed(newSpeed);
    String speedText = NumberFormat.getInstance()
      .format(newSpeed);
    if (newSpeed < 1.0D) {
      if (newSpeed == 0.5D) {
        speedText = "1/2";
      } else if (newSpeed == 0.25D) {
        speedText = "1/4";
      } else if (newSpeed == 0.2D) {
        speedText = "1/5";
      } else if ((newSpeed > 0.3D) && (newSpeed < 0.34D)) {
        speedText = "1/3";
      } else if ((newSpeed > 0.16D) && (newSpeed < 0.168D)) {
        speedText = "1/6";
      } else if ((newSpeed > 0.14D) && (newSpeed < 0.143D)) {
        speedText = "1/7";
      }
    }
    speedLabel.setText(Messages.getString("Speed__") + speedText + "x");
    speedLabel.repaint();
  }
  
  public void setSpeedSliderValue(int value) {
    speedSlider.setValue(value);
  }
  
  public void stopWorld() {
    if (okActionListener != null) {
      okActionListener.actionPerformed(null);
    }
  }
  





  public void updateGUI()
  {
    int renderWidth = renderPanel.getWidth();
    



    if (showStdOut) {
      textScrollPane.setPreferredSize(new Dimension(renderWidth, 
        stdOutHeight));
      stdOutSplitPane.setBottomComponent(textScrollPane);
      stdOutSplitPane.setDividerSize(8);
    } else {
      stdOutSplitPane.setBottomComponent(null);
      stdOutSplitPane.setDividerLocation(0);
      stdOutSplitPane.setDividerSize(0);
    }
    












    watcherSplitPane.setRightComponent(null);
    watcherSplitPane.setDividerLocation(0);
    watcherSplitPane.setDividerSize(0);
    
    packDialog();
  }
  
  public void addNotify() {
    super.addNotify();
    showStdOut = false;
    updateGUI();
  }
  
  public Component getRenderCanvas() {
    Component authoringToolRenderPanel = renderPanel
      .getComponent(0);
    if ((authoringToolRenderPanel instanceof Container)) {
      return 
        ((Container)authoringToolRenderPanel).getComponent(0);
    }
    return null;
  }
  
  public class RenderCanvasFocusListener extends java.awt.event.FocusAdapter { public RenderCanvasFocusListener() {}
    
    public void focusLost() { Component renderCanvas = getRenderCanvas();
      if ((renderCanvas != null) && (renderCanvas.isShowing())) {
        renderCanvas.requestFocus();
      }
    }
  }
  
  public void traverseTree() {
    edu.cmu.cs.stage3.alice.core.World w = authoringTool.getWorld();
    
    findSounds(w);
  }
  
  public void setButtonsCapturing(boolean choice) {
    stopCaptureButton.setEnabled(choice);
    startCaptureButton.setEnabled(!choice);
    encodeButton.setEnabled(!choice);
    resumeButton.setEnabled(!choice);
    stopButton.setEnabled(!choice);
    restartButton.setEnabled(!choice);
  }
  
  public void setClear(boolean choice) {
    clearButton.setEnabled(choice);
    encodeButton.setEnabled(choice);
  }
  

  public void findSoundsfromResponse(Response s)
  {
    if ((s instanceof SoundResponse))
    {
      ((SoundResponse)s).addSoundListener(new movieMaker.SoundHandler(authoringTool
        .getSoundStorage(), authoringTool));
    }
    
    if (s.getChildCount() > 0) {
      edu.cmu.cs.stage3.alice.core.Element[] children = s.getChildren(Sandbox.class);
      for (int y = 0; y < children.length; y++) {
        findSounds((Sandbox)children[y]);
      }
      children = 
        s.getChildren(Response.class);
      for (int y = 0; y < children.length; y++) {
        findSoundsfromResponse((Response)children[y]);
      }
    }
  }
  

  public void findSoundsfromBehavior(Behavior s)
  {
    if (s.getChildCount() > 0) {
      edu.cmu.cs.stage3.alice.core.Element[] children = s
        .getChildren(Response.class);
      for (int y = 0; y < children.length; y++) {
        if ((children[y] instanceof SoundResponse))
        {
          ((SoundResponse)children[y]).addSoundListener(new movieMaker.SoundHandler(authoringTool
            .getSoundStorage(), authoringTool));
        }
      }
    }
  }
  

  public void findSounds(Sandbox sbox)
  {
    edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty p = responses;
    Object[] objs = p.getArrayValue();
    
    for (int x = 0; x < objs.length; x++) {
      if (objs[x] != null) {
        findSoundsfromResponse((Response)objs[x]);
      }
    }
    p = behaviors;
    objs = p.getArrayValue();
    
    for (int x = 0; x < objs.length; x++) {
      if (objs[x] != null)
        findSoundsfromBehavior((Behavior)objs[x]);
    }
    edu.cmu.cs.stage3.alice.core.Element[] t = sbox
      .getChildren(Sandbox.class);
    
    for (int x = 0; x < t.length; x++) {
      findSounds((Sandbox)t[x]);
    }
  }
  
  public void setExportDirectory(String directory) {
    exportDirectory = directory;
  }
  
  public void captureInit()
  {
    videoHandler = new MovieCapturer(exportDirectory + "/frames/");
    frameSequencer = videoHandler.getFrameSequencer();
    authoringTool.pause();
    

    traverseTree();
    authoringTool.getSoundStorage().frameList = new java.util.ArrayList();
  }
  
  public void removeFiles(String dir)
  {
    File f = new File(dir);
    String[] files = f.list();
    










    if (files == null) {
      return;
    }
    for (int i = 0; i < files.length; i++) {
      String x = files[i];
      File empty = new File(dir + x);
      empty.delete();
    }
  }
  
  public boolean getRunning()
  {
    return running;
  }
  
  public boolean getEnd()
  {
    return endCapturing;
  }
  
  public JLabel getStatusLabel() {
    return statusLabel;
  }
  
  public JPanel getButtonPanel() {
    return buttonPanel;
  }
  
  public void createStatusFrame()
  {
    statusFrame = new JFrame();
    statusFrame.setSize(200, 100);
    statusFrame.setVisible(false);
    statusFrame.getContentPane().add(statusLabel);
    statusFrame.pack();
  }
  
  public void disable() {
    startCaptureButton.setEnabled(false);
    stopCaptureButton.setEnabled(false);
    clearButton.setEnabled(false);
    encodeButton.setEnabled(false);
    
    resumeButton.setEnabled(false);
    pauseButton.setEnabled(false);
    restartButton.setEnabled(false);
    stopButton.setEnabled(false);
    
    speedSlider.setEnabled(false);
    fileName.setEnabled(false);
    fileType.setEnabled(false);
    takePictureButton.setEnabled(false);
  }
  
  public void enable() {
    startCaptureButton.setEnabled(true);
    clearButton.setEnabled(true);
    encodeButton.setEnabled(true);
    
    resumeButton.setEnabled(true);
    restartButton.setEnabled(true);
    stopButton.setEnabled(true);
    
    speedSlider.setEnabled(true);
    fileName.setEnabled(true);
    fileType.setEnabled(true);
    takePictureButton.setEnabled(true);
  }
  
  public void showStatus()
  {
    disable();
    statusFrame.setSize(200, 100);
    statusFrame.setVisible(true);
    try {
      if (edu.cmu.cs.stage3.alice.authoringtool.AikMin.isWindows()) {
        statusFrame.setAlwaysOnTop(true);
        Point location = captureBar.getLocationOnScreen();
        statusFrame.setLocation(new Point(x, 
          (int)(y + renderPanel.getHeight() / 2.0D)));
      } else {
        statusFrame.setLocation(0, 0);
      }
    }
    catch (Exception localException) {}
  }
  


  public void stopCaptureAction()
  {
    running = false;
    if (authoringTool.getWorldClock().isPaused()) {
      authoringTool.pause();
    }
    
    authoringTool.getSoundStorage().setListening(false, 
      System.currentTimeMillis() / 1000.0D);
    
    recordLabel.setIcon(null);
    setButtonsCapturing(false);
    setClear(true);
    try
    {
      writing.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  



  public void clearAction()
  {
    endCapturing = false;
    running = false;
    pulsing = false;
    
    setClear(false);
    
    removeFiles(exportDirectory + "/frames/");
    try
    {
      if (timerThread != null) {
        timerThread.join();
      }
    }
    catch (InterruptedException localInterruptedException) {}
    
    timeLabel.setText("0:00  ");
    
    authoringTool.setSoundStorage(new SoundStorage());
    videoHandler = new MovieCapturer(exportDirectory + "/frames/");
    authoringTool.getSoundStorage().frameList = new java.util.ArrayList();
    frameSequencer = videoHandler.getFrameSequencer();
    traverseTree();
  }
  
  public void startCaptureAction()
  {
    setClear(false);
    

    timeLabel.setBackground(Color.RED);
    
    setButtonsCapturing(true);
    

    recordLabel.setIcon(new javax.swing.ImageIcon(
      edu.cmu.cs.stage3.alice.authoringtool.JAlice.class
      .getResource("images/record.png")));
    
    recordLabel.setAlignmentX(1.0F);
    if ((movieCapture == null) || (capturing == null) || (!capturing.isAlive()))
    {
      endCapturing = true;
      
      movieCapture = new movieMaker.StartMovieCapture(authoringTool, 
        this, frameSequencer, 
        videoHandler.getFramesPerSecond());
      
      running = true;
      
      capturing = new Thread(movieCapture);
      writing = new Thread(new WriteFrames(exportDirectory + "/frames", 
        frameSequencer, frameSequencer.getFrameNumber()));
      timerThread = new Thread(new StartTimer(timeLabel));
      
      capturing.start();
      writing.start();
      timerThread.start();
    }
    else {
      running = true;
      writing = new Thread(new WriteFrames(exportDirectory + "/frames", 
        frameSequencer, frameSequencer.getFrameNumber()));
      writing.start();
    }
    
    if (!authoringTool.getWorldClock().isPaused())
      authoringTool.resume();
    authoringTool.getSoundStorage().setListening(true, 
      System.currentTimeMillis() / 1000.0D);
  }
  
  public JPanel getCapturePanel()
  {
    return captureBar;
  }
  
  public JPanel getExportPanel() {
    return exportBar;
  }
  

  public void encodeAction()
  {
    setClear(false);
    setButtonsCapturing(false);
    encodeButton.setEnabled(false);
    
    endCapturing = false;
    pulsing = true;
    
    timeLabel.setText("0:00  ");
    try
    {
      capturing.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    

    pulse = new Thread(new StartStatusPulsing(statusLabel, 
      Messages.getString("Encoding_Video")));
    pulse.start();
    

    showStatus();
    



    compressing = new Thread(new StartMovieCompression(null));
    compressing.start();
  }
  

  public void captureActionListeners()
  {
    stopCaptureButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        stopCaptureAction();
      }
      
    });
    clearButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clearAction();
      }
      
    });
    startCaptureButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        startCaptureAction();
      }
      
    });
    encodeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        encodeAction();
      }
    });
  }
  


  public Rectangle getRenderPanelLocation()
  {
    Rectangle area = new Rectangle(0, 0, 0, 0);
    
    if ((stdOutSplitPane != null) && 
      (stdOutSplitPane.getLocationOnScreen() != null)) {
      x = stdOutSplitPane.getLocationOnScreen().x;
      y = stdOutSplitPane.getLocationOnScreen().y;
    }
    
    if (showStdOut)
      height = stdOutHeight;
    return area;
  }
  

  private class StartMovieCompression
    implements Runnable
  {
    private StartMovieCompression() {}
    
    public void run()
    {
      MovieWriter writer = new MovieWriter(
        videoHandler.getFramesPerSecond(), exportDirectory + 
        "/frames/", fileName.getText() + "_NoSound", 
        exportDirectory + "/frames/");
      if (writer == null) {
        afterEncoding(true); return;
      }
      
      boolean result;
      
      boolean result;
      if (fileType.getSelectedIndex() == 0) {
        result = writer.writeQuicktime();
      } else {
        result = writer.writeAVI();
      }
      pulsing = false;
      try {
        pulse.join();
      }
      catch (InterruptedException localInterruptedException) {}
      writer = null;
      
      if (!result) {
        statusLabel.setText(
          Messages.getString("Video_failed_to_encrypt"));
        afterEncoding(true);
        return;
      }
      
      if (authoringTool.getSoundStorage() == null) {
        System.err.print(Messages.getString("No_Sound_Storage"));
        afterEncoding(true);
        return;
      }
      
      movieMaker.Merge m = new movieMaker.Merge(
        authoringTool.getSoundStorage()
        .createURL(
        exportDirectory + "/" + fileName.getText() + 
        ".mov"));
      if (m == null) {
        afterEncoding(true);
        return;
      }
      
      Vector sources = new Vector();
      
      pulsing = true;
      pulse = new Thread(new CaptureContentPane.StartStatusPulsing(CaptureContentPane.this, statusLabel, 
        Messages.getString("Encoding_Sound")));
      pulse.start();
      

      if (frameSequencer != null) {
        sources = 
        
          authoringTool.getSoundStorage().encodeFiles(
          (frameSequencer.getFrameNumber() + 1) / 16.0D, 
          exportDirectory + "/frames/" + 
          authoringTool.numEncoded);
      } else {
        System.err.print(Messages.getString("No_Sequencer"));
        afterEncoding(true);
        return;
      }
      
      pulsing = false;
      try {
        pulse.join();
      }
      catch (InterruptedException localInterruptedException1) {}
      
      pulsing = true;
      
      pulse = new Thread(new CaptureContentPane.StartStatusPulsing(CaptureContentPane.this, statusLabel, 
        Messages.getString("Merging_Sound_and_Audio")));
      pulse.start();
      
      if (sources == null) {
        afterEncoding(true);
        return;
      }
      
      sources.add(authoringTool.getSoundStorage().createURL(
        exportDirectory + "/frames/" + fileName.getText() + 
        "_NoSound.mov"));
      

      if (sources.size() > 0) {
        m.doMerge(sources);
      }
      m = null;
      sources.clear();
      sources = null;
      pulsing = false;
      try {
        pulse.join();
      }
      catch (InterruptedException localInterruptedException2) {}
      
      afterEncoding(false);
    }
  }
  




  void afterEncoding(boolean error)
  {
    if (error) {
      System.err.print(Messages.getString("Error_in_making_video_"));
    }
    
    running = false;
    pulsing = false;
    statusFrame.setVisible(false);
    authoringTool.setSoundStorage(new SoundStorage());
    videoHandler = new MovieCapturer(exportDirectory + "/frames/");
    authoringTool.getSoundStorage().frameList = new java.util.ArrayList();
    frameSequencer = videoHandler.getFrameSequencer();
    traverseTree();
    authoringTool.numEncoded += 1;
    enable();
    encodeButton.setEnabled(false);
  }
  
  private class StartStatusPulsing implements Runnable
  {
    private JLabel label;
    private String text;
    
    public StartStatusPulsing(JLabel l, String s)
    {
      label = l;
      text = s;
    }
    
    public void run()
    {
      long time = 300L;
      while (pulsing) {
        label.setText(text + ".");
        try {
          Thread.sleep(time);
        }
        catch (InterruptedException localInterruptedException) {}
        label.setText(text + "..");
        try {
          Thread.sleep(time);
        }
        catch (InterruptedException localInterruptedException1) {}
      }
    }
  }
  

  private class WriteFrames
    implements Runnable
  {
    private int frameNumber;
    String fileLocation;
    private NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    private FrameSequencer myFrameSeq;
    
    public WriteFrames(String dirPath, FrameSequencer fs, int num)
    {
      fileLocation = fs.getDirectory();
      numberFormat.setMinimumIntegerDigits(6);
      numberFormat.setGroupingUsed(false);
      myFrameSeq = fs;
      frameNumber = num;
    }
    
    public void run() {
      while ((running) || (myFrameSeq.getNumFrames() > 0)) {
        try {
          Thread.sleep(1L);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        movieMaker.Picture picture = myFrameSeq.removeFrame();
        
        if (picture != null) {
          String fileName = fileLocation + "frame" + 
            numberFormat.format(frameNumber) + ".jpg";
          

          picture.setFileName(fileName);
          

          picture.write(fileName);
          

          frameNumber += 1;
        }
      }
    }
  }
  
  private class StartTimer
    implements Runnable
  {
    private JLabel label;
    private int timer = 0;
    
    public StartTimer(JLabel l) {
      label = l;
    }
    
    public void run() {
      java.text.DecimalFormat det = new java.text.DecimalFormat("00");
      while (endCapturing) {
        if (getRunning()) {
          try {
            Thread.sleep(1000L);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          timer += 1;
          int seconds = timer % 60;
          int minutes = timer / 60;
          label.setText(minutes + ":" + det.format(seconds) + "  ");
        }
      }
    }
  }
}
