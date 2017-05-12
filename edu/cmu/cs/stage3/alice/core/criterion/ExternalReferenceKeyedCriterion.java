package edu.cmu.cs.stage3.alice.core.criterion;





















public class ExternalReferenceKeyedCriterion
  extends ElementKeyedCriterion
{
  public ExternalReferenceKeyedCriterion(String key)
  {
    super(key);
  }
  
  public ExternalReferenceKeyedCriterion(String key, boolean ignoreCase) { super(key, ignoreCase); }
  

  public static ElementKeyedCriterion valueOf(String s)
  {
    return ElementKeyedCriterion.valueOf(s, ExternalReferenceKeyedCriterion.class);
  }
}
