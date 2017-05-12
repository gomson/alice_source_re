package edu.cmu.cs.stage3.alice.core.question.visualization.array;

import edu.cmu.cs.stage3.alice.core.property.ArrayOfModelsVisualizationProperty;

public class ItemAtIndex extends edu.cmu.cs.stage3.alice.core.question.ModelQuestion { public ItemAtIndex() {}
  
  public final ArrayOfModelsVisualizationProperty subject = new ArrayOfModelsVisualizationProperty(this, "subject", null);
  public final edu.cmu.cs.stage3.alice.core.property.NumberProperty index = new edu.cmu.cs.stage3.alice.core.property.NumberProperty(this, "index", new Integer(-1));
  
  public Object getValue() {
    edu.cmu.cs.stage3.alice.core.visualization.ArrayOfModelsVisualization arrayOfModelsVisualizationValue = subject.getArrayOfModelsVisualizationValue();
    if (arrayOfModelsVisualizationValue != null) {
      return arrayOfModelsVisualizationValue.get(index.intValue());
    }
    return null;
  }
}
