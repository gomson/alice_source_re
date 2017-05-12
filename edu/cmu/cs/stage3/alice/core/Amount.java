package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.util.Enumerable;










public class Amount
  extends Enumerable
{
  private int m_Amount_ID;
  
  private Amount(int amount_id)
  {
    m_Amount_ID = amount_id;
  }
  
  public static final Amount TINY = new Amount(0);
  public static final Amount LITTLE = new Amount(1);
  public static final Amount NORMAL = new Amount(2);
  public static final Amount BIG = new Amount(3);
  public static final Amount HUGE = new Amount(4);
  
  public int getAmount(double amount, Box subjectBoundingBox, Box objectBoundingBox)
  {
    return m_Amount_ID;
  }
  
  public boolean equals(Object o)
  {
    if (o == this) return true;
    if ((o != null) && ((o instanceof Amount))) {
      Amount amount = (Amount)o;
      return m_Amount_ID == m_Amount_ID;
    }
    return false;
  }
  
  public static Amount valueOf(String s) {
    return (Amount)Enumerable.valueOf(s, Amount.class);
  }
}
