package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElementContainerListener;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
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
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;

public abstract class CompositeComponentElementPanel extends GroupingPanel implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener, javax.swing.event.PopupMenuListener
{
  protected final boolean USE_DEPTH = false;
  
  protected static Vector timers = new Vector();
  protected static boolean shouldReact = true;
  
  public static final int LEFT_INDENT = 15;
  public static final int RIGHT_INDENT = 5;
  public static final int STRUT_SIZE = 8;
  public static final int SCROLL_SIZE = 8;
  public static int SCROLL_AMOUNT = 3;
  
  public static final int SCROLL_DELTA = 1;
  public static final int SCROLL_START = 3;
  public static final int MAX_SCROLL = 10;
  public ObjectArrayProperty componentElements;
  public CompositeComponentOwner m_owner;
  protected boolean HACK_started = false;
  
  protected JScrollPane topLevelScrollPane;
  protected boolean inserting = false;
  protected boolean insertingElement = false;
  private boolean invalidEvent = false;
  protected InsertPanel insertPanel;
  protected GridBagLayout panelLayout;
  protected GridBagConstraints panelConstraints;
  protected Component glue;
  protected Component strut;
  protected Insets insets;
  protected int dropPanelPosition = -2;
  
  protected static Color dndFeedBackColor = AuthoringToolResources.getColor("dndHighlight2");
  
  protected static CompositeComponentElementPanel s_currentComponentPanel;
  protected static Component s_componentPanelMoved;
  protected int dropPanelLocation = -2;
  protected int m_depth = -1;
  protected int lineLocation = -1;
  protected int oldLineLocation = -1;
  protected int insertLocation = -1;
  protected static boolean showDropPanel = false;
  protected boolean shouldDrawLine = false;
  protected java.awt.event.ContainerAdapter containerAdapter;
  protected Timer HACK_timer;
  protected Timer lingerTimer;
  protected Timer insertTimer;
  protected AuthoringTool authoringTool;
  private static Container s_prevContainer;
  private static Rectangle s_prevRect;
  
