import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class JPKSClient extends JFrame
{
  JButton help,logout;
  JTextField input;
  JTextArea display,question,ranking;
  int displayLines = 0;
  int rankingLines = 0;
  final static int MAXDISPLAYLINES = 25;
  Container cp;
  String login;
  JLabel user;
  //Image im = getToolkit().getImage(getClass().getResource("89.jpg"));
  PrintWriter os;
  BufferedReader is;
  ActionListener handler = new ButtonHandler();
  ActionListener textHandler = new TextHandler();
  PaintDemo image;
  Image img;
  
  public JPKSClient(String nick, BufferedReader is, PrintWriter os)
  {
    super("JPKS Client: "+nick);
    login = nick;
    this.is = is;
    this.os = os;
/*
    try
    {
      sock = new Socket(server.getHost(), 6666);
      is = new BufferedReader(
           new InputStreamReader(sock.getInputStream()));
      os = new PrintWriter(sock.getOutputStream(), true);	   
      os.println("log" + login);
*/      	   
    cp = getContentPane();
    cp.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 100.0; gbc.weighty = 100.0;
        
    gbc.gridx=1; gbc.gridy=0;
    gbc.anchor = GridBagConstraints.CENTER;
    /*
    QuestImage qi = new QuestImage(); 
    cp.add(qi, gbc);
    */
    JPanel rp = new JPanel();
    rp.setLayout(new GridBagLayout());
    GridBagConstraints gbc2 = new GridBagConstraints();
    
    gbc2.gridx = 1; gbc2.gridy = 0;
    gbc2.anchor = GridBagConstraints.CENTER;
    rp.add(new JLabel("Chat", JLabel.LEFT), gbc2);
    
    gbc2.gridx = 1; gbc2.gridy = 1;
    gbc2.anchor = GridBagConstraints.CENTER;
    rp.add(display = new JTextArea(MAXDISPLAYLINES,60), gbc2);
    display.setEditable(false);
    //display.setFont(new Font("Monospaced", Font.PLAIN, 11));
    display.setForeground(Color.BLUE.darker().darker());
    
    gbc2.gridx = 1; gbc2.gridy = 2;
    gbc2.anchor = GridBagConstraints.CENTER;
    rp.add(new JLabel("Pytanie", JLabel.LEFT), gbc2);
    
    gbc2.gridx = 1; gbc2.gridy = 3;
    gbc2.anchor = GridBagConstraints.CENTER;    
    rp.add(question = new JTextArea(11,60), gbc2);
    question.setEditable(false);
    //question.setFont(new Font("Monospaced", Font.PLAIN, 11));
    question.setForeground(Color.BLACK);
    question.setBackground(new Color(255,255,240));
    
    //rp.add(new JLabel("Ranking", JLabel.LEFT), BorderLayout.CENTER);
    
    JPanel xp = new JPanel();
    xp.setLayout(new BorderLayout(0, 20));
    
    xp.add(ranking = new JTextArea(15,20), BorderLayout.NORTH);
    ranking.setEditable(false);
    ranking.setFont(new Font("Monospaced", Font.PLAIN, 11));
    ranking.setForeground(Color.GREEN.darker().darker());
    ranking.setBackground(new Color(240,255,240));
    
    gbc2.gridx = 1; gbc2.gridy = 4;
    gbc2.anchor = GridBagConstraints.CENTER;
    rp.add(new JLabel("Twoja odpowiedź", JLabel.LEFT), gbc2);
     
    gbc2.gridx = 1; gbc2.gridy = 5;
    gbc2.anchor = GridBagConstraints.CENTER;
    rp.add(input = new JTextField(60),gbc2);
    input.addActionListener(textHandler);    
    
    cp.add(rp, gbc);

    //xp.add(new JLabel("Test"), BorderLayout.CENTER);
    
    xp.add(image = new PaintDemo(), BorderLayout.SOUTH);
    image.color = cp.getBackground();
    image.draw = true;
    image.img = getToolkit().getImage(getClass().getResource("jpks.gif"));
    image.repaint();
        
    JPanel jp = new JPanel();
    jp.add(help = new JButton("Pomoc"));
    help.addActionListener(handler);
    jp.add(logout = new JButton("Zakoncz"));
    logout.addActionListener(handler);
    
    gbc.gridx = 2; gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.CENTER;
    xp.add(jp, BorderLayout.CENTER);
    
    cp.add(xp, gbc);
    
    setSize(1024,740);
    setVisible(true);
    image.repaint();
    input.requestFocus();
    
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        zakoncz();
	setVisible(false);
	dispose();
      }
    });    

    new Session().start();    
    
//    }
//    catch(IOException e)
//    {
    
