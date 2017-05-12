package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.IntegerProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;












public class KeyClickBehavior
  extends TriggerBehavior
  implements KeyListener
{
  public KeyClickBehavior() {}
  
  private static Class[] s_supportedCoercionClasses = { KeyIsPressedBehavior.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses; }
  
  public final IntegerProperty keyCode = new IntegerProperty(this, "keyCode", null);
  public final ElementArrayProperty renderTargets = new ElementArrayProperty(this, "renderTargets", null, [Ledu.cmu.cs.stage3.alice.core.RenderTarget.class);
  private RenderTarget[] m_renderTargets = null;
  
  public void manufactureAnyNecessaryDetails() {
    if (details.size() == 1) {
      Variable code = new Variable();
      name.set("code");
      code.setParent(this);
      valueClass.set(Integer.class);
      details.add(code);
    }
  }
  
  public void manufactureDetails() {
    super.manufactureDetails();
    Variable keyChar = new Variable();
    name.set("keyChar");
    keyChar.setParent(this);
    valueClass.set(Character.class);
    details.add(keyChar);
    
    Variable code = new Variable();
    name.set("code");
    code.setParent(this);
    valueClass.set(Integer.class);
    details.add(code);
  }
  
  private void updateDetails(KeyEvent keyEvent) {
    for (int i = 0; i < details.size(); i++) {
      Variable detail = (Variable)details.get(i);
      if (name.getStringValue().equals("keyChar")) {
        value.set(new Character(keyEvent.getKeyChar()));
      } else if (name.getStringValue().equals("code")) {
        value.set(new Integer(keyEvent.getKeyCode()));
      }
    }
  }
  
  private boolean checkKeyCode(KeyEvent keyEvent) {
    int actualValue = keyEvent.getKeyCode();
    int requiredValue = keyCode.intValue(actualValue);
    return actualValue == requiredValue;
  }
  
  public void keyPressed(KeyEvent keyEvent) {}
  
  public void keyReleased(KeyEvent keyEvent) { updateDetails(keyEvent);
    if (checkKeyCode(keyEvent)) {
      trigger(keyEvent.getWhen() * 0.001D);
    }
  }
  
  public void keyTyped(KeyEvent keyEvent) {}
  
  protected void started(World world, double time) {
    super.started(world, time);
    m_renderTargets = ((RenderTarget[])renderTargets.get());
    if (m_renderTargets == null) {
      m_renderTargets = ((RenderTarget[])world.getDescendants(RenderTarget.class));
    }
    for (int i = 0; i < m_renderTargets.length; i++) {
      m_renderTargets[i].addKeyListener(this);
    }
  }
  
  protected void stopped(World world, double time) {
    super.stopped(world, time);
    for (int i = 0; i < m_renderTargets.length; i++) {
      m_renderTargets[i].removeKeyListener(this);
    }
    m_renderTargets = null;
  }
}
