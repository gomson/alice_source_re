package edu.cmu.cs.stage3.alice.scenegraph.renderer;

import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Geometry;
import edu.cmu.cs.stage3.alice.scenegraph.Visual;
import javax.vecmath.Vector3d;

public abstract interface PickInfo
{
  public abstract Component getSource();
  
  public abstract Visual[] getVisuals();
  
  public abstract Geometry[] getGeometries();
  
  public abstract boolean[] isFrontFacings();
  
  public abstract int[] getSubElements();
  
  public abstract double[] getZs();
  
  public abstract int getCount();
  
  public abstract Visual getVisualAt(int paramInt);
  
  public abstract boolean isFrontFacingAt(int paramInt);
  
  public abstract Geometry getGeometryAt(int paramInt);
  
  public abstract int getSubElementAt(int paramInt);
  
  public abstract double getZAt(int paramInt);
  
  public abstract Vector3d getLocalPositionAt(int paramInt);
}
