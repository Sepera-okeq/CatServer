package catserver.server.network;

import catserver.server.natives.NativeCalls;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NettyEncryptingEncoder;
import net.minecraftforge.fml.common.FMLLog;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class CatNettyEncryptingEncoder extends NettyEncryptingEncoder {

    private final long AES_KEY;

    public CatNettyEncryptingEncoder(Cipher cipher, byte[] encodedKey) {
        super(cipher);
        this.AES_KEY = NativeCalls.aesCreateEncryptKey(encodedKey);
    }

    @Override
    protected void encode(ChannelHandlerContext p_encode_1_, ByteBuf in, ByteBuf out) throws ShortBufferException, Exception {
        if (in.hasMemoryAddress() && out.hasMemoryAddress()) {
            int bakInReaderIndex = in.readerIndex();
            int bakOutWriterIndex = out.writerIndex();
            try {
                long inAddress = in.memoryAddress();
                out.memoryAddress(); // invoke the method at first to catch
                int i = in.readableBytes(); // in data len
                out.ensureWritable(i); // alloc memory if not enough
                NativeCalls.aesEncrypt(inAddress + in.readerIndex(), out.memoryAddress() + out.writerIndex(), i, AES_KEY);
                out.writerIndex(out.writerIndex() + i);
                in.skipBytes(i); // redefine reader index;
            } catch (Throwable e) { // catch get memory address
                in.readerIndex(bakInReaderIndex);
                out.writerIndex(bakOutWriterIndex);
                FMLLog.log.warn("Native encrypt data error", e);
                super.encode(p_encode_1_, in, out);
            }
        } else {
            FMLLog.log.warn("The Buff has no memory address: {} or {}", in.getClass(), out.getClass());
            super.encode(p_encode_1_, in, out);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        NativeCalls.aesReleaseEncryptKey(AES_KEY);
        super.finalize();
    }
}
