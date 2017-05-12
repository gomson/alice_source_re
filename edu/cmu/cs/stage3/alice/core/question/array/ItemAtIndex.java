package edu.cmu.cs.stage3.alice.core.question.array;

import edu.cmu.cs.stage3.alice.core.Array;
import edu.cmu.cs.stage3.alice.core.SimulationException;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.lang.Messages;


















public class ItemAtIndex
  extends ArrayObjectQuestion
{
  public ItemAtIndex() {}
  
  public final NumberProperty index = new NumberProperty(this, "index", new Integer(-1));
  
  public Object getValue(Array arrayValue) {
    int i = index.intValue();
    int n = arrayValue.size();
    if ((i >= 0) && (i < n)) {
      return arrayValue.itemValueAtIndex(i);
    }
    throw new SimulationException(Messages.getString("index_out_of_bounds_exception___") + i + " " + Messages.getString("is_not_in_range__0_") + n + ")", null, this);
  }
}
