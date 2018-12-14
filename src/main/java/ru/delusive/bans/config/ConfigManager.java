package ru.delusive.bans.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;

public class ConfigManager {
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationOptions options;
    private Config cfg;
    //private Config.GlobalParams globalParams;
    private Config.BansParams banFields;
    private String aliasName;
    private boolean isEnabled;

    public ConfigManager(ConfigurationLoader<CommentedConfigurationNode> loader) throws IOException, ObjectMappingException {
        this.loader = loader;
        this.options = ConfigurationOptions.defaults().setShouldCopyDefaults(true);

        update();

        //globalParams = cfg.getGlobalParams();
        banFields = cfg.getDbParams().getBansTable().getBansVars();
        aliasName = cfg.getDbParams().getAlias();
        isEnabled = cfg.getGlobalParams().getIsEnabled();
    }

    public Config getConfig(){
        return this.cfg;
    }

    public Config.BansParams getBanFields() {
        return banFields;
    }

    public String getAliasName() {
        return aliasName;
    }

    public boolean getIsEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(boolean enbld) {
        this.isEnabled = enbld;
    }

    private void update() throws IOException, ObjectMappingException {
        CommentedConfigurationNode node = loader.load(options);
        Config cfg = node.getValue(TypeToken.of(Config.class), new Config());
        loader.save(node);
        this.cfg = cfg;
    }

}
