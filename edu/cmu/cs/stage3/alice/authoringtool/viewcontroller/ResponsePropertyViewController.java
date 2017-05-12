package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor;
import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;


public class ResponsePropertyViewController
  extends PropertyViewController
{
  protected JLabel responseLabel = new JLabel();
  protected GroupingPanel responsePanel = new GroupingPanel();
  
  protected Element root = null;
  
  public ResponsePropertyViewController() {
    responseLabel.setOpaque(false);
    responsePanel.setOpaque(false);
    responsePanel.setLayout(new BorderLayout());
    responsePanel.setBorder(null);
  }
  
  public void set(Property property, boolean allowExpressions, boolean omitPropertyName, PopupItemFactory factory) {
    super.set(property, true, allowExpressions, false, omitPropertyName, factory);
    setPopupEnabled(true);
    refreshGUI();
  }
  
  protected MouseListener getMouseListener() {
    new MouseAdapter() {
      public void mousePressed(MouseEvent ev) {
        popupButton.doClick();
      }
    };
  }
  
  public void setRoot(Element root) {
    this.root = root;
  }
  
  protected String getHTMLColorString(Color color) {
    int r = color.getRed();
    int g = color.getGreen();
    int b = color.getBlue();
    return new String("#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b));
  }
  
  public void getHTML(StringBuffer toWriteTo) {
    boolean isEnabled = false;
    if ((property.getOwner() instanceof Behavior)) {
      isEnabled = property.getOwner()).isEnabled.booleanValue();
    }
    String strikeStart = "";
    String strikeEnd = "";
    if (!isEnabled) {
      strikeStart = "<strike><font color=\"" + getHTMLColorString(AuthoringToolResources.getColor("disabledHTMLText")) + "\">";
      strikeEnd = "</font></strike>";
    }
    
    if (responsePanel.getComponentCount() > 0) {
      if (((responsePanel.getComponent(0) instanceof CompositeElementPanel)) && 
        ((property.get() instanceof CompositeResponse))) {
        CompositeElementPanel compPanel = (CompositeElementPanel)responsePanel.getComponent(0);
        int colSpan = CompositeElementEditor.getDepthCount(property.get()).componentResponses);
        compPanel.getHTML(toWriteTo, colSpan + 1, true, !isEnabled);
      } else {
        Color bgColor = responsePanel.getComponent(0).getBackground();
        if (!isEnabled) {
          bgColor = AuthoringToolResources.getColor("disabledHTML");
        }
        toWriteTo.append("<tr>\n<td bgcolor=" + getHTMLColorString(bgColor) + " style=\"border-left: 1px solid #c0c0c0; border-top: 1px solid #c0c0c0; border-right: 1px solid #c0c0c0; border-bottom: 1px solid #c0c0c0;\">" + strikeStart);
        toWriteTo.append(GUIFactory.getHTMLStringForComponent(responsePanel.getComponent(0)));
        toWriteTo.append(strikeEnd + "</td>\n</tr>\n");
      }
    } else {
      toWriteTo.append(strikeStart);
      super.getHTML(toWriteTo);
      toWriteTo.append(strikeEnd);
    }
  }
  

  protected void updatePopupStructure()
  {
    popupStructure = PopupMenuUtilities.makePropertyStructure(property, factory, includeDefaults, allowExpressions, includeOther, root);
  }
  
  protected Component getNativeComponent() {
    return responsePanel;
  }
  
  protected Class getNativeClass() {
    return Response.class;
  }
  
  protected void updateNativeComponent() {
    responsePanel.removeAll();
    Response response = (Response)property.get();
    JComponent gui = GUIFactory.getGUI(response);
    responsePanel.add(gui, "Center");
  }
}
