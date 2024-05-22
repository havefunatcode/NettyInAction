package book.netty.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent logEvent) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(logEvent.getReceivedTimestamp());
        stringBuilder.append(" [");
        stringBuilder.append(logEvent.getSource().toString());
        stringBuilder.append("] [");
        stringBuilder.append(logEvent.getLogfile());
        stringBuilder.append("] : ");
        stringBuilder.append(logEvent.getMsg());
        System.out.println(stringBuilder.toString());
    }
}
