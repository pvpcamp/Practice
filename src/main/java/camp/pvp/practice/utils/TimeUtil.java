package camp.pvp.practice.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static String get(Date date) {
        long duration = new Date().getTime() - date.getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;

        return fromMinSec(minutes, seconds);
    }

    public static String get(Date date1, Date date2) {
        long duration = date1.getTime() - date2.getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;

        return fromMinSec(minutes, seconds);
    }

    public static String get(long time) {
        long duration = System.currentTimeMillis() - time;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;

        return fromMinSec(minutes, seconds);
    }

    public static String fromMinSec(long min, long sec) {
        String m = String.valueOf(min);
        String s = String.valueOf(sec);

        if(m.toCharArray().length < 2) {
            m = "0" + m;
        }

        if(s.toCharArray().length < 2) {
            s = "0" + s;
        }

        return m + ":" + s;
    }
}
