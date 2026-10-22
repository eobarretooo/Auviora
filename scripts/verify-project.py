"""Check localized resources and native APK integrity without running inference."""
from pathlib import Path
import argparse
import struct
import zipfile
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parent.parent
parser = argparse.ArgumentParser()
parser.add_argument("--apk", type=Path)
args = parser.parse_args()

def strings(folder):
    return {e.attrib["name"]: e.text for e in ET.parse(root / "app/src/main/res" / folder / "strings.xml").getroot()}

english, portuguese = strings("values"), strings("values-pt-rBR")
assert english.keys() == portuguese.keys(), "Missing locale keys"
assert all(english.values()) and all(portuguese.values()), "Blank translations"
assert "NavigationBar(" not in (root / "app/src/main/java/app/auviora/android/AuvioraUi.kt").read_text(), "Bottom navigation prohibited"
assert "C:/Dev/MiniCPM" not in (root / "inference/src/main/cpp/CMakeLists.txt").read_text(), "Sibling runtime dependency"
print(f"PASS: {len(english)} paired English/pt-BR resources; left-navigation source contract")

if args.apk:
    with zipfile.ZipFile(args.apk) as apk:
        names = [n for n in apk.namelist() if n.startswith("lib/") and n.endswith(".so")]
        assert all(n.startswith("lib/arm64-v8a/") for n in names), "Unexpected ABI"
        assert not any(any(x in n for x in ["bench", "completion", "quantize", "libomni"]) for n in names), "Unrelated native tool library"
        assert "lib/arm64-v8a/libomp.so" in names, "OpenMP runtime missing"
        assert "lib/arm64-v8a/libauviora_inference.so" in names, "JNI bridge missing"
        for name in names:
            data = apk.read(name)
            assert data[:5] == b"\x7fELF\x02", name
            phoff = struct.unpack_from("<Q", data, 32)[0]
            size, count = struct.unpack_from("<HH", data, 54)
            for i in range(count):
                header = struct.unpack_from("<IIQQQQQQ", data, phoff + i * size)
                if header[0] == 1:
                    assert header[-1] >= 16384, f"Unaligned LOAD segment: {name}"
        print(f"PASS: {len(names)} ARM64 native libraries; 16KB ELF LOAD alignment; required JNI/OpenMP; no desktop tools")
