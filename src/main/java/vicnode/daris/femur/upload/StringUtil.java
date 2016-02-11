package vicnode.daris.femur.upload;

public class StringUtil {

    public static String trimNonDigits(String s) {
        if (s == null) {
            return null;
        }
        s = trimLeadingNonDigits(s);
        if (s != null) {
            s = trimTrailingNonDigits(s);
        }
        return s;
    }

    public static String trimLeadingNonDigits(String s) {
        if (s == null) {
            return null;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                return s.substring(i);
            }
        }
        return null;
    }

    public static String trimTrailingNonDigits(String s) {
        if (s == null) {
            return null;
        }
        for (int i = s.length() - 1; i > 0; i--) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                return s.substring(0, i + 1);
            }
        }
        return null;
    }

    public static String substringAfter(String s, String cs) {
        int idx = s.indexOf(cs);
        System.out.println(s);
        System.out.println(cs);
        System.out.println(idx);
        if (idx >= 0) {
            String ss = s.substring(idx + cs.length());
            System.out.println(ss);
            return ss;
        } else {
            System.out.println("null");
            return null;
        }
    }

    public static void main(String[] args) {
        // System.out.println(trimNonDigits("mfc597p"));
        System.out.println(substringAfter(
                "/mnt/VolumeStorage/Femur/MFC Xtreme pQCT data 2014 etc/MFC_BATCH3/370-00004442-00016303/Original_DCM/BMP",
                "/Femur/"));
    }
}
