package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;


















































































class CustomListButton
  extends JButton
  implements ActionListener
{
  private Vector checkBoxes = new Vector();
  
  public CustomListButton() {
    addActionListener(this);
    setHorizontalAlignment(2);
  }
  
  public void addCheckBox(CustomCheckBox c)
  {
    checkBoxes.add(c);
  }
  
  public void actionPerformed(ActionEvent e) {
    boolean areAllSelected = true;
    for (int i = 0; i < checkBoxes.size(); i++) {
      CustomCheckBox currentCheckBox = (CustomCheckBox)checkBoxes.get(i);
      if (!currentCheckBox.isSelected()) {
        areAllSelected = false;
        break;
      }
    }
    if (areAllSelected) {
      for (int i = 0; i < checkBoxes.size(); i++) {
        CustomCheckBox currentCheckBox = 
          (CustomCheckBox)checkBoxes.get(i);
        currentCheckBox.setSelected(false);
      }
    } else {
      for (int i = 0; i < checkBoxes.size(); i++) {
        CustomCheckBox currentCheckBox = 
          (CustomCheckBox)checkBoxes.get(i);
        currentCheckBox.setSelected(true);
      }
    }
  }
}
