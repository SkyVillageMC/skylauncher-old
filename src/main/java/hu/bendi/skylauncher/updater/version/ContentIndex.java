package hu.bendi.skylauncher.updater.version;

import java.util.List;

public class ContentIndex {
    public List<Mod> mods;

    public class Mod {
        public String name;
        public String fileName;
        public String url;
        public String sha1;
    }
}
