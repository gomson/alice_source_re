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












public class KeyIsPressedBehavior
  extends AbstractConditionalBehavior
  implements KeyListener
{
  public KeyIsPressedBehavior() {}
  
  private static Class[] s_supportedCoercionClasses = { KeyClickBehavior.class };
  private int m_keyCode = -1;
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses; }
  
  public final IntegerProperty keyCode = new IntegerProperty(this, "keyCode", null);
  public final ElementArrayProperty renderTargets = new ElementArrayProperty(this, "renderTargets", null, [Ledu.cmu.cs.stage3.alice.core.RenderTarget.class);
  
  private RenderTarget[] m_renderTargets = null;
  
  public void manufactureDetails() {
    super.manufactureDetails();
    Variable keyChar = new Variable();
    name.set("keyChar");
    keyChar.setParent(this);
    valueClass.set(Character.class);
    details.add(keyChar);
  }
  
  private void updateDetails(KeyEvent keyEvent) { for (int i = 0; i < details.size(); i++) {
      Variable detail = (Variable)details.get(i);
      if (name.getStringValue().equals("keyChar"))
        value.set(new Character(keyEvent.getKeyChar()));
    }
  }
  
  private boolean checkKeyCode(int actualValue) {
    int requiredValue = keyCode.intValue(actualValue);
    return actualValue == requiredValue;
  }
  
  public void keyPressed(KeyEvent keyEvent) { if (m_keyCode == -1) {
      int keyCode = keyEvent.getKeyCode();
      if (checkKeyCode(keyCode)) {
        m_keyCode = keyCode;
        updateDetails(keyEvent);
        set(true);
      }
    }
  }
  
  public void keyReleased(KeyEvent keyEvent) { if (m_keyCode != -1) {
      int keyCode = keyEvent.getKeyCode();
      if (keyCode == m_keyCode) {
        updateDetails(keyEvent);
        set(false);
        m_keyCode = -1;
      }
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
    listeningToKeypress = true;
    m_keyCode = -1;
  }
  
  protected void stopped(World world, double time) {
    super.stopped(world, time);
    for (int i = 0; i < m_renderTargets.length; i++) {
      m_renderTargets[i].removeKeyListener(this);
    }
    m_renderTargets = null;
    listeningToKeypress = false;
  }
}
