package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.Editor;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.Composite;
import edu.cmu.cs.stage3.alice.core.question.userdefined.IfElse;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import edu.cmu.cs.stage3.alice.core.response.IfElseInOrder;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;





















public abstract class CompositeElementEditor
  extends GroupingPanel
  implements Editor, PropertyListener
{
  public CompositeElementEditor()
  {
    mainColor = Color.white;
    configInit();
    guiInit();
    addMouseListener(new MouseListener()
    {
      public void mouseClicked(MouseEvent e)
      {
        if ((e.getModifiers() & 0x1) > 0) {
          TempColorPicker tempColorPicker = new TempColorPicker(CompositeElementEditor.this);
          DialogManager.showMessageDialog(tempColorPicker);
        }
      }
      


      public void mousePressed(MouseEvent mouseevent) {}
      


      public void mouseEntered(MouseEvent mouseevent) {}
      


      public void mouseExited(MouseEvent mouseevent) {}
      


      public void mouseReleased(MouseEvent mouseevent) {}
    });
  }
  

  public MainCompositeElementPanel getMainPanel()
  {
    return compositeElementPanel;
  }
  
  public void setBackground(Color backgroundColor)
  {
    super.setBackground(backgroundColor);
    if (compositeElementPanel != null) {
      compositeElementPanel.setBackground(backgroundColor);
    }
  }
  
  private void configInit() {
    if (AuthoringToolResources.getMiscItem("javaLikeSyntax") != null) {
      IS_JAVA = AuthoringToolResources.getMiscItem("javaLikeSyntax").equals("true");
    }
  }
  
  public JComponent getJComponent() {
    return this;
  }
  
  public Object getObject()
  {
    return elementBeingEdited;
  }
  
  public void setAuthoringTool(AuthoringTool authoringTool)
  {
    this.authoringTool = authoringTool;
  }
  
  protected abstract MainCompositeElementPanel createElementTree(Element paramElement);
  
  protected abstract void initPrototypes();
  
  protected abstract void addPrototypes(Container paramContainer);
  
  protected abstract Color getEditorColor();
  
  protected void guiInit()
  {
    setBorder(null);
    removeAll();
    buttonPanel = new JPanel();
    Color buttonPanelColor = Color.white;
    buttonPanel.setOpaque(true);
    buttonPanel.setBackground(buttonPanelColor);
    buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));
    buttonPanel.setMinimumSize(new Dimension(0, 0));
    buttonPanel.setLayout(new FlowLayout(0)
    {
      public Dimension preferredLayoutSize(Container target)
      {
        Insets insets = target.getParent().getInsets();
        int hgap = getHgap();
        int vgap = getVgap();
        int maxwidth = target.getParent().getWidth() - (left + right + hgap * 2);
        int nmembers = target.getComponentCount();
        int x = 0;
        int y = top + vgap;
        int rowh = 0;
        int start = 0;
        boolean ltr = target.getComponentOrientation().isLeftToRight();
        for (int i = 0; i < nmembers; i++)
        {
          Component m = target.getComponent(i);
          if (m.isVisible())
          {
            Dimension d = m.getPreferredSize();
            m.setSize(width, height);
            if ((x == 0) || (x + width <= maxwidth))
            {
              if (x > 0)
                x += hgap;
              x += width;
              rowh = Math.max(rowh, height);
            }
            else {
              x = width;
              y += vgap + rowh;
              rowh = height;
              start = i;
            }
          }
        }
        return new Dimension(target.getParent().getWidth() - (left + right), y + rowh + vgap);
      }
      
      public Dimension minimumLayoutSize(Container target)
      {
        return preferredLayoutSize(target);
      }
      
    });
    initPrototypes();
    addPrototypes(buttonPanel);
    JScrollPane buttonPanelScrollPane = new JScrollPane(buttonPanel, 20, 30);
    mainElementContainer = new JPanel();
    mainElementContainer.setLayout(new BorderLayout());
    mainElementContainer.setMinimumSize(new Dimension(0, 0));
    mainElementContainer.setBorder(null);
    mainElementContainer.setOpaque(false);
    mainElementContainer.setBackground(getEditorColor());
    setLayout(new BorderLayout());
    setBackground(getEditorColor());
    add(buttonPanel, "South");
    add(mainElementContainer, "Center");
    updateGui();
  }
  
  public void updateGui()
  {
    mainElementContainer.removeAll();
    if ((elementBeingEdited != null) && (authoringTool != null))
    {
      clearAllListening();
      compositeElementPanel = createElementTree(elementBeingEdited);
      if (compositeElementPanel != null)
      {
        mainElementContainer.add(compositeElementPanel, "Center");
        setBackground(compositeElementPanel.getBackground());
      }
    }
    else {
      JLabel emptyLabel = new JLabel(Messages.getString("Not_an_editable_element"));
      emptyLabel.setFont(emptyLabel.getFont().deriveFont(2));
      JPanel emptyPanel = new JPanel();
      emptyPanel.setLayout(new GridBagLayout());
      emptyPanel.setBackground(getBackground());
      emptyPanel.add(emptyLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
      mainElementContainer.add(emptyPanel, "Center");
    }
    revalidate();
    repaint();
  }
  
  public static int getDepthCount(ObjectArrayProperty children) {
    int maxDepth = 0;
    for (int i = 0; i < children.size(); i++) {
      if ((children.get(i) instanceof CompositeResponse)) {
        CompositeResponse currentResponse = (CompositeResponse)children.get(i);
        int depth = getDepthCount(componentResponses);
        if (depth > maxDepth) {
          maxDepth = depth;
        }
        if ((currentResponse instanceof IfElseInOrder)) {
          IfElseInOrder ifResponse = (IfElseInOrder)currentResponse;
          depth = getDepthCount(elseComponentResponses);
          if (depth > maxDepth) {
            maxDepth = depth;
          }
        }
      } else if ((children.get(i) instanceof Composite)) {
        Composite currentQuestion = (Composite)children.get(i);
        int depth = getDepthCount(components);
        if (depth > maxDepth) {
          maxDepth = depth;
        }
        if ((currentQuestion instanceof IfElse)) {
          IfElse ifQuestion = (IfElse)currentQuestion;
          depth = getDepthCount(elseComponents);
          if (depth > maxDepth) {
            maxDepth = depth;
          }
        }
      }
    }
    return 1 + maxDepth;
  }
  
  public void getHTML(StringBuffer toWriteTo, boolean useColor) {
    if (compositeElementPanel != null) {
      int colSpan = getDepthCount(compositeElementPanel.m_components);
      compositeElementPanel.getHTML(toWriteTo, colSpan + 1, useColor, false);
    }
  }
  


  public void prePropertyChange(PropertyEvent propertyevent) {}
  


  public void propertyChanging(PropertyEvent propertyevent) {}
  

  public void propertyChanged(PropertyEvent propertyevent) {}
  

  protected void startListeningToTree(Element element)
  {
    if (element == null) {}
  }
  
  protected void stopListeningToTree(Element element)
  {
    if (element == null) {}
  }
  
  protected void clearAllListening()
  {
    if (compositeElementPanel != null) {
      compositeElementPanel.clean();
    }
  }
  


  public void stateChanging(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {}
  


  public void worldLoading(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {}
  


  public void worldUnLoading(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {}
  


  public void worldStarting(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {}
  


  public void worldStopping(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {}
  


  public void worldPausing(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {}
  

  public void worldSaving(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {}
  

  public void stateChanged(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {}
  

  public void worldLoaded(AuthoringToolStateChangedEvent ev)
  {
    updateGui();
  }
  





















  public final String editorName = Messages.getString("Composite_Editor");
  






  public static boolean IS_JAVA = false;
  


  protected static Configuration authoringToolConfig = Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());
  protected Element elementBeingEdited;
  protected MainCompositeElementPanel compositeElementPanel;
  protected JPanel mainElementContainer;
  protected JPanel buttonPanel;
  protected Color mainColor;
  protected AuthoringTool authoringTool;
  
  public void worldUnLoaded(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {}
  
  public void worldStarted(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {}
  
  public void worldStopped(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {}
  
  public void worldPaused(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {}
  
  public void worldSaved(AuthoringToolStateChangedEvent authoringtoolstatechangedevent) {}
}
