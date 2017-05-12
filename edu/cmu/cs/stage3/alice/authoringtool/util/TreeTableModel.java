package edu.cmu.cs.stage3.alice.authoringtool.util;

import javax.swing.tree.TreeModel;

public abstract interface TreeTableModel
  extends TreeModel
{
  public abstract int getColumnCount();
  
  public abstract String getColumnName(int paramInt);
  
  public abstract Class getColumnClass(int paramInt);
  
  public abstract Object getValueAt(Object paramObject, int paramInt);
  
  public abstract Class getTypeAt(Object paramObject, int paramInt);
  
  public abstract boolean isNullValidAt(Object paramObject, int paramInt);
  
  public abstract boolean isCellEditable(Object paramObject, int paramInt);
  
  public abstract void setValueAt(Object paramObject1, Object paramObject2, int paramInt);
}
