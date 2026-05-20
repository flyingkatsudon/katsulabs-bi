import { chromium } from 'playwright';
import { mkdirSync, readdirSync, renameSync } from 'fs';
import { join } from 'path';

const ARTIFACTS = '/opt/cursor/artifacts';
const OUT = join(ARTIFACTS, 'cboard-renewal-demo.webm');

mkdirSync(ARTIFACTS, { recursive: true });

const browser = await chromium.launch({ headless: true });
const context = await browser.newContext({
  viewport: { width: 1280, height: 720 },
  recordVideo: { dir: ARTIFACTS, size: { width: 1280, height: 720 } },
});
const page = await context.newPage();

try {
  await page.goto('http://localhost:5173/login', { waitUntil: 'networkidle', timeout: 60000 });
  await page.waitForTimeout(800);
  await page.getByRole('button', { name: '로그인' }).click();
  await page.waitForURL((url) => !url.pathname.includes('/login'), { timeout: 30000 });
  await page.waitForTimeout(1500);

  const demoBoard = page.getByRole('link', { name: /데모 대시보드/ });
  if (await demoBoard.count()) {
    await demoBoard.first().click();
    await page.waitForTimeout(2500);
  }

  await page.goto('http://localhost:5173/config/datasource', { waitUntil: 'networkidle' });
  await page.waitForTimeout(1500);
  await page.goto('http://localhost:5173/config/widget', { waitUntil: 'networkidle' });
  await page.waitForTimeout(1500);
  await page.goto('http://localhost:5173/config/board', { waitUntil: 'networkidle' });
  await page.waitForTimeout(2000);
} finally {
  await context.close();
  await browser.close();
}

const videos = readdirSync(ARTIFACTS).filter((f) => f.endsWith('.webm'));
if (videos.length > 0) {
  renameSync(join(ARTIFACTS, videos[videos.length - 1]), OUT);
  console.log('VIDEO:' + OUT);
}
