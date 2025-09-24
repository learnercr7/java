package au.koi.mms.model;

public class PTMember extends Member {
  public PTMember(String id, String name, String email, String phone,
                  double baseFee, int performanceRating, boolean goalAchieved, int discountPercent){
    super(id,name,email,phone,baseFee,performanceRating,goalAchieved,discountPercent);
  }

  @Override public String getType(){ return "PT"; }

  @Override public double calculateFee(){
    double fee = getBaseFee();
    // PT members get an extra 5% off if they achieved their goal
    int totalDiscount = getDiscountPercent() + (isGoalAchieved() ? 5 : 0);
    fee = fee * (1.0 - Math.max(0, Math.min(100, totalDiscount))/100.0);
    return Math.max(0.0, fee);
  }
}
