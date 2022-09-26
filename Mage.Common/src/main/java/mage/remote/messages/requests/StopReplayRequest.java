/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mage.remote.messages.requests;

import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;
import mage.remote.interfaces.MageServer;


public class StopReplayRequest extends ServerRequest {
    private final UUID gameId;

    public StopReplayRequest(UUID gameId) {
        this.gameId = gameId;
    }

    @Override
    public void handleMessage(MageServer server, ChannelHandlerContext ctx) {
        server.stopReplay(gameId, getSessionId(ctx));
    }
}
