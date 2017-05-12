package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.core.Code;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Group;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.alice.core.response.DoInOrder;
import edu.cmu.cs.stage3.alice.core.response.DoTogether;
import edu.cmu.cs.stage3.alice.core.response.PropertyAnimation;
import edu.cmu.cs.stage3.alice.core.response.TurnAnimation;
import edu.cmu.cs.stage3.alice.core.response.Wait;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.HowMuch;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

public class ElementPopupUtilities
{
  protected static Configuration authoringToolConfig = Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());
  
  protected static Class[] elementPopupRunnableParams = { Element.class };
  private static Runnable emptyRunnable = new Runnable() { public void run() {}
  };
  
  public ElementPopupUtilities() {}
  
  public static void createAndShowElementPopupMenu(Element element, Vector structure, java.awt.Component component, int x, int y) { javax.swing.JPopupMenu popup = makeElementPopupMenu(element, structure);
    popup.show(component, x, y);
    PopupMenuUtilities.ensurePopupIsOnScreen(popup);
  }
  

  /**
   * @deprecated
   */
  public static javax.swing.JPopupMenu makeElementPopup(Element element, Vector structure) { return makeElementPopupMenu(element, structure); }
  
  public static javax.swing.JPopupMenu makeElementPopupMenu(Element element, Vector structure) {
    if ((element != null) && (structure != null)) {
      Object[] initArgs = { element };
      substituteRunnables(initArgs, structure);
      return PopupMenuUtilities.makePopupMenu(structure);
    }
    return null;
  }
  
  public static void substituteRunnables(Object[] initArgs, Vector structure)
  {
    for (java.util.ListIterator iter = structure.listIterator(); iter.hasNext();) {
      Object o = iter.next();
      if (((o instanceof Class)) && (ElementPopupRunnable.class.isAssignableFrom((Class)o))) {
        try {
          ElementPopupRunnable r = (ElementPopupRunnable)((Class)o).getConstructor(elementPopupRunnableParams).newInstance(initArgs);
          StringObjectPair newPair = new StringObjectPair(r.getDefaultLabel(), r);
          iter.set(newPair);
        } catch (NoSuchMethodException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_building_popup_"), e);
        } catch (IllegalAccessException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_building_popup_"), e);
        } catch (InstantiationException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_building_popup_"), e);
        } catch (java.lang.reflect.InvocationTargetException e) {
          AuthoringTool.showErrorDialog(Messages.getString("Error_building_popup_"), e);
        }
      } else if ((o instanceof ElementPopupRunnable)) {
        ElementPopupRunnable r = (ElementPopupRunnable)o;
        StringObjectPair newPair = new StringObjectPair(r.getDefaultLabel(), r);
        iter.set(newPair);
      } else if ((o instanceof StringObjectPair)) {
        StringObjectPair pair = (StringObjectPair)o;
        if (((pair.getObject() instanceof Class)) && (ElementPopupRunnable.class.isAssignableFrom((Class)pair.getObject()))) {
          try {
            StringObjectPair newPair = new StringObjectPair(pair.getString(), ((Class)pair.getObject()).getConstructor(elementPopupRunnableParams).newInstance(initArgs));
            iter.set(newPair);
          }
          catch (NoSuchMethodException e) {
            AuthoringTool.showErrorDialog(Messages.getString("Error_building_popup_"), e);
          } catch (IllegalAccessException e) {
            AuthoringTool.showErrorDialog(Messages.getString("Error_building_popup_"), e);
          } catch (InstantiationException e) {
            AuthoringTool.showErrorDialog(Messages.getString("Error_building_popup_"), e);
          } catch (java.lang.reflect.InvocationTargetException e) {
            AuthoringTool.showErrorDialog(Messages.getString("Error_building_popup_"), e);
          }
        } else if ((pair.getObject() instanceof Vector)) {
          substituteRunnables(initArgs, (Vector)pair.getObject());
        }
      }
    }
  }
  
  public static Vector makeCoerceToStructure(Element element) {
    if ((element != null) && (element.isCoercionSupported())) {
      Vector structure = new Vector();
      Vector subStructure = new Vector();
      
      Class[] classes = element.getSupportedCoercionClasses();
      if (classes != null) {
        for (int i = 0; i < classes.length; i++) {
          final Class c = classes[i];
          if ((element instanceof TurnAnimation)) {
            TurnAnimation turnAnimation = (TurnAnimation)element;
            if (((direction.get() == edu.cmu.cs.stage3.alice.core.Direction.FORWARD) || (direction.get() == edu.cmu.cs.stage3.alice.core.Direction.BACKWARD)) && 
              (edu.cmu.cs.stage3.alice.core.response.RollAnimation.class.isAssignableFrom(c))) {}

          }
          else
          {
            String repr = AuthoringToolResources.getReprForValue(c);
            Runnable runnable = new Runnable() {
              public void run() {
                AuthoringTool.getHack().getUndoRedoStack().startCompound();
                coerceTo(c);
                AuthoringTool.getHack().getUndoRedoStack().stopCompound();
              }
            };
            subStructure.add(new StringObjectPair(repr, runnable));
          } }
        if (subStructure.size() > 0) {
          structure.add(new StringObjectPair(Messages.getString("change_to"), subStructure));
          return structure;
        }
        
        return null;
      }
    }
    

    return null;
  }
  
  public static Vector getDefaultStructure(Element element) {
    return getDefaultStructure(element, true, null, null, null);
  }
  
  public static Vector getDefaultStructure(Element element, boolean elementEnabled, AuthoringTool authoringTool, JTree jtree, TreePath treePath) {
    if ((element instanceof Response))
      return getDefaultResponseStructure((Response)element);
    if ((element instanceof edu.cmu.cs.stage3.alice.core.Question))
      return getDefaultQuestionStructure((edu.cmu.cs.stage3.alice.core.Question)element);
    if (AuthoringToolResources.characterCriterion.accept(element))
      return getDefaultCharacterStructure(element, elementEnabled, authoringTool, jtree, treePath);
    if ((element instanceof edu.cmu.cs.stage3.alice.core.World))
      return getDefaultWorldStructure((edu.cmu.cs.stage3.alice.core.World)element);
    if ((element instanceof Group)) {
      return getDefaultGroupStructure((Group)element, jtree, treePath);
    }
    return getDefaultElementStructure(element, jtree, treePath);
  }
  
  public static Vector getDefaultCharacterStructure(Element element, boolean elementEnabled, AuthoringTool authoringTool, JTree jtree, TreePath treePath)
  {
    Vector popupStructure = new Vector();
    popupStructure.add(new StringObjectPair(AuthoringToolResources.getReprForValue(element), null));
    popupStructure.add(new StringObjectPair("separator", javax.swing.JSeparator.class));
    

    if (elementEnabled) {
      popupStructure.add(new StringObjectPair(Messages.getString("methods"), PopupMenuUtilities.makeDefaultOneShotStructure(element)));
    }
    if ((jtree != null) && (treePath != null)) {
      Runnable renameRunnable = new RenameRunnable(element, jtree, treePath);
      popupStructure.add(renameRunnable);
    }
    if (((element instanceof edu.cmu.cs.stage3.alice.core.Sandbox)) && (authoringToolConfig.getValue("enableScripting").equalsIgnoreCase("true"))) {
      popupStructure.add(EditScriptRunnable.class);
    }
    

    if (AuthoringToolResources.characterCriterion.accept(element)) {
      if (((element instanceof Transformable)) && 
        (!(element instanceof edu.cmu.cs.stage3.alice.core.Camera))) {
        popupStructure.add(GetAGoodLookAtRunnable.class);
        popupStructure.add(StorePoseRunnable.class);
      }
      

      popupStructure.add(DeleteRunnable.class);
      popupStructure.add(SaveCharacterRunnable.class);


    }
    else
    {

      popupStructure.add(DeleteRunnable.class);
    }
    






    return popupStructure;
  }
  
  public static Vector getDefaultWorldStructure(edu.cmu.cs.stage3.alice.core.World world) {
    Vector popupStructure = new Vector();
    popupStructure.add(EditScriptRunnable.class);
    return popupStructure;
  }
  
  public static Vector getDefaultResponseStructure(Response response) {
    Vector structure = new Vector();
    structure.add(MakeCopyRunnable.class);
    structure.add(DeleteRunnable.class);
    structure.add(ToggleCommentingRunnable.class);
    Vector coerceToStructure = makeCoerceToStructure(response);
    if (coerceToStructure != null) {
      structure.addAll(coerceToStructure);
    }
    
    return structure;
  }
  
  public static Vector getDefaultQuestionStructure(edu.cmu.cs.stage3.alice.core.Question question) {
    Vector structure = new Vector();
    
    structure.add(DeleteRunnable.class);
    
    Vector coerceToStructure = makeCoerceToStructure(question);
    if (coerceToStructure != null) {
      structure.addAll(coerceToStructure);
    }
    
    return structure;
  }
  
  public static Vector getDefaultGroupStructure(Group group, JTree jtree, TreePath treePath) {
    Vector structure = new Vector();
    
    structure.add(new StringObjectPair(AuthoringToolResources.getReprForValue(group), emptyRunnable));
    structure.add(new StringObjectPair("separator", javax.swing.JSeparator.class));
    structure.add(SortGroupAlphabeticallyRunnable.class);
    if ((jtree != null) && (treePath != null)) {
      Runnable renameRunnable = new RenameRunnable(group, jtree, treePath);
      structure.add(renameRunnable);
    }
    structure.add(DeleteRunnable.class);
    
    return structure;
  }
  
  public static Vector getDefaultElementStructure(Element element, JTree jtree, TreePath treePath) {
    Vector structure = new Vector();
    if ((jtree != null) && (treePath != null)) {
      Runnable renameRunnable = new RenameRunnable(element, jtree, treePath);
      structure.add(renameRunnable);
    }
    structure.add(DeleteRunnable.class);
    
    return structure;
  }
  
  public static abstract class ElementPopupRunnable implements Runnable {
    protected Element element;
    
    protected ElementPopupRunnable(Element element) {
      this.element = element;
    }
    
    public Element getElement() {
      return element;
    }
    
    public abstract String getDefaultLabel();
  }
  
  public static class DeleteRunnable extends ElementPopupUtilities.ElementPopupRunnable
  {
    public static final edu.cmu.cs.stage3.util.Criterion namedHeadCriterion = new edu.cmu.cs.stage3.util.Criterion() {
      public boolean accept(Object o) {
        if (((o instanceof Transformable)) && 
          ("head".equalsIgnoreCase(name.getStringValue()))) {
          return true;
        }
        
        return false;
      }
    };
    protected AuthoringTool authoringTool;
    
    public DeleteRunnable(Element element) {
      super();
      authoringTool = AuthoringTool.getHack();
    }
    
    public DeleteRunnable(Element element, AuthoringTool authoringTool) {
      super();
      this.authoringTool = authoringTool;
    }
    
    public String getDefaultLabel() {
      return Messages.getString("delete");
    }
    
    public void run() {
      if ((element instanceof edu.cmu.cs.stage3.alice.core.Camera)) {
        String message = Messages.getString("The_Camera_is_a_critical_part_of_the_World___Very_bad_things_can_happen_if_you_delete_the_Camera__nAre_you_sure_you_want_to_delete_it_");
        int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(message, Messages.getString("Delete_Camera_"), 0);
        if (result == 0) {}

      }
      else if (((element instanceof edu.cmu.cs.stage3.alice.core.light.DirectionalLight)) && 
        (element.getRoot().getDescendants(edu.cmu.cs.stage3.alice.core.light.DirectionalLight.class).length == 1)) {
        String message = Messages.getString("You_are_about_to_delete_the_last_directional_light_in_the_World___If_you_do_this__everything_will_probably_become_very_dark__nAre_you_sure_you_want_to_delete_it_");
        int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(message, Messages.getString("Delete_Light_"), 0);
        if (result != 0) {
          return;
        }
      }
      


      edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = element.getRoot().getPropertyReferencesTo(element, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true);
      
      if (references.length > 0) {
        AuthoringToolResources.garbageCollectIfPossible(references);
        references = element.getRoot().getPropertyReferencesTo(element, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true);
      }
      
      if (references.length > 0) {
        for (int i = 0; i < references.length; i++) {
          Element refReferenceI = references[i].getReference();
          Property refPropertyI = references[i].getProperty();
          Element refOwnerI = refPropertyI.getOwner();
          if ((references[i].getProperty().isAlsoKnownAs(edu.cmu.cs.stage3.alice.core.Sandbox.class, "textureMaps")) && 
            ((refOwnerI instanceof edu.cmu.cs.stage3.alice.core.Sandbox))) {
            refReferenceI.setParent(refOwnerI);
          }
          



          if ((references[i].getProperty().isAlsoKnownAs(Model.class, "geometry")) && 
            ((refOwnerI instanceof Model))) {
            refReferenceI.setParent(refOwnerI);
          }
        }
        
        references = element.getRoot().getPropertyReferencesTo(element, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true);
      }
      if (references.length > 0) {
        edu.cmu.cs.stage3.alice.authoringtool.dialog.DeleteContentPane.showDeleteDialog(this, authoringTool);



      }
      else
      {



        AuthoringTool.getHack().getUndoRedoStack().startCompound();
        
        if ((element instanceof Group)) {
          for (int i = 0; i < element.getChildCount(); i++) {
            if ((element.getChildAt(i) instanceof Transformable)) {
              if ((element.getChildAt(i) instanceof Model)) {
                Model model = (Model)element.getChildAt(i);
                if ((vehicle.get() instanceof edu.cmu.cs.stage3.alice.core.ReferenceFrame)) {
                  Property[] affectedProperties = calculateAffectedProperties(model);
                  authoringTool.performOneShot(createDestroyResponse(model), createDestroyUndoResponse(model), affectedProperties);
                } else {
                  vehicle.set(null);
                }
              } else {
                element).vehicle.set(null);
              }
            }
          }
        }
        
        if ((element instanceof Transformable)) {
          if ((element instanceof Model)) {
            Model model = (Model)element;
            if ((vehicle.get() instanceof edu.cmu.cs.stage3.alice.core.ReferenceFrame)) {
              Property[] affectedProperties = calculateAffectedProperties(model);
              authoringTool.performOneShot(createDestroyResponse(model), createDestroyUndoResponse(model), affectedProperties);
            } else {
              vehicle.set(null);
            }
          } else {
            element).vehicle.set(null);
          }
        }
        Element parent = element.getParent();
        if (parent != null)
        {
          Property[] properties = parent.getProperties();
          for (int i = 0; i < properties.length; i++) {
            if (properties[i].get() == element) {
              properties[i].set(AuthoringToolResources.getDefaultValueForClass(properties[i].getValueClass()));
            } else if ((properties[i] instanceof ObjectArrayProperty)) {
              ObjectArrayProperty oap = (ObjectArrayProperty)properties[i];
              int j = 0;
              while (j < oap.size()) {
                if (oap.get(j) == element) {
                  oap.remove(j);
                } else {
                  j++;
                }
              }
            }
          }
          
          parent.removeChild(element);
        }
        
        AuthoringTool.getHack().getUndoRedoStack().stopCompound();
      }
    }
    
    protected Response createDestroyResponse(Model model) {
      TurnAnimation turnAnimation = new TurnAnimation();
      subject.set(model);
      direction.set(edu.cmu.cs.stage3.alice.core.Direction.LEFT);
      amount.set(new Double(10.0D));
      style.set(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY);
      PropertyAnimation opacityAnimation = new PropertyAnimation();
      element.set(model);
      propertyName.set("opacity");
      value.set(new Double(0.0D));
      howMuch.set(HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      style.set(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY);
      DoTogether doTogether = new DoTogether();
      componentResponses.add(turnAnimation);
      componentResponses.add(opacityAnimation);
      PropertyAnimation vehicleAnimation = new PropertyAnimation();
      element.set(model);
      propertyName.set("vehicle");
      value.set(null);
      duration.set(new Double(0.0D));
      howMuch.set(HowMuch.INSTANCE);
      Wait wait = new Wait();
      duration.set(new Double(0.2D));
      DoInOrder doInOrder = new DoInOrder();
      componentResponses.add(wait);
      Element[] heads = model.search(namedHeadCriterion);
      if ((heads != null) && (heads.length > 0)) {
        Element head = heads[0];
        if ((head instanceof Transformable)) {
          edu.cmu.cs.stage3.alice.core.Camera camera = authoringTool.getCurrentCamera();
          if (camera != null) {
            edu.cmu.cs.stage3.alice.core.response.PointAtAnimation pointAt = new edu.cmu.cs.stage3.alice.core.response.PointAtAnimation();
            subject.set(head);
            target.set(camera);
            duration.set(new Double(0.5D));
            componentResponses.add(pointAt);
            Wait wait2 = new Wait();
            duration.set(new Double(0.4D));
            componentResponses.add(wait2);
          }
        }
      }
      componentResponses.add(doTogether);
      componentResponses.add(vehicleAnimation);
      return doInOrder;
    }
    
    protected Response createDestroyUndoResponse(Model model) {
      TurnAnimation turnAnimation = new TurnAnimation();
      subject.set(model);
      direction.set(edu.cmu.cs.stage3.alice.core.Direction.RIGHT);
      amount.set(new Double(5.0D));
      style.set(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY);
      PropertyAnimation opacityAnimation = new PropertyAnimation();
      element.set(model);
      propertyName.set("opacity");
      value.set(opacity.get());
      howMuch.set(HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      style.set(edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY);
      duration.set(new Double(0.8D));
      DoTogether doTogether = new DoTogether();
      componentResponses.add(turnAnimation);
      componentResponses.add(opacityAnimation);
      PropertyAnimation vehicleAnimation = new PropertyAnimation();
      element.set(model);
      propertyName.set("vehicle");
      value.set(vehicle.get());
      duration.set(new Double(0.0D));
      howMuch.set(HowMuch.INSTANCE);
      DoInOrder doInOrder = new DoInOrder();
      componentResponses.add(vehicleAnimation);
      componentResponses.add(doTogether);
      Element[] heads = model.search(namedHeadCriterion);
      if ((heads != null) && (heads.length > 0)) {
        Element head = heads[0];
        if ((head instanceof Transformable)) {
          Wait wait2 = new Wait();
          duration.set(new Double(0.4D));
          componentResponses.add(wait2);
          edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation povAnimation = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
          subject.set(head);
          pointOfView.set(((Transformable)head).getPointOfView());
          duration.set(new Double(0.5D));
          componentResponses.add(povAnimation);
        }
      }
      return doInOrder;
    }
    
    protected Property[] calculateAffectedProperties(Model model) {
      Vector properties = new Vector();
      properties.add(localTransformation);
      properties.add(vehicle);
      Element[] descendants = model.getDescendants();
      for (int i = 0; i < descendants.length; i++) {
        if ((descendants[i] instanceof Model)) {
          properties.add(opacity);
          properties.add(localTransformation);
        }
      }
      return (Property[])properties.toArray(new Property[0]);
    }
  }
  
  public static class RenameRunnable extends ElementPopupUtilities.ElementPopupRunnable {
    private JTree jtree;
    private TreePath treePath;
    
    public RenameRunnable(Element element, JTree jtree, TreePath treePath) {
      super();
      this.jtree = jtree;
      this.treePath = treePath;
    }
    
    public String getDefaultLabel() {
      return Messages.getString("rename");
    }
    
    public void run() {
      jtree.startEditingAtPath(treePath);
    }
  }
  
  public static class MakeCopyRunnable extends ElementPopupUtilities.ElementPopupRunnable {
    public MakeCopyRunnable(Element element) {
      super();
    }
    
    public String getDefaultLabel() {
      return Messages.getString("make_copy");
    }
    
    public void run() {
      AuthoringTool.getHack().getUndoRedoStack().startCompound();
      
      String name = AuthoringToolResources.getNameForNewChild(element.name.getStringValue(), element.getParent());
      

      if ((element.getParent() instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse)) {
        int index = element.getParent()).componentResponses.indexOf(element);
        Element copy = element.HACK_createCopy(name, element.getParent(), index + 1, null, null);
        element.getParent()).componentResponses.add(index + 1, copy);
      } else if ((element.getParent() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite)) {
        int index = element.getParent()).components.indexOf(element);
        Element copy = element.HACK_createCopy(name, element.getParent(), index + 1, null, null);
        element.getParent()).components.add(index + 1, copy);
      } else {
        Element copy = element.createCopyNamed(name);
        AuthoringToolResources.addElementToAppropriateProperty(copy, copy.getParent());
      }
      
      AuthoringTool.getHack().getUndoRedoStack().stopCompound();
    }
  }
  
  public static class MakeSharedCopyRunnable extends ElementPopupUtilities.ElementPopupRunnable {
    protected Class[] classesToShare = {
      edu.cmu.cs.stage3.alice.core.Geometry.class, 
      edu.cmu.cs.stage3.alice.core.Sound.class, 
      edu.cmu.cs.stage3.alice.core.TextureMap.class };
    
    public MakeSharedCopyRunnable(Element element)
    {
      super();
    }
    
    public String getDefaultLabel() {
      return Messages.getString("make_shared_copy");
    }
    
    public void run() {
      AuthoringTool.getHack().getUndoRedoStack().startCompound();
      
      String name = AuthoringToolResources.getNameForNewChild(element.name.getStringValue(), element.getParent());
      Element copy = element.createCopyNamed(name, classesToShare);
      AuthoringToolResources.addElementToAppropriateProperty(copy, copy.getParent());
      
      AuthoringTool.getHack().getUndoRedoStack().stopCompound();
    }
  }
  
  public static class PrintStatisticsRunnable extends ElementPopupUtilities.ElementPopupRunnable {
    public PrintStatisticsRunnable(Element element) {
      super();
    }
    
    public String getDefaultLabel() {
      return Messages.getString("print_statistics");
    }
    
    public void run() {
      edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter itaCounter = new edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter();
      edu.cmu.cs.stage3.alice.core.util.TextureMapCounter textureMapCounter = new edu.cmu.cs.stage3.alice.core.util.TextureMapCounter();
      
      element.visit(itaCounter, HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      element.visit(textureMapCounter, HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
      
      System.out.println(Messages.getString("Statistics_for_") + AuthoringToolResources.getReprForValue(element) + ":");
      System.out.println("  " + Messages.getString("object_count__") + itaCounter.getShownIndexedTriangleArrayCount());
      System.out.println("    " + Messages.getString("face_count__") + itaCounter.getShownIndexCount() / 3);
      System.out.println("  " + Messages.getString("vertex_count__") + itaCounter.getShownVertexCount());
      System.out.println(" " + Messages.getString("texture_count__") + textureMapCounter.getTextureMapCount());
      System.out.println(Messages.getString("texture_memory__") + textureMapCounter.getTextureMapMemoryCount() + " bytes");
    }
  }
  
  public static class SaveCharacterRunnable extends ElementPopupUtilities.ElementPopupRunnable {
    protected AuthoringTool authoringTool;
    
    public SaveCharacterRunnable(Element element) {
      this(element, AuthoringTool.getHack());
    }
    
    public SaveCharacterRunnable(Element element, AuthoringTool authoringTool) {
      super();
      this.authoringTool = authoringTool;
    }
    
    public String getDefaultLabel() {
      return Messages.getString("save_object___");
    }
    
    public void run() {
      authoringTool.saveCharacter(element);
    }
  }
  


























  public static class ToggleCommentingRunnable
    extends ElementPopupUtilities.ElementPopupRunnable
  {
    public ToggleCommentingRunnable(Element element)
    {
      super();
      if (!(element instanceof Code)) {
        throw new IllegalArgumentException(Messages.getString("ToggleCommentRunnable_only_accepts_Responses_or_User_Defined_Questions__found__") + element);
      }
    }
    
    public String getDefaultLabel() {
      Code code = (Code)element;
      if (isCommentedOut.booleanValue()) {
        return Messages.getString("enable");
      }
      return Messages.getString("disable");
    }
    
    public void run()
    {
      Code code = (Code)element;
      isCommentedOut.set(isCommentedOut.booleanValue() ? Boolean.FALSE : Boolean.TRUE);
    }
  }
  
  public static class SetElementScopeRunnable extends ElementPopupUtilities.ElementPopupRunnable {
    private AuthoringTool authoringTool;
    
    public SetElementScopeRunnable(Element element, AuthoringTool authoringTool) {
      super();
      this.authoringTool = authoringTool;
    }
    
    public String getDefaultLabel() {
      return Messages.getString("switch_to_this_element_s_scope");
    }
    
    public void run() {
      authoringTool.setElementScope(element);
    }
  }
  
  public static class StorePoseRunnable extends ElementPopupUtilities.ElementPopupRunnable {
    protected AuthoringTool authoringTool;
    
    public StorePoseRunnable(Element element) {
      this(element, AuthoringTool.getHack());
    }
    
    public StorePoseRunnable(Element element, AuthoringTool authoringTool) {
      super();
      this.authoringTool = authoringTool;
    }
    
    public String getDefaultLabel() {
      return Messages.getString("capture_pose");
    }
    
    public void run() {
      if ((element instanceof Transformable)) {
        Transformable transformable = (Transformable)element;
        edu.cmu.cs.stage3.alice.core.Pose pose = edu.cmu.cs.stage3.alice.core.Pose.manufacturePose(transformable, transformable);
        name.set(AuthoringToolResources.getNameForNewChild("pose", element));
        element.addChild(pose);
        poses.add(pose);
      }
    }
  }
  
  public static class EditScriptRunnable extends ElementPopupUtilities.ElementPopupRunnable {
    protected AuthoringTool authoringTool;
    
    public EditScriptRunnable(Element element) {
      this(element, AuthoringTool.getHack());
    }
    
    public EditScriptRunnable(Element element, AuthoringTool authoringTool) {
      super();
      this.authoringTool = authoringTool;
    }
    
    public String getDefaultLabel() {
      return Messages.getString("edit_script");
    }
    
    public void run() {
      if ((element instanceof edu.cmu.cs.stage3.alice.core.Sandbox)) {
        authoringTool.editObject(element).script);
      }
    }
  }
  


























  public static class GetAGoodLookAtRunnable
    extends ElementPopupUtilities.ElementPopupRunnable
  {
    protected AuthoringTool authoringTool;
    


























    public GetAGoodLookAtRunnable(Element element)
    {
      this(element, AuthoringTool.getHack());
    }
    
    public GetAGoodLookAtRunnable(Element element, AuthoringTool authoringTool) {
      super();
      this.authoringTool = authoringTool;
    }
    
    public String getDefaultLabel() {
      return Messages.getString("Camera_get_a_good_look_at_this");
    }
    
    public void run() {
      if ((authoringTool.getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera)) {
        if ((element instanceof Transformable)) {
          authoringTool.getAGoodLookAt((Transformable)element, (edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera)authoringTool.getCurrentCamera());
        } else {
          AuthoringTool.showErrorDialog(Messages.getString("Can_t_get_a_good_look__element_is_not_a_Transformable"), null);
        }
      } else {
        AuthoringTool.showErrorDialog(Messages.getString("Can_t_get_a_good_look__camera_is_not_symmetric_perspective"), null);
      }
    }
  }
  
  public static class SortGroupAlphabeticallyRunnable extends ElementPopupUtilities.ElementPopupRunnable {
    protected java.util.Comparator sorter = new java.util.Comparator() {
      public int compare(Object o1, Object o2) {
        if (((o1 instanceof Element)) && ((o2 instanceof Element))) {
          String name1 = name.getStringValue();
          String name2 = name.getStringValue();
          return name1.compareTo(name2);
        }
        return 0;
      }
    };
    
    public SortGroupAlphabeticallyRunnable(Element element)
    {
      super();
    }
    
    public String getDefaultLabel() {
      return Messages.getString("sort_alphabetically");
    }
    
    public void run() {
      if ((element instanceof Group)) {
        Group group = (Group)element;
        Object[] values = values.getArrayValue();
        java.util.Arrays.sort(values, sorter);
        values.clear();
        values.set(values);
      } else {
        AuthoringTool.showErrorDialog(Messages.getString("Unable_to_sort_") + element + " " + Messages.getString("alphabetically_because_it_is_not_a_Group_"), null);
      }
    }
  }
}
