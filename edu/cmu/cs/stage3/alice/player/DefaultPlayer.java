package edu.cmu.cs.stage3.alice.player;

import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.swing.DialogManager;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.NumberFormat;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.metal.MetalSliderUI;

public class DefaultPlayer extends AbstractPlayer
{
  public static Class rendererClass = null;
  public static DefaultPlayer player = new DefaultPlayer(rendererClass);
  

  public DefaultPlayer(Class rendererClass) { super(rendererClass); }
  
  private Vector m_frames = new Vector();
  
  protected boolean isPreserveAndRestoreRequired() {
    return true;
  }
  
  protected int getDesiredFrameWidth() {
    return 800;
  }
  
  protected int getDesiredFrameHeight() { return 600; }
  

  private JButton m_pauseButton = new JButton("pause");
  private JButton m_resumeButton = new JButton("resume");
  private JButton m_startButton = new JButton("restart");
  private JButton m_stopButton = new JButton("stop");
  private JSlider speedSlider;
  private JLabel speedLabel;
  
  public void updateSpeed(double newSpeed) {
    player.setSpeed(newSpeed);
    String speedText = NumberFormat.getInstance()
      .format(newSpeed);
    if (newSpeed < 1.0D) {
      if (newSpeed == 0.5D) {
        speedText = "1/2";
      } else if (newSpeed == 0.25D) {
        speedText = "1/4";
      } else if (newSpeed == 0.2D) {
        speedText = "1/5";
      } else if ((newSpeed > 0.3D) && (newSpeed < 0.34D)) {
        speedText = "1/3";
      } else if ((newSpeed > 0.16D) && (newSpeed < 0.168D)) {
        speedText = "1/6";
      } else if ((newSpeed > 0.14D) && (newSpeed < 0.143D)) {
        speedText = "1/7";
      }
    }
    speedLabel.setText(" Speed: " + speedText + "x");
    speedLabel.repaint();
  }
  
  protected void handleRenderTarget(RenderTarget renderTarget) { JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout());
    
    m_pauseButton.setEnabled(true);
    m_resumeButton.setEnabled(false);
    m_startButton.setEnabled(true);
    m_stopButton.setEnabled(true);
    
    speedLabel = new JLabel("  Speed: 1x    ");
    speedLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
    speedLabel.setSize(new Dimension(100, 12));
    


    speedSlider = new JSlider(0, 9, 0);
    
    speedSlider.setUI(new MetalSliderUI() {
      public void paintTrack(Graphics g) {
        super.paintTrack(g);

      }
      

    });
    speedSlider.setSnapToTicks(true);
    speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(ChangeEvent ce) {
        JSlider s = (JSlider)ce.getSource();
        int value = s.getValue();
        if (value >= 0) {
          updateSpeed(value + 1.0D);
        } else if (value < 0) {
          updateSpeed(1.0D / (1 + value * -1));
        }
        
      }
    });
    buttonPanel.add(speedLabel);
    buttonPanel.add(speedSlider);
    buttonPanel.add(m_pauseButton);
    buttonPanel.add(m_resumeButton);
    buttonPanel.add(m_startButton);
    buttonPanel.add(m_stopButton);
    
    Frame frame = new Frame("Alice .a2w player");
    
    frame.setSize(getDesiredFrameWidth(), getDesiredFrameHeight());
    frame.setLayout(new java.awt.BorderLayout());
    frame.add(buttonPanel, "North");
    frame.add(renderTarget.getAWTComponent(), "Center");
    frame.setVisible(true);
    frame.addWindowListener(new java.awt.event.WindowAdapter()
    {
      public void windowClosing(WindowEvent ev) {
        m_frames.removeElement(ev.getSource());
        if (m_frames.size() == 0) {
          System.exit(0);
        }
        
      }
    });
    m_pauseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        m_pauseButton.setEnabled(false);
        m_resumeButton.setEnabled(true);
        DefaultPlayer.player.pauseWorld();
      }
      
    });
    m_resumeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        m_pauseButton.setEnabled(true);
        m_resumeButton.setEnabled(false);
        DefaultPlayer.player.resumeWorld();
      }
      
    });
    m_stopButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        m_startButton.setText("start");
        m_pauseButton.setEnabled(false);
        m_stopButton.setEnabled(false);
        m_resumeButton.setEnabled(false);
        DefaultPlayer.player.stopWorld();
      }
      
    });
    m_startButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DefaultPlayer.player.stopWorldIfNecessary();
        m_startButton.setText("restart");
        m_pauseButton.setEnabled(true);
        m_stopButton.setEnabled(true);
        m_resumeButton.setEnabled(false);
        DefaultPlayer.player.startWorld();
      }
      
    });
    m_frames.add(frame);
    DialogManager.initialize(frame);
  }
  
  private static File getFileFromArgs(String[] args, int startFrom) {
    File file = null;
    String path = "";
    int i = startFrom;
    while (i < args.length) {
      path = path + args[i];
      i++;
      file = new File(path);
      if (file.exists()) {
        break;
      }
      path = path + " ";
      file = null;
    }
    
    return file;
  }
  
  public static void main(String[] args) throws java.net.URISyntaxException
  {
    File file = null;
    if (args.length > 0) {
      int startFrom = 1;
      if (args[0].equals("-directx")) {
        rendererClass = edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer.class;



      }
      else if (args[0].equals("-jogl")) {
        rendererClass = edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer.class;
      } else if (args[0].equals("-null")) {
        rendererClass = edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer.Renderer.class;
      } else {
        System.err.println(args[0]);
        startFrom = 0;
      }
      file = getFileFromArgs(args, startFrom);
    }
    if (file == null) {
      String temp = player.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().toString();
      temp = temp.substring(5, temp.lastIndexOf(".")) + ".a2w";
      file = new File(temp);
      if (!file.exists()) {
        Frame frame = new Frame();
        FileDialog fd = new FileDialog(frame);
        fd.setVisible(true);
        
        if ((fd.getDirectory() != null) && (fd.getFile() != null)) {
          file = new File(fd.getDirectory() + fd.getFile());
        }
        frame.dispose();
      }
    }
    try
    {
      player.loadWorld(file, new edu.cmu.cs.stage3.progress.ProgressObserver() {
        private int i = 0;
        private int n = 80;
        private int m_total = -1;
        
        public void progressBegin(int total) { progressUpdateTotal(total); }
        

        public void progressUpdateTotal(int total) { m_total = total; }
        
        public void progressUpdate(int current, String description) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
          if (m_total == -1) {
            System.out.print("?");
          }
          else if (i < (int)(current / m_total * n)) {
            System.out.print(".");
            i += 1;
          }
        }
        

        public void progressEnd() {}
      });
      player.startWorld();
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
