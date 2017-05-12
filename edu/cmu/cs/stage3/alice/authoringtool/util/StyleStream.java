package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.lang.Messages;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Position;
import javax.swing.text.Style;
import org.mozilla.universalchardet.UniversalDetector;

















public class StyleStream
  extends PrintStream
{
  protected Style style;
  protected StyledStreamTextPane styledStreamTextPane;
  
  public StyleStream(StyledStreamTextPane styledStreamTextPane, Style style)
    throws UnsupportedEncodingException
  {
    super(System.out, true, "UTF-8");
    this.styledStreamTextPane = styledStreamTextPane;
    this.style = style;
  }
  
  public void write(int b) {
    try {
      styledStreamTextPane.document.insertString(styledStreamTextPane.endPosition.getOffset() - 1, String.valueOf(b), style);
    } catch (BadLocationException e) {
      AuthoringTool.showErrorDialog(Messages.getString("Error_while_printing_"), e);
    }
  }
  
  public void write(byte[] buf, int off, int len) {
    try {
      styledStreamTextPane.document.insertString(styledStreamTextPane.endPosition.getOffset() - 1, new String(buf, off, len, detectEncoding(buf)), style);
    } catch (BadLocationException e) {
      AuthoringTool.showErrorDialog(Messages.getString("Error_while_printing_"), e);
    } catch (Exception localException) {}
  }
  
  public static String detectEncoding(byte[] bytes) {
    String DEFAULT_ENCODING = "UTF-8";
    UniversalDetector detector = 
      new UniversalDetector(null);
    detector.handleData(bytes, 0, bytes.length);
    detector.dataEnd();
    String encoding = detector.getDetectedCharset();
    detector.reset();
    if (encoding == null) {
      encoding = DEFAULT_ENCODING;
    }
    return encoding;
  }
}
