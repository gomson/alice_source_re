package edu.cmu.cs.stage3.swing;

import javax.swing.JDialog;

public abstract class ContentPane extends javax.swing.JPanel { public static final int OK_OPTION = 0;
  
  public ContentPane() {}
  
  public String getTitle() { return getClass().getName(); }
  
  public boolean isReadyToDispose(int option) {
    return true;
  }
  
  public void handleDispose() { m_dialog.dispose(); }
  
  public void preDialogShow(JDialog dialog) {
    m_dialog = dialog;
    m_dialog.setLocationRelativeTo(dialog.getOwner());
  }
  
  public void postDialogShow(JDialog dialog) { m_dialog = null; }
  
  public static final int CANCEL_OPTION = 2;
  protected JDialog m_dialog;
  public void addOKActionListener(java.awt.event.ActionListener l) {}
  
  public void removeOKActionListener(java.awt.event.ActionListener l) {}
  
  public void addCancelActionListener(java.awt.event.ActionListener l) {}
  
  public void removeCancelActionListener(java.awt.event.ActionListener l) {}
  
  public void setDialogTitle(String title) {
    if (m_dialog != null) {
      m_dialog.setTitle(title);
    }
  }
  
  public void packDialog()
  {
    if (m_dialog != null) {
      m_dialog.pack();
    }
  }
}
