package net.poisonvenom.betterspawners.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import net.poisonvenom.betterspawners.BetterSpawners;

import java.util.*;

public class MaskSpawner extends Module {
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .build()
    );
    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("nearest-spawner-side-color")
            .description("Color of the nearest spawner found.")
            .defaultValue(new SettingColor(255, 0, 130, 55))
            .visible(() -> (shapeMode.get() == ShapeMode.Sides || shapeMode.get() == ShapeMode.Both))
            .build()
    );
    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("nearest-spawner-line-color")
            .description("Color of the nearest spawner found.")
            .defaultValue(new SettingColor(255, 0, 130, 200))
            .visible(() -> (shapeMode.get() == ShapeMode.Lines || shapeMode.get() == ShapeMode.Both))
            .build()
    );

    private MobSpawnerBlockEntity spwnr = null;
    private boolean concealed = false;
    private boolean keyPressed = false;

    public MaskSpawner() {
        super(BetterSpawners.Main, "SpawnerMask", "Masks spawners that have been activated.");
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
        if (spwnr != null && distance2Player(spwnr) < 16 && !concealed) {
            int delay = spwnr.getLogic().spawnDelay;
            ChatUtils.sendMsg(Text.of("Spawner delay: " + delay));
            if (delay == 20) {
                mc.options.backKey.setPressed(true);
                ChatUtils.sendMsg(Text.of("Final delay: " + delay));
                concealed = true;
                keyPressed = true;
            }
        }
        if (concealed && distance2Player(spwnr) > 16 && keyPressed) {
            mc.options.backKey.setPressed(false);
            keyPressed = false;
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        render(new Box(new Vec3d(spwnr.getPos().getX() + 1, spwnr.getPos().getY() + 1, spwnr.getPos().getZ() + 1), new Vec3d(spwnr.getPos().getX(), spwnr.getPos().getY(), spwnr.getPos().getZ())), sideColor.get(), lineColor.get(), shapeMode.get(), event);
    }

    private void render(Box box, Color sides, Color lines, ShapeMode shapemode, Render3DEvent event) {
        event.renderer.box(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, sides, lines, shapemode, 0);
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
                    if (blockEntity instanceof MobSpawnerBlockEntity spawner) {
                        if (spwnr == null) {
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
