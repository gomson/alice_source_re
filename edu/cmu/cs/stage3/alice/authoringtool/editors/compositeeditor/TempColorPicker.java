package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.response.Comment;
import edu.cmu.cs.stage3.alice.core.response.DoInOrder;
import edu.cmu.cs.stage3.alice.core.response.DoTogether;
import edu.cmu.cs.stage3.alice.core.response.ForEachInOrder;
import edu.cmu.cs.stage3.alice.core.response.ForEachTogether;
import edu.cmu.cs.stage3.alice.core.response.IfElseInOrder;
import edu.cmu.cs.stage3.alice.core.response.LoopNInOrder;
import edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;





public class TempColorPicker
  extends JPanel
{
  protected CompositeElementEditor editor;
  
  private class TileColorPicker
    extends JPanel
  {
    public Color toChange;
    public Color foregroundToChange;
    public JLabel tile;
    public JButton openPicker;
    public JButton openPicker2;
    public String nameKey;
    public Object classKey;
    public String foregroundNameKey;
    
    public TileColorPicker(String nameKey, Object classKey)
    {
      toChange = AuthoringToolResources.getColor(nameKey);
      this.nameKey = nameKey;
      this.classKey = classKey;
      String name = AuthoringToolResources.getReprForValue(classKey);
      tile = new JLabel(name);
      tile.setOpaque(true);
      tile.setBackground(toChange);
      openPicker = new JButton(Messages.getString("select_color"));
      openPicker.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          JColorChooser colorChooser = new JColorChooser();
          Color newColor = DialogManager.showDialog(colorChooser, Messages.getString("Pick_color_for_") + tile.getText(), toChange);
          tile.setBackground(newColor);
          toChange = newColor;
          AuthoringToolResources.putColor(nameKey, newColor);
          editor.guiInit();
          repaint();
          editor.repaint();
        }
      });
      setLayout(new GridBagLayout());
      add(tile, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
      add(openPicker, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 13, 0, new Insets(0, 8, 0, 0), 0, 0));
      setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }
    
    public TileColorPicker(String nameKey, Class classKey, String foregroundNameKey) {
      this(nameKey, classKey);
      this.foregroundNameKey = foregroundNameKey;
      foregroundToChange = AuthoringToolResources.getColor(foregroundNameKey);
      tile.setForeground(foregroundToChange);
      openPicker2 = new JButton(Messages.getString("select_text_color"));
      openPicker2.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          JColorChooser colorChooser = new JColorChooser();
          Color newColor = DialogManager.showDialog(colorChooser, Messages.getString("Pick_color_for_") + tile.getText(), foregroundToChange);
          tile.setForeground(newColor);
          foregroundToChange = newColor;
          AuthoringToolResources.putColor(foregroundNameKey, newColor);
          editor.guiInit();
          repaint();
          editor.repaint();
        }
      });
      add(openPicker2, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 13, 0, new Insets(0, 8, 0, 0), 0, 0));
    }
  }
  
  public TempColorPicker(CompositeElementEditor r) {
    editor = r;
    setLayout(new GridBagLayout());
    add(new TileColorPicker(Messages.getString("DoInOrder"), DoInOrder.class), new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 4, 0, 0), 0, 0));
    add(new TileColorPicker(Messages.getString("DoTogether"), DoTogether.class), new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 4, 0, 0), 0, 0));
    add(new TileColorPicker(Messages.getString("IfElseInOrder"), IfElseInOrder.class), new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 4, 0, 0), 0, 0));
    add(new TileColorPicker(Messages.getString("LoopNInOrder"), LoopNInOrder.class), new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 4, 0, 0), 0, 0));
    add(new TileColorPicker(Messages.getString("WhileLoopInOrder"), WhileLoopInOrder.class), new GridBagConstraints(0, 4, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 4, 0, 0), 0, 0));
    add(new TileColorPicker(Messages.getString("ForEachInOrder"), ForEachInOrder.class), new GridBagConstraints(0, 5, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 4, 0, 0), 0, 0));
    add(new TileColorPicker(Messages.getString("ForAllTogether"), ForEachTogether.class), new GridBagConstraints(0, 6, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 4, 0, 0), 0, 0));
    add(new TileColorPicker(Messages.getString("response"), "response"), new GridBagConstraints(0, 7, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 4, 0, 0), 0, 0));
    add(new TileColorPicker(Messages.getString("Comment"), Comment.class, "commentForeground"), new GridBagConstraints(0, 8, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 4, 0, 0), 0, 0));
    add(new TileColorPicker(AuthoringToolResources.QUESTION_STRING, "+ - * /"), new GridBagConstraints(0, 9, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 4, 0, 0), 0, 0));
    add(new TileColorPicker(Messages.getString("userDefinedQuestionEditor"), AuthoringToolResources.QUESTION_STRING + " editor"), new GridBagConstraints(0, 10, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 4, 0, 0), 0, 0));
  }
}
