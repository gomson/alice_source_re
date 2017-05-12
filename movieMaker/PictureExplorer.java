package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.border.LineBorder;

















public class PictureExplorer
  implements MouseMotionListener, ActionListener, MouseListener
{
  private int xIndex = 0;
  private int yIndex = 0;
  
  private JFrame pictureFrame;
  
  private JScrollPane scrollPane;
  
  private JLabel xLabel;
  
  private JButton xPrevButton;
  
  private JButton yPrevButton;
  
  private JButton xNextButton;
  
  private JButton yNextButton;
  
  private JLabel yLabel;
  
  private JTextField xValue;
  
  private JTextField yValue;
  
  private JLabel rValue;
  
  private JLabel gValue;
  
  private JLabel bValue;
  
  private JLabel colorLabel;
  
  private JPanel colorPanel;
  
  private JMenuBar menuBar;
  private JMenu zoomMenu;
  private JMenuItem twentyFive;
  private JMenuItem fifty;
  private JMenuItem seventyFive;
  private JMenuItem hundred;
  private JMenuItem hundredFifty;
  private JMenuItem twoHundred;
  private JMenuItem fiveHundred;
  private DigitalPicture picture;
  private ImageIcon scrollImageIcon;
  private ImageDisplay imageDisplay;
  private double zoomFactor;
  private int numberBase = 0;
  





  public PictureExplorer(DigitalPicture picture)
  {
    this.picture = picture;
    zoomFactor = 1.0D;
    

    createWindow();
  }
  



  public void changeToBaseOne()
  {
    numberBase = 1;
  }
  




  public void setTitle(String title)
  {
    pictureFrame.setTitle(title);
  }
  



  private void createAndInitPictureFrame()
  {
    pictureFrame = new JFrame();
    pictureFrame.setResizable(true);
    pictureFrame.getContentPane().setLayout(new BorderLayout());
    pictureFrame.setDefaultCloseOperation(2);
    pictureFrame.setTitle(picture.getTitle());
    PictureExplorerFocusTraversalPolicy newPolicy = new PictureExplorerFocusTraversalPolicy(null);
    pictureFrame.setFocusTraversalPolicy(newPolicy);
  }
  





  private void setUpMenuBar()
  {
    menuBar = new JMenuBar();
    zoomMenu = new JMenu(Messages.getString("Zoom"));
    twentyFive = new JMenuItem("25%");
    fifty = new JMenuItem("50%");
    seventyFive = new JMenuItem("75%");
    hundred = new JMenuItem("100%");
    hundred.setEnabled(false);
    hundredFifty = new JMenuItem("150%");
    twoHundred = new JMenuItem("200%");
    fiveHundred = new JMenuItem("500%");
    

    twentyFive.addActionListener(this);
    fifty.addActionListener(this);
    seventyFive.addActionListener(this);
    hundred.addActionListener(this);
    hundredFifty.addActionListener(this);
    twoHundred.addActionListener(this);
    fiveHundred.addActionListener(this);
    

    zoomMenu.add(twentyFive);
    zoomMenu.add(fifty);
    zoomMenu.add(seventyFive);
    zoomMenu.add(hundred);
    zoomMenu.add(hundredFifty);
    zoomMenu.add(twoHundred);
    zoomMenu.add(fiveHundred);
    menuBar.add(zoomMenu);
    

    pictureFrame.setJMenuBar(menuBar);
  }
  



  private void createAndInitScrollingImage()
  {
    scrollPane = new JScrollPane();
    
    BufferedImage bimg = picture.getBufferedImage();
    imageDisplay = new ImageDisplay(bimg);
    imageDisplay.addMouseMotionListener(this);
    imageDisplay.addMouseListener(this);
    imageDisplay.setToolTipText(Messages.getString("Click_a_mouse_button_on_a_pixel_to_see_the_pixel_information"));
    scrollPane.setViewportView(imageDisplay);
    pictureFrame.getContentPane().add(scrollPane, "Center");
  }
  




  private void createWindow()
  {
    createAndInitPictureFrame();
    

    setUpMenuBar();
    

    createInfoPanel();
    

    createAndInitScrollingImage();
    

    pictureFrame.pack();
    pictureFrame.setVisible(true);
  }
  





  private void setUpNextAndPreviousButtons()
  {
    Icon prevIcon = new ImageIcon(SoundExplorer.class.getResource("leftArrow.gif"), 
      Messages.getString("previous_index"));
    Icon nextIcon = new ImageIcon(SoundExplorer.class.getResource("rightArrow.gif"), 
      Messages.getString("next_index"));
    
    xPrevButton = new JButton(prevIcon);
    xNextButton = new JButton(nextIcon);
    yPrevButton = new JButton(prevIcon);
    yNextButton = new JButton(nextIcon);
    

    xNextButton.setToolTipText(Messages.getString("Click_to_go_to_the_next_x_value"));
    xPrevButton.setToolTipText(Messages.getString("Click_to_go_to_the_previous_x_value"));
    yNextButton.setToolTipText(Messages.getString("Click_to_go_to_the_next_y_value"));
    yPrevButton.setToolTipText(Messages.getString("Click_to_go_to_the_previous_y_value"));
    

    int prevWidth = prevIcon.getIconWidth() + 2;
    int nextWidth = nextIcon.getIconWidth() + 2;
    int prevHeight = prevIcon.getIconHeight() + 2;
    int nextHeight = nextIcon.getIconHeight() + 2;
    Dimension prevDimension = new Dimension(prevWidth, prevHeight);
    Dimension nextDimension = new Dimension(nextWidth, nextHeight);
    xPrevButton.setPreferredSize(prevDimension);
    yPrevButton.setPreferredSize(prevDimension);
    xNextButton.setPreferredSize(nextDimension);
    yNextButton.setPreferredSize(nextDimension);
    

    xPrevButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        xIndex -= 1;
        if (xIndex < 0)
          xIndex = 0;
        PictureExplorer.this.displayPixelInformation(xIndex, yIndex);
      }
      

    });
    yPrevButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        yIndex -= 1;
        if (yIndex < 0)
          yIndex = 0;
        PictureExplorer.this.displayPixelInformation(xIndex, yIndex);
      }
      

    });
    xNextButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        xIndex += 1;
        if (xIndex >= picture.getWidth())
          xIndex = (picture.getWidth() - 1);
        PictureExplorer.this.displayPixelInformation(xIndex, yIndex);
      }
      

    });
    yNextButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        yIndex += 1;
        if (yIndex >= picture.getHeight())
          yIndex = (picture.getHeight() - 1);
        PictureExplorer.this.displayPixelInformation(xIndex, yIndex);
      }
    });
  }
  






  public JPanel createLocationPanel(Font labelFont)
  {
    JPanel locationPanel = new JPanel();
    locationPanel.setLayout(new FlowLayout());
    Box hBox = Box.createHorizontalBox();
    

    xLabel = new JLabel("X:");
    yLabel = new JLabel("Y:");
    

    xValue = new JTextField(Integer.toString(xIndex + numberBase), 6);
    xValue.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        displayPixelInformation(xValue.getText(), yValue.getText());
      }
    });
    yValue = new JTextField(Integer.toString(yIndex + numberBase), 6);
    yValue.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        displayPixelInformation(xValue.getText(), yValue.getText());
      }
      

    });
    setUpNextAndPreviousButtons();
    

    xLabel.setFont(labelFont);
    yLabel.setFont(labelFont);
    xValue.setFont(labelFont);
    yValue.setFont(labelFont);
    

    hBox.add(Box.createHorizontalGlue());
    hBox.add(xLabel);
    hBox.add(xPrevButton);
    hBox.add(xValue);
    hBox.add(xNextButton);
    hBox.add(Box.createHorizontalStrut(10));
    hBox.add(yLabel);
    hBox.add(yPrevButton);
    hBox.add(yValue);
    hBox.add(yNextButton);
    locationPanel.add(hBox);
    hBox.add(Box.createHorizontalGlue());
    
    return locationPanel;
  }
  






  private JPanel createColorInfoPanel(Font labelFont)
  {
    JPanel colorInfoPanel = new JPanel();
    colorInfoPanel.setLayout(new FlowLayout());
    

    Pixel pixel = new Pixel(picture, xIndex, yIndex);
    

    rValue = new JLabel("R: " + pixel.getRed());
    gValue = new JLabel("G: " + pixel.getGreen());
    bValue = new JLabel("B: " + pixel.getBlue());
    

    colorLabel = new JLabel(Messages.getString("Color_at_location__"));
    colorPanel = new JPanel();
    colorPanel.setBorder(new LineBorder(Color.black, 1));
    

    colorPanel.setBackground(pixel.getColor());
    

    rValue.setFont(labelFont);
    gValue.setFont(labelFont);
    bValue.setFont(labelFont);
    colorLabel.setFont(labelFont);
    colorPanel.setPreferredSize(new Dimension(25, 25));
    

    colorInfoPanel.add(rValue);
    colorInfoPanel.add(gValue);
    colorInfoPanel.add(bValue);
    colorInfoPanel.add(colorLabel);
    colorInfoPanel.add(colorPanel);
    
    return colorInfoPanel;
  }
  





  private void createInfoPanel()
  {
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BorderLayout());
    

    Font largerFont = new Font(infoPanel.getFont().getName(), 
      infoPanel.getFont().getStyle(), 14);
    

    JPanel locationPanel = createLocationPanel(largerFont);
    

    JPanel colorInfoPanel = createColorInfoPanel(largerFont);
    

    infoPanel.add("North", locationPanel);
    infoPanel.add("South", colorInfoPanel);
    

    pictureFrame.getContentPane().add("North", infoPanel);
  }
  





  public void checkScroll()
  {
    int xPos = (int)(xIndex * zoomFactor);
    int yPos = (int)(yIndex * zoomFactor);
    

    if (zoomFactor > 1.0D)
    {

      JViewport viewport = scrollPane.getViewport();
      Rectangle rect = viewport.getViewRect();
      int rectMinX = (int)rect.getX();
      int rectWidth = (int)rect.getWidth();
      int rectMaxX = rectMinX + rectWidth - 1;
      int rectMinY = (int)rect.getY();
      int rectHeight = (int)rect.getHeight();
      int rectMaxY = rectMinY + rectHeight - 1;
      

      int maxIndexX = (int)(picture.getWidth() * zoomFactor) - rectWidth - 1;
      int maxIndexY = (int)(picture.getHeight() * zoomFactor) - rectHeight - 1;
      


      int viewX = xPos - rectWidth / 2;
      int viewY = yPos - rectHeight / 2;
      

      if (viewX < 0) {
        viewX = 0;
      } else if (viewX > maxIndexX)
        viewX = maxIndexX;
      if (viewY < 0) {
        viewY = 0;
      } else if (viewY > maxIndexY) {
        viewY = maxIndexY;
      }
      
      viewport.scrollRectToVisible(new Rectangle(viewX, viewY, rectWidth, rectHeight));
    }
  }
  






  public void zoom(double factor)
  {
    zoomFactor = factor;
    

    int width = (int)(picture.getWidth() * zoomFactor);
    int height = (int)(picture.getHeight() * zoomFactor);
    BufferedImage bimg = picture.getBufferedImage();
    

    imageDisplay.setImage(bimg.getScaledInstance(width, height, 1));
    imageDisplay.setCurrentX((int)(xIndex * zoomFactor));
    imageDisplay.setCurrentY((int)(yIndex * zoomFactor));
    imageDisplay.revalidate();
    checkScroll();
  }
  



  public void repaint()
  {
    pictureFrame.repaint();
  }
  








  public void mouseDragged(MouseEvent e)
  {
    displayPixelInformation(e);
  }
  






  private boolean isLocationInPicture(int x, int y)
  {
    boolean result = false;
    if ((x >= 0) && (x < picture.getWidth()) && 
      (y >= 0) && (y < picture.getHeight())) {
      result = true;
    }
    return result;
  }
  






  public void displayPixelInformation(String xString, String yString)
  {
    int x = -1;
    int y = -1;
    try {
      x = Integer.parseInt(xString);
      x -= numberBase;
      y = Integer.parseInt(yString);
      y -= numberBase;
    }
    catch (Exception localException) {}
    
    if ((x >= 0) && (y >= 0)) {
      displayPixelInformation(x, y);
    }
  }
  






  private void displayPixelInformation(int pictureX, int pictureY)
  {
    if (isLocationInPicture(pictureX, pictureY))
    {

      xIndex = pictureX;
      yIndex = pictureY;
      

      Pixel pixel = new Pixel(picture, xIndex, yIndex);
      

      xValue.setText(Integer.toString(xIndex + numberBase));
      yValue.setText(Integer.toString(yIndex + numberBase));
      rValue.setText("R: " + pixel.getRed());
      gValue.setText("G: " + pixel.getGreen());
      bValue.setText("B: " + pixel.getBlue());
      colorPanel.setBackground(new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue()));

    }
    else
    {
      clearInformation();
    }
    

    imageDisplay.setCurrentX((int)(xIndex * zoomFactor));
    imageDisplay.setCurrentY((int)(yIndex * zoomFactor));
  }
  






  private void displayPixelInformation(MouseEvent e)
  {
    int cursorX = e.getX();
    int cursorY = e.getY();
    

    int pictureX = (int)(cursorX / zoomFactor + numberBase);
    int pictureY = (int)(cursorY / zoomFactor + numberBase);
    

    displayPixelInformation(pictureX, pictureY);
  }
  





  private void clearInformation()
  {
    xValue.setText("N/A");
    yValue.setText("N/A");
    rValue.setText("R: N/A");
    gValue.setText("G: N/A");
    bValue.setText("B: N/A");
    colorPanel.setBackground(Color.black);
    xIndex = -1;
    yIndex = -1;
  }
  





  public void mouseMoved(MouseEvent e) {}
  




  public void mouseClicked(MouseEvent e)
  {
    displayPixelInformation(e);
  }
  




  public void mousePressed(MouseEvent e)
  {
    displayPixelInformation(e);
  }
  






  public void mouseReleased(MouseEvent e) {}
  





  public void mouseEntered(MouseEvent e) {}
  





  public void mouseExited(MouseEvent e) {}
  





  private void enableZoomItems()
  {
    twentyFive.setEnabled(true);
    fifty.setEnabled(true);
    seventyFive.setEnabled(true);
    hundred.setEnabled(true);
    hundredFifty.setEnabled(true);
    twoHundred.setEnabled(true);
    fiveHundred.setEnabled(true);
  }
  






  public void actionPerformed(ActionEvent a)
  {
    if (a.getActionCommand().equals(Messages.getString("Update")))
    {
      repaint();
    }
    
    if (a.getActionCommand().equals("25%"))
    {
      zoom(0.25D);
      enableZoomItems();
      twentyFive.setEnabled(false);
    }
    
    if (a.getActionCommand().equals("50%"))
    {
      zoom(0.5D);
      enableZoomItems();
      fifty.setEnabled(false);
    }
    
    if (a.getActionCommand().equals("75%"))
    {
      zoom(0.75D);
      enableZoomItems();
      seventyFive.setEnabled(false);
    }
    
    if (a.getActionCommand().equals("100%"))
    {
      zoom(1.0D);
      enableZoomItems();
      hundred.setEnabled(false);
    }
    
    if (a.getActionCommand().equals("150%"))
    {
      zoom(1.5D);
      enableZoomItems();
      hundredFifty.setEnabled(false);
    }
    
    if (a.getActionCommand().equals("200%"))
    {
      zoom(2.0D);
      enableZoomItems();
      twoHundred.setEnabled(false);
    }
    
    if (a.getActionCommand().equals("500%"))
    {
      zoom(5.0D);
      enableZoomItems();
      fiveHundred.setEnabled(false);
    }
  }
  








  private class PictureExplorerFocusTraversalPolicy
    extends FocusTraversalPolicy
  {
    private PictureExplorerFocusTraversalPolicy() {}
    







    public Component getComponentAfter(Container focusCycleRoot, Component aComponent)
    {
      if (aComponent.equals(xValue)) {
        return yValue;
      }
      return xValue;
    }
    




    public Component getComponentBefore(Container focusCycleRoot, Component aComponent)
    {
      if (aComponent.equals(xValue)) {
        return yValue;
      }
      return xValue;
    }
    
    public Component getDefaultComponent(Container focusCycleRoot)
    {
      return xValue;
    }
    
    public Component getLastComponent(Container focusCycleRoot)
    {
      return yValue;
    }
    
    public Component getFirstComponent(Container focusCycleRoot)
    {
      return xValue;
    }
  }
}
