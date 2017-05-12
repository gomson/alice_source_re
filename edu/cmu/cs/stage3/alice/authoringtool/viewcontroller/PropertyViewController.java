package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities;
import edu.cmu.cs.stage3.alice.core.Array;
import edu.cmu.cs.stage3.alice.core.CopyFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.question.PropertyValue;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.vecmath.Vector3d;

public abstract class PropertyViewController extends GroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable
{
  protected static RefreshThread refreshThread = new RefreshThread();
  protected static java.util.Set propertyViewControllersToRefresh = new java.util.HashSet();
  
  public static int created = 0;
  public static int released = 0;
  protected Property property;
  
  static { refreshThread.start(); }
  
  public static RefreshThread getRefreshThread()
  {
    return refreshThread;
  }
  

  protected boolean allowExpressions;
  protected boolean includeDefaults;
  protected boolean includeOther;
  protected boolean omitPropertyName;
  protected static boolean handlingQuestionAlready = false;
  

  protected PopupItemFactory factory;
  

  private boolean popupEnabled = false;
  protected boolean editingEnabled = true;
  protected boolean sleeping = false;
  protected Vector popupStructure;
  protected javax.swing.JButton popupButton = new javax.swing.JButton(new javax.swing.ImageIcon(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getResource("images/popupArrow.gif")));
  protected edu.cmu.cs.stage3.alice.core.event.PropertyListener propertyListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
    protected long lastTime = System.currentTimeMillis();
    
    public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {
      if ((ev.getProperty() == property) && 
        ((ev.getValue() instanceof Element))) {
        getValuename.removePropertyListener(this);
      }
    }
    
