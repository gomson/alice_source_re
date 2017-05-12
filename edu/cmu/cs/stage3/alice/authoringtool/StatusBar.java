package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter;
import edu.cmu.cs.stage3.alice.core.util.TextureMapCounter;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.util.HowMuch;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.vecmath.Vector3d;















public class StatusBar
  extends JPanel
  implements AuthoringToolStateListener
{
  Border border1;
  
  public StatusBar(AuthoringTool authoringTool)
  {
    jbInit();
    authoringTool.addAuthoringToolStateListener(this);
  }
  



  public void worldLoaded(AuthoringToolStateChangedEvent ev)
  {
    updateWorldStats(ev.getWorld());
  }
  

  public void worldUnLoaded(AuthoringToolStateChangedEvent ev) { updateWorldStats(null); }
  
  public void stateChanging(AuthoringToolStateChangedEvent ev) {}
  
  public void worldLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldUnLoading(AuthoringToolStateChangedEvent ev) {}
  
  public void worldStarting(AuthoringToolStateChangedEvent ev) {}
  public void worldStopping(AuthoringToolStateChangedEvent ev) {}
  public void worldPausing(AuthoringToolStateChangedEvent ev) {}
  public void worldSaving(AuthoringToolStateChangedEvent ev) {}
  public void stateChanged(AuthoringToolStateChangedEvent ev) {}
  public void worldStarted(AuthoringToolStateChangedEvent ev) {}
  public void worldStopped(AuthoringToolStateChangedEvent ev) {}
  public void worldPaused(AuthoringToolStateChangedEvent ev) {}
  public void worldSaved(AuthoringToolStateChangedEvent ev) {}
  private void updateWorldStats(World world) { Vector3d goodHSB = new Vector3d(0.3333333333333333D, 1.0D, 0.85D);
    Vector3d badHSB = new Vector3d(0.0D, 1.0D, 1.0D);
    
    Color goodColor = new Color(Color.HSBtoRGB((float)x, (float)y, (float)z));
    Color badColor = new Color(Color.HSBtoRGB((float)x, (float)y, (float)z));
    

    int minDangerObjectCount = 1000;
    int maxDangerObjectCount = 2000;
    int minDangerFaceCount = 10000;
    int maxDangerFaceCount = 50000;
    int minDangerTextureMemory = 33554432;
    int maxDangerTextureMemory = 67108864;
    
    if (world != null) {
      IndexedTriangleArrayCounter itaCounter = new IndexedTriangleArrayCounter();
      TextureMapCounter textureMapCounter = new TextureMapCounter();
      
      world.visit(itaCounter, HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      world.visit(textureMapCounter, HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      
      int objectCount = itaCounter.getShownIndexedTriangleArrayCount();
      int faceCount = itaCounter.getShownIndexCount() / 3;
      double textureMemory = textureMapCounter.getTextureMapMemoryCount();
      
      if (objectCount <= minDangerObjectCount) {
        objectCountLabel.setBackground(goodColor);
      } else if (objectCount >= maxDangerObjectCount) {
        objectCountLabel.setBackground(badColor);
      } else {
        float portion = (objectCount - minDangerObjectCount) / (maxDangerObjectCount - minDangerObjectCount);
        Vector3d hsb = MathUtilities.interpolate(goodHSB, badHSB, portion);
        objectCountLabel.setBackground(new Color(Color.HSBtoRGB((float)x, (float)y, (float)z)));
      }
      
      if (faceCount <= minDangerFaceCount) {
        faceCountLabel.setBackground(goodColor);
      } else if (faceCount >= maxDangerFaceCount) {
        faceCountLabel.setBackground(badColor);
      } else {
        float portion = (faceCount - minDangerFaceCount) / (maxDangerFaceCount - minDangerFaceCount);
        Vector3d hsb = MathUtilities.interpolate(goodHSB, badHSB, portion);
        faceCountLabel.setBackground(new Color(Color.HSBtoRGB((float)x, (float)y, (float)z)));
      }
      
      if (textureMemory <= minDangerTextureMemory) {
        textureMemoryLabel.setBackground(goodColor);
      } else if (textureMemory >= maxDangerTextureMemory) {
        textureMemoryLabel.setBackground(badColor);
      } else {
        float portion = (float)(textureMemory - minDangerTextureMemory) / (maxDangerTextureMemory - minDangerTextureMemory);
        Vector3d hsb = MathUtilities.interpolate(goodHSB, badHSB, portion);
        textureMemoryLabel.setBackground(new Color(Color.HSBtoRGB((float)x, (float)y, (float)z)));
      }
      
      String textureSuffix = "";
      if (textureMemory > 524288.0D) {
        textureMemory /= 1048576.0D;
        textureSuffix = " MB";
      } else {
        textureMemory /= 1024.0D;
        textureSuffix = " KB";
      }
      
      DecimalFormat memoryFormat = new DecimalFormat("#0.#");
      
      objectCountLabel.setText("object count: " + objectCount);
      faceCountLabel.setText("face count: " + faceCount);
      textureMemoryLabel.setText("texture memory: " + memoryFormat.format(textureMemory) + textureSuffix);
    } else {
      objectCountLabel.setText("object count: 0");
      faceCountLabel.setText("face count: 0");
      textureMemoryLabel.setText("texture memory: 0 bytes");
      
      objectCountLabel.setBackground(goodColor);
      faceCountLabel.setBackground(goodColor);
      textureMemoryLabel.setBackground(goodColor);
    }
  }
  





  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel worldStatsPanel = new JPanel();
  JLabel worldStatsLabel = new JLabel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel objectCountLabel = new JLabel();
  JLabel faceCountLabel = new JLabel();
  JLabel textureMemoryLabel = new JLabel();
  Component filler1;
  Border border2;
  
  private void jbInit() {
    border1 = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    filler1 = Box.createGlue();
    border2 = BorderFactory.createBevelBorder(1, Color.white, Color.white, new Color(142, 142, 142), new Color(99, 99, 99));
    setBackground(new Color(204, 204, 204));
    setBorder(border1);
    setLayout(gridBagLayout1);
    worldStatsLabel.setText("World Statistics:");
    worldStatsPanel.setLayout(gridBagLayout2);
    objectCountLabel.setBackground(new Color(0, 204, 0));
    objectCountLabel.setForeground(Color.black);
    objectCountLabel.setOpaque(true);
    objectCountLabel.setHorizontalAlignment(0);
    objectCountLabel.setHorizontalTextPosition(0);
    objectCountLabel.setText("object count:");
    faceCountLabel.setBackground(new Color(0, 204, 0));
    faceCountLabel.setForeground(Color.black);
    faceCountLabel.setOpaque(true);
    faceCountLabel.setHorizontalAlignment(0);
    faceCountLabel.setHorizontalTextPosition(0);
    faceCountLabel.setText("face count:");
    textureMemoryLabel.setBackground(new Color(0, 204, 0));
    textureMemoryLabel.setForeground(Color.black);
    textureMemoryLabel.setOpaque(true);
    textureMemoryLabel.setHorizontalAlignment(0);
    textureMemoryLabel.setHorizontalTextPosition(0);
    textureMemoryLabel.setText("texture memory:");
    worldStatsPanel.setBorder(border2);
    add(worldStatsPanel, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 
      17, 1, new Insets(0, 0, 0, 0), 0, 4));
    worldStatsPanel.add(worldStatsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 2, 0, 6), 0, 0));
    worldStatsPanel.add(objectCountLabel, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 0, 0, 6), 10, 0));
    worldStatsPanel.add(faceCountLabel, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 0, 0, 6), 10, 0));
    worldStatsPanel.add(textureMemoryLabel, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(0, 0, 0, 6), 10, 0));
    worldStatsPanel.add(filler1, new GridBagConstraints(4, 0, 1, 1, 1.0D, 0.0D, 
      10, 2, new Insets(0, 0, 0, 0), 0, 0));
  }
}
