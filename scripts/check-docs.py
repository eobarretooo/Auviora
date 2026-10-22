"""Check local Markdown/HTML links, media references and fenced blocks without network access."""
import argparse
import re
from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import unquote, urlsplit


class Links(HTMLParser):
    def __init__(self):
        super().__init__()
        self.targets = []

    def handle_starttag(self, tag, attrs):
        for name, value in attrs:
            if name in ("src", "href") and value:
                self.targets.append(value)


def targets(text):
    # Exclude examples/code, which are not clickable Markdown links.
    fence = None
    prose = []
    for line in text.splitlines():
        match = re.match(r"^\s*(`{3,}|~{3,})", line)
        if match:
            marker = match[1]
            if fence is None:
                fence = marker
            elif marker[0] == fence[0] and len(marker) >= len(fence):
                fence = None
        elif fence is None:
            prose.append(line)
    if fence:
        raise ValueError("Unclosed fenced code block")
    text = "\n".join(prose)
    html = Links()
    html.feed(text)
    return html.targets + re.findall(r"!?\[[^\]]*\]\(<?([^\s)>]+)>?\)", text)


def anchors(text):
    found = set(re.findall(r'<a\s+(?:id|name)="([^"]+)"', text))
    counts = {}
    for heading in re.findall(r"^#{1,6}\s+(.+?)\s*#*\s*$", text, re.M):
        slug = re.sub(r"[^\w\- ]", "", heading.lower()).replace(" ", "-")
        count = counts.get(slug, 0)
        counts[slug] = count + 1
        found.add(slug + (f"-{count}" if count else ""))
    return found


def check(root):
    paths = sorted({*root.glob("*.md"), *root.glob("docs/**/*.md")})
    errors, references = [], 0
    for path in paths:
        text = path.read_text(encoding="utf-8")
        try:
            links = targets(text)
        except ValueError as error:
            errors.append(f"{path.relative_to(root)}: {error}")
            continue
        for target in links:
            url = urlsplit(target)
            if url.scheme or url.netloc:
                continue
            references += 1
            dest = (path.parent / unquote(url.path)).resolve() if url.path else path
            if not dest.is_relative_to(root) or not dest.exists():
                errors.append(f"{path.relative_to(root)}: missing/escaping target {target}")
            elif url.fragment and dest.suffix == ".md":
                if unquote(url.fragment) not in anchors(dest.read_text(encoding="utf-8")):
                    errors.append(f"{path.relative_to(root)}: missing anchor {target}")
    if errors:
        raise ValueError("\n".join(errors))
    print(f"PASS: {len(paths)} Markdown files, {references} local links/media/anchors, closed fences")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parent.parent)
    check(parser.parse_args().root.resolve())
