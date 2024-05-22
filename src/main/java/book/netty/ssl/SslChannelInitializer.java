package book.netty.ssl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class SslChannelInitializer extends ChannelInitializer<Channel> {
    private final SslContext context;
    // true인 경우 처음 기록된 메시지가 암호화되지 않는다.
    private final boolean startTls;

    public SslChannelInitializer(SslContext context, boolean startTls) {
        this.context = context;
        this.startTls = startTls;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        // 각 SslHandler 인스턴스마다 Channel의 ByteBufAllocator를 이용하여 SslContext에서 새로운 SSLEngine을 얻는다.
        SSLEngine sslEngine = context.newEngine(ch.alloc());
        ch.pipeline().addFirst("ssl", new SslHandler(sslEngine, startTls));
    }

}
