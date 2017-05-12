package edu.cmu.cs.stage3.alice.core.response;








public class TurnAwayFromConstraint
  extends AbstractPointAtConstraint
{
  public TurnAwayFromConstraint() {}
  







  public class RuntimeTurnAwayFromConstraint
    extends AbstractPointAtConstraint.RuntimeAbstractPointAtConstraint
  {
    public RuntimeTurnAwayFromConstraint()
    {
      super();
    }
    
    protected boolean onlyAffectYaw() { return true; }
    
    protected boolean isTurnAroundNecessary()
    {
      return true;
    }
  }
}
