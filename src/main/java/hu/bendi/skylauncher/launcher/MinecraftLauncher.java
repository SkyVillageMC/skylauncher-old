package hu.bendi.skylauncher.launcher;

import hu.bendi.skylauncher.Launcher;
import hu.bendi.skylauncher.settings.LauncherSettings;
import hu.bendi.skylauncher.updater.Updater;
import hu.bendi.skylauncher.updater.version.Version;
import hu.bendi.skylauncher.utils.Constants;
import hu.bendi.skylauncher.utils.OsUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static hu.bendi.skylauncher.Launcher.LOGGER;

public class MinecraftLauncher {

    public static Version ver;

    public void play(String username) throws IOException {
        Updater.canPlay = false;
        if (ver == null) {
            Launcher.executorService.submit(() -> {
                try {
                    new Updater().checkForUpdates();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return;
        }

        Updater.currentAction = "J\u00E1t\u00E9k ind\u00EDt\u00E1sa.";

        ProcessBuilder pb = new ProcessBuilder();
        pb.redirectOutput(new File(Constants.GAME_DIR,"game.log"));
        pb.redirectError(new File(Constants.GAME_DIR,"mcerr.log"));
        List<String> args = getArgs("91cffd14-8c2a-39ef-9534-931c99c457f2",username);
        pb.command(args);
        pb.directory(Constants.GAME_DIR);
        LOGGER.info("Launching game with arguments: " + Arrays.toString(args.toArray()));
        Process p = pb.start();
        try {
            p.waitFor(5, TimeUnit.SECONDS);
            if (!p.isAlive()) System.out.println(p.exitValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toolkit.getDefaultToolkit().beep();
        }

    }

    public List<String> getArgs(String accesToken, String username) {
        OsUtils.OS os = OsUtils.getOs();
        List<String> args = new ArrayList<>();
        args.add("java");
        args.add("-Xmx" + LauncherSettings.customRam +"M");
        if (os != OsUtils.OS.LINUX) {
            args.add("\"-Djava.library.path="+Constants.NATIVES_DIR.getAbsolutePath()+"\"");
        } else {
            args.add("-Djava.library.path="+Constants.NATIVES_DIR.getAbsolutePath());
            args.add("-Dorg.lwjgl.librarypath="+Constants.NATIVES_DIR.getAbsolutePath());
        }

        args.add("-Dminecraft.launcher.brand=java-minecraft-launcher");
        if (os != OsUtils.OS.LINUX) {
            args.add("\"-Dminecraft.launcher.version=SkyLauncher\"");
        } else {
            args.add("-Dminecraft.launcher.version=SkyLauncher");
        }
        args.add("-cp");
        args.add(getCp());
        args.add(ver.mainClass);
        args.add("--gameDir");
        args.add(Constants.GAME_DIR.getPath());
        args.add("--assetsDir");
        args.add(new File(Constants.GAME_DIR,"assets").getAbsolutePath());
        args.add("--assetIndex");
        args.add(ver.assetIndex.id);
        if (LauncherSettings.useCustomResolution) {
            args.add("--width");
            args.add(String.valueOf(LauncherSettings.height));
            args.add("--height");
            args.add(String.valueOf(LauncherSettings.width));
        }
        args.add("--uuid");
        args.add("91cffd148c2a39ef9534931c99c457f2");
        args.add("--accessToken");
        args.add(accesToken);
        args.add("--version");
        args.add("1.16.5");
        args.add("--userType");
        args.add("mojang");
        args.add("--versionType");
        args.add("release");
        args.add("--username");
        args.add(username);

        return args;
    }

    private String getCp() {
        StringBuilder sb = new StringBuilder();
        OsUtils.OS os = OsUtils.getOs();
        if (os != OsUtils.OS.LINUX) sb.append("\"");
        Arrays.asList(ver.libraries).forEach(lib -> {
            if (lib.onlyIn != null) {
                if (lib.onlyIn[0].equalsIgnoreCase("OSX")) {
                    if (os == OsUtils.OS.OSX) sb.append(Updater.getLibPath(lib.name).getAbsolutePath()).append(os != OsUtils.OS.LINUX ? ";" : ":");
                } else if (!lib.onlyIn[0].equalsIgnoreCase("OSX")) {
                    if (os != OsUtils.OS.OSX) sb.append(Updater.getLibPath(lib.name).getAbsolutePath()).append(os != OsUtils.OS.LINUX ? ";" : ":");
                }
            }else {
                sb.append(Updater.getLibPath(lib.name).getPath()).append(os != OsUtils.OS.LINUX ? ";" : ":");
            }
        });
        if (os == OsUtils.OS.LINUX) {
            for (int i = 0; i < 8; i++) {
                sb.append(new File(Constants.NATIVES_DIR, "native" + i + ".jar")).append(":");
            }
        }
        sb.append(new File(Constants.GAME_DIR,"client.jar").getAbsolutePath());
        if (os != OsUtils.OS.LINUX) sb.append("\"");
        return sb.toString();
    }


}
