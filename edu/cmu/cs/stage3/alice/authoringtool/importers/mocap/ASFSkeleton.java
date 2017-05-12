package edu.cmu.cs.stage3.alice.authoringtool.importers.mocap;

import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Pose;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.response.DoTogether;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import java.util.Hashtable;
import java.util.Vector;






























public class ASFSkeleton
{
  public Vector bones;
  public Hashtable bones_dict;
  public double lengthscale;
  public double anglescale;
  
  public ASFSkeleton()
  {
    bones = new Vector();
    bones_dict = new Hashtable();
  }
  
  public ASFBone getRoot() {
    return (ASFBone)bones_dict.get("root");
  }
  
  public Model buildBones() {
    return getRoot().buildBone(null);
  }
  
  public void setBasePose(Model mod) {
    getRoot().setBasePose(mod);
  }
  
  public void addFrames() {
    getRoot().addFrames();
  }
  
  public UserDefinedResponse buildAnim() {
    UserDefinedResponse anim = new UserDefinedResponse();
    DoTogether partAnims = new DoTogether();
    anim.addChild(partAnims);
    componentResponses.add(partAnims);
    
    getRoot().buildAnim(partAnims);
    return anim;
  }
  
  public Pose[] buildPoses() {
    Pose[] poseList = new Pose[2];
    poseList[0] = new Pose();
    poseList[1] = new Pose();
    
    0poseMap.set(new Hashtable());
    1poseMap.set(new Hashtable());
    
    getRoot().buildPoses(getRootrealMod, poseList[0], poseList[1]);
    return poseList;
  }
}
