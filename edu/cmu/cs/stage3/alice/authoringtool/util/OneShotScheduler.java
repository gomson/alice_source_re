package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Scheduler;
import edu.cmu.cs.stage3.alice.core.event.ScheduleListener;


















public class OneShotScheduler
  extends Scheduler
{
  public OneShotScheduler() {}
  
  public boolean isPropertyAffected(Property property)
  {
    ScheduleListener[] scheduleListeners = getScheduleListeners();
    for (int i = 0; i < scheduleListeners.length; i++) {
      ScheduleListener sl = scheduleListeners[i];
      if ((sl instanceof OneShotSimpleBehavior)) {
        Property[] affectedProperties = ((OneShotSimpleBehavior)sl).getAffectedProperties();
        for (int j = 0; j < affectedProperties.length; j++) {
          if (property == affectedProperties[j]) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
