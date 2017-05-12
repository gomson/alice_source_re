package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.visualization.StackOfModelsVisualization;

public class StackOfModelsVisualizationProperty extends CollectionOfModelsVisualizationProperty
{
  public StackOfModelsVisualizationProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, StackOfModelsVisualization defaultValue) {
    super(owner, name, defaultValue, StackOfModelsVisualization.class);
  }
  
  public StackOfModelsVisualization getStackOfModelsVisualizationValue() { return (StackOfModelsVisualization)getCollectionOfModelsVisualizationValue(); }
}
