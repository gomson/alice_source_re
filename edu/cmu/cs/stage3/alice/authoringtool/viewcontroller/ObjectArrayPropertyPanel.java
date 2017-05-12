package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;












public class ObjectArrayPropertyPanel
  extends ExpandablePanel
{
  protected ObjectArrayProperty objectArrayProperty;
  protected JPanel contentPanel = new JPanel();
  protected AuthoringTool authoringTool;
  protected RefreshListener refreshListener = new RefreshListener();
  
  public ObjectArrayPropertyPanel(String title, AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
    guiInit(title);
  }
  
  private void guiInit(String title) {
    setTitle(title);
    contentPanel.setLayout(new GridBagLayout());
    setContent(contentPanel);
    setOpaque(false);
    contentPanel.setOpaque(false);
  }
  
  public void setObjectArrayProperty(ObjectArrayProperty objectArrayProperty) {
    if (this.objectArrayProperty != null) {
      this.objectArrayProperty.removeObjectArrayPropertyListener(refreshListener);
    }
    
    this.objectArrayProperty = objectArrayProperty;
    
    if (objectArrayProperty != null) {
      objectArrayProperty.addObjectArrayPropertyListener(refreshListener);
    }
    
    refreshGUI();
  }
  
  public void refreshGUI() {
    contentPanel.removeAll();
    
    if (objectArrayProperty != null) {
      int count = 0;
      for (int i = 0; i < objectArrayProperty.size(); i++) {
        Object object = objectArrayProperty.get(i);
        JComponent gui = GUIFactory.getGUI(object);
        if (gui != null) {
          contentPanel.add(gui, new GridBagConstraints(0, count++, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 2), 0, 0));
        } else {
          AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_gui_for_object__") + object, null);
        }
      }
      
      Component glue = Box.createGlue();
      contentPanel.add(glue, new GridBagConstraints(0, count++, 1, 1, 1.0D, 1.0D, 17, 1, new Insets(2, 2, 2, 2), 0, 0));
    }
    revalidate();
    repaint(); }
  
  protected class RefreshListener implements ObjectArrayPropertyListener { protected RefreshListener() {}
    
    public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
    
    public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) { refreshGUI(); }
  }
}
