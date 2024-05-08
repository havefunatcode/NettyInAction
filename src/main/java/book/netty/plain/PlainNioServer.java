package book.netty.plain;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class PlainNioServer {
    public void serve(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket ssocket = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        // 서버를 선택된 포트로 바인딩
        ssocket.bind(address);
        // 채널을 처리할 셀럭터를 연다.
        Selector selector = Selector.open();
        // 연결을 수락할 ServerSocket을 셀렉터에 등록
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());

        while (true) {
            try {
                // 처리할 새로운 이벤트릴 기다리고 다음 들어오는 이벤트까지 블로킹
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            // 이벤트를 수신한 모든 SelectionKey 인스턴스를 얻음
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    // 이벤트가 수락할 수 있는 새로운 연결인지 확인
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        // 클라이언트를 수락하고 셀렉터에 등록
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());
                        System.out.println("Accepted connection from " + client);
                    }
                    // 소켓에 데이터를 기록할 수 있는지 확인
                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();

                        while (buffer.hasRemaining()) {
                            // 연결된 클라이언트로 데이터를 출력
                            if (client.write(buffer) == 0) {
                                break;
                            }
                        }
                        client.close();
                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }
    }
}
