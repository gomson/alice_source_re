package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.util.Criterion;

public abstract interface ReferenceResolver
{
  public abstract Element resolveReference(Criterion paramCriterion)
    throws UnresolvableReferenceException;
}
