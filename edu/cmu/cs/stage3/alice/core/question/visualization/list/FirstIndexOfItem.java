package edu.cmu.cs.stage3.alice.core.question.visualization.list;

import edu.cmu.cs.stage3.alice.core.property.ListOfModelsVisualizationProperty;

public class FirstIndexOfItem extends edu.cmu.cs.stage3.alice.core.question.NumberQuestion {
  public FirstIndexOfItem() {}
  
  public final ListOfModelsVisualizationProperty subject = new ListOfModelsVisualizationProperty(this, "subject", null);
  public final edu.cmu.cs.stage3.alice.core.property.ModelProperty item = new edu.cmu.cs.stage3.alice.core.property.ModelProperty(this, "item", null);
  public final edu.cmu.cs.stage3.alice.core.property.NumberProperty startFromIndex = new edu.cmu.cs.stage3.alice.core.property.NumberProperty(this, "startFromIndex", null);
  
  public Object getValue() {
    edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization listOfModelsVisualizationValue = subject.getListOfModelsVisualizationValue();
    if (listOfModelsVisualizationValue != null) {
      return new Integer(listOfModelsVisualizationValue.indexOf(item.getModelValue(), startFromIndex.intValue(-1)));
    }
    return null;
  }
}
