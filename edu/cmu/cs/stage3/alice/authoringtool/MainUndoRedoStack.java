package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener;
import edu.cmu.cs.stage3.alice.authoringtool.event.UndoRedoListener;
import edu.cmu.cs.stage3.alice.authoringtool.util.ChangeLogger;
import edu.cmu.cs.stage3.alice.authoringtool.util.ChildChangeUndoableRedoable;
import edu.cmu.cs.stage3.alice.authoringtool.util.CompoundUndoableRedoable;
import edu.cmu.cs.stage3.alice.authoringtool.util.ContextAssignableUndoableRedoable;
import edu.cmu.cs.stage3.alice.authoringtool.util.DefaultUndoRedoStack;
import edu.cmu.cs.stage3.alice.authoringtool.util.ObjectArrayPropertyUndoableRedoable;
import edu.cmu.cs.stage3.alice.authoringtool.util.OneShotScheduler;
import edu.cmu.cs.stage3.alice.authoringtool.util.PropertyUndoableRedoable;
import edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.core.event.ChildrenListener;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ScriptProperty;
import java.util.HashSet;
import java.util.Iterator;

public class MainUndoRedoStack extends DefaultUndoRedoStack implements PropertyListener, ObjectArrayPropertyListener, ChildrenListener, AuthoringToolStateListener
{
  protected AuthoringTool authoringTool;
  private boolean isListening = true;
  private boolean inCompound = false;
  private CompoundUndoableRedoable compoundItem;
  private int unmodifiedIndex = -1;
  private boolean scriptHasChanged = false;
  protected HashSet listeners = new HashSet();
  

  protected ChangeLogger changeLogger = null;
  protected Object preChangeValue;
  
  public MainUndoRedoStack(AuthoringTool authoringTool) { this.authoringTool = authoringTool;
    loggingInit(authoringTool);
  }
  

  private void loggingInit(AuthoringTool authoringTool)
  {
    changeLogger = new ChangeLogger(authoringTool);
  }
  


  public void logInstructorIntervention(String type, String comment)
  {
    if (changeLogger != null) {
      changeLogger.recordInstructorIntervention(type, comment);
    }
  }
  
  public void addUndoRedoListener(UndoRedoListener listener)
  {
    listeners.add(listener);
  }
  
  public void removeUndoRedoListener(UndoRedoListener listener) {
    listeners.remove(listener);
  }
  
  public int getUnmodifiedIndex() {
    return unmodifiedIndex;
  }
  
  public boolean isScriptDirty() {
    return scriptHasChanged;
  }
  
  protected synchronized void fireChange() {
    for (Iterator iter = listeners.iterator(); iter.hasNext();) {
      ((UndoRedoListener)iter.next()).onChange();
    }
  }
  
  public synchronized void setUnmodified() {
    unmodifiedIndex = getCurrentUndoableRedoableIndex();
    scriptHasChanged = false;
    fireChange();
  }
  
  public synchronized void setIsListening(boolean isListening) {
    this.isListening = isListening;
  }
  
  public synchronized boolean getIsListening() {
    return isListening;
  }
  
  public synchronized void startCompound() {
    if (!inCompound) {
      compoundItem = new CompoundUndoableRedoable();
      compoundItem.setContext(authoringTool.getContext());
      push(compoundItem);
      inCompound = true;
    }
  }
  
  public synchronized void stopCompound() {
    inCompound = false;
  }
  
  public synchronized UndoableRedoable undo() {
    stopCompound();
    boolean temp = isListening;
    isListening = false;
    UndoableRedoable ur = super.undo();
    loadContext(ur.getContext());
    Object o = ur.getAffectedObject();
    if ((o instanceof Variable)) {
      Variable v = (Variable)o;
      if ((v.getParent() instanceof edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization)) {
        for (int i = 0; i < 5; i++) {
          super.undo();
        }
      }
    }
    
    isListening = temp;
    fireChange();
    return ur;
  }
  
  public synchronized UndoableRedoable redo() {
    stopCompound();
    boolean temp = isListening;
    isListening = false;
    int currentIndex = getCurrentUndoableRedoableIndex();
    if (currentIndex < size() - 1) {
      loadContext(((UndoableRedoable)get(currentIndex + 1)).getContext());
    }
    UndoableRedoable ur = super.redo();
    isListening = temp;
    fireChange();
    return ur;
  }
  
  protected synchronized void loadContext(Object context) {
    authoringTool.setContext(context);
  }
  

