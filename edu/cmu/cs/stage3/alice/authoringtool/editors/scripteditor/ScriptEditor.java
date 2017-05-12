package edu.cmu.cs.stage3.alice.authoringtool.editors.scripteditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.Editor;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.util.ScriptEditorPane;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.property.ScriptProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;












public class ScriptEditor
  extends JPanel
  implements Editor
{
  public String editorName = Messages.getString("Script_Editor");
  
  protected ScriptProperty scriptProperty;
  protected AuthoringTool authoringTool;
  protected ScriptEditorPane scriptEditorPane = new ScriptEditorPane();
  protected CaretListener caretListener = new CaretListener() {
    public void caretUpdate(CaretEvent e) {
      updateLineNumber();
    }
  };
  protected DocumentListener documentListener = new DocumentListener()
  {
    public void changedUpdate(DocumentEvent e) { scriptProperty.set(scriptEditorPane.getText()); }
    public void insertUpdate(DocumentEvent e) { scriptProperty.set(scriptEditorPane.getText()); }
    public void removeUpdate(DocumentEvent e) { scriptProperty.set(scriptEditorPane.getText()); }
  };
  
  public ScriptEditor() {
    jbInit();
    guiInit();
  }
  
  private void guiInit() {
    scriptScrollPane.setViewportView(scriptEditorPane);
    scriptEditorPane.addCaretListener(caretListener);
    scriptEditorPane.performAllAction.setEnabled(false);
    scriptEditorPane.performSelectedAction.setEnabled(false);
  }
  
  public JComponent getJComponent() {
    return this;
  }
  
  public Object getObject() {
    return scriptProperty;
  }
  
  public void setObject(ScriptProperty scriptProperty) {
    scriptEditorPane.getDocument().removeDocumentListener(documentListener);
    this.scriptProperty = scriptProperty;
    
    if (this.scriptProperty != null) {
      if (scriptProperty.getStringValue() == null) {
        scriptProperty.set("");
      }
      scriptEditorPane.setText(scriptProperty.getStringValue());
      
      scriptEditorPane.getDocument().addDocumentListener(documentListener);
      
      scriptEditorPane.resetUndoManager();
      scriptEditorPane.setSandbox(scriptProperty.getOwner().getSandbox());
    } else {
      scriptEditorPane.resetUndoManager();
      scriptEditorPane.setSandbox(null);
    }
  }
  
  public void setAuthoringTool(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
  }
  


  public void updateLineNumber() { lineNumberLabel.setText("  " + Messages.getString("line_number__") + (scriptEditorPane.getCurrentLineNumber() + 1) + "     "); }
  
  public void stateChanging(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarting(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopping(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPausing(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaving(AuthoringToolStateChangedEvent ev) {}
  
  public void stateChanged(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarted(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopped(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPaused(AuthoringToolStateChangedEvent ev) {}
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel southPanel = new JPanel();
  JScrollPane scriptScrollPane = new JScrollPane();
  JLabel lineNumberLabel = new JLabel();
  BoxLayout boxLayout1 = new BoxLayout(southPanel, 0);
  Border border1;
  Border border2;
  Border border3;
  JPanel bogusPanel = new JPanel();
  
  public void worldSaved(AuthoringToolStateChangedEvent ev) {}
  
  private void jbInit() { border1 = BorderFactory.createBevelBorder(1, Color.white, Color.lightGray, new Color(142, 142, 142), new Color(99, 99, 99));
    border2 = BorderFactory.createBevelBorder(1, Color.white, Color.gray, new Color(142, 142, 142), new Color(99, 99, 99));
    border3 = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    border4 = BorderFactory.createBevelBorder(1, Color.white, Color.lightGray, new Color(99, 99, 99), new Color(142, 142, 142));
    setLayout(borderLayout1);
    lineNumberLabel.setBorder(border1);
    lineNumberLabel.setText("  " + Messages.getString("line_number__") + "    ");
    southPanel.setLayout(boxLayout1);
    southPanel.setBorder(border3);
    bogusPanel.setBorder(border4);
    add(southPanel, "South");
    southPanel.add(bogusPanel, null);
    southPanel.add(lineNumberLabel, null);
    add(scriptScrollPane, "Center");
  }
  
  Border border4;
}
