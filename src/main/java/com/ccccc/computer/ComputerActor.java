package com.ccccc.computer;

import com.ccccc.CCCCC;
import com.ccccc.accessors.ServerComputerMixinAccessor;
import com.ccccc.peripherals.PeripheralActorInterface;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.config.ConfigSpec;
import dan200.computercraft.shared.util.IDAssigner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

class ComputerActor {

    private ServerComputer serverComputer;
    private UUID instanceID;
    private int computerID;
    private final ServerLevel level;
    private final ComputerFamily family;
    private boolean wasOn = false;
    private boolean hasToChangeState = false;

    public ComputerActor(int computerID, ComputerFamily family, ServerLevel level) {
        this.computerID = computerID;
        this.level = level;
        this.family = family;
    }

    @Contract("null -> fail")
    public final @NotNull ServerComputer createServerComputer(MinecraftServer server) {
        if (server == null) throw new IllegalStateException("Cannot access server computer on the client.");

        var computer = ServerContext.get(server).registry().get(instanceID);
        if (computer == null) {
            if (computerID < 0)
                computerID = ComputerCraftAPI.createUniqueNumberedSaveDir(server, IDAssigner.COMPUTER);
            computer = createComputer(computerID);
            instanceID = computer.register();
        }

        serverComputer = computer;

        return computer;
    }

    protected ServerComputer createComputer(int id) {
        return new ServerComputer(level, new BlockPos(0, 0, 0), ServerComputer.properties(id, family)
                .terminalSize(ConfigSpec.computerTermWidth.get(), ConfigSpec.computerTermHeight.get())
        );
    }

    public void tick() {
        if (serverComputer instanceof ServerComputerMixinAccessor accessor) {
            accessor.cCCCC$tickActor();
            hasToChangeState = wasOn != serverComputer.isOn();
            wasOn = serverComputer.isOn();
        }
    }

    public void turnOn() {
        serverComputer.turnOn();
    }

    public void shutdown() {
        serverComputer.shutdown();
    }

    public boolean isOn() {
        return serverComputer.isOn();
    }

    public boolean hasToChangeState() {
        return hasToChangeState;
    }

    public ServerComputer getServerComputer(){
        return serverComputer;
    }

    static @Nullable ServerComputer createActor(@NotNull MovementContext ctx) {
        if (ctx.world instanceof ServerLevel serverLevel) {
            if (!ctx.blockEntityData.contains("ComputerId")) return null;
            int computerID = ctx.blockEntityData.getInt("ComputerId");

            String label = null;
            if (ctx.blockEntityData.contains("label"))
                label = ctx.blockEntityData.getString("label");

            ComputerActor computerActor;
            ServerComputer serverComputer;
            if ((ctx.temporaryData instanceof ComputerActor sa)) {
                computerActor = sa;
                serverComputer = computerActor.serverComputer;
            } else {
                if (!ctx.blockEntityData.contains("id")) return null;

                var blockId = ctx.blockEntityData.getString("id");
                if (blockId.equals("computercraft:computer_advanced"))
                    computerActor = new ComputerActor(computerID, ComputerFamily.ADVANCED, serverLevel);
                else if (blockId.equals("computercraft:computer_command"))
                    computerActor = new ComputerActor(computerID, ComputerFamily.COMMAND, serverLevel);
                else
                    computerActor = new ComputerActor(computerID, ComputerFamily.NORMAL, serverLevel);


                ctx.temporaryData = computerActor;
                serverComputer = computerActor.createServerComputer(serverLevel.getServer());
                if (label != null)
                    serverComputer.setLabel(label);
            }

            if (ctx.blockEntityData.contains("On")) {
                if (ctx.blockEntityData.getBoolean("On"))
                    serverComputer.turnOn();
            }

            return serverComputer;
        }
        return null;
    }

    void addPeripherals(MovementContext ctx) {
        for (var actor : ctx.contraption.getActors()) {
            if (actor.getRight().temporaryData instanceof PeripheralActorInterface peripheral) {
                serverComputer.setPeripheral(ComputerSide.BACK,peripheral.getPeripheral());
            }
        }
    }
}