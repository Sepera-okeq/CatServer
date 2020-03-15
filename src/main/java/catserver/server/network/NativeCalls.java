package catserver.server.network;

public class NativeCalls {

    public static native long aesCreateEncryptKey(byte[] encodedKey);

    public static native void aesEncrypt(long inAddr, long outAddr, int len, long aesKeyAddr);

    public static native void aesReleaseEncryptKey(long aesKeyAddr);

    public static native long aesCreateDecryptKey(byte[] encodedKey);

    public static native void aesDecrypt(long inAddr, long outAddr, int len, long aesKeyAddr);

    public static native void aesReleaseDecryptKey(long aesKeyAddr);
}