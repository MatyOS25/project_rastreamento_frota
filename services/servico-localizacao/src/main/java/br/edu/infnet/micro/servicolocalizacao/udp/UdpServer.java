package br.edu.infnet.micro.servicolocalizacao.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.context.SmartLifecycle;

@Component
public class UdpServer implements SmartLifecycle {

    private final EventLoopGroup group = new NioEventLoopGroup();
    private final UdpServerHandler serverHandler;
    private boolean running = false;

    @Value("${udp.server.port}")
    private int port;

    public UdpServer(UdpServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    public void start() {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioDatagramChannel.class)
             .option(ChannelOption.SO_BROADCAST, true)
             .handler(serverHandler);

            b.bind(port).sync();
            running = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void stop() {
        group.shutdownGracefully();
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    // Estes métodos são opcionais, mas podem ser úteis para controle fino do ciclo de vida
    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
