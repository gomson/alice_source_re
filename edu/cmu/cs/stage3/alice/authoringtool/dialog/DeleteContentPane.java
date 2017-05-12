package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable;
import edu.cmu.cs.stage3.alice.authoringtool.util.HighlightingGlassPane;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.question.PropertyValue;
import edu.cmu.cs.stage3.alice.core.reference.ObjectArrayPropertyReference;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.Criterion;
import edu.cmu.cs.stage3.util.HowMuch;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListModel;

public class DeleteContentPane extends edu.cmu.cs.stage3.swing.ContentPane implements edu.cmu.cs.stage3.alice.core.event.PropertyListener, edu.cmu.cs.stage3.alice.core.event.ChildrenListener
{
  public static final int LESS_DETAIL_MODE = 0;
  public static final int MORE_DETAIL_MODE = 1;
  protected int mode = -1;
  protected static AuthoringTool authoringTool;
  protected edu.cmu.cs.stage3.alice.authoringtool.util.ImagePanel errorIconPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.ImagePanel();
  
  protected ElementPopupUtilities.DeleteRunnable deleteRunnable;
  protected HighlightingGlassPane glassPane;
  protected Element danglingElementToClear;
  
  public static void showDeleteDialog(ElementPopupUtilities.DeleteRunnable deleteRunnable, AuthoringTool authoringTool)
  {
    DeleteContentPane dcp = new DeleteContentPane(authoringTool);
    dcp.setDeleteRunnable(deleteRunnable);
    dcp.refresh();
    if (edu.cmu.cs.stage3.swing.DialogManager.showDialog(dcp) == 0) {
      deleteRunnable.run();
    }
  }
  














  public DeleteContentPane(AuthoringTool authoringTool)
  {
    authoringTool = authoringTool;
    jbInit();
    java.net.URL errorImageResources = edu.cmu.cs.stage3.alice.authoringtool.JAlice.class
      .getResource("images/error.gif");
    errorIconPanel.setImage(java.awt.Toolkit.getDefaultToolkit()
      .createImage(errorImageResources));
    iconPanel.add(errorIconPanel, "Center");
    
    messageArea.setLineWrap(true);
    messageArea.setWrapStyleWord(true);
    messageArea.setOpaque(false);
    
    referencesList
      .setCellRenderer(new edu.cmu.cs.stage3.alice.authoringtool.util.PropertyReferenceListCellRenderer());
    referencesList
      .addListSelectionListener(new ReferencesSelectionListener());
    glassPane = new HighlightingGlassPane(
      authoringTool);
    
    setPreferredSize(new java.awt.Dimension(600, 300));
  }
  
  public String getTitle() {
    return Messages.getString("Alice___Can_t_Delete");
  }
  
