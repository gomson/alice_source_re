package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource.CompoundBorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders.MarginBorder;







public class EditObjectButton
  extends JButton
  implements GUIElement, Releasable
{
  protected AuthoringTool authoringTool;
  protected Object object;
  protected JComponent animationSource;
  private Configuration authoringToolConfig = Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());
  
  public EditObjectButton() {
    setBackground(new Color(240, 240, 255));
    setMargin(new Insets(0, 2, 0, 2));
    setText(Messages.getString("edit"));
    int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
    setFont(new Font("SansSerif", 1, (int)(11 * fontSize / 12.0D)));
    setFocusPainted(false);
    setBorder(new BorderUIResource.CompoundBorderUIResource(new CustomButtonBorder(), new BasicBorders.MarginBorder()));
    





    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        if (authoringTool != null) {
          if (object != null) {
            if (animationSource != null) {
              authoringTool.editObject(object, animationSource);
            } else {
              authoringTool.editObject(object);
            }
          } else {
            AuthoringTool.showErrorDialog(Messages.getString("object_unexpectedly_null_in_EditObjectButton"), null);
          }
        } else {
          AuthoringTool.showErrorDialog(Messages.getString("authoringTool_unexpectedly_null_in_EditObjectButton"), null);
        }
      }
    });
  }
  
  public void setAuthoringTool(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
  }
  
  public void setObject(Object object) {
    this.object = object;
  }
  
  public Object getObject() {
    return object;
  }
  

  public void setAnimationSource(JComponent animationSource) { this.animationSource = animationSource; }
  
  public void goToSleep() {}
  
  public void wakeUp() {}
  
  public void clean() {
    object = null;
    animationSource = null;
  }
  
  public void die() {
    clean();
    authoringTool = null;
  }
  
  public void release() {
    GUIFactory.releaseGUI(this);
  }
  
  class CustomButtonBorder extends AbstractBorder implements UIResource {
    protected Insets insets = new Insets(1, 3, 1, 3);
    protected Border line = BorderFactory.createLineBorder(Color.darkGray, 1);
    protected Border spacer = BorderFactory.createEmptyBorder(0, 3, 0, 3);
    protected Border raisedBevel = new EditObjectButton.CustomBevelBorder(EditObjectButton.this, 0);
    protected Border loweredBevel = new EditObjectButton.CustomBevelBorder(EditObjectButton.this, 1);
    protected Border raisedBorder = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(line, raisedBevel), spacer);
    protected Border loweredBorder = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(line, loweredBevel), spacer);
    
    CustomButtonBorder() {}
    
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
      JButton button = (JButton)c;
      ButtonModel model = button.getModel();
      
      if (model.isEnabled()) {
        if ((model.isPressed()) && (model.isArmed())) {
          loweredBorder.paintBorder(button, g, x, y, w, h);
        } else {
          raisedBorder.paintBorder(button, g, x, y, w, h);
        }
      } else {
        raisedBorder.paintBorder(button, g, x, y, w, h);
      }
    }
    
    public Insets getBorderInsets(Component c) {
      return insets;
    }
  }
  
  class CustomBevelBorder extends BevelBorder {
    public CustomBevelBorder(int type) {
      super();
    }
    
    public Color getHighlightInnerColor(Component c) {
      return c.getBackground();
    }
    
    public Color getShadowInnerColor(Component c) {
      return c.getBackground();
    }
  }
}
