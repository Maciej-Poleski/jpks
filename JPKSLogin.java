import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class JPKSLogin extends Applet
{
  //Button login;
  TextField name;
  LoginHandler handler;
  
  public void init()
  {
    handler = new LoginHandler();
    name = new TextField(20);
    add(name);
    name.addActionListener(handler);
    //login = new Button("Log in");
    //add(login);
    //login.addActionListener(handler);
  }
  
  class LoginHandler implements ActionListener
  {
    boolean verifyLogin(String nick)
    {
      for(int i=0; i<nick.length(); i++)
      {
        char c = nick.charAt(i);
	if(c != '_' && c != '.' && c != ' ' 
	&& !('a'<=c && c<='z') && !('A'<=c && c<='Z')
	&& !('0'<=c && c<='9'))
	  return false;
      }
      return true;
    }
    
    public void actionPerformed(ActionEvent e)
    {
      String nick = name.getText().trim();
      if(nick.equals("Niedozwolone znaki") ||
         nick.equals("Zbyt dlugi login") ||
	 nick.equals("JPKS") ||
         nick.equals("JPKS is down") ||
	 nick.equals("PKS")) return;
      int letterCnt = 0;
      for(int i=0; i<nick.length(); i++)
        if(Character.isLetter(nick.charAt(i))) letterCnt++;
      if(letterCnt<2) return;		 
      if(nick.length()>0)
      { 
        if(nick.length()<=20)
	{
	  if(verifyLogin(nick))
	  {
	    // Proba zalogowania sie
	    try
	    {
	      Socket sock = new Socket(getCodeBase().getHost(), 6666);
	      BufferedReader is = new BufferedReader(
	                         new InputStreamReader(sock.getInputStream()));
	      PrintWriter os = new PrintWriter(sock.getOutputStream(), true);
	      os.println("log" + nick);
	      new JPKSClient(nick, is, os);
	    }
	    catch(IOException ioe)
	    {
	      name.setText("JPKS is down");
	    }
	  }
	  else
	    name.setText("Niedozwolone znaki");    		
	}  
	else
	  name.setText("Zbyt dlugi login");  
      }
    }
  }
  
}