package au.koi.mms.gui;

import au.koi.mms.model.Member;
import au.koi.mms.repo.InMemoryMemberRepository;
import au.koi.mms.repo.MemberRepository;
import au.koi.mms.service.MemberService;
import au.koi.mms.service.PerformanceService;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.*;

public class GuiApp extends JFrame {
  private final MemberRepository repo = new InMemoryMemberRepository();
  private final MemberService memberService = new MemberService(repo);
  private final PerformanceService perfService = new PerformanceService(repo);
  private final MemberTableModel tableModel = new MemberTableModel(repo);
  private final JTable table = new JTable(tableModel);
  private Path currentFile = Paths.get("member_data.csv");

  public GuiApp(){
    super("Member Management System");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(900,600); setLocationRelativeTo(null);
    setLayout(new BorderLayout());
    add(new JScrollPane(table), BorderLayout.CENTER);
    setJMenuBar(menu()); add(toolbar(), BorderLayout.NORTH);
  }

  private JMenuBar menu(){
    JMenuBar mb=new JMenuBar(); JMenu f=new JMenu("File");
    JMenuItem o=new JMenuItem("Open CSV..."), s=new JMenuItem("Save As..."), x=new JMenuItem("Exit");
    o.addActionListener(e->open()); s.addActionListener(e->saveAs()); x.addActionListener(e->dispose());
    f.add(o); f.add(s); f.addSeparator(); f.add(x); mb.add(f); return mb;
  }
  private JToolBar toolbar(){
    JToolBar tb=new JToolBar();
    JButton add=new JButton("Add"), edit=new JButton("Edit"), del=new JButton("Delete"),
            sortN=new JButton("Sort Name"), sortF=new JButton("Sort Fee"),
            find=new JButton("Find ID"), letters=new JButton("Letters"), disc=new JButton("Award 10%");
    add.addActionListener(e->doAdd()); edit.addActionListener(e->doEdit()); del.addActionListener(e->doDelete());
    sortN.addActionListener(e->tableModel.setData(memberService.sortByNameMerge()));
    sortF.addActionListener(e->tableModel.setData(memberService.sortByFeeQuick()));
    find.addActionListener(e->doFind());
    letters.addActionListener(e->doLetters());
    disc.addActionListener(e->doDiscounts());
    for (JButton b:new JButton[]{add,edit,del,sortN,sortF,find,letters,disc}) tb.add(b);
    return tb;
  }

  private void open(){
    JFileChooser fc=new JFileChooser(currentFile.toFile());
    if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
      currentFile=fc.getSelectedFile().toPath();
      try{ repo.clear(); repo.loadFromCsv(currentFile); tableModel.refresh();
        JOptionPane.showMessageDialog(this,"Loaded "+repo.size()+" records.");
      }catch(IOException ex){ JOptionPane.showMessageDialog(this,"Load error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    }
  }
  private void saveAs(){
    JFileChooser fc=new JFileChooser(currentFile.toFile());
    if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
      try{ repo.saveToCsv(fc.getSelectedFile().toPath());
        JOptionPane.showMessageDialog(this,"Saved "+repo.size()+" records.");
      }catch(IOException ex){ JOptionPane.showMessageDialog(this,"Save error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    }
  }
  private void doAdd(){
    MemberFormDialog dlg=new MemberFormDialog(this,null); dlg.setVisible(true);
    dlg.result().ifPresent(m->{ repo.add(m); tableModel.refresh(); });
  }
  private void doEdit(){
    int r=table.getSelectedRow(); if(r<0){ JOptionPane.showMessageDialog(this,"Select a row."); return; }
    Member m=tableModel.getAt(r);
    MemberFormDialog dlg=new MemberFormDialog(this,m); dlg.setVisible(true);
    dlg.result().ifPresent(updated->{ repo.update(updated); tableModel.refresh(); });
  }
  private void doDelete(){
    int r=table.getSelectedRow(); if(r<0){ JOptionPane.showMessageDialog(this,"Select a row."); return; }
    if(JOptionPane.showConfirmDialog(this,"Delete selected member?","Confirm",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
      repo.deleteById(tableModel.getAt(r).getId()); tableModel.refresh();
    }
  }
  private void doFind(){
    String id=JOptionPane.showInputDialog(this,"Enter ID:"); if(id==null||id.trim().isEmpty()) return;
    java.util.Optional<Member> res=memberService.binarySearchById(id); JOptionPane.showMessageDialog(this, res.map(Object::toString).orElse("Not found"));
  }
  private void doLetters(){
    perfService.enqueueMonthlyLetters();
    try{ perfService.saveLettersToFolder("letters"); JOptionPane.showMessageDialog(this,"Letters saved to /letters"); }
    catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
  }
  private void doDiscounts(){
    int n=0; for(Member m:repo.findAll()){ if(m.isGoalAchieved() && m.getDiscountPercent()<10){ m.setDiscountPercent(10); n++; } }
    tableModel.refresh(); JOptionPane.showMessageDialog(this,"Awarded to "+n+" members.");
  }
}
