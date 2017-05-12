package edu.cmu.cs.stage3.caitlin.personbuilder;

import edu.cmu.cs.stage3.progress.ProgressCancelException;
import edu.cmu.cs.stage3.progress.ProgressObserver;
import java.awt.CardLayout;
import java.net.URL;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.w3c.dom.Node;




















public class AllStepsPanel
  extends JPanel
{
  protected CardLayout cLayout = null;
  protected int selectedPanel = 0;
  protected int numSteps = 0;
  protected ImageIcon nextImage = null;
  protected ImageIcon backImage = null;
  protected Vector stepPanels = new Vector();
  
  public AllStepsPanel(Node root, ModelWrapper modelWrapper, ProgressObserver progressObserver, int progressOffset) throws ProgressCancelException {
    createGuiElements(root, modelWrapper, progressObserver, progressOffset);
    setSelected(1);
  }
  
  private void createGuiElements(Node root, ModelWrapper modelWrapper, ProgressObserver progressObserver, int progressOffset) throws ProgressCancelException {
    Vector imageURLs = XMLDirectoryUtilities.getImageURLs(root);
    for (int i = 0; i < imageURLs.size(); i++) {
      URL url = (URL)imageURLs.elementAt(i);
      if (url.toString().indexOf("stepNext.jpg") != -1) {
        nextImage = new ImageIcon(url);
      } else if (url.toString().indexOf("stepBack.jpg") != -1) {
        backImage = new ImageIcon(url);
      }
    }
    Vector stepNodes = XMLDirectoryUtilities.getDirectories(root);
    if (stepNodes != null) {
      cLayout = new CardLayout();
      setLayout(cLayout);
      numSteps = stepNodes.size();
      progressObserver.progressUpdateTotal(progressOffset + stepNodes.size());
      for (int i = 0; i < stepNodes.size(); i++) {
        Node currentStepNode = (Node)stepNodes.elementAt(i);
        Vector stepImages = XMLDirectoryUtilities.getImages(currentStepNode);
        if ((stepImages != null) && (stepImages.size() > 0)) {
          StepPanel stepPanel = new StepPanel(currentStepNode, nextImage, backImage, modelWrapper);
          stepPanels.addElement(stepPanel);
          add(stepPanel, "Step " + (i + 1) + " Panel");
        }
        progressObserver.progressUpdate(progressOffset++, null);
      }
    }
  }
  
  public void resetDefaults() {
    for (int i = 0; i < stepPanels.size(); i++) {
      StepPanel stepPanel = (StepPanel)stepPanels.elementAt(i);
      stepPanel.resetDefaults();
    }
  }
  
  public int getSelected() {
    return selectedPanel;
  }
  
  public void setSelected(int i) {
    if (i < 1)
      i = 1;
    if (i > numSteps)
      i = numSteps;
    selectedPanel = i;
    cLayout.show(this, "Step " + i + " Panel");
  }
}
