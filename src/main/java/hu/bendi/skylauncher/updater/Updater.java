package hu.bendi.skylauncher.updater;

import com.google.gson.GsonBuilder;
import hu.bendi.skylauncher.Launcher;
import hu.bendi.skylauncher.launcher.MinecraftLauncher;
import hu.bendi.skylauncher.updater.version.ContentIndex;
import hu.bendi.skylauncher.updater.version.Index;
import hu.bendi.skylauncher.updater.version.Version;
import hu.bendi.skylauncher.utils.Constants;
import hu.bendi.skylauncher.utils.Crypto;
import hu.bendi.skylauncher.utils.OsUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.regex.Matcher;

import static hu.bendi.skylauncher.Launcher.LOGGER;

public class Updater {

    public static double progress;
    public static String currentAction = "T\u00E9tlen.";
    public static boolean canPlay = true;
    private int max;
    private int current;

    private static final OsUtils.OS os = OsUtils.getOs();

    public static File getLibPath(String name) {
        File libDir = new File(Constants.GAME_DIR,"libraries");
        String path = name.split(":")[0].replaceAll("\\.",
                Matcher.quoteReplacement(File.separator))+File.separator+name.split(":")[1]+File.pathSeparator;
        String fileName = name.split(":")[1] + "-" + name.split(":")[2] + ".jar";
        if (path.endsWith(":")) {
            path = path.substring(0, path.length() - 1) + "/";
        }
        return new File(libDir,path + fileName);
    }

    public void checkForUpdates() throws IOException {
        canPlay = false;
        FileUtils.copyURLToFile(new URL("https://bendimester23.tk/assets/1.14.4.json"),
                new File(Constants.GAME_DIR,"version.json"));
        String json = FileUtils.readFileToString(
                new File(Constants.GAME_DIR,"version.json"), Charset.defaultCharset());

        Version version = new GsonBuilder().create().fromJson(json,Version.class);

        MinecraftLauncher.ver = version;

        currentAction = "Kliens let\u00F6lt\u00E9se.";
        progress = 0;
        File client = new File(Constants.GAME_DIR,"client.jar");
        if (!client.exists()) {
            LOGGER.info("Downloading Minecraft jar.");
            FileUtils.copyURLToFile(new URL(version.client.url),client);
        }else {
            if (!Crypto.calcSHA1(client).equalsIgnoreCase(version.client.sha1)) {
                progress = 0.1;
                LOGGER.info("Updating Minecraft jar");
                FileUtils.copyURLToFile(new URL(version.client.url),client);
            }
        }
        progress = 1;
        currentAction = "K\u00F6nyvt\u00E1rak let\u00F6lt\u00E9se.";

        max = version.libraries.length;
        current = 0;

        for (Version.Library lib : version.libraries) {
            current++;
            progress = ((float)current) / max;
            if (lib.onlyIn != null) {
                if (lib.onlyIn[0].equalsIgnoreCase("OSX")) {
                    if (os == OsUtils.OS.OSX) updateLibrary(lib);
                } else if (!lib.onlyIn[0].equalsIgnoreCase("OSX")) {
                    if (os != OsUtils.OS.OSX) updateLibrary(lib);
                }
            }else {
                updateLibrary(lib);
            }
        }

        currentAction = "Er\u0151forr\u00E1sok let\u00F6lt\u00E9se.";
        current = 0;
        downloadAssets(new URL(version.assetIndex.url));

        currentAction = "Tartalom friss\u00EDt\u00E9se";
        progress = 0;

        FileUtils.copyURLToFile(new URL("https://bendimester23.tk/assets/content.json"),
                new File(Constants.GAME_DIR,"content.json"));
        String cJson = FileUtils.readFileToString(
                new File(Constants.GAME_DIR,"content.json"), Charset.defaultCharset());

        ContentIndex content = Launcher.GSON.fromJson(cJson, ContentIndex.class);
        if (!Constants.MODS_DIR.exists()) Constants.MODS_DIR.mkdirs();
        float c = 0;
        float m = content.mods.size();
        for (ContentIndex.Mod mod : content.mods) {
            currentAction = "Tartalom friss\u00EDt\u00E9se: " + mod.name;
            c++;
            progress = (c)/m;
            File f = new File(Constants.MODS_DIR, mod.fileName + ".jar");
            if (!f.exists()) {
                LOGGER.info("Downloading content " + mod.fileName);
                FileUtils.copyURLToFile(new URL(mod.url), f);
            }else if (!Crypto.calcSHA1(f).equalsIgnoreCase(mod.sha1)) {
                LOGGER.info("Updating content " + mod.fileName);
                FileUtils.copyURLToFile(new URL(mod.url), f);
            }
        }

        currentAction = "Ind\u00EDt\u00E1sra k\u00E9sz.";
        progress = 0;
        canPlay = true;
        LOGGER.info("Update check done.");
    }

