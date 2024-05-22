package book.netty.udp;

import book.netty.encoder.LogEventEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

public class LogEventBroadcaster {
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final File file;

    public LogEventBroadcaster(InetSocketAddress address, File file) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public void run() throws Exception {
        Channel channel = bootstrap.bind(0).sync().channel();
        long pointer = 0;

        while (true) {
            long len = file.length();
            if (len < pointer) {
                pointer = len;
            } else if (len > pointer) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                randomAccessFile.seek(pointer);
                String line;

                while ((line = randomAccessFile.readLine()) != null) {
                    channel.writeAndFlush(new LogEvent(null, line, file.getAbsolutePath(), -1));
                }
                pointer = randomAccessFile.getFilePointer();
                randomAccessFile.close();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }

        LogEventBroadcaster broadcaster = new LogEventBroadcaster(new InetSocketAddress("255.255.255.255", Integer.parseInt(args[0])), new File(args[1]));
        try {
            broadcaster.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            broadcaster.stop();
        }
    }
}
