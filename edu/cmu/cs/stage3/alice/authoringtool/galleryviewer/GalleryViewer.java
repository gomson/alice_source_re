package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipFile;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GalleryViewer extends GroupingPanel
{
  private Package authoringToolPackage = Package.getPackage("edu.cmu.cs.stage3.alice.authoringtool");
  protected static final String FILENAME = "directoryIndex.xml";
  protected static final String FILENAME2 = "galleryIndex.xml";
  public static final int LOCAL = 1;
  public static final int WEB = 2;
  public static final int CD = 3;
  protected final Insets panelInset = new Insets(1, 2, 0, 0);
  public static final String webGalleryName = Messages.getString("Web_Gallery");
  public static final String localGalleryName = Messages.getString("Local_Gallery");
  public static final String cdGalleryName = Messages.getString("CD_Gallery");
  public static final String customGalleryName = Messages.getString("Custom_Gallery");
  public static boolean showBuilder = false;
  
  public static String webGalleryRoot;
  
  public static String localGalleryRoot;
  public static String cdGalleryRoot;
  public static String customGalleryRoot;
  protected static boolean alreadyEnteredWebGallery = false;
  

  public static final String homeName = Messages.getString("Home");
  protected static final Color backgroundColor = new Color(118, 128, 128);
  protected static final Color textColor = new Color(255, 255, 255);
  protected static final Color linkColor = new Color(153, 204, 255);
  protected static final String noModelsYet = Messages.getString("No_models_found_yet_");
  

  public static String cacheDir;
  
  protected RootDirectoryStructure webGallery = null;
  protected RootDirectoryStructure localGallery = null;
  protected RootDirectoryStructure cdGallery = null;
  protected RootDirectoryStructure customGallery = null;
  
  protected Vector currentGalleryObjects;
  
  protected GroupingPanel objectPanel;
  protected java.awt.FlowLayout objectPanelLayout;
  protected JPanel directoryPanel;
  protected JPanel searchPanel;
  protected Vector rootDirectories;
  protected DirectoryStructure directoryOnDisplay;
  protected DirectoryStructure searchResults;
  protected DirectoryStructure oldDirectoryOnDisplay;
  protected ImageIcon customGalleryIcon;
  protected ImageIcon webGalleryIcon;
  protected ImageIcon localGalleryIcon;
  protected ImageIcon cdGalleryIcon;
  protected ImageIcon add3DTextIcon;
  protected ObjectXmlData add3DTextData;
  protected TextBuilderButton add3DTextButton;
  protected JButton upLevelButton;
  public static ImageIcon noFolderImageIcon;
  public static ImageIcon loadingImageIcon;
  public static ImageIcon noImageIcon;
  protected boolean inBrowseMode;
  protected boolean stopSearch = true;
  
  protected class SearchingLabel extends JLabel
  {
    protected String doneString = Messages.getString("Ready_to_search");
    protected String searchingString1 = Messages.getString("Searching");
    protected String searchingString2 = Messages.getString("Searching__");
    protected String searchingString3 = Messages.getString("Searching____");
    protected String searchingString4 = Messages.getString("Searching______");
    protected int state = 0;
    
    public SearchingLabel()
    {
      setText(doneString);
      setFont(new java.awt.Font("Dialog", 1, 18));
      setForeground(Color.white);
    }
    
    public void advance() {
      state = ((state + 1) % 4);
      switch (state) {
      case 0:  setText(searchingString1); break;
      case 1:  setText(searchingString2); break;
      case 2:  setText(searchingString3); break;
      case 3:  setText(searchingString4);
      }
      repaint();
    }
    
    public void reset() {
      setText(doneString);
      repaint();
    }
  }
  
  protected SearchingLabel searchingProgressLabel = new SearchingLabel();
  protected String lastSearchString = "";
  protected String stopSearchString = Messages.getString("Stop_Search");
  protected String startSearchString = Messages.getString("Search");
  protected String startSearchWebString = Messages.getString("Search_www_alice_org");
  protected String webGalleryHostName = Messages.getString("www_alice_org");
  protected String searchString = Messages.getString("Browse_Gallery");
  protected String browseString = Messages.getString("Search_Gallery");
  protected JLabel attributeLabel;
  protected JPanel attributePanel;
  protected JTextField searchField;
  protected JButton searchButton;
  protected JButton searchWebButton;
  protected JButton searchBrowseButton;
  protected JPanel headerPanel;
  protected JLabel noObjectsLabel;
  protected JLabel noSearchResults;
  protected JLabel searching;
  protected GridBagConstraints glueConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 17, 2, new Insets(0, 0, 0, 0), 0, 0);
  
  protected Thread changingThread;
  protected Vector builderButtonsVector;
  protected static double bitsPerSecond = 0.0D;
  
  protected AuthoringTool authoringTool;
  protected static javax.swing.JFrame aliceFrame;
  protected ModelInfoContentPane modelContentPane;
  protected int searchCount = 0;
  protected boolean updatePanelsWhileLoading = false;
  protected boolean stopBuildingGallery = false;
  protected boolean isInWebGallery = false;
  protected boolean oldIsInWebGalleryValue = false;
  
  protected AliceCharacterFilter characterFilter = new AliceCharacterFilter();
  protected DirectoryFilter directoryFilter = new DirectoryFilter();
  protected ThumbnailFilter thumbnailFilter = new ThumbnailFilter();
  
  protected class AliceCharacterFilter implements java.io.FileFilter
  {
    private javax.swing.filechooser.FileFilter filter;
    
    public AliceCharacterFilter() {}
    
    public void setFilter(javax.swing.filechooser.FileFilter f) {
      filter = f;
    }
    
    public boolean accept(File fileToCheck) {
      if (filter != null) {
        if (!fileToCheck.isDirectory()) {
          return (filter.accept(fileToCheck)) || (fileToCheck.getName().endsWith(".link"));
        }
        
        return false;
      }
      

      if (!fileToCheck.isDirectory()) {
        return (fileToCheck.getName().endsWith(".a2c")) || (fileToCheck.getName().endsWith(".link"));
      }
      
      return false;
    }
  }
  
  protected class ThumbnailFilter implements java.io.FilenameFilter
  {
    public ThumbnailFilter() {}
    
    public boolean accept(File directory, String name) {
      if (name.equalsIgnoreCase("directoryThumbnail.png")) {
        return true;
      }
      return false;
    }
  }
  
  protected class DirectoryFilter implements java.io.FilenameFilter
  {
    public DirectoryFilter() {}
    
    public boolean accept(File directory, String name) {
      if (name.indexOf('.') == -1) {
        return true;
      }
      


      return false;
    }
  }
  
  public class ObjectXmlData
  {
    public String name;
    public int size = -1;
    public javax.vecmath.Vector3d dimensions = new javax.vecmath.Vector3d();
    public String objectFilename = null;
    public String imageFilename = null;
    public Vector details = new Vector();
    public Transferable transferable;
    public int type;
    public GalleryViewer mainViewer;
    public GalleryViewer.DirectoryStructure directoryData = null;
    public GalleryViewer.DirectoryStructure parentDirectory = null;
    public long timeStamp = 1L;
    public boolean isThere = false;
    
    public ObjectXmlData() {}
    
    public boolean equals(Object toCheck)
    {
      if ((toCheck instanceof ObjectXmlData)) {
        ObjectXmlData b = (ObjectXmlData)toCheck;
        boolean toReturn = (size == size) && (name.equals(name)) && (timeStamp == timeStamp);
        return toReturn;
      }
      return false;
    }
    
    public boolean matches(String toSearch) {
      StringTokenizer tokenizer = new StringTokenizer(toSearch.toUpperCase(), " ");
      while (tokenizer.hasMoreTokens()) {
        String current = tokenizer.nextToken();
        if (name.toUpperCase().indexOf(current) > -1) {
          return true;
        }
      }
      return false;
    }
    
    public void setDimensions(String dimensionString) {
      StringTokenizer tokenizer = new StringTokenizer(dimensionString, "x");
      String length = "";String width = "";String depth = "";
      if (tokenizer.hasMoreTokens()) {
        length = tokenizer.nextToken();
        while ((length.endsWith("m")) || (length.endsWith(" ")) || (length.endsWith("M"))) {
          length = length.substring(0, length.length() - 1);
        }
      }
      if (tokenizer.hasMoreTokens()) {
        width = tokenizer.nextToken();
        while ((width.endsWith("m")) || (width.endsWith(" ")) || (width.endsWith("M"))) {
          width = width.substring(0, width.length() - 1);
        }
      }
      if (tokenizer.hasMoreTokens()) {
        depth = tokenizer.nextToken();
        while ((depth.endsWith("m")) || (depth.endsWith(" ")) || (depth.endsWith("M"))) {
          depth = depth.substring(0, depth.length() - 1);
        }
      }
      try {
        double lengthVal = Double.parseDouble(length);
        double widthVal = Double.parseDouble(width);
        double depthVal = Double.parseDouble(depth);
        dimensions.set(lengthVal, widthVal, depthVal);
      }
      catch (Exception localException) {}
    }
    
    public void setSize(String sizeString)
    {
      char suffix = sizeString.charAt(sizeString.length() - 1);
      if ((suffix == 'm') || (suffix == 'M')) {
        try {
          float megSize = Float.parseFloat(sizeString.substring(0, sizeString.length() - 1));
          size = ((int)(megSize * 1000.0F));
        }
        catch (NumberFormatException e) {
          size = -1;
        }
        
      } else if ((sizeString.endsWith("mb")) || (sizeString.endsWith("Mb")) || (sizeString.endsWith("MB")) || (sizeString.endsWith("mB"))) {
        try {
          float megSize = Float.parseFloat(sizeString.substring(0, sizeString.length() - 2));
          size = ((int)(megSize * 1000.0F));
        }
        catch (NumberFormatException e) {
          size = -1;
        }
        
      } else if ((sizeString.endsWith("kb")) || (sizeString.endsWith("Kb")) || (sizeString.endsWith("KB")) || (sizeString.endsWith("kB"))) {
        try {
          size = ((int)Float.parseFloat(sizeString.substring(0, sizeString.length() - 2)));
        }
        catch (NumberFormatException e) {
          size = -1;
        }
        
      } else if ((suffix == 'k') || (suffix == 'K')) {
        try {
          size = ((int)Float.parseFloat(sizeString.substring(0, sizeString.length() - 1)));
        }
        catch (NumberFormatException e) {
          size = -1;
        }
      } else {
        try
        {
          size = ((int)Float.parseFloat(sizeString));
        }
        catch (NumberFormatException e) {
          size = -1;
        }
      }
    }
    

    public void incrementName() {}
    
    public void addDetail(String detailName, Object detailValue)
    {
      details.add(new StringObjectPair(detailName, detailValue));
    }
    
    public String getDetail(String toGet) {
      for (int i = 0; i < details.size(); i++) {
        StringObjectPair current = (StringObjectPair)details.get(i);
        if (current.getString().equalsIgnoreCase(toGet)) {
          if ((current.getObject() instanceof String)) {
            return (String)current.getObject();
          }
          
          String toReturn = "";
          if ((current.getObject() instanceof Vector)) {
            Vector allDetails = (Vector)current.getObject();
            for (int j = 0; j < allDetails.size(); j++) {
              String currentDetail = allDetails.get(j).toString();
              if (j < allDetails.size() - 1) {
                currentDetail = currentDetail + ", ";
              }
              toReturn = toReturn + currentDetail;
            }
            return toReturn;
          }
        }
      }
      
      return null;
    }
  }
  
  protected class DirectoryXmlData
  {
    public Vector directories = new Vector();
    public Vector models = new Vector();
    public String name = "directory";
    
    public DirectoryXmlData(String name) {
      this.name = name;
    }
    
    public void addDirectory(GalleryViewer.ObjectXmlData dir) {
      for (int i = 0; i < directories.size(); i++) {
        GalleryViewer.ObjectXmlData current = (GalleryViewer.ObjectXmlData)directories.get(i);
        if (current.equals(dir)) {
          return;
        }
      }
      directories.add(dir);
    }
    
    public void addDirectory(GalleryViewer.ObjectXmlData dir, int index) {
      for (int i = 0; i < directories.size(); i++) {
        GalleryViewer.ObjectXmlData current = (GalleryViewer.ObjectXmlData)directories.get(i);
        if (current.equals(dir)) {
          return;
        }
      }
      directories.insertElementAt(dir, index);
    }
    
    public void addModel(GalleryViewer.ObjectXmlData model) {
      for (int i = 0; i < models.size(); i++) {
        GalleryViewer.ObjectXmlData current = (GalleryViewer.ObjectXmlData)models.get(i);
        if (current.equals(model)) {
          return;
        }
      }
      models.add(model);
    }
    
    public void addModel(GalleryViewer.ObjectXmlData model, int index) {
      for (int i = 0; i < models.size(); i++) {
        GalleryViewer.ObjectXmlData current = (GalleryViewer.ObjectXmlData)models.get(i);
        if (current.equals(model)) {
          return;
        }
      }
      if ((index > -1) && (index <= models.size())) {
        models.insertElementAt(model, index);
      }
      else {
        models.add(model);
      }
    }
    


    public GalleryViewer.ObjectXmlData getModel(GalleryViewer.ObjectXmlData toGet)
    {
      for (int i = 0; i < models.size(); i++) {
        GalleryViewer.ObjectXmlData current = (GalleryViewer.ObjectXmlData)models.get(i);
        if (current.equals(toGet)) {
          return current;
        }
      }
      return null;
    }
    
    public GalleryViewer.DirectoryStructure getDirectory(int i) {
      if (directories != null) {
        return directories.get(i)).directoryData;
      }
      return null;
    }
    
    public GalleryViewer.ObjectXmlData getDirectory(GalleryViewer.ObjectXmlData toGet) {
      for (int i = 0; i < directories.size(); i++) {
        GalleryViewer.ObjectXmlData current = (GalleryViewer.ObjectXmlData)directories.get(i);
        if (current.equals(toGet)) {
          return current;
        }
      }
      return null;
    }
    
    public GalleryViewer.DirectoryStructure getDirectoryNamed(String toSearchFor) {
      if (directories != null) {
        for (int i = 0; i < directories.size(); i++) {
          String currentName = directories.get(i)).name;
          if (currentName.equals(toSearchFor)) {
            return directories.get(i)).directoryData;
          }
        }
      }
      return null;
    }
    
    public GalleryViewer.ObjectXmlData getDirectoryXMLNamed(String toSearchFor) {
      if (directories != null) {
        for (int i = 0; i < directories.size(); i++) {
          String currentName = directories.get(i)).name;
          if (currentName.equals(toSearchFor)) {
            return (GalleryViewer.ObjectXmlData)directories.get(i);
          }
        }
      }
      return null;
    }
  }
  
  protected class DirectoryStructure
  {
    public String name;
    public DirectoryStructure parent;
    public GalleryViewer.DirectoryXmlData xmlData;
    public DirectoryStructure firstLocalDirectory = null;
    public DirectoryStructure secondLocalDirectory = null;
    public String path;
    public DirectoryStructure directoryToUse;
    public GalleryViewer.ObjectXmlData data;
    public GalleryViewer.RootDirectoryStructure rootNode;
    
    public DirectoryStructure(GalleryViewer.RootDirectoryStructure root, String name, String path)
    {
      directoryToUse = this;
      rootNode = root;
      parent = null;
      this.name = name;
      this.path = path;
    }
    
    public boolean equals(Object toCompareTo) {
      if ((toCompareTo instanceof DirectoryStructure)) {
        DirectoryStructure b = (DirectoryStructure)toCompareTo;
        if ((name.equals(name)) && 
          (data != null) && (data != null)) {
          return data.timeStamp == data.timeStamp;
        }
      }
      
      return false;
    }
    
    public boolean contains(GalleryViewer.ObjectXmlData toSearchFor) {
      if (xmlData != null) {
        for (int i = 0; i < xmlData.models.size(); i++) {
          if (((GalleryViewer.ObjectXmlData)xmlData.models.get(i)).equals(toSearchFor)) {
            return true;
          }
        }
        for (int i = 0; i < xmlData.directories.size(); i++) {
          if (((GalleryViewer.ObjectXmlData)xmlData.directories.get(i)).equals(toSearchFor)) {
            return true;
          }
        }
      }
      return false;
    }
    
    public Vector getObjectMatches(String searchString) {
      Vector toReturn = null;
      if (xmlData != null) {
        toReturn = new Vector();
        for (int i = 0; i < xmlData.models.size(); i++) {
          if (((GalleryViewer.ObjectXmlData)xmlData.models.get(i)).matches(searchString)) {
            toReturn.add(xmlData.models.get(i));
          }
          if (stopSearch) {
            break;
          }
        }
      }
      return toReturn;
    }
    
    public GalleryViewer.ObjectXmlData getObjectNamed(String toSearchFor) {
      if (xmlData != null) {
        for (int i = 0; i < xmlData.models.size(); i++) {
          String currentName = xmlData.models.get(i)).name;
          if (currentName.equals(toSearchFor)) {
            return (GalleryViewer.ObjectXmlData)xmlData.models.get(i);
          }
        }
        for (int i = 0; i < xmlData.directories.size(); i++) {
          String currentName = xmlData.directories.get(i)).name;
          if (currentName.equals(toSearchFor)) {
            return (GalleryViewer.ObjectXmlData)xmlData.directories.get(i);
          }
        }
      }
      return null;
    }
    
    public GalleryViewer.ObjectXmlData getObjectFileNamed(String toSearchFor) {
      if (xmlData != null) {
        for (int i = 0; i < xmlData.models.size(); i++) {
          String currentName = xmlData.models.get(i)).parentDirectory.rootNode.rootPath + xmlData.models.get(i)).objectFilename;
          if (currentName.equals(toSearchFor)) {
            return (GalleryViewer.ObjectXmlData)xmlData.models.get(i);
          }
        }
        for (int i = 0; i < xmlData.directories.size(); i++) {
          String currentName = xmlData.directories.get(i)).parentDirectory.rootNode.rootPath + xmlData.directories.get(i)).objectFilename;
          if (currentName.equals(toSearchFor)) {
            return (GalleryViewer.ObjectXmlData)xmlData.directories.get(i);
          }
        }
      }
      return null;
    }
    
    public GalleryViewer.ObjectXmlData getModel(Transferable t) {
      if (xmlData != null) {
        for (int i = 0; i < xmlData.models.size(); i++) {
          if (t == xmlData.models.get(i)).transferable) {
            return (GalleryViewer.ObjectXmlData)xmlData.models.get(i);
          }
        }
      }
      return null;
    }
    
    public DirectoryStructure getDirectoryNamed(String toSearchFor) {
      if (xmlData != null) {
        return xmlData.getDirectoryNamed(toSearchFor);
      }
      return null;
    }
    
    public GalleryViewer.ObjectXmlData getDirectoryXMLNamed(String toSearchFor) {
      if (xmlData != null) {
        return xmlData.getDirectoryXMLNamed(toSearchFor);
      }
      return null;
    }
    
    public String getGUIPath() {
      DirectoryStructure parentDir = parent;
      String toReturn = new String(name);
      while (parentDir != null) {
        toReturn = name + File.separator + toReturn;
        parentDir = parent;
      }
      toReturn = GalleryViewer.homeName + File.separator + toReturn;
      return toReturn;
    }
    
    public DirectoryStructure getDirectory(int i) {
      if (xmlData != null) {
        return xmlData.getDirectory(i);
      }
      return null;
    }
    
    private DirectoryStructure initDirStructure(GalleryViewer.ObjectXmlData currentDirData) {
      if (currentDirData == null) {
        return null;
      }
      parentDirectory = this;
      DirectoryStructure currentDir = new DirectoryStructure(GalleryViewer.this, rootNode, name, objectFilename);
      if (firstLocalDirectory != null) {
        firstLocalDirectory = firstLocalDirectory.getDirectoryNamed(name);
        currentDir.equals(firstLocalDirectory);
      }
      

      if (secondLocalDirectory != null) {
        secondLocalDirectory = secondLocalDirectory.getDirectoryNamed(name);
        if (!currentDir.equals(secondLocalDirectory)) {}
      }
      

      data = currentDirData;
      directoryData = currentDir;
      parent = this;
      return currentDir;
    }
    
    private GalleryViewer.ObjectXmlData createObjectFromZip(File zipFileSource, DocumentBuilder builder) {
      ZipFile zipFile = null;
      try {
        zipFile = new ZipFile(zipFileSource);
      }
      catch (Exception e) {
        return null;
      }
      String xml = GalleryViewer.this.getXML(zipFile);
      GalleryViewer.ObjectXmlData currentModelData = null;
      if ((xml != null) && (xml != "")) {
        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(xml.getBytes());
        
        try
        {
          org.w3c.dom.Document document = builder.parse(bais);
          xmlRoot = document.getDocumentElement();
        }
        catch (IOException e) {
          org.w3c.dom.Element xmlRoot;
          return null;
        }
        catch (IllegalArgumentException e) {
          return null;
        }
        catch (SAXException e) {
          return null; }
        org.w3c.dom.Element xmlRoot;
        org.w3c.dom.Document document; NodeList xmlModels = xmlRoot.getElementsByTagName("model");
        Node currentModel = null;
        if (xmlModels.getLength() < 1) {
          currentModel = xmlRoot;
        }
        else {
          currentModel = xmlModels.item(0);
        }
        currentModelData = GalleryViewer.this.createObjectXmlData(currentModel, rootNode.rootPath, rootNode.type);
      }
      if (currentModelData == null) {
        currentModelData = GalleryViewer.this.createObjectXmlData(zipFileSource, rootNode.rootPath, rootNode.type);
      }
      else {
        String relativeFilename = GalleryViewer.this.getRelativeDirectory(rootNode.rootPath, zipFileSource.getAbsolutePath(), File.separator);
        objectFilename = (relativeFilename + zipFileSource.getName());
        imageFilename = objectFilename;
      }
      size = ((int)(zipFileSource.length() / 1000.0D));
      timeStamp = zipFileSource.lastModified();
      transferable = GalleryViewer.this.createFileTransferable(zipFileSource.getAbsolutePath());
      return currentModelData;
    }
    
    private void removeMissing(Vector toSearch) {
      int count = 0;
      while (count < toSearch.size()) {
        GalleryViewer.ObjectXmlData current = (GalleryViewer.ObjectXmlData)toSearch.get(count);
        if (!isThere) {
          toSearch.remove(count);
        }
        else {
          count++;
        }
      }
      for (int i = 0; i < toSearch.size(); i++) {
        getisThere = false;
      }
    }
    
    protected String getLinkPath(File linkFile) {
      try {
        BufferedReader fileReader = new BufferedReader(new java.io.FileReader(linkFile));
        char[] b = new char['Ϩ'];
        int numRead = fileReader.read(b);
        String content = new String(b, 0, numRead);
        while (numRead != -1) {
          numRead = fileReader.read(b);
          if (numRead != -1) {
            String newContent = new String(b, 0, numRead);
            content = content + newContent;
          }
        }
        
        return rootNode.rootPath + GalleryViewer.this.makeRelativePathReady(content);
      }
      catch (Exception localException) {}
      

      return null;
    }
    
    public int updateSelf(File dirFile) throws IOException, IllegalArgumentException, SAXException, ParserConfigurationException {
      if ((dirFile == null) || (!dirFile.isDirectory()) || (!dirFile.canRead())) {
        return -1;
      }
      File[] modelsInDir = dirFile.listFiles(characterFilter);
      File[] dirsInDir = dirFile.listFiles(directoryFilter);
      int total = modelsInDir.length + dirsInDir.length;
      glueConstraints.gridx = total;
      int count = 0;
      for (int dirIndex = 0; dirIndex < dirsInDir.length; dirIndex++) {
        if (stopBuildingGallery) {
          return -1;
        }
        GalleryViewer.ObjectXmlData currentDirData = getObjectFileNamed(dirsInDir[dirIndex].getAbsolutePath());
        if (currentDirData == null) {
          currentDirData = GalleryViewer.this.createDirectoryObjectXmlData(dirsInDir[dirIndex], rootNode.rootPath, rootNode.type);
          DirectoryStructure currentDir = initDirStructure(currentDirData);
          if ((currentDir != null) && (currentDirData != null)) {
            xmlData.addDirectory(directoryToUse.data, dirIndex);
            if (updatePanelsWhileLoading) {
              GalleryViewer.this.directoryAdded(directoryToUse.data, count);
              GalleryViewer.this.updateLoading(count / total);
            }
          }
        }
        else {
          count++;
        }
        if (currentDirData != null) {
          isThere = true;
        }
      }
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = null;
      try {
        builder = factory.newDocumentBuilder();
      }
      catch (ParserConfigurationException e) {
        return count;
      }
      for (int i = 0; i < modelsInDir.length; i++) {
        if (stopBuildingGallery) {
          return -1;
        }
        String absolutePath = modelsInDir[i].getAbsolutePath();
        File toAdd = modelsInDir[i];
        if (modelsInDir[i].getAbsolutePath().endsWith(".link")) {
          absolutePath = getLinkPath(modelsInDir[i]);
          if (absolutePath != null) {
            toAdd = new File(absolutePath);
          }
        }
        
        if (absolutePath != null) {
          GalleryViewer.ObjectXmlData currentModelData = getObjectFileNamed(absolutePath);
          if (currentModelData == null) {
            currentModelData = createObjectFromZip(toAdd, builder);
            if (currentModelData != null) {
              count++;
              parentDirectory = this;
              xmlData.addModel(currentModelData, i);
              if (updatePanelsWhileLoading) {
                GalleryViewer.this.modelAdded(currentModelData, count);
                GalleryViewer.this.updateLoading(count / total);
              }
            }
          }
          else {
            count++;
          }
          if (currentModelData != null) {
            isThere = true;
          }
        }
      }
      
      if ((GalleryViewer.showBuilder) && (name.equalsIgnoreCase("people")) && (updatePanelsWhileLoading) && (directoryOnDisplay != searchResults)) {
        for (int p = 0; p < builderButtonsVector.size(); p++) {
          final GenericBuilderButton builderButton = (GenericBuilderButton)builderButtonsVector.get(p);
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              objectPanel.add(builderButton);
              builderButton.updateGUI();
              objectPanel.repaint();
            }
          });
        }
      }
      if (name.startsWith(GalleryViewer.localGalleryName)) {
        count++;
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            objectPanel.add(add3DTextButton);
            add3DTextButton.updateGUI();
            objectPanel.repaint();
          }
        });
      }
      removeMissing(xmlData.directories);
      removeMissing(xmlData.models);
      return count;
    }
    
    public int initSelf(File dirFile) throws IOException, IllegalArgumentException, SAXException, ParserConfigurationException {
      if ((dirFile == null) || (!dirFile.isDirectory()) || (!dirFile.canRead())) {
        return -1;
      }
      xmlData = new GalleryViewer.DirectoryXmlData(GalleryViewer.this, name);
      File[] modelsInDir = dirFile.listFiles(characterFilter);
      File[] dirsInDir = dirFile.listFiles(directoryFilter);
      
      int total = modelsInDir.length + dirsInDir.length;
      glueConstraints.gridx = total;
      int count = 0;
      for (int i = 0; i < dirsInDir.length; i++) {
        if (stopBuildingGallery) {
          xmlData = null;
          return -1;
        }
        count++;
        GalleryViewer.ObjectXmlData currentDirData = GalleryViewer.this.createDirectoryObjectXmlData(dirsInDir[i], rootNode.rootPath, rootNode.type);
        DirectoryStructure currentDir = initDirStructure(currentDirData);
        if ((currentDir != null) && (currentDirData != null)) {
          xmlData.addDirectory(directoryToUse.data);
          if (updatePanelsWhileLoading) {
            GalleryViewer.this.directoryAdded(directoryToUse.data, count);
            GalleryViewer.this.updateLoading(count / total);
          }
        }
      }
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = null;
      try {
        builder = factory.newDocumentBuilder();
      }
      catch (ParserConfigurationException e) {
        return count;
      }
      for (int i = 0; i < modelsInDir.length; i++) {
        if (stopBuildingGallery) {
          xmlData = null;
          return -1;
        }
        File toAdd = modelsInDir[i];
        if (modelsInDir[i].getAbsolutePath().endsWith(".link")) {
          String absolutePath = getLinkPath(modelsInDir[i]);
          if (absolutePath != null) {
            toAdd = new File(absolutePath);
          }
        }
        GalleryViewer.ObjectXmlData currentModelData = createObjectFromZip(toAdd, builder);
        if (currentModelData != null) {
          count++;
          parentDirectory = this;
          xmlData.addModel(currentModelData);
          if (updatePanelsWhileLoading) {
            GalleryViewer.this.modelAdded(currentModelData, count);
            GalleryViewer.this.updateLoading(count / total);
          }
        }
      }
      if ((GalleryViewer.showBuilder) && (name.equalsIgnoreCase("people")) && (updatePanelsWhileLoading) && (directoryOnDisplay != searchResults)) {
        for (int p = 0; p < builderButtonsVector.size(); p++) {
          count++;
          final GenericBuilderButton builderButton = (GenericBuilderButton)builderButtonsVector.get(p);
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              objectPanel.add(builderButton);
              builderButton.updateGUI();
              objectPanel.repaint();
            }
          });
        }
      }
      if (name.startsWith(GalleryViewer.localGalleryName)) {
        count++;
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            objectPanel.add(add3DTextButton);
            add3DTextButton.updateGUI();
            objectPanel.repaint();
          }
        });
      }
      
      if (count == 0) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            objectPanel.removeAll();
            objectPanel.add(noObjectsLabel);
          }
        });
      }
      return count;
    }
    
    public int initSelf(String xml) throws IOException, IllegalArgumentException, SAXException, ParserConfigurationException {
      if (xml == null) {
        return -1;
      }
      
      xmlData = new GalleryViewer.DirectoryXmlData(GalleryViewer.this, name);
      xmlData.name = name;
      java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(xml.getBytes());
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      
      try
      {
        DocumentBuilder builder = factory.newDocumentBuilder();
        

        org.w3c.dom.Document document = builder.parse(bais);
        bais.close();
        

        xmlRoot = document.getDocumentElement();
      } catch (ParserConfigurationException e) {
        org.w3c.dom.Element xmlRoot;
        throw e;
      }
      catch (IOException e) {
        throw e;
      }
      catch (IllegalArgumentException e) {
        throw e;
      }
      catch (SAXException e) {
        throw e;
      }
      org.w3c.dom.Element xmlRoot;
      org.w3c.dom.Document document;
      NodeList xmlDirectories = xmlRoot.getElementsByTagName("directory");
      NodeList xmlModels = xmlRoot.getElementsByTagName("model");
      

      int total = xmlDirectories.getLength() + xmlModels.getLength();
      glueConstraints.gridx = total;
      int count = 0;
      for (int i = 0; i < xmlDirectories.getLength(); i++) {
        if (stopBuildingGallery) {
          xmlData = null;
          return -1;
        }
        count++;
        Node currentDirectory = xmlDirectories.item(i);
        GalleryViewer.ObjectXmlData currentDirData = GalleryViewer.this.createObjectXmlData(currentDirectory, rootNode.rootPath, rootNode.type);
        if (currentDirData != null)
        {

          parentDirectory = this;
          DirectoryStructure currentDir = new DirectoryStructure(GalleryViewer.this, rootNode, name, objectFilename);
          if (firstLocalDirectory != null) {
            firstLocalDirectory = firstLocalDirectory.getDirectoryNamed(name);
            currentDir.equals(firstLocalDirectory);
          }
          

          if (secondLocalDirectory != null) {
            secondLocalDirectory = secondLocalDirectory.getDirectoryNamed(name);
            if (!currentDir.equals(secondLocalDirectory)) {}
          }
          

          data = currentDirData;
          directoryData = currentDir;
          parent = this;
          if ((currentDir != null) && (currentDirData != null)) {
            xmlData.addDirectory(directoryToUse.data);
            if (updatePanelsWhileLoading) {
              GalleryViewer.this.directoryAdded(directoryToUse.data, count);
              GalleryViewer.this.updateLoading(count / total);
            }
          }
        }
      }
      
      for (int i = 0; i < xmlModels.getLength(); i++) {
        if (stopBuildingGallery) {
          xmlData = null;
          return -1;
        }
        count++;
        Node currentModel = xmlModels.item(i);
        GalleryViewer.ObjectXmlData currentModelData = GalleryViewer.this.createObjectXmlData(currentModel, rootNode.rootPath, rootNode.type);
        if (currentModelData != null) {
          parentDirectory = this;
          xmlData.addModel(currentModelData);
          if (updatePanelsWhileLoading) {
            GalleryViewer.this.modelAdded(currentModelData, count);
            GalleryViewer.this.updateLoading(count / total);
          }
        }
      }
      

      return count;
    }
  }
  
  protected class RootDirectoryStructure {
    public String rootPath;
    public int type;
    public GalleryViewer.DirectoryStructure directory;
    public GalleryViewer.ObjectXmlData xmlData;
    
    public RootDirectoryStructure(String rootPath, int type, GalleryViewer.DirectoryStructure directory, GalleryViewer.ObjectXmlData xmlData) {
      this.xmlData = xmlData;
      this.rootPath = rootPath;
      this.type = type;
      this.directory = directory;
    }
    
    public GalleryViewer.ObjectXmlData getObject(String toGet) {
      toGet.indexOf(rootPath);
      
      return null;
    }
  }
  
  public void setAuthoringTool(AuthoringTool authoringTool)
  {
    this.authoringTool = authoringTool;
    characterFilter.setFilter(this.authoringTool.getCharacterFileFilter());
    add3DTextButton.set(add3DTextData, add3DTextIcon, authoringTool);
  }
  
  public boolean shouldShowWebWarning() {
    return edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getValue(authoringToolPackage, "showWebWarningDialog").equalsIgnoreCase("true");
  }
  
  public GalleryViewer() {
    showBuilder = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getValue(authoringToolPackage, "showBuilderMode").equalsIgnoreCase("true");
    URL mainWebGalleryURL = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getMainWebGalleryURL();
    if (mainWebGalleryURL != null) {
      webGalleryHostName = mainWebGalleryURL.getHost();
      startSearchWebString = (Messages.getString("Search_") + webGalleryHostName);
    }
    File mainLocalGalleryFile = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getMainDiskGalleryDirectory();
    File mainCDGalleryFile = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getMainCDGalleryDirectory();
    File mainCustomGalleryFile = new File(edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getValue(authoringToolPackage, "directories.charactersDirectory"));
    
    cacheDir = File.separator + "webGalleryCache" + File.separator;
    File testDir = new File(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceUserDirectory(), cacheDir);
    if (!testDir.exists()) {
      testDir.mkdirs();
    }
    

    rootDirectories = new Vector();
    inBrowseMode = true;
    guiInit();
    
    String[] file = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getValueList(authoringToolPackage, "directories.galleryDirectory");
    RootDirectoryStructure templocal = createDirectory(new File(mainLocalGalleryFile.getAbsolutePath() + System.getProperty("file.separator") + "Core"), localGalleryName + " (Core)", 1);
    for (int i = 0; i < file.length; i++) {
      File temp = new File(mainLocalGalleryFile.getAbsolutePath() + System.getProperty("file.separator") + file[i]);
      localGallery = createDirectory(temp, localGalleryName + " (" + file[i] + ")", 1);
      if (localGallery != null) {
        rootDirectories.add(localGallery);
        localGalleryRoot = localGallery.rootPath;
        if (edu.cmu.cs.stage3.alice.authoringtool.AikMin.locale.equalsIgnoreCase(file[i])) {
          templocal = localGallery;
        }
      }
    }
    if (templocal != null) {
      localGallery = templocal;
    }
    cdGallery = createDirectory(mainCDGalleryFile, cdGalleryName, 3);
    if (cdGallery != null) {
      rootDirectories.add(cdGallery);
      cdGalleryRoot = cdGallery.rootPath;
    }
    webGallery = createDirectory(mainWebGalleryURL, webGalleryName, "galleryIndex.xml", false);
    if (webGallery != null) {
      rootDirectories.add(webGallery);
      webGalleryRoot = webGallery.rootPath;
    }
    else {
      webGallery = createDirectory(mainWebGalleryURL, webGalleryName, "directoryIndex.xml", false);
      if (webGallery != null) {
        rootDirectories.add(webGallery);
        webGalleryRoot = webGallery.rootPath;
      }
    }
    customGallery = createDirectory(mainCustomGalleryFile, customGalleryName, 1);
    if (customGallery != null) {
      rootDirectories.add(customGallery);
      customGalleryRoot = customGallery.rootPath;
    }
    if (localGallery != null) {
      webGallery.directory.firstLocalDirectory = localGallery.directory;
    }
    if (cdGallery != null) {
      webGallery.directory.secondLocalDirectory = cdGallery.directory;
    }
    if (inBrowseMode) {
      if (localGallery != null) {
        directoryOnDisplay = localGallery.directory;
      }
      else {
        directoryOnDisplay = null;
      }
    }
    else {
      directoryOnDisplay = searchResults;
    }
    
    searchResults = new DirectoryStructure(null, Messages.getString("Search"), null);
    searchResults.xmlData = new DirectoryXmlData(Messages.getString("Search"));
    
    refreshGUI();
  }
  
  private GalleryObject getGalleryObject(java.awt.Component c) {
    if ((c instanceof GalleryObject)) {
      return (GalleryObject)c;
    }
    if (c == null) {
      return null;
    }
    return getGalleryObject(c.getParent());
  }
  
  private String getRelativeDirectory(String root, String filename, String separator) {
    int split = filename.indexOf(root);
    String toReturn = null;
    if (split >= 0) {
      toReturn = filename.substring(split + root.length());
      split = toReturn.lastIndexOf(separator);
      if ((split >= 0) && (split < toReturn.length() - 1)) {
        toReturn = toReturn.substring(0, split) + separator;
      }
      else {
        toReturn = "";
      }
    }
    return toReturn;
  }
  
  private String removeRootFromDirectory(String root, String filename) {
    int split = filename.indexOf(root);
    String toReturn = null;
    if (split >= 0) {
      toReturn = filename.substring(split + root.length());
    }
    return toReturn;
  }
  
  private String getFilename(String filename, String separator) {
    int split = filename.lastIndexOf(separator);
    String toReturn = null;
    if (split >= 0) {
      toReturn = filename.substring(split, filename.length());
    }
    return toReturn;
  }
  


  public void saveModel(edu.cmu.cs.stage3.alice.core.Element toSave, Transferable transferable)
  {
    ObjectXmlData objectToAdd = null;
    java.awt.Image image = null;
    for (int i = 0; i < objectPanel.getComponentCount(); i++) {
      if ((objectPanel.getComponent(i) instanceof GalleryObject)) {
        GalleryObject currentObject = (GalleryObject)objectPanel.getComponent(i);
        if (data.transferable == transferable) {
          objectToAdd = data;
          image = image.getImage();
          if (type == 2) break;
          return;
        }
      }
    }
    

    String path = null;
    if (objectToAdd != null) {
      path = objectFilename;
      int split = path.lastIndexOf('/');
      path = path.substring(0, split);
    }
    else {
      try {
        if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor)) {
          URL url = (URL)transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor);
          if (url != null) {
            path = getRelativeDirectory(webGalleryRoot, url.toString(), "/");
            path = webGalleryName + File.separator + reverseWebReady(path);
          }
        }
      }
      catch (Exception e) {
        return;
      }
      if (path != null) {
        DirectoryStructure dirOwner = getDirectoryStructure(path);
        if (dirOwner != null) {
          objectToAdd = dirOwner.getModel(transferable);
          if (objectToAdd != null) {
            if (type == 2) {
              image = WebGalleryObject.retrieveImage(webGalleryRoot, imageFilename, timeStamp);
            }
            else {
              return;
            }
          }
        }
      }
    }
    if (objectToAdd != null) {
      String localFilename = reverseWebReady(objectFilename);
      String baseFilename = localFilename.substring(0, localFilename.length() - 3);
      String xmlFilename = objectFilename.substring(0, objectFilename.length() - 3) + "xml";
      String pngFilename = baseFilename + "png";
      String a2cFilename = baseFilename + "a2c";
      GalleryObject.storeThumbnail(localGalleryRoot + pngFilename, image, timeStamp);
      getXML(parentDirectory.rootNode.rootPath, xmlFilename, type, -1L, localGalleryRoot, true);
      File objectFile = createFile(localGalleryRoot + a2cFilename);
      if (objectFile != null) {
        try {
          toSave.store(objectFile);
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    }
  }
  
  protected void addObject(edu.cmu.cs.stage3.alice.core.Model toAdd) {
    if (authoringTool != null) {
      authoringTool.getUndoRedoStack().startCompound();
      authoringTool.addCharacter(toAdd, null);
      authoringTool.getUndoRedoStack().stopCompound();
    }
  }
  
  protected void addObject(File toAdd) {
    if (authoringTool != null) {
      authoringTool.loadAndAddCharacter(toAdd);
    }
  }
  
  protected void addObject(URL toAdd) {
    if (authoringTool != null) {
      authoringTool.loadAndAddCharacter(toAdd);
    }
  }
  
  private DirectoryStructure getRootDirectoryNamed(String dirName) {
    for (int i = 0; i < rootDirectories.size(); i++) {
      RootDirectoryStructure current = (RootDirectoryStructure)rootDirectories.get(i);
      if (directory.name.equalsIgnoreCase(dirName)) {
        return directory;
      }
    }
    return null;
  }
  
  public String getDirectory() {
    if (directoryOnDisplay == null) {
      return homeName;
    }
    
    return directoryOnDisplay.getGUIPath();
  }
  
  protected void displayModelDialog(ObjectXmlData data, ImageIcon image)
  {
    if (modelContentPane == null) {
      modelContentPane = new ModelInfoContentPane();
    }
    modelContentPane.set(data, image);
    int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(modelContentPane);
    if (result == 0) {
      if (type != 2) {
        File file = new File(modelContentPane.getFilename());
        addObject(file);
      } else {
        int returnVal;
        do {
          try { URL url = new URL(modelContentPane.getFilename());
            addObject(url);
          }
          catch (Exception exception) {
            Object[] options = { Messages.getString("Retry"), 
              Messages.getString("Cancel") };
            returnVal = edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog(
              Messages.getString("Alice_can_t_reach_the_web_gallery__Your_computer_may_not_be_connected_to_the_internet_properly_"), 
              Messages.getString("Internet_Connection_Error"), 
              0, 
              2, 
              null, 
              options, 
              options[1]);
          } } while (returnVal != 0);
      }
    }
  }
  






  protected DirectoryStructure getDirectoryStructure(String pathToSet)
  {
    if (pathToSet == null) {
      return null;
    }
    StringTokenizer token = new StringTokenizer(pathToSet, File.separator);
    boolean isFirst = true;
    DirectoryStructure currentDirToSet = null;
    while (token.hasMoreTokens()) {
      String current = token.nextToken();
      if ((current != null) && (!current.equalsIgnoreCase("")) && (!current.equalsIgnoreCase(" "))) {
        if (isFirst) {
          isFirst = false;
          if (current.equalsIgnoreCase(homeName)) {
            currentDirToSet = null;
            isFirst = true;
          }
          else if (current.equalsIgnoreCase(webGalleryName)) {
            currentDirToSet = getRootDirectoryNamed(webGalleryName);
          }
          else if (current.startsWith(localGalleryName)) {
            currentDirToSet = getRootDirectoryNamed(localGalleryName + " (" + edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getValue(authoringToolPackage, "language") + ")");
            if (currentDirToSet == null) currentDirToSet = getRootDirectoryNamed(localGalleryName + " (Core)");
          }
          else if (current.equalsIgnoreCase(cdGalleryName)) {
            currentDirToSet = getRootDirectoryNamed(cdGalleryName);
          }
          else {
            return null;
          }
          
        }
        else if (currentDirToSet != null) {
          currentDirToSet = currentDirToSet.getDirectoryNamed(current);
        }
        else {
          return null;
        }
      }
    }
    
    return currentDirToSet;
  }
  
  public void setDirectory(String pathToSet) {
    DirectoryStructure d = null;
    if (edu.cmu.cs.stage3.alice.authoringtool.AikMin.isWindows()) {
      d = getDirectoryStructure(pathToSet);
    } else {
      pathToSet = pathToSet.replace("\\", "/");
      d = getDirectoryStructure(pathToSet);
    }
    if (d == null) {
      AuthoringTool.showErrorDialog(Messages.getString("Error_changing_gallery_viewer_to_") + pathToSet, null);
      return;
    }
    changeDirectory(d);
  }
  
  private RootDirectoryStructure createDirectory(URL root, String name, String rootFilename, boolean initXML) {
    String path = getRootString(root);
    if (path == null) {
      return null;
    }
    rootFilename = makeWebRelativePathReady(rootFilename);
    ObjectXmlData data = new ObjectXmlData();
    name = name;
    type = 2;
    objectFilename = rootFilename;
    size = -1;
    mainViewer = this;
    transferable = new java.awt.datatransfer.StringSelection(name);
    RootDirectoryStructure toReturn = new RootDirectoryStructure(path, 2, null, data);
    DirectoryStructure dirStruct = new DirectoryStructure(toReturn, name, rootFilename);
    if (initXML) {
      try {
        String xml = getXML(path, rootFilename, 2, getURLTimeStamp(path + rootFilename), cacheDir, false);
        if (xml == null) {
          return null;
        }
        dirStruct.initSelf(xml);
      }
      catch (Exception e) {
        return null;
      }
    }
    directoryData = dirStruct;
    directory = dirStruct;
    return toReturn;
  }
  
  private RootDirectoryStructure createDirectory(File root, String name, int type) {
    if ((root == null) || (!root.exists()) || (!root.isDirectory()) || (!root.canRead())) {
      return null;
    }
    String path = getRootString(root);
    ObjectXmlData data = new ObjectXmlData();
    name = name;
    objectFilename = "";
    size = -1;
    type = type;
    mainViewer = this;
    transferable = new java.awt.datatransfer.StringSelection(name);
    RootDirectoryStructure toReturn = new RootDirectoryStructure(path, type, null, data);
    DirectoryStructure dirStruct = null;
    try {
      dirStruct = new DirectoryStructure(toReturn, name, "");
      dirStruct.initSelf(root);
    }
    catch (Exception e) {
      return null;
    }
    directoryData = dirStruct;
    directory = dirStruct;
    return toReturn;
  }
  
  private String getRootString(File file) {
    if (file == null) {
      return null;
    }
    String toReturn = file.getAbsolutePath();
    if (!toReturn.endsWith(File.separator)) {
      toReturn = toReturn + File.separator;
    }
    return toReturn;
  }
  
  private String getRootString(URL url) {
    if (url == null) {
      return null;
    }
    String toReturn = url.toString();
    if (!toReturn.endsWith("/")) {
      toReturn = toReturn + "/";
    }
    return toReturn;
  }
  
  protected static synchronized void setDownloadRate(long time, int bytes) {
    double newRate = bytes / (time * 0.001D);
    if (bitsPerSecond != 0.0D) {
      bitsPerSecond = bitsPerSecond * 0.8D + newRate * 0.2D;
    }
    else {
      bitsPerSecond = newRate;
    }
  }
  
  public static File createFile(String filename) {
    int nameSplit = filename.lastIndexOf(File.separator);
    if (File.separator.equals("\\")) {
      int split2 = filename.lastIndexOf("/");
      if (split2 > nameSplit) {
        nameSplit = split2;
      }
    }
    else {
      int split2 = filename.lastIndexOf("\\");
      if (split2 > nameSplit) {
        nameSplit = split2;
      }
    }
    String parentDir = filename.substring(0, nameSplit + 1);
    File cacheFile = new File(filename);
    if (!cacheFile.exists()) {
      try {
        File parentDirFile = new File(parentDir);
        if (!parentDirFile.exists()) {
          parentDirFile.mkdirs();
        }
        cacheFile.createNewFile();
      }
      catch (Exception e) {
        return null;
      }
    }
    return cacheFile;
  }
  
  protected long getURLTimeStamp(String urlString) {
    long toReturn = 0L;
    try {
      URL url = new URL(urlString);
      toReturn = url.openConnection().getLastModified();
    }
    catch (Exception localException) {}
    
    return toReturn;
  }
  
  private String getXML(String root, String relativeFile, int type, final long sourceTimeStamp, String cacheDirectory, boolean forceCache) {
    String xmlTemp = null;
    String cacheFilenameTemp = null;
    boolean needToCache = true;
    if ((type == 1) || (type == 3)) {
      File file = new File(root + relativeFile);
      if ((file.exists()) && (file.canRead())) {
        xmlTemp = getXML(file);
        cacheFilenameTemp = cacheDirectory + relativeFile;
        needToCache = false;
      }
      else {
        AuthoringTool.showErrorDialog(Messages.getString("Error_accessing_the_local_gallery__") + file.getAbsolutePath() + " " + Messages.getString("is_either_not_there_or_can_not_be_read"), null);
        return null;
      }
    }
    else {
      cacheFilenameTemp = cacheDirectory + reverseWebReady(relativeFile);
      File cachedCopy = new File(cacheFilenameTemp);
      long cacheTimeStamp = -2L;
      if ((cachedCopy.exists()) && (cachedCopy.canRead())) {
        cacheTimeStamp = cachedCopy.lastModified();
        if (cacheTimeStamp == sourceTimeStamp) {
          xmlTemp = getXML(cachedCopy);
          needToCache = false;
        }
      }
      

      URL url = null;
      int returnVal;
      do {
        try { url = new URL(root + relativeFile);
          xmlTemp = getXML(url);
        }
        catch (Exception e) {
          xmlTemp = null;
        }
        if (xmlTemp != null) break;
        Object[] options = { Messages.getString("Retry"), 
          Messages.getString("Cancel") };
        returnVal = edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog(Messages.getString("Alice_can_t_reach_the_web_gallery__Your_computer_may_not_be_connected_to_the_internet_properly_"), 
          Messages.getString("Internet_Connection_Error"), 
          0, 
          2, 
          null, 
          options, 
          options[1]);
      } while (returnVal == 0);
      return null;
    }
    




    final String xml = xmlTemp;
    final String cacheFilename = cacheFilenameTemp;
    if ((needToCache) || (forceCache)) {
      Runnable doStore = new Runnable() {
        public void run() {
          File cacheFile = GalleryViewer.createFile(cacheFilename);
          if (cacheFile != null) {
            try {
              java.io.FileOutputStream fos = new java.io.FileOutputStream(cacheFile);
              java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(fos);
              java.io.BufferedWriter bw = new java.io.BufferedWriter(osw);
              bw.write(xml);
              bw.flush();
              osw.close();
              cacheFile.setLastModified(sourceTimeStamp);

            }
            catch (Exception localException) {}
          }
        }
      };
      Thread t = new Thread(doStore);
      t.start();
    }
    return xml;
  }
  
  private String getXML(ZipFile zipFile) {
    String content = "";
    try {
      java.util.zip.ZipEntry entry = zipFile.getEntry("galleryData.xml");
      if (entry != null) {
        java.io.InputStream stream = zipFile.getInputStream(entry);
        BufferedReader fileReader = new BufferedReader(new java.io.InputStreamReader(stream));
        char[] b = new char['Ϩ'];
        int numRead = fileReader.read(b);
        content = new String(b, 0, numRead);
        while (numRead != -1) {
          numRead = fileReader.read(b);
          if (numRead != -1) {
            String newContent = new String(b, 0, numRead);
            content = content + newContent;
          }
        }
      }
      zipFile.close();
    }
    catch (Exception e) {
      return null;
    }
    return content;
  }
  
  private String getXML(File file) {
    String content = "";
    try {
      java.io.FileReader fileReader = new java.io.FileReader(file);
      char[] b = new char['Ϩ'];
      int numRead = fileReader.read(b);
      content = new String(b, 0, numRead);
      while (numRead != -1) {
        numRead = fileReader.read(b);
        if (numRead != -1) {
          String newContent = new String(b, 0, numRead);
          content = content + newContent;
        }
      }
      fileReader.close();
    }
    catch (IOException e) {
      return null;
    }
    return content;
  }
  
  private String getXML(URL url) {
    String content = "";
    try {
      java.io.InputStream urlStream = url.openStream();
      java.io.BufferedInputStream bufis = new java.io.BufferedInputStream(urlStream);
      byte[] b = new byte['Ϩ'];
      int numRead = bufis.read(b);
      content = new String(b, 0, numRead);
      while (numRead != -1) {
        numRead = bufis.read(b);
        if (numRead != -1) {
          String newContent = new String(b, 0, numRead);
          content = content + newContent;
        }
      }
      urlStream.close();
    }
    catch (java.net.MalformedURLException e) {
      return null;
    }
    catch (Exception e) {
      return null;
    }
    return content;
  }
  
  private Transferable createURLTransferable(String filename) {
    URL toReturn = null;
    try {
      URL url = new URL(filename);
      if (!url.getProtocol().equalsIgnoreCase("http")) {
        return null;
      }
      java.net.HttpURLConnection connection = (java.net.HttpURLConnection)url.openConnection();
      int response = connection.getResponseCode();
      if (response != 200) {
        return null;
      }
      connection.disconnect();
      return new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable(url);
    }
    catch (java.net.MalformedURLException e)
    {
      return null;
    }
    catch (IOException e) {}
    return null;
  }
  
  private Transferable createFileTransferable(String filename)
  {
    File fileToTransfer = new File(filename);
    if ((fileToTransfer.exists()) && (fileToTransfer.canRead())) {
      java.util.ArrayList list = new java.util.ArrayList(1);
      list.add(fileToTransfer);
      return new edu.cmu.cs.stage3.alice.authoringtool.datatransfer.FileListTransferable(list);
    }
    
    return null;
  }
  
  protected static void enteredWebGallery()
  {
    alreadyEnteredWebGallery = true;
  }
  
  public void switchMode() {
    if (inBrowseMode) {
      searchBrowseButton.setText(searchString);
      headerPanel.remove(directoryPanel);
      headerPanel.add(searchPanel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(0, 0, 0, 0), 0, 0));
      oldDirectoryOnDisplay = directoryOnDisplay;
      directoryOnDisplay = searchResults;
    }
    else {
      searchBrowseButton.setText(browseString);
      headerPanel.remove(searchPanel);
      stopSearch = true;
      isInWebGallery = oldIsInWebGalleryValue;
      headerPanel.add(directoryPanel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(0, 0, 0, 0), 0, 0));
      directoryOnDisplay = oldDirectoryOnDisplay;
    }
    inBrowseMode = (!inBrowseMode);
    refreshGUI();
  }
  
  private void guiInit() {
    setBackground(backgroundColor);
    objectPanel = new GroupingPanel();
    objectPanel.setBorder(null);
    objectPanel.setBackground(backgroundColor);
    objectPanelLayout = new java.awt.FlowLayout();
    objectPanelLayout.setAlignment(0);
    objectPanelLayout.setVgap(0);
    objectPanelLayout.setHgap(1);
    objectPanel.setLayout(objectPanelLayout);
    
    directoryPanel = new JPanel();
    directoryPanel.setLayout(new java.awt.GridBagLayout());
    directoryPanel.setOpaque(false);
    

    attributeLabel = new JLabel(" ");
    attributeLabel.setForeground(textColor);
    
    attributePanel = new JPanel();
    attributePanel.setOpaque(false);
    attributePanel.setLayout(new java.awt.GridBagLayout());
    
    attributePanel.add(attributeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(0, 0, 0, 0), 0, 0));
    
    searchBrowseButton = new JButton(browseString);
    searchBrowseButton.setBackground(new Color(240, 240, 255));
    searchBrowseButton.setMargin(new Insets(2, 2, 2, 2));
    searchBrowseButton.setMinimumSize(new Dimension(100, 26));
    
    if (!inBrowseMode) {
      searchBrowseButton.setText(searchString);
    }
    searchBrowseButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        switchMode();
      }
      
    });
    searchField = new JTextField(15);
    searchField.setMinimumSize(new Dimension(200, 26));
    searchField.setPreferredSize(new Dimension(200, 26));
    searchField.setMaximumSize(new Dimension(200, 26));
    
    searchField.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        searchGallery(null, searchField.getText(), false);
      }
      
    });
    searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      public void insertUpdate(DocumentEvent ev) {
        searchWebButton.setEnabled(false);
      }
      
      public void removeUpdate(DocumentEvent ev) { searchWebButton.setEnabled(false); }
      
      private void updateHeightTextField() {
        searchWebButton.setEnabled(false);
      }
      
      public void changedUpdate(DocumentEvent ev) {
        searchWebButton.setEnabled(false);
      }
      
    });
    searchButton = new JButton(startSearchString);
    searchButton.setBackground(new Color(240, 240, 255));
    searchButton.setMargin(new Insets(2, 2, 2, 2));
    


    searchButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!stopSearch) {
          stopSearch = true;
          isInWebGallery = oldIsInWebGalleryValue;
          searchButton.setText(startSearchString);
        }
        else {
          searchGallery(null, searchField.getText(), false);
        }
      }
    });
    searchWebButton = new JButton(startSearchWebString);
    searchWebButton.setBackground(new Color(240, 240, 255));
    searchWebButton.setMargin(new Insets(2, 2, 2, 2));
    


    searchWebButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!stopSearch) {
          stopSearch = true;
          isInWebGallery = oldIsInWebGalleryValue;
          searchWebButton.setText(startSearchWebString);

        }
        else if ((!GalleryViewer.alreadyEnteredWebGallery) && (shouldShowWebWarning())) {
          int dialogVal = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(Messages.getString("You_are_about_to_search_the_online_gallery__This_is_accessed_through_the_internet_n") + 
            Messages.getString("and_is_potentially_slow_depending_on_your_connection_"), Messages.getString("Web_gallery_may_be_slow"), 2);
          if (dialogVal == 0) {
            GalleryViewer.enteredWebGallery();
            searchGallery(webGallery.directory, searchField.getText(), true);
          }
        } else {
          searchGallery(webGallery.directory, searchField.getText(), true);
        }
        
      }
    });
    searchWebButton.setEnabled(false);
    searchPanel = new JPanel();
    searchPanel.setOpaque(false);
    searchPanel.setBorder(null);
    searchPanel.setLayout(new java.awt.GridBagLayout());
    searchPanel.add(searchField, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(2, 4, 0, 8), 0, 0));
    searchPanel.add(searchButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(2, 0, 0, 0), 0, 0));
    searchPanel.add(searchWebButton, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(2, 4, 0, 0), 0, 0));
    searchPanel.add(searchingProgressLabel, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(2, 4, 0, 0), 0, 0));
    searchPanel.add(javax.swing.Box.createHorizontalGlue(), new GridBagConstraints(4, 0, 1, 1, 1.0D, 1.0D, 18, 1, new Insets(0, 0, 0, 0), 0, 0));
    
    headerPanel = new JPanel();
    headerPanel.setMinimumSize(new Dimension(1, 34));
    headerPanel.setOpaque(false);
    headerPanel.setLayout(new java.awt.GridBagLayout());
    
    if (inBrowseMode) {
      headerPanel.add(directoryPanel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(0, 0, 0, 0), 0, 0));
    }
    else {
      headerPanel.add(searchPanel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(0, 0, 0, 0), 0, 0));
    }
    headerPanel.add(attributeLabel, new GridBagConstraints(0, 1, 2, 1, 0.0D, 0.0D, 18, 0, new Insets(0, 30, 0, 0), 0, 0));
    headerPanel.add(searchBrowseButton, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 12, 0, new Insets(2, 0, 0, 4), 0, 0));
    headerPanel.add(javax.swing.Box.createHorizontalGlue(), new GridBagConstraints(1, 1, 2, 1, 1.0D, 1.0D, 13, 1, new Insets(0, 0, 0, 0), 0, 0));
    headerPanel.add(javax.swing.Box.createHorizontalGlue(), new GridBagConstraints(1, 0, 1, 1, 1.0D, 1.0D, 13, 1, new Insets(0, 0, 0, 0), 0, 0));
    


    customGalleryIcon = new ImageIcon(GalleryViewer.class.getResource("images/defaultFolderIcon.png"));
    webGalleryIcon = new ImageIcon(GalleryViewer.class.getResource("images/webGalleryIcon.png"));
    loadingImageIcon = new ImageIcon(GalleryViewer.class.getResource("images/loadingImageIcon.png"));
    noImageIcon = new ImageIcon(GalleryViewer.class.getResource("images/noImageIcon.png"));
    noFolderImageIcon = new ImageIcon(GalleryViewer.class.getResource("images/defaultFolderIcon.png"));
    localGalleryIcon = new ImageIcon(GalleryViewer.class.getResource("images/localGalleryIcon.png"));
    cdGalleryIcon = new ImageIcon(GalleryViewer.class.getResource("images/cdGalleryIcon.png"));
    add3DTextIcon = new ImageIcon(GalleryViewer.class.getResource("images/3DText.png"));
    ImageIcon upLevelIcon = new ImageIcon(GalleryViewer.class.getResource("images/upLevelIcon.png"));
    ImageIcon upLevelIconPressed = new ImageIcon(GalleryViewer.class.getResource("images/upLevelIconPressed.png"));
    
    ImageIcon upLevelIconDisabled = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDisabledIcon(upLevelIcon, 45);
    
    upLevelButton = new JButton(upLevelIcon);
    upLevelButton.setToolTipText(Messages.getString("Move_Up_a_Level"));
    upLevelButton.setOpaque(false);
    upLevelButton.setDisabledIcon(upLevelIconDisabled);
    upLevelButton.setPressedIcon(upLevelIconPressed);
    upLevelButton.setSize(upLevelIcon.getIconWidth() + 2, upLevelIcon.getIconHeight() + 2);
    upLevelButton.setMargin(new Insets(0, 0, 0, 0));
    upLevelButton.setBorder(null);
    upLevelButton.setCursor(new java.awt.Cursor(12));
    upLevelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        goUpOneLevel();
      }
      

    });
    add3DTextData = new ObjectXmlData();
    add3DTextData.name = Messages.getString("Create_3D_Text");
    add3DTextData.mainViewer = this;
    add3DTextData.transferable = new java.awt.datatransfer.StringSelection(add3DTextData.name);
    
    add3DTextButton = new TextBuilderButton();
    add3DTextButton.set(add3DTextData, add3DTextIcon, null);
    
    Vector builderVector = edu.cmu.cs.stage3.caitlin.personbuilder.PersonBuilder.getAllBuilders();
    builderButtonsVector = new Vector();
    
    for (int i = 0; i < builderVector.size(); i++) {
      if ((builderVector.get(i) instanceof StringObjectPair)) {
        StringObjectPair sop = (StringObjectPair)builderVector.get(i);
        ImageIcon builderIcon = null;
        if ((sop.getObject() instanceof ImageIcon)) {
          builderIcon = (ImageIcon)sop.getObject();
          



          ObjectXmlData personBuilderData = new ObjectXmlData();
          name = sop.getString();
          mainViewer = this;
          transferable = new java.awt.datatransfer.StringSelection(name);
          PersonBuilderButton currentButton = new PersonBuilderButton();
          currentButton.set(personBuilderData, builderIcon);
          builderButtonsVector.add(currentButton);
        }
      }
    }
    noObjectsLabel = new JLabel();
    noObjectsLabel.setFont(new java.awt.Font("Dialog", 0, 24));
    noObjectsLabel.setForeground(Color.white);
    noObjectsLabel.setText(Messages.getString("No_folders_or_Alice_characters_found_in_this_directory_"));
    
    noSearchResults = new JLabel();
    noSearchResults.setFont(new java.awt.Font("Dialog", 0, 24));
    noSearchResults.setForeground(Color.white);
    noSearchResults.setText(Messages.getString("No_models_were_found_"));
    
    searching = new JLabel();
    searching.setFont(new java.awt.Font("Dialog", 0, 24));
    searching.setForeground(Color.white);
    searching.setText(noModelsYet);
    
    setLayout(new java.awt.BorderLayout());
    add(headerPanel, "North");
    javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(objectPanel, 20, 30);
    add(scrollPane, "Center");
    

    scrollPane.getHorizontalScrollBar().setUnitIncrement(44);
    



    int fontSize = Integer.parseInt(edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getValue(authoringToolPackage, "fontSize"));
    setPreferredSize(new Dimension(Integer.MAX_VALUE, 250 + (fontSize - 12) * 6));
    setMinimumSize(new Dimension(100, 250));
  }
  
  private String getNodeText(Node node) {
    String toReturn = "";
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i).getNodeType() == 3) {
        toReturn = toReturn + children.item(i).getNodeValue();
      }
    }
    return toReturn;
  }
  
  private Object getDetailedNodeText(Node node) {
    Object toReturn = null;
    NodeList children = node.getChildNodes();
    if ((children.getLength() == 1) && (children.item(0).getNodeType() == 3)) {
      toReturn = children.item(0).getNodeValue();
    }
    else if (children.getLength() > 0) {
      Vector detailVector = new Vector();
      for (int i = 0; i < children.getLength(); i++) {
        Node currentNode = children.item(i);
        if ((currentNode.getNodeType() == 1) && 
          (currentNode.getFirstChild().getNodeType() == 3)) {
          detailVector.add(currentNode.getFirstChild().getNodeValue());
        }
      }
      
      toReturn = detailVector;
    }
    return toReturn;
  }
  
  public static String reverseWebReady(String url) {
    String toReturn = "";
    int beginning = 0;
    int index = url.indexOf("%20");
    while (index != -1) {
      toReturn = toReturn + url.substring(beginning, index);
      toReturn = toReturn + " ";
      beginning = index + 3;
      index = url.indexOf("%20", beginning);
    }
    toReturn = toReturn + url.substring(beginning, url.length());
    toReturn = toReturn.replace('/', File.separatorChar);
    return toReturn;
  }
  
  private String makeWebReady(String url) {
    String toReturn = "";
    int beginning = 0;
    for (int i = 0; i < url.length(); i++) {
      if (url.charAt(i) == ' ') {
        toReturn = toReturn + url.substring(beginning, i) + "%20";
        beginning = i + 1;
      }
    }
    toReturn = toReturn + url.substring(beginning, url.length());
    toReturn = toReturn.replace('\\', '/');
    return toReturn;
  }
  
  private String makeWebRelativePathReady(String url) {
    String toReturn = makeWebReady(url);
    if (toReturn.charAt(0) == '/') {
      toReturn = toReturn.substring(1);
    }
    return toReturn;
  }
  
  private String makeRelativePathReady(String relativeFilename) {
    String pathname = new String(relativeFilename);
    if (File.separatorChar == '\\') {
      pathname = pathname.replace('/', File.separatorChar);
    }
    else if (File.separatorChar == '/') {
      pathname = pathname.replace('\\', File.separatorChar);
    }
    if (pathname.charAt(0) == File.separatorChar) {
      pathname = pathname.substring(1);
    }
    return pathname;
  }
  
  private GalleryObject createGalleryObject(ObjectXmlData currentObject) {
    ObjectXmlData localMatch = null;
    if ((parentDirectory.firstLocalDirectory != null) && (parentDirectory.firstLocalDirectory.xmlData != null)) {
      localMatch = parentDirectory.firstLocalDirectory.xmlData.getModel(currentObject);
    }
    if ((localMatch == null) && (parentDirectory.secondLocalDirectory != null) && (parentDirectory.secondLocalDirectory.xmlData != null)) {
      localMatch = parentDirectory.secondLocalDirectory.xmlData.getModel(currentObject);
    }
    if (localMatch != null) {
      currentObject = localMatch;
    }
    GalleryObject toReturn = (GalleryObject)edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(currentObject);
    try {
      toReturn.set(currentObject);
    }
    catch (Exception e) {
      return null;
    }
    toReturn.loadImage();
    return toReturn;
  }
  
  private GalleryObject createGalleryDirectory(ObjectXmlData currentObject) {
    GalleryObject toReturn = (GalleryObject)edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(currentObject);
    try {
      toReturn.set(currentObject);
    }
    catch (Exception e) {
      return null;
    }
    





    toReturn.loadImage();
    return toReturn;
  }
  
  private ObjectXmlData createObjectXmlData(File dirFile, String root, int type) {
    if ((dirFile == null) || (dirFile.isDirectory()) || (!dirFile.canRead())) {
      return null;
    }
    ObjectXmlData currentObject = new ObjectXmlData();
    type = type;
    mainViewer = this;
    
    name = dirFile.getName();
    timeStamp = dirFile.lastModified();
    objectFilename = removeRootFromDirectory(root, dirFile.getAbsolutePath());
    transferable = createFileTransferable(root + objectFilename);
    imageFilename = objectFilename;
    size = ((int)(dirFile.length() / 1000L));
    return currentObject;
  }
  
  private ObjectXmlData createDirectoryObjectXmlData(File dirFile, String root, int type) {
    if ((dirFile == null) || (!dirFile.isDirectory()) || (!dirFile.canRead())) {
      return null;
    }
    
    ObjectXmlData currentObject = new ObjectXmlData();
    type = type;
    mainViewer = this;
    
    name = dirFile.getName();
    timeStamp = dirFile.lastModified();
    objectFilename = removeRootFromDirectory(root, dirFile.getAbsolutePath());
    transferable = createFileTransferable(root + objectFilename);
    File[] thumbFiles = dirFile.listFiles(thumbnailFilter);
    if ((thumbFiles != null) && (thumbFiles.length > 0)) {
      imageFilename = makeRelativePathReady(objectFilename + File.separator + thumbFiles[0].getName());
    }
    else {
      imageFilename = null;
    }
    size = -1;
    return currentObject;
  }
  
  private ObjectXmlData createObjectXmlData(Node currentModel, String root, int type)
  {
    ObjectXmlData currentObject = new ObjectXmlData();
    type = type;
    mainViewer = this;
    
    NodeList nodeDetails = currentModel.getChildNodes();
    for (int j = 0; j < nodeDetails.getLength(); j++) {
      Node currentDetail = nodeDetails.item(j);
      if ((currentDetail != null) && 
        (currentDetail.getNodeType() == 1) && (!currentDetail.getNodeName().equals("#text"))) {
        try {
          if (currentDetail.getNodeName().equalsIgnoreCase("name")) {
            name = getNodeText(currentDetail);
          }
          else if (currentDetail.getNodeName().equalsIgnoreCase("timestamp")) {
            timeStamp = Long.parseLong(getNodeText(currentDetail));
          }
          else if (currentDetail.getNodeName().equalsIgnoreCase("objectfilename")) {
            String rawFilename = getNodeText(currentDetail);
            if (type == 2) {
              objectFilename = makeWebRelativePathReady(rawFilename);
            }
            else {
              objectFilename = makeRelativePathReady(rawFilename);
            }
            if (type == 2) {
              transferable = createURLTransferable(root + objectFilename);
              if (transferable == null) {
                return null;
              }
            }
            else if ((type == 1) || (type == 3)) {
              transferable = createFileTransferable(root + objectFilename);
              if (transferable == null) {
                return null;
              }
            }
          }
          else if (currentDetail.getNodeName().equalsIgnoreCase("imagefilename")) {
            String rawFilename = getNodeText(currentDetail);
            if (type == 2) {
              imageFilename = makeWebRelativePathReady(rawFilename);
            }
            else {
              imageFilename = makeRelativePathReady(rawFilename);
            }
          }
          else if (currentDetail.getNodeName().equalsIgnoreCase("size")) {
            currentObject.setSize(getNodeText(currentDetail));
          }
          else if (currentDetail.getNodeName().equalsIgnoreCase("physicalsize")) {
            currentObject.setDimensions(getNodeText(currentDetail));
          }
          else {
            currentObject.addDetail(currentDetail.getNodeName(), getDetailedNodeText(currentDetail));
          }
        }
        catch (Exception e) {
          currentObject = null;
          break;
        }
      }
    }
    
    return currentObject;
  }
  
  private void buildNewDirectory(DirectoryStructure newDir, boolean setAsCurrent)
  {
    DirectoryStructure oldDir = directoryOnDisplay;
    boolean oldIsInWebGallery = isInWebGallery;
    if (setAsCurrent)
    {
      directoryOnDisplay = newDir;
      
      if (!stopBuildingGallery)
      {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            GalleryViewer.this.createDirectoryButtons();
            directoryPanel.revalidate();
            repaint();
          }
        });
      }
      else {
        directoryOnDisplay = oldDir;
        refreshGUI(); return;
      }
    }
    String xml;
    if (rootNode.type == 2) {
      isInWebGallery = true;
      boolean oldUpdateValue = updatePanelsWhileLoading;
      updatePanelsWhileLoading = false;
      if (firstLocalDirectory != null) {
        if (firstLocalDirectory.xmlData == null) {
          buildNewDirectory(firstLocalDirectory, false);
        }
      }
      else if ((secondLocalDirectory != null) && 
        (secondLocalDirectory.xmlData == null)) {
        buildNewDirectory(secondLocalDirectory, false);
      }
      

      updatePanelsWhileLoading = oldUpdateValue;
      long tempTimeStamp = -1L;
      if (data != null)
      {
        if (data.timeStamp == -1L) {
          data.timeStamp = getURLTimeStamp(rootNode.rootPath + path);
          tempTimeStamp = data.timeStamp;
        }
        

      }
      else {
        tempTimeStamp = getURLTimeStamp(rootNode.rootPath + path);
      }
      String xml = getXML(rootNode.rootPath, path, 2, tempTimeStamp, cacheDir, false);
      
      if (xml == null)
      {
        if (setAsCurrent) {
          isInWebGallery = oldIsInWebGallery;
          directoryOnDisplay = oldDir;
          refreshGUI();
        }
        
      }
    }
    else
    {
      if ((directoryOnDisplay.name.startsWith(localGalleryName)) || (directoryOnDisplay.name.equals(cdGalleryName))) {
        isInWebGallery = false;
      }
      xml = null;
    }
    int totalInside = -1;
    try {
      if (xml != null)
      {
        totalInside = newDir.initSelf(xml);

      }
      else if (rootNode.type != 2) {
        totalInside = newDir.initSelf(new File(rootNode.rootPath + path));
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      if (setAsCurrent) {
        isInWebGallery = oldIsInWebGallery;
        directoryOnDisplay = oldDir;
        refreshGUI();
      }
      return;
    }
    if ((setAsCurrent) && (totalInside < 0)) {
      isInWebGallery = oldIsInWebGallery;
      directoryOnDisplay = oldDir;
      refreshGUI();
    }
    if (updatePanelsWhileLoading) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          removeAttribution();
        }
      });
    }
  }
  
  public int searchDirectory(DirectoryStructure toSearch, String toSearchFor) {
    if ((toSearch != null) && (!stopSearch)) {
      if (xmlData == null) {
        updatePanelsWhileLoading = false;
        buildNewDirectory(toSearch, false);
      }
      Vector matches = toSearch.getObjectMatches(toSearchFor);
      if (matches != null) {
        for (int i = 0; i < matches.size(); i++) {
          if (!searchResults.contains((ObjectXmlData)matches.get(i))) {
            searchResults.xmlData.addModel((ObjectXmlData)matches.get(i));
            if (searchCount == 0) {
              objectPanel.removeAll();
            }
            modelAdded((ObjectXmlData)matches.get(i), searchCount);
            searchCount += 1;
          }
        }
      }
      int count = matches.size();
      for (int i = 0; i < xmlData.directories.size(); i++) {
        count += searchDirectory(xmlData.getDirectory(i), toSearchFor);
      }
      return count;
    }
    return 0;
  }
  
  public void searchGallery(final DirectoryStructure toSearch, final String toSearchFor, final boolean isWeb) {
    oldIsInWebGalleryValue = isInWebGallery;
    if (toSearchFor == null) {
      return;
    }
    lastSearchString = toSearchFor;
    if (!isWeb) {
      searchResults.xmlData.models.removeAllElements();
      searching.setText(noModelsYet);
      searchCount = 0;
      objectPanel.removeAll();
      

      objectPanel.add(searching);
      objectPanel.revalidate();
      objectPanel.repaint();
    }
    stopSearch = false;
    searchingProgressLabel.reset();
    if (isWeb) {
      searchWebButton.setText(stopSearchString);
    }
    else {
      searchButton.setText(stopSearchString);
    }
    

    Thread searchThread = new Thread() {
      public void run() {
        int total = 0;
        Thread t = new Thread() {
          public void run() {
            while (!stopSearch) {
              SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  searchingProgressLabel.advance();
                }
              });
              try {
                Thread.sleep(250L);
              }
              catch (Exception localException) {}
            }
          }
        };
        t.start();
        
        if (toSearch == null) {
          for (int i = 0; i < rootDirectories.size(); i++) {
            GalleryViewer.RootDirectoryStructure currentRoot = (GalleryViewer.RootDirectoryStructure)rootDirectories.get(i);
            if ((!isWeb) && (currentRoot != webGallery)) {
              total += searchDirectory(directory, toSearchFor);
            }
            
          }
        } else {
          total = searchDirectory(toSearch, toSearchFor);
        }
        final int finalTotal = total;
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if ((finalTotal <= 0) && (searchCount <= 0)) {
              objectPanel.removeAll();
              
              if (val$isWeb) {
                noSearchResults.setText(Messages.getString("No_models_matching__") + val$toSearchFor + "\" " + Messages.getString("were_found_on_") + webGalleryHostName);
              }
              else {
                noSearchResults.setText(Messages.getString("No_models_matching__") + val$toSearchFor + "\" " + Messages.getString("were_found_on_your_machine_"));
              }
              
              objectPanel.add(noSearchResults);
              objectPanel.revalidate();
              objectPanel.repaint();
            }
            if (val$isWeb) {
              searchWebButton.setText(startSearchWebString);
            }
            else {
              searchButton.setText(startSearchString);
              searchWebButton.setEnabled(true);
            }
            searchingProgressLabel.reset();
          }
        });
        stopSearch = true;
        isInWebGallery = oldIsInWebGalleryValue;
      }
      
    };
    searchThread.start();
  }
  
  protected void changeDirectory(DirectoryStructure toChangeTo) {
    DirectoryStructure temp = null;
    if (toChangeTo != null) {
      temp = directoryToUse;
    }
    



    final DirectoryStructure actualToChangeTo = temp;
    if (actualToChangeTo == directoryOnDisplay)
    {
      return;
    }
    Runnable toRun = null;
    if ((actualToChangeTo != null) && (xmlData == null)) {
      toRun = new Runnable() {
        public void run() {
          updatePanelsWhileLoading = true;
          
          GalleryViewer.this.buildNewDirectory(actualToChangeTo, true);
          updatePanelsWhileLoading = false;
        }
        
      };
    } else {
      toRun = new Runnable() {
        public void run() {
          directoryOnDisplay = actualToChangeTo;
          if (directoryOnDisplay == null) {
            isInWebGallery = false;

          }
          else if ((directoryOnDisplay.name.startsWith(GalleryViewer.localGalleryName)) || (directoryOnDisplay.name.equals(GalleryViewer.cdGalleryName))) {
            isInWebGallery = false;
          }
          else if (directoryOnDisplay.name.equals(GalleryViewer.webGalleryName)) {
            isInWebGallery = true;
          }
          
          if ((directoryOnDisplay != null) && (!isInWebGallery)) {
            try {
              directoryOnDisplay.updateSelf(new File(directoryOnDisplay.rootNode.rootPath + directoryOnDisplay.path));
            }
            catch (Exception e) {
              e.printStackTrace();
            }
          }
          refreshGUI();
        }
      };
    }
    if ((changingThread != null) && (changingThread.isAlive())) {
      stopBuildingGallery = true;
      
      while (changingThread.isAlive()) {
        try
        {
          Thread.sleep(10L);
        }
        catch (Exception localException) {}
      }
      
      stopBuildingGallery = false;
    }
    if (isInWebGallery) {
      attributeLabel.setText(Messages.getString("Loading_from_") + webGalleryHostName + "...");
    }
    else {
      attributeLabel.setText(Messages.getString("Loading___"));
    }
    objectPanel.removeAll();
    

    changingThread = new Thread(toRun);
    changingThread.start();
  }
  
  private void updateLoading(final float percentage) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (isInWebGallery) {
          attributeLabel.setText(Messages.getString("Loading_from_") + webGalleryHostName + "..." + String.valueOf((int)(percentage * 100.0F)) + "%");
        }
        else {
          attributeLabel.setText(Messages.getString("Loading___") + String.valueOf((int)(percentage * 100.0F)) + "%");
        }
      }
    });
  }
  
  protected static String cleanUpName(String name)
  {
    String newName = new String(name);
    if ((name.length() > 4) && (name.charAt(name.length() - 4) == '.')) {
      newName = newName.substring(0, newName.length() - 4);
    }
    return newName;
  }
  
  public void diplayAttribution(ObjectXmlData o) {
    String modeledBy = o.getDetail("modeledby");
    String programmedBy = o.getDetail("programmedby");
    String paintedBy = o.getDetail("paintedby");
    String displayName = cleanUpName(name);
    String attributeString = displayName;
    if ((modeledBy != null) || (programmedBy != null) || (paintedBy != null)) {
      attributeString = attributeString + ": ";
    }
    boolean haveOne = false;
    if (modeledBy != null) {
      attributeString = attributeString + Messages.getString("Modeled_by_") + modeledBy;
      haveOne = true;
    }
    if (paintedBy != null) {
      if (haveOne) {
        attributeString = attributeString + ", ";
      }
      attributeString = attributeString + Messages.getString("Painted_by_") + paintedBy;
      haveOne = true;
    }
    if (programmedBy != null) {
      if (haveOne)
        attributeString = attributeString + ", ";
      attributeString = attributeString + Messages.getString("Programmed_by_") + programmedBy;
    }
    attributeLabel.setText(attributeString);
    attributePanel.repaint();
  }
  
  public void removeAttribution() {
    attributeLabel.setText(" ");
    attributePanel.repaint();
  }
  
  protected void refreshGUI() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (!stopBuildingGallery) {
          if (inBrowseMode) {
            GalleryViewer.this.createDirectoryButtons();
            directoryPanel.revalidate();
          }
          else {
            searchPanel.revalidate();
          }
          repaint();
        }
        else {
          return;
        }
        if (!stopBuildingGallery) {
          GalleryViewer.this.createGalleryPanels();
          objectPanel.revalidate();
          repaint();
        }
        else {
          return;
        }
        removeAttribution();
      }
    });
  }
  





  private void bumpDown(int index) {}
  




  private void resetLayout(int index) {}
  




  private void removeGalleryObject(GalleryObject toRemove)
  {
    objectPanel.remove(toRemove);
    resetLayout(0);
  }
  
  private void modelAdded(ObjectXmlData added, int count) {
    final GalleryObject toAdd = createGalleryObject(added);
    if (toAdd != null) {
      count++;
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          objectPanel.add(toAdd);
          objectPanel.repaint();
        }
      });
    }
  }
  
  private void directoryAdded(ObjectXmlData added, final int count) {
    final GalleryObject toAdd = createGalleryDirectory(added);
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (toAdd != null) {
          GalleryViewer.this.bumpDown(count);
          
          objectPanel.add(toAdd);
          objectPanel.revalidate();
        }
      }
    });
  }
  
  private GalleryObject getGalleryObject(String objectName) {
    for (int i = 0; i < objectPanel.getComponentCount(); i++) {
      if ((objectPanel.getComponent(i) instanceof GalleryObject)) {
        GalleryObject current = (GalleryObject)objectPanel.getComponent(i);
        if (data.name.equals(objectName)) {
          return current;
        }
      }
    }
    return null;
  }
  



  private void createGalleryPanels()
  {
    if (directoryOnDisplay == null) {
      createRootGalleryPanels();
    }
    else {
      DirectoryXmlData xmlData = directoryOnDisplay.xmlData;
      objectPanel.removeAll();
      int count = 0;
      if ((xmlData == null) || (directories == null) || (models == null)) {
        return;
      }
      int size = directories.size() + models.size();
      boolean isPeople = false;
      boolean isLocal = false;
      if (name.equalsIgnoreCase("people")) {
        size += builderButtonsVector.size();
        isPeople = true;
      }
      if (name.startsWith(localGalleryName)) {
        size++;
        isLocal = true;
      }
      
      for (int i = 0; i < directories.size(); i++) {
        if (!stopBuildingGallery) {
          ObjectXmlData currentDirectory = (ObjectXmlData)directories.get(i);
          long oldTime = System.currentTimeMillis();
          GalleryObject toAdd = createGalleryDirectory(currentDirectory);
          
          if (toAdd != null)
          {
            objectPanel.add(toAdd);
            count++;
          }
        }
        else {
          return;
        }
      }
      for (int i = 0; i < models.size(); i++) {
        if (!stopBuildingGallery) {
          ObjectXmlData currentModel = (ObjectXmlData)models.get(i);
          long oldTime = System.currentTimeMillis();
          GalleryObject toAdd = createGalleryObject(currentModel);
          
          if (toAdd != null)
          {
            objectPanel.add(toAdd);
            count++;
          }
        }
        else {
          return;
        }
      }
      
      if ((isPeople) && (showBuilder)) {
        for (int p = 0; p < builderButtonsVector.size(); p++) {
          count++;
          GenericBuilderButton builderButton = (GenericBuilderButton)builderButtonsVector.get(p);
          
          objectPanel.add(builderButton);
          builderButton.updateGUI();
        }
      }
      

      if (isLocal) {
        count++;
        bumpDown(count);
        
        objectPanel.add(add3DTextButton);
        add3DTextButton.updateGUI();
        objectPanel.repaint();
      }
      if (count == 0) {
        objectPanel.removeAll();
        
        if (directoryOnDisplay == searchResults)
        {
          objectPanel.add(noSearchResults);
        }
        else
        {
          objectPanel.add(noObjectsLabel);
        }
      }
    }
  }
  
  private void createRootGalleryPanels() {
    if ((rootDirectories != null) && (rootDirectories.size() > 0)) {
      int count = 0;
      int maxCount = rootDirectories.size();
      objectPanel.removeAll();
      
      for (int i = 0; i < rootDirectories.size(); i++) {
        if (!stopBuildingGallery) {
          RootDirectoryStructure currentRoot = (RootDirectoryStructure)rootDirectories.get(i);
          GalleryObject toAdd = (GalleryObject)edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(xmlData);
          try {
            toAdd.set(xmlData);
            if (directory.name == cdGalleryName) {
              toAdd.setImage(cdGalleryIcon);
            }
            else if (directory.name.startsWith(localGalleryName)) {
              toAdd.setImage(localGalleryIcon);
            }
            else if (directory.name == webGalleryName) {
              toAdd.setImage(webGalleryIcon);
              isTopLevelDirectory = true;
            }
            else if (directory.name == customGalleryName) {
              toAdd.setImage(customGalleryIcon);
            }
          }
          catch (Exception e) {
            toAdd = null;
            continue;
          }
          if (toAdd != null)
          {
            objectPanel.add(toAdd);
            count++;
          }
        }
        else {
          return;
        }
      }
    }
  }
  

  protected void goUpOneLevel()
  {
    if (directoryOnDisplay != null) {
      changeDirectory(directoryOnDisplay.parent);
    }
  }
  
  private void createDirectoryButtons() {
    DirectoryStructure currentDir = directoryOnDisplay;
    Stack dirs = new Stack();
    directoryPanel.removeAll();
    JLabel currentDirLabel = new JLabel();
    currentDirLabel.setForeground(textColor);
    int count = 0;
    upLevelButton.setEnabled(true);
    if (currentDir != null) {
      DirectoryBarButton rootButton = new DirectoryBarButton(null, this);
      directoryPanel.add(rootButton, new GridBagConstraints(count, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 3, 0, 3), 0, 0));
      count++;
      while (currentDir != null) {
        if ((isInWebGallery) && ((name.startsWith(localGalleryName)) || (name.equals(cdGalleryName)))) {
          if (webGallery != null) {
            dirs.push(webGallery.directory);
            currentDir = webGallery.directory.parent;
          }
          else {
            dirs.push(currentDir);
            currentDir = parent;
          }
        }
        else {
          dirs.push(currentDir);
          currentDir = parent;
        }
      }
      while (!dirs.empty()) {
        currentDir = (DirectoryStructure)dirs.pop();
        if (count > 0) {
          JLabel arrow = new JLabel(">");
          arrow.setForeground(textColor);
          directoryPanel.add(arrow, new GridBagConstraints(count, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 3, 0, 3), 0, 0));
          count++;
        }
        if (currentDir == directoryOnDisplay) {
          currentDirLabel.setText(name);
          directoryPanel.add(currentDirLabel, new GridBagConstraints(count, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
        }
        else {
          DirectoryBarButton currentButton = new DirectoryBarButton(currentDir, this);
          directoryPanel.add(currentButton, new GridBagConstraints(count, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
        }
        count++;
      }
    }
    else
    {
      currentDirLabel.setText(homeName);
      directoryPanel.add(currentDirLabel, new GridBagConstraints(count, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
      count++;
      upLevelButton.setEnabled(false);
      upLevelButton.repaint();
    }
    directoryPanel.add(upLevelButton, new GridBagConstraints(count, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 4, 0, 0), 0, 0));
    count++;
    directoryPanel.add(javax.swing.Box.createHorizontalGlue(), new GridBagConstraints(count, 0, 1, 1, 1.0D, 1.0D, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
  }
  
  private void exploreXML(Node current, int level) {
    if (current != null) {
      System.out.println(level + ": " + Messages.getString("Name__") + current.getNodeName() + Messages.getString("__Type__") + current.getNodeType() + Messages.getString("__Value__") + current.getNodeValue() + Messages.getString("__Children__"));
      NodeList children = current.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        exploreXML(children.item(i), level + 1);
      }
      System.out.println("}");
    }
  }
}
