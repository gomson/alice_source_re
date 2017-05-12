package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.util.Enumerable;











public class Limb
  extends Enumerable
{
  public static final Limb rightArm = new Limb("rightUpperArm");
  public static final Limb leftArm = new Limb("leftUpperArm");
  public static final Limb rightLeg = new Limb("rightUpperLeg");
  public static final Limb leftLeg = new Limb("leftUpperLeg");
  
  protected String limbName = "rightUpperArm";
  
  public Limb(String limbName) {
    this.limbName = limbName;
  }
  
  public static Limb valueOf(String s) {
    return (Limb)Enumerable.valueOf(s, Limb.class);
  }
}
