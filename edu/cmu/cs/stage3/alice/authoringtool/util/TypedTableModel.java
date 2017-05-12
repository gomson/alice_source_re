package edu.cmu.cs.stage3.alice.authoringtool.util;

import javax.swing.table.TableModel;

public abstract interface TypedTableModel
  extends TableModel
{
  public abstract Class getTypeAt(int paramInt1, int paramInt2);
  
  public abstract boolean isNullValidAt(int paramInt1, int paramInt2);
}
