package edu.cmu.cs.stage3.alice.core.summary;
import edu.cmu.cs.stage3.alice.core.World;

public class WorldSummary extends ElementSummary { public WorldSummary() {}
  private World getWorld() { return (World)getElement(); }
  
  public void setWorld(World world) {
    super.setElement(world);
  }
}
