package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.scripting.Code;
import edu.cmu.cs.stage3.alice.scripting.CompileType;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import org.python.core.Py;
import org.python.core.PyException;

public class ScriptEditorPane extends JEditorPane
{
  protected Sandbox sandbox;
  protected Action[] actions;
  protected JPopupMenu popup;
  protected String findString = "";
  protected String replaceWithString = "";
  protected boolean matchCase = false;
  protected boolean findFromStart = true;
  





  public final Action performAllAction = new AbstractAction() {
    public void actionPerformed(ActionEvent ev) {
      if (sandbox != null) {
        String script = getText();
        try {
          Code code = sandbox.compile(script, "<ScriptEditorPane>", CompileType.EXEC_MULTIPLE);
          sandbox.exec(code);
        } catch (PyException e) {
          if (!Py.matchException(e, Py.SystemExit))
          {

            Py.printException(e, null, AuthoringTool.getPyStdErr());
          }
        }
      }
    }
  };
  

  public final Action performSelectedAction = new AbstractAction() {
    public void actionPerformed(ActionEvent ev) {
      if (sandbox != null) {
        try {
          int selectionStart = getLineStartOffset(getLineOfOffset(getSelectionStart()));
          int selectionEnd = getLineEndOffset(getLineOfOffset(getSelectionEnd()));
          String script = getText(selectionStart, selectionEnd - selectionStart);
          try {
            Code code = sandbox.compile(script, "<ScriptEditorPane>", CompileType.EXEC_MULTIPLE);
            sandbox.exec(code);
          } catch (PyException e) {
            if (Py.matchException(e, Py.SystemExit))
              return;
          }
          Py.printException(e, null, AuthoringTool.getPyStdErr());

        }
        catch (BadLocationException ble)
        {
          AuthoringTool.showErrorDialog(Messages.getString("Error_getting_selected_code_"), ble);
        }
      }
    }
  };
  
  public ScriptEditorPane() {
    actionInit();
    popupInit();
    setDefaultKeyBindingsEnabled(true);
    setFont(new Font("Monospaced", 0, 12));
    getDocument().addUndoableEditListener(undoHandler);
    addMouseListener(editorPaneMouseListener);
    setSize(new Dimension(10000, 100));
  }
  
  private void actionInit() {
    performAllAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(115, 2));
    performAllAction.putValue("ActionCommandKey", "performAll");
    performAllAction.putValue("MnemonicKey", new Integer(65));
    performAllAction.putValue("Name", Messages.getString("Perform_All__Ctrl_F4_"));
    performAllAction.putValue("ShortDescription", Messages.getString("Performs_the_entire_script_"));
    
    performSelectedAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(115, 0));
    performSelectedAction.putValue("ActionCommandKey", "performSelected");
    performSelectedAction.putValue("MnemonicKey", new Integer(83));
    performSelectedAction.putValue("Name", Messages.getString("Perform_Selected__F4_"));
    performSelectedAction.putValue("ShortDescription", Messages.getString("Performs_the_selected_lines_of_the_script__or_the_line_the_cursor_is_on_if_there_is_no_selection_"));
    
    undoAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(90, 2));
    undoAction.putValue("ActionCommandKey", "undoEdit");
    undoAction.putValue("MnemonicKey", new Integer(85));
    undoAction.putValue("Name", Messages.getString("Undo__Ctrl_Z_"));
    undoAction.putValue("ShortDescription", Messages.getString("Undo_last_edit"));
    
    redoAction.putValue("AcceleratorKey", KeyStroke.getKeyStroke(89, 2));
    redoAction.putValue("ActionCommandKey", "redoEdit");
    redoAction.putValue("MnemonicKey", new Integer(82));
    redoAction.putValue("Name", Messages.getString("Redo__Ctrl_Y_"));
    redoAction.putValue("ShortDescription", Messages.getString("Redo_last_undo"));
    
    actions = new Action[] { performAllAction, performSelectedAction, undoAction, redoAction };
  }
  
  private void popupInit() {
    popup = new JPopupMenu("");
    if (performAllAction.isEnabled()) {
      popup.add(performAllAction);
    }
    if (performSelectedAction.isEnabled()) {
      popup.add(performSelectedAction);
    }
    if ((performAllAction.isEnabled()) || (performSelectedAction.isEnabled())) {
      popup.addSeparator();
    }
    popup.add(undoAction);
    popup.add(redoAction);
  }
  
  public boolean findNext(String stringToFind) { int startFrom;
    int startFrom;
    if (getSelectionStart() != getSelectionEnd()) {
      startFrom = getSelectionEnd();
    } else {
      startFrom = getCaretPosition();
    }
    String script = getText();
    int startSelection;
    int startSelection; if (matchCase) {
      startSelection = script.indexOf(stringToFind, startFrom);
    } else {
      startSelection = script.toLowerCase().indexOf(stringToFind.toLowerCase(), startFrom);
    }
    int endSelection = startSelection + stringToFind.length();
    if ((startSelection >= 0) && (endSelection <= script.length())) {
      select(startSelection, endSelection);
      return true;
    }
    return false;
  }
  
  public void replaceCurrent(String stringToReplaceWith)
  {
    replaceSelection(stringToReplaceWith);
  }
  
  public void replaceAllRemaining(String stringToFind, String stringToReplaceWith) {
    int currentPosition = getCaretPosition();
    
    if (findNext(findString)) {
      replaceCurrent(stringToReplaceWith);
      while (findNext(stringToFind)) {
        replaceCurrent(stringToReplaceWith);
      }
    } else {
      DialogManager.showMessageDialog(Messages.getString("String__") + findString + " " + Messages.getString("not_found_"), Messages.getString("String_not_found"), 1);
    }
    setCaretPosition(currentPosition);
  }
  
  public void setDocument(Document doc) {
    super.setDocument(doc);
    getDocument().addUndoableEditListener(undoHandler);
  }
  
  public edu.cmu.cs.stage3.alice.core.Element getSandbox() {
    return sandbox;
  }
  
  public void setSandbox(Sandbox sandbox) {
    this.sandbox = sandbox;
  }
  
  public void setDefaultKeyBindingsEnabled(boolean b) {
    setDefaultKeyBindingsEnabled(b, actions);
  }
  
  public void setDefaultKeyBindingsEnabled(boolean b, Action[] whichActions) {
    javax.swing.text.Keymap keymap = getKeymap();
    if (b) {
      for (int i = 0; i < whichActions.length; i++) {
        getInputMap().put((KeyStroke)whichActions[i].getValue("AcceleratorKey"), whichActions[i].getValue("ActionCommandKey"));
        getActionMap().put(whichActions[i].getValue("ActionCommandKey"), whichActions[i]);
      }
    } else {
      for (int i = 0; i < whichActions.length; i++) {
        getInputMap().remove((KeyStroke)whichActions[i].getValue("AcceleratorKey"));
        getActionMap().remove(whichActions[i].getValue("ActionCommandKey"));
      }
    }
  }
  
  public int getCurrentLineNumber() {
    try {
      return getLineOfOffset(getCaretPosition());
    } catch (BadLocationException e) {}
    return -1;
  }
  



  public int getLineOfOffset(int offset)
    throws BadLocationException
  {
    Document doc = getDocument();
    if ((doc instanceof PlainDocument)) {
      if (offset < 0)
        throw new BadLocationException(Messages.getString("Can_t_translate_offset_to_line"), -1);
      if (offset > doc.getLength()) {
        throw new BadLocationException(Messages.getString("Can_t_translate_offset_to_line"), doc.getLength() + 1);
      }
      javax.swing.text.Element map = getDocument().getDefaultRootElement();
      return map.getElementIndex(offset);
    }
    
    throw new UnsupportedOperationException(Messages.getString("Cannot_find_line_number__only_PlainDocuments_supported_at_this_time_"));
  }
  
  public int getLineStartOffset(int line) throws BadLocationException
  {
    Document doc = getDocument();
    if ((doc instanceof PlainDocument)) {
      javax.swing.text.Element map = doc.getDefaultRootElement();
      if (line < 0)
        throw new BadLocationException(Messages.getString("Negative_line"), -1);
      if (line >= map.getElementCount()) {
        throw new BadLocationException(Messages.getString("No_such_line"), getDocument().getLength() + 1);
      }
      javax.swing.text.Element lineElem = map.getElement(line);
      return lineElem.getStartOffset();
    }
    
    throw new UnsupportedOperationException(Messages.getString("Cannot_find_line_start_offset__only_PlainDocuments_supported_at_this_time_"));
  }
  
  public int getLineEndOffset(int line) throws BadLocationException
  {
    Document doc = getDocument();
    if ((doc instanceof PlainDocument)) {
      javax.swing.text.Element map = doc.getDefaultRootElement();
      if (line < 0)
        throw new BadLocationException(Messages.getString("Negative_line"), -1);
      if (line >= map.getElementCount()) {
        throw new BadLocationException(Messages.getString("No_such_line"), getDocument().getLength() + 1);
      }
      javax.swing.text.Element lineElem = map.getElement(line);
      return lineElem.getEndOffset();
    }
    
    throw new UnsupportedOperationException(Messages.getString("Cannot_find_line_end_offset__only_PlainDocuments_supported_at_this_time_"));
  }
  





  protected final MouseListener editorPaneMouseListener = new CustomMouseAdapter() {
    protected void popupResponse(MouseEvent e) {
      ScriptEditorPane.this.popupInit();
      popup.show(e.getComponent(), e.getX(), e.getY());
      PopupMenuUtilities.ensurePopupIsOnScreen(popup);
    }
  };
  




  public final UndoAction undoAction = new UndoAction();
  public final RedoAction redoAction = new RedoAction();
  protected UndoableEditListener undoHandler = new UndoHandler();
  protected UndoManager undoManager = new UndoManager();
  
  public void resetUndoManager() {
    undoManager.discardAllEdits();
    undoAction.update();
    redoAction.update();
  }
  
  class UndoHandler implements UndoableEditListener { UndoHandler() {}
    
    public void undoableEditHappened(UndoableEditEvent ev) { undoManager.addEdit(ev.getEdit());
      undoAction.update();
      redoAction.update();
    }
  }
  
  class UndoAction extends AbstractAction {
    public UndoAction() {
      super();
      setEnabled(false);
    }
    
    public void actionPerformed(ActionEvent ev) {
      try {
        undoManager.undo();
      } catch (CannotUndoException e) {
        AuthoringTool.showErrorDialog(Messages.getString("Error__unable_to_undo_"), e);
      }
      update();
      redoAction.update();
    }
    
    protected void update() {
      if (undoManager.canUndo()) {
        setEnabled(true);
        putValue("Name", undoManager.getUndoPresentationName());
      } else {
        setEnabled(false);
        putValue("Name", Messages.getString("Undo"));
      }
    }
  }
  
  class RedoAction extends AbstractAction {
    public RedoAction() {
      super();
      setEnabled(false);
    }
    
    public void actionPerformed(ActionEvent ev) {
      try {
        undoManager.redo();
      } catch (CannotRedoException e) {
        AuthoringTool.showErrorDialog(Messages.getString("Error__unable_to_redo_"), e);
      }
      update();
      undoAction.update();
    }
    
    protected void update() {
      if (undoManager.canRedo()) {
        setEnabled(true);
        putValue("Name", undoManager.getRedoPresentationName());
      } else {
        setEnabled(false);
        putValue("Name", Messages.getString("Redo"));
      }
    }
  }
}
