package edu.cmu.cs.stage3.alice.scenegraph.io;

import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.Component;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.util.Enumerable;
import java.io.ByteArrayOutputStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipOutputStream;
import javax.vecmath.Matrix4d;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XML
{
  public static final double VERSION = 1.0D;
  
  public XML() {}
  
  private static String encodeIntArray(int[] array, int offset, int length, boolean isHexadecimal)
  {
    StringBuffer buffer = new StringBuffer();
    int index = offset;
    for (int lcv = 0; lcv < length; lcv++)
    {
      int value = array[(index++)];
      String s; String s; if (isHexadecimal) {
        s = Integer.toHexString(value).toUpperCase();
      } else {
        s = Integer.toString(value);
      }
      buffer.append(s);
      if (lcv < length - 1) {
        buffer.append(' ');
      }
    }
    return buffer.toString();
  }
  
  private static void decodeIntArray(String s, int[] array, int offset, int length, boolean isHexadecimal) { int index = offset;
    int begin = 0;
    for (int lcv = 0; lcv < length; lcv++) {
      int end = s.indexOf(' ', begin);
      if (end == -1) {
        end = s.length();
      }
      String substr = s.substring(begin, end);
      int value;
      int value; if (isHexadecimal) {
        value = (int)Long.parseLong(substr, 16);
      } else {
        value = Integer.parseInt(substr);
      }
      array[(index++)] = value;
      begin = end + 1;
    }
  }
  
  private static String encodeIntArray(int[] array, boolean isHexadecimal) { return encodeIntArray(array, 0, array.length, isHexadecimal); }
  
  private static void decodeIntArray(String s, int[] array, boolean isHexadecimal) {
    decodeIntArray(s, array, 0, array.length, isHexadecimal);
  }
  
  private static String encodeDoubleArray(double[] array, int offset, int length) {
    StringBuffer buffer = new StringBuffer();
    int index = offset;
    for (int lcv = 0; lcv < length; lcv++) {
      buffer.append(Double.toString(array[(index++)]));
      if (lcv < length - 1) {
        buffer.append(' ');
      }
    }
    return buffer.toString();
  }
  
  private static void decodeDoubleArray(String s, double[] array, int offset, int length) { int index = offset;
    int begin = 0;
    for (int lcv = 0; lcv < length; lcv++) {
      int end = s.indexOf(' ', begin);
      if (end == -1) {
        end = s.length();
      }
      String substr = s.substring(begin, end);
      array[(index++)] = Double.parseDouble(substr);
      begin = end + 1;
    }
  }
  
  private static String encodeDoubleArray(double[] array) { return encodeDoubleArray(array, 0, array.length); }
  

  private static void decodeDoubleArray(String s, double[] array) { decodeDoubleArray(s, array, 0, array.length); }
  
  private static String encodeTuple3d(javax.vecmath.Tuple3d tuple3d) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(Double.toString(x));
    buffer.append(' ');
    buffer.append(Double.toString(y));
    buffer.append(' ');
    buffer.append(Double.toString(z));
    return buffer.toString();
  }
  
  private static void decodeTuple3d(String s, javax.vecmath.Tuple3d tuple3d) { int begin = 0;
    int end = s.indexOf(' ', begin);
    x = Double.parseDouble(s.substring(begin, end));
    
    begin = end + 1;
    end = s.indexOf(' ', begin);
    y = Double.parseDouble(s.substring(begin, end));
    
    begin = end + 1;
    end = s.length();
    z = Double.parseDouble(s.substring(begin, end));
  }
  
  private static String encodeTuple2f(javax.vecmath.Tuple2f tuple2f) { StringBuffer buffer = new StringBuffer();
    buffer.append(Float.toString(x));
    buffer.append(' ');
    buffer.append(Float.toString(y));
    return buffer.toString();
  }
  
  private static void decodeTuple2f(String s, javax.vecmath.Tuple2f tuple2f) { int begin = 0;
    int end = s.indexOf(' ', begin);
    x = Float.parseFloat(s.substring(begin, end));
    
    begin = end + 1;
    end = s.length();
    y = Float.parseFloat(s.substring(begin, end));
  }
  

  private static String encodeEnumerable(Enumerable enumerable) { return enumerable.getRepr(); }
  
  private static Enumerable decodeEnumerable(Class cls, String s) {
    Enumerable[] array = Enumerable.getItems(cls);
    for (int i = 0; i < array.length; i++) {
      if (s.equals(array[i].getRepr())) {
        return array[i];
      }
    }
    return null;
  }
  

  private static String getKey(edu.cmu.cs.stage3.alice.scenegraph.Element element) { return Integer.toString(element.hashCode()); }
  
  private static org.w3c.dom.Element encodeElement(edu.cmu.cs.stage3.alice.scenegraph.Element element, Document document, String s, Dictionary elementsToBeEncoded) {
    org.w3c.dom.Element xmlElement = document.createElement(s);
    xmlElement.setAttribute("class", element.getClass().getName());
    xmlElement.setAttribute("key", getKey(element));
    Enumeration enum0 = Property.getProperties(element.getClass()).elements();
    while (enum0.hasMoreElements()) {
      Property property = (Property)enum0.nextElement();
      String propertyName = property.getMixedCaseName();
      if (!propertyName.equals("Parent"))
      {
        if (!propertyName.equals("Bonus"))
        {

          org.w3c.dom.Element xmlProperty = document.createElement("property");
          xmlProperty.setAttribute("name", propertyName);
          Object value = property.get(element);
          if (value != null) {
            Class propertyValueClass = value.getClass();
            if (edu.cmu.cs.stage3.alice.scenegraph.Element.class.isAssignableFrom(propertyValueClass)) {
              String key = Integer.toString(value.hashCode());
              xmlProperty.setAttribute("key", key);
              if (!Component.class.isAssignableFrom(propertyValueClass))
              {
                elementsToBeEncoded.put(key, value);
              }
            }
            else if (Matrix4d.class.isAssignableFrom(propertyValueClass)) {
              xmlProperty.setAttribute("class", "javax.vecmath.Matrix4d");
              Matrix4d m = (Matrix4d)value;
              double[] row = new double[4];
              for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
                org.w3c.dom.Element xmlRow = document.createElement("row");
                m.getRow(rowIndex, row);
                




                xmlRow.appendChild(document.createTextNode(encodeDoubleArray(row)));
                xmlProperty.appendChild(xmlRow);
              }
            } else if (javax.vecmath.Matrix3d.class.isAssignableFrom(propertyValueClass)) {
              xmlProperty.setAttribute("class", "javax.vecmath.Matrix3d");
              javax.vecmath.Matrix3d m = (javax.vecmath.Matrix3d)value;
              double[] row = new double[3];
              for (int rowIndex = 0; rowIndex < 3; rowIndex++) {
                org.w3c.dom.Element xmlRow = document.createElement("row");
                m.getRow(rowIndex, row);
                




                xmlRow.appendChild(document.createTextNode(encodeDoubleArray(row)));
                xmlProperty.appendChild(xmlRow);
              }
            } else if (java.awt.Image.class.isAssignableFrom(propertyValueClass)) {
              java.awt.Image image = (java.awt.Image)value;
              try {
                int width = edu.cmu.cs.stage3.image.ImageUtilities.getWidth(image);
                int height = edu.cmu.cs.stage3.image.ImageUtilities.getHeight(image);
                int[] pixels = edu.cmu.cs.stage3.image.ImageUtilities.getPixels(image, width, height);
                xmlProperty.setAttribute("class", "java.awt.Image");
                xmlProperty.setAttribute("width", Integer.toString(width));
                xmlProperty.setAttribute("height", Integer.toString(width));
                int pixelIndex = 0;
                for (int rowIndex = 0; rowIndex < height; rowIndex++) {
                  org.w3c.dom.Element xmlRow = document.createElement("row");
                  












                  xmlRow.appendChild(document.createTextNode(encodeIntArray(pixels, pixelIndex, width, true)));
                  pixelIndex += width;
                  xmlProperty.appendChild(xmlRow);
                }
              } catch (InterruptedException ie) {
                throw new RuntimeException();
              }
            } else if (Color.class.isAssignableFrom(propertyValueClass)) {
              xmlProperty.setAttribute("class", "edu.cmu.cs.stage3.alice.scenegraph.Color");
              Color color = (Color)value;
              org.w3c.dom.Element xmlRed = document.createElement("red");
              xmlRed.appendChild(document.createTextNode(Double.toString(red)));
              xmlProperty.appendChild(xmlRed);
              org.w3c.dom.Element xmlGreen = document.createElement("green");
              xmlGreen.appendChild(document.createTextNode(Double.toString(green)));
              xmlProperty.appendChild(xmlGreen);
              org.w3c.dom.Element xmlBlue = document.createElement("blue");
              xmlBlue.appendChild(document.createTextNode(Double.toString(blue)));
              xmlProperty.appendChild(xmlBlue);
              org.w3c.dom.Element xmlAlpha = document.createElement("alpha");
              xmlAlpha.appendChild(document.createTextNode(Double.toString(alpha)));
              xmlProperty.appendChild(xmlAlpha);
            } else if ([I.class.isAssignableFrom(propertyValueClass)) {
              int[] array = (int[])value;
              xmlProperty.setAttribute("class", "[I");
              xmlProperty.setAttribute("length", Integer.toString(array.length));
              xmlProperty.appendChild(document.createTextNode(encodeIntArray(array, false)));




            }
            else if ([D.class.isAssignableFrom(propertyValueClass)) {
              double[] array = (double[])value;
              xmlProperty.setAttribute("class", "[D");
              xmlProperty.setAttribute("length", Integer.toString(array.length));
              xmlProperty.appendChild(document.createTextNode(encodeDoubleArray(array)));





            }
            else if ([Ledu.cmu.cs.stage3.alice.scenegraph.Vertex3d.class.isAssignableFrom(propertyValueClass)) {
              xmlProperty.setAttribute("class", "[Ledu.cmu.cs.stage3.alice.scenegraph.Vertex3d;");
              Vertex3d[] array = (Vertex3d[])value;
              for (int i = 0; i < array.length; i++) {
                org.w3c.dom.Element xmlVertex = document.createElement("vertex");
                Vertex3d vertex = array[i];
                if (position != null) {
                  org.w3c.dom.Element xmlPosition = document.createElement("position");
                  xmlPosition.appendChild(document.createTextNode(encodeTuple3d(position)));
                  xmlVertex.appendChild(xmlPosition);
                }
                








                if (normal != null) {
                  org.w3c.dom.Element xmlNormal = document.createElement("normal");
                  xmlNormal.appendChild(document.createTextNode(encodeTuple3d(normal)));
                  xmlVertex.appendChild(xmlNormal);
                }
                










                if (textureCoordinate0 != null) {
                  org.w3c.dom.Element xmlTextureCoordinate0 = document.createElement("textureCoordinate0");
                  xmlTextureCoordinate0.appendChild(document.createTextNode(encodeTuple2f(textureCoordinate0)));
                  xmlVertex.appendChild(xmlTextureCoordinate0);
                }
                







                xmlProperty.appendChild(xmlVertex);
              }
            } else if (Enumerable.class.isAssignableFrom(propertyValueClass)) {
              xmlProperty.setAttribute("class", propertyValueClass.getName());
              xmlProperty.appendChild(document.createTextNode(encodeEnumerable((Enumerable)value)));
            } else {
              xmlProperty.setAttribute("class", propertyValueClass.getName());
              xmlProperty.appendChild(document.createTextNode(value.toString()));
            }
          }
          
          xmlElement.appendChild(xmlProperty);
        } }
    }
    return xmlElement;
  }
  
  private static org.w3c.dom.Element encodeComponent(Component component, Document document, String s, Dictionary map) {
    org.w3c.dom.Element xmlComponent = encodeElement(component, document, s, map);
    if ((component instanceof edu.cmu.cs.stage3.alice.scenegraph.Container)) {
      edu.cmu.cs.stage3.alice.scenegraph.Container container = (edu.cmu.cs.stage3.alice.scenegraph.Container)component;
      for (int i = 0; i < container.getChildCount(); i++) {
        xmlComponent.appendChild(encodeComponent(container.getChildAt(i), document, "child", map));
      }
    }
    return xmlComponent;
  }
  





  private static void internalStore(Component component, java.io.OutputStream os)
    throws NoSuchMethodException, java.lang.reflect.InvocationTargetException, IllegalAccessException, javax.xml.parsers.ParserConfigurationException, java.io.IOException, java.io.FileNotFoundException
  {
    javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
    javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.newDocument();
    Dictionary elementsToBeEncoded = new java.util.Hashtable();
    org.w3c.dom.Element rootNode = encodeComponent(component, document, "root", elementsToBeEncoded);
    rootNode.setAttribute("version", Double.toString(1.0D));
    
    Dictionary elementsAlreadyEncoded = new java.util.Hashtable();
    boolean atLeastOneElementHasBeenEncoded;
    do { atLeastOneElementHasBeenEncoded = false;
      Enumeration enum0 = elementsToBeEncoded.keys();
      while (enum0.hasMoreElements()) {
        Object key = enum0.nextElement();
        if (elementsAlreadyEncoded.get(key) == null)
        {

          edu.cmu.cs.stage3.alice.scenegraph.Element element = (edu.cmu.cs.stage3.alice.scenegraph.Element)elementsToBeEncoded.get(key);
          rootNode.appendChild(encodeElement(element, document, "element", elementsToBeEncoded));
          elementsAlreadyEncoded.put(key, element);
          atLeastOneElementHasBeenEncoded = true;
        }
      }
    } while (atLeastOneElementHasBeenEncoded);
    





    document.appendChild(rootNode);
    document.getDocumentElement().normalize();
    
    Class cls = document.getClass();
    Class[] parameterTypes = { java.io.OutputStream.class };
    Object[] args = { os };
    java.lang.reflect.Method method = cls.getMethod("write", parameterTypes);
    method.invoke(document, args);
  }
  
  public static void store(Component component, java.io.OutputStream os) {
    try { ByteArrayOutputStream baos = new ByteArrayOutputStream();
      internalStore(component, baos);
      baos.flush();
      byte[] ba = baos.toByteArray();
      ZipOutputStream zos;
      ZipOutputStream zos;
      if ((os instanceof ZipOutputStream)) {
        zos = (ZipOutputStream)os;
      } else {
        zos = new ZipOutputStream(os);
      }
      java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry("root.xml");
      zos.setMethod(8);
      zos.putNextEntry(zipEntry);
      zos.write(ba, 0, ba.length);
      zos.closeEntry();
      zos.flush();
      zos.finish();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
  
  public static void store(Component component, java.io.File file) {
    try { java.io.OutputStream os = new java.io.FileOutputStream(file);
      store(component, os);
      os.close();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }
  
  public static void store(Component component, String path) { store(component, new java.io.File(path)); }
  
  private static org.w3c.dom.Element getFirstChild(Node node, String tag)
  {
    Node childNode = node.getFirstChild();
    while (childNode != null) {
      if (((childNode instanceof org.w3c.dom.Element)) && 
        (childNode.getNodeName().equals(tag))) {
        return (org.w3c.dom.Element)childNode;
      }
      
      childNode = childNode.getNextSibling();
    }
    return null;
  }
  
  private static org.w3c.dom.Element[] getChildren(Node node, String tag) { Vector vector = new Vector();
    Node childNode = node.getFirstChild();
    while (childNode != null) {
      if (((childNode instanceof org.w3c.dom.Element)) && 
        (childNode.getNodeName().equals(tag))) {
        vector.addElement(childNode);
      }
      
      childNode = childNode.getNextSibling();
    }
    org.w3c.dom.Element[] array = new org.w3c.dom.Element[vector.size()];
    vector.copyInto(array);
    return array;
  }
  


  private static String getNodeText(Node node)
  {
    StringBuffer propertyTextBuffer = new StringBuffer();
    org.w3c.dom.NodeList children = node.getChildNodes();
    for (int j = 0; j < children.getLength(); j++) {
      org.w3c.dom.Text textNode = (org.w3c.dom.Text)children.item(j);
      propertyTextBuffer.append(textNode.getData().trim());
    }
    return propertyTextBuffer.toString();
  }
  
  private static Object valueOf(Class cls, String text) { if (String.class.isAssignableFrom(cls))
      return text;
    if ((cls.equals(Double.class)) && (text.equals("Infinity")))
      return new Double(Double.POSITIVE_INFINITY);
    if ((cls.equals(Double.class)) && (text.equals("NaN"))) {
      return new Double(NaN.0D);
    }
    Class[] parameterTypes = { String.class };
    try {
      java.lang.reflect.Method valueOfMethod = cls.getMethod("valueOf", parameterTypes);
      int modifiers = valueOfMethod.getModifiers();
      if ((java.lang.reflect.Modifier.isPublic(modifiers)) && (java.lang.reflect.Modifier.isStatic(modifiers))) {
        Object[] parameters = { text };
        return valueOfMethod.invoke(null, parameters);
      }
      throw new RuntimeException("valueOf method not public static.");
    }
    catch (NoSuchMethodException nsme) {
      throw new RuntimeException("NoSuchMethodException:" + cls + " " + text);
    } catch (IllegalAccessException iae) {
      throw new RuntimeException("IllegalAccessException: " + cls + " " + text);
    } catch (java.lang.reflect.InvocationTargetException ite) {
      throw new RuntimeException("java.lang.reflect.InvocationTargetException: " + cls + " " + text);
    }
  }
  
  private static edu.cmu.cs.stage3.alice.scenegraph.Element decodeElement(org.w3c.dom.Element xmlElement, Dictionary map, Vector referencesToBeResolved)
  {
    try {
      String classname = xmlElement.getAttribute("class");
      String elementKey = xmlElement.getAttribute("key");
      String elementName = xmlElement.getAttribute("name");
      Class cls = Class.forName(classname);
      edu.cmu.cs.stage3.alice.scenegraph.Element sgElement = (edu.cmu.cs.stage3.alice.scenegraph.Element)cls.newInstance();
      sgElement.setName(elementName);
      map.put(elementKey, sgElement);
      org.w3c.dom.Element[] xmlProperties = getChildren(xmlElement, "property");
      for (int propertyIndex = 0; propertyIndex < xmlProperties.length; propertyIndex++) {
        org.w3c.dom.Element xmlProperty = xmlProperties[propertyIndex];
        String propertyName = xmlProperty.getAttribute("name");
        Property sgProperty = Property.getPropertyMixedCaseNamed(cls, propertyName);
        if (sgProperty == null) {
          throw new RuntimeException("could not find property named: " + propertyName + " on " + cls + ".");
        }
        String propertyValueClassname = xmlProperty.getAttribute("class");
        if (propertyValueClassname.length() > 0) {
          Class propertyValueClass = Class.forName(propertyValueClassname);
          Object value;
          Object value; if (Matrix4d.class.isAssignableFrom(propertyValueClass)) {
            Matrix4d m = new Matrix4d();
            org.w3c.dom.Element[] xmlRows = getChildren(xmlProperty, "row");
            double[] row = new double[4];
            for (int rowIndex = 0; rowIndex < 4; rowIndex++)
            {



              decodeDoubleArray(getNodeText(xmlRows[rowIndex]), row);
              m.setRow(rowIndex, row);
            }
            value = m; } else { Object value;
            if (javax.vecmath.Matrix3d.class.isAssignableFrom(propertyValueClass)) {
              javax.vecmath.Matrix3d m = new javax.vecmath.Matrix3d();
              org.w3c.dom.Element[] xmlRows = getChildren(xmlProperty, "row");
              double[] row = new double[3];
              for (int rowIndex = 0; rowIndex < 3; rowIndex++)
              {



                decodeDoubleArray(getNodeText(xmlRows[rowIndex]), row);
                m.setRow(rowIndex, row);
              }
              value = m; } else { Object value;
              if (java.awt.Image.class.isAssignableFrom(propertyValueClass)) {
                int width = Integer.parseInt(xmlProperty.getAttribute("width"));
                int height = Integer.parseInt(xmlProperty.getAttribute("height"));
                org.w3c.dom.Element[] xmlRows = getChildren(xmlProperty, "row");
                int[] pixels = new int[width * height];
                int pixelIndex = 0;
                for (int rowIndex = 0; rowIndex < height; rowIndex++) {
                  String s = getNodeText(xmlRows[rowIndex]);
                  decodeIntArray(s, pixels, pixelIndex, width, true);
                  pixelIndex += width;
                }
                













                value = java.awt.Toolkit.getDefaultToolkit().createImage(new java.awt.image.MemoryImageSource(width, height, pixels, 0, width)); } else { Object value;
                if (Color.class.isAssignableFrom(propertyValueClass)) {
                  Color sgColor = new Color();
                  red = Float.parseFloat(getNodeText(getFirstChild(xmlProperty, "red")));
                  green = Float.parseFloat(getNodeText(getFirstChild(xmlProperty, "green")));
                  blue = Float.parseFloat(getNodeText(getFirstChild(xmlProperty, "blue")));
                  alpha = Float.parseFloat(getNodeText(getFirstChild(xmlProperty, "alpha")));
                  value = sgColor; } else { Object value;
                  if ([I.class.isAssignableFrom(propertyValueClass)) {
                    int length = Integer.parseInt(xmlProperty.getAttribute("length"));
                    int[] array = new int[length];
                    decodeIntArray(getNodeText(xmlProperty), array, false);
                    




                    value = array; } else { Object value;
                    if ([D.class.isAssignableFrom(propertyValueClass)) {
                      int length = Integer.parseInt(xmlProperty.getAttribute("length"));
                      double[] array = new double[length];
                      decodeDoubleArray(getNodeText(xmlProperty), array);
                      




                      value = array; } else { Object value;
                      if ([Ledu.cmu.cs.stage3.alice.scenegraph.Vertex3d.class.isAssignableFrom(propertyValueClass)) {
                        org.w3c.dom.Element[] xmlVertices = getChildren(xmlProperty, "vertex");
                        Vertex3d[] array = new Vertex3d[xmlVertices.length];
                        for (int vertexIndex = 0; vertexIndex < xmlVertices.length; vertexIndex++) {
                          Vertex3d vertex = new Vertex3d();
                          org.w3c.dom.Element xmlVertex = xmlVertices[vertexIndex];
                          org.w3c.dom.Element xmlPosition = getFirstChild(xmlVertex, "position");
                          if (xmlPosition != null) {
                            position = new javax.vecmath.Point3d();
                            decodeTuple3d(getNodeText(xmlPosition), position);
                          }
                          



                          org.w3c.dom.Element xmlNormal = getFirstChild(xmlVertex, "normal");
                          if (xmlNormal != null) {
                            normal = new javax.vecmath.Vector3d();
                            decodeTuple3d(getNodeText(xmlNormal), normal);
                          }
                          



                          org.w3c.dom.Element xmlTextureCoordinate0 = getFirstChild(xmlVertex, "textureCoordinate0");
                          if (xmlTextureCoordinate0 != null) {
                            textureCoordinate0 = new javax.vecmath.TexCoord2f();
                            decodeTuple2f(getNodeText(xmlTextureCoordinate0), textureCoordinate0);
                          }
                          

                          array[vertexIndex] = vertex;
                        }
                        value = array; } else { Object value;
                        if (Enumerable.class.isAssignableFrom(propertyValueClass)) {
                          value = decodeEnumerable(propertyValueClass, getNodeText(xmlProperty));
                        } else
                          value = valueOf(propertyValueClass, getNodeText(xmlProperty));
                      } } } } } } }
          sgProperty.set(sgElement, value);
        } else {
          String propertyValueKey = xmlProperty.getAttribute("key");
          if (propertyValueKey.length() > 0) {
            referencesToBeResolved.addElement(new PropertyReference(sgProperty, sgElement, propertyValueKey));
          } else {
            sgProperty.set(sgElement, null);
          }
        }
      }
      return sgElement;
    }
    catch (Throwable t) {
      t.printStackTrace();
      System.exit(0); }
    return null;
  }
  
  private static Component decodeComponent(org.w3c.dom.Element xmlComponent, Dictionary map, Vector referencesToBeResolved) {
    Component sgComponent = (Component)decodeElement(xmlComponent, map, referencesToBeResolved);
    org.w3c.dom.Element[] xmlChildren = getChildren(xmlComponent, "child");
    for (int i = 0; i < xmlChildren.length; i++) {
      decodeComponent(xmlChildren[i], map, referencesToBeResolved).setParent((edu.cmu.cs.stage3.alice.scenegraph.Container)sgComponent);
    }
    return sgComponent;
  }
  


  private static Component internalLoad(java.io.InputStream is)
    throws org.xml.sax.SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException
  {
    javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
    javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(is);
    org.w3c.dom.Element xmlRoot = document.getDocumentElement();
    
    Dictionary map = new java.util.Hashtable();
    Vector referencesToBeResolved = new Vector();
    Component sgRoot = decodeComponent(xmlRoot, map, referencesToBeResolved);
    
    org.w3c.dom.Element[] xmlElements = getChildren(xmlRoot, "element");
    for (int i = 0; i < xmlElements.length; i++) {
      decodeElement(xmlElements[i], map, referencesToBeResolved);
    }
    

    Enumeration enum0 = referencesToBeResolved.elements();
    while (enum0.hasMoreElements()) {
      PropertyReference propertyReference = (PropertyReference)enum0.nextElement();
      propertyReference.resolve(map);
    }
    return sgRoot;
  }
  
  public static Component load(java.io.InputStream is) {
    try { java.util.zip.ZipInputStream zis;
      java.util.zip.ZipInputStream zis;
      if ((is instanceof java.util.zip.ZipInputStream)) {
        zis = (java.util.zip.ZipInputStream)is;
      } else {
        zis = new java.util.zip.ZipInputStream(is);
      }
      java.util.zip.ZipEntry zipEntry = zis.getNextEntry();
      
      int BUFFER_SIZE = 2048;
      byte[] buffer = new byte['ࠀ'];
      ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
      for (;;) {
        int count = zis.read(buffer, 0, 2048);
        if (count == -1) {
          break;
        }
        baos.write(buffer, 0, count);
      }
      
      java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
      return internalLoad(bais);
    }
    catch (Throwable t) {
      t.printStackTrace();
      throw new RuntimeException(t.toString());
    }
  }
  
  public static Component load(java.io.File file) {
    try {
      return load(new java.io.FileInputStream(file));
    } catch (java.io.FileNotFoundException fnfe) {
      fnfe.printStackTrace(); }
    return null;
  }
}
