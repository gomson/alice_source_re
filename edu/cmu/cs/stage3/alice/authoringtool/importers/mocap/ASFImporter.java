package edu.cmu.cs.stage3.alice.authoringtool.importers.mocap;

import edu.cmu.cs.stage3.alice.authoringtool.AbstractImporter;
import edu.cmu.cs.stage3.alice.authoringtool.util.BackslashConverterFilterInputStream;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.io.EStreamTokenizer;
import edu.cmu.cs.stage3.math.Matrix33;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

























public class ASFImporter
  extends AbstractImporter
{
  protected ASFSkeleton skel;
  private static final World scene = new World();
  
  public ASFImporter()
  {
    skel = new ASFSkeleton();
  }
  
  public Map getExtensionMap() {
    HashMap map = new HashMap();
    map.put("ASF", "Acclaim Skeleton File");
    return map;
  }
  


  private Model parseASF(InputStream is)
    throws IOException
  {
    skel.anglescale = 1.0D;
    skel.lengthscale = 0.0254D;
    



    BackslashConverterFilterInputStream bcfis = new BackslashConverterFilterInputStream(is);
    BufferedReader br = new BufferedReader(new InputStreamReader(bcfis));
    EStreamTokenizer tokenizer = new EStreamTokenizer(br);
    
    tokenizer.commentChar(35);
    tokenizer.eolIsSignificant(true);
    tokenizer.lowerCaseMode(true);
    tokenizer.parseNumbers();
    tokenizer.wordChars(95, 95);
    tokenizer.wordChars(58, 58);
    

    while ((ttype != -3) || ((!sval.equals(":units")) && (!sval.equals(":root")))) {
      tokenizer.nextToken();
    }
    

    if (sval.equals(":units")) {
      tokenizer.nextToken();
      



      while ((ttype != -3) || (sval.charAt(0) != ':')) {
        if (ttype != -3) {
          tokenizer.nextToken();
        }
        else
        {
          if (sval.equals("length")) {
            tokenizer.nextToken();
            skel.lengthscale /= nval;
          }
          else if (sval.equals("angle")) {
            tokenizer.nextToken();
            if (sval.equals("deg")) {
              skel.anglescale = 0.017453292519943295D;
            }
          }
          tokenizer.nextToken();
        }
      }
      
      while ((ttype != -3) || (!sval.equals(":root"))) {
        tokenizer.nextToken();
      }
      tokenizer.nextToken();
    }
    




    skel.bones.addElement(new ASFBone("root"));
    skel.bones_dict.put("root", skel.bones.lastElement());
    ASFBone bone = skel.getRoot();
    
    while ((ttype != -3) || (sval.charAt(0) != ':')) {
      if (ttype != -3) {
        tokenizer.nextToken();
      }
      else
      {
        if (sval.equals("position")) {
          tokenizer.nextToken();
          base_position.x = (nval * skel.lengthscale);
          tokenizer.nextToken();
          base_position.y = (nval * skel.lengthscale);
          tokenizer.nextToken();
          base_position.z = (-nval * skel.lengthscale);
        } else if (sval.equals("orientation")) {
          tokenizer.nextToken();
          base_axis.rotateX(-nval * skel.anglescale);
          tokenizer.nextToken();
          base_axis.rotateY(-nval * skel.anglescale);
          tokenizer.nextToken();
          base_axis.rotateZ(nval * skel.anglescale);
        } else if (sval.equals("order")) {
          while (tokenizer.nextToken() != 10) {
            if (sval.equals("tx")) {
              dof.addElement(ASFBone.DOF_TX);
            } else if (sval.equals("ty")) {
              dof.addElement(ASFBone.DOF_TY);
            } else if (sval.equals("tz")) {
              dof.addElement(ASFBone.DOF_TZ);
            } else if (sval.equals("rx")) {
              dof.addElement(ASFBone.DOF_RX);
            } else if (sval.equals("ry")) {
              dof.addElement(ASFBone.DOF_RY);
            } else if (sval.equals("rz")) {
              dof.addElement(ASFBone.DOF_RZ);
            }
          }
        }
        tokenizer.nextToken();
      }
    }
    
    while ((ttype != -3) || (!sval.equals(":bonedata"))) {
      tokenizer.nextToken();
    }
    tokenizer.nextToken();
    





    while ((ttype != -3) || (sval.charAt(0) != ':'))
    {
      while ((ttype != -3) || ((sval.charAt(0) != ':') && (!sval.equals("begin")))) {
        tokenizer.nextToken();
      }
      if (sval.charAt(0) == ':')
        break;
      tokenizer.nextToken();
      


      bone = new ASFBone();
      
      while ((ttype != -3) || (!sval.equals("end"))) {
        if (ttype != -3) {
          tokenizer.nextToken();
        }
        else
        {
          if (sval.equals("axis")) {
            tokenizer.nextToken();
            base_axis.rotateX(-nval * skel.anglescale);
            tokenizer.nextToken();
            base_axis.rotateY(-nval * skel.anglescale);
            tokenizer.nextToken();
            base_axis.rotateZ(nval * skel.anglescale);
          } else if (sval.equals("direction")) {
            tokenizer.nextToken();
            direction.x = nval;
            tokenizer.nextToken();
            direction.y = nval;
            tokenizer.nextToken();
            direction.z = (-nval);
          } else if (sval.equals("length")) {
            tokenizer.nextToken();
            length = (nval * skel.lengthscale);
          } else if (sval.equals("name")) {
            tokenizer.nextToken();
            name = sval;
          }
          else if (sval.equals("dof")) {
            while (tokenizer.nextToken() != 10) {
              if (sval.equals("tx")) {
                dof.addElement(ASFBone.DOF_TX);
              } else if (sval.equals("ty")) {
                dof.addElement(ASFBone.DOF_TY);
              } else if (sval.equals("tz")) {
                dof.addElement(ASFBone.DOF_TZ);
              } else if (sval.equals("rx")) {
                dof.addElement(ASFBone.DOF_RX);
              } else if (sval.equals("ry")) {
                dof.addElement(ASFBone.DOF_RY);
              } else if (sval.equals("rz")) {
                dof.addElement(ASFBone.DOF_RZ);
              } else if (sval.equals("l")) {
                dof.addElement(ASFBone.DOF_L);
              }
            }
          }
          tokenizer.nextToken();
        }
      }
      tokenizer.nextToken();
      
      skel.bones.addElement(bone);
      skel.bones_dict.put(name, bone);
    }
    

    while ((ttype != -3) || (!sval.equals(":hierarchy"))) {
      tokenizer.nextToken();
    }
    tokenizer.nextToken();
    
    while ((ttype != -3) || (!sval.equals("begin"))) {
      tokenizer.nextToken();
    }
    tokenizer.nextToken();
    




    while ((ttype != -3) || (!sval.equals("end"))) {
      if (ttype != -3) {
        tokenizer.nextToken();
      }
      else {
        bone = (ASFBone)skel.bones_dict.get(sval);
        
        while (tokenizer.nextToken() != 10) {
          children.addElement(skel.bones_dict.get(sval));
        }
      }
    }
    return skel.buildBones();
  }
  
  protected Element load(InputStream is, String ext) throws IOException
  {
    return parseASF(is);
  }
  
  public ASFSkeleton loadSkeleton(InputStream is) throws IOException {
    parseASF(is);
    return skel;
  }
  
  public ASFSkeleton getSkeleton() {
    return skel;
  }
}
