package edu.cmu.cs.stage3.caitlin.personbuilder;

import edu.cmu.cs.stage3.xml.Encoder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;






























public class XMLDirectoryBuilder
{
  public XMLDirectoryBuilder()
  {
    File mainFile = loadMainDirectory();
    generateDocument(mainFile);
  }
  
  protected void generateDocument(File mainFile) {
    Document document;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.newDocument();
    } catch (ParserConfigurationException pce) { Document document;
      document = null;
    }
    
    if (document != null) {
      Element root = document.createElement("directory");
      root.setAttribute("path", mainFile.getName());
      if (mainFile.isDirectory()) {
        File[] kidFiles = mainFile.listFiles();
        for (int i = 0; i < kidFiles.length; i++) {
          createChildren(document, root, kidFiles[i], mainFile.getName());
        }
      }
      
      document.appendChild(root);
      document.getDocumentElement().normalize();
      try
      {
        FileWriter fileWriter = new FileWriter(mainFile.getAbsolutePath() + File.separator + "structure.xml");
        Encoder.write(document, fileWriter);
        fileWriter.close();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }
  



  protected void createChildren(Document document, Element element, File file, String dir)
  {
    if (file.isDirectory()) {
      Element childElement = document.createElement("directory");
      childElement.setAttribute("path", dir + '/' + file.getName());
      element.appendChild(childElement);
      
      File[] kidFiles = file.listFiles();
      for (int i = 0; i < kidFiles.length; i++) {
        createChildren(document, childElement, kidFiles[i], dir + '/' + file.getName());
      }
    } else if ((file.getName().endsWith(".jpg")) || (file.getName().endsWith(".gif"))) {
      Element childElement = document.createElement("image");
      childElement.setAttribute("path", dir + '/' + file.getName());
      element.appendChild(childElement);
    } else if (file.getName().endsWith(".xml")) {
      Element childElement = document.createElement("xml");
      childElement.setAttribute("path", dir + '/' + file.getName());
      element.appendChild(childElement);
    } else if (file.getName().endsWith(".a2c")) {
      Element childElement = document.createElement("model");
      childElement.setAttribute("path", dir + '/' + file.getName());
      element.appendChild(childElement);
    }
  }
  
  protected File loadMainDirectory()
  {
    URL imageURL = ClassLoader.getSystemResource("edu\\cmu\\cs\\stage3\\caitlin\\personbuilder\\images");
    File mainFile = new File(imageURL.getFile());
    
    return mainFile;
  }
}
