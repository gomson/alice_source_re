package edu.cmu.cs.stage3.swing.numpad;

import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class NumPad
  extends ContentPane
{
  public NumPad()
  {
    init();
  }
  
  public String getTitle() {
    return Messages.getString("Custom_Number");
  }
  
  public void addOKActionListener(ActionListener l) {
    okayButton.addActionListener(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    okayButton.removeActionListener(l);
  }
  
  public void addCancelActionListener(ActionListener l) {
    cancelButton.addActionListener(l);
  }
  

  public void removeCancelActionListener(ActionListener l) { cancelButton.removeActionListener(l); }
  
  private void doKey(JButton button, String imageString, ActionListener onClick) {
    button.addActionListener(onClick);
    
    Image image = null;
    URL resource = NumPad.class.getResource(imageString + ".gif");
    if (resource != null) {
      image = Toolkit.getDefaultToolkit().getImage(resource);
    }
    ImageIcon icon = new ImageIcon(image);
    ImageIcon rolloverIcon = null;
    ImageIcon pressedIcon = null;
    if (image != null) {
      rolloverIcon = new ImageIcon(createLightenedOrDarkenedImage(image, 30));
      pressedIcon = new ImageIcon(createLightenedOrDarkenedImage(image, -30));
    }
    
    if (icon != null) {
      button.setText(null);
      button.setBorder(null);
      button.setIcon(icon);
      Dimension iconSize = new Dimension(icon.getIconWidth(), icon.getIconHeight());
      button.setMinimumSize(iconSize);
      button.setMaximumSize(iconSize);
      button.setPreferredSize(iconSize);
      if (rolloverIcon != null) {
        button.setRolloverIcon(rolloverIcon);
      }
      if (pressedIcon != null) {
        button.setPressedIcon(pressedIcon);
      }
    }
  }
  
  private void keyInit() {
    doKey(oneButton, "one", new ActionListener()
    {
      public void actionPerformed(ActionEvent ev) {
        numberTextField.dispatchEvent(new KeyEvent(numberTextField, 400, System.currentTimeMillis(), 0, 0, '1'));
      }
    });
    doKey(twoButton, "two", new ActionListener()
    {
      public void actionPerformed(ActionEvent ev) {
        numberTextField.dispatchEvent(new KeyEvent(numberTextField, 400, System.currentTimeMillis(), 0, 0, '2'));
      }
    });
    doKey(threeButton, "three", new ActionListener()
    {
      public void actionPerformed(ActionEvent ev) {
        numberTextField.dispatchEvent(new KeyEvent(numberTextField, 400, System.currentTimeMillis(), 0, 0, '3'));
      }
    });
    doKey(fourButton, "four", new ActionListener()
    {
      public void actionPerformed(ActionEvent ev) {
        numberTextField.dispatchEvent(new KeyEvent(numberTextField, 400, System.currentTimeMillis(), 0, 0, '4'));
      }
    });
    doKey(fiveButton, "five", new ActionListener()
    {
      public void actionPerformed(ActionEvent ev) {
        numberTextField.dispatchEvent(new KeyEvent(numberTextField, 400, System.currentTimeMillis(), 0, 0, '5'));
      }
    });
    doKey(sixButton, "six", new ActionListener()
    {
      public void actionPerformed(ActionEvent ev) {
        numberTextField.dispatchEvent(new KeyEvent(numberTextField, 400, System.currentTimeMillis(), 0, 0, '6'));
      }
    });
    doKey(sevenButton, "seven", new ActionListener()
    {
      public void actionPerformed(ActionEvent ev) {
        numberTextField.dispatchEvent(new KeyEvent(numberTextField, 400, System.currentTimeMillis(), 0, 0, '7'));
      }
    });
    doKey(eightButton, "eight", new ActionListener()
    {
      public void actionPerformed(ActionEvent ev) {
        numberTextField.dispatchEvent(new KeyEvent(numberTextField, 400, System.currentTimeMillis(), 0, 0, '8'));
      }
    });
    doKey(nineButton, "nine", new ActionListener()
    {
      public void actionPerformed(ActionEvent ev) {
        numberTextField.dispatchEvent(new KeyEvent(numberTextField, 400, System.currentTimeMillis(), 0, 0, '9'));
      }
    });
    doKey(zeroButton, "zero", new ActionListener()
    {
      public void actionPerformed(ActionEvent ev) {
        numberTextField.dispatchEvent(new KeyEvent(numberTextField, 400, System.currentTimeMillis(), 0, 0, '0'));
      }
    });
    doKey(decimalButton, "decimal", new ActionListener()
    {
      public void actionPerformed(ActionEvent ev) {
        numberTextField.dispatchEvent(new KeyEvent(numberTextField, 400, System.currentTimeMillis(), 0, 0, '.'));
      }
    });
    doKey(slashButton, "slash", new ActionListener()
    {
      public void actionPerformed(ActionEvent ev) {
        numberTextField.dispatchEvent(new KeyEvent(numberTextField, 400, System.currentTimeMillis(), 0, 0, '/'));
      }
    });
    doKey(backspaceButton, "backspace", new ActionListener()
    {
      public void actionPerformed(ActionEvent ev)
      {
        try {
          numberTextField.setText(numberTextField.getText(0, numberTextField.getText().length() - 1).toString());
        } catch (Exception localException) {}
      }
    });
    doKey(clearButton, "clear", new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        numberTextField.setText("");
      }
    });
    doKey(plusMinusButton, "plusminus", new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        try {
          if ("-".equals(numberTextField.getDocument().getText(0, 1))) {
            numberTextField.getDocument().remove(0, 1);
          } else {
            numberTextField.getDocument().insertString(0, "-", null);
          }
        } catch (BadLocationException e) {
          throw new RuntimeException(e.getMessage());
        }
      }
    });
  }
  
  private void init() {
    jbInit();
    keyInit();
    numberTextField.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        refresh();
      }
      
      public void insertUpdate(DocumentEvent e) { refresh(); }
      
      public void removeUpdate(DocumentEvent e) {
        refresh();
      }
    });
    numberTextField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        if (okayButton.isEnabled()) {
          okayButton.doClick();
        } else {
          Toolkit.getDefaultToolkit().beep();
        }
      }
    });
    refresh();
  }
  
  public String getNumberString() {
    return numberTextField.getText();
  }
  
  public void setNumberString(String value) {
    if (value != null) {
      numberTextField.setText(value);
    } else {
      numberTextField.setText("");
    }
  }
  
  public void selectAll() {
    numberTextField.selectAll();
  }
  
  protected void refresh() {
    Double number = parseDouble(numberTextField.getText());
    
    if (number != null) {
      okayButton.setEnabled(true);
      numberTextField.setForeground(Color.black);
    } else {
      okayButton.setEnabled(false);
      numberTextField.setForeground(Color.red);
    }
  }
  



  public static Double parseDouble(String doubleString)
  {
    Double number = null;
    if (doubleString.trim().equalsIgnoreCase("infinity")) {
      number = new Double(Double.POSITIVE_INFINITY);
    } else if (doubleString.trim().equalsIgnoreCase("-infinity")) {
      number = new Double(Double.NEGATIVE_INFINITY);
    } else if (doubleString.indexOf('/') > -1) {
      if (doubleString.lastIndexOf('/') == doubleString.indexOf('/')) {
        String numeratorString = doubleString.substring(0, doubleString.indexOf('/'));
        String denominatorString = doubleString.substring(doubleString.indexOf('/') + 1);
        try {
          number = new Double(Double.parseDouble(numeratorString) / Double.parseDouble(denominatorString));
        }
        catch (NumberFormatException localNumberFormatException) {}
      }
    } else {
      try {
        number = Double.valueOf(doubleString);
      }
      catch (NumberFormatException localNumberFormatException1) {}
    }
    
    return number;
  }
  


  public Image createLightenedOrDarkenedImage(Image i, int percent)
  {
    LightenDarkenFilter filter = new LightenDarkenFilter(percent);
    ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
    Image filteredImage = Toolkit.getDefaultToolkit().createImage(prod);
    
    return filteredImage;
  }
  
  public class LightenDarkenFilter
    extends RGBImageFilter
  {
    private int percent;
    
    public LightenDarkenFilter(int p)
    {
      percent = p;
      
      canFilterIndexColorModel = true;
    }
    
    public int filterRGB(int x, int y, int rgb)
    {
      int r = rgb >> 16 & 0xFF;
      int g = rgb >> 8 & 0xFF;
      int b = rgb >> 0 & 0xFF;
      
      if (percent > 0) {
        r = 255 - (255 - r) * (100 - percent) / 100;
        g = 255 - (255 - g) * (100 - percent) / 100;
        b = 255 - (255 - b) * (100 - percent) / 100;
      } else {
        r = r * (100 + percent) / 100;
        g = g * (100 + percent) / 100;
        b = b * (100 + percent) / 100;
      }
      
      if (r < 0)
        r = 0;
      if (g < 0)
        g = 0;
      if (b < 0) {
        b = 0;
      }
      if (r > 255)
        r = 255;
      if (g > 255)
        g = 255;
      if (b > 255) {
        b = 255;
      }
      return rgb & 0xFF000000 | r << 16 | g << 8 | b << 0;
    }
  }
  




  BorderLayout borderLayout1 = new BorderLayout();
  JPanel mainPanel = new JPanel();
  JPanel buttonPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JTextField numberTextField = new JTextField();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JButton okayButton = new JButton();
  JButton cancelButton = new JButton();
  JPanel keyWell = new JPanel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JButton sevenButton = new JButton();
  JButton eightButton = new JButton();
  JButton nineButton = new JButton();
  JButton backspaceButton = new JButton();
  JButton fourButton = new JButton();
  JButton fiveButton = new JButton();
  JButton sixButton = new JButton();
  JButton clearButton = new JButton();
  JButton oneButton = new JButton();
  JButton twoButton = new JButton();
  JButton threeButton = new JButton();
  JButton plusMinusButton = new JButton();
  JButton zeroButton = new JButton();
  JButton decimalButton = new JButton();
  JButton slashButton = new JButton();
  
  private void jbInit() {
    setLayout(borderLayout1);
    mainPanel.setLayout(gridBagLayout1);
    buttonPanel.setLayout(gridBagLayout2);
    okayButton.setText(Messages.getString("Okay"));
    cancelButton.setText(Messages.getString("Cancel"));
    numberTextField.setFont(new Font("Dialog", 0, 24));
    keyWell.setLayout(gridBagLayout3);
    keyWell.setBackground(new Color(17, 17, 17));
    sevenButton.setText("7");
    eightButton.setText("8");
    nineButton.setText("9");
    backspaceButton.setText("<");
    fourButton.setText("4");
    fiveButton.setText("5");
    sixButton.setText("6");
    clearButton.setText("C");
    oneButton.setText("1");
    twoButton.setText("2");
    threeButton.setText("3");
    plusMinusButton.setText("-");
    zeroButton.setText("0");
    decimalButton.setText(".");
    slashButton.setText("/");
    add(mainPanel, "North");
    mainPanel.add(numberTextField, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 10, 2, new Insets(8, 8, 8, 8), 0, 0));
    add(buttonPanel, "Center");
    buttonPanel.add(okayButton, new GridBagConstraints(0, 1, 1, 1, 0.0D, 1.0D, 12, 0, new Insets(4, 4, 4, 4), 0, 0));
    buttonPanel.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 0.0D, 1.0D, 18, 0, new Insets(4, 4, 4, 4), 0, 0));
    mainPanel.add(keyWell, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 8, 8, 8), 0, 0));
    keyWell.add(sevenButton, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 0, 0), 0, 0));
    keyWell.add(eightButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 0, 0), 0, 0));
    keyWell.add(nineButton, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 0, 0), 0, 0));
    keyWell.add(backspaceButton, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 0, 4), 0, 0));
    keyWell.add(fourButton, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 0, 0), 0, 0));
    keyWell.add(fiveButton, new GridBagConstraints(1, 1, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 0, 0), 0, 0));
    keyWell.add(sixButton, new GridBagConstraints(2, 1, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 0, 0), 0, 0));
    keyWell.add(clearButton, new GridBagConstraints(3, 1, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 0, 4), 0, 0));
    keyWell.add(oneButton, new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 0, 0), 0, 0));
    keyWell.add(twoButton, new GridBagConstraints(1, 2, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 0, 0), 0, 0));
    keyWell.add(threeButton, new GridBagConstraints(2, 2, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 0, 0), 0, 0));
    keyWell.add(plusMinusButton, new GridBagConstraints(3, 2, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 0, 4), 0, 0));
    keyWell.add(zeroButton, new GridBagConstraints(0, 3, 2, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 4, 0), 0, 0));
    keyWell.add(decimalButton, new GridBagConstraints(2, 3, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 4, 0), 0, 0));
    keyWell.add(slashButton, new GridBagConstraints(3, 3, 1, 1, 0.0D, 0.0D, 18, 1, new Insets(4, 4, 4, 4), 0, 0));
  }
}
