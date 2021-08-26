package software.amazon.kendra.datasource.convert;

public class NumberConverter {

    public static Float doubleToFloat(Double val) {
        return val == null ? null : val.floatValue();
    }

    public static Double floatToDouble(Float val) {
        return val == null ? null : Double.valueOf(val.toString());
    }
}
