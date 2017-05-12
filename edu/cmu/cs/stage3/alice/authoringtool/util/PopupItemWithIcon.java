package edu.cmu.cs.stage3.alice.authoringtool.util;

import javax.swing.Icon;























public class PopupItemWithIcon
{
  protected Object item;
  protected Icon icon;
  
  public PopupItemWithIcon(Object item, Icon icon)
  {
    this.item = item;
    this.icon = icon;
  }
  
  public Object getItem() {
    return item;
  }
  
  public Icon getIcon() {
    return icon;
  }
}
