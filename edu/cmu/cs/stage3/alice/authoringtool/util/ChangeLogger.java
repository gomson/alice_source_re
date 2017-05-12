package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.JAliceFrame;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener;
import edu.cmu.cs.stage3.alice.core.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;





public class ChangeLogger
  implements AuthoringToolStateListener
{
  protected AuthoringTool authoringTool;
  protected World world;
  private Package authoringToolPackage = Package.getPackage("edu.cmu.cs.stage3.alice.authoringtool");
  
  String dataDirectory;
  protected PrintWriter printWriter = null;
  
  public ChangeLogger(AuthoringTool authoringTool)
  {
    this.authoringTool = authoringTool;
    authoringTool.addAuthoringToolStateListener(this);
    


    dataDirectory = (Configuration.getValue(authoringToolPackage, "directories.worldsDirectory") + System.getProperty("file.separator") + "loggingData");
  }
  
  public void pushUndoableRedoable(UndoableRedoable ur) {
    if ((ur instanceof ChildChangeUndoableRedoable)) {
      record(((ChildChangeUndoableRedoable)ur).getLogString());
    } else if ((ur instanceof ObjectArrayPropertyUndoableRedoable)) {
      record(((ObjectArrayPropertyUndoableRedoable)ur).getLogString());
    } else if ((ur instanceof PropertyUndoableRedoable)) {
      record(((PropertyUndoableRedoable)ur).getLogString());
    } else if ((ur instanceof OneShotUndoableRedoable)) {
      record(((OneShotUndoableRedoable)ur).getLogString());
    }
  }
  
  public void recordInstructorIntervention(String type, String comment) {
    String logString = "TIME=<" + System.currentTimeMillis() + "> " + "TYPE=<" + type + "> " + "COMMENT=<" + comment + ">";
    record(logString);
  }
  
  protected void record(String toRecord)
  {
    if ((toRecord != null) && (authoringTool.instructorInControl) && 
      (printWriter != null)) { printWriter.println(toRecord);printWriter.flush();
    }
  }
  
  protected void recordWorldEvent(long time, String type) {
    String logString = "TIME=<" + time + "> " + "EVENT=<World> " + "TYPE=<" + type + ">";
    record(logString);
  }
  




  private File file = null;
  

  public void stateChanged(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoading(AuthoringToolStateChangedEvent ev)
  {
    if ((printWriter != null) && (JAliceFrame.isLogging)) {
      printWriter.close();
      printWriter = null;
      if (file.length() == 0L) {
        file.delete();
        file = null;
      }
    }
  }
  
  public void worldLoaded(AuthoringToolStateChangedEvent ev) {
    if ((JAliceFrame.isLogging) && (file == null)) {
      file = new File(dataDirectory);
      if (!file.exists()) {
        file.mkdir();
      }
      file = new File(dataDirectory + System.getProperty("file.separator") + System.currentTimeMillis() + ".txt");
      try
      {
        printWriter = new PrintWriter(new FileOutputStream(file));
      } catch (FileNotFoundException fnfe) {
        fnfe.printStackTrace(); } } }
  
  public void stateChanging(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarting(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopping(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPausing(AuthoringToolStateChangedEvent ev) {}
  public void worldSaving(AuthoringToolStateChangedEvent ev) {}
  public void worldUnLoaded(AuthoringToolStateChangedEvent ev) {}
  public void worldStarted(AuthoringToolStateChangedEvent ev) { recordWorldEvent(System.currentTimeMillis(), "start"); }
  
  public void worldStopped(AuthoringToolStateChangedEvent ev) {
    recordWorldEvent(System.currentTimeMillis(), "stop");
  }
  
  public void worldPaused(AuthoringToolStateChangedEvent ev) { recordWorldEvent(System.currentTimeMillis(), "pause"); }
  
  public void worldSaved(AuthoringToolStateChangedEvent ev) {
    recordWorldEvent(System.currentTimeMillis(), "save");
  }
}
