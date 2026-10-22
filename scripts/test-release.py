"""Exercise release verification against the actual unsigned APK and hostile local ZIP fixtures."""
import os
import tempfile
import unittest
import zipfile
from pathlib import Path

import release


class ReleaseChecks(unittest.TestCase):
    sdk = Path(os.environ["ANDROID_HOME"])
    apk = release.ROOT / "app/build/outputs/apk/release/app-release-unsigned.apk"

    def test_real_unsigned_candidate_is_explicitly_not_distributable(self):
        result = release.verify(self.apk, self.sdk, allow_unsigned=True)
        self.assertFalse(result["signed"])
        self.assertFalse(result["bundled_weights"])
        self.assertEqual(len(result["native_libraries"]), 10)

    def test_unsigned_apk_cannot_pass_distribution_gate(self):
        with self.assertRaisesRegex(ValueError, "signature verification failed"):
            release.verify(self.apk, self.sdk)

    def test_debug_apk_is_rejected(self):
        with self.assertRaisesRegex(ValueError, "debuggable metadata"):
            release.verify(release.ROOT / "app/build/outputs/apk/debug/app-debug.apk", self.sdk, allow_unsigned=True)

    def modified(self, replacement=None, extra=None):
        temporary = tempfile.TemporaryDirectory(dir=release.ROOT / "artifacts")
        self.addCleanup(temporary.cleanup)
        target = Path(temporary.name) / "hostile-fixture.apk"
        with zipfile.ZipFile(self.apk) as original, zipfile.ZipFile(target, "w") as changed:
            for entry in original.infolist():
                data = original.read(entry)
                if replacement and entry.filename == replacement[0]:
                    data = replacement[1]
                changed.writestr(entry, data)
            if extra:
                changed.writestr(*extra)
        return target

    def test_renamed_gguf_payload_is_rejected(self):
        apk = self.modified(extra=("assets/hidden-model.bin", b"GGUFfixture-only"))
        with self.assertRaisesRegex(ValueError, "Bundled model weights"):
            release.verify(apk, self.sdk, allow_unsigned=True)

    def test_notice_mismatch_is_rejected(self):
        apk = self.modified(replacement=("assets/third-party-notices.txt", b"incorrect"))
        with self.assertRaisesRegex(ValueError, "notices do not match"):
            release.verify(apk, self.sdk, allow_unsigned=True)

    def test_stale_embedded_source_commit_is_rejected(self):
        apk = self.modified(replacement=("assets/auviora-release-source.txt", b"0" * 40 + b"\n"))
        with self.assertRaisesRegex(ValueError, "different source commit"):
            release.verify(apk, self.sdk, allow_unsigned=True)


if __name__ == "__main__":
    unittest.main()
