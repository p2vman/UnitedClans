package unitedclans.langs;

import org.bukkit.configuration.file.YamlConfiguration;

public class Langs {
    public enum LangEN {
        TITLE("title", "musi "),
        MSG("hello", "hi ");
        private String path;
        private String msg;
        private static YamlConfiguration LANG;

        LangEN(String path, String msg) {
            this.path = path;
            this.msg = msg;
        }

        public static void setFile(YamlConfiguration config) {
            LANG = config;
        }

        @Override
        public String toString() {
            return LANG.getString(path, msg);
        }

        public String getMsg() {
            return this.msg;
        }
        public String getPath() {
            return this.path;
        }
    }

    public enum LangRU {
        TITLE("title", "муси "),
        MSG("hello", "привет ");
        private String path;
        private String msg;
        private static YamlConfiguration LANG;

        LangRU(String path, String msg) {
            this.path = path;
            this.msg = msg;
        }

        public static void setFile(YamlConfiguration config) {
            LANG = config;
        }

        @Override
        public String toString() {
            return LANG.getString(path, msg);
        }

        public String getMsg() {
            return this.msg;
        }
        public String getPath() {
            return this.path;
        }
    }
}