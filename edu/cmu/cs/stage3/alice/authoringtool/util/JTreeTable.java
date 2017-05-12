package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;














































public class JTreeTable
  extends JTable
{
  protected TreeTableCellRenderer tree;
  
  public JTreeTable(TreeTableModel treeTableModel)
  {
    tree = new TreeTableCellRenderer(treeTableModel);
    

    super.setModel(new TreeTableModelAdapter(treeTableModel, tree));
    

    tree.setSelectionModel(new DefaultTreeSelectionModel() {});
    tree.setRowHeight(getRowHeight());
    

    setDefaultRenderer(TreeTableModel.class, tree);
    setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());
    
    setShowGrid(false);
    setIntercellSpacing(new Dimension(0, 0));
  }
  
  public JTree getTree() {
    return tree;
  }
  
  public Object getNodeAtPoint(Point point) {
    return ((TreeTableModelAdapter)getModel()).nodeForRow(rowAtPoint(point));
  }
  





  public int getEditingRow()
  {
    return getColumnClass(editingColumn) == TreeTableModel.class ? -1 : editingRow;
  }
  
  protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed)
  {
    return false;
  }
  
  public class TreeTableCellRenderer
    extends JTree implements TableCellRenderer
  {
    protected int visibleRow;
    
    public TreeTableCellRenderer(TreeModel model)
    {
      super();
    }
    
    public void setBounds(int x, int y, int w, int h) {
      super.setBounds(x, 0, w, getHeight());
    }
    
    public void paint(Graphics g) {
      g.translate(0, -visibleRow * getRowHeight());
      super.paint(g);
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (isSelected) {
        setBackground(table.getSelectionBackground());
      } else {
        setBackground(table.getBackground());
      }
      visibleRow = row;
      return this;
    }
  }
  
  public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor { public TreeTableCellEditor() {}
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) { return tree; }
    
    public Object getCellEditorValue()
    {
      return null;
    }
  }
  
  public void editingCanceled(ChangeEvent ev) {
    super.editingCanceled(ev);
  }
  
  public void removeEditor() {
    super.removeEditor();
  }
}
