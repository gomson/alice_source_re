package edu.cmu.cs.stage3.alice.core.question.visualization.list;

import edu.cmu.cs.stage3.alice.core.property.ListOfModelsVisualizationProperty;

public class ItemAtIndex extends edu.cmu.cs.stage3.alice.core.question.ModelQuestion { public ItemAtIndex() {}
  
  public final ListOfModelsVisualizationProperty subject = new ListOfModelsVisualizationProperty(this, "subject", null);
  public final edu.cmu.cs.stage3.alice.core.property.NumberProperty index = new edu.cmu.cs.stage3.alice.core.property.NumberProperty(this, "index", new Integer(-1));
  
  public Object getValue() {
    edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization listOfModelsVisualizationValue = subject.getListOfModelsVisualizationValue();
    if (listOfModelsVisualizationValue != null) {
      return listOfModelsVisualizationValue.get(index.intValue());
    }
    return null;
  }
}
