package catserver.server.network;

import catserver.server.natives.NativeCalls;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.NettyCompressionDecoder;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLLog;

import java.util.List;

public class CatNettyCompressionDecoder extends NettyCompressionDecoder {

    private final long ctx;

    public CatNettyCompressionDecoder(int thresholdIn) {
        super(thresholdIn);
        this.ctx = NativeCalls.compCreateDecodeCtx(this);
    }

    @Override
    protected void decode(ChannelHandlerContext p_decode_1_, ByteBuf in, List<Object> unknownList) throws Exception {
        if (in.hasMemoryAddress()) {
            int bakInReadIndex = in.readerIndex();
            try {
                long inAddress = in.memoryAddress();
                if (in.readableBytes() != 0)
                {
                    PacketBuffer packetbuffer = new PacketBuffer(in);
                    int i = packetbuffer.readVarInt();

                    if (i == 0)
                    {
                        unknownList.add(packetbuffer.readBytes(packetbuffer.readableBytes()));
                    }
                    else
                    {
                        if (i < this.getCompressionThreshold())
                        {
                            throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.getCompressionThreshold());
                        }

                        if (i > 2097152)
                        {
                            throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of " + 2097152);
                        }
                        ByteBuf out = Unpooled.directBuffer(i);
                        out.ensureWritable(i);
                        NativeCalls.compDecode(inAddress + in.readerIndex(), out.memoryAddress(), in.readableBytes(), i, ctx, this);
                        in.skipBytes(in.readableBytes());
                        out.writerIndex(i);
                        unknownList.add(out);
                    }
                }
            } catch (Throwable e) {
                FMLLog.log.warn("Native comp data error", e);
                in.readerIndex(bakInReadIndex);
                super.decode(p_decode_1_, in, unknownList);
            }
        } else {
            FMLLog.log.warn("The Buff has no memory address: {}", in.getClass());
            super.decode(p_decode_1_, in, unknownList);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        NativeCalls.compReleaseDecodeCtx(ctx);
        super.finalize();
    }
}
