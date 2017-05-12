package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;










public class FontChooser
  extends JPanel
{
  protected HashSet changeListeners = new HashSet();
  
  public FontChooser(boolean sizeVisible, boolean previewVisible) {
    init(sizeVisible, previewVisible);
  }
  
  private void init(boolean sizeVisible, boolean previewVisible) {
    jbInit();
    
    fontSizeCombo.setVisible(sizeVisible);
    previewScrollPane.setVisible(previewVisible);
    
    String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    for (int i = 0; i < families.length; i++) {
      fontFaceCombo.addItem(families[i]);
    }
    fontFaceCombo.setSelectedIndex(0);
    
    fontSizeCombo.addItem(" 8");
    fontSizeCombo.addItem(" 9");
    fontSizeCombo.addItem("10");
    fontSizeCombo.addItem("11");
    fontSizeCombo.addItem("12");
    fontSizeCombo.addItem("14");
    fontSizeCombo.addItem("16");
    fontSizeCombo.addItem("18");
    fontSizeCombo.addItem("20");
    fontSizeCombo.addItem("22");
    fontSizeCombo.addItem("24");
    fontSizeCombo.addItem("26");
    fontSizeCombo.addItem("28");
    fontSizeCombo.addItem("36");
    fontSizeCombo.addItem("48");
    fontSizeCombo.addItem("72");
    fontSizeCombo.setSelectedIndex(0);
    fontSizeCombo.setInputVerifier(
      new InputVerifier() {
        public boolean verify(JComponent c) {
          try {
            String text = (String)((JComboBox)c).getSelectedItem();
            Integer.parseInt(text.trim());
            return true;
          } catch (NumberFormatException e) {
            return false;
          } catch (Exception e) {
            AuthoringTool.showErrorDialog(e.getMessage(), e); }
          return true;

        }
        

      });
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        fireChange(ev.getSource());
      }
      
    };
    fontFaceCombo.addActionListener(actionListener);
    fontSizeCombo.addActionListener(actionListener);
    boldToggle.addActionListener(actionListener);
    italicToggle.addActionListener(actionListener);
  }
  
  public Font getChosenFont() {
    String familyName = (String)fontFaceCombo.getSelectedItem();
    int size = Integer.parseInt(((String)fontSizeCombo.getSelectedItem()).trim());
    int style = (boldToggle.isSelected() ? 1 : 0) | (italicToggle.isSelected() ? 2 : 0);
    
    return new Font(familyName, style, size);
  }
  
  public void setSizeVisible(boolean sizeVisible) {
    fontSizeCombo.setVisible(sizeVisible);
  }
  
  public void setPreviewVisible(boolean previewVisible) {
    previewScrollPane.setVisible(previewVisible);
  }
  
  public void setFontFace(String fontName) {
    fontFaceCombo.setSelectedItem(fontName);
    fireChange(fontFaceCombo);
  }
  
  public void setFontSize(int size) {
    fontSizeCombo.setSelectedItem(Integer.toString(size));
    fireChange(fontSizeCombo);
  }
  

  public void setFontStyle(int bitmask)
  {
    boolean bold = (bitmask & 0x1) > 0;
    boolean italic = (bitmask & 0x2) > 0;
    
    if (bold != boldToggle.isSelected()) {
      boldToggle.setSelected(bold);
      fireChange(boldToggle);
    }
    if (italic != italicToggle.isSelected()) {
      italicToggle.setSelected(italic);
      fireChange(italicToggle);
    }
  }
  
  public void setSampleText(String text) {
    if (text != null) {
      previewTextArea.setText(text);
    } else {
      previewTextArea.setText("The quick brown fox jumped over the lazy dog's back.");
    }
  }
  
  public void addChangeListener(ChangeListener listener) {
    changeListeners.add(listener);
  }
  
  public void removeChangeListener(ChangeListener listener) {
    changeListeners.remove(listener);
  }
  
  protected void fireChange(Object source) {
    Font font = getChosenFont();
    previewTextArea.setFont(font);
    ChangeEvent ev = new ChangeEvent(source);
    
    for (Iterator iter = changeListeners.iterator(); iter.hasNext();) {
      ((ChangeListener)iter.next()).stateChanged(ev);
    }
  }
  




  JComboBox fontFaceCombo = new JComboBox();
  JComboBox fontSizeCombo = new JComboBox();
  JToggleButton boldToggle = new JToggleButton();
  JToggleButton italicToggle = new JToggleButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  Border border1;
  Component component1;
  JScrollPane previewScrollPane = new JScrollPane();
  JTextArea previewTextArea = new JTextArea();
  
  private void jbInit() {
    border1 = BorderFactory.createEmptyBorder(4, 4, 4, 4);
    component1 = Box.createGlue();
    
    boldToggle.setFont(new Font("SansSerif", 1, 14));
    boldToggle.setAlignmentX(0.5F);
    boldToggle.setMaximumSize(new Dimension(27, 27));
    boldToggle.setMinimumSize(new Dimension(27, 27));
    boldToggle.setPreferredSize(new Dimension(27, 27));
    boldToggle.setMargin(new Insets(2, 3, 2, 2));
    boldToggle.setText("B");
    italicToggle.setFont(new Font("Serif", 3, 14));
    italicToggle.setMaximumSize(new Dimension(27, 27));
    italicToggle.setMinimumSize(new Dimension(27, 27));
    italicToggle.setPreferredSize(new Dimension(27, 27));
    italicToggle.setMargin(new Insets(2, 1, 2, 3));
    italicToggle.setText("I");
    setLayout(gridBagLayout1);
    setBorder(border1);
    fontSizeCombo.setMinimumSize(new Dimension(60, 26));
    fontSizeCombo.setPreferredSize(new Dimension(60, 26));
    fontSizeCombo.setEditable(true);
    previewTextArea.setText("The quick brown fox jumped over the lazy dog's back.");
    add(fontFaceCombo, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 
      16, 0, new Insets(0, 0, 0, 0), 0, 0));
    add(fontSizeCombo, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 
      16, 0, new Insets(0, 4, 0, 0), 0, 0));
    add(boldToggle, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 
      16, 0, new Insets(0, 4, 0, 0), 0, 0));
    add(italicToggle, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 
      16, 0, new Insets(0, 0, 0, 0), 0, 0));
    add(component1, new GridBagConstraints(4, 0, 1, 1, 1.0D, 0.0D, 
      16, 2, new Insets(0, 0, 0, 0), 0, 0));
    add(previewScrollPane, new GridBagConstraints(0, 1, 5, 1, 1.0D, 1.0D, 
      11, 1, new Insets(8, 0, 0, 0), 0, 0));
    previewScrollPane.getViewport().add(previewTextArea, null);
  }
}
