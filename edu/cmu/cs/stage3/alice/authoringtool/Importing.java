package edu.cmu.cs.stage3.alice.authoringtool;

import java.util.ArrayList;
import java.util.List;























public class Importing
{
  protected ArrayList importers = new ArrayList();
  
  public Importing() {
    init();
  }
  
  private void init() {
    Class[] importerClasses = AuthoringToolResources.getImporterClasses();
    for (int i = 0; i < importerClasses.length; i++) {
      try {
        importers.add(importerClasses[i].newInstance());
      } catch (Throwable t) {
        AuthoringTool.showErrorDialog("Error creating importer of type " + importerClasses[i], t);
      }
    }
  }
  
  public List getImporters() {
    return importers;
  }
}
