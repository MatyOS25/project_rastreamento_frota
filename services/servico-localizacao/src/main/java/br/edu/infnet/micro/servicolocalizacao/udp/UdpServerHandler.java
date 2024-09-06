package br.edu.infnet.micro.servicolocalizacao.udp;

import br.edu.infnet.micro.servicolocalizacao.service.LocationService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    public static final Logger logger = LoggerFactory.getLogger(UdpServerHandler.class);
    private final LocationService locationService;

    public UdpServerHandler(LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        String receivedMessage = msg.content().toString(io.netty.util.CharsetUtil.UTF_8);
        logger.info("Mensagem recebida: {}", receivedMessage);
        locationService.processLocationData(receivedMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
