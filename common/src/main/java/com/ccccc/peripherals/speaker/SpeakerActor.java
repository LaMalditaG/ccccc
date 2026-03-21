package com.ccccc.peripherals.speaker;

import com.ccccc.peripherals.PeripheralActorInterface;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.network.client.SpeakerMoveClientMessage;
import dan200.computercraft.shared.network.server.ServerNetworking;
import dan200.computercraft.shared.peripheral.speaker.SpeakerPeripheral;
import dan200.computercraft.shared.peripheral.speaker.SpeakerPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpeakerActor implements PeripheralActorInterface {
    private Peripheral peripheral;
    private Level level;
    private Vec3 pos;

    static SpeakerPeripheral createActor(@NotNull MovementContext ctx){
        if (ctx.world instanceof ServerLevel serverLevel) {
            var actor = new SpeakerActor();
            ctx.temporaryData = actor;
            actor.pos = ctx.position;
            actor.level = serverLevel;

            actor.peripheral = new Peripheral(actor);
            return actor.peripheral;
        }
        return null;
    }

    Level getLevel(){
        return level;
    }

    public Vec3 getPos(){
        return pos;
    }

    public void updatePos(@NotNull MovementContext ctx){
        pos = ctx.position;
    }

    public void tick(){
        peripheral.forceSpeakerChangePos();
        peripheral.update();
    }

    @Override
    public SpeakerPeripheral getPeripheral() {
        return peripheral;
    }

    private static final class Peripheral extends SpeakerPeripheral {
        private final SpeakerActor speaker;

        private Peripheral(SpeakerActor speaker) {
            this.speaker = speaker;
            this.speaker.level = speaker.level;
        }

        @Contract(" -> new")
        @Override
        public @NotNull SpeakerPosition getPosition() {
            return SpeakerPosition.of(speaker.level, speaker.pos);
        }

        @Override
        public boolean equals(@Nullable IPeripheral other) {
            return this == other || (other instanceof Peripheral o && speaker == o.speaker);
        }

        void forceSpeakerChangePos(){
            ServerNetworking.sendToAllTracking(
                new SpeakerMoveClientMessage(getSource(), getPosition()),
                speaker.level.getChunkAt(BlockPos.containing(speaker.pos))
            );

        }
    }
}
