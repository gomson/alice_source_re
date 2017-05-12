package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.IntegerProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.alice.core.question.PickQuestion;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;










public class MouseButtonClickBehavior
  extends TriggerBehavior
  implements MouseListener
{
  public MouseButtonClickBehavior() {}
  
  private static Class[] s_supportedCoercionClasses = { MouseButtonIsPressedBehavior.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses; }
  
  public final IntegerProperty requiredModifierMask = new IntegerProperty(this, "requiredModifierMask", new Integer(0));
  public final IntegerProperty excludedModifierMask = new IntegerProperty(this, "excludedModifierMask", new Integer(0));
  public final ElementArrayProperty renderTargets = new ElementArrayProperty(this, "renderTargets", null, [Ledu.cmu.cs.stage3.alice.core.RenderTarget.class);
  

  public final TransformableProperty onWhat = new TransformableProperty(this, "onWhat", null);
  
  private RenderTarget[] m_renderTargets = null;
  private MouseEvent m_pressedEvent = null;
  public long m_clickTimeThreshold = 300L;
  public int m_clickDistanceThresholdSquared = 100;
  
  public void manufactureAnyNecessaryDetails() {
    if (details.size() == 2) {
      Question what = new PickQuestion();
      name.set("what");
      what.setParent(this);
      details.add(what);
    }
    for (int i = 0; i < details.size(); i++) {
      Object o = details.get(i);
      if ((o instanceof PickQuestion)) {
        name.set("what");
      }
    }
  }
  
  public void manufactureDetails() {
    super.manufactureDetails();
    
    Variable x = new Variable();
    name.set("x");
    x.setParent(this);
    valueClass.set(Number.class);
    details.add(x);
    
    Variable y = new Variable();
    name.set("y");
    y.setParent(this);
    valueClass.set(Number.class);
    details.add(y);
    
    manufactureAnyNecessaryDetails();
  }
  
  private void updateDetails(MouseEvent mouseEvent) { for (int i = 0; i < details.size(); i++) {
      Expression detail = (Expression)details.get(i);
      if (name.getStringValue().equals("x")) {
        value.set(new Double(mouseEvent.getX()));
      } else if (name.getStringValue().equals("y")) {
        value.set(new Double(mouseEvent.getY()));
      } else if (name.getStringValue().equals("what"))
        ((PickQuestion)detail).setMouseEvent(mouseEvent);
    }
  }
  
  private boolean checkModifierMask(InputEvent e) {
    int modifiers = e.getModifiers();
    Integer requiredModifierMaskValue = (Integer)requiredModifierMask.getValue();
    Integer excludedModifierMaskValue = (Integer)excludedModifierMask.getValue();
    int required = 0;
    if (requiredModifierMaskValue != null) {
      required = requiredModifierMaskValue.intValue();
    }
    int excluded = 0;
    if (excludedModifierMaskValue != null) {
      excluded = excludedModifierMaskValue.intValue();
    }
    return ((modifiers & required) == required) && ((modifiers & excluded) == 0);
  }
  
  public void mouseClicked(MouseEvent mouseEvent) {}
  
  public void mouseEntered(MouseEvent mouseEvent) {}
  
  public void mouseExited(MouseEvent mouseEvent) {}
  
  public void mousePressed(MouseEvent mouseEvent) {
    m_pressedEvent = mouseEvent;
  }
  
  public void mouseReleased(MouseEvent mouseEvent) {
    int dx = mouseEvent.getX() - m_pressedEvent.getX();
    int dy = mouseEvent.getY() - m_pressedEvent.getY();
    long dt = mouseEvent.getWhen() - m_pressedEvent.getWhen();
    if ((dt < m_clickTimeThreshold) && 
      (dx * dx + dy * dy < m_clickDistanceThresholdSquared) && 
      (isEnabled.booleanValue()) && 
      (checkModifierMask(mouseEvent))) {
      updateDetails(mouseEvent);
      Transformable onWhatValue = onWhat.getTransformableValue();
      boolean success;
      boolean success; if (onWhatValue != null) {
        PickInfo pickInfo = RenderTarget.pick(mouseEvent);
        boolean success; if ((pickInfo != null) && (pickInfo.getCount() > 0)) {
          Model model = (Model)pickInfo.getVisualAt(0).getBonus();
          success = (onWhatValue == model) || (onWhatValue.isAncestorOf(model));
        } else {
          success = false;
        }
      } else {
        success = true;
      }
      if (success) {
        updateDetails(mouseEvent);
        trigger(mouseEvent.getWhen() * 0.001D);
      }
    }
  }
  


  protected void started(World world, double time)
  {
    super.started(world, time);
    m_renderTargets = ((RenderTarget[])renderTargets.get());
    if (m_renderTargets == null) {
      m_renderTargets = ((RenderTarget[])world.getDescendants(RenderTarget.class));
    }
    for (int i = 0; i < m_renderTargets.length; i++) {
      m_renderTargets[i].addMouseListener(this);
    }
  }
  
  protected void stopped(World world, double time) {
    super.stopped(world, time);
    for (int i = 0; i < m_renderTargets.length; i++) {
      m_renderTargets[i].removeMouseListener(this);
    }
    m_renderTargets = null;
  }
}
