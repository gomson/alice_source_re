package edu.cmu.cs.stage3.progress;

import java.awt.event.ActionListener;
import java.util.Vector;

public abstract class ProgressPane extends edu.cmu.cs.stage3.swing.ContentPane implements ProgressObserver
{
  private javax.swing.JLabel m_descriptionLabel;
  private javax.swing.JProgressBar m_progressBar;
  private javax.swing.JButton m_cancelButton;
  private String m_title;
  private String m_preDescription;
  private Vector m_okActionListeners = new Vector();
  private Vector m_cancelActionListeners = new Vector();
  
  private final int UNKNOWN_TOTAL_MAX = 100;
  
  private int m_total;
  private boolean m_isCanceled = false;
  private boolean m_isFinished = false;
  
  public ProgressPane(String title, String preDescription) {
    m_title = title;
    m_preDescription = preDescription;
    
    m_descriptionLabel = new javax.swing.JLabel();
    
    m_progressBar = new javax.swing.JProgressBar();
    m_progressBar.setPreferredSize(new java.awt.Dimension(240, 16));
    
    m_cancelButton = new javax.swing.JButton(edu.cmu.cs.stage3.lang.Messages.getString("Cancel"));
    m_cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        ProgressPane.this.onCancel();
      }
      
    });
    setLayout(new java.awt.GridBagLayout());
    
    java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
    
    anchor = 18;
    fill = 1;
    gridwidth = 0;
    
    weightx = 1.0D;
    
    insets.top = 10;
    insets.left = 10;
    insets.right = 10;
    add(m_descriptionLabel, gbc);
    
    insets.top = 0;
    insets.bottom = 10;
    add(m_progressBar, gbc);
    
    weighty = 1.0D;
    add(new javax.swing.JLabel(), gbc);
    weighty = 0.0D;
    
    weightx = 0.0D;
    fill = 0;
    anchor = 10;
    add(m_cancelButton, gbc);
  }
  




  public void handleDispose()
  {
    onCancel();
  }
  

  protected abstract void construct()
    throws ProgressCancelException;
  
  public void preDialogShow(javax.swing.JDialog dialog)
  {
    super.preDialogShow(dialog);
    new Thread(new Runnable() {
      public void run() {
        try {
          construct();
          fireOKActionListeners();
        } catch (ProgressCancelException pce) {
          fireCancelActionListeners();
        }
      }
    })
    







      .start();
  }
  
  public boolean isCancelEnabled() {
    return m_cancelButton.isEnabled();
  }
  
  public void setIsCancelEnabled(boolean isCancelEnabled) { m_cancelButton.setEnabled(isCancelEnabled); }
  

  public boolean isReadyToDispose(int option)
  {
    if (m_isFinished) {
      return true;
    }
    if (isCancelEnabled()) {
      return true;
    }
    if (option == 2) {
      return false;
    }
    return true;
  }
  


  public String getTitle()
  {
    return m_title;
  }
  
  public void addOKActionListener(ActionListener l)
  {
    m_okActionListeners.addElement(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    m_okActionListeners.removeElement(l);
  }
  
  public void addCancelActionListener(ActionListener l) {
    m_cancelActionListeners.addElement(l);
  }
  

  public void removeCancelActionListener(ActionListener l) { m_cancelActionListeners.removeElement(l); }
  
  public void progressBegin(int total) {
    m_descriptionLabel.setText(m_preDescription);
    progressUpdateTotal(total);
    m_isCanceled = false;
    m_isFinished = false;
  }
  
  public void progressUpdateTotal(int total) { m_total = total;
    if (m_total == -1) {
      m_progressBar.setMaximum(100);
    } else {
      m_progressBar.setMaximum(m_total);
    }
  }
  
  public void progressUpdate(final int current, final String description) throws ProgressCancelException {
    if (m_isCanceled) {
      m_isCanceled = false;
      throw new ProgressCancelException();
    }
    
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (m_preDescription != null) {
          if (description != null) {
            m_descriptionLabel.setText(m_preDescription + description);
          } else {
            m_descriptionLabel.setText(m_preDescription);
          }
        }
        
        if (m_total == -1) {
          m_progressBar.setValue(current % 100);
        } else {
          m_progressBar.setValue(current);
        }
      }
    });
  }
  
  public void progressEnd() {}
  
  private void onCancel()
  {
    m_isCanceled = true;
  }
  
  private void fireActionListeners(final Vector actionListeners, final java.awt.event.ActionEvent e) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        for (int i = 0; i < actionListeners.size(); i++) {
          ActionListener l = (ActionListener)actionListeners.elementAt(i);
          l.actionPerformed(e);
        }
      }
    });
  }
  
  protected void fireOKActionListeners() { fireActionListeners(m_okActionListeners, new java.awt.event.ActionEvent(this, 1001, edu.cmu.cs.stage3.lang.Messages.getString("OK"))); }
  
  protected void fireCancelActionListeners() {
    fireActionListeners(m_cancelActionListeners, new java.awt.event.ActionEvent(this, 1001, edu.cmu.cs.stage3.lang.Messages.getString("Cancel")));
  }
}
