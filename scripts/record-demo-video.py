#!/usr/bin/env python3
"""Record a short demo video of CBoard local login and dashboard."""
from pathlib import Path
from playwright.sync_api import sync_playwright

BASE = "http://127.0.0.1:8080/bdp"
OUT_DIR = Path(__file__).resolve().parent.parent / "docs" / "videos"
OUT_DIR.mkdir(parents=True, exist_ok=True)


def main() -> None:
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(
            viewport={"width": 1280, "height": 720},
            record_video_dir=str(OUT_DIR),
            record_video_size={"width": 1280, "height": 720},
        )
        page = context.new_page()
        page.goto(f"{BASE}/login.jsp", wait_until="networkidle")
        page.wait_for_timeout(1500)
        page.select_option("#businessCode", "SY")
        page.fill('input[name="v1"]', "000admin")
        page.fill('input[name="v2"]', "qwerty!23")
        page.click('input[type="submit"]')
        page.wait_for_url("**/starter.jsp**", timeout=15000)
        page.wait_for_timeout(4000)
        title = page.title()
        context.close()
        browser.close()

    webm_files = sorted(OUT_DIR.glob("*.webm"), key=lambda f: f.stat().st_mtime)
    if not webm_files:
        raise SystemExit("No video file recorded")
    latest = webm_files[-1]
    target_webm = OUT_DIR / "cboard-verification-demo.webm"
    latest.rename(target_webm)
    print(f"Recorded: {target_webm} (title after login: {title})")


if __name__ == "__main__":
    main()
