package book.netty.websocket;

import book.netty.http.HttpRequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            // 이벤트가 핸드셰이크 성공을 의미하는 경우 HTTP 메시지는 더 이상 수신하지 않는다.
            ctx.pipeline().remove(HttpRequestHandler.class);
            // 연결된 모든 웹소켓 클라이언트에 새로운 클라이언트가 연결된 것을 알린다.
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
            // 모든 메시지를 수신할 수 있게 새로운 웹소켓 채널을 ChannelGroup에 추가
            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // channelRead0()이 반환될 때 TextWebSocketFrame의 참조 카운터가 감소하기 때문에 호출해야 한다.
        // 모든 작업이 비동기식이므로, writeAndFlush()가 나중에 완료될 수 있기 때문에 잘못된 참조에 접근하지 않도록 호출한다.
        group.writeAndFlush(msg.retain());
    }
}
