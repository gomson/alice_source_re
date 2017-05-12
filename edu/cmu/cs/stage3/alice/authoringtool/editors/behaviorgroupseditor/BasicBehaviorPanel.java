package edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel.DnDGrip;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public abstract class BasicBehaviorPanel extends DnDGroupingPanel implements GUIElement, edu.cmu.cs.stage3.alice.core.event.PropertyListener
{
  public static final Color COLOR = AuthoringToolResources.getColor("behavior");
  
  protected JPopupMenu popUpMenu;
  
  protected Behavior m_behavior = null;
  
  protected GroupingPanel m_containingPanel;
  protected GroupingPanel labelPanel;
  protected AuthoringTool authoringTool;
  protected String typeString;
  protected final java.awt.event.MouseListener behaviorMouseListener = new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter()
  {
    protected void popupResponse(MouseEvent e) {
      popUpMenu.show(e.getComponent(), e.getX(), e.getY());
      PopupMenuUtilities.ensurePopupIsOnScreen(popUpMenu);
    }
  };
  
  public BasicBehaviorPanel() {
    addMouseListener(behaviorMouseListener);
    grip.addMouseListener(behaviorMouseListener);
  }
  
  public void set(Behavior behavior, String type, AuthoringTool authoringTool) {
    clean();
    super.reset();
    this.authoringTool = authoringTool;
    m_behavior = behavior;
    setTransferable(new ElementReferenceTransferable(behavior));
    typeString = AuthoringToolResources.getReprForValue(m_behavior.getClass());
    m_behavior.isEnabled.addPropertyListener(this);
    popUpMenu = createPopup();
    guiInit();
  }
  
  public void set(Behavior behavior, AuthoringTool authoringTool) {
    clean();
    super.reset();
    this.authoringTool = authoringTool;
    m_behavior = behavior;
    setTransferable(new ElementReferenceTransferable(behavior));
    typeString = AuthoringToolResources.getReprForValue(m_behavior.getClass());
    m_behavior.isEnabled.addPropertyListener(this);
    popUpMenu = createPopup();
    guiInit();
  }
  
  protected String getHTMLColorString(Color color) {
    String r = Integer.toHexString(color.getRed());
    String g = Integer.toHexString(color.getGreen());
    String b = Integer.toHexString(color.getBlue());
    
    if (r.length() == 1) {
      r = "0" + r;
    }
    if (g.length() == 1) {
      g = "0" + g;
    }
    if (b.length() == 1) {
      b = "0" + b;
    }
    return new String("#" + r + g + b);
  }
  
  public void getHTML(StringBuffer toWriteTo, boolean useColor) {
    Color bgColor = COLOR;
    String strikeStart = "";
    String strikeEnd = "";
    if (!m_behavior.isEnabled.booleanValue()) {
      bgColor = AuthoringToolResources.getColor("disabledHTML");
      strikeStart = "<strike><font color=\"" + getHTMLColorString(AuthoringToolResources.getColor("disabledHTMLText")) + "\">";
      strikeEnd = "</font></strike>";
    }
    toWriteTo.append("<tr>\n<td bgcolor=" + getHTMLColorString(bgColor) + "><b>" + strikeStart + 
      GUIFactory.getHTMLStringForComponent(this) + "</b>" + strikeEnd + "</td>\n</tr>\n");
  }
  
  public Behavior getBehavior() {
    return m_behavior;
  }
  
  protected JPopupMenu createPopup() {
    Vector popupStructure = new Vector();
    
    popupStructure.add(ElementPopupUtilities.DeleteRunnable.class);
    Vector coerceStructure = ElementPopupUtilities.makeCoerceToStructure(m_behavior);
    if ((coerceStructure != null) && (coerceStructure.size() > 0)) {
      popupStructure.add(coerceStructure.elementAt(0));
    }
    StringObjectPair commentOut = null;
    if (m_behavior.isEnabled.booleanValue()) {
      Runnable setEnabled = new Runnable() {
        public void run() {
          m_behavior.isEnabled.set(false);
          repaint();
        }
      };
      commentOut = new StringObjectPair(Messages.getString("disable"), setEnabled);
    }
    else {
      Runnable setEnabled = new Runnable() {
        public void run() {
          m_behavior.isEnabled.set(true);
          repaint();
        }
      };
      commentOut = new StringObjectPair(Messages.getString("enable"), setEnabled);
    }
    popupStructure.add(commentOut);
    return ElementPopupUtilities.makeElementPopupMenu(m_behavior, popupStructure);
  }
  
  public void goToSleep() {
    if (m_behavior != null) {
      m_behavior.isEnabled.removePropertyListener(this);
    }
  }
  
  public void wakeUp() {
    if (m_behavior != null) {
      m_behavior.isEnabled.addPropertyListener(this);
    }
  }
  
  public void release() {
    super.release();
    releasePanel(m_containingPanel);
    GUIFactory.releaseGUI(this);
  }
  
  public void clean() {
    goToSleep();
    popUpMenu = null;
    if (m_containingPanel != null) {
      releasePanel(m_containingPanel);
      m_containingPanel.removeAll();
    }
  }
  
  protected void removeAllListening() {
    setTransferable(null);
    removeMouseListener(behaviorMouseListener);
    grip.removeMouseListener(behaviorMouseListener);
  }
  
  public void die() {
    clean();
    removeAllListening();
    removeAll();
    m_behavior = null;
    m_containingPanel = null;
    authoringTool = null;
  }
  
  protected void releasePanel(Container toRelease) {
    if (toRelease != null) {
      for (int i = 0; i < toRelease.getComponentCount(); i++) {
        if ((toRelease.getComponent(i) instanceof Releasable)) {
          ((Releasable)toRelease.getComponent(i)).release();
        }
      }
    }
  }
  
  public static void buildLabel(JPanel container, String typeString)
  {
    int oldLocation = 0;
    int insertX = 0;
    
    int locationLeft = typeString.indexOf('<', 0);
    int locationRight = typeString.indexOf('>', locationLeft);
    while ((locationLeft > -1) && (locationRight > -1) && (oldLocation < typeString.length())) {
      String currentSubstring = typeString.substring(oldLocation, locationLeft);
      String key = typeString.substring(locationLeft + 1, locationRight);
      
      Icon icon = AuthoringToolResources.getIconForValue(key);
      Component toAdd; if (icon == null) {
        Component toAdd = new GroupingPanel();
        ((GroupingPanel)toAdd).setLayout(new java.awt.BorderLayout(0, 0));
        ((GroupingPanel)toAdd).setBackground(COLOR);
        ((GroupingPanel)toAdd).setBorder(BorderFactory.createCompoundBorder(((GroupingPanel)toAdd).getBorder(), BorderFactory.createEmptyBorder(0, 2, 0, 0)));
        JLabel expressionLabel = new JLabel(key);
        ((GroupingPanel)toAdd).add(expressionLabel, "Center");
      }
      else {
        toAdd = new JLabel(icon);
      }
      container.add(new JLabel(currentSubstring), new GridBagConstraints(insertX, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 0), 0, 0));
      insertX++;
      container.add(toAdd, new GridBagConstraints(insertX, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 0), 0, 0));
      insertX++;
      oldLocation = locationRight + 1;
      locationLeft = typeString.indexOf('<', oldLocation);
      locationRight = typeString.indexOf('>', locationLeft);
    }
    if (oldLocation < typeString.length()) {
      String currentSubstring = typeString.substring(oldLocation, typeString.length());
      container.add(new JLabel(currentSubstring), new GridBagConstraints(insertX, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 0), 0, 0));
      insertX++;
    }
  }
  
  protected void buildLabel(JPanel container)
  {
    int oldLocation = 0;
    int insertX = 0;
    
    int locationLeft = typeString.indexOf('<', 0);
    int locationRight = typeString.indexOf('>', locationLeft);
    while ((locationLeft > -1) && (locationRight > -1) && (oldLocation < typeString.length())) {
      String currentSubstring = typeString.substring(oldLocation, locationLeft);
      String key = typeString.substring(locationLeft + 1, locationRight);
      
      Property prop = m_behavior.getPropertyNamed(key);
      Component toAdd; Component toAdd; if (prop != null) {
        PopupItemFactory propPIF = new edu.cmu.cs.stage3.alice.authoringtool.util.SetPropertyImmediatelyFactory(prop);
        boolean shouldAllowExpressions = true;
        Class desiredValueClass = PopupMenuUtilities.getDesiredValueClass(prop);
        
        if ((edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom(desiredValueClass)) || (prop.getName().equalsIgnoreCase("keyCode")) || (prop.getName().equalsIgnoreCase("onWhat"))) {
          shouldAllowExpressions = false;
        }
        toAdd = GUIFactory.getPropertyViewController(prop, true, shouldAllowExpressions, AuthoringToolResources.shouldGUIOmitPropertyName(prop), propPIF);
      }
      else {
        toAdd = new JLabel(AuthoringToolResources.getIconForValue(key));
        if (toAdd == null) {
          toAdd = new JLabel(Messages.getString("_no_image_"));
        }
      }
      container.add(new JLabel(currentSubstring), new GridBagConstraints(insertX, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 0), 0, 0));
      insertX++;
      container.add(toAdd, new GridBagConstraints(insertX, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 0), 0, 0));
      insertX++;
      oldLocation = locationRight + 1;
      locationLeft = typeString.indexOf('<', oldLocation);
      locationRight = typeString.indexOf('>', locationLeft);
    }
    if (oldLocation < typeString.length()) {
      String currentSubstring = typeString.substring(oldLocation, typeString.length());
      container.add(new JLabel(currentSubstring), new GridBagConstraints(insertX, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 2, 0, 0), 0, 0));
      insertX++;
    }
  }
  

  public void prePropertyChange(PropertyEvent propertyEvent) {}
  
  public void propertyChanging(PropertyEvent propertyEvent) {}
  
  public void paintForeground(Graphics g)
  {
    super.paintForeground(g);
    if (!m_behavior.isEnabled.booleanValue()) {
      Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
      edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects.paintDisabledEffect(g, bounds);
    }
  }
  
  public void propertyChanged(PropertyEvent propertyEvent)
  {
    popUpMenu = createPopup();
    guiInit();
  }
  
  protected void guiInit() {}
}
