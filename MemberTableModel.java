package au.koi.mms.gui;
import au.koi.mms.model.Member;
import au.koi.mms.repo.MemberRepository;
import javax.swing.table.AbstractTableModel;
import java.util.*;

public class MemberTableModel extends AbstractTableModel {
  private final MemberRepository repo;
  private List<Member> data;
  private final String[] cols={"ID","Name","Type","Email","Phone","BaseFee","Rating","Achieved","Discount","Fee"};
  public MemberTableModel(MemberRepository repo){ this.repo=repo; this.data=new ArrayList<>(repo.findAll()); }
  public void refresh(){ this.data=new ArrayList<>(repo.findAll()); fireTableDataChanged(); }
  public void setData(List<Member> d){ this.data=new ArrayList<>(d); fireTableDataChanged(); }
  public Member getAt(int r){ return data.get(r); }
  @Override public int getRowCount(){ return data.size(); }
  @Override public int getColumnCount(){ return cols.length; }
  @Override public String getColumnName(int c){ return cols[c]; }
  @Override public Object getValueAt(int r,int c){
    Member m=data.get(r);
    switch(c){
      case 0: return m.getId();
      case 1: return m.getName();
      case 2: return m.getType();
      case 3: return m.getEmail();
      case 4: return m.getPhone();
      case 5: return m.getBaseFee();
      case 6: return m.getPerformanceRating();
      case 7: return m.isGoalAchieved();
      case 8: return m.getDiscountPercent();
      case 9: return m.calculateFee();
      default: return "";
    }
  }
}
