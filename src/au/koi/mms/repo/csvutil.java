package au.koi.mms.repo;

import au.koi.mms.model.Member;
import au.koi.mms.model.PTMember;
import au.koi.mms.model.RegularMember;

final class CsvUtil {
    private CsvUtil(){}

    static boolean toBool(String s){
        String v = s.trim().toLowerCase();
        return v.equals("true") || v.equals("t") || v.equals("yes") || v.equals("y");
    }
    static int toInt(String s){ return Integer.parseInt(s.trim()); }
    static double toDouble(String s){ return Double.parseDouble(s.trim()); }

    // id,name,type,email,phone,baseFee,performanceRating,goalAchieved,discountPercent
    static Member fromFields(String[] t){
        if (t == null || t.length < 9)
            throw new IllegalArgumentException("CSV row has < 9 fields");
        String id=t[0].trim(), name=t[1].trim(), type=t[2].trim();
        String email=t[3].trim(), phone=t[4].trim();
        double base = toDouble(t[5]);
        int pr = toInt(t[6]);
        boolean goal = toBool(t[7]);
        int disc = toInt(t[8]);

        if (type.equalsIgnoreCase("PT") ||
            type.equalsIgnoreCase("TRAINER") ||
            type.equalsIgnoreCase("PTMEMBER")) {
            return new PTMember(id,name,email,phone,base,pr,goal,disc);
        }
        return new RegularMember(id,name,email,phone,base,pr,goal,disc);
    }
}
