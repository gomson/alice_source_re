package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


















public class CollectionEditorPanel
  extends JPanel
{
  private ObjectArrayPropertyEditor objectArrayPropertyEditor = new ObjectArrayPropertyEditor();
  
  public CollectionEditorPanel() {
    JScrollPane editorScrollPane = new JScrollPane();
    editorScrollPane.setViewportView(objectArrayPropertyEditor);
    
    setLayout(new BorderLayout());
    
    add(new JLabel(Messages.getString("Values_")), "North");
    add(editorScrollPane, "Center");
    
    setPreferredSize(new Dimension(350, 350));
  }
  
  public void setCollection(Collection collection) {
    objectArrayPropertyEditor.setType(valueClass.getClassValue());
    objectArrayPropertyEditor.setObjectArrayProperty(values);
  }
}
