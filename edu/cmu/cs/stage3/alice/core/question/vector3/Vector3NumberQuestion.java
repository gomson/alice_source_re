package edu.cmu.cs.stage3.alice.core.question.vector3;

import javax.vecmath.Vector3d;

public abstract class Vector3NumberQuestion extends edu.cmu.cs.stage3.alice.core.question.NumberQuestion { public Vector3NumberQuestion() {}
  public final edu.cmu.cs.stage3.alice.core.property.Vector3Property vector3 = new edu.cmu.cs.stage3.alice.core.property.Vector3Property(this, "vector3", new Vector3d());
  
  protected abstract double getValue(Vector3d paramVector3d);
  
  public Object getValue() { Vector3d value = vector3.getVector3Value();
    if (value != null) {
      return new Double(getValue(value));
    }
    return null;
  }
}
