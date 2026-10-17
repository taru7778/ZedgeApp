#!/usr/bin/env python3
"""Run this after filling in your values: python3 check_config.py
It lists every CONFIG placeholder that is still unfilled and checks that the
Firebase / R2 URLs are identical across the panel, generator, bots and app."""
import os, re, sys

HERE = os.path.dirname(os.path.abspath(__file__))
FILES = ['index.html', 'generator.yml', 'zedge1.yml', 'zedge2.yml', 'zedge3.yml',
         'app/app/src/main/java/com/zedge/contentstudio/core/Core.kt']
PLACEHOLDERS = re.compile(r'YOUR[-_][A-Z0-9_-]+|YOUR-SUBDOMAIN|000000000000')

bad = 0
for rel in FILES:
    p = os.path.join(HERE, rel)
    if not os.path.exists(p):
        print(f'!! missing file: {rel}'); bad += 1; continue
    for i, line in enumerate(open(p, encoding='utf-8'), 1):
        if PLACEHOLDERS.search(line) and 'OPTIONAL' not in line and 'CONFIG spot' not in line:
            print(f'UNFILLED  {rel}:{i}: {line.strip()[:110]}'); bad += 1

def grab(rel, pattern):
    return re.findall(pattern, open(os.path.join(HERE, rel), encoding='utf-8').read())

# consistency: DB URLs
db_panel = grab('index.html', r'databaseURL:\s*"([^"]+)"')
db_gen = grab('generator.yml', r"zedge_[1-4]:\s*'([^']+)'")[:4]
db_bots = [grab(f'zedge{n}.yml', r'const firebaseDatabaseURL = "([^"]+)"')[0] for n in range(1, 4)]
db_app = grab(FILES[-1], r'Account\("zedge[1-4]", "ZEDGE[1-4]", "([^"]+)"')
for n in range(3):
    vals = {db_panel[n], db_gen[n], db_bots[n], db_app[n]}
    if len(vals) != 1:
        print(f'MISMATCH  Firebase URL for account {n + 1} differs between files: {vals}'); bad += 1

r2 = set(grab('index.html', r'R2_WORKER_URL = "([^"]+)"') + grab('generator.yml', r"R2_WORKER_URL = '([^']+)'")
         + grab(FILES[-1], r'R2_WORKER_URL = "([^"]+)"')
         + [grab(f'zedge{n}.yml', r'const r2WorkerUrl = "([^"]+)"')[0] for n in range(1, 4)])
if len(r2) != 1:
    print(f'MISMATCH  R2 worker URL differs between files: {r2}'); bad += 1

print('\nOK - all config filled and consistent.' if bad == 0 else f'\n{bad} problem(s) found - fix the lines above.')
sys.exit(1 if bad else 0)
