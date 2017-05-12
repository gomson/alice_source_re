package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementTreeCellEditor;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementTreeCellRenderer;
import edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Group;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.tree.TreePath;

public class WorldTreeComponent extends JPanel
{
  protected World world;
  protected Element bogusRoot = new edu.cmu.cs.stage3.alice.core.Transformable();
  protected WorldTreeModel worldTreeModel = new WorldTreeModel();
  protected ElementTreeCellRenderer cellRenderer = new ElementTreeCellRenderer();
  protected ElementTreeCellEditor cellEditor = new ElementTreeCellEditor();
  protected Element selectedElement;
  protected edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringtoolConfig;
  protected DragSource dragSource = new DragSource();
  protected java.util.HashSet elementSelectionListeners = new java.util.HashSet();
  protected WorldTreeDropTargetListener worldTreeDropTargetListener = new WorldTreeDropTargetListener();
  protected AuthoringTool authoringTool;
  
  public WorldTreeComponent(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
    modelInit();
    jbInit();
    treeInit();
    dndInit();
    selectionInit();
  }
  
  private void modelInit() {
    worldTreeModel.setRoot(bogusRoot);
  }
  
  private void treeInit() {
    worldTree.setModel(worldTreeModel);
    worldTree.addTreeSelectionListener(worldSelectionListener);
    worldTree.putClientProperty("JTree.lineStyle", "Angled");
    worldTree.setCellEditor(cellEditor);
    worldTree.setCellRenderer(cellRenderer);
    CustomTreeUI treeUI = new CustomTreeUI();
    worldTree.setUI(treeUI);
    treeUI.setExpandedIcon(AuthoringToolResources.getIconForString("minus"));
    treeUI.setCollapsedIcon(AuthoringToolResources.getIconForString("plus"));
    worldTree.setEditable(true);
    worldTree.addMouseListener(worldTreeMouseListener);
    worldTree.addTreeWillExpandListener(
      new javax.swing.event.TreeWillExpandListener() {
        public void treeWillCollapse(TreeExpansionEvent ev) throws javax.swing.tree.ExpandVetoException {
          if (ev.getPath().getLastPathComponent() == worldTreeModel.getRoot()) {
            throw new javax.swing.tree.ExpandVetoException(ev);
          }
        }
        

        public void treeWillExpand(TreeExpansionEvent ev) {}
      });
    treeScrollPane.setBorder(null);
  }
  
  private void dndInit() {
    dragSource.createDefaultDragGestureRecognizer(worldTree, 1073741827, new ElementTreeDragGestureListener(worldTree));
    worldTree.setDropTarget(new java.awt.dnd.DropTarget(worldTree, worldTreeDropTargetListener));
  }
  
  private void selectionInit() {
    authoringTool.addElementSelectionListener(
      new edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener() {
        public void elementSelected(Element element) {
          setSelectedElement(element);
        }
      });
  }
  
  public void startListening(AuthoringTool authoringTool)
  {
    authoringTool.addAuthoringToolStateListener(worldTreeModel);
  }
  
  public World getWorld() {
    return world;
  }
  
  public void setWorld(World world) {
    this.world = world;
    if (world == null) {
      worldTreeModel.setRoot(bogusRoot);
      setCurrentScope(bogusRoot);
    } else {
      worldTreeModel.setRoot(world);
      setCurrentScope(world);
    }
    

    revalidate();
    repaint();
  }
  
  public void setCurrentScope(Element element) {
    worldTreeModel.setCurrentScope(element);
  }
  
  public void setSelectedElement(Element element) {
    if (element != selectedElement) {
      selectedElement = element;
      if (element == null) {
        worldTree.clearSelection();
      } else {
        Object[] path;
        for (;;) { path = worldTreeModel.getPath(element);
          if ((path == null) || (path.length == 0))
            try {
              Thread.sleep(10L);
            } catch (InterruptedException ie) {
              return;
            }
        }
        TreePath selectedPath = new TreePath(path);
        worldTree.setSelectionPath(selectedPath);
        worldTree.scrollPathToVisible(selectedPath);
      }
    }
  }
  


  public Element getSelectedElement()
  {
    return selectedElement;
  }
  
  private TreeSelectionListener worldSelectionListener = new TreeSelectionListener() {
    public void valueChanged(TreeSelectionEvent ev) {
      TreePath path = ev.getNewLeadSelectionPath();
      if (path != null) {
        Object o = path.getLastPathComponent();
        if ((o instanceof Element)) {
          selectedElement = ((Element)o);
          if (selectedElement != worldTreeModel.HACK_getOriginalRoot())
          {

            authoringTool.setSelectedElement(selectedElement);
          }
        }
      } else {
        setSelectedElement((Element)worldTreeModel.getRoot());
      }
    }
  };
  

