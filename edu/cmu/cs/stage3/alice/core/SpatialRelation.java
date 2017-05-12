package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.util.Enumerable;
import javax.vecmath.Vector3d;




















public class SpatialRelation
  extends Enumerable
{
  private Vector3d m_placeAxis;
  
  private SpatialRelation(Vector3d placeAxis)
  {
    m_placeAxis = placeAxis;
  }
  
  public static final SpatialRelation LEFT_OF = new SpatialRelation(new Vector3d(-1.0D, 0.0D, 0.0D));
  public static final SpatialRelation RIGHT_OF = new SpatialRelation(new Vector3d(1.0D, 0.0D, 0.0D));
  public static final SpatialRelation ABOVE = new SpatialRelation(new Vector3d(0.0D, 1.0D, 0.0D));
  public static final SpatialRelation BELOW = new SpatialRelation(new Vector3d(0.0D, -1.0D, 0.0D));
  public static final SpatialRelation IN_FRONT_OF = new SpatialRelation(new Vector3d(0.0D, 0.0D, 1.0D));
  public static final SpatialRelation BEHIND = new SpatialRelation(new Vector3d(0.0D, 0.0D, -1.0D));
  
  public static final SpatialRelation FRONT_RIGHT_OF = new SpatialRelation(new Vector3d(0.7071068D, 0.0D, 0.7071068D));
  public static final SpatialRelation FRONT_LEFT_OF = new SpatialRelation(new Vector3d(-0.7071068D, 0.0D, 0.7071068D));
  public static final SpatialRelation BEHIND_RIGHT_OF = new SpatialRelation(new Vector3d(0.7071068D, 0.0D, -0.7071068D));
  public static final SpatialRelation BEHIND_LEFT_OF = new SpatialRelation(new Vector3d(-0.7071068D, 0.0D, -0.7071068D));
  
  public Vector3d getPlaceVector(double amount, Box subjectBoundingBox, Box objectBoundingBox) {
    double x = amount * m_placeAxis.x;
    double y = amount * m_placeAxis.y;
    double z = amount * m_placeAxis.z;
    
    if (m_placeAxis.x > 0.0D) {
      x += m_placeAxis.x * (getMaximumx - getMinimumx);
    } else if (m_placeAxis.x < 0.0D) {
      x += m_placeAxis.x * (getMaximumx - getMinimumx);
    }
    if (m_placeAxis.y > 0.0D) {
      y += m_placeAxis.y * (getMaximumy - getMinimumy);
    } else if (m_placeAxis.y < 0.0D) {
      y += m_placeAxis.y * (getMaximumy - getMinimumy);
    }
    if (m_placeAxis.z > 0.0D) {
      z += m_placeAxis.z * (getMaximumz - getMinimumz);
    } else if (m_placeAxis.z < 0.0D) {
      z += m_placeAxis.z * (getMaximumz - getMinimumz);
    }
    return new Vector3d(x, y, z);
  }
  
  public boolean equals(Object o)
  {
    if (o == this) return true;
    if ((o != null) && ((o instanceof SpatialRelation))) {
      SpatialRelation spatialRelation = (SpatialRelation)o;
      if (m_placeAxis == null) {
        return m_placeAxis == null;
      }
      return m_placeAxis.equals(m_placeAxis);
    }
    
    return false;
  }
  
  public static SpatialRelation valueOf(String s) {
    return (SpatialRelation)Enumerable.valueOf(s, SpatialRelation.class);
  }
}
