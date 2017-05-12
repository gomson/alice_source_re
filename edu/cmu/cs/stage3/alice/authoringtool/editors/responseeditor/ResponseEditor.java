package edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CommonMathQuestionsTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.response.Comment;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import edu.cmu.cs.stage3.alice.core.response.DoInOrder;
import edu.cmu.cs.stage3.alice.core.response.DoTogether;
import edu.cmu.cs.stage3.alice.core.response.ForEachInOrder;
import edu.cmu.cs.stage3.alice.core.response.ForEachTogether;
import edu.cmu.cs.stage3.alice.core.response.IfElseInOrder;
import edu.cmu.cs.stage3.alice.core.response.LoopNInOrder;
import edu.cmu.cs.stage3.alice.core.response.Print;
import edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.ScriptResponse;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.Wait;
import edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;

public class ResponseEditor extends CompositeElementEditor
{
  public final String editorName = Messages.getString("Response_Editor");
  
  protected DnDGroupingPanel doInOrderPrototype;
  
  protected DnDGroupingPanel doTogetherPrototype;
  
  protected DnDGroupingPanel doIfTruePrototype;
  protected DnDGroupingPanel loopPrototype;
  protected JComponent waitPrototype;
  protected DnDGroupingPanel sequentialLoopPrototype;
  protected DnDGroupingPanel forAllTogetherPrototype;
  protected DnDGroupingPanel scriptPrototype;
  protected DnDGroupingPanel scriptDefinedPrototype;
  protected DnDGroupingPanel loopIfTruePrototype;
  protected DnDGroupingPanel commentPrototype;
  protected JComponent printPrototype;
  protected DnDGroupingPanel mathPrototype;
  
  public ResponseEditor() {}
  
  public void setObject(UserDefinedResponse toEdit)
  {
    clearAllListening();
    elementBeingEdited = toEdit;
    updateGui();
  }
  
  protected Color getEditorColor() {
    return AuthoringToolResources.getColor("userDefinedResponseEditor");
  }
  
  protected MainCompositeElementPanel createElementTree(Element selected) {
    if ((selected instanceof CompositeResponse)) {
      MainCompositeResponsePanel toReturn = null;
      if ((selected instanceof DoInOrder)) {
        toReturn = new MainSequentialResponsePanel();
        toReturn.set(selected, authoringTool);
      }
      else if ((selected instanceof DoTogether)) {
        toReturn = new MainParallelResponsePanel();
        toReturn.set(selected, authoringTool);
      }
      return toReturn;
    }
    return null;
  }
  
