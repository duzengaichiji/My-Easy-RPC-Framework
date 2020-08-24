package enumeration;

public enum  PackageType {
    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    PackageType(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }


}
