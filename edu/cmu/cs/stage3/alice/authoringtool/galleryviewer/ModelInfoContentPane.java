package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.swing.ContentPane;
import edu.cmu.cs.stage3.util.StringObjectPair;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;









public class ModelInfoContentPane
  extends ContentPane
{
  GalleryViewer.ObjectXmlData data;
  ImageIcon image;
  JLabel imageLabel;
  JLabel nameLabel;
  JButton addObjectButton;
  JButton cancelButton;
  JPanel detailsPanel;
  String filename;
  
  public ModelInfoContentPane()
  {
    guiInit();
  }
  
  public String getTitle() {
    return GalleryObject.getDisplayName(data.name);
  }
  
  public void addOKActionListener(ActionListener l) {
    addObjectButton.addActionListener(l);
  }
  
  public void removeOKActionListener(ActionListener l) {
    addObjectButton.removeActionListener(l);
  }
  
  public void addCancelActionListener(ActionListener l) {
    cancelButton.addActionListener(l);
  }
  
  public void removeCancelActionListener(ActionListener l) {
    cancelButton.removeActionListener(l);
  }
  
  public void set(GalleryViewer.ObjectXmlData data, ImageIcon image) {
    this.data = data;
    this.image = image;
    if (directoryData != null) {
      filename = (String.valueOf(directoryData.rootNode.rootPath) + String.valueOf(objectFilename));
    } else if (parentDirectory != null)
      filename = (String.valueOf(parentDirectory.rootNode.rootPath) + String.valueOf(objectFilename));
    imageLabel.setIcon(image);
    setName(GalleryObject.getDisplayName(name));
    nameLabel.setText(GalleryObject.getDisplayName(name));
    buildDetails();
  }
  
  private void buildDetails() {
    int count = 0;
    detailsPanel.removeAll();
    JLabel size = new JLabel(Messages.getString("size_"));
    size.setForeground(GalleryViewer.textColor);
    JLabel sizeDetail = new JLabel(String.valueOf(String.valueOf(String.valueOf(data.size))).concat(" kb"));
    sizeDetail.setForeground(GalleryViewer.textColor);
    detailsPanel.add(size, new GridBagConstraints(0, count, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(2, 0, 0, 0), 0, 0));
    detailsPanel.add(sizeDetail, new GridBagConstraints(1, count, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(2, 4, 0, 0), 0, 0));
    count++;
    for (int i = 0; i < data.details.size(); i++) {
      StringObjectPair current = (StringObjectPair)data.details.get(i);
      String currentString = current.getString();
      if (currentString.equalsIgnoreCase(Messages.getString("modeledby"))) {
        currentString = Messages.getString("modeled_by");
      } else if (currentString.equalsIgnoreCase(Messages.getString("paintedby"))) {
        currentString = Messages.getString("painted_by");
      } else if (currentString.equalsIgnoreCase(Messages.getString("physicalsize")))
        currentString = Messages.getString("physical_size");
      JLabel title = new JLabel(String.valueOf(String.valueOf(currentString)).concat(":"));
      JLabel detail = new JLabel();
      title.setForeground(GalleryViewer.textColor);
      detail.setForeground(GalleryViewer.textColor);
      if ((current.getObject() != null) && (!current.getObject().equals("")))
      {
        if ((current.getObject() instanceof String)) {
          detail.setText(current.getObject().toString());
          detailsPanel.add(title, new GridBagConstraints(0, count, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(2, 0, 0, 0), 0, 0));
          detailsPanel.add(detail, new GridBagConstraints(1, count, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(2, 4, 0, 0), 0, 0));
          count++;

        }
        else if ((current.getObject() instanceof Vector))
        {
          Vector detailVector = (Vector)current.getObject();
          JPanel detailContainer = new JPanel();
          detailContainer.setOpaque(false);
          detailContainer.setBorder(null);
          detailContainer.setLayout(new BoxLayout(detailContainer, 1));
          for (int index = 0; index < detailVector.size(); index++) {
            if ((detailVector.get(index) != null) && ((detailVector.get(index) instanceof String)))
            {
              String currentDetail = (String)detailVector.get(index);
              if (!currentDetail.equalsIgnoreCase("")) {
                JLabel detailLabel = new JLabel(currentDetail);
                detailLabel.setForeground(GalleryViewer.textColor);
                detailContainer.add(detailLabel);
              }
            }
          }
          detailsPanel.add(title, new GridBagConstraints(0, count, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(2, 0, 0, 0), 0, 0));
          detailsPanel.add(detailContainer, new GridBagConstraints(1, count, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(2, 4, 0, 0), 0, 0));
          count++;
        } }
    }
    detailsPanel.add(Box.createVerticalGlue(), new GridBagConstraints(0, count, 1, 1, 1.0D, 1.0D, 18, 1, new Insets(0, 0, 0, 0), 0, 0));
  }
  
  private void guiInit() {
    setBackground(GalleryObject.BACKGROUND);
    imageLabel = new JLabel();
    nameLabel = new JLabel();
    nameLabel.setForeground(GalleryViewer.textColor);
    detailsPanel = new JPanel();
    detailsPanel.setOpaque(false);
    detailsPanel.setLayout(new GridBagLayout());
    addObjectButton = new JButton(Messages.getString("Add_instance_to_world"));
    cancelButton = new JButton(Messages.getString("Cancel"));
    setLayout(new GridBagLayout());
    add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(2, 0, 0, 0), 0, 0));
    add(imageLabel, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 20, 20, 20), 0, 0));
    add(addObjectButton, new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 18, 0, new Insets(2, 6, 6, 2), 0, 0));
    add(cancelButton, new GridBagConstraints(1, 2, 1, 1, 0.0D, 0.0D, 12, 0, new Insets(2, 2, 6, 6), 0, 0));
    add(detailsPanel, new GridBagConstraints(1, 1, 1, 1, 0.0D, 0.0D, 12, 0, new Insets(2, 2, 2, 6), 0, 0));
  }
  







  public String getFilename()
  {
    return filename;
  }
}
