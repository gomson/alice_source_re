package edu.cmu.cs.stage3.alice.authoringtool.importers.mocap;

import edu.cmu.cs.stage3.alice.authoringtool.AbstractImporter;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.util.BackslashConverterFilterInputStream;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Pose;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.io.EStreamTokenizer;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Vector3;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import javax.vecmath.Vector3d;























public class AMCImporter
  extends AbstractImporter
{
  protected ASFSkeleton skel = null;
  private static final World scene = new World();
  protected Model applyTo = null;
  
  protected double fps = 30.0D;
  protected int nativeFPS = 60;
  
  protected String animationName = "motionCaptureAnimation";
  protected String AMCPath = "";
  
  public AMCImporter() {}
  
  public Map getExtensionMap()
  {
    HashMap map = new HashMap();
    map.put("AMC", "Acclaim Motion Capture");
    return map;
  }
  
  public Element load(String filename) throws IOException {
    animationName = new File(filename).getName();
    animationName = animationName.substring(0, animationName.lastIndexOf('.'));
    if (animationName.lastIndexOf('.') != -1) {
      animationName = animationName.substring(animationName.lastIndexOf('.') + 1);
    }
    AMCPath = new File(filename).getAbsolutePath();
    AMCPath = AMCPath.substring(0, AMCPath.indexOf(new File(filename).getName()));
    return super.load(filename);
  }
  
  public Element load(File file) throws IOException {
    animationName = file.getName();
    animationName = animationName.substring(0, animationName.lastIndexOf('.'));
    if (animationName.lastIndexOf('.') != -1) {
      animationName = animationName.substring(animationName.lastIndexOf('.') + 1);
    }
    AMCPath = file.getAbsolutePath();
    AMCPath = AMCPath.substring(0, AMCPath.indexOf(file.getName()));
    return super.load(file);
  }
  
  public Element load(URL url) throws IOException {
    String externalForm = url.toExternalForm();
    String fullName = externalForm.substring(externalForm.lastIndexOf('/') + 1);
    animationName = fullName.substring(0, fullName.lastIndexOf('.'));
    if (animationName.lastIndexOf('.') != -1) {
      animationName = animationName.substring(animationName.lastIndexOf('.') + 1);
    }
    return super.load(url);
  }
  
  protected Element load(InputStream is, String ext) throws IOException
  {
    BackslashConverterFilterInputStream bcfis = new BackslashConverterFilterInputStream(is);
    BufferedReader br = new BufferedReader(new InputStreamReader(bcfis));
    EStreamTokenizer tokenizer = new EStreamTokenizer(br);
    
    tokenizer.commentChar(35);
    tokenizer.eolIsSignificant(false);
    tokenizer.lowerCaseMode(true);
    tokenizer.parseNumbers();
    tokenizer.wordChars(95, 95);
    tokenizer.wordChars(58, 58);
    

    String ASFfilename = "";
    String ASFpath = "";
    double dt = 1.0D / nativeFPS;
    
    while ((ttype != -3) || (!sval.equals(":degrees"))) {
      if (ttype != -3) {
        tokenizer.nextToken();
      }
      else {
        if (sval.equals(":asf-file")) {
          tokenizer.nextToken();
          ASFfilename = sval;
        } else if (sval.equals(":asf-path")) {
          tokenizer.nextToken();
          ASFpath = sval;
        } else if (sval.equals(":samples-per-second")) {
          tokenizer.nextToken();
          nativeFPS = ((int)nval);
          dt = 1.0D / nativeFPS;
        }
        tokenizer.nextToken();
      }
    }
    File ASFfile = new File("");
    
    if (!ASFfilename.equals("")) {
      ASFfile = new File(ASFfilename);
      if (!ASFfile.isFile())
      {
        int previ = 0;
        for (int i = ASFpath.indexOf(";"); i != -1; i = ASFpath.indexOf(";", i + 1)) {
          String temp = ASFpath.substring(previ, i - 1);
          if ((!temp.endsWith(File.separator)) && (!temp.equals(""))) temp = temp.concat(File.separator);
          ASFfile = new File(temp.concat(ASFfilename));
          if (ASFfile.isFile()) break;
          previ = i + 1;
        }
        if (!ASFfile.isFile()) {
          String temp = ASFpath.substring(previ);
          if ((!temp.endsWith(File.separator)) && (!temp.equals(""))) temp = temp.concat(File.separator);
          ASFfile = new File(temp.concat(ASFfilename));
        }
        if (!ASFfile.isFile()) {
          String temp = AMCPath;
          if ((!temp.endsWith(File.separator)) && (!temp.equals(""))) temp = temp.concat(File.separator);
          ASFfile = new File(temp.concat(ASFfilename));
        }
      }
    }
    


    MocapImporterOptionsDialog optionsDialog = new MocapImporterOptionsDialog();
    
    if (ASFfile.isFile()) {
      optionsDialog.setASFFile(ASFfile.getPath());
      optionsDialog.setASFPath(ASFfile.getParentFile());
    } else {
      optionsDialog.setASFPath(new File(AMCPath)); }
    optionsDialog.setNativeFPS(nativeFPS);
    optionsDialog.pack();
    optionsDialog.setVisible(true);
    
    if (!ok) {
      return null;
    }
    applyTo = ((Model)optionsDialog.getSelectedPart());
    ASFfile = new File(optionsDialog.getASFFile());
    fps = optionsDialog.getFPS();
    nativeFPS = optionsDialog.getNativeFPS();
    dt = 1.0D / nativeFPS;
    
    if (!ASFfile.isFile()) { return null;
    }
    InputStream ASFis = new FileInputStream(ASFfile);
    skel = new ASFImporter().loadSkeleton(ASFis);
    ASFis.close();
    
    int samplenumber = 0;
    
    if (applyTo == null) {
      applyTo = skel.getRoot().model;
    }
    skel.setBasePose(applyTo);
    

    tokenizer.nextToken();
    while (ttype != -1) {
      samplenumber = (int)nval;
      tokenizer.nextToken();
      


      while (ttype == -3) {
        ASFBone bone = (ASFBone)skel.bones_dict.get(sval);
        tokenizer.nextToken();
        
        position = ((Vector3)base_position.clone());
        axis = new Matrix33();
        

        ListIterator li2 = dof.listIterator();
        while (li2.hasNext()) {
          Integer d = (Integer)li2.next();
          if (!d.equals(ASFBone.DOF_L))
          {
            if (d.equals(ASFBone.DOF_TX)) {
              position.x = (nval * skel.lengthscale);
            } else if (d.equals(ASFBone.DOF_TY)) {
              position.y = (nval * skel.lengthscale);
            } else if (d.equals(ASFBone.DOF_TZ)) {
              position.z = (-nval * skel.lengthscale);
            } else if (d.equals(ASFBone.DOF_RX)) {
              axis.rotateX(-nval * skel.anglescale);
            } else if (d.equals(ASFBone.DOF_RY)) {
              axis.rotateY(-nval * skel.anglescale);
            } else if (d.equals(ASFBone.DOF_RZ))
              axis.rotateZ(nval * skel.anglescale);
          }
          tokenizer.nextToken();
        }
        
        if (lastTime + 1.0D / fps <= (samplenumber - 1) * dt) {
          lastTime = ((samplenumber - 1) * dt);
          hasFrame = true;
        }
      }
      skel.addFrames();
    }
    

    Response anim = skel.buildAnim();
    Pose[] poses = skel.buildPoses();
    
    name.set(AuthoringToolResources.getNameForNewChild(animationName, applyTo));
    0name.set(AuthoringToolResources.getNameForNewChild(animationName + "_startPose", applyTo));
    1name.set(AuthoringToolResources.getNameForNewChild(animationName + "_endPose", applyTo));
    
    applyTo.responses.add(anim);
    applyTo.misc.add(poses[0]);
    applyTo.misc.add(poses[1]);
    
    applyTo.addChild(anim);
    applyTo.addChild(poses[0]);
    applyTo.addChild(poses[1]);
    
    if (applyTo == skel.getRoot().model) {
      applyTo.name.set("MocapSkeleton");
    }
    return applyTo;
  }
}
