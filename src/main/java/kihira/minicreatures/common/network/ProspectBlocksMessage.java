/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.network;

import io.netty.buffer.ByteBuf;
import kihira.minicreatures.client.ProspectingHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ProspectBlocksMessage implements IMessage {
    int[][] blocks;
    int size;

    public ProspectBlocksMessage() {}
    public ProspectBlocksMessage(int[] ... blocks) {
        this.blocks = blocks;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        size = buf.readShort();
        blocks = new int[size][];
        for (int i = 0; i < size; i++) {
            blocks[i] = new int[]{buf.readInt(), buf.readInt(), buf.readInt()};
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(blocks.length);
        for (int[] block : blocks) {
            for (int coord : block) {
                buf.writeInt(coord);
            }
        }
    }

    public static class ProspectBlocksMessageHandler implements IMessageHandler<ProspectBlocksMessage, IMessage> {

        @Override
        public IMessage onMessage(ProspectBlocksMessage message, MessageContext ctx) {
            ProspectingHandler.INSTANCE.blocks = message.blocks.clone();
            ProspectingHandler.INSTANCE.showTime = 200;
            return null;
        }
    }
}
