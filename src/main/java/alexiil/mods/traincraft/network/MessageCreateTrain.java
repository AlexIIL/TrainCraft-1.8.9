package alexiil.mods.traincraft.network;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import alexiil.mods.traincraft.TrainCraft;
import alexiil.mods.traincraft.api.Train;
import alexiil.mods.traincraft.api.TrainCraftAPI;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MessageCreateTrain implements IMessage, IMessageHandler<MessageCreateTrain, IMessage> {
    private Train train;
    private ByteBuf payload;

    // Used for the handler and message creation
    public MessageCreateTrain() {}

    public MessageCreateTrain(Train train) {
        this.train = train;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        payload = buf.readBytes(length);
        TrainCraft.trainCraftLog.info("MessageCreateTrain::fromBytes | Read a train payload data from " + length + " bytes");
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBuf payload = Unpooled.buffer();
        train.writeCreateToByteBuf(payload);
        TrainCraft.trainCraftLog.info("MessageCreateTrain::toBytes | Wrote train " + train.id + " in " + payload.readableBytes() + " bytes");
        buf.writeInt(payload.readableBytes());
        buf.writeBytes(payload);
    }

    @Override
    public IMessage onMessage(MessageCreateTrain message, MessageContext ctx) {
        /* Run this on the main thread otherwise the world will not have synchronized with the netty thread */
        Minecraft.getMinecraft().addScheduledTask(() -> {
            TrainCraft.trainCraftLog.info("MessageCreateTrain::onMessage | Recieved a payload of size " + message.payload.readableBytes() + " bytes");
            Train train = Train.createFromByteBuf(message.payload);

            TrainCraft.trainCraftLog.info("MessageCreateTrain::onMessage | Created a train " + train);
            TrainCraftAPI.WORLD_CACHE.createTrain(train);
        });
        return null;
    }
}
