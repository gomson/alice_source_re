package edu.cmu.cs.stage3.alice.core.question.visualization.list;

import edu.cmu.cs.stage3.alice.core.property.ListOfModelsVisualizationProperty;

public class Contains extends edu.cmu.cs.stage3.alice.core.question.BooleanQuestion { public Contains() {}
  
  public final ListOfModelsVisualizationProperty subject = new ListOfModelsVisualizationProperty(this, "subject", null);
  public final edu.cmu.cs.stage3.alice.core.property.ModelProperty item = new edu.cmu.cs.stage3.alice.core.property.ModelProperty(this, "item", null);
  
  public Object getValue() {
    edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization listOfModelsVisualizationValue = subject.getListOfModelsVisualizationValue();
    if (listOfModelsVisualizationValue != null) {
      if (listOfModelsVisualizationValue.contains(item.getModelValue())) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
    
    return null;
  }
}
