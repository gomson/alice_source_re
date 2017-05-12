package edu.cmu.cs.stage3.alice.core.behavior;

import java.util.Vector;

public class IsCollidingBehavior extends AbstractConditionalBehavior
{
  public IsCollidingBehavior() {}
  
  private static Class[] s_supportedCoercionClasses = { CollisionBehavior.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses; }
  

  public final edu.cmu.cs.stage3.alice.core.property.CollectionProperty a = new edu.cmu.cs.stage3.alice.core.property.CollectionProperty(this, "a", null);
  public final edu.cmu.cs.stage3.alice.core.property.CollectionProperty b = new edu.cmu.cs.stage3.alice.core.property.CollectionProperty(this, "b", null);
  
  private edu.cmu.cs.stage3.alice.core.World m_world = null;
  private Vector m_a = new Vector();
  private Vector m_b = new Vector();
  
  protected void started(edu.cmu.cs.stage3.alice.core.World world, double time) {
    super.started(world, time);
    m_world = world;
    
    m_a.clear();
    edu.cmu.cs.stage3.alice.core.Collection aCollection = a.getCollectionValue();
    for (int i = 0; i < values.size(); i++) {
      m_a.addElement(values.get(i));
    }
    
    m_b.clear();
    edu.cmu.cs.stage3.alice.core.Collection bCollection = b.getCollectionValue();
    for (int i = 0; i < values.size(); i++) {
      m_b.addElement(values.get(i));
    }
    
    for (int i = 0; i < m_a.size(); i++) {
      m_world.addCollisionManagementFor((edu.cmu.cs.stage3.alice.core.Transformable)m_a.elementAt(i));
    }
    for (int i = 0; i < m_b.size(); i++)
      m_world.addCollisionManagementFor((edu.cmu.cs.stage3.alice.core.Transformable)m_b.elementAt(i));
  }
  
  protected void stopped(edu.cmu.cs.stage3.alice.core.World world, double time) {
    super.stopped(world, time);
    if (m_world != world) {
      throw new Error();
    }
    for (int i = 0; i < m_a.size(); i++) {
      m_world.removeCollisionManagementFor((edu.cmu.cs.stage3.alice.core.Transformable)m_a.elementAt(i));
    }
    for (int i = 0; i < m_b.size(); i++) {
      m_world.removeCollisionManagementFor((edu.cmu.cs.stage3.alice.core.Transformable)m_b.elementAt(i));
    }
    m_world = null;
  }
}
