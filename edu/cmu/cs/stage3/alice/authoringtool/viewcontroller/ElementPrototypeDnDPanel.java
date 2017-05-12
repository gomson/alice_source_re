package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.TransferableFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ElementPrototypeDnDPanel extends DnDGroupingPanel implements GUIElement, Releasable
{
  protected static TilePool tilePool = new TilePool();
  
  protected ElementPrototype elementPrototype;
  protected JPanel subPanel = new GroupingPanel();
  protected String elementName;
  protected GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0);
  
  public ElementPrototypeDnDPanel() {
    subPanel.setLayout(new GridBagLayout());
    subPanel.setOpaque(false);
    subPanel.setBorder(null);
    add(subPanel, "Center");
    addDragSourceComponent(subPanel);
  }
  
  public void set(ElementPrototype elementPrototype) {
    this.elementPrototype = elementPrototype;
    setTransferable(TransferableFactory.createTransferable(elementPrototype));
    
    if (CallToUserDefinedResponse.class.isAssignableFrom(elementPrototype.getElementClass())) {
      setBackground(AuthoringToolResources.getColor("userDefinedResponse"));
    } else if (Response.class.isAssignableFrom(elementPrototype.getElementClass())) {
      setBackground(AuthoringToolResources.getColor("response"));
    } else if (CallToUserDefinedQuestion.class.isAssignableFrom(elementPrototype.getElementClass())) {
      setBackground(AuthoringToolResources.getColor("userDefinedQuestion"));
    } else if (Question.class.isAssignableFrom(elementPrototype.getElementClass())) {
      setBackground(AuthoringToolResources.getColor("question"));
    } else if (edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class.isAssignableFrom(elementPrototype.getElementClass())) {
      setBackground(AuthoringToolResources.getColor("userDefinedQuestionComponent"));
    } else {
      setBackground(AuthoringToolResources.getColor("elementPrototypeDnDPanel"));
    }
    
    elementName = AuthoringToolResources.getReprForValue(elementPrototype.getElementClass());
    
    refreshGUI();
  }
  
  public void goToSleep() {}
  
  public void wakeUp() {}
  
  public void clean() { elementPrototype = null;
    setTransferable(null);
    refreshGUI();
  }
  
  public void die() {
    clean();
    subPanel.removeAll();
    removeAll();
  }
  
  public void release() {
    GUIFactory.releaseGUI(this);
  }
  
  public void refreshGUI() {
    java.awt.Component[] components = subPanel.getComponents();
    for (int i = 0; i < components.length; i++) {
      if ((components[i] instanceof Tile)) {
        removeDragSourceComponent(components[i]);
        tilePool.releaseTile(((Tile)components[i]).getText(), (Tile)components[i]);
      }
    }
    
    subPanel.removeAll();
    
    if (elementPrototype != null) {
      StringObjectPair[] propertyValues = elementPrototype.getKnownPropertyValues();
      Vector keys = new Vector();
      HashMap propertyMap = new HashMap();
      for (int i = 0; i < propertyValues.length; i++) {
        keys.add(propertyValues[i].getString());
        propertyMap.put(propertyValues[i].getString(), propertyValues[i].getObject());
      }
      
      constraints.gridx = 0;
      String format = AuthoringToolResources.getFormat(elementPrototype.getElementClass());
      FormatTokenizer tokenizer = new FormatTokenizer(format);
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        if ((token.startsWith("<<<")) && (token.endsWith(">>>"))) {
          String propertyName = token.substring(token.lastIndexOf("<") + 1, token.indexOf(">"));
          if (keys.contains(propertyName)) {
            addTile(AuthoringToolResources.getReprForValue(propertyMap.get(propertyName), false), true);
            constraints.gridx += 1;
            keys.remove(propertyName);
          } else {
            AuthoringTool.showErrorDialog(Messages.getString("no_value_available_for_") + token, null);
            addTile(token, true);
            constraints.gridx += 1;
          }
        } else if ((!token.startsWith("<<")) || (!token.endsWith(">>")))
        {
          if ((token.startsWith("<")) && (token.endsWith(">"))) {
            token = token.substring(token.lastIndexOf("<") + 1, token.indexOf(">"));
            addTile(token, true);
            constraints.gridx += 1;
          } else {
            while (token.indexOf("&lt;") > -1) {
              token = new StringBuffer(token).replace(token.indexOf("&lt;"), token.indexOf("&lt;") + 4, "<").toString();
            }
            addTile(token, false);
            constraints.gridx += 1;
          }
        }
      }
    }
    revalidate();
    repaint();
  }
  
  public void addTile(String text, boolean opaque) {
    Tile tile = tilePool.getTile(text);
    tile.setOpaque(opaque);
    tile.setBorderEnabled(opaque);
    subPanel.add(tile, constraints);
    addDragSourceComponent(tile);
  }
  
  public static class Tile extends GroupingPanel {
    protected String text;
    
    public Tile(String text) {
      this.text = text;
      setLayout(new BorderLayout());
      setBackground(AuthoringToolResources.getColor("prototypeParameter"));
      JLabel tileLabel = new JLabel(text);
      tileLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
      add(tileLabel, "Center");
    }
    
    public String getText() {
      return text;
    }
    
    public void setBorderEnabled(boolean enabled) {
      if (enabled) {
        setBorder(border);
      } else {
        setBorder(null);
      }
    }
    
    public void release() {}
  }
  
  static class TilePool {
    TilePool() {}
    
    protected HashMap tileListMap = new HashMap();
    
    public ElementPrototypeDnDPanel.Tile getTile(String text) {
      LinkedList tileList = (LinkedList)tileListMap.get(text);
      if ((tileList != null) && (!tileList.isEmpty())) {
        return (ElementPrototypeDnDPanel.Tile)tileList.removeFirst();
      }
      ElementPrototypeDnDPanel.Tile tilePanel = new ElementPrototypeDnDPanel.Tile(text);
      return tilePanel;
    }
    
    public void releaseTile(String text, ElementPrototypeDnDPanel.Tile tile)
    {
      LinkedList tileList = (LinkedList)tileListMap.get(text);
      if (tileList == null) {
        tileList = new LinkedList();
        tileListMap.put(text, tileList);
      }
      tileList.addFirst(tile);
    }
  }
}
