package edu.cmu.cs.stage3.alice.authoringtool.event;

import edu.cmu.cs.stage3.alice.core.Element;























public class ElementScopeChangedEvent
{
  protected Element oldElementScope;
  protected Element newElementScope;
  
  public ElementScopeChangedEvent(Element oldElementScope, Element newElementScope)
  {
    this.oldElementScope = oldElementScope;
    this.newElementScope = newElementScope;
  }
  
  public Element getOldElementScope() {
    return oldElementScope;
  }
  
  public Element getNewElementScope() {
    return newElementScope;
  }
}
