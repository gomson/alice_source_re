package edu.cmu.cs.stage3.caitlin.personbuilder;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.CopyFactory;
import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.core.criterion.ElementNamedCriterion;
import edu.cmu.cs.stage3.alice.core.light.DirectionalLight;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TextureMapProperty;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.util.HowMuch;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Node;

public class ModelWrapper
{
  protected World miniWorld;
  protected SymmetricPerspectiveCamera camera;
  protected DirectionalLight directionalLight;
  protected OnscreenRenderTarget renderTarget;
  protected java.awt.event.MouseListener renderTargetMouseListener;
  protected AuthoringTool authoringTool;
  protected RotateManipulator rtom;
  protected Model person = null;
  protected Model template = null;
  protected CopyFactory personFactory = null;
  protected Image[] textureLayers = new Image[10];
  protected Hashtable partsTable = new Hashtable();
  protected java.net.URL url = null;
  
  protected Vector propertyNameList = new Vector();
  protected Vector propertyValueList = new Vector();
  protected Vector propertyDescList = new Vector();
  protected Vector itemChoosersWithAlts = new Vector();
  
  public ModelWrapper(Node root) {
    worldInit();
    try {
      loadInitModel(root);
    } catch (Exception e) {
      e.printStackTrace();
    }
    makeNewPerson();
  }
  
  public void registerItemChooserWithAlt(ItemChooser itemChooser) {
    itemChoosersWithAlts.addElement(itemChooser);
  }
  
