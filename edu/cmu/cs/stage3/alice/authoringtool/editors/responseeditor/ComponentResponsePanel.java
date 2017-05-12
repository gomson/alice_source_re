package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.ComponentElementPanel;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Response;
import javax.swing.BorderFactory;


























public class ComponentResponsePanel
  extends ComponentElementPanel
{
  protected Response m_response;
  
  public ComponentResponsePanel()
  {
    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    remove(grip);
  }
  
  public void set(Element element) {
    if ((element instanceof Response)) {
      m_response = ((Response)element);
      super.set(element);
    }
  }
  
  public Response getResponse() {
    return m_response;
  }
}
