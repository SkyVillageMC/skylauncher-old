package hu.bendi.skylauncher.updater.version;

import java.util.HashMap;
import java.util.Map;

public class Version {
    public String id;
    public String type;
    public String mainClass;
    public String assets;
    public AssetIndex assetIndex;
    public Download client;
    public Library[] libraries;

    public class AssetIndex {
        public double totalSize;
        public String id;
        public boolean known;
        public String url;
        public String sha1;
        public double size;
    }

    public class Download {
        public String url;
        public String sha1;
        public double size;
    }

    public class Library {
        public String name;
        public String[] onlyIn;
        public Map<String,Download> downloads = new HashMap<>();
        public HashMap<String,String> natives;
    }
}
