package edu.cmu.cs.stage3.alice.authoringtool.importers;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse;
import edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse;
import edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse;
import edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline;
import edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse;
import edu.cmu.cs.stage3.pratt.maxkeyframing.TCBSpline;
import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JLabel;

public class ASEImporter extends edu.cmu.cs.stage3.alice.authoringtool.AbstractImporter
{
  protected StreamTokenizer tokenizer;
  protected HashMap modelsToParentStrings;
  protected HashMap namesToModels;
  protected HashMap namesToMaterials;
  protected HashMap modelsToMaterialIndices;
  protected HashMap modelsToKeyframeAnims;
  protected ArrayList models;
  protected Material[] materials = null;
  
  protected int firstFrame;
  
  protected int lastFrame;
  protected int frameSpeed;
  protected int ticksPerFrame;
  protected double timeScaleFactor;
  protected String currentObject = Messages.getString("_none_");
  protected String currentlyLoading = Messages.getString("_none_");
  protected int currentProgress = 0;
  
  protected ProgressDialog progressDialog;
  
  protected static Configuration importersConfig = Configuration.getLocalConfiguration(ASEImporter.class.getPackage());
  
  static
  {
    if (importersConfig.getValue("aseImporter.useSpecular") == null) {
      importersConfig.setValue("aseImporter.useSpecular", "false");
    }
    if (importersConfig.getValue("aseImporter.colorToWhiteWhenTextured") == null) {
      importersConfig.setValue("aseImporter.colorToWhiteWhenTextured", "true");
    }
    if (importersConfig.getValue("aseImporter.groupMultipleRootObjects") == null) {
      importersConfig.setValue("aseImporter.groupMultipleRootObjects", "true");
    }
    if (importersConfig.getValue("aseImporter.createNormalsIfNoneExist") == null) {
      importersConfig.setValue("aseImporter.createNormalsIfNoneExist", "true");
    }
    if (importersConfig.getValue("aseImporter.createUVsIfNoneExist") == null)
      importersConfig.setValue("aseImporter.createUVsIfNoneExist", "true");
  }
  
  public ASEImporter() {}
  
  public java.util.Map getExtensionMap() { HashMap map = new HashMap();
    map.put("ASE", Messages.getString("3D_Studio_ascii_export"));
    return map;
  }
  
  protected Element load(java.io.InputStream is, String ext) throws IOException {
    edu.cmu.cs.stage3.alice.authoringtool.util.BackslashConverterFilterInputStream bcfis = new edu.cmu.cs.stage3.alice.authoringtool.util.BackslashConverterFilterInputStream(is);
    java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(bcfis));
    tokenizer = new StreamTokenizer(br);
    
    tokenizer.eolIsSignificant(false);
    tokenizer.lowerCaseMode(false);
    tokenizer.parseNumbers();
    tokenizer.wordChars(42, 42);
    tokenizer.wordChars(95, 95);
    tokenizer.wordChars(58, 58);
    
    modelsToParentStrings = new HashMap();
    namesToModels = new HashMap();
    namesToMaterials = new HashMap();
    modelsToMaterialIndices = new HashMap();
    modelsToKeyframeAnims = new HashMap();
    models = new ArrayList();
    


