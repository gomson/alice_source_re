package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.util.Criterion;

public abstract interface ReferenceGenerator
{
  public abstract Criterion generateReference(Element paramElement);
}
