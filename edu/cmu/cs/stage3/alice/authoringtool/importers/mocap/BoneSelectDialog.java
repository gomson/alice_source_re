package edu.cmu.cs.stage3.alice.authoringtool.importers.mocap;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;





























public class BoneSelectDialog
  extends JDialog
{
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JList partsList = new JList();
  JButton selectButton = new JButton();
  JButton skipButton = new JButton();
  JLabel promptLabel = new JLabel();
  JButton limpButton = new JButton();
  
  public Element selectedPart = null;
  public boolean descend = false;
  JScrollPane jScrollPane1 = new JScrollPane();
  
  public BoneSelectDialog(String matching, Element[] possibilities) {
    try {
      setContentPane(new JPanel());
      jbInit();
      guiInit(matching, possibilities);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void guiInit(String matching, Element[] possibilities) {
    DefaultListModel listOfStuff = new DefaultListModel();
    partsList.setModel(listOfStuff);
    for (int i = 0; i < possibilities.length; i++) {
      listOfStuff.addElement(possibilities[i]);
    }
    promptLabel.setText(Messages.getString("Which_Part_is_the_bone_") + matching + "?");
  }
  
  private void jbInit() throws Exception {
    getContentPane().setLayout(gridBagLayout1);
    partsList.setSelectionMode(0);
    selectButton.setText(Messages.getString("Select"));
    selectButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        selectButton_actionPerformed(e);
      }
    });
    skipButton.setText(Messages.getString("Skip_Bone"));
    skipButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        skipButton_actionPerformed(e);
      }
    });
    promptLabel.setText("jLabel1");
    setModal(true);
    setTitle(Messages.getString("Select_Matching_Bone"));
    limpButton.setText(Messages.getString("Skip_Limb"));
    limpButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        limpButton_actionPerformed(e);
      }
    });
    getContentPane().add(selectButton, new GridBagConstraints(0, 2, 1, 1, 0.5D, 0.0D, 
      10, 0, new Insets(5, 0, 5, 0), 0, 0));
    getContentPane().add(skipButton, new GridBagConstraints(1, 2, 1, 1, 0.5D, 0.0D, 
      10, 0, new Insets(5, 0, 5, 0), 0, 0));
    getContentPane().add(promptLabel, new GridBagConstraints(0, 0, 2, 1, 0.0D, 0.0D, 
      17, 0, new Insets(5, 5, 0, 0), 0, 0));
    getContentPane().add(limpButton, new GridBagConstraints(2, 2, 1, 1, 0.5D, 0.0D, 
      10, 0, new Insets(5, 0, 5, 0), 0, 0));
    getContentPane().add(jScrollPane1, new GridBagConstraints(0, 1, 3, 1, 1.0D, 1.0D, 
      10, 1, new Insets(0, 0, 0, 0), 0, 60));
    jScrollPane1.getViewport().add(partsList, null);
  }
  
  void skipButton_actionPerformed(ActionEvent e) {
    selectedPart = null;
    descend = true;
    setVisible(false);
  }
  
  void selectButton_actionPerformed(ActionEvent e) {
    selectedPart = ((Element)partsList.getSelectedValue());
    descend = false;
    setVisible(false);
  }
  
  void limpButton_actionPerformed(ActionEvent e) {
    selectedPart = null;
    descend = false;
    setVisible(false);
  }
  
  public Element getSelectedPart() {
    return selectedPart;
  }
  
  public boolean doDescend() {
    return descend;
  }
}
