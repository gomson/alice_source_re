package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities;
import edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Print;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class QuestionPrintViewController extends DnDGroupingPanel implements GUIElement
{
  protected Print printStatement;
  protected JPanel subPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
  protected JLabel printPrefixLabel = new JLabel(edu.cmu.cs.stage3.lang.Messages.getString("Print__"));
  protected JLabel printSuffixLabel = new JLabel("");
  protected HashMap guiMap = new HashMap();
  protected MouseListener mouseListener = new MouseListener();
  protected PropertyListener commentedListener = new PropertyListener() {
    public void propertyChanging(PropertyEvent ev) {}
    
    public void propertyChanged(PropertyEvent ev) { if (ev.getValue().equals(Boolean.TRUE)) {
        setEnabled(false);
      } else {
        setEnabled(true);
      }
      revalidate();
      repaint();
    }
  };
  protected PropertyListener updateListener = new PropertyListener() {
    public void propertyChanging(PropertyEvent ev) {}
    
    public void propertyChanged(PropertyEvent ev) { refreshGUI(); }
  };
  
  protected boolean sleeping = false;
  
  public QuestionPrintViewController() {
    setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 2, 3, 2));
    
    subPanel.setLayout(new GridBagLayout());
    subPanel.setOpaque(false);
    subPanel.setBorder(null);
    
    addMouseListener(mouseListener);
    grip.addMouseListener(mouseListener);
    subPanel.addMouseListener(mouseListener);
  }
  
  public Print get() {
    return printStatement;
  }
  
  public void set(Print printStatement) {
    super.reset();
    
    stopListening();
    
    this.printStatement = printStatement;
    if (this.printStatement != null) {
      setTransferable(TransferableFactory.createTransferable(printStatement));
      setBackground(AuthoringToolResources.getColor("Print"));
      
      add(subPanel, "Center");
      addDragSourceComponent(subPanel);
      
      startListening();
    }
    
    refreshGUI();
  }
  
  protected void startListening() {
    if (printStatement != null) {
      printStatement.isCommentedOut.addPropertyListener(commentedListener);
      printStatement.text.addPropertyListener(updateListener);
      printStatement.object.addPropertyListener(updateListener);
    }
  }
  
  protected void stopListening() {
    if (printStatement != null) {
      printStatement.isCommentedOut.removePropertyListener(commentedListener);
      printStatement.text.removePropertyListener(updateListener);
      printStatement.object.removePropertyListener(updateListener);
    }
  }
  
  public void setEnabled(boolean b) {
    super.setEnabled(b);
    Component[] children = subPanel.getComponents();
    for (int i = 0; i < children.length; i++) {
      children[i].setEnabled(b);
    }
  }
  
  public void paintForeground(Graphics g) {
    super.paintForeground(g);
    if (printStatement.isCommentedOut.booleanValue()) {
      edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects.paintDisabledEffect(g, getBounds());
    }
  }
  
  public void goToSleep() {
    stopListening();
    sleeping = true;
  }
  
  public void wakeUp() {
    startListening();
    sleeping = false;
  }
  
  public void clean() {
    stopListening();
    printStatement = null;
    setTransferable(null);
    removeAll();
  }
  
  public void die() {
    clean();
  }
  
  public void release() {
    super.release();
    GUIFactory.releaseGUI(this);
  }
  
  public void refreshGUI() {
    subPanel.removeAll();
    if (printStatement != null) {
      String format = AuthoringToolResources.getFormat(printStatement.getClass());
      
      FormatTokenizer formatTokenizer = new FormatTokenizer(format);
      if (formatTokenizer.hasMoreTokens()) {
        printPrefixLabel.setText(formatTokenizer.nextToken());
      } else {
        printPrefixLabel.setText("");
      }
      String token = null;
      while (formatTokenizer.hasMoreTokens()) {
        token = formatTokenizer.nextToken();
      }
      if ((token != null) && ((!token.startsWith("<")) || (!token.endsWith(">")))) {
        printSuffixLabel.setText(token);
      } else {
        printSuffixLabel.setText("");
      }
      
      int i = 0;
      if (printStatement.text.get() == null) {
        boolean omitName = AuthoringToolResources.shouldGUIOmitPropertyName(printStatement.object);
        JComponent objectPropertyGui = GUIFactory.getPropertyViewController(printStatement.object, true, true, omitName, new SetPropertyImmediatelyFactory(printStatement.object));
        
        subPanel.add(printPrefixLabel, new GridBagConstraints(i++, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        subPanel.add(objectPropertyGui, new GridBagConstraints(i++, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        subPanel.add(printSuffixLabel, new GridBagConstraints(i++, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
      } else if (printStatement.object.get() == null) {
        boolean omitName = AuthoringToolResources.shouldGUIOmitPropertyName(printStatement.text);
        JComponent textPropertyGui = GUIFactory.getPropertyViewController(printStatement.text, true, true, omitName, new SetPropertyImmediatelyFactory(printStatement.text));
        
        subPanel.add(printPrefixLabel, new GridBagConstraints(i++, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        subPanel.add(textPropertyGui, new GridBagConstraints(i++, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        subPanel.add(printSuffixLabel, new GridBagConstraints(i++, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
      } else {
        boolean omitName = AuthoringToolResources.shouldGUIOmitPropertyName(printStatement.text);
        JComponent textPropertyGui = GUIFactory.getPropertyViewController(printStatement.text, true, true, omitName, new SetPropertyImmediatelyFactory(printStatement.text));
        omitName = AuthoringToolResources.shouldGUIOmitPropertyName(printStatement.text);
        JComponent objectPropertyGui = GUIFactory.getPropertyViewController(printStatement.object, true, true, omitName, new SetPropertyImmediatelyFactory(printStatement.object));
        
        subPanel.add(printPrefixLabel, new GridBagConstraints(i++, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        subPanel.add(textPropertyGui, new GridBagConstraints(i++, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 2), 0, 0));
        subPanel.add(objectPropertyGui, new GridBagConstraints(i++, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        subPanel.add(printSuffixLabel, new GridBagConstraints(i++, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
      }
      
      subPanel.add(javax.swing.Box.createGlue(), new GridBagConstraints(i++, 0, 1, 1, 1.0D, 0.0D, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
    }
    
    revalidate();
    repaint();
  }
  
  class MouseListener extends CustomMouseAdapter { MouseListener() {}
    
    public void popupResponse(MouseEvent ev) { Vector structure = ElementPopupUtilities.getDefaultStructure(printStatement);
      if ((structure != null) && (!structure.isEmpty())) {
        ElementPopupUtilities.createAndShowElementPopupMenu(printStatement, structure, QuestionPrintViewController.this, ev.getX(), ev.getY());
      }
    }
  }
}
