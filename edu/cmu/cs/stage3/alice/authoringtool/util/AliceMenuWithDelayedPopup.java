package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.util.Vector;
import javax.swing.JPopupMenu;






















public class AliceMenuWithDelayedPopup
  extends AliceMenu
{
  protected Vector structure;
  protected boolean menuHasBeenPopulated = false;
  
  public AliceMenuWithDelayedPopup(String title, Vector structure) {
    super(title);
    this.structure = structure;
  }
  
  public JPopupMenu getPopupMenu() {
    if (!menuHasBeenPopulated) {
      PopupMenuUtilities.populateDelayedMenu(this, structure);
      menuHasBeenPopulated = true;
    }
    return super.getPopupMenu();
  }
}
