package edu.cmu.cs.stage3.caitlin.personbuilder;

import edu.cmu.cs.stage3.alice.core.CopyFactory;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.ExceptionWrapper;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Vector3;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



















public class ItemChooser
  extends JPanel
{
  BorderLayout borderLayout1 = new BorderLayout();
  JLabel itemPicture = new JLabel();
  JButton backButton = new JButton();
  JButton forwardButton = new JButton();
  ImageIcon nextImage = null;
  ImageIcon backImage = null;
  ModelWrapper modelWrapper = null;
  Vector commandInfos = new Vector();
  int index = 0;
  
  public ItemChooser(Node itemsNode, ImageIcon nextImage, ImageIcon backImage, ModelWrapper modelWrapper) {
    this.nextImage = nextImage;
    this.backImage = backImage;
    this.modelWrapper = modelWrapper;
    try {
      initializeChoices(itemsNode);
      jbInit();
      
      CommandInfo currentInfo = (CommandInfo)commandInfos.elementAt(index);
      itemPicture.setIcon(imageIcon);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void resetDefaults() {
    index = 0;
    CommandInfo currentInfo = (CommandInfo)commandInfos.elementAt(index);
    itemPicture.setIcon(imageIcon);
  }
  
  private void initializeCommandInfos(Node dirNode, Node choicesNode) {
    NodeList choiceNodeList = choicesNode.getChildNodes();
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    int altModelCount = 0;
    for (int i = 0; i < choiceNodeList.getLength(); i++) {
      Node choiceNode = choiceNodeList.item(i);
      
      if (choiceNode.getNodeName().equals("addTexture")) {
        CommandInfo cmdInfo = new CommandInfo();
        id = "addTexture";
        NamedNodeMap attrs = choiceNode.getAttributes();
        for (int j = 0; j < attrs.getLength(); j++) {
          Node attr = attrs.item(j);
          if (attr.getNodeName().equals("icon")) {
            String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
            try {
              ImageIcon icon = new ImageIcon(PersonBuilder.class.getResource(imageFileName), imageFileName);
              imageIcon = icon;
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else if (attr.getNodeName().equals("texture")) {
            String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
            Image img = toolkit.createImage(PersonBuilder.class.getResource(imageFileName));
            try {
              MediaTracker tracker = new MediaTracker(this);
              tracker.addImage(img, 0);
              tracker.waitForID(0);
            }
            catch (InterruptedException localInterruptedException) {}
            texture = img;
          } else if (attr.getNodeName().equals("layer")) {
            String layerString = attr.getNodeValue();
            int layer = Integer.parseInt(layerString);
            level = layer;
          } else if (attr.getNodeName().equals("useAltModel")) {
            String altModelName = attr.getNodeValue();
            altModelName = altModelName;
          }
        }
        commandInfos.addElement(cmdInfo);
      }
      else if (choiceNode.getNodeName().equals("setModel")) {
        CommandInfo cmdInfo = new CommandInfo();
        id = "setModel";
        NamedNodeMap attrs = choiceNode.getAttributes();
        for (int j = 0; j < attrs.getLength(); j++) {
          Node attr = attrs.item(j);
          if (attr.getNodeName().equals("icon")) {
            String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
            try {
              ImageIcon icon = new ImageIcon(PersonBuilder.class.getResource(imageFileName), imageFileName);
              imageIcon = icon;
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else if (attr.getNodeName().equals("model")) {
            String modelString = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
            Model part = null;
            try {
              part = (Model)Element.load(PersonBuilder.class.getResource(modelString), null);
              vehicle.set(null);
            } catch (IOException ioe) {
              ioe.printStackTrace();
            } catch (UnresolvablePropertyReferencesException upre) {
              upre.printStackTrace();
            }
            model = part;
            modelFactory = part.createCopyFactory();
            modelString = modelString;
          } else if (attr.getNodeName().equals("parent")) {
            String parentString = attr.getNodeValue();
            parentString = parentString;
          } else if (attr.getNodeName().equals("altModel")) {
            String altModelName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
            Model altModel = null;
            try {
              altModel = (Model)Element.load(PersonBuilder.class.getResource(altModelName), null);
              vehicle.set(null);
            } catch (IOException ioe) {
              ioe.printStackTrace();
            } catch (UnresolvablePropertyReferencesException upre) {
              upre.printStackTrace();
            }
            altModel = altModel;
            altModelFactory = altModel.createCopyFactory();
            altModelCount++;
          }
        }
        
        commandInfos.addElement(cmdInfo);
      }
      else if (choiceNode.getNodeName().equals("addModelAndTexture")) {
        CommandInfo cmdInfo = new CommandInfo();
        id = "addModelAndTexture";
        NamedNodeMap attrs = choiceNode.getAttributes();
        for (int j = 0; j < attrs.getLength(); j++) {
          Node attr = attrs.item(j);
          if (attr.getNodeName().equals("icon")) {
            String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
            ImageIcon icon = new ImageIcon(PersonBuilder.class.getResource(imageFileName), imageFileName);
            imageIcon = icon;
          } else if (attr.getNodeName().equals("model")) {
            String modelString = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
            Model part = null;
            try {
              part = (Model)Element.load(PersonBuilder.class.getResource(modelString), null);
              vehicle.set(null);
            } catch (IOException ioe) {
              ioe.printStackTrace();
            } catch (UnresolvablePropertyReferencesException upre) {
              upre.printStackTrace();
            }
            model = part;
            modelFactory = part.createCopyFactory();
            modelString = modelString;
          } else if (attr.getNodeName().equals("parent")) {
            String parentString = attr.getNodeValue();
            parentString = parentString;
          } else if (attr.getNodeName().equals("texture")) {
            String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
            Image img = toolkit.createImage(PersonBuilder.class.getResource(imageFileName));
            try {
              MediaTracker tracker = new MediaTracker(this);
              tracker.addImage(img, 0);
              tracker.waitForID(0);
            }
            catch (InterruptedException localInterruptedException1) {}
            texture = img;
          } else if (attr.getNodeName().equals("layer")) {
            String layerString = attr.getNodeValue();
            int layer = Integer.parseInt(layerString);
            level = layer;
          } else if (attr.getNodeName().equals("useAltModel")) {
            String altModelName = attr.getNodeValue();
            altModelName = altModelName;
          } else if (attr.getNodeName().equals("x")) {
            x = Double.parseDouble(attr.getNodeValue());
          } else if (attr.getNodeName().equals("y")) {
            y = Double.parseDouble(attr.getNodeValue());
          } else if (attr.getNodeName().equals("z")) {
            z = Double.parseDouble(attr.getNodeValue());
          }
        }
        commandInfos.addElement(cmdInfo);
      } else if (choiceNode.getNodeName().equals("setMultipleModelsAndTexture")) {
        CommandInfo cmdInfo = new CommandInfo();
        id = "setMultipleModelsAndTexture";
        NamedNodeMap attrs = choiceNode.getAttributes();
        for (int j = 0; j < attrs.getLength(); j++) {
          Node attr = attrs.item(j);
          if (attr.getNodeName().equals("icon")) {
            String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
            ImageIcon icon = new ImageIcon(PersonBuilder.class.getResource(imageFileName), imageFileName);
            imageIcon = icon;
          } else if (attr.getNodeName().equals("model1")) {
            String modelString = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
            Model part = null;
            try {
              part = (Model)Element.load(PersonBuilder.class.getResource(modelString), null);
              vehicle.set(null);
            } catch (IOException ioe) {
              ioe.printStackTrace();
            } catch (UnresolvablePropertyReferencesException upre) {
              upre.printStackTrace();
            }
            model = part;
            modelFactory = part.createCopyFactory();
            modelString = modelString;
          } else if (attr.getNodeName().equals("parent1")) {
            String parentString = attr.getNodeValue();
            parentString = parentString;
          } else if (attr.getNodeName().equals("model2")) {
            String modelString = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
            Model part = null;
            try {
              part = (Model)Element.load(PersonBuilder.class.getResource(modelString), null);
              vehicle.set(null);
            } catch (IOException ioe) {
              ioe.printStackTrace();
            } catch (UnresolvablePropertyReferencesException upre) {
              upre.printStackTrace();
            }
            altModel = part;
            altModelFactory = part.createCopyFactory();
          }
          else if (attr.getNodeName().equals("parent2")) {
            String parentString = attr.getNodeValue();
            altParentString = parentString;
          } else if (attr.getNodeName().equals("texture")) {
            String imageFileName = XMLDirectoryUtilities.getPath(dirNode) + "/" + attr.getNodeValue();
            Image img = toolkit.createImage(PersonBuilder.class.getResource(imageFileName));
            try {
              MediaTracker tracker = new MediaTracker(this);
              tracker.addImage(img, 0);
              tracker.waitForID(0);
            }
            catch (InterruptedException localInterruptedException2) {}
            texture = img;
          } else if (attr.getNodeName().equals("layer")) {
            String layerString = attr.getNodeValue();
            int layer = Integer.parseInt(layerString);
            level = layer;
          }
        }
        commandInfos.addElement(cmdInfo);
      }
    }
    if (altModelCount > 0) {
      modelWrapper.registerItemChooserWithAlt(this);
    }
  }
  
  private Model reloadModel(String modelString) {
    Model part = null;
    try {
      part = (Model)Element.load(PersonBuilder.class.getResource(modelString), new Model());
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (UnresolvablePropertyReferencesException upre) {
      upre.printStackTrace();
    }
    return part;
  }
  
  private void initializeChoices(Node itemsNode) {
    Vector allImages = XMLDirectoryUtilities.getImages(itemsNode);
    Document configDoc = loadXMLFile(itemsNode);
    
    if ((allImages != null) && (allImages.size() > 0) && (configDoc != null)) {
      NodeList choices = configDoc.getChildNodes();
      for (int i = 0; i < choices.getLength(); i++) {
        initializeCommandInfos(itemsNode, choices.item(i));
      }
    }
  }
  
  private Document loadXMLFile(Node itemsNode) {
    Document document = null;
    URL fileURL = null;
    
    Vector xmlFiles = XMLDirectoryUtilities.getXMLURLs(itemsNode);
    if (xmlFiles.size() == 0) {
      System.out.println(Messages.getString("No_xml_file_found__"));
    } else if (xmlFiles.size() > 1) {
      System.out.println(Messages.getString("Multiple_xml_files_found__"));
    } else {
      fileURL = (URL)xmlFiles.elementAt(0);
    }
    
    document = (Document)XMLDirectoryUtilities.loadURL(fileURL);
    return document;
  }
  
  private void currentLosingFocus() {
    if ((index >= 0) && (index < commandInfos.size())) {
      CommandInfo currentInfo = (CommandInfo)commandInfos.elementAt(index);
      if ((id.equals("addTexture")) && (altModelName != null)) {
        modelWrapper.switchToOrigModel(altModelName);
      } else if (id.equals("addTexture")) {
        modelWrapper.clearLevel(level);
      } else if (id.equals("addModelAndTexture"))
      {
        modelWrapper.removeModel(model.name.getStringValue());
        
        modelWrapper.clearLevel(level);
        

        if (altModelName != null) {
          modelWrapper.switchToOrigModel(altModelName);
        }
      }
    }
  }
  
  private void setIcon() {
    try {
      if ((index >= 0) && (index < commandInfos.size())) {
        CommandInfo currentInfo = (CommandInfo)commandInfos.elementAt(index);
        itemPicture.setIcon(imageIcon);
        if (id.equals("addTexture")) {
          modelWrapper.addTexture(texture, level);
          if (altModelName != null) {
            modelWrapper.switchToAltModel(altModelName);
          }
        } else if (id.equals("setModel")) {
          Model modelCopy = (Model)modelFactory.manufactureCopy(null);
          modelWrapper.setModel(modelCopy, parentString);
        } else if (id.equals("addModelAndTexture"))
        {
          modelWrapper.addTexture(texture, level);
          

          Model modelCopy = (Model)modelFactory.manufactureCopy(null);
          Vector3 position = new Vector3(x, y, z);
          modelWrapper.addModel(modelCopy, parentString, position);
          

          if (altModelName != null) {
            modelWrapper.switchToAltModel(altModelName);
          }
        } else if (id.equals("setMultipleModelsAndTexture"))
        {
          Model modelCopy = (Model)modelFactory.manufactureCopy(null);
          modelWrapper.setModel(modelCopy, parentString);
          
          modelCopy = (Model)altModelFactory.manufactureCopy(null);
          modelWrapper.setModel(modelCopy, altParentString);
          

          modelWrapper.addTexture(texture, level);
        }
      }
    } catch (UnresolvablePropertyReferencesException upre) {
      throw new ExceptionWrapper(upre, "UnresolvablePropertyReferencesException");
    }
  }
  


  public Model getAltModel()
  {
    CommandInfo currentInfo = (CommandInfo)commandInfos.elementAt(index);
    if (altModelFactory != null) {
      try {
        return (Model)altModelFactory.manufactureCopy(null);
      } catch (UnresolvablePropertyReferencesException upre) {
        throw new ExceptionWrapper(upre, "UnresolvablePropertyReferencesException");
      }
    }
    return null;
  }
  
  public Model getOriginalModel()
  {
    CommandInfo currentInfo = (CommandInfo)commandInfos.elementAt(index);
    if (modelFactory != null) {
      try {
        return (Model)modelFactory.manufactureCopy(null);
      } catch (UnresolvablePropertyReferencesException upre) {
        throw new ExceptionWrapper(upre, "UnresolvablePropertyReferencesException");
      }
    }
    return null;
  }
  
  protected class CommandInfo
  {
    public String id = new String();
    public ImageIcon imageIcon = null;
    public String modelString = null;
    public Model model = null;
    public Model altModel = null;
    public CopyFactory modelFactory = null;
    public CopyFactory altModelFactory = null;
    public String altModelName = null;
    public String altParentString = null;
    public Image texture = null;
    public int level = -1;
    public String parentString = new String();
    public double x = 0.0D;
    public double y = 0.0D;
    public double z = 0.0D;
    
    protected CommandInfo() {}
  }
  
  private void jbInit() throws Exception { setBackground(new Color(155, 159, 206));
    itemPicture.setMaximumSize(new Dimension(110, 110));
    itemPicture.setMinimumSize(new Dimension(110, 110));
    itemPicture.setPreferredSize(new Dimension(110, 110));
    setLayout(borderLayout1);
    backButton.setIcon(backImage);
    backButton.setBackground(new Color(155, 159, 206));
    backButton.setBorder(null);
    backButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        backButton_actionPerformed(e);
      }
    });
    forwardButton.setActionCommand("next");
    forwardButton.setIcon(nextImage);
    forwardButton.setBackground(new Color(155, 159, 206));
    forwardButton.setBorder(null);
    forwardButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        forwardButton_actionPerformed(e);
      }
    });
    add(itemPicture, "Center");
    add(backButton, "West");
    add(forwardButton, "East");
  }
  
  void backButton_actionPerformed(ActionEvent e) {
    currentLosingFocus();
    index -= 1;
    
    if (index < 0)
      index += commandInfos.size();
    setIcon();
  }
  
  void forwardButton_actionPerformed(ActionEvent e) {
    currentLosingFocus();
    index += 1;
    
    if (index > commandInfos.size() - 1)
      index -= commandInfos.size();
    setIcon();
  }
}