    private void downloadAssets(URL assetIndex) throws IOException {
        File assetDir = new File(Constants.GAME_DIR, "assets");
        if (!assetDir.exists()) assetDir.mkdirs();
        File assetIndexFile = new File(assetDir, "indexes"+File.separator+"1.16.json");
        if (!assetIndexFile.exists()) {
            FileUtils.copyURLToFile(assetIndex, assetIndexFile);
        }
        Index index = new GsonBuilder().create().fromJson(FileUtils.readFileToString(assetIndexFile,Charset.defaultCharset()),
                Index.class);

        max = index.objects.values().size();

        index.objects.values().forEach(object -> {
            try {
                downloadAsset(object.hash);
            } catch (IOException e) {
                e.printStackTrace();
            }
            current++;
            progress = ((float)current) / max;
        });
    }

    private void downloadAsset(String hash) throws IOException {
        URL url = new URL("https://resources.download.minecraft.net/" +hash.substring(0,2)+"/"+hash);
        File path = new File(Constants.GAME_DIR,"assets"+File.separator+"objects"+File.separator
            +hash.substring(0,2));
        if (!path.exists()) path.mkdirs();
        File asset = new File(path,hash);
        if (!asset.exists()) FileUtils.copyURLToFile(url,asset);
    }

    int nativeIndex = 0;

    private void updateLibrary(Version.Library lib) throws IOException {
        LOGGER.info("Downloading library " + lib.name);
        currentAction = "K\u00F6nyvt\u00E1r let\u00F6lt\u00E9se: " + lib.name;
        File libFile = null;
        try {
            libFile = getLibPath(lib.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Version.Download download = lib.downloads.get("artifact");
        if (!Objects.requireNonNull(libFile).exists()) FileUtils.copyURLToFile(new URL(download.url), libFile);
        else if (!Crypto.calcSHA1(libFile).equalsIgnoreCase(download.sha1)) FileUtils.copyURLToFile(new URL(download.url), libFile);

        if (lib.natives != null && lib.downloads != null) {
            String url = "";
            switch (os) {
                case WIN:
                    if (lib.natives.get("windows") != null) {
                        url = lib.downloads.get(lib.natives.get("windows")).url;
                    }
                    break;
                case OSX:
                    LOGGER.warning("Downloaded OSX library! The launcher has no OSX support!");
                    if (lib.natives.get("osx") != null) {
                        url = lib.downloads.get(lib.natives.get("osx")).url;
                    }
                    break;
                case LINUX:
                    if (lib.natives.get("linux") != null) {
                        url = lib.downloads.get(lib.natives.get("linux")).url;
                    }
                    break;
            }
            LOGGER.info("Downloading natives for " + lib.name);
            LOGGER.info("Url: " + url + " Path: " + new File(Constants.NATIVES_DIR,"native" + nativeIndex+".jar").getAbsolutePath());
            FileUtils.copyURLToFile(new URL(url), new File(Constants.NATIVES_DIR,"native" + nativeIndex+".jar"));
            nativeIndex++;
        }
    }
}
