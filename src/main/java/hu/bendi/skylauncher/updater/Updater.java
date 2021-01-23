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
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;

public class Updater {

    public static double progress;
    public static String currentAction = "Tétlen.";
    public static boolean canPlay = true;
    private int max;
    private int current;

    private static void unZipFile(File f) {
        // Create zip file stream.
        try (ZipArchiveInputStream archive = new ZipArchiveInputStream(
                new BufferedInputStream(new FileInputStream(f)))) {

            ZipArchiveEntry entry;
            while ((entry = archive.getNextZipEntry()) != null) {
                if (!entry.getName().toLowerCase().contains("meta-inf")
                        || !entry.getName().toLowerCase().contains("manifest")) {
                    if (!entry.getName().toLowerCase().contains("sha1")
                            || !entry.getNameSource().name().toLowerCase().contains("git")
                            || !entry.isDirectory()) {

                        File file = new File(Constants.GAME_DIR, "natives" + File.separator + entry.getName());
                        String dir = file.toPath().toString().substring(0, file.toPath().toString().lastIndexOf("\\"));
                        if (!new File(dir).exists()) Files.createDirectories(new File(dir).toPath());
                        // Stream file content
                        if (!file.exists()) IOUtils.copy(archive, new FileOutputStream(file));

                    }
                }

                new File(Constants.GAME_DIR, "natives" + File.separator + "META_INF").delete();

            }
        } catch (IOException e) {
            if (!(e instanceof FileNotFoundException)) {
                e.printStackTrace();
            }
        }
    }

    public static File getLibPath(String name) {
        File libDir = new File(Constants.GAME_DIR,"libraries");
        String path = name.split(":")[0].replaceAll("\\.",
                Matcher.quoteReplacement(File.separator))+File.separator+name.split(":")[1]+"\\";
        if (!new File(libDir,path).exists()) new File(libDir,path).mkdirs();
        String fileName = name.split(":")[1] + "-" + name.split(":")[2] + ".jar";
        return new File(libDir,path+fileName);
    }

    public void checkForUpdates() throws IOException {
        canPlay = false;
        FileUtils.copyURLToFile(new URL("http://46.139.50.88/assets/1.14.4.json"),
                new File(Constants.GAME_DIR,"version.json"));
        String json = FileUtils.readFileToString(
                new File(Constants.GAME_DIR,"version.json"), Charset.defaultCharset());

        Version version = new GsonBuilder().create().fromJson(json,Version.class);

        MinecraftLauncher.ver = version;

        currentAction = "Kliens letöltése.";
        progress = 0;
        File client = new File(Constants.GAME_DIR,"client.jar");
        if (!client.exists()) {
            System.out.println("[Launcher] Downloading client jar.");
            FileUtils.copyURLToFile(new URL(version.client.url),client);
        }else {
            if (!Crypto.calcSHA1(client).equalsIgnoreCase(version.client.sha1)) {
                progress = 0.1;
                System.out.println("[Launcher] Updating client jar.");
                FileUtils.copyURLToFile(new URL(version.client.url),client);
            }
        }
        progress = 1;
        currentAction = "Könyvtárak letöltése.";
        OsUtils.OS os = new OsUtils().getOs();

        max = version.libraries.length;
        current = 0;

        for (Version.Library lib : version.libraries) {
            current++;
            progress = ((float)current) / max;
            if (lib.onlyIn != null) {
                if (lib.onlyIn[0].equalsIgnoreCase("OSX")) {
                    if (os == OsUtils.OS.OSX) updateLibrary(lib);
                } else if (!lib.onlyIn[0].equalsIgnoreCase("OSX")) {
                    if (os != OsUtils.OS.OSX)updateLibrary(lib);
                }
            }else {
                updateLibrary(lib);
            }
        }

        currentAction = "Erõforrások letöltése.";
        current = 0;
        downloadAssets(new URL(version.assetIndex.url));
        System.out.println("[Launcher] Update check done.");
        File folder = new File(Constants.GAME_DIR,"natives");
        File[] fList = folder.listFiles();

        for (File file : Objects.requireNonNull(fList)) {
            String pes = file.getPath();
            if (pes.contains(".git") || pes.contains(".sha1")) {
                file.delete();
            }
        }
        Arrays.asList(Objects.requireNonNull(new File(Constants.GAME_DIR, "tmp").listFiles())).forEach(File::delete);
        new File(Constants.GAME_DIR,"tmp").delete();

        currentAction = "Tartalom frissítése";
        progress = 0;

        FileUtils.copyURLToFile(new URL("http://46.139.50.88/assets/content.json"),
                new File(Constants.GAME_DIR,"content.json"));
        String cJson = FileUtils.readFileToString(
                new File(Constants.GAME_DIR,"content.json"), Charset.defaultCharset());

        ContentIndex content = Launcher.GSON.fromJson(cJson, ContentIndex.class);
        if (!Constants.MODS_DIR.exists()) Constants.MODS_DIR.mkdirs();
        float c = 0;
        float m = content.mods.size();
        for (ContentIndex.Mod mod : content.mods) {
            currentAction = "Tartalom frissítése: " + mod.name;
            c++;
            progress = (c)/m;
            File f = new File(Constants.MODS_DIR, mod.fileName + ".jar");
            if (!f.exists()) {
                System.out.println("[Launcher] Downloading content " + mod.fileName);
                FileUtils.copyURLToFile(new URL(mod.url), f);
            }else if (!Crypto.calcSHA1(f).toLowerCase().equals(mod.sha1.toLowerCase())) {
                System.out.println("[Launcher] Updating content " + mod.fileName);
                FileUtils.copyURLToFile(new URL(mod.url), f);
            }
        }

        currentAction = "Indításra kész.";
        progress = 0;
        canPlay = true;
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
        URL url = new URL("http://resources.download.minecraft.net/"+hash.substring(0,2)+"/"+hash);
        File path = new File(Constants.GAME_DIR,"assets"+File.separator+"objects"+File.separator
            +hash.substring(0,2));
        if (!path.exists()) path.mkdirs();
        File asset = new File(path,hash);
        if (!asset.exists()) FileUtils.copyURLToFile(url,asset);
    }

    private void updateLibrary(Version.Library lib) throws IOException {
        currentAction = "Könyvtár letöltése: " + lib.name;
        File libFile = null;
        try {
            libFile = getLibPath(lib.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Version.Download download = lib.downloads.get("artifact");
        if (!Objects.requireNonNull(libFile).exists()) FileUtils.copyURLToFile(new URL(download.url), libFile);
        else if (!Crypto.calcSHA1(libFile).equalsIgnoreCase(download.sha1)) FileUtils.copyURLToFile(new URL(download.url), libFile);

        OsUtils.OS os = new OsUtils().getOs();

        if (lib.natives != null && lib.downloads != null) {
            String url = "";
            switch (os) {
                case WIN:
                    if (lib.natives.get("windows") != null) {
                        url = lib.downloads.get(lib.natives.get("windows")).url;
                    }
                    break;
                case OSX:
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
            File tmp = new File(Constants.GAME_DIR,"tmp");
            if (!tmp.exists()) tmp.mkdirs();
            FileUtils.copyURLToFile(new URL(url),new File(tmp,lib.name.replaceAll(":","_")+".jar"));

            unZipFile(new File(tmp,lib.name.replaceAll(":","_")+".jar"));
        }
    }
}
