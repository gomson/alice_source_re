package edu.cmu.cs.stage3.alice.gallery.batch;

import edu.cmu.cs.stage3.alice.core.Element;
import java.io.File;

public abstract interface ElementHandler
{
  public abstract void handleElement(Element paramElement, File paramFile);
}
