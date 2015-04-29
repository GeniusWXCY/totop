package com.genius.totop.model;

/**
 * 版本
 */
public class Version {

    public String id;
    /**
     * 版本名称
     */
    public String versionName;
    /**
     * 版本号代码
     */
    public int versionCode;
    /**
     * 包名
     */
    public String packageName;
    /**
     * 包地址
     */
    public String packageUrl;
    /**
     * 更新说明
     */
    public String content;
    /**
     * 需要强制更新的版本，低于此版本都必须强制更新
     */
    public int lowestVersionCode;
}
