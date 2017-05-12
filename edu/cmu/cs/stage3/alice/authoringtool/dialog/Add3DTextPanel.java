package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.util.FontChooser;
import edu.cmu.cs.stage3.alice.core.Text3D;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.FontProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;
import javax.swing.JTextArea;



















public class Add3DTextPanel
  extends AbstractFontPanel
{
  public Add3DTextPanel()
  {
    super("The quick brown fox", true);
  }
  
  public Text3D createText3D() {
    Text3D text3D = new Text3D();
    text.set(textArea.getText().trim());
    font.set(fontChooser.getChosenFont());
    isFirstClass.set(Boolean.TRUE);
    String name = textArea.getText().trim();
    name = name.replace('\\', '_');
    name = name.replace('/', '_');
    name = name.replace(':', '_');
    name = name.replace('*', '_');
    name = name.replace('?', '_');
    name = name.replace('"', '_');
    name = name.replace('<', '_');
    name = name.replace('>', '_');
    name = name.replace('|', '_');
    name = name.replace('.', '_');
    name = name.replace('\n', '_');
    name = name.replace('\t', '_');
    
    if (name.length() > 20) {
      name = name.substring(0, 21).trim();
    }
    if (name.compareTo(" ") == 0) {
      name.set(name);
    } else {
      name.set(Messages.getString("3D_Text"));
    }
    text3D.create3DTextGeometry();
    return text3D;
  }
}
