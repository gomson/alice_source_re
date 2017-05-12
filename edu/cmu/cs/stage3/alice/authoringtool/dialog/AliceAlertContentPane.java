package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.JAlice;
import edu.cmu.cs.stage3.alice.authoringtool.util.ImagePanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.StyleStream;
import edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;











public abstract class AliceAlertContentPane
  extends ContentPane
{
  public static final int LESS_DETAIL_MODE = 0;
  public static final int MORE_DETAIL_MODE = 1;
  protected Dimension smallSize;
  protected Dimension largeSize;
  protected int mode = -1;
  protected ImagePanel errorIconPanel = new ImagePanel();
  protected StyledStreamTextPane detailTextPane = new StyledStreamTextPane();
  
  protected StyleStream detailStream;
  

  public AliceAlertContentPane()
  {
    init();
  }
  
  public void preDialogShow(JDialog dialog) {
    super.preDialogShow(dialog);
  }
  
  public void postDialogShow(JDialog dialog) {
    super.postDialogShow(dialog);
  }
  
  public String getTitle() {
    return Messages.getString("Alice___Alert");
  }
  
  public void addOKActionListener(ActionListener l) {
    cancelButton.addActionListener(l);
  }
  
  protected void init() {
    jbInit();
    smallSize = new Dimension(700, 130);
    largeSize = new Dimension(700, 350);
    setMode(0);
    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        if (mode == 0) {
          smallSize = getSize();
        } else if (mode == 1) {
          largeSize = getSize();
        }
      }
    });
  }
  








  public void setMessage(String message)
  {
    messageLabel.setText(message);
  }
  
  public Dimension getPreferredSize() {
    Dimension preferredSize = super.getPreferredSize();
    




    return preferredSize;
  }
  
  protected void setLessDetail()
  {
    detailButton.setText(Messages.getString("More_Detail___"));
    remove(detailScrollPane);
    add(detailPanel, "Center");
    
    buttonPanel.removeAll();
    buttonConstraints.gridx = 0;
    
    buttonConstraints.gridx += 1;
    buttonPanel.add(cancelButton, buttonConstraints);
    buttonConstraints.gridx += 1;
    glueConstraints.gridx = buttonConstraints.gridx;
    buttonPanel.add(buttonGlue, glueConstraints);
    buttonConstraints.gridx += 1;
    buttonPanel.add(detailButton, buttonConstraints);
  }
  
  protected void setMoreDetail() {
    detailButton.setText(Messages.getString("Less_Detail___"));
    remove(detailPanel);
    add(detailScrollPane, "Center");
    
    buttonPanel.removeAll();
    buttonConstraints.gridx = 0;
    
    buttonConstraints.gridx += 1;
    buttonPanel.add(copyButton, buttonConstraints);
    buttonConstraints.gridx += 1;
    buttonPanel.add(cancelButton, buttonConstraints);
    buttonConstraints.gridx += 1;
    glueConstraints.gridx = buttonConstraints.gridx;
    buttonPanel.add(buttonGlue, glueConstraints);
    buttonConstraints.gridx += 1;
    buttonPanel.add(detailButton, buttonConstraints);
  }
  
  protected void handleModeSwitch(int mode) {
    if (mode == 0) {
      setLessDetail();
    } else if (mode == 1) {
      setMoreDetail();
    } else {
      throw new IllegalArgumentException(
        Messages.getString("Illegal_mode__") + mode);
    }
  }
  
  public void setMode(int mode) {
    if (this.mode != mode) {
      this.mode = mode;
      handleModeSwitch(mode);
      packDialog();
    }
  }
  
  public void toggleMode()
  {
    if (mode == 0) {
      setMode(1);
    } else {
      setMode(0);
    }
  }
  
  protected void writeAliceHeaderToTextPane() {
    detailTextPane.setText("");
    detailStream.println(messageLabel.getText() + "\n");
    detailStream.println();
    
    detailStream.println(Messages.getString("Alice_version__") + 
      JAlice.getVersion());
    String[] systemProperties = { "os.name", "os.version", "os.arch", 
      "java.vm.name", "java.vm.version", "user.dir" };
    for (int i = 0; i < systemProperties.length; i++) {
      detailStream.println(systemProperties[i] + ": " + 
        System.getProperty(systemProperties[i]));
    }
    detailStream.println();
  }
  



















  protected String postStacktrace()
  {
    try
    {
      URL url = new URL(
        "http://www.alice.org/bugreport/stacktrace.php");
      URLConnection connection = url.openConnection();
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setUseCaches(false);
      connection.setRequestProperty("Content-Type", 
        "application/x-www-form-urlencoded");
      
      String stacktrace = URLEncoder.encode(detailTextPane
        .getText());
      while (stacktrace.indexOf("%0D") > -1)
      {
        int i = stacktrace.indexOf("%0D");
        stacktrace = stacktrace.substring(0, i) + 
          stacktrace.substring(i + 3);
      }
      String content = "stacktrace=" + stacktrace;
      DataOutputStream output = new DataOutputStream(
        new BufferedOutputStream(
        connection.getOutputStream()));
      output.writeBytes(content);
      output.flush();
      output.close();
      


      BufferedReader input = new BufferedReader(
        new InputStreamReader(connection.getInputStream()));
      String stacktraceIDString = input.readLine();
      input.close();
      return stacktraceIDString;
    }
    catch (Throwable t) {
      AuthoringTool.showErrorDialog(
        Messages.getString("Error_posting_stacktrace_to_bug_database_"), 
        t); }
    return "0";
  }
  
  protected void copyDetailText()
  {
    String detailText = detailTextPane.getText();
    
    Toolkit.getDefaultToolkit()
      .getSystemClipboard()
      .setContents(
      new StringSelection(detailText), 
      
      AuthoringTool.getHack());
  }
  



  void detailButton_actionPerformed(ActionEvent e)
  {
    toggleMode();
  }
  



  void copyButton_actionPerformed(ActionEvent e)
  {
    copyDetailText();
  }
  




  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JPanel buttonPanel = new JPanel();
  protected JPanel messagePanel = new JPanel();
  protected JPanel detailPanel = new JPanel();
  protected JScrollPane detailScrollPane = new JScrollPane();
  protected JTextArea messageLabel = new JTextArea();
  
  protected JButton copyButton = new JButton();
  protected JButton cancelButton = new JButton();
  protected JButton detailButton = new JButton();
  
  protected GridBagConstraints buttonConstraints = new GridBagConstraints(0, 
    0, 1, 1, 0.0D, 0.0D, 10, 
    0, new Insets(2, 2, 2, 2), 0, 0);
  protected GridBagConstraints glueConstraints = new GridBagConstraints(0, 0, 
    1, 1, 1.0D, 0.0D, 10, 
    2, new Insets(0, 0, 0, 0), 0, 0);
  protected Component buttonGlue;
  
  private void jbInit() {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    setLocation(1, 1);
    
    buttonGlue = Box.createHorizontalGlue();
    
    messageLabel.setEditable(false);
    messageLabel.setLineWrap(true);
    messageLabel.setOpaque(false);
    messageLabel.setPreferredSize(new Dimension(402, 1));
    messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    messageLabel.setText(
      Messages.getString("An_unknown_error_has_occurred_"));
    


    cancelButton.setText(Messages.getString("OK"));
    
    detailButton.setText(Messages.getString("More_Detail___"));
    
    detailButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        detailButton_actionPerformed(e);


      }
      



    });
    copyButton.setText(Messages.getString("Copy_Error_to_Clipboard"));
    copyButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        copyButton_actionPerformed(e);
      }
      
    });
    messagePanel.setLayout(new BorderLayout());
    messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    detailPanel.setLayout(new BorderLayout());
    detailPanel.setBorder(null);
    detailPanel.setPreferredSize(new Dimension(492, 0));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
    buttonPanel.setLayout(new GridBagLayout());
    
    URL errorImageResources = JAlice.class
      .getResource("images/errorDialogueIcon.png");
    errorIconPanel.setImage(Toolkit.getDefaultToolkit()
      .createImage(errorImageResources));
    messagePanel.add(errorIconPanel, "West");
    messagePanel.add(messageLabel, "Center");
    
    detailTextPane.setEditable(false);
    detailStream = detailTextPane
      .getNewStyleStream(detailTextPane.stdErrStyle);
    detailScrollPane.setViewportView(detailTextPane);
    detailScrollPane.setPreferredSize(new Dimension(492, 300));
    
    add(messagePanel, "North");
    add(detailPanel, "Center");
    add(buttonPanel, "South");
  }
}
