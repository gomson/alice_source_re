package edu.cmu.cs.stage3.alice.core.response.vector3;
import javax.vecmath.Vector3d;

public class SetX extends Vector3Response { public class RuntimeSetX extends Vector3Response.RuntimeVector3Response { public RuntimeSetX() { super(); }
    
    protected void set(Vector3d vector3, double v) {
      x = v;
    }
  }
  
  public SetX() {}
}
