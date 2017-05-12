package edu.cmu.cs.stage3.caitlin.personbuilder;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.w3c.dom.Node;





















public class NavigationPanel
  extends JPanel
  implements ActionListener
{
  protected Vector stepButtons = new Vector();
  protected Vector stepImages = new Vector();
  protected Vector selStepImages = new Vector();
  protected JButton backButton = null;
  protected JButton nextButton = null;
  protected ImageIcon spacerIcon = null;
  protected ImageIcon noBackImage = null;
  protected ImageIcon noNextImage = null;
  protected ImageIcon backImage = null;
  protected ImageIcon nextImage = null;
  protected int stepIndex = 0;
  protected AllStepsPanel allStepsPanel;
  
  public NavigationPanel(Node root, AllStepsPanel allStepsPanel) {
    createGuiElements(root);
    this.allStepsPanel = allStepsPanel;
    addGuiElements();
    setSelectedStep(0, 1);
  }
  
  public void setFirstStep() {
    int index = allStepsPanel.getSelected();
    allStepsPanel.setSelected(0);
    setSelectedStep(0, index - 1);
  }
  
  public void actionPerformed(ActionEvent ae) {
    Object actionObj = ae.getSource();
    int index = stepButtons.indexOf(actionObj);
    if ((index <= 0) || (index >= stepButtons.size() - 1)) {
      if (index == 0) {
        index = allStepsPanel.getSelected();
        index--;
      } else if (index == stepButtons.size() - 1) {
        index = allStepsPanel.getSelected();
        index++;
      }
    }
    
    int prevStep = allStepsPanel.getSelected() - 1;
    allStepsPanel.setSelected(index);
    int curStep = allStepsPanel.getSelected() - 1;
    
    setSelectedStep(curStep, prevStep);
  }
  
  protected void setSelectedStep(int curStep, int prevStep) {
    if ((prevStep >= 0) && (curStep != prevStep)) {
      JButton curButton = (JButton)stepButtons.elementAt(prevStep + 1);
      ImageIcon curImage = (ImageIcon)stepImages.elementAt(prevStep);
      curButton.setIcon(curImage);
    }
    
    if ((prevStep < selStepImages.size()) && (curStep != prevStep)) {
      JButton newButton = (JButton)stepButtons.elementAt(curStep + 1);
      ImageIcon newImage = (ImageIcon)selStepImages.elementAt(curStep);
      newButton.setIcon(newImage);
    }
    
    if (curStep == 0) {
      backButton.setIcon(noBackImage);
    } else {
      backButton.setIcon(backImage);
    }
    
    if (curStep == selStepImages.size() - 1) {
      nextButton.setIcon(noNextImage);
    } else {
      nextButton.setIcon(nextImage);
    }
  }
  
  private void addGuiElements()
  {
    setLayout(new FlowLayout(1, 0, 5));
    setBackground(new Color(155, 159, 206));
    for (int i = 0; i < stepButtons.size(); i++) {
      add((JButton)stepButtons.elementAt(i));
      if (i != stepButtons.size() - 1) {
        JLabel spLabel = new JLabel(spacerIcon);
        add(spLabel);
      }
    }
  }
  
  private void createGuiElements(Node root) {
    Vector imageURLs = XMLDirectoryUtilities.getImageURLs(root);
    for (int i = 0; i < imageURLs.size(); i++) {
      URL url = (URL)imageURLs.elementAt(i);
      if (url.toString().indexOf("nextBtn.jpg") != -1) {
        nextImage = new ImageIcon(url);
        nextButton = new JButton(nextImage);
        nextButton.setBorderPainted(false);
        nextButton.setBorder(null);
        nextButton.addActionListener(this);
      } else if (url.toString().indexOf("backBtn.jpg") != -1) {
        backImage = new ImageIcon(url);
        backButton = new JButton(backImage);
        backButton.setBorderPainted(false);
        backButton.setBorder(null);
        backButton.addActionListener(this);
        stepButtons.addElement(backButton);
      } else if (url.toString().indexOf("noBackBtn.jpg") != -1) {
        noBackImage = new ImageIcon(url);
      } else if (url.toString().indexOf("noNextBtn.jpg") != -1) {
        noNextImage = new ImageIcon(url);
      } else if (url.toString().indexOf("spacer.jpg") != -1) {
        spacerIcon = new ImageIcon(url);
      }
    }
    
    Vector stepNodes = XMLDirectoryUtilities.getDirectories(root);
    for (int i = 0; i < stepNodes.size(); i++) {
      Node currentStepNode = (Node)stepNodes.elementAt(i);
      Vector currentStepImages = XMLDirectoryUtilities.getImageURLs(currentStepNode);
      if ((currentStepImages != null) && (currentStepImages.size() > 1)) {
        ImageIcon icon = new ImageIcon((URL)currentStepImages.elementAt(0));
        ImageIcon selIcon = new ImageIcon((URL)currentStepImages.elementAt(1));
        stepImages.addElement(icon);
        selStepImages.addElement(selIcon);
        JButton stepBtn = new JButton(icon);
        stepBtn.setBorderPainted(false);
        stepBtn.addActionListener(this);
        stepBtn.setBorder(null);
        stepButtons.addElement(stepBtn);
      }
    }
    
    stepButtons.addElement(nextButton);
  }
}
