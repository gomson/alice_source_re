package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber;
import edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString;
import edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo;
import edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge;
import edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge;
import edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth;
import edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek;
import edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth;
import edu.cmu.cs.stage3.alice.core.question.time.DayOfYear;
import edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM;
import edu.cmu.cs.stage3.alice.core.question.time.HourOfDay;
import edu.cmu.cs.stage3.alice.core.question.time.IsAM;
import edu.cmu.cs.stage3.alice.core.question.time.IsPM;
import edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour;
import edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear;
import edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute;
import edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart;
import edu.cmu.cs.stage3.alice.core.question.time.Year;
import edu.cmu.cs.stage3.alice.core.response.Print;
import edu.cmu.cs.stage3.lang.Messages;








public class ToStringQuestion
  extends Question
{
  public final ObjectProperty what = new ObjectProperty(this, "what", new String(""), Object.class)
  {

    protected boolean getValueOfExpression() { return true; }
  };
  
  public ToStringQuestion() {}
  
  public Class getValueClass() { return String.class; }
  
  public Object getValue()
  {
    Object value = what.getValue();
    Object o = what.get();
    if ((o instanceof TimeElapsedSinceWorldStart)) {
      Print.outputtext = Messages.getString("time_elapsed_as_a_string_is_");
    } else if ((o instanceof Year)) {
      Print.outputtext = Messages.getString("year_as_a_string_is_");
    } else if ((o instanceof MonthOfYear)) {
      Print.outputtext = Messages.getString("month_of_year_as_a_string_is_");
    } else if ((o instanceof DayOfYear)) {
      Print.outputtext = Messages.getString("day_of_year_as_a_string_is_");
    } else if ((o instanceof DayOfMonth)) {
      Print.outputtext = Messages.getString("day_of_month_as_a_string_is_");
    } else if ((o instanceof DayOfWeek)) {
      Print.outputtext = Messages.getString("day_of_week_as_a_string_is_");
    } else if ((o instanceof DayOfWeekInMonth)) {
      Print.outputtext = Messages.getString("day_of_week_in_month_as_a_string_is_");
    } else if ((o instanceof IsAM)) {
      Print.outputtext = Messages.getString("is_AM_as_a_string_is_");
    } else if ((o instanceof IsPM)) {
      Print.outputtext = Messages.getString("is_PM_as_a_string_is_");
    } else if ((o instanceof HourOfAMOrPM)) {
      Print.outputtext = Messages.getString("hour_of_AM_or_PM_as_a_string_is_");
    } else if ((o instanceof HourOfDay)) {
      Print.outputtext = Messages.getString("hour_of_day_as_a_string_is_");
    } else if ((o instanceof MinuteOfHour)) {
      Print.outputtext = Messages.getString("minutes_of_hour_as_a_string_is_");
    } else if ((o instanceof SecondOfMinute)) {
      Print.outputtext = Messages.getString("seconds_of_minute_as_a_string_is_");
    }
    else if ((o instanceof DistanceFromLeftEdge)) {
      Print.outputtext = Messages.getString("mouse_distance_from_left_edge_as_a_string_is_");
    } else if ((o instanceof DistanceFromTopEdge)) {
      Print.outputtext = Messages.getString("mouse_distance_from_top_edge_as_a_string_is_");
    }
    else if ((o instanceof AskUserForNumber)) {
      Print.outputtext = Messages.getString("ask_user_for_a_number_as_a_string_is_");
    } else if ((o instanceof AskUserYesNo)) {
      Print.outputtext = Messages.getString("ask_user_for_yes_or_no_as_a_string_is_");
    } else if ((o instanceof AskUserForString)) {
      Print.outputtext = Messages.getString("ask_user_for_a_string_as_a_string_is_");
    }
    else if ((o instanceof RandomNumber)) {
      Print.outputtext = Messages.getString("random_number_as_a_string_is_");
    } else if (Print.outputtext != null) {
      Print.outputtext = Print.outputtext.substring(0, Print.outputtext.length() - 4) + " " + Messages.getString("as_a_string_is_");
    }
    if ((value instanceof Element))
      return ((Element)value).getTrimmedKey();
    if (value != null) {
      return value.toString();
    }
    return null;
  }
}
