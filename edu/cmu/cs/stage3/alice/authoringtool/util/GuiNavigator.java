package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Container;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;







public class GuiNavigator
  extends JPanel
  implements Runnable
{
  public static final int SLIDE_MODE = 1;
  public static final int DRIVE_MODE = 2;
  public static final int TILT_MODE = 3;
  public static final int SMALL_IMAGES = 1;
  public static final int LARGE_IMAGES = 2;
  protected int mode = 0;
  protected int imageSize = 2;
  protected long lastTime;
  protected Transformable objectToNavigate;
  protected Transformable coreHelper = new Transformable();
  protected World world;
  protected Vector3d tempVec = new Vector3d();
  protected Matrix44 oldTransformation;
  protected ImagePanel slidePanel = new ImagePanel();
  protected ImagePanel drivePanel = new ImagePanel();
  protected ImagePanel tiltPanel = new ImagePanel();
  protected NavMouseListener navMouseListener = new NavMouseListener();
  protected int buffer = 4;
  
  protected AuthoringTool authoringTool;
  protected Image slide = AuthoringToolResources.getImageForString("guiNavigator/slide");
  protected Image slideDown = AuthoringToolResources.getImageForString("guiNavigator/slideDown");
  protected Image slideDownLeft = AuthoringToolResources.getImageForString("guiNavigator/slideDownLeft");
  protected Image slideDownRight = AuthoringToolResources.getImageForString("guiNavigator/slideDownRight");
  protected Image slideHighlight = AuthoringToolResources.getImageForString("guiNavigator/slideHighlight");
  protected Image slideLeft = AuthoringToolResources.getImageForString("guiNavigator/slideLeft");
  protected Image slideRight = AuthoringToolResources.getImageForString("guiNavigator/slideRight");
  protected Image slideUp = AuthoringToolResources.getImageForString("guiNavigator/slideUp");
  protected Image slideUpLeft = AuthoringToolResources.getImageForString("guiNavigator/slideUpLeft");
  protected Image slideUpRight = AuthoringToolResources.getImageForString("guiNavigator/slideUpRight");
  protected Image drive = AuthoringToolResources.getImageForString("guiNavigator/drive");
  protected Image driveBack = AuthoringToolResources.getImageForString("guiNavigator/driveBack");
  protected Image driveBackLeft = AuthoringToolResources.getImageForString("guiNavigator/driveBackLeft");
  protected Image driveBackRight = AuthoringToolResources.getImageForString("guiNavigator/driveBackRight");
  protected Image driveForward = AuthoringToolResources.getImageForString("guiNavigator/driveForward");
  protected Image driveForwardLeft = AuthoringToolResources.getImageForString("guiNavigator/driveForwardLeft");
  protected Image driveForwardRight = AuthoringToolResources.getImageForString("guiNavigator/driveForwardRight");
  protected Image driveHighlight = AuthoringToolResources.getImageForString("guiNavigator/driveHighlight");
  protected Image driveLeft = AuthoringToolResources.getImageForString("guiNavigator/driveLeft");
  protected Image driveRight = AuthoringToolResources.getImageForString("guiNavigator/driveRight");
  protected Image tilt = AuthoringToolResources.getImageForString("guiNavigator/tilt");
  protected Image tiltDown = AuthoringToolResources.getImageForString("guiNavigator/tiltDown");
  protected Image tiltHighlight = AuthoringToolResources.getImageForString("guiNavigator/tiltHighlight");
  protected Image tiltUp = AuthoringToolResources.getImageForString("guiNavigator/tiltUp");
  
  public GuiNavigator() {
    setOpaque(false);
    
    slidePanel.setImage(slide);
    drivePanel.setImage(drive);
    tiltPanel.setImage(tilt);
    
    setLayout(new GridBagLayout());
    add(slidePanel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
    add(drivePanel, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
    add(tiltPanel, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
    
    slidePanel.addMouseListener(navMouseListener);
    drivePanel.addMouseListener(navMouseListener);
    tiltPanel.addMouseListener(navMouseListener);
    
    slidePanel.setToolTipText(Messages.getString("Move_the_Camera_Up__Down__Left__and_Right_"));
    drivePanel.setToolTipText(Messages.getString("Move_the_Camera_Forward__Backward__Turn_the_Camera_Left__Right_"));
    tiltPanel.setToolTipText(Messages.getString("Tilt_the_Camera_Forward_and_Backward_"));
  }
  
  public void setAuthoringTool(AuthoringTool authoringTool) {
    this.authoringTool = authoringTool;
  }
  
  public ImagePanel getSlidePanel() {
    return slidePanel;
  }
  
  public ImagePanel getDrivePanel() {
    return drivePanel;
  }
  
  public ImagePanel getTiltPanel() {
    return tiltPanel;
  }
  
  public void setObjectToNavigate(Transformable objectToNavigate) {
    this.objectToNavigate = objectToNavigate;
    if (objectToNavigate != null) {
      world = objectToNavigate.getWorld();
    } else {
      world = null;
    }
  }
  
  public void setImageSize(int size) {
    if (size == 1) {
      imageSize = size;
      slide = AuthoringToolResources.getImageForString("smallGuiNavigator/slide");
      slideDown = AuthoringToolResources.getImageForString("smallGuiNavigator/slideDown");
      slideDownLeft = AuthoringToolResources.getImageForString("smallGuiNavigator/slideDownLeft");
      slideDownRight = AuthoringToolResources.getImageForString("smallGuiNavigator/slideDownRight");
      slideHighlight = AuthoringToolResources.getImageForString("smallGuiNavigator/slideHighlight");
      slideLeft = AuthoringToolResources.getImageForString("smallGuiNavigator/slideLeft");
      slideRight = AuthoringToolResources.getImageForString("smallGuiNavigator/slideRight");
      slideUp = AuthoringToolResources.getImageForString("smallGuiNavigator/slideUp");
      slideUpLeft = AuthoringToolResources.getImageForString("smallGuiNavigator/slideUpLeft");
      slideUpRight = AuthoringToolResources.getImageForString("smallGuiNavigator/slideUpRight");
      drive = AuthoringToolResources.getImageForString("smallGuiNavigator/drive");
      driveBack = AuthoringToolResources.getImageForString("smallGuiNavigator/driveBack");
      driveBackLeft = AuthoringToolResources.getImageForString("smallGuiNavigator/driveBackLeft");
      driveBackRight = AuthoringToolResources.getImageForString("smallGuiNavigator/driveBackRight");
      driveForward = AuthoringToolResources.getImageForString("smallGuiNavigator/driveForward");
      driveForwardLeft = AuthoringToolResources.getImageForString("smallGuiNavigator/driveForwardLeft");
      driveForwardRight = AuthoringToolResources.getImageForString("smallGuiNavigator/driveForwardRight");
      driveHighlight = AuthoringToolResources.getImageForString("smallGuiNavigator/driveHighlight");
      driveLeft = AuthoringToolResources.getImageForString("smallGuiNavigator/driveLeft");
      driveRight = AuthoringToolResources.getImageForString("smallGuiNavigator/driveRight");
      tilt = AuthoringToolResources.getImageForString("smallGuiNavigator/tilt");
      tiltDown = AuthoringToolResources.getImageForString("smallGuiNavigator/tiltDown");
      tiltHighlight = AuthoringToolResources.getImageForString("smallGuiNavigator/tiltHighlight");
      tiltUp = AuthoringToolResources.getImageForString("smallGuiNavigator/tiltUp");
    } else if (size == 2) {
      imageSize = size;
      slide = AuthoringToolResources.getImageForString("guiNavigator/slide");
      slideDown = AuthoringToolResources.getImageForString("guiNavigator/slideDown");
      slideDownLeft = AuthoringToolResources.getImageForString("guiNavigator/slideDownLeft");
      slideDownRight = AuthoringToolResources.getImageForString("guiNavigator/slideDownRight");
      slideHighlight = AuthoringToolResources.getImageForString("guiNavigator/slideHighlight");
      slideLeft = AuthoringToolResources.getImageForString("guiNavigator/slideLeft");
      slideRight = AuthoringToolResources.getImageForString("guiNavigator/slideRight");
      slideUp = AuthoringToolResources.getImageForString("guiNavigator/slideUp");
      slideUpLeft = AuthoringToolResources.getImageForString("guiNavigator/slideUpLeft");
      slideUpRight = AuthoringToolResources.getImageForString("guiNavigator/slideUpRight");
      drive = AuthoringToolResources.getImageForString("guiNavigator/drive");
      driveBack = AuthoringToolResources.getImageForString("guiNavigator/driveBack");
      driveBackLeft = AuthoringToolResources.getImageForString("guiNavigator/driveBackLeft");
      driveBackRight = AuthoringToolResources.getImageForString("guiNavigator/driveBackRight");
      driveForward = AuthoringToolResources.getImageForString("guiNavigator/driveForward");
      driveForwardLeft = AuthoringToolResources.getImageForString("guiNavigator/driveForwardLeft");
      driveForwardRight = AuthoringToolResources.getImageForString("guiNavigator/driveForwardRight");
      driveHighlight = AuthoringToolResources.getImageForString("guiNavigator/driveHighlight");
      driveLeft = AuthoringToolResources.getImageForString("guiNavigator/driveLeft");
      driveRight = AuthoringToolResources.getImageForString("guiNavigator/driveRight");
      tilt = AuthoringToolResources.getImageForString("guiNavigator/tilt");
      tiltDown = AuthoringToolResources.getImageForString("guiNavigator/tiltDown");
      tiltHighlight = AuthoringToolResources.getImageForString("guiNavigator/tiltHighlight");
      tiltUp = AuthoringToolResources.getImageForString("guiNavigator/tiltUp");
    } else {
      AuthoringTool.showErrorDialog(Messages.getString("Invalid_image_size_constant_in_setImageSize__") + imageSize, null);
    }
    updateImages();
  }
  
  public void updateImages() {
    if (navMouseListener.isMouseDown()) {
      int offsetx = navMouseListener.getOffsetX();
      int offsety = navMouseListener.getOffsetY();
      if (mode == 1) {
        if (offsety > buffer) {
          if (offsetx > buffer) {
            slidePanel.setImage(slideDownRight);
          } else if (Math.abs(offsetx) > buffer) {
            slidePanel.setImage(slideDownLeft);
          } else {
            slidePanel.setImage(slideDown);
          }
        } else if (Math.abs(offsety) > buffer) {
          if (offsetx > buffer) {
            slidePanel.setImage(slideUpRight);
          } else if (Math.abs(offsetx) > buffer) {
            slidePanel.setImage(slideUpLeft);
          } else {
            slidePanel.setImage(slideUp);
          }
        }
        else if (offsetx > buffer) {
          slidePanel.setImage(slideRight);
        } else if (Math.abs(offsetx) > buffer) {
          slidePanel.setImage(slideLeft);
        } else {
          slidePanel.setImage(slideHighlight);
        }
        
        drivePanel.setImage(drive);
        tiltPanel.setImage(tilt);
      } else if (mode == 2) {
        if (offsety > buffer) {
          if (offsetx > buffer) {
            drivePanel.setImage(driveBackRight);
          } else if (Math.abs(offsetx) > buffer) {
            drivePanel.setImage(driveBackLeft);
          } else {
            drivePanel.setImage(driveBack);
          }
        } else if (Math.abs(offsety) > buffer) {
          if (offsetx > buffer) {
            drivePanel.setImage(driveForwardRight);
          } else if (Math.abs(offsetx) > buffer) {
            drivePanel.setImage(driveForwardLeft);
          } else {
            drivePanel.setImage(driveForward);
          }
        }
        else if (offsetx > buffer) {
          drivePanel.setImage(driveRight);
        } else if (Math.abs(offsetx) > buffer) {
          drivePanel.setImage(driveLeft);
        } else {
          drivePanel.setImage(driveHighlight);
        }
        
        slidePanel.setImage(slide);
        tiltPanel.setImage(tilt);
      } else if (mode == 3) {
        if (offsety > buffer) {
          tiltPanel.setImage(tiltUp);
        } else if (Math.abs(offsety) > buffer) {
          tiltPanel.setImage(tiltDown);
        } else {
          tiltPanel.setImage(tiltHighlight);
        }
        slidePanel.setImage(slide);
        drivePanel.setImage(drive);
      } else {
        slidePanel.setImage(slide);
        drivePanel.setImage(drive);
        tiltPanel.setImage(tilt);
      }
    }
    else if (mode == 1) {
      slidePanel.setImage(slideHighlight);
      drivePanel.setImage(drive);
      tiltPanel.setImage(tilt);
    } else if (mode == 2) {
      slidePanel.setImage(slide);
      drivePanel.setImage(driveHighlight);
      tiltPanel.setImage(tilt);
    } else if (mode == 3) {
      slidePanel.setImage(slide);
      drivePanel.setImage(drive);
      tiltPanel.setImage(tiltHighlight);
    } else {
      slidePanel.setImage(slide);
      drivePanel.setImage(drive);
      tiltPanel.setImage(tilt);
    }
  }
  
  public void run()
  {
    int offsetx = navMouseListener.getOffsetX();
    int offsety = navMouseListener.getOffsetY();
    
    long time = System.currentTimeMillis();
    long deltaTime = time - lastTime;
    





    if (deltaTime > 0L) {
      double dt = deltaTime * 0.001D;
      tempVec.x = 0.0D;
      tempVec.y = 0.0D;
      tempVec.z = 0.0D;
      

      coreHelper.setPositionRightNow(tempVec, objectToNavigate);
      Matrix3d m = objectToNavigate.getOrientationAsAxes(world);
      
      tempVec.x = m20;
      tempVec.y = 0.0D;
      tempVec.z = m22;
      
      coreHelper.setOrientationRightNow(tempVec, MathUtilities.getYAxis(), world);
      
      switch (mode) {
      case 1: 
        tempVec.x = 0.0D;
        tempVec.y = (-0.1D * offsety * dt);
        tempVec.z = 0.0D;
        objectToNavigate.moveRightNow(tempVec, world);
        
        tempVec.x = (0.05D * offsetx * dt);
        tempVec.y = 0.0D;
        tempVec.z = 0.0D;
        objectToNavigate.moveRightNow(tempVec, objectToNavigate);
        break;
      case 2: 
        if (objectToNavigate.getOrientationAsAxes(world).m11 < 0.0D) {
          tempVec.x = -1.0D;
          tempVec.y = -1.0D;
          tempVec.z = 1.0D;
          coreHelper.scaleSpaceRightNow(tempVec, null);
        }
        
        if (navMouseListener.shiftIsDown) {
          tempVec.x = 0.0D;tempVec.y = (-0.05D * offsety * dt);tempVec.z = 0.0D;
          objectToNavigate.moveRightNow(tempVec, world);
        } else if (navMouseListener.controlIsDown) {
          objectToNavigate.rotateRightNow(MathUtilities.getYAxis(), 0.001D * offsetx * dt, coreHelper);
        } else {
          tempVec.x = 0.0D;tempVec.y = 0.0D;tempVec.z = (-0.05D * offsety * dt);
          objectToNavigate.moveRightNow(tempVec, coreHelper);
          objectToNavigate.rotateRightNow(MathUtilities.getYAxis(), 6.0E-4D * offsetx * dt, coreHelper);
        }
        break;
      case 3: 
        objectToNavigate.rotateRightNow(MathUtilities.getXAxis(), -0.001D * offsety * dt, objectToNavigate);
      }
      
    }
    lastTime = time; }
  
  class NavMouseListener extends ScreenWrappingMouseListener { NavMouseListener() {}
    
    protected Component source = null;
    public boolean shiftIsDown = false;
    public boolean controlIsDown = false;
    
    protected void setMode(MouseEvent ev) {
      if (ev == null) {
        mode = 0;
      }
      else if (ev.getComponent() == slidePanel) {
        mode = 1;
      } else if (ev.getComponent() == drivePanel) {
        mode = 2;
      } else if (ev.getComponent() == tiltPanel) {
        mode = 3;
      }
    }
    

    public void mousePressed(MouseEvent ev)
    {
      super.mousePressed(ev);
      


      lastTime = System.currentTimeMillis();
      
      authoringTool.getUndoRedoStack().setIsListening(false);
      oldTransformation = objectToNavigate.getLocalTransformation();
      setMode(ev);
      if (imageSize == 2) {
        if (mode == 1) {
          pressedx = 39;
          pressedy = 38;
        } else if (mode == 2) {
          pressedx = 58;
          pressedy = 28;
        } else if (mode == 3) {
          pressedx = 31;
          pressedy = 42;
        }
      } else if (imageSize == 1) {
        if (mode == 1) {
          pressedx = 19;
          pressedy = 18;
        } else if (mode == 2) {
          pressedx = 29;
          pressedy = 13;
        } else if (mode == 3) {
          pressedx = 14;
          pressedy = 20;
        }
      } else {
        AuthoringTool.showErrorDialog(Messages.getString("Invalid_image_size_constant_in_NavMouseListener__") + imageSize, null);
      }
      
      super.mouseDragged(ev);
      
      Container root = world.getSceneGraphScene();
      
      coreHelper.vehicle.set(world);
      coreHelper.setTransformationRightNow(objectToNavigate.getTransformation(world), world);
      
      shiftIsDown = ev.isShiftDown();
      controlIsDown = ev.isControlDown();
      
      authoringTool.getScheduler().addEachFrameRunnable(GuiNavigator.this);
      updateImages();
    }
    
    public void mouseReleased(MouseEvent ev) {
      super.mouseReleased(ev);
      
      authoringTool.getScheduler().removeEachFrameRunnable(GuiNavigator.this);
      
      authoringTool.getUndoRedoStack().push(new PointOfViewUndoableRedoable(objectToNavigate, oldTransformation, objectToNavigate.getLocalTransformation(), authoringTool.getOneShotScheduler()));
      authoringTool.getUndoRedoStack().setIsListening(true);
      if (ev.getComponent().contains(ev.getPoint())) {
        setMode(ev);
      } else {
        setMode(null);
      }
      updateImages();
    }
    
    public void mouseDragged(MouseEvent ev) {
      super.mouseDragged(ev);
      
      shiftIsDown = ev.isShiftDown();
      controlIsDown = ev.isControlDown();
      setMode(ev);
      updateImages();
    }
    
    public void mouseEntered(MouseEvent ev) {
      setMode(ev);
      updateImages();
    }
    
    public void mouseExited(MouseEvent ev) { setMode(null);
      updateImages();
    }
    
    public void mouseMoved(MouseEvent ev) { setMode(ev);
      updateImages();
    }
    
    public void mouseClicked(MouseEvent ev) {}
  }
}
