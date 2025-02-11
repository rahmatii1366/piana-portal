package ir.piana.dev.openidc.core.types;

public class LeakyBucketConstants {
    public final static int LOGIN_BUCKET_VOLUME = 50;
    public final static double LOGIN_LEAK_RATE = (double) 5 / 12000;
    public final static int TOKEN_LOGIN_WEIGHT = 5;
    public final static int LOGIN_WEIGHT = 1;
    public final static int CHECK_PERMISSION_BUCKET_VOLUME = 40;
    public final static double CHECK_PERMISSION_LEAK_RATE = (double) 5 / 60;
    public final static int CHECK_PERMISSION_DEFAULT_WEIGHT = 1;
}
