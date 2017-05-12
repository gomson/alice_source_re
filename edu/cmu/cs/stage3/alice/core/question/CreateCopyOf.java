package edu.cmu.cs.stage3.alice.core.question;
import edu.cmu.cs.stage3.alice.core.Model;

public class CreateCopyOf extends SubjectQuestion { private java.util.Vector m_copies = new java.util.Vector();
  
  public CreateCopyOf() {}
  public Class getValueClass() { return Model.class; }
  
  protected Object getValue(edu.cmu.cs.stage3.alice.core.Transformable subjectValue)
  {
    Class[] classesToShare = { edu.cmu.cs.stage3.alice.core.TextureMap.class, edu.cmu.cs.stage3.alice.core.Geometry.class };
    Model original = (Model)subject.getTransformableValue();
    Model copy = (Model)original.HACK_createCopy(null, original.getParent(), -1, classesToShare, null);
    m_copies.addElement(copy);
    return copy;
  }
  
  protected void started(edu.cmu.cs.stage3.alice.core.World world, double time) {
    super.started(world, time);
    m_copies.clear();
  }
  
  protected void stopped(edu.cmu.cs.stage3.alice.core.World world, double time) {
    super.stopped(world, time);
    for (int i = 0; i < m_copies.size(); i++) {
      Model copy = (Model)m_copies.elementAt(i);
      vehicle.set(null);
      
      copy.removeFromParent();
    }
    

    m_copies.clear();
  }
}
