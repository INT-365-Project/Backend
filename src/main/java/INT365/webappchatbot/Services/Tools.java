package INT365.webappchatbot.Services;

public class Tools {

    public static Boolean convertIntToBoolean(Integer i) {
        return i == 1;
    }

    public static Integer convertBooleanToInt(Boolean b) {
        return b ? 1 : 0;
    }

    public static String randomFileNameNumber() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        int max = alphabet.length() - 1;
        int min = 0;
        int range = max - min + 1;
        for (int i = 0; i < 8; i++) {
            int randomNumber = (int) (Math.random() * range) + min;
            stringBuilder.append(alphabet.charAt(randomNumber));
        }
        return stringBuilder.toString();
    }
}
