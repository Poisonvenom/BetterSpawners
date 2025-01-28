package net.poisonvenom.betterspawners;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.poisonvenom.betterspawners.modules.*;


public class BetterSpawners extends MeteorAddon {
        public static final Logger LOG = LoggerFactory.getLogger(BetterSpawners.class);
        public static final Category Main = new Category("betterspawners");
        @Override
        public void onInitialize() {
                Modules.get().add(new ActivatedSpawnerDetector());
                Modules.get().add(new MaskSpawner());
                Modules.get().add(new CaveDisturbanceDetector());
        }

        @Override
        public void onRegisterCategories() {
                Modules.registerCategory(Main);
        }

        public String getPackage() {
                return "net.poisonvenom.betterspawners";
        }

}