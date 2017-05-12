package edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.CopyFactory;
import edu.cmu.cs.stage3.alice.core.Group;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

public class BehaviorGroupsEditor extends GroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.Editor, edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener
{
  protected GroupingPanel m_containingPanel;
  protected BehaviorGroupEditor worldEditor;
  protected GridBagLayout containingPanelLayout;
  protected JPanel m_header;
  protected JScrollPane scrollPane;
  protected Component glue;
  protected final String BEHAVIOR_NAME = Messages.getString("Events");
  protected static final String DEFAULT_NAME = Messages.getString("event");
  protected static final int SPACE = 8;
  protected static final int OBJECT = 1;
  protected static final int BEHAVIOR = 2;
  protected static final int BAD = -1;
  protected int editorCount = 0;
  protected int counter = 0;
  protected final Color BACKGROUND_COLOR = AuthoringToolResources.getColor("behaviorBackground");
  protected World world;
  protected AuthoringTool authoringTool;
  protected JButton newBehaviorButton;
  protected JPopupMenu behaviorMenu;
  protected Vector behaviorRunnables = new Vector();
  protected JLabel menuLabel = new JLabel();
  protected Vector allSandboxes = new Vector();
  protected boolean paintDropPotential = false;
  protected boolean beingDroppedOn = false;
  


  protected Color dndHighlightColor = AuthoringToolResources.getColor("dndHighlight");
  protected Color dndHighlightColor2 = AuthoringToolResources.getColor("dndHighlight2");
  protected DropPotentialFeedbackListener dropPotentialFeedbackListener = new DropPotentialFeedbackListener();
  
  public final javax.swing.AbstractAction newBehaviorAction = new javax.swing.AbstractAction() {
    public void actionPerformed(java.awt.event.ActionEvent e) {
      setRunnables(world, "");
      createNewBehavior(e);
    }
  };
  
  protected class DropPotentialFeedbackListener implements edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener { protected DropPotentialFeedbackListener() {}
    
    private void doCheck() { Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
      try {
        int type = BehaviorGroupsEditor.checkTransferable(transferable);
        boolean transferableHasPotential = type != -1;
        if (paintDropPotential != transferableHasPotential) {
          paintDropPotential = transferableHasPotential;
          repaint();
        }
      }
      catch (Exception localException) {}
    }
    

    public void dragGestureRecognized(java.awt.dnd.DragGestureEvent dge) {}
    

    public void dragStarted()
    {
      doCheck();
    }
    
    public void dragEnter(DragSourceDragEvent dsde) {
      doCheck();
    }
    
    public void dragExit(java.awt.dnd.DragSourceEvent dse) {
      doCheck();
    }
    

    public void dragOver(DragSourceDragEvent dsde) {}
    
    public void dropActionChanged(DragSourceDragEvent dsde)
    {
      doCheck();
    }
    
    public void dragDropEnd(java.awt.dnd.DragSourceDropEvent dsde) {
      paintDropPotential = false;
      beingDroppedOn = false;
      repaint();
    }
  }
  
  public java.awt.Rectangle getScrollPaneVisibleRect() {
    return scrollPane.getVisibleRect();
  }
  
  public javax.swing.JComponent getScrollPane() {
    return scrollPane;
  }
  
  public javax.swing.JComponent getContainingPanel() {
    return m_containingPanel;
  }
  
  protected class BehaviorMenuItem extends javax.swing.JMenuItem
  {
    private DnDGroupingPanel visibleComponent;
    private JPanel internalComponent;
    