    public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev)
    {
      if ((ev.getProperty() == property) && 
        ((ev.getValue() instanceof Element))) {
        getValuename.addPropertyListener(this);
      }
      


      synchronized (PropertyViewController.propertyViewControllersToRefresh) {
        PropertyViewController.propertyViewControllersToRefresh.add(PropertyViewController.this);
      }
    }
  };
  



















  protected QuestionDeletionListener questionDeletionListener = new QuestionDeletionListener();
  protected JLabel expressionLabel = new JLabel();
  protected javax.swing.JComponent questionViewController;
  protected boolean beingDroppedOn = false;
  protected java.awt.Color dndHighlightColor = AuthoringToolResources.getColor("dndHighlight");
  protected java.awt.Color dndHighlightColor2 = AuthoringToolResources.getColor("dndHighlight2");
  protected boolean paintDropPotential = false;
  protected DropPotentialFeedbackListener dropPotentialFeedbackListener = new DropPotentialFeedbackListener();
  protected JLabel unitLabel = new JLabel();
  
  protected JPanel rightPanel = new GroupingPanel()
  {
    public void release() {}
  };
  
  public PropertyViewController()
  {
    created += 1;
    
    setLayout(new java.awt.BorderLayout(0, 0));
    setBackground(AuthoringToolResources.getColor("propertyViewControllerBackground"));
    setBorder(javax.swing.BorderFactory.createCompoundBorder(getBorder(), javax.swing.BorderFactory.createEmptyBorder(0, 2, 0, 0)));
    popupButton.setContentAreaFilled(false);
    popupButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
    popupButton.setFocusPainted(false);
    popupButton.setBorderPainted(false);
    popupButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent ev) {
          if (editingEnabled) {
            updatePopupStructure();
            triggerPopup(ev);
          }
          
        }
      });
    addMouseListener(getMouseListener());
    



    unitLabel.setFont(unitLabel.getFont().deriveFont(0));
    rightPanel.setOpaque(false);
    rightPanel.setLayout(new java.awt.BorderLayout());
    rightPanel.setBorder(null);
    rightPanel.removeContainerListener(edu.cmu.cs.stage3.alice.authoringtool.util.GUIElementContainerListener.getStaticListener());
  }
  















  protected JLabel nameLabel = new JLabel();
  
  public void set(Property property, boolean includeDefaults, boolean allowExpressions, boolean includeOther, boolean omitPropertyName, PopupItemFactory factory) {
    clean();
    this.property = property;
    this.includeDefaults = includeDefaults;
    this.allowExpressions = allowExpressions;
    this.includeOther = includeOther;
    this.omitPropertyName = omitPropertyName;
    this.factory = factory;
    if (!omitPropertyName) {
      nameLabel = new JLabel(AuthoringToolResources.getReprForValue(property, false) + " = ");
      int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
      nameLabel.setFont(new java.awt.Font("Dialog", 3, (int)(12 * fontSize / 12.0D)));
      add(nameLabel, "West");
    }
    
    setPopupEnabled(true);
    
    if (!sleeping) {
      startListening();
      if ((property.get() instanceof Element)) {
        getname.addPropertyListener(propertyListener);
      }
    }
  }
  
  public Property getProperty() {
    return property;
  }
  
  protected void getLabels(java.awt.Component c, Vector v) {
    if ((c instanceof JLabel)) {
      if ((c.isVisible()) && (
        (c != nameLabel) || (!omitPropertyName)))
      {
        v.add(c);
      }
    }
    else if ((c instanceof java.awt.Container)) {
      java.awt.Container containerC = (java.awt.Container)c;
      for (int i = 0; i < containerC.getComponentCount(); i++) {
        getLabels(containerC.getComponent(i), v);
      }
    }
  }
  
  protected String getHTMLColorString(java.awt.Color color)
  {
    String r = Integer.toHexString(color.getRed());
    String g = Integer.toHexString(color.getGreen());
    String b = Integer.toHexString(color.getBlue());
    
    if (r.length() == 1) {
      r = "0" + r;
    }
    if (g.length() == 1) {
      g = "0" + g;
    }
    if (b.length() == 1) {
      b = "0" + b;
    }
    return new String("#" + r + g + b);
  }
  
  public void getHTML(StringBuffer toWriteTo) {
    String tempString = "";
    


    boolean isNativeComponent = true;
    for (int i = 0; i < getComponentCount(); i++) {
      if (getComponent(i) == getNativeComponent()) {
        break;
      }
      if (getComponent(i) == expressionLabel) {
        isNativeComponent = false;
        break;
      }
      if ((getComponent(i) instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel)) {
        toWriteTo.append("( " + edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(getComponent(i)) + " )");
        return;
      }
    }
    tempString = tempString + "<span style=\"background-color: " + getHTMLColorString(getBackground()) + "\">";
    if (!omitPropertyName) {
      tempString = tempString + AuthoringToolResources.getReprForValue(property, false) + " = ";
    }
    if (isNativeComponent) {
      tempString = tempString + edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(getNativeComponent());
    } else {
      tempString = tempString + edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getHTMLStringForComponent(expressionLabel);
    }
    if (unitLabel.getParent() != null) {
      tempString = tempString + " " + unitLabel.getText();
    }
    tempString = tempString + "</span>";
    


























    toWriteTo.append(tempString);
  }
  
  public void goToSleep() {
    stopListening();
    
    sleeping = true;
  }
  
  public void wakeUp()
  {
    startListening();
    sleeping = false;
  }
  
  public void die() {
    clean();
  }
  



  public void release()
  {
    edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
    released += 1;
  }
  
  public boolean isPopupEnabled() {
    return popupEnabled;
  }
  
  public void setPopupEnabled(boolean popupEnabled) {
    if (popupEnabled != this.popupEnabled) {
      this.popupEnabled = popupEnabled;
      if (popupEnabled) {
        if (editingEnabled)
        {
          rightPanel.add(popupButton, "East");
        }
      }
      else {
        rightPanel.remove(popupButton);
      }
    }
  }
  
  public void setEditingEnabled(boolean editingEnabled) {
    if (this.editingEnabled != editingEnabled) {
      this.editingEnabled = editingEnabled;
      if (!editingEnabled) {
        if (popupEnabled)
        {
          rightPanel.remove(popupButton);
        }
      }
      else if (popupEnabled)
      {
        rightPanel.add(popupButton, "East");
      }
    }
  }
  
  public boolean isEditingEnabled()
  {
    return editingEnabled;
  }
  
  protected void startListening() {
    if (property != null) {
      property.addPropertyListener(propertyListener);
      property.getOwner().addChildrenListener(questionDeletionListener);
    }
    edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.addListener(dropPotentialFeedbackListener);
  }
  
  protected void stopListening() {
    if (property != null) {
      property.removePropertyListener(propertyListener);
      property.getOwner().removeChildrenListener(questionDeletionListener);
    }
    edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.removeListener(dropPotentialFeedbackListener);
  }
  
  public void clean() {
    stopListening();
    cleanOutPropertyValueQuestions();
    removeAll();
    popupEnabled = false;
  }
  
  protected java.awt.event.MouseListener getMouseListener() {
    new java.awt.event.MouseAdapter() {
      public void mouseReleased(java.awt.event.MouseEvent ev) {
        if ((ev.getX() >= 0) && (ev.getX() < ev.getComponent().getWidth()) && (ev.getY() >= 0) && (ev.getY() < ev.getComponent().getHeight()) && 
          (isEnabled())) {
          popupButton.doClick();
        }
      }
    };
  }
  
  protected void refreshGUI()
  {
    add(rightPanel, "East");
    
    expressionLabel.setForeground(new java.awt.Color(0, 0, 0));
    Object value = property.get();
    if ((value instanceof edu.cmu.cs.stage3.alice.core.question.AbstractScriptDefinedObject)) {
      if (isAncestorOf(getNativeComponent())) {
        remove(getNativeComponent());
      } else if ((questionViewController != null) && (isAncestorOf(questionViewController))) {
        remove(questionViewController);
      }
      expressionLabel.setText("`" + evalScript.getStringValue() + "`");
      if (!isAncestorOf(expressionLabel)) {
        add(expressionLabel, "Center");
      }
    } else if ((value instanceof PropertyValue)) {
      if (isAncestorOf(getNativeComponent())) {
        remove(getNativeComponent());
      } else if ((questionViewController != null) && (isAncestorOf(questionViewController))) {
        remove(questionViewController);
      }
      







      questionViewController = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(value);
      add(questionViewController, "Center");
    } else if ((value instanceof Question)) {
      if (isAncestorOf(getNativeComponent())) {
        remove(getNativeComponent());
      } else if ((questionViewController != null) && (isAncestorOf(questionViewController))) {
        remove(questionViewController);
      } else if (isAncestorOf(expressionLabel)) {
        remove(expressionLabel);
      }
      questionViewController = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(value);
      add(questionViewController, "Center");
    } else if ((value instanceof edu.cmu.cs.stage3.alice.core.Expression)) {
      if (isAncestorOf(getNativeComponent())) {
        remove(getNativeComponent());
      } else if ((questionViewController != null) && (isAncestorOf(questionViewController))) {
        remove(questionViewController);
      }
      expressionLabel.setText(AuthoringToolResources.getReprForValue(value, property, property.getOwner().data));
      if (!isAncestorOf(expressionLabel)) {
        add(expressionLabel, "Center");
      }
    } else if (value == null) {
      if (isAncestorOf(getNativeComponent())) {
        remove(getNativeComponent());
      } else if ((questionViewController != null) && (isAncestorOf(questionViewController))) {
        remove(questionViewController);
      }
      
      expressionLabel.setForeground(new java.awt.Color(200, 30, 30));
      
      expressionLabel.setText(AuthoringToolResources.getReprForValue(null, property, property.getOwner().data));
      if (!isAncestorOf(expressionLabel)) {
        add(expressionLabel, "Center");
      }
    } else if (getNativeClass().isAssignableFrom(value.getClass())) {
      if (isAncestorOf(expressionLabel)) {
        remove(expressionLabel);
      } else if ((questionViewController != null) && (isAncestorOf(questionViewController))) {
        remove(questionViewController);
      }
      updateNativeComponent();
      if (!isAncestorOf(getNativeComponent())) {
        add(getNativeComponent(), "Center");
      }
    } else {
      edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Bad value: " + value, null);
    }
    

    JLabel mainLabel = null;
    if (isAncestorOf(expressionLabel)) {
      mainLabel = expressionLabel;
    } else if (((getNativeComponent() instanceof JLabel)) && (isAncestorOf(getNativeComponent()))) {
      mainLabel = (JLabel)getNativeComponent();
    }
    if (mainLabel != null) {
      String mainString = mainLabel.getText();
      if (mainString == null) {
        mainString = "";
      }
      
      if ((!(property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.Comment)) && 
        (!(property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.Print))) {
        String unitString = null;
        java.util.Collection unitMapValues = AuthoringToolResources.getUnitMapValues();
        for (java.util.Iterator iter = unitMapValues.iterator(); iter.hasNext();) {
          String s = edu.cmu.cs.stage3.lang.Messages.getString(((String)iter.next()).replace(" ", "_"));
          if (mainString.endsWith(" " + s)) {
            unitString = s;
            break;
          }
        }
        if (unitString != null) {
          mainLabel.setText(mainString.substring(0, mainString.length() - unitString.length()));
          unitLabel.setText(unitString);
          if (!isAncestorOf(unitLabel)) {
            rightPanel.add(unitLabel, "Center");
          }
        }
        else if (isAncestorOf(unitLabel)) {
          rightPanel.remove(unitLabel);
        }
        
      }
    }
    else if (isAncestorOf(unitLabel)) {
      rightPanel.remove(unitLabel);
    }
    



    javax.swing.SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          revalidate();
          repaint();
        }
      });
  }
  
  protected abstract java.awt.Component getNativeComponent();
  
  protected abstract Class getNativeClass();
  
  protected abstract void updateNativeComponent();
  
  protected void updatePopupStructure() { popupStructure = PopupMenuUtilities.makePropertyStructure(property, factory, includeDefaults, allowExpressions, includeOther, null); }
  
  public void triggerPopup(java.awt.event.ActionEvent ev)
  {
    if ((popupStructure != null) && 
      (isEnabled())) {
      PopupMenuUtilities.createAndShowPopupMenu(popupStructure, this, 0, getHeight());
    }
  }
  
  public void paintForeground(java.awt.Graphics g)
  {
    super.paintForeground(g);
    if ((beingDroppedOn) && (editingEnabled)) {
      Dimension size = getSize();
      g.setColor(dndHighlightColor2);
      g.drawRect(0, 0, width - 1, height - 1);
      g.drawRect(1, 1, width - 3, height - 3);
    } else if ((paintDropPotential) && (editingEnabled)) {
      Dimension size = getSize();
      g.setColor(dndHighlightColor);
      g.drawRect(0, 0, width - 1, height - 1);
      g.drawRect(1, 1, width - 3, height - 3);
    }
  }
  
  protected void cleanOutPropertyValueQuestions() {
    if (property != null) {
      Element[] children = property.getOwner().getChildren();
      for (int i = 0; i < children.length; i++) {
        if ((children[i] instanceof PropertyValue)) {
          PropertyValue pv = (PropertyValue)children[i];
          if ((data.get("createdByPropertyViewController") != null) && (data.get("createdByPropertyViewController").equals("true")) && 
            (property.getOwner().getPropertyReferencesTo(pv, edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false, false).length == 0)) {
            property.getOwner().removeChild(pv);
          }
        }
      }
    }
  }
  

  protected boolean checkTransferable(Transferable transferable)
  {
    if (transferable != null) {
      Class desiredValueClass = PopupMenuUtilities.getDesiredValueClass(property);
      












      try
      {
        if ((AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementReferenceTransferable.variableReferenceFlavor)) && (allowExpressions)) {
          java.util.List accessibleExpressions = new java.util.ArrayList(java.util.Arrays.asList(property.getOwner().findAccessibleExpressions(Object.class)));
          














          Variable variable = (Variable)transferable.getTransferData(ElementReferenceTransferable.variableReferenceFlavor);
          
          if (accessibleExpressions.contains(variable)) {
            Vector propertyValueStructure = new Vector();
            if (Element.class.isAssignableFrom(variable.getValueClass())) {
              propertyValueStructure = PopupMenuUtilities.makePropertyValueStructure(variable, desiredValueClass, factory, property.getOwner());
            }
            
            if (edu.cmu.cs.stage3.alice.core.List.class.isAssignableFrom(variable.getValueClass())) {
              edu.cmu.cs.stage3.alice.core.List list = (edu.cmu.cs.stage3.alice.core.List)variable.getValue();
              Element parent = property.getOwner().getParent();
              PropertyReference[] references = new PropertyReference[0];
              if (parent != null) {
                references = parent.getPropertyReferencesTo(property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false);
              }
              

              if (((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.list.ListObjectQuestion)) && (references.length > 0)) {
                Class itemValueClass = references[0].getProperty().getValueClass();
                if ((list != null) && (itemValueClass.isAssignableFrom(valueClass.getClassValue()))) {
                  return true;
                }
              } else {
                if ((list != null) && (desiredValueClass.isAssignableFrom(valueClass.getClassValue())))
                  return true;
                if (desiredValueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.List.class)) {
                  return true;
                }
                if (Number.class.isAssignableFrom(desiredValueClass)) {
                  return true;
                }
                if (Boolean.class.isAssignableFrom(desiredValueClass)) {
                  return true;
                }
              }
            } else if (Array.class.isAssignableFrom(variable.getValueClass())) {
              Array array = (Array)variable.getValue();
              Element parent = property.getOwner().getParent();
              PropertyReference[] references = new PropertyReference[0];
              if (parent != null) {
                references = parent.getPropertyReferencesTo(property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false);
              }
              

              if (((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.array.ArrayObjectQuestion)) && (references.length > 0)) {
                Class itemValueClass = references[0].getProperty().getValueClass();
                if ((array != null) && (itemValueClass.isAssignableFrom(valueClass.getClassValue()))) {
                  return true;
                }
              } else {
                if ((array != null) && (desiredValueClass.isAssignableFrom(valueClass.getClassValue())))
                  return true;
                if (desiredValueClass.isAssignableFrom(Array.class)) {
                  return true;
                }
                if (Number.class.isAssignableFrom(desiredValueClass))
                  return true;
              }
            } else {
              if (desiredValueClass.isAssignableFrom(variable.getValueClass()))
                return true;
              if (desiredValueClass.isAssignableFrom(variable.getClass()))
                return true;
              if (Boolean.class.isAssignableFrom(desiredValueClass))
                return true;
              if ((Number.class.isAssignableFrom(desiredValueClass)) && (Vector3d.class.isAssignableFrom(variable.getValueClass())))
                return true;
              if (Response.class.isAssignableFrom(desiredValueClass))
                return true;
              if (!propertyValueStructure.isEmpty())
                return true;
            }
          }
        } else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementReferenceTransferable.responseReferenceFlavor)) {
          if (((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.Print)) || ((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Print))) {
            return false;
          }
          if (desiredValueClass.isAssignableFrom(Response.class)) {
            return true;
          }
        } else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementReferenceTransferable.elementReferenceFlavor)) {
          Element element = (Element)transferable.getTransferData(ElementReferenceTransferable.elementReferenceFlavor);
          Vector propertyValueStructure = new Vector();
          if ((!(element instanceof edu.cmu.cs.stage3.alice.core.Expression)) && (allowExpressions)) {
            propertyValueStructure = PopupMenuUtilities.makePropertyValueStructure(element, desiredValueClass, factory, property.getOwner());
          }
          
          if ((element instanceof edu.cmu.cs.stage3.alice.core.Sound))
            return false;
          if ((element instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber))
            return false;
          if ((element instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo))
            return false;
          if ((element instanceof edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString)) {
            return false;
          }
          if (desiredValueClass.isInstance(element))
            return true;
          if (((element instanceof Transformable)) && (javax.vecmath.Matrix4d.class.isAssignableFrom(desiredValueClass)))
            return true;
          if (((element instanceof Transformable)) && (Vector3d.class.isAssignableFrom(desiredValueClass)))
            return true;
          if (((element instanceof Transformable)) && (edu.cmu.cs.stage3.math.Quaternion.class.isAssignableFrom(desiredValueClass)))
            return true;
          if ((Boolean.class.isAssignableFrom(desiredValueClass)) && (allowExpressions))
            return true;
          if ((Number.class.isAssignableFrom(desiredValueClass)) && (Vector3d.class.isAssignableFrom(element.getClass())) && (allowExpressions))
            return true;
          if ((Response.class.isAssignableFrom(desiredValueClass)) && (!(element instanceof edu.cmu.cs.stage3.alice.core.Behavior))) {
            if (((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.Print)) || ((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Print))) {
              return false;
            }
            return true; }
          if ((!propertyValueStructure.isEmpty()) && (!(element instanceof edu.cmu.cs.stage3.alice.core.Behavior))) {
            return true;
          }
        } else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor)) {
          Property p = (Property)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor);
          if ((desiredValueClass.isAssignableFrom(p.getValueClass())) && (allowExpressions))
            return true;
          if ((Boolean.class.isAssignableFrom(desiredValueClass)) && (allowExpressions))
            return true;
          if ((Number.class.isAssignableFrom(desiredValueClass)) && (Vector3d.class.isAssignableFrom(p.getValueClass())) && (allowExpressions))
            return true;
          if (Response.class.isAssignableFrom(desiredValueClass))
            return true;
        } else {
          if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
            ElementPrototype elementPrototype = (ElementPrototype)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
            Class elementClass = elementPrototype.getElementClass();
            if ((((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.Print)) || ((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Print))) && 
              (Response.class.isAssignableFrom(elementClass))) {
              return false;
            }
            boolean hookItUp = false;
            if (desiredValueClass.isAssignableFrom(elementClass)) {
              hookItUp = true;
            } else if ((Question.class.isAssignableFrom(elementClass)) && (allowExpressions))
            {
              Question testQuestion = (Question)elementPrototype.createNewElement();
              if (desiredValueClass.isAssignableFrom(testQuestion.getValueClass())) {
                hookItUp = true;
              } else { if ((elementPrototype.getDesiredProperties().length == 0) && (Number.class.isAssignableFrom(desiredValueClass)) && (Vector3d.class.isAssignableFrom(testQuestion.getValueClass()))) {
                  return true;
                }
                
                if (!(property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.IfElseInOrder)) {} } }
            return true;
          }
          


          if ((AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CommonMathQuestionsTransferable.commonMathQuestionsFlavor)) && (allowExpressions)) {
            if (Number.class.isAssignableFrom(desiredValueClass)) {
              return true;
            }
          } else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor)) {
            CopyFactory copyFactory = (CopyFactory)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor);
            if ((((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.Print)) || ((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Print))) && 
              (Response.class.isAssignableFrom(copyFactory.getValueClass()))) {
              return false;
            }
            if (desiredValueClass.isAssignableFrom(copyFactory.getValueClass()))
              return true;
            if ((Question.class.isAssignableFrom(copyFactory.getValueClass())) && 
              (desiredValueClass.isAssignableFrom(copyFactory.HACK_getExpressionValueClass()))) {
              return true;
            }
          }
        }
      } catch (Throwable t) {
        return false;
      }
    }
    
    return false;
  }
  
  protected boolean checkDrag(DropTargetDragEvent dtde) {
    Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
    return checkTransferable(transferable);
  }
  
  public void dragEnter(DropTargetDragEvent dtde) {
    if (checkDrag(dtde)) {
      beingDroppedOn = true;
      repaint();
    } else {
      super.dragEnter(dtde);
    }
  }
  
  public void dragExit(java.awt.dnd.DropTargetEvent dte) {
    if (beingDroppedOn) {
      beingDroppedOn = false;
      repaint();
    } else {
      super.dragExit(dte);
    }
  }
  

  public void dragOver(DropTargetDragEvent dtde)
  {
    if (beingDroppedOn) {
      if (!checkDrag(dtde)) {
        dtde.rejectDrag();
        beingDroppedOn = false;
        repaint();
      }
    } else {
      super.dragOver(dtde);
    }
  }
  
  public void dropActionChanged(DropTargetDragEvent dtde) {
    if (beingDroppedOn) {
      if (!checkDrag(dtde)) {
        dtde.rejectDrag();
        beingDroppedOn = false;
        repaint();
      }
    } else {
      super.dragOver(dtde);
    }
  }
  
  public void drop(DropTargetDropEvent dtde) {
    Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
    Class desiredValueClass = PopupMenuUtilities.getDesiredValueClass(property);
    try
    {
      if (!checkTransferable(transferable)) {
        super.drop(dtde);
        return;
      }
      if ((AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.questionReferenceFlavor)) && (allowExpressions)) {
        Question question = (Question)transferable.getTransferData(ElementReferenceTransferable.questionReferenceFlavor);
        if (desiredValueClass.isAssignableFrom(question.getValueClass())) {
          if ((dtde.getDropAction() & 0x1) > 0) {
            dtde.acceptDrop(1);
            Element copy = question.createCopyNamed(name.getStringValue());
            property.getOwner().addChild(copy);
            









            property.set(copy);
            dtde.dropComplete(true);
          } else if ((dtde.getDropAction() & 0x2) > 0) {
            dtde.acceptDrop(2);
            question.removeFromParent();
            property.getOwner().addChild(question);
            property.set(question);
            dtde.dropComplete(true);
          } else if ((dtde.getDropAction() & 0x40000000) > 0) {
            dtde.acceptDrop(1073741824);
            property.set(question);
            dtde.dropComplete(true);
          } else {
            super.drop(dtde);
          }
        } else if (Boolean.class.isAssignableFrom(desiredValueClass)) {
          dtde.acceptDrop(1073741824);
          Vector structure = PopupMenuUtilities.makeComparatorStructure(question, this.factory, property.getOwner());
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else if ((Number.class.isAssignableFrom(desiredValueClass)) && (Vector3d.class.isAssignableFrom(question.getValueClass()))) {
          dtde.acceptDrop(1073741824);
          Vector structure = PopupMenuUtilities.makePartsOfPositionStructure(question, this.factory, property.getOwner());
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else if (Response.class.isAssignableFrom(desiredValueClass)) {
          dtde.acceptDrop(1073741824);
          Vector structure = PopupMenuUtilities.makeExpressionResponseStructure(question, this.factory, property.getOwner());
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else {
          super.drop(dtde);
        }
      } else if ((AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.variableReferenceFlavor)) && (allowExpressions)) {
        Variable variable = (Variable)transferable.getTransferData(ElementReferenceTransferable.variableReferenceFlavor);
        
        Vector propertyValueStructure = new Vector();
        if (Element.class.isAssignableFrom(variable.getValueClass())) {
          propertyValueStructure = PopupMenuUtilities.makePropertyValueStructure(variable, desiredValueClass, this.factory, property.getOwner());
        }
        
        if (edu.cmu.cs.stage3.alice.core.List.class.isAssignableFrom(variable.getValueClass())) {
          edu.cmu.cs.stage3.alice.core.List list = (edu.cmu.cs.stage3.alice.core.List)variable.getValue();
          Element parent = property.getOwner().getParent();
          PropertyReference[] references = new PropertyReference[0];
          if (parent != null) {
            references = parent.getPropertyReferencesTo(property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false);
          }
          

          if (((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.list.ListObjectQuestion)) && (references.length > 0)) {
            Class itemValueClass = references[0].getProperty().getValueClass();
            if ((list != null) && (itemValueClass.isAssignableFrom(valueClass.getClassValue()))) {
              dtde.acceptDrop(1073741824);
              property.set(variable);
              dtde.dropComplete(true);
            } else {
              super.drop(dtde);
            }
          }
          else if ((list != null) && ((desiredValueClass.isAssignableFrom(valueClass.getClassValue())) || (Boolean.class.isAssignableFrom(desiredValueClass)) || (Number.class.isAssignableFrom(desiredValueClass)))) {
            dtde.acceptDrop(1073741824);
            PopupItemFactory factory = new PopupItemFactory() {
              public Object createItem(final Object object) {
                new Runnable() {
                  public void run() {
                    Question q = (Question)object;
                    property.getOwner().addChild(q);
                    property.set(q);
                  }
                  
                };
              }
            };
            Vector structure = PopupMenuUtilities.makeListQuestionStructure(variable, factory, desiredValueClass, property.getOwner());
            if (desiredValueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.List.class)) {
              String repr = AuthoringToolResources.getReprForValue(variable, property);
              structure.add(0, new StringObjectPair("Separator", javax.swing.JSeparator.class));
              structure.add(0, new StringObjectPair(repr, new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(property).createItem(variable)));
            }
            JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
            popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            PopupMenuUtilities.ensurePopupIsOnScreen(popup);
            dtde.dropComplete(true);
          } else if (desiredValueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.List.class)) {
            dtde.acceptDrop(1073741824);
            property.set(variable);
            dtde.dropComplete(true);
          }
          else {
            super.drop(dtde);
          }
        }
        else if (Array.class.isAssignableFrom(variable.getValueClass())) {
          Array array = (Array)variable.getValue();
          Element parent = property.getOwner().getParent();
          PropertyReference[] references = new PropertyReference[0];
          if (parent != null) {
            references = parent.getPropertyReferencesTo(property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false);
          }
          

          if (((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.array.ArrayObjectQuestion)) && (references.length > 0)) {
            Class itemValueClass = references[0].getProperty().getValueClass();
            if ((array != null) && (itemValueClass.isAssignableFrom(valueClass.getClassValue()))) {
              dtde.acceptDrop(1073741824);
              property.set(variable);
              dtde.dropComplete(true);
            } else {
              super.drop(dtde);
            }
          }
          else if ((array != null) && (desiredValueClass.isAssignableFrom(valueClass.getClassValue()))) {
            dtde.acceptDrop(1073741824);
            PopupItemFactory factory = new PopupItemFactory() {
              public Object createItem(final Object object) {
                new Runnable() {
                  public void run() {
                    Question q = (Question)object;
                    

                    q.setParent(property.getOwner());
                    property.set(q);
                  }
                  
                };
              }
            };
            Vector structure = PopupMenuUtilities.makeArrayQuestionStructure(variable, factory, desiredValueClass, property.getOwner());
            JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
            popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            PopupMenuUtilities.ensurePopupIsOnScreen(popup);
            dtde.dropComplete(true);
          } else if (desiredValueClass.isAssignableFrom(Number.class)) {
            dtde.acceptDrop(1073741824);
            PopupItemFactory factory = new PopupItemFactory() {
              public Object createItem(final Object object) {
                new Runnable() {
                  public void run() {
                    Question q = (Question)object;
                    

                    q.setParent(property.getOwner());
                    property.set(q);
                  }
                  
                };
              }
            };
            Vector structure = PopupMenuUtilities.makeArrayQuestionStructure(variable, factory, desiredValueClass, property.getOwner());
            JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
            popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            PopupMenuUtilities.ensurePopupIsOnScreen(popup);
            dtde.dropComplete(true);
          } else if (desiredValueClass.isAssignableFrom(Array.class)) {
            dtde.acceptDrop(1073741824);
            property.set(variable);
            dtde.dropComplete(true);
          } else {
            super.drop(dtde);
          }
        }
        else if (desiredValueClass.isAssignableFrom(variable.getValueClass())) {
          dtde.acceptDrop(1073741824);
          if (!propertyValueStructure.isEmpty()) {
            Vector structure = new Vector();
            String variableRepr = AuthoringToolResources.getReprForValue(variable, false);
            if (variable.equals(property.get())) {
              structure.addElement(new StringObjectPair(variableRepr, new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemWithIcon(this.factory.createItem(variable), PopupMenuUtilities.currentValueIcon)));
            } else {
              structure.addElement(new StringObjectPair(variableRepr, this.factory.createItem(variable)));
            }
            structure.add(new StringObjectPair("Separator", javax.swing.JSeparator.class));
            structure.addAll(propertyValueStructure);
            JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
            popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          } else {
            property.set(variable);
          }
          dtde.dropComplete(true);
        } else if (desiredValueClass.isAssignableFrom(variable.getClass())) {
          dtde.acceptDrop(1073741824);
          property.set(variable);
          dtde.dropComplete(true);
        } else if (Boolean.class.isAssignableFrom(desiredValueClass)) {
          dtde.acceptDrop(1073741824);
          Vector structure = PopupMenuUtilities.makeComparatorStructure(variable, this.factory, property.getOwner());
          if (!propertyValueStructure.isEmpty()) {
            structure.add(new StringObjectPair("Separator", javax.swing.JSeparator.class));
            structure.addAll(propertyValueStructure);
          }
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else if ((Number.class.isAssignableFrom(desiredValueClass)) && (Vector3d.class.isAssignableFrom(variable.getValueClass()))) {
          dtde.acceptDrop(1073741824);
          Vector structure = PopupMenuUtilities.makePartsOfPositionStructure(variable, this.factory, property.getOwner());
          if (!propertyValueStructure.isEmpty()) {
            structure.add(new StringObjectPair("Separator", javax.swing.JSeparator.class));
            structure.addAll(propertyValueStructure);
          }
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else if (Response.class.isAssignableFrom(desiredValueClass)) {
          dtde.acceptDrop(1073741824);
          Vector structure = PopupMenuUtilities.makeExpressionResponseStructure(variable, this.factory, property.getOwner());
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else if (!propertyValueStructure.isEmpty()) {
          dtde.acceptDrop(1073741824);
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(propertyValueStructure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else {
          super.drop(dtde);
        }
      } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.responseReferenceFlavor)) {
        Response response = (Response)transferable.getTransferData(ElementReferenceTransferable.responseReferenceFlavor);
        if (desiredValueClass.isAssignableFrom(Response.class)) {
          if ((dtde.getDropAction() & 0x1) > 0) {
            dtde.acceptDrop(1);
            Element copy = response.createCopyNamed(name.getStringValue());
            property.getOwner().addChild(copy);
            property.set(copy);
            dtde.dropComplete(true);
          } else if ((dtde.getDropAction() & 0x2) > 0) {
            dtde.acceptDrop(2);
            response.removeFromParent();
            property.getOwner().addChild(response);
            property.set(response);
            dtde.dropComplete(true);
          } else if ((dtde.getDropAction() & 0x40000000) > 0) {
            dtde.acceptDrop(1073741824);
            property.set(response);
            dtde.dropComplete(true);
          } else {
            super.drop(dtde);
          }
        } else {
          super.drop(dtde);
        }
      } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.elementReferenceFlavor)) {
        Element element = (Element)transferable.getTransferData(ElementReferenceTransferable.elementReferenceFlavor);
        Vector propertyValueStructure = new Vector();
        if (allowExpressions) {
          propertyValueStructure = PopupMenuUtilities.makePropertyValueStructure(element, desiredValueClass, this.factory, property.getOwner());
        }
        
        if (desiredValueClass.isInstance(element)) {
          dtde.acceptDrop(2);
          if (!propertyValueStructure.isEmpty()) {
            Vector structure = new Vector();
            String elementRepr = AuthoringToolResources.getReprForValue(element, false);
            if (element.equals(property.get())) {
              structure.addElement(new StringObjectPair(elementRepr, new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemWithIcon(this.factory.createItem(element), PopupMenuUtilities.currentValueIcon)));
            } else {
              structure.addElement(new StringObjectPair(elementRepr, this.factory.createItem(element)));
            }
            structure.add(new StringObjectPair("Separator", javax.swing.JSeparator.class));
            structure.addAll(propertyValueStructure);
            JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
            popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          } else {
            property.set(element);
          }
          dtde.dropComplete(true);
        } else if (Boolean.class.isAssignableFrom(desiredValueClass)) {
          dtde.acceptDrop(1073741824);
          Vector structure = PopupMenuUtilities.makeComparatorStructure(element, this.factory, property.getOwner());
          if (!propertyValueStructure.isEmpty()) {
            structure.add(new StringObjectPair("Separator", javax.swing.JSeparator.class));
            structure.addAll(propertyValueStructure);
          }
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else if ((Number.class.isAssignableFrom(desiredValueClass)) && (Vector3d.class.isAssignableFrom(element.getClass()))) {
          dtde.acceptDrop(1073741824);
          Vector structure = PopupMenuUtilities.makePartsOfPositionStructure(element, this.factory, property.getOwner());
          if (!propertyValueStructure.isEmpty()) {
            structure.add(new StringObjectPair("Separator", javax.swing.JSeparator.class));
            structure.addAll(propertyValueStructure);
          }
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else if (((element instanceof Transformable)) && (javax.vecmath.Matrix4d.class.isAssignableFrom(desiredValueClass))) {
          dtde.acceptDrop(1073741824);
          
          if (((property.getOwner() instanceof Transformable)) && (property == property.getOwner()).localTransformation)) {
            edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation povAnim = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
            subject.set(property.getOwner());
            pointOfView.set(edu.cmu.cs.stage3.math.Matrix44.IDENTITY);
            asSeenBy.set(element);
            edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation undoResponse = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
            subject.set(property.getOwner());
            pointOfView.set(property.getOwner()).localTransformation.getMatrix4dValue());
            asSeenBy.set(property.getOwner()).vehicle.getValue());
            Property[] properties = { property.getOwner()).localTransformation };
            edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().performOneShot(povAnim, undoResponse, properties);
          }
          else {
            edu.cmu.cs.stage3.alice.core.question.PointOfView POVQuestion = new edu.cmu.cs.stage3.alice.core.question.PointOfView();
            subject.set(element);
            property.set(POVQuestion);
          }
          dtde.dropComplete(true);
        } else if (((element instanceof Transformable)) && (Vector3d.class.isAssignableFrom(desiredValueClass))) {
          dtde.acceptDrop(1073741824);
          property.set(localTransformation.getMatrix44Value().getPosition());
          dtde.dropComplete(true);
        } else if (((element instanceof Transformable)) && (edu.cmu.cs.stage3.math.Quaternion.class.isAssignableFrom(desiredValueClass))) {
          dtde.acceptDrop(1073741824);
          property.set(localTransformation.getMatrix44Value().getAxes().getQuaternion());
          dtde.dropComplete(true);
        } else if (Response.class.isAssignableFrom(desiredValueClass)) {
          dtde.acceptDrop(1073741824);
          Vector structure = PopupMenuUtilities.makeResponseStructure(element, this.factory, property.getOwner());
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          if (popup != null) {
            popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          }
          dtde.dropComplete(true);
        } else if (!propertyValueStructure.isEmpty()) {
          dtde.acceptDrop(1073741824);
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(propertyValueStructure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else {
          super.drop(dtde);
        }
      } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor)) {
        Property p = (Property)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor);
        if ((desiredValueClass.isAssignableFrom(p.getValueClass())) && (allowExpressions)) {
          dtde.acceptDrop(2);
          PropertyValue propertyValueQuestion = new PropertyValue();
          element.set(p.getOwner());
          propertyName.set(p.getName());
          data.put("createdByPropertyViewController", "true");
          property.getOwner().addChild(propertyValueQuestion);
          property.set(propertyValueQuestion);
          dtde.dropComplete(true);
        } else if ((Boolean.class.isAssignableFrom(desiredValueClass)) && (allowExpressions)) {
          dtde.acceptDrop(1073741824);
          PropertyValue propertyValueQuestion = new PropertyValue();
          element.set(p.getOwner());
          propertyName.set(p.getName());
          data.put("createdByPropertyViewController", "true");
          Vector structure = PopupMenuUtilities.makeComparatorStructure(propertyValueQuestion, this.factory, property.getOwner());
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else if ((Number.class.isAssignableFrom(desiredValueClass)) && (Vector3d.class.isAssignableFrom(p.getValueClass())) && (allowExpressions)) {
          dtde.acceptDrop(1073741824);
          PropertyValue propertyValueQuestion = new PropertyValue();
          element.set(p.getOwner());
          propertyName.set(p.getName());
          data.put("createdByPropertyViewController", "true");
          Vector structure = PopupMenuUtilities.makePartsOfPositionStructure(propertyValueQuestion, this.factory, property.getOwner());
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else if (Response.class.isAssignableFrom(desiredValueClass)) {
          dtde.acceptDrop(1073741824);
          edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype responsePrototype;
          edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype responsePrototype;
          if ((p instanceof edu.cmu.cs.stage3.alice.core.property.VehicleProperty)) {
            StringObjectPair[] knownPropertyValues = {
              new StringObjectPair("element", p.getOwner()), 
              new StringObjectPair("propertyName", p.getName()), 
              new StringObjectPair("duration", new Double(0.0D)) };
            
            String[] desiredProperties = { "value" };
            responsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation.class, knownPropertyValues, desiredProperties);
          } else {
            StringObjectPair[] knownPropertyValues = {
              new StringObjectPair("element", p.getOwner()), 
              new StringObjectPair("propertyName", p.getName()) };
            
            String[] desiredProperties = { "value" };
            responsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class, knownPropertyValues, desiredProperties);
          }
          
          PopupItemFactory prototypeFactory = new PopupItemFactory() {
            public Object createItem(final Object object) {
              new Runnable() {
                public void run() {
                  ElementPrototype ep = (ElementPrototype)object;
                  Element element = ep.createNewElement();
                  property.getOwner().addChild(element);
                  property.set(element);
                }
                
              };
            }
          };
          Vector structure = PopupMenuUtilities.makePrototypeStructure(responsePrototype, prototypeFactory, property.getOwner());
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else {
          super.drop(dtde);
        }
      } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
        ElementPrototype elementPrototype = (ElementPrototype)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
        Class elementClass = elementPrototype.getElementClass();
        boolean hookItUp = false;
        if (desiredValueClass.isAssignableFrom(elementClass)) {
          hookItUp = true;
        } else if ((Question.class.isAssignableFrom(elementClass)) && (allowExpressions))
        {
          Question testQuestion = (Question)elementPrototype.createNewElement();
          if (desiredValueClass.isAssignableFrom(testQuestion.getValueClass())) {
            if ((testQuestion instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion)) {
              edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion userDefinedQuestion = userDefinedQuestion.getUserDefinedQuestionValue();
              if (userDefinedQuestion.isAncestorOf(property.getOwner())) {
                Object[] options = { "Yes, I understand what I am doing.", "No, I made this call accidentally." };
                int result = edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog("The code you have just dropped in creates a recursive call. We recommend that you understand\nwhat recursion is before making a call like this.  Are you sure you want to do this?", "Recursion Warning", 0, 2, null, options, options[1]);
                if (result == 0) {
                  hookItUp = true;
                }
              } else {
                hookItUp = true;
              }
            } else if (property.get() != null) {
              hookItUp = true;
              if ((testQuestion instanceof edu.cmu.cs.stage3.alice.core.question.BinaryBooleanResultingInBooleanQuestion)) {
                elementPrototype = elementPrototype.createCopy(new StringObjectPair("a", property.get()));
              } else if ((testQuestion instanceof edu.cmu.cs.stage3.alice.core.question.BinaryNumberResultingInNumberQuestion)) {
                elementPrototype = elementPrototype.createCopy(new StringObjectPair("a", property.get()));
              } else if ((testQuestion instanceof edu.cmu.cs.stage3.alice.core.question.UnaryBooleanResultingInBooleanQuestion)) {
                elementPrototype = elementPrototype.createCopy(new StringObjectPair("a", property.get()));
              } else if ((testQuestion instanceof edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion)) {
                elementPrototype = elementPrototype.createCopy(new StringObjectPair("a", property.get()));
              } else if ((testQuestion instanceof edu.cmu.cs.stage3.alice.core.question.StringConcatQuestion)) {
                elementPrototype = elementPrototype.createCopy(new StringObjectPair("a", property.get()));
              }
            } else {
              hookItUp = true;
            }
            
          }
          else if (Boolean.class.isAssignableFrom(desiredValueClass)) {
            dtde.acceptDrop(1073741824);
            Vector structure = PopupMenuUtilities.makeComparatorStructure(testQuestion, this.factory, property.getOwner());
            JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
            popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            PopupMenuUtilities.ensurePopupIsOnScreen(popup);
            dtde.dropComplete(true);
          }
          else if ((elementPrototype.getDesiredProperties().length == 0) && (Number.class.isAssignableFrom(desiredValueClass)) && (Vector3d.class.isAssignableFrom(testQuestion.getValueClass()))) {
            dtde.acceptDrop(1073741824);
            Element element = elementPrototype.createNewElement();
            property.getOwner().addChild(element);
            Vector structure = PopupMenuUtilities.makePartsOfPositionStructure(element, this.factory, property.getOwner());
            JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
            popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            PopupMenuUtilities.ensurePopupIsOnScreen(popup);
            dtde.dropComplete(true);
          }
        }
        
        if (hookItUp) {
          dtde.acceptDrop(2);
          PopupItemFactory factory = new PopupItemFactory() {
            public Object createItem(final Object object) {
              new Runnable() {
                public void run() {
                  ElementPrototype ep = (ElementPrototype)object;
                  Element element = ep.createNewElement();
                  property.getOwner().addChild(element);
                  property.set(element);
                }
              };
            }
          };
          
          if ((elementPrototype.getDesiredProperties().length > 0) && (elementPrototype.getDesiredProperties().length < 4)) {
            Vector structure = PopupMenuUtilities.makePrototypeStructure(elementPrototype, factory, property.getOwner());
            JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
            popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          } else {
            ((Runnable)factory.createItem(elementPrototype)).run();
          }
          dtde.dropComplete(true);
        } else {
          super.drop(dtde);
        }
      } else if ((AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CommonMathQuestionsTransferable.commonMathQuestionsFlavor)) && (allowExpressions)) {
        if (Number.class.isAssignableFrom(desiredValueClass)) {
          dtde.acceptDrop(1073741824);
          PopupItemFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(property);
          Vector structure = PopupMenuUtilities.makeCommonMathQuestionStructure(property.get(), factory, property.getOwner());
          JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
          popup.show(this, (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
          PopupMenuUtilities.ensurePopupIsOnScreen(popup);
          dtde.dropComplete(true);
        } else {
          super.drop(dtde);
        }
      } else if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor)) {
        CopyFactory copyFactory = (CopyFactory)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor);
        if (desiredValueClass.isAssignableFrom(copyFactory.getValueClass())) {
          dtde.acceptDrop(1073741824);
          Element element = copyFactory.manufactureCopy(property.getOwner().getRoot());
          property.set(element);
          property.getOwner().addChild(element);
          dtde.dropComplete(true);
        } else if (Question.class.isAssignableFrom(copyFactory.getValueClass())) {
          Question question = (Question)copyFactory.manufactureCopy(property.getOwner().getRoot());
          if (desiredValueClass.isAssignableFrom(question.getValueClass())) {
            dtde.acceptDrop(1);
            property.set(question);
            property.getOwner().addChild(question);
            dtde.dropComplete(true);
          } else {
            super.drop(dtde);
          }
        } else {
          super.drop(dtde);
        }
      } else {
        super.drop(dtde);
      }
    } catch (java.awt.datatransfer.UnsupportedFlavorException e) {
      edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: bad flavor", e);
    } catch (java.io.IOException e) {
      edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: IOException", e);
    } catch (Throwable t) {
      edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work.", t);
    }
    beingDroppedOn = false;
    repaint();
  }
  
  protected class DropPotentialFeedbackListener implements edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener { protected DropPotentialFeedbackListener() {}
    
    private void doCheck() { Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
      boolean transferableHasPotential = checkTransferable(transferable);
      if (paintDropPotential != transferableHasPotential) {
        paintDropPotential = transferableHasPotential;
        repaint();
      }
    }
    

    public void dragGestureRecognized(java.awt.dnd.DragGestureEvent dge) {}
    
    public void dragStarted()
    {
      doCheck();
    }
    
    public void dragEnter(java.awt.dnd.DragSourceDragEvent dsde) {
      doCheck();
    }
    
    public void dragExit(java.awt.dnd.DragSourceEvent dse) {
      doCheck();
    }
    

    public void dragOver(java.awt.dnd.DragSourceDragEvent dsde) {}
    
    public void dropActionChanged(java.awt.dnd.DragSourceDragEvent dsde)
    {
      doCheck();
    }
    
    public void dragDropEnd(java.awt.dnd.DragSourceDropEvent dsde) {
      paintDropPotential = false;
      repaint();
    } }
  
  protected class QuestionDeletionListener implements edu.cmu.cs.stage3.alice.core.event.ChildrenListener { protected QuestionDeletionListener() {}
    
    public void childrenChanging(edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev) {}
    
    public void childrenChanged(edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev) { if ((ev.getChangeType() == 3) && 
        ((ev.getChild() instanceof Question))) {
        Question question = (Question)ev.getChild();
        if ((data.get("associatedProperty") != null) && 
          (data.get("associatedProperty").equals(property.getName()))) {
          Object newValue = null;
          PropertyViewController.handlingQuestionAlready = true;
          if ((question instanceof edu.cmu.cs.stage3.alice.core.question.BinaryBooleanResultingInBooleanQuestion)) {
            newValue = a.get();
          } else if ((question instanceof edu.cmu.cs.stage3.alice.core.question.BinaryNumberResultingInNumberQuestion)) {
            newValue = a.get();
          } else if ((question instanceof edu.cmu.cs.stage3.alice.core.question.UnaryBooleanResultingInBooleanQuestion)) {
            newValue = a.get();
          } else if ((question instanceof edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion)) {
            newValue = a.get();
          } else {
            newValue = property.getDefaultValue();
          }
          if ((newValue instanceof Element))
          {
            Element element = (Element)newValue;
            



            if (element.getParent() == question) {
              edu.cmu.cs.stage3.alice.core.event.ChildrenListener[] childlisteners = question.getChildrenListeners();
              for (int i = 0; i < childlisteners.length; i++) {
                question.removeChildrenListener(childlisteners[i]);
              }
              Element parent = element.getParent();
              int oldIndex = parent.getIndexOfChild(element);
              element.setParent(null);
              edu.cmu.cs.stage3.alice.core.event.ChildrenEvent childChangedEvent = new edu.cmu.cs.stage3.alice.core.event.ChildrenEvent(parent, element, 3, oldIndex, -1);
              
              edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().childrenChanged(childChangedEvent);
              for (int i = 0; i < childlisteners.length; i++) {
                question.addChildrenListener(childlisteners[i]);
              }
            }
          }
          edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory factory = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(property);
          ((Runnable)factory.createItem(newValue)).run();
          PropertyViewController.handlingQuestionAlready = false;
        }
      }
    }
  }
  
  public static class RefreshThread
    extends Thread
  {
    protected boolean haltThread = false;
    protected boolean pauseThread = false;
    
    public RefreshThread() {
      setName("PropertyViewController.RefreshThread");
    }
    
    public void halt() {
      haltThread = true;
      synchronized (this) {
        notify();
      }
    }
    
    public void pause() {
      pauseThread = true;
    }
    
    public void unpause() {
      pauseThread = false;
      synchronized (this) {
        notify();
      }
    }
    
    public void run() {
      while (!haltThread) {
        synchronized (PropertyViewController.propertyViewControllersToRefresh) {
          if (!PropertyViewController.propertyViewControllersToRefresh.isEmpty()) {
            for (java.util.Iterator iter = PropertyViewController.propertyViewControllersToRefresh.iterator(); iter.hasNext();) {
              final PropertyViewController pvc = (PropertyViewController)iter.next();
              javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  pvc.refreshGUI();
                }
              });
            }
            PropertyViewController.propertyViewControllersToRefresh.clear();
          }
        }
        try {
          Thread.sleep(100L);
          if (pauseThread) {
            synchronized (this) {
              while (pauseThread) {
                wait();
              }
            }
          }
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
  }
}
