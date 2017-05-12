package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Scheduler;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.Matrix44Property;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation;
import edu.cmu.cs.stage3.math.Matrix44;
















public class PointOfViewUndoableRedoable
  extends OneShotUndoableRedoable
{
  public PointOfViewUndoableRedoable(Transformable transformable, Matrix44 oldTransformation, Matrix44 newTransformation, Scheduler scheduler)
  {
    super(new PointOfViewAnimation(), new PointOfViewAnimation(), new OneShotSimpleBehavior(), scheduler);
    
    PointOfViewAnimation redoResponse = (PointOfViewAnimation)getRedoResponse();
    PointOfViewAnimation undoResponse = (PointOfViewAnimation)getUndoResponse();
    OneShotSimpleBehavior oneShotBehavior = getOneShotBehavior();
    
    subject.set(transformable);
    pointOfView.set(newTransformation);
    subject.set(transformable);
    pointOfView.set(oldTransformation);
    
    oneShotBehavior.setAffectedProperties(new Property[] { localTransformation });
  }
}
