package com.nhx.core.metadata;

//服务目录?
public abstract class Directory {
    private transient String directoryCache;

    /** 服务所属组别 */
    public abstract String getGroup();

    /** 服务名称 */
    public abstract String getServiceProviderName();

    /** 服务版本号 */
    public abstract String getVersion();

    public String directoryString() {
        if (directoryCache != null) {
            return directoryCache;
        }

        //StringBuilder buf = StringBuilderHelper.get();
        StringBuilder buf = new StringBuilder();
        buf.append(getGroup())
                .append('-')
                .append(getServiceProviderName())
                .append('-')
                .append(getVersion());

        directoryCache = buf.toString();

        return directoryCache;
    }

    public void clear() {
        directoryCache = null;
    }
}
