package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Element;




















public class ScopedElementTreeModel
  extends FilteringElementTreeModel
{
  protected Element currentScope;
  
  public ScopedElementTreeModel() {}
  
  public Element getCurrentScope()
  {
    return currentScope;
  }
  
  public void setCurrentScope(Element scope) {
    currentScope = scope;
  }
  
  public boolean isElementInScope(Element element) {
    if (currentScope != null) {
      if (element == currentScope)
        return true;
      if (element.isDescendantOf(currentScope)) {
        return true;
      }
    }
    
    return false;
  }
}
