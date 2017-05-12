package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;










public class WorldFileChooser
  extends JFileChooser
  implements PropertyChangeListener
{
  public WorldFileChooser()
  {
    addPropertyChangeListener("directoryChanged", this);
  }
  
  public void approveSelection() {
    if (getDialogType() == 0) {
      File worldDir = getSelectedFile();
      File worldFile = new File(worldDir, "elementData.xml");
      if (!worldFile.exists()) {
        DialogManager.showMessageDialog(Messages.getString("Your_selection_is_not_a_valid_world_folder_"));
      } else if (!worldFile.canRead()) {
        DialogManager.showMessageDialog(Messages.getString("Cannot_read_world_from_disk___Access_denied_"));
      } else if (!isWorld(worldFile)) {
        DialogManager.showMessageDialog(Messages.getString("Your_selection_is_not_a_valid_world_folder_"));
      } else {
        super.approveSelection();
      }
    } else if (getDialogType() == 1) {
      File worldDir = getSelectedFile();
      File worldFile = new File(worldDir, "elementData.xml");
      if (worldFile.exists()) {
        int retVal = DialogManager.showConfirmDialog(Messages.getString("A_World_already_exists_in_") + worldDir.getAbsolutePath() + Messages.getString("__nWould_you_like_to_replace_it_"), Messages.getString("Replace_World_"), 0);
        if (retVal == 0) {
          super.approveSelection();
        }
      } else {
        super.approveSelection();
      }
    } else {
      super.approveSelection();
    }
  }
  
  public void propertyChange(PropertyChangeEvent ev) {
    File currentDirectory = (File)ev.getNewValue();
    if ((currentDirectory != null) && (currentDirectory.canRead())) {
      File worldFile = new File(currentDirectory, "elementData.xml");
      if ((worldFile.exists()) && (worldFile.canRead()) && (isWorld(worldFile))) {
        setCurrentDirectory(currentDirectory.getParentFile());
        setSelectedFile(currentDirectory);
        approveSelection();
      }
    }
  }
  
  protected boolean isWorld(File xmlFile) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(xmlFile);
      
      Element rootElement = document.getDocumentElement();
      rootElement.normalize();
      String className = rootElement.getAttribute("class");
      return (className != null) && (className.trim().equals("edu.cmu.cs.stage3.alice.core.World"));
    }
    catch (SAXParseException localSAXParseException) {}catch (SAXException localSAXException) {}catch (ParserConfigurationException localParserConfigurationException) {}catch (IOException localIOException) {}
    








    return false;
  }
}
