package edu.cmu.cs.stage3.alice.gallery.batch;

import edu.cmu.cs.stage3.alice.core.World;

public class BatchLoad extends Batch { public BatchLoad() {}
  protected void initialize(World world) { edu.cmu.cs.stage3.alice.core.Camera camera = new edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera();
    name.set("Camera");
    world.addChild(camera);
    edu.cmu.cs.stage3.alice.core.Model model = new edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera();
    name.set("Ground");
    world.addChild(model);
  }
}