  public class ElementTreeDragGestureListener
    implements java.awt.dnd.DragGestureListener
  {
    protected JTree tree;
    protected WorldTreeModel treeModel;
    
    public ElementTreeDragGestureListener(JTree tree)
    {
      this.tree = tree;
      treeModel = ((WorldTreeModel)tree.getModel());
    }
    
    public void dragGestureRecognized(DragGestureEvent dge) {
      DnDManager.fireDragGestureRecognized(dge);
      
      if (tree.isEditing()) {
        return;
      }
      









      Point p = dge.getDragOrigin();
      TreePath path = tree.getPathForLocation((int)p.getX(), (int)p.getY());
      worldTree.setSelectionPath(path);
      if (path != null) {
        Object element = path.getLastPathComponent();
        if (((element instanceof Element)) && 
          (treeModel.isElementInScope((Element)element))) {
          Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory.createTransferable(element);
          try {
            dragSource.startDrag(dge, DragSource.DefaultMoveDrop, transferable, DnDManager.getInternalListener());
            DnDManager.fireDragStarted(transferable, WorldTreeComponent.this);
          } catch (InvalidDnDOperationException e) { e.printStackTrace();
          }
        }
      }
    }
  }
  
  public class WorldTreeDropTargetListener implements java.awt.dnd.DropTargetListener { public WorldTreeDropTargetListener() {}
    
    protected boolean checkDrag(DropTargetDragEvent dtde) { if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, AuthoringToolResources.getReferenceFlavorForClass(Model.class))) {
        dtde.acceptDrag(2);
        return true;
      }
      
