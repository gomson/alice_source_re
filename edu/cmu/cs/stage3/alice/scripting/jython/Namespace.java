package edu.cmu.cs.stage3.alice.scripting.jython;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import java.util.Hashtable;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyStringMap;










public class Namespace
  extends PyStringMap
{
  public Namespace() {}
  
  private Hashtable m_map = new Hashtable();
  private World m_world = null;
  private PyElement m_pyWorld = null;
  
  public void setWorld(World world) {
    m_world = world;
    m_pyWorld = getPyElement(m_world);
    m_map.clear();
    clear();
  }
  
  PyElement getPyElement(Element element) { PyElement pyElement = (PyElement)m_map.get(element);
    if (pyElement == null) {
      if ((element instanceof Sandbox)) {
        pyElement = new PySandbox((Sandbox)element, this);
      } else {
        pyElement = new PyElement(element, this);
      }
      m_map.put(element, pyElement);
    }
    return pyElement;
  }
  
  PyObject java2py(Object o) { if ((o instanceof Element)) {
      return getPyElement((Element)o);
    }
    return Py.java2py(o);
  }
  
  public synchronized PyObject __finditem__(String key)
  {
    PyObject py = super.__finditem__(key);
    if (py != null) {
      return py;
    }
    if (key.equalsIgnoreCase(m_world.name.getStringValue())) {
      return m_pyWorld;
    }
    Expression expression = m_world.lookup(key);
    if (expression != null) {
      return java2py(expression.getValue());
    }
    return m_pyWorld.__findattr__(key);
  }
}
