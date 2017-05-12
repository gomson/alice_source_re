package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule;
import edu.cmu.cs.stage3.caitlin.stencilhelp.application.StencilApplication;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StencilManager implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener, KeyListener, StencilClient, StencilStackChangeListener
{
  StencilApplication stencilApp = null;
  StencilPanel stencilPanel = null;
  ObjectPositionManager positionManager = null;
  Vector updateShapes = null;
  Vector clearRegions = null;
  StencilParser stencilParser = null;
  
  Vector mouseEventListeners = new Vector();
  Vector keyEventListeners = new Vector();
  Vector stencilFocusListeners = new Vector();
  Vector layoutChangeListeners = new Vector();
  Vector stencilStackChangeListeners = new Vector();
  Vector readWriteListeners = new Vector();
  Vector stencilList = new Vector();
  int currentStencilIndex = 0;
  StencilObject focalObject = null;
  StencilMouseAdapter mouseAdapter = new StencilMouseAdapter();
  Stencil errorStencil = null;
  
  boolean writeEnabled = true;
  boolean stencilChanged = false;
  String worldToLoad = null;
  String nextStack = null;
  String previousStack = null;
  long lastRedrawTime = -1L;
  
  boolean overrideMinResolution = false;
  
  private NavigationBar navBar;
  private boolean editorDirty = false;
  private boolean captureMode = false;
  private File captureFolder;
  public static final boolean SHOW_TUTORIAL_MENUES = false;
  private TutorialEditorMenus tutorialEditorMenus;
  private TutorialEditor tutorialEditor;
  private File tutorialFile;
  
  public StencilManager(StencilApplication stencilApp) {
    this.stencilApp = stencilApp;
    stencilPanel = new StencilPanel(this);
    
    tutorialEditorMenus = new TutorialEditorMenus(this);
    stencilPanel.addMessageListener(tutorialEditorMenus);
    
    addMouseEventListener(stencilPanel);
    addReadWriteListener(stencilPanel);
    positionManager = new ObjectPositionManager(stencilApp);
    



    insertNewStencil(false);
    triggerRefresh();
    tutorialEditor = new TutorialEditor(this);
  }
  
  protected void createDefaultStencilObjects()
  {
    Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    
    NavigationBar navBar = new NavigationBar(this, positionManager);
    addMouseEventListener(navBar);
    addStencilStackChangeListener(navBar);
    currentStencil.addObject(navBar);
    


    addStencilFocusListener(tutorialEditorMenus);
    
    currentStencil.addObject(tutorialEditorMenus);
  }
  
  protected void triggerRefresh() {
    stencilPanel.redraw();
  }
  
  protected void triggerRefresh(long time) {
    if (time > lastRedrawTime) {
      lastRedrawTime = time;
      stencilPanel.redraw();
    }
  }
  


  protected void requestFocus(StencilObject newFocalObject)
  {
    if ((focalObject != null) && ((focalObject instanceof StencilFocusListener)) && (stencilFocusListeners.contains(focalObject))) {
      ((StencilFocusListener)focalObject).focusLost();
    }
    if ((newFocalObject != null) && ((newFocalObject instanceof StencilFocusListener)) && (stencilFocusListeners.contains(focalObject))) {
      ((StencilFocusListener)newFocalObject).focusGained();
    }
    
    setNewFocalObject(newFocalObject);
  }
  
  protected String showDialog(FileFilter filter)
  {
    JFileChooser chooser = new JFileChooser();
    if (filter != null) {
      chooser.setFileFilter(filter);
    }
    int returnVal = chooser.showOpenDialog(stencilPanel);
    if (returnVal == 0) {
      return chooser.getCurrentDirectory() + File.separator + chooser.getSelectedFile().getName();
    }
    return null;
  }
  


  public Stencil newStencil()
  {
    return new Stencil();
  }
  
  public Stencil newStencil(int stepsToGoBack) {
    Stencil newStencil = new Stencil();
    newStencil.setSteps(stepsToGoBack);
    return newStencil;
  }
  
  public void loadStencilsFile() {
    File loadFile = null;
    StencilFileFilter filter = new StencilFileFilter();
    
    while (loadFile == null) {
      String fileName = showDialog(filter);
      

      if (fileName != null) {
        loadFile = new File(fileName);
        

        if (!loadFile.exists()) {
          int ans = DialogManager.showConfirmDialog(Messages.getString("Can_t_find_") + fileName + Messages.getString("__Please_choose_another"), Messages.getString("Can_t_find_file"), 2);
          
          if (ans == 2) {
            return;
          }
          loadFile = null;
        }
      }
      else
      {
        return;
      }
    }
    
    loadStencilTutorial(loadFile);
  }
  
  public void saveStencilsFile()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: goto +104 -> 106
    //   5: aload_0
    //   6: aconst_null
    //   7: invokevirtual 281	edu/cmu/cs/stage3/caitlin/stencilhelp/client/StencilManager:showDialog	(Ljavax/swing/filechooser/FileFilter;)Ljava/lang/String;
    //   10: astore_2
    //   11: aload_2
    //   12: ifnull +93 -> 105
    //   15: new 246	java/io/File
    //   18: dup
    //   19: aload_2
    //   20: invokespecial 283	java/io/File:<init>	(Ljava/lang/String;)V
    //   23: astore_1
    //   24: aload_2
    //   25: ldc_w 324
    //   28: invokevirtual 326	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   31: ifne +24 -> 55
    //   34: new 234	java/lang/StringBuilder
    //   37: dup
    //   38: aload_2
    //   39: invokestatic 298	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   42: invokespecial 304	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   45: ldc_w 324
    //   48: invokevirtual 250	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: invokevirtual 260	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   54: astore_2
    //   55: aload_1
    //   56: invokevirtual 286	java/io/File:exists	()Z
    //   59: ifeq +47 -> 106
    //   62: ldc_w 330
    //   65: invokestatic 292	edu/cmu/cs/stage3/lang/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   68: astore_3
    //   69: aload_3
    //   70: ldc_w 332
    //   73: invokestatic 292	edu/cmu/cs/stage3/lang/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   76: iconst_1
    //   77: invokestatic 309	edu/cmu/cs/stage3/swing/DialogManager:showConfirmDialog	(Ljava/lang/Object;Ljava/lang/String;I)I
    //   80: istore 4
    //   82: iload 4
    //   84: ifeq +22 -> 106
    //   87: iload 4
    //   89: iconst_1
    //   90: if_icmpne +8 -> 98
    //   93: aconst_null
    //   94: astore_1
    //   95: goto +11 -> 106
    //   98: iload 4
    //   100: iconst_2
    //   101: if_icmpne +5 -> 106
    //   104: return
    //   105: return
    //   106: aload_1
    //   107: ifnull -102 -> 5
    //   110: invokestatic 334	javax/xml/parsers/DocumentBuilderFactory:newInstance	()Ljavax/xml/parsers/DocumentBuilderFactory;
    //   113: astore_3
    //   114: aload_3
    //   115: invokevirtual 340	javax/xml/parsers/DocumentBuilderFactory:newDocumentBuilder	()Ljavax/xml/parsers/DocumentBuilder;
    //   118: astore 4
    //   120: aload 4
    //   122: invokevirtual 344	javax/xml/parsers/DocumentBuilder:newDocument	()Lorg/w3c/dom/Document;
    //   125: astore_2
    //   126: goto +6 -> 132
    //   129: astore_3
    //   130: aconst_null
    //   131: astore_2
    //   132: aload_2
    //   133: ifnull +177 -> 310
    //   136: aload_2
    //   137: ldc_w 350
    //   140: invokeinterface 352 2 0
    //   145: astore_3
    //   146: aload_3
    //   147: ldc_w 358
    //   150: ldc_w 360
    //   153: invokeinterface 362 3 0
    //   158: aload_0
    //   159: getfield 117	edu/cmu/cs/stage3/caitlin/stencilhelp/client/StencilManager:worldToLoad	Ljava/lang/String;
    //   162: ifnull +16 -> 178
    //   165: aload_3
    //   166: ldc_w 368
    //   169: aload_0
    //   170: getfield 117	edu/cmu/cs/stage3/caitlin/stencilhelp/client/StencilManager:worldToLoad	Ljava/lang/String;
    //   173: invokeinterface 362 3 0
    //   178: aload_0
    //   179: getfield 119	edu/cmu/cs/stage3/caitlin/stencilhelp/client/StencilManager:nextStack	Ljava/lang/String;
    //   182: ifnull +16 -> 198
    //   185: aload_3
    //   186: ldc_w 370
    //   189: aload_0
    //   190: getfield 119	edu/cmu/cs/stage3/caitlin/stencilhelp/client/StencilManager:nextStack	Ljava/lang/String;
    //   193: invokeinterface 362 3 0
    //   198: aload_0
    //   199: getfield 121	edu/cmu/cs/stage3/caitlin/stencilhelp/client/StencilManager:previousStack	Ljava/lang/String;
    //   202: ifnull +16 -> 218
    //   205: aload_3
    //   206: ldc_w 371
    //   209: aload_0
    //   210: getfield 121	edu/cmu/cs/stage3/caitlin/stencilhelp/client/StencilManager:previousStack	Ljava/lang/String;
    //   213: invokeinterface 362 3 0
    //   218: iconst_0
    //   219: istore 4
    //   221: goto +27 -> 248
    //   224: aload_0
    //   225: getfield 98	edu/cmu/cs/stage3/caitlin/stencilhelp/client/StencilManager:stencilList	Ljava/util/Vector;
    //   228: iload 4
    //   230: invokevirtual 174	java/util/Vector:elementAt	(I)Ljava/lang/Object;
    //   233: checkcast 178	edu/cmu/cs/stage3/caitlin/stencilhelp/client/StencilManager$Stencil
    //   236: astore 5
    //   238: aload 5
    //   240: aload_2
    //   241: aload_3
    //   242: invokevirtual 372	edu/cmu/cs/stage3/caitlin/stencilhelp/client/StencilManager$Stencil:write	(Lorg/w3c/dom/Document;Lorg/w3c/dom/Element;)V
    //   245: iinc 4 1
    //   248: iload 4
    //   250: aload_0
    //   251: getfield 98	edu/cmu/cs/stage3/caitlin/stencilhelp/client/StencilManager:stencilList	Ljava/util/Vector;
    //   254: invokevirtual 376	java/util/Vector:size	()I
    //   257: if_icmplt -33 -> 224
    //   260: aload_2
    //   261: aload_3
    //   262: invokeinterface 380 2 0
    //   267: pop
    //   268: aload_2
    //   269: invokeinterface 384 1 0
    //   274: invokeinterface 388 1 0
    //   279: new 391	java/io/FileWriter
    //   282: dup
    //   283: aload_1
    //   284: invokespecial 393	java/io/FileWriter:<init>	(Ljava/io/File;)V
    //   287: astore 4
    //   289: aload_2
    //   290: aload 4
    //   292: invokestatic 395	edu/cmu/cs/stage3/xml/Encoder:write	(Lorg/w3c/dom/Document;Ljava/io/Writer;)V
    //   295: aload 4
    //   297: invokevirtual 400	java/io/FileWriter:close	()V
    //   300: goto +10 -> 310
    //   303: astore 4
    //   305: aload 4
    //   307: invokevirtual 403	java/io/IOException:printStackTrace	()V
    //   310: return
    // Line number table:
    //   Java source line #181	-> byte code offset #0
    //   Java source line #183	-> byte code offset #2
    //   Java source line #184	-> byte code offset #5
    //   Java source line #187	-> byte code offset #11
    //   Java source line #188	-> byte code offset #15
    //   Java source line #190	-> byte code offset #24
    //   Java source line #191	-> byte code offset #34
    //   Java source line #194	-> byte code offset #55
    //   Java source line #195	-> byte code offset #62
    //   Java source line #196	-> byte code offset #69
    //   Java source line #198	-> byte code offset #82
    //   Java source line #200	-> byte code offset #87
    //   Java source line #201	-> byte code offset #93
    //   Java source line #202	-> byte code offset #95
    //   Java source line #203	-> byte code offset #104
    //   Java source line #209	-> byte code offset #105
    //   Java source line #183	-> byte code offset #106
    //   Java source line #217	-> byte code offset #110
    //   Java source line #218	-> byte code offset #114
    //   Java source line #219	-> byte code offset #120
    //   Java source line #220	-> byte code offset #126
    //   Java source line #221	-> byte code offset #130
    //   Java source line #224	-> byte code offset #132
    //   Java source line #225	-> byte code offset #136
    //   Java source line #226	-> byte code offset #146
    //   Java source line #227	-> byte code offset #158
    //   Java source line #228	-> byte code offset #165
    //   Java source line #229	-> byte code offset #178
    //   Java source line #230	-> byte code offset #185
    //   Java source line #231	-> byte code offset #198
    //   Java source line #232	-> byte code offset #205
    //   Java source line #233	-> byte code offset #218
    //   Java source line #234	-> byte code offset #224
    //   Java source line #235	-> byte code offset #238
    //   Java source line #233	-> byte code offset #245
    //   Java source line #238	-> byte code offset #260
    //   Java source line #239	-> byte code offset #268
    //   Java source line #245	-> byte code offset #279
    //   Java source line #246	-> byte code offset #289
    //   Java source line #247	-> byte code offset #295
    //   Java source line #248	-> byte code offset #300
    //   Java source line #249	-> byte code offset #305
    //   Java source line #252	-> byte code offset #310
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	311	0	this	StencilManager
    //   1	283	1	saveFile	File
    //   10	45	2	fileName	String
    //   125	2	2	document	Document
    //   131	159	2	document	Document
    //   68	2	3	msg	String
    //   113	2	3	factory	DocumentBuilderFactory
    //   129	2	3	pce	ParserConfigurationException
    //   145	117	3	root	Element
    //   80	19	4	ans	int
    //   118	3	4	builder	DocumentBuilder
    //   219	30	4	i	int
    //   287	9	4	fileWriter	FileWriter
    //   303	3	4	ioe	IOException
    //   236	3	5	stencil	Stencil
    // Exception table:
    //   from	to	target	type
    //   110	126	129	javax/xml/parsers/ParserConfigurationException
    //   279	300	303	java/io/IOException
  }
  
  public void toggleLock()
  {
    setWriteEnabled(!writeEnabled);
  }
  

  public void createNewHole(Point p)
  {
    String id = stencilApp.getIDForPoint(p, false);
    if (id != null) {
      Hole h = new Hole(id, positionManager, stencilApp, this);
      addMouseEventListener(h);
      addLayoutChangeListener(h);
      Note n = new Note(p, new Point(30, 30), h, positionManager, this, false);
      addMouseEventListener(n);
      addKeyEventListener(n);
      Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
      currentStencil.addObject(h);
      currentStencil.addObject(n);
      
      setNewFocalObject(n);
      announceLayoutChange();
      n.initializeNote();
      triggerRefresh();
    }
  }
  
  public void createNewFrame(Point p) {
    String id = stencilApp.getIDForPoint(p, false);
    if (id != null) {
      Frame h = new Frame(id, positionManager);
      addLayoutChangeListener(h);
      Note n = new Note(p, new Point(30, 30), h, positionManager, this, false);
      addMouseEventListener(n);
      addKeyEventListener(n);
      Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
      currentStencil.addObject(h);
      currentStencil.addObject(n);
      
      setNewFocalObject(n);
      announceLayoutChange();
      n.initializeNote();
      triggerRefresh();
    }
  }
  
  public void createNewNote(Point p) { Note n = new Note(p, new Point(0, 0), null, positionManager, this, false);
    addMouseEventListener(n);
    addKeyEventListener(n);
    Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    currentStencil.addObject(n);
    announceLayoutChange();
    n.initializeNote();
    triggerRefresh();
    


    setNewFocalObject(n);
  }
  
  public void removeAllObjects() {
    setNewFocalObject(null);
    Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    currentStencil.removeAllObjects();
    stencilChanged = true;
  }
  
  protected Vector getShapesToDraw()
  {
    Vector shapes = new Vector();
    Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    Vector currentObjects = currentStencil.getObjects();
    for (int i = 0; i < currentObjects.size(); i++) {
      StencilObject screenObj = (StencilObject)currentObjects.elementAt(i);
      Vector temp = screenObj.getShapes();
      if (temp != null) {
        shapes.addAll(temp);
      }
    }
    return shapes;
  }
  

  public Vector getUpdateShapes()
  {
    updateDrawInfo();
    return updateShapes;
  }
  
  public Vector getClearRegions() {
    return clearRegions;
  }
  
  protected void updateDrawInfo() {
    updateShapes = new Vector();
    clearRegions = new Vector();
    Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    Vector currentObjects = currentStencil.getObjects();
    Vector unmodifiedObjects = new Vector();
    boolean overlapping = false;
    
    if (!stencilChanged)
    {

      for (int i = 0; i < currentObjects.size(); i++) {
        StencilObject obj = (StencilObject)currentObjects.elementAt(i);
        
        if (obj.isModified()) {
          Vector objShapes = obj.getShapes();
          if (objShapes != null) {
            updateShapes.addAll(objShapes);
          }
          Rectangle clear = obj.getPreviousRectangle();
          if (clear != null) {
            clearRegions.addElement(clear);
          }
        } else {
          unmodifiedObjects.addElement(obj);
        }
      }
      

      for (int i = 0; i < unmodifiedObjects.size(); i++) {
        StencilObject obj = (StencilObject)unmodifiedObjects.elementAt(i);
        Rectangle objRect = obj.getRectangle();
        Rectangle prevObjRect = obj.getPreviousRectangle();
        if (prevObjRect == null)
          prevObjRect = objRect;
        if (objRect != null) {
          for (int j = 0; j < clearRegions.size(); j++) {
            Rectangle clearRect = (Rectangle)clearRegions.elementAt(j);
            if ((clearRect.intersects(objRect)) || (clearRect.intersects(prevObjRect))) {
              overlapping = true;
              break;
            }
          }
        }
      }
    }
    

    if ((overlapping) || (stencilChanged)) {
      updateShapes = new Vector();
      clearRegions = new Vector();
      clearRegions.addElement(new Rectangle(0, 0, stencilPanel.getWidth(), stencilPanel.getHeight()));
      for (int i = 0; i < currentObjects.size(); i++) {
        StencilObject obj = (StencilObject)currentObjects.elementAt(i);
        Vector newShapes = obj.getShapes();
        if (newShapes != null)
          updateShapes.addAll(obj.getShapes());
      }
      stencilChanged = false;
    }
  }
  
  public boolean hasNext()
  {
    boolean autoAdvancingHole = false;
    Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    if (currentStencil != null) {
      Vector stencilObjects = currentStencil.getObjects();
      for (int i = 0; i < stencilObjects.size(); i++) {
        StencilObject stencilObj = (StencilObject)stencilObjects.elementAt(i);
        if (((stencilObj instanceof Hole)) && 
          (((Hole)stencilObj).getAutoAdvance()) && (((Hole)stencilObj).getAdvanceEvent() != 2)) {
          autoAdvancingHole = true;
        }
      }
    }
    
    if ((currentStencilIndex < stencilList.size() - 1) && (!autoAdvancingHole)) {
      return true;
    }
    return false;
  }
  
  public boolean hasPrevious() { if (currentStencilIndex > 0) {
      Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
      if (currentStencil.getStepsToGoBack() > 0) {
        return true;
      }
      return false;
    }
    return false;
  }
  
  public int getStencilNumber() { return currentStencilIndex; }
  

  public int getNumberOfStencils() { return stencilList.size(); }
  
  public void reloadStencils() {
    loadWorld();
    

    Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    currentStencil.setCurrentStencil(false);
    currentStencilIndex = 0;
    

    currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    currentStencil.setCurrentStencil(true);
    broadcastStencilNumberChange();
    

    stencilApp.clearWayPoints();
    stencilApp.makeWayPoint();
    
    stencilChanged = true;
  }
  
  protected boolean checkState(Stencil currentStencil) {
    if (writeEnabled)
    {
      currentStencil.setEndState(stencilApp.getCurrentState());
      return true;
    }
    
    if (currentStencil.getEndState() != null) {
      boolean appInRightState = stencilApp.doesStateMatch(currentStencil.getEndState());
      if (!appInRightState) {
        return false;
      }
    }
    
    return true;
  }
  
  public void showNextStencil()
  {
    if (currentStencilIndex < stencilList.size() - 1)
    {
      Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
      
      boolean checkState = checkState(currentStencil);
      
      currentStencil.setCurrentStencil(false);
      currentStencilIndex += 1;
      

      currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
      currentStencil.setCurrentStencil(true);
      
      if (!checkState)
      {

        currentStencil.setErrorStencil(true);
      }
      broadcastStencilNumberChange();
      
      stencilChanged = true;
      stencilApp.makeWayPoint();
    }
  }
  
  public void showPreviousStencil() { Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    if (currentStencil != null) {
      int stepsToGoBack = currentStencil.getStepsToGoBack();
      
      if (stepsToGoBack > 0) {
        for (int i = 0; i < stepsToGoBack; i++) {
          stencilApp.goToPreviousWayPoint();
        }
        
        if (currentStencilIndex > stepsToGoBack - 1) {
          currentStencil.setCurrentStencil(false);
          currentStencilIndex -= stepsToGoBack;
        }
        
        currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
        currentStencil.setCurrentStencil(true);
        broadcastStencilNumberChange();
        stencilChanged = true;
      }
    }
  }
  
  protected File getFile(String fileName) {
    int index = fileName.indexOf("/");
    int length = fileName.length();
    String fileString = null;
    if (index != -1) {
      fileString = fileName.substring(0, index) + File.separator + fileName.substring(index + 1, length);
    }
    

    File file = new File(fileString);
    
    if (file.exists()) {
      return file;
    }
    return null;
  }
  
  public void showNextStack() { File nextFile = getFile(nextStack);
    if (nextFile != null)
      loadStencilTutorial(nextFile);
  }
  
  public void showPreviousStack() {
    File previousFile = getFile(previousStack);
    if (previousFile != null) {
      loadStencilTutorial(previousFile);
    }
  }
  
  public void insertNewStencil() {
    Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    boolean checkState = checkState(currentStencil);
    


    currentStencil.setCurrentStencil(false);
    currentStencilIndex += 1;
    

    currentStencil = new Stencil();
    stencilList.insertElementAt(currentStencil, currentStencilIndex);
    createDefaultStencilObjects();
    currentStencil.setCurrentStencil(true);
    
    broadcastStencilNumberChange();
    stencilChanged = true;
    stencilApp.makeWayPoint();
    triggerRefresh();
  }
  



  public boolean isDropAccessible(Point p) { return false; }
  
  public void update() {
    if (getIsShowing())
    {
      announceLayoutChange();
    }
  }
  
  public void stateChanged() {}
  
  public java.awt.Component getStencilComponent() {
    return stencilPanel;
  }
  
  public void showStencils(boolean show) {
    stencilPanel.setIsDrawing(show);
    if (getIsShowing()) {
      stencilPanel.removeMouseListener(mouseAdapter);
      stencilPanel.removeMouseMotionListener(this);
      stencilPanel.addMouseListener(mouseAdapter);
      stencilPanel.addMouseMotionListener(this);
      if (writeEnabled) {
        addStencilStackChangeListener(this);
      }
      update();
    }
    else {
      stencilApp.setVisible(false);
      stencilPanel.removeMouseListener(mouseAdapter);
      stencilPanel.removeMouseMotionListener(this);
      currentStencilIndex = 0;
      stencilList.removeAllElements();
      
      stencilList.addElement(newStencil());
    }
  }
  
  public boolean getIsShowing() {
    return stencilPanel.getIsDrawing();
  }
  
  public void loadStencilTutorial(File tutorialFile)
  {
    showStencils(true);
    
    stencilParser = new StencilParser(this, positionManager, stencilApp);
    worldToLoad = null;
    nextStack = null;
    previousStack = null;
    Vector newStencilList = stencilParser.parseFile(tutorialFile);
    

    mouseEventListeners = new Vector();
    keyEventListeners = new Vector();
    stencilFocusListeners = new Vector();
    layoutChangeListeners = new Vector();
    stencilStackChangeListeners = new Vector();
    currentStencilIndex = 0;
    
    setNewFocalObject(null);
    stencilPanel.removeAllMessageListeners();
    
    addMouseEventListener(stencilPanel);
    addReadWriteListener(stencilPanel);
    if ((stencilApp instanceof AuthoringTool)) {
      ((AuthoringTool)stencilApp).stencilManagerReFocus();
    } else {
      stencilApp.deFocus();
    }
    
    stencilList = newStencilList;
    addLinks();
    
    if (tutorialFile == null) {
      insertNewStencil(true);
    }
    Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    currentStencil.setCurrentStencil(true);
    broadcastCurrentStencilChange();
    broadcastStencilNumberChange();
    stencilChanged = true;
    triggerRefresh();
    



    stencilApp.clearWayPoints();
    stencilApp.makeWayPoint();
    
    this.tutorialFile = tutorialFile;
    tutorialEditor.update();
  }
  

  public void addStencilStackChangeListener(StencilStackChangeListener sscListener)
  {
    stencilStackChangeListeners.addElement(sscListener);
  }
  
  public void removeStencilStackChangeListener(StencilStackChangeListener sscListener) { stencilStackChangeListeners.remove(sscListener); }
  
  protected void broadcastStencilNumberChange() {
    for (int i = 0; i < stencilStackChangeListeners.size(); i++)
      ((StencilStackChangeListener)stencilStackChangeListeners.elementAt(i)).numberOfStencilsChanged(stencilList.size());
  }
  
  protected void broadcastCurrentStencilChange() {
    for (int i = 0; i < stencilStackChangeListeners.size(); i++) {
      ((StencilStackChangeListener)stencilStackChangeListeners.elementAt(i)).currentStencilChanged(currentStencilIndex);
    }
  }
  
  public void addReadWriteListener(ReadWriteListener rwListener)
  {
    readWriteListeners.addElement(rwListener);
  }
  
  public void removeReadWriteListener(ReadWriteListener rwListener) { readWriteListeners.removeElement(rwListener); }
  
  public void setWriteEnabled(boolean enabled)
  {
    writeEnabled = enabled;
    
    for (int i = 0; i < readWriteListeners.size(); i++) {
      ReadWriteListener rwL = (ReadWriteListener)readWriteListeners.elementAt(i);
      rwL.setWriteEnabled(writeEnabled);
    }
    triggerRefresh();
  }
  
  protected void loadWorld() {
    if (worldToLoad != null) {
      int index = worldToLoad.indexOf("/");
      int length = worldToLoad.length();
      String worldString = null;
      if (index != -1) {
        worldString = worldToLoad.substring(0, index) + File.separator + worldToLoad.substring(index + 1, length);
      }
      if (worldString != null) {
        stencilApp.performTask("loadWorld<" + worldString + ">");
      }
    }
  }
  
  public void setWorld(String worldToLoad) {
    this.worldToLoad = worldToLoad;
    loadWorld();
  }
  
  public void setNextAndPreviousStacks(String previousStack, String nextStack) {
    this.previousStack = previousStack;
    this.nextStack = nextStack;
  }
  
  protected void addLinks() {
    if ((previousStack != null) && (stencilList.size() > 0)) {
      Stencil stencil = (Stencil)stencilList.elementAt(0);
      stencil.addObject(new Link(this, positionManager, false));
    }
    
    if ((nextStack != null) && (stencilList.size() > 0)) {
      Stencil stencil = (Stencil)stencilList.elementAt(stencilList.size() - 1);
      stencil.addObject(new Link(this, positionManager, true));
    }
  }
  
  public void addLayoutChangeListener(LayoutChangeListener lcListener)
  {
    layoutChangeListeners.addElement(lcListener);
  }
  
  public void removeLayoutChangeListener(LayoutChangeListener lcListener) { layoutChangeListeners.remove(lcListener); }
  
  protected void announceLayoutChange() {
    boolean error = false;
    
    for (int i = 0; i < layoutChangeListeners.size(); i++) {
      LayoutChangeListener lcListener = (LayoutChangeListener)layoutChangeListeners.elementAt(i);
      if (!(lcListener instanceof Note))
        error = !lcListener.layoutChanged();
      if (error) {
        Stencil currentStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
        
        currentStencil.setErrorStencil(true);
      }
    }
    for (int i = 0; i < layoutChangeListeners.size(); i++) {
      LayoutChangeListener lcListener = (LayoutChangeListener)layoutChangeListeners.elementAt(i);
      if ((lcListener instanceof Note))
        lcListener.layoutChanged();
      stencilChanged = true;
    }
    triggerRefresh();
  }
  
  public void addStencilFocusListener(StencilFocusListener sfListener)
  {
    stencilFocusListeners.addElement(sfListener);
  }
  
  public void removeStencilFocusListener(StencilFocusListener sfListener) {
    stencilFocusListeners.remove(sfListener);
  }
  
  protected void setNewFocalListener(MouseEventListener meListener)
  {
    StencilObject newFocalObject = null;
    if ((meListener instanceof StencilObject)) {
      newFocalObject = (StencilObject)meListener;
    }
    

    if (focalObject != newFocalObject) {
      if ((focalObject != null) && ((focalObject instanceof StencilFocusListener)) && (stencilFocusListeners.contains(focalObject))) {
        ((StencilFocusListener)focalObject).focusLost();
      }
      if ((newFocalObject != null) && ((newFocalObject instanceof StencilFocusListener)) && (stencilFocusListeners.contains(focalObject))) {
        ((StencilFocusListener)newFocalObject).focusGained();
      }
      if ((focalObject instanceof Hole)) {
        stencilApp.deFocus();
      }
      
      setNewFocalObject(newFocalObject);
    }
  }
  
  public void addMouseEventListener(MouseEventListener meListener)
  {
    if ((meListener instanceof StencilPanel)) {
      mouseEventListeners.addElement(meListener);
    } else {
      mouseEventListeners.insertElementAt(meListener, 0);
    }
  }
  
  public void removeMouseEventListener(MouseEventListener meListener) {
    mouseEventListeners.removeElement(meListener);
  }
  
  public void mousePressed(MouseEvent e) {
    for (int i = 0; i < mouseEventListeners.size(); i++) {
      MouseEventListener meListener = (MouseEventListener)mouseEventListeners.elementAt(i);
      if (meListener.contains(e.getPoint())) {
        setNewFocalListener(meListener);
        
        boolean refresh = meListener.mousePressed(e);
        if (refresh) {
          triggerRefresh(e.getWhen());
        }
        return;
      }
    }
  }
  
  public void mouseReleased(MouseEvent e) {
    if ((focalObject != null) && ((focalObject instanceof MouseEventListener))) {
      boolean refresh = ((MouseEventListener)focalObject).mouseReleased(e);
      if (refresh) {
        triggerRefresh(e.getWhen());
      }
    }
  }
  
  public void mouseClicked(MouseEvent e) {
    for (int i = 0; i < mouseEventListeners.size(); i++) {
      MouseEventListener meListener = (MouseEventListener)mouseEventListeners.elementAt(i);
      if (meListener.contains(e.getPoint())) {
        setNewFocalListener(meListener);
        

        boolean refresh = meListener.mouseClicked(e);
        if (refresh) {
          triggerRefresh(e.getWhen());
        }
        return;
      }
    }
  }
  






  public void mouseEntered(MouseEvent e) {}
  






  public void mouseExited(MouseEvent e) {}
  






  public void mouseMoved(MouseEvent e)
  {
    for (int i = 0; i < mouseEventListeners.size(); i++) {
      MouseEventListener meListener = (MouseEventListener)mouseEventListeners.elementAt(i);
      if (meListener.contains(e.getPoint())) {
        boolean refresh = meListener.mouseMoved(e);
        if (refresh)
          triggerRefresh(e.getWhen());
        return;
      }
    }
  }
  
  public void mouseDragged(MouseEvent e) {
    if ((focalObject != null) && ((focalObject instanceof MouseEventListener))) {
      boolean refresh = ((MouseEventListener)focalObject).mouseDragged(e);
      if (refresh) {
        triggerRefresh(e.getWhen());
      }
    }
  }
  
  public void addKeyEventListener(KeyEventListener keListener) {
    keyEventListeners.addElement(keListener);
  }
  
  public void removeKeyEventListener(KeyEventListener keListener) { keyEventListeners.remove(keListener); }
  
  public void keyTyped(KeyEvent e) {
    if ((focalObject != null) && ((focalObject instanceof KeyEventListener))) {
      boolean refresh = ((KeyEventListener)focalObject).keyTyped(e);
      if (refresh) {
        triggerRefresh(e.getWhen());
      }
    }
  }
  
  public void keyPressed(KeyEvent e) {
    if ((focalObject != null) && ((focalObject instanceof KeyEventListener))) {
      boolean refresh = ((KeyEventListener)focalObject).keyPressed(e);
      if (refresh)
        triggerRefresh(e.getWhen());
    }
  }
  
  public void keyReleased(KeyEvent e) {
    if ((focalObject != null) && ((focalObject instanceof KeyEventListener))) {
      boolean refresh = ((KeyEventListener)focalObject).keyReleased(e);
      if (refresh) {
        triggerRefresh(e.getWhen());
      }
    }
  }
  
  public void numberOfStencilsChanged(int newNumberOfStencils) {
    if (writeEnabled) {
      Stencil localStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    }
  }
  
  public void currentStencilChanged(int selectedStencil) {
    if (writeEnabled) {
      Stencil localStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    }
  }
  
  protected void setNewFocalObject(StencilObject newFocalObject)
  {
    focalObject = newFocalObject;
    if ((writeEnabled) && 
      ((newFocalObject instanceof Note))) {}
  }
  







  public boolean newTutorial()
  {
    if ((editorDirty) && (!handleNonSaved()))
      return false;
    loadStencilTutorial(null);
    tutorialEditor.update();
    return true;
  }
  
  public void insertNewStencil(boolean paramBoolean) { if ((stencilList != null) && (stencilList.size() > 0)) {
      Stencil localStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
      boolean bool = checkState(localStencil);
      if (!bool) {}
      localStencil.setCurrentStencil(false);
      currentStencilIndex += 1;
    }
    Stencil localStencil = new Stencil();
    stencilList.insertElementAt(localStencil, currentStencilIndex);
    createDefaultStencilObjects();
    localStencil.setCurrentStencil(true);
    broadcastStencilNumberChange();
    stencilChanged = true;
    stencilApp.makeWayPoint();
    editorDirty = paramBoolean;
    triggerRefresh();
  }
  
  public void addToCurrentStencil(StencilObject paramStencilObject) { if (paramStencilObject != null) {
      Stencil localStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
      localStencil.addObject(paramStencilObject);
    }
  }
  
  public void addStencilObject(Note paramNote, StencilObject paramStencilObject) { if (paramStencilObject != null) {
      if ((paramStencilObject instanceof MouseEventListener))
        addMouseEventListener((MouseEventListener)paramStencilObject);
      addLayoutChangeListener((LayoutChangeListener)paramStencilObject);
    }
    addMouseEventListener(paramNote);
    addKeyEventListener(paramNote);
    addToCurrentStencil(paramStencilObject);
    addToCurrentStencil(paramNote);
    setNewFocalObject(paramNote);
    announceLayoutChange();
    paramNote.initializeNote();
    editorDirty = true;
    triggerRefresh();
  }
  
  public Note createNewHole(Point paramPoint, boolean paramBoolean) { String str = stencilApp.getIDForPoint(paramPoint, false);
    Note localNote = null;
    if (str != null) {
      Hole localHole = new Hole(str, positionManager, stencilApp, this);
      localHole.setAutoAdvance(paramBoolean, 4);
      localNote = new Note(paramPoint, new Point(30, 30), localHole, positionManager, this, false);
      addStencilObject(localNote, localHole);
    }
    return localNote;
  }
  
  public Note createNewFrame(Point paramPoint, boolean paramBoolean) { String str = stencilApp.getIDForPoint(paramPoint, false);
    Note localNote = null;
    if (str != null) {
      Frame localFrame = new Frame(str, positionManager);
      localNote = new Note(paramPoint, new Point(30, 30), localFrame, positionManager, this, paramBoolean);
      addStencilObject(localNote, localFrame);
    }
    return localNote;
  }
  
  public Note createNewNote(Point paramPoint, boolean paramBoolean) { Note localNote = new Note(paramPoint, new Point(0, 0), null, positionManager, this, paramBoolean);
    addStencilObject(localNote, null);
    return localNote;
  }
  
  public void removeLastStencilObject() { Stencil localStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    localStencil.removeLastObject();
    stencilChanged = true;
    editorDirty = true;
  }
  
  public void removeAllObjectsFromCurrentStencil() { setNewFocalObject(null);
    Stencil localStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    localStencil.removeAllObjects();
    stencilChanged = true;
    editorDirty = true;
  }
  
  public void gotoStencil(int paramInt) { Stencil localStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
    if ((paramInt < stencilList.size()) && (paramInt != currentStencilIndex)) {
      boolean bool = checkState(localStencil);
      localStencil.setCurrentStencil(false);
      currentStencilIndex = paramInt;
      localStencil = (Stencil)stencilList.elementAt(currentStencilIndex);
      localStencil.setCurrentStencil(true);
      broadcastStencilNumberChange();
      stencilChanged = true;
      stencilApp.makeWayPoint();
    }
  }
  
  public void showStencilPanel(boolean paramBoolean) { stencilPanel.setIsDrawing(paramBoolean); }
  

  public void setTutorialWorld(String paramString) { worldToLoad = paramString; }
  
  public void regainFocus() {
    if ((stencilApp instanceof AuthoringTool))
      ((AuthoringTool)stencilApp).stencilManagerReFocus();
  }
  
  public void removeCurrStencil() { if (stencilList.size() > 1) {
      setNewFocalObject(null);
      ((Stencil)stencilList.get(currentStencilIndex)).setCurrentStencil(false);
      stencilList.remove(currentStencilIndex);
      if (currentStencilIndex == stencilList.size())
        currentStencilIndex -= 1;
      ((Stencil)stencilList.get(currentStencilIndex)).setCurrentStencil(true);
      broadcastStencilNumberChange();
      stencilChanged = true;
      stencilApp.makeWayPoint();
      editorDirty = true;
      triggerRefresh();
    } else {
      removeAllObjectsFromCurrentStencil();
    }
  }
  
  public boolean isWriteEnabled() { return writeEnabled; }
  
  public boolean handleNonSaved() {
    String str = "File not saved, save now?";
    int i = DialogManager.showConfirmDialog(str, "File not saved", 1);
    if (i == 0) {
      saveStencilsFile();
      return true;
    }
    return i == 1;
  }
  
  public boolean setInstructorMode(boolean paramBoolean) { if ((editorDirty) && (writeEnabled != paramBoolean) && (!paramBoolean))
      if (handleNonSaved()) {
        editorDirty = false;
      } else
        return false;
    setWriteEnabled(paramBoolean);
    tutorialEditor.update();
    tutorialEditor.display(paramBoolean);
    return true;
  }
  
  public boolean isInstructorMode() { return writeEnabled; }
  
  public void setCaptureFolder(File paramFile) {
    captureFolder = paramFile;
  }
  
  public void setCaptureMode(boolean paramBoolean) { captureMode = paramBoolean; }
  

  public class Stencil
  {
    public Stencil() {}
    
    protected Vector stencilObjects = new Vector();
    protected StateCapsule endStateCapsule = null;
    protected boolean error = false;
    protected int goBackSteps = 1;
    
    public void setSteps(int goBackSteps) {
      this.goBackSteps = goBackSteps;
    }
    
    public int getStepsToGoBack() {
      return goBackSteps;
    }
    
    public StateCapsule getEndState() {
      return endStateCapsule;
    }
    
    public void setEndState(StateCapsule stateCapsule) {
      endStateCapsule = stateCapsule;
    }
    
    public void write(Document document, Element element) {
      Element stencilElement = document.createElement("stencil");
      String stencilTitle = null;
      

      if (endStateCapsule != null) {
        Element stateNode = document.createElement("stateCapsule");
        org.w3c.dom.CDATASection stateDataSection = document.createCDATASection(endStateCapsule.getStorableRepr());
        stateNode.appendChild(stateDataSection);
        stencilElement.appendChild(stateNode);
      }
      for (int i = 0; i < stencilObjects.size(); i++) {
        StencilObject stencilObj = (StencilObject)stencilObjects.elementAt(i);
        if ((stencilObj instanceof Note)) {
          ((Note)stencilObj).write(document, stencilElement);
        } else if ((stencilObj instanceof NavigationBar)) {
          stencilTitle = ((NavigationBar)stencilObj).getTitleString();
        }
      }
      if (stencilTitle != null)
        stencilElement.setAttribute("title", stencilTitle);
      stencilElement.setAttribute("stepsToGoBack", Integer.toString(goBackSteps));
      element.appendChild(stencilElement);
    }
    
    public void setErrorStencil(boolean idError) {
      stencilChanged = true;
      error = idError;
      triggerRefresh(System.currentTimeMillis());
    }
    
    public void setCurrentStencil(boolean currentStencil) {
      if (currentStencil)
      {
        error = false;
        

        addAllListeners();
        
        for (int i = 0; i < stencilObjects.size(); i++) {
          StencilObject obj = (StencilObject)stencilObjects.elementAt(i);
          if ((obj instanceof Hole)) {
            boolean success = ((Hole)obj).layoutChanged();
            
            WaitAndUpdateThread godot = new WaitAndUpdateThread(1250L, this, (LayoutChangeListener)obj);
            godot.start();
          } else if ((obj instanceof Frame)) {
            boolean success = ((Frame)obj).layoutChanged();
            

            WaitAndUpdateThread godot = new WaitAndUpdateThread(1250L, this, (LayoutChangeListener)obj);
            godot.start();
          }
        }
        for (int i = 0; i < stencilObjects.size(); i++) {
          StencilObject obj = (StencilObject)stencilObjects.elementAt(i);
          
          if ((obj instanceof Note)) {
            ((Note)obj).updatePosition();
          }
        }
      } else {
        if ((error) && (errorStencil != null))
          errorStencil.removeAllListeners();
        removeAllListeners();
      }
    }
    
    public void addObject(StencilObject stencilObject) { stencilObjects.addElement(stencilObject);
      if ((stencilObject instanceof ReadWriteListener)) {
        addReadWriteListener((ReadWriteListener)stencilObject);
        ((ReadWriteListener)stencilObject).setWriteEnabled(writeEnabled);
      }
    }
    
    public void removeObject(StencilObject stencilObject) { stencilObjects.removeElement(stencilObject);
      if ((stencilObject instanceof ReadWriteListener))
        removeReadWriteListener((ReadWriteListener)stencilObject);
    }
    
    protected void addAllListeners() {
      for (int i = 0; i < stencilObjects.size(); i++)
      {
        StencilObject obj = (StencilObject)stencilObjects.elementAt(i);
        if ((obj instanceof MouseEventListener)) {
          addMouseEventListener((MouseEventListener)obj);
        }
        if ((obj instanceof KeyEventListener)) {
          addKeyEventListener((KeyEventListener)obj);
        }
        if ((obj instanceof StencilFocusListener)) {
          addStencilFocusListener((StencilFocusListener)obj);
        }
        if ((obj instanceof LayoutChangeListener)) {
          addLayoutChangeListener((LayoutChangeListener)obj);
        }
        if ((obj instanceof StencilStackChangeListener)) {
          addStencilStackChangeListener((StencilStackChangeListener)obj);
        }
        if ((obj instanceof StencilPanelMessageListener)) {
          stencilPanel.addMessageListener((StencilPanelMessageListener)obj);
        }
      }
    }
    
    protected void removeAllListeners() {
      for (int i = 0; i < stencilObjects.size(); i++)
      {
        StencilObject obj = (StencilObject)stencilObjects.elementAt(i);
        if ((obj instanceof MouseEventListener)) {
          removeMouseEventListener((MouseEventListener)obj);
        }
        if ((obj instanceof KeyEventListener)) {
          removeKeyEventListener((KeyEventListener)obj);
        }
        if ((obj instanceof StencilFocusListener)) {
          removeStencilFocusListener((StencilFocusListener)obj);
        }
        if ((obj instanceof LayoutChangeListener)) {
          removeLayoutChangeListener((LayoutChangeListener)obj);
        }
        if ((obj instanceof StencilStackChangeListener)) {
          removeStencilStackChangeListener((StencilStackChangeListener)obj);
        }
        if ((obj instanceof StencilPanelMessageListener)) {
          stencilPanel.removeMessageListener((StencilPanelMessageListener)obj);
        }
      }
    }
    
    public void removeAllObjects() {
      Vector newStencilObjects = new Vector();
      for (int i = 0; i < stencilObjects.size(); i++) {
        if (((stencilObjects.elementAt(i) instanceof Menu)) || ((stencilObjects.elementAt(i) instanceof NavigationBar)) || ((stencilObjects.elementAt(i) instanceof Link))) {
          newStencilObjects.addElement(stencilObjects.elementAt(i));
        } else {
          StencilObject obj = (StencilObject)stencilObjects.elementAt(i);
          if ((obj instanceof MouseEventListener)) {
            removeMouseEventListener((MouseEventListener)obj);
          }
          if ((obj instanceof KeyEventListener)) {
            removeKeyEventListener((KeyEventListener)obj);
          }
          if ((obj instanceof StencilFocusListener)) {
            removeStencilFocusListener((StencilFocusListener)obj);
          }
          if ((obj instanceof LayoutChangeListener)) {
            removeLayoutChangeListener((LayoutChangeListener)obj);
          }
          if ((obj instanceof StencilStackChangeListener)) {
            removeStencilStackChangeListener((StencilStackChangeListener)obj);
          }
          if ((obj instanceof StencilPanelMessageListener)) {
            stencilPanel.removeAllMessageListeners();
          }
        }
      }
      stencilObjects = newStencilObjects;
    }
    
    public Vector getObjects() { if (error)
      {

        if (errorStencil == null)
          errorStencil = stencilParser.getErrorStencil();
        errorStencil.addAllListeners();
        return errorStencil.getObjects();
      }
      return stencilObjects;
    }
    
    public String getTitle()
    {
      String str = "";
      for (int i = 0; i < stencilObjects.size(); i++) {
        StencilObject localStencilObject = (StencilObject)stencilObjects.elementAt(i);
        if ((localStencilObject instanceof NavigationBar)) {
          str = ((NavigationBar)localStencilObject).getTitleString();
          break;
        }
      }
      return str != null ? str : "";
    }
    
    public String getFirstNote() { String str = "";
      for (int i = 0; i < stencilObjects.size(); i++) {
        StencilObject localStencilObject = (StencilObject)stencilObjects.elementAt(i);
        if ((localStencilObject instanceof Note)) {
          str = ((Note)localStencilObject).getFirstNote();
          break;
        }
      }
      return str != null ? str : "";
    }
    
    public void removeLastObject() { if (stencilObjects.size() > 0)
        if (stencilObjects.size() == 1) {
          removeAllObjects();
        } else {
          StencilObject localStencilObject = (StencilObject)stencilObjects.get(stencilObjects.size() - 1);
          stencilObjects.removeElementAt(stencilObjects.size() - 1);
          if ((localStencilObject instanceof ReadWriteListener))
            removeReadWriteListener((ReadWriteListener)localStencilObject);
          localStencilObject = (StencilObject)stencilObjects.get(stencilObjects.size() - 1);
          if (((localStencilObject instanceof Frame)) || ((localStencilObject instanceof Hole))) {
            stencilObjects.removeElementAt(stencilObjects.size() - 1);
            if ((localStencilObject instanceof ReadWriteListener))
              removeReadWriteListener((ReadWriteListener)localStencilObject);
          }
        }
    }
  }
  
  protected class StencilFileFilter extends FileFilter {
    protected StencilFileFilter() {}
    
    public boolean accept(File pathname) {
      if (pathname.getName().endsWith(".stl"))
        return true;
      if (pathname.isDirectory()) {
        return true;
      }
      return false;
    }
    
    public String getDescription() {
      return "Stencil Files";
    }
  }
  
  protected class StencilMouseAdapter extends edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter {
    protected StencilMouseAdapter() {}
    
    protected void singleClickResponse(MouseEvent ev) { mouseClicked(ev); }
    
    protected void doubleClickResponse(MouseEvent ev)
    {
      mouseClicked(ev);
    }
    
    protected void mouseUpResponse(MouseEvent ev) {
      mouseReleased(ev);
    }
    
    protected void mouseDownResponse(MouseEvent ev) {
      mousePressed(ev);
    }
  }
}
