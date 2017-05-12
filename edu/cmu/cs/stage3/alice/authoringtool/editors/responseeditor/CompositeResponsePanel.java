package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import edu.cmu.cs.stage3.lang.Messages;






























public abstract class CompositeResponsePanel
  extends CompositeElementPanel
{
  protected CompositeResponse m_response;
  
  public CompositeResponsePanel()
  {
    headerText = Messages.getString("Composite_Response");
  }
  
  public void set(CompositeResponse response, AuthoringTool authoringToolIn) {
    m_response = ((CompositeResponse)m_element);
    super.set(response, authoringToolIn);
  }
  
  public Response getResponse() {
    return m_response;
  }
}
