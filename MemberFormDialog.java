package au.koi.mms.gui;
import au.koi.mms.model.*; import javax.swing.*; import java.awt.*; import java.util.Optional;

public class MemberFormDialog extends JDialog {
  private final JTextField tfId=new JTextField(10), tfName=new JTextField(20), tfEmail=new JTextField(20), tfPhone=new JTextField(15), tfBase=new JTextField(8);
  private final JComboBox<MemberType> cbType=new JComboBox<>(MemberType.values());
  private final JComboBox<Integer> cbRating=new JComboBox<>(new Integer[]{1,2,3,4,5});
  private final JCheckBox chkGoal=new JCheckBox("Goal achieved");
  private final JSpinner spDisc=new JSpinner(new SpinnerNumberModel(0,0,100,1));
  private Optional<Member> result=Optional.empty();

  public MemberFormDialog(Frame owner, Member existing){
    super(owner,true);
    setTitle(existing==null?"Add Member":"Edit Member");
    setLayout(new GridLayout(0,2,6,6));
    add(new JLabel("ID:")); add(tfId);
    add(new JLabel("Name:")); add(tfName);
    add(new JLabel("Type:")); add(cbType);
    add(new JLabel("Email:")); add(tfEmail);
    add(new JLabel("Phone:")); add(tfPhone);
    add(new JLabel("Base Fee:")); add(tfBase);
    add(new JLabel("Rating:")); add(cbRating);
    add(chkGoal); add(new JLabel(""));
    add(new JLabel("Discount %:")); add(spDisc);
    JButton ok=new JButton("OK"), cancel=new JButton("Cancel");
    add(ok); add(cancel);

    if(existing!=null){
      tfId.setText(existing.getId()); tfId.setEnabled(false);
      tfName.setText(existing.getName()); cbType.setSelectedItem(existing.getType());
      tfEmail.setText(existing.getEmail()); tfPhone.setText(existing.getPhone());
      tfBase.setText(String.valueOf(existing.getBaseFee()));
      cbRating.setSelectedItem(existing.getPerformanceRating()); chkGoal.setSelected(existing.isGoalAchieved());
      spDisc.setValue(existing.getDiscountPercent());
    }

    ok.addActionListener(e->{
      try{
        MemberType t=(MemberType)cbType.getSelectedItem();
        Member m = (t==MemberType.PT)
          ? new PTMember(tfId.getText().trim(), tfName.getText().trim(), tfEmail.getText().trim(), tfPhone.getText().trim(),
                         Double.parseDouble(tfBase.getText().trim()), (Integer)cbRating.getSelectedItem(), chkGoal.isSelected(), (Integer)spDisc.getValue())
          : new RegularMember(tfId.getText().trim(), tfName.getText().trim(), tfEmail.getText().trim(), tfPhone.getText().trim(),
                         Double.parseDouble(tfBase.getText().trim()), (Integer)cbRating.getSelectedItem(), chkGoal.isSelected(), (Integer)spDisc.getValue());
        result=Optional.of(m); dispose();
      }catch(Exception ex){ JOptionPane.showMessageDialog(this,"Invalid input: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    });
    cancel.addActionListener(e->{ result=Optional.empty(); dispose(); });
    pack(); setLocationRelativeTo(owner);
  }
  public Optional<Member> result(){ return result; }
}
