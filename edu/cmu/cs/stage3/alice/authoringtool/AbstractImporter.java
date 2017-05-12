package edu.cmu.cs.stage3.alice.authoringtool;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.lang.Messages;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;













public abstract class AbstractImporter
  implements Importer
{
  public AbstractImporter() {}
  
  private Object location = null;
  protected String plainName = null;
  
  public abstract Map getExtensionMap();
  
  public Element load(String filename) throws IOException {
    location = new File(filename).getParentFile();
    String fullName = new File(filename).getName();
    plainName = fullName.substring(0, fullName.indexOf('.'));
    if (!Element.isPotentialNameValid(plainName))
    {

      plainName = Element.generateValidName(plainName);
    }
    FileInputStream fis = new FileInputStream(filename);
    Element castMember = load(fis, getExtension(filename));
    fis.close();
    plainName = null;
    return castMember;
  }
  
  public Element load(File file) throws IOException {
    location = file.getParentFile();
    String fullName = file.getName();
    plainName = fullName.substring(0, fullName.indexOf('.'));
    if (!Element.isPotentialNameValid(plainName))
    {

      plainName = Element.generateValidName(plainName);
    }
    FileInputStream fis = new FileInputStream(file);
    Element castMember = load(fis, getExtension(file.getName()));
    fis.close();
    plainName = null;
    return castMember;
  }
  
  public Element load(URL url) throws IOException {
    String externalForm = url.toExternalForm();
    location = new URL(externalForm.substring(0, externalForm.lastIndexOf('/') + 1));
    String fullName = externalForm.substring(externalForm.lastIndexOf('/') + 1);
    plainName = fullName.substring(0, fullName.lastIndexOf('.'));
    if (!Element.isPotentialNameValid(plainName))
    {

      plainName = Element.generateValidName(plainName);
    }
    Element castMember = load(url.openStream(), getExtension(url.getFile()));
    plainName = null;
    return castMember;
  }
  
  protected abstract Element load(InputStream paramInputStream, String paramString) throws IOException;
  
  public Object getLocation() {
    return location;
  }
  
  private String getExtension(String filename) {
    if (filename == null) {
      throw new IllegalArgumentException(Messages.getString("null_filename_encountered"));
    }
    filename.trim();
    int i = filename.lastIndexOf(".");
    if (i == -1) {
      throw new IllegalArgumentException(Messages.getString("unable_to_determine_the_extension_of_") + filename);
    }
    String ext = filename.substring(i + 1);
    if (ext.length() < 1) {
      throw new IllegalArgumentException(Messages.getString("unable_to_determine_the_extension_of_") + filename);
    }
    ext = ext.toUpperCase();
    if (getExtensionMap().get(ext) == null) {
      throw new IllegalArgumentException(ext + " " + Messages.getString("files_are_not_supported_by_this_Importer"));
    }
    return ext;
  }
}
