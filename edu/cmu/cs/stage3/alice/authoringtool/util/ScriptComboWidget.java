package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.scripting.Code;
import edu.cmu.cs.stage3.alice.scripting.CompileType;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.python.core.Py;
import org.python.core.PyException;












public class ScriptComboWidget
  extends JPanel
{
  protected Sandbox sandbox;
  protected OneShotScheduler oneShotScheduler = new OneShotScheduler();
  
  public final AbstractAction runAction = new AbstractAction() {
    public void actionPerformed(ActionEvent ev) {
      runScript();
    }
  };
  
  public ScriptComboWidget() {
    actionInit();
    guiInit();
  }
  
  private void actionInit()
  {
    runAction.putValue("ActionCommandKey", "go");
    
    runAction.putValue("Name", Messages.getString("Go"));
    runAction.putValue("ShortDescription", Messages.getString("Execute_the_given_script"));
  }
  
  public void setSandbox(Sandbox sandbox)
  {
    this.sandbox = sandbox;
  }
  
  public void runScript() {
    Object item = comboBox.getEditor().getItem();
    if ((item instanceof String)) {
      String script = ((String)item).trim();
      if (script.length() != 0) {
        try {
          Code code = sandbox.compile(script, "<Run Line>", CompileType.EXEC_SINGLE);
          sandbox.exec(code);
          
          for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (script.equals(comboBox.getItemAt(i))) {
              comboBox.removeItemAt(i);
              break;
            }
          }
          comboBox.insertItemAt(script, 0);
          comboBox.setSelectedItem(script);
        } catch (PyException e) {
          Py.printException(e, null, AuthoringTool.getPyStdErr());
        } catch (Throwable t) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_running_jython_code_"), t);
        }
      }
    } else {
      comboBox.removeItem(item);
    }
  }
  




  private JComboBox comboBox = new JComboBox();
  private JButton runButton = new JButton(runAction);
  
  private void guiInit() {
    setLayout(new BorderLayout());
    comboBox.setEditable(true);
    comboBox.getEditor().addActionListener(runAction);
    
    add(comboBox, "Center");
    add(runButton, "East");
  }
}
