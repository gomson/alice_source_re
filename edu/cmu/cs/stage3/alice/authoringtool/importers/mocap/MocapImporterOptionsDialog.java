package edu.cmu.cs.stage3.alice.authoringtool.importers.mocap;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.filechooser.FileFilter;

























public class MocapImporterOptionsDialog
  extends JDialog
{
  class ElementOrNullWrapper
  {
    public Element obj = null;
    public String text = null;
    
    public ElementOrNullWrapper(Element o, String t) {
      obj = o;
      text = t;
    }
    
    public ElementOrNullWrapper(Element o) {
      obj = o;
    }
    
    public String toString() {
      if (text != null) {
        return text;
      }
      return obj.name.getStringValue();
    }
  }
  

  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JList partsList = new JList();
  JLabel promptLabel = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JComboBox nativeFPSCombo = new JComboBox();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JComboBox aliceFPSCombo = new JComboBox();
  JButton okButton = new JButton();
  JLabel jLabel3 = new JLabel();
  JTextField jSkelFile = new JTextField();
  JButton jFileBoxButton = new JButton();
  JButton cancelButton = new JButton();
  
  public Element selectedPart = null;
  public double fps = 30.0D;
  public int nativeFPS = 60;
  public String asfFile = "etc/Skeleton.asf";
  public File ASFPath = new File("");
  public boolean ok = false;
  
  public MocapImporterOptionsDialog()
  {
    try {
      setContentPane(new JPanel());
      jbInit();
      guiInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void guiInit() {
    DefaultListModel listOfStuff = new DefaultListModel();
    partsList.setModel(listOfStuff);
    
    listOfStuff.addElement(new ElementOrNullWrapper(null, Messages.getString("Build_Model_from_skeleton_file")));
    
    Sandbox[] possibilities = (Sandbox[])getHackgetWorldsandboxes.getArrayValue();
    
    for (int i = 0; i < possibilities.length; i++) {
      listOfStuff.addElement(new ElementOrNullWrapper(possibilities[i]));
    }
    partsList.setSelectedIndex(0);
    
    aliceFPSCombo.insertItemAt("15", 0);
    aliceFPSCombo.insertItemAt("30", 1);
    aliceFPSCombo.setSelectedIndex(1);
    
    nativeFPSCombo.insertItemAt("60", 0);
    nativeFPSCombo.insertItemAt("120", 1);
    nativeFPSCombo.setSelectedIndex(0);
  }
  
  private void jbInit() throws Exception {
    getContentPane().setLayout(gridBagLayout1);
    partsList.setSelectionMode(0);
    promptLabel.setText(Messages.getString("Which_character_shall_I_animate_"));
    setModal(true);
    setTitle(Messages.getString("Mocap_Importer_Options"));
    jLabel1.setText(Messages.getString("Motion_Captured_FPS_"));
    jLabel2.setText(Messages.getString("Alice_KeyFrames_sec_"));
    okButton.setText("OK");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jLabel3.setText(Messages.getString("Skeleton_File_"));
    jSkelFile.setPreferredSize(new Dimension(100, 21));
    jSkelFile.setText("etc/Skeleton.asf");
    jFileBoxButton.setText("...");
    jFileBoxButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jFileBoxButton_actionPerformed(e);
      }
    });
    nativeFPSCombo.setEditable(true);
    aliceFPSCombo.setEditable(true);
    cancelButton.setText(Messages.getString("Cancel"));
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    getContentPane().add(promptLabel, new GridBagConstraints(0, 0, 3, 1, 0.0D, 0.0D, 
      17, 0, new Insets(5, 5, 0, 0), 0, 0));
    getContentPane().add(jScrollPane1, new GridBagConstraints(0, 1, 3, 1, 1.0D, 1.0D, 
      10, 1, new Insets(0, 0, 0, 0), 0, 60));
    getContentPane().add(jLabel1, new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 
      13, 0, new Insets(0, 5, 0, 5), 0, 0));
    getContentPane().add(jLabel2, new GridBagConstraints(0, 4, 1, 2, 0.0D, 0.0D, 
      13, 0, new Insets(0, 5, 0, 5), 0, 0));
    getContentPane().add(nativeFPSCombo, new GridBagConstraints(1, 3, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(5, 5, 0, 0), 0, 0));
    getContentPane().add(aliceFPSCombo, new GridBagConstraints(1, 4, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(5, 5, 0, 0), 0, 0));
    getContentPane().add(jLabel3, new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 
      13, 0, new Insets(0, 5, 0, 5), 0, 0));
    getContentPane().add(jSkelFile, new GridBagConstraints(1, 2, 1, 1, 1.0D, 0.0D, 
      17, 2, new Insets(5, 5, 0, 0), 0, 0));
    jScrollPane1.getViewport().add(partsList, null);
    getContentPane().add(jFileBoxButton, new GridBagConstraints(2, 2, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(5, 1, 0, 5), -19, -8));
    getContentPane().add(okButton, new GridBagConstraints(0, 6, 1, 1, 0.0D, 0.0D, 
      10, 0, new Insets(5, 0, 5, 0), 0, 0));
    getContentPane().add(cancelButton, new GridBagConstraints(1, 6, 1, 1, 0.0D, 0.0D, 
      10, 0, new Insets(0, 0, 0, 0), 0, 0));
  }
  
  void okButton_actionPerformed(ActionEvent e) {
    selectedPart = partsList.getSelectedValue()).obj;
    asfFile = jSkelFile.getText();
    fps = Double.parseDouble((String)aliceFPSCombo.getSelectedItem());
    nativeFPS = Integer.parseInt((String)nativeFPSCombo.getSelectedItem());
    ok = true;
    setVisible(false);
  }
  
  public Element getSelectedPart() {
    return selectedPart;
  }
  
  public double getFPS() {
    return fps;
  }
  
  public int getNativeFPS() {
    return nativeFPS;
  }
  
  public String getASFFile() {
    return asfFile;
  }
  
  public void setNativeFPS(int nfps) {
    nativeFPS = nfps;
    if (nfps == 60) {
      nativeFPSCombo.setSelectedIndex(0);
    } else if (nfps == 120) {
      nativeFPSCombo.setSelectedIndex(1);
    } else {
      nativeFPSCombo.insertItemAt(String.valueOf(nativeFPS), 2);
      nativeFPSCombo.setSelectedIndex(2);
    }
  }
  
  public void setASFFile(String filename) {
    jSkelFile.setText(filename);
  }
  
  public void setASFPath(File path) {
    ASFPath = path;
  }
  
  void jFileBoxButton_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new JFileChooser();
    














    chooser.setFileFilter(new FileFilter()
    {
      public boolean accept(File f)
      {
        if (f.isDirectory())
          return true;
        if (f.toString().toLowerCase().endsWith(".asf")) {
          return true;
        }
        return false;
      }
      
      public String getDescription() {
        return "ASF - Acclaim Skeleton File";
      }
    });
    try
    {
      chooser.setCurrentDirectory(ASFPath);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    
    int returnVal = chooser.showOpenDialog(null);
    if (returnVal == 0)
      jSkelFile.setText(chooser.getSelectedFile().getPath());
  }
  
  void cancelButton_actionPerformed(ActionEvent e) {
    ok = false;
    setVisible(false);
  }
}
