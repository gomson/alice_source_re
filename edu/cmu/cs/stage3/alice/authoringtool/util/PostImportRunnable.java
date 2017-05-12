package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Element;




















public abstract class PostImportRunnable
  implements Runnable
{
  private Element m_importedElement;
  
  public PostImportRunnable() {}
  
  public Element getImportedElement()
  {
    return m_importedElement;
  }
  
  public void setImportedElement(Element importedElement) { m_importedElement = importedElement; }
}
