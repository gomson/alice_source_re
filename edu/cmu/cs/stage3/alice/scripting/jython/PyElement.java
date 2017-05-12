package edu.cmu.cs.stage3.alice.scripting.jython;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Property;
import org.python.core.Py;
import org.python.core.PyJavaInstance;
import org.python.core.PyObject;














public class PyElement
  extends PyJavaInstance
{
  private Element m_element;
  private Namespace m_namespace;
  
  public PyElement(Element element, Namespace namespace)
  {
    super(element);
    m_element = element;
    m_namespace = namespace;
  }
  
  protected Element getElement() {
    return m_element;
  }
  
  protected Namespace getNamespace() { return m_namespace; }
  







  public PyObject __findattr__(String name)
  {
    Element descendant = m_element.getChildNamedIgnoreCase(name);
    if (descendant == null) {
      Property property = m_element.getPropertyNamedIgnoreCase(name);
      if (property == null) {
        if (name.startsWith("_")) {
          descendant = m_element.getChildNamed(name.substring(1));
          if (descendant != null) {
            return m_namespace.getPyElement(descendant);
          }
          property = m_element.getPropertyNamedIgnoreCase(name.substring(1));
          if (property != null) {
            return Py.java2py(property);
          }
        }
        
        return super.__findattr__(name);
      }
      return m_namespace.java2py(property.get());
    }
    Object value;
    Object value;
    if ((descendant instanceof Expression)) {
      value = ((Expression)descendant).getValue();
    } else {
      value = descendant;
    }
    return m_namespace.java2py(value);
  }
  



  public void __setattr__(String name, PyObject attr)
  {
    Property property = m_element.getPropertyNamedIgnoreCase(name);
    if (property != null)
    {
      property.set(attr.__tojava__(property.getValueClass()));
    } else {
      super.__setattr__(name, attr);
    }
  }
}
