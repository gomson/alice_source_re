package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Array;
import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.OverridableElementProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion;
import edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment;
import edu.cmu.cs.stage3.alice.core.response.PropertyAnimation;
import edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.Criterion;
import edu.cmu.cs.stage3.util.StringObjectPair;
import edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion;
import edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

public class PopupMenuUtilities
{
  protected static Configuration authoringToolConfig = Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());
  
  protected static HashMap recentlyUsedValues = new HashMap();
  
  protected static java.util.Hashtable runnablesToActionListeners = new java.util.Hashtable();
  public static final PopupItemFactory oneShotFactory = new PopupItemFactory() {
    public Object createItem(final Object o) {
      new Runnable() {
        public void run() {
          if ((o instanceof ResponsePrototype)) {
            Response response = ((ResponsePrototype)o).createNewResponse();
            Response undoResponse = AuthoringToolResources.createUndoResponse(response);
            Property[] properties = AuthoringToolResources.getAffectedProperties(response);
            AuthoringTool.getHack().performOneShot(response, undoResponse, properties);
          }
        }
      };
    }
  };
  
  public static final javax.swing.Icon currentValueIcon = AuthoringToolResources.getIconForValue("currentValue");
  public static final Object NO_CURRENT_VALUE = new Object();
  
  protected static Criterion isNamedElement = new Criterion() {
    public boolean accept(Object o) {
      if (((o instanceof Element)) && 
        (name.get() != null)) {
        return true;
      }
      
      return false;
    }
  };
  
  protected static Criterion isNotActualParameter = new Criterion() {
    public boolean accept(Object o) {
      if ((o instanceof Variable)) {
        Variable variable = (Variable)o;
        if ((variable.getParent() instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse)) {
          edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse callToUserDefinedResponse = (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse)variable.getParent();
          if (requiredActualParameters.contains(variable))
            return false;
          if (keywordActualParameters.contains(variable)) {
            return false;
          }
        } else if ((variable.getParent() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion)) {
          edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion callToUserDefinedQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion)variable.getParent();
          if (requiredActualParameters.contains(variable))
            return false;
          if (keywordActualParameters.contains(variable)) {
            return false;
          }
        }
      }
      
      return true;
    }
  };
  
  protected static HashMap specialStringMap = new HashMap();
  public static JPopupMenu item;
  
  static { JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    
    specialStringMap.put("<keyCode>", Messages.getString("a_key"));
    specialStringMap.put("<keyCode>", Messages.getString("a_key"));
    specialStringMap.put("<mouse>", Messages.getString("the_mouse"));
    specialStringMap.put("<onWhat>", Messages.getString("something"));
    specialStringMap.put("<condition>", Messages.getString("something"));
    specialStringMap.put("<variable>", Messages.getString("a_variable"));
    specialStringMap.put("<arrowKeys>", Messages.getString("the_arrow_keys"));
    specialStringMap.put("<button>", "");
    specialStringMap.put("<objects>", Messages.getString("objects"));
    specialStringMap.put("<subject>", Messages.getString("subject")); }
  
  public PopupMenuUtilities() {}
  
  public static void addRecentlyUsedValue(Class valueClass, Object value) { if (!recentlyUsedValues.containsKey(valueClass)) {
      recentlyUsedValues.put(valueClass, new java.util.ArrayList());
    }
    java.util.List recentList = (java.util.List)recentlyUsedValues.get(valueClass);
    while (recentList.contains(value)) {
      recentList.remove(value);
    }
    recentList.add(0, value);
  }
  
  public static void clearRecentlyUsedValues() {
    recentlyUsedValues.clear();
  }
  
  public static void createAndShowPopupMenu(Vector structure, java.awt.Component component, int x, int y) {
    JPopupMenu popup = makePopupMenu(structure);
    popup.show(component, x, y);
    ensurePopupIsOnScreen(popup);
  }
  
  public static JPopupMenu makePopupMenu(Vector structure) {
    AliceMenuWithDelayedPopup menu = makeMenu("", structure);
    if (menu != null) {
      return menu.getPopupMenu();
    }
    return null;
  }
  































































  public static AliceMenuWithDelayedPopup makeMenu(String title, Vector structure)
  {
    if ((structure == null) || (structure.isEmpty())) {
      return null;
    }
    AliceMenuWithDelayedPopup menu = new AliceMenuWithDelayedPopup(title, structure);
    menu.setUI(new AliceMenuUI());
    menu.setDelay(0);
    return menu;
  }
  
  public static void populateDelayedMenu(AliceMenuWithDelayedPopup menu, Vector structure)
  {
    for (Enumeration enum0 = structure.elements(); enum0.hasMoreElements();) {
      Object o = enum0.nextElement();
      if (!(o instanceof StringObjectPair)) {
        throw new IllegalArgumentException("structure must be made only of StringObjectPairs, found: " + o);
      }
      
      StringObjectPair pair = (StringObjectPair)o;
      String name = pair.getString();
      

      if (name != null) {
        for (Iterator iter = specialStringMap.keySet().iterator(); iter.hasNext();) {
          String s = (String)iter.next();
          if (name.indexOf(s) > -1) {
            StringBuffer sb = new StringBuffer(name);
            sb.replace(name.indexOf(s), name.indexOf(s) + s.length(), (String)specialStringMap.get(s));
            name = sb.toString();
          }
        }
      }
      
      Object content = pair.getObject();
      if ((content instanceof DelayedBindingPopupItem)) {
        content = ((DelayedBindingPopupItem)content).createItem();
      }
      
      javax.swing.Icon icon = null;
      if ((content instanceof PopupItemWithIcon)) {
        icon = ((PopupItemWithIcon)content).getIcon();
        content = ((PopupItemWithIcon)content).getItem();
      }
      
      if ((content instanceof Vector)) {
        javax.swing.JMenu submenu = makeMenu(name, (Vector)content);
        if (submenu != null) {
          menu.add(submenu);
        }
      } else if ((content instanceof java.awt.event.ActionListener)) {
        JMenuItem menuItem = makeMenuItem(name, icon);
        menuItem.addActionListener((java.awt.event.ActionListener)content);
        menu.add(menuItem);
      } else if ((content instanceof Runnable)) {
        JMenuItem menuItem = makeMenuItem(name, icon);
        java.awt.event.ActionListener listener = (java.awt.event.ActionListener)runnablesToActionListeners.get(content);
        if (listener == null) {
          listener = new PopupMenuItemActionListener((Runnable)content, menu);
        }
        
        menuItem.addActionListener(listener);
        menu.add(menuItem);
      } else if (content == JSeparator.class) {
        menu.addSeparator();
      } else if ((content instanceof java.awt.Component)) {
        menu.add((java.awt.Component)content);
      } else if (content == null)
      {
        JLabel label = new JLabel(Messages.getString(name));
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4));
        menu.add(label);
      }
    }
  }
  








































  public static JMenuItem makeMenuItem(String text) { return makeMenuItem(text, null); }
  
  public static JMenuItem makeMenuItem(String text, javax.swing.Icon icon) { JMenuItem item;
    JMenuItem item;
    if (icon != null) {
      item = new JMenuItem(text, icon);
    } else {
      item = new JMenuItem(text);
    }
    item.setUI(new AliceMenuItemUI());
    return item;
  }
  
  public static boolean isStringInStructure(String s, Vector structure) {
    for (Enumeration enum0 = structure.elements(); enum0.hasMoreElements();) {
      StringObjectPair pair = (StringObjectPair)enum0.nextElement();
      String string = pair.getString();
      Object content = pair.getObject();
      if (string == s) {
        return true;
      }
      if (((content instanceof Vector)) && 
        (isStringInStructure(s, (Vector)content))) {
        return true;
      }
    }
    
    return false;
  }
  
  public static boolean isObjectInStructure(Object o, Vector structure) {
    for (Enumeration enum0 = structure.elements(); enum0.hasMoreElements();) {
      StringObjectPair pair = (StringObjectPair)enum0.nextElement();
      Object content = pair.getObject();
      if (content == o) {
        return true;
      }
      if (((content instanceof Vector)) && 
        (isObjectInStructure(o, (Vector)content))) {
        return true;
      }
    }
    
    return false;
  }
  
  public static Vector makeElementStructure(Element root, final Criterion criterion, final PopupItemFactory factory, final Element context, final Object currentValue)
  {
    Vector structure = new Vector();
    
    if (criterion.accept(root)) {
      if (root.equals(currentValue)) {
        structure.addElement(new StringObjectPair("the entire " + (String)name.getValue(), new PopupItemWithIcon(factory.createItem(root), currentValueIcon)));
      } else {
        structure.addElement(new StringObjectPair("the entire " + (String)name.getValue(), factory.createItem(root)));
      }
      if (root.getChildCount() > 0) {
        Element[] children = root.getChildren();
        for (int i = 0; i < children.length; i++) {
          Element child = children[i];
          if (child.getChildCount() == 0) {
            if (criterion.accept(child)) {
              if (child.equals(currentValue)) {
                structure.addElement(new StringObjectPair((String)name.getValue(), new PopupItemWithIcon(factory.createItem(child), currentValueIcon)));
              } else {
                structure.addElement(new StringObjectPair((String)name.getValue(), factory.createItem(child)));
              }
            }
          }
          else if ((child.search(criterion).length > 0) || (criterion.accept(child))) {
            DelayedBindingPopupItem delayedBindingPopupItem = new DelayedBindingPopupItem() {
              public Object createItem() {
                Vector subStructure = PopupMenuUtilities.makeElementStructure(PopupMenuUtilities.this, criterion, factory, context, currentValue);
                if ((subStructure.size() == 1) && (criterion.accept(PopupMenuUtilities.this))) {
                  if (equals(currentValue)) {
                    return new PopupItemWithIcon(factory.createItem(PopupMenuUtilities.this), PopupMenuUtilities.currentValueIcon);
                  }
                  return factory.createItem(PopupMenuUtilities.this);
                }
                
                return subStructure;
              }
            };
            
            if (!(child instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)) {
              structure.addElement(new StringObjectPair((String)name.getValue(), delayedBindingPopupItem));
            }
          }
        }
        

        if (structure.size() > 1) {
          structure.insertElementAt(new StringObjectPair("Separator", JSeparator.class), 1);
        }
      }
    } else {
      Element[] children = root.getChildren();
      for (int i = 0; i < children.length; i++) {
        Element child = children[i];
        if (child.getChildCount() == 0) {
          if (criterion.accept(child)) {
            if (child.equals(currentValue)) {
              structure.addElement(new StringObjectPair((String)name.getValue(), new PopupItemWithIcon(factory.createItem(child), currentValueIcon)));
            } else {
              structure.addElement(new StringObjectPair((String)name.getValue(), factory.createItem(child)));
            }
          }
        }
        else if ((child.search(criterion).length > 0) || (criterion.accept(child))) {
          DelayedBindingPopupItem delayedBindingPopupItem = new DelayedBindingPopupItem() {
            public Object createItem() {
              Vector subStructure = PopupMenuUtilities.makeElementStructure(PopupMenuUtilities.this, criterion, factory, context, currentValue);
              if ((subStructure.size() == 1) && (criterion.accept(PopupMenuUtilities.this))) {
                if (equals(currentValue)) {
                  return new PopupItemWithIcon(factory.createItem(PopupMenuUtilities.this), PopupMenuUtilities.currentValueIcon);
                }
                return factory.createItem(PopupMenuUtilities.this);
              }
              
              return subStructure;
            }
            
          };
          structure.addElement(new StringObjectPair((String)name.getValue(), delayedBindingPopupItem));
        }
      }
    }
    

    return structure;
  }
  
  public static Vector makeFlatElementStructure(Element root, Criterion criterion, PopupItemFactory factory, Element context, Object currentValue) {
    Vector structure = new Vector();
    
    Element[] elements = root.search(criterion);
    for (int i = 0; i < elements.length; i++) {
      String text = AuthoringToolResources.getReprForValue(elements[i]);
      if (context != null) {
        text = AuthoringToolResources.getNameInContext(elements[i], context);
      }
      if (!(elements[i] instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)) {
        if (elements[i].equals(currentValue)) {
          structure.addElement(new StringObjectPair(text, new PopupItemWithIcon(factory.createItem(elements[i]), currentValueIcon)));
        } else {
          structure.addElement(new StringObjectPair(text, factory.createItem(elements[i])));
        }
      }
    }
    
    return structure;
  }
  
























  public static Vector makeFlatExpressionStructure(Class valueClass, PopupItemFactory factory, Element context, Object currentValue)
  {
    return makeFlatExpressionStructure(valueClass, null, factory, context, currentValue);
  }
  
  public static Vector makeFlatExpressionStructure(Class valueClass, Criterion criterion, PopupItemFactory factory, Element context, Object currentValue) {
    Vector structure = new Vector();
    if (context != null)
    {
      Expression[] expressions = context.findAccessibleExpressions(valueClass);
      for (int i = 0; i < expressions.length; i++) {
        if ((criterion == null) || (criterion.accept(expressions[i]))) {
          String text = AuthoringToolResources.getReprForValue(expressions[i]);
          if (context != null) {
            text = AuthoringToolResources.getNameInContext(expressions[i], context);
          }
          if ((expressions[i] instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)) {
            CallToUserDefinedQuestionPrototype prototype = new CallToUserDefinedQuestionPrototype((edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)expressions[i]);
            if (prototype.getDesiredProperties().length > 0) {
              structure.addElement(new StringObjectPair(text, makePrototypeStructure(prototype, factory, context)));
            } else {
              structure.addElement(new StringObjectPair(text, factory.createItem(prototype.createNewElement())));
            }
          }
          else if (expressions[i].equals(currentValue)) {
            structure.addElement(new StringObjectPair(text, new PopupItemWithIcon(factory.createItem(expressions[i]), currentValueIcon)));
          } else {
            structure.addElement(new StringObjectPair(text, factory.createItem(expressions[i])));
          }
        }
      }
    }
    

    return structure;
  }
  
  public static Vector makeElementAndExpressionStructure(Element root, Class valueClass, PopupItemFactory factory, boolean makeFlat, Element context, Object currentValue) { Vector structure;
    Vector structure;
    if (makeFlat) {
      structure = makeFlatElementStructure(root, new InstanceOfCriterion(valueClass), factory, context, currentValue);
    } else {
      structure = makeElementStructure(root, new InstanceOfCriterion(valueClass), factory, context, currentValue);
    }
    
    Vector expressionStructure = makeFlatExpressionStructure(valueClass, factory, context, currentValue);
    if ((expressionStructure != null) && (expressionStructure.size() > 0))
    {
      if (structure.size() > 0) {
        structure.add(new StringObjectPair("Separator", JSeparator.class));
      }
      structure.add(new StringObjectPair("expressions", expressionStructure));
    }
    
    return structure;
  }
  


  public static Vector makeResponseStructure(Element element, final PopupItemFactory factory, final Element context)
  {
    Vector structure = new Vector();
    Class valueClass = element.getClass();
    if ((element instanceof Expression)) {
      valueClass = ((Expression)element).getValueClass();
    }
    
    Vector oneShotStructure = AuthoringToolResources.getOneShotStructure(valueClass);
    if ((oneShotStructure != null) && (oneShotStructure.size() > 0)) {
      boolean isFirst = true;
      String[] groupsToUse = AuthoringToolResources.getOneShotGroupsToInclude();
      for (int i = 0; i < oneShotStructure.size(); i++) {
        StringObjectPair sop = (StringObjectPair)oneShotStructure.get(i);
        String currentGroupName = sop.getString();
        boolean useIt = false;
        for (int j = 0; j < groupsToUse.length; j++) {
          if (currentGroupName.compareTo(groupsToUse[j]) == 0) {
            useIt = true;
          }
        }
        
        if (useIt) {
          if (!isFirst) {
            structure.add(new StringObjectPair("separator", JSeparator.class));
          }
          else {
            isFirst = false;
          }
          Vector responseNames = (Vector)sop.getObject();
          structure.addAll(makeOneShotStructure(responseNames, element, factory, context));
        }
      }
      if ((element instanceof CollectionOfModelsVisualization)) {
        final CollectionOfModelsVisualization visualization = (CollectionOfModelsVisualization)element;
        Collection collection = visualization.getItemsCollection();
        if ((((collection instanceof edu.cmu.cs.stage3.alice.core.List)) || ((collection instanceof Array))) && 
          (collection != null) && (values.size() > 0) && (Model.class.isAssignableFrom(valueClass.getClassValue()))) {
          if (structure.size() > 0) {
            structure.add(new StringObjectPair("Separator", JSeparator.class));
          }
          DelayedBindingPopupItem delayedBindingPopupItem = new DelayedBindingPopupItem() {
            public Object createItem() {
              Vector subStructure = new Vector();
              edu.cmu.cs.stage3.alice.core.Question question = null;
              Object[] items = values.getArrayValue();
              if ((PopupMenuUtilities.this instanceof edu.cmu.cs.stage3.alice.core.List))
              {
                question = new edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtBeginning();
                subject.set(visualization);
                Vector responseStructure = PopupMenuUtilities.makeResponseStructure(question, factory, context);
                subStructure.add(new StringObjectPair("item at the beginning", responseStructure));
                

                for (int i = 0; i < items.length; i++) {
                  question = new edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtIndex();
                  subject.set(visualization);
                  index.set(new Double(i));
                  responseStructure = PopupMenuUtilities.makeResponseStructure(question, factory, context);
                  subStructure.add(new StringObjectPair("item " + i, responseStructure));
                }
                

                question = new edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtEnd();
                subject.set(visualization);
                responseStructure = PopupMenuUtilities.makeResponseStructure(question, factory, context);
                subStructure.add(new StringObjectPair("item at the end", responseStructure));
              }
              else if ((PopupMenuUtilities.this instanceof Array)) {
                for (int i = 0; i < items.length; i++) {
                  question = new edu.cmu.cs.stage3.alice.core.question.visualization.array.ItemAtIndex();
                  subject.set(visualization);
                  index.set(new Double(i));
                  Vector responseStructure = PopupMenuUtilities.makeResponseStructure(question, factory, context);
                  subStructure.add(new StringObjectPair("element " + i, responseStructure));
                }
              }
              return subStructure;
            }
          };
          structure.add(new StringObjectPair("item responses", delayedBindingPopupItem));
        }
      }
      
      if ((element instanceof edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization)) {
        edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization visualization = (edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization)element;
        structure.add(new StringObjectPair("Separator", JSeparator.class));
        edu.cmu.cs.stage3.alice.core.Question question = new edu.cmu.cs.stage3.alice.core.question.visualization.model.Item();
        subject.set(visualization);
        Vector responseStructure = makeResponseStructure(question, factory, context);
        structure.add(new StringObjectPair("item", responseStructure));
      }
    }
    
    return structure;
  }
  


  public static Vector makeExpressionResponseStructure(final Expression expression, final PopupItemFactory factory, final Element context)
  {
    Vector structure = new Vector();
    

    if ((expression instanceof Variable))
    {
      StringObjectPair[] known = { new StringObjectPair("element", expression), new StringObjectPair("propertyName", "value"), new StringObjectPair("duration", new Integer(0)) };
      String[] desired = { "value" };
      ResponsePrototype rp = new ResponsePrototype(PropertyAnimation.class, known, desired);
      Vector setValueStructure = makePrototypeStructure(rp, factory, context);
      if ((setValueStructure != null) && (!setValueStructure.isEmpty())) {
        structure.add(new StringObjectPair("set value", setValueStructure));
      }
    }
    

    Vector oneShotStructure = AuthoringToolResources.getOneShotStructure(expression.getValueClass());
    if ((oneShotStructure != null) && (oneShotStructure.size() > 0)) {
      boolean isFirst = true;
      String[] groupsToUse = AuthoringToolResources.getOneShotGroupsToInclude();
      for (int i = 0; i < oneShotStructure.size(); i++) {
        StringObjectPair sop = (StringObjectPair)oneShotStructure.get(i);
        String currentGroupName = sop.getString();
        boolean useIt = false;
        for (int j = 0; j < groupsToUse.length; j++) {
          if (currentGroupName.compareTo(groupsToUse[j]) == 0) {
            useIt = true;
          }
        }
        
        if (useIt) {
          if (!isFirst) {
            structure.add(new StringObjectPair("separator", JSeparator.class));
          }
          else {
            isFirst = false;
          }
          Vector responseNames = (Vector)sop.getObject();
          Vector subStructure = makeOneShotStructure(responseNames, expression, factory, context);
          
          if (subStructure.size() > 0) {
            if (structure.size() > 0) {
              structure.add(new StringObjectPair("Separator", JSeparator.class));
            }
            structure.addAll(subStructure);
          }
        }
      }
    }
    


    if ((expression instanceof Variable)) {
      Variable variable = (Variable)expression;
      if (Collection.class.isAssignableFrom(expression.getValueClass())) {
        Collection collection = (Collection)value.get();
        if ((((collection instanceof edu.cmu.cs.stage3.alice.core.List)) || ((collection instanceof Array))) && 
          (collection != null) && (values.size() > 0) && (Model.class.isAssignableFrom(valueClass.getClassValue()))) {
          if (structure.size() > 0) {
            structure.add(new StringObjectPair("Separator", JSeparator.class));
          }
          DelayedBindingPopupItem delayedBindingPopupItem = new DelayedBindingPopupItem() {
            public Object createItem() {
              Vector subStructure = new Vector();
              Object[] items = values.getArrayValue();
              for (int i = 0; i < items.length; i++) {
                edu.cmu.cs.stage3.alice.core.Question question = null;
                if ((PopupMenuUtilities.this instanceof edu.cmu.cs.stage3.alice.core.List)) {
                  question = new edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex();
                  list.set(expression);
                  index.set(new Double(i));
                } else if ((PopupMenuUtilities.this instanceof Array)) {
                  question = new edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex();
                  array.set(expression);
                  index.set(new Double(i));
                }
                Vector responseStructure = PopupMenuUtilities.makeResponseStructure(question, factory, context);
                subStructure.add(new StringObjectPair("item" + i, responseStructure));
              }
              return subStructure;
            }
          };
          structure.add(new StringObjectPair("item responses", delayedBindingPopupItem));
        }
      }
    }
    

    return structure;
  }
  
  public static Vector makeOneShotStructure(Vector responseNames, Element element, PopupItemFactory factory, Element context) {
    Vector structure = new Vector();
    if (responseNames != null) {
      for (Iterator iter = responseNames.iterator(); iter.hasNext();) {
        Object item = iter.next();
        if ((item instanceof String)) {
          String className = (String)item;
          try {
            if (className.startsWith("edu.cmu.cs.stage3.alice.core.response.PropertyAnimation")) {
              String propertyName = AuthoringToolResources.getSpecifier(className);
              
              if (propertyName.equals("vehicle")) {
                StringObjectPair[] knownPropertyValues = {
                  new StringObjectPair("element", element), 
                  new StringObjectPair("propertyName", propertyName), 
                  new StringObjectPair("duration", new Double(0.0D)) };
                
                String[] desiredProperties = { "value" };
                ResponsePrototype responsePrototype = new ResponsePrototype(edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation.class, knownPropertyValues, desiredProperties);
                String responseName = AuthoringToolResources.getFormattedReprForValue(PropertyAnimation.class, knownPropertyValues);
                Vector subStructure = makePrototypeStructure(responsePrototype, factory, context);
                structure.add(new StringObjectPair(responseName, subStructure));
              } else {
                StringObjectPair[] knownPropertyValues = {
                  new StringObjectPair("element", element), 
                  new StringObjectPair("propertyName", propertyName) };
                
                String[] desiredProperties = { "value" };
                ResponsePrototype responsePrototype = new ResponsePrototype(PropertyAnimation.class, knownPropertyValues, desiredProperties);
                String responseName = AuthoringToolResources.getFormattedReprForValue(PropertyAnimation.class, knownPropertyValues);
                Vector subStructure = makePrototypeStructure(responsePrototype, factory, context);
                structure.add(new StringObjectPair(responseName, subStructure));
              }
            } else {
              Class responseClass = Class.forName(className);
              java.util.LinkedList known = new java.util.LinkedList();
              String format = AuthoringToolResources.getFormat(responseClass);
              FormatTokenizer tokenizer = new FormatTokenizer(format);
              while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                
                if ((token.startsWith("<<<")) && (token.endsWith(">>>"))) {
                  String propertyName = token.substring(token.lastIndexOf("<") + 1, token.indexOf(">"));
                  
                  known.add(new StringObjectPair(propertyName, element));
                }
              }
              StringObjectPair[] knownPropertyValues = (StringObjectPair[])known.toArray(new StringObjectPair[0]);
              String[] desiredProperties = AuthoringToolResources.getDesiredProperties(responseClass);
              ResponsePrototype responsePrototype = new ResponsePrototype(responseClass, knownPropertyValues, desiredProperties);
              String responseName = AuthoringToolResources.getFormattedReprForValue(responseClass, knownPropertyValues);
              
              if (responsePrototype.getDesiredProperties().length > 0) {
                Vector subStructure = makePrototypeStructure(responsePrototype, factory, context);
                structure.add(new StringObjectPair(responseName, subStructure));
              } else {
                structure.add(new StringObjectPair(responseName, factory.createItem(responsePrototype)));
              }
            }
          } catch (Throwable t) {
            AuthoringTool.showErrorDialog("Error creating popup item.", t);
          }
        } else if ((item instanceof StringObjectPair)) {
          try {
            String label = ((StringObjectPair)item).getString();
            Vector subResponseNames = (Vector)((StringObjectPair)item).getObject();
            Vector subStructure = makeOneShotStructure(subResponseNames, element, factory, context);
            structure.add(new StringObjectPair(label, subStructure));
          } catch (Throwable t) {
            AuthoringTool.showErrorDialog("Error creating popup item.", t);
          }
        }
      }
    }
    
    return structure;
  }
  


  public static Vector makePropertyAssignmentForUserDefinedQuestionStructure(Property property, PopupItemFactory factory, Element context)
  {
    Vector structure = new Vector();
    
    StringObjectPair[] known = { new StringObjectPair("element", property.getOwner()), new StringObjectPair("propertyName", property.getName()) };
    String[] desired = { "value" };
    ElementPrototype ep = new ElementPrototype(PropertyAssignment.class, known, desired);
    Vector setValueStructure = makePrototypeStructure(ep, factory, context);
    if ((setValueStructure != null) && (!setValueStructure.isEmpty())) {
      structure.add(new StringObjectPair("set value", setValueStructure));
    }
    
    return structure;
  }
  





  public static Vector makePrototypeStructure(final ElementPrototype elementPrototype, final PopupItemFactory factory, final Element context)
  {
    Vector structure = new Vector();
    final String[] desiredProperties = elementPrototype.getDesiredProperties();
    if ((desiredProperties == null) || (desiredProperties.length == 0)) {
      structure.add(new StringObjectPair("no properties to set on " + elementPrototype.getElementClass().getName() + "; please report this bug", factory.createItem(elementPrototype)));
    } else if (desiredProperties.length > 0) {
      String preRepr = elementPrototype.getElementClass().getName() + "." + desiredProperties[0];
      String propertyRepr = AuthoringToolResources.getReprForValue(preRepr);
      if (propertyRepr.equals(preRepr)) {
        propertyRepr = desiredProperties[0];
      }
      
      structure.add(new StringObjectPair(propertyRepr, null));
      
      Class preValueClass = null;
      Vector preDefaultStructure = null;
      if (PropertyAnimation.class.isAssignableFrom(elementPrototype.getElementClass())) {
        preDefaultStructure = getDefaultValueStructureForPropertyAnimation(elementPrototype.getKnownPropertyValues());
        preValueClass = getValueClassForPropertyAnimation(elementPrototype.getKnownPropertyValues());
      } else if (PropertyAssignment.class.isAssignableFrom(elementPrototype.getElementClass())) {
        preDefaultStructure = getDefaultValueStructureForPropertyAnimation(elementPrototype.getKnownPropertyValues());
        preValueClass = getValueClassForPropertyAnimation(elementPrototype.getKnownPropertyValues());
      } else if ((edu.cmu.cs.stage3.alice.core.question.list.ListBooleanQuestion.class.isAssignableFrom(elementPrototype.getElementClass())) || 
        (edu.cmu.cs.stage3.alice.core.question.list.ListNumberQuestion.class.isAssignableFrom(elementPrototype.getElementClass())) || 
        (edu.cmu.cs.stage3.alice.core.question.list.ListObjectQuestion.class.isAssignableFrom(elementPrototype.getElementClass())) || 
        (edu.cmu.cs.stage3.alice.core.response.list.ListResponse.class.isAssignableFrom(elementPrototype.getElementClass())))
      {
        if (desiredProperties[0].equals("item")) {
          preValueClass = getValueClassForList(elementPrototype.getKnownPropertyValues());
          preDefaultStructure = getDefaultValueStructureForClass(preValueClass);
        } else if (desiredProperties[0].equals("index")) {
          preValueClass = Element.getValueClassForPropertyNamed(elementPrototype.getElementClass(), desiredProperties[0]);
          preDefaultStructure = getDefaultValueStructureForCollectionIndexProperty(elementPrototype.getKnownPropertyValues());
        } else {
          preValueClass = Element.getValueClassForPropertyNamed(elementPrototype.getElementClass(), desiredProperties[0]);
          preDefaultStructure = getDefaultValueStructureForProperty(elementPrototype.getElementClass(), desiredProperties[0]);
        }
      } else if ((edu.cmu.cs.stage3.alice.core.question.array.ArrayNumberQuestion.class.isAssignableFrom(elementPrototype.getElementClass())) || 
        (edu.cmu.cs.stage3.alice.core.question.array.ArrayObjectQuestion.class.isAssignableFrom(elementPrototype.getElementClass())) || 
        (edu.cmu.cs.stage3.alice.core.response.array.ArrayResponse.class.isAssignableFrom(elementPrototype.getElementClass())))
      {
        if (desiredProperties[0].equals("item")) {
          preValueClass = getValueClassForArray(elementPrototype.getKnownPropertyValues());
          preDefaultStructure = getDefaultValueStructureForClass(preValueClass);
        } else if (desiredProperties[0].equals("index")) {
          preValueClass = Element.getValueClassForPropertyNamed(elementPrototype.getElementClass(), desiredProperties[0]);
          preDefaultStructure = getDefaultValueStructureForCollectionIndexProperty(elementPrototype.getKnownPropertyValues());
        } else {
          preValueClass = Element.getValueClassForPropertyNamed(elementPrototype.getElementClass(), desiredProperties[0]);
          preDefaultStructure = getDefaultValueStructureForProperty(elementPrototype.getElementClass(), desiredProperties[0]);
        }
      } else if ((elementPrototype instanceof CallToUserDefinedResponsePrototype)) {
        edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse actualResponse = ((CallToUserDefinedResponsePrototype)elementPrototype).getActualResponse();
        Object[] params = requiredFormalParameters.getArrayValue();
        for (int i = 0; i < params.length; i++) {
          if (name.getStringValue().equals(desiredProperties[0])) {
            preValueClass = (Class)valueClass.getValue();
            break;
          }
        }
        preDefaultStructure = getDefaultValueStructureForClass(preValueClass);
      } else if ((elementPrototype instanceof CallToUserDefinedQuestionPrototype)) {
        edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion actualQuestion = ((CallToUserDefinedQuestionPrototype)elementPrototype).getActualQuestion();
        Object[] params = requiredFormalParameters.getArrayValue();
        for (int i = 0; i < params.length; i++) {
          if (name.getStringValue().equals(desiredProperties[0])) {
            preValueClass = (Class)valueClass.getValue();
            break;
          }
        }
        preDefaultStructure = getDefaultValueStructureForClass(preValueClass);
      } else if ((edu.cmu.cs.stage3.alice.core.question.IsEqualTo.class.isAssignableFrom(elementPrototype.getElementClass())) || 
        (edu.cmu.cs.stage3.alice.core.question.IsNotEqualTo.class.isAssignableFrom(elementPrototype.getElementClass()))) {
        preValueClass = getValueClassForComparator(elementPrototype.knownPropertyValues);
        preDefaultStructure = getDefaultValueStructureForClass(preValueClass);
      } else {
        preValueClass = Element.getValueClassForPropertyNamed(elementPrototype.getElementClass(), desiredProperties[0]);
        preDefaultStructure = getDefaultValueStructureForProperty(elementPrototype.getElementClass(), desiredProperties[0]);
      }
      


      Class valueClass = preValueClass;
      
      PopupItemFactory recursiveFactory = new PopupItemFactory() {
        public Object createItem(Object o) {
          if ((PopupMenuUtilities.this != null) && (!isInstance(o))) {
            if ((isAssignableFrom(javax.vecmath.Vector3d.class)) && ((o instanceof edu.cmu.cs.stage3.alice.core.Transformable))) {
              edu.cmu.cs.stage3.alice.core.question.Position positionQuestion = new edu.cmu.cs.stage3.alice.core.question.Position();
              subject.set(o);
              o = positionQuestion;
            } else if ((isAssignableFrom(javax.vecmath.Matrix4d.class)) && ((o instanceof edu.cmu.cs.stage3.alice.core.Transformable))) {
              edu.cmu.cs.stage3.alice.core.question.PointOfView POVQuestion = new edu.cmu.cs.stage3.alice.core.question.PointOfView();
              subject.set(o);
              o = POVQuestion;
            } else if ((isAssignableFrom(edu.cmu.cs.stage3.math.Quaternion.class)) && ((o instanceof edu.cmu.cs.stage3.alice.core.Transformable))) {
              edu.cmu.cs.stage3.alice.core.question.Quaternion quaternionQuestion = new edu.cmu.cs.stage3.alice.core.question.Quaternion();
              subject.set(o);
              o = quaternionQuestion;
            }
          }
          if (desiredProperties.length == 1)
          {
            return factory.createItem(elementPrototype.createCopy(new StringObjectPair(desiredProperties[0], o)));
          }
          
          return PopupMenuUtilities.makePrototypeStructure(elementPrototype.createCopy(new StringObjectPair(desiredProperties[0], o)), factory, context);
        }
        

      };
      Vector defaultStructure = processStructure(preDefaultStructure, recursiveFactory, NO_CURRENT_VALUE);
      
      Vector recentlyUsedStructure = new Vector();
      if (recentlyUsedValues.containsKey(preValueClass)) {
        java.util.List recentList = (java.util.List)recentlyUsedValues.get(preValueClass);
        int count = 0;
        for (Iterator iter = recentList.iterator(); iter.hasNext();) {
          if (count > Integer.parseInt(authoringToolConfig.getValue("maxRecentlyUsedValues"))) {
            break;
          }
          Object value = iter.next();
          if (!structureContains(preDefaultStructure, value)) {
            recentlyUsedStructure.add(value);
            count++;
          }
        }
      }
      


      if (!defaultStructure.isEmpty()) {
        if (structure.size() > 0) {
          structure.add(new StringObjectPair("Separator", JSeparator.class));
        }
        structure.addAll(defaultStructure);
      }
      

      if (!recentlyUsedStructure.isEmpty()) {
        if (structure.size() > 0) {
          structure.add(new StringObjectPair("Separator", JSeparator.class));
        }
        addLabelsToValueStructure(recentlyUsedStructure, elementPrototype.getElementClass(), desiredProperties[0]);
        structure.addAll(processStructure(recentlyUsedStructure, recursiveFactory, NO_CURRENT_VALUE));
      }
      



      InstanceOfCriterion instanceOf = new InstanceOfCriterion(valueClass);
      Criterion elementIsNamed = new Criterion() {
        public boolean accept(Object o) {
          if (((o instanceof Element)) && 
            (name.get() != null)) {
            return true;
          }
          
          return false;
        }
      };
      InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
      Criterion criterion = new MatchesAllCriterion(new Criterion[] { instanceOf, elementIsNamed, inAppropriateOAPCriterion });
      Class elementClass = elementPrototype.getElementClass();
      StringObjectPair[] knownPropertyValues = elementPrototype.getKnownPropertyValues();
      


      if (((edu.cmu.cs.stage3.alice.core.response.AbstractPointAtAnimation.class.isAssignableFrom(elementClass)) && (desiredProperties[0].equals("target"))) || (
        (edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation.class.isAssignableFrom(elementPrototype.elementClass)) && (desiredProperties[0].equals("value")))) {
        for (int i = 0; i < knownPropertyValues.length; i++) {
          String propertyName = knownPropertyValues[i].getString();
          if ((propertyName.equals("subject")) || (propertyName.equals("element"))) {
            Object transformableValue = knownPropertyValues[i].getObject();
            Criterion notSelf = new Criterion() {
              public boolean accept(Object o) {
                if (o == PopupMenuUtilities.this) {
                  return false;
                }
                return true;
              }
            };
            criterion = new MatchesAllCriterion(new Criterion[] { instanceOf, elementIsNamed, inAppropriateOAPCriterion, notSelf });
            break;
          }
        }
      }
      
      if ((valueClass != null) && ((Element.class.isAssignableFrom(valueClass)) || (valueClass.isAssignableFrom(Element.class)))) {
        Element[] elements = context.getRoot().search(criterion);
        if (elements.length > 0) {
          if (elements.length < 10) {
            if (structure.size() > 0) {
              structure.add(new StringObjectPair("Separator", JSeparator.class));
            }
            structure.addAll(makeFlatElementStructure(context.getRoot(), criterion, recursiveFactory, context, NO_CURRENT_VALUE));
          } else {
            if (structure.size() > 0) {
              structure.add(new StringObjectPair("Separator", JSeparator.class));
            }
            structure.addAll(makeElementStructure(context.getRoot(), criterion, recursiveFactory, context, NO_CURRENT_VALUE));
          }
        }
      }
      if ((valueClass != null) && (!Object.class.isAssignableFrom(valueClass)))
      {
        if ((javax.vecmath.Vector3d.class.isAssignableFrom(valueClass)) || (valueClass.isAssignableFrom(javax.vecmath.Vector3d.class))) {
          Criterion modelCriterion = new MatchesAllCriterion(new Criterion[] { new InstanceOfCriterion(Model.class), elementIsNamed, inAppropriateOAPCriterion });
          Element[] elements = context.getRoot().search(modelCriterion);
          if (elements.length > 0) {
            if (elements.length < 10) {
              if (structure.size() > 0) {
                structure.add(new StringObjectPair("Separator", JSeparator.class));
              }
              structure.addAll(makeFlatElementStructure(context.getRoot(), modelCriterion, recursiveFactory, context, NO_CURRENT_VALUE));
            } else {
              if (structure.size() > 0) {
                structure.add(new StringObjectPair("Separator", JSeparator.class));
              }
              structure.addAll(makeElementStructure(context.getRoot(), modelCriterion, recursiveFactory, context, NO_CURRENT_VALUE));
            }
          }
        }
        if (valueClass.isAssignableFrom(javax.vecmath.Matrix4d.class)) {
          Criterion modelCriterion = new MatchesAllCriterion(new Criterion[] { new InstanceOfCriterion(Model.class), elementIsNamed, inAppropriateOAPCriterion });
          Element[] elements = context.getRoot().search(modelCriterion);
          if (elements.length > 0) {
            if (elements.length < 10) {
              if (structure.size() > 0) {
                structure.add(new StringObjectPair("Separator", JSeparator.class));
              }
              structure.addAll(makeFlatElementStructure(context.getRoot(), modelCriterion, recursiveFactory, context, NO_CURRENT_VALUE));
            } else {
              if (structure.size() > 0) {
                structure.add(new StringObjectPair("Separator", JSeparator.class));
              }
              structure.addAll(makeElementStructure(context.getRoot(), modelCriterion, recursiveFactory, context, NO_CURRENT_VALUE));
            }
          }
        }
        if (valueClass.isAssignableFrom(edu.cmu.cs.stage3.math.Quaternion.class)) {
          Criterion modelCriterion = new MatchesAllCriterion(new Criterion[] { new InstanceOfCriterion(Model.class), elementIsNamed, inAppropriateOAPCriterion });
          Element[] elements = context.getRoot().search(modelCriterion);
          if (elements.length > 0) {
            if (elements.length < 10) {
              if (structure.size() > 0) {
                structure.add(new StringObjectPair("Separator", JSeparator.class));
              }
              structure.addAll(makeFlatElementStructure(context.getRoot(), modelCriterion, recursiveFactory, context, NO_CURRENT_VALUE));
            } else {
              if (structure.size() > 0) {
                structure.add(new StringObjectPair("Separator", JSeparator.class));
              }
              structure.addAll(makeElementStructure(context.getRoot(), modelCriterion, recursiveFactory, context, NO_CURRENT_VALUE));
            }
          }
        }
      }
      




      PopupItemFactory metaFactory = new PopupItemFactory() {
        public Object createItem(final Object o) {
          new Runnable() {
            public void run() {
              ((Runnable)val$factory.createItem(val$elementPrototype.createCopy(new StringObjectPair(val$desiredProperties[0], o)))).run();
            }
          };
        }
      };
      if ((valueClass != null) && (valueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.Sound.class)) && (valueClass != Object.class)) {
        File soundDir = new File(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceHomeDirectory(), "sounds").getAbsoluteFile();
        if ((soundDir.exists()) && (soundDir.isDirectory())) {
          java.util.ArrayList sounds = new java.util.ArrayList();
          File[] fileList = soundDir.listFiles();
          for (int i = 0; i < fileList.length; i++) {
            if ((fileList[i].isFile()) && (fileList[i].canRead())) {
              sounds.add(fileList[i]);
            }
          }
          
          if (sounds.size() > 0) {
            if (structure.size() > 0) {
              structure.add(new StringObjectPair("Separator", JSeparator.class));
            }
            for (Iterator iter = sounds.iterator(); iter.hasNext();) {
              File soundFile = (File)iter.next();
              String name = soundFile.getName();
              name = name.substring(0, name.lastIndexOf('.'));
              Runnable importSoundRunnable = new Runnable() {
                public void run() {
                  edu.cmu.cs.stage3.alice.core.Sound sound = (edu.cmu.cs.stage3.alice.core.Sound)AuthoringTool.getHack().importElement(PopupMenuUtilities.this, context.getSandbox());
                  if (sound != null) {
                    ((Runnable)factory.createItem(elementPrototype.createCopy(new StringObjectPair(desiredProperties[0], sound)))).run();
                  }
                  
                }
                
              };
              structure.add(new StringObjectPair(name, importSoundRunnable));
            }
          }
        }
        
        if (structure.size() > 0) {
          structure.add(new StringObjectPair("Separator", JSeparator.class));
        }
        Runnable importRunnable = new Runnable()
        {
          public void run() {
            PropertyPopupPostImportRunnable propertyPopupPostImportRunnable = new PropertyPopupPostImportRunnable(PopupMenuUtilities.this);
            AuthoringTool.getHack().setImportFileFilter("Sound Files");
            AuthoringTool.getHack().importElement(null, context.getSandbox(), propertyPopupPostImportRunnable);
          }
        };
        Runnable recordRunnable = new Runnable() {
          public void run() {
            edu.cmu.cs.stage3.alice.core.Sound sound = AuthoringTool.getHack().promptUserForRecordedSound(getSandbox());
            if (sound != null) {
              ((Runnable)factory.createItem(elementPrototype.createCopy(new StringObjectPair(desiredProperties[0], sound)))).run();
            }
          }
        };
        structure.add(new StringObjectPair(Messages.getString("import_sound_file___"), importRunnable));
        structure.add(new StringObjectPair(Messages.getString("record_new_sound___"), recordRunnable));
      }
      

      Vector expressionStructure = makeFlatExpressionStructure(valueClass, recursiveFactory, context, NO_CURRENT_VALUE);
      if ((expressionStructure != null) && (expressionStructure.size() > 0)) {
        if (structure.size() > 0) {
          structure.add(new StringObjectPair("Separator", JSeparator.class));
        }
        structure.add(new StringObjectPair("expressions", expressionStructure));
      }
      
      boolean nullValid;
      boolean nullValid;
      if ((elementPrototype instanceof CallToUserDefinedResponsePrototype)) {
        nullValid = true; } else { boolean nullValid;
        if ((elementPrototype instanceof CallToUserDefinedQuestionPrototype)) {
          nullValid = true;
        } else {
          nullValid = AuthoringToolResources.shouldGUIIncludeNone(elementPrototype.getElementClass(), desiredProperties[0]);
        }
      }
      if (nullValid) {
        if (structure.size() > 0) {
          structure.add(new StringObjectPair("Separator", JSeparator.class));
        }
        String nullRepr = AuthoringToolResources.getReprForValue(null, elementClass, desiredProperties[0], "menuContext");
        if (desiredProperties.length == 1) {
          structure.add(new StringObjectPair(nullRepr, factory.createItem(elementPrototype.createCopy(new StringObjectPair(desiredProperties[0], null)))));
        } else {
          structure.add(new StringObjectPair(nullRepr, makePrototypeStructure(elementPrototype.createCopy(new StringObjectPair(desiredProperties[0], null)), factory, context)));
        }
      }
      

      final PopupItemFactory otherFactory = new PopupItemFactory() {
        public Object createItem(final Object o) {
          new Runnable() {
            public void run() {
              ((Runnable)val$factory.createItem(val$elementPrototype.createCopy(new StringObjectPair(val$desiredProperties[0], o)))).run();
            }
          };
        }
      };
      if ((desiredProperties.length == 1) && (GUIFactory.isOtherDialogSupportedForClass(valueClass))) {
        if (structure.size() > 0) {
          structure.add(new StringObjectPair("Separator", JSeparator.class));
        }
        Runnable runnable = new Runnable()
        {
          public void run() {
            GUIFactory.showOtherDialog(PopupMenuUtilities.this, null, otherFactory, context);
          }
        };
        structure.add(new StringObjectPair(Messages.getString("other..."), runnable));
      }
      

      if ((edu.cmu.cs.stage3.alice.core.List.class.isAssignableFrom(valueClass)) && (desiredProperties.length == 1)) {
        if (structure.size() > 0) {
          structure.add(new StringObjectPair("Separator", JSeparator.class));
        }
        Runnable createNewListRunnable = new Runnable() {
          public void run() {
            AuthoringTool authoringTool = AuthoringTool.getHack();
            ObjectArrayProperty variables = getSandbox().variables;
            Variable variable = authoringTool.showNewVariableDialog(Messages.getString("Create_new_list"), getRoot(), true, true);
            if (variable != null) {
              if (variables != null) {
                authoringTool.getUndoRedoStack().startCompound();
                try {
                  variables.getOwner().addChild(variable);
                  variables.add(variable);
                } finally {
                  authoringTool.getUndoRedoStack().stopCompound();
                }
              }
              ((Runnable)factory.createItem(elementPrototype.createCopy(new StringObjectPair(desiredProperties[0], variable)))).run();
            }
          }
        };
        structure.add(new StringObjectPair(Messages.getString("create_new_list___"), createNewListRunnable));
      }
    }
    
    return structure;
  }
  
  /**
   * @deprecated
   */
  public static Vector makePropertyStructure(Property property, PopupItemFactory factory, boolean includeDefaults, boolean includeExpressions, boolean includeOther) {
    return makePropertyStructure(property, factory, includeDefaults, includeExpressions, includeOther, null);
  }
  




  public static Vector makePropertyStructure(Property property, final PopupItemFactory factory, boolean includeDefaults, boolean includeExpressions, boolean includeOther, Element root)
  {
    if (root == null) {
      root = property.getOwner().getRoot();
    }
    Vector structure = new Vector();
    Class targetValueClass = getDesiredValueClass(property);
    if (edu.cmu.cs.stage3.alice.core.List.class.isAssignableFrom(targetValueClass)) {
      Element parent = property.getOwner().getParent();
      edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = parent.getPropertyReferencesTo(property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false);
      if (((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.list.ListObjectQuestion)) && (references.length > 0)) {
        Class itemValueClass = references[0].getProperty().getValueClass();
        Criterion criterion = new Criterion() {
          public boolean accept(Object o) {
            if ((o instanceof Variable)) {
              edu.cmu.cs.stage3.alice.core.List list = (edu.cmu.cs.stage3.alice.core.List)((Variable)o).getValue();
              if ((list != null) && 
                (isAssignableFrom(valueClass.getClassValue()))) {
                return true;
              }
            }
            
            return false;
          }
        };
        structure = makeFlatExpressionStructure(targetValueClass, criterion, factory, property.getOwner(), property.get());
        
        if (!AuthoringToolResources.shouldGUIOmitNone(property)) {
          if (structure.size() > 0) {
            structure.add(new StringObjectPair("Separator", JSeparator.class));
          }
          if (property.get() == null) {
            structure.add(new StringObjectPair(AuthoringToolResources.getReprForValue(null, property, "menuContext"), new PopupItemWithIcon(factory.createItem(null), currentValueIcon)));
          } else {
            structure.add(new StringObjectPair(AuthoringToolResources.getReprForValue(null, property, "menuContext"), factory.createItem(null)));
          }
        }
      } else {
        structure = makeFlatExpressionStructure(targetValueClass, factory, property.getOwner(), property.get());
        
        if (!AuthoringToolResources.shouldGUIOmitNone(property)) {
          if (structure.size() > 0) {
            structure.add(new StringObjectPair("Separator", JSeparator.class));
          }
          if (property.get() == null) {
            structure.add(new StringObjectPair(AuthoringToolResources.getReprForValue(null, property, "menuContext"), new PopupItemWithIcon(factory.createItem(null), currentValueIcon)));
          } else {
            structure.add(new StringObjectPair(AuthoringToolResources.getReprForValue(null, property, "menuContext"), factory.createItem(null)));
          }
        }
        

        if (structure.size() > 0) {
          structure.add(new StringObjectPair("Separator", JSeparator.class));
        }
        Runnable createNewListRunnable = new Runnable() {
          public void run() {
            AuthoringTool authoringTool = AuthoringTool.getHack();
            ObjectArrayProperty variables = getOwner().getSandbox().variables;
            Variable variable = authoringTool.showNewVariableDialog(Messages.getString("Create_new_list"), getOwner().getRoot(), true, true);
            if (variable != null) {
              if (variables != null) {
                variables.getOwner().addChild(variable);
                variables.add(variable);
              }
              ((Runnable)factory.createItem(variable)).run();
            }
          }
        };
        structure.add(new StringObjectPair(Messages.getString("create_new_list___"), createNewListRunnable));
      }
    } else if (Array.class.isAssignableFrom(targetValueClass)) {
      Element parent = property.getOwner().getParent();
      edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = parent.getPropertyReferencesTo(property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false);
      if (((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.array.ArrayObjectQuestion)) && (references.length > 0)) {
        Class itemValueClass = references[0].getProperty().getValueClass();
        Criterion criterion = new Criterion() {
          public boolean accept(Object o) {
            if ((o instanceof Variable)) {
              Array array = (Array)((Variable)o).getValue();
              if ((array != null) && 
                (isAssignableFrom(valueClass.getClassValue()))) {
                return true;
              }
            }
            
            return false;
          }
        };
        structure = makeFlatExpressionStructure(targetValueClass, criterion, factory, property.getOwner(), property.get());
        
        if (!AuthoringToolResources.shouldGUIOmitNone(property)) {
          if (structure.size() > 0) {
            structure.add(new StringObjectPair("Separator", JSeparator.class));
          }
          if (property.get() == null) {
            structure.add(new StringObjectPair(AuthoringToolResources.getReprForValue(null, property, "menuContext"), new PopupItemWithIcon(factory.createItem(null), currentValueIcon)));
          } else {
            structure.add(new StringObjectPair(AuthoringToolResources.getReprForValue(null, property, "menuContext"), factory.createItem(null)));
          }
        }
      } else {
        structure = makeFlatExpressionStructure(targetValueClass, factory, property.getOwner(), property.get());
        
        if (!AuthoringToolResources.shouldGUIOmitNone(property)) {
          if (structure.size() > 0) {
            structure.add(new StringObjectPair("Separator", JSeparator.class));
          }
          if (property.get() == null) {
            structure.add(new StringObjectPair(AuthoringToolResources.getReprForValue(null, property, "menuContext"), new PopupItemWithIcon(factory.createItem(null), currentValueIcon)));
          } else {
            structure.add(new StringObjectPair(AuthoringToolResources.getReprForValue(null, property, "menuContext"), factory.createItem(null)));
          }
        }
      }
    } else {
      if (includeDefaults) {
        if (Response.class.isAssignableFrom(targetValueClass)) {
          final Element context = root;
          Property referenceProperty = property;
          PopupItemFactory userDefinedResponsePopupFactory = new PopupItemFactory() {
            public Object createItem(Object o) {
              edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse userDefinedResponse = (edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse)o;
              CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = new CallToUserDefinedResponsePrototype(userDefinedResponse);
              callToUserDefinedResponsePrototype.calculateDesiredProperties();
              PopupItemFactory prototypePopupFactory = new PopupItemFactory() {
                public Object createItem(Object prototype) {
                  CallToUserDefinedResponsePrototype completedPrototype = (CallToUserDefinedResponsePrototype)prototype;
                  return val$factory.createItem(completedPrototype.createNewElement());
                }
              };
              if (requiredFormalParameters.size() > 0) {
                if (((getOwner() instanceof edu.cmu.cs.stage3.alice.core.behavior.MouseButtonClickBehavior)) || 
                  ((getOwner() instanceof edu.cmu.cs.stage3.alice.core.behavior.MouseButtonIsPressedBehavior))) {
                  return PopupMenuUtilities.makePrototypeStructure(callToUserDefinedResponsePrototype, prototypePopupFactory, getOwner());
                }
                return PopupMenuUtilities.makePrototypeStructure(callToUserDefinedResponsePrototype, prototypePopupFactory, context);
              }
              
              return prototypePopupFactory.createItem(callToUserDefinedResponsePrototype);
            }
            
          };
          InstanceOfCriterion criterion = new InstanceOfCriterion(edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse.class);
          structure.addAll(makeElementStructure(root, criterion, userDefinedResponsePopupFactory, root, property.get()));

        }
        else
        {

          Vector defaultStructure = getDefaultValueStructureForProperty(property);
          

















          Vector recentlyUsedStructure = new Vector();
          if (recentlyUsedValues.containsKey(targetValueClass)) {
            java.util.List recentList = (java.util.List)recentlyUsedValues.get(targetValueClass);
            int count = 0;
            for (Iterator iter = recentList.iterator(); iter.hasNext();) {
              if (count > Integer.parseInt(authoringToolConfig.getValue("maxRecentlyUsedValues"))) {
                break;
              }
              Object value = iter.next();
              if (!structureContains(defaultStructure, value)) {
                recentlyUsedStructure.add(value);
                count++;
              }
            }
          }
          

          Object currentValue = property.get();
          Vector unlabeledDefaultValueStructure = getUnlabeledDefaultValueStructureForProperty(property.getOwner().getClass(), property.getName(), property);
          if ((currentValue != null) && 
            (!unlabeledDefaultValueStructure.contains(currentValue)) && (!recentlyUsedStructure.contains(currentValue)) && 
            (!(currentValue instanceof Expression))) {
            addRecentlyUsedValue(currentValue.getClass(), currentValue);
            recentlyUsedStructure.add(0, currentValue);
            if (recentlyUsedStructure.size() > Integer.parseInt(authoringToolConfig.getValue("maxRecentlyUsedValues"))) {
              recentlyUsedStructure.remove(recentlyUsedStructure.size() - 1);
            }
          }
          

          if (structure.size() > 0) {
            structure.add(new StringObjectPair("Separator", JSeparator.class));
          }
          structure.addAll(processStructure(defaultStructure, factory, property.get()));
          if ((!recentlyUsedStructure.isEmpty()) && (!property.getName().equalsIgnoreCase("keyCode"))) {
            if (structure.size() > 0) {
              structure.add(new StringObjectPair("Separator", JSeparator.class));
            }
            addLabelsToValueStructure(recentlyUsedStructure, property.getOwner().getClass(), property.getName());
            structure.addAll(processStructure(recentlyUsedStructure, factory, property.get()));
          }
          

          if ((Element.class.isAssignableFrom(targetValueClass)) || (targetValueClass.isAssignableFrom(Element.class))) { Criterion criterion;
            Criterion criterion;
            if (((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Behavior)) && (Response.class.isAssignableFrom(targetValueClass))) {
              criterion = new Criterion() {
                public boolean accept(Object o) {
                  if (((o instanceof Response)) && 
                    (!(((Response)o).getParent() instanceof Response)) && 
                    (!(o instanceof edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse))) {
                    return true;
                  }
                  

                  return false;
                }
              }; } else { Criterion criterion;
              if (((property.getOwner() instanceof PropertyAnimation)) && (property.getName().equals("element"))) {
                PropertyAnimation propertyAnimation = (PropertyAnimation)property.getOwner();
                String propertyName = propertyName.getStringValue();
                Criterion hasProperty = new Criterion() {
                  public boolean accept(Object o) {
                    if (((o instanceof Element)) && 
                      (((Element)o).getPropertyNamed(PopupMenuUtilities.this) != null)) {
                      return true;
                    }
                    
                    return false;
                  }
                };
                criterion = new MatchesAllCriterion(new Criterion[] { hasProperty, isNamedElement });
              } else { Criterion criterion;
                if (((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.PointAtAnimation)) && (property.getName().equals("target"))) {
                  Object transformableValue = property.getOwner().getPropertyNamed("subject").get();
                  Criterion notSelf = new Criterion() {
                    public boolean accept(Object o) {
                      if (o == PopupMenuUtilities.this) {
                        return false;
                      }
                      return true;
                    }
                  };
                  InstanceOfCriterion instanceOf = new InstanceOfCriterion(targetValueClass);
                  InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
                  criterion = new MatchesAllCriterion(new Criterion[] { instanceOf, notSelf, isNamedElement, inAppropriateOAPCriterion }); } else { Criterion criterion;
                  if ((property instanceof edu.cmu.cs.stage3.alice.core.property.VehicleProperty)) {
                    Object transformableValue = property.getOwner();
                    Criterion notSelf = new Criterion() {
                      public boolean accept(Object o) {
                        if (o == PopupMenuUtilities.this) {
                          return false;
                        }
                        return true;
                      }
                    };
                    InstanceOfCriterion instanceOf = new InstanceOfCriterion(targetValueClass);
                    InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
                    criterion = new MatchesAllCriterion(new Criterion[] { instanceOf, notSelf, isNamedElement, inAppropriateOAPCriterion });
                  } else { Criterion criterion;
                    if (((property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.PointAtConstraint)) && (property.getName().equals("target"))) {
                      Object transformableValue = property.getOwner().getPropertyNamed("subject").get();
                      Criterion notSelf = new Criterion() {
                        public boolean accept(Object o) {
                          if (o == PopupMenuUtilities.this) {
                            return false;
                          }
                          return true;
                        }
                      };
                      InstanceOfCriterion instanceOf = new InstanceOfCriterion(targetValueClass);
                      InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
                      criterion = new MatchesAllCriterion(new Criterion[] { instanceOf, notSelf, isNamedElement, inAppropriateOAPCriterion });
                    } else {
                      InstanceOfCriterion instanceOf = new InstanceOfCriterion(targetValueClass);
                      Criterion criterion; if (Expression.class.isAssignableFrom(targetValueClass)) {
                        criterion = new MatchesAllCriterion(new Criterion[] { instanceOf, isNamedElement });
                      } else {
                        edu.cmu.cs.stage3.util.criterion.NotCriterion notExpression = new edu.cmu.cs.stage3.util.criterion.NotCriterion(new InstanceOfCriterion(Expression.class));
                        InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
                        criterion = new MatchesAllCriterion(new Criterion[] { instanceOf, notExpression, isNamedElement, inAppropriateOAPCriterion });
                      }
                    } } } } }
            Element[] elements = root.search(criterion);
            
            if ((structure.size() > 0) && (elements.length > 0)) {
              structure.add(new StringObjectPair("Separator", JSeparator.class));
            }
            if (elements.length < 10) {
              structure.addAll(makeFlatElementStructure(root, criterion, factory, root, property.get()));
            } else {
              structure.addAll(makeElementStructure(root, criterion, factory, root, property.get()));
            }
            

            if (targetValueClass.isAssignableFrom(edu.cmu.cs.stage3.alice.core.Sound.class)) {
              final edu.cmu.cs.stage3.alice.core.World world = root.getWorld();
              if (structure.size() > 0) {
                structure.add(new StringObjectPair("Separator", JSeparator.class));
              }
              Runnable runnable = new Runnable() {
                public void run() {
                  PropertyPopupPostImportRunnable propertyPopupPostImportRunnable = new PropertyPopupPostImportRunnable(PopupMenuUtilities.this);
                  AuthoringTool.getHack().setImportFileFilter("Sound Files");
                  AuthoringTool.getHack().importElement(null, world, propertyPopupPostImportRunnable);
                }
              };
              structure.add(new StringObjectPair(Messages.getString("import sound file..."), runnable));
            }
          }
        }
      }
      
      if (!AuthoringToolResources.shouldGUIOmitNone(property)) {
        if (structure.size() > 0) {
          structure.add(new StringObjectPair("Separator", JSeparator.class));
        }
        if (property.get() == null) {
          structure.add(new StringObjectPair(AuthoringToolResources.getReprForValue(null, property, "menuContext"), new PopupItemWithIcon(factory.createItem(null), currentValueIcon)));
        } else {
          structure.add(new StringObjectPair(AuthoringToolResources.getReprForValue(null, property, "menuContext"), factory.createItem(null)));
        }
      }
      
      if (includeExpressions)
      {
        Vector expressionStructure = makeFlatExpressionStructure(targetValueClass, factory, property.getOwner(), property.get());
        if ((expressionStructure != null) && (expressionStructure.size() > 0)) {
          if (structure.size() > 0) {
            structure.add(new StringObjectPair("Separator", JSeparator.class));
          }
          structure.add(new StringObjectPair("expressions", expressionStructure));
        } else if (structure.size() == 0) {
          JLabel label = new JLabel("no expressions for this type");
          label.setForeground(java.awt.Color.gray);
          label.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 8, 2, 2));
          structure.add(new StringObjectPair("", label));
        }
      }
      
      if ((Boolean.class.isAssignableFrom(targetValueClass)) && (includeExpressions)) {
        Vector booleanLogicStructure = makeBooleanLogicStructure(property.get(), factory, property.getOwner());
        if (booleanLogicStructure != null) {
          if (structure.size() > 0) {
            structure.add(new StringObjectPair("Separator", JSeparator.class));
          }
          structure.add(new StringObjectPair("logic", booleanLogicStructure));
        }
      }
      
      if ((Number.class.isAssignableFrom(targetValueClass)) && (includeExpressions)) {
        Vector mathStructure = makeCommonMathQuestionStructure(property.get(), factory, property.getOwner());
        if (mathStructure != null) {
          if (structure.size() > 0) {
            structure.add(new StringObjectPair("Separator", JSeparator.class));
          }
          structure.add(new StringObjectPair("math", mathStructure));
        }
      }
      
      if (!AuthoringToolResources.shouldGUIOmitScriptDefined(property)) {
        if (structure.size() > 0) {
          structure.add(new StringObjectPair("Separator", JSeparator.class));
        }
        Runnable scriptDefinedRunnable = new Runnable() {
          public void run() {
            GUIFactory.showScriptDefinedPropertyDialog(PopupMenuUtilities.this, factory);
          }
        };
        structure.add(new StringObjectPair("script-defined...", scriptDefinedRunnable));
      }
      


      if ((includeOther) && (GUIFactory.isOtherDialogSupportedForClass(targetValueClass))) {
        if (structure.size() > 0) {
          structure.add(new StringObjectPair("Separator", JSeparator.class));
        }
        final Class finalOtherValueClass = targetValueClass;
        Runnable runnable = new Runnable() {
          public void run() {
            GUIFactory.showOtherPropertyDialog(PopupMenuUtilities.this, factory, null, finalOtherValueClass);
          }
        };
        structure.add(new StringObjectPair(Messages.getString("other..."), runnable));
      }
    }
    
    if (structure.size() == 0) {
      JLabel label = new JLabel("nothing to choose");
      label.setForeground(java.awt.Color.gray);
      label.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
      structure.add(new StringObjectPair("", label));
    }
    
    return structure;
  }
  
  public static Vector makeListQuestionStructure(Variable listVariable, final PopupItemFactory factory, Class returnValueClass, Element context) {
    Vector structure = new Vector();
    
    edu.cmu.cs.stage3.alice.core.List list = (edu.cmu.cs.stage3.alice.core.List)listVariable.getValue();
    PopupItemFactory prototypeToItemFactory = new PopupItemFactory() {
      public Object createItem(Object object) {
        ElementPrototype ep = (ElementPrototype)object;
        return PopupMenuUtilities.this.createItem(ep.createNewElement());
      }
    };
    
    if (returnValueClass.isAssignableFrom(valueClass.getClassValue())) {
      Runnable firstItemRunnable = new Runnable() {
        public void run() { edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning itemAtBeginning = new edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning();
          list.set(PopupMenuUtilities.this);
          ((Runnable)factory.createItem(itemAtBeginning)).run();
        } };
      Runnable lastItemRunnable = new Runnable() {
        public void run() { edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd itemAtEnd = new edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd();
          list.set(PopupMenuUtilities.this);
          ((Runnable)factory.createItem(itemAtEnd)).run();
        } };
      Runnable randomItemRunnable = new Runnable() {
        public void run() { edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex itemAtRandomIndex = new edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex();
          list.set(PopupMenuUtilities.this);
          ((Runnable)factory.createItem(itemAtRandomIndex)).run();
        }
      };
      StringObjectPair[] known = { new StringObjectPair("list", listVariable) };
      String[] desired = { "index" };
      ElementPrototype ithPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex.class, known, desired);
      Vector ithStructure = makePrototypeStructure(ithPrototype, prototypeToItemFactory, context);
      
      structure.add(new StringObjectPair("first item from list", firstItemRunnable));
      structure.add(new StringObjectPair("last item from list", lastItemRunnable));
      structure.add(new StringObjectPair("random item from list", randomItemRunnable));
      structure.add(new StringObjectPair("ith item from list", ithStructure));
    }
    Element.class.isAssignableFrom(valueClass.getClassValue());
    

























    if (returnValueClass.isAssignableFrom(Boolean.class)) {
      Runnable isEmptyRunnable = new Runnable() {
        public void run() { edu.cmu.cs.stage3.alice.core.question.list.IsEmpty isEmpty = new edu.cmu.cs.stage3.alice.core.question.list.IsEmpty();
          list.set(PopupMenuUtilities.this);
          ((Runnable)factory.createItem(isEmpty)).run();
        }
      };
      StringObjectPair[] known = { new StringObjectPair("list", listVariable) };
      String[] desired = { "item" };
      ElementPrototype containsPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.list.Contains.class, known, desired);
      Vector containsStructure = makePrototypeStructure(containsPrototype, prototypeToItemFactory, context);
      
      if (structure.size() > 0) {
        structure.add(new StringObjectPair("Separator", JSeparator.class));
      }
      structure.add(new StringObjectPair("is list empty", isEmptyRunnable));
      structure.add(new StringObjectPair("list contains", containsStructure));
    }
    
    if (returnValueClass.isAssignableFrom(Number.class)) {
      Runnable sizeRunnable = new Runnable() {
        public void run() { edu.cmu.cs.stage3.alice.core.question.list.Size size = new edu.cmu.cs.stage3.alice.core.question.list.Size();
          list.set(PopupMenuUtilities.this);
          ((Runnable)factory.createItem(size)).run();
        }
      };
      StringObjectPair[] known = { new StringObjectPair("list", listVariable) };
      String[] desired = { "item" };
      
      ElementPrototype firstIndexOfItemPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.list.FirstIndexOfItem.class, known, desired);
      Vector firstIndexOfItemStructure = makePrototypeStructure(firstIndexOfItemPrototype, prototypeToItemFactory, context);
      
      ElementPrototype lastIndexOfItemPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.list.LastIndexOfItem.class, known, desired);
      Vector lastIndexOfItemStructure = makePrototypeStructure(lastIndexOfItemPrototype, prototypeToItemFactory, context);
      
      if (structure.size() > 0) {
        structure.add(new StringObjectPair("Separator", JSeparator.class));
      }
      structure.add(new StringObjectPair("size of list", sizeRunnable));
      structure.add(new StringObjectPair("first index of", firstIndexOfItemStructure));
      structure.add(new StringObjectPair("last index of", lastIndexOfItemStructure));
    }
    
    return structure;
  }
  
  public static Vector makeArrayQuestionStructure(Variable arrayVariable, final PopupItemFactory factory, Class returnValueClass, Element context) {
    Vector structure = new Vector();
    
    Array array = (Array)arrayVariable.getValue();
    
    PopupItemFactory prototypeToItemFactory = new PopupItemFactory() {
      public Object createItem(Object object) {
        ElementPrototype ep = (ElementPrototype)object;
        return PopupMenuUtilities.this.createItem(ep.createNewElement());
      }
    };
    
    if (returnValueClass.isAssignableFrom(valueClass.getClassValue())) {
      StringObjectPair[] known = { new StringObjectPair("array", arrayVariable) };
      String[] desired = { "index" };
      ElementPrototype ithPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex.class, known, desired);
      Vector ithStructure = makePrototypeStructure(ithPrototype, prototypeToItemFactory, context);
      
      structure.add(new StringObjectPair("ith item from array", ithStructure));
    }
    
    if (returnValueClass.isAssignableFrom(Number.class)) {
      Runnable sizeRunnable = new Runnable() {
        public void run() { edu.cmu.cs.stage3.alice.core.question.array.Size size = new edu.cmu.cs.stage3.alice.core.question.array.Size();
          array.set(PopupMenuUtilities.this);
          ((Runnable)factory.createItem(size)).run();
        }
      };
      


      if (structure.size() > 0) {
        structure.add(new StringObjectPair("Separator", JSeparator.class));
      }
      structure.add(new StringObjectPair("size of array", sizeRunnable));
    }
    
    return structure;
  }
  


  public static Vector makeCommonMathQuestionStructure(Object firstOperand, PopupItemFactory factory, Element context)
  {
    Vector structure = new Vector();
    
    if (!(firstOperand instanceof Number))
    {
      if ((!(firstOperand instanceof Expression)) || (!Number.class.isAssignableFrom(((Expression)firstOperand).getValueClass())))
      {
        if (firstOperand != null)
        {

          throw new IllegalArgumentException("firstOperand must represent a Number"); }
      }
    }
    String firstOperandRepr = AuthoringToolResources.getReprForValue(firstOperand, false);
    







    StringObjectPair[] known = { new StringObjectPair("a", firstOperand) };
    String[] desired = { "b" };
    
    ElementPrototype addPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.NumberAddition.class, known, desired);
    Vector addStructure = makePrototypeStructure(addPrototype, factory, context);
    ElementPrototype subtractPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.NumberSubtraction.class, known, desired);
    Vector subtractStructure = makePrototypeStructure(subtractPrototype, factory, context);
    ElementPrototype multiplyPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.NumberMultiplication.class, known, desired);
    Vector multiplyStructure = makePrototypeStructure(multiplyPrototype, factory, context);
    ElementPrototype dividePrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.NumberDivision.class, known, desired);
    Vector divideStructure = makePrototypeStructure(dividePrototype, factory, context);
    
    structure.add(new StringObjectPair(firstOperandRepr + " +", addStructure));
    structure.add(new StringObjectPair(firstOperandRepr + " -", subtractStructure));
    structure.add(new StringObjectPair(firstOperandRepr + " *", multiplyStructure));
    structure.add(new StringObjectPair(firstOperandRepr + " /", divideStructure));
    
    return structure;
  }
  


  public static Vector makeBooleanLogicStructure(Object firstOperand, PopupItemFactory factory, Element context)
  {
    Vector structure = new Vector();
    
    if (!(firstOperand instanceof Boolean))
    {
      if ((!(firstOperand instanceof Expression)) || (!Boolean.class.isAssignableFrom(((Expression)firstOperand).getValueClass())))
      {
        if (firstOperand == null) {
          return null;
        }
        throw new IllegalArgumentException("firstOperand must represent a Boolean");
      }
    }
    String firstOperandRepr = AuthoringToolResources.getReprForValue(firstOperand, false);
    







    StringObjectPair[] known = { new StringObjectPair("a", firstOperand) };
    String[] desired = { "b" };
    
    ElementPrototype andPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.And.class, known, desired);
    Vector andStructure = makePrototypeStructure(andPrototype, factory, context);
    ElementPrototype orPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.Or.class, known, desired);
    Vector orStructure = makePrototypeStructure(orPrototype, factory, context);
    ElementPrototype notPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.Not.class, known, new String[0]);
    Object notItem = factory.createItem(notPrototype);
    ElementPrototype equalPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.IsEqualTo.class, known, desired);
    Vector equalStructure = makePrototypeStructure(equalPrototype, factory, context);
    ElementPrototype notEqualPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.IsNotEqualTo.class, known, desired);
    Vector notEqualStructure = makePrototypeStructure(notEqualPrototype, factory, context);
    
    structure.add(new StringObjectPair(firstOperandRepr + " and", andStructure));
    structure.add(new StringObjectPair(firstOperandRepr + " or", orStructure));
    structure.add(new StringObjectPair("not " + firstOperandRepr, notItem));
    structure.add(new StringObjectPair("Separator", JSeparator.class));
    structure.add(new StringObjectPair(firstOperandRepr + " ==", equalStructure));
    structure.add(new StringObjectPair(firstOperandRepr + " !=", notEqualStructure));
    
    return structure;
  }
  


  public static Vector makeComparatorStructure(Object firstOperand, PopupItemFactory factory, Element context)
  {
    Vector structure = new Vector();
    
    String firstOperandRepr = AuthoringToolResources.getReprForValue(firstOperand, false);
    







    StringObjectPair[] known = { new StringObjectPair("a", firstOperand) };
    String[] desired = { "b" };
    ElementPrototype equalPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.IsEqualTo.class, known, desired);
    Vector equalStructure = makePrototypeStructure(equalPrototype, factory, context);
    ElementPrototype notEqualPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.IsNotEqualTo.class, known, desired);
    Vector notEqualStructure = makePrototypeStructure(notEqualPrototype, factory, context);
    
    structure.add(new StringObjectPair(firstOperandRepr + " ==", equalStructure));
    structure.add(new StringObjectPair(firstOperandRepr + " !=", notEqualStructure));
    
    if (((firstOperand instanceof Number)) || (((firstOperand instanceof Expression)) && (Number.class.isAssignableFrom(((Expression)firstOperand).getValueClass()))))
    {




      ElementPrototype lessThanPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.NumberIsLessThan.class, known, desired);
      Vector lessThanStructure = makePrototypeStructure(lessThanPrototype, factory, context);
      ElementPrototype greaterThanPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.NumberIsGreaterThan.class, known, desired);
      Vector greaterThanStructure = makePrototypeStructure(greaterThanPrototype, factory, context);
      ElementPrototype lessThanOrEqualPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.NumberIsLessThanOrEqualTo.class, known, desired);
      Vector lessThanOrEqualStructure = makePrototypeStructure(lessThanOrEqualPrototype, factory, context);
      ElementPrototype greaterThanOrEqualPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.NumberIsGreaterThanOrEqualTo.class, known, desired);
      Vector greaterThanOrEqualStructure = makePrototypeStructure(greaterThanOrEqualPrototype, factory, context);
      


      structure.add(new StringObjectPair(firstOperandRepr + " <", lessThanStructure));
      structure.add(new StringObjectPair(firstOperandRepr + " >", greaterThanStructure));
      structure.add(new StringObjectPair(firstOperandRepr + " <=", lessThanOrEqualStructure));
      structure.add(new StringObjectPair(firstOperandRepr + " >=", greaterThanOrEqualStructure));
    }
    
    return structure;
  }
  


  public static Vector makePartsOfPositionStructure(Object position, PopupItemFactory factory, Element context)
  {
    Vector structure = new Vector();
    
    if (!(position instanceof javax.vecmath.Vector3d))
    {
      if ((!(position instanceof Expression)) || (!javax.vecmath.Vector3d.class.isAssignableFrom(((Expression)position).getValueClass())))
      {
        if (position != null)
        {

          throw new IllegalArgumentException("position must represent a javax.vecmath.Vector3d"); }
      }
    }
    String positionRepr = AuthoringToolResources.getReprForValue(position, false);
    
    StringObjectPair[] known = { new StringObjectPair("vector3", position) };
    
    ElementPrototype xPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.vector3.X.class, known, new String[0]);
    Object xItem = factory.createItem(xPrototype);
    ElementPrototype yPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.vector3.Y.class, known, new String[0]);
    Object yItem = factory.createItem(yPrototype);
    ElementPrototype zPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.vector3.Z.class, known, new String[0]);
    Object zItem = factory.createItem(zPrototype);
    
    structure.add(new StringObjectPair(positionRepr + "'s distance right", xItem));
    structure.add(new StringObjectPair(positionRepr + "'s distance up", yItem));
    structure.add(new StringObjectPair(positionRepr + "'s distance forward", zItem));
    
    return structure;
  }
  


  public static Vector makeResponsePrintStructure(final PopupItemFactory factory, Element context)
  {
    Vector structure = new Vector();
    
    StringObjectPair[] known = new StringObjectPair[0];
    
    Runnable textStringRunnable = new Runnable() {
      public void run() {
        ElementPrototype elementPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.response.Print.class, PopupMenuUtilities.this, new String[] { "text" });
        
        String text = edu.cmu.cs.stage3.swing.DialogManager.showInputDialog(Messages.getString("Enter_text_to_print"), Messages.getString("Enter_Text_String"), -1);
        if (text != null) {
          ((Runnable)factory.createItem(elementPrototype.createCopy(new StringObjectPair("text", text)))).run();
        }
      }
    };
    structure.add(new StringObjectPair(Messages.getString("text_string___"), textStringRunnable));
    
    ElementPrototype elementPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.response.Print.class, known, new String[] { "object" });
    structure.add(new StringObjectPair(Messages.getString("object"), makePrototypeStructure(elementPrototype, factory, context)));
    
    return structure;
  }
  


  public static Vector makeQuestionPrintStructure(final PopupItemFactory factory, Element context)
  {
    Vector structure = new Vector();
    
    StringObjectPair[] known = new StringObjectPair[0];
    
    Runnable textStringRunnable = new Runnable() {
      public void run() {
        ElementPrototype elementPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.userdefined.Print.class, PopupMenuUtilities.this, new String[] { "text" });
        
        String text = edu.cmu.cs.stage3.swing.DialogManager.showInputDialog(Messages.getString("Enter text to print"), Messages.getString("Enter Text String"), -1);
        if (text != null) {
          ((Runnable)factory.createItem(elementPrototype.createCopy(new StringObjectPair("text", text)))).run();
        }
      }
    };
    structure.add(new StringObjectPair(Messages.getString("text string..."), textStringRunnable));
    
    ElementPrototype elementPrototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.userdefined.Print.class, known, new String[] { "object" });
    structure.add(new StringObjectPair(Messages.getString("object"), makePrototypeStructure(elementPrototype, factory, context)));
    
    return structure;
  }
  


  public static Vector makePropertyValueStructure(Element element, Class valueClass, PopupItemFactory factory, Element context)
  {
    Vector structure = new Vector();
    
    Class elementClass = null;
    if ((element instanceof Expression)) {
      elementClass = ((Expression)element).getValueClass();
    } else {
      elementClass = element.getClass();
    }
    
    StringObjectPair[] known = { new StringObjectPair("element", element) };
    String[] desired = { "propertyName" };
    ElementPrototype prototype = new ElementPrototype(edu.cmu.cs.stage3.alice.core.question.PropertyValue.class, known, desired);
    
    String[] propertyNames = getPropertyNames(elementClass, valueClass);
    String prefix = AuthoringToolResources.getReprForValue(element, false) + ".";
    for (int i = 0; i < propertyNames.length; i++)
    {
      if ((!propertyNames[i].equals("visualization")) && (!propertyNames[i].equals("isFirstClass"))) {
        String propertyName = AuthoringToolResources.getReprForValue(propertyNames[i], false);
        structure.add(new StringObjectPair(prefix + propertyName, factory.createItem(prototype.createCopy(new StringObjectPair("propertyName", propertyName)))));
      }
    }
    
    return structure;
  }
  
  private static String[] getPropertyNames(Class elementClass, Class valueClass)
  {
    try {
      Element element = (Element)elementClass.newInstance();
      Property[] properties = element.getProperties();
      Vector propertyNames = new Vector();
      for (int i = 0; i < properties.length; i++) {
        if (valueClass.isAssignableFrom(properties[i].getValueClass())) {
          propertyNames.add(properties[i].getName());
        }
      }
      return (String[])propertyNames.toArray(new String[0]);
    } catch (InstantiationException ie) {
      return null;
    } catch (IllegalAccessException iae) {}
    return null;
  }
  
  public static Vector getDefaultValueStructureForProperty(Class elementClass, String propertyName)
  {
    return getDefaultValueStructureForProperty(elementClass, propertyName, null);
  }
  
  public static Vector getDefaultValueStructureForProperty(Property property) {
    return getDefaultValueStructureForProperty(property.getOwner().getClass(), property.getName(), property);
  }
  
  public static Vector getDefaultValueStructureForProperty(Class elementClass, String propertyName, Property property)
  {
    Vector structure = new Vector(getUnlabeledDefaultValueStructureForProperty(elementClass, propertyName, property));
    addLabelsToValueStructure(structure, elementClass, propertyName);
    return structure;
  }
  
  public static Vector getUnlabeledDefaultValueStructureForProperty(Class elementClass, String propertyName, Property property)
  {
    if (property != null)
    {
      if (((property.getOwner() instanceof PropertyAnimation)) && (property.getName().equals("value"))) {
        PropertyAnimation propertyAnimation = (PropertyAnimation)property.getOwner();
        if (element.getElementValue() != null)
          elementClass = element.getElementValue().getClass();
        if ((element.getElementValue() instanceof Variable)) {
          Variable var = (Variable)element.getElementValue();
          elementClass = var.getValueClass();
        }
        propertyName = propertyName.getStringValue();
      }
      else if (((property.getOwner() instanceof PropertyAssignment)) && (property.getName().equals("value"))) {
        PropertyAssignment propertyAssignment = (PropertyAssignment)property.getOwner();
        if (element.getElementValue() != null)
          elementClass = element.getElementValue().getClass();
        if ((element.getElementValue() instanceof Variable)) {
          Variable var = (Variable)element.getElementValue();
          elementClass = var.getValueClass();
        }
        propertyName = propertyName.getStringValue();
      }
    }
    Vector structure = AuthoringToolResources.getDefaultPropertyValues(elementClass, propertyName);
    if (structure == null) {
      structure = new Vector();
    }
    if (structure.size() < 1) { Class valueClass;
      Class valueClass;
      if (property != null) { Class valueClass;
        if (((property.getOwner() instanceof BinaryObjectResultingInBooleanQuestion)) && ((property.getName().equals("a")) || (property.getName().equals("b")))) { Object otherValue;
          Object otherValue;
          if (property.getName().equals("a")) {
            otherValue = getOwnerb.get();
          } else {
            otherValue = getOwnera.get();
          }
          Class valueClass;
          if ((otherValue instanceof Expression)) {
            valueClass = ((Expression)otherValue).getValueClass(); } else { Class valueClass;
            if (otherValue != null) {
              valueClass = otherValue.getClass();
            } else
              valueClass = property.getValueClass();
          }
        } else {
          valueClass = property.getValueClass();
        }
      } else {
        valueClass = Element.getValueClassForPropertyNamed(elementClass, propertyName);
      }
      structure.addAll(getUnlabeledDefaultValueStructureForClass(valueClass));
    }
    
    return structure;
  }
  
  public static Vector getDefaultValueStructureForCollectionIndexProperty(StringObjectPair[] knownPropertyValues) {
    Object collection = null;
    for (int i = 0; i < knownPropertyValues.length; i++) {
      if (knownPropertyValues[i].getString().equals("list")) {
        collection = knownPropertyValues[i].getObject();
        break; }
      if (knownPropertyValues[i].getString().equals("array")) {
        collection = knownPropertyValues[i].getObject();
        break;
      }
    }
    
    Collection realCollection = null;
    if ((collection instanceof Variable)) {
      realCollection = (Collection)((Variable)collection).getValue();
    } else if ((collection instanceof Collection)) {
      realCollection = (Collection)collection;
    }
    
    Vector structure = new Vector();
    if (realCollection != null) {
      int size = values.size();
      for (int i = 0; (i < size) && (i < 10); i++) {
        structure.add(new StringObjectPair(Integer.toString(i), new Double(i)));
      }
    }
    
    if (structure.size() < 1) {
      structure.add(new StringObjectPair("0", new Double(0.0D)));
    }
    
    return structure;
  }
  





























































  public static Vector getDefaultValueStructureForClass(Class valueClass)
  {
    Vector structure = getUnlabeledDefaultValueStructureForClass(valueClass);
    addLabelsToValueStructure(structure);
    return structure;
  }
  
  public static Vector getUnlabeledDefaultValueStructureForClass(Class valueClass) {
    Vector structure = new Vector();
    
    if (Boolean.class.isAssignableFrom(valueClass)) {
      structure.add(Boolean.TRUE);
      structure.add(Boolean.FALSE);
    } else if (edu.cmu.cs.stage3.alice.scenegraph.Color.class.isAssignableFrom(valueClass)) {
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.RED);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.PINK);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY);
      structure.add(edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY);
    } else if (Number.class.isAssignableFrom(valueClass)) {
      structure.add(new Double(0.25D));
      structure.add(new Double(0.5D));
      structure.add(new Double(1.0D));
      structure.add(new Double(2.0D));
    } else if (edu.cmu.cs.stage3.util.Enumerable.class.isAssignableFrom(valueClass)) {
      edu.cmu.cs.stage3.util.Enumerable[] enumItems = edu.cmu.cs.stage3.util.Enumerable.getItems(valueClass);
      for (int i = 0; i < enumItems.length; i++) {
        structure.add(enumItems[i]);
      }
    } else if (edu.cmu.cs.stage3.alice.core.Style.class.isAssignableFrom(valueClass)) {
      structure.add(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_GENTLY);
      structure.add(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY);
      structure.add(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY);
      structure.add(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY);
    } else if (edu.cmu.cs.stage3.math.Vector3.class.isAssignableFrom(valueClass)) {
      structure.add(new edu.cmu.cs.stage3.math.Vector3(0.0D, 0.0D, 0.0D));
    } else if (edu.cmu.cs.stage3.math.Vector4.class.isAssignableFrom(valueClass)) {
      structure.add(new edu.cmu.cs.stage3.math.Vector4(0.0D, 0.0D, 0.0D, 0.0D));
    } else if (edu.cmu.cs.stage3.math.Matrix33.class.isAssignableFrom(valueClass)) {
      structure.add(edu.cmu.cs.stage3.math.Matrix33.IDENTITY);
    } else if (edu.cmu.cs.stage3.math.Matrix44.class.isAssignableFrom(valueClass)) {
      structure.add(edu.cmu.cs.stage3.math.Matrix44.IDENTITY);
    } else if (edu.cmu.cs.stage3.math.Quaternion.class.isAssignableFrom(valueClass)) {
      structure.add(new edu.cmu.cs.stage3.math.Quaternion());
    } else if (String.class.isAssignableFrom(valueClass)) {
      structure.add(Messages.getString("default_string"));
    }
    
    return structure;
  }
  
  private static Vector getDefaultValueStructureForPropertyAnimation(StringObjectPair[] knownPropertyValues)
  {
    Vector structure = new Vector();
    Element element = null;
    String propertyName = null;
    for (int i = 0; i < knownPropertyValues.length; i++) {
      if (knownPropertyValues[i].getString().equals("element")) {
        element = (Element)knownPropertyValues[i].getObject();
        break;
      }
    }
    for (int i = 0; i < knownPropertyValues.length; i++) {
      if (knownPropertyValues[i].getString().equals("propertyName")) {
        propertyName = (String)knownPropertyValues[i].getObject();
        break;
      }
    }
    if ((element != null) && (propertyName != null)) {
      Class elementClass = element.getClass();
      if ((element instanceof Expression)) {
        elementClass = ((Expression)element).getValueClass();
      }
      structure = getDefaultValueStructureForProperty(elementClass, propertyName, element.getPropertyNamed(propertyName));
    }
    
    return structure;
  }
  
  private static void addLabelsToValueStructure(Vector structure) {
    for (ListIterator iter = structure.listIterator(); iter.hasNext();) {
      Object item = iter.next();
      if ((item instanceof StringObjectPair)) {
        StringObjectPair sop = (StringObjectPair)item;
        if ((sop.getObject() instanceof Vector)) {
          addLabelsToValueStructure((Vector)sop.getObject());
        }
      } else if ((item instanceof Vector)) {
        AuthoringTool.showErrorDialog("Unexpected Vector found while processing value structure", null);
      } else {
        String text = AuthoringToolResources.getReprForValue(item);
        iter.set(new StringObjectPair(text, item));
      }
    }
  }
  
  private static void addLabelsToValueStructure(Vector structure, Class elementClass, String propertyName) {
    for (ListIterator iter = structure.listIterator(); iter.hasNext();) {
      Object item = iter.next();
      if ((item instanceof StringObjectPair)) {
        StringObjectPair sop = (StringObjectPair)item;
        if ((sop.getObject() instanceof Vector)) {
          addLabelsToValueStructure((Vector)sop.getObject(), elementClass, propertyName);
        }
      } else if ((item instanceof Vector)) {
        AuthoringTool.showErrorDialog("Unexpected Vector found while processing value structure", null);
      } else {
        String text = AuthoringToolResources.getReprForValue(item, elementClass, propertyName, "menuContext");
        iter.set(new StringObjectPair(text, item));
      }
    }
  }
  
  private static Vector processStructure(Vector structure, PopupItemFactory factory, Object currentValue) {
    Vector processed = new Vector();
    for (Iterator iter = structure.iterator(); iter.hasNext();) {
      Object item = iter.next();
      if ((item instanceof StringObjectPair)) {
        StringObjectPair sop = (StringObjectPair)item;
        if ((sop.getObject() instanceof Vector)) {
          processed.add(new StringObjectPair(sop.getString(), processStructure((Vector)sop.getObject(), factory, currentValue)));
        }
        else if (((currentValue == null) && (sop.getObject() == null)) || ((currentValue != null) && (currentValue.equals(sop.getObject())))) {
          processed.add(new StringObjectPair(sop.getString(), new PopupItemWithIcon(factory.createItem(sop.getObject()), currentValueIcon)));
        } else {
          processed.add(new StringObjectPair(sop.getString(), factory.createItem(sop.getObject())));
        }
      }
      else {
        AuthoringTool.showErrorDialog("Unexpected Vector found while processing value structure", null);
      }
    }
    return processed;
  }
  
  public static boolean structureContains(Vector structure, Object value) {
    for (Iterator iter = structure.iterator(); iter.hasNext();) {
      Object item = iter.next();
      if ((item instanceof StringObjectPair)) {
        StringObjectPair sop = (StringObjectPair)item;
        if ((sop.getObject() instanceof Vector)) {
          if (structureContains((Vector)sop.getObject(), value)) {
            return true;
          }
        } else if (sop.getObject() == null) {
          if (value == null) {
            return true;
          }
        }
        else if (sop.getObject().equals(value)) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  private static Class getValueClassForPropertyAnimation(StringObjectPair[] knownPropertyValues)
  {
    Class valueClass = null;
    Element element = null;
    String propertyName = null;
    for (int i = 0; i < knownPropertyValues.length; i++) {
      if (knownPropertyValues[i].getString().equals("element")) {
        element = (Element)knownPropertyValues[i].getObject();
        break;
      }
    }
    for (int i = 0; i < knownPropertyValues.length; i++) {
      if (knownPropertyValues[i].getString().equals("propertyName")) {
        propertyName = (String)knownPropertyValues[i].getObject();
        break;
      }
    }
    if (((element instanceof Variable)) && ("value".equals(propertyName))) {
      valueClass = ((Variable)element).getValueClass();
    } else if ((element != null) && (propertyName != null)) {
      Class elementClass = element.getClass();
      if ((element instanceof Expression)) {
        elementClass = ((Expression)element).getValueClass();
      }
      valueClass = Element.getValueClassForPropertyNamed(elementClass, propertyName);
    }
    
    return valueClass;
  }
  
  private static Class getValueClassForList(StringObjectPair[] knownPropertyValues) {
    Class valueClass = null;
    Object list = null;
    for (int i = 0; i < knownPropertyValues.length; i++) {
      if (knownPropertyValues[i].getString().equals("list")) {
        list = knownPropertyValues[i].getObject();
        break;
      }
    }
    if ((list instanceof Variable)) {
      edu.cmu.cs.stage3.alice.core.List realList = (edu.cmu.cs.stage3.alice.core.List)((Variable)list).getValue();
      if (realList != null) {
        valueClass = valueClass.getClassValue();
      }
    } else if ((list instanceof edu.cmu.cs.stage3.alice.core.List)) {
      valueClass = valueClass.getClassValue();
    } else {
      valueClass = Object.class;
    }
    
    return valueClass;
  }
  
  private static Class getValueClassForArray(StringObjectPair[] knownPropertyValues) {
    Class valueClass = null;
    Object array = null;
    for (int i = 0; i < knownPropertyValues.length; i++) {
      if (knownPropertyValues[i].getString().equals("array")) {
        array = knownPropertyValues[i].getObject();
        break;
      }
    }
    
    if ((array instanceof Variable)) {
      Array realArray = (Array)((Variable)array).getValue();
      valueClass = valueClass.getClassValue();
    } else if ((array instanceof Array)) {
      valueClass = valueClass.getClassValue();
    } else {
      valueClass = Object.class;
    }
    
    return valueClass;
  }
  
  private static Class generalizeValueClass(Class valueClass) {
    Class newValueClass = valueClass;
    if (Number.class.isAssignableFrom(valueClass)) {
      newValueClass = Number.class;
    } else if (Model.class.isAssignableFrom(valueClass)) {
      newValueClass = Model.class;
    }
    return newValueClass;
  }
  
  private static Class getValueClassForComparator(StringObjectPair[] knownPropertyValues) {
    Class valueClass = null;
    Object operand = null;
    for (int i = 0; i < knownPropertyValues.length; i++) {
      if (knownPropertyValues[i].getString().equals("a")) {
        operand = knownPropertyValues[i].getObject();
        break;
      }
    }
    if ((operand instanceof Expression)) {
      valueClass = ((Expression)operand).getValueClass();
    } else if (operand != null) {
      valueClass = operand.getClass();
    }
    return generalizeValueClass(valueClass);
  }
  
  public static Class getDesiredValueClass(Property property) {
    Class targetValueClass = property.getValueClass();
    if (((property.getOwner() instanceof BinaryObjectResultingInBooleanQuestion)) && ((property.getName().equals("a")) || (property.getName().equals("b")))) { Object ourValue;
      Object otherValue;
      Object ourValue;
      if (property.getName().equals("a")) {
        Object otherValue = getOwnerb.get();
        ourValue = getOwnera.get();
      } else {
        otherValue = getOwnera.get();
        ourValue = getOwnerb.get(); }
      Class otherValueClass;
      Class otherValueClass;
      if ((otherValue instanceof Expression)) {
        otherValueClass = ((Expression)otherValue).getValueClass(); } else { Class otherValueClass;
        if (otherValue != null) {
          otherValueClass = otherValue.getClass();
        } else
          otherValueClass = property.getValueClass();
      }
      if ((ourValue instanceof Expression)) {
        targetValueClass = ((Expression)ourValue).getValueClass();
      } else if (ourValue != null) {
        targetValueClass = ourValue.getClass();
      } else {
        targetValueClass = property.getValueClass();
      }
      if (targetValueClass != otherValueClass) {
        targetValueClass = otherValueClass;
      }
      targetValueClass = generalizeValueClass(targetValueClass);
    }
    return targetValueClass;
  }
  
  public static Vector makeDefaultOneShotStructure(Element element)
  {
    return makeResponseStructure(element, oneShotFactory, element.getRoot());
  }
  

  public static void hidePopup()
  {
    item.firePropertyChange("focusable", false, true);
  }
  
  public static void ensurePopupIsOnScreen(JPopupMenu popup) {
    Point location = popup.getLocation(null);
    Dimension size = popup.getSize(null);
    Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    height -= 28;
    
    javax.swing.SwingUtilities.convertPointToScreen(location, popup);
    
    if (x < 0) {
      x = 0;
    } else if (x + width > width) {
      x -= x + width - width;
    }
    if (y < 0) {
      y = 0;
    } else if (y + height > height) {
      y -= y + height - height;
    }
    
    popup.setLocation(location);
    
    item = popup;
    item.addPropertyChangeListener("focusable", new java.beans.PropertyChangeListener()
    {




      public void propertyChange(java.beans.PropertyChangeEvent arg0)
      {




        int i = 0;
      }
    });
  }
  

















  public static java.awt.event.ActionListener getPopupMenuItemActionListener(Runnable runnable)
  {
    return new PopupMenuItemActionListener(runnable);
  }
  
  public static JPopupMenu makeDisabledPopup(String s) {
    JPopupMenu popup = new JPopupMenu();
    JMenuItem item = makeMenuItem(s, null);
    item.setEnabled(false);
    popup.add(item);
    return popup;
  }
  
  public static Criterion getAvailableExpressionCriterion(Class valueClass, Element context)
  {
    if (context == null) {
      AuthoringTool.showErrorDialog("Error: null context while looking for expressions; using World", null);
      context = AuthoringTool.getHack().getWorld();
    }
    Criterion isAccessible = new edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAccessibleFromCriterion(context);
    edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAssignableToCriterion isAssignable = new edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAssignableToCriterion(valueClass);
    return new MatchesAllCriterion(new Criterion[] { isNotActualParameter, isAccessible, isNamedElement, isAssignable });
  }
  
  public static void printStructure(Vector structure) {
    printStructure(structure, 0);
  }
  
  private static void printStructure(Vector structure, int indent) {
    String tabs = "";
    for (int i = 0; i < indent; i++) {
      tabs = tabs + "\t";
    }
    for (Iterator iter = structure.iterator(); iter.hasNext();) {
      Object item = iter.next();
      if ((item instanceof StringObjectPair)) {
        StringObjectPair sop = (StringObjectPair)item;
        if ((sop.getObject() instanceof Vector)) {
          printStructure((Vector)sop.getObject(), indent + 1);
        } else {
          System.out.println(tabs + sop.getString() + " : " + sop.getObject());
        }
      } else {
        AuthoringTool.showErrorDialog("unexpected object found while printing structure: " + item, null);
      }
    }
  }
}
