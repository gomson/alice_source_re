package edu.cmu.cs.stage3.alice.core.question.time;

import edu.cmu.cs.stage3.alice.core.question.IntegerQuestion;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;















public class Year
  extends IntegerQuestion
{
  public Year() {}
  
  public Object getValue()
  {
    Calendar calendar = new GregorianCalendar();
    Date date = new Date();
    calendar.setTime(date);
    return new Integer(calendar.get(1));
  }
}
