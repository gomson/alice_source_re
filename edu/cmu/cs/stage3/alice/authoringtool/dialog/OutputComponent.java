package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.util.StyleStream;
import edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane;
import java.awt.BorderLayout;
import javax.swing.BoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;





















public class OutputComponent
  extends JPanel
{
  protected JScrollPane scrollPane;
  protected StyledStreamTextPane textPane;
  protected StyleStream stdOutStream;
  protected StyleStream stdErrStream;
  
  public OutputComponent()
  {
    guiInit();
    miscInit();
  }
  
  private void guiInit() {
    setLayout(new BorderLayout());
    
    textPane = new StyledStreamTextPane();
    scrollPane = new JScrollPane(textPane, 
      22, 
      30);
    
    add(scrollPane, "Center");
  }
  
  private void miscInit() {
    stdOutStream = textPane.getNewStyleStream(textPane.stdOutStyle);
    stdErrStream = textPane.getNewStyleStream(textPane.stdErrStyle);
    

    scrollPane.getVerticalScrollBar().getModel()
      .addChangeListener(new ChangeListener() {
        private int max = 0;
        private JScrollBar scrollBar = scrollPane
          .getVerticalScrollBar();
        
        public void stateChanged(ChangeEvent e) {
          final int newMax = ((BoundedRangeModel)e
            .getSource()).getMaximum();
          if (newMax != max)
          {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                scrollBar.setValue(newMax);
                max = newMax;
              }
            });
          }
        }
      });
  }
  
  public StyledStreamTextPane getTextPane() {
    return textPane;
  }
  
  public StyleStream getStdOutStream() {
    return stdOutStream;
  }
  
  public StyleStream getStdErrStream() {
    return stdErrStream;
  }
  
  public void clear() {
    textPane.setText("");
  }
}
