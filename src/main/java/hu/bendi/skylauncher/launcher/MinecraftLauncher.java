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

        Updater.currentAction = "Játék indítása.";

        ProcessBuilder pb = new ProcessBuilder();
        pb.redirectOutput(new File(Constants.GAME_DIR,"game.log"));
        pb.redirectError(new File(Constants.GAME_DIR,"mcerr.log"));
        pb.command(getArgs("91cffd14-8c2a-39ef-9534-931c99c457f2",username));
        pb.directory(Constants.GAME_DIR);
        Process p = pb.start();
        try {
            p.waitFor(3, TimeUnit.SECONDS);
            if (!p.isAlive()) System.out.println(p.exitValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toolkit.getDefaultToolkit().beep();
        }

    }

    public List<String> getArgs(String accesToken, String username) {
        List<String> args = new ArrayList<>();
        args.add("java");//("C:\\Program Files\\Java\\jdk-12.0.2\\bin\\javaw.exe");
        args.add("-Xmx" + LauncherSettings.customRam +"M");
//        args.add("-XX:+UseConcMarkSweepGC");
//        args.add("-XX:+CMSIncrementalMode");
//        args.add("-XX:-UseAdaptiveSizePolicy");
//        args.add("-Xmn128M");
//        args.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
        args.add("\"-Djava.library.path="+Constants.NATIVES_DIR.getPath()+"\"");
        args.add("-Dminecraft.launcher.brand=java-minecraft-launcher");
        args.add("\"-Dminecraft.launcher.version=SkyLauncher\"");
        args.add("-cp");
        args.add(getCp());
        args.add(ver.mainClass);
        args.add("--gameDir");
        args.add(Constants.GAME_DIR.getPath());
        args.add("--assetsDir");
        args.add(new File(Constants.GAME_DIR,"assets").getPath());
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
        OsUtils.OS os = new OsUtils().getOs();
        sb.append("\"");
        Arrays.asList(ver.libraries).forEach(lib -> {
            if (lib.onlyIn != null) {
                if (lib.onlyIn[0].equalsIgnoreCase("OSX")) {
                    if (os == OsUtils.OS.OSX) sb.append(Updater.getLibPath(lib.name).getPath()+";");
                } else if (!lib.onlyIn[0].equalsIgnoreCase("OSX")) {
                    if (os != OsUtils.OS.OSX) sb.append(Updater.getLibPath(lib.name).getPath()+";");
                }
            }else {
                sb.append(Updater.getLibPath(lib.name).getPath()+";");
            }
        });
        sb.append(new File(Constants.GAME_DIR,"client.jar").getPath()+"\"");
        return sb.toString();
    }


}
