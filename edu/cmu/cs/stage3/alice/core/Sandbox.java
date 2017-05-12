package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ScriptProperty;
import java.util.Vector;



















public abstract class Sandbox
  extends Element
{
  public Sandbox() {}
  
  public final ScriptProperty script = new ScriptProperty(this, "script", null);
  public final ElementArrayProperty responses = new ElementArrayProperty(this, "responses", null, [Ledu.cmu.cs.stage3.alice.core.Response.class);
  public final ElementArrayProperty behaviors = new ElementArrayProperty(this, "behaviors", null, [Ledu.cmu.cs.stage3.alice.core.Behavior.class);
  public final ElementArrayProperty variables = new ElementArrayProperty(this, "variables", null, [Ledu.cmu.cs.stage3.alice.core.Variable.class);
  public final ElementArrayProperty questions = new ElementArrayProperty(this, "questions", null, [Ledu.cmu.cs.stage3.alice.core.Question.class);
  public final ElementArrayProperty textureMaps = new ElementArrayProperty(this, "textureMaps", null, [Ledu.cmu.cs.stage3.alice.core.TextureMap.class);
  public final ElementArrayProperty sounds = new ElementArrayProperty(this, "sounds", null, [Ledu.cmu.cs.stage3.alice.core.Sound.class);
  public final ElementArrayProperty geometries = new ElementArrayProperty(this, "geometries", null, [Ledu.cmu.cs.stage3.alice.core.Geometry.class);
  public final ElementArrayProperty misc = new ElementArrayProperty(this, "misc", null, [Ledu.cmu.cs.stage3.alice.core.Element.class);
  
  private Behavior m_currentBehavior = null;
  
  public Expression lookup(String key) {
    if (m_currentBehavior != null) {
      Expression expression = m_currentBehavior.stackLookup(key);
      if (expression != null) {
        return expression;
      }
      return m_currentBehavior.detailLookup(key);
    }
    
    return null;
  }
  
  public Behavior getCurrentBehavior()
  {
    return m_currentBehavior;
  }
  
  protected void scheduleBehaviors(double t) {
    for (int i = 0; i < behaviors.size(); i++) {
      Behavior behaviorI = (Behavior)behaviors.get(i);
      





      m_currentBehavior = behaviorI;
      behaviorI.preSchedule(t);
      behaviorI.schedule(t);
      behaviorI.postSchedule(t);
      m_currentBehavior = null;
    }
  }
  
  protected void internalFindAccessibleExpressions(Class cls, Vector v)
  {
    for (int i = 0; i < variables.size(); i++) {
      internalAddExpressionIfAssignableTo((Expression)variables.get(i), cls, v);
    }
    for (int i = 0; i < questions.size(); i++) {
      internalAddExpressionIfAssignableTo((Expression)questions.get(i), cls, v);
    }
    super.internalFindAccessibleExpressions(cls, v);
  }
}