//    }

  }
  
  void zakoncz()
  {
    os.println("out");
  }
  /*
  class QuestImage extends Container
  {
    public void paint(Graphics g)
    {
      g.drawImage(im, 0, 0, this);
    }
  }
  */
  
  class TextHandler implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
     /*
      String cmd = input.getText();
      displayLines++;
      if(displayLines==MAXDISPLAYLINES)
      {
        displayLines--;
        String s = display.getText();
        int pos = s.indexOf('\n');
	display.setText(s.substring(pos+1));
      }
      display.append(cmd+'\n');
      */
      String cmd = input.getText().trim();
      if(cmd.length()>0)
      {
        if(cmd.length()>60)
	  input.setText("Zbyt dlugi tekst");
	else
	{
          cmd = UTF8ASC(cmd);  
          os.println("ans" + cmd);
          input.setText("");
	}
      }	
    }
  }

  private static String UTF8ASC(String cmd)
  {
    cmd = cmd.replaceAll("ą", "%a");
    cmd = cmd.replaceAll("ć", "%c");
    cmd = cmd.replaceAll("ę", "%e");
    cmd = cmd.replaceAll("ł", "%l");
    cmd = cmd.replaceAll("ń", "%n");
    cmd = cmd.replaceAll("ó", "%o");
    cmd = cmd.replaceAll("ś", "%s");
    cmd = cmd.replaceAll("ź", "%x");
    cmd = cmd.replaceAll("ż", "%z");
    cmd = cmd.replaceAll("Ą", "%A");
    cmd = cmd.replaceAll("Ć", "%C");
    cmd = cmd.replaceAll("Ę", "%E");
    cmd = cmd.replaceAll("Ł", "%L");
    cmd = cmd.replaceAll("Ń", "%N");
    cmd = cmd.replaceAll("Ó", "%O");
    cmd = cmd.replaceAll("Ś", "%S");
    cmd = cmd.replaceAll("Ź", "%X");
    cmd = cmd.replaceAll("Ż", "%Z");
    return cmd;
  }
  
  private static String ASCUTF8(String cmd)
  {
    cmd = cmd.replaceAll("%a", "ą");
    cmd = cmd.replaceAll("%c", "ć");
    cmd = cmd.replaceAll("%e", "ę");
    cmd = cmd.replaceAll("%l", "ł");
    cmd = cmd.replaceAll("%n", "ń");
    cmd = cmd.replaceAll("%o", "ó");
    cmd = cmd.replaceAll("%s", "ś");
    cmd = cmd.replaceAll("%x", "ź");
    cmd = cmd.replaceAll("%z", "ż");
    cmd = cmd.replaceAll("%A", "Ą");
    cmd = cmd.replaceAll("%C", "Ć");
    cmd = cmd.replaceAll("%E", "Ę");
    cmd = cmd.replaceAll("%L", "Ł");
    cmd = cmd.replaceAll("%N", "Ń");
    cmd = cmd.replaceAll("%O", "Ó");
    cmd = cmd.replaceAll("%S", "Ś");
    cmd = cmd.replaceAll("%X", "Ź");
    cmd = cmd.replaceAll("%Z", "Ż");
    return cmd;
  }
  
  class ButtonHandler implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      if(e.getSource() == help)
      {
        new HelpWindow();
      }                    
      if(e.getSource() == logout)
      {
        os.println("out");
        setVisible(false);
	dispose();
      }
    }
  }

  class Session extends Thread
  {
    void displayAppend(String cmd)
    {
      displayLines++;
      if(displayLines==MAXDISPLAYLINES)
      {
        displayLines--;
        String s = display.getText();
        int pos = s.indexOf('\n');
	display.setText(s.substring(pos+1));
      }
      display.append(cmd+'\n');
    }
    
    void rankingAppend(String cmd)
    {
      rankingLines++;
      if(rankingLines>10) return;
      ranking.append(cmd+'\n');
    }
    
    public void run()
    {
      try
      {
        image.draw = true;
	image.repaint();
        String line;
        while((line = is.readLine())!=null)
	{
          line = ASCUTF8(line);
	  if(line.equals("cle"))
	  {
	    question.setText("");
	  }
	  else if(line.equals("pre"))
	  {
	    //question.setText("");
	  }
	  else if(line.startsWith("que"))
	  {	  
	    if(question.getForeground().equals(Color.BLACK))
	    {
	      question.setForeground(Color.RED);
	      question.setText("");
	      image.img = img;
	      image.draw = true;
	      image.repaint();
	    }  
	    question.append(line.substring(3)+"\n");
	  }   
	  else if(line.equals("non"))
	    question.setForeground(Color.BLACK); 
	  else if(line.startsWith("cnt"))
	  {
	    question.append(line.substring(3)+"...");
	  }    
	  else if(line.startsWith("txt"))
	    displayAppend(line.substring(3));
	  else if(line.equals("rpr"))
	  {
	    rankingLines = 0;
	    ranking.setText("Ranking TOP 10:\n\n");
	  }    
	  else if(line.startsWith("rnk"))
	  {
	    rankingAppend(line.substring(3));
	    //img = getToolkit().getImage(getClass().getResource("jpks.gif"));
	    //image.draw = false;
	  }
          else if(line.startsWith("lib"))
	  {
	    question.setForeground(Color.BLACK);
	    question.append("\n" + "Wzorcowa odpowiedz: " + 
	                    line.substring(3) + "\n");  
	  }
	  else if(line.startsWith("pkt"))
	    question.append("Punkt zdobywa: " + line.substring(3) + "\n");
          else if(line.equals("rep"))
	  {
	    image.draw = true;
	    image.repaint();
	  }
          else if(line.startsWith("img"))
	  {
	    //displayAppend(line.substring(3));
	    img = getToolkit().getImage(getClass().getResource("img/"+line.substring(3)));
	  }	    
	  else
	    displayAppend(line);
	}
      }
      catch(IOException e)
      {
      }
    }
  }
}
