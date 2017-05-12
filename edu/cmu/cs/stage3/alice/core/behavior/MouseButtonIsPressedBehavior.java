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
import java.awt.event.MouseMotionListener;








public class MouseButtonIsPressedBehavior
  extends AbstractConditionalBehavior
  implements MouseListener, MouseMotionListener
{
  public MouseButtonIsPressedBehavior() {}
  
  private static Class[] s_supportedCoercionClasses = { MouseButtonClickBehavior.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses; }
  
  public final IntegerProperty requiredModifierMask = new IntegerProperty(this, "requiredModifierMask", new Integer(0));
  public final IntegerProperty excludedModifierMask = new IntegerProperty(this, "excludedModifierMask", new Integer(0));
  public final ElementArrayProperty renderTargets = new ElementArrayProperty(this, "renderTargets", null, [Ledu.cmu.cs.stage3.alice.core.RenderTarget.class);
  

  public final TransformableProperty onWhat = new TransformableProperty(this, "onWhat", null);
  
  private RenderTarget[] m_renderTargets = null;
  
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
  
  public void mousePressed(MouseEvent mouseEvent) { updateDetails(mouseEvent);
    if ((isEnabled.booleanValue()) && 
      (checkModifierMask(mouseEvent))) {
      Transformable onWhatValue = onWhat.getTransformableValue();
      boolean success;
      boolean success; if (onWhatValue != null) {
        PickInfo pickInfo = RenderTarget.pick(mouseEvent);
        boolean success; if (pickInfo.getCount() > 0) {
          Model model = (Model)pickInfo.getVisualAt(0).getBonus();
          success = (onWhatValue == model) || (onWhatValue.isAncestorOf(model));
        } else {
          success = false;
        }
      } else {
        success = true;
      }
      if (success) {
        set(true);
      }
    }
  }
  
  public void mouseReleased(MouseEvent mouseEvent) {
    updateDetails(mouseEvent);
    set(false);
  }
  
  public void mouseDragged(MouseEvent mouseEvent) { updateDetails(mouseEvent); }
  

  public void mouseMoved(MouseEvent mouseEvent) {}
  

  protected void started(World world, double time)
  {
    super.started(world, time);
    m_renderTargets = ((RenderTarget[])renderTargets.get());
    if (m_renderTargets == null) {
      m_renderTargets = ((RenderTarget[])world.getDescendants(RenderTarget.class));
    }
    for (int i = 0; i < m_renderTargets.length; i++) {
      m_renderTargets[i].addMouseListener(this);
      m_renderTargets[i].addMouseMotionListener(this);
    }
  }
  
  protected void stopped(World world, double time) {
    super.stopped(world, time);
    for (int i = 0; i < m_renderTargets.length; i++) {
      m_renderTargets[i].removeMouseListener(this);
      m_renderTargets[i].removeMouseMotionListener(this);
    }
    m_renderTargets = null;
  }
}