  public void addOKActionListener(ActionListener l) {
    okayButton.addActionListener(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    okayButton.removeActionListener(l);
  }
  
  public void addCancelActionListener(ActionListener l) {
    cancelButton.addActionListener(l);
  }
  
  public void removeCancelActionListener(ActionListener l) {
    cancelButton.removeActionListener(l);
  }
  
  public void postDialogShow(javax.swing.JDialog dialog) {
    glassPane.setHighlightingEnabled(false);
    stopListening();
    TEMP_checkForListening();
    super.postDialogShow(dialog);
  }
  
  public void setDeleteRunnable(ElementPopupUtilities.DeleteRunnable deleteRunnable)
  {
    this.deleteRunnable = deleteRunnable;
  }
  
  public void TEMP_checkForListening() {
    Element[] elements = authoringTool
      .getWorld().getDescendants();
    for (int i = 0; i < elements.length; i++) {
      edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = elements[i]
        .getChildrenListeners();
      for (int j = 0; j < childrenListeners.length; j++) {
        if (childrenListeners[j] == this) {
          System.out.println(Messages.getString("child_listener__") + 
            elements[i]);
        }
      }
      Property[] properties = elements[i]
        .getProperties();
      for (int j = 0; j < properties.length; j++) {
        edu.cmu.cs.stage3.alice.core.event.PropertyListener[] propertyListeners = properties[j]
          .getPropertyListeners();
        for (int k = 0; k < propertyListeners.length; k++) {
          if (propertyListeners[k] == this) {
            System.out.println(
              Messages.getString("property_listener__") + 
              properties[j]);
          }
        }
      }
    }
  }
  
  public void startListening() {
    ListModel list = referencesList.getModel();
    for (int i = 0; i < list.getSize(); i++) {
      if ((list.getElementAt(i) instanceof PropertyReference)) {
        PropertyReference reference = (PropertyReference)list
          .getElementAt(i);
        Element source = reference
          .getProperty().getOwner();
        listenUpToRootElement(source);
        if ((source instanceof PropertyValue)) {
          Property[] properties = source
            .getParent().getProperties();
          for (int p = 0; p < properties.length; p++) {
            if (properties[p].get() == source) {
              properties[p].addPropertyListener(this);
            }
          }
        } else if ((reference instanceof ObjectArrayPropertyReference)) {
          ObjectArrayPropertyReference oAPR = (ObjectArrayPropertyReference)reference;
          PropertyReference[] references = oAPR
            .getReference()
            .getPropertyReferencesTo(
            deleteRunnable.getElement(), 
            HowMuch.INSTANCE_AND_ALL_DESCENDANTS, 
            true, true);
          if ((references != null) || (references.length > 0)) {
            reference.getProperty().addPropertyListener(this);
          }
        }
        else {
          reference.getProperty().addPropertyListener(this);
        }
      }
    }
  }
  

  public void stopListening()
  {
    ListModel list = referencesList.getModel();
    for (int i = 0; i < list.getSize(); i++) {
      if ((list.getElementAt(i) instanceof PropertyReference)) {
        PropertyReference reference = (PropertyReference)list
          .getElementAt(i);
        Element source = reference
          .getProperty().getOwner();
        stopListeningUpToRootElement(source);
        if ((source instanceof PropertyValue)) {
          if (source.getParent() != null) {
            Property[] properties = source
              .getParent().getProperties();
            for (int p = 0; p < properties.length; p++) {
              if (properties[p].get() == source) {
                properties[p].removePropertyListener(this);
              }
            }
          }
        } else if ((reference instanceof ObjectArrayPropertyReference))
        {
          PropertyReference[] references = reference
            .getProperty()
            .getOwner()
            .getPropertyReferencesTo(
            deleteRunnable.getElement(), 
            HowMuch.INSTANCE_AND_ALL_DESCENDANTS, 
            true, true);
          if ((references != null) || (references.length > 0)) {
            reference.getProperty().removePropertyListener(this);
          }
        }
        else {
          reference.getProperty().removePropertyListener(this);
        }
      }
    }
  }
  



  public void propertyChanging(PropertyEvent propertyEvent) {}
  


  public void propertyChanged(PropertyEvent propertyEvent)
  {
    refresh();
  }
  
  public void childrenChanging(ChildrenEvent childrenEvent)
  {
    danglingElementToClear = childrenEvent.getParent();
  }
  

  public void childrenChanged(ChildrenEvent childrenEvent)
  {
    stopListeningUpToRootElement(danglingElementToClear);
    refresh();
  }
  
  protected void listenUpToRootElement(Element current)
  {
    if (current != null) {
      boolean alreadyChildrenListening = false;
      edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = current
        .getChildrenListeners();
      for (int j = 0; j < childrenListeners.length; j++) {
        if (childrenListeners[j] == this) {
          alreadyChildrenListening = true;
        }
      }
      if (!alreadyChildrenListening) {
        current.addChildrenListener(this);
      }
      Property[] properties = current
        .getProperties();
      for (int i = 0; i < properties.length; i++) {
        edu.cmu.cs.stage3.alice.core.event.PropertyListener[] propListeners = properties[i]
          .getPropertyListeners();
        boolean alreadyPropListening = false;
        for (int j = 0; j < propListeners.length; j++) {
          if (propListeners[j] == this) {
            alreadyPropListening = true;
          }
        }
        if (!alreadyPropListening) {
          properties[i].addPropertyListener(this);
        }
      }
      listenUpToRootElement(current.getParent());
    }
  }
  
  protected void stopListeningUpToRootElement(Element current)
  {
    if (current != null) {
      boolean alreadyChildrenListening = false;
      edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childrenListeners = current
        .getChildrenListeners();
      for (int j = 0; j < childrenListeners.length; j++) {
        if (childrenListeners[j] == this) {
          alreadyChildrenListening = true;
        }
      }
      if (alreadyChildrenListening) {
        current.removeChildrenListener(this);
      }
      Property[] properties = current
        .getProperties();
      for (int i = 0; i < properties.length; i++) {
        edu.cmu.cs.stage3.alice.core.event.PropertyListener[] propListeners = properties[i]
          .getPropertyListeners();
        boolean alreadyPropListening = false;
        for (int j = 0; j < propListeners.length; j++) {
          if (propListeners[j] == this) {
            alreadyPropListening = true;
          }
        }
        if (alreadyPropListening) {
          properties[i].removePropertyListener(this);
        }
      }
      stopListeningUpToRootElement(current.getParent());
    }
  }
  
  public void refresh() {
    if (deleteRunnable != null) {
      Element element = deleteRunnable
        .getElement();
      String elementRepr = 
        AuthoringToolResources.getReprForValue(element);
      
      PropertyReference[] references = element
        .getRoot()
        .getPropertyReferencesTo(
        element, 
        HowMuch.INSTANCE_AND_ALL_DESCENDANTS, 
        true, true);
      
      if (references.length > 0)
      {
        AuthoringToolResources.garbageCollectIfPossible(references);
        references = element
          .getRoot()
          .getPropertyReferencesTo(
          element, 
          HowMuch.INSTANCE_AND_ALL_DESCENDANTS, 
          true, true);
      }
      
      stopListening();
      referencesList.setListData(references);
      startListening();
      
      if (references.length > 0) {
        okayButton.setEnabled(false);
        removeReferenceButton.setEnabled(true);
        removeAllReferenceButton.setEnabled(true);
        messageArea
          .setText(elementRepr + " " + 
          Messages.getString("cannot_be_deleted_because_other_parts_of_the_World_contain_references_to_it__You_will_need_to_remove_these_references_in_order_to_delete_") + 
          elementRepr + 
          ".\n\n" + 
          
          Messages.getString("Select_each_reference_below__and_either_remove_the_reference_manually__or_click_the_Remove_Reference_button_to_have_the_reference_removed_by_the_system_"));
        referencesList.setSelectedIndex(0);
      } else {
        okayButton.setEnabled(true);
        removeReferenceButton.setEnabled(false);
        removeAllReferenceButton.setEnabled(false);
        messageArea
          .setText(
          Messages.getString("All_references_have_now_been_deleted___Click_Okay_to_delete_") + 
          elementRepr + ".");
        setDialogTitle(Messages.getString("Alice___Can_Delete"));
      }
    }
  }
  
  public static String getHighlightID(PropertyReference reference)
  {
    String highlightID = null;
    if (reference != null)
    {
      Element source = reference
        .getProperty().getOwner();
      World world = authoringTool.getWorld();
      
      if (source == world) {
        highlightID = 
          "details<>:viewController<>:property<" + reference.getProperty().getName() + ">";
      }
      
      if ((highlightID == null) && 
        ((source instanceof Model))) {
        highlightID = 
        
          "details<" + source.getKey(world) + ">:viewController<>:property<" + reference.getProperty().getName() + ">";
      }
      

      if ((highlightID == null) && 
        ((source instanceof edu.cmu.cs.stage3.alice.core.Variable))) {
        Element sourceParent = source
          .getParent();
        
        if (sourceParent == world) {
          highlightID = 
          
            "details<>:viewController<>:variable<" + reference.getProperty().getOwner().getKey(world) + ">";
        } else if ((sourceParent instanceof Model)) {
          highlightID = 
          


            "details<" + sourceParent.getKey(world) + ">:viewController<>:variable<" + reference.getProperty().getOwner().getKey(sourceParent) + ">";
        } else if (((sourceParent instanceof CallToUserDefinedResponse)) || 
          ((sourceParent instanceof UserDefinedResponse))) {
          Element[] userDefinedResponses = world
            .getDescendants(UserDefinedResponse.class);
          for (int i = 0; i < userDefinedResponses.length; i++)
          {
            if ((userDefinedResponses[i] == sourceParent) || 
            
              (userDefinedResponses[i].isAncestorOf(sourceParent))) {
              highlightID = "editors:element<" + userDefinedResponses[i].getKey(world) + ">:elementTile<" + source.getKey(world) + ">:property<" + reference.getProperty().getName() + ">";
              break;
            }
          }
          if (highlightID == null) {
            Element[] behaviors = world
              .getDescendants(Behavior.class);
            for (int i = 0; i < behaviors.length; i++)
            {
              if (behaviors[i].isAncestorOf(sourceParent)) {
                highlightID = 
                


                  "behaviors:elementTile<" + source.getKey(world) + ">:property<" + reference.getProperty().getName() + ">";
                break;
              }
            }
          }
        }
      }
      

      if ((highlightID == null) && 
        ((source instanceof Collection))) {
        Element sourceParent = source
          .getParent();
        
        if ((sourceParent instanceof edu.cmu.cs.stage3.alice.core.Variable)) {
          Element variableParent = sourceParent
            .getParent();
          if (variableParent == world) {
            highlightID = 
              "details<>:viewController<>:variable<" + sourceParent.getKey(world) + ">";
          } else if ((variableParent instanceof Model)) {
            highlightID = 
            

              "details<" + variableParent.getKey(world) + ">:viewController<>:variable<" + sourceParent.getKey(variableParent) + ">";
          }
        }
      }
      

      if ((highlightID == null) && 
        ((source instanceof edu.cmu.cs.stage3.alice.core.List))) {
        Element sourceParent = source
          .getParent();
        
        if (sourceParent == world) {
          highlightID = 
          
            "details<>:viewController<>:variable<" + reference.getProperty().getOwner().getKey(world) + ">";
        } else if ((sourceParent instanceof Model)) {
          highlightID = 
          


            "details<" + sourceParent.getKey(world) + ">:viewController<>:variable<" + reference.getProperty().getOwner().getKey(sourceParent) + ">";
        }
      }
      


      if (highlightID == null) {
        Criterion userDefinedResponsesCriterion = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
          UserDefinedResponse.class);
        Criterion userDefinedQuestionsCriterion = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
          edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion.class);
        Criterion[] searchCriterion = {
          userDefinedResponsesCriterion, 
          userDefinedQuestionsCriterion };
        Element[] userDefinedResponsesAndQuestions = world
          .search(new edu.cmu.cs.stage3.util.criterion.MatchesAnyCriterion(
          searchCriterion));
        Element sourceParent = source
          .getParent();
        for (int i = 0; i < userDefinedResponsesAndQuestions.length; i++)
        {
          if (userDefinedResponsesAndQuestions[i].isAncestorOf(source)) {
            highlightID = 
            
              "editors:element<" + userDefinedResponsesAndQuestions[i].getKey(world) + ">";
            break;
          }
        }
        if (highlightID == null) {
          Element[] behaviors = world
            .getDescendants(Behavior.class);
          for (int i = 0; i < behaviors.length; i++) {
            if (behaviors[i].isAncestorOf(source)) {
              highlightID = "behaviors";
              break;
            }
          }
        }
        if (((reference.getProperty() instanceof edu.cmu.cs.stage3.alice.core.property.UserDefinedResponseProperty)) || 
          ((reference.getProperty() instanceof edu.cmu.cs.stage3.alice.core.property.UserDefinedQuestionProperty)) || 
          ((source instanceof PropertyValue)))
        {

          Property[] properties = sourceParent
            .getProperties();
          boolean setIt = false;
          for (int p = 0; p < properties.length; p++) {
            if (properties[p].get() == source) {
              highlightID = 
              

                highlightID + ":elementTile<" + sourceParent.getKey(world) + ">:property<" + properties[p].getName() + ">";
              setIt = true;
            }
          }
          if (!setIt) {
            highlightID = 
              highlightID + ":elementTile<" + source.getKey(world) + ">";
          }
        } else {
          highlightID = 
          
            highlightID + ":elementTile<" + source.getKey(world) + ">:property<" + reference.getProperty().getName() + ">";
        }
      }
    }
    


