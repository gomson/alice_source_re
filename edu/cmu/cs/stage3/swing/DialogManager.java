package edu.cmu.cs.stage3.swing;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.EmptyStackException;
import java.util.Stack;
import javax.accessibility.AccessibleContext;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.plaf.FileChooserUI;










public class DialogManager
{
  public DialogManager() {}
  
  private static boolean isResizable = true;
  
  public static void setResize(boolean resize) { isResizable = resize; }
  
  private static Stack s_stack = new Stack();
  
  private static JDialog createModalDialog(String title) {
    Component parent;
    try { parent = (Component)s_stack.peek();
    } catch (EmptyStackException ese) { Component parent;
      parent = new Frame(Messages.getString("empty_stack"));
      parent.setVisible(true); }
    JDialog dialog;
    JDialog dialog;
    if ((parent instanceof Dialog)) {
      dialog = new JDialog((Dialog)parent, title, true);
    } else {
      dialog = new JDialog((Frame)parent, title, true);
    }
    dialog.setDefaultCloseOperation(0);
    return dialog;
  }
  
  private static void showModalDialog(JDialog dialog, boolean requiresSetLocationRelativeToOwner) {
    if (requiresSetLocationRelativeToOwner) {
      dialog.setLocationRelativeTo(dialog.getOwner());
    }
    s_stack.push(dialog);
    try {
      dialog.setVisible(true);
    } finally {
      s_stack.pop();
    }
  }
  

  private static void showModalDialog(JDialog dialog)
  {
    showModalDialog(dialog, true);
  }
  
  public static void initialize(Window root) {
    s_stack.clear();
    s_stack.push(root);
  }
  
