package edu.cmu.cs.stage3.alice.gallery.modeleditor;

import edu.cmu.cs.stage3.alice.core.Model;

public class ModelEditor extends javax.swing.JFrame { public static void main(String[] args) { ModelEditor modelEditor = new ModelEditor();
    modelEditor.setLocation(0, 0);
    modelEditor.setSize(1280, 1000);
    edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory drtf = new edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory();
    modelEditor.init(drtf);
    if (args.length > 0) {
      modelEditor.onFileOpen(new java.io.File(args[0]));
    } else {
      modelEditor.onFileOpen();
    }
    modelEditor.setVisible(true);
    
    m_tree.requestFocus();
  }
  

  private edu.cmu.cs.stage3.alice.core.World m_world;
  private edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera m_camera;
  private edu.cmu.cs.stage3.alice.core.RenderTarget m_renderTarget;
  private edu.cmu.cs.stage3.alice.core.decorator.PivotDecorator m_pivotDecorator;
  private ElementTree m_tree;
  private ElementTreeModel m_treeModel;
  private int m_treeMouseEventModifiers = 0;
  
  private javax.swing.JButton m_prev;
  private javax.swing.JButton m_next;
  private javax.swing.JTextField m_modeledBy;
  private javax.swing.JTextField m_paintedBy;
  private javax.swing.JButton m_revert;
  private javax.swing.JCheckBox m_forceWireframe;
  private javax.swing.JFileChooser m_fileChooser;
  private java.io.File m_file;
  private boolean m_isDirty;
  
