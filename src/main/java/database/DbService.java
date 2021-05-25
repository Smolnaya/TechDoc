package database;

public class DbService {
    public boolean intToBoolean(int val) {
        return val != 0;
    }

    public int booleanToInt(boolean val) {
        if (val) {
            return 1;
        } else return 0;
    }

    public float smToFloat(float val) {
        return val * 28.35f;
    }

    public float floatToSm(float val) {
        return val / 28.35f;
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
        try {
            float size = Float.parseFloat(value);
            return (size >= 1 && size <= 1638) && (size % 0.5 == 0);
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public boolean isIndentValue(String value) {
        try {
            float size = Float.parseFloat(value);
            return (size >= 1 && size <= 1638) && (size % 0.5 == 0);
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