  protected void initPrototypes()
  {
    String doInOrderString = AuthoringToolResources.getReprForValue(DoInOrder.class);
    String doTogetherString = AuthoringToolResources.getReprForValue(DoTogether.class);
    String doIfTrueString = AuthoringToolResources.getReprForValue(IfElseInOrder.class);
    String loopString = AuthoringToolResources.getReprForValue(LoopNInOrder.class);
    
    String sequentialLoopString = AuthoringToolResources.getReprForValue(ForEachInOrder.class);
    String forAllTogetherString = AuthoringToolResources.getReprForValue(ForEachTogether.class);
    String scriptString = AuthoringToolResources.getReprForValue(ScriptResponse.class);
    String scriptDefinedString = AuthoringToolResources.getReprForValue(ScriptDefinedResponse.class);
    String loopIfTrueString = AuthoringToolResources.getReprForValue(WhileLoopInOrder.class);
    String commentString = AuthoringToolResources.getReprForValue(Comment.class);
    String mathString = AuthoringToolResources.getReprForValue("+ - * /");
    
    Color DO_IN_ORDER_COLOR = AuthoringToolResources.getColor("DoInOrder");
    Color DO_TOGETHER_COLOR = AuthoringToolResources.getColor("DoTogether");
    Color COUNT_LOOP_COLOR = AuthoringToolResources.getColor("LoopNInOrder");
    Color DO_IF_COLOR = AuthoringToolResources.getColor("IfElseInOrder");
    
    Color SEQUENTIAL_LOOP_COLOR = AuthoringToolResources.getColor("ForEachInOrder");
    Color FOR_ALL_TOGETHER_COLOR = AuthoringToolResources.getColor("ForAllTogether");
    Color SCRIPT_COLOR = AuthoringToolResources.getColor("ScriptResponse");
    Color SCRIPT_DEFINED_COLOR = AuthoringToolResources.getColor("ScriptDefinedResponse");
    Color COMMENT_COLOR = AuthoringToolResources.getColor("Comment");
    Color COMMENT_FOREGROUND = AuthoringToolResources.getColor("commentForeground");
    Color DO_WHILE_COLOR = AuthoringToolResources.getColor("WhileLoopInOrder");
    
    Color MATH_COLOR = AuthoringToolResources.getColor("question");
    
    doInOrderPrototype = new DnDGroupingPanel();
    
    doInOrderPrototype.setBackground(DO_IN_ORDER_COLOR);
    JLabel DIOJLabel = new JLabel(doInOrderString);
    doInOrderPrototype.add(DIOJLabel, "Center");
    doInOrderPrototype.setTransferable(
      new ResponsePrototypeReferenceTransferable(
      new ResponsePrototype(
      DoInOrder.class, null, null)));
    doInOrderPrototype.addDragSourceComponent(DIOJLabel);
    
    doTogetherPrototype = new DnDGroupingPanel();
    
    doTogetherPrototype.setBackground(DO_TOGETHER_COLOR);
    JLabel DTJLabel = new JLabel(doTogetherString);
    doTogetherPrototype.add(DTJLabel, "Center");
    doTogetherPrototype.setTransferable(
      new ResponsePrototypeReferenceTransferable(
      new ResponsePrototype(
      DoTogether.class, null, null)));
    doTogetherPrototype.addDragSourceComponent(DTJLabel);
    
    doIfTruePrototype = new DnDGroupingPanel();
    
    doIfTruePrototype.setBackground(DO_IF_COLOR);
    JLabel DITLabel = new JLabel(doIfTrueString);
    doIfTruePrototype.add(DITLabel, "Center");
    String[] DITdesired = { "condition" };
    doIfTruePrototype.setTransferable(
      new ResponsePrototypeReferenceTransferable(
      new ResponsePrototype(
      IfElseInOrder.class, null, DITdesired)));
    doIfTruePrototype.addDragSourceComponent(DITLabel);
    
    String[] CLdesired = { "end" };
    loopPrototype = new DnDGroupingPanel();
    
    loopPrototype.setBackground(COUNT_LOOP_COLOR);
    JLabel LLabel = new JLabel(loopString);
    loopPrototype.add(LLabel, "Center");
    loopPrototype.setTransferable(
      new ResponsePrototypeReferenceTransferable(
      new ResponsePrototype(
      LoopNInOrder.class, null, CLdesired)));
    loopPrototype.addDragSourceComponent(LLabel);
    
    String[] LITdesired = { "condition" };
    loopIfTruePrototype = new DnDGroupingPanel();
    
    loopIfTruePrototype.setBackground(DO_WHILE_COLOR);
    JLabel LITLabel = new JLabel(loopIfTrueString);
    loopIfTruePrototype.add(LITLabel, "Center");
    loopIfTruePrototype.setTransferable(
      new ResponsePrototypeReferenceTransferable(
      new ResponsePrototype(
      WhileLoopInOrder.class, null, LITdesired)));
    loopIfTruePrototype.addDragSourceComponent(LITLabel);
    
    String[] SLdesired = { "list" };
    sequentialLoopPrototype = new DnDGroupingPanel();
    
    sequentialLoopPrototype.setBackground(SEQUENTIAL_LOOP_COLOR);
    JLabel SLLabel = new JLabel(sequentialLoopString);
    sequentialLoopPrototype.add(SLLabel, "Center");
    sequentialLoopPrototype.setTransferable(
      new ResponsePrototypeReferenceTransferable(
      new ResponsePrototype(
      ForEachInOrder.class, null, SLdesired)));
    sequentialLoopPrototype.addDragSourceComponent(SLLabel);
    
    String[] FATdesired = { "list" };
    forAllTogetherPrototype = new DnDGroupingPanel();
    
    forAllTogetherPrototype.setBackground(FOR_ALL_TOGETHER_COLOR);
    JLabel FATLabel = new JLabel(forAllTogetherString);
    forAllTogetherPrototype.add(FATLabel, "Center");
    forAllTogetherPrototype.setTransferable(
      new ResponsePrototypeReferenceTransferable(
      new ResponsePrototype(
      ForEachTogether.class, null, FATdesired)));
    forAllTogetherPrototype.addDragSourceComponent(FATLabel);
    
    String[] Wdesired = { "duration" };
    waitPrototype = GUIFactory.getGUI(
      new ResponsePrototype(
      Wait.class, null, Wdesired));
    

    scriptDefinedPrototype = new DnDGroupingPanel();
    scriptDefinedPrototype.setBackground(SCRIPT_DEFINED_COLOR);
    
    JLabel scriptDefinedLabel = new JLabel(scriptDefinedString);
    
    scriptDefinedPrototype.add(scriptDefinedLabel, "Center");
    scriptDefinedPrototype.setTransferable(
      new ResponsePrototypeReferenceTransferable(
      new ResponsePrototype(
      ScriptDefinedResponse.class, null, null)));
    scriptDefinedPrototype.addDragSourceComponent(scriptDefinedLabel);
    
    scriptPrototype = new DnDGroupingPanel();
    scriptPrototype.setBackground(SCRIPT_COLOR);
    JLabel scriptLabel = new JLabel(scriptString);
    scriptPrototype.add(scriptLabel, "Center");
    scriptPrototype.setTransferable(
      new ResponsePrototypeReferenceTransferable(
      new ResponsePrototype(
      ScriptResponse.class, null, null)));
    scriptPrototype.addDragSourceComponent(scriptLabel);
    
    StringObjectPair[] Cknown = { new StringObjectPair("text", Messages.getString("No_comment")) };
    commentPrototype = new DnDGroupingPanel();
    commentPrototype.setBackground(COMMENT_COLOR);
    JLabel commentLabel = new JLabel(commentString);
    commentLabel.setForeground(COMMENT_FOREGROUND);
    commentPrototype.add(commentLabel, "Center");
    commentPrototype.setTransferable(
      new ResponsePrototypeReferenceTransferable(
      new ResponsePrototype(
      Comment.class, Cknown, null)));
    commentPrototype.addDragSourceComponent(commentLabel);
    
    printPrototype = GUIFactory.getGUI(
      new ResponsePrototype(
      Print.class, null, null));
    
    mathPrototype = new DnDGroupingPanel();
    mathPrototype.setBackground(MATH_COLOR);
    JLabel mathLabel = new JLabel(mathString);
    int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
    mathLabel.setFont(new Font("Monospaced", 1, (int)(12.0D * (fontSize / 12.0D))));
    mathPrototype.add(mathLabel, "Center");
    mathPrototype.setTransferable(new CommonMathQuestionsTransferable());
    mathPrototype.addDragSourceComponent(mathLabel);
  }
  

  protected void addPrototypes(Container prototypeContainer)
  {
    prototypeContainer.add(doInOrderPrototype);
    prototypeContainer.add(doTogetherPrototype);
    prototypeContainer.add(doIfTruePrototype);
    prototypeContainer.add(loopPrototype);
    prototypeContainer.add(loopIfTruePrototype);
    prototypeContainer.add(sequentialLoopPrototype);
    prototypeContainer.add(forAllTogetherPrototype);
    
    prototypeContainer.add(Box.createHorizontalStrut(10));
    
    prototypeContainer.add(waitPrototype);
    if (authoringToolConfig.getValue("enableScripting").equalsIgnoreCase("true")) {
      prototypeContainer.add(scriptPrototype);
      prototypeContainer.add(scriptDefinedPrototype);
    }
    
    prototypeContainer.add(printPrototype);
    prototypeContainer.add(commentPrototype);
    
    Component buttonGlue = Box.createHorizontalGlue();
    prototypeContainer.add(buttonGlue);
  }
}
