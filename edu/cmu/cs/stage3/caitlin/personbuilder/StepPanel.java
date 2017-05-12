package edu.cmu.cs.stage3.caitlin.personbuilder;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.w3c.dom.Node;






















public class StepPanel
  extends JPanel
{
  ImageIcon backImage = null;
  ImageIcon nextImage = null;
  Vector choosers = new Vector();
  
  public StepPanel(Node stepNode, ImageIcon nextImage, ImageIcon backImage, ModelWrapper modelWrapper) {
    setBackground(new Color(155, 159, 206));
    this.backImage = backImage;
    this.nextImage = nextImage;
    
    choosers = getChoosers(stepNode, modelWrapper);
    addChoosers(choosers);
  }
  
  public void resetDefaults() {
    for (int i = 0; i < choosers.size(); i++) {
      if ((choosers.elementAt(i) instanceof ItemChooser)) {
        ItemChooser chooser = (ItemChooser)choosers.elementAt(i);
        chooser.resetDefaults();
      }
    }
  }
  
  private Vector getChoosers(Node stepNode, ModelWrapper modelWrapper)
  {
    Vector choosers = new Vector();
    
    Vector colorNodes = XMLDirectoryUtilities.getSetColorNodes(stepNode);
    for (int i = 0; i < colorNodes.size(); i++) {
      ColorSelector colorSelector = new ColorSelector(modelWrapper);
      choosers.addElement(colorSelector);
    }
    
    Vector chooserNodes = XMLDirectoryUtilities.getDirectories(stepNode);
    double incr = 3.0D / chooserNodes.size();
    for (int i = 0; i < chooserNodes.size(); i++) {
      Node chooserNode = (Node)chooserNodes.elementAt(i);
      if (chooserNode.getNodeName().equals("directory")) {
        ItemChooser chooser = new ItemChooser(chooserNode, nextImage, backImage, modelWrapper);
        choosers.addElement(chooser);
      }
    }
    return choosers;
  }
  
  private void addChoosers(Vector choosers) {
    setLayout(new GridLayout(3, 1));
    for (int i = 0; i < choosers.size(); i++) {
      add((JPanel)choosers.elementAt(i));
    }
  }
}
