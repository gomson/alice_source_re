package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.manipulator.RenderTargetModelManipulator;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ListProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import java.util.Enumeration;
import java.util.Vector;














public class DefaultMouseInteractionBehavior
  extends InternalResponseBehavior
{
  public final ElementArrayProperty renderTargets = new ElementArrayProperty(this, "renderTargets", null, [Ledu.cmu.cs.stage3.alice.core.RenderTarget.class);
  public final ListProperty objects = new ListProperty(this, "objects", null);
  
  protected Vector manipulators = new Vector();
  
  public DefaultMouseInteractionBehavior() {}
  
  protected void scheduled(double t) {}
  
  protected void objectsValueChanged(List value) { Enumeration enum0 = manipulators.elements();
    while (enum0.hasMoreElements()) {
      RenderTargetModelManipulator rtmm = (RenderTargetModelManipulator)enum0.nextElement();
      rtmm.clearObjectsOfInterestList();
      if (value != null) {
        for (int i = 0; i < values.getArrayValue().length; i++)
          rtmm.addObjectOfInterest((Transformable)values.getArrayValue()[i]);
      }
    }
  }
  
  private void setIsEnabled(boolean value) {
    if (manipulators != null) {
      Enumeration enum0 = manipulators.elements();
      while (enum0.hasMoreElements()) {
        ((RenderTargetModelManipulator)enum0.nextElement()).setEnabled(isEnabled.booleanValue());
      }
    }
  }
  
  protected void enabled() {
    setIsEnabled(true);
  }
  
  protected void disabled() {
    setIsEnabled(false);
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == objects) {
      objectsValueChanged(objects.getListValue());
    } else {
      super.propertyChanged(property, value);
    }
  }
  
  protected void started(World world, double time) {
    super.started(world, time);
    manipulators.clear();
    RenderTarget[] renderTargetsValue = (RenderTarget[])renderTargets.get();
    if (renderTargetsValue == null) {
      renderTargetsValue = (RenderTarget[])world.getDescendants(RenderTarget.class);
    }
    for (int i = 0; i < renderTargetsValue.length; i++) {
      RenderTargetModelManipulator rtmm = new RenderTargetModelManipulator(renderTargetsValue[i].getInternal());
      
      rtmm.setPickAllForOneObjectOfInterestEnabled(false);
      manipulators.addElement(rtmm);
    }
    objectsValueChanged(objects.getListValue());
    setIsEnabled(isEnabled.booleanValue());
  }
  
  protected void stopped(World world, double time) {
    super.stopped(world, time);
    Enumeration enum0 = manipulators.elements();
    while (enum0.hasMoreElements()) {
      ((RenderTargetModelManipulator)enum0.nextElement()).setRenderTarget(null);
    }
    manipulators.clear();
  }
}
