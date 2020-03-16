package catserver.server.network;

import catserver.server.natives.NativeCalls;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NettyEncryptingDecoder;
import net.minecraftforge.fml.common.FMLLog;

import javax.crypto.Cipher;
import java.util.List;

public class CatNettyEncryptingDecoder extends NettyEncryptingDecoder {

    private final long AES_KEY;

    public CatNettyEncryptingDecoder(Cipher cipher, byte[] encodedKey) {
        super(cipher);
        this.AES_KEY = NativeCalls.aesCreateDecryptKey(encodedKey);
    }

    @Override
    protected void decode(ChannelHandlerContext out, ByteBuf in, List<Object> unknownList) throws Exception {
        if (in.hasMemoryAddress()) {
            int bakInReaderIndex = in.readerIndex();
            try {
                long inAddress = in.memoryAddress();
                int i = in.readableBytes();
                ByteBuf outByteBuf = out.alloc().directBuffer(i);
                NativeCalls.aesDecrypt(inAddress + in.readerIndex(), outByteBuf.memoryAddress(), i, AES_KEY);
                in.skipBytes(i);
                outByteBuf.writerIndex(i);
                unknownList.add(outByteBuf);
            } catch (Throwable error) {
                in.readerIndex(bakInReaderIndex);
                FMLLog.log.warn("Native decrypt data error", error);
                super.decode(out, in, unknownList);
            }
        } else {
            FMLLog.log.warn("The Buff has no memory address: {}", in.getClass());
            super.decode(out, in, unknownList);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        NativeCalls.aesReleaseDecryptKey(AES_KEY);
        super.finalize();
    }
}
