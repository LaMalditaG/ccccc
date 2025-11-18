package com.ccccc.peripherals.wirelessmodem;

import com.ccccc.peripherals.PeripheralActorInterface;
import com.ccccc.peripherals.speaker.SpeakerActor;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.modem.ModemState;
import dan200.computercraft.shared.peripheral.modem.wireless.WirelessModemPeripheral;
import dan200.computercraft.shared.peripheral.speaker.SpeakerPeripheral;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class WirelessModemActor implements PeripheralActorInterface {
    WirelessModemPeripheral peripheral;
    Level level;
    Vec3 pos;

    @Override
    public IPeripheral getPeripheral() {
        return peripheral;
    }

    static WirelessModemPeripheral createActor(@NotNull MovementContext ctx){
        if (ctx.world instanceof ServerLevel serverLevel) {
            var actor = new WirelessModemActor();
            ctx.temporaryData = actor;
//            actor.pos = ctx.position;
            actor.level = serverLevel;
            var blockId = ctx.blockEntityData.getString("id");
            boolean advanced = blockId.equals("computercraft:wireless_modem_advanced");

            actor.peripheral = new WirelessModemActor.Peripheral(actor,advanced);
            return actor.peripheral;
        }
        return null;
    }

    public void updatePos(@NotNull MovementContext ctx){
        pos = ctx.position;
    }

    private static class Peripheral extends WirelessModemPeripheral{
        WirelessModemActor actor;

        Peripheral(WirelessModemActor actor,boolean advanced){
            super(new ModemState(),advanced);
            this.actor = actor;
        }

        @Override
        public String getType(){
            return "contraption";
        }

        @Override
        public Level getLevel() {
            return actor.level;
        }

        @Override
        public Vec3 getPosition() {
            return actor.pos;
        }

        @Override
        public boolean equals(@Nullable IPeripheral other) {
            return false;
        }
    }
}
