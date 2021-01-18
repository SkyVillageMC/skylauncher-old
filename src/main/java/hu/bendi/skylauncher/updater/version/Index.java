package hu.bendi.skylauncher.updater.version;

import java.util.HashMap;

public class Index {
    public HashMap<String, Object> objects;

    public class Object {
        public String hash;
        public double size;
    }
}
