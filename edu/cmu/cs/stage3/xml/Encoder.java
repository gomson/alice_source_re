package edu.cmu.cs.stage3.xml;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;









public class Encoder
{
  public Encoder() {}
  
  public static void write(Document document, OutputStream os)
    throws IOException
  {
    BufferedOutputStream bos;
    BufferedOutputStream bos;
    if ((os instanceof BufferedOutputStream)) {
      bos = (BufferedOutputStream)os;
    } else {
      bos = new BufferedOutputStream(os);
    }
    OutputFormat outputFormat = new OutputFormat(document, "UTF-8", true);
    XMLSerializer xmlSerializer = new XMLSerializer(bos, outputFormat);
    xmlSerializer.serialize(document);
    bos.flush(); }
  
  public static void write(Document document, Writer w) throws IOException { BufferedWriter bw;
    BufferedWriter bw;
    if ((w instanceof BufferedWriter)) {
      bw = (BufferedWriter)w;
    } else {
      bw = new BufferedWriter(w);
    }
    OutputFormat outputFormat = new OutputFormat(document, "UTF-8", true);
    XMLSerializer xmlSerializer = new XMLSerializer(bw, outputFormat);
    xmlSerializer.serialize(document);
    bw.flush();
  }
}
