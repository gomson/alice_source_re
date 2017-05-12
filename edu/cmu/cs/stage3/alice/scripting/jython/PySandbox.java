package edu.cmu.cs.stage3.alice.scripting.jython;

import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import org.python.core.PyObject;














public class PySandbox
  extends PyElement
{
  public PySandbox(Sandbox sandbox, Namespace namespace)
  {
    super(sandbox, namespace);
  }
  
  private Sandbox getSandbox() { return (Sandbox)getElement(); }
  


  public void __setattr__(String name, PyObject attr)
  {
    for (int i = 0; i < getSandboxvariables.size(); i++) {
      Variable variable = (Variable)getSandboxvariables.get(i);
      if (name.equalsIgnoreCase(name.getStringValue()))
      {
        value.set(attr.__tojava__(variable.getValueClass()));
        return;
      }
    }
    super.__setattr__(name, attr);
  }
}
