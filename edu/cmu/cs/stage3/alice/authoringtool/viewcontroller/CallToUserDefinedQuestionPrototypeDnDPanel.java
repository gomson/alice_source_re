package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class CallToUserDefinedQuestionPrototypeDnDPanel extends DnDGroupingPanel implements GUIElement, Releasable
{
  protected CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype;
  protected GroupingPanel subPanel = new GroupingPanel();
  protected GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0);
  protected RefreshListener refreshListener = new RefreshListener();
  protected boolean nameEditorShouldBeInvoked = false;
  protected MouseListener mouseListener = new MouseListener();
  protected ElementNamePropertyViewController nameViewController;
  
  public CallToUserDefinedQuestionPrototypeDnDPanel() {
    setBackground(AuthoringToolResources.getColor("userDefinedQuestion"));
    subPanel.setLayout(new GridBagLayout());
    subPanel.setOpaque(false);
    subPanel.setBorder(null);
    add(subPanel, "Center");
    addDragSourceComponent(subPanel);
  }
  
  public void set(CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype) {
    clean();
    
    this.callToUserDefinedQuestionPrototype = callToUserDefinedQuestionPrototype;
    setTransferable(TransferableFactory.createTransferable(callToUserDefinedQuestionPrototype));
    startListening();
    refreshGUI();
  }
  
  protected void startListening() {
    if (callToUserDefinedQuestionPrototype != null) {
      callToUserDefinedQuestionPrototype.startListening();
      UserDefinedQuestion question = callToUserDefinedQuestionPrototype.getActualQuestion();
      if (question != null) {
        requiredFormalParameters.addObjectArrayPropertyListener(refreshListener);
        keywordFormalParameters.addObjectArrayPropertyListener(refreshListener);
        Object[] variables = requiredFormalParameters.getArrayValue();
        for (int j = 0; j < variables.length; j++) {
          name.addPropertyListener(refreshListener);
        }
        variables = keywordFormalParameters.getArrayValue();
        for (int j = 0; j < variables.length; j++) {
          name.addPropertyListener(refreshListener);
        }
        name.addPropertyListener(refreshListener);
      }
    }
  }
  

  protected void stopListening()
  {
    if (callToUserDefinedQuestionPrototype != null) {
      callToUserDefinedQuestionPrototype.stopListening();
      UserDefinedQuestion question = callToUserDefinedQuestionPrototype.getActualQuestion();
      if (question != null) {
        requiredFormalParameters.removeObjectArrayPropertyListener(refreshListener);
        keywordFormalParameters.removeObjectArrayPropertyListener(refreshListener);
        Object[] variables = requiredFormalParameters.getArrayValue();
        for (int j = 0; j < variables.length; j++) {
          name.removePropertyListener(refreshListener);
        }
        variables = keywordFormalParameters.getArrayValue();
        for (int j = 0; j < variables.length; j++) {
          name.removePropertyListener(refreshListener);
        }
        name.removePropertyListener(refreshListener);
      }
    }
  }
  

  public void goToSleep()
  {
    stopListening();
  }
  
  public void wakeUp() {
    startListening();
  }
  
  public void clean() {
    stopListening();
    callToUserDefinedQuestionPrototype = null;
    setTransferable(null);
    refreshGUI();
  }
  
  public void die() {
    clean();
    subPanel.removeAll();
    removeAll();
  }
  
  public void release() {
    GUIFactory.releaseGUI(this);
  }
  
  public void editName() {
    nameEditorShouldBeInvoked = true;
    refreshGUI();
  }
  
  public void refreshGUI() {
    Component[] components = subPanel.getComponents();
    for (int i = 0; i < components.length; i++) {}
    

    if (nameViewController != null)
    {
      removeDragSourceComponent(nameViewController);
    }
    subPanel.removeAll();
    
    if (callToUserDefinedQuestionPrototype != null) {
      UserDefinedQuestion question = callToUserDefinedQuestionPrototype.getActualQuestion();
      constraints.gridx = 0;
      nameViewController = null;
      if (question != null) {
        nameViewController = GUIFactory.getElementNamePropertyViewController(question);
        nameViewController.setBorder(null);
        nameViewController.setOpaque(false);
        subPanel.add(nameViewController, constraints);
        
        addDragSourceComponent(nameViewController);
      } else {
        subPanel.add(new JLabel("<null question>"), constraints);
      }
      constraints.gridx += 1;
      subPanel.add(javax.swing.Box.createHorizontalStrut(6), constraints);
      constraints.gridx += 1;
      if ((question != null) && (requiredFormalParameters.size() > 0)) {
        Object[] variables = requiredFormalParameters.getArrayValue();
        for (int i = 0; i < variables.length; i++) {
          addTile(name.getStringValue());
          constraints.gridx += 1;
        }
      }
      

      if ((nameEditorShouldBeInvoked) && (nameViewController != null)) {
        nameViewController.editValue();
        nameEditorShouldBeInvoked = false;
      }
    } else {
      subPanel.add(new JLabel("<null callToUserDefinedQuestionPrototype>"), constraints);
    }
    
    revalidate();
    repaint();
  }
  
  public void addTile(String text) {
    GroupingPanel tilePanel = new GroupingPanel();
    tilePanel.setLayout(new BorderLayout());
    tilePanel.setBackground(AuthoringToolResources.getColor("prototypeParameter"));
    JLabel tileLabel = new JLabel(text);
    tileLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
    tilePanel.add(tileLabel, "Center");
    subPanel.add(tilePanel, constraints);
    
    addDragSourceComponent(tilePanel);
  }
  
  class MouseListener extends CustomMouseAdapter { MouseListener() {}
    
    public void singleClickResponse(MouseEvent ev) { AuthoringTool.getHack().editObject(callToUserDefinedQuestionPrototype.getActualQuestion(), CallToUserDefinedQuestionPrototypeDnDPanel.this); }
  }
  
  class RefreshListener implements edu.cmu.cs.stage3.alice.core.event.PropertyListener, edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener { RefreshListener() {}
    
    public void propertyChanging(PropertyEvent ev) {}
    
    public void propertyChanged(PropertyEvent ev) { refreshGUI(); }
    
    public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
    
    public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) { if (ev.getChangeType() == 1) {
        Variable variable = (Variable)ev.getItem();
        name.addPropertyListener(this);
      } else if (ev.getChangeType() == 3) {
        Variable variable = (Variable)ev.getItem();
        name.removePropertyListener(this);
      }
      refreshGUI();
    }
  }
}
