package edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Composite;
import edu.cmu.cs.stage3.lang.Messages;





























public abstract class CompositeQuestionPanel
  extends CompositeElementPanel
{
  protected Composite m_question;
  
  public CompositeQuestionPanel()
  {
    headerText = Messages.getString("Composite_Question");
  }
  
  public void set(Element question, AuthoringTool authoringToolIn) {
    m_question = ((Composite)m_element);
    super.set(question, authoringToolIn);
  }
  
  public Composite getQuestion() {
    return m_question;
  }
}
