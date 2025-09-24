package au.koi.mms.util;
import au.koi.mms.ui.ConsoleApp;
import au.koi.mms.gui.GuiApp;
import javax.swing.*;
import java.util.Scanner;

public class Main {
  public static void main(String[] args){
    System.out.println("Choose mode: 1=GUI, 2=TBI");
    System.out.print("Enter 1 or 2: ");
    Scanner scanner = new Scanner(System.in);
    try{
      String in=scanner.nextLine().trim();
      if("1".equals(in)) SwingUtilities.invokeLater(()-> new GuiApp().setVisible(true));
      else new ConsoleApp().run();
    } finally {
      try{ scanner.close(); } catch(Exception ignore){}
    }
  }
}