    return highlightID;
  }
  
  public static javax.swing.ImageIcon getDeleteIcon(PropertyReference reference)
  {
    javax.swing.ImageIcon toReturn = new javax.swing.ImageIcon();
    if (reference != null) {
      String id = getHighlightID(reference);
      java.awt.Image image = authoringTool.getImageForID(id);
      if (image != null) {
        toReturn.setImage(image);
      }
    }
    return toReturn;
  }
  
  public static String getDeleteString(PropertyReference reference)
  {
    String highlightID = null;
    if (reference != null) {
      Element source = reference
        .getProperty().getOwner();
      World world = authoringTool.getWorld();
      String ourName = getReferencename.getStringValue();
      
      if (source == world) {
        highlightID = 
        
          Messages.getString("The_World_s_") + reference.getProperty().getName() + " " + Messages.getString("is_set_to_") + ourName;
      }
      
      if ((highlightID == null) && 
        ((source instanceof Model))) {
        highlightID = 
        


          Messages.getString("The_") + name.getStringValue() + Messages.getString("_s_") + reference.getProperty().getName() + " " + Messages.getString("is_set_to_") + ourName;
      }
      

      if ((highlightID == null) && 
        ((source instanceof edu.cmu.cs.stage3.alice.core.Variable))) {
        Element sourceParent = source
          .getParent();
        if (sourceParent == world) {
          highlightID = 
          


            Messages.getString("The_World_s_variable__") + getPropertygetOwnername.getStringValue() + " " + Messages.getString("is_set_to_") + ourName;
        } else if ((sourceParent instanceof Model)) {
          highlightID = 
          



            Messages.getString("The_") + name.getStringValue() + Messages.getString("_s_variable__") + getPropertygetOwnername.getStringValue() + " " + Messages.getString("is_set_to_") + ourName;
        } else if (((sourceParent instanceof CallToUserDefinedResponse)) || 
          ((sourceParent instanceof UserDefinedResponse))) {
          Element[] userDefinedResponses = world
            .getDescendants(UserDefinedResponse.class);
          for (int i = 0; i < userDefinedResponses.length; i++)
          {
            if ((userDefinedResponses[i] == sourceParent) || 
            
              (userDefinedResponses[i].isAncestorOf(sourceParent))) {
              highlightID = 
              




                Messages.getString("The_method__") + userDefinedResponses[i].getKey() + " " + Messages.getString("contains_") + source.getRepr() + " " + Messages.getString("which_is_set_to_") + ourName;
              break;
            }
          }
          if (highlightID == null) {
            Element[] behaviors = world
              .getDescendants(Behavior.class);
            for (int i = 0; i < behaviors.length; i++)
            {
              if (behaviors[i].isAncestorOf(sourceParent)) {
                highlightID = 
                


                  Messages.getString("The_behavior_") + source.getRepr() + Messages.getString("__property__") + reference.getProperty().getName();
                break;
              }
            }
          }
        }
      }
      

      if ((highlightID == null) && 
        ((source instanceof Collection))) {
        Element sourceParent = source
          .getParent();
        if ((sourceParent instanceof edu.cmu.cs.stage3.alice.core.Variable)) {
          Element variableParent = sourceParent
            .getParent();
          if (variableParent == world) {
            highlightID = 
            






              Messages.getString("The_World_s_variable__") + name.getStringValue() + " " + Messages.getString("element_number_") + ((Collection)source).getIndexOfChild(reference.getReference()) + " " + Messages.getString("is_set_to_") + ourName;
          } else if ((variableParent instanceof Model)) {
            highlightID = 
            

              Messages.getString("The_") + name.getStringValue() + Messages.getString("_s_variable__") + name.getStringValue() + " " + Messages.getString("element_number_") + ((Collection)source).getIndexOfChild(reference.getReference()) + " " + Messages.getString("is_set_to_") + ourName;
          }
        }
      }
      


      if (highlightID == null) {
        Criterion userDefinedResponsesCriterion = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
          UserDefinedResponse.class);
        Criterion userDefinedQuestionsCriterion = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(
          edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion.class);
        Criterion[] searchCriterion = {
          userDefinedResponsesCriterion, 
          userDefinedQuestionsCriterion };
        Element[] userDefinedResponsesAndQuestions = world
          .search(new edu.cmu.cs.stage3.util.criterion.MatchesAnyCriterion(
          searchCriterion));
        Element sourceParent = source
          .getParent();
        for (int i = 0; i < userDefinedResponsesAndQuestions.length; i++)
        {
          if (userDefinedResponsesAndQuestions[i].isAncestorOf(source)) {
            highlightID = 
            
              Messages.getString("The_method__") + userDefinedResponsesAndQuestions[i].getKey() + "\"";
            break;
          }
        }
        if (highlightID == null) {
          Element[] behaviors = world
            .getDescendants(Behavior.class);
          for (int i = 0; i < behaviors.length; i++) {
            if (behaviors[i].isAncestorOf(source)) {
              highlightID = Messages.getString("The_behavior");
              break;
            }
          }
        }
        if (((reference.getProperty() instanceof edu.cmu.cs.stage3.alice.core.property.UserDefinedResponseProperty)) || 
          ((reference.getProperty() instanceof edu.cmu.cs.stage3.alice.core.property.UserDefinedQuestionProperty)) || 
          ((source instanceof PropertyValue)))
        {

          Property[] properties = sourceParent
            .getProperties();
          boolean setIt = false;
          for (int p = 0; p < properties.length; p++) {
            if (properties[p].get() == source) {
              highlightID = 
              




                highlightID + " " + Messages.getString("has_a_line_of_code_") + AuthoringToolResources.getReprForValue(source.getClass()) + " " + Messages.getString("who_s_") + properties[p].getName() + " " + Messages.getString("is_set_to_") + ourName;
              setIt = true;
            }
          }
          if (!setIt) {
            highlightID = 
              highlightID + ":elementTile<" + source.getKey(world) + ">";
          }
        } else {
          highlightID = 
          
            highlightID + ":elementTile<" + source.getKey(world) + ">:property<" + reference.getProperty().getName() + ">";
        }
      }
    }
    


    return highlightID;
  }
  
  class ReferencesSelectionListener implements javax.swing.event.ListSelectionListener {
    ReferencesSelectionListener() {}
    
    public void valueChanged(javax.swing.event.ListSelectionEvent ev) { String highlightID = null;
      PropertyReference reference = (PropertyReference)referencesList
        .getSelectedValue();
      highlightID = DeleteContentPane.getHighlightID(reference);
      glassPane.setHighlightID(highlightID);
      if (highlightID != null) {
        glassPane.setHighlightingEnabled(true);
      }
    }
  }
  
  void removeReferenceButton_actionPerformed(ActionEvent e) {
    PropertyReference reference = (PropertyReference)referencesList
      .getSelectedValue();
    Element source = reference.getProperty()
      .getOwner();
    Element sourceParent = source.getParent();
    if (reference != null) {
      if (((source instanceof CallToUserDefinedResponse)) || 
        ((source instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion)) || 
        ((source instanceof PropertyValue)))
      {

        if ((sourceParent instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse)) {
          source.removeFromParent();
        } else {
          Property[] properties = sourceParent
            .getProperties();
          for (int p = 0; p < properties.length; p++) {
            if (properties[p].get() == source) {
              properties[p].removePropertyListener(this);
              properties[p]
                .set(
                AuthoringToolResources.getDefaultValueForClass(properties[p]
                .getValueClass()));
            }
          }
        }
      }
      else if ((reference instanceof ObjectArrayPropertyReference)) {
        ObjectArrayPropertyReference oAPR = (ObjectArrayPropertyReference)reference;
        oAPR.getObjectArrayProperty().set(oAPR.getIndex(), null);
        PropertyReference[] references = oAPR
          .getProperty()
          .getOwner()
          .getPropertyReferencesTo(
          deleteRunnable.getElement(), 
          HowMuch.INSTANCE_AND_ALL_DESCENDANTS, 
          true, true);
        if ((references == null) || (references.length < 1)) {
          reference.getProperty().removePropertyListener(this);
        }
      }
      else {
        reference.getProperty().removePropertyListener(this);
        reference
          .getProperty()
          .set(
          AuthoringToolResources.getDefaultValueForClass(reference
          .getProperty().getValueClass()));
      }
      














      refresh();
    }
  }
  
  void removeAllReferenceButton_actionPerformed(ActionEvent e)
  {
    stopListening();
    for (int i = 0; i < referencesList.getModel().getSize(); i++) {
      PropertyReference reference = (PropertyReference)referencesList
        .getModel().getElementAt(i);
      Element source = reference
        .getProperty().getOwner();
      Element sourceParent = source
        .getParent();
      if (reference != null) {
        if (((source instanceof CallToUserDefinedResponse)) || 
          ((source instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion)) || 
          ((source instanceof PropertyValue)))
        {

          if ((sourceParent instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse)) {
            source.removeFromParent();
          } else {
            Property[] properties = sourceParent
              .getProperties();
            for (int p = 0; p < properties.length; p++) {
              if (properties[p].get() == source) {
                properties[p].removePropertyListener(this);
                properties[p]
                  .set(
                  AuthoringToolResources.getDefaultValueForClass(properties[p]
                  .getValueClass()));
              }
            }
          }
        }
        else if ((reference instanceof ObjectArrayPropertyReference)) {
          ObjectArrayPropertyReference oAPR = (ObjectArrayPropertyReference)reference;
          oAPR.getObjectArrayProperty().set(oAPR.getIndex(), null);
          PropertyReference[] otherReferences = oAPR
            .getProperty()
            .getOwner()
            .getPropertyReferencesTo(
            deleteRunnable.getElement(), 
            HowMuch.INSTANCE_AND_ALL_DESCENDANTS, 
            true, true);
          if ((otherReferences == null) || (otherReferences.length < 1)) {
            reference.getProperty().removePropertyListener(this);
          }
        } else {
          reference.getProperty().removePropertyListener(this);
          reference
            .getProperty()
            .set(
            AuthoringToolResources.getDefaultValueForClass(reference
            .getProperty().getValueClass()));
        }
      }
    }
    refresh();
  }
  




  BorderLayout borderLayout1 = new BorderLayout();
  JPanel buttonPanel = new JPanel();
  JPanel mainPanel = new JPanel();
  JButton okayButton = new JButton();
  JButton removeReferenceButton = new JButton();
  JButton removeAllReferenceButton = new JButton();
  JPanel iconPanel = new JPanel();
  JPanel messagePanel = new JPanel();
  JPanel referencesPanel = new JPanel();
  java.awt.GridBagLayout gridBagLayout1 = new java.awt.GridBagLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  BorderLayout borderLayout3 = new BorderLayout();
  BorderLayout borderLayout4 = new BorderLayout();
  javax.swing.JScrollPane referencesScrollPane = new javax.swing.JScrollPane();
  JTextArea messageArea = new JTextArea();
  javax.swing.border.Border border1;
  javax.swing.border.Border border2;
  JButton cancelButton = new JButton();
  java.awt.GridBagLayout gridBagLayout2 = new java.awt.GridBagLayout();
  JList referencesList = new JList();
  
  private void jbInit() {
    border1 = javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10);
    border2 = javax.swing.BorderFactory.createEmptyBorder(0, 10, 10, 10);
    setLayout(borderLayout1);
    okayButton.setText(Messages.getString("OK"));
    removeReferenceButton.setText(Messages.getString("Remove_Reference"));
    removeReferenceButton
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          removeReferenceButton_actionPerformed(e);
        }
        
      });
    removeAllReferenceButton.setText(
      Messages.getString("Remove_All_References"));
    removeAllReferenceButton
      .addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          removeAllReferenceButton_actionPerformed(e);
        }
        
      });
    mainPanel.setLayout(gridBagLayout1);
    iconPanel.setLayout(borderLayout2);
    messagePanel.setLayout(borderLayout3);
    referencesPanel.setLayout(borderLayout4);
    messageArea.setText(Messages.getString("Message_goes_here_"));
    mainPanel.setBorder(border1);
    buttonPanel.setBorder(border2);
    buttonPanel.setLayout(gridBagLayout2);
    cancelButton.setText(Messages.getString("Cancel"));
    referencesList
      .setSelectionMode(0);
    add(buttonPanel, "South");
    add(mainPanel, "Center");
    buttonPanel.add(okayButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 
      0.0D, 13, 0, 
      new Insets(8, 8, 8, 4), 0, 0));
    buttonPanel.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0D, 
      0.0D, 13, 0, 
      new Insets(8, 0, 8, 0), 0, 0));
    buttonPanel.add(removeReferenceButton, new GridBagConstraints(0, 0, 1, 
      1, 1.0D, 0.0D, 17, 0, 
      new Insets(8, 0, 8, 8), 0, 0));
    buttonPanel.add(removeAllReferenceButton, new GridBagConstraints(0, 1, 
      1, 1, 1.0D, 0.0D, 17, 
      0, new Insets(8, 0, 8, 8), 0, 0));
    mainPanel.add(iconPanel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 
      18, 0, 
      new Insets(10, 10, 10, 10), 0, 0));
    mainPanel.add(messagePanel, new GridBagConstraints(1, 0, 1, 1, 1.0D, 
      0.0D, 10, 2, 
      new Insets(0, 0, 0, 0), 0, 0));
    messagePanel.add(messageArea, "Center");
    mainPanel.add(referencesPanel, new GridBagConstraints(0, 1, 2, 1, 1.0D, 
      1.0D, 10, 1, 
      new Insets(10, 0, 0, 0), 0, 0));
    referencesPanel.add(referencesScrollPane, "Center");
    referencesScrollPane.getViewport().add(referencesList, null);
  }
}
