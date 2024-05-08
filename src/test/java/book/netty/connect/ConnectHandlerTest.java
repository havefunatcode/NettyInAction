package book.netty.connect;

import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectHandlerTest {

    @Test
    public void testChannelActive() {
        ConnectHandler connectHandler = new ConnectHandler();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(connectHandler);

        embeddedChannel.pipeline().fireChannelActive();
        assertTrue(embeddedChannel.finish());

        String output = embeddedChannel.readOutbound();
        assertEquals("Client null connected\n", output);
    }

}
