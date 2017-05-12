package edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator;
import edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode;
import edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetMultiManipulator;
import edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype;
import edu.cmu.cs.stage3.alice.core.Dummy;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Group;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.core.decorator.BoundingBoxDecorator;
import edu.cmu.cs.stage3.alice.core.decorator.BoxDecorator;
import edu.cmu.cs.stage3.alice.core.decorator.PivotDecorator;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.VehicleProperty;
import edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent;
import edu.cmu.cs.stage3.awt.AWTUtilities;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Matrix44;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public class CameraViewPanel extends JPanel implements edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener, edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorListener
{
  public static final int NAVIGATOR_TAB = 0;
  public static final int MANIPULATOR_TAB = 1;
  public static final int SINGLE_VIEW_MODE = 0;
  public static final int QUAD_VIEW_MODE = 1;
  public static final int MORE_CONTROLS = 0;
  public static final int FEWER_CONTROLS = 1;
  public static final String NONE_DUMMY = Messages.getString("_None_");
  
  protected World world;
  protected SceneEditor sceneEditor;
  public static OnscreenRenderTarget renderTarget = null;
  protected edu.cmu.cs.stage3.alice.core.Camera renderCamera = null;
  
  protected Matrix44 originalCameraPOV;
  protected RenderTargetMultiManipulator rtmm;
  protected edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable = null;
  protected edu.cmu.cs.stage3.alice.core.Transformable selectedTransformable = null;
  protected edu.cmu.cs.stage3.alice.core.Transformable blankTransformable = new edu.cmu.cs.stage3.alice.core.Transformable();
  protected java.util.HashMap resizeTable = new java.util.HashMap();
  
  GuiNavigator guiNavigator;
  
  edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer galleryViewer;
  
  protected double minimumViewingAngle;
  protected ButtonGroup singleQuadGroup = new ButtonGroup();
  protected ButtonGroup mouseModeGroup = new ButtonGroup();
  protected int viewMode = -1;
  protected BoundingBoxDecorator boundingBoxDecorator = new BoundingBoxDecorator();
  protected PivotDecorator pivotDecorator = new PivotDecorator();
  protected boolean showDroppingFeedback = false;
  protected Vector3d droppingFeedbackDimensions;
  protected Vector3d droppingFeedbackLocation;
  protected BoxDecorator dropFeedbackDecorator = new BoxDecorator();
  protected PivotDecorator dropFeedbackPivotDecorator = new PivotDecorator();
  protected edu.cmu.cs.stage3.alice.core.Transformable pickFeedbackTransformable = new edu.cmu.cs.stage3.alice.core.Transformable();
  protected edu.cmu.cs.stage3.alice.core.Transformable boundingBoxFeedbackTransformable;
  protected edu.cmu.cs.stage3.math.Plane pickingPlane = new edu.cmu.cs.stage3.math.Plane(new Vector3d(0.0D, 0.0D, 0.0D), new Vector3d(0.0D, 1.0D, 0.0D));
  protected Element ground;
  protected boolean successFullVisualDrop = false;
  



  protected edu.cmu.cs.stage3.alice.scenegraph.Transformable helper = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
  protected edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = null;
  protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgCameraTransformable = null;
  protected edu.cmu.cs.stage3.alice.scenegraph.Scene sgScene = null;
  protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgIdentity = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
  protected Point originalMousePoint = new Point();
  protected boolean firstTimeKeyIsDown = true;
  protected Vector3d tempVec = new Vector3d();
  protected Vector3d zeroVec = new Vector3d(0.0D, 0.0D, 0.0D);
  protected javax.vecmath.Vector4d tempVec4 = new javax.vecmath.Vector4d();
  protected Vector3d cameraForward = new Vector3d();
  protected Vector3d cameraUp = new Vector3d();
  protected Matrix44 oldTransformation;
  protected java.awt.Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(""), new Point(0, 0), "invisible cursor");
  
  protected java.awt.Cursor savedCursor;
  
  protected RenderTargetManipulatorMode defaultMoveMode;
  
  protected RenderTargetManipulatorMode moveUpDownMode;
  
  protected RenderTargetManipulatorMode turnLeftRightMode;
  
  protected RenderTargetManipulatorMode turnForwardBackwardMode;
  
  protected RenderTargetManipulatorMode tumbleMode;
  
  protected RenderTargetManipulatorMode resizeMode;
  protected RenderTargetManipulatorMode copyMode;
  protected RenderTargetManipulatorMode orthoScrollMode;
  protected RenderTargetManipulatorMode orthoZoomInMode;
  ManipulatorModeButton defaultMoveModeButton;
  ManipulatorModeButton moveUpDownModeButton;
  ManipulatorModeButton turnLeftRightModeButton;
  ManipulatorModeButton turnForwardBackwardModeButton;
  ManipulatorModeButton tumbleModeButton;
  ManipulatorModeButton resizeModeButton;
  ManipulatorModeButton copyModeButton;
  ManipulatorModeButton orthoScrollModeButton;
  ManipulatorModeButton orthoZoomInModeButton;
  protected ManipulatorModeButton singleViewButtonSave;
  protected ManipulatorModeButton quadViewButtonSave;
  protected OnscreenRenderTarget renderTargetFromTheRight = null;
  protected OnscreenRenderTarget renderTargetFromTheTop = null;
  protected OnscreenRenderTarget renderTargetFromTheFront = null;
  
  protected edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera cameraFromTheRight = new edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera();
  protected edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera cameraFromTheTop = new edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera();
  protected edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera cameraFromTheFront = new edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera();
  
  protected edu.cmu.cs.stage3.alice.core.Transformable camTransformableFromTheRight = new edu.cmu.cs.stage3.alice.core.Transformable();
  protected edu.cmu.cs.stage3.alice.core.Transformable camTransformableFromTheTop = new edu.cmu.cs.stage3.alice.core.Transformable();
  protected edu.cmu.cs.stage3.alice.core.Transformable camTransformableFromTheFront = new edu.cmu.cs.stage3.alice.core.Transformable();
  
  protected RenderTargetMultiManipulator rtmmFromTheRight;
  
  protected RenderTargetMultiManipulator rtmmFromTheTop;
  protected RenderTargetMultiManipulator rtmmFromTheFront;
  protected JPanel quadPanel = new JPanel();
  
  protected String fromRightText = Messages.getString("from_the_right");
  protected String fromTopText = Messages.getString("from_the_top");
  protected String fromFrontText = Messages.getString("from_the_front");
  
  protected String selectedButtonText = Messages.getString("Mouse_Controls");
  protected Color defaultBorderColor = new Color(102, 102, 153);
  protected Color highlightBorderColor = new Color(102, 153, 102);
  
  protected Configuration authoringToolConfig = Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());
  protected Color viewLabelColor = new Color(255, 255, 255, 128);
  private int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
  protected java.awt.Font viewLabelFont = new java.awt.Font("SansSerif", 1, (int)(18 * fontSize / 12.0D));
  
  protected boolean originalTileDraggingOption;
  protected javax.swing.ListCellRenderer enumerableComboRenderer = new javax.swing.DefaultListCellRenderer() {
    public Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if ((value instanceof edu.cmu.cs.stage3.util.Enumerable)) {
        value = ((edu.cmu.cs.stage3.util.Enumerable)value).getRepr();
      }
      return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
  };
  
  protected java.awt.dnd.DropTargetListener renderTargetDropTargetListener = new java.awt.dnd.DropTargetListener() {
    private edu.cmu.cs.stage3.alice.authoringtool.importers.ImageImporter imageImporter = new edu.cmu.cs.stage3.alice.authoringtool.importers.ImageImporter();
    
    private boolean checkDrag(DropTargetDragEvent dtde) { boolean toReturn = false;
      if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, java.awt.datatransfer.DataFlavor.javaFileListFlavor)) {
        toReturn = true;
      } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor)) {
        toReturn = true;
      } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.transformableReferenceFlavor)) {
        toReturn = true;
      } else { if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor)) {
          try {
            Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
            if ((transferable instanceof edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable)) {
              dtde.rejectDrag();
              return false;
            }
            ResponsePrototype prototype = (ResponsePrototype)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor);
            if ((edu.cmu.cs.stage3.alice.core.response.CompositeResponse.class.isAssignableFrom(prototype.getElementClass())) || 
              (edu.cmu.cs.stage3.alice.core.response.Print.class.isAssignableFrom(prototype.getElementClass())) || 
              (edu.cmu.cs.stage3.alice.core.response.Comment.class.isAssignableFrom(prototype.getElementClass()))) {
              dtde.rejectDrag();
              return false;
            }
            toReturn = true;
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }
        





        dtde.rejectDrag();
        return false;
      }
      if ((dtde.getDropAction() & 0x1) > 0) {
        dtde.acceptDrag(1);
      } else if ((dtde.getDropAction() & 0x2) > 0) {
        dtde.acceptDrag(2);
      } else if ((dtde.getDropAction() & 0x40000000) > 0) {
        dtde.acceptDrag(1073741824);
      }
      return toReturn;
    }
    
    private void startDraggingFromGallery() {
      GalleryObject galleryObject = getGalleryObject(edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentDragComponent());
      startDraggingFromGallery(galleryObject);
    }
    
    private void startDraggingFromGallery(GalleryObject galleryObject) {
      if (galleryObject != null) {
        authoringToolConfig.setValue("gui.pickUpTiles", "false");
        Vector3d dimensions = galleryObject.getBoundingBox();
        
        if (!showDroppingFeedback) {
          showDroppingFeedback = true;
          dropFeedbackDecorator.setIsShowing(true);
          dropFeedbackPivotDecorator.setIsShowing(true);
        }
        dropFeedbackDecorator.setWidth(x);
        dropFeedbackDecorator.setHeight(y);
        dropFeedbackDecorator.setDepth(z);
        edu.cmu.cs.stage3.math.Box boundingBox = new edu.cmu.cs.stage3.math.Box();
        boundingBox.setMinimum(new Vector3d(0.0D, 0.0D, 0.0D));
        boundingBox.setMaximum(new Vector3d(x / 2.0D, y, z / 2.0D));
        dropFeedbackPivotDecorator.setOverrideBoundingBox(boundingBox);
        
        Rectangle renderRect = renderPanel.getBounds();
        Point correctPoint = renderPanel.getLocationOnScreen();
        originalMousePoint.x = (x + width / 2);
        originalMousePoint.y = (y + height / 2);
        

        sgCamera = CameraViewPanel.renderTarget.getCameras()[0];
        sgCameraTransformable = ((edu.cmu.cs.stage3.alice.scenegraph.Transformable)sgCamera.getParent());
        sgScene = ((edu.cmu.cs.stage3.alice.scenegraph.Scene)sgCamera.getRoot());
        
        oldTransformation = new Matrix44(boundingBoxFeedbackTransformable.getSceneGraphTransformable().getLocalTransformation());
        
        helper.setParent(sgScene);
        sgIdentity.setParent(sgScene);
      }
    }
    
    private void stopDraggingFromGallery() {
      if (originalTileDraggingOption) {
        authoringToolConfig.setValue("gui.pickUpTiles", "true");
      }
      else {
        authoringToolConfig.setValue("gui.pickUpTiles", "false");
      }
      showDroppingFeedback = false;
      dropFeedbackDecorator.setIsShowing(false);
      dropFeedbackPivotDecorator.setIsShowing(false);
      firstTimeKeyIsDown = true;
    }
    
    public void dragEnter(DropTargetDragEvent dtde)
    {
      checkDrag(dtde);
      
      originalTileDraggingOption = authoringToolConfig.getValue("gui.pickUpTiles").equalsIgnoreCase("true");
      
      if ((!authoringToolConfig.getValue("showObjectLoadFeedback").equalsIgnoreCase("true")) || ((!AuthoringToolResources.safeIsDataFlavorSupported(dtde, java.awt.datatransfer.DataFlavor.javaFileListFlavor)) && (!AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor))))
      {







        successFullVisualDrop = false;
      }
    }
    
    public void dragExit(java.awt.dnd.DropTargetEvent dte) {
      stopDraggingFromGallery();
    }
    
    private GalleryObject getGalleryObject(Component sourceComponent) {
      while ((sourceComponent != null) && (!(sourceComponent instanceof GalleryObject))) {
        sourceComponent = sourceComponent.getParent();
      }
      if ((sourceComponent instanceof GalleryObject)) {
        return (GalleryObject)sourceComponent;
      }
      
      return null;
    }
    

    private void turnAndRaiseManipulator(int keyMode, Point point)
    {
      int dx = x - originalMousePoint.x;
      int dy = y - originalMousePoint.y;
      edu.cmu.cs.stage3.alice.scenegraph.Transformable sgPickedTransformable = boundingBoxFeedbackTransformable.getSceneGraphTransformable();
      double deltaFactor; double deltaFactor; if ((sgCamera instanceof edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera)) {
        edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthoCamera = (edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera)sgCamera;
        double nearClipHeightInScreen = CameraViewPanel.renderTarget.getAWTComponent().getHeight();
        double nearClipHeightInWorld = orthoCamera.getPlane()[3] - orthoCamera.getPlane()[1];
        deltaFactor = nearClipHeightInWorld / nearClipHeightInScreen;
      } else {
        double projectionMatrix11 = CameraViewPanel.renderTarget.getProjectionMatrix(sgCamera).getElement(1, 1);
        double nearClipDist = sgCamera.getNearClippingPlaneDistance();
        double nearClipHeightInWorld = 2.0D * (nearClipDist / projectionMatrix11);
        double nearClipHeightInScreen = CameraViewPanel.renderTarget.getAWTComponent().getHeight();
        double pixelHeight = nearClipHeightInWorld / nearClipHeightInScreen;
        double objectDist = sgPickedTransformable.getPosition(sgCameraTransformable).getLength();
        deltaFactor = objectDist * pixelHeight / nearClipDist;
      }
      
      boolean shiftDown = keyMode >= 2;
      boolean controlDown = (keyMode == 1) || (keyMode == 3);
      


      if (controlDown) {
        if (shiftDown) {
          helper.setTransformation(MathUtilities.createIdentityMatrix4d(), sgCameraTransformable);
          helper.setPosition(zeroVec, sgPickedTransformable);
          sgPickedTransformable.rotate(MathUtilities.getXAxis(), -dy * 0.01D, helper);
          sgPickedTransformable.rotate(MathUtilities.getYAxis(), -dx * 0.01D, sgPickedTransformable);
        } else {
          helper.setTransformation(MathUtilities.createIdentityMatrix4d(), sgScene);
          helper.setPosition(zeroVec, sgPickedTransformable);
          sgPickedTransformable.rotate(MathUtilities.getYAxis(), -dx * 0.01D, helper);
        }
      } else if (shiftDown) {
        helper.setTransformation(MathUtilities.createIdentityMatrix4d(), sgScene);
        helper.setPosition(zeroVec, sgPickedTransformable);
        tempVec.x = 0.0D;
        tempVec.y = (-dy * deltaFactor);
        tempVec.z = 0.0D;
        sgPickedTransformable.translate(tempVec, helper);
      } else {
        Matrix4d cameraTransformation = sgCameraTransformable.getAbsoluteTransformation();
        cameraUp.x = m10;
        cameraUp.y = m11;
        cameraUp.z = m12;
        cameraForward.x = m20;
        cameraForward.y = m21;
        cameraForward.z = m22;
        
        helper.setPosition(zeroVec, sgPickedTransformable);
        if (Math.abs(cameraForward.y) < Math.abs(cameraUp.y)) {
          cameraForward.y = 0.0D;
          helper.setOrientation(cameraForward, cameraUp, sgScene);
        }
        else {
          cameraUp.y = 0.0D;
          cameraForward.negate();
          helper.setOrientation(cameraUp, cameraForward, sgScene);
        }
        

        tempVec.x = (dx * deltaFactor);
        tempVec.y = 0.0D;
        tempVec.z = (-dy * deltaFactor);
        sgPickedTransformable.translate(tempVec, helper);
      }
    }
    
































    private void placeMouseOnBoundingBoxBottom()
    {
      Vector3d xyzInCamera = boundingBoxFeedbackTransformable.transformTo(new Vector3d(0.0D, 0.0D, 0.0D), renderCamera);
      Vector3d xyzInViewport = CameraViewPanel.renderTarget.transformFromCameraToViewport(xyzInCamera, renderCamera.getSceneGraphCamera());
      Rectangle rect = CameraViewPanel.renderTarget.getActualViewport(renderCamera.getSceneGraphCamera());
      Point newPoint = new Point((int)x + x + renderPanel.getLocationOnScreen().x, (int)y + y + renderPanel.getLocationOnScreen().y);
      AWTUtilities.setCursorLocation(newPoint);
      pickingPlane = new edu.cmu.cs.stage3.math.Plane(boundingBoxFeedbackTransformable.getPosition(world), new Vector3d(0.0D, 1.0D, 0.0D));
      boundingBoxFeedbackTransformable.setPositionRightNow(0.0D, 0.0D, 0.0D);
    }
    
    public void dragOver(DropTargetDragEvent dtde) {
      checkDrag(dtde);
      if ((authoringToolConfig.getValue("showObjectLoadFeedback").equalsIgnoreCase("true")) && ((AuthoringToolResources.safeIsDataFlavorSupported(dtde, java.awt.datatransfer.DataFlavor.javaFileListFlavor)) || (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor)))) {
        GalleryObject galleryObject = getGalleryObject(edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentDragComponent());
        if (galleryObject != null) {
          int keyMode = 0;
          if (AWTUtilities.isKeyPressed(17)) {
            keyMode++;
          }
          if (AWTUtilities.isKeyPressed(16)) {
            keyMode += 2;
          }
          if (keyMode > 0) {
            if (firstTimeKeyIsDown) {
              firstTimeKeyIsDown = false;
              AWTUtilities.setCursorLocation(originalMousePoint);
              AWTUtilities.setIsCursorShowing(false);
            }
            else
            {
              turnAndRaiseManipulator(keyMode, AWTUtilities.getCursorLocation());
              AWTUtilities.setCursorLocation(originalMousePoint);
            }
          } else {
            if (!firstTimeKeyIsDown) {
              placeMouseOnBoundingBoxBottom();
              AWTUtilities.setIsCursorShowing(true);
              
              firstTimeKeyIsDown = true;
            }
            
            if (ground != null) {
              successFullVisualDrop = true;
              if (!showDroppingFeedback) {
                startDraggingFromGallery(galleryObject);
              }
              Point myPoint2 = AWTUtilities.getCursorLocation();
              Point panelLocation = renderPanel.getLocationOnScreen();
              Rectangle rect = CameraViewPanel.renderTarget.getActualViewport(renderCamera.getSceneGraphCamera());
              if ((myPoint2 != null) && (rect != null) && (panelLocation != null)) {
                x -= x + x;
                y -= y + y;
              }
              try {
                edu.cmu.cs.stage3.math.Ray ray = CameraViewPanel.renderTarget.getRayAtPixel(renderCamera.getSceneGraphCamera(), x, y);
                Matrix44 cameraToGround = renderCamera.getTransformation((edu.cmu.cs.stage3.alice.core.ReferenceFrame)ground);
                ray.transform(cameraToGround);
                double intersection = pickingPlane.intersect(ray);
                Vector3d intersectionPoint = ray.getPoint(intersection);
                Vector3d oldLocation = pickFeedbackTransformable.getPosition();
                pickFeedbackTransformable.setPositionRightNow(intersectionPoint);
                Vector3d pointInCameraSpace = boundingBoxFeedbackTransformable.getPosition(renderCamera);
                if (z >= 0.0D) return;
                pickFeedbackTransformable.setPositionRightNow(oldLocation);
                successFullVisualDrop = false;
                stopDraggingFromGallery();
              }
              catch (Exception e)
              {
                successFullVisualDrop = false;
                stopDraggingFromGallery();
              }
            } else {
              successFullVisualDrop = false;
              stopDraggingFromGallery();
            }
          }
        }
      }
    }
    
    public void dropActionChanged(DropTargetDragEvent dtde)
    {
      checkDrag(dtde);
    }
    
    public void drop(DropTargetDropEvent dtde) {
      boolean visualDrop = successFullVisualDrop;
      Matrix44 dropPoint = null;
      if ((visualDrop) && (ground != null))
      {

        dropPoint = boundingBoxFeedbackTransformable.getTransformation(world);
        boundingBoxFeedbackTransformable.setLocalTransformationRightNow(MathUtilities.createIdentityMatrix4d());
      }
      AWTUtilities.setIsCursorShowing(true);
      
      stopDraggingFromGallery();
      pickingPlane = new edu.cmu.cs.stage3.math.Plane(new Vector3d(0.0D, 0.0D, 0.0D), new Vector3d(0.0D, 1.0D, 0.0D));
      try {
        if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, java.awt.datatransfer.DataFlavor.javaFileListFlavor)) {
          dtde.acceptDrop(1);
          java.util.Map imageImporterExtensionMap = imageImporter.getExtensionMap();
          Transferable transferable = dtde.getTransferable();
          java.util.List fileList = (java.util.List)transferable.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
          if (!fileList.isEmpty()) {
            for (java.util.Iterator iter = fileList.iterator(); iter.hasNext();) {
              File file = (File)iter.next();
              if ((file.exists()) && (file.canRead()) && (!file.isDirectory()))
              {
                String fileName = file.getName();
                String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase();
                if (imageImporterExtensionMap.get(extension.toUpperCase()) != null) {
                  String elementName = extension.length() > 0 ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
                  Element element = imageImporter.load(file);
                  if ((element instanceof edu.cmu.cs.stage3.alice.core.TextureMap)) {
                    elementName = AuthoringToolResources.getNameForNewChild(elementName, world);
                    name.set(elementName);
                    edu.cmu.cs.stage3.alice.core.Billboard billboard = AuthoringToolResources.makeBillboard((edu.cmu.cs.stage3.alice.core.TextureMap)element, true);
                    world.addChild(billboard);
                    AuthoringToolResources.addElementToAppropriateProperty(billboard, world);
                    vehicle.set(world);
                    CameraViewPanel.renderTarget.markDirty();
                  } else {
                    AuthoringTool.showErrorDialog(file.getAbsolutePath() + " " + Messages.getString("did_not_produce_a_TextureMap_when_loaded_"), null, false);
                  }
                }
                else if (extension.equalsIgnoreCase("a2c")) {
                  if ((visualDrop) && (dropPoint != null)) {
                    authoringTool.loadAndAddCharacter(file, dropPoint);
                  } else {
                    authoringTool.loadAndAddCharacter(file);


                  }
                  


                }
                else if (authoringTool.isImportable(extension)) {
                  authoringTool.importElement(file, world, null, false);
                }
                else {
                  edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("Alice_does_not_know_how_to_open_this_file___n") + file.getAbsolutePath());
                }
              }
              else {
                edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("This_file_does_not_exist__cannot_be_read__or_is_a_directory__n") + file.getAbsolutePath());
              }
            }
          }
          else {
            dtde.getDropTargetContext().dropComplete(false);
            return;
          }
          dtde.dropComplete(true);
        } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.transformableReferenceFlavor)) {
          dtde.acceptDrop(2);
          Transferable transferable = dtde.getTransferable();
          edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.transformableReferenceFlavor);
          java.util.Vector popupStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeDefaultOneShotStructure(transformable);
          edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu(popupStructure, dtde.getDropTargetContext().getComponent(), getLocationx, getLocationy);
          dtde.dropComplete(true);
        } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor)) {
          dtde.acceptDrop(2);
          Transferable transferable = dtde.getTransferable();
          ResponsePrototype responsePrototype = (ResponsePrototype)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor);
          if (responsePrototype.getDesiredProperties().length > 0) {
            java.util.Vector popupStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePrototypeStructure(responsePrototype, edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.oneShotFactory, authoringTool.getWorld());
            edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu(popupStructure, dtde.getDropTargetContext().getComponent(), getLocationx, getLocationy);
          } else {
            ((Runnable)edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.oneShotFactory.createItem(responsePrototype)).run();
          }
          dtde.dropComplete(true);
        } else if (AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor)) {
          dtde.acceptDrop(2);
          Transferable transferable = dtde.getTransferable();
          java.net.URL url = (java.net.URL)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor);
          String extension = url.getPath().substring(url.getPath().lastIndexOf('.') + 1).toUpperCase();
          if (url.getPath().toLowerCase().endsWith(".a2c")) {
            if ((visualDrop) && (dropPoint != null)) {
              authoringTool.loadAndAddCharacter(url, dropPoint);
            } else {
              authoringTool.loadAndAddCharacter(url);
            }
          } else if (authoringTool.isImportable(extension)) {
            authoringTool.importElement(url, authoringTool.getWorld());
          }
          











          dtde.dropComplete(true);
















        }
        else
        {















          dtde.rejectDrop();
          dtde.dropComplete(false);
        }
      } catch (java.awt.datatransfer.UnsupportedFlavorException e) {
        AuthoringTool.showErrorDialog(Messages.getString("Drop_didn_t_work__bad_flavor"), e);
      } catch (java.io.IOException e) {
        AuthoringTool.showErrorDialog(Messages.getString("Drop_didn_t_work__IOException"), e);
      }
    }
  };
  
  java.awt.dnd.DropTarget renderTargetDropTarget;
  java.awt.dnd.DropTargetListener doNothingDropTargetListener = new java.awt.dnd.DropTargetListener() { public void dragEnter(DropTargetDragEvent dtde) {}
    
    public void dragExit(java.awt.dnd.DropTargetEvent dte) {}
    
    public void dragOver(DropTargetDragEvent dtde) {}
    
    public void dropActionChanged(DropTargetDragEvent dtde) {}
    
    public void drop(DropTargetDropEvent dtde) {}
  };
  protected java.awt.event.MouseListener renderTargetMouseListener = new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter() {
    public void singleClickResponse(MouseEvent ev) {
      edu.cmu.cs.stage3.alice.core.Transformable picked = pickedTransformable;
      Element selectedElement = authoringTool.getSelectedElement();
      if (selectedElement != picked)
      {
        if (affectSubpartsCheckBox.isSelected()) {
          authoringTool.setSelectedElement(picked);
        } else if ((selectedElement != null) && (selectedElement.isDescendantOf(picked))) {
          authoringTool.setSelectedElement(picked);
        } else {
          Element parent = null;
          while ((picked != null) && (!picked.doEventsStopAscending()) && (picked.getParent() != selectedElement)) {
            parent = picked.getParent();
            if ((parent instanceof edu.cmu.cs.stage3.alice.core.Transformable)) {
              picked = (edu.cmu.cs.stage3.alice.core.Transformable)parent;
            } else {
              picked = null;
            }
          }
          if (picked != null) {
            authoringTool.setSelectedElement(picked);
          } else {
            authoringTool.setSelectedElement(parent);
          }
        }
      }
    }
  };
  

  edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener dummyGroupListener = new edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener() {
    public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
    
    public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) { updateMoveCameraCombo(); }
  };
  

  edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener potentialDummyGroupListener = new edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener() {
    public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {}
    
    public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) { World world = authoringTool.getWorld();
      if (AuthoringToolResources.hasDummyObjectGroup(world)) {
        Group dummyGroup = AuthoringToolResources.getDummyObjectGroup(world);
        values.addObjectArrayPropertyListener(dummyGroupListener);
        groups.removeObjectArrayPropertyListener(this);
      }
    }
  };
  
  protected AuthoringTool authoringTool;
  protected AuthoringToolListener authoringToolListener = new AuthoringToolListener();
  



  public CameraViewPanel(SceneEditor sceneEditor)
  {
    this.sceneEditor = sceneEditor;
    jbInit();
    guiInit();
    
    if (boundingBoxFeedbackTransformable == null) {
      boundingBoxFeedbackTransformable = new edu.cmu.cs.stage3.alice.core.Transformable();
    }
    boundingBoxFeedbackTransformable.setLocalTransformationRightNow(MathUtilities.createIdentityMatrix4d());
    boundingBoxFeedbackTransformable.setParent(pickFeedbackTransformable);
    boundingBoxFeedbackTransformable.vehicle.set(pickFeedbackTransformable);
    dropFeedbackDecorator.setReferenceFrame(boundingBoxFeedbackTransformable);
    dropFeedbackPivotDecorator.setTransformable(boundingBoxFeedbackTransformable);
  }
  
  private void guiInit()
  {
    if (edu.cmu.cs.stage3.alice.authoringtool.AikMin.isWindows()) {
      controlScrollPane.setPreferredSize(new Dimension(355 + (fontSize - 12) * 14, 0));
    } else {
      controlScrollPane.setPreferredSize(new Dimension(405 + (fontSize - 12) * 14, 0));
    }
    
    guiNavigator = new GuiNavigator();
    guiNavigator.setCursor(java.awt.Cursor.getPredefinedCursor(12));
    navPanel.add(guiNavigator, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
    navPanel.setOpaque(true);
    
    try
    {
      galleryViewer = new edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer();
      galleryPanel.removeAll();
      galleryPanel.add(galleryViewer, "Center");
    } catch (Throwable t) {
      AuthoringTool.showErrorDialog(Messages.getString("Unable_to_create_the_gallery_viewer"), t);
    }
    

    String[] defaultAspectRatios = AuthoringToolResources.getDefaultAspectRatios();
    for (int i = 0; i < defaultAspectRatios.length; i++) {
      aspectRatioComboBox.addItem(defaultAspectRatios[i]);
    }
    lensAngleSlider.setMinimum(1);
    lensAngleSlider.setMaximum(999);
    
    aspectRatioComboBox.addActionListener(new AspectRatioComboBoxListener());
    lensAngleSlider.addChangeListener(new LensAngleSliderListener());
    

    singleQuadGroup.add(singleViewButton);
    singleQuadGroup.add(quadViewButton);
    singleViewButton.setSelected(true);
    
    quadPanel.setBackground(Color.black);
    quadPanel.setLayout(new java.awt.GridLayout(2, 2, 1, 1));
    

    moveCameraCombo.setRenderer(new edu.cmu.cs.stage3.alice.authoringtool.util.ElementListCellRenderer(0));
    

    cameraDummyButton.setIcon(AuthoringToolResources.getIconForString("dummyAtCamera"));
    objectDummyButton.setIcon(AuthoringToolResources.getIconForString("dummyAtObject"));
    

    singleViewButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Switch_to_a_Single_View_of_the_World_") + "</font></html>");
    quadViewButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Switch_to_a_Quad_View_of_the_World__p__p_The_Quad_View_shows_the_world_from_p_the_Right__Top__Front__and_regular_view_") + "</font></html>");
    affectSubpartsCheckBox.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Affect_Subparts__p__p_When_selected__the_mouse_will_p_manipulate_the_part_of_an_Object_p_clicked_on_instead_of_the_whole_Object_") + "</font></html>");
    aspectRatioComboBox.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Change_the_Aspect_Ratio_of_the_World__p__p_The_Aspect_Ratio_is_a_measure_of_the_width_p_of_the_window_that_we_look_at_the_world_through_p_versus_its_height_") + "</font></html>");
    aspectRatioLabel.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("The_Aspect_Ratio_is_a_measure_of_the_width_p_of_the_window_that_we_look_at_the_world_through_p_versus_its_height_") + "</font></html>");
    lensAngleSlider.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Change_the_Lens_Angle_of_the_Camera__p__p_A_wider_lens_angle_will_let_p_you_see_more_of_the_world_") + "</font></html>");
    lensAngleLabel.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("A_wider_lens_angle_will_let_p_you_see_more_of_the_world_") + "</font></html>");
    cameraDummyButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Create_a_New_Dummy_Object_p_at_the_Current_Position_of_the_Camera_") + "</font></html>");
    objectDummyButton.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Create_a_New_Dummy_Object_p_at_the_Current_Position_of_the_Selected_Object_") + "</font></html>");
    moveCameraCombo.setToolTipText("<html><font face=arial size=-1>" + Messages.getString("Move_the_Camera_to_a_Dummy_p_Object_s_Point_of_View_") + "</font></html>");
    moveCameraLabel.setToolTipText(moveCameraCombo.getToolTipText());
    
    setVisibleControls(1);
  }
  











































  public void renderInit(AuthoringTool authoringTool)
  {
    this.authoringTool = authoringTool;
    renderTarget = authoringTool.getRenderTargetFactory().createOnscreenRenderTarget();
    if (renderTarget != null) {
      renderTarget.setName(Messages.getString("perspective"));
      rtmm = new RenderTargetMultiManipulator(renderTarget);
      rtmm.addRenderTargetPickManipulatorListener(this);
      rtmm.setMode(defaultMoveMode);
      


      renderPanel.add(renderTarget.getAWTComponent(), "Center");
      renderTarget.addRenderTargetListener(this);
      renderTargetDropTarget = new java.awt.dnd.DropTarget(renderTarget.getAWTComponent(), renderTargetDropTargetListener);
      renderTarget.getAWTComponent().setDropTarget(renderTargetDropTarget);
      renderTarget.getAWTComponent().addMouseListener(renderTargetMouseListener);
      


      renderTargetFromTheRight = authoringTool.getRenderTargetFactory().createOnscreenRenderTarget();
      renderTargetFromTheTop = authoringTool.getRenderTargetFactory().createOnscreenRenderTarget();
      renderTargetFromTheFront = authoringTool.getRenderTargetFactory().createOnscreenRenderTarget();
      
      renderTargetFromTheRight.setName(Messages.getString("right"));
      renderTargetFromTheTop.setName(Messages.getString("top"));
      renderTargetFromTheFront.setName(Messages.getString("front"));
      
      renderTargetFromTheRight.addRenderTargetListener(this);
      renderTargetFromTheTop.addRenderTargetListener(this);
      renderTargetFromTheFront.addRenderTargetListener(this);
      
      renderTargetFromTheRight.addCamera(cameraFromTheRight.getSceneGraphCamera());
      renderTargetFromTheTop.addCamera(cameraFromTheTop.getSceneGraphCamera());
      renderTargetFromTheFront.addCamera(cameraFromTheFront.getSceneGraphCamera());
      
      rtmmFromTheRight = new RenderTargetMultiManipulator(renderTargetFromTheRight);
      rtmmFromTheTop = new RenderTargetMultiManipulator(renderTargetFromTheTop);
      rtmmFromTheFront = new RenderTargetMultiManipulator(renderTargetFromTheFront);
      
      rtmmFromTheRight.setMode(defaultMoveMode);
      rtmmFromTheTop.setMode(defaultMoveMode);
      rtmmFromTheFront.setMode(defaultMoveMode);
      
      cameraFromTheRight.vehicle.set(camTransformableFromTheRight);
      cameraFromTheTop.vehicle.set(camTransformableFromTheTop);
      cameraFromTheFront.vehicle.set(camTransformableFromTheFront);
      
      cameraFromTheRight.minimumX.set(new Double(NaN.0D));
      cameraFromTheRight.minimumY.set(new Double(-3.0D));
      cameraFromTheRight.maximumX.set(new Double(NaN.0D));
      cameraFromTheRight.maximumY.set(new Double(3.0D));
      cameraFromTheTop.minimumX.set(new Double(NaN.0D));
      cameraFromTheTop.minimumY.set(new Double(-3.0D));
      cameraFromTheTop.maximumX.set(new Double(NaN.0D));
      cameraFromTheTop.maximumY.set(new Double(3.0D));
      cameraFromTheFront.minimumX.set(new Double(NaN.0D));
      cameraFromTheFront.minimumY.set(new Double(-3.0D));
      cameraFromTheFront.maximumX.set(new Double(NaN.0D));
      cameraFromTheFront.maximumY.set(new Double(3.0D));
      
      cameraFromTheRight.nearClippingPlaneDistance.set(new Double(0.1D));
      cameraFromTheTop.nearClippingPlaneDistance.set(new Double(0.1D));
      cameraFromTheFront.nearClippingPlaneDistance.set(new Double(0.1D));
      
      cameraFromTheRight.farClippingPlaneDistance.set(new Double(1000.0D));
      cameraFromTheTop.farClippingPlaneDistance.set(new Double(1000.0D));
      cameraFromTheFront.farClippingPlaneDistance.set(new Double(1000.0D));
      
      Matrix44 m = new Matrix44();
      m.rotateY(-1.5707963267948966D);
      m.setPosition(new Vector3d(500.0D, 0.75D, 0.0D));
      camTransformableFromTheRight.localTransformation.set(m);
      m = new Matrix44();
      m.rotateY(3.141592653589793D);
      m.rotateX(-1.5707963267948966D);
      m.setPosition(new Vector3d(0.0D, 500.0D, 0.0D));
      camTransformableFromTheTop.localTransformation.set(m);
      m = new Matrix44();
      m.rotateY(3.141592653589793D);
      m.setPosition(new Vector3d(0.0D, 0.75D, 500.0D));
      camTransformableFromTheFront.localTransformation.set(m);
      
      singleViewButton.setSelected(true);
      setViewMode(0);
    }
    else {
      AuthoringTool.showErrorDialog(Messages.getString("CameraViewPanel_unable_to_create_renderTarget"), null);
    }
  }
  



  public void setAuthoringTool(AuthoringTool authoringTool)
  {
    defaultMoveMode = new edu.cmu.cs.stage3.alice.authoringtool.util.DefaultMoveMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
    moveUpDownMode = new edu.cmu.cs.stage3.alice.authoringtool.util.RaiseLowerMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
    turnLeftRightMode = new edu.cmu.cs.stage3.alice.authoringtool.util.TurnLeftRightMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
    turnForwardBackwardMode = new edu.cmu.cs.stage3.alice.authoringtool.util.TurnForwardBackwardMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
    tumbleMode = new edu.cmu.cs.stage3.alice.authoringtool.util.TumbleMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
    resizeMode = new edu.cmu.cs.stage3.alice.authoringtool.util.ResizeMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
    copyMode = new edu.cmu.cs.stage3.alice.authoringtool.util.CopyMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
    orthoScrollMode = new edu.cmu.cs.stage3.alice.authoringtool.util.OrthographicScrollMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
    

    orthoZoomInMode = new edu.cmu.cs.stage3.alice.authoringtool.util.OrthographicZoomMode(authoringTool, authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
    

    defaultMoveModeButton = new ManipulatorModeButton(defaultMoveMode, AuthoringToolResources.getIconForValue("manipulatorModes/defaultMoveMode"), Messages.getString("Move_Objects_Freely"), Messages.getString("Move_Objects_Freely_with_the_Mouse__p__nbsp__p__b_shift__b___move_up_down_p__b_control__b___turn_left_right_p__b_shift_control__b___tumble"));
    moveUpDownModeButton = new ManipulatorModeButton(moveUpDownMode, AuthoringToolResources.getIconForValue("manipulatorModes/moveUpDownMode"), Messages.getString("Move_Objects_Up_and_Down"), Messages.getString("Move_Objects_Up_and_Down_with_the_Mouse_"));
    turnLeftRightModeButton = new ManipulatorModeButton(turnLeftRightMode, AuthoringToolResources.getIconForValue("manipulatorModes/turnLeftRightMode"), Messages.getString("Turn_Objects_Left_and_Right"), Messages.getString("Turn_Objects_Left_and_Right_with_the_Mouse_"));
    turnForwardBackwardModeButton = new ManipulatorModeButton(turnForwardBackwardMode, AuthoringToolResources.getIconForValue("manipulatorModes/turnForwardBackwardMode"), Messages.getString("Turn_Objects_Forwards_and_Backwards"), Messages.getString("Turn_Objects_Forwards_and_Backwards_with_the_Mouse_"));
    tumbleModeButton = new ManipulatorModeButton(tumbleMode, AuthoringToolResources.getIconForValue("manipulatorModes/tumbleMode"), Messages.getString("Tumble_Objects"), Messages.getString("Tumble_Objects_with_the_Mouse_"));
    resizeModeButton = new ManipulatorModeButton(resizeMode, AuthoringToolResources.getIconForValue("manipulatorModes/resizeMode"), Messages.getString("Resize_Objects"), Messages.getString("Resize_Objects_with_the_Mouse_p__nbsp__p__b_shift__b___resize_more_slowly"));
    copyModeButton = new ManipulatorModeButton(copyMode, AuthoringToolResources.getIconForValue("manipulatorModes/copyMode"), Messages.getString("Copy_Objects"), Messages.getString("Copy_Objects_with_the_Mouse_"));
    orthoScrollModeButton = new ManipulatorModeButton(orthoScrollMode, AuthoringToolResources.getIconForValue("manipulatorModes/orthoScrollMode"), Messages.getString("Scroll_View"), Messages.getString("Scroll_View_with_the_Mouse__p__nbsp__p__b_shift__b___scroll_more_slowly_p__b_control__b___scroll_more_quickly_p__b_shift_control__b___scroll_much_more_quickly"));
    orthoZoomInModeButton = new ManipulatorModeButton(orthoZoomInMode, AuthoringToolResources.getIconForValue("manipulatorModes/zoomInMode"), Messages.getString("Zoom_View_In_and_Out"), Messages.getString("Zoom_View_In_and_Out_with_the_Mouse__p__nbsp__p__b_shift__b___zoom_more_slowly"));
    




    Dimension bestSize = Toolkit.getDefaultToolkit().getBestCursorSize(22, 22);
    java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(width, height, 2);
    bi.getGraphics().drawImage(AuthoringToolResources.getImageForString("manipulatorModes/orthoScrollMode"), 0, 0, 22, 22, null);
    orthoScrollMode.setPreferredCursor(Toolkit.getDefaultToolkit().createCustomCursor(bi, new Point(11, 11), "orthoScrollMode"));
    
    bestSize = Toolkit.getDefaultToolkit().getBestCursorSize(22, 22);
    bi = new java.awt.image.BufferedImage(width, height, 2);
    bi.getGraphics().drawImage(AuthoringToolResources.getImageForString("manipulatorModes/zoomInMode"), 0, 0, 22, 22, null);
    orthoZoomInMode.setPreferredCursor(Toolkit.getDefaultToolkit().createCustomCursor(bi, new Point(7, 7), "zoomInMode"));
    





    singleViewButtonSave = (this.quadViewButtonSave = defaultMoveModeButton);
    
    authoringTool.addElementSelectionListener(
      new edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener() {
        public void elementSelected(Element element) {
          if ((element instanceof edu.cmu.cs.stage3.alice.core.Transformable)) {
            selectedTransformable = ((edu.cmu.cs.stage3.alice.core.Transformable)element);
          } else {
            selectedTransformable = null;
          }
          CameraViewPanel.renderTarget.markDirty();
        }
        
      });
    guiNavigator.setAuthoringTool(authoringTool);
    
    if (galleryViewer != null) {
      galleryViewer.setAuthoringTool(authoringTool);
    }
    
    authoringTool.addAuthoringToolStateListener(authoringToolListener);
  }
  
  public int getViewMode() {
    return viewMode;
  }
  
  public void setViewMode(int viewMode) {
    if (this.viewMode != viewMode) {
      for (Enumeration enum0 = mouseModeGroup.getElements(); enum0.hasMoreElements();) {
        ManipulatorModeButton b = (ManipulatorModeButton)enum0.nextElement();
        if (b.isSelected()) {
          if (viewMode == 0) {
            singleViewButtonSave = b;
          } else if (viewMode == 1) {
            quadViewButtonSave = b;
          }
        }
      }
      
      this.viewMode = viewMode;
      
      superRenderPanel.removeAll();
      quadPanel.removeAll();
      mouseModePanel.removeAll();
      for (Enumeration enum0 = mouseModeGroup.getElements(); enum0.hasMoreElements();) {
        ManipulatorModeButton b = (ManipulatorModeButton)enum0.nextElement();
        b.setSelected(false);
        mouseModeGroup.remove(b);
      }
      if (viewMode == 0) {
        mouseModePanel.add(defaultMoveModeButton, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        mouseModePanel.add(moveUpDownModeButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        mouseModePanel.add(turnLeftRightModeButton, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        mouseModePanel.add(turnForwardBackwardModeButton, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        mouseModePanel.add(tumbleModeButton, new GridBagConstraints(4, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        mouseModePanel.add(resizeModeButton, new GridBagConstraints(5, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        mouseModePanel.add(copyModeButton, new GridBagConstraints(6, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        

        mouseModeGroup.add(defaultMoveModeButton);
        mouseModeGroup.add(moveUpDownModeButton);
        mouseModeGroup.add(turnLeftRightModeButton);
        mouseModeGroup.add(turnForwardBackwardModeButton);
        mouseModeGroup.add(tumbleModeButton);
        mouseModeGroup.add(resizeModeButton);
        mouseModeGroup.add(copyModeButton);
        

        defaultMoveModeButton.setSelected(true);
        selectedButtonText = defaultMoveModeButton.titleText;
        guiNavigator.setImageSize(2);
        superRenderPanel.add(renderAndNavPanel, "Center");
      } else if (viewMode == 1) {
        mouseModePanel.add(defaultMoveModeButton, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        mouseModePanel.add(turnLeftRightModeButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        mouseModePanel.add(turnForwardBackwardModeButton, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        mouseModePanel.add(tumbleModeButton, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        mouseModePanel.add(resizeModeButton, new GridBagConstraints(4, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        mouseModePanel.add(copyModeButton, new GridBagConstraints(5, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        
        mouseModePanel.add(orthoScrollModeButton, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        mouseModePanel.add(orthoZoomInModeButton, new GridBagConstraints(1, 1, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 2, 2), 0, 0));
        


        mouseModeGroup.add(defaultMoveModeButton);
        mouseModeGroup.add(turnLeftRightModeButton);
        mouseModeGroup.add(turnForwardBackwardModeButton);
        mouseModeGroup.add(tumbleModeButton);
        mouseModeGroup.add(resizeModeButton);
        mouseModeGroup.add(copyModeButton);
        mouseModeGroup.add(orthoScrollModeButton);
        mouseModeGroup.add(orthoZoomInModeButton);
        


        defaultMoveModeButton.setSelected(true);
        selectedButtonText = defaultMoveModeButton.titleText;
        
        quadPanel.add(renderAndNavPanel);
        quadPanel.add(renderTargetFromTheTop.getAWTComponent());
        quadPanel.add(renderTargetFromTheRight.getAWTComponent());
        quadPanel.add(renderTargetFromTheFront.getAWTComponent());
        
        guiNavigator.setImageSize(1);
        superRenderPanel.add(quadPanel, "Center");
      }
      
      if (affectSubpartsCheckBox.isSelected()) {
        affectSubpartsCheckBox.doClick();
      }
      mouseInfoBorder.setTitle(selectedButtonText);
      mouseInfoBorder.setTitleColor(defaultBorderColor);
      mouseModePanel.repaint();
      

      setTargetsDirty();
      revalidate();
      repaint();
    }
  }
  
  public void setTargetsDirty() {
    renderTarget.markDirty();
    renderTargetFromTheTop.markDirty();
    renderTargetFromTheRight.markDirty();
    renderTargetFromTheFront.markDirty();
  }
  
  private boolean HACK_isInvokedFromSetWorld = false;
  
  public void setWorld(World world) { edu.cmu.cs.stage3.alice.scenegraph.Camera[] sgCameras = renderTarget.getCameras();
    for (int i = 0; i < sgCameras.length; i++) {
      renderTarget.removeCamera(sgCameras[i]);
    }
    
    moveCameraCombo.removeAllItems();
    moveCameraCombo.addItem(NONE_DUMMY);
    
    this.world = world;
    if (world != null) {
      ground = world.getChildNamedIgnoreCase("ground");
    }
    if (this.world != null) {
      edu.cmu.cs.stage3.alice.core.Camera[] cameras = (edu.cmu.cs.stage3.alice.core.Camera[])world.getDescendants(edu.cmu.cs.stage3.alice.core.Camera.class);
      if (cameras.length > 0) {
        renderCamera = cameras[0];
        renderTarget.addCamera(renderCamera.getSceneGraphCamera());
        originalCameraPOV = renderCamera.getTransformation(world);
        
        guiNavigator.setObjectToNavigate(renderCamera);
        

        if ((renderCamera instanceof SymmetricPerspectiveCamera)) {
          SymmetricPerspectiveCamera cam = (SymmetricPerspectiveCamera)renderCamera;
          Number hAngle = horizontalViewingAngle.getNumberValue();
          Number vAngle = verticalViewingAngle.getNumberValue();
          Boolean letterboxed = isLetterboxedAsOpposedToDistorted.getBooleanValue();
          if (letterboxed == null) {
            isLetterboxedAsOpposedToDistorted.set(Boolean.TRUE);
            letterboxed = isLetterboxedAsOpposedToDistorted.getBooleanValue();
          }
          
          if ((hAngle == null) && (vAngle != null)) {
            horizontalViewingAngle.set(new Double(1.3333333333333333D * vAngle.doubleValue()));
            hAngle = horizontalViewingAngle.getNumberValue();
          } else if ((hAngle != null) && (vAngle == null)) {
            verticalViewingAngle.set(new Double(0.75D * hAngle.doubleValue()));
            vAngle = verticalViewingAngle.getNumberValue();
          } else if ((hAngle == null) && (vAngle == null)) {
            verticalViewingAngle.set(new Double(0.5D));
            vAngle = verticalViewingAngle.getNumberValue();
            horizontalViewingAngle.set(new Double(1.3333333333333333D * vAngle.doubleValue()));
            hAngle = horizontalViewingAngle.getNumberValue();
          }
          
          double aspectRatio = hAngle.doubleValue() / vAngle.doubleValue();
          String aspectRatioString = (String)data.get("edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.aspectRatioString");
          

          if (aspectRatioString == null) {
            aspectRatioString = "4/3";
          }
          
          Double parsedAspectRatio = AuthoringToolResources.parseDouble(aspectRatioString);
          if (parsedAspectRatio == null) {
            parsedAspectRatio = new Double(0.0D);
          }
          if (aspectRatio != parsedAspectRatio.doubleValue())
          {

            aspectRatioString = Double.toString(aspectRatio);
            data.put("edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.aspectRatioString", aspectRatioString);
          }
          int lensAngle;
          int lensAngle;
          if (aspectRatio >= 1.0D) {
            lensAngle = (int)(hAngle.doubleValue() * 1000.0D / 3.141592653589793D);
          } else {
            lensAngle = (int)(vAngle.doubleValue() * 1000.0D / 3.141592653589793D);
          }
          
          HACK_isInvokedFromSetWorld = true;
          
          aspectRatioComboBox.setSelectedItem(aspectRatioString);
          try
          {
            lensAngleSlider.setValue(lensAngle);
          } finally {
            HACK_isInvokedFromSetWorld = false;
          }
        }
      }
      

      camTransformableFromTheRight.vehicle.set(world);
      camTransformableFromTheTop.vehicle.set(world);
      camTransformableFromTheFront.vehicle.set(world);
      

      pickFeedbackTransformable.vehicle.set(ground);
    } else {
      renderCamera = null;
    }
  }
  
  public Dimension getRenderSize() {
    Dimension size = renderPanel.getSize();
    
    return size;
  }
  
  public void activate() {
    if (viewMode == 0) {
      if (!superRenderPanel.isAncestorOf(renderAndNavPanel)) {
        superRenderPanel.add(renderAndNavPanel, "Center");
      }
    } else if ((viewMode == 1) && 
      (!superRenderPanel.isAncestorOf(quadPanel))) {
      superRenderPanel.add(quadPanel, "Center");
    }
  }
  
  public void deactivate()
  {
    superRenderPanel.removeAll();
  }
  
  public void setVisibleControls(int howMuch) {
    if (howMuch == 0) {
      controlPanel.add(aspectPanel, new GridBagConstraints(0, 3, 2, 1, 1.0D, 0.0D, 
        10, 2, new Insets(0, 0, 0, 0), 0, 0));
      controlPanel.add(jSeparator2, new GridBagConstraints(0, 4, 1, 1, 1.0D, 0.0D, 
        10, 2, new Insets(0, 0, 0, 0), 0, 0));
      controlPanel.add(markerPanel, new GridBagConstraints(0, 5, 2, 1, 1.0D, 0.0D, 
        10, 2, new Insets(0, 0, 0, 0), 0, 0));
      controlPanel.add(jSeparator3, new GridBagConstraints(0, 6, 1, 1, 1.0D, 0.0D, 
        10, 2, new Insets(0, 0, 0, 0), 0, 0));
      moreFewerControlsButton.setText(Messages.getString("fewer_controls____"));
    } else if (howMuch == 1) {
      controlPanel.remove(aspectPanel);
      controlPanel.remove(jSeparator2);
      controlPanel.remove(markerPanel);
      controlPanel.remove(jSeparator3);
      moreFewerControlsButton.setText(Messages.getString("more_controls____"));
    }
  }
  



  protected void dropDummy(edu.cmu.cs.stage3.alice.core.ReferenceFrame referenceFrame)
  {
    World world = authoringTool.getWorld();
    Group dummyGroup = AuthoringToolResources.getDummyObjectGroup(world);
    Dummy dummy = new Dummy();
    name.set(AuthoringToolResources.getNameForNewChild("Dummy", dummyGroup));
    dummyGroup.addChild(dummy);
    values.add(dummy);
    vehicle.set(world);
    dummy.setPointOfViewRightNow(referenceFrame);
  }
  
  protected void updateMoveCameraCombo() {
    moveCameraCombo.removeAllItems();
    moveCameraCombo.addItem(NONE_DUMMY);
    
    World world = authoringTool.getWorld();
    if (AuthoringToolResources.hasDummyObjectGroup(world)) {
      Group dummyGroup = AuthoringToolResources.getDummyObjectGroup(world);
      Object[] dummies = values.getArrayValue();
      for (int i = 0; i < dummies.length; i++) {
        moveCameraCombo.addItem(dummies[i]);
        if ((renderCamera != null) && ((dummies[i] instanceof Dummy))) {
          Matrix4d m = renderCamera.getTransformation((Dummy)dummies[i]);
          if (m.equals(MathUtilities.getIdentityMatrix4d())) {
            moveCameraCombo.setSelectedItem(dummies[i]);
          }
        }
      }
    }
  }
  























































  private boolean oldBoundingBoxValue = false;
  
  public void cleared(RenderTargetEvent ev) { setTargetsDirty();
    if (selectedTransformable != null) {
      oldBoundingBoxValue = selectedTransformable.isBoundingBoxShowing.booleanValue();
      
      if (!oldBoundingBoxValue) {
        boundingBoxDecorator.setReferenceFrame(selectedTransformable);
        boundingBoxDecorator.setIsShowing(true);
        pivotDecorator.setTransformable(selectedTransformable);
        pivotDecorator.setIsShowing(true);
      }
    }
    

    if (world != null) {
      Element[] dummies = world.search(new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(Dummy.class));
      for (int i = 0; i < dummies.length; i++) {
        Dummy dummy = (Dummy)dummies[i];
        dummy.getSceneGraphVisual().setIsShowing(true);
      }
    }
  }
  
  public void rendered(RenderTargetEvent ev) {
    setTargetsDirty();
    if (selectedTransformable != null) {
      selectedTransformable.isBoundingBoxShowing.set(oldBoundingBoxValue);
      if (!oldBoundingBoxValue) {
        boundingBoxDecorator.setIsShowing(false);
        boundingBoxDecorator.setReferenceFrame(null);
        pivotDecorator.setIsShowing(false);
        pivotDecorator.setTransformable(null);
      }
    }
    

    if (world != null) {
      Element[] dummies = world.search(new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(Dummy.class));
      for (int i = 0; i < dummies.length; i++) {
        Dummy dummy = (Dummy)dummies[i];
        dummy.getSceneGraphVisual().setIsShowing(false);
      }
    }
    
    edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget evRenderTarget = ev.getRenderTarget();
    String text = null;
    if (evRenderTarget == renderTargetFromTheRight) {
      text = fromRightText;
    } else if (evRenderTarget == renderTargetFromTheTop) {
      text = fromTopText;
    } else if (evRenderTarget == renderTargetFromTheFront) {
      text = fromFrontText;
    }
    if (text != null) {
      Graphics g = evRenderTarget.getOffscreenGraphics();
      g.setColor(viewLabelColor);
      g.setFont(viewLabelFont);
      g.drawString(text, 12, 18);
      g.dispose();
    }
  }
  
  public void swapped(RenderTargetEvent renderTargetEvent) {
    setTargetsDirty();
  }
  


  public void prePick(edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorEvent ev) {}
  

  public void postPick(edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorEvent ev)
  {
    edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo = ev.getPickInfo();
    if ((pickInfo != null) && (pickInfo.getCount() > 0) && (pickInfo.getVisualAt(0) != null) && (pickInfo.getVisualAt(0).getBonus() != null)) {
      Object bonus = pickInfo.getVisualAt(0).getBonus();
      if ((bonus instanceof edu.cmu.cs.stage3.alice.core.Transformable)) {
        pickedTransformable = ((edu.cmu.cs.stage3.alice.core.Transformable)bonus);
      } else {
        pickedTransformable = null;
      }
    } else {
      pickedTransformable = null;
    }
  }
  




















  class ManipulatorModeButton
    extends javax.swing.JToggleButton
    implements java.awt.event.MouseListener
  {
    protected RenderTargetManipulatorMode mode;
    



















    protected Border selectedBorder = BorderFactory.createBevelBorder(1);
    protected Border unselectedBorder = BorderFactory.createBevelBorder(0);
    protected String titleText;
    
    public ManipulatorModeButton(RenderTargetManipulatorMode mode, javax.swing.Icon icon, String titleText, String toolTip) {
      super();
      this.mode = mode;
      setToolTipText("<html><font face=arial size=-1>" + toolTip + "</font></html>");
      this.titleText = titleText;
      Dimension d;
      if (icon != null) {
        d = new Dimension(icon.getIconWidth() + 4, icon.getIconHeight() + 4);
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
      } else {
        AuthoringTool.showErrorDialog(Messages.getString("no_icon_found_for_mode__") + mode.getClass().getName(), null);
      }
      
      setBorder(unselectedBorder);
      setOpaque(false);
      addMouseListener(this);
      addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent ev) {
          if (isSelected()) {
            rtmm.setMode(mode);
            rtmmFromTheRight.setMode(mode);
            rtmmFromTheTop.setMode(mode);
            rtmmFromTheFront.setMode(mode);
            setBorder(selectedBorder);
            selectedButtonText = titleText;
          } else {
            setBorder(unselectedBorder);
          }
        }
      });
    }
    
    public void doClick() {
      super.doClick();
      selectedButtonText = titleText;
      mouseInfoBorder.setTitle(selectedButtonText);
      mouseInfoBorder.setTitleColor(defaultBorderColor);
      mouseModePanel.repaint();
    }
    
    public void mouseClicked(MouseEvent e) {}
    
    public void mouseEntered(MouseEvent e)
    {
      mouseInfoBorder.setTitle(titleText);
      mouseInfoBorder.setTitleColor(highlightBorderColor);
      mouseModePanel.repaint();
    }
    
    public void mouseExited(MouseEvent e) { mouseInfoBorder.setTitle(selectedButtonText);
      mouseInfoBorder.setTitleColor(defaultBorderColor);
      mouseModePanel.repaint();
    }
    
    public void mousePressed(MouseEvent e) {}
    
    public void mouseReleased(MouseEvent e) {}
  }
  
  class AspectRatioComboBoxListener implements ActionListener {
    AspectRatioComboBoxListener() {}
    
    public void actionPerformed(ActionEvent ev) { if ((renderCamera instanceof SymmetricPerspectiveCamera)) {
        SymmetricPerspectiveCamera cam = (SymmetricPerspectiveCamera)renderCamera;
        String aspectRatioString = (String)aspectRatioComboBox.getSelectedItem();
        Double parsedAspectRatio = AuthoringToolResources.parseDouble(aspectRatioString);
        if (parsedAspectRatio == null) {
          aspectRatioComboBox.setSelectedItem((String)data.get("edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.aspectRatioString"));
        }
        else if (!HACK_isInvokedFromSetWorld)
        {

          authoringTool.getUndoRedoStack().startCompound();
          try {
            data.put("edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.aspectRatioString", aspectRatioString);
            double newAspectRatio = parsedAspectRatio.doubleValue();
            double lensAngle = lensAngleSlider.getValue() * 3.141592653589793D / 1000.0D;
            double hAngle;
            double hAngle; double vAngle; if (newAspectRatio < 1.0D) {
              double vAngle = lensAngle;
              hAngle = vAngle * newAspectRatio;
            } else {
              hAngle = lensAngle;
              vAngle = hAngle / newAspectRatio;
            }
            horizontalViewingAngle.set(new Double(hAngle));
            verticalViewingAngle.set(new Double(vAngle));
          } finally {
            authoringTool.getUndoRedoStack().stopCompound();
          }
        }
      }
    }
  }
  
  class LensAngleSliderListener implements javax.swing.event.ChangeListener { private Object previousHorizontalAngle;
    private Object previousVerticalAngle;
    
    LensAngleSliderListener() {}
    
    public boolean duringChange = false;
    
    public void stateChanged(javax.swing.event.ChangeEvent ev) { if ((renderCamera instanceof SymmetricPerspectiveCamera)) {
        SymmetricPerspectiveCamera cam = (SymmetricPerspectiveCamera)renderCamera;
        
        if (!duringChange) {
          duringChange = true;
          previousHorizontalAngle = horizontalViewingAngle.get();
          previousVerticalAngle = verticalViewingAngle.get();
        }
        authoringTool.getUndoRedoStack().setIsListening(false);
        
        double aspectRatio = horizontalViewingAngle.doubleValue() / verticalViewingAngle.doubleValue();
        
        double lensAngle = lensAngleSlider.getValue() * 3.141592653589793D / 1000.0D;
        double hAngle;
        double hAngle; double vAngle; if (aspectRatio < 1.0D) {
          double vAngle = lensAngle;
          hAngle = vAngle * aspectRatio;
        } else {
          hAngle = lensAngle;
          vAngle = hAngle / aspectRatio;
        }
        horizontalViewingAngle.set(new Double(hAngle));
        verticalViewingAngle.set(new Double(vAngle));
        
        authoringTool.getUndoRedoStack().setIsListening(true);
        if (!lensAngleSlider.getValueIsAdjusting()) {
          duringChange = false;
          if (!HACK_isInvokedFromSetWorld)
          {

            authoringTool.getUndoRedoStack().startCompound();
            authoringTool.getUndoRedoStack().push(new edu.cmu.cs.stage3.alice.authoringtool.util.PropertyUndoableRedoable(horizontalViewingAngle, previousHorizontalAngle, horizontalViewingAngle.get()));
            authoringTool.getUndoRedoStack().push(new edu.cmu.cs.stage3.alice.authoringtool.util.PropertyUndoableRedoable(verticalViewingAngle, previousVerticalAngle, verticalViewingAngle.get()));
            authoringTool.getUndoRedoStack().stopCompound();
          }
        }
      }
    }
  }
  
  class AuthoringToolListener implements edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener { AuthoringToolListener() {}
    
    public void worldUnLoading(AuthoringToolStateChangedEvent ev) { World world = authoringTool.getWorld();
      if (AuthoringToolResources.hasDummyObjectGroup(world)) {
        Group dummyGroup = AuthoringToolResources.getDummyObjectGroup(world);
        values.removeObjectArrayPropertyListener(dummyGroupListener);
      }
    }
    
    public void worldLoaded(AuthoringToolStateChangedEvent ev) {
      World world = authoringTool.getWorld();
      if (world != null) {
        if (AuthoringToolResources.hasDummyObjectGroup(world)) {
          Group dummyGroup = AuthoringToolResources.getDummyObjectGroup(world);
          values.addObjectArrayPropertyListener(dummyGroupListener);
        } else {
          groups.addObjectArrayPropertyListener(potentialDummyGroupListener);
        }
      }
    }
    
    public void stateChanged(AuthoringToolStateChangedEvent ev) { setTargetsDirty(); }
    public void stateChanging(AuthoringToolStateChangedEvent ev) { setTargetsDirty(); }
    public void worldLoading(AuthoringToolStateChangedEvent ev) { setTargetsDirty(); }
    public void worldStarting(AuthoringToolStateChangedEvent ev) { setTargetsDirty(); }
    public void worldStopping(AuthoringToolStateChangedEvent ev) { setTargetsDirty(); }
    public void worldPausing(AuthoringToolStateChangedEvent ev) { setTargetsDirty(); }
    public void worldUnLoaded(AuthoringToolStateChangedEvent ev) { setTargetsDirty(); }
    public void worldSaving(AuthoringToolStateChangedEvent ev) { setTargetsDirty(); }
    public void worldStarted(AuthoringToolStateChangedEvent ev) { setTargetsDirty(); }
    public void worldStopped(AuthoringToolStateChangedEvent ev) { setTargetsDirty(); }
    public void worldPaused(AuthoringToolStateChangedEvent ev) { setTargetsDirty(); }
    public void worldSaved(AuthoringToolStateChangedEvent ev) { setTargetsDirty(); }
  }
  



  void moreFewerControlsButton_actionPerformed(ActionEvent e)
  {
    if (aspectPanel.getParent() != null) {
      setVisibleControls(1);
    } else {
      setVisibleControls(0);
    }
  }
  

















  TitledBorder titledBorder1;
  















  void singleViewButton_actionPerformed(ActionEvent e)
  {
    setViewMode(0);
  }
  
  void quadViewButton_actionPerformed(ActionEvent e) {
    setViewMode(1);
  }
  
  void affectSubpartsCheckBox_actionPerformed(ActionEvent e) {
    boolean ascendTreeEnabled = !affectSubpartsCheckBox.isSelected();
    rtmm.setAscendTreeEnabled(ascendTreeEnabled);
    
    if (ascendTreeEnabled) {
      defaultMoveModeButton.doClick();
    } else {
      tumbleModeButton.doClick();
    }
  }
  
  void cameraDummyButton_actionPerformed(ActionEvent e) {
    if (renderCamera != null) {
      dropDummy(renderCamera);
    }
  }
  
  void objectDummyButton_actionPerformed(ActionEvent e) {
    Element selectedElement = authoringTool.getSelectedElement();
    if ((selectedElement != null) && ((selectedElement instanceof edu.cmu.cs.stage3.alice.core.ReferenceFrame))) {
      dropDummy((edu.cmu.cs.stage3.alice.core.ReferenceFrame)selectedElement);
    }
  }
  
  void moveCameraCombo_actionPerformed(ActionEvent e) {
    Object selected = moveCameraCombo.getSelectedItem();
    if ((selected instanceof Dummy)) {
      Dummy dummy = (Dummy)selected;
      PointOfViewAnimation anim = new PointOfViewAnimation();
      PointOfViewAnimation undoAnim = new PointOfViewAnimation();
      pointOfView.set(MathUtilities.createIdentityMatrix4d());
      subject.set(renderCamera);
      asSeenBy.set(dummy);
      pointOfView.set(renderCamera.getLocalTransformation());
      subject.set(renderCamera);
      authoringTool.performOneShot(anim, undoAnim, new edu.cmu.cs.stage3.alice.core.Property[] { renderCamera.localTransformation });
    }
  }
  





  java.awt.FlowLayout flowLayout4 = new java.awt.FlowLayout();
  BorderLayout borderLayout1 = new BorderLayout();
  ButtonGroup btnGrpTheMouse = new ButtonGroup();
  Border border1;
  TitledBorder titledBorder2;
  Border border2;
  TitledBorder titledBorder3;
  Border border3;
  Border border4;
  Border border5;
  JPanel galleryPanel = new JPanel();
  JLabel placeHolderLabel = new JLabel();
  Component component2;
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel superRenderPanel = new JPanel();
  JPanel navPanel = new JPanel();
  JPanel renderPanel = new JPanel();
  BorderLayout borderLayout5 = new BorderLayout();
  JPanel renderAndNavPanel = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  BorderLayout borderLayout4 = new BorderLayout();
  Border border6;
  TitledBorder mouseInfoBorder = new TitledBorder(Messages.getString("Mouse_Controls"));
  javax.swing.JScrollPane controlScrollPane = new javax.swing.JScrollPane();
  java.awt.GridBagLayout gridBagLayout = new java.awt.GridBagLayout();
  JSlider lensAngleSlider = new JSlider();
  JPanel aspectPanel = new JPanel();
  JLabel moveCameraLabel = new JLabel();
  JSeparator jSeparator3 = new JSeparator();
  JSeparator jSeparator2 = new JSeparator();
  JSeparator jSeparator1 = new JSeparator();
  JRadioButton quadViewButton = new JRadioButton();
  JPanel mouseModePanel = new JPanel();
  JComboBox aspectRatioComboBox = new JComboBox();
  JPanel mousePanel = new JPanel();
  JPanel markerPanel = new JPanel();
  JButton objectDummyButton = new JButton();
  JCheckBox affectSubpartsCheckBox = new JCheckBox();
  JRadioButton singleViewButton = new JRadioButton();
  JLabel aspectRatioLabel = new JLabel();
  JComboBox moveCameraCombo = new JComboBox();
  JPanel controlPanel = new JPanel();
  JButton cameraDummyButton = new JButton();
  Component component1;
  JLabel lensAngleLabel = new JLabel();
  
  private JButton moreFewerControlsButton = new JButton();
  
  private void jbInit() {
    mouseInfoBorder.setTitleFont(new java.awt.Font("Dialog", 0, 11));
    border1 = BorderFactory.createEtchedBorder(Color.white, new Color(165, 164, 164));
    titledBorder2 = new TitledBorder(BorderFactory.createEmptyBorder(), "");
    border2 = BorderFactory.createEtchedBorder(Color.white, new Color(165, 164, 164));
    titledBorder3 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(165, 164, 164)), Messages.getString("Point_Of_View"));
    border3 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
    border4 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
    border5 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
    component2 = javax.swing.Box.createVerticalStrut(150);
    border6 = BorderFactory.createEmptyBorder(0, 0, 1, 1);
    component1 = javax.swing.Box.createGlue();
    flowLayout4.setHgap(0);
    flowLayout4.setVgap(0);
    placeHolderLabel.setFont(new java.awt.Font("Dialog", 0, (int)(24 * fontSize / 12.0D)));
    placeHolderLabel.setForeground(Color.gray);
    placeHolderLabel.setText(Messages.getString("The_Gallery_will_go_here_"));
    galleryPanel.setLayout(borderLayout2);
    navPanel.setLayout(gridBagLayout);
    navPanel.setBackground(Color.white);
    renderPanel.setLayout(borderLayout5);
    renderPanel.setBackground(Color.black);
    renderAndNavPanel.setLayout(borderLayout3);
    renderAndNavPanel.setBackground(Color.black);
    superRenderPanel.setLayout(borderLayout4);
    superRenderPanel.setBackground(Color.black);
    lensAngleSlider.setOpaque(false);
    lensAngleSlider.setPreferredSize(new Dimension(150, 16));
    lensAngleSlider.setMinimumSize(new Dimension(10, 16));
    aspectPanel.setLayout(gridBagLayout);
    aspectPanel.setBorder(border4);
    aspectPanel.setOpaque(false);
    moveCameraLabel.setText(Messages.getString("move_camera_to_dummy_"));
    jSeparator3.setForeground(Color.gray);
    jSeparator2.setForeground(Color.gray);
    jSeparator1.setForeground(Color.gray);
    quadViewButton.setOpaque(false);
    quadViewButton.setText(Messages.getString("quad_view"));
    quadViewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        quadViewButton_actionPerformed(e);
      }
    });
    mouseModePanel.setLayout(gridBagLayout);
    mouseModePanel.setOpaque(false);
    mouseModePanel.setBorder(mouseInfoBorder);
    aspectRatioComboBox.setMinimumSize(new Dimension(80, (int)(20 * fontSize / 12.0D)));
    aspectRatioComboBox.setPreferredSize(new Dimension(80, (int)(20 * fontSize / 12.0D)));
    aspectRatioComboBox.setEditable(true);
    mousePanel.setLayout(gridBagLayout);
    mousePanel.setBackground(new Color(236, 235, 235));
    mousePanel.setBorder(border3);
    markerPanel.setLayout(gridBagLayout);
    markerPanel.setBorder(border5);
    markerPanel.setOpaque(false);
    objectDummyButton.setHorizontalAlignment(2);
    objectDummyButton.setMargin(new Insets(2, 8, 2, 8));
    objectDummyButton.setText(Messages.getString("drop_dummy_at_selected_object"));
    objectDummyButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        objectDummyButton_actionPerformed(e);
      }
    });
    affectSubpartsCheckBox.setOpaque(false);
    affectSubpartsCheckBox.setText(Messages.getString("affect_subparts"));
    affectSubpartsCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        affectSubpartsCheckBox_actionPerformed(e);
      }
    });
    singleViewButton.setOpaque(false);
    singleViewButton.setText(Messages.getString("single_view"));
    singleViewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        singleViewButton_actionPerformed(e);
      }
    });
    aspectRatioLabel.setText(Messages.getString("aspect_ratio_"));
    moveCameraCombo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moveCameraCombo_actionPerformed(e);
      }
    });
    controlPanel.setLayout(gridBagLayout);
    controlPanel.setBackground(new Color(236, 235, 235));
    cameraDummyButton.setHorizontalAlignment(2);
    cameraDummyButton.setMargin(new Insets(2, 8, 2, 8));
    cameraDummyButton.setText(Messages.getString("drop_dummy_at_camera"));
    cameraDummyButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cameraDummyButton_actionPerformed(e);
      }
    });
    lensAngleLabel.setText(Messages.getString("lens_angle_"));
    controlScrollPane.setHorizontalScrollBarPolicy(31);
    moreFewerControlsButton.setText(Messages.getString("more_controls____"));
    moreFewerControlsButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moreFewerControlsButton_actionPerformed(e);
      }
      
    });
    galleryPanel.add(placeHolderLabel, "North");
    galleryPanel.add(component2, "Center");
    titledBorder1 = new TitledBorder("");
    renderAndNavPanel.add(renderPanel, "Center");
    renderAndNavPanel.add(navPanel, "South");
    controlScrollPane.getViewport().add(controlPanel, null);
    markerPanel.add(cameraDummyButton, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 
      17, 2, new Insets(0, 0, 4, 0), 0, 0));
    markerPanel.add(objectDummyButton, new GridBagConstraints(0, 1, 1, 1, 1.0D, 0.0D, 
      17, 2, new Insets(0, 0, 4, 0), 0, 0));
    markerPanel.add(moveCameraLabel, new GridBagConstraints(0, 2, 1, 1, 1.0D, 0.0D, 
      17, 2, new Insets(4, 0, 2, 0), 0, 0));
    markerPanel.add(moveCameraCombo, new GridBagConstraints(0, 3, 1, 1, 1.0D, 0.0D, 
      17, 2, new Insets(0, 0, 0, 0), 0, 0));
    controlPanel.add(mousePanel, new GridBagConstraints(0, 0, 2, 1, 1.0D, 0.0D, 
      11, 2, new Insets(0, 0, 0, 0), 0, 0));
    controlPanel.add(markerPanel, new GridBagConstraints(0, 5, 2, 1, 1.0D, 0.0D, 
      10, 2, new Insets(0, 0, 0, 0), 0, 0));
    mousePanel.add(affectSubpartsCheckBox, new GridBagConstraints(0, 8, 2, 1, 1.0D, 0.0D, 
      17, 2, new Insets(0, 16, 0, 0), 0, 0));
    mousePanel.add(singleViewButton, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 
      18, 0, new Insets(0, 0, 0, 0), 0, 0));
    mousePanel.add(quadViewButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 
      18, 0, new Insets(0, 0, 0, 0), 0, 0));
    mousePanel.add(mouseModePanel, new GridBagConstraints(0, 1, 2, 1, 1.0D, 0.0D, 
      18, 1, new Insets(4, 16, 0, 16), 0, 0));
    controlPanel.add(aspectPanel, new GridBagConstraints(0, 3, 2, 1, 1.0D, 0.0D, 
      10, 2, new Insets(0, 0, 0, 0), 0, 0));
    aspectPanel.add(aspectRatioLabel, new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 
      14, 0, new Insets(0, 4, 4, 4), 0, 0));
    aspectPanel.add(aspectRatioComboBox, new GridBagConstraints(1, 2, 1, 1, 0.0D, 0.0D, 
      16, 2, new Insets(0, 6, 4, 10), 0, 0));
    aspectPanel.add(lensAngleLabel, new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 
      14, 0, new Insets(0, 4, 4, 4), 0, 0));
    aspectPanel.add(lensAngleSlider, new GridBagConstraints(1, 3, 1, 1, 0.0D, 0.0D, 
      16, 2, new Insets(0, 0, 4, 4), 0, 0));
    controlPanel.add(component1, new GridBagConstraints(0, 8, 2, 1, 1.0D, 1.0D, 
      10, 1, new Insets(0, 0, 0, 0), 0, 0));
    controlPanel.add(jSeparator1, new GridBagConstraints(0, 1, 1, 1, 1.0D, 0.0D, 
      10, 2, new Insets(0, 0, 0, 0), 0, 0));
    controlPanel.add(jSeparator2, new GridBagConstraints(0, 4, 1, 1, 1.0D, 0.0D, 
      10, 2, new Insets(0, 0, 0, 0), 0, 0));
    controlPanel.add(jSeparator3, new GridBagConstraints(0, 6, 1, 1, 1.0D, 0.0D, 
      10, 2, new Insets(0, 0, 0, 0), 0, 0));
    controlPanel.add(moreFewerControlsButton, new GridBagConstraints(0, 7, 1, 1, 0.0D, 0.0D, 
      17, 0, new Insets(4, 4, 0, 0), 0, 0));
    superRenderPanel.add(renderAndNavPanel, "Center");
    
    setLayout(borderLayout1);
    add(controlScrollPane, "East");
    add(galleryPanel, "South");
    add(superRenderPanel, "Center");
  }
}
