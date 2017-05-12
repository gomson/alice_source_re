package edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CommonMathQuestionsTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach;
import edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse;
import edu.cmu.cs.stage3.alice.core.question.userdefined.LoopN;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Print;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Return;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.question.userdefined.While;
import edu.cmu.cs.stage3.alice.core.response.ForEachInOrder;
import edu.cmu.cs.stage3.alice.core.response.IfElseInOrder;
import edu.cmu.cs.stage3.alice.core.response.LoopNInOrder;
import edu.cmu.cs.stage3.alice.core.response.ScriptResponse;
import edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;



public class QuestionEditor
  extends CompositeElementEditor
{
  public final String editorName = AuthoringToolResources.QUESTION_STRING + " Editor";
  
  protected DnDGroupingPanel ifElsePrototype;
  
  protected DnDGroupingPanel loopNPrototype;
  protected DnDGroupingPanel forEachPrototype;
  protected DnDGroupingPanel scriptPrototype;
  protected DnDGroupingPanel whilePrototype;
  protected DnDGroupingPanel commentPrototype;
  protected JComponent printPrototype;
  protected DnDGroupingPanel returnPrototype;
  protected DnDGroupingPanel mathPrototype;
  public static final DataFlavor componentReferenceFlavor = AuthoringToolResources.getReferenceFlavorForClass(edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class);
  

  public QuestionEditor() {}
  
  public void setObject(UserDefinedQuestion toEdit)
  {
    clearAllListening();
    elementBeingEdited = toEdit;
    updateGui();
  }
  
  protected Color getEditorColor() {
    return AuthoringToolResources.getColor("userDefinedQuestionEditor");
  }
  
  protected MainCompositeElementPanel createElementTree(Element selected) {
    if ((selected instanceof UserDefinedQuestion)) {
      MainCompositeQuestionPanel toReturn = new MainCompositeQuestionPanel();
      toReturn.set(selected, authoringTool);
      return toReturn;
    }
    return null;
  }
  

  protected void initPrototypes()
  {
    String ifElseString = AuthoringToolResources.getReprForValue(IfElseInOrder.class);
    String loopNString = AuthoringToolResources.getReprForValue(LoopNInOrder.class);
    String forEachString = AuthoringToolResources.getReprForValue(ForEachInOrder.class);
    String scriptString = AuthoringToolResources.getReprForValue(ScriptResponse.class);
    String whileString = AuthoringToolResources.getReprForValue(WhileLoopInOrder.class);
    String commentString = AuthoringToolResources.getReprForValue(edu.cmu.cs.stage3.alice.core.response.Comment.class);
    String returnString = AuthoringToolResources.getReprForValue(Return.class);
    String mathString = AuthoringToolResources.getReprForValue("+ - * /");
    

    Color LOOP_N_COLOR = AuthoringToolResources.getColor("LoopNInOrder");
    Color IF_ELSE_COLOR = AuthoringToolResources.getColor("IfElseInOrder");
    Color FOR_EACH_COLOR = AuthoringToolResources.getColor("ForEachInOrder");
    Color SCRIPT_COLOR = AuthoringToolResources.getColor("ScriptResponse");
    Color COMMENT_COLOR = AuthoringToolResources.getColor("Comment");
    Color COMMENT_FOREGROUND = AuthoringToolResources.getColor("commentForeground");
    Color WHILE_COLOR = AuthoringToolResources.getColor("WhileLoopInOrder");
    Color MATH_COLOR = AuthoringToolResources.getColor("question");
    Color RETURN_COLOR = AuthoringToolResources.getColor("Return");
    Color PRINT_COLOR = AuthoringToolResources.getColor("Print");
    
    ifElsePrototype = new DnDGroupingPanel();
    
    ifElsePrototype.setBackground(IF_ELSE_COLOR);
    JLabel DITLabel = new JLabel(ifElseString);
    ifElsePrototype.add(DITLabel, "Center");
    String[] DITdesired = { "condition" };
    ifElsePrototype.setTransferable(
      new ElementPrototypeReferenceTransferable(
      new ElementPrototype(
      IfElse.class, null, DITdesired)));
    ifElsePrototype.addDragSourceComponent(DITLabel);
    
    String[] CLdesired = { "end" };
    loopNPrototype = new DnDGroupingPanel();
    
    loopNPrototype.setBackground(LOOP_N_COLOR);
    JLabel LLabel = new JLabel(loopNString);
    loopNPrototype.add(LLabel, "Center");
    loopNPrototype.setTransferable(
      new ElementPrototypeReferenceTransferable(
      new ElementPrototype(
      LoopN.class, null, CLdesired)));
    loopNPrototype.addDragSourceComponent(LLabel);
    
    String[] LITdesired = { "condition" };
    whilePrototype = new DnDGroupingPanel();
    
    whilePrototype.setBackground(WHILE_COLOR);
    JLabel LITLabel = new JLabel(whileString);
    whilePrototype.add(LITLabel, "Center");
    whilePrototype.setTransferable(
      new ElementPrototypeReferenceTransferable(
      new ElementPrototype(
      While.class, null, LITdesired)));
    whilePrototype.addDragSourceComponent(LITLabel);
    
    String[] SLdesired = { "list" };
    forEachPrototype = new DnDGroupingPanel();
    
    forEachPrototype.setBackground(FOR_EACH_COLOR);
    JLabel SLLabel = new JLabel(forEachString);
    forEachPrototype.add(SLLabel, "Center");
    forEachPrototype.setTransferable(
      new ElementPrototypeReferenceTransferable(
      new ElementPrototype(
      ForEach.class, null, SLdesired)));
    forEachPrototype.addDragSourceComponent(SLLabel);
    

    StringObjectPair[] Cknown = { new StringObjectPair("text", Messages.getString("No_comment")) };
    commentPrototype = new DnDGroupingPanel();
    commentPrototype.setBackground(COMMENT_COLOR);
    JLabel commentLabel = new JLabel(commentString);
    commentLabel.setForeground(COMMENT_FOREGROUND);
    commentPrototype.add(commentLabel, "Center");
    commentPrototype.setTransferable(
      new ElementPrototypeReferenceTransferable(
      new ElementPrototype(
      edu.cmu.cs.stage3.alice.core.question.userdefined.Comment.class, Cknown, null)));
    commentPrototype.addDragSourceComponent(commentLabel);
    
    printPrototype = GUIFactory.getGUI(
      new ElementPrototype(
      Print.class, null, null));
    printPrototype.setBackground(PRINT_COLOR);
    
    returnPrototype = new DnDGroupingPanel();
    returnPrototype.setBackground(RETURN_COLOR);
    JLabel returnLabel = new JLabel(returnString);
    returnPrototype.add(returnLabel, "Center");
    returnPrototype.setTransferable(
      new ElementPrototypeReferenceTransferable(
      new ElementPrototype(
      Return.class, null, null)));
    returnPrototype.addDragSourceComponent(returnLabel);
    
    mathPrototype = new DnDGroupingPanel();
    mathPrototype.setBackground(MATH_COLOR);
    JLabel mathLabel = new JLabel(mathString);
    int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
    mathLabel.setFont(new Font("Monospaced", 1, (int)(12 * fontSize / 12.0D)));
    mathPrototype.add(mathLabel, "Center");
    mathPrototype.setTransferable(new CommonMathQuestionsTransferable());
    mathPrototype.addDragSourceComponent(mathLabel);
  }
  

  protected void addPrototypes(Container prototypeContainer)
  {
    prototypeContainer.add(ifElsePrototype);
    prototypeContainer.add(loopNPrototype);
    prototypeContainer.add(whilePrototype);
    prototypeContainer.add(forEachPrototype);
    prototypeContainer.add(printPrototype);
    prototypeContainer.add(commentPrototype);
    prototypeContainer.add(returnPrototype);
    
    java.awt.Component buttonGlue = Box.createHorizontalGlue();
    prototypeContainer.add(buttonGlue);
  }
}
