"""
Render the Mermaid diagrams to PNG via the Kroki service.
Stdlib only (no pip installs):  python docs/render_diagrams.py
"""
import urllib.request
import os

KROKI = "https://kroki.io/mermaid/png"
HERE = os.path.dirname(os.path.abspath(__file__))

JOBS = [("erd.mmd", "erd.png"), ("flow.mmd", "flow.png")]

for src, out in JOBS:
    src_path = os.path.join(HERE, src)
    out_path = os.path.join(HERE, out)
    with open(src_path, "rb") as f:
        body = f.read()
    req = urllib.request.Request(KROKI, data=body,
                                 headers={"Content-Type": "text/plain",
                                          "Accept": "image/png",
                                          "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"})
    with urllib.request.urlopen(req, timeout=60) as resp:
        png = resp.read()
    with open(out_path, "wb") as f:
        f.write(png)
    print(f"Wrote {out} ({len(png)} bytes)")