  public CompositeComponentElementPanel()
  {
    insets = new Insets(3, 2, 0, 2);
    panelLayout = new GridBagLayout();
    panelConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 18, 2, insets, 0, 0);
    setOpaque(true);
    setLayout(panelLayout);
    setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 15, 0, 5));
    glue = Box.createVerticalGlue();
    strut = Box.createVerticalStrut(8);
    insertPanel = new InsertPanel();
    HACK_timer = new Timer(200, new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        removeDropPanelFromCurrentComponent();
      }
    });
    HACK_timer.setRepeats(false);
    s_currentComponentPanel = this;
  }
  
  protected boolean isInverted() {
    return false;
  }
  
  protected void startListening()
  {
    if (componentElements != null) {
      componentElements.addObjectArrayPropertyListener(this);
    }
  }
  
  protected void stopListening() {
    if (componentElements != null) {
      componentElements.removeObjectArrayPropertyListener(this);
    }
  }
  
  protected void setTopLevelScrollPanel() {
    Container currentOwner = (Container)m_owner;
    while ((!(currentOwner instanceof MainCompositeElementPanel)) && (currentOwner != null)) {
      currentOwner = currentOwner.getParent();
    }
    if (currentOwner != null) {
      topLevelScrollPane = scrollPane;
    }
  }
  
  public void set(ObjectArrayProperty elements, CompositeComponentOwner owner, AuthoringTool authoringToolIn) {
    clean();
    authoringTool = authoringToolIn;
    componentElements = elements;
    clean();
    m_owner = owner;
    updateGUI();
    startListening();
  }
  
  public void setAuthoringTool(AuthoringTool authoringToolIn) {
    authoringTool = authoringToolIn;
  }
  
  public void setStrut(int size) {
    strut = Box.createVerticalStrut(size);
    clean();
    updateGUI();
    wakeUp();
  }
  
  public void goToSleep() {
    stopListening();
    HACK_timer.stop();
  }
  
  public void wakeUp() {
    startListening();
  }
  
  public void clean() {
    goToSleep();
  }
  
  protected void removeAllListening() {}
  
  public void die()
  {
    clean();
    removeAll();
    removeContainerListener(containerAdapter);
  }
  
  public CompositeComponentOwner getOwner() {
    return m_owner;
  }
  
  public Element getElement() {
    return componentElements.getOwner();
  }
  
  protected static void stopAllTimers() {
    for (int i = 0; i < timers.size(); i++) {
      Timer t = (Timer)timers.elementAt(i);
      t.stop();
    }
    timers.removeAllElements();
  }
  
  protected abstract Component makeGUI(Element paramElement);
  
  protected void updateGUI() {
    if (componentElements != null) {
      if (componentElements.size() > 0) {
        removeAll();
        resetGUI();
        for (int i = 0; i < componentElements.size(); i++) {
          Element currentElement = (Element)componentElements.getArrayValue()[i];
          Component toAdd = makeGUI(currentElement);
          if (toAdd != null) {
            addElementPanel(toAdd, i);
          }
        }
      }
      else {
        addDropTrough();
      }
      
    }
    else if (m_owner != null) {
      m_owner.setEnabled(false);
    }
    
    revalidate();
    repaint();
  }
  
  protected void resetGUI() {
    if (getComponentCount() < 2) {
      removeAll();
      add(strut, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(0, 0, 0, 0), 0, 0));
      add(glue, new GridBagConstraints(0, 2, 1, 1, 1.0D, 1.0D, 18, 0, new Insets(0, 0, 0, 0), 0, 0));
    }
  }
  
  protected void addDropTrough() {
    if ((componentElements != null) && (componentsIsEmpty())) {
      removeAll();
      panelConstraints.gridy = 0;
      add(insertPanel, panelConstraints, 0);
      add(strut, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(0, 0, 0, 0), 0, 0));
      add(glue, new GridBagConstraints(0, 2, 1, 1, 1.0D, 1.0D, 18, 0, new Insets(0, 0, 0, 0), 0, 0));
    }
  }
  
  public void objectArrayPropertyChanging(ObjectArrayPropertyEvent propertyEvent) {
    invalidEvent = false;
    if ((propertyEvent.getChangeType() == 1) && 
      (componentElements.get(propertyEvent.getNewIndex()) == propertyEvent.getItem())) {
      invalidEvent = true;
    }
    
    if (propertyEvent.getChangeType() == 2) {
      if (componentElements.get(propertyEvent.getNewIndex()) == propertyEvent.getItem()) {
        invalidEvent = true;
      }
      if (componentElements.get(propertyEvent.getOldIndex()) != propertyEvent.getItem()) {
        invalidEvent = true;
      }
    }
    if ((propertyEvent.getChangeType() == 3) && 
      (componentElements.get(propertyEvent.getOldIndex()) != propertyEvent.getItem())) {
      invalidEvent = true;
    }
  }
  

  protected int getElementComponentCount()
  {
    int count = 0;
    for (int i = 0; i < getComponentCount(); i++) {
      if (((getComponent(i) instanceof CompositeElementPanel)) || ((getComponent(i) instanceof ComponentElementPanel))) {
        count++;
      }
    }
    return count;
  }
  
  protected boolean checkGUI() {
    Component[] c = getComponents();
    Element[] elements = (Element[])componentElements.get();
    int elementCount = getElementComponentCount();
    boolean aOkay = elements.length == elementCount;
    if (aOkay)
    {
      for (int i = 0; i < elements.length; i++) {
        if ((c[i] instanceof CompositeElementPanel)) {
          if (i < elements.length) {
            if (((CompositeElementPanel)c[i]).getElement() != elements[i]) {
              aOkay = false;
              break;
            }
          }
          else {
            aOkay = false;
            break;
          }
        }
        if ((c[i] instanceof ComponentElementPanel)) {
          if (i < elements.length) {
            if (((ComponentElementPanel)c[i]).getElement() != elements[i]) {
              aOkay = false;
              break;
            }
          }
          else {
            aOkay = false;
            break;
          }
        }
      }
    }
    return aOkay;
  }
  
  public void objectArrayPropertyChanged(ObjectArrayPropertyEvent propertyEvent) {
    if ((shouldReact) && (!invalidEvent)) {
      boolean successful = true;
      Element eventElement = (Element)propertyEvent.getItem();
      int index = propertyEvent.getNewIndex();
      if (propertyEvent.getChangeType() == 1) {
        Component toInsert = s_componentPanelMoved;
        s_componentPanelMoved = null;
        boolean isCorrectPanel = false;
        if (toInsert == null) {
          toInsert = makeGUI(eventElement);
          isCorrectPanel = true;
        }
        if (((toInsert instanceof CompositeElementPanel)) && (!isCorrectPanel)) {
          if (((CompositeElementPanel)toInsert).getElement() == eventElement) {
            isCorrectPanel = true;
          }
        }
        else if (((toInsert instanceof ComponentElementPanel)) && (!isCorrectPanel) && 
          (((ComponentElementPanel)toInsert).getElement() == eventElement)) {
          isCorrectPanel = true;
        }
        
        if (!isCorrectPanel) {
          toInsert = makeGUI(eventElement);
        }
        if (toInsert != null) {
          addElementPanel(toInsert, index);
        }
        else {
          successful = false;
        }
      }
      if (propertyEvent.getChangeType() == 3) {
        s_componentPanelMoved = getComponent(eventElement);
        if (s_componentPanelMoved != null) {
          removeContainerListener(GUIElementContainerListener.getStaticListener());
          remove(s_componentPanelMoved);
          addContainerListener(GUIElementContainerListener.getStaticListener());
        }
        else {
          successful = false;
        }
      }
      if (propertyEvent.getChangeType() == 2) {
        s_componentPanelMoved = null;
        Component c = getComponent(eventElement);
        if (c != null) {
          removeContainerListener(GUIElementContainerListener.getStaticListener());
          remove(c);
          addElementPanel(c, index);
          addContainerListener(GUIElementContainerListener.getStaticListener());
        }
        else {
          successful = false;
        }
      }
      successful = (successful) && (checkGUI());
      if (successful) {
        revalidate();
        repaint();
      }
      else {
        clean();
        updateGUI();
        wakeUp();
      }
    }
  }
  
  public void addElementPanel(Component toAdd, int position)
  {
    if ((getComponentCount() != 0) && (getComponent(0) == insertPanel)) {
      remove(insertPanel);
    }
    if ((position > getElementComponentCount()) || (position < 0)) {
      position = getElementComponentCount();
      panelConstraints.gridy = position;
    }
    add(toAdd, panelConstraints, position);
    restoreLayout();
    doLayout();
    if ((toAdd instanceof JComponent)) {
      if (topLevelScrollPane != null) {
        Rectangle rect = toAdd.getBounds();
        int horizValue = topLevelScrollPane.getHorizontalScrollBar().getValue();
        ((JComponent)toAdd).scrollRectToVisible(rect);
        topLevelScrollPane.getHorizontalScrollBar().setValue(horizValue);
      }
      else {
        ((JComponent)toAdd).scrollRectToVisible(toAdd.getBounds());
      }
    }
    repaint();
    revalidate();
  }
  
  protected Component getComponent(Element element) {
    for (int i = 0; i < getComponentCount(); i++) {
      if ((getComponent(i) instanceof ComponentElementPanel)) {
        ComponentElementPanel c = (ComponentElementPanel)getComponent(i);
        if (c.getElement() == element) {
          return c;
        }
      }
      else if ((getComponent(i) instanceof CompositeElementPanel)) {
        CompositeElementPanel c = (CompositeElementPanel)getComponent(i);
        if (c.getElement() == element) {
          return c;
        }
      }
    }
    return null;
  }
  
  protected void bumpDown(int position) {
    for (int i = position; i < getComponentCount(); i++) {
      panelConstraints.gridy = (i + 1);
      panelLayout.setConstraints(getComponent(i), panelConstraints);
    }
  }
  
  public void setBackground(Color color) {
    super.setBackground(color);
    if (insertPanel != null) {
      insertPanel.setBackground(color);
    }
  }
  
  protected void restoreLayout() {
    for (int i = 0; i < getComponentCount(); i++) {
      Component current = getComponent(i);
      GridBagConstraints c = panelLayout.getConstraints(current);
      if (gridy != i) {
        gridy = i;
        panelLayout.setConstraints(current, c);
      }
    }
  }
  
  public void remove(Component c) {
    super.remove(c);
    if ((((c instanceof CompositeElementPanel)) || ((c instanceof ComponentElementPanel))) && 
      (componentElements != null) && (componentsIsEmpty())) {
      addDropTrough();
      return;
    }
    
    restoreLayout();
  }
  
  public void removeDropPanel() {
    if (componentsIsEmpty()) {
      insertPanel.setHighlight(false);
      insertPanel.setHighlight(false);
    }
  }
  
  public Point convertToDropPanelSpace(Point p) {
    if (insertPanel.isShowing()) {
      return SwingUtilities.convertPoint(this, p, insertPanel);
    }
    
    return null;
  }
  
  public InsertPanel getDropPanel()
  {
    return insertPanel;
  }
  
  public void setBorder(int top, int left, int bottom, int right) {
    setBorder(javax.swing.BorderFactory.createEmptyBorder(top, left, bottom, right));
  }
  
  public void setEmptyString(String emptyString) {
    insertPanel.m_doNothingLabel = emptyString;
    insertPanel.m_label.setText(insertPanel.m_doNothingLabel);
  }
  



  public void dragEnter(DropTargetDragEvent dtde)
  {
    if ((!m_owner.isExpanded()) && 
      ((m_owner.getParent() instanceof CompositeComponentElementPanel))) {
      ((CompositeComponentElementPanel)m_owner.getParent()).dragEnter(dtde);
      return;
    }
    
    stopAllTimers();
    AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor);
    dtde.acceptDrag(dtde.getDropAction());
  }
  
  public abstract void dragOver(DropTargetDragEvent paramDropTargetDragEvent);
  
  protected Component getContainingComponentPanel(Component c)
  {
    if (c == m_owner.getGrip()) {
      return c;
    }
    if (c == null) {
      return null;
    }
    if ((c.getParent() instanceof CompositeComponentElementPanel)) {
      return c;
    }
    if (c.getParent() == null) {
      return null;
    }
    return getContainingComponentPanel(c.getParent());
  }
  
  protected int getElementCount() {
    return componentElements.size();
  }
  
  public ObjectArrayProperty getComponentProperty() {
    return componentElements;
  }
  
  protected boolean checkDropLocation(int i, Element toDrop) {
    return true;
  }
  
  protected int getInsertLocation(Point panelSpacePoint) {
    int position = -1;
    int size = componentElements.size();
    for (int i = 0; i < size; i++) {
      Component c = getComponent(i);
      int point = c.getY() + (int)(c.getHeight() / 2.0D);
      if (panelSpacePoint.getY() < point) {
        position = i;
        break;
      }
    }
    return position;
  }
  


  private static void drawRect(Container container, Color color, Rectangle rect)
  {
    Graphics g = container.getGraphics();
    if ((g != null) && 
      (color != null)) {
      g.setColor(color);
      g.fillRect(x, y, width, height);
      g.dispose();
    }
  }
  
  private static void drawFeedback(Container container, Rectangle rect)
  {
    if ((container != null) && (container.equals(s_prevContainer)) && 
      (rect != null) && (rect.equals(s_prevRect))) {
      drawRect(container, dndFeedBackColor, rect);
      return;
    }
    
    if (s_prevContainer != null) {
      drawRect(s_prevContainer, s_prevContainer.getBackground(), s_prevRect);
      if ((s_prevContainer instanceof CompositeComponentElementPanel)) {
        s_prevContainershouldDrawLine = false;
      }
    }
    if (container != null) {
      drawRect(container, dndFeedBackColor, rect);
      if ((container instanceof CompositeComponentElementPanel)) {
        shouldDrawLine = true;
      }
    }
    s_prevContainer = container;
    s_prevRect = rect;
  }
  
  protected void paintLine(Container toPaint, int location) {
    if ((location == -1) || (location >= toPaint.getComponentCount())) {
      int lastSpot = toPaint.getComponentCount() - 1;
      for (int i = lastSpot; i >= 0; i--) {
        if ((toPaint.getComponent(i) instanceof GroupingPanel)) {
          lastSpot = i;
          break;
        }
        lastSpot = i;
      }
      if (lastSpot < 0) {
        lastSpot = 0;
      }
      Rectangle bounds = toPaint.getComponent(lastSpot).getBounds();
      lineLocation = (y + height);
    }
    else {
      Rectangle bounds = toPaint.getComponent(location).getBounds();
      lineLocation = (y - 2);
    }
    Rectangle toPaintBounds = toPaint.getBounds();
    drawFeedback(toPaint, new Rectangle(2, lineLocation, width - 4, 2));
  }
  
  public void paint(Graphics g) {
    super.paint(g);
    if (shouldDrawLine) {
      paintLine(this, insertLocation);
    }
  }
  
  protected boolean componentsIsEmpty() {
    return componentElements.size() == 0;
  }
  
  protected void scrollIt(Point hoverPoint) {
    Dimension scrollSpace = topLevelScrollPane.getSize();
    int currentValue = topLevelScrollPane.getVerticalScrollBar().getValue();
    int maxValue = topLevelScrollPane.getVerticalScrollBar().getMaximum();
    int minValue = topLevelScrollPane.getVerticalScrollBar().getMinimum();
    int amountToScroll = SCROLL_AMOUNT;
    if (y < 8) {
      if (currentValue > minValue) {
        if (currentValue - amountToScroll >= minValue) {
          currentValue -= amountToScroll;
        }
        else {
          currentValue = minValue;
        }
        topLevelScrollPane.getVerticalScrollBar().setValue(currentValue);
        SCROLL_AMOUNT += 1;
        if (SCROLL_AMOUNT > 10) {
          SCROLL_AMOUNT = 10;
        }
      }
    }
    else if (y > scrollSpace.getHeight() - 8.0D) {
      if (currentValue < maxValue) {
        if (currentValue + amountToScroll <= maxValue) {
          currentValue += amountToScroll;
        }
        else {
          currentValue = maxValue;
        }
        topLevelScrollPane.getVerticalScrollBar().setValue(currentValue);
        SCROLL_AMOUNT += 1;
        if (SCROLL_AMOUNT > 10) {
          SCROLL_AMOUNT = 10;
        }
      }
    }
    else {
      SCROLL_AMOUNT = 3;
    }
  }
  
  protected boolean isMyParent(Element currentParent, Element toSearchFor)
  {
    if (currentParent == toSearchFor) {
      return true;
    }
    if (currentParent == null) {
      return false;
    }
    return isMyParent(currentParent.getParent(), toSearchFor);
  }
  


  protected void insertDropPanel(DropTargetDragEvent dtde)
  {
    Component dropComponent = dtde.getDropTargetContext().getComponent();
    Transferable currentTransferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
    Element currentElement = null;
    

    if (topLevelScrollPane == null) {
      setTopLevelScrollPanel();
    }
    
    if (this != s_currentComponentPanel) {
      s_currentComponentPanel.removeDropPanel();
      s_currentComponentPanel = this;
    }
    Point panelSpacePoint = dtde.getLocation();
    if (topLevelScrollPane != null) {
      Point mainSpacePoint = SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), panelSpacePoint, topLevelScrollPane);
      scrollIt(mainSpacePoint);
    }
    if (dropComponent != this) {
      dropComponent = getContainingComponentPanel(dropComponent);
      panelSpacePoint = SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), this);
    }
    if (componentsIsEmpty()) {
      drawFeedback(null, null);
      getDropPanel().setHighlight(true);
      return;
    }
    if (currentTransferable != null) {
      try {
        currentElement = (Element)currentTransferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
      }
      catch (Exception localException) {}
    }
    else {
      insertLocation = -1;
      return;
    }
    
    int position = getInsertLocation(panelSpacePoint);
    if (checkDropLocation(position, currentElement)) {
      insertLocation = position;
      paintLine(this, insertLocation);
    }
  }
  


  public void removeDropPanelFromCurrentComponent()
  {
    shouldDrawLine = false;
    drawFeedback(null, null);
    dropPanelLocation = -2;
    if (s_currentComponentPanel != null) {
      s_currentComponentPanel.removeDropPanel();
    }
  }
  
  protected void unhook(Element element) {
    element.removeFromParent();
  }
  
  protected void addToElement(Element toAdd, ObjectArrayProperty toAddTo, int location) {
    String newName = AuthoringToolResources.getNameForNewChild(name.getStringValue(), toAddTo.getOwner());
    name.set(newName);
    toAdd.setParent(toAddTo.getOwner());
    toAddTo.add(location, toAdd);
  }
  
  public void popupMenuCanceled(PopupMenuEvent e) {
    inserting = false;
    removeDropPanelFromCurrentComponent();
  }
  
  public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    inserting = false;
    removeDropPanelFromCurrentComponent();
  }
  

  public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
  
  protected boolean isValidDrop(Element current, Element toCheck)
  {
    if (toCheck == current) {
      return false;
    }
    if (current.getParent() != null) {
      return isValidDrop(current.getParent(), toCheck);
    }
    return true;
  }
  
  public abstract void drop(DropTargetDropEvent paramDropTargetDropEvent);
  
  protected int getLastElementLocation() {
    return componentElements.size();
  }
  
  protected boolean isRecursive(Element toDrop) {
    Element potentialParent = null;
    if ((toDrop instanceof CallToUserDefinedResponse)) {
      potentialParent = userDefinedResponse.getUserDefinedResponseValue();
    }
    else if ((toDrop instanceof CallToUserDefinedQuestion)) {
      potentialParent = userDefinedQuestion.getUserDefinedQuestionValue();
    }
    if (potentialParent != null) {
      return isMyParent(componentElements.getOwner(), potentialParent);
    }
    return false;
  }
  
  protected boolean canFindOwner(Element lookingFor, Element current) {
    if (current == null) {
      return false;
    }
    if (lookingFor == current) {
      return true;
    }
    
    return canFindOwner(lookingFor, current.getParent());
  }
  
  protected boolean checkLoop(Element e)
  {
    if ((e instanceof Variable)) {
      Variable v = (Variable)e;
      if (((v.getParent() instanceof edu.cmu.cs.stage3.alice.core.response.ForEach)) || ((v.getParent() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach))) {
        if (canFindOwner(v.getParent(), componentElements.getOwner())) {
          return true;
        }
        
        return false;
      }
    }
    
    return true;
  }
  
  protected void performDrop(Element toDrop, DropTargetDropEvent dtde) {
    if (isRecursive(toDrop)) {
      Object[] options = { Messages.getString("Yes__I_understand_what_I_am_doing_"), 
        Messages.getString("No__I_made_this_call_accidentally_") };
      int recursionReturn = edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog(
        Messages.getString("The_code_you_have_just_dropped_in_creates_a__recursive_method_call___We_recommend_that_you_understand_n") + 
        Messages.getString("what_recursion_is_before_making_a_call_like_this___Are_you_sure_you_want_to_do_this_"), Messages.getString("Recursion_Warning"), 0, 2, null, options, options[1]);
      if (recursionReturn != 0) {
        return;
      }
    }
    if (authoringTool != null) {
      authoringTool.getUndoRedoStack().startCompound();
    }
    
    int dropPanelLocationTemp = getInsertLocation(SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), this));
    if (isInverted()) {
      if (dropPanelLocationTemp == -1) {
        dropPanelLocationTemp = 0;
      }
      else {
        dropPanelLocationTemp = componentElements.size() - dropPanelLocationTemp;
      }
    }
    inserting = false;
    boolean isCopy = false;
    Component sourceComponent = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentDragComponent();
    removeDropPanelFromCurrentComponent();
    if ((dtde.getDropAction() & 0x1) > 0)
    {
      toDrop = toDrop.HACK_createCopy(null, null, dropPanelLocationTemp, null, componentElements.getOwner());
      isCopy = true;
    }
    insertingElement = true;
    boolean alreadyDone = false;
    if ((!isCopy) && (componentElements.contains(toDrop)))
    {
      int oldIndex = componentElements.indexOf(toDrop);
      if ((dropPanelLocationTemp <= -1) || (dropPanelLocationTemp >= getLastElementLocation())) {
        if (componentElements.size() == 0) {
          dropPanelLocationTemp = 0;
        }
        else {
          dropPanelLocationTemp = getLastElementLocation() - 1;
        }
        
      }
      else if (dropPanelLocationTemp > oldIndex) {
        dropPanelLocationTemp--;
      }
      
      int dif = Math.abs(oldIndex - dropPanelLocationTemp);
      if (dif > 0) {
        componentElements.shift(oldIndex, dropPanelLocationTemp);
      }
      alreadyDone = true;
    }
    if (!alreadyDone) {
      unhook(toDrop);
      if ((dropPanelLocationTemp >= -1) && (dropPanelLocationTemp <= getLastElementLocation())) {
        addToElement(toDrop, componentElements, dropPanelLocationTemp);
      }
      else {
        addToElement(toDrop, componentElements, -1);
      }
    }
    insertingElement = false;
    if (authoringTool != null) {
      authoringTool.getUndoRedoStack().stopCompound();
    }
  }
  
  public void dropActionChanged(DropTargetDragEvent dtde) {
    dtde.acceptDrag(dtde.getDropAction());
  }
  
  public void dragExit(java.awt.dnd.DropTargetEvent dte) {
    HACK_timer.restart();
    HACK_timer.start();
    timers.add(HACK_timer);
  }
}
