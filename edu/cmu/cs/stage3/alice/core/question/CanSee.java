package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;

public class CanSee extends BooleanQuestion {
  public CanSee() {}
  
  public final edu.cmu.cs.stage3.alice.core.property.CameraProperty camera = new edu.cmu.cs.stage3.alice.core.property.CameraProperty(this, "camera", null);
  public final edu.cmu.cs.stage3.alice.core.property.ModelProperty object = new edu.cmu.cs.stage3.alice.core.property.ModelProperty(this, "object", null);
  public final BooleanProperty checkForOcclusion = new BooleanProperty(this, "checkForOcclusion", Boolean.FALSE);
  
  public Object getValue() {
    edu.cmu.cs.stage3.alice.core.Camera cameraValue = camera.getCameraValue();
    edu.cmu.cs.stage3.alice.core.Model objectValue = object.getModelValue();
    if (cameraValue.canSee(objectValue, checkForOcclusion.booleanValue())) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
