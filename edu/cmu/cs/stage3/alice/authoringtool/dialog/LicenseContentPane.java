package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


















public class LicenseContentPane
  extends ContentPane
{
  private JButton m_okButton = new JButton(
    Messages.getString("OK"));
  
  public LicenseContentPane() {
    String text = "";
    try {
      URL urlToDictionary = getClass().getResource("license.txt");
      InputStream stream = urlToDictionary.openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(stream));
      try {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        
        while (line != null) {
          sb.append(line);
          sb.append("\n");
          line = br.readLine();
        }
        text = sb.toString();
      } finally {
        br.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    int width = 700;
    setPreferredSize(new Dimension(width, 500));
    JTextArea headerTextArea = new JTextArea();
    headerTextArea.setText("Please read the following license agreement carefully.");
    headerTextArea.setEditable(false);
    headerTextArea.setLineWrap(true);
    headerTextArea.setWrapStyleWord(true);
    headerTextArea.setOpaque(false);
    
    JTextArea textArea = new JTextArea();
    textArea.setText(text);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setMargin(new Insets(10, 10, 10, 10));
    
    JScrollPane scrollPane = new JScrollPane(textArea);
    

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, 2));
    buttonPane.add(Box.createHorizontalGlue());
    buttonPane.add(m_okButton);
    buttonPane.add(Box.createHorizontalGlue());
    
    headerTextArea.setAlignmentX(0.0F);
    scrollPane.setAlignmentX(0.0F);
    buttonPane.setAlignmentX(0.0F);
    
    BoxLayout boxLayout = new BoxLayout(this, 3);
    setLayout(boxLayout);
    add(headerTextArea);
    add(scrollPane);
    add(buttonPane);
  }
  
  public String getTitle() {
    return Messages.getString("Alice_license");
  }
  
  public void addOKActionListener(ActionListener l) {
    m_okButton.addActionListener(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    m_okButton.removeActionListener(l);
  }
}
