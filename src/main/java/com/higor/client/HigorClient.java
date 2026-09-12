package com.higor.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = HigorClient.MODID, name = HigorClient.NAME, version = HigorClient.VERSION)
public class HigorClient {

    public static final String MODID = "higorclient";
    public static final String NAME = "HIGOR CLIENT";
    public static final String VERSION = "0.1.0";

    @Mod.Instance(MODID)
    public static HigorClient instance;

    public ModuleManager moduleManager;
    public ConfigManager configManager;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("[HIGOR CLIENT] Pre-Init iniciando...");
        this.configManager = new ConfigManager(event.getModConfigurationDirectory());
        this.moduleManager = new ModuleManager();
        MinecraftForge.EVENT_BUS.register(this);
        System.out.println("[HIGOR CLIENT] Pre-Init concluido.");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("[HIGOR CLIENT] Init iniciando...");
        this.moduleManager.registerAll();
        this.configManager.loadAll();
        System.out.println("[HIGOR CLIENT] Core " + VERSION + " inicializado");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        System.out.println("[HIGOR CLIENT] Post-Init concluido.");
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && moduleManager != null) {
            for (int i = 0; i < moduleManager.getModules().size(); i++) {
                moduleManager.getModules().get(i).onUpdate();
            }
        }
    }

    // ============= MODULE =============
    public static class Module {
        private final String name;
        private final String category;
        private boolean enabled;

        public Module(String name, String category) {
            this.name = name;
            this.category = category;
            this.enabled = false;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public void toggle() { setEnabled(!enabled); }
        public void onUpdate() {}
    }

    // ============= MODULE MANAGER =============
    public static class ModuleManager {
        private final List<Module> modules = new ArrayList<Module>();

        public void registerAll() {
            System.out.println("[HIGOR CLIENT] Registrando modulos...");
            // Modulos serao adicionados nas Etapas 3, 4 e 5
            System.out.println("[HIGOR CLIENT] " + modules.size() + " modulos registrados.");
        }

        public void register(Module m) { modules.add(m); }
        public List<Module> getModules() { return modules; }
    }

    // ============= CONFIG MANAGER =============
    public static class ConfigManager {
        private final File configDir;
        private final Gson gson;

        public ConfigManager(File minecraftConfigDir) {
            this.configDir = new File(minecraftConfigDir, "higorclient");
            this.gson = new GsonBuilder().setPrettyPrinting().create();
            if (!configDir.exists()) {
                boolean ok = configDir.mkdirs();
                System.out.println("[HIGOR CLIENT] Pasta config criada: " + ok);
            }
        }

        public void loadAll() {
            System.out.println("[HIGOR CLIENT] Carregando configuracoes...");
            createIfMissing("modules.json", "{}");
            createIfMissing("hud.json", "{}");
            createIfMissing("settings.json", "{\"theme\":\"dark-blue\",\"version\":\"0.1.0\"}");
            createIfMissing("profiles.json", "{\"active\":\"default\"}");
        }

        private void createIfMissing(String fileName, String content) {
            File f = new File(configDir, fileName);
            if (!f.exists()) {
                try {
                    FileWriter w = new FileWriter(f);
                    w.write(content);
                    w.close();
                    System.out.println("[HIGOR CLIENT] Criado: " + fileName);
                } catch (IOException e) {
                    System.err.println("[HIGOR CLIENT] Erro ao criar " + fileName + ": " + e.getMessage());
                }
            }
        }
    }
                        }
