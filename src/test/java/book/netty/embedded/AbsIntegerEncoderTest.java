package book.netty.embedded;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbsIntegerEncoderTest {
    @Test
    public void testEncoded() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 1; i< 10; i++) {
            buffer.writeInt(i * -1);
        }

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new AbsIntegerEncoder());
        assertTrue(embeddedChannel.writeOutbound(buffer));
        assertTrue(embeddedChannel.finish());

        for (int i = 1; i < 10; i++) {
            assertEquals(i, (Integer) embeddedChannel.readOutbound());
        }

        assertNull(embeddedChannel.readOutbound());
    }
}