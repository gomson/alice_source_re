package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.JAlice;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;





public class VariablesViewController
  extends GroupingPanel
  implements GUIElement, Releasable
{
  protected ObjectArrayProperty variables;
  protected RefreshListener refreshListener = new RefreshListener();
  
  protected boolean sleeping = false;
  protected Configuration authoringToolConfig = Configuration.getLocalConfiguration(JAlice.class.getPackage());
  
  public VariablesViewController() {
    setOpaque(false);
    setLayout(new BoxLayout(this, 0));
    setBorder(null);
  }
  
  public void set(ObjectArrayProperty variables) {
    clean();
    this.variables = variables;
    if (!sleeping) {
      startListening();
    }
    refreshGUI();
  }
  
  public void goToSleep() {
    if (!sleeping) {
      stopListening();
      sleeping = true;
    }
  }
  
  public void wakeUp() {
    if (sleeping) {
      startListening();
      sleeping = false;
    }
  }
  
  public void die() {
    clean();
  }
  
  public void clean() {
    stopListening();
    removeAll();
    variables = null;
  }
  
  public void release() {
    GUIFactory.releaseGUI(this);
  }
  
  public void refreshGUI() {
    removeAll();
    Object[] vars = variables.getArrayValue();
    for (int i = 0; i < vars.length; i++) {
      Variable variable = (Variable)vars[i];
      if (variable != null) {
        JComponent gui = null;
        if (gui == null) {
          SetPropertyImmediatelyFactory setImmediatelyFactory = new SetPropertyImmediatelyFactory(value);
          gui = GUIFactory.getPropertyViewController(value, true, true, false, setImmediatelyFactory);
        }
        add(gui);
        if (("true".equalsIgnoreCase((String)AuthoringToolResources.getMiscItem("javaLikeSyntax"))) && 
          (i < vars.length - 1)) {
          add(new JLabel(", "));
        }
      }
    }
  }
  
  protected void startListening()
  {
    if (variables != null)
    {















































      variables.addObjectArrayPropertyListener(refreshListener);
      Object[] vars = variables.getArrayValue();
      for (int i = 0; i < vars.length; i++) {
        if (vars[i] != null) {
          name.addPropertyListener(refreshListener);
        }
      }
    }
  }
  
  protected void stopListening() {
    if (variables != null)
    {















































      variables.removeObjectArrayPropertyListener(refreshListener);
      Object[] vars = variables.getArrayValue();
      for (int i = 0; i < vars.length; i++)
        if (vars[i] != null)
          name.removePropertyListener(refreshListener);
    }
  }
  
  class RefreshListener implements ObjectArrayPropertyListener, PropertyListener {
    RefreshListener() {}
    
    public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
    
    public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) { if (ev.getChangeType() == 1) {
        Variable variable = (Variable)ev.getItem();
        if (variable != null) {
          name.addPropertyListener(this);
        }
      } else if (ev.getChangeType() == 3) {
        Variable variable = (Variable)ev.getItem();
        if (variable != null) {
          name.removePropertyListener(this);
        }
      }
      refreshGUI(); }
    
    public void propertyChanging(PropertyEvent ev) {}
    
    public void propertyChanged(PropertyEvent ev) { refreshGUI(); }
  }
}
