package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer.ObjectXmlData;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CollectionPropertyViewController;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementNamePropertyViewController;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPropertyViewController;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyDnDPanel;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyGUI;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.SoundViewController;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariableDnDPanel;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariableGUI;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Sound;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import edu.cmu.cs.stage3.swing.numpad.NumPad;
import java.awt.Container;
import java.util.HashMap;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JLabel;

public class GUIFactory
{
  public static HashMap guiCache = new HashMap();
  protected static CollectionEditorPanel collectionEditorPanel;
  
  public GUIFactory() {}
  
  public static CollectionEditorPanel getCollectionEditorPanel() {
    if (collectionEditorPanel == null) {
      collectionEditorPanel = new CollectionEditorPanel();
    }
    return collectionEditorPanel;
  }
  
  public static JComponent getGUI(Object o) {
    AuthoringTool authoringTool = AuthoringTool.getHack();
    JComponent viewController = null;
    
    if ((o instanceof edu.cmu.cs.stage3.alice.core.response.IfElseInOrder)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ConditionalResponsePanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ConditionalResponsePanel)viewController).set((edu.cmu.cs.stage3.alice.core.response.IfElseInOrder)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.response.LoopNInOrder)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.CountLoopPanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.CountLoopPanel)viewController).set((edu.cmu.cs.stage3.alice.core.response.LoopNInOrder)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.LoopIfTrueResponsePanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.LoopIfTrueResponsePanel)viewController).set((edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.response.ForEachInOrder)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ForEachInListSequentialLoopPanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ForEachInListSequentialLoopPanel)viewController).set((edu.cmu.cs.stage3.alice.core.response.ForEachInOrder)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.response.ForEachTogether)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ForAllTogetherResponsePanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ForAllTogetherResponsePanel)viewController).set((edu.cmu.cs.stage3.alice.core.response.ForEachTogether)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.response.DoTogether)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ParallelResponsePanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ParallelResponsePanel)viewController).set((edu.cmu.cs.stage3.alice.core.response.DoTogether)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.response.DoInOrder)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.SequentialResponsePanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.SequentialResponsePanel)viewController).set((edu.cmu.cs.stage3.alice.core.response.DoInOrder)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.IfElsePanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.IfElsePanel)viewController).set((edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.LoopNPanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.LoopNPanel)viewController).set((edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.While)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.WhilePanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.WhilePanel)viewController).set((edu.cmu.cs.stage3.alice.core.question.userdefined.While)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.ForEachPanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor.ForEachPanel)viewController).set((edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.response.Print)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ResponsePrintViewController.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ResponsePrintViewController)viewController).set((edu.cmu.cs.stage3.alice.core.response.Print)o);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Print)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.QuestionPrintViewController.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.QuestionPrintViewController)viewController).set((edu.cmu.cs.stage3.alice.core.question.userdefined.Print)o);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component)) {
      viewController = getOrCreateGUI(FormattedElementViewController.class);
      if (viewController != null) {
        ((FormattedElementViewController)viewController).setElement((Element)o);
      }
    } else if (((o instanceof Response)) || ((o instanceof edu.cmu.cs.stage3.alice.core.Question))) {
      viewController = getOrCreateGUI(FormattedElementViewController.class);
      if (viewController != null) {
        ((FormattedElementViewController)viewController).setElement((Element)o);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.behavior.TriggerBehavior)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.TriggerBehaviorPanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.TriggerBehaviorPanel)viewController).set((edu.cmu.cs.stage3.alice.core.behavior.TriggerBehavior)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.behavior.AbstractConditionalBehavior)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.ConditionalBehaviorPanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.ConditionalBehaviorPanel)viewController).set((edu.cmu.cs.stage3.alice.core.behavior.AbstractConditionalBehavior)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.behavior.InternalResponseBehavior)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.InternalResponseBehaviorPanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.InternalResponseBehaviorPanel)viewController).set((edu.cmu.cs.stage3.alice.core.behavior.InternalResponseBehavior)o, authoringTool);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.Behavior)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.GenericBehaviorPanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.GenericBehaviorPanel)viewController).set((edu.cmu.cs.stage3.alice.core.Behavior)o, authoringTool);
      }
    } else if ((o instanceof Sound)) {
      viewController = getOrCreateGUI(SoundViewController.class);
      if (viewController != null) {
        ((SoundViewController)viewController).setSound((Sound)o);
      }
    } else if ((o instanceof edu.cmu.cs.stage3.alice.core.TextureMap)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.TextureMapViewController.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.TextureMapViewController)viewController).setTextureMap((edu.cmu.cs.stage3.alice.core.TextureMap)o);
      }
    } else if ((o instanceof CallToUserDefinedResponsePrototype)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedResponsePrototypeDnDPanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedResponsePrototypeDnDPanel)viewController).set((CallToUserDefinedResponsePrototype)o);
      }
    } else if ((o instanceof CallToUserDefinedQuestionPrototype)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedQuestionPrototypeDnDPanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedQuestionPrototypeDnDPanel)viewController).set((CallToUserDefinedQuestionPrototype)o);
      }
    } else if ((o instanceof ElementPrototype))
    {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPrototypeDnDPanel.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPrototypeDnDPanel)viewController).set((ElementPrototype)o);
      }
    } else if ((o instanceof Element)) {
      viewController = getOrCreateGUI(ElementDnDPanel.class);
      if (viewController != null) {
        ((ElementDnDPanel)viewController).set((Element)o);
      }
    } else if ((o instanceof GalleryViewer.ObjectXmlData)) {
      GalleryViewer.ObjectXmlData objectXmlData = (GalleryViewer.ObjectXmlData)o;
      if (directoryData == null) {
        if (type == 2) {
          viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.WebGalleryObject.class);
        } else if ((type == 1) || 
          (type == 3)) {
          viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.LocalGalleryObject.class);
        }
      }
      else if (type == 2) {
        viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.WebGalleryDirectory.class);
      } else if ((type == 1) || 
        (type == 3)) {
        viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.LocalGalleryDirectory.class);
      }
      
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject)viewController).set((GalleryViewer.ObjectXmlData)o);
      }
    }
    
    return viewController;
  }
  
  public static EditObjectButton getEditObjectButton(Object object, JComponent animationSource) {
    AuthoringTool authoringTool = AuthoringTool.getHack();
    EditObjectButton editObjectButton = (EditObjectButton)getOrCreateGUI(EditObjectButton.class);
    editObjectButton.setAuthoringTool(authoringTool);
    editObjectButton.setObject(object);
    editObjectButton.setAnimationSource(animationSource);
    return editObjectButton;
  }
  
  public static void releaseGUI(GUIElement guiElement)
  {
    guiElement.clean();
    Class guiClass = guiElement.getClass();
    Set guiSet = (Set)guiCache.get(guiClass);
    if (guiSet == null) {
      guiSet = new java.util.HashSet();
      guiCache.put(guiClass, guiSet);
    }
    guiSet.add(guiElement);
  }
  


  protected static JComponent getOrCreateGUI(Class guiClass)
  {
    Set guiSet = (Set)guiCache.get(guiClass);
    if ((guiSet != null) && (!guiSet.isEmpty())) {
      JComponent guiElement = (JComponent)guiSet.iterator().next();
      if (guiElement.getParent() != null) {
        guiElement.getParent().remove(guiElement);
      }
      guiSet.remove(guiElement);
      


      return guiElement;
    }
    try {
      return (JComponent)guiClass.newInstance();
    } catch (Throwable t) {
      AuthoringTool.showErrorDialog(Messages.getString("Error_creating_new_GUI__") + guiClass, t);
    }
    
    return null;
  }
  
  public static PropertyGUI getPropertyGUI(Property property, boolean includeDefaults, boolean allowExpressions, PopupItemFactory factory) {
    PropertyGUI propertyGUI = (PropertyGUI)getOrCreateGUI(PropertyGUI.class);
    if (propertyGUI != null) {
      propertyGUI.set(AuthoringTool.getHack(), property, includeDefaults, allowExpressions, factory);
    }
    return propertyGUI;
  }
  
  public static String cleanHTMLString(String toReturn) {
    if (toReturn == "") return toReturn;
    while ((toReturn != null) && (toReturn.indexOf("<") > -1)) {
      toReturn = toReturn.substring(0, toReturn.indexOf("<")) + "&lt;" + toReturn.substring(toReturn.indexOf("<") + 1, toReturn.length());
    }
    if (toReturn == "") return toReturn;
    while ((toReturn != null) && (toReturn.indexOf(">") > -1)) {
      toReturn = toReturn.substring(0, toReturn.indexOf(">")) + "&gt;" + toReturn.substring(toReturn.indexOf(">") + 1, toReturn.length());
    }
    if (toReturn == "") return toReturn;
    while ((toReturn != null) && (toReturn.indexOf("\"") > -1)) {
      toReturn = toReturn.substring(0, toReturn.indexOf("\"")) + "&quot;" + toReturn.substring(toReturn.indexOf("\"") + 1, toReturn.length());
    }
    return toReturn;
  }
  
  public static String getHTMLStringForComponent(java.awt.Component c) {
    String toReturn = "";
    
    if ((c instanceof JLabel)) {
      if (((JLabel)c).getIcon() != null) {
        javax.swing.Icon icon = ((JLabel)c).getIcon();
        if (icon.equals(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("mouse"))) {
          toReturn = Messages.getString("_b_the_mouse__b_");
        } else if (icon.equals(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("arrowKeys"))) {
          toReturn = Messages.getString("_b_the_arrow_keys__b_");
        }
      }
      String labelText = cleanHTMLString(((JLabel)c).getText());
      if (((JLabel)c).getFont().isBold()) {
        toReturn = toReturn + "<b>" + labelText + "</b>";
      }
      if (((JLabel)c).getFont().isItalic()) {
        toReturn = toReturn + "<i>" + labelText + "</i>";
      }
      toReturn = " " + toReturn + " ";
      if ((((JLabel)c).getText() == null) || (((JLabel)c).getText().equals("more..."))) {
        toReturn = "";
      }
    } else if ((c instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController)) {
      edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController viewControllerC = (edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController)c;
      StringBuffer htmlStringBuf = new StringBuffer();
      viewControllerC.getHTML(htmlStringBuf);
      toReturn = " " + htmlStringBuf.toString() + " ";
    } else if ((c instanceof CollectionPropertyViewController)) {
      CollectionPropertyViewController viewControllerC = (CollectionPropertyViewController)c;
      StringBuffer htmlStringBuf = new StringBuffer();
      viewControllerC.getHTML(htmlStringBuf);
      toReturn = " " + htmlStringBuf.toString() + " ";
    } else if ((c instanceof Container)) {
      Container containerC = (Container)c;
      for (int i = 0; i < containerC.getComponentCount(); i++) {
        toReturn = toReturn + getHTMLStringForComponent(containerC.getComponent(i)) + " ";
      }
    }
    toReturn = toReturn.trim();
    while (toReturn.indexOf("  ") > -1) {
      int index = toReturn.indexOf("  ");
      toReturn = toReturn.substring(0, index) + toReturn.substring(index + 1, toReturn.length());
    }
    
    return toReturn;
  }
  
  public static PropertyDnDPanel getPropertyDnDPanel(Property property) {
    PropertyDnDPanel propertyDnDPanel = (PropertyDnDPanel)getOrCreateGUI(PropertyDnDPanel.class);
    if (propertyDnDPanel != null) {
      propertyDnDPanel.set(AuthoringTool.getHack(), property);
    }
    return propertyDnDPanel;
  }
  

  /**
   * @deprecated
   */
  public static JComponent createPropertyViewController(Property property, boolean includeDefaults, boolean allowExpressions, boolean omitPropertyName, PopupItemFactory factory) { return getPropertyViewController(property, includeDefaults, allowExpressions, omitPropertyName, factory); }
  
  public static JComponent getPropertyViewController(Property property, boolean includeDefaults, boolean allowExpressions, boolean omitPropertyName, PopupItemFactory factory) {
    JComponent viewController = null;
    Class desiredValueClass = PopupMenuUtilities.getDesiredValueClass(property);
    
    if ((property.getName().equals("keyCode")) && (Integer.class.isAssignableFrom(desiredValueClass))) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.KeyCodePropertyViewController.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.KeyCodePropertyViewController)viewController).set(property, allowExpressions, factory);
      }
    } else if (Boolean.class.isAssignableFrom(desiredValueClass)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.BooleanPropertyViewController.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.BooleanPropertyViewController)viewController).set(property, includeDefaults, allowExpressions, omitPropertyName, factory);
      }
    } else if (edu.cmu.cs.stage3.alice.scenegraph.Color.class.isAssignableFrom(desiredValueClass)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ColorPropertyViewController.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ColorPropertyViewController)viewController).set(property, allowExpressions, omitPropertyName, factory);
      }
    } else if (Number.class.isAssignableFrom(desiredValueClass)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.NumberPropertyViewController.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.NumberPropertyViewController)viewController).set(property, includeDefaults, allowExpressions, omitPropertyName, factory);
      }
    } else if (Response.class.isAssignableFrom(desiredValueClass)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ResponsePropertyViewController.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ResponsePropertyViewController)viewController).set(property, allowExpressions, omitPropertyName, factory);
      }
    }
    else if (String.class.isAssignableFrom(desiredValueClass)) {
      boolean emptyStringWritesNull = true;
      if (property.getName().equals("script")) {
        allowExpressions = false;
        emptyStringWritesNull = false;
      }
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.StringPropertyViewController.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.StringPropertyViewController)viewController).set(property, includeDefaults, allowExpressions, omitPropertyName, emptyStringWritesNull, factory);
      }
    } else if (java.awt.Font.class.isAssignableFrom(desiredValueClass)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FontPropertyViewController.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FontPropertyViewController)viewController).set(property, omitPropertyName);
      }
    } else if (edu.cmu.cs.stage3.alice.core.Style.class.isAssignableFrom(desiredValueClass)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.StylePropertyViewController.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.StylePropertyViewController)viewController).set(property, allowExpressions, omitPropertyName, factory);
      }
    } else if (edu.cmu.cs.stage3.util.Enumerable.class.isAssignableFrom(desiredValueClass)) {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.EnumerablePropertyViewController.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.EnumerablePropertyViewController)viewController).set(property, allowExpressions, omitPropertyName, factory);
      }
    } else if (edu.cmu.cs.stage3.alice.core.Collection.class.isAssignableFrom(desiredValueClass)) {
      if (allowExpressions) {
        viewController = getOrCreateGUI(ElementPropertyViewController.class);
        if (viewController != null) {
          ((ElementPropertyViewController)viewController).set(property, allowExpressions, omitPropertyName, factory);
        }
      } else {
        viewController = getOrCreateGUI(CollectionPropertyViewController.class);
        if (viewController != null) {
          ((CollectionPropertyViewController)viewController).set(property, omitPropertyName);
        }
      }
    } else if (Sound.class.isAssignableFrom(desiredValueClass)) {
      viewController = getOrCreateGUI(SoundViewController.class);
      
      if (viewController != null) {
        ((SoundViewController)viewController).setSound((Sound)property.getValue());
      }
      try {
        Thread.sleep(200L);
      }
      catch (InterruptedException localInterruptedException) {}
      

      viewController = getOrCreateGUI(ElementPropertyViewController.class);
      if (viewController != null) {
        ((ElementPropertyViewController)viewController).set(property, allowExpressions, omitPropertyName, factory);
      }
    } else if (Element.class.isAssignableFrom(desiredValueClass)) {
      viewController = getOrCreateGUI(ElementPropertyViewController.class);
      if (viewController != null) {
        ((ElementPropertyViewController)viewController).set(property, allowExpressions, omitPropertyName, factory);
      }
    } else if ((property instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)) {
      edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)property;
      if (Variable.class.isAssignableFrom(oap.getComponentType())) {
        viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariablesViewController.class);
        if (viewController != null) {
          ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.VariablesViewController)viewController).set(oap);
        }
      }
    } else {
      viewController = getOrCreateGUI(edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.DefaultPropertyViewController.class);
      if (viewController != null) {
        ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.DefaultPropertyViewController)viewController).set(property, includeDefaults, allowExpressions, false, omitPropertyName, factory);
      }
    }
    
    return viewController;
  }
  
  /**
   * @deprecated
   */
  public static JComponent createVariableGUI(Variable variable, boolean includeDefaults, PopupItemFactory factory) {
    return getVariableGUI(variable, includeDefaults, factory);
  }
  













  /**
   * @deprecated
   */
  public static JComponent createVariableDnDPanel(Variable variable)
  {
    return getVariableDnDPanel(variable);
  }
  
  public static VariableGUI getVariableGUI(Variable variable, boolean includeDefaults, PopupItemFactory factory) {
    VariableGUI variableGUI = (VariableGUI)getOrCreateGUI(VariableGUI.class);
    if (variableGUI != null) {
      variableGUI.set(AuthoringTool.getHack(), variable, includeDefaults, factory);
    }
    return variableGUI;
  }
  
  public static VariableDnDPanel getVariableDnDPanel(Variable variable) {
    VariableDnDPanel variableDnDPanel = (VariableDnDPanel)getOrCreateGUI(VariableDnDPanel.class);
    if (variableDnDPanel != null) {
      variableDnDPanel.set(AuthoringTool.getHack(), variable);
    }
    return variableDnDPanel;
  }
  
  /**
   * @deprecated
   */
  public static JComponent createResponseViewController(Response response) {
    return getGUI(response);
  }
  
  /**
   * @deprecated
   */
  public static JComponent createQuestionViewController(edu.cmu.cs.stage3.alice.core.Question question) {
    return getGUI(question);
  }
  
  /**
   * @deprecated
   */
  public static JComponent createSoundViewController(Sound sound) {
    return getGUI(sound);
  }
  
  /**
   * @deprecated
   */
  public static JComponent createTextureMapViewController(edu.cmu.cs.stage3.alice.core.TextureMap textureMap) {
    return getGUI(textureMap);
  }
  
  /**
   * @deprecated
   */
  public static JComponent createElementPrototypeDnDPanel(ElementPrototype elementPrototype) {
    return getGUI(elementPrototype);
  }
  
  /**
   * @deprecated
   */
  public static JComponent createResponsePrototypeDnDPanel(ResponsePrototype responsePrototype) {
    return getGUI(responsePrototype);
  }
  
  /**
   * @deprecated
   */
  public static JComponent createCallToUserDefinedResponsePrototypeDnDPanel(CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype) {
    return getGUI(callToUserDefinedResponsePrototype);
  }
  
  /**
   * @deprecated
   */
  public static JComponent createRightClickNameEditor(Element element) {
    return getElementNamePropertyViewController(element);
  }
  
  public static ElementNamePropertyViewController getElementNamePropertyViewController(Element element) {
    ElementNamePropertyViewController enpvc = (ElementNamePropertyViewController)getOrCreateGUI(ElementNamePropertyViewController.class);
    enpvc.set(element);
    return enpvc;
  }
  
  public static ElementDnDPanel getElementDnDPanel(Element element) {
    ElementDnDPanel panel = (ElementDnDPanel)getOrCreateGUI(ElementDnDPanel.class);
    panel.set(element);
    return panel;
  }
  
  public static boolean isOtherDialogSupportedForClass(Class valueClass) {
    if (Number.class.isAssignableFrom(valueClass))
      return true;
    if (edu.cmu.cs.stage3.alice.core.Style.class.isAssignableFrom(valueClass))
      return false;
    if (edu.cmu.cs.stage3.alice.scenegraph.Color.class.isAssignableFrom(valueClass))
      return true;
    if (String.class.isAssignableFrom(valueClass))
      return true;
    if (Response.class.isAssignableFrom(valueClass)) {
      return true;
    }
    return false;
  }
  
  public static void showOtherPropertyDialog(Property property, PopupItemFactory factory)
  {
    showOtherPropertyDialog(property, factory, null);
  }
  
  public static void showOtherPropertyDialog(Property property, PopupItemFactory factory, java.awt.Point location) {
    showOtherPropertyDialog(property, factory, location, null);
  }
  
  public static void showOtherPropertyDialog(Property property, PopupItemFactory factory, java.awt.Point location, Class valueClass) {
    if (valueClass == null) {
      valueClass = property.getValueClass();
    }
    if (Number.class.isAssignableFrom(valueClass)) {
      String initialValue = "";
      if (property.getValue() != null) {
        String propertyKey = "edu.cmu.cs.stage3.alice.authoringtool.userRepr." + property.getName();
        Object userRepr = getOwnerdata.get(propertyKey);
        if ((userRepr instanceof String)) {
          initialValue = (String)userRepr;
        } else {
          initialValue = property.getValue().toString();
        }
      }
      NumPad numPad = new NumPad();
      numPad.setNumberString(initialValue);
      numPad.selectAll();
      int result = DialogManager.showDialog(numPad);
      if (result == 0) {
        String numberString = numPad.getNumberString();
        Double number = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.parseDouble(numberString);
        ((Runnable)factory.createItem(number)).run();
        String propertyKey = "edu.cmu.cs.stage3.alice.authoringtool.userRepr." + property.getName();
        getOwnerdata.put(propertyKey, numberString);
        PopupMenuUtilities.addRecentlyUsedValue(valueClass, number);
      }
    } else if (edu.cmu.cs.stage3.alice.core.Style.class.isAssignableFrom(valueClass)) {
      System.out.println(Messages.getString("Not_supported_yet"));
    } else if (edu.cmu.cs.stage3.alice.scenegraph.Color.class.isAssignableFrom(valueClass)) {
      java.awt.Color currentColor = java.awt.Color.white;
      if ((property.getValue() instanceof edu.cmu.cs.stage3.alice.scenegraph.Color)) {
        currentColor = ((edu.cmu.cs.stage3.alice.scenegraph.Color)property.getValue()).createAWTColor();
      }
      javax.swing.JColorChooser colorChooser = new javax.swing.JColorChooser();
      java.awt.Color color = DialogManager.showDialog(colorChooser, Messages.getString("Custom_Color"), currentColor);
      if (color != null) {
        ((Runnable)factory.createItem(new edu.cmu.cs.stage3.alice.scenegraph.Color(color))).run();
        PopupMenuUtilities.addRecentlyUsedValue(valueClass, new edu.cmu.cs.stage3.alice.scenegraph.Color(color));
      }
    } else if (String.class.isAssignableFrom(valueClass)) {
      Object currentValue = property.getValue();
      String currentString = "";
      if (currentValue != null) {
        currentString = currentValue.toString();
      }
      String string = (String)DialogManager.showInputDialog(Messages.getString("Enter_a_string_"), Messages.getString("Enter_a_string"), -1, null, null, currentString);
      if (string != null) {
        ((Runnable)factory.createItem(string)).run();
        PopupMenuUtilities.addRecentlyUsedValue(valueClass, string);
      }
    } else if (Response.class.isAssignableFrom(valueClass)) {
      String script = DialogManager.showInputDialog(Messages.getString("Please_enter_a_jython_script_that_will_evaluate_to_a_response_"), Messages.getString("Custom_Response_Script"), -1);
      ScriptDefinedResponse scriptResponse = new ScriptDefinedResponse();
      script.set(script);
      property.getOwner().addChild(scriptResponse);
      ((Runnable)factory.createItem(scriptResponse)).run();
    } else {
      AuthoringTool.showErrorDialog(Messages.getString("Other____is_not_supported_for_") + valueClass.getName(), null);
    }
  }
  
  public static void showOtherDialog(Class valueClass, Object initialValue, PopupItemFactory factory, Element anchorForAnonymousItems)
  {
    if ((initialValue != null) && (!valueClass.isAssignableFrom(initialValue.getClass()))) {
      initialValue = null;
    }
    
    if (Number.class.isAssignableFrom(valueClass)) {
      if (initialValue == null) {
        initialValue = "1";
      }
      NumPad numPad = new NumPad();
      numPad.setNumberString((String)initialValue);
      numPad.selectAll();
      int result = DialogManager.showDialog(numPad);
      if (result == 0) {
        String numberString = numPad.getNumberString();
        Double number = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.parseDouble(numberString);
        ((Runnable)factory.createItem(number)).run();
        PopupMenuUtilities.addRecentlyUsedValue(valueClass, number);
      }
      
    }
    else if (!edu.cmu.cs.stage3.alice.core.Style.class.isAssignableFrom(valueClass))
    {
      if (edu.cmu.cs.stage3.alice.scenegraph.Color.class.isAssignableFrom(valueClass)) {
        java.awt.Color currentColor = java.awt.Color.white;
        if (initialValue != null) {
          currentColor = ((edu.cmu.cs.stage3.alice.scenegraph.Color)initialValue).createAWTColor();
        }
        javax.swing.JColorChooser colorChooser = new javax.swing.JColorChooser();
        java.awt.Color color = DialogManager.showDialog(colorChooser, Messages.getString("Custom_Color"), currentColor);
        if (color != null) {
          ((Runnable)factory.createItem(new edu.cmu.cs.stage3.alice.scenegraph.Color(color))).run();
          PopupMenuUtilities.addRecentlyUsedValue(valueClass, new edu.cmu.cs.stage3.alice.scenegraph.Color(color));
        }
      } else if (String.class.isAssignableFrom(valueClass)) {
        String currentString = "";
        if (initialValue != null) {
          currentString = (String)initialValue;
        }
        String string = (String)DialogManager.showInputDialog(Messages.getString("Enter_a_string_"), Messages.getString("Enter_a_string"), -1, null, null, currentString);
        if (string != null) {
          ((Runnable)factory.createItem(string)).run();
          PopupMenuUtilities.addRecentlyUsedValue(valueClass, string);
        }
      } else if (Response.class.isAssignableFrom(valueClass)) {
        String script = DialogManager.showInputDialog(Messages.getString("Please_enter_a_jython_script_that_will_evaluate_to_a_response_"), Messages.getString("Custom_Response_Script"), -1);
        ScriptDefinedResponse scriptResponse = new ScriptDefinedResponse();
        script.set(script);
        if (anchorForAnonymousItems != null) {
          anchorForAnonymousItems.addChild(scriptResponse);
        }
        ((Runnable)factory.createItem(scriptResponse)).run();
      } else {
        AuthoringTool.showErrorDialog(Messages.getString("Other____is_not_supported_for_") + valueClass.getName(), null);
      }
    }
  }
  
  public static void showScriptDefinedPropertyDialog(Property property, PopupItemFactory factory) { Class valueClass = property.getValueClass();
    String initialValue = "";
    if ((property.get() instanceof edu.cmu.cs.stage3.alice.core.question.AbstractScriptDefinedObject)) {
      initialValue = getevalScript.getStringValue();
    }
    String script = (String)DialogManager.showInputDialog(Messages.getString("Please_enter_a_jython_script_that_will_evaluate_to_the_appropriate_type_"), Messages.getString("Script_Expression"), -1, null, null, initialValue);
    if (script != null) {
      edu.cmu.cs.stage3.alice.core.question.ScriptDefinedObject scriptDefinedObject = new edu.cmu.cs.stage3.alice.core.question.ScriptDefinedObject();
      valueClass.set(valueClass);
      evalScript.set(script);
      property.getOwner().addChild(scriptDefinedObject);
      ((Runnable)factory.createItem(scriptDefinedObject)).run();
    }
  }
}
