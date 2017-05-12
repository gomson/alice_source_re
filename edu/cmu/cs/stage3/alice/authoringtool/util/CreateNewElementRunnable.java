package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;




















public class CreateNewElementRunnable
  implements Runnable
{
  private Class elementClass;
  private Element parent;
  
  public CreateNewElementRunnable(Class elementClass, Element parent)
  {
    this.elementClass = elementClass;
    this.parent = parent;
  }
  
  public void run() {
    try {
      Object instance = elementClass.newInstance();
      if ((instance instanceof Element)) {
        String simpleName = elementClass.getName();
        simpleName = simpleName.substring(simpleName.lastIndexOf('.') + 1);
        name.set(AuthoringToolResources.getNameForNewChild(simpleName, parent));
        ((Element)instance).setParent(parent);
      }
    } catch (Throwable t) {
      if (elementClass != null) {
        AuthoringTool.showErrorDialog(Messages.getString("Error_creating_new_instance_of_") + elementClass.getName(), t);
      } else {
        AuthoringTool.showErrorDialog(Messages.getString("Error_creating_new_intance_of_null_"), t);
      }
    }
  }
}