    progressDialog = new ProgressDialog();
    progressDialog.start();
    try {
      do {
        for (;;) {
          tokenizer.nextToken();
          
          if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*SCENE"))) {
            currentObject = Messages.getString("scene_info");
            parseSceneInfo();
            currentObject = Messages.getString("_none_");
          } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*GEOMOBJECT"))) {
            currentObject = Messages.getString("unnamed_object");
            parseGeomObject();
            currentObject = Messages.getString("_none_");
          } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*HELPEROBJECT"))) {
            currentObject = Messages.getString("unnamed_helper_object");
            parseHelperObject();
            currentObject = Messages.getString("_none_");
          } else { if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*MATERIAL_LIST"))) break;
            currentObject = Messages.getString("material_list");
            parseMaterialList();
            currentObject = Messages.getString("_none_");
          } } } while (tokenizer.ttype != -1);

    }
    catch (IOException e)
    {
      AuthoringTool.showErrorDialog(Messages.getString("Error_parsing_ASE__IOException_caught_at_line_") + tokenizer.lineno(), e);
      return null;
    } catch (InvalidFormatError e) {
      AuthoringTool.showErrorDialog(Messages.getString("Error_parsing_ASE__Invalid_Format__") + e.getMessage() + Messages.getString("__at_line_") + tokenizer.lineno(), e, false);
      return null;
    }
    
    Element element = null;
    try {
      ArrayList rootModels = new ArrayList();
      for (Iterator iter = models.iterator(); iter.hasNext();) {
        Transformable model = (Transformable)iter.next();
        String parentString = (String)modelsToParentStrings.get(model);
        if (parentString == null) {
          rootModels.add(model);
          isFirstClass.set(Boolean.TRUE);
        } else {
          Transformable parent = (Transformable)namesToModels.get(parentString);
          if (parent == null) {
            AuthoringTool.showErrorDialog(name.getValue() + Messages.getString("_s_parent__") + parentString + Messages.getString("__does_not_exist___putting_it_at_the_top_level___"), null);
            rootModels.add(model);
            isFirstClass.set(Boolean.TRUE);
          } else {
            parent.addChild(model);
            parts.add(model);
            vehicle.set(parent);
            isFirstClass.set(Boolean.FALSE);
          }
        }
      }
      
      if (rootModels.size() == 1) {
        element = (Transformable)rootModels.get(0);
      } else if (rootModels.size() > 1) {
        if (importersConfig.getValue("aseImporter.groupMultipleRootObjects").equalsIgnoreCase("true")) {
          element = new Model();
          name.set(null);
          isFirstClass.set(Boolean.TRUE);
          for (Iterator iter = rootModels.iterator(); iter.hasNext();) {
            Transformable model = (Transformable)iter.next();
            element.addChild(model);
            parts.add(model);
            vehicle.set((Model)element);
            isFirstClass.set(Boolean.FALSE);
          }
        } else {
          element = new edu.cmu.cs.stage3.alice.core.Module();
          name.set(null);
          for (Iterator iter = rootModels.iterator(); iter.hasNext();) {
            Transformable model = (Transformable)iter.next();
            element.addChild(model);
            isFirstClass.set(Boolean.TRUE);
          }
        }
      } else if (rootModels.size() < 1) {
        return null;
      }
      
      String currentName = (String)name.getValue();
      if (currentName == null) {
        name.set(plainName);
      } else if (!currentName.equalsIgnoreCase(plainName)) {
        name.set(plainName + "_" + currentName);
      }
      
      Transformable dummyScene = new Transformable();
      if ((element instanceof Model)) {
        Transformable trans = (Transformable)element;
        vehicle.set(dummyScene);
        currentObject = ((String)name.getValue());
        currentlyLoading = Messages.getString("fixing_transformations");
        fixTransformations(trans, dummyScene);
        currentObject = ((String)name.getValue());
        currentlyLoading = Messages.getString("fixing_vertices");
        fixVertices(trans);
        currentlyLoading = Messages.getString("_none_");
        vehicle.set(null);
        localTransformation.set(edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d());
      } else {
        Element[] children = element.getChildren();
        for (int i = 0; i < children.length; i++) {
          Transformable trans = (Transformable)children[i];
          vehicle.set(dummyScene);
          currentObject = ((String)name.getValue());
          currentlyLoading = Messages.getString("fixing_transformations");
          fixTransformations(trans, dummyScene);
          currentObject = ((String)name.getValue());
          currentlyLoading = Messages.getString("fixing_vertices");
          fixVertices(trans);
          currentlyLoading = Messages.getString("_none_");
          vehicle.set(null);
        }
      }
      
      for (Iterator iter = models.iterator(); iter.hasNext();) {
        Transformable trans = (Transformable)iter.next();
        if ((trans instanceof Model)) {
          Model model = (Model)trans;
          int materialIndex;
          try
          {
            materialIndex = ((Integer)modelsToMaterialIndices.get(model)).intValue();
          } catch (NullPointerException e) { int materialIndex;
            materialIndex = -1;
          }
          if ((materialIndex >= 0) && (materialIndex < materials.length)) {
            Material material = materials[materialIndex];
            if (material != null)
            {
              if ((diffuseTexture != null) && (importersConfig.getValue("aseImporter.colorToWhiteWhenTextured").equalsIgnoreCase("true"))) {
                ambientColor.set(edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE);
                color.set(edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE);
              }
              else
              {
                ambientColor.set(ambient);
                color.set(diffuse);
              }
              

              if (importersConfig.getValue("aseImporter.useSpecular").equalsIgnoreCase("true")) {
                specularHighlightColor.set(specular);
                specularHighlightExponent.set(new Double(shine + 1.0D));
              }
              opacity.set(new Double(1.0D - transparency));
              diffuseColorMap.set(diffuseTexture);
              opacityMap.set(opacityTexture);
              specularHighlightColorMap.set(shineTexture);
              bumpMap.set(bumpTexture);
            }
          } else if (materialIndex != -1) {
            AuthoringTool.showErrorDialog(name.getValue() + " " + Messages.getString("referenced_a_material_index_out_of_range___no_material_properties_assigned_"), null);
          }
        }
      }
      
      for (int i = 0; i < materials.length; i++) {
        if (materials[i] != null) {
          Transformable materialOwner = null;
          if ((element instanceof Transformable)) {
            materialOwner = (Transformable)element;

          }
          else
          {
            for (Iterator iter = models.iterator(); iter.hasNext();) {
              Transformable trans = (Transformable)iter.next();
              try {
                int materialIndex = ((Integer)modelsToMaterialIndices.get(trans)).intValue();
                if (materialIndex == i) {
                  materialOwner = getRootModel(trans);
                }
              }
              catch (NullPointerException localNullPointerException1) {}
            }
          }
          


          if (materialOwner != null) {
            if (materials[i].diffuseTexture != null) {
              materialOwner.addChild(materials[i].diffuseTexture);
              textureMaps.add(materials[i].diffuseTexture);
            }
            if (materials[i].opacityTexture != null) {
              materialOwner.addChild(materials[i].opacityTexture);
              textureMaps.add(materials[i].diffuseTexture);
            }
            if (materials[i].shineTexture != null) {
              materialOwner.addChild(materials[i].shineTexture);
              textureMaps.add(materials[i].shineTexture);
            }
            if (materials[i].bumpTexture != null) {
              materialOwner.addChild(materials[i].bumpTexture);
              textureMaps.add(materials[i].bumpTexture);
            }
          } else {
            AuthoringTool.showErrorDialog(Messages.getString("ASEImporter_Error__no_materialOwner_to_attach_textures_to_"), null);
          }
        }
      }
      


      for (Iterator iter = rootModels.iterator(); iter.hasNext();) {
        Transformable root = (Transformable)iter.next();
        
        edu.cmu.cs.stage3.alice.core.response.DoTogether rootAnim = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
        name.set("keyframeAnimation");
        for (Iterator jter = models.iterator(); jter.hasNext();) {
          Transformable trans = (Transformable)jter.next();
          
          if ((trans.isDescendantOf(root)) || (trans.equals(root))) {
            ArrayList anims = (ArrayList)modelsToKeyframeAnims.get(trans);
            if (anims != null)
            {
              String prefix = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(trans);
              prefix = prefix.replace('.', '_');
              for (Iterator kter = anims.iterator(); kter.hasNext();) {
                KeyframeResponse anim = (KeyframeResponse)kter.next();
                duration.set(null);
                String baseName = name.getStringValue();
                name.set(prefix + "_" + baseName);
                subject.set(trans);
                rootAnim.addChild(anim);
                componentResponses.add(anim);
              }
            }
          }
        }
        
        if (!componentResponses.isEmpty()) {
          root.addChild(rootAnim);
          responses.add(rootAnim);
        }
      }
    } catch (Throwable t) {
      AuthoringTool.showErrorDialog(Messages.getString("An_unexpected_error_occured_while_loading_an_ASE_"), t);
    }
    
    progressDialog.stop();
    progressDialog.setVisible(false);
    progressDialog.dispose();
    
    tokenizer = null;
    modelsToParentStrings = null;
    namesToModels = null;
    namesToMaterials = null;
    modelsToMaterialIndices = null;
    modelsToKeyframeAnims = null;
    models = null;
    materials = null;
    
    return element;
  }
  
  protected Transformable getRootModel(Transformable trans) {
    Element parent = trans.getParent();
    if (!(parent instanceof Transformable)) {
      return trans;
    }
    return getRootModel((Transformable)parent);
  }
  

  protected void fixTransformations(Transformable root, Transformable scene)
  {
    root.setTransformationRightNow(root.getLocalTransformation(), scene);
    








    Element[] children = root.getChildren();
    for (int i = 0; i < children.length; i++) {
      if ((children[i] instanceof Transformable)) {
        fixTransformations((Transformable)children[i], scene);
      }
    }
  }
  
  protected void fixVertices(Transformable root) {
    currentObject = ((String)name.getValue());
    if (((root instanceof Model)) && 
      ((geometry.getValue() instanceof IndexedTriangleArray))) {
      IndexedTriangleArray geom = (IndexedTriangleArray)geometry.getValue();
      Vertex3d[] vertices = (Vertex3d[])vertices.getValue();
      if (vertices != null) {
        progressDialog.setMax(vertices.length - 1);
        currentProgress = 0;
        for (int i = 0; i < vertices.length; i++) {
          currentProgress = i;
          edu.cmu.cs.stage3.math.Vector4 v = new edu.cmu.cs.stage3.math.Vector4(position.x, position.y, position.z, 1.0D);
          edu.cmu.cs.stage3.math.Vector4 vprime = edu.cmu.cs.stage3.math.Vector4.multiply(v, root.getSceneGraphReferenceFrame().getInverseAbsoluteTransformation());
          position.set(x, y, z);
        }
        vertices.set(vertices);
      }
    }
    

    Element[] children = root.getChildren();
    for (int i = 0; i < children.length; i++) {
      if ((children[i] instanceof Transformable)) {
        fixVertices((Transformable)children[i]);
      }
    }
  }
  
  protected String parseString() throws ASEImporter.InvalidFormatError, IOException {
    tokenizer.nextToken();
    
    if (tokenizer.ttype == 34) {
      return tokenizer.sval;
    }
    throw new InvalidFormatError(Messages.getString("String_value_expected"));
  }
  
  protected int parseInt() throws ASEImporter.InvalidFormatError, IOException
  {
    tokenizer.nextToken();
    
    if (tokenizer.ttype == -2) {
      return (int)tokenizer.nval;
    }
    throw new InvalidFormatError(Messages.getString("int_value_expected"));
  }
  
  protected double parseDouble() throws ASEImporter.InvalidFormatError, IOException
  {
    tokenizer.nextToken();
    
    if (tokenizer.ttype == -2) {
      return tokenizer.nval;
    }
    throw new InvalidFormatError(Messages.getString("double_value_expected"));
  }
  
  protected float parseFloat() throws ASEImporter.InvalidFormatError, IOException
  {
    tokenizer.nextToken();
    
    if (tokenizer.ttype == -2) {
      return (float)tokenizer.nval;
    }
    throw new InvalidFormatError(Messages.getString("double_value_expected"));
  }
  
  protected void parseUnknownBlock() throws ASEImporter.InvalidFormatError, IOException {
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if (tokenizer.ttype != 123) break;
        parseUnknownBlock(); }
      if (tokenizer.ttype == 125)
        return;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished_block"));
  }
  
  protected void parseSceneInfo()
    throws IOException
  {
    firstFrame = 0;
    lastFrame = 0;
    frameSpeed = 0;
    ticksPerFrame = 0;
    timeScaleFactor = 1.0D;
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__SCENE"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*SCENE_FIRSTFRAME"))) {
          firstFrame = parseInt();
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*SCENE_LASTFRAME"))) {
          lastFrame = parseInt();
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*SCENE_FRAMESPEED"))) {
          frameSpeed = parseInt();
        } else { if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*SCENE_TICKSPERFRAME"))) break;
          ticksPerFrame = parseInt(); } }
      if (tokenizer.ttype == 125) {
        try {
          timeScaleFactor = (1.0D / ticksPerFrame * (1.0D / frameSpeed));
        } catch (Exception e) {
          timeScaleFactor = 1.0D;
        }
        return;
      } } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__SCENE"));
  }
  
  protected void parseMaterialList()
    throws ASEImporter.InvalidFormatError, IOException
  {
    int count = 0;
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__MATERIAL_LIST"));
    }
    
    currentProgress = 0;
    do {
      for (;;) { tokenizer.nextToken();
        
        if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MATERIAL_COUNT"))) {
          count = parseInt();
          materials = new Material[count];
        } else { if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*MATERIAL"))) break;
          if (count < 1) {
            throw new InvalidFormatError(Messages.getString("material_declared_before_number_of_materials_defined"));
          }
          
          parseMaterial();
          currentObject = Messages.getString("_none_"); } }
      if (tokenizer.ttype == 125)
        return;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__MESH_VERTEX_LIST"));
  }
  
  protected void parseMaterial()
    throws ASEImporter.InvalidFormatError, IOException
  {
    Material material = new Material(null);
    
    int index = parseInt();
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__MATERIAL__n_"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MATERIAL_NAME"))) {
          name = tokenizer.sval;
          namesToMaterials.put(name, material);
          currentObject = name;
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MATERIAL_AMBIENT"))) {
          ambient = new edu.cmu.cs.stage3.alice.scenegraph.Color(parseDouble(), parseDouble(), parseDouble());
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MATERIAL_DIFFUSE"))) {
          diffuse = new edu.cmu.cs.stage3.alice.scenegraph.Color(parseDouble(), parseDouble(), parseDouble());
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MATERIAL_SPECULAR"))) {
          specular = new edu.cmu.cs.stage3.alice.scenegraph.Color(parseDouble(), parseDouble(), parseDouble());
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MATERIAL_SHINE"))) {
          shine = parseDouble();
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MATERIAL_SHINESTRENGTH"))) {
          shinestrength = parseDouble();
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MATERIAL_TRANSPARENCY"))) {
          transparency = parseDouble();
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MAP_AMBIENT"))) {
          ambientTexture = parseMap();
          currentObject = name;
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MAP_DIFFUSE"))) {
          diffuseTexture = parseMap();
          currentObject = name;
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MAP_SHINE"))) {
          shineTexture = parseMap();
          currentObject = name;
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MAP_SHINESTRENGTH"))) {
          shineStrengthTexture = parseMap();
          currentObject = name;
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MAP_SELFILLUM"))) {
          selfIllumTexture = parseMap();
          currentObject = name;
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MAP_OPACITY"))) {
          opacityTexture = parseMap();
          currentObject = name;
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MAP_BUMP"))) {
          bumpTexture = parseMap();
          currentObject = name;
        } else { if (tokenizer.ttype != 123) break;
          parseUnknownBlock(); } }
      if (tokenizer.ttype == 125) {
        materials[index] = material;
        return;
      } } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__MATERIAL"));
  }
  
  protected TextureMap parseMap()
    throws ASEImporter.InvalidFormatError, IOException
  {
    TextureMap texture = null;
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__MAP__map_type_"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*BITMAP"))) break;
        String filename = parseString();
        currentObject = filename;
        
        File imageFile = new File(filename);
        String justName = imageFile.getName();
        String extension = justName.substring(justName.lastIndexOf('.') + 1);
        java.io.BufferedInputStream bis = null;
        
        if (imageFile.exists()) {
          if (imageFile.canRead()) {
            bis = new java.io.BufferedInputStream(new java.io.FileInputStream(imageFile));
          } else {
            AuthoringTool.showErrorDialog(Messages.getString("Cannot_read_from_file__") + filename + " " + Messages.getString("specified_on_line_") + tokenizer.lineno(), null, false);
          }
        }
        else {
          Object location = getLocation();
          if ((location instanceof File)) {
            imageFile = new File((File)location, filename);
            if (!imageFile.exists()) {
              imageFile = new File((File)location, justName);
            }
            if (imageFile.exists()) {
              if (imageFile.canRead()) {
                bis = new java.io.BufferedInputStream(new java.io.FileInputStream(imageFile));
              } else {
                AuthoringTool.showErrorDialog(Messages.getString("Cannot_read_from_file__") + filename + " " + Messages.getString("specified_on_line_") + tokenizer.lineno(), null, false);
              }
            }
            else {
              AuthoringTool.showErrorDialog(Messages.getString("Unable_to_find_file__") + filename + " " + Messages.getString("specified_on_line_") + tokenizer.lineno(), null, false);
            }
          }
          else if ((location instanceof java.net.URL))
          {
            StringBuffer name = new StringBuffer();
            char[] chars = new char[justName.length()];
            justName.getChars(0, justName.length(), chars, 0);
            for (int i = 0; i < chars.length; i++) {
              char c = chars[i];
              if (c == ' ') {
                name.append("%20");
              } else if (c == '#') {
                name.append("%23");
              } else if (c == ';') {
                name.append("%3B");
              } else if (c == '@') {
                name.append("%40");
              } else if (c == '&') {
                name.append("%26");
              } else if (c == '=') {
                name.append("%3D");
              } else if (c == '+') {
                name.append("%2B");
              } else if (c == '$') {
                name.append("%24");
              } else if (c == ',') {
                name.append("%2C");
              } else if (c == '%') {
                name.append("%25");
              } else if (c == '"') {
                name.append("%22");
              } else if (c == '{') {
                name.append("%7B");
              } else if (c == '}') {
                name.append("%7D");
              } else if (c == '^') {
                name.append("%5E");
              } else if (c == '[') {
                name.append("%5B");
              } else if (c == ']') {
                name.append("%5D");
              } else if (c == '`') {
                name.append("%60");
              } else {
                name.append(c);
              }
            }
            java.net.URL url = (java.net.URL)location;
            url = new java.net.URL(url.toExternalForm() + name.toString());
            bis = new java.io.BufferedInputStream(url.openStream());
          } else {
            AuthoringTool.showErrorDialog(Messages.getString("location_is_not_a_File_or_URL__") + location, null, false);
          }
        }
        if (bis == null) {
          AuthoringTool.showErrorDialog(Messages.getString("BufferedInputStream_is_null_for_") + filename, null);
        }
        else
        {
          String codec = edu.cmu.cs.stage3.image.ImageIO.mapExtensionToCodecName(extension);
          if (codec == null) {
            AuthoringTool.showErrorDialog(Messages.getString("Can_t_find_appropriate_codec_for_") + filename, null);
          }
          else
          {
            java.awt.Image image = edu.cmu.cs.stage3.image.ImageIO.load(codec, bis);
            if (image == null) {
              AuthoringTool.showErrorDialog(Messages.getString("Image_loaded_is_null_for_") + filename, null);
            }
            else
            {
              String textureName = justName.substring(0, justName.indexOf('.'));
              texture = new TextureMap();
              
              if ((image instanceof java.awt.image.BufferedImage)) {
                java.awt.image.BufferedImage bi = (java.awt.image.BufferedImage)image;
                if (bi.getColorModel().hasAlpha()) {
                  format.set(new Integer(3));
                }
              }
              
              name.set(textureName);
              image.set(image); } } } }
      if (tokenizer.ttype == 125)
        return texture;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__MAP_DIFFUSE"));
  }
  
  protected void parseHelperObject()
    throws IOException
  {
    Model helper = new Model();
    isFirstClass.set(Boolean.FALSE);
    models.add(helper);
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__HELPEROBJECT"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*NODE_NAME"))) {
          name.set(parseString());
          namesToModels.put(name.getValue(), helper);
          currentObject = ((String)name.getValue());
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*NODE_PARENT"))) {
          modelsToParentStrings.put(helper, parseString());
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*NODE_TM"))) {
          currentlyLoading = Messages.getString("transformation");
          currentProgress = 0;
          localTransformation.set(parseTransformation());
          currentProgress = 0;
          currentlyLoading = Messages.getString("_none_");
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*TM_ANIMATION"))) {
          ArrayList anims = parseAnimationNode();
          modelsToKeyframeAnims.put(helper, anims);
        } else { if (tokenizer.ttype != 123) break;
          parseUnknownBlock(); } }
      if (tokenizer.ttype == 125)
        return;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__HELPEROBJECT"));
  }
  
  protected void parseGeomObject()
    throws IOException
  {
    Model model = new Model();
    isFirstClass.set(Boolean.FALSE);
    models.add(model);
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__GEOMOBJECT"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*NODE_NAME"))) {
          name.set(parseString());
          namesToModels.put(name.getValue(), model);
          currentObject = ((String)name.getValue());
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*NODE_PARENT"))) {
          modelsToParentStrings.put(model, parseString());
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*NODE_TM"))) {
          currentlyLoading = Messages.getString("transformation");
          currentProgress = 0;
          localTransformation.set(parseTransformation());
          currentProgress = 0;
          currentlyLoading = Messages.getString("_none_");
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MESH"))) {
          currentlyLoading = Messages.getString("mesh");
          currentProgress = 0;
          geometry.set(parseMesh());
          geometry.getElementValue().setParent(model);
          geometry.getElementValue().name.set(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild("__ita__", model));
          currentProgress = 0;
          currentlyLoading = Messages.getString("_none_");
        } else if (((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*PROP_CASTSHADOW"))) && (
        
          (tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*PROP_RECVSHADOW"))))
        {
          if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MATERIAL_REF"))) {
            modelsToMaterialIndices.put(model, new Integer(parseInt()));
          } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*TM_ANIMATION"))) {
            ArrayList anims = parseAnimationNode();
            modelsToKeyframeAnims.put(model, anims);
          } else { if (tokenizer.ttype != 123) break;
            parseUnknownBlock(); } } }
      if (tokenizer.ttype == 125)
        return;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__GEOMOBJECT"));
  }
  
  protected edu.cmu.cs.stage3.math.Matrix44 parseTransformation()
    throws ASEImporter.InvalidFormatError, IOException
  {
    edu.cmu.cs.stage3.math.Matrix44 m = new edu.cmu.cs.stage3.math.Matrix44();
    Matrix33 rot = new Matrix33();
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__NODE_TM"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*TM_ROW0"))) {
          m00 = parseDouble();
          m01 = parseDouble();
          m02 = parseDouble();
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*TM_ROW1"))) {
          m10 = parseDouble();
          m11 = parseDouble();
          m12 = parseDouble();
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*TM_ROW2"))) {
          m20 = parseDouble();
          m21 = parseDouble();
          m22 = parseDouble();
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*TM_ROW3")))
        {
          m30 = (-parseDouble());
          m32 = (-parseDouble());
          m31 = parseDouble();
        } else { if (tokenizer.ttype != 123) break;
          parseUnknownBlock(); } }
      if (tokenizer.ttype == 125)
      {









        m00 = m00;
        m01 = (-m02);
        m02 = m01;
        m10 = (-m20);
        m11 = m22;
        m12 = (-m21);
        m20 = m10;
        m21 = (-m12);
        m22 = m11;
        

        return m;
      } } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__NODE_TM"));
  }
  
  protected IndexedTriangleArray parseMesh()
    throws ASEImporter.InvalidFormatError, IOException
  {
    IndexedTriangleArray geometry = new IndexedTriangleArray();
    Vertex3d[] verts = null;
    int[] coordIndices = null;
    int[] uvIndices = null;
    double[] coordinates = null;
    double[] normals = null;
    float[] uvs = null;
    double[] colors = null;
    int numVerts = -1;
    int numUVs = -1;
    int numFaces = -1;
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__MESH"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MESH_NUMVERTEX"))) {
          numVerts = parseInt();
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MESH_NUMFACES"))) {
          numFaces = parseInt();
          coordIndices = new int[numFaces * 3];
          uvIndices = new int[numFaces * 3];
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MESH_NUMTVERTEX"))) {
          numUVs = parseInt();
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MESH_VERTEX_LIST"))) {
          if (numVerts < 0) {
            throw new InvalidFormatError(Messages.getString("illegal_number_of_vertices_defined_or_coordinates_declared_before_number_of_vertices_defined"));
          }
          coordinates = new double[numVerts * 3];
          
          currentlyLoading = Messages.getString("coordinates");
          progressDialog.setMax(numVerts);
          parseVertexList(coordinates);
          currentlyLoading = Messages.getString("mesh");
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MESH_TVERTLIST"))) {
          if (numUVs < 0) {
            throw new InvalidFormatError(Messages.getString("uvs_declared_before_number_of_texture_vertices_defined"));
          }
          uvs = new float[numUVs * 2];
          
          currentlyLoading = "uvs";
          progressDialog.setMax(numUVs);
          parseUVList(uvs);
          currentlyLoading = Messages.getString("mesh");
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MESH_NORMALS"))) {
          if (numVerts < 0) {
            throw new InvalidFormatError(Messages.getString("normals_declared_before_number_of_vertices_defined"));
          }
          normals = new double[numFaces * 3 * 3];
          
          currentlyLoading = Messages.getString("normals");
          progressDialog.setMax(numFaces);
          parseNormals(normals);
          currentlyLoading = Messages.getString("mesh");
        }
        else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MESH_FACE_LIST"))) {
          if (coordIndices == null) {
            throw new InvalidFormatError(Messages.getString("faces_declared_before_number_of_faces_defined"));
          }
          
          currentlyLoading = Messages.getString("coordinate_indices");
          progressDialog.setMax(coordIndices.length / 3);
          parseFaceList(coordIndices);
          currentlyLoading = Messages.getString("mesh");
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MESH_TFACELIST"))) {
          if (uvIndices == null) {
            throw new InvalidFormatError(Messages.getString("texture_faces_declared_before_number_of_faces_defined"));
          }
          
          currentlyLoading = Messages.getString("texture_indices");
          progressDialog.setMax(uvIndices.length / 3);
          parseUVFaceList(uvIndices);
          currentlyLoading = Messages.getString("mesh");
        } else { if (tokenizer.ttype != 123) break;
          parseUnknownBlock(); } }
      if (tokenizer.ttype == 125) {
        if ((numVerts > 0) && (coordIndices != null)) {
          int vertexFormat = 0;
          if (coordinates != null) {
            vertexFormat |= 0x1;
          }
          if ((normals != null) || (importersConfig.getValue("aseImporter.createNormalsIfNoneExist").equalsIgnoreCase("true"))) {
            vertexFormat |= 0x2;
          }
          if ((uvs != null) || (importersConfig.getValue("aseImporter.createUVsIfNoneExist").equalsIgnoreCase("true"))) {
            vertexFormat |= 0x10;
          }
          if (colors != null) {
            vertexFormat |= 0x4;
          }
          
          verts = new Vertex3d[numFaces * 3];
          for (int i = 0; i < numFaces * 3; i++) {
            verts[i] = new Vertex3d(vertexFormat);
          }
          
          int[] indices = new int[numFaces * 3];
          for (int i = 0; i < numFaces * 3; i++) {
            indices[i] = i;
          }
          
          for (int i = 0; i < numFaces; i++) {
            for (int j = 0; j < 3; j++) {
              if (coordinates != null) {
                3position.x = coordinates[(coordIndices[(i * 3 + j)] * 3 + 0)];
                3position.y = coordinates[(coordIndices[(i * 3 + j)] * 3 + 1)];
                3position.z = coordinates[(coordIndices[(i * 3 + j)] * 3 + 2)];
              }
              if (normals != null) {
                3normal.x = normals[(i * 9 + j * 3 + 0)];
                3normal.y = normals[(i * 9 + j * 3 + 1)];
                3normal.z = normals[(i * 9 + j * 3 + 2)];
              }
              if (uvs != null) {
                3textureCoordinate0.x = uvs[(uvIndices[(i * 3 + j)] * 2 + 0)];
                3textureCoordinate0.y = uvs[(uvIndices[(i * 3 + j)] * 2 + 1)];
              }
            }
          }
          

          vertices.set(verts);
          indices.set(indices);
          
          if ((normals == null) && (importersConfig.getValue("aseImporter.createNormalsIfNoneExist").equalsIgnoreCase("true"))) {
            edu.cmu.cs.stage3.alice.gallery.ModelFixer.calculateNormals(geometry);
          }
        }
        
        return geometry;
      } } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__MESH"));
  }
  
  protected void parseVertexList(double[] coordinates)
    throws ASEImporter.InvalidFormatError, IOException
  {
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__MESH_VERTEX_LIST"));
    }
    
    currentProgress = 0;
    do {
      for (;;) { tokenizer.nextToken();
        
        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*MESH_VERTEX"))) break;
        int index = parseInt();
        
        coordinates[(index * 3 + 0)] = (-parseDouble());
        coordinates[(index * 3 + 2)] = (-parseDouble());
        coordinates[(index * 3 + 1)] = parseDouble();
        currentProgress += 1; }
      if (tokenizer.ttype == 125)
        return;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__MESH_VERTEX_LIST"));
  }
  
  protected void parseUVList(float[] uvs)
    throws ASEImporter.InvalidFormatError, IOException
  {
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__MESH_TVERTLIST"));
    }
    
    currentProgress = 0;
    do {
      for (;;) { tokenizer.nextToken();
        
        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*MESH_TVERT"))) break;
        int index = parseInt();
        uvs[(index * 2 + 0)] = parseFloat();
        uvs[(index * 2 + 1)] = parseFloat();
        currentProgress += 1; }
      if (tokenizer.ttype == 125)
        return;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__MESH_TVERTLIST"));
  }
  
  protected void parseNormals(double[] normals)
    throws ASEImporter.InvalidFormatError, IOException
  {
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__MESH_NORMALS"));
    }
    

    int face = 0;
    int v = 0;
    currentProgress = 0;
    do {
      for (;;) { tokenizer.nextToken();
        
        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*MESH_VERTEXNORMAL"))) break;
        int index = parseInt();
        int realv = v;
        
        if (v == 1) realv = 2;
        if (v == 2) { realv = 1;
        }
        normals[(face * 9 + realv * 3 + 0)] = (-parseDouble());
        normals[(face * 9 + realv * 3 + 2)] = (-parseDouble());
        normals[(face * 9 + realv * 3 + 1)] = parseDouble();
        v++;
        if (v == 3) {
          v = 0;
          face++;
        }
        currentProgress += 1; }
      if (tokenizer.ttype == 125)
        return;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__MESH_NORMALS"));
  }
  

  protected void parseVertexColors(double[] colors) {}
  

  protected void parseFaceList(int[] indices)
    throws ASEImporter.InvalidFormatError, IOException
  {
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__MESH_FACE_LIST"));
    }
    
    currentProgress = 0;
    do {
      for (;;) { tokenizer.nextToken();
        
        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*MESH_FACE"))) break;
        parseMeshFace(indices);
        currentProgress += 1; }
      if (tokenizer.ttype == 125)
        return;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__MESH_FACE_LIST"));
  }
  
  protected void parseMeshFace(int[] indices)
    throws ASEImporter.InvalidFormatError, IOException
  {
    int index = parseInt();
    do {
      for (;;) {
        tokenizer.nextToken();
        

        if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("A:"))) {
          indices[(index * 3 + 0)] = parseInt();
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("B:"))) {
          indices[(index * 3 + 2)] = parseInt();
        } else { if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("C:"))) break;
          indices[(index * 3 + 1)] = parseInt(); } }
      if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*MESH_FACE"))) {
        tokenizer.pushBack();
        return; }
      if (tokenizer.ttype == 125) {
        tokenizer.pushBack();
        return;
      } } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__MESH_FACE"));
  }
  
  protected void parseUVFaceList(int[] indices)
    throws ASEImporter.InvalidFormatError, IOException
  {
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__MESH_TFACELIST"));
    }
    
    currentProgress = 0;
    do {
      for (;;) { tokenizer.nextToken();
        

        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*MESH_TFACE"))) break;
        int index = parseInt();
        indices[(index * 3 + 0)] = parseInt();
        indices[(index * 3 + 2)] = parseInt();
        indices[(index * 3 + 1)] = parseInt();
        currentProgress += 1; }
      if (tokenizer.ttype == 125)
        return;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__MESH_TFACELIST"));
  }
  
  protected ArrayList parseAnimationNode()
    throws ASEImporter.InvalidFormatError, IOException
  {
    ArrayList anims = new ArrayList();
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__TM_ANIMATION"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*CONTROL_POS_LINEAR"))) {
          anims.add(parseLinearPositionAnimation());
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*CONTROL_POS_BEZIER"))) {
          anims.add(parseBezierPositionAnimation());
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*CONTROL_POS_TCB"))) {
          anims.add(parseTCBPositionAnimation());
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*CONTROL_ROT_LINEAR"))) {
          anims.add(parseLinearQuaternionAnimation());
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*CONTROL_ROT_BEZIER"))) {
          anims.add(parseBezierQuaternionAnimation());
        } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*CONTROL_ROT_TCB"))) {
          anims.add(parseTCBQuaternionAnimation());
        } else if (((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*CONTROL_SCALE_LINEAR"))) && 
        
          ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*CONTROL_SCALE_BEZIER"))) && (
          
          (tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*CONTROL_SCALE_TCB"))))
        {
          if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*CONTROL_POS_TRACK"))) {
            anims.add(parseSampledPositionAnimation());
          } else if ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*CONTROL_ROT_TRACK"))) {
            anims.add(parseSampledQuaternionAnimation());
          } else if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*CONTROL_SCALE_TRACK")))
          {
            if (tokenizer.ttype != 123) break;
            parseUnknownBlock(); } } }
      if (tokenizer.ttype == 125)
        return anims;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__TM_ANIMATION"));
  }
  
  protected KeyframeResponse parseLinearPositionAnimation()
    throws ASEImporter.InvalidFormatError, IOException
  {
    PositionKeyframeResponse keyframeResponse = new PositionKeyframeResponse();
    name.set("linearPositionKeyframeAnim");
    edu.cmu.cs.stage3.pratt.maxkeyframing.LinearSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.LinearSpline();
    spline.set(spline);
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__CONTROL_POS_LINEAR"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*CONTROL_POS_KEY"))) break;
        double time = parseDouble() * timeScaleFactor;
        

        double x = -parseDouble();
        double z = -parseDouble();
        double y = parseDouble();
        spline.addKey(new edu.cmu.cs.stage3.pratt.maxkeyframing.Vector3SimpleKey(time, new javax.vecmath.Vector3d(x, y, z))); }
      if (tokenizer.ttype == 125)
        return keyframeResponse;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__CONTROL_POS_LINEAR"));
  }
  
  protected KeyframeResponse parseBezierPositionAnimation()
    throws ASEImporter.InvalidFormatError, IOException
  {
    PositionKeyframeResponse keyframeResponse = new PositionKeyframeResponse();
    name.set("bezierPositionKeyframeAnim");
    edu.cmu.cs.stage3.pratt.maxkeyframing.BezierSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.BezierSpline();
    spline.set(spline);
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__CONTROL_POS_BEZIER"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*CONTROL_BEZIER_POS_KEY"))) break;
        double time = parseDouble() * timeScaleFactor;
        

        double x = -parseDouble();
        double z = -parseDouble();
        double y = parseDouble();
        double intan_x = -parseDouble();
        double intan_z = -parseDouble();
        double intan_y = parseDouble();
        double outtan_x = -parseDouble();
        double outtan_z = -parseDouble();
        double outtan_y = parseDouble();
        spline.addKey(new edu.cmu.cs.stage3.pratt.maxkeyframing.Vector3BezierKey(time, new javax.vecmath.Vector3d(x, y, z), new javax.vecmath.Vector3d(intan_x, intan_y, intan_z), new javax.vecmath.Vector3d(outtan_x, outtan_y, outtan_z))); }
      if (tokenizer.ttype == 125) {
        spline.convertMAXTangentsToBezierTangents(timeScaleFactor);
        return keyframeResponse;
      } } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__CONTROL_POS_BEZIER"));
  }
  
  protected KeyframeResponse parseTCBPositionAnimation()
    throws ASEImporter.InvalidFormatError, IOException
  {
    PositionKeyframeResponse keyframeResponse = new PositionKeyframeResponse();
    name.set("tcbPositionKeyframeAnim");
    TCBSpline spline = new TCBSpline();
    spline.set(spline);
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__CONTROL_POS_TCB"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*CONTROL_TCB_POS_KEY"))) break;
        double time = parseDouble() * timeScaleFactor;
        

        double x = -parseDouble();
        double z = -parseDouble();
        double y = parseDouble();
        double tension = parseDouble();
        double continuity = parseDouble();
        double bias = parseDouble();
        double easeIn = parseDouble();
        double easeOut = parseDouble();
        spline.addKey(new edu.cmu.cs.stage3.pratt.maxkeyframing.Vector3TCBKey(time, new javax.vecmath.Vector3d(x, y, z), tension, continuity, bias)); }
      if (tokenizer.ttype == 125)
        return keyframeResponse;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__CONTROL_POS_TCB"));
  }
  
  protected KeyframeResponse parseLinearQuaternionAnimation()
    throws ASEImporter.InvalidFormatError, IOException
  {
    QuaternionKeyframeResponse keyframeResponse = new QuaternionKeyframeResponse();
    name.set("quaternionKeyframeAnim");
    QuaternionSlerpSpline spline = new QuaternionSlerpSpline();
    spline.set(spline);
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__CONTROL_ROT_LINEAR"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*CONTROL_ROT_KEY"))) break;
        double time = parseDouble() * timeScaleFactor;
        

        double axis_x = -parseDouble();
        double axis_z = -parseDouble();
        double axis_y = parseDouble();
        double angle = -parseDouble();
        spline.addKey(new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKey(time, new edu.cmu.cs.stage3.math.Quaternion(new edu.cmu.cs.stage3.math.AxisAngle(axis_x, axis_y, axis_z, angle)))); }
      if (tokenizer.ttype == 125)
        return keyframeResponse;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__CONTROL_ROT_LINEAR"));
  }
  
  protected KeyframeResponse parseBezierQuaternionAnimation()
    throws ASEImporter.InvalidFormatError, IOException
  {
    QuaternionKeyframeResponse keyframeResponse = new QuaternionKeyframeResponse();
    name.set("quaternionKeyframeAnim");
    QuaternionSlerpSpline spline = new QuaternionSlerpSpline();
    spline.set(spline);
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__CONTROL_ROT_BEZIER"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*CONTROL_ROT_KEY"))) break;
        double time = parseDouble() * timeScaleFactor;
        

        double axis_x = -parseDouble();
        double axis_z = -parseDouble();
        double axis_y = parseDouble();
        double angle = -parseDouble();
        spline.addKey(new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKey(time, new edu.cmu.cs.stage3.math.Quaternion(new edu.cmu.cs.stage3.math.AxisAngle(axis_x, axis_y, axis_z, angle)))); }
      if (tokenizer.ttype == 125)
        return keyframeResponse;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__CONTROL_ROT_BEZIER"));
  }
  
  protected KeyframeResponse parseTCBQuaternionAnimation()
    throws ASEImporter.InvalidFormatError, IOException
  {
    QuaternionKeyframeResponse keyframeResponse = new QuaternionKeyframeResponse();
    name.set("tcbQuaternionKeyframeAnim");
    TCBSpline spline = new TCBSpline();
    spline.set(spline);
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__CONTROL_ROT_TCB"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*CONTROL_TCB_ROT_KEY"))) break;
        double time = parseDouble() * timeScaleFactor;
        

        double axis_x = -parseDouble();
        double axis_z = -parseDouble();
        double axis_y = parseDouble();
        double angle = -parseDouble();
        double tension = parseDouble();
        double continuity = parseDouble();
        double bias = parseDouble();
        double easeIn = parseDouble();
        double easeOut = parseDouble();
        spline.addKey(new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionTCBKey(time, new edu.cmu.cs.stage3.math.Quaternion(new edu.cmu.cs.stage3.math.AxisAngle(axis_x, axis_y, axis_z, angle)), tension, continuity, bias)); }
      if (tokenizer.ttype == 125) {
        spline.correctForMAXRelativeKeys();
        return keyframeResponse;
      } } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__CONTROL_ROT_TCB"));
  }
  
  protected KeyframeResponse parseLinearScaleAnimation()
    throws ASEImporter.InvalidFormatError, IOException
  {
    ScaleKeyframeResponse keyframeResponse = new ScaleKeyframeResponse();
    name.set("linearScaleKeyframeAnim");
    edu.cmu.cs.stage3.pratt.maxkeyframing.LinearSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.LinearSpline();
    spline.set(spline);
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__CONTROL_SCALE_LINEAR"));
    }
    do {
      do {
        tokenizer.nextToken();
      }
      while ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*CONTROL_SCALE_KEY")));
      
      if (tokenizer.ttype == 125)
        return keyframeResponse;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__CONTROL_SCALE_LINEAR"));
  }
  
  protected KeyframeResponse parseBezierScaleAnimation()
    throws ASEImporter.InvalidFormatError, IOException
  {
    ScaleKeyframeResponse keyframeResponse = new ScaleKeyframeResponse();
    name.set("bezierScaleKeyframeAnim");
    edu.cmu.cs.stage3.pratt.maxkeyframing.BezierSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.BezierSpline();
    spline.set(spline);
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__CONTROL_SCALE_BEZIER"));
    }
    do {
      do {
        tokenizer.nextToken();
      }
      while ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*CONTROL_BEZIER_SCALE_KEY")));
      
      if (tokenizer.ttype == 125) {
        spline.convertMAXTangentsToBezierTangents(timeScaleFactor);
        return keyframeResponse;
      } } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__CONTROL_SCALE_BEZIER"));
  }
  
  protected KeyframeResponse parseTCBScaleAnimation()
    throws ASEImporter.InvalidFormatError, IOException
  {
    ScaleKeyframeResponse keyframeResponse = new ScaleKeyframeResponse();
    name.set("tcbScaleKeyframeAnim");
    TCBSpline spline = new TCBSpline();
    spline.set(spline);
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__CONTROL_SCALE_TCB"));
    }
    do {
      do {
        tokenizer.nextToken();
      }
      while ((tokenizer.sval != null) && (tokenizer.sval.equalsIgnoreCase("*CONTROL_TCB_SCALE_KEY")));
      
      if (tokenizer.ttype == 125)
        return keyframeResponse;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__CONTROL_SCALE_TCB"));
  }
  
  protected KeyframeResponse parseSampledPositionAnimation()
    throws ASEImporter.InvalidFormatError, IOException
  {
    PositionKeyframeResponse keyframeResponse = new PositionKeyframeResponse();
    name.set("sampledPositionAnim");
    edu.cmu.cs.stage3.pratt.maxkeyframing.CatmullRomSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.CatmullRomSpline();
    spline.set(spline);
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__CONTROL_POS_TRACK"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*CONTROL_POS_SAMPLE"))) break;
        double time = parseDouble() * timeScaleFactor;
        

        double x = -parseDouble();
        double z = -parseDouble();
        double y = parseDouble();
        spline.addKey(new edu.cmu.cs.stage3.pratt.maxkeyframing.Vector3SimpleKey(time, new javax.vecmath.Vector3d(x, y, z))); }
      if (tokenizer.ttype == 125)
        return keyframeResponse;
    } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__CONTROL_POS_TRACK"));
  }
  
  protected KeyframeResponse parseSampledQuaternionAnimation()
    throws ASEImporter.InvalidFormatError, IOException
  {
    QuaternionKeyframeResponse keyframeResponse = new QuaternionKeyframeResponse();
    name.set("sampledQuaternionAnim");
    QuaternionSlerpSpline spline = new QuaternionSlerpSpline();
    spline.set(spline);
    
    tokenizer.nextToken();
    if (tokenizer.ttype != 123) {
      throw new InvalidFormatError(Messages.getString("Block_expected_after__CONTROL_ROT_TRACK"));
    }
    do {
      for (;;) {
        tokenizer.nextToken();
        
        if ((tokenizer.sval == null) || (!tokenizer.sval.equalsIgnoreCase("*CONTROL_ROT_SAMPLE"))) break;
        double time = parseDouble() * timeScaleFactor;
        

        double axis_x = -parseDouble();
        double axis_z = -parseDouble();
        double axis_y = parseDouble();
        double angle = -parseDouble();
        spline.addKey(new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKey(time, new edu.cmu.cs.stage3.math.Quaternion(new edu.cmu.cs.stage3.math.AxisAngle(axis_x, axis_y, axis_z, angle)))); }
      if (tokenizer.ttype == 125) {
        spline.correctForMAXRelativeKeys();
        return keyframeResponse;
      } } while (tokenizer.ttype != -1);
    throw new InvalidFormatError(Messages.getString("unfinished__CONTROL_ROT_TRACK"));
  }
  
  class InvalidFormatError extends Error
  {
    public InvalidFormatError(String s)
    {
      super();
    }
  }
  
  class ProgressDialog extends javax.swing.JDialog {
    protected JLabel linesLabel = new JLabel(Messages.getString("Lines_read__0"));
    protected JLabel objectLabel = new JLabel(Messages.getString("Object___none_"));
    protected JLabel progressLabel = new JLabel(Messages.getString("Loading__none___"));
    protected javax.swing.JProgressBar progressBar = new javax.swing.JProgressBar();
    protected javax.swing.JPanel progressPanel = new javax.swing.JPanel();
    protected javax.swing.Timer timer = null;
    
    public ProgressDialog() {
      super(Messages.getString("Importing___"), false);
      
      progressBar.setMinimum(0);
      linesLabel.setAlignmentX(0.0F);
      objectLabel.setAlignmentX(0.0F);
      progressPanel.setAlignmentX(0.0F);
      
      progressPanel.setLayout(new java.awt.BorderLayout());
      progressPanel.add("West", progressLabel);
      progressPanel.add("Center", progressBar);
      
      getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), 1));
      getContentPane().add(linesLabel);
      getContentPane().add(objectLabel);
      getContentPane().add(progressPanel);
      
      setDefaultCloseOperation(0);
      
      timer = new javax.swing.Timer(100, 
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent ev) {
            linesLabel.setText(Messages.getString("Lines_read__") + tokenizer.lineno());
            objectLabel.setText(Messages.getString("Object__") + currentObject);
            progressLabel.setText(Messages.getString("Loading_") + currentlyLoading + ": ");
            progressBar.setValue(currentProgress);
          }
          

        });
      pack();
      setVisible(true);
    }
    
    public void start() {
      timer.start();
    }
    
    public void stop() {
      timer.stop();
    }
    
    public void setMax(int max) {
      progressBar.setMaximum(max);
    }
  }
  
  private class Material {
    public String name;
    public edu.cmu.cs.stage3.alice.scenegraph.Color ambient;
    public edu.cmu.cs.stage3.alice.scenegraph.Color diffuse;
    public edu.cmu.cs.stage3.alice.scenegraph.Color specular;
    public double shine;
    public double shinestrength;
    public double transparency;
    public TextureMap ambientTexture = null;
    public TextureMap diffuseTexture = null;
    public TextureMap shineTexture = null;
    public TextureMap shineStrengthTexture = null;
    public TextureMap selfIllumTexture = null;
    public TextureMap opacityTexture = null;
    public TextureMap bumpTexture = null;
    
    private Material() {}
  }
}
