package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.text.BreakIterator;
import javax.swing.JOptionPane;











public class SimpleOutput
{
  public SimpleOutput() {}
  
  public static void showWarning(String message)
  {
    message = addNewLines(message);
    JOptionPane.showMessageDialog(null, message, Messages.getString("Warning_Display"), 
      2);
  }
  




  public static void showError(String message)
  {
    message = addNewLines(message);
    JOptionPane.showMessageDialog(null, message, Messages.getString("Error_Display"), 
      0);
  }
  




  public static void showInformation(String message)
  {
    message = addNewLines(message);
    JOptionPane.showMessageDialog(null, message, Messages.getString("Information_Display"), 
      1);
  }
  






  public static String addNewLines(String message)
  {
    BreakIterator boundary = 
      BreakIterator.getLineInstance();
    boundary.setText(message);
    int start = boundary.first();
    String result = "";
    String currLine = "";
    String temp = null;
    

    for (int end = boundary.next(); 
        end != -1; 
        end = boundary.next())
    {

      temp = message.substring(start, end);
      





      if (temp.length() + currLine.length() > 100)
      {
        result = result + currLine + "\n";
        currLine = temp;
      }
      else
      {
        currLine = currLine + temp;
      }
      start = end;
    }
    


















    if (result.length() == 0) {
      result = message;
    }
    else {
      result = result + currLine;
    }
    return result;
  }
}
