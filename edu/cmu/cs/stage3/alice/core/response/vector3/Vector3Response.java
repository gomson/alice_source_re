package edu.cmu.cs.stage3.alice.core.response.vector3;

import javax.vecmath.Vector3d;

public class Vector3Response extends edu.cmu.cs.stage3.alice.core.Response { public Vector3Response() {}
  
  public final edu.cmu.cs.stage3.alice.core.property.Vector3Property vector3 = new edu.cmu.cs.stage3.alice.core.property.Vector3Property(this, "vector3", new Vector3d());
  public final edu.cmu.cs.stage3.alice.core.property.NumberProperty value = new edu.cmu.cs.stage3.alice.core.property.NumberProperty(this, "value", new Double(0.0D));
  

  protected Number getDefaultDuration() { return new Double(0.0D); }
  
  public abstract class RuntimeVector3Response extends edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse { public RuntimeVector3Response() { super(); }
    
    private Vector3d m_vector3;
    private double m_value;
    protected abstract void set(Vector3d paramVector3d, double paramDouble);
    
    public void prologue(double t) { super.prologue(t);
      m_vector3 = vector3.getVector3Value();
      m_value = value.doubleValue();
    }
    
    public void epilogue(double t) {
      super.prologue(t);
      set(m_vector3, m_value);
    }
  }
}
