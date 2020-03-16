package catserver.server.natives;

public class NativeCalls {

    //AES start
    public static native long aesCreateEncryptKey(byte[] encodedKey);

    public static native void aesEncrypt(long inAddr, long outAddr, int len, long aesKeyAddr);

    public static native void aesReleaseEncryptKey(long aesKeyAddr);

    public static native long aesCreateDecryptKey(byte[] encodedKey);

    public static native void aesDecrypt(long inAddr, long outAddr, int len, long aesKeyAddr);

    public static native void aesReleaseDecryptKey(long aesKeyAddr);

    //COMP start
    public static native long compCreateEncodeCtx(Object obj, int level);

    public static native long compCreateDecodeCtx(Object obj);

    public static native int compEncode(long inAddr, long outAddr, int inlen, int outlen, long ctx, Object obj);

    public static native int compDecode(long inAddr, long outAddr, int len, int outlen, long ctx, Object obj);

    public static native void compReleaseEncodeCtx(long ctx);

    public static native void compReleaseDecodeCtx(long ctx);
}