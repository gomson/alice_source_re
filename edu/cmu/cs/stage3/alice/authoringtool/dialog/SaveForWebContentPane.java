package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.JAlice;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class SaveForWebContentPane extends ContentPane
{
  private AuthoringTool authoringTool;
  private boolean ignoreSizeChange = false;
  private Vector m_okActionListeners = new Vector();
  

  private static Configuration authoringToolConfig = Configuration.getLocalConfiguration(JAlice.class
    .getPackage());
  
  public SaveForWebContentPane(AuthoringTool authoringTool)
  {
    this.authoringTool = authoringTool;
    jbInit();
    guiInit();
    authorNameTextField
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveButton.doClick();
        }
      });
  }
  
  public String getTitle() {
    return Messages.getString("Save_World_for_the_Web");
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
  
  public File getExportDirectory() {
    return new File(directoryPath.getText());
  }
  
  public String getExportFileName() {
    return htmlFileName.getText();
  }
  
  public String getExportAuthorName() {
    return authorNameTextField.getText();
  }
  
  public int getExportWidth() {
    return Integer.parseInt(widthTextField.getText());
  }
  
  public int getExportHeight() {
    return Integer.parseInt(heightTextField.getText());
  }
  
  public boolean isCodeToBeExported() {
    return saveCodeCheckBox.isSelected();
  }
  
  private void guiInit() {
    setSize(575, 510);
    widthTextField.setText("320");
    heightTextField.setText("240");
    constrainAspectRatioCheckBox.setSelected(true);
    
    widthTextField.getDocument().addDocumentListener(
      new DocumentListener() {
        public void insertUpdate(DocumentEvent ev) {
          updateHeightTextField();
        }
        
        public void removeUpdate(DocumentEvent ev) {
          updateHeightTextField();
        }
        
        private void updateHeightTextField() {
          if ((constrainAspectRatioCheckBox.isSelected()) && 
            (!ignoreSizeChange)) {
            ignoreSizeChange = true;
            double aspectRatio = 
              AuthoringToolResources.getAspectRatio(authoringTool.getWorld());
            int width = -1;
            try {
              width = Integer.parseInt(widthTextField
                .getText());
            }
            catch (NumberFormatException localNumberFormatException) {}
            if (width > 0) {
              heightTextField.setText(
                Integer.toString((int)(width / aspectRatio)));
            }
            ignoreSizeChange = false;
          }
        }
        


        public void changedUpdate(DocumentEvent ev) {}
      });
    heightTextField.getDocument().addDocumentListener(
      new DocumentListener() {
        public void insertUpdate(DocumentEvent ev) {
          updateWidthTextField();
        }
        
        public void removeUpdate(DocumentEvent ev) {
          updateWidthTextField();
        }
        
        private void updateWidthTextField() {
          if ((constrainAspectRatioCheckBox.isSelected()) && 
            (!ignoreSizeChange)) {
            ignoreSizeChange = true;
            double aspectRatio = 
              AuthoringToolResources.getAspectRatio(authoringTool.getWorld());
            int height = -1;
            try {
              height = Integer.parseInt(heightTextField
                .getText());
            }
            catch (NumberFormatException localNumberFormatException) {}
            if (height > 0) {
              widthTextField.setText(
                Integer.toString((int)(aspectRatio * height)));
            }
            ignoreSizeChange = false;
          }
        }
        


        public void changedUpdate(DocumentEvent ev) {}
      });
    setTitleTextField.getDocument().addDocumentListener(
      new DocumentListener()
      {
        public void insertUpdate(DocumentEvent ev) {
          SaveForWebContentPane.this.updateTextFields();
        }
        
        public void removeUpdate(DocumentEvent ev) {
          SaveForWebContentPane.this.updateTextFields();
        }
        



        public void changedUpdate(DocumentEvent ev) {}
      });saveDirectoryCheckBox
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          SaveForWebContentPane.this.updateDirectory();
        }
        
      });
    saveCodeCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SaveForWebContentPane.this.updateTextFields();
      }
      

    });browseDirectoryButton
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          DialogManager.showDialog(htmlFileChooser, Messages.getString("Set_directory"));
        }
        
      });
    saveButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String authorName = authorNameTextField.getText();
        if (authorName.length() > 0) {
          int width;
          try {
            width = Integer.parseInt(widthTextField.getText());
          } catch (NumberFormatException nfe) { int width;
            width = -1;
          }
          if (width > 0) {
            int height;
            try {
              height = Integer.parseInt(heightTextField.getText());
            } catch (NumberFormatException nfe) { int height;
              height = -1;
            }
            if (height > 0) {
              SaveForWebContentPane.this.fireOKActionListeners();
            } else {
              DialogManager.showMessageDialog(
                Messages.getString("You_must_enter_a_valid_height__a_number_greater_than_0__before_proceeding_"), 
                Messages.getString("You_have_not_entered_a_valid_height_"), 
                2);
            }
          } else {
            DialogManager.showMessageDialog(
              Messages.getString("You_must_enter_a_valid_width__a_number_greater_than_0__before_proceeding_"), 
              Messages.getString("You_have_not_entered_a_valid_width_"), 
              2);
          }
        } else {
          DialogManager.showMessageDialog(
            Messages.getString("You_must_enter_the_author_s_name_before_proceeding_"), 
            Messages.getString("You_have_not_entered_the_author_s_name"), 
            2);
        }
        
      }
    });
    File currentDir = new File(
      authoringToolConfig.getValue("directories.worldsDirectory"));
    try {
      if (currentDir.exists()) {
        rootDirectoryPath = 
          (currentDir.getAbsolutePath() + File.separator);
        

        htmlFileChooser.setCurrentDirectory(currentDir);

      }
      else
      {
        rootDirectoryPath = 
        
          (JAlice.getAliceUserDirectory().getAbsolutePath() + File.separator);
        htmlFileChooser
          .setCurrentDirectory(
          JAlice.getAliceUserDirectory());
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    







    updateDirectory();
    










    htmlFileChooser.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ev) {
        if (ev.getActionCommand().equals("ApproveSelection")) {
          File file = htmlFileChooser.getSelectedFile();
          rootDirectoryPath = 
            (file.getAbsolutePath() + File.separator);
          SaveForWebContentPane.this.updateDirectory();
        } else { ev.getActionCommand().equals(
            "CancelSelection");
        }
      }
    });
  }
  
  public void setVisible(boolean visibility)
  {
    super.setVisible(visibility);
    if ((visibility) && 
      (authoringTool != null)) {
      File currentName = authoringTool
        .getCurrentWorldLocation();
      if (currentName != null) {
        String newTitle = currentName.getName();
        newTitle = newTitle.substring(0, newTitle.lastIndexOf("."));
        setTitleTextField.setText(newTitle);
        updateDirectory();
        updateTextFields();
        updateRatio();
      }
    }
  }
  




















  private String getValidFilename(String newValue)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < newValue.length(); i++) {
      char c = newValue.charAt(i);
      switch (c) {
      case ' ': 
      case '"': 
      case '*': 
      case '/': 
      case ':': 
      case '<': 
      case '>': 
      case '?': 
      case '\\': 
      case '|': 
        sb.append('_');
        break;
      default: 
        sb.append(c);
      }
    }
    return sb.toString();
  }
  
  private void updateDirectory() {
    if (saveDirectoryCheckBox.isSelected()) {
      localDirectoryPath = getValidFilename(setTitleTextField.getText());
      directoryPath.setText(rootDirectoryPath + localDirectoryPath + 
        File.separator);
    } else {
      directoryPath.setText(rootDirectoryPath);
    }
  }
  
  private void updateTextFields() {
    String newValue = getValidFilename(setTitleTextField.getText());
    
    if (saveDirectoryCheckBox.isSelected()) {
      updateDirectory();
    }
    a2wFileName.setText(newValue + ".a2w");
    htmlFileName.setText(newValue + ".html");
  }
  
  private void updateRatio() {
    if (constrainAspectRatioCheckBox.isSelected()) {
      double aspectRatio = 
        AuthoringToolResources.getAspectRatio(authoringTool.getWorld());
      int width = -1;
      int height = -1;
      try {
        width = Integer.parseInt(widthTextField.getText());
      }
      catch (NumberFormatException localNumberFormatException) {}
      try
      {
        height = Integer.parseInt(heightTextField.getText());
      }
      catch (NumberFormatException localNumberFormatException1) {}
      
      if (width > 0) {
        height = (int)(width / aspectRatio);
        heightTextField.setText(Integer.toString(height));
      } else if (height > 0) {
        width = (int)(aspectRatio * height);
        widthTextField.setText(Integer.toString(width));
      }
    }
  }
  



  void constrainAspectRatioCheckBox_actionPerformed(ActionEvent ev)
  {
    updateRatio();
  }
  




  private BorderLayout borderLayout1 = new BorderLayout();
  private Border border1;
  private Border setTitleBorder;
  private Border border2;
  private JPanel mainPanel = new JPanel();
  private JFileChooser htmlFileChooser = new JFileChooser();
  private JPanel fileChooserPanel = new JPanel();
  private Border border3;
  private JTextField heightTextField = new JTextField();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private GridBagLayout setTitleGridBagLayout = new GridBagLayout();
  private JTextField widthTextField = new JTextField();
  private JLabel heightLabel = new JLabel();
  private JLabel widthLabel = new JLabel();
  private JCheckBox constrainAspectRatioCheckBox = new JCheckBox();
  private JPanel controlsPanel = new JPanel();
  private JPanel setTitlePanel = new JPanel();
  private JTextField setTitleTextField = new JTextField(
    Messages.getString("My_Alice_World"));
  private GridBagLayout gridBagLayout2 = new GridBagLayout();
  private Border border4;
  private TitledBorder titledBorder1;
  private GridBagLayout gridBagLayout3 = new GridBagLayout();
  private JPanel directoryPathPanel = new JPanel();
  private String rootDirectoryPath = "c:\\";
  private String localDirectoryPath = Messages.getString("My_Alice_World");
  private JLabel directoryPath = new JLabel(
    rootDirectoryPath + localDirectoryPath + File.separator);
  private JButton browseDirectoryButton = new JButton(
    Messages.getString("browse"));
  private JLabel a2wFileName = new JLabel(
    Messages.getString("My_Alice_World_a2w"));
  private JLabel htmlFileName = new JLabel(
    Messages.getString("My_Alice_World_html"));
  private JLabel appletLabel = new JLabel(
    "aliceapplet.jar");
  private JTextField authorNameTextField = new JTextField();
  private JLabel authorLabel = new JLabel(
    Messages.getString("Author_s_name"));
  
  private String a2wTitle = Messages.getString("Your_world");
  private String htmlTitle = Messages.getString("The_web_page");
  private String appletTitle = Messages.getString("The_Alice_applet");
  
  private JCheckBox saveDirectoryCheckBox = new JCheckBox();
  private JCheckBox saveCodeCheckBox = new JCheckBox();
  

  private JLabel filesToSaveLabel = new JLabel(
    Messages.getString("These_files_will_be_saved_in_this_directory_"));
  private JButton saveButton = new JButton(
    Messages.getString("Save"));
  private JButton cancelButton = new JButton(
    Messages.getString("Cancel"));
  
  private void jbInit() {
    border1 = new TitledBorder(
    
      BorderFactory.createEtchedBorder(Color.white, 
      new Color(142, 142, 142)), 
      Messages.getString("Size_in_browser"));
    setTitleBorder = new TitledBorder(
    
      BorderFactory.createEtchedBorder(Color.white, 
      new Color(142, 142, 142)), 
      Messages.getString("Title"));
    border2 = BorderFactory.createEmptyBorder(8, 8, 0, 8);
    border3 = BorderFactory.createEmptyBorder(12, 12, 12, 12);
    border4 = BorderFactory.createEtchedBorder(
      Color.white, new Color(142, 142, 142));
    titledBorder1 = new TitledBorder(border4, 
      Messages.getString("Save_Location"));
    fileChooserPanel.setLayout(gridBagLayout3);
    setLayout(borderLayout1);
    mainPanel.setLayout(gridBagLayout2);
    mainPanel.setBorder(border3);
    heightTextField.setColumns(6);
    widthTextField.setColumns(6);
    heightLabel.setText(Messages.getString("height_"));
    widthLabel.setText(Messages.getString("width_"));
    setTitleTextField.setColumns(36);
    authorNameTextField.setColumns(28);
    constrainAspectRatioCheckBox.setText(
      Messages.getString("constrain_aspect_ratio"));
    constrainAspectRatioCheckBox
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          constrainAspectRatioCheckBox_actionPerformed(e);
        }
      });
    controlsPanel.setLayout(gridBagLayout1);
    controlsPanel.setBorder(border1);
    
    setTitlePanel.setLayout(setTitleGridBagLayout);
    setTitlePanel.setBorder(setTitleBorder);
    
    fileChooserPanel.setBorder(titledBorder1);
    htmlFileChooser
      .setFileSelectionMode(1);
    

    int fontSize = Integer.parseInt(authoringToolConfig
      .getValue("fontSize"));
    directoryPathPanel.setPreferredSize(new java.awt.Dimension(300, 
      fontSize * 2 - 5));
    directoryPathPanel.setBorder(
      BorderFactory.createLineBorder(Color.black, 1));
    directoryPathPanel.setLayout(new BorderLayout());
    directoryPathPanel.add(directoryPath, "Center");
    add(mainPanel, "Center");
    filesToSaveLabel.setForeground(Color.black);
    saveDirectoryCheckBox.setSelected(true);
    saveDirectoryCheckBox
      .setText(
      Messages.getString("Use_title_to_create_new_directory_for_the_files_below"));
    saveCodeCheckBox.setSelected(false);
    saveCodeCheckBox.setText(
      Messages.getString("Add_the_code_for_this_world_to_this_page"));
    
    a2wFileName.setToolTipText(a2wTitle);
    a2wFileName.setBounds(2, 2, 2, 2);
    htmlFileName.setToolTipText(htmlTitle);
    htmlFileName.setBounds(2, 2, 2, 2);
    appletLabel.setToolTipText(appletTitle);
    appletLabel.setBounds(2, 2, 2, 2);
    
    setTitlePanel.add(setTitleTextField, new GridBagConstraints(0, 0, 2, 1, 
      1.0D, 1.0D, 17, 0, 
      new Insets(4, 4, 4, 4), 0, 0));
    setTitlePanel.add(authorNameTextField, new GridBagConstraints(1, 1, 1, 
      1, 1.0D, 1.0D, 17, 0, 
      new Insets(0, 4, 0, 0), 0, 0));
    setTitlePanel.add(authorLabel, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 
      4, 0, 0), 0, 0));
    
    controlsPanel.add(widthLabel, new GridBagConstraints(0, 1, 1, 1, 0.0D, 
      0.0D, 14, 0, 
      new Insets(0, 20, 4, 4), 0, 0));
    controlsPanel.add(widthTextField, new GridBagConstraints(1, 1, 1, 1, 
      0.0D, 0.0D, 16, 
      0, new Insets(0, 0, 4, 0), 0, 0));
    controlsPanel.add(heightLabel, new GridBagConstraints(0, 2, 1, 1, 0.0D, 
      0.0D, 14, 0, 
      new Insets(0, 20, 4, 4), 0, 0));
    controlsPanel.add(heightTextField, new GridBagConstraints(1, 2, 1, 1, 
      0.0D, 0.0D, 16, 
      0, new Insets(0, 0, 4, 0), 0, 0));
    controlsPanel.add(constrainAspectRatioCheckBox, new GridBagConstraints(
      1, 3, 1, 1, 1.0D, 0.0D, 16, 
      0, new Insets(0, 0, 4, 0), 0, 0));
    
    fileChooserPanel.add(directoryPathPanel, new GridBagConstraints(0, 0, 
      2, 1, 1.0D, 1.0D, 17, 
      2, new Insets(4, 8, 0, 8), 0, 0));
    fileChooserPanel.add(browseDirectoryButton, new GridBagConstraints(2, 
      0, 1, 1, 1.0D, 1.0D, 17, 
      0, new Insets(4, 0, 0, 8), 0, 0));
    
    fileChooserPanel.add(saveDirectoryCheckBox, new GridBagConstraints(0, 
      1, 1, 1, 1.0D, 1.0D, 17, 
      0, new Insets(2, 8, 4, 0), 0, 0));
    fileChooserPanel.add(saveCodeCheckBox, new GridBagConstraints(0, 2, 1, 
      1, 1.0D, 1.0D, 17, 0, 
      new Insets(2, 8, 4, 0), 0, 0));
    





    fileChooserPanel.add(filesToSaveLabel, new GridBagConstraints(0, 3, 2, 
      1, 1.0D, 1.0D, 18, 
      0, new Insets(4, 10, 0, 0), 0, 0));
    
    fileChooserPanel.add(a2wFileName, new GridBagConstraints(0, 4, 2, 1, 
      1.0D, 1.0D, 18, 
      0, new Insets(2, 20, 0, 0), 0, 0));
    fileChooserPanel.add(htmlFileName, new GridBagConstraints(0, 5, 2, 1, 
      1.0D, 1.0D, 18, 
      0, new Insets(4, 20, 0, 0), 0, 0));
    fileChooserPanel.add(appletLabel, new GridBagConstraints(0, 6, 2, 1, 
      1.0D, 1.0D, 18, 
      0, new Insets(4, 20, 0, 0), 0, 0));
    
    mainPanel.add(setTitlePanel, new GridBagConstraints(0, 0, 3, 1, 1.0D, 
      0.0D, 10, 1, 
      new Insets(0, 0, 12, 0), 0, 0));
    mainPanel.add(controlsPanel, new GridBagConstraints(0, 1, 3, 1, 1.0D, 
      0.0D, 10, 1, 
      new Insets(0, 0, 12, 0), 0, 0));
    mainPanel.add(fileChooserPanel, new GridBagConstraints(0, 2, 3, 1, 1.0D, 
      1.0D, 10, 1, 
      new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(javax.swing.Box.createHorizontalGlue(), 
      new GridBagConstraints(0, 3, 1, 1, 1.0D, 1.0D, 
      10, 1, 
      new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(saveButton, new GridBagConstraints(1, 3, 1, 1, 0.0D, 0.0D, 
      10, 0, new Insets(
      4, 0, 4, 0), 0, 0));
    mainPanel.add(cancelButton, new GridBagConstraints(2, 3, 1, 1, 0.0D, 0.0D, 
      10, 0, new Insets(
      4, 4, 4, 0), 0, 0));
  }
}