  protected void replaceModel(String modelName, Model model) {
    if ((model != null) && 
      (name.getStringValue().equals(modelName)) && 
      (person != null)) {
      ElementNamedCriterion nameCriterion = 
        new ElementNamedCriterion(modelName);
      Element[] parts = person.search(nameCriterion, HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      isFirstClass.set(false);
      if (parts.length > 0) {
        Element part = null;
        part = parts[0];
        if (part != null) {
          Vector3 posToParent = 
            ((Model)part).getPosition((ReferenceFrame)part.getParent());
          edu.cmu.cs.stage3.math.Matrix33 orientToParent = 
            ((Model)part).getOrientationAsAxes((ReferenceFrame)part.getParent());
          part.replaceWith(model);
          if ((part instanceof Model)) {
            vehicle.set(vehicle.get());
            vehicle.set(null);
            if (posToParent != null)
              model.setPositionRightNow(posToParent, (ReferenceFrame)model.getParent());
            if (orientToParent != null) {
              model.setOrientationRightNow(orientToParent, (ReferenceFrame)model.getParent());
            }
            TextureMap tMap = person.diffuseColorMap.getTextureMapValue();
            person.diffuseColorMap.set(tMap, HowMuch.INSTANCE_AND_PARTS);
          }
        }
      }
      else {
        System.out.println(name.getStringValue() + " is not found");
      }
    }
  }
  

  public void switchToAltModel(String modelName)
  {
    for (int i = 0; i < itemChoosersWithAlts.size(); i++) {
      ItemChooser itemChooser = (ItemChooser)itemChoosersWithAlts.elementAt(i);
      Model model = itemChooser.getAltModel();
      replaceModel(modelName, model);
    }
  }
  
  public void switchToOrigModel(String modelName)
  {
    for (int i = 0; i < itemChoosersWithAlts.size(); i++) {
      ItemChooser itemChooser = (ItemChooser)itemChoosersWithAlts.elementAt(i);
      Model model = itemChooser.getOriginalModel();
      replaceModel(modelName, model);
    }
  }
  
  public void resetWorld()
  {
    partsTable = new Hashtable();
    textureLayers = new Image[10];
    makeNewPerson();
  }
  
  protected void worldInit() {
    miniWorld = new World();
    camera = new SymmetricPerspectiveCamera();
    directionalLight = new DirectionalLight();
    
    camera.vehicle.set(miniWorld);
    camera.setPositionRightNow(0.0D, 1.5D, 6.0D);
    camera.verticalViewingAngle.set(new Double(0.5235987755982988D));
    directionalLight.vehicle.set(camera);
    directionalLight.setOrientationRightNow(camera.getOrientationAsQuaternion());
    directionalLight.turnRightNow(Direction.FORWARD, 0.15D);
    directionalLight.turnRightNow(Direction.LEFT, 0.075D);
    directionalLight.color.set(edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE);
    
    java.awt.Color dkBlue = new java.awt.Color(12, 36, 106);
    miniWorld.atmosphereColor.set(new edu.cmu.cs.stage3.alice.scenegraph.Color(dkBlue));
    miniWorld.ambientLightColor.set(edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY);
  }
  
  protected void makeNewPerson() {
    if (personFactory == null) {
      try {
        personFactory = template.createCopyFactory();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    try {
      person = ((Model)personFactory.manufactureCopy(null));
      addModelToWorld(person, "none", null);
    } catch (UnresolvablePropertyReferencesException upre) {
      throw new edu.cmu.cs.stage3.alice.core.ExceptionWrapper(upre, "UnresolvablePropertyReferencesException");
    }
  }
  
  protected void loadInitModel(Node root) {
    Vector modelURLs = XMLDirectoryUtilities.getModelURLs(root);
    for (int i = 0; i < modelURLs.size(); i++) {
      url = ((java.net.URL)modelURLs.elementAt(i));
      try {
        template = ((Model)Element.load(url, null));
      } catch (IOException ioe) {
        ioe.printStackTrace();
      } catch (UnresolvablePropertyReferencesException upre) {
        upre.printStackTrace();
      }
    }
  }
  
  private void initializeModels(Model part, String parentName, Vector3 position)
  {
    Model partsToAttach = (Model)partsTable.get(part.getKey());
    if ((partsToAttach != null) && (partsToAttach.getParent() == null)) {
      addChildToModel(part, partsToAttach, position);
    }
  }
  
  private Element[] removeModelFromWorld(Model model) {
    if (model != null) {
      Element[] kids = model.getChildren();
      model.removeFromParent();
      vehicle.set(null);
      return kids;
    }
    return null;
  }
  
  private void removeAllKids(Model parent) {
    if (parent.getChildCount() > 0) {
      Element[] oldKids = parent.getChildren();
      for (int i = 0; i < oldKids.length; i++) {
        if ((oldKids[i] instanceof Model)) {
          oldKids[i].removeFromParent();
          Model oldModel = (Model)oldKids[i];
          vehicle.set(null);
          parent.removeChild(oldModel);
        }
      }
    }
  }
  
  private void addKidsToModel(Model newParent, Element[] kids)
  {
    removeAllKids(newParent);
    if ((newParent != null) && (kids != null)) {
      for (int i = 0; i < kids.length; i++) {
        if ((kids[i] instanceof Model)) {
          Model kidModel = (Model)kids[i];
          addChildToModel(newParent, kidModel, null);
        }
      }
    }
  }
  
  private void addChildToModel(Model parent, Model child, Vector3 position) {
    parent.addChild(child);
    parts.add(child);
    child.setParent(parent);
    isFirstClass.set(false);
    vehicle.set(parent);
    if (position != null)
      child.setPositionRightNow(position, parent);
  }
  
  private void addModelToWorld(Model model, String parent, Vector3 position) {
    if (parent.equals("none")) {
      person = model;
      regenerateTexture();
      person.vehicle.set(miniWorld);
      camera.pointAtRightNow(person);
    }
  }
  
  public void removeModel() {
    if (person != null)
      person.vehicle.set(null);
  }
  
  private void renderInit() {
    authoringTool = AuthoringTool.getHack();
    renderTarget = authoringTool.getRenderTargetFactory().createOnscreenRenderTarget();
    if (renderTarget != null) {
      renderTarget.addCamera(camera.getSceneGraphCamera());
      rtom = new RotateManipulator(renderTarget);
      rtom.setTransformableToRotate(person);
    } else {
      System.err.println("PersonBuilder unable to create renderTarget");
    }
  }
  
  public java.awt.Component getRenderPanel() {
    renderInit();
    return renderTarget.getAWTComponent();
  }
  
  public Model getModel() {
    return person;
  }
  



  protected void regenerateTexture()
  {
    BufferedImage finalTexture = null;
    Graphics2D g2 = null;
    
    for (int i = 0; i < textureLayers.length; i++) {
      if (textureLayers[0] == null) {
        Image im = person.diffuseColorMap.getTextureMapValue().image.getImageValue();
        textureLayers[0] = im;
      }
      if (textureLayers[i] != null) {
        if (finalTexture == null) {
          finalTexture = 
            new BufferedImage(
            textureLayers[i].getHeight(null), 
            textureLayers[i].getWidth(null), 
            6);
          g2 = finalTexture.createGraphics();
        }
        g2.drawImage(textureLayers[i], 0, 0, null);
      }
    }
    
    if (finalTexture != null) {
      person.diffuseColorMap.getTextureMapValue().image.set(finalTexture);
      person.diffuseColorMap.getTextureMapValue().touchImage();
    }
  }
  
  public void addTexture(Image texture, int level) {
    textureLayers[level] = texture;
    regenerateTexture();
  }
  
  public void clearLevel(int level) {
    textureLayers[level] = null;
  }
  
  public void addModel(Model modelToAdd, String parentName, Vector3 position) {
    if (person != null) {
      ElementNamedCriterion nameCriterion = 
        new ElementNamedCriterion(parentName);
      Element[] parents = person.search(nameCriterion, HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      if (parents.length > 0) {
        modelToAdd.setParent(parents[0]);
        0parts.add(modelToAdd);
        vehicle.set(parents[0]);
        isFirstClass.set(false);
        
        modelToAdd.setPositionRightNow(position, (ReferenceFrame)parents[0]);
      }
    }
    TextureMap tMap = person.diffuseColorMap.getTextureMapValue();
    diffuseColorMap.set(tMap, HowMuch.INSTANCE_AND_PARTS);
    person.diffuseColorMap.set(tMap, HowMuch.INSTANCE_AND_PARTS);
  }
  
  public void removeModel(String modelName) {
    if (person != null) {
      ElementNamedCriterion nameCriterion = 
        new ElementNamedCriterion(modelName);
      Element[] models = person.search(nameCriterion, HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      if (models.length > 0) {
        models[0].getParent().removeChild(models[0]);
        0vehicle.set(null);
      }
    }
    TextureMap tMap = person.diffuseColorMap.getTextureMapValue();
    person.diffuseColorMap.set(tMap, HowMuch.INSTANCE_AND_PARTS);
  }
  
  private void rootModelChanged() {
    regenerateTexture();
    
    for (int i = 0; i < propertyNameList.size(); i++) {
      String propName = (String)propertyNameList.elementAt(i);
      String propValue = (String)propertyValueList.elementAt(i);
      String propDesc = (String)propertyDescList.elementAt(i);
      
      setPropertyValue(propName, propValue, propDesc);
    }
  }
  
  public String getModelName()
  {
    if (person != null) {
      return person.name.getStringValue();
    }
    return "";
  }
  
  public void setModel(Model part, String parentName) {
    if ((!parentName.equals("none")) || (person != null))
    {
      if (person != null) {
        ElementNamedCriterion nameCriterion = 
          new ElementNamedCriterion(parentName);
        Element[] parents = person.search(nameCriterion, HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
        if (parents.length > 0) {
          Element child = parents[0].getChildNamed(name.getStringValue());
          if (child != null) {
            isFirstClass.set(false);
            Vector3 posToParent = ((Model)child).getPosition((ReferenceFrame)parents[0]);
            child.replaceWith(part);
            if ((child instanceof Model)) {
              vehicle.set(vehicle.get());
              vehicle.set(null);
              if (posToParent != null) {
                part.setPositionRightNow(posToParent, (ReferenceFrame)part.getParent());
              }
            }
          }
        } else {
          System.out.println(name.getStringValue() + " is not found");
        }
      }
      TextureMap tMap = person.diffuseColorMap.getTextureMapValue();
      person.diffuseColorMap.set(tMap, HowMuch.INSTANCE_AND_PARTS);
    }
  }
  
  private void setPropertyValue(String propertyName, String propertyValue, String propertyDescription) {
    edu.cmu.cs.stage3.alice.core.Property property = person.getPropertyNamed(propertyName);
    if ((property != null) && ((property instanceof StringProperty))) {
      property.set(propertyValue);
    } else if ((property != null) && ((property instanceof DictionaryProperty))) {
      ((DictionaryProperty)property).put(propertyDescription, propertyValue);
    }
  }
  
  public void setProperty(String propertyName, String propertyValue, String propertyDesc) {
    int propertyIndex = propertyNameList.indexOf(propertyName);
    

    if (propertyIndex != -1) {
      propertyValueList.setElementAt(propertyValue, propertyIndex);
      if (propertyDesc != null) {
        propertyDescList.setElementAt(propertyDesc, propertyIndex);
      } else
        propertyDescList.setElementAt("", propertyIndex);
    } else {
      propertyNameList.addElement(propertyName);
      propertyValueList.addElement(propertyValue);
      if (propertyDesc != null) {
        propertyDescList.addElement(propertyDesc);
      } else {
        propertyDescList.addElement("");
      }
    }
    setPropertyValue(propertyName, propertyValue, propertyDesc);
  }
  
  public void setColor(java.awt.Color color) {
    BufferedImage baseColor = new BufferedImage(512, 512, 2);
    Graphics2D g = (Graphics2D)baseColor.getGraphics();
    g.setColor(color);
    g.fillRect(0, 0, 512, 512);
    addTexture(baseColor, 0);
  }
}
