package edu.cmu.cs.stage3.alice.core.response.vector3;
import javax.vecmath.Vector3d;

public class SetY extends Vector3Response { public class RuntimeSetY extends Vector3Response.RuntimeVector3Response { public RuntimeSetY() { super(); }
    
    protected void set(Vector3d vector3, double v) {
      y = v;
    }
  }
  
  public SetY() {}
}
