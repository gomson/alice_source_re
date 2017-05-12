package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Element;























public class ElementEditorClassPair
{
  Element element;
  Class editorClass;
  
  public ElementEditorClassPair(Element element, Class editorClass)
  {
    this.element = element;
    this.editorClass = editorClass;
  }
  
  public Element getElement() {
    return element;
  }
  
  public Class getEditorClass() {
    return editorClass;
  }
}
