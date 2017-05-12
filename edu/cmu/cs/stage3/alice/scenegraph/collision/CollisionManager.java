package edu.cmu.cs.stage3.alice.scenegraph.collision;

import edu.cmu.cs.stage3.alice.scenegraph.Visual;

public class CollisionManager implements edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener {
  public CollisionManager() {}
  
  public void absoluteTransformationChanged(edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent absoluteTransformationEvent) {}
  
  public void activateObject(Visual a) {}
  
  public void deactivateObject(Visual a) {}
  
  public void activatePair(Visual a, Visual b) {}
  
  public void deactivatePair(Visual a, Visual b) {}
  
  public void deleteObject(Visual a) {}
  
  public Visual[][] update(int space) {
    return null;
  }
}