    public BehaviorMenuItem(String s) {
      visibleComponent = new DnDGroupingPanel();
      internalComponent = new JPanel();
      visibleComponent.setLayout(new GridBagLayout());
      BasicBehaviorPanel.buildLabel(internalComponent, s);
      internalComponent.setOpaque(false);
      visibleComponent.setOpaque(true);
      visibleComponent.add(internalComponent, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
      visibleComponent.add(javax.swing.Box.createHorizontalGlue(), new GridBagConstraints(2, 0, 1, 1, 1.0D, 1.0D, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
      visibleComponent.setSize(visibleComponent.getPreferredSize());
      
      Dimension dim = visibleComponent.getPreferredSize();
      internalComponent.doLayout();
      java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(width, height, 2);
      
      JLabel l = new JLabel(Messages.getString("TEST"));
      
      l.print(image.createGraphics());
      setIcon(new javax.swing.ImageIcon(image));
    }
    
    public void menuSelectionChanged(boolean isIncluded) {
      super.menuSelectionChanged(isIncluded);
      if (isIncluded) {
        internalComponent.setBackground(Color.white);
      }
      else {
        internalComponent.setBackground(Color.black);
      }
    }
  }
  
  public static void setName(Behavior toSet, edu.cmu.cs.stage3.alice.core.Element parent) {
    String newName = AuthoringToolResources.getNameForNewChild(DEFAULT_NAME, parent);
    name.set(newName);
  }
  
  public BehaviorGroupsEditor() {
    authoringTool = AuthoringTool.getHack();
    behaviorMenu = new JPopupMenu();
    Class[] behaviors = AuthoringToolResources.getBehaviorClasses();
    Vector structure = new Vector();
    edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.addListener(dropPotentialFeedbackListener);
    for (int i = 0; i < behaviors.length; i++) {
      Class currentBehavior = behaviors[i];
      String behaviorName = AuthoringToolResources.getReprForValue(currentBehavior);
      if (behaviorName == null) {
        behaviorName = Messages.getString("No_Name");
      }
      CreateNewBehaviorRunnable runnable = new CreateNewBehaviorRunnable(currentBehavior, world);
      behaviorRunnables.add(runnable);
      structure.add(new edu.cmu.cs.stage3.util.StringObjectPair(behaviorName, runnable));
    }
    if (structure.size() > 0) {
      behaviorMenu = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu(structure);
    } else {
      behaviorMenu = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeDisabledPopup(Messages.getString("None_Available"));
    }
    behaviorMenu.add(menuLabel, 0);
    initGUI();
    refreshGUI();
  }
  
  public BehaviorGroupsEditor(World theWorld) {
    this();
    setObject(theWorld);
  }
  
  protected BehaviorGroupEditor getEditor(Sandbox toCheck) {
    for (int i = 0; i < m_containingPanel.getComponentCount(); i++) {
      if ((m_containingPanel.getComponent(i) instanceof BehaviorGroupEditor)) {
        BehaviorGroupEditor bge = (BehaviorGroupEditor)m_containingPanel.getComponent(i);
        if (bge.getElement() == toCheck) {
          return bge;
        }
      }
    }
    return null;
  }
  
  public Vector getEditors() {
    Vector toReturn = new Vector();
    for (int i = 0; i < m_containingPanel.getComponentCount(); i++) {
      if ((m_containingPanel.getComponent(i) instanceof BehaviorGroupEditor)) {
        BehaviorGroupEditor bge = (BehaviorGroupEditor)m_containingPanel.getComponent(i);
        Vector behaviorsComponents = bge.getBehaviorComponents();
        for (int j = 0; j < behaviorsComponents.size(); j++) {
          toReturn.add(behaviorsComponents.get(j));
        }
      }
    }
    return toReturn;
  }
  
  protected boolean checkGUI() {
    int total = 1;
    for (int i = 0; i < world.sandboxes.size(); i++) {
      if (world.sandboxes.get(i)).behaviors.size() > 0) {
        total++;
      }
    }
    for (int i = 0; i < world.groups.size(); i++) {
      Group currentGroup = (Group)world.groups.get(i);
      for (int j = 0; j < currentGroup.size(); j++) {
        if ((((Sandbox)currentGroup.getChildAt(j) instanceof Sandbox)) && 
          (getChildAtbehaviors.size() > 0)) {
          total++;
        }
      }
    }
    
    int count = 0;
    for (int i = 0; i < m_containingPanel.getComponentCount(); i++) {
      if ((m_containingPanel.getComponent(i) instanceof BehaviorGroupEditor)) {
        BehaviorGroupEditor bge = (BehaviorGroupEditor)m_containingPanel.getComponent(i);
        if (bge.getElement() == world) {
          count++;
        }
        else if (world.sandboxes.contains(bge.getElement())) {
          if (getElementbehaviors.size() <= 0) {
            return false;
          }
          
          count++;
        }
        else
        {
          boolean found = false;
          for (int j = 0; j < world.groups.size(); j++) {
            Group currentGroup = (Group)world.groups.get(j);
            if (currentGroup.contains(bge.getElement())) {
              if (getElementbehaviors.size() <= 0) {
                return false;
              }
              
              count++;
              found = true;
            }
          }
          
          if (!found) {
            return false;
          }
        }
      }
    }
    if (count != total) {
      return false;
    }
    return true;
  }
  
  protected void resetConstraints() {
    for (int i = 0; i < m_containingPanel.getComponentCount(); i++) {
      Component c = m_containingPanel.getComponent(i);
      GridBagConstraints constraints = containingPanelLayout.getConstraints(c);
      if (c != null) {
        gridy = i;
        containingPanelLayout.setConstraints(c, constraints);
      }
    }
    m_containingPanel.revalidate();
    m_containingPanel.repaint();
  }
  
  public void editorRemoved(BehaviorGroupEditor toRemove) {
    if (getEditor((Sandbox)toRemove.getElement()) != null) {
      editorCount -= 1;
      if (editorCount == 1) {
        worldEditor.removeLabel();
      }
      m_containingPanel.remove(toRemove);
      if (!checkGUI()) {
        refreshGUI();
      }
      else {
        resetConstraints();
      }
    }
  }
  
  public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
  
  public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev)
  {
    if ((ev.getItem() instanceof Behavior)) {
      Sandbox behaviorOwner = (Sandbox)ev.getObjectArrayProperty().getOwner();
      if (behaviors.size() == 0) {
        BehaviorGroupEditor toRemove = getEditor(behaviorOwner);
        if (toRemove != null) {
          editorRemoved(toRemove);
        }
      }
      else if ((ev.getChangeType() == 1) && (behaviors.size() == 1)) {
        addEditor(behaviorOwner);
      }
    }
    else if ((ev.getItem() instanceof Sandbox)) {
      Sandbox child = (Sandbox)ev.getItem();
      if (ev.getChangeType() == 1) {
        addEditor(child);
      }
      else if (ev.getChangeType() == 3) {
        BehaviorGroupEditor toRemove = getEditor(child);
        if (toRemove != null) {
          editorRemoved(toRemove);
        }
        behaviors.removeObjectArrayPropertyListener(this);
      }
    }
    else if ((ev.getItem() instanceof Group)) {
      Group child = (Group)ev.getItem();
      if (ev.getChangeType() == 1) {
        values.addObjectArrayPropertyListener(this);
        for (int j = 0; j < child.size(); j++) {
          if ((child.getChildAt(j) instanceof Sandbox)) {
            Sandbox current = (Sandbox)child.getChildAt(j);
            addEditor(current);
          }
        }
      }
      else if (ev.getChangeType() == 3) {
        values.removeObjectArrayPropertyListener(this);
        for (int j = 0; j < child.size(); j++) {
          if (((Sandbox)child.getChildAt(j) instanceof Sandbox)) {
            Sandbox current = (Sandbox)child.getChildAt(j);
            behaviors.removeObjectArrayPropertyListener(this);
            BehaviorGroupEditor toRemove = getEditor(current);
            if (toRemove != null) {
              editorRemoved(toRemove);
            }
          }
        }
      }
    }
  }
  
