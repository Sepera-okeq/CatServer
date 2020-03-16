package catserver.server.network;

import catserver.server.CatServer;
import catserver.server.natives.NativeCalls;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NettyCompressionEncoder;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLLog;

public class CatNettyCompressionEncoder extends NettyCompressionEncoder {

    private final long ctx;
    private int consumed;
    private boolean finished;

    public CatNettyCompressionEncoder(int thresholdIn) {
        super(thresholdIn);
        int level = CatServer.getConfig().networkCompressionLevel;
        if (level < -1 || level > 9) {
            FMLLog.log.error("networkCompressionLevel set error in catserver.yml. it's only for -1 to 9. but you set {}", level);
            level = -1;
        }
        this.ctx = NativeCalls.compCreateEncodeCtx(this, level);
    }

    @Override
    protected void encode(ChannelHandlerContext p_encode_1_, ByteBuf in, ByteBuf out) throws Exception {
        if (in.hasMemoryAddress() && out.hasMemoryAddress()) {
            int bakInReaderIndex = in.readerIndex();
            int bakOutWriterIndex = out.writerIndex();
            try {
                long inAddress = in.memoryAddress();
                out.memoryAddress();
                int i = in.readableBytes();
                PacketBuffer packetbuffer = new PacketBuffer(out);
                if (i < this.getCompressionThreshold()) {
                    packetbuffer.writeVarInt(0);
                    packetbuffer.writeBytes(in);
                } else {
                    packetbuffer.writeVarInt(i);
                    while (!finished) {
                        if (!out.isWritable()) {
                            out.ensureWritable(8192);
                        }
                        int produced = NativeCalls.compEncode(inAddress + in.readerIndex(), out.memoryAddress() + out.writerIndex(), in.readableBytes(), out.writableBytes(), ctx, this);
                        in.readerIndex(in.readerIndex() + consumed);
                        out.writerIndex(out.writerIndex() + produced);
                    }
                }
            } catch (Throwable e) {
                FMLLog.log.warn("Native comp data error", e);
                in.readerIndex(bakInReaderIndex);
                out.writerIndex(bakOutWriterIndex);
                super.encode(p_encode_1_, in, out);
            }
        } else {
            FMLLog.log.warn("The Buff has no memory address: {} or {}", in.getClass(), out.getClass());
            super.encode(p_encode_1_, in, out);
        }
        this.finished = false;
        this.consumed = 0;
    }

    @Override
    protected void finalize() throws Throwable {
        NativeCalls.compReleaseEncodeCtx(ctx);
        super.finalize();
    }
}