      dtde.rejectDrag();
      return false;
    }
    
    public void dragEnter(DropTargetDragEvent dtde) {
      if (checkDrag(dtde)) {
        worldTree.setDropLinesActive(true);
      }
    }
    
    public void dragOver(DropTargetDragEvent dtde) {
      if (checkDrag(dtde)) {
        worldTree.setCursorLocation(dtde.getLocation());
        
        TreePath parentPath = worldTree.getParentPath();
        Element parent = (Element)parentPath.getLastPathComponent();
        
        Transferable transferable = DnDManager.getCurrentTransferable();
        Element child = null;
        if ((transferable != null) && (AuthoringToolResources.safeIsDataFlavorSupported(transferable, AuthoringToolResources.getReferenceFlavorForClass(Model.class)))) {
          try {
            child = (Element)transferable.getTransferData(AuthoringToolResources.getReferenceFlavorForClass(Model.class));
          } catch (Exception e) {
            AuthoringTool.showErrorDialog("Error encountered extracting drop transferable.", e);
          }
        }
        
        if (child != null) {
          if (isAcceptableDrop(parent, child))
          {
            worldTree.setShowDropLines(true);

          }
          else
          {

            worldTree.setShowDropLines(false);
          }
        } else {
          worldTree.setShowDropLines(true);
        }
      }
    }
    
    public void dropActionChanged(DropTargetDragEvent dtde) {
      checkDrag(dtde);
    }
    
    public void dragExit(java.awt.dnd.DropTargetEvent dte) {
      worldTree.setDropLinesActive(false);
    }
    
    public void drop(DropTargetDropEvent dtde) {
      boolean succeeded = true;
      
      worldTree.setCursorLocation(dtde.getLocation());
      try
      {
        if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, AuthoringToolResources.getReferenceFlavorForClass(Model.class))) {
          Transferable transferable = dtde.getTransferable();
          final Model model = (Model)transferable.getTransferData(AuthoringToolResources.getReferenceFlavorForClass(Model.class));
          
          TreePath parentPath = worldTree.getParentPath();
          final Element parent = (Element)parentPath.getLastPathComponent();
          
          if (isAcceptableDrop(parent, model)) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                try {
                  WorldTreeComponent.WorldTreeDropTargetListener.this.insertChild(parent, model, worldTree.getParentToPredecessorPaths());
                } catch (Throwable t) {
                  AuthoringTool.showErrorDialog("Error moving child.", t);
                }
              }
            });
            dtde.acceptDrop(2);
            succeeded = true;
          } else {
            dtde.rejectDrop();
            succeeded = false;
          }
        } else {
          dtde.rejectDrop();
          succeeded = false;
        }
      } catch (java.awt.datatransfer.UnsupportedFlavorException e) {
        AuthoringTool.showErrorDialog("Drop didn't work: bad flavor", e);
        succeeded = false;
      } catch (java.io.IOException e) {
        AuthoringTool.showErrorDialog("Drop didn't work: IOException", e);
        succeeded = false;
      } catch (Throwable t) {
        AuthoringTool.showErrorDialog("Drop didn't work.", t);
        succeeded = false;
      }
      
      worldTree.setDropLinesActive(false);
      dtde.dropComplete(succeeded);
    }
    
    private boolean isAcceptableDrop(Element parent, Element child) {
      if ((parent instanceof Group)) {
        Group group = (Group)parent;
        Class childValueClass = child.getClass();
        if ((child instanceof Expression)) {
          childValueClass = ((Expression)child).getValueClass();
        }
        if (!valueClass.getClassValue().isAssignableFrom(childValueClass)) {
          return false;
        }
      }
      
      if (((parent instanceof Group)) || ((parent instanceof World))) {
        if (((parent instanceof World)) && ((child.getParent() instanceof World)))
        {
          return true; }
        if (((parent instanceof Group)) && ((child.getParent() instanceof Group)))
        {
          if (!name.getValue().equals(getParentname.getValue())) {
            for (int i = 0; i < parent.getChildCount(); i++) {
              if (getChildAtname.getValue().equals(name.getValue()))
                return false;
            }
          } else
            return true;
        } else if (child.getParent() == null)
        {
          return true;
        }
        

        for (int i = 0; i < parent.getChildCount(); i++) {
          if (getChildAtname.getValue().equals(name.getValue()))
            return false;
        }
        return true;
      }
      
      return false;
    }
    
    private void insertChild(Element parent, Element child, TreePath[] parentToPredecessor) {
      int index = 0;
      TreePath parentPath = parentToPredecessor[0];
      TreePath predecessorPath = parentToPredecessor[(parentToPredecessor.length - 1)];
      ObjectArrayProperty oap = null;
      
      if ((parent instanceof World)) {
        oap = sandboxes;
      } else if ((parent instanceof Group)) {
        oap = values;
      }
      
      if (predecessorPath == parentPath) {
        index = 0;
      } else {
        int i = parentToPredecessor.length - 1;
        while ((i >= 0) && (parentToPredecessor[i] != null) && (!parentToPredecessor[i].getParentPath().equals(parentPath))) {
          i--;
        }
        Element predecessor = (Element)parentToPredecessor[i].getLastPathComponent();
        if (oap != null) {
          index = 1 + oap.indexOf(predecessor);
        }
      }
      if ((oap != null) && (isAcceptableDrop(parent, child))) {
        int currentIndex = oap.indexOf(child);
        if ((currentIndex > -1) && (currentIndex < index)) {
          index--;
        }
        
        authoringTool.getUndoRedoStack().startCompound();
        

        child.removeFromParentsProperties();
        
        child.setParent(parent);
        oap.add(index, child);
        
        authoringTool.getUndoRedoStack().stopCompound();
      }
    }
  }
  
  public class CustomTreeUI extends javax.swing.plaf.metal.MetalTreeUI {
    public CustomTreeUI() {}
    
    protected java.awt.event.MouseListener createMouseListener() { return new CustomMouseHandler(); }
    
    protected boolean startEditing(TreePath path, MouseEvent ev)
    {
      boolean result = super.startEditing(path, ev);
      if (result) {
        cellEditor.selectText();
      }
      return result; }
    
    public class CustomMouseHandler extends java.awt.event.MouseAdapter { protected long pressTime;
      protected Point pressPoint;
      
      public CustomMouseHandler() {}
      
      protected long clickDelay = 300L;
      protected double clickDistance = 8.0D;
      
      public void mousePressed(MouseEvent ev) {
        pressTime = ev.getWhen();
        pressPoint = ev.getPoint();
        if ((tree != null) && (tree.isEnabled())) {
          tree.requestFocus();
          TreePath path = getClosestPathForLocation(tree, ev.getX(), ev.getY());
          
          if (path != null) {
            Rectangle bounds = getPathBounds(tree, path);
            if (ev.getY() > y + height) {
              return;
            }
            if (javax.swing.SwingUtilities.isLeftMouseButton(ev)) {
              checkForClickInExpandControl(path, ev.getX(), ev.getY());
            }
          }
        }
      }
      
      public void mouseReleased(MouseEvent ev) {
        if ((tree != null) && (tree.isEnabled())) {
          tree.requestFocus();
          TreePath path = getClosestPathForLocation(tree, ev.getX(), ev.getY());
          
          if (path != null) {
            Element element = (Element)path.getLastPathComponent();
            boolean elementInScope = ((WorldTreeModel)tree.getModel()).isElementInScope(element);
            
            if (elementInScope) {
              Rectangle bounds = getPathBounds(tree, path);
              if (ev.getY() > y + height) {
                return;
              }
              
              int x = ev.getX();
              if ((x > x) && (x <= x + width)) {
                if ((isClick(ev)) && 
                  (startEditing(path, ev))) {
                  return;
                }
                

                selectPathForEvent(path, ev);
              }
            }
          }
        }
      }
      
      protected boolean isClick(MouseEvent ev) {
        if (ev.getClickCount() > 1) {
          return true;
        }
        
        long time = ev.getWhen();
        Point p = ev.getPoint();
        
        if (time - pressTime <= clickDelay) {
          double dx = p.getX() - pressPoint.getX();
          double dy = p.getY() - pressPoint.getY();
          double dist = Math.sqrt(dx * dx + dy * dy);
          if (dist <= clickDistance) {
            return true;
          }
        }
        
        return false;
      }
    }
  }
  




  protected final java.awt.event.MouseListener worldTreeMouseListener = new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter()
  {
    protected Vector defaultStructure;
    
    protected void popupResponse(MouseEvent ev) {
      JTree tree = (JTree)ev.getSource();
      TreePath path = tree.getPathForLocation(ev.getX(), ev.getY());
      if (path != null) {
        Object node = path.getLastPathComponent();
        if ((node instanceof Element)) {
          JPopupMenu popup = createPopup((Element)node, path);
          if (popup != null) {
            popup.show(ev.getComponent(), ev.getX(), ev.getY());
          }
        }
      }
      else {
        edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu(getDefaultStructure(), ev.getComponent(), ev.getX(), ev.getY());
      }
    }
    
    private JPopupMenu createPopup(Element element, TreePath path) {
      Vector structure = edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.getDefaultStructure(element, worldTreeModel.isElementInScope(element), authoringTool, worldTree, path);
      return edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.makeElementPopupMenu(element, structure);
    }
    
    protected Vector getDefaultStructure() {
      if (defaultStructure == null) {
        defaultStructure = new Vector();
        defaultStructure.add(new edu.cmu.cs.stage3.util.StringObjectPair("create new group", new Runnable() {
          public void run() {
            Group newGroup = new Group();
            String name = AuthoringToolResources.getNameForNewChild("Group", world);
            name.set(name);
            valueClass.set(Model.class);
            world.addChild(newGroup);
            world.groups.add(newGroup);
          }
        }));
      }
      return defaultStructure;
    }
  };
  




  BorderLayout borderLayout2 = new BorderLayout();
  TitledBorder titledBorder1;
  JScrollPane treeScrollPane = new JScrollPane();
  JPanel treePanel = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  WorldTree worldTree = new WorldTree();
  
  public void jbInit()
  {
    titledBorder1 = new TitledBorder("");
    
    setLayout(borderLayout2);
    if (authoringtoolConfig != null) {
      int fontSize = Integer.parseInt(authoringtoolConfig.getValue("fontSize"));
      worldTree.setFont(new java.awt.Font("Dialog", 0, (int)(14 * fontSize / 12.0D)));
    } else {
      worldTree.setFont(new java.awt.Font("Dialog", 0, 14));
    }
    setMinimumSize(new java.awt.Dimension(5, 0));
    treePanel.setLayout(borderLayout4);
    worldTree.setBorder(new JScrollPane().getViewportBorder());
    

    add(treeScrollPane, "Center");
    treeScrollPane.getViewport().add(treePanel, null);
    treePanel.add(worldTree, "Center");
  }
  
  public static class HackBorder extends javax.swing.border.AbstractBorder { public HackBorder() {}
    
    private static final Insets insets = new Insets(1, 1, 2, 2);
    
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
      g.translate(x, y);
      
      g.setColor(MetalLookAndFeel.getControlDarkShadow());
      g.drawRect(0, 0, w - 2, h - 2);
      g.setColor(MetalLookAndFeel.getControlHighlight());
      
      g.drawLine(w - 1, 1, w - 1, h - 1);
      g.drawLine(1, h - 1, w - 1, h - 1);
      
      g.setColor(MetalLookAndFeel.getControl());
      g.drawLine(w - 2, 2, w - 2, 2);
      g.drawLine(1, h - 2, 1, h - 2);
      
      g.translate(-x, -y);
    }
    
    public Insets getBorderInsets(Component c) {
      return insets;
    }
  }
}
