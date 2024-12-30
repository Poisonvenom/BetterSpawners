package net.poisonvenom.spawnermask;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.poisonvenom.spawnermask.modules.*;


public class SpawnerMask extends MeteorAddon {
        public static final Logger LOG = LoggerFactory.getLogger(SpawnerMask.class);
        public static final Category Main = new Category("SpawnerMask");
        @Override
        public void onInitialize() {
                Modules.get().add(new ActivatedSpawnerDetector());
                Modules.get().add(new MaskSpawner());
        }

        @Override
        public void onRegisterCategories() {
                Modules.registerCategory(Main);
        }

        public String getPackage() {
                return "net.poisonvenom.spawnermask";
        }

}