package edu.cmu.cs.stage3.caitlin.personbuilder;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;



















public class RenderPanel
  extends JPanel
{
  ModelWrapper modelWrapper = null;
  
  public RenderPanel(ModelWrapper modelWrapper) {
    this.modelWrapper = modelWrapper;
    setLayout(new BorderLayout());
  }
  
  public void initialize() {
    add(modelWrapper.getRenderPanel(), "Center");
    JLabel label = new JLabel(Messages.getString("Click_and_drag_to_rotate_your_person"));
    label.setBackground(new Color(155, 159, 206));
    label.setForeground(Color.black);
    label.setHorizontalAlignment(0);
    add(label, "North");
  }
}
