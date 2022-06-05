package skyshop.categories;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import skyshop.skyshop.SkyShop;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CategoryLoader {
    public Category loadCategory(SkyShop _plugin, String _categoryName)
    {
        Category cat = new Category();

        cat.setup(getConfig(_plugin, _categoryName), _categoryName, _plugin);

        return cat;
    }

    public void reloadCategory(SkyShop _plugin, Category _cat)
    {
        _cat.setup(getConfig(_plugin, _cat.name), _cat.name, _plugin);
    }

    FileConfiguration getConfig(SkyShop _plugin, String _categoryName)
    {
        FileConfiguration customConfig = null;
        File customConfigFile = null;
        Reader defConfigStream = null;

        //Load from dataFolder
        customConfigFile = new File(_plugin.getDataFolder(), _categoryName + ".yml");

        if (!_plugin.getDataFolder().exists())
        {
            _plugin.getLogger().info("Data folder doesn't exist. Creating it");

            _plugin.getDataFolder().mkdir();
        }

        //Doesn't exist for now, create it
        if (!customConfigFile.exists()) {
            _plugin.getLogger().info("File " + _categoryName + ".yml" + " doesn't exist. Creating it");

            try {
                defConfigStream = new InputStreamReader(_plugin.getResource(_categoryName + ".yml"), StandardCharsets.UTF_8);

                customConfigFile.createNewFile();
                copyInputStreamToFile(_plugin.getResource(_categoryName + ".yml"), customConfigFile);

                if (defConfigStream != null) {
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);

                    customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
                    customConfig.setDefaults(defConfig);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
        }

        return customConfig;
    }

    void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[8192];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }

    }
}
