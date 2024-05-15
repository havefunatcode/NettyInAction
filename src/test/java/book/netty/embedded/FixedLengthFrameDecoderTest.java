package book.netty.embedded;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FixedLengthFrameDecoderTest {
    @Test
    public void testFramesDecoded() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf input = buffer.duplicate();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        // Write Byte
        assertTrue(embeddedChannel.writeInbound(input.retain()));
        assertTrue(embeddedChannel.finish());

        // Read Message
        ByteBuf readInbound = (ByteBuf) embeddedChannel.readInbound();
        assertEquals(buffer.readSlice(3), readInbound);
        readInbound.release();

        readInbound = (ByteBuf) embeddedChannel.readInbound();
        assertEquals(buffer.readSlice(3), readInbound);
        readInbound.release();

        readInbound = (ByteBuf) embeddedChannel.readInbound();
        assertEquals(buffer.readSlice(3), readInbound);
        readInbound.release();

        assertNull(embeddedChannel.readInbound());
        buffer.release();
    }

    @Test
    public void testFramesDecoded2() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf input = buffer.duplicate();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));

        assertFalse(embeddedChannel.writeInbound(input.readBytes(2)));
        assertTrue(embeddedChannel.writeInbound(input.readBytes(7)));

        assertTrue(embeddedChannel.finish());

        ByteBuf readInbound = (ByteBuf) embeddedChannel.readInbound();
        assertEquals(buffer.readSlice(3), readInbound);
        readInbound.release();

        readInbound = (ByteBuf) embeddedChannel.readInbound();
        assertEquals(buffer.readSlice(3), readInbound);
        readInbound.release();

        readInbound = (ByteBuf) embeddedChannel.readInbound();
        assertEquals(buffer.readSlice(3), readInbound);
        readInbound.release();

        assertNull(embeddedChannel.readInbound());
        buffer.release();
    }

}