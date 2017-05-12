package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

















public class InvisibleSplitPaneUI
  extends BasicSplitPaneUI
{
  public InvisibleSplitPaneUI() {}
  
  public static ComponentUI createUI(JComponent x)
  {
    return new InvisibleSplitPaneUI();
  }
  
  public BasicSplitPaneDivider createDefaultDivider() {
    return new InvisibleSplitPaneDivider(this);
  }
  
  public class InvisibleSplitPaneDivider
    extends BasicSplitPaneDivider
  {
    public InvisibleSplitPaneDivider(BasicSplitPaneUI ui)
    {
      super();
    }
    
    public void paint(Graphics g) {}
  }
}
