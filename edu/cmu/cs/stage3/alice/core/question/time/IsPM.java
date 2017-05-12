package edu.cmu.cs.stage3.alice.core.question.time;

import edu.cmu.cs.stage3.alice.core.question.BooleanQuestion;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;














public class IsPM
  extends BooleanQuestion
{
  public IsPM() {}
  
  public Object getValue()
  {
    Calendar calendar = new GregorianCalendar();
    Date date = new Date();
    calendar.setTime(date);
    if (calendar.get(9) == 1) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
