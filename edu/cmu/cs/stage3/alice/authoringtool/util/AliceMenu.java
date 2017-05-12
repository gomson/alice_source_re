package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.swing.AbstractButton;
import javax.swing.AbstractButton.AccessibleAbstractButton;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenu.WinListener;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuItemUI;
import javax.swing.plaf.PopupMenuUI;


































public class AliceMenu
  extends JMenu
  implements Accessible, MenuElement
{
  private static final String uiClassID = "MenuUI";
  private AlicePopupMenu popupMenu;
  private ChangeListener menuChangeListener = null;
  





  private MenuEvent menuEvent = null;
  





  private static Hashtable listenerRegistry = null;
  


  private int delay;
  


  private boolean receivedKeyPressed;
  


  private static final boolean TRACE = false;
  

  private static final boolean VERBOSE = false;
  

  private static final boolean DEBUG = false;
  

  protected JMenu.WinListener popupListener;
  


  public AliceMenu()
  {
    this("");
  }
  




  public AliceMenu(String s)
  {
    super(s);
  }
  





  public AliceMenu(Action a)
  {
    this();
    setAction(a);
  }
  





  public AliceMenu(String s, boolean b)
  {
    this(s);
  }
  






  public void updateUI()
  {
    setUI((MenuItemUI)UIManager.getUI(this));
    
    if (popupMenu != null) {
      popupMenu.setUI((PopupMenuUI)UIManager.getUI(popupMenu));
    }
  }
  






  public String getUIClassID()
  {
    return "MenuUI";
  }
  















  public void setModel(ButtonModel newModel)
  {
    ButtonModel oldModel = getModel();
    super.setModel(newModel);
    
    if ((oldModel != null) && (menuChangeListener != null)) {
      oldModel.removeChangeListener(menuChangeListener);
      menuChangeListener = null;
    }
    
    model = newModel;
    
    if (newModel != null) {
      menuChangeListener = createMenuChangeListener();
      newModel.addChangeListener(menuChangeListener);
    }
  }
  




  public boolean isSelected()
  {
    return getModel().isSelected();
  }
  









  public void setSelected(boolean b)
  {
    ButtonModel model = getModel();
    boolean oldValue = model.isSelected();
    
    if ((accessibleContext != null) && (oldValue != b)) {
      if (b) {
        accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.SELECTED);
      } else {
        accessibleContext.firePropertyChange("AccessibleState", AccessibleState.SELECTED, null);
      }
    }
    if (b != model.isSelected()) {
      getModel().setSelected(b);
    }
  }
  




  public boolean isPopupMenuVisible()
  {
    ensurePopupMenuCreated();
    
    return popupMenu.isVisible();
  }
  










  public void setPopupMenuVisible(boolean b)
  {
    if (!isEnabled()) {
      return;
    }
    





    boolean isVisible = isPopupMenuVisible();
    
    if (b != isVisible) {
      ensurePopupMenuCreated();
      


      if ((b) && (isShowing())) {
        Point p = getPopupMenuOrigin();
        getPopupMenu().show(this, x, y);
      } else {
        getPopupMenu().setVisible(false);
      }
    }
  }
  






  protected Point getPopupMenuOrigin()
  {
    int x = 0;
    int y = 0;
    JPopupMenu pm = getPopupMenu();
    

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    height -= 28;
    Dimension s = getSize();
    Dimension pmSize = pm.getSize();
    


    if (width == 0) {
      pmSize = pm.getPreferredSize();
    }
    
    Point position = getLocationOnScreen();
    Container parent = getParent();
    
    if ((parent instanceof JPopupMenu))
    {

      if (getComponentOrientation().isLeftToRight())
      {

        if (x + width + width < width) {
          x = width;
        } else {
          x = 0 - width;
        }
        

      }
      else if (x < width) {
        x = width;
      } else {
        x = 0 - width;
      }
      

      if (y + height < height) {
        y = 0;
      } else {
        y = height - height;
      }
    }
    else
    {
      if (getComponentOrientation().isLeftToRight())
      {

        if (x + width < width) {
          x = 0;
        } else {
          x = width - width;
        }
        

      }
      else if (x + width < width) {
        x = 0;
      } else {
        x = width - width;
      }
      

      if (y + height + height < height) {
        y = height;
      } else {
        y = 0 - height;
      }
    }
    
    return new Point(x, y);
  }
  












  public int getDelay()
  {
    return delay;
  }
  














  public void setDelay(int d)
  {
    if (d < 0) {
      throw new IllegalArgumentException(Messages.getString("Delay_must_be_a_positive_integer"));
    }
    
    delay = d;
  }
  






  private void ensurePopupMenuCreated()
  {
    if (popupMenu == null) {
      AliceMenu thisMenu = this;
      popupMenu = new AlicePopupMenu();
      popupMenu.setInvoker(this);
      popupListener = createWinListener(popupMenu);
      popupMenu.addPopupMenuListener(new PopupMenuListener()
      {
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
        
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
        
        public void popupMenuCanceled(PopupMenuEvent e)
        {
          fireMenuCanceled();
        }
      });
    }
  }
  





  public void setMenuLocation(int x, int y)
  {
    if (popupMenu != null) {
      popupMenu.setLocation(x, y);
    }
  }
  






  public JMenuItem add(JMenuItem menuItem)
  {
    AccessibleContext ac = menuItem.getAccessibleContext();
    ac.setAccessibleParent(this);
    ensurePopupMenuCreated();
    
    return popupMenu.add(menuItem);
  }
  






  public Component add(Component c)
  {
    if ((c instanceof JComponent)) {
      AccessibleContext ac = ((JComponent)c).getAccessibleContext();
      
      if (ac != null) {
        ac.setAccessibleParent(this);
      }
    }
    
    ensurePopupMenuCreated();
    popupMenu.add(c);
    
    return c;
  }
  









  public Component add(Component c, int index)
  {
    if ((c instanceof JComponent)) {
      AccessibleContext ac = ((JComponent)c).getAccessibleContext();
      
      if (ac != null) {
        ac.setAccessibleParent(this);
      }
    }
    
    ensurePopupMenuCreated();
    popupMenu.add(c, index);
    
    return c;
  }
  





  public JMenuItem add(String s)
  {
    return add(new JMenuItem(s));
  }
  












  public JMenuItem add(Action a)
  {
    JMenuItem mi = createActionComponent(a);
    mi.setAction(a);
    add(mi);
    
    return mi;
  }
  












  protected JMenuItem createActionComponent(Action a)
  {
    JMenuItem mi = new JMenuItem((String)a.getValue("Name"), (Icon)a.getValue("SmallIcon"))
    {
      protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
        PropertyChangeListener pcl = createActionChangeListener(this);
        
        if (pcl == null) {
          pcl = super.createActionPropertyChangeListener(a);
        }
        
        return pcl;
      }
      
    };
    mi.setHorizontalTextPosition(4);
    mi.setVerticalTextPosition(0);
    mi.setEnabled(a.isEnabled());
    
    return mi;
  }
  









  protected PropertyChangeListener createActionChangeListener(JMenuItem b)
  {
    return new ActionChangedListener(b);
  }
  
  private class ActionChangedListener implements PropertyChangeListener
  {
    JMenuItem menuItem;
    
    ActionChangedListener(JMenuItem mi) {
      setTarget(mi);
    }
    
    public void propertyChange(PropertyChangeEvent e) {
      String propertyName = e.getPropertyName();
      
      if (e.getPropertyName().equals("Name")) {
        String text = (String)e.getNewValue();
        menuItem.setText(text);
      } else if (propertyName.equals("enabled")) {
        Boolean enabledState = (Boolean)e.getNewValue();
        menuItem.setEnabled(enabledState.booleanValue());
      } else if (e.getPropertyName().equals("SmallIcon")) {
        Icon icon = (Icon)e.getNewValue();
        menuItem.setIcon(icon);
        menuItem.invalidate();
        menuItem.repaint();
      }
    }
    
    public void setTarget(JMenuItem b) {
      menuItem = b;
    }
  }
  



  public void addSeparator()
  {
    ensurePopupMenuCreated();
    popupMenu.addSeparator();
  }
  









  public void insert(String s, int pos)
  {
    if (pos < 0) {
      throw new IllegalArgumentException(Messages.getString("index_less_than_zero_"));
    }
    
    ensurePopupMenuCreated();
    popupMenu.insert(new JMenuItem(s), pos);
  }
  









  public JMenuItem insert(JMenuItem mi, int pos)
  {
    if (pos < 0) {
      throw new IllegalArgumentException(Messages.getString("index_less_than_zero_"));
    }
    
    AccessibleContext ac = mi.getAccessibleContext();
    ac.setAccessibleParent(this);
    ensurePopupMenuCreated();
    popupMenu.insert(mi, pos);
    
    return mi;
  }
  









  public JMenuItem insert(Action a, int pos)
  {
    if (pos < 0) {
      throw new IllegalArgumentException(Messages.getString("index_less_than_zero_"));
    }
    
    ensurePopupMenuCreated();
    
    JMenuItem mi = new JMenuItem((String)a.getValue("Name"), (Icon)a.getValue("SmallIcon"));
    mi.setHorizontalTextPosition(4);
    mi.setVerticalTextPosition(0);
    mi.setEnabled(a.isEnabled());
    mi.setAction(a);
    popupMenu.insert(mi, pos);
    
    return mi;
  }
  







  public void insertSeparator(int index)
  {
    if (index < 0) {
      throw new IllegalArgumentException(Messages.getString("index_less_than_zero_"));
    }
    
    ensurePopupMenuCreated();
    popupMenu.insert(new JPopupMenu.Separator(), index);
  }
  











  public JMenuItem getItem(int pos)
  {
    if (pos < 0) {
      throw new IllegalArgumentException(Messages.getString("index_less_than_zero_"));
    }
    
    Component c = getMenuComponent(pos);
    
    if ((c instanceof JMenuItem)) {
      JMenuItem mi = (JMenuItem)c;
      
      return mi;
    }
    

    return null;
  }
  






  public int getItemCount()
  {
    return getMenuComponentCount();
  }
  






  public boolean isTearOff()
  {
    throw new Error(Messages.getString("boolean_isTearOff______not_yet_implemented"));
  }
  





  public void remove(JMenuItem item)
  {
    if (popupMenu != null) {
      popupMenu.remove(item);
    }
  }
  







  public void remove(int pos)
  {
    if (pos < 0) {
      throw new IllegalArgumentException(Messages.getString("index_less_than_zero_"));
    }
    if (pos > getItemCount()) {
      throw new IllegalArgumentException(Messages.getString("index_greater_than_the_number_of_items_"));
    }
    if (popupMenu != null) {
      popupMenu.remove(pos);
    }
  }
  




  public void remove(Component c)
  {
    if (popupMenu != null) {
      popupMenu.remove(c);
    }
  }
  


  public void removeAll()
  {
    if (popupMenu != null) {
      popupMenu.removeAll();
    }
  }
  




  public int getMenuComponentCount()
  {
    int componentCount = 0;
    
    if (popupMenu != null) {
      componentCount = popupMenu.getComponentCount();
    }
    
    return componentCount;
  }
  







  public Component getMenuComponent(int n)
  {
    if (popupMenu != null) {
      return popupMenu.getComponent(n);
    }
    
    return null;
  }
  







  public Component[] getMenuComponents()
  {
    if (popupMenu != null) {
      return popupMenu.getComponents();
    }
    
    return new Component[0];
  }
  







  public boolean isTopLevelMenu()
  {
    if ((getParent() instanceof JMenuBar)) {
      return true;
    }
    
    return false;
  }
  








  public boolean isMenuComponent(Component c)
  {
    if (c == this) {
      return true;
    }
    
    if ((c instanceof JPopupMenu)) {
      JPopupMenu comp = (JPopupMenu)c;
      
      if (comp == getPopupMenu()) {
        return true;
      }
    }
    

    int ncomponents = getMenuComponentCount();
    Component[] component = getMenuComponents();
    
    for (int i = 0; i < ncomponents; i++) {
      Component comp = component[i];
      

      if (comp == c) {
        return true;
      }
      

      if ((comp instanceof AliceMenu)) {
        AliceMenu subMenu = (AliceMenu)comp;
        
        if (subMenu.isMenuComponent(c)) {
          return true;
        }
      }
    }
    
    return false;
  }
  






  private Point translateToPopupMenu(Point p)
  {
    return translateToPopupMenu(x, y);
  }
  


  private Point translateToPopupMenu(int x, int y)
  {
    int newY;
    

    int newX;
    
    int newY;
    
    if ((getParent() instanceof JPopupMenu)) {
      int newX = x - getSizewidth;
      newY = y;
    } else {
      newX = x;
      newY = y - getSizeheight;
    }
    
    return new Point(newX, newY);
  }
  



  public JPopupMenu getPopupMenu()
  {
    ensurePopupMenuCreated();
    
    return popupMenu;
  }
  




  public void addMenuListener(MenuListener l)
  {
    listenerList.add(MenuListener.class, l);
  }
  




  public void removeMenuListener(MenuListener l)
  {
    listenerList.remove(MenuListener.class, l);
  }
  













  protected void fireMenuSelected()
  {
    Object[] listeners = listenerList.getListenerList();
    


    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == MenuListener.class) {
        if (listeners[(i + 1)] == null) {
          throw new Error(getText() + " " + Messages.getString("has_a_NULL_Listener_") + i);
        }
        

        if (menuEvent == null) {
          menuEvent = new MenuEvent(this);
        }
        
        ((MenuListener)listeners[(i + 1)]).menuSelected(menuEvent);
      }
    }
  }
  














  protected void fireMenuDeselected()
  {
    Object[] listeners = listenerList.getListenerList();
    


    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == MenuListener.class) {
        if (listeners[(i + 1)] == null) {
          throw new Error(getText() + " " + Messages.getString("has_a_NULL_Listener_") + i);
        }
        

        if (menuEvent == null) {
          menuEvent = new MenuEvent(this);
        }
        
        ((MenuListener)listeners[(i + 1)]).menuDeselected(menuEvent);
      }
    }
  }
  














  protected void fireMenuCanceled()
  {
    Object[] listeners = listenerList.getListenerList();
    


    for (int i = listeners.length - 2; i >= 0; i -= 2)
      if (listeners[i] == MenuListener.class) {
        if (listeners[(i + 1)] == null) {
          throw new Error(getText() + " " + Messages.getString("has_a_NULL_Listener_") + i);
        }
        

        if (menuEvent == null) {
          menuEvent = new MenuEvent(this);
        }
        
        ((MenuListener)listeners[(i + 1)]).menuCanceled(menuEvent);
      }
  }
  
  class MenuChangeListener implements ChangeListener, Serializable {
    MenuChangeListener() {}
    
    boolean isSelected = false;
    
    public void stateChanged(ChangeEvent e) {
      ButtonModel model = (ButtonModel)e.getSource();
      boolean modelSelected = model.isSelected();
      
      if (modelSelected != isSelected) {
        if (modelSelected) {
          fireMenuSelected();
        } else {
          fireMenuDeselected();
        }
        
        isSelected = modelSelected;
      }
    }
  }
  
  private ChangeListener createMenuChangeListener()
  {
    return new MenuChangeListener();
  }
  







  protected JMenu.WinListener createWinListener(JPopupMenu p)
  {
    return new JMenu.WinListener(this, p);
  }
  







































  public void menuSelectionChanged(boolean isIncluded)
  {
    setSelected(isIncluded);
  }
  









  public MenuElement[] getSubElements()
  {
    if (popupMenu == null) {
      return new MenuElement[0];
    }
    MenuElement[] result = new MenuElement[1];
    result[0] = popupMenu;
    
    return result;
  }
  







  public Component getComponent()
  {
    return this;
  }
  













  public void setAccelerator(KeyStroke keyStroke)
  {
    throw new Error(Messages.getString("setAccelerator___is_not_defined_for_AliceMenu___Use_setMnemonic___instead_"));
  }
  







  protected void processFocusEvent(FocusEvent e)
  {
    switch (e.getID()) {
    case 1005: 
      receivedKeyPressed = false;
      break;
    }
    
    


    super.processFocusEvent(e);
  }
  










  protected void processKeyEvent(KeyEvent e)
  {
    boolean createMenuEvent = false;
    
    switch (e.getID()) {
    case 401: 
      if (isSelected()) {
        createMenuEvent = this.receivedKeyPressed = 1;
      } else {
        receivedKeyPressed = false;
      }
      
      break;
    
    case 402: 
      if (receivedKeyPressed) {
        receivedKeyPressed = false;
        createMenuEvent = true;
      }
      
      break;
    
    default: 
      createMenuEvent = receivedKeyPressed;
    }
    
    if (createMenuEvent) {
      MenuSelectionManager.defaultManager().processKeyEvent(e);
    }
    if (e.isConsumed()) {
      return;
    }
    

















    if ((isSelected()) && ((e.getKeyCode() == 9) || (e.getKeyChar() == '\t'))) {
      if ((Boolean)UIManager.get("Menu.consumesTabs") == Boolean.TRUE) {
        e.consume();
        
        return;
      }
      MenuSelectionManager.defaultManager().clearSelectedPath();
    }
    

    super.processKeyEvent(e);
  }
  





  public void doClick(int pressTime)
  {
    MenuElement[] me = buildMenuElementArray(this);
    MenuSelectionManager.defaultManager().setSelectedPath(me);
  }
  




  private MenuElement[] buildMenuElementArray(AliceMenu leaf)
  {
    Vector elements = new Vector();
    Component current = leaf.getPopupMenu();
    
    do
    {
      for (;;)
      {
        if ((current instanceof JPopupMenu)) {
          JPopupMenu pop = (JPopupMenu)current;
          elements.insertElementAt(pop, 0);
          current = pop.getInvoker();
        } else { if (!(current instanceof AliceMenu)) break;
          AliceMenu menu = (AliceMenu)current;
          elements.insertElementAt(menu, 0);
          current = menu.getParent();
        } } } while (!(current instanceof JMenuBar));
    JMenuBar bar = (JMenuBar)current;
    elements.insertElementAt(bar, 0);
    
    MenuElement[] me = new MenuElement[elements.size()];
    elements.copyInto(me);
    
    return me;
  }
  





  private void writeObject(ObjectOutputStream s)
    throws IOException
  {
    s.defaultWriteObject();
    
    if ((ui != null) && (getUIClassID().equals("MenuUI"))) {
      ui.installUI(this);
    }
  }
  








  protected String paramString()
  {
    return super.paramString();
  }
  











  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJMenu();
    }
    
    return accessibleContext;
  }
  







  protected class AccessibleJMenu
    extends AbstractButton.AccessibleAbstractButton
    implements ChangeListener, AccessibleSelection
  {
    public AccessibleJMenu()
    {
      super();
      addChangeListener(this);
    }
    
    public void stateChanged(ChangeEvent e) {
      firePropertyChange("AccessibleVisibleData", new Boolean(false), new Boolean(true));
    }
    







    public int getAccessibleChildrenCount()
    {
      Component[] children = getMenuComponents();
      int count = 0;
      
      for (int j = 0; j < children.length; j++) {
        if ((children[j] instanceof Accessible)) {
          count++;
        }
      }
      
      return count;
    }
    





    public Accessible getAccessibleChild(int i)
    {
      Component[] children = getMenuComponents();
      int count = 0;
      
      for (int j = 0; j < children.length; j++) {
        if ((children[j] instanceof Accessible)) {
          if (count == i) {
            if ((children[j] instanceof JComponent))
            {




              AccessibleContext ac = ((Accessible)children[j]).getAccessibleContext();
              ac.setAccessibleParent(AliceMenu.this);
            }
            
            return (Accessible)children[j];
          }
          count++;
        }
      }
      

      return null;
    }
    






    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.MENU;
    }
    







    public AccessibleSelection getAccessibleSelection()
    {
      return this;
    }
    



    public int getAccessibleSelectionCount()
    {
      MenuElement[] me = MenuSelectionManager.defaultManager().getSelectedPath();
      
      if (me != null) {
        for (int i = 0; i < me.length; i++) {
          if ((me[i] == AliceMenu.this) && 
            (i + 1 < me.length)) {
            return 1;
          }
        }
      }
      

      return 0;
    }
    






    public Accessible getAccessibleSelection(int i)
    {
      if ((i < 0) || (i >= getItemCount())) {
        return null;
      }
      
      MenuElement[] me = MenuSelectionManager.defaultManager().getSelectedPath();
      
      if (me != null) {
        for (int j = 0; j < me.length; j++) {
          if (me[j] == AliceMenu.this)
          {
            do
            {

              if ((me[j] instanceof JMenuItem)) {
                return (Accessible)me[j];
              }
              j++; } while (j < me.length);
          }
        }
      }
      




      return null;
    }
    








    public boolean isAccessibleChildSelected(int i)
    {
      MenuElement[] me = MenuSelectionManager.defaultManager().getSelectedPath();
      
      if (me != null) {
        JMenuItem mi = getItem(i);
        
        for (int j = 0; j < me.length; j++) {
          if (me[j] == mi) {
            return true;
          }
        }
      }
      
      return false;
    }
    









    public void addAccessibleSelection(int i)
    {
      if ((i < 0) || (i >= getItemCount())) {
        return;
      }
      
      JMenuItem mi = getItem(i);
      
      if (mi != null) {
        if ((mi instanceof AliceMenu)) {
          MenuElement[] me = AliceMenu.this.buildMenuElementArray((AliceMenu)mi);
          MenuSelectionManager.defaultManager().setSelectedPath(me);
        } else {
          mi.doClick();
          MenuSelectionManager.defaultManager().setSelectedPath(null);
        }
      }
    }
    





    public void removeAccessibleSelection(int i)
    {
      if ((i < 0) || (i >= getItemCount())) {
        return;
      }
      
      JMenuItem mi = getItem(i);
      
      if ((mi != null) && ((mi instanceof AliceMenu)) && 
        (((AliceMenu)mi).isSelected())) {
        MenuElement[] old = MenuSelectionManager.defaultManager().getSelectedPath();
        MenuElement[] me = new MenuElement[old.length - 2];
        
        for (int j = 0; j < old.length - 2; j++) {
          me[j] = old[j];
        }
        
        MenuSelectionManager.defaultManager().setSelectedPath(me);
      }
    }
    






    public void clearAccessibleSelection()
    {
      MenuElement[] old = MenuSelectionManager.defaultManager().getSelectedPath();
      
      if (old != null) {
        for (int j = 0; j < old.length; j++) {
          if (old[j] == AliceMenu.this) {
            MenuElement[] me = new MenuElement[j + 1];
            System.arraycopy(old, 0, me, 0, j);
            me[j] = getPopupMenu();
            MenuSelectionManager.defaultManager().setSelectedPath(me);
          }
        }
      }
    }
    
    public void selectAllAccessibleSelection() {}
  }
}
