package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.util.Criterion;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;






public class ElementUsageGraph
  extends JPanel
{
  protected Element root;
  protected Criterion elementCriterion;
  protected Criterion acceptAllCriterion = new Criterion() {
    public boolean accept(Object o) {
      return true;
    }
  };
  protected ClassNameComparator classNameComparator = new ClassNameComparator();
  protected float saturation = 0.5F;
  protected float brightness = 0.9F;
  protected HashMap classCountMap = new HashMap();
  protected GridBagConstraints labelConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0);
  protected GridBagConstraints barConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 17, 1, new Insets(0, 0, 8, 0), 0, 12);
  protected Random random = new Random();
  
  public ElementUsageGraph() {
    setBackground(Color.white);
    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
  }
  
  public ElementUsageGraph(Element root, Criterion elementCriterion) {
    this();
    setRoot(root);
    setElementCriterion(elementCriterion);
  }
  
  public void setRoot(Element root) {
    this.root = root;
  }
  
  public Element getRoot() {
    return root;
  }
  
  public void setElementCriterion(Criterion elementCriterion) {
    this.elementCriterion = elementCriterion;
  }
  
  public Criterion getElementCriterion() {
    return elementCriterion;
  }
  
  public void refresh() {
    classCountMap.clear();
    removeAll();
    int gridy = 0;
    
    Criterion criterion = elementCriterion != null ? elementCriterion : acceptAllCriterion;
    if (root != null) {
      int maxCount = 0;
      Element[] elements = root.search(criterion);
      for (int i = 0; i < elements.length; i++) {
        Class c = elements[i].getClass();
        if (classCountMap.containsKey(c)) {
          Integer count = (Integer)classCountMap.get(c);
          classCountMap.put(c, new Integer(count.intValue() + 1));
          maxCount = Math.max(maxCount, count.intValue() + 1);
        } else {
          classCountMap.put(c, new Integer(1));
          maxCount = Math.max(maxCount, 1);
        }
      }
      
      JLabel totalLabel = new JLabel("Total: " + elements.length);
      barConstraints.gridy = (gridy++);
      add(totalLabel, barConstraints);
      
      List classList = new ArrayList(classCountMap.keySet());
      Collections.sort(classList, classNameComparator);
      for (Iterator iter = classList.iterator(); iter.hasNext();) {
        Class c = (Class)iter.next();
        String name = c.getName();
        name = name.substring(name.lastIndexOf('.') + 1);
        int count = ((Integer)classCountMap.get(c)).intValue();
        double portion = count / maxCount;
        random.setSeed(name.hashCode());
        Color color = Color.getHSBColor(random.nextFloat(), saturation, brightness);
        
        JLabel label = new JLabel(name + ": " + count);
        labelConstraints.gridy = (gridy++);
        add(label, labelConstraints);
        
        HorizontalBar bar = new HorizontalBar(portion, color);
        barConstraints.gridy = (gridy++);
        add(bar, barConstraints);
      }
    }
    
    revalidate();
    repaint();
  }
  
  protected class HorizontalBar extends JPanel {
    protected double portion;
    protected Color color;
    
    public HorizontalBar(double portion, Color color) {
      this.portion = portion;
      this.color = color;
      
      setOpaque(false);
    }
    
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      
      g.setColor(color);
      Dimension size = getSize();
      int width = width - 1;
      if (width > 2) {
        width = (int)Math.round((width - 1.0D) * portion);
      }
      g.fill3DRect(0, 0, width, height - 1, true);
    }
  }
  
  protected class ClassNameComparator implements Comparator { protected ClassNameComparator() {}
    
    public int compare(Object o1, Object o2) { if (((o1 instanceof Class)) && ((o2 instanceof Class))) {
        String name1 = ((Class)o1).getName();
        name1 = name1.substring(name1.lastIndexOf('.') + 1);
        String name2 = ((Class)o2).getName();
        name2 = name2.substring(name2.lastIndexOf('.') + 1);
        return name1.compareTo(name2);
      }
      return 0;
    }
  }
}
