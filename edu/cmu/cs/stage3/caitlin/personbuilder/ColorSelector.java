package edu.cmu.cs.stage3.caitlin.personbuilder;

import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;








public class ColorSelector
  extends JPanel
  implements ActionListener, ChangeListener
{
  protected JButton otherColorsButton = null;
  
  protected ButtonGroup whichColorChooserGroup = null;
  protected JRadioButton otherColorsRadio = null;
  protected JRadioButton humanColorsRadio = null;
  protected JSlider colorSlider = null;
  
  protected Color skinColor;
  
  protected JColorChooser colorChooser;
  protected ModelWrapper modelWrapper = null;
  
  public ColorSelector(ModelWrapper modelWrapper) {
    this.modelWrapper = modelWrapper;
    
    setBackground(new Color(155, 159, 206));
    
    init();
  }
  
  protected void init() {
    setLayout(new BorderLayout());
    

    humanColorsRadio = new JRadioButton();
    humanColorsRadio.setSelected(true);
    humanColorsRadio.setBackground(new Color(155, 159, 206));
    otherColorsRadio = new JRadioButton();
    otherColorsRadio.setSelected(false);
    otherColorsRadio.setBackground(new Color(155, 159, 206));
    

    whichColorChooserGroup = new ButtonGroup();
    whichColorChooserGroup.add(humanColorsRadio);
    whichColorChooserGroup.add(otherColorsRadio);
    

    humanColorsRadio.addActionListener(this);
    otherColorsRadio.addActionListener(this);
    
    JPanel humanColorPanel = new JPanel();
    humanColorPanel.setBackground(new Color(155, 159, 206));
    humanColorPanel.setLayout(new FlowLayout());
    colorSlider = new JSlider();
    skinColor = Color.getHSBColor(0.06963788F, 0.61F, 0.665F);
    modelWrapper.setColor(skinColor);
    colorSlider.setBackground(skinColor);
    colorSlider.addChangeListener(this);
    
    humanColorPanel.add(humanColorsRadio);
    humanColorPanel.add(colorSlider);
    
    JPanel otherColorPanel = new JPanel();
    otherColorPanel.setBackground(new Color(155, 159, 206));
    otherColorPanel.setLayout(new FlowLayout());
    
    otherColorsButton = new JButton(Messages.getString("Choose_skin_color___"));
    otherColorsButton.addActionListener(this);
    otherColorsButton.setEnabled(false);
    
    otherColorPanel.add(otherColorsRadio);
    otherColorPanel.add(otherColorsButton);
    
    add(humanColorPanel, "North");
    add(otherColorPanel, "Center");
  }
  
  public void actionPerformed(ActionEvent ae) {
    if (ae.getSource() == otherColorsButton) {
      if (colorChooser == null) {
        colorChooser = new JColorChooser();
      }
      Color selectedColor = DialogManager.showDialog(colorChooser, Messages.getString("more_colors___"), skinColor);
      if (selectedColor != null)
        modelWrapper.setColor(selectedColor);
    } else if (ae.getSource() == otherColorsRadio) {
      colorSlider.setEnabled(false);
      otherColorsButton.setEnabled(true);
    } else if (ae.getSource() == humanColorsRadio) {
      colorSlider.setEnabled(true);
      modelWrapper.setColor(skinColor);
      otherColorsButton.setEnabled(false);
    }
  }
  
  public void stateChanged(ChangeEvent ce) {
    int position = colorSlider.getValue();
    float s = 0.89F + -0.56F * position / 100.0F;
    float b = 0.33F + 0.67F * position / 100.0F;
    skinColor = Color.getHSBColor(0.06963788F, s, b);
    colorSlider.setBackground(skinColor);
    if (!colorSlider.getValueIsAdjusting()) {
      modelWrapper.setColor(skinColor);
    }
  }
}
