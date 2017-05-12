package edu.cmu.cs.stage3.alice.authoringtool.util.event;

import edu.cmu.cs.stage3.alice.core.Element;























public class EditorPanelEvent
{
  protected Element element;
  
  public EditorPanelEvent(Element element)
  {
    this.element = element;
  }
  
  public Element getElement() {
    return element;
  }
}
