package catserver.server;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CatServerConfig {
    private final File configFile;
    private YamlConfiguration config;

    private boolean firstInit = false;

    public boolean hopperAsync = false;
    public boolean entityMoveAsync = true;
    public boolean modMobAsync = false;
    public int entityPoolNum = 3;

    public boolean keepSpawnInMemory = true;
    public boolean enableSkipEntityTick = true;
    public boolean enableSkipTileEntityTick = false;
    public boolean enableCapture = true;
    public long worldGenMaxTickTime = 15000000L;
    public List<String> disableForgeGenerateWorlds = Arrays.asList("ExampleCustomWorld");
    public boolean preventBlockLoadChunk = false;

    public List<String> fakePlayerPermissions = Arrays.asList("essentials.build");
    public boolean fakePlayerEventPass = false;

    public boolean disableUpdateGameProfile = false;
    public boolean disableFMLHandshake = false;
    public boolean disableFMLStatusModInfo = false;

    public boolean enableNative = false;

    public CatServerConfig(String file) {
        this.configFile = new File(file);
    }

    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        // async
        hopperAsync = getOrWriteBooleanConfig("async.hopper", hopperAsync);
        entityMoveAsync = getOrWriteBooleanConfig("async.entityMove", entityMoveAsync);
        modMobAsync = getOrWriteBooleanConfig("async.modMob", modMobAsync);
        entityPoolNum = getOrWriteIntConfig("async.asyncPoolNum", entityPoolNum);
        // world
        keepSpawnInMemory = getOrWriteBooleanConfig("world.keepSpawnInMemory", keepSpawnInMemory);
        enableSkipEntityTick = getOrWriteBooleanConfig("world.enableSkipEntityTick", enableSkipEntityTick);
        enableSkipTileEntityTick = getOrWriteBooleanConfig("world.enableSkipTileEntityTick", enableSkipTileEntityTick);
        enableCapture = getOrWriteBooleanConfig("world.enableCapture", enableCapture);
        worldGenMaxTickTime = getOrWriteIntConfig("world.worldGenMaxTick", 15) * 1000000;
        disableForgeGenerateWorlds = getOrWriteStringListConfig("world.disableForgeGenerateWorlds", disableForgeGenerateWorlds);
        preventBlockLoadChunk = getOrWriteBooleanConfig("world.preventBlockLoadChunk", preventBlockLoadChunk);
        // fakeplayer
        fakePlayerPermissions = getOrWriteStringListConfig("fakePlayer.permissions", fakePlayerPermissions);
        fakePlayerEventPass = getOrWriteBooleanConfig("fakePlayer.eventPass", fakePlayerEventPass);
        // general
        disableUpdateGameProfile = getOrWriteBooleanConfig("disableUpdateGameProfile", disableUpdateGameProfile);
        disableFMLHandshake = getOrWriteBooleanConfig("disableFMLHandshake", disableFMLHandshake);
        disableFMLStatusModInfo = getOrWriteBooleanConfig("disableFMLStatusModInfo", disableFMLStatusModInfo);
        //native
        if (!firstInit) enableNative = getOrWriteBooleanConfig("enableNative", enableNative);
        // save config
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        firstInit = true;
    }

    private boolean getOrWriteBooleanConfig(String path, boolean def) {
        if (config.contains(path)) {
            return config.getBoolean(path);
        }
        config.set(path, def);
        return def;
    }

    private int getOrWriteIntConfig(String path, int def) {
        if (config.contains(path)) {
            return config.getInt(path);
        }
        config.set(path, def);
        return def;
    }

    private List<String> getOrWriteStringListConfig(String path, List<String> def) {
        if (config.contains(path)) {
            return config.getStringList(path);
        }
        config.set(path, def);
        return def;
    }
}
