package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Scheduler;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;
import edu.cmu.cs.stage3.alice.core.response.SizeAnimation;
import edu.cmu.cs.stage3.util.HowMuch;
import java.util.ArrayList;
import javax.vecmath.Vector3d;













public class SizeUndoableRedoable
  extends OneShotUndoableRedoable
{
  public SizeUndoableRedoable(Transformable transformable, Vector3d oldSize, Vector3d newSize, Scheduler scheduler)
  {
    super(new SizeAnimation(), new SizeAnimation(), new OneShotSimpleBehavior(), scheduler);
    
    SizeAnimation redoResponse = (SizeAnimation)getRedoResponse();
    SizeAnimation undoResponse = (SizeAnimation)getUndoResponse();
    OneShotSimpleBehavior oneShotBehavior = getOneShotBehavior();
    
    subject.set(transformable);
    size.set(newSize);
    subject.set(transformable);
    size.set(oldSize);
    
    ArrayList affectedProperties = new ArrayList();
    Transformable[] transformables = (Transformable[])transformable.getDescendants(Transformable.class, HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
    for (int i = 0; i < transformables.length; i++) {
      affectedProperties.add(localTransformation);
      if ((transformables[i] instanceof Model)) {
        affectedProperties.add(visualScale);
      }
    }
    
    oneShotBehavior.setAffectedProperties((Property[])affectedProperties.toArray(new Property[0]));
  }
}
