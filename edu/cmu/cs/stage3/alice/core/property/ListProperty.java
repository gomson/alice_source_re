package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.List;





















public class ListProperty
  extends CollectionProperty
{
  public ListProperty(Element owner, String name, List defaultValue)
  {
    super(owner, name, defaultValue, List.class);
  }
  
  public List getListValue() { return (List)getCollectionValue(); }
}
