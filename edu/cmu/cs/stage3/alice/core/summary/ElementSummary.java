package edu.cmu.cs.stage3.alice.core.summary;
import edu.cmu.cs.stage3.alice.core.Element;

public abstract class ElementSummary { private Element m_element;
  public ElementSummary() {} private int m_elementCount = -1;
  
  protected Element getElement() { return m_element; }
  

  protected void setElement(Element element) { m_element = element; }
  
  public int getElementCount() {
    if (m_element != null) {
      return m_element.getElementCount();
    }
    return m_elementCount;
  }
  
  public void encode(java.io.OutputStream os) {}
  
  public void decode(java.io.InputStream is) {}
}
