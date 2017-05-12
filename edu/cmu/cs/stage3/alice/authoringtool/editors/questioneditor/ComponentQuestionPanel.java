package edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor;

import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.ComponentElementPanel;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Component;
import javax.swing.BorderFactory;



























public class ComponentQuestionPanel
  extends ComponentElementPanel
{
  protected Component m_question;
  
  public ComponentQuestionPanel()
  {
    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    remove(grip);
  }
  
  public void set(Element element) {
    if ((element instanceof Component)) {
      m_question = ((Component)element);
      super.set(element);
    }
    else {
      throw new IllegalArgumentException();
    }
  }
  
  public Component getQuestion() {
    return m_question;
  }
}
