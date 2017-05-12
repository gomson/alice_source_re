package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.core.Element;
import java.awt.Component;
import java.awt.Container;

public abstract interface CompositeComponentOwner
{
  public abstract Element getElement();
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract Container getParent();
  
  public abstract boolean isExpanded();
  
  public abstract Component getGrip();
}
