package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.io.FileUtilities;
import edu.cmu.cs.stage3.io.KeepFileDoesNotExistException;
import edu.cmu.cs.stage3.io.KeepFileNotSupportedException;
import edu.cmu.cs.stage3.media.DataSource;
import edu.cmu.cs.stage3.media.Manager;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.w3c.dom.Document;











public class DataSourceProperty
  extends ObjectProperty
{
  public DataSourceProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, DataSource defaultValue)
  {
    super(owner, name, defaultValue, DataSource.class);
  }
  
  public DataSource getDataSourceValue() {
    return (DataSource)getValue();
  }
  
  private String getFilename() {
    DataSource dataSourceValue = getDataSourceValue();
    return getOwnername.getStringValue() + '.' + 
      dataSourceValue.getExtension();
  }
  

  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version)
    throws IOException
  {
    m_associatedFileKey = null;
    
    String filename = getFilename(getNodeText(node));
    
    InputStream is = loader.readFile(filename);
    set(Manager.createDataSource(is, 
      FileUtilities.getExtension(filename)));
    loader.closeCurrentFile();
    try
    {
      String durationHintText = node.getAttribute("durationHint");
      if (durationHintText != null) {
        double durationHint = Double.parseDouble(durationHintText);
        getDataSourceValue().setDurationHint(durationHint);
      }
    }
    catch (Throwable localThrowable) {}
    
    try
    {
      m_associatedFileKey = loader.getKeepKey(filename);
    } catch (KeepFileNotSupportedException kfnse) {
      m_associatedFileKey = null;
    }
  }
  


  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator)
    throws IOException
  {
    DataSource dataSourceValue = getDataSourceValue();
    if (dataSourceValue != null) {
      double duration = dataSourceValue.getDuration(true);
      if (!Double.isNaN(duration))
      {

        node.setAttribute("durationHint", Double.toString(duration));
      }
      
      String filename = getFilename();
      Object associatedFileAbsolutePath;
      try {
        associatedFileAbsolutePath = storer.getKeepKey(filename);
      } catch (KeepFileNotSupportedException kfnse) { Object associatedFileAbsolutePath;
        associatedFileAbsolutePath = null;
      }
      if ((m_associatedFileKey == null) || 
        (!m_associatedFileKey.equals(associatedFileAbsolutePath))) {
        m_associatedFileKey = null;
        OutputStream os = storer.createFile(filename, dataSourceValue.isCompressionWorthwhile());
        BufferedOutputStream bos = new BufferedOutputStream(os);
        bos.write(dataSourceValue.getData());
        bos.flush();
        storer.closeCurrentFile();
        m_associatedFileKey = associatedFileAbsolutePath;
      } else {
        try {
          storer.keepFile(filename);
        } catch (KeepFileNotSupportedException kfnse) {
          kfnse.printStackTrace();
        } catch (KeepFileDoesNotExistException kfdne) {
          kfdne.printStackTrace();
        }
      }
      node.appendChild(document.createTextNode("java.io.File[" + filename + 
        "]"));
    }
  }
  

  public void keepAnyAssociatedFiles(DirectoryTreeStorer storer)
    throws KeepFileNotSupportedException, KeepFileDoesNotExistException
  {
    super.keepAnyAssociatedFiles(storer);
    DataSource dataSourceValue = getDataSourceValue();
    

    if (dataSourceValue != null) {
      String filename = getFilename();
      storer.keepFile(filename);
    }
  }
}
