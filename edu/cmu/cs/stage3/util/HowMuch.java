package edu.cmu.cs.stage3.util;






















public class HowMuch
  extends Enumerable
{
  public static final HowMuch INSTANCE = new HowMuch(false, false);
  public static final HowMuch INSTANCE_AND_PARTS = new HowMuch(true, true);
  public static final HowMuch INSTANCE_AND_ALL_DESCENDANTS = new HowMuch(true, false);
  private boolean m_descend;
  private boolean m_respectDescendant;
  
  private HowMuch(boolean descend, boolean respectDescendant) { m_descend = descend;
    m_respectDescendant = respectDescendant;
  }
  
  public boolean getDescend() { return m_descend; }
  
  public boolean getRespectDescendant() {
    return m_respectDescendant;
  }
  
  public static HowMuch valueOf(String s) { return (HowMuch)Enumerable.valueOf(s, HowMuch.class); }
}