  public ModelEditor()
  {
    addWindowListener(new java.awt.event.WindowAdapter()
    {
      public void windowClosing(java.awt.event.WindowEvent e) {
        ModelEditor.this.onFileExit();
      }
      
    });
    javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
    
    javax.swing.JMenu fileMenu = new javax.swing.JMenu("File");
    fileMenu.setMnemonic(70);
    
    javax.swing.JMenuItem fileOpenMenuItem = new javax.swing.JMenuItem("Open...");
    fileOpenMenuItem.setMnemonic(79);
    fileOpenMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        ModelEditor.this.onFileOpen();
      }
      
    });
    javax.swing.JMenuItem fileSaveMenuItem = new javax.swing.JMenuItem("Save");
    fileSaveMenuItem.setMnemonic(83);
    fileSaveMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        ModelEditor.this.onFileSave();




      }
      




    });
    javax.swing.JMenuItem fileExitMenuItem = new javax.swing.JMenuItem("Exit...");
    fileExitMenuItem.setMnemonic(88);
    fileExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        ModelEditor.this.onFileExit();
      }
      
    });
    javax.swing.JMenu actionMenu = new javax.swing.JMenu("Action");
    actionMenu.setMnemonic(65);
    
    javax.swing.JMenuItem actionNextMenuItem = new javax.swing.JMenuItem("Next");
    actionNextMenuItem.setMnemonic(78);
    
    actionNextMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        ModelEditor.this.onNext();
      }
      
    });
    javax.swing.JMenuItem actionPrevMenuItem = new javax.swing.JMenuItem("Previous");
    actionPrevMenuItem.setMnemonic(80);
    
    actionPrevMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        ModelEditor.this.onPrev();
      }
      
    });
    setJMenuBar(menuBar);
    
    menuBar.add(fileMenu);
    fileMenu.add(fileOpenMenuItem);
    fileMenu.add(fileSaveMenuItem);
    
    fileMenu.add(fileExitMenuItem);
    
    menuBar.add(actionMenu);
    actionMenu.add(actionNextMenuItem);
    actionMenu.add(actionPrevMenuItem);
    
    m_treeModel = new ElementTreeModel();
    m_treeModel.setRoot(new Model() {});
    m_tree = new ElementTree(m_treeModel);
    ElementTreeCellRenderer cellRenderer = new ElementTreeCellRenderer();
    final ElementTreeCellEditor cellEditor = new ElementTreeCellEditor(m_tree, cellRenderer);
    m_tree.setCellRenderer(cellRenderer);
    m_tree.setCellEditor(cellEditor);
    m_tree.setEditable(true);
    m_tree.setScrollsOnExpand(true);
    
    javax.swing.ToolTipManager toolTipManager = javax.swing.ToolTipManager.sharedInstance();
    toolTipManager.registerComponent(m_tree);
    toolTipManager.setLightWeightPopupEnabled(false);
    
    cellEditor.addCellEditorListener(new javax.swing.event.CellEditorListener() {
      public void editingStopped(javax.swing.event.ChangeEvent e) {
        edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)m_tree.getLastSelectedPathComponent();
        String nameValue = (String)cellEditor.getCellEditorValue();
        if (!name.getStringValue().equals(nameValue))
        {

          name.set(nameValue);
          setIsDirty(true);
        }
      }
      

      public void editingCanceled(javax.swing.event.ChangeEvent e) {}
    });
    m_tree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
      public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
        javax.swing.tree.TreePath treePath = e.getPath();
        ModelEditor.this.onSelect((edu.cmu.cs.stage3.alice.core.Element)m_tree.getLastSelectedPathComponent());
      }
      
    });
    m_tree.addMouseListener(new java.awt.event.MouseAdapter() {
      private javax.swing.JPopupMenu m_popupMenu;
      private edu.cmu.cs.stage3.alice.core.Element m_element;
      
      private void handlePopup(java.awt.event.MouseEvent e) { if (m_popupMenu == null) {
          m_popupMenu = new javax.swing.JPopupMenu();
          javax.swing.JMenuItem menuItem = new javax.swing.JMenuItem("Delete");
          menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] propertyReferences = m_world.getPropertyReferencesTo(m_element);
              if (propertyReferences.length > 0) {
                StringBuffer sb = new StringBuffer();
                sb.append("Cannot delete " + m_element.getTrimmedKey() + ".  The following properties reference it:\n");
                for (int i = 0; i < propertyReferences.length; i++) {
                  edu.cmu.cs.stage3.alice.core.Property property = propertyReferences[i].getProperty();
                  sb.append("    ");
                  sb.append(property.getOwner().getTrimmedKey());
                  sb.append('[');
                  sb.append(property.getName());
                  sb.append("] = ");
                  Object value = property.getValue();
                  if ((value instanceof edu.cmu.cs.stage3.alice.core.Element)) {
                    sb.append(((edu.cmu.cs.stage3.alice.core.Element)value).getTrimmedKey());
                  } else {
                    sb.append(value);
                  }
                  sb.append('\n');
                }
                javax.swing.JOptionPane.showMessageDialog(ModelEditor.this, sb.toString());
              } else {
                int result = javax.swing.JOptionPane.showConfirmDialog(ModelEditor.this, "Would you like to delete: " + m_element.getTrimmedKey());
                if (result == 0) {
                  m_treeModel.removeDescendant(m_element);
                  setIsDirty(true);
                }
              }
              m_element = null;
            }
          });
          m_popupMenu.add(menuItem);
        }
        javax.swing.tree.TreePath path = m_tree.getClosestPathForLocation(e.getX(), e.getY());
        m_element = ((edu.cmu.cs.stage3.alice.core.Element)path.getLastPathComponent());
        if (m_element != null) {
          m_popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
      
      public void mouseReleased(java.awt.event.MouseEvent e) {
        if (e.isPopupTrigger()) {
          handlePopup(e);
        }
      }
      
      public void mousePressed(java.awt.event.MouseEvent e) {
        m_treeMouseEventModifiers = e.getModifiers();
        if (e.isPopupTrigger()) {
          handlePopup(e);
        }
        ModelEditor.this.onSelect((edu.cmu.cs.stage3.alice.core.Element)m_tree.getLastSelectedPathComponent());
      }
      
    });
    m_tree.setShowsRootHandles(true);
    m_tree.setToggleClickCount(0);
    
    m_prev = new javax.swing.JButton();
    m_prev.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        ModelEditor.this.onPrev();
      }
    });
    m_next = new javax.swing.JButton();
    m_next.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        ModelEditor.this.onNext();
      }
      
    });
    m_modeledBy = new javax.swing.JTextField();
    m_modeledBy.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        ModelEditor.this.onModeledByChange();
      }
      
      public void removeUpdate(javax.swing.event.DocumentEvent e) { ModelEditor.this.onModeledByChange(); }
      
      public void changedUpdate(javax.swing.event.DocumentEvent e) {
        ModelEditor.this.onModeledByChange();
      }
    });
    m_paintedBy = new javax.swing.JTextField();
    m_paintedBy.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        ModelEditor.this.onPaintedByChange();
      }
      
      public void removeUpdate(javax.swing.event.DocumentEvent e) { ModelEditor.this.onPaintedByChange(); }
      
      public void changedUpdate(javax.swing.event.DocumentEvent e) {
        ModelEditor.this.onPaintedByChange();
      }
      
    });
    m_revert = new javax.swing.JButton("Revert");
    m_revert.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        ModelEditor.this.onRevert();
      }
    });
    m_revert.setEnabled(false);
    
    m_forceWireframe = new javax.swing.JCheckBox("Force Wireframe");
    m_forceWireframe.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        ModelEditor.this.onForceWireframe(m_forceWireframe.isSelected());
      }
      

    });
    m_fileChooser = new javax.swing.JFileChooser();
  }
  
  public void init(edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory renderTargetFactory) { m_world = new edu.cmu.cs.stage3.alice.core.World();
    m_world.atmosphereColor.set(new edu.cmu.cs.stage3.alice.scenegraph.Color(0.75D, 0.75D, 1.0D));
    m_world.ambientLightBrightness.set(new Double(0.2D));
    
    m_renderTarget = new edu.cmu.cs.stage3.alice.core.RenderTarget();
    m_renderTarget.commit(renderTargetFactory);
    


































    java.awt.event.MouseListener cameraOrbiter = new java.awt.event.MouseListener()
    {
      private int m_prevX;
      
      public void mouseClicked(java.awt.event.MouseEvent e)
      {
        final int x = e.getX();
        final int y = e.getY();
        new Thread()
        {
          public void run() {
            edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo = m_renderTarget.pick(x, y, false, true);
            if (pickInfo.getCount() > 0) {
              System.err.println(pickInfo.getVisualAt(0).getBonus());
            } else {
              System.err.println("null");
            }
          }
        }.run();
      }
      

      public void mousePressed(java.awt.event.MouseEvent e) { m_prevX = e.getX(); }
      
      public void mouseReleased(java.awt.event.MouseEvent e) {}
      
      public void mouseEntered(java.awt.event.MouseEvent e) {}
      
      public void mouseExited(java.awt.event.MouseEvent e) {}
      
      public void mouseMoved(java.awt.event.MouseEvent e) {}
      
      public void mouseDragged(java.awt.event.MouseEvent e) {
        int x = e.getX();
        m_camera.rotateRightNow(edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), 0.001D * (x - m_prevX), getModel());
        m_prevX = x;
      }
      
    };
    m_renderTarget.addMouseListener(cameraOrbiter);
    m_renderTarget.addMouseMotionListener(cameraOrbiter);
    
    edu.cmu.cs.stage3.alice.core.light.DirectionalLight sun = new edu.cmu.cs.stage3.alice.core.light.DirectionalLight();
    vehicle.set(m_world);
    m_world.addChild(sun);
    sun.setOrientationRightNow(0.0D, -1.0D, 0.0D, 0.0D, 0.0D, 1.0D);
    
    m_camera = new edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera();
    m_camera.verticalViewingAngle.set(new Double(0.5D));
    m_camera.vehicle.set(m_world);
    m_camera.renderTarget.set(m_renderTarget);
    m_camera.name.set("Camera");
    m_world.addChild(m_camera);
    
    Model ground = new Model();
    name.set("Ground");
    m_world.addChild(ground);
    

    m_pivotDecorator = new edu.cmu.cs.stage3.alice.core.decorator.PivotDecorator();
    
    javax.swing.JPanel westPanel = new javax.swing.JPanel();
    
    westPanel.setLayout(new java.awt.GridBagLayout());
    java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
    
    anchor = 18;
    fill = 1;
    gridwidth = 1;
    weightx = 1.0D;
    
    westPanel.add(m_prev, gbc);
    gridwidth = 0;
    westPanel.add(m_next, gbc);
    
    weighty = 1.0D;
    javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(m_tree, 20, 30);
    westPanel.add(scrollPane, gbc);
    
    weighty = 0.0D;
    
    gridwidth = -1;
    westPanel.add(new javax.swing.JLabel("modeled by: "), gbc);
    gridwidth = 0;
    westPanel.add(m_modeledBy, gbc);
    gridwidth = -1;
    westPanel.add(new javax.swing.JLabel("painted by: "), gbc);
    gridwidth = 0;
    westPanel.add(m_paintedBy, gbc);
    
    gridwidth = 0;
    westPanel.add(m_revert, gbc);
    westPanel.add(m_forceWireframe, gbc);
    
    javax.swing.JSplitPane splitPane = new javax.swing.JSplitPane(1, westPanel, m_renderTarget.getAWTComponent());
    getContentPane().setLayout(new java.awt.GridLayout(1, 1));
    getContentPane().add(splitPane);
  }
  
  public void setIsDirty(boolean isDirty) {
    if (m_isDirty != isDirty) {
      m_isDirty = isDirty;
      m_revert.setEnabled(m_isDirty);
      updateTitle();
    }
  }
  
  public Model getModel() { return (Model)m_treeModel.getRoot(); }
  
  private void expandTree() {
    for (int i = 0; i < m_tree.getRowCount(); i++) {
      m_tree.expandRow(i);
    }
    m_tree.invalidate();
  }
  
  private void releasePreviousModel() { Model prevModel = getModel();
    if (prevModel != null)
    {


      vehicle.set(null);
      prevModel.setParent(null);
    }
  }
  
  public void setModel(Model model) { m_treeMouseEventModifiers = 0;
    m_treeModel.setRoot(model);
    expandTree();
    m_tree.setSelectionInterval(0, 0);
    m_tree.requestFocus();
    if (model != null) {
      model.setParent(m_world);
      




      vehicle.set(m_world);
      m_camera.getAGoodLookAtRightNow(model);
      edu.cmu.cs.stage3.math.Sphere sphere = model.getBoundingSphere();
      double radius = sphere.getRadius();
      m_camera.nearClippingPlaneDistance.set(new Double(0.1D));
      m_camera.farClippingPlaneDistance.set(new Double(radius * 2.0D + m_camera.getDistanceTo(model)));
      m_camera.moveRightNow(0.0D, 0.0D, -m_camera.nearClippingPlaneDistance.doubleValue());
      String modeledBy = (String)data.get("modeled by");
      if (modeledBy != null) {
        m_modeledBy.setText(modeledBy);
      } else {
        m_modeledBy.setText("");
      }
      String paintedBy = (String)data.get("painted by");
      if (paintedBy != null) {
        m_paintedBy.setText(paintedBy);
      } else {
        m_paintedBy.setText("");
      }
      onForceWireframe(m_forceWireframe.isSelected());
    } else {
      m_modeledBy.setText("");
      m_paintedBy.setText("");
    }
  }
  
  private boolean isContinueAppropriateAfterCheckingForSave() {
    if (m_isDirty) {
      int option = javax.swing.JOptionPane.showConfirmDialog(this, "Changes have been made.  Would you like to save before continuing?", "Check for save", 1, -1);
      switch (option) {
      case 0: 
        onFileSave();
        return true;
      case 1: 
        return true;
      case -1: 
      case 2: 
        return false;
      }
      throw new Error();
    }
    
    return true;
  }
  
  private void updateTitle() { StringBuffer sb = new StringBuffer("Model editor: ");
    if (m_isDirty) {
      sb.append("*");
    }
    sb.append(m_file.getPath());
    setTitle(sb.toString());
  }
  
  private java.io.File[] getSiblingFiles() { java.io.File directory = m_file.getParentFile();
    directory.listFiles(new java.io.FilenameFilter()
    {
      public boolean accept(java.io.File dir, String name) { return name.endsWith(".a2c"); }
    });
  }
  
  private void setFile(java.io.File file) {
    m_file = file;
    updateTitle();
    java.io.File[] siblingFiles = getSiblingFiles();
    int n = siblingFiles.length;
    for (int i = 0; i < n; i++)
      if (siblingFiles[i].equals(m_file)) {
        if (i == 0) {
          m_prev.setEnabled(false);
          m_prev.setText("<< { None }");
        } else {
          m_prev.setEnabled(true);
          m_prev.setText("<< " + siblingFiles[(i - 1)].getName());
          m_prev.setActionCommand(siblingFiles[(i - 1)].getPath());
        }
        if (i == n - 1) {
          m_next.setEnabled(false);
          m_next.setText("{ None } >>");
        } else {
          m_next.setEnabled(true);
          m_next.setText(siblingFiles[(i + 1)].getName() + " >>");
          m_next.setActionCommand(siblingFiles[(i + 1)].getPath());
        }
      }
  }
  
  private void onPrev() {
    if (isContinueAppropriateAfterCheckingForSave())
      open(new java.io.File(m_prev.getActionCommand()));
  }
  
  private void onNext() {
    if (isContinueAppropriateAfterCheckingForSave())
      open(new java.io.File(m_next.getActionCommand()));
  }
  
  private void onRevert() {
    open(m_file);
  }
  
  private void onModeledByChange() {
    getModeldata.put("modeled by", m_modeledBy.getText());
    setIsDirty(true);
  }
  
  private void onPaintedByChange() { getModeldata.put("painted by", m_paintedBy.getText());
    setIsDirty(true);
  }
  
  private void negativeAppearance(Model model) {
    edu.cmu.cs.stage3.alice.scenegraph.Appearance sgAppearance = model.getSceneGraphAppearance();
    
    sgAppearance.setShadingStyle(edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH);
    sgAppearance.setOpacity(0.25D);
    for (int i = 0; i < parts.size(); i++)
      negativeAppearance((Model)parts.get(i));
  }
  
  private void positiveAppearance(Model model) {
    edu.cmu.cs.stage3.alice.scenegraph.Appearance sgAppearance = model.getSceneGraphAppearance();
    edu.cmu.cs.stage3.alice.scenegraph.TextureMap sgTextureMap = null;
    if (diffuseColorMap.getTextureMapValue() != null) {
      sgTextureMap = diffuseColorMap.getTextureMapValue().getSceneGraphTextureMap();
    }
    sgAppearance.setDiffuseColorMap(sgTextureMap);
    
    sgAppearance.setShadingStyle(shadingStyle.getShadingStyleValue());
    sgAppearance.setOpacity(opacity.doubleValue());
    for (int i = 0; i < parts.size(); i++) {
      positiveAppearance((Model)parts.get(i));
    }
  }
  
  private void textureMapAppearance(Model model, edu.cmu.cs.stage3.alice.core.TextureMap textureMap) {
    edu.cmu.cs.stage3.alice.scenegraph.Appearance sgAppearance = model.getSceneGraphAppearance();
    if ((m_treeMouseEventModifiers & 0x2) != 0) {
      sgAppearance.setDiffuseColorMap(textureMap.getSceneGraphTextureMap());
    }
    else if (diffuseColorMap.get() != textureMap)
    {

      sgAppearance.setDiffuseColorMap(null);
    }
    
    for (int i = 0; i < parts.size(); i++)
      textureMapAppearance((Model)parts.get(i), textureMap);
  }
  
  private void onSelect(edu.cmu.cs.stage3.alice.core.Element element) {
    positiveAppearance(getModel());
    if ((element instanceof Model)) {
      if (getModel() != element) {
        negativeAppearance(getModel());
        positiveAppearance((Model)element);
      }
      m_pivotDecorator.setTransformable((Model)element);
    } else if ((element instanceof edu.cmu.cs.stage3.alice.core.TextureMap)) {
      textureMapAppearance(getModel(), (edu.cmu.cs.stage3.alice.core.TextureMap)element);
    }
    m_pivotDecorator.setIsShowing(element instanceof Model);
  }
  
  private void onForceWireframe(Model model, boolean forceWireframe) {
    edu.cmu.cs.stage3.alice.scenegraph.Appearance sgAppearance = model.getSceneGraphAppearance();
    edu.cmu.cs.stage3.alice.scenegraph.FillingStyle fillingStyle;
    edu.cmu.cs.stage3.alice.scenegraph.FillingStyle fillingStyle; if (forceWireframe) {
      fillingStyle = edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME;
    } else {
      fillingStyle = fillingStyle.getFillingStyleValue();
    }
    sgAppearance.setFillingStyle(fillingStyle);
    for (int i = 0; i < parts.size(); i++)
      onForceWireframe((Model)parts.get(i), forceWireframe);
  }
  
  private void onForceWireframe(boolean forceWireframe) {
    onForceWireframe(getModel(), forceWireframe);
  }
  
  private void open(java.io.File file) {
    setFile(file);
    try {
      releasePreviousModel();
      Model model = (Model)edu.cmu.cs.stage3.alice.core.Element.load(file, m_world);
      setModel(model);
      hardenPoses();
      setIsDirty(false);
    } catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
      edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] propertyReferences = upre.getPropertyReferences();
      for (int i = 0; i < propertyReferences.length; i++) {
        System.err.println(propertyReferences[i]);
      }
    } catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }
  
  private void onFileOpen() { onFileOpen(null); }
  
  private void onFileOpen(java.io.File file) {
    if (file == null) {
      if (m_file != null) {
        m_fileChooser.setCurrentDirectory(m_file.getParentFile());
      }
    }
    else if (file.isDirectory()) {
      m_fileChooser.setCurrentDirectory(file);
    } else {
      open(file);
      return;
    }
    
    m_fileChooser.setDialogType(0);
    m_fileChooser.setFileSelectionMode(0);
    m_fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter()
    {
      public boolean accept(java.io.File file) {
        return (file.isDirectory()) || (file.getName().endsWith(".a2c"));
      }
      
      public String getDescription() {
        return "Alice Character (*.a2c)";
      }
      
    });
    m_fileChooser.setPreferredSize(new java.awt.Dimension(500, 300));
    m_fileChooser.rescanCurrentDirectory();
    if (m_fileChooser.showDialog(this, null) == 0) {
      open(m_fileChooser.getSelectedFile());
    }
    m_tree.requestFocus();
  }
  
  private void onFileSave() {
    try {
      softenPoses();
      getModel().store(m_file);
      setIsDirty(false);
    } catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }
  


  private void onFileExit()
  {
    if (isContinueAppropriateAfterCheckingForSave()) {
      System.exit(0);
    }
  }
  
  private void hardenPoses() {
    edu.cmu.cs.stage3.alice.core.Pose[] poses = (edu.cmu.cs.stage3.alice.core.Pose[])getModel().getDescendants(edu.cmu.cs.stage3.alice.core.Pose.class);
    for (int i = 0; i < poses.length; i++)
      poses[i].HACK_harden();
  }
  
  private void softenPoses() {
    edu.cmu.cs.stage3.alice.core.Pose[] poses = (edu.cmu.cs.stage3.alice.core.Pose[])getModel().getDescendants(edu.cmu.cs.stage3.alice.core.Pose.class);
    for (int i = 0; i < poses.length; i++) {
      poses[i].HACK_soften();
    }
  }
}
