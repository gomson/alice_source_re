package edu.cmu.cs.stage3.alice.core.response.vector3;
import javax.vecmath.Vector3d;

public class SetZ extends Vector3Response { public class RuntimeSetZ extends Vector3Response.RuntimeVector3Response { public RuntimeSetZ() { super(); }
    
    protected void set(Vector3d vector3, double v) {
      z = v;
    }
  }
  
  public SetZ() {}
}
