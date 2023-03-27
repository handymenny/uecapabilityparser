package it.smartphonecombo.uecapabilityparser.util

enum class OsType {
    WINDOWS,
    LINUX,
    MAC,
    SOLARIS,
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
                    "sunos" in this -> SOLARIS
                    else -> OTHER
                }
            }
    }
}
