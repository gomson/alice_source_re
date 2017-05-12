package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.alice.authoringtool.editors.variablegroupeditor.VariableGroupEditor;
import edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Pose;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation;
import edu.cmu.cs.stage3.alice.core.response.PropertyAnimation;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.Transferable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.border.Border;

public class DragFromComponent extends JPanel implements edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener
{
  public static final int PROPERTIES_TAB = 0;
  public static final int ANIMATIONS_TAB = 1;
  public static final int QUESTIONS_TAB = 2;
  public static final int OTHER_TAB = 3;
  protected edu.cmu.cs.stage3.alice.authoringtool.util.Configuration config = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(JAlice.class.getPackage());
  
  protected Element element;
  protected VariableGroupEditor variableGroupEditor = new VariableGroupEditor();
  protected edu.cmu.cs.stage3.alice.authoringtool.dialog.NewResponseContentPane newResponseContentPane;
  protected edu.cmu.cs.stage3.alice.authoringtool.dialog.NewQuestionContentPane newQuestionContentPane;
  protected ObjectArrayProperty vars;
  protected ObjectArrayProperty responses;
  protected ResponsesListener responsesListener = new ResponsesListener();
  protected ObjectArrayProperty questions;
  protected QuestionsListener questionsListener = new QuestionsListener();
  protected ObjectArrayProperty poses;
  protected PosesListener posesListener = new PosesListener();
  protected GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 10, 2, new Insets(0, 0, 0, 0), 0, 0);
  protected GridBagConstraints glueConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0);
  protected Border spacingBorder = BorderFactory.createEmptyBorder(4, 0, 8, 0);
  protected edu.cmu.cs.stage3.alice.core.event.ChildrenListener parentListener = new edu.cmu.cs.stage3.alice.core.event.ChildrenListener() {
    private Element parent;
    
    public void childrenChanging(ChildrenEvent ev) { if ((ev.getChild() == element) && (ev.getChangeType() == 3))
        parent = element.getParent();
    }
    
    public void childrenChanged(ChildrenEvent ev) {
      if ((ev.getChild() == element) && (ev.getChangeType() == 3)) {
        setElement(null);
        parent.removeChildrenListener(this);
      }
    }
  };
  protected edu.cmu.cs.stage3.alice.core.event.PropertyListener nameListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
    public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) {}
    
    public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev) { ownerLabel.setText(ev.getValue().toString() + Messages.getString("_s_details")); }
  };
  
  protected JButton newAnimationButton = new JButton(Messages.getString("create_new_method"));
  protected JButton newQuestionButton = new JButton(Messages.getString("create_new_") + AuthoringToolResources.QUESTION_STRING);
  protected JButton capturePoseButton = new JButton(Messages.getString("capture_pose"));
  
  protected UserDefinedResponse newlyCreatedAnimation;
  protected UserDefinedQuestion newlyCreatedQuestion;
  protected Pose newlyCreatedPose;
  protected AuthoringTool authoringTool;
  protected edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.SoundsPanel soundsPanel;
  protected edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.TextureMapsPanel textureMapsPanel;
  protected edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ObjectArrayPropertyPanel miscPanel;
  protected HashSet panelsToClean = new HashSet();
  
  public DragFromComponent(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
    variableGroupEditor.setAuthoringTool(authoringTool);
    newResponseContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.NewResponseContentPane();
    newQuestionContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.NewQuestionContentPane();
    soundsPanel = new edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.SoundsPanel(authoringTool);
    textureMapsPanel = new edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.TextureMapsPanel(authoringTool);
    miscPanel = new edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ObjectArrayPropertyPanel(Messages.getString("Misc"), authoringTool);
    jbInit();
    guiInit();
  }
  
  private void guiInit() {
    newAnimationButton.setBackground(new Color(240, 240, 255));
    newAnimationButton.setMargin(new Insets(2, 4, 2, 4));
    newAnimationButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent ev) {
          if (responses != null) {
            newResponseContentPane.reset(responses.getOwner());
            int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(newResponseContentPane);
            if (result == 0) {
              authoringTool.getUndoRedoStack().startCompound();
              try {
                UserDefinedResponse response = new UserDefinedResponse();
                name.set(newResponseContentPane.getNameValue());
                responses.getOwner().addChild(response);
                responses.add(response);
              } finally {
                authoringTool.getUndoRedoStack().stopCompound();
              }
              
            }
          }
        }
      });
    newQuestionButton.setBackground(new Color(240, 240, 255));
    newQuestionButton.setMargin(new Insets(2, 4, 2, 4));
    newQuestionButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent ev) {
          if (questions != null) {
            newQuestionContentPane.reset(questions.getOwner());
            int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(newQuestionContentPane);
            if (result == 0) {
              authoringTool.getUndoRedoStack().startCompound();
              try {
                UserDefinedQuestion question = new UserDefinedQuestion();
                name.set(newQuestionContentPane.getNameValue());
                valueClass.set(newQuestionContentPane.getTypeValue());
                questions.getOwner().addChild(question);
                questions.add(question);
              } finally {
                authoringTool.getUndoRedoStack().stopCompound();
              }
              
            }
            
          }
        }
      });
    capturePoseButton.setBackground(new Color(240, 240, 255));
    capturePoseButton.setMargin(new Insets(2, 4, 2, 4));
    capturePoseButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent ev) {
          if (poses != null) {
            authoringTool.getUndoRedoStack().startCompound();
            try {
              Transformable transformable = (Transformable)poses.getOwner();
              Pose pose = Pose.manufacturePose(transformable, transformable);
              name.set(AuthoringToolResources.getNameForNewChild("pose", poses.getOwner()));
              poses.getOwner().addChild(pose);
              poses.add(pose);
            } finally {
              authoringTool.getUndoRedoStack().stopCompound();
            }
            
          }
          
        }
      });
    tabbedPane.setUI(new edu.cmu.cs.stage3.alice.authoringtool.util.AliceTabbedPaneUI());
    tabbedPane.setOpaque(false);
    tabbedPane.setSelectedIndex(1);
    

    propertiesScrollPane.setBackground(Color.white);
    animationsScrollPane.setBackground(Color.white);
    questionsScrollPane.setBackground(Color.white);
    otherScrollPane.setBackground(Color.white);
    
    soundsPanel.setExpanded(false);
    textureMapsPanel.setExpanded(false);
    miscPanel.setExpanded(false);
    

    String cappedQuestionString = AuthoringToolResources.QUESTION_STRING.substring(0, 1).toUpperCase() + AuthoringToolResources.QUESTION_STRING.substring(1);
    comboPanel.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("This_area_displays_the_details_p_of_the_Selected_Object_") + "</font></html>");
    tabbedPane.setToolTipTextAt(0, "<html><font face=arial size=-1>" + Messages.getString("Open_the_Properties_Tab_p_of_the_Selected_Object__p__p_Use_this_tab_to_view_and_edit_p_the_Properties_of_the_Selected_Object_") + "</font></html>");
    tabbedPane.setToolTipTextAt(1, "<html><font face=arial size=-1>" + Messages.getString("Open_the_Methods_Tab_p_of_the_Selected_Object__p__p_Use_this_tab_to_view_and_edit_p_the_Methods_of_the_Selected_Object_") + "</font></html>");
    tabbedPane.setToolTipTextAt(2, "<html><font face=arial size=-1>" + Messages.getString("Open_the_") + cappedQuestionString + "s" + " " + Messages.getString("Tab_p_of_the_Selected_Object__p__p_Use_this_tab_to_view_and_edit_p_the_") + cappedQuestionString + "s " + Messages.getString("of_the_Selected_Object_") + "</font></html>");
    newAnimationButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Create_a_New_Blank_Method_p_and_Open_it_for_Editing_") + "</font></html>");
    newQuestionButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Create_a_New_Blank_") + cappedQuestionString + Messages.getString("_p_and_Open_it_for_Editing_") + "</font></html>");
    propertiesPanel.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Properties_Tab_p__p_This_tab_allows_you_to_view_and_edit_p_the_Properties_of_the_Selected_Object_") + "</font></html>");
    animationsPanel.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Methods_Tab_p__p_Methods_are_the_actions_that_an_object_knows_how_to_do__p_Most_objects_come_with_default_methods__and_you_can_p_create_your_own_methods_as_well_") + "</font></html>");
    questionsPanel.setToolTipText("<html><font face=arial size=-1>" + cappedQuestionString + "s" + " " + Messages.getString("Tab_p__p_") + cappedQuestionString + "s" + " " + Messages.getString("are_the_things_that_an_object_can_p_answer_about_themselves_or_the_world_") + "</font></html>");
  }
  
  public void paintComponent(java.awt.Graphics g) {
    super.paintComponent(g);
    g.setColor(Color.black);
    g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
  }
  
  public void elementSelected(Element element)
  {
    setElement(element);
    authoringTool.hackStencilUpdate();
  }
  
















  public Element getElement()
  {
    return element;
  }
  
  public synchronized void setElement(Element element) {
    vars = null;
    if (responses != null) {
      responses.removeObjectArrayPropertyListener(responsesListener);
      responses = null;
    }
    if (questions != null) {
      questions.removeObjectArrayPropertyListener(questionsListener);
      questions = null;
    }
    if (poses != null) {
      poses.removeObjectArrayPropertyListener(posesListener);
      poses = null;
    }
    if (this.element != null) {
      if (this.element.getParent() != null) {
        this.element.getParent().removeChildrenListener(parentListener);
      }
      elementname.removePropertyListener(nameListener);
    }
    
    this.element = element;
    
    if (element != null) {
      ownerLabel.setText(AuthoringToolResources.getReprForValue(element) + Messages.getString("_s_details"));
      if (element.getParent() != null) {
        element.getParent().addChildrenListener(parentListener);
      }
      name.addPropertyListener(nameListener);
      if (element.getSandbox() == element) {
        vars = ((ObjectArrayProperty)element.getPropertyNamed("variables"));
        responses = ((ObjectArrayProperty)element.getPropertyNamed("responses"));
        if (responses != null) {
          responses.addObjectArrayPropertyListener(responsesListener);
        }
        questions = ((ObjectArrayProperty)element.getPropertyNamed("questions"));
        if (questions != null) {
          questions.addObjectArrayPropertyListener(questionsListener);
        }
      }
      if ((element instanceof Transformable)) {
        poses = poses;
        if (poses != null) {
          poses.addObjectArrayPropertyListener(posesListener);
        }
      }
    } else {
      ownerLabel.setText("");
    }
    
    int selectedIndex = tabbedPane.getSelectedIndex();
    refreshGUI();
    tabbedPane.setSelectedIndex(selectedIndex);
  }
  
  public void selectTab(int index) {
    tabbedPane.setSelectedIndex(index);
  }
  
  public int getSelectedTab() {
    return tabbedPane.getSelectedIndex();
  }
  
  public String getKeyForComponent(java.awt.Component c) {
    World world = authoringTool.getWorld();
    if (c == variableGroupEditor.getNewVariableButton())
      return "newVariableButton";
    if (c == newAnimationButton)
      return "newAnimationButton";
    if (c == newQuestionButton)
      return "newQuestionButton";
    if ((c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel))
      try {
        Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel)c).getTransferable();
        if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor)) {
          edu.cmu.cs.stage3.alice.core.Variable v = (edu.cmu.cs.stage3.alice.core.Variable)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor);
          return "variable<" + v.getKey(world) + ">"; }
        if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.textureMapReferenceFlavor)) {
          edu.cmu.cs.stage3.alice.core.TextureMap t = (edu.cmu.cs.stage3.alice.core.TextureMap)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.textureMapReferenceFlavor);
          return "textureMap<" + t.getKey(world) + ">"; }
        if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.Sound.class))) {
          edu.cmu.cs.stage3.alice.core.Sound s = (edu.cmu.cs.stage3.alice.core.Sound)transferable.getTransferData(AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.Sound.class));
          return "sound<" + s.getKey(world) + ">"; }
        if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
          Element e = (Element)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
          return "misc<" + e.getKey(world) + ">"; }
        if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor)) {
          Property p = (Property)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor);
          return "property<" + p.getName() + ">"; }
        if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor)) {
          edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor);
          return "userDefinedResponse<" + p.getActualResponse().getKey(world) + ">"; }
        if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor)) {
          edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor);
          return "userDefinedQuestion<" + p.getActualQuestion().getKey(world) + ">"; }
        if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
          ElementPrototype p = (ElementPrototype)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
          if (Response.class.isAssignableFrom(p.getElementClass()))
            return "responsePrototype<" + p.getElementClass().getName() + ">";
          if (Question.class.isAssignableFrom(p.getElementClass())) {
            return "questionPrototype<" + p.getElementClass().getName() + ">";
          }
          return null;
        }
        
        return null;
      }
      catch (Exception e) {
        AuthoringTool.showErrorDialog(Messages.getString("Error_examining_DnDGroupingPanel_"), e);
        return null;
      }
    if ((c instanceof EditObjectButton)) {
      Object o = ((EditObjectButton)c).getObject();
      if ((o instanceof Element)) {
        Element e = (Element)o;
        return "editObjectButton<" + e.getKey(world) + ">";
      }
      return null;
    }
    
    return null;
  }
  
  public java.awt.Component getComponentForKey(String key)
  {
    String prefix = AuthoringToolResources.getPrefix(key);
    String spec = AuthoringToolResources.getSpecifier(key);
    World world = authoringTool.getWorld();
    if (key.equals("newVariableButton"))
      return variableGroupEditor.getNewVariableButton();
    if (key.equals("newAnimationButton"))
      return newAnimationButton;
    if (key.equals("newQuestionButton"))
      return newQuestionButton;
    if ((prefix.equals("variable")) && (spec != null)) {
      Element e = world.getDescendantKeyed(spec);
      if (e != null) {
        return AuthoringToolResources.findElementDnDPanel(variableGroupEditor, e);
      }
    } else if ((prefix.equals("textureMap")) && (spec != null)) {
      Element e = world.getDescendantKeyed(spec);
      if (e != null) {
        return AuthoringToolResources.findElementDnDPanel(textureMapsPanel, e);
      }
    } else if ((prefix.equals("sound")) && (spec != null)) {
      Element e = world.getDescendantKeyed(spec);
      if (e != null) {
        return AuthoringToolResources.findElementDnDPanel(soundsPanel, e);
      }
    } else if ((prefix.equals("misc")) && (spec != null)) {
      Element e = world.getDescendantKeyed(spec);
      if (e != null)
        return AuthoringToolResources.findElementDnDPanel(miscPanel, e);
    } else {
      if ((prefix.equals("property")) && (spec != null))
        return AuthoringToolResources.findPropertyDnDPanel(propertiesPanel, element, spec);
      if ((prefix.equals("userDefinedResponse")) && (spec != null)) {
        Response actualResponse = (Response)world.getDescendantKeyed(spec);
        if (actualResponse != null) {
          return AuthoringToolResources.findUserDefinedResponseDnDPanel(animationsPanel, actualResponse);
        }
      } else if ((prefix.equals("userDefinedQuestion")) && (spec != null)) {
        Question actualQuestion = (Question)world.getDescendantKeyed(spec);
        if (actualQuestion != null) {
          return AuthoringToolResources.findUserDefinedQuestionDnDPanel(questionsPanel, actualQuestion);
        }
      } else if ((prefix.equals("responsePrototype")) && (spec != null)) {
        try {
          Class elementClass = Class.forName(spec);
          if (elementClass == null) break label531;
          return AuthoringToolResources.findPrototypeDnDPanel(animationsPanel, elementClass);
        }
        catch (Exception e) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_while_looking_for_ProtoypeDnDPanel_using_class_") + spec, e);
        }
      } else if ((prefix.equals("questionPrototype")) && (spec != null)) {
        try {
          Class elementClass = Class.forName(spec);
          if (elementClass == null) break label531;
          return AuthoringToolResources.findPrototypeDnDPanel(questionsPanel, elementClass);
        }
        catch (Exception e) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_while_looking_for_ProtoypeDnDPanel_using_class_") + spec, e);
        }
      } else if ((prefix.equals("editObjectButton")) && (spec != null)) {
        Element e = world.getDescendantKeyed(spec);
        if (e != null) {
          if ((e instanceof UserDefinedResponse))
            return AuthoringToolResources.findEditObjectButton(animationsPanel, e);
          if ((e instanceof UserDefinedQuestion)) {
            return AuthoringToolResources.findEditObjectButton(questionsPanel, e);
          }
          return AuthoringToolResources.findEditObjectButton(propertiesPanel, e);
        }
      }
    }
    label531:
    return null;
  }
  
  public java.awt.Component getPropertyViewComponentForKey(String key) {
    String prefix = AuthoringToolResources.getPrefix(key);
    String spec = AuthoringToolResources.getSpecifier(key);
    World world = authoringTool.getWorld();
    if (key.equals("newVariableButton"))
      return variableGroupEditor.getNewVariableButton();
    if (key.equals("newAnimationButton"))
      return newAnimationButton;
    if (key.equals("newQuestionButton"))
      return newQuestionButton;
    if ((prefix.equals("variable")) && (spec != null)) {
      Element e = world.getDescendantKeyed(spec);
      if (e != null) {
        return AuthoringToolResources.findPropertyViewController(variableGroupEditor, e, "value");
      }
    } else if ((prefix.equals("textureMap")) && (spec != null)) {
      Element e = world.getDescendantKeyed(spec);
      if (e != null) {
        return AuthoringToolResources.findElementDnDPanel(textureMapsPanel, e);
      }
    } else if ((prefix.equals("sound")) && (spec != null)) {
      Element e = world.getDescendantKeyed(spec);
      if (e != null) {
        return AuthoringToolResources.findElementDnDPanel(soundsPanel, e);
      }
    } else if ((prefix.equals("misc")) && (spec != null)) {
      Element e = world.getDescendantKeyed(spec);
      if (e != null)
        return AuthoringToolResources.findElementDnDPanel(miscPanel, e);
    } else {
      if ((prefix.equals("property")) && (spec != null))
        return AuthoringToolResources.findPropertyViewController(propertiesPanel, element, spec);
      if ((prefix.equals("userDefinedResponse")) && (spec != null)) {
        Response actualResponse = (Response)world.getDescendantKeyed(spec);
        if (actualResponse != null) {
          return AuthoringToolResources.findUserDefinedResponseDnDPanel(animationsPanel, actualResponse);
        }
      } else if ((prefix.equals("userDefinedQuestion")) && (spec != null)) {
        Question actualQuestion = (Question)world.getDescendantKeyed(spec);
        if (actualQuestion != null) {
          return AuthoringToolResources.findUserDefinedQuestionDnDPanel(questionsPanel, actualQuestion);
        }
      } else if ((prefix.equals("responsePrototype")) && (spec != null)) {
        try {
          Class elementClass = Class.forName(spec);
          if (elementClass == null) break label534;
          return AuthoringToolResources.findPrototypeDnDPanel(animationsPanel, elementClass);
        }
        catch (Exception e) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_while_looking_for_ProtoypeDnDPanel_using_class_") + spec, e);
        }
      } else if ((prefix.equals("questionPrototype")) && (spec != null)) {
        try {
          Class elementClass = Class.forName(spec);
          if (elementClass == null) break label534;
          return AuthoringToolResources.findPrototypeDnDPanel(questionsPanel, elementClass);
        }
        catch (Exception e) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_while_looking_for_ProtoypeDnDPanel_using_class_") + spec, e);
        }
      } else if ((prefix.equals("editObjectButton")) && (spec != null)) {
        Element e = world.getDescendantKeyed(spec);
        if (e != null) {
          if ((e instanceof UserDefinedResponse))
            return AuthoringToolResources.findEditObjectButton(animationsPanel, e);
          if ((e instanceof UserDefinedQuestion)) {
            return AuthoringToolResources.findEditObjectButton(questionsPanel, e);
          }
          return AuthoringToolResources.findEditObjectButton(propertiesPanel, e);
        }
      }
    }
    label534:
    return null;
  }
  
  protected void cleanPanels() {
    for (Iterator iter = panelsToClean.iterator(); iter.hasNext();) {
      JPanel panel = (JPanel)iter.next();
      java.awt.Component[] children = panel.getComponents();
      for (int i = 0; i < children.length; i++) {
        if ((children[i] instanceof edu.cmu.cs.stage3.alice.authoringtool.util.Releasable)) {
          ((edu.cmu.cs.stage3.alice.authoringtool.util.Releasable)children[i]).release();
        }
      }
      panel.removeAll();
    }
    panelsToClean.clear();
  }
  
  public synchronized void refreshGUI() {
    cleanPanels();
    propertiesPanel.removeAll();
    animationsPanel.removeAll();
    questionsPanel.removeAll();
    if (element != null) {
      constraintsgridy = 0;
      
      if (vars != null) {
        variableGroupEditor.setVariableObjectArrayProperty(vars);
        propertiesPanel.add(variableGroupEditor, this.constraints);
        constraintsgridy += 1;
      }
      

      if (poses != null) {
        JPanel subPanel = new JPanel();
        subPanel.setBackground(Color.white);
        subPanel.setLayout(new GridBagLayout());
        subPanel.setBorder(spacingBorder);
        panelsToClean.add(subPanel);
        
        int count = 0;
        Object[] poseArray = poses.getArrayValue();
        for (int i = 0; i < poseArray.length; i++) {
          Pose pose = (Pose)poseArray[i];
          
          javax.swing.JComponent gui = GUIFactory.getGUI(pose);
          if (gui != null) {
            GridBagConstraints constraints = new GridBagConstraints(0, count, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0);
            subPanel.add(gui, constraints);
            count++;
            if ((newlyCreatedPose == pose) && ((gui instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel))) {
              ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel)gui).editName();
              newlyCreatedPose = null;
            }
          } else {
            AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_pose__") + pose, null);
          }
        }
        
        GridBagConstraints c = new GridBagConstraints(0, count, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(4, 2, 2, 2), 0, 0);
        subPanel.add(capturePoseButton, c);
        
        propertiesPanel.add(subPanel, this.constraints);
        constraintsgridy += 1;
      }
      

      Vector propertyStructure = AuthoringToolResources.getPropertyStructure(element, false);
      if (propertyStructure != null) {
        for (Iterator iter = propertyStructure.iterator(); iter.hasNext();) {
          StringObjectPair sop = (StringObjectPair)iter.next();
          String groupName = sop.getString();
          Vector propertyNames = (Vector)sop.getObject();
          
          JPanel subPanel = new JPanel();
          JPanel toAdd = subPanel;
          subPanel.setBackground(Color.white);
          subPanel.setLayout(new GridBagLayout());
          subPanel.setBorder(spacingBorder);
          panelsToClean.add(subPanel);
          if (groupName.compareTo("seldom used properties") == 0) {
            ExpandablePanel expandPanel = new ExpandablePanel();
            expandPanel.setTitle(Messages.getString("Seldom_Used_Properties"));
            expandPanel.setContent(subPanel);
            expandPanel.setExpanded(false);
            toAdd = expandPanel;
          }
          
          if (propertyNames != null) {
            int i = 0;
            for (Iterator jter = propertyNames.iterator(); jter.hasNext();) {
              String name = (String)jter.next();
              final Property property = element.getPropertyNamed(name);
              if (property != null) {
                edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory oneShotFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
                  public Object createItem(final Object o) {
                    new Runnable() {
                      public void run() {
                        if (((val$property.getOwner() instanceof Transformable)) && (val$property == val$property.getOwner()).vehicle)) {
                          ((Transformable)val$property.getOwner()).setVehiclePreservingAbsoluteTransformation((edu.cmu.cs.stage3.alice.core.ReferenceFrame)o);
                        } else if (val$property.getName().equals("localTransformation")) {
                          PointOfViewAnimation povAnim = new PointOfViewAnimation();
                          subject.set(val$property.getOwner());
                          pointOfView.set(o);
                          asSeenBy.set(element.getParent());
                          PointOfViewAnimation undoResponse = new PointOfViewAnimation();
                          subject.set(val$property.getOwner());
                          pointOfView.set(val$property.getOwner()).localTransformation.getMatrix4dValue());
                          asSeenBy.set(val$property.getOwner()).vehicle.getValue());
                          Property[] properties = { val$property.getOwner()).localTransformation };
                          authoringTool.performOneShot(povAnim, undoResponse, properties);
                        } else {
                          PropertyAnimation response = new PropertyAnimation();
                          element.set(val$property.getOwner());
                          propertyName.set(val$property.getName());
                          value.set(o);
                          PropertyAnimation undoResponse = new PropertyAnimation();
                          element.set(val$property.getOwner());
                          propertyName.set(val$property.getName());
                          value.set(val$property.getValue());
                          
                          Vector pVector = new Vector();
                          pVector.add(val$property);
                          Element[] descendants = val$property.getOwner().getDescendants();
                          for (int i = 0; i < descendants.length; i++) {
                            Property p = descendants[i].getPropertyNamed(val$property.getName());
                            if (p != null) {
                              pVector.add(p);
                            }
                          }
                          Property[] properties = (Property[])pVector.toArray(new Property[0]);
                          authoringTool.performOneShot(response, undoResponse, properties);
                        }
                      }
                    };
                  }
                };
                javax.swing.JComponent gui = GUIFactory.getPropertyGUI(property, true, false, oneShotFactory);
                if (gui != null) {
                  GridBagConstraints constraints = new GridBagConstraints(0, i, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0);
                  subPanel.add(gui, constraints);
                  i++;
                } else {
                  AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_property__") + property, null);
                }
              } else {
                AuthoringTool.showErrorDialog(Messages.getString("no_property_on_") + element + " " + Messages.getString("named_") + name, null);
              }
            }
          }
          
          propertiesPanel.add(toAdd, this.constraints);
          constraintsgridy += 1;
        }
      }
      

      if ((element instanceof Sandbox)) {
        Sandbox sandbox = (Sandbox)element;
        soundsPanel.setSounds(sounds);
        propertiesPanel.add(soundsPanel, this.constraints);
        constraintsgridy += 1;
        textureMapsPanel.setTextureMaps(textureMaps);
        propertiesPanel.add(textureMapsPanel, this.constraints);
        constraintsgridy += 1;
        if (misc.size() > 0) {
          miscPanel.setObjectArrayProperty(misc);
          propertiesPanel.add(miscPanel, this.constraints);
          constraintsgridy += 1;
        }
        propertiesPanel.add(javax.swing.Box.createVerticalStrut(8), this.constraints);
        constraintsgridy += 1;
      }
      
      if (element.data.get("modeled by") != null) {
        propertiesPanel.add(new JLabel(Messages.getString("modeled_by___") + element.data.get("modeled by")), this.constraints);
        constraintsgridy += 1;
      }
      if (element.data.get("painted by") != null) {
        propertiesPanel.add(new JLabel(Messages.getString("painted_by___") + element.data.get("painted by")), this.constraints);
        constraintsgridy += 1;
      }
      if (element.data.get("programmed by") != null) {
        propertiesPanel.add(new JLabel(Messages.getString("programmed_by___") + element.data.get("programmed by")), this.constraints);
        constraintsgridy += 1;
      }
      if (element.data.get("modeled by") != null) {
        java.text.NumberFormat formatter = new java.text.DecimalFormat("#.####");
        propertiesPanel.add(new JLabel(Messages.getString("depth___") + formatter.format(((Model)element).getDepth())), this.constraints);
        constraintsgridy += 1;
        propertiesPanel.add(new JLabel(Messages.getString("height___") + formatter.format(((Model)element).getHeight())), this.constraints);
        constraintsgridy += 1;
        propertiesPanel.add(new JLabel(Messages.getString("width___") + formatter.format(((Model)element).getWidth())), this.constraints);
        constraintsgridy += 1;
      }
      glueConstraints.gridy = constraintsgridy;
      propertiesPanel.add(javax.swing.Box.createGlue(), glueConstraints);
      
      constraintsgridy = 0;
      

      if (responses != null) {
        JPanel subPanel = new JPanel();
        subPanel.setBackground(Color.white);
        subPanel.setLayout(new GridBagLayout());
        subPanel.setBorder(spacingBorder);
        panelsToClean.add(subPanel);
        
        int count = 0;
        Object[] responseArray = responses.getArrayValue();
        for (int i = 0; i < responseArray.length; i++) {
          Class responseClass = edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class;
          Response response = (Response)responseArray[i];
          
          if ((response instanceof UserDefinedResponse)) {
            edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype((UserDefinedResponse)response);
            javax.swing.JComponent gui = GUIFactory.getGUI(callToUserDefinedResponsePrototype);
            if (gui != null) {
              EditObjectButton editButton = GUIFactory.getEditObjectButton(response, gui);
              editButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Open_this_method_for_editing_") + "</font></html>");
              JPanel guiPanel = new JPanel();
              panelsToClean.add(guiPanel);
              guiPanel.setBackground(Color.white);
              guiPanel.setLayout(new GridBagLayout());
              guiPanel.add(gui, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 16, 0, new Insets(0, 0, 0, 0), 0, 0));
              guiPanel.add(editButton, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 16, 0, new Insets(0, 4, 0, 0), 0, 0));
              GridBagConstraints constraints = new GridBagConstraints(0, count, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0);
              subPanel.add(guiPanel, constraints);
              count++;
              if ((newlyCreatedAnimation == response) && ((gui instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedResponsePrototypeDnDPanel)))
              {
                authoringTool.editObject(newlyCreatedAnimation);
                newlyCreatedAnimation = null;
              }
            } else {
              AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_callToUserDefinedResponsePrototype__") + callToUserDefinedResponsePrototype, null);
            }
          } else {
            AuthoringTool.showErrorDialog(Messages.getString("Response_is_not_a_userDefinedResponse__") + response, null);
          }
        }
        
        GridBagConstraints c = new GridBagConstraints(0, count, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(4, 2, 2, 2), 0, 0);
        subPanel.add(newAnimationButton, c);
        
        animationsPanel.add(subPanel, this.constraints);
        constraintsgridy += 1;
      }
      

      Vector oneShotStructure = AuthoringToolResources.getOneShotStructure(element.getClass());
      if (oneShotStructure != null) {
        for (Iterator iter = oneShotStructure.iterator(); iter.hasNext();) {
          StringObjectPair sop = (StringObjectPair)iter.next();
          
          Vector responseNames = (Vector)sop.getObject();
          JPanel subPanel = new JPanel();
          JPanel toAdd = subPanel;
          subPanel.setBackground(Color.white);
          subPanel.setLayout(new GridBagLayout());
          subPanel.setBorder(spacingBorder);
          panelsToClean.add(subPanel);
          
          if (responseNames != null)
          {

            int i = 0;
            for (Iterator jter = responseNames.iterator(); jter.hasNext();) {
              Object item = jter.next();
              if ((item instanceof String)) {
                String className = (String)item;
                try {
                  if (!className.startsWith("edu.cmu.cs.stage3.alice.core.response.PropertyAnimation")) {
                    Class responseClass = Class.forName(className);
                    LinkedList known = new LinkedList();
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
                    edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype responsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype(responseClass, knownPropertyValues, desiredProperties);
                    javax.swing.JComponent gui = GUIFactory.getGUI(responsePrototype);
                    if (gui != null) {
                      GridBagConstraints constraints = new GridBagConstraints(0, i, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0);
                      subPanel.add(gui, constraints);
                      i++;
                    } else {
                      AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_responsePrototype__") + responsePrototype, null);
                    }
                  }
                } catch (ClassNotFoundException e) {
                  AuthoringTool.showErrorDialog(Messages.getString("Error_while_looking_for_class_") + className, e);
                }
              }
            }
          }
          
          animationsPanel.add(toAdd, this.constraints);
          constraintsgridy += 1;
        }
      }
      glueConstraints.gridy = constraintsgridy;
      animationsPanel.add(javax.swing.Box.createGlue(), glueConstraints);
      


      constraintsgridy = 0;
      if (questions != null) {
        JPanel subPanel = new JPanel();
        subPanel.setBackground(Color.white);
        subPanel.setLayout(new GridBagLayout());
        subPanel.setBorder(spacingBorder);
        panelsToClean.add(subPanel);
        
        int count = 0;
        Object[] questionsArray = questions.getArrayValue();
        for (int i = 0; i < questionsArray.length; i++) {
          Class questionClass = edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class;
          Question question = (Question)questionsArray[i];
          
          if ((question instanceof UserDefinedQuestion)) {
            edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype((UserDefinedQuestion)question);
            javax.swing.JComponent gui = GUIFactory.getGUI(callToUserDefinedQuestionPrototype);
            if (gui != null) {
              EditObjectButton editButton = GUIFactory.getEditObjectButton(question, gui);
              editButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Open_this_question_for_editing_") + "</font></html>");
              JPanel guiPanel = new JPanel();
              panelsToClean.add(guiPanel);
              guiPanel.setBackground(Color.white);
              guiPanel.setLayout(new GridBagLayout());
              guiPanel.add(gui, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 16, 0, new Insets(0, 0, 0, 0), 0, 0));
              guiPanel.add(editButton, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 16, 0, new Insets(0, 4, 0, 0), 0, 0));
              GridBagConstraints constraints = new GridBagConstraints(0, count, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0);
              subPanel.add(guiPanel, constraints);
              count++;
              if (newlyCreatedQuestion == question) {
                authoringTool.editObject(newlyCreatedQuestion);
                newlyCreatedQuestion = null;
              }
            } else {
              AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_callToUserDefinedQuestionPrototype__") + callToUserDefinedQuestionPrototype, null);
            }
          } else {
            throw new RuntimeException(Messages.getString("ERROR__question_is_not_a_userDefinedQuestion"));
          }
        }
        
        GridBagConstraints c = new GridBagConstraints(0, count, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(4, 2, 2, 2), 0, 0);
        subPanel.add(newQuestionButton, c);
        
        questionsPanel.add(subPanel, this.constraints);
        constraintsgridy += 1;
      }
      

      Vector questionStructure = AuthoringToolResources.getQuestionStructure(element.getClass());
      if (questionStructure != null) {
        for (Iterator iter = questionStructure.iterator(); iter.hasNext();) {
          StringObjectPair sop = (StringObjectPair)iter.next();
          String groupName = sop.getString();
          Vector questionNames = (Vector)sop.getObject();
          
          JPanel subPanel = new JPanel();
          subPanel.setBackground(Color.white);
          subPanel.setLayout(new GridBagLayout());
          panelsToClean.add(subPanel);
          
          ExpandablePanel expandPanel = new ExpandablePanel();
          expandPanel.setTitle(Messages.getString(groupName.replace(" ", "_")));
          expandPanel.setContent(subPanel);
          
          if (questionNames != null) {
            int i = 0;
            for (Iterator jter = questionNames.iterator(); jter.hasNext();) {
              String className = (String)jter.next();
              try {
                Class questionClass = Class.forName(className);
                Question tempQuestion = (Question)questionClass.newInstance();
                LinkedList known = new LinkedList();
                String format = AuthoringToolResources.getFormat(questionClass);
                FormatTokenizer tokenizer = new FormatTokenizer(format);
                while (tokenizer.hasMoreTokens()) {
                  String token = tokenizer.nextToken();
                  if ((token.startsWith("<<<")) && (token.endsWith(">>>"))) {
                    String propertyName = token.substring(token.lastIndexOf("<") + 1, token.indexOf(">"));
                    known.add(new StringObjectPair(propertyName, element));
                  }
                }
                if (edu.cmu.cs.stage3.alice.core.question.PartKeyed.class.isAssignableFrom(questionClass)) {
                  known.add(new StringObjectPair("key", ""));
                }
                StringObjectPair[] knownPropertyValues = (StringObjectPair[])known.toArray(new StringObjectPair[0]);
                
                String[] desiredProperties = AuthoringToolResources.getDesiredProperties(questionClass);
                if (edu.cmu.cs.stage3.alice.core.question.PartKeyed.class.isAssignableFrom(questionClass)) {
                  desiredProperties = new String[0];
                }
                ElementPrototype elementPrototype = new ElementPrototype(questionClass, knownPropertyValues, desiredProperties);
                javax.swing.JComponent gui = GUIFactory.getGUI(elementPrototype);
                if (gui != null) {
                  GridBagConstraints constraints = new GridBagConstraints(0, i, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0);
                  subPanel.add(gui, constraints);
                  i++;
                } else {
                  AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_elementPrototype__") + elementPrototype, null);
                }
              } catch (ClassNotFoundException e) {
                AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_class__") + className, e);
              } catch (IllegalAccessException e) {
                AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_class__") + className, e);
              } catch (InstantiationException e) {
                AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_class__") + className, e);
              }
            }
          }
          
          questionsPanel.add(expandPanel, this.constraints);
          constraintsgridy += 1;
        }
      }
      glueConstraints.gridy = constraintsgridy;
      questionsPanel.add(javax.swing.Box.createGlue(), glueConstraints);
    }
    

















    revalidate();
    repaint(); }
  
  public class ResponsesListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener { public ResponsesListener() {}
    
    public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
    
    public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) { if (ev.getChangeType() == 1) {
        newlyCreatedAnimation = ((UserDefinedResponse)ev.getItem());
      }
      int selectedIndex = tabbedPane.getSelectedIndex();
      refreshGUI();
      tabbedPane.setSelectedIndex(selectedIndex);
    } }
  
  public class QuestionsListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener { public QuestionsListener() {}
    
    public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
    
    public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) { if (ev.getChangeType() == 1) {
        newlyCreatedQuestion = ((UserDefinedQuestion)ev.getItem());
      }
      int selectedIndex = tabbedPane.getSelectedIndex();
      refreshGUI();
      tabbedPane.setSelectedIndex(selectedIndex);
    } }
  
  public class PosesListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener { public PosesListener() {}
    
    public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
    
    public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) { if (ev.getChangeType() == 1) {
        newlyCreatedPose = ((Pose)ev.getItem());
      }
      int selectedIndex = tabbedPane.getSelectedIndex();
      refreshGUI();
      tabbedPane.setSelectedIndex(selectedIndex);
    }
  }
  




  BorderLayout borderLayout1 = new BorderLayout();
  JPanel propertySubPanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  
  Border border1;
  Border border2;
  Border border3;
  Border border4;
  Border border5;
  Border border6;
  Border border7;
  JPanel comboPanel = new JPanel();
  JLabel ownerLabel = new JLabel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JTabbedPane tabbedPane = new JTabbedPane();
  JScrollPane propertiesScrollPane = new JScrollPane();
  JScrollPane animationsScrollPane = new JScrollPane();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JPanel propertiesPanel = new JPanel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JPanel animationsPanel = new JPanel();
  JScrollPane questionsScrollPane = new JScrollPane();
  JPanel questionsPanel = new JPanel();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  JScrollPane otherScrollPane = new JScrollPane();
  JPanel otherPanel = new JPanel();
  GridBagLayout gridBagLayout5 = new GridBagLayout();
  Border border8;
  Border border9;
  
  private void jbInit() {
    border1 = BorderFactory.createEmptyBorder(2, 0, 0, 0);
    border2 = BorderFactory.createLineBorder(java.awt.SystemColor.controlText, 1);
    border3 = BorderFactory.createCompoundBorder(border2, border1);
    border4 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
    border5 = BorderFactory.createEmptyBorder(2, 2, 2, 2);
    border6 = BorderFactory.createLineBorder(java.awt.SystemColor.controlText, 1);
    border7 = BorderFactory.createCompoundBorder(border6, border5);
    border8 = BorderFactory.createEmptyBorder();
    border9 = BorderFactory.createLineBorder(Color.black, 1);
    setLayout(borderLayout1);
    propertySubPanel.setLayout(borderLayout2);
    propertySubPanel.setBorder(border1);
    propertySubPanel.setMinimumSize(new java.awt.Dimension(0, 0));
    propertySubPanel.setOpaque(false);
    setBackground(new Color(204, 204, 204));
    setMinimumSize(new java.awt.Dimension(0, 0));
    borderLayout2.setHgap(8);
    borderLayout2.setVgap(6);
    comboPanel.setLayout(gridBagLayout1);
    ownerLabel.setForeground(Color.black);
    ownerLabel.setText(Messages.getString("owner_s_details"));
    propertiesPanel.setBackground(Color.white);
    propertiesPanel.setLayout(gridBagLayout2);
    animationsPanel.setBackground(Color.white);
    animationsPanel.setLayout(gridBagLayout3);
    questionsPanel.setBackground(Color.white);
    questionsPanel.setLayout(gridBagLayout4);
    otherPanel.setBackground(Color.white);
    otherPanel.setLayout(gridBagLayout5);
    propertiesScrollPane.getViewport().setBackground(Color.white);
    propertiesScrollPane.setBorder(null);
    animationsScrollPane.getViewport().setBackground(Color.white);
    animationsScrollPane.setBorder(null);
    questionsScrollPane.getViewport().setBackground(Color.white);
    questionsScrollPane.setBorder(null);
    otherScrollPane.getViewport().setBackground(Color.white);
    otherScrollPane.setBorder(null);
    comboPanel.setOpaque(false);
    add(propertySubPanel, "Center");
    propertySubPanel.add(comboPanel, "North");
    comboPanel.add(ownerLabel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 
      17, 2, new Insets(0, 8, 0, 0), 0, 0));
    propertySubPanel.add(tabbedPane, "Center");
    tabbedPane.add(propertiesScrollPane, Messages.getString("properties"));
    propertiesScrollPane.getViewport().add(propertiesPanel, null);
    tabbedPane.add(animationsScrollPane, Messages.getString("methods"));
    tabbedPane.add(questionsScrollPane, AuthoringToolResources.QUESTION_STRING);
    
    otherScrollPane.getViewport().add(otherPanel, null);
    questionsScrollPane.getViewport().add(questionsPanel, null);
    animationsScrollPane.getViewport().add(animationsPanel, null);
  }
}
