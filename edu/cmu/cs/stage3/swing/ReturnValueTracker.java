package edu.cmu.cs.stage3.swing;

import javax.swing.JDialog;

abstract class ReturnValueTracker
{
  private JDialog m_dialog;
  private int m_returnValue;
  private java.awt.event.WindowListener m_windowListener = new java.awt.event.WindowAdapter()
  {

    public void windowClosing(java.awt.event.WindowEvent e) { onWindowClosing(e); }
  };
  
  protected abstract void onWindowClosing(java.awt.event.WindowEvent paramWindowEvent);
  
  public ReturnValueTracker(JDialog dialog, int initialReturnValue) { m_dialog = dialog;
    m_returnValue = initialReturnValue;
    m_dialog.addWindowListener(m_windowListener);
  }
  
  public void setReturnValue(int returnValue) { m_returnValue = returnValue; }
  
  public int getReturnValue() {
    return m_returnValue;
  }
  
  protected JDialog getDialog() { return m_dialog; }
  
  public void removeListeners()
  {
    m_dialog.removeWindowListener(m_windowListener);
  }
}
