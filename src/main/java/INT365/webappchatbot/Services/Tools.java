package INT365.webappchatbot.Services;

public class Tools {

    public static Boolean convertIntToBoolean(Integer i) {
        return i == 1;
    }

    public static Integer convertBooleanToInt(Boolean b) {
        return b ? 1 : 0;
    }
}
