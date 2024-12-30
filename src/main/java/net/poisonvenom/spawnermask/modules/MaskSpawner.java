package net.poisonvenom.spawnermask.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import net.poisonvenom.spawnermask.SpawnerMask;

import java.util.*;

public class MaskSpawner extends Module {
    private MobSpawnerBlockEntity spwnr = null;
    private boolean concealed = false;
    private boolean keyPressed = false;

    public MaskSpawner() {
        super(SpawnerMask.Main,"MaskSpawner", "Masks spawners that have been activated.");
    }
    @Override
    public void onActivate() {
        setSpawner();
    }
    @Override
    public void onDeactivate() {
        spwnr = null;
        concealed = false;
        keyPressed = false;
    }

    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        if (spwnr != null && distance2Player(spwnr) < 16 && !concealed){
            int delay = spwnr.getLogic().spawnDelay;
            ChatUtils.sendMsg(Text.of("Spawner delay: " + delay));
            if (delay == 20) {
                mc.options.backKey.setPressed(true);
                ChatUtils.sendMsg(Text.of("Final delay: " + delay));
                concealed = true;
                keyPressed = true;
            }
        }
        if (concealed && distance2Player(spwnr) > 17 && keyPressed) {
            mc.options.backKey.setPressed(false);
            keyPressed = false;
        }
    }

    private void setSpawner() {
        if (mc.world == null) return;
        if (mc.player == null) return;

        int renderDistance = mc.options.getViewDistance().getValue();
        ChunkPos playerChunkPos = new ChunkPos(mc.player.getBlockPos());
        for (int chunkX = playerChunkPos.x - renderDistance; chunkX <= playerChunkPos.x + renderDistance; chunkX++) {
            for (int chunkZ = playerChunkPos.z - renderDistance; chunkZ <= playerChunkPos.z + renderDistance; chunkZ++) {
                WorldChunk chunk = mc.world.getChunk(chunkX, chunkZ);
                List<BlockEntity> blockEntities = new ArrayList<>(chunk.getBlockEntities().values());

                for (BlockEntity blockEntity : blockEntities) {
                    if (blockEntity instanceof MobSpawnerBlockEntity spawner){
                        if(spwnr == null) {
                            spwnr = spawner;
                        } else {
                            if (distance2Player(spawner) <= distance2Player(spwnr)) {
                                spwnr = spawner;
                            }
                        }
                    }
                }
            }
        }
        ChatUtils.sendMsg(Text.of("Located nearest spawner."));
    }

    private double distance2Player(MobSpawnerBlockEntity spawner) {
        if (mc.player != null) {
            return mc.player.getPos().distanceTo(new Vec3d(spawner.getPos().getX(), spawner.getPos().getY(), spawner.getPos().getZ()));
        } else {
            return 0.0;
        }
    }
}
