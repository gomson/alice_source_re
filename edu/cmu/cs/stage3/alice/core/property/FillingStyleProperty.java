package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.scenegraph.FillingStyle;





















public class FillingStyleProperty
  extends EnumerableProperty
{
  public FillingStyleProperty(Element owner, String name, FillingStyle defaultValue)
  {
    super(owner, name, defaultValue, FillingStyle.class);
  }
  
  public FillingStyle getFillingStyleValue() { return (FillingStyle)getEnumerableValue(); }
}
