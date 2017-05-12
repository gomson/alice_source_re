package edu.cmu.cs.stage3.alice.core.response;








public class TurnToFaceConstraint
  extends AbstractPointAtConstraint
{
  public TurnToFaceConstraint() {}
  







  public class RuntimeTurnToFaceConstraint
    extends AbstractPointAtConstraint.RuntimeAbstractPointAtConstraint
  {
    public RuntimeTurnToFaceConstraint()
    {
      super();
    }
    
    protected boolean onlyAffectYaw() { return true; }
  }
}
