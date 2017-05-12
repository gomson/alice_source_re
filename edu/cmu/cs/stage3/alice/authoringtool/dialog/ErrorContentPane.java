package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.util.StyleStream;
import edu.cmu.cs.stage3.alice.core.ExceptionWrapper;
import edu.cmu.cs.stage3.lang.Messages;
import javax.swing.JDialog;
























public class ErrorContentPane
  extends AliceAlertContentPane
{
  public static final int LESS_DETAIL_MODE = 0;
  public static final int MORE_DETAIL_MODE = 1;
  protected Throwable throwable;
  protected String message;
  
  public ErrorContentPane() {}
  
  public void preDialogShow(JDialog dialog)
  {
    super.preDialogShow(dialog);
    writeAliceHeaderToTextPane();
    writeThrowableToTextPane();
  }
  
  public String getTitle() {
    return Messages.getString("Alice___Error");
  }
  
  public void setThrowable(Throwable t) {
    throwable = t;
  }
  
  public void setDetails(String m) {
    message = m;
  }
  
  protected void writeThrowableToTextPane() {
    if (throwable != null) {
      detailStream.println(
        Messages.getString("Throwable_that_caused_the_error_"));
      throwable.printStackTrace(detailStream);
      if ((throwable instanceof ExceptionWrapper)) {
        ExceptionWrapper ew = (ExceptionWrapper)throwable;
        Exception e = ew.getWrappedException();
        detailStream.println(Messages.getString("Wrapped_exception_"));
        e.printStackTrace(detailStream);
      }
    }
    else if (message != null) {
      detailStream.println(message);
    }
    else
    {
      new Exception(Messages.getString("No_throwable_given__Here_s_the_stack_trace_")).printStackTrace(detailStream);
    }
    detailStream.println();
  }
}
