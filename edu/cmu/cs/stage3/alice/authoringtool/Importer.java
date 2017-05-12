package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.alice.core.Element;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public abstract interface Importer
{
  public abstract Map getExtensionMap();
  
  public abstract Element load(String paramString)
    throws IOException;
  
  public abstract Element load(File paramFile)
    throws IOException;
  
  public abstract Element load(URL paramURL)
    throws IOException;
}
