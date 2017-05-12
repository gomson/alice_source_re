package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.visualization.ArrayOfModelsVisualization;

public class ArrayOfModelsVisualizationProperty extends CollectionOfModelsVisualizationProperty
{
  public ArrayOfModelsVisualizationProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, ArrayOfModelsVisualization defaultValue) {
    super(owner, name, defaultValue, ArrayOfModelsVisualization.class);
  }
  
  public ArrayOfModelsVisualization getArrayOfModelsVisualizationValue() { return (ArrayOfModelsVisualization)getCollectionOfModelsVisualizationValue(); }
}
