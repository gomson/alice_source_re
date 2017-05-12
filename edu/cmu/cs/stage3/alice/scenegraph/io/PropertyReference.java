package edu.cmu.cs.stage3.alice.scenegraph.io;

import edu.cmu.cs.stage3.alice.scenegraph.Property;

class PropertyReference { public PropertyReference(Property property, edu.cmu.cs.stage3.alice.scenegraph.Element element, String key) { m_property = property;
    m_element = element;
    m_key = key;
  }
  
  public void resolve(java.util.Dictionary map) { Object value = map.get(m_key);
    if (value != null) {
      m_property.set(m_element, value);
    } else {
      throw new RuntimeException("could resolve reference- property: " + m_property + "; element: " + m_element + "; key: " + m_key);
    }
  }
  
  private Property m_property;
  private edu.cmu.cs.stage3.alice.scenegraph.Element m_element;
  private String m_mixedCasePropertyName;
  private String m_key;
}
