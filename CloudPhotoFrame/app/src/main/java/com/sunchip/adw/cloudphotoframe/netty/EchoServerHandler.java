package com.sunchip.adw.cloudphotoframe.netty;


import android.util.Log;

import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;

import org.greenrobot.eventbus.EventBus;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


@ChannelHandler.Sharable
public class EchoServerHandler extends SimpleChannelInboundHandler<String> {

    private static final String TAG = "EchoServerHandler";
    private NettyServerListener mListener;

    public EchoServerHandler(NettyServerListener listener) {
        this.mListener = listener;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//    	System.out.println("channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();                //5
        ctx.close();
        //6
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Log.d(TAG, "msg:" + msg);
        if (msg.equals("photo")) {
            Log.d(TAG, "photo");
            EventBus.getDefault().post(new BaseErrarEvent("photo",9998));
            return; //客户端发送来的心跳数据
        }
        mListener.onMessageResponseServer(msg, ctx.channel().id().asShortText());
    }


    /**
     * 连接成功
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.e(TAG, "channelActive===========================" + ctx);
        mListener.onChannelConnect(ctx.channel());
//        NettyTcpServer.getInstance().setConnectStatus(true);
    }

    //断开连接
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.e(TAG, "channelInactive");
//        NettyTcpServer.getInstance().setConnectStatus(false);
        mListener.onChannelDisConnect(ctx.channel());
    }
}