  public static int showDialog(ContentPane contentPane) {
    JDialog dialog = createModalDialog(contentPane.getTitle());
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(contentPane, "Center");
    dialog.pack();
    









































    ReturnValueTracker returnValueTracker = new ReturnValueTracker(dialog, contentPane)
    {
      private ContentPane m_contentPane;
      private ActionListener m_okListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (m_contentPane.isReadyToDispose(0)) {
            setReturnValue(0);
            getDialog().dispose();
          }
        }
      };
      private ActionListener m_cancelListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (m_contentPane.isReadyToDispose(2)) {
            setReturnValue(2);
            getDialog().dispose();
          }
        }
      };
      







      protected void onWindowClosing(WindowEvent e)
      {
        if (m_contentPane.isReadyToDispose(2)) {
          setReturnValue(2);
          m_contentPane.handleDispose();
        }
      }
      
      public void removeListeners() {
        super.removeListeners();
        m_contentPane.removeOKActionListener(m_okListener);
        m_contentPane.removeCancelActionListener(m_cancelListener);
      }
    };
    


    if (!isResizable) {
      dialog.setResizable(false);
    }
    contentPane.preDialogShow(dialog);
    showModalDialog(dialog, false);
    contentPane.postDialogShow(dialog);
    
    returnValueTracker.removeListeners();
    
    return returnValueTracker.getReturnValue();
  }
  








  public static Color showDialog(JColorChooser colorChooser, String title, Color initialColor)
  {
    ActionListener colorTracker = new ActionListener()
    {
      Color m_color = null;
      
      public void actionPerformed(ActionEvent e) { m_color = DialogManager.this.getColor(); }
      
      public Color getColor() {
        return m_color;
      }
      
    };
    Component parent = (Component)s_stack.peek();
    JDialog dialog = JColorChooser.createDialog(parent, title, true, colorChooser, colorTracker, null);
    showModalDialog(dialog);
    return colorTracker.getColor();
  }
  
  public static int showDialog(JFileChooser fileChooser, String approveButtonText) {
    if (approveButtonText != null) {
      fileChooser.setApproveButtonText(approveButtonText);
      fileChooser.setDialogType(2);
    }
    String title = fileChooser.getUI().getDialogTitle(fileChooser);
    fileChooser.getAccessibleContext().setAccessibleDescription(title);
    
    JDialog dialog = createModalDialog(title);
    
    Container contentPane = dialog.getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(fileChooser, "Center");
    














    dialog.pack();
    































    ReturnValueTracker returnValueTracker = new ReturnValueTracker(dialog, fileChooser)
    {
      private JFileChooser m_fileChooser;
      private ActionListener m_actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String command = e.getActionCommand();
          if (command.equals("ApproveSelection")) {
            setReturnValue(0);
          } else if (command.equals("CancelSelection")) {
            setReturnValue(1);
          }
          getDialog().dispose();
        }
      };
      





      protected void onWindowClosing(WindowEvent e)
      {
        setReturnValue(1);
        getDialog().dispose();
      }
      
      public void removeListeners() {
        super.removeListeners();
        m_fileChooser.removeActionListener(m_actionListener);

      }
      

    };
    fileChooser.rescanCurrentDirectory();
    
    showModalDialog(dialog);
    
    returnValueTracker.removeListeners();
    
    return returnValueTracker.getReturnValue();
  }
  
  public static int showOpenDialog(JFileChooser fileChooser) { fileChooser.setDialogType(0);
    return showDialog(fileChooser, null);
  }
  
  public static int showSaveDialog(JFileChooser fileChooser) { fileChooser.setDialogType(1);
    return showDialog(fileChooser, null);
  }
  


  private static String getUIManagerString(Object key)
  {
    if (key.equals("OptionPane.inputDialogTitle"))
      return Messages.getString("Input");
    if (key.equals("OptionPane.messageDialogTitle"))
      return Messages.getString("Message");
    if (key.equals("OptionPane.titleText")) {
      return Messages.getString("Select_an_Option");
    }
    return "";
  }
  
  public static String showInputDialog(Object message) {
    return showInputDialog(message, getUIManagerString("OptionPane.inputDialogTitle"), 3);
  }
  
  public static String showInputDialog(Object message, Object initialSelectionValue) { return (String)showInputDialog(message, getUIManagerString("OptionPane.inputDialogTitle"), 3, null, null, initialSelectionValue); }
  
  public static String showInputDialog(Object message, String title, int messageType)
  {
    return (String)showInputDialog(message, title, messageType, null, null, null);
  }
  
  public static Object showInputDialog(Object message, String title, int messageType, Icon icon, Object[] selectionValues, Object initialSelectionValue) {
    JOptionPane pane = new JOptionPane(message, messageType, 2, icon, null, null);
    pane.setWantsInput(true);
    pane.setSelectionValues(selectionValues);
    pane.setInitialSelectionValue(initialSelectionValue);
    
    Component parent = (Component)s_stack.peek();
    pane.setComponentOrientation(parent.getComponentOrientation());
    
    JDialog dialog = pane.createDialog(parent, title);
    

    pane.selectInitialValue();
    
    showModalDialog(dialog);
    
    Object value = pane.getInputValue();
    
    if (value == JOptionPane.UNINITIALIZED_VALUE) {
      return null;
    }
    return value;
  }
  
  public static void showMessageDialog(Object message)
  {
    showMessageDialog(message, getUIManagerString("OptionPane.messageDialogTitle"), 1);
  }
  
  public static void showMessageDialog(Object message, String title, int messageType) {
    showMessageDialog(message, title, messageType, null);
  }
  
  public static void showMessageDialog(Object message, String title, int messageType, Icon icon) {
    showOptionDialog(message, title, -1, messageType, icon, null, null);
  }
  
  public static int showConfirmDialog(Object message) {
    return showConfirmDialog(message, getUIManagerString("OptionPane.titleText"), 1);
  }
  
  public static int showConfirmDialog(Object message, String title, int optionType) {
    return showConfirmDialog(message, title, optionType, 3);
  }
  
  public static int showConfirmDialog(Object message, String title, int optionType, int messageType) {
    return showConfirmDialog(message, title, optionType, messageType, null);
  }
  
  public static int showConfirmDialog(Object message, String title, int optionType, int messageType, Icon icon) {
    return showOptionDialog(message, title, optionType, messageType, icon, null, null);
  }
  
  public static int showOptionDialog(Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue) {
    JOptionPane pane = new JOptionPane(message, messageType, optionType, icon, options, initialValue);
    
    pane.setInitialValue(initialValue);
    Component parent = (Component)s_stack.peek();
    pane.setComponentOrientation(parent.getComponentOrientation());
    
    JDialog dialog = pane.createDialog(parent, title);
    pane.selectInitialValue();
    showModalDialog(dialog);
    
    Object selectedValue = pane.getValue();
    
    if (selectedValue == null)
      return -1;
    if (options == null) {
      if ((selectedValue instanceof Integer))
        return ((Integer)selectedValue).intValue();
      return -1;
    }
    int counter = 0; for (int maxCounter = options.length; counter < maxCounter; counter++) {
      if (options[counter].equals(selectedValue))
        return counter;
    }
    return -1;
  }
}
