package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.While;
import edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;













public abstract class WhileElementPanel
  extends CompositeElementPanel
{
  protected JComponent conditionalInput;
  protected JLabel endHeader;
  protected BooleanProperty m_condition;
  protected static BufferedImage whileLoopBackgroundImage;
  
  public WhileElementPanel()
  {
    headerText = Messages.getString("While");
    backgroundColor = AuthoringToolResources.getColor("WhileLoopInOrder");
  }
  
  protected void variableInit() {
    super.variableInit();
    if ((m_element instanceof WhileLoopInOrder)) {
      WhileLoopInOrder proxy = (WhileLoopInOrder)m_element;
      m_condition = condition;
    }
    else if ((m_element instanceof While)) {
      While proxy = (While)m_element;
      m_condition = condition;
    }
  }
  
  protected void startListening() {
    super.startListening();
    if (m_condition != null) {
      m_condition.addPropertyListener(this);
    }
  }
  
  protected void stopListening() {
    super.stopListening();
    if (m_condition != null) {
      m_condition.removePropertyListener(this);
    }
  }
  
  public void setHeaderLabel() {
    if (headerLabel != null) {
      headerLabel.setText(headerText);
      if (CompositeElementEditor.IS_JAVA) {
        headerLabel.setText("while (");
      }
    }
    if (endHeader != null) {
      endHeader.setText("");
      if (CompositeElementEditor.IS_JAVA) {
        if (!isExpanded) {
          endHeader.setText(") { " + getDots() + " }");
        }
        else {
          endHeader.setText(") {");
        }
      }
    }
  }
  
  protected void generateGUI()
  {
    super.generateGUI();
    if (endHeader == null) {
      endHeader = new JLabel();
    }
  }
  
  protected void restoreDrag() {
    super.restoreDrag();
    addDragSourceComponent(endHeader);
  }
  
  protected void updateGUI() {
    super.updateGUI();
    PopupItemFactory pif = new SetPropertyImmediatelyFactory(m_condition);
    conditionalInput = GUIFactory.getPropertyViewController(m_condition, true, true, AuthoringToolResources.shouldGUIOmitPropertyName(m_condition), pif);
    headerPanel.remove(glue);
    headerPanel.add(conditionalInput, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
    headerPanel.add(endHeader, new GridBagConstraints(4, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 2, 0, 2), 0, 0));
    headerPanel.add(glue, new GridBagConstraints(5, 0, 1, 1, 1.0D, 0.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
  }
  



  protected static Dimension whileLoopBackgroundImageSize = new Dimension(-1, -1);
  
  protected void createBackgroundImage(int width, int height) {
    whileLoopBackgroundImageSize.setSize(width, height);
    whileLoopBackgroundImage = new BufferedImage(width, height, 2);
    Graphics2D g = (Graphics2D)whileLoopBackgroundImage.getGraphics();
    g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
    g.setColor(backgroundColor);
    g.fillRect(0, 0, width, height);
  }
  




  protected void paintTextureEffect(Graphics g, Rectangle bounds)
  {
    if ((width > whileLoopBackgroundImageSizewidth) || (height > whileLoopBackgroundImageSizeheight)) {
      createBackgroundImage(width, height);
    }
    g.setClip(x, y, width, height);
    g.drawImage(whileLoopBackgroundImage, x, y, this);
  }
}
