package edu.cmu.cs.stage3.alice.core.criterion;





















public class InternalReferenceKeyedCriterion
  extends ElementKeyedCriterion
{
  public InternalReferenceKeyedCriterion(String key)
  {
    super(key);
  }
  
  public InternalReferenceKeyedCriterion(String key, boolean ignoreCase) { super(key, ignoreCase); }
  

  public static ElementKeyedCriterion valueOf(String s)
  {
    return ElementKeyedCriterion.valueOf(s, InternalReferenceKeyedCriterion.class);
  }
}