  protected void createNewBehavior(java.awt.event.ActionEvent e) {
    behaviorMenu.show(newBehaviorButton, 0, newBehaviorButton.getHeight());
    edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(behaviorMenu);
  }
  
  protected void addEditor(Sandbox toAdd) {
    ObjectArrayProperty currentGroup = behaviors;
    if ((getEditor(toAdd) == null) && (currentGroup != null)) {
      behaviors.addObjectArrayPropertyListener(this);
      if (currentGroup.size() > 0) {
        editorCount += 1;
        if (editorCount > 1) {
          worldEditor.addLabel();
        }
        BehaviorGroupEditor editor = new BehaviorGroupEditor();
        editor.set(currentGroup.getOwner(), authoringTool);
        editor.setDropTarget(new DropTarget(editor, editor));
        m_containingPanel.remove(glue);
        m_containingPanel.add(editor, new GridBagConstraints(0, m_containingPanel.getComponentCount(), 1, 1, 0.0D, 0.0D, 18, 2, new Insets(5, 4, 3, 2), 0, 0));
        m_containingPanel.add(glue, new GridBagConstraints(0, m_containingPanel.getComponentCount(), 1, 1, 1.0D, 1.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        if (!checkGUI()) {
          refreshGUI();
        }
        else {
          resetConstraints();
          revalidate();
          repaint();
        }
      }
    }
  }
  
  class CreateNewBehaviorRunnable implements Runnable {
    Class behaviorClass;
    Sandbox owner;
    
    public CreateNewBehaviorRunnable(Class behaviorClass, Sandbox owner) {
      this.behaviorClass = behaviorClass;
      this.owner = owner;
    }
    
    public void setOwner(Sandbox owner) {
      this.owner = owner;
    }
    
    public void run() {
      try {
        if (authoringTool != null) {
          authoringTool.getUndoRedoStack().startCompound();
        }
        Object instance = behaviorClass.newInstance();
        if ((instance instanceof Behavior)) {
          Behavior behavior = (Behavior)instance;
          BehaviorGroupsEditor.setName(behavior, owner);
          behavior.setParent(owner);
          owner.behaviors.add(0, behavior);
          behavior.manufactureDetails();
          if ((behavior instanceof edu.cmu.cs.stage3.alice.core.behavior.KeyboardNavigationBehavior)) {
            subject.set(authoringTool.getCurrentCamera());
          }
        }
        if (authoringTool != null) {
          authoringTool.getUndoRedoStack().stopCompound();
        }
      } catch (Throwable t) {
        AuthoringTool.showErrorDialog(Messages.getString("Failed_to_create_new_event"), t);
      }
    }
  }
  
  public void release() {
    super.release();
    if (world != null) {
      world.sandboxes.removeObjectArrayPropertyListener(this);
      world.groups.removeObjectArrayPropertyListener(this);
      for (int i = 0; i < world.sandboxes.size(); i++) {
        Sandbox current = (Sandbox)world.sandboxes.get(i);
        behaviors.removeObjectArrayPropertyListener(this);
      }
      for (int i = 0; i < world.groups.size(); i++) {
        Group currentGroup = (Group)world.groups.get(i);
        values.removeObjectArrayPropertyListener(this);
        for (int j = 0; j < currentGroup.size(); j++) {
          if (((Sandbox)currentGroup.getChildAt(j) instanceof Sandbox)) {
            Sandbox current = (Sandbox)currentGroup.getChildAt(j);
            behaviors.removeObjectArrayPropertyListener(this);
          }
        }
      }
    }
  }
  
  public void setAuthoringTool(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
    for (int i = 0; i < m_containingPanel.getComponentCount(); i++) {
      if ((m_containingPanel.getComponent(i) instanceof BehaviorGroupEditor)) {
        ((BehaviorGroupEditor)m_containingPanel.getComponent(i)).setAuthoringTool(authoringTool);
      }
    }
  }
  
  protected void setRunnables(Sandbox s, String label) {
    for (int i = 0; i < behaviorRunnables.size(); i++) {
      ((CreateNewBehaviorRunnable)behaviorRunnables.get(i)).setOwner(s);
    }
    menuLabel.setText(label);
  }
  
  public void setObject(World theWorld) {
    release();
    world = theWorld;
    
    if (world != null) {
      setRunnables(world, "");
      world.sandboxes.addObjectArrayPropertyListener(this);
      world.groups.addObjectArrayPropertyListener(this);
      for (int i = 0; i < world.sandboxes.size(); i++) {
        Sandbox current = (Sandbox)world.sandboxes.get(i);
        behaviors.addObjectArrayPropertyListener(this);
      }
      for (int i = 0; i < world.groups.size(); i++) {
        Group currentGroup = (Group)world.groups.get(i);
        values.addObjectArrayPropertyListener(this);
        for (int j = 0; j < currentGroup.size(); j++) {
          if (((Sandbox)currentGroup.getChildAt(j) instanceof Sandbox)) {
            Sandbox current = (Sandbox)currentGroup.getChildAt(j);
            if (behaviors != null) {
              behaviors.addObjectArrayPropertyListener(this);
            }
          }
        }
      }
    }
    refreshGUI();
  }
  
  public Object getObject() {
    return world;
  }
  
  public javax.swing.JComponent getJComponent() {
    return this;
  }
  
  private void removeAllElements(JPanel container) {
    for (int i = 0; i < container.getComponentCount(); i++) {
      if ((container.getComponent(i) instanceof BehaviorGroupEditor)) {
        ((BehaviorGroupEditor)container.getComponent(i)).release();
      }
    }
    container.removeAll();
  }
  
  protected void initGUI() {
    setMinimumSize(new Dimension(0, 0));
    setLayout(new java.awt.BorderLayout());
    setBackground(BACKGROUND_COLOR);
    
    setBorder(null);
    
    scrollPane = new JScrollPane(20, 30);
    scrollPane.setBorder(null);
    add(scrollPane, "Center");
    
    m_containingPanel = new GroupingPanel()
    {
      public void dragEnter(DropTargetDragEvent dtde) {
        BehaviorGroupsEditor.this.dragEnter(dtde);
      }
      
      public void dragExit(java.awt.dnd.DropTargetEvent dte) {
        BehaviorGroupsEditor.this.dragExit(dte);
      }
      
      public void dragOver(DropTargetDragEvent dtde)
      {
        BehaviorGroupsEditor.this.dragOver(dtde);
      }
      
      public void drop(DropTargetDropEvent dtde)
      {
        BehaviorGroupsEditor.this.drop(dtde);
      }
      
      public void dropActionChanged(DropTargetDragEvent dtde)
      {
        BehaviorGroupsEditor.this.dropActionChanged(dtde);
      }
    };
    String toolTipText = "<html><body>" + 
      Messages.getString("_p_Events__p_") + 
      Messages.getString("_p_Events_run_Methods_when_certain_things_happen__p_") + 
      Messages.getString("_p__like_when_the_mouse_is_clicked_on_an_Object_or__p_") + 
      Messages.getString("_p_when_a_certain_key_is_pressed___p_") + "</body></html>";
    containingPanelLayout = new GridBagLayout();
    m_containingPanel.setLayout(containingPanelLayout);
    m_containingPanel.setBorder(null);
    m_containingPanel.setBackground(BACKGROUND_COLOR);
    m_containingPanel.setToolTipText(toolTipText);
    scrollPane.setViewportView(m_containingPanel);
    
    newBehaviorButton = new JButton(Messages.getString("create_new_event"));
    newBehaviorButton.setToolTipText(Messages.getString("Display_Menu_of_New_Event_Types"));
    newBehaviorButton.setMargin(new Insets(2, 2, 2, 2));
    newBehaviorButton.addActionListener(newBehaviorAction);
    newBehaviorButton.setBackground(new Color(240, 240, 255));
    newBehaviorButton.setDropTarget(new DropTarget(newBehaviorButton, this));
    
    m_header = new JPanel();
    m_header.setLayout(new java.awt.FlowLayout(0, 2, 2));
    m_header.setBackground(BACKGROUND_COLOR);
    m_header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
    m_header.setToolTipText(toolTipText);
    JLabel behaviorLabel = new JLabel(BEHAVIOR_NAME);
    int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
    java.awt.Font behaviorFont = new java.awt.Font("Helvetica", 1, (int)(16 * fontSize / 12.0D));
    behaviorLabel.setFont(behaviorFont);
    behaviorLabel.setDropTarget(new DropTarget(behaviorLabel, this));
    m_header.add(behaviorLabel);
    m_header.add(javax.swing.Box.createHorizontalStrut(4));
    m_header.add(newBehaviorButton);
    m_header.setDropTarget(new DropTarget(m_header, this));
    add(m_header, "North");
    
    glue = javax.swing.Box.createVerticalGlue();
    setToolTipText(toolTipText);
  }
  
  protected synchronized void refreshGUI() {
    if (world != null) {
      removeAll();
      add(m_header, "North");
      add(scrollPane, "Center");
      removeAllElements(m_containingPanel);
      int count = 0;
      worldEditor = new BehaviorGroupEditor();
      worldEditor.set(world, authoringTool);
      worldEditor.setEmptyString(" " + Messages.getString("No_events"));
      m_containingPanel.add(worldEditor, new GridBagConstraints(0, count, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(5, 4, 3, 2), 0, 0));
      BehaviorGroupEditor editor = null;
      for (int i = 0; i < world.sandboxes.size(); i++) {
        ObjectArrayProperty currentGroup = world.sandboxes.get(i)).behaviors;
        if ((currentGroup != null) && 
          (currentGroup.size() > 0)) {
          count++;
          editor = new BehaviorGroupEditor();
          editor.set(currentGroup.getOwner(), authoringTool);
          
          m_containingPanel.add(editor, new GridBagConstraints(0, count, 1, 1, 0.0D, 0.0D, 18, 2, new Insets(5, 4, 3, 2), 0, 0));
        }
      }
      

      if (count == 0) {
        worldEditor.removeLabel();
      }
      m_containingPanel.add(glue, new GridBagConstraints(0, count + 1, 1, 1, 1.0D, 1.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
      editorCount = (count + 1);
    }
    revalidate();
    repaint();
  }
  
  protected void printComponents(Container c)
  {
    System.out.println(c + "n\n");
    for (int i = 0; i < c.getComponentCount(); i++) {
      if ((c.getComponent(i) instanceof Container)) {
        printComponents((Container)c.getComponent(i));
      }
      else {
        System.out.println(c.getComponent(i));
      }
    }
  }
  
  protected static int checkDragEvent(DropTargetDragEvent dtde) throws java.io.IOException, java.awt.datatransfer.UnsupportedFlavorException {
    if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor)) {
      Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
      CopyFactory copyFactory = (CopyFactory)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor);
      Class valueClass = copyFactory.getValueClass();
      if (Behavior.class.isAssignableFrom(valueClass)) {
        return 2;
      }
      
      return -1;
    }
    
    if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.transformableReferenceFlavor)) {
      return 1;
    }
    if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, ElementReferenceTransferable.behaviorReferenceFlavor)) {
      return 2;
    }
    return -1;
  }
  
  protected static int checkTransferable(Transferable transferable) throws java.io.IOException, java.awt.datatransfer.UnsupportedFlavorException {
    if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor)) {
      CopyFactory copyFactory = (CopyFactory)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor);
      Class valueClass = copyFactory.getValueClass();
      if (Behavior.class.isAssignableFrom(valueClass)) {
        return 2;
      }
      
      return -1;
    }
    
    if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementReferenceTransferable.transformableReferenceFlavor)) {
      return 1;
    }
    if (AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementReferenceTransferable.behaviorReferenceFlavor)) {
      return 2;
    }
    return -1;
  }
  
  protected Component getTopComponent(Component c) {
    if (((c instanceof BehaviorGroupEditor)) || (c == this) || (c == m_containingPanel) || (c == null)) {
      return c;
    }
    
    return getTopComponent(c.getParent());
  }
  
  private Component getPrimaryComponent(Component c)
  {
    if (c == null) {
      return c;
    }
    if (c.getParent() == m_containingPanel) {
      return c;
    }
    return getPrimaryComponent(c.getParent());
  }
  
  protected BehaviorGroupEditor getEditor(Point p) {
    int numSpots = editorCount * 2;
    int[] spots = new int[numSpots];
    for (int i = 0; i < editorCount; i++) {
      Component c = m_containingPanel.getComponent(i);
      spots[(i * 2)] = getBoundsy;
      spots[(i * 2 + 1)] = (getBoundsy + getBoundsheight);
    }
    int closestSpot = -1;
    int minDist = Integer.MAX_VALUE;
    for (int i = 0; i < numSpots; i++) {
      int d = Math.abs(y - spots[i]);
      if (d < minDist) {
        minDist = d;
        closestSpot = i;
      }
    }
    return (BehaviorGroupEditor)m_containingPanel.getComponent(closestSpot / 2);
  }
  
  protected void paintLineInEditor(DropTargetDragEvent dtde) {
    BehaviorGroupEditor currentEditor = getEditor(javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), m_containingPanel));
    
    currentEditor.dragOver(dtde);
  }
  




  public void dragEnter(DropTargetDragEvent dtde)
  {
    try
    {
      int type = checkDragEvent(dtde);
      if ((type != -1) && (type != 2)) {
        dtde.acceptDrag(2);
        counter += 1;
        beingDroppedOn = true;
        repaint();
      }
      else {
        beingDroppedOn = false;
        repaint();
        dtde.rejectDrag();
      }
    }
    catch (Exception e) {
      dtde.rejectDrag();
      beingDroppedOn = false;
      repaint();
    }
  }
  
  public void dragExit(java.awt.dnd.DropTargetEvent dte) {
    if (beingDroppedOn) {
      beingDroppedOn = false;
      repaint();
    } else {
      super.dragExit(dte);
    }
  }
  
  public void dragOver(DropTargetDragEvent dtde) {
    try {
      int type = checkDragEvent(dtde);
      if (type == -1) {
        dtde.rejectDrag();
        if (beingDroppedOn) {
          beingDroppedOn = false;
          repaint();
        }
      }
      else if (type == 1) {
        dtde.acceptDrag(2);
        if (!beingDroppedOn) {
          beingDroppedOn = true;
          repaint();
        }
      } else if (type == 2) {
        Component primary = getPrimaryComponent(dtde.getDropTargetContext().getComponent());
        if (!(primary instanceof BehaviorGroupEditor)) {
          BehaviorGroupEditor currentEditor = getEditor(javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), m_containingPanel));
          if (currentEditor != null) {
            componentElementPanel.dragOver(dtde);
          }
        }
      }
    }
    catch (Exception e)
    {
      dtde.rejectDrag();
      beingDroppedOn = false;
      repaint();
    }
  }
  
  public void drop(DropTargetDropEvent dtde) {
    Transferable transferable = dtde.getTransferable();
    boolean dropSuccess = true;
    try {
      int type = checkTransferable(transferable);
      if (type == -1) {
        dtde.rejectDrop();
        dropSuccess = false;
      }
      else if (type == 1) {
        dtde.acceptDrop(2);
        edu.cmu.cs.stage3.alice.core.Transformable droppedElement = (edu.cmu.cs.stage3.alice.core.Transformable)dtde.getTransferable().getTransferData(ElementReferenceTransferable.transformableReferenceFlavor);
        setRunnables(droppedElement, Messages.getString("New_") + name.get() + " " + Messages.getString("event"));
        behaviorMenu.show(dtde.getDropTargetContext().getComponent(), (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
        edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(behaviorMenu);
      }
      else if (type == 2) {
        Component primary = getPrimaryComponent(dtde.getDropTargetContext().getComponent());
        if (!(primary instanceof BehaviorGroupEditor)) {
          BehaviorGroupEditor currentEditor = getEditor(javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), m_containingPanel));
          if (currentEditor != null) {
            componentElementPanel.drop(dtde);
          }
        }
      }
    }
    catch (Exception e) {
      AuthoringTool.showErrorDialog(Messages.getString("The_drop_failed_"), e);
      dropSuccess = false;
    }
    beingDroppedOn = false;
    paintDropPotential = false;
    repaint();
    dtde.dropComplete(dropSuccess);
  }
  
  public void dropActionChanged(DropTargetDragEvent dtde) {}
  
  public void paintForeground(Graphics g)
  {
    super.paintForeground(g);
    Point p = javax.swing.SwingUtilities.convertPoint(m_containingPanel, m_containingPanel.getLocation(), this);
    if (beingDroppedOn) {
      Dimension size = m_containingPanel.getSize();
      g.setColor(dndHighlightColor2);
      g.drawRect(x, y, width - 1, height - 1);
      g.drawRect(x + 1, y + 1, width - 3, height - 3);
    } else if (paintDropPotential) {
      Dimension size = m_containingPanel.getSize();
      g.setColor(dndHighlightColor);
      g.drawRect(x, y, width - 1, height - 1);
      g.drawRect(x + 1, y + 1, width - 3, height - 3); } }
  
  public void stateChanging(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarting(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopping(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPausing(AuthoringToolStateChangedEvent ev) {}
  
  public void worldSaving(AuthoringToolStateChangedEvent ev) {}
  
  public void stateChanged(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoaded(AuthoringToolStateChangedEvent ev) { refreshGUI(); }
  
  public void worldUnLoaded(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarted(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStopped(AuthoringToolStateChangedEvent ev) {}
  
  public void worldPaused(AuthoringToolStateChangedEvent ev) {}
  public void worldSaved(AuthoringToolStateChangedEvent ev) { refreshGUI(); }
}
