package book.netty.embedded;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FrameChunkDecoderTest {
    @Test
    public void testFramesDecoded() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf input = buffer.duplicate();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new FrameChunkDecoder(3));

        assertTrue(embeddedChannel.writeInbound(input.readBytes(2)));
        try {
            embeddedChannel.writeInbound(input.readBytes(4));
            fail();
        } catch (TooLongFrameException e) {

        }
        assertTrue(embeddedChannel.writeInbound(input.readBytes(3)));
        assertTrue(embeddedChannel.finish());

        ByteBuf readInbound = (ByteBuf) embeddedChannel.readInbound();
        assertEquals(buffer.readSlice(2), readInbound);
        readInbound.release();

        readInbound = (ByteBuf) embeddedChannel.readInbound();
        assertEquals(buffer.skipBytes(4).readSlice(3), readInbound);
        readInbound.release();
        buffer.release();
    }
}