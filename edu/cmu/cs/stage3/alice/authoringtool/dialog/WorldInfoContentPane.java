package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementUsageGraph;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter;
import edu.cmu.cs.stage3.alice.core.util.TextureMapCounter;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import edu.cmu.cs.stage3.util.HowMuch;
import edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.Border;







public class WorldInfoContentPane
  extends ContentPane
{
  protected World world;
  protected ElementUsageGraph responseUsageGraph;
  Border border1;
  
  public WorldInfoContentPane()
  {
    jbInit();
    guiInit();
  }
  
  public String getTitle() {
    return Messages.getString("World_Statistics");
  }
  
  private void guiInit() {
    setSize(500, 600);
    responseUsageGraph = new ElementUsageGraph();
    responseUsageGraph
      .setElementCriterion(new InstanceOfCriterion(
      Response.class));
    responseUsagePanel.add(responseUsageGraph, 
      "Center");
  }
  
  public void setWorld(World world) {
    this.world = world;
    responseUsageGraph.setRoot(world);
    refresh();
  }
  
  private void refresh() {
    if (world != null) {
      IndexedTriangleArrayCounter itaCounter = new IndexedTriangleArrayCounter();
      TextureMapCounter textureMapCounter = new TextureMapCounter();
      
      world.visit(itaCounter, 
        HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      world.visit(textureMapCounter, 
        HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      
      int playCount = 0;
      int saveCount = 0;
      long worldOpenTime = 0L;
      
      if (world.data
        .get("edu.cmu.cs.stage3.alice.authoringtool.playCount") != null) {
        playCount = 
          Integer.parseInt((String)world.data
          .get("edu.cmu.cs.stage3.alice.authoringtool.playCount"));
      }
      if (world.data
        .get("edu.cmu.cs.stage3.alice.authoringtool.saveCount") != null) {
        saveCount = 
          Integer.parseInt((String)world.data
          .get("edu.cmu.cs.stage3.alice.authoringtool.saveCount"));
      }
      if (world.data
        .get("edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime") != null) {
        worldOpenTime = 
          Long.parseLong((String)world.data
          .get("edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime"));
      }
      worldOpenTime /= 60000L;
      
      objectCountLabel.setText(
        Messages.getString("Number_of_3D_objects__") + 
        itaCounter.getShownIndexedTriangleArrayCount());
      polyCountLabel.setText(Messages.getString("Number_of_polygons__") + 
        itaCounter.getShownIndexCount() / 3);
      textureCountLabel.setText(
        Messages.getString("Number_of_textures__") + 
        textureMapCounter.getTextureMapCount());
      textureMemoryLabel
        .setText(Messages.getString("Texture_memory_used__") + 
        
        AuthoringToolResources.formatMemorySize(textureMapCounter
        .getTextureMapMemoryCount()));
      userDefinedResponseCountLabel
        .setText(
        Messages.getString("Number_of_user_defined_animations__") + 
        world.getDescendants(UserDefinedResponse.class).length);
      userDefinedQuestionCountLabel
        .setText(
        Messages.getString("Number_of_user_defined_questions__") + 
        world.getDescendants(UserDefinedQuestion.class).length);
      behaviorCountLabel
        .setText(Messages.getString("Number_of_behaviors__") + 
        world.getDescendants(Behavior.class).length);
      playCountLabel.setText(
        Messages.getString("Number_of_times_the_world_has_been_run__") + 
        playCount);
      saveCountLabel.setText(
        Messages.getString("Number_of_times_the_world_has_been_saved__") + 
        saveCount);
      worldOpenTimeLabel.setText(
        Messages.getString("Amount_of_time_the_world_has_been_open__") + 
        worldOpenTime + " " + (
        worldOpenTime == 1L ? Messages.getString("minute") : 
        Messages.getString("minutes")));
      currentRendererLabel.setText(
        Messages.getString("Current_renderer__") + 
        
        AuthoringTool.getHack().getCurrentRendererText());
      
      responseUsageGraph.refresh();
    }
  }
  





  BorderLayout borderLayout1 = new BorderLayout();
  JPanel buttonPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JButton doneButton = new JButton();
  Border border2;
  JScrollPane mainScrollPane = new JScrollPane();
  JLabel textureMemoryLabel = new JLabel();
  JPanel mainPanel = new JPanel();
  JLabel responseUsageLabel = new JLabel();
  JLabel objectCountLabel = new JLabel();
  JLabel polyCountLabel = new JLabel();
  JLabel currentRendererLabel = new JLabel();
  Component component1;
  JPanel responseUsagePanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  JLabel behaviorCountLabel = new JLabel();
  JLabel userDefinedResponseCountLabel = new JLabel();
  JLabel textureCountLabel = new JLabel();
  JLabel playCountLabel = new JLabel();
  JLabel saveCountLabel = new JLabel();
  JLabel worldOpenTimeLabel = new JLabel();
  JLabel userDefinedQuestionCountLabel = new JLabel();
  
  private void jbInit() {
    border1 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
    border2 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
    component1 = Box.createGlue();
    setBackground(new Color(232, 230, 255));
    setLayout(borderLayout1);
    buttonPanel.setOpaque(false);
    buttonPanel.setLayout(gridBagLayout1);
    doneButton.setText(Messages.getString("Done"));
    textureMemoryLabel.setText(Messages.getString("Texture_memory_used__"));
    mainPanel.setBorder(border2);
    mainPanel.setOpaque(false);
    mainPanel.setLayout(gridBagLayout2);
    responseUsageLabel.setText(Messages.getString("Animation_usage_"));
    objectCountLabel.setText(Messages.getString("Number_of_3D_objects__"));
    polyCountLabel.setText(Messages.getString("Number_of_polygons__"));
    responseUsagePanel.setOpaque(false);
    responseUsagePanel.setLayout(borderLayout2);
    behaviorCountLabel.setText(Messages.getString("Number_of_behaviors__"));
    userDefinedResponseCountLabel.setText(
      Messages.getString("Number_of_user_defined_animations_"));
    mainScrollPane.getViewport().setBackground(new Color(232, 230, 255));
    mainScrollPane.setOpaque(false);
    textureCountLabel.setText(Messages.getString("Number_of_textures__"));
    playCountLabel.setText(
      Messages.getString("Number_of_times_the_world_has_been_run__"));
    saveCountLabel.setText(
      Messages.getString("Number_of_times_the_world_has_been_saved__"));
    worldOpenTimeLabel.setText(
      Messages.getString("Amount_of_time_the_world_has_been_open__"));
    userDefinedQuestionCountLabel.setText(
      Messages.getString("Number_of_user_defined_questions__"));
    add(buttonPanel, "South");
    buttonPanel.add(doneButton, new GridBagConstraints(0, 0, 1, 1, 0.0D, 
      0.0D, 10, 0, 
      new Insets(2, 0, 2, 0), 0, 0));
    add(mainScrollPane, "Center");
    mainScrollPane.getViewport().add(mainPanel, null);
    mainPanel.add(objectCountLabel, new GridBagConstraints(0, 0, 2, 1, 1.0D, 
      0.0D, 17, 0, 
      new Insets(0, 0, 2, 0), 0, 0));
    mainPanel.add(polyCountLabel, new GridBagConstraints(0, 1, 2, 1, 1.0D, 
      0.0D, 17, 0, 
      new Insets(0, 0, 2, 0), 0, 0));
    mainPanel.add(textureMemoryLabel, new GridBagConstraints(0, 3, 2, 1, 
      1.0D, 0.0D, 17, 0, 
      new Insets(0, 0, 2, 0), 0, 0));
    mainPanel.add(userDefinedResponseCountLabel, new GridBagConstraints(0, 
      4, 2, 1, 0.0D, 0.0D, 17, 
      0, new Insets(0, 0, 2, 0), 0, 0));
    mainPanel.add(behaviorCountLabel, new GridBagConstraints(0, 6, 2, 1, 
      1.0D, 0.0D, 17, 0, 
      new Insets(0, 0, 2, 0), 0, 0));
    mainPanel.add(responseUsageLabel, new GridBagConstraints(0, 11, 2, 1, 
      1.0D, 0.0D, 17, 0, 
      new Insets(8, 0, 2, 0), 0, 0));
    mainPanel.add(responseUsagePanel, new GridBagConstraints(0, 12, 2, 1, 
      1.0D, 0.0D, 17, 
      2, new Insets(0, 0, 4, 0), 0, 0));
    mainPanel.add(component1, new GridBagConstraints(0, 13, 2, 1, 1.0D, 1.0D, 
      10, 1, new Insets(
      0, 0, 0, 0), 0, 0));
    mainPanel.add(textureCountLabel, new GridBagConstraints(0, 2, 2, 1, 
      1.0D, 0.0D, 17, 0, 
      new Insets(0, 0, 2, 0), 0, 0));
    mainPanel.add(playCountLabel, new GridBagConstraints(0, 7, 2, 1, 1.0D, 
      0.0D, 17, 0, 
      new Insets(0, 0, 2, 0), 0, 0));
    mainPanel.add(saveCountLabel, new GridBagConstraints(0, 8, 2, 1, 1.0D, 
      0.0D, 17, 0, 
      new Insets(0, 0, 2, 0), 0, 0));
    mainPanel.add(worldOpenTimeLabel, new GridBagConstraints(0, 9, 1, 1, 
      1.0D, 0.0D, 17, 0, 
      new Insets(0, 0, 2, 0), 0, 0));
    mainPanel.add(currentRendererLabel, new GridBagConstraints(0, 10, 1, 1, 
      1.0D, 0.0D, 17, 0, 
      new Insets(0, 0, 2, 0), 0, 0));
    
    mainPanel.add(userDefinedQuestionCountLabel, new GridBagConstraints(0, 
      5, 2, 1, 0.0D, 0.0D, 17, 
      0, new Insets(0, 0, 2, 0), 0, 0));
  }
  
  public void addOKActionListener(ActionListener l) {
    doneButton.addActionListener(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    doneButton.removeActionListener(l);
  }
}