  public synchronized void push(UndoableRedoable ur)
  {
    if ((changeLogger != null) && (JAliceFrame.isLogging)) {
      changeLogger.pushUndoableRedoable(ur);
    }
    
    if ((ur instanceof ContextAssignableUndoableRedoable)) {
      ((ContextAssignableUndoableRedoable)ur).setContext(authoringTool.getContext());
    }
    if (inCompound) {
      compoundItem.addItem(ur);
    } else {
      super.push(ur);
    }
    fireChange();
  }
  
  public synchronized UndoableRedoable pop_() {
    stopCompound();
    UndoableRedoable ur = super.pop_();
    fireChange();
    return ur;
  }
  
  public synchronized void clear() {
    super.clear();
    setUnmodified();
    fireChange();
  }
  
  public synchronized UndoableRedoable removeUndoable(int index) {
    UndoableRedoable ur = super.removeUndoable(index);
    fireChange();
    return ur;
  }
  
  public synchronized void propertyChanging(PropertyEvent propertyEvent)
  {
    if (isListening)
    {

      propertyEvent.setOldValue(propertyEvent.getProperty().get());
    }
  }
  
  public synchronized void propertyChanged(PropertyEvent propertyEvent) {
    if (isListening)
    {
      if ((propertyEvent.getProperty() instanceof ObjectArrayProperty)) {
        return;
      }
      

      if (authoringTool.getOneShotScheduler().isPropertyAffected(propertyEvent.getProperty())) {
        return;
      }
      

      if ((propertyEvent.getProperty() instanceof ScriptProperty)) {
        scriptHasChanged = true;
        fireChange();
        return;
      }
      push(new PropertyUndoableRedoable(propertyEvent.getProperty(), propertyEvent.getOldValue(), propertyEvent.getProperty().get()));
    }
  }
  
  public synchronized void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
  
  public synchronized void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev)
  {
    if (isListening)
    {
      if (authoringTool.getOneShotScheduler().isPropertyAffected(ev.getObjectArrayProperty())) {
        return;
      }
      
      push(new ObjectArrayPropertyUndoableRedoable(ev.getObjectArrayProperty(), ev.getChangeType(), ev.getOldIndex(), ev.getNewIndex(), ev.getItem()));
    }
  }
  
  public synchronized void childrenChanging(ChildrenEvent childrenEvent) {}
  
  public synchronized void childrenChanged(ChildrenEvent childrenEvent) { if (isListening) {
      push(new ChildChangeUndoableRedoable(childrenEvent));
    }
    
    int changeType = childrenEvent.getChangeType();
    if (changeType == 1) {
      listenTo(childrenEvent.getChild());
    } else if (changeType == 3) {
      stopListeningTo(childrenEvent.getChild());
    }
  }
  
  public synchronized void listenTo(Element element) {
    if (element != null) {
      Element[] elements = element.getDescendants();
      for (int i = 0; i < elements.length; i++) {
        Property[] properties = elements[i].getProperties();
        for (int j = 0; j < properties.length; j++) {
          properties[j].addPropertyListener(this);
          if ((properties[j] instanceof ObjectArrayProperty)) {
            ((ObjectArrayProperty)properties[j]).addObjectArrayPropertyListener(this);
          }
        }
        boolean alreadyChildrenListening = false;
        ChildrenListener[] childrenListeners = elements[i].getChildrenListeners();
        for (int j = 0; j < childrenListeners.length; j++) {
          if (childrenListeners[j] == this) {
            alreadyChildrenListening = true;
          }
        }
        if (!alreadyChildrenListening) {
          elements[i].addChildrenListener(this);
        }
      }
    }
  }
  
  public synchronized void stopListeningTo(Element element) {
    if (element != null) {
      Element[] elements = element.getDescendants();
      for (int i = 0; i < elements.length; i++) {
        Property[] properties = elements[i].getProperties();
        for (int j = 0; j < properties.length; j++) {
          properties[j].removePropertyListener(this);
          if ((properties[j] instanceof ObjectArrayProperty)) {
            ((ObjectArrayProperty)properties[j]).removeObjectArrayPropertyListener(this);
          }
        }
        elements[i].removeChildrenListener(this);
      }
    }
  }
  


  public void stateChanged(AuthoringToolStateChangedEvent ev)
  {
    if (ev.getCurrentState() == 3) {
      stopListeningTo(ev.getWorld());
    } else {
      listenTo(ev.getWorld());
    }
  }
  
  public void worldUnLoading(AuthoringToolStateChangedEvent ev) {
    stopListeningTo(ev.getWorld());
  }
  
  public void worldLoaded(AuthoringToolStateChangedEvent ev) {
    listenTo(ev.getWorld());
  }
  
  public void stateChanging(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarting(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopping(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPausing(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaving(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarted(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopped(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPaused(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaved(AuthoringToolStateChangedEvent ev) {}
}
