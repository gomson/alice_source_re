package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.HighlightingGlassPane;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.SimulationException;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Component;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JPanel;












public class SimulationExceptionPanel
  extends JPanel
{
  private AuthoringTool m_authoringTool;
  private SimulationException m_simulationException;
  private HighlightingGlassPane m_glassPane;
  private JLabel m_descriptionLabel = new JLabel();
  


  public SimulationExceptionPanel(AuthoringTool authoringTool)
  {
    m_authoringTool = authoringTool;
    m_glassPane = new HighlightingGlassPane(
      authoringTool);
    
    setLayout(new GridBagLayout());
    
    GridBagConstraints gbc = new GridBagConstraints();
    anchor = 18;
    fill = 1;
    gridwidth = 0;
    
    add(new JLabel(
      Messages.getString("Alice_has_detected_a_problem_with_your_world_")), 
      gbc);
    insets.left = 8;
    add(m_descriptionLabel, gbc);
  }
  

  public void setSimulationException(SimulationException simulationException)
  {
    m_simulationException = simulationException;
    
    m_descriptionLabel.setText(simulationException.getMessage());
    






    Element element = simulationException
      .getElement();
    World world = m_authoringTool.getWorld();
    Element ancestor = null;
    Element[] userDefinedResponses = world
      .getDescendants(UserDefinedResponse.class);
    for (int i = 0; i < userDefinedResponses.length; i++) {
      if (userDefinedResponses[i].isAncestorOf(element)) {
        ancestor = userDefinedResponses[i];
        break;
      }
    }
    if (ancestor == null) {
      Element[] userDefinedQuestions = world
        .getDescendants(Component.class);
      for (int i = 0; i < userDefinedQuestions.length; i++) {
        if (userDefinedQuestions[i].isAncestorOf(element)) {
          ancestor = userDefinedQuestions[i];
          break;
        }
      }
    }
    
    String highlightID = null;
    if ((m_simulationException instanceof SimulationPropertyException)) {
      SimulationPropertyException spe = (SimulationPropertyException)m_simulationException;
      highlightID = "editors:element<" + ancestor.getKey(world) + ">:elementTile<" + element.getKey(world) + ">:property<" + spe.getProperty().getName() + ">";
    }
    if ((highlightID == null) && 
      (element != null) && (ancestor != null)) {
      highlightID = 
        "editors:element<" + ancestor.getKey(world) + ">:elementTile<" + element.getKey(world) + ">";
    }
    
    m_glassPane.setHighlightID(highlightID);
  }
  
  public void setErrorHighlightingEnabled(boolean enabled) {
    m_glassPane.setHighlightingEnabled(enabled);
  }
  
  public Rectangle getErrorRect() {
    return m_glassPane.getHighlightRect();
  }
}
