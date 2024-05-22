package book.netty.codec;

import book.netty.decoder.ByteToCharDecoder;
import book.netty.encoder.CharToByteEncoder;
import io.netty.channel.CombinedChannelDuplexHandler;

public class CombinedByteCharCodec extends CombinedChannelDuplexHandler<ByteToCharDecoder, CharToByteEncoder> {
    public CombinedByteCharCodec(ByteToCharDecoder inboundHandler, CharToByteEncoder outboundHandler) {
        super(inboundHandler, outboundHandler);
    }
}
