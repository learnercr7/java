package au.koi.mms.model;

public abstract class Member {
    private String id, name, email, phone;
    private double baseFee;
    private int performanceRating;        // 1..5
    private boolean goalAchieved;
    private int discountPercent;          // 0..100
    private MemberType type;
    protected Member(String id, String name, String email, String phone,
                     double baseFee, int performanceRating,
                     boolean goalAchieved, int discountPercent, MemberType type) {
        this.id = id; this.name = name; this.email = email; this.phone = phone;
        this.baseFee = baseFee; this.performanceRating = performanceRating;
        this.goalAchieved = goalAchieved; this.discountPercent = discountPercent;
        this.type = type;
    }
    public String getId(){ return id; }
    public String getName(){ return name; }
    public String getEmail(){ return email; }
    public String getPhone(){ return phone; }
    public double getBaseFee(){ return baseFee; }
    public int getPerformanceRating(){ return performanceRating; }
    public boolean isGoalAchieved(){ return goalAchieved; }
    public int getDiscountPercent(){ return discountPercent; }
    public MemberType getType(){ return type; }
    public void setName(String v){ name=v; }
    public void setEmail(String v){ email=v; }
    public void setPhone(String v){ phone=v; }
    public void setBaseFee(double v){ baseFee=v; }
    public void setPerformanceRating(int v){ performanceRating=v; }
    public void setGoalAchieved(boolean v){ goalAchieved=v; }
    public void setDiscountPercent(int v){ discountPercent=Math.max(0,Math.min(100,v)); }
    public abstract double calculateFee();
    public String toCsv(){
        return String.join(",",
                id, name, type.name(), email, phone,
                String.valueOf(baseFee),
                String.valueOf(performanceRating),
                String.valueOf(goalAchieved),
                String.valueOf(discountPercent));
    }
    @Override public String toString() {
        return id + " | " + name + " | " + type + " | fee $" + calculateFee();
    }
    protected static double round2(double v){ return Math.round(v*100.0)/100.0; }
}