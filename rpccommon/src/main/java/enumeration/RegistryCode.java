package enumeration;

public enum  RegistryCode {
    SingleImpl(0),
    MultiImpl(1),
    GroupImpl(2);
    private final int code;

    RegistryCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
