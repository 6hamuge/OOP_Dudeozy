package First;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DDayCalculator {
    public static long calculateDaysUntil(int year, int month, int day) throws Exception {
        String targetDay = String.format("%04d%02d%02d", year, month, day);
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
        Date targetDate = yyyyMMdd.parse(targetDay);
        Date todayDate = new Date();
        long gap = targetDate.getTime() - todayDate.getTime();
        return gap / (24 * 60 * 60 * 1000) + 1;
    }
}
