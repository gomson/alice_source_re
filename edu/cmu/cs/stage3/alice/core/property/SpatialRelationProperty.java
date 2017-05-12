package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.SpatialRelation;





















public class SpatialRelationProperty
  extends EnumerableProperty
{
  public SpatialRelationProperty(Element owner, String name, SpatialRelation defaultValue)
  {
    super(owner, name, defaultValue, SpatialRelation.class);
  }
  
  public SpatialRelation getSpatialRelationValue() { return (SpatialRelation)getEnumerableValue(); }
}
