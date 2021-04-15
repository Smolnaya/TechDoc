package database;

public class DbService {
    public boolean toBoolean(int val) {
        return val != 0;
    }

    // Word: от 1 до 1638, кратный 0,5 (например 10,5 или 105,5).
    public boolean isSizeValue(String value) {
        try {
            float size = Float.parseFloat(value);
            return (size >= 1 && size <= 1638) && (size % 0.5 == 0);
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public boolean isLineSpaceValue(String value) {
        return true;
    }
}
