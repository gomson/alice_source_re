package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalTabbedPaneUI;










public class AliceTabbedPaneUI
  extends MetalTabbedPaneUI
{
  protected Color defaultTabForeground;
  
  public AliceTabbedPaneUI()
  {
    setTabAreaInsets(new Insets(0, 0, 0, 0));
  }
  
  protected void installDefaults() {
    super.installDefaults();
    
    selectColor = Color.white;
    selectHighlight = Color.black;
    tabAreaBackground = Color.white;
    darkShadow = Color.black;
    focus = new Color(255, 255, 255, 0);
    highlight = Color.darkGray;
    

    contentBorderInsets = new Insets(1, 1, 1, 1);
    
    defaultTabForeground = UIManager.getColor("TabbedPane.foreground");
  }
  
  public void setTabAreaInsets(Insets insets) {
    tabAreaInsets = insets;
  }
  
  public void update(Graphics g, JComponent c) {
    int tabPlacement = tabPane.getTabPlacement();
    Insets insets = c.getInsets();
    Dimension size = c.getSize();
    

    Component selectedComponent = tabPane.getSelectedComponent();
    if (selectedComponent != null) {
      selectColor = (this.tabAreaBackground = selectedComponent.getBackground());
    }
    int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
    if ((tabAreaBackground.equals(new Color(255, 255, 210))) && (tabPane.getTabCount() > 0) && (tabAreaHeight == 0)) {
      tabAreaHeight = 50;
    }
    g.setColor(tabAreaBackground);
    g.fillRect(left, top + tabAreaHeight, width - right - left, height - tabAreaHeight);
    
    paint(g, c);
  }
  


  protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect)
  {
    Rectangle tabRect = rects[tabIndex];
    int selectedIndex = tabPane.getSelectedIndex();
    boolean isSelected = selectedIndex == tabIndex;
    paintTabBackground(g, tabPlacement, tabIndex, x, y, width, height, isSelected);
    paintTabBorder(g, tabPlacement, tabIndex, x, y, width, height, isSelected);
    

    if ((isSelected) && (tabPane.getSelectedComponent() != null)) {
      Color background = tabPane.getSelectedComponent().getBackground();
      int brightness = (background.getRed() + background.getGreen() + background.getBlue()) / 3;
      if (brightness < 128) {
        if (!tabPane.getForegroundAt(tabIndex).equals(Color.white)) {
          tabPane.setForegroundAt(tabIndex, Color.white);
        }
      }
      else if (!tabPane.getForegroundAt(tabIndex).equals(defaultTabForeground)) {
        tabPane.setForegroundAt(tabIndex, null);
      }
      
    }
    else if (!tabPane.getForegroundAt(tabIndex).equals(defaultTabForeground)) {
      tabPane.setForegroundAt(tabIndex, null);
    }
    

    String title = tabPane.getTitleAt(tabIndex);
    Font font = tabPane.getFont();
    if (isSelected) {
      font = font.deriveFont(1);
    } else {
      font = font.deriveFont(0);
    }
    FontMetrics metrics = g.getFontMetrics(font);
    Icon icon = getIconForTab(tabIndex);
    layoutLabel(tabPlacement, metrics, tabIndex, title, icon, tabRect, iconRect, textRect, isSelected);
    paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
    paintIcon(g, tabPlacement, tabIndex, icon, iconRect, isSelected);
    paintFocusIndicator(g, tabPlacement, rects, tabIndex, iconRect, textRect, isSelected);
  }
  











  public int getTabAreaHeight()
  {
    Insets tabAreaInsets = getTabAreaInsets(tabPane.getTabPlacement());
    int runCount = tabPane.getTabRunCount();
    int tabRunOverlay = getTabRunOverlay(tabPane.getTabPlacement());
    return runCount > 0 ? runCount * (maxTabHeight - tabRunOverlay) + tabRunOverlay + top + bottom : 0;
  }
  
  public int getTabAreaHeightIgnoringInsets() {
    int runCount = tabPane.getTabRunCount();
    int tabRunOverlay = getTabRunOverlay(tabPane.getTabPlacement());
    return runCount > 0 ? runCount * (maxTabHeight - tabRunOverlay) + tabRunOverlay : 0;
  }
  
  protected FontMetrics getFontMetrics() {
    Font font = tabPane.getFont().deriveFont(1);
    return Toolkit.getDefaultToolkit().getFontMetrics(font);
  }
}
