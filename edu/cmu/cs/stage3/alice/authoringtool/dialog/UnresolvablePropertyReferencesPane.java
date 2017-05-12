package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import java.io.PrintStream;



















public class UnresolvablePropertyReferencesPane
  extends ContentPane
{
  public UnresolvablePropertyReferencesPane(UnresolvablePropertyReferencesException upre)
  {
    System.err.println(upre.getElement());
    PropertyReference[] propertyReferences = upre
      .getPropertyReferences();
    for (int i = 0; i < propertyReferences.length; i++) {
      System.err.println(propertyReferences[i]);
    }
  }
  
  public String getTitle() {
    return Messages.getString("Unresolvable_References");
  }
}
