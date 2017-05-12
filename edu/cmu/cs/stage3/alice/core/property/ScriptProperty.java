package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.ExceptionWrapper;
import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.alice.scripting.Code;
import edu.cmu.cs.stage3.alice.scripting.CompileType;
import edu.cmu.cs.stage3.io.DirectoryTreeLoader;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.io.KeepFileDoesNotExistException;
import edu.cmu.cs.stage3.io.KeepFileNotSupportedException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;
import org.w3c.dom.Document;






public class ScriptProperty
  extends StringProperty
{
  private Code m_code = null;
  
  public ScriptProperty(edu.cmu.cs.stage3.alice.core.Element owner, String name, String defaultValue) { super(owner, name, defaultValue); }
  
  protected void onSet(Object value)
  {
    super.onSet(value);
    m_code = null;
  }
  
  public Code getCode(CompileType compileType) { String script = getStringValue();
    if ((script != null) && (script.length() > 0)) {
      if (m_code == null) {
        m_code = getOwner().compile(script, this, compileType);
      }
    } else {
      m_code = null;
    }
    return m_code;
  }
  
  private String loadScript(InputStream is) throws IOException { BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(is)));
    StringBuffer sb = new StringBuffer();
    for (;;) {
      String s = br.readLine();
      if (s == null) break;
      sb.append(s);
      sb.append('\n');
    }
    


    if (sb.length() > 0) {
      return sb.substring(0, sb.length() - 1);
    }
    return "";
  }
  
  private void storeScript(OutputStream os) throws IOException {
    BufferedOutputStream bos = new BufferedOutputStream(os);
    String script = getStringValue();
    if (script != null) {
      bos.write(script.getBytes());
    }
    bos.flush();
  }
  
  protected void decodeObject(org.w3c.dom.Element node, DirectoryTreeLoader loader, Vector referencesToBeResolved, double version) throws IOException {
    m_associatedFileKey = null;
    String filename = getFilename(getNodeText(node));
    InputStream is = loader.readFile(filename);
    set(loadScript(is));
    loader.closeCurrentFile();
    try {
      m_associatedFileKey = loader.getKeepKey(filename);
    } catch (KeepFileNotSupportedException kfnse) {
      m_associatedFileKey = null;
    }
  }
  
  protected void encodeObject(Document document, org.w3c.dom.Element node, DirectoryTreeStorer storer, ReferenceGenerator referenceGenerator) throws IOException {
    String filename = getName() + ".py";
    Object associatedFileKey;
    try {
      associatedFileKey = storer.getKeepKey(filename);
    } catch (KeepFileNotSupportedException kfnse) { Object associatedFileKey;
      associatedFileKey = null;
    }
    if ((m_associatedFileKey == null) || (!m_associatedFileKey.equals(associatedFileKey))) {
      m_associatedFileKey = null;
      OutputStream os = storer.createFile(filename, true);
      storeScript(os);
      storer.closeCurrentFile();
      try {
        m_associatedFileKey = storer.getKeepKey(filename);
      } catch (KeepFileNotSupportedException kfnse) {
        m_associatedFileKey = null;
      }
    }
    else if (storer.isKeepFileSupported()) {
      try {
        storer.keepFile(filename);
      } catch (KeepFileNotSupportedException kfnse) {
        throw new Error(storer + " returns true for isKeepFileSupported(), but then throws " + kfnse);
      } catch (KeepFileDoesNotExistException kfdne) {
        throw new ExceptionWrapper(kfdne, filename);
      }
    }
    
    node.appendChild(createNodeForString(document, "java.io.File[" + filename + "]"));
  }
  
  public void keepAnyAssociatedFiles(DirectoryTreeStorer storer) throws KeepFileNotSupportedException, KeepFileDoesNotExistException {
    super.keepAnyAssociatedFiles(storer);
    String filename = getName() + ".py";
    storer.keepFile(filename);
  }
}
