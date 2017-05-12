package edu.cmu.cs.stage3.caitlin.personbuilder;

import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.progress.ProgressCancelException;
import edu.cmu.cs.stage3.progress.ProgressObserver;
import edu.cmu.cs.stage3.swing.DialogManager;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;





public class PersonBuilder
  extends JPanel
{
  public static Vector getAllBuilders()
  {
    Vector builders = new Vector();
    String name = "";
    ImageIcon icon = null;
    Document doc = (Document)XMLDirectoryUtilities.loadFile("images/builders.xml");
    Node root = doc.getDocumentElement();
    
    NodeList nl = root.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node.getNodeName().equals("builder")) {
        NamedNodeMap nodeMap = node.getAttributes();
        for (int j = 0; j < nodeMap.getLength(); j++) {
          Node attr = nodeMap.item(j);
          if (attr.getNodeName().equals("name")) {
            name = attr.getNodeValue();
          } else if (attr.getNodeName().equals("icon")) {
            String iconName = attr.getNodeValue();
            icon = new ImageIcon(PersonBuilder.class.getResource("images/" + iconName), iconName);
          }
        }
        StringObjectPair sop = new StringObjectPair(name, icon);
        builders.addElement(sop);
      }
    }
    
    return builders;
  }
  
  private AllStepsPanel allStepsPanel = null;
  private NavigationPanel navPanel = null;
  private RenderPanel renderPanel = null;
  private NamePanel namePanel = null;
  private ModelWrapper modelWrapper = null;
  private String builderName = "";
  
  public PersonBuilder(String builderName, ProgressObserver progressObserver) throws ProgressCancelException {
    this.builderName = builderName;
    String builderFile = "images/" + builderName + ".xml";
    int progressOffset = 0;
    
    progressObserver.progressBegin(7);
    try {
      Document doc = (Document)XMLDirectoryUtilities.loadFile(builderFile);
      progressObserver.progressUpdate(progressOffset++, null);
      Node root = doc.getDocumentElement();
      progressObserver.progressUpdate(progressOffset++, null);
      modelWrapper = new ModelWrapper(root);
      progressObserver.progressUpdate(progressOffset++, null);
      allStepsPanel = new AllStepsPanel(root, modelWrapper, progressObserver, progressOffset);
      navPanel = new NavigationPanel(root, allStepsPanel);
      renderPanel = new RenderPanel(modelWrapper);
      namePanel = new NamePanel();
      renderPanel.initialize();
      
      setLayout(new BorderLayout());
      add(allStepsPanel, "East");
      add(renderPanel, "Center");
      add(navPanel, "North");
      add(namePanel, "South");
    } finally {
      progressObserver.progressEnd();
    }
    setBackground(new Color(155, 159, 206));
    setSize(500, 500);
  }
  
  public void reset() {
    modelWrapper.resetWorld();
    try {
      allStepsPanel.resetDefaults();
    } catch (Exception e) {
      e.printStackTrace();
    }
    navPanel.setFirstStep();
  }
  
  public Model getModel() { Model model = modelWrapper.getModel();
    String text = namePanel.getCreatedBy();
    if (text.length() == 0) {
      text = Messages.getString("Anonymous");
    }
    data.put(Messages.getString("created_by"), text);
    
    text = namePanel.getName();
    if (text.length() == 0) {
      text = DialogManager.showInputDialog(Messages.getString("What_would_you_like_to_name_your_character_"));
    }
    if (text == null) {
      text = "";
    } else
      text = text.trim();
    if ((text.startsWith("\"")) || (text.startsWith("'"))) {
      text = text.substring(1);
    }
    if ((text.endsWith("\"")) || (text.endsWith("'"))) {
      text = text.substring(0, text.length() - 1);
    }
    name.set(text);
    
    return model;
  }
}
