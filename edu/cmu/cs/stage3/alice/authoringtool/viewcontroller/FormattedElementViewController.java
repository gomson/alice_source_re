package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.UserDefinedQuestionProperty;
import edu.cmu.cs.stage3.alice.core.property.UserDefinedResponseProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FormattedElementViewController extends DnDGroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement
{
  protected Element element;
  protected List visibleProperties;
  protected JPanel subPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel();
  protected String format;
  protected java.util.HashMap guiMap = new java.util.HashMap();
  
  protected JPanel moreTile = new JPanel();
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
  
  protected PropertyListener userDefinedListener = new PropertyListener() {
    public void propertyChanging(PropertyEvent ev) {
      if ((element instanceof CallToUserDefinedResponse)) {
        if ((ev.getProperty().getValue() instanceof UserDefinedResponse)) {
          getPropertygetValuename.removePropertyListener(updateListener);
        }
      } else if (((element instanceof CallToUserDefinedQuestion)) && 
        ((ev.getProperty().getValue() instanceof UserDefinedQuestion))) {
        getPropertygetValuename.removePropertyListener(updateListener);
      }
    }
    
    public void propertyChanged(PropertyEvent ev) {
      if ((element instanceof CallToUserDefinedResponse)) {
        if ((ev.getProperty().getValue() instanceof UserDefinedResponse)) {
          getPropertygetValuename.addPropertyListener(updateListener);
        }
      } else if (((element instanceof CallToUserDefinedQuestion)) && 
        ((ev.getProperty().getValue() instanceof UserDefinedQuestion))) {
        getPropertygetValuename.addPropertyListener(updateListener);
      }
    }
  };
  
  protected boolean sleeping = false;
  
  public FormattedElementViewController() {
    setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 2, 3, 2));
    
    subPanel.setLayout(new java.awt.GridBagLayout());
    subPanel.setOpaque(false);
    subPanel.setBorder(null);
    
    moreTile.setLayout(new java.awt.BorderLayout());
    moreTile.setOpaque(false);
    moreTile.setBorder(null);
    JLabel moreLabel = new JLabel(edu.cmu.cs.stage3.lang.Messages.getString("more___"), AuthoringToolResources.getIconForValue("popupArrow"), 10);
    moreLabel.setHorizontalTextPosition(10);
    moreLabel.setIconTextGap(2);
    moreTile.add(moreLabel, "Center");
    
    moreTile.addMouseListener(
      new java.awt.event.MouseAdapter() {
        public void mouseReleased(MouseEvent ev) {
          if ((ev.getX() >= 0) && (ev.getX() < ev.getComponent().getWidth()) && (ev.getY() >= 0) && (ev.getY() < ev.getComponent().getHeight()) && 
            (isEnabled())) {
            Vector structure = new Vector();
            Property[] properties = getUnsetProperties();
            for (int i = 0; i < properties.length; i++) {
              FormattedElementViewController.SetPropertyImmediatelyFactory factory = new FormattedElementViewController.SetPropertyImmediatelyFactory(FormattedElementViewController.this, properties[i], true);
              structure.add(new edu.cmu.cs.stage3.util.StringObjectPair(properties[i].getName(), edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePropertyStructure(properties[i], factory, true, true, true, null)));
            }
            edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu(structure, moreTile, 0, moreTile.getHeight());
          }
          
        }
        

      });
    addMouseListener(mouseListener);
    grip.addMouseListener(mouseListener);
    subPanel.addMouseListener(mouseListener);
  }
  













  public Element getElement()
  {
    return element;
  }
  
  public void setElement(Element element) {
    super.reset();
    
    stopListening();
    
    this.element = element;
    if (this.element != null) {
      format = AuthoringToolResources.getFormat(element.getClass());
      
      calculateVisibleProperties();
      setTransferable(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory.createTransferable(element));
      
      if ((element instanceof edu.cmu.cs.stage3.alice.core.response.Comment)) {
        setBackground(AuthoringToolResources.getColor("Comment"));
      } else if ((element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Comment)) {
        setBackground(AuthoringToolResources.getColor("Comment"));
      } else if ((element instanceof edu.cmu.cs.stage3.alice.core.response.Print)) {
        setBackground(AuthoringToolResources.getColor("Print"));
      } else if ((element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Print)) {
        setBackground(AuthoringToolResources.getColor("Print"));
      } else if ((element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment)) {
        setBackground(AuthoringToolResources.getColor("PropertyAssignment"));
      } else if ((element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Return)) {
        setBackground(AuthoringToolResources.getColor("Return"));
      } else if ((element instanceof CallToUserDefinedResponse)) {
        setBackground(AuthoringToolResources.getColor("userDefinedResponse"));
      } else if ((element instanceof Response)) {
        setBackground(AuthoringToolResources.getColor("response"));
      } else if ((element instanceof CallToUserDefinedQuestion)) {
        setBackground(AuthoringToolResources.getColor("userDefinedQuestion"));
      } else if ((element instanceof edu.cmu.cs.stage3.alice.core.Question)) {
        setBackground(AuthoringToolResources.getColor("question"));
      } else {
        setBackground(AuthoringToolResources.getColor("formattedElementViewController"));
      }
      


      add(subPanel, "Center");
      addDragSourceComponent(subPanel);
      
      startListening();
    }
    
    refreshGUI();
  }
  
  public Component getMoreTile() {
    return moreTile;
  }
  
  protected void startListening() {
    element.data.addPropertyListener(updateListener);
    if ((element instanceof Response)) {
      Response response = (Response)element;
      isCommentedOut.addPropertyListener(commentedListener);
    }
    



    if ((element instanceof CallToUserDefinedResponse)) {
      CallToUserDefinedResponse callToUserDefinedResponse = (CallToUserDefinedResponse)element;
      userDefinedResponse.addPropertyListener(userDefinedListener);
      if (userDefinedResponse.getUserDefinedResponseValue() != null) {
        userDefinedResponse.getUserDefinedResponseValue().name.addPropertyListener(updateListener);
      }
    } else if ((element instanceof CallToUserDefinedQuestion)) {
      CallToUserDefinedQuestion callToUserDefinedQuestion = (CallToUserDefinedQuestion)element;
      userDefinedQuestion.addPropertyListener(userDefinedListener);
      if (userDefinedQuestion.getUserDefinedQuestionValue() != null) {
        userDefinedQuestion.getUserDefinedQuestionValue().name.addPropertyListener(updateListener);
      }
    }
  }
  
  protected void stopListening() {
    if ((element instanceof Response)) {
      Response response = (Response)element;
      isCommentedOut.removePropertyListener(commentedListener);
    }
    



    if ((element instanceof CallToUserDefinedResponse)) {
      CallToUserDefinedResponse callToUserDefinedResponse = (CallToUserDefinedResponse)element;
      userDefinedResponse.removePropertyListener(userDefinedListener);
      if (userDefinedResponse.getUserDefinedResponseValue() != null) {
        userDefinedResponse.getUserDefinedResponseValue().name.removePropertyListener(updateListener);
      }
    } else if ((element instanceof CallToUserDefinedQuestion)) {
      CallToUserDefinedQuestion callToUserDefinedQuestion = (CallToUserDefinedQuestion)element;
      userDefinedQuestion.removePropertyListener(userDefinedListener);
      if (userDefinedQuestion.getUserDefinedQuestionValue() != null) {
        userDefinedQuestion.getUserDefinedQuestionValue().name.removePropertyListener(updateListener);
      }
    }
  }
  
  private void calculateVisibleProperties() {
    visibleProperties = new LinkedList();
    if (element != null) {
      String visiblePropertiesString = (String)element.data.get("edu.cmu.cs.stage3.alice.authoringtool.visibleProperties");
      if (visiblePropertiesString != null) {
        StringTokenizer tokenizer = new StringTokenizer(visiblePropertiesString, ",");
        while (tokenizer.hasMoreTokens()) {
          String token = tokenizer.nextToken();
          








          Property property = element.getPropertyNamed(token);
          if ((property != null) && (!visibleProperties.contains(property))) {
            visibleProperties.add(property);
          }
        }
      }
    }
    

    FormatTokenizer formatTokenizer = new FormatTokenizer(format);
    while (formatTokenizer.hasMoreTokens()) {
      String token = formatTokenizer.nextToken();
      if ((token.startsWith("<")) && (token.endsWith(">"))) {
        Property property = element.getPropertyNamed(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
        if ((property != null) && (!visibleProperties.contains(property))) {
          visibleProperties.add(property);
        }
      }
    }
    
    if (visibleProperties.size() > 0) {
      StringBuffer sb = new StringBuffer();
      for (Iterator iter = visibleProperties.iterator(); iter.hasNext();) {
        Object o = iter.next();
        if ((o instanceof Property)) {
          o = ((Property)o).getName();
        }
        sb.append((String)o);
        sb.append(",");
      }
      sb.setLength(sb.length() - 1);
      element.data.put("edu.cmu.cs.stage3.alice.authoringtool.visibleProperties", sb.toString());
    }
  }
  
  public void setEnabled(boolean b) {
    super.setEnabled(b);
    Component[] children = subPanel.getComponents();
    for (int i = 0; i < children.length; i++) {
      children[i].setEnabled(b);
    }
  }
  

  protected Property[] getUnsetProperties()
  {
    if (((element instanceof edu.cmu.cs.stage3.alice.core.response.ScriptResponse)) || 
      ((element instanceof edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse)) || 
      ((element instanceof edu.cmu.cs.stage3.alice.core.response.Comment)))
    {
      return new Property[0];
    }
    

    String[] propertiesToOmit = AuthoringToolResources.getParameterizedPropertiesToOmit();
    List propertiesToOmitList = java.util.Arrays.asList(propertiesToOmit);
    
    LinkedList unsetProperties = new LinkedList();
    if (element != null) {
      Property[] properties = element.getProperties();
      for (int i = 0; i < properties.length; i++) {
        if ((!propertiesToOmitList.contains(properties[i].getName())) && 
          (!visibleProperties.contains(properties[i]))) {
          unsetProperties.add(properties[i]);
        }
      }
    }
    
    if (unsetProperties.size() > 0) {
      return (Property[])unsetProperties.toArray(new Property[0]);
    }
    return new Property[0];
  }
  
  public void paintForeground(java.awt.Graphics g)
  {
    super.paintForeground(g);
    if (((element instanceof Response)) && 
      (element).isCommentedOut.booleanValue())) {
      edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects.paintDisabledEffect(g, getBounds());
    }
  }
  
  public void goToSleep()
  {
    stopListening();
    sleeping = true;
  }
  
  public void wakeUp() {
    startListening();
    sleeping = false;
  }
  
  public void clean() {
    stopListening();
    element = null;
    setTransferable(null);
    removeAll();
  }
  
  public void die() {
    clean();
  }
  
  public void release() {
    super.release();
    edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI(this);
  }
  
  public void refreshGUI() {
    subPanel.removeAll();
    if (element != null) {
      calculateVisibleProperties();
      
      LinkedList unusedVisibleProperties = new LinkedList(visibleProperties);
      FormatTokenizer formatTokenizer = new FormatTokenizer(format);
      int i = 0;
      while (formatTokenizer.hasMoreTokens()) {
        String token = formatTokenizer.nextToken();
        javax.swing.JComponent gui = null;
        if ((token.startsWith("<")) && (token.endsWith(">"))) {
          property = element.getPropertyNamed(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
          if (property != null) {
            if ((((element instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation)) || ((element instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue))) && (property.getName().equals("propertyName"))) {
              gui = new StringPropertyLabel((StringProperty)property);
            } else if (((element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment)) && (property.getName().equals("propertyName"))) {
              gui = new StringPropertyLabel((StringProperty)property);
            } else if (((element instanceof CallToUserDefinedResponse)) && (property.getName().equals("userDefinedResponse"))) {
              gui = new PropertyLabel(property);
            } else if (((element instanceof CallToUserDefinedQuestion)) && (property.getName().equals("userDefinedQuestion"))) {
              gui = new PropertyLabel(property);
            }
            else
            {
              allowExpressions = true;
              omitName = AuthoringToolResources.shouldGUIOmitPropertyName(property);
              gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(property, true, allowExpressions, omitName, new SetPropertyImmediatelyFactory(property, false));
            }
            
            unusedVisibleProperties.remove(property);
          }
        } else {
          while (token.indexOf("&lt;") > -1) { Property property;
            boolean allowExpressions; boolean omitName; token = new StringBuffer(token).replace(token.indexOf("&lt;"), token.indexOf("&lt;") + 4, "<").toString();
          }
          gui = new JLabel(token);
          gui.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 0, 3));
          int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
          
















          if (((element instanceof edu.cmu.cs.stage3.alice.core.response.Comment)) || ((element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Comment))) {
            ((JLabel)gui).setForeground(AuthoringToolResources.getColor("commentForeground"));
            ((JLabel)gui).setFont(new java.awt.Font("Helvetica", 1, (int)(13 * fontSize / 12.0D)));
          }
        }
        if (gui != null) {
          subPanel.add(gui, new GridBagConstraints(i++, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        }
      }
      

      for (Iterator iter = unusedVisibleProperties.iterator(); iter.hasNext();) {
        Property property = (Property)iter.next();
        if (property != null) {
          javax.swing.JComponent gui = null;
          if (((element instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation)) && (property.getName().equals("propertyName"))) {
            gui = new StringPropertyLabel((StringProperty)property);
          } else if (((element instanceof CallToUserDefinedResponse)) && (property.getName().equals("userDefinedResponse"))) {
            gui = new PropertyLabel(property);
          } else {
            boolean allowExpressions = !String.class.isAssignableFrom(property.getValueClass());
            boolean omitName = AuthoringToolResources.shouldGUIOmitPropertyName(property);
            gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyViewController(property, true, allowExpressions, omitName, new SetPropertyImmediatelyFactory(property, false));
          }
          subPanel.add(gui, new GridBagConstraints(i++, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        }
      }
      

      boolean isUserDefined = ((element instanceof CallToUserDefinedResponse)) || ((element instanceof CallToUserDefinedQuestion));
      if ((getUnsetProperties().length > 0) && (!isUserDefined)) {
        subPanel.add(moreTile, new GridBagConstraints(i++, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 4, 0, 0), 0, 0));
      }
      
      subPanel.add(javax.swing.Box.createGlue(), new GridBagConstraints(i++, 0, 1, 1, 1.0D, 0.0D, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
    }
    
    revalidate();
    repaint();
  }
  
  public class SetPropertyImmediatelyFactory extends edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory {
    protected boolean addToVisibleProperties;
    
    public SetPropertyImmediatelyFactory(Property property, boolean addToVisibleProperties) {
      super();
      this.addToVisibleProperties = addToVisibleProperties;
    }
    
    protected void run(Object value) {
      super.run(value);
      if (addToVisibleProperties) {
        visibleProperties.add(property);
        element.data.put("edu.cmu.cs.stage3.alice.authoringtool.visibleProperties", (String)element.data.get("edu.cmu.cs.stage3.alice.authoringtool.visibleProperties") + "," + property.getName());
        refreshGUI();
      }
    }
  }
  
  class MouseListener extends edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter { MouseListener() {}
    
    public void popupResponse(MouseEvent ev) { Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.getDefaultStructure(element);
      if ((structure != null) && (!structure.isEmpty())) {
        edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.createAndShowElementPopupMenu(element, structure, FormattedElementViewController.this, ev.getX(), ev.getY());
      }
    }
    
    public void doubleClickResponse(MouseEvent ev) {
      if ((element instanceof CallToUserDefinedResponse)) {
        edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().editObject(element).userDefinedResponse.getUserDefinedResponseValue(), FormattedElementViewController.this);
      } else if ((element instanceof CallToUserDefinedQuestion)) {
        edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().editObject(element).userDefinedQuestion.getUserDefinedQuestionValue(), FormattedElementViewController.this);
      }
    }
  }
}
