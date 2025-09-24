package au.koi.mms.model;

public class RegularMember extends Member {
    public RegularMember(String id, String name, String email, String phone,
                         double baseFee, int performanceRating,
                         boolean goalAchieved, int discountPercent) {
        super(id, name, email, phone, baseFee, performanceRating, goalAchieved, discountPercent, MemberType.REGULAR);
    }

    @Override
    public double calculateFee() {
        double fee = getBaseFee();
        if (isGoalAchieved()) fee = fee * (100 - getDiscountPercent()) / 100.0;
        if (getPerformanceRating() >= 5) fee = fee * 0.95;   // extra perk
        return round2(fee);
    }
}
