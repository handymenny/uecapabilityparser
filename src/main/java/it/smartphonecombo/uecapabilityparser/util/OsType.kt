package it.smartphonecombo.uecapabilityparser.util

enum class OsType {
    WINDOWS,
    LINUX,
    MAC,
    BSD,
    OTHER;

    companion object {
        val CURRENT: OsType =
            with(System.getProperty("os.name").lowercase()) {
                when {
                    "win" in this -> WINDOWS
                    "nix" in this -> LINUX
                    "nux" in this -> LINUX
                    "aix" in this -> LINUX
                    "mac" in this -> MAC
                    "bsd" in this -> BSD
                    else -> OTHER
                }
            }
    }
}
