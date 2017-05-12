package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.Actions;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.StyleStream;
import edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;

public class RenderContentPane extends ContentPane
{
  protected JPanel renderPanel = new JPanel(
    new java.awt.BorderLayout());
  protected JPanel buttonPanel = new JPanel(
    new java.awt.GridBagLayout());
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
  
  protected class TextOutputDocumentListener implements javax.swing.event.DocumentListener {
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
          RenderContentPane.TextOutputDocumentListener.this.update();
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
            if (renderCanvas != null)
              renderCanvas.requestFocus();
          }
        });
      }
    }
  }
  
  protected class RenderComponentListener extends java.awt.event.ComponentAdapter {
    protected RenderComponentListener() {}
    
    public void componentResized(java.awt.event.ComponentEvent ev) {
      if ((shouldConstrainAspectOnResize()) && 
        (!doNotListenToResize)) {
        doNotListenToResize = true;
        Rectangle oldBounds = getRenderBounds();
        Dimension newSize = renderPanel
          .getSize();
        Rectangle newBounds = new Rectangle(
          oldBounds.getLocation(), newSize);
        
        int deltaWidth = Math.abs(width - width);
        int deltaHeight = Math.abs(height - height);
        
        constrainToAspectRatio(newBounds, deltaWidth < deltaHeight);
        
        config.setValue(
          "rendering.renderWindowBounds", x + ", " + y + ", " + width + ", " + height);
        renderPanel.setPreferredSize(new Dimension(
          width, height));
        buttonPanel.setPreferredSize(new Dimension(
          width, buttonPanel.getHeight()));
        packDialog();
        doNotListenToResize = false;
      }
    }
  }
  
  protected TextOutputDocumentListener textListener = new TextOutputDocumentListener();
  
  public RenderContentPane(AuthoringTool authoringTool)
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
        new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent ev) {
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
        authoringTool.getJAliceFrame().sceneEditor.makeDirty();
        renderPanel.invalidate();
        parentDialog.pack();
      }
    }
  }
  
  public void postDialogShow(JDialog parentDialog) {
    super.postDialogShow(parentDialog);
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
  
  private void guiInit() {
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
      authoringTool.getActions().restartWorldAction);
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
    
    add(buttonPanel, "North");
    add(stdOutSplitPane, "Center");
    
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
    if ((!showStdOut) && 
      (!authoringTool.getWatcherPanel().isThereSomethingToWatch()))
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
    String speedText = java.text.NumberFormat.getInstance()
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
    int renderHeight = renderPanel.getHeight();
    


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
    if (authoringTool.getWatcherPanel().isThereSomethingToWatch()) {
      watcherScrollPane.setPreferredSize(new Dimension(
        watcherWidth, renderHeight));
      watcherSplitPane.setRightComponent(watcherScrollPane);
      watcherSplitPane.setDividerSize(8);
    } else {
      watcherSplitPane.setRightComponent(null);
      watcherSplitPane.setDividerLocation(0);
      watcherSplitPane.setDividerSize(0);
    }
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
    if ((authoringToolRenderPanel instanceof java.awt.Container)) {
      return 
        ((java.awt.Container)authoringToolRenderPanel).getComponent(0);
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
}
