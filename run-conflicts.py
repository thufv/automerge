#!/usr/bin/env python
# -*- coding: utf-8 -*-
# vim: ts=4 sw=4 expandtab:

import os, sys, json

PROJECT_ROOT = os.path.abspath('.')
BIN_DIR = os.path.join(PROJECT_ROOT, 'bin')
OUTPUTS_DIR = os.path.join(PROJECT_ROOT, 'outputs')
CONFLICTS_FILE = os.path.join(OUTPUTS_DIR, 'conflicts.json')

# Specify automerge .jar executable file name.
JAR = 'AutoMerge.jar'

import subprocess
from threading import Timer

def call(arg, logfile, timeout=15 * 60):
    p = subprocess.Popen(arg, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True, cwd=BIN_DIR)

    def kill(p):
        p.kill()
        print('Timeout!')
        os.system('echo "Timeout!" >> %s' % logfile)

    timer = Timer(timeout, kill, [p])

    try:
        timer.start()
        stdout, stderr = p.communicate()
        return_code = p.returncode
        return (stdout, stderr, return_code)
    finally:
        timer.cancel()

def run_with_option(label, options=[]):
    print('[Options] %s' % (options if options else '<default>'))

    total = len(conflicts)
    cnt = 0
    prefix_len = len(os.path.join(PROJECT_ROOT, 'commits'))

    for conflict in conflicts:
        cnt += 1

        base = os.path.join(PROJECT_ROOT, conflict['base']) if conflict['base'] != '' else ''
        left = os.path.join(PROJECT_ROOT, conflict['left'])
        right = os.path.join(PROJECT_ROOT, conflict['right'])
        expected = os.path.join(PROJECT_ROOT, conflict['expected'])

        log = os.path.join(OUTPUTS_DIR, '%s.log' % label)

        cmd = 'java -jar %s -e %s -o tmp.java -m structured -log info -f -S ' % (JAR, expected)
        cmd += '%s ' % (' '.join(options))
        cmd += '%s %s %s >> %s 2>&1' % (left, base, right, log)
        print('[%i/%i] Running %s' % (cnt, total, left))

        call(cmd, log)

def run():
    from options import OPTIONS
    for label, opt in OPTIONS:
        run_with_option(label, opt)

if __name__ == '__main__':
    # load conflicts
    with open(CONFLICTS_FILE, 'r') as f:
        conflicts = json.loads(f.read())
        f.close()

    if len(sys.argv) == 2:
        run_with_option(sys.argv[1])
    elif len(sys.argv) > 2:
        run_with_option(sys.argv[1], sys.argv[2:])
    else:
        run()
