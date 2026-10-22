"""Capture real Android media and optimize it with ADB/FFmpeg (Python 3.10+)."""
import argparse
import json
import re
import subprocess
from pathlib import Path


def run(*args):
    return subprocess.run(args, check=True, capture_output=True, timeout=240).stdout


def probe(path):
    return json.loads(run("ffprobe", "-v", "error", "-count_frames", "-show_streams",
                          "-show_format", "-of", "json", str(path)))


def crop(value):
    if not re.fullmatch(r"\d+:\d+:\d+:\d+", value):
        raise argparse.ArgumentTypeError("Use WIDTH:HEIGHT:X:Y, in source pixels")
    return value


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    for name in ("capture", "record"):
        p = commands.add_parser(name)
        p.add_argument("--serial", required=True)
        p.add_argument("--output", type=Path, required=True)
        if name == "capture":
            p.add_argument("--crop", type=crop, required=True)
            p.add_argument("--width", type=int, default=608)
        else:
            p.add_argument("--seconds", type=int, choices=range(1, 181), default=15)
    p = commands.add_parser("convert")
    p.add_argument("input", type=Path)
    p.add_argument("output", type=Path)
    p.add_argument("--crop", type=crop, required=True)
    p.add_argument("--start", type=float, default=0)
    p.add_argument("--seconds", type=float, default=15)
    p.add_argument("--width", type=int, default=480)
    p.add_argument("--fps", type=int, default=12)
    p = commands.add_parser("check")
    p.add_argument("paths", type=Path, nargs="+")
    args = parser.parse_args()
    if args.command == "check":
        for path in args.paths:
            data = probe(path)
            stream = data["streams"][0]
            size = path.stat().st_size
            assert 0 < size < 8 * 1024 * 1024, f"Size budget exceeded: {path}"
            assert stream["width"] >= 320 and stream["height"] >= 600
            if path.suffix.lower() == ".gif":
                assert int(stream["nb_read_frames"]) > 1
                assert 8 <= float(data["format"]["duration"]) <= 15.2
            print(json.dumps({"path": path.as_posix(), "bytes": size,
                              "width": stream["width"], "height": stream["height"],
                              "frames": stream.get("nb_read_frames"),
                              "duration": data["format"].get("duration")}))
        return
    args.output.parent.mkdir(parents=True, exist_ok=True)
    if args.output.exists():
        raise FileExistsError(f"Refusing to overwrite {args.output}")
    if args.command == "capture":
        raw = run("adb", "-s", args.serial, "exec-out", "screencap", "-p")
        subprocess.run(["ffmpeg", "-v", "error", "-f", "image2pipe", "-i", "pipe:0",
                        "-vf", f"crop={args.crop},scale={args.width}:-1:flags=lanczos",
                        "-frames:v", "1", "-compression_level", "9", str(args.output)],
                       input=raw, check=True, capture_output=True, timeout=60)
    elif args.command == "record":
        # Do not change app data, locale or network settings. Drive the real UI separately.
        remote = "/data/local/tmp/auviora-media-" + str(__import__("time").time_ns()) + ".mp4"
        run("adb", "-s", args.serial, "shell", "screenrecord", "--bit-rate", "4M",
            "--time-limit", str(args.seconds), remote)
        run("adb", "-s", args.serial, "pull", remote, str(args.output))
        run("adb", "-s", args.serial, "shell", "rm", remote)
    else:
        if args.seconds <= 0 or args.start < 0 or not 1 <= args.fps <= 30:
            parser.error("Invalid time or frame rate")
        filters = (f"crop={args.crop},fps={args.fps},scale={args.width}:-1:flags=lanczos,"
                   "split[a][b];[a]palettegen=stats_mode=diff[p];"
                   "[b][p]paletteuse=dither=sierra2_4a")
        run("ffmpeg", "-v", "error", "-ss", str(args.start), "-i", str(args.input),
            "-t", str(args.seconds), "-filter_complex", filters, "-loop", "0", str(args.output))
    print(args.output.as_posix())


if __name__ == "__main__":
    main()
