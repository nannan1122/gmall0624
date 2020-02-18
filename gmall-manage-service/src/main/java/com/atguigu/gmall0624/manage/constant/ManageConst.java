package com.atguigu.gmall0624.manage.constant;

public class ManageConst {
    public static final String SKUKEY_PREFIX="sku:";

    public static final String SKUKEY_SUFFIX=":info";

    public static final int SKUKEY_TIMEOUT=7*24*60*60;
    //锁的过期时间
    public static final int SKULOCK_EXPIRE_PX=10000;
    //锁的后缀
    public static final String SKULOCK_SUFFIX=":lock";

    public static final String REDISSION_SERVER_ADRESS="redis://192.168.146.131:6379";


}
