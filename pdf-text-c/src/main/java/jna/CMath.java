package jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface CMath extends Library {
    CMath INSTANCE = Native.loadLibrary(Platform.isWindows() ? "msvcrt" : "c", CMath.class);
    double cosh(double value);
}