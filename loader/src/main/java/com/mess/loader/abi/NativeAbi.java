package com.mess.loader.abi;

public enum NativeAbi {
    UNKNOWN(-1, ""),
    X86(3, "x86"),
    X86_64(64, "x86_64"),
    ARMEABI_V7A(40, "armeabi-v7a"),
    ARM64_V8A(183, "arm64-v8a");

    private final int magic;
    private final String arch;

    NativeAbi(int magic, String arch) {
        this.magic = magic;
        this.arch = arch;
    }

    public int getMagic() {
        return magic;
    }

    public String getArch() {
        return arch;
    }

    public static NativeAbi create(int magic) {
        for (NativeAbi abi : values()) {
            if (abi.magic == magic) {
                return abi;
            }
        }
        return UNKNOWN;
    }
}
