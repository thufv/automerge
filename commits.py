#!/usr/bin/env python
# -*- coding: utf-8 -*-
# vim: ts=4 sw=4 expandtab:

# Author: Fengmin Zhu

# Useful wrappers for subprocess

import subprocess

# run shell and return the output
def sh(*args, **kwargs):
    if len(args) == 1:
        cmd = args[0]
        if isinstance(cmd, str):
            p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, **kwargs)
        elif isinstance(cmd, list):
            p = subprocess.Popen(cmd, stdout=subprocess.PIPE, **kwargs)
        else:
            raise TypeError('str or list expected')
    else:
        p = subprocess.Popen(args, stdout=subprocess.PIPE, **kwargs)

    (out, _) = p.communicate()
    return out.strip()

def pipe(first, then):
    if isinstance(first, list) and isinstance(then, list):
        p1 = subprocess.Popen(first, stdout=subprocess.PIPE)
        p2 = subprocess.Popen(then, stdin=p1.stdout, stdout=subprocess.PIPE)
        p1.stdout.close()
        
        (out, _) = p2.communicate()
        return out.strip()
    else:
        raise TypeError('list expected')

import os, sys, filecmp

ROOT = os.path.join('.', 'commits')         # output dir: commits
PROJ_ROOT = os.path.join('.', 'projects')   # input dir: repositories

sh('mkdir', '-p', ROOT)

# remove an empty dir
def remove_empty_dir(d):
    if os.path.exists(d) and not os.listdir(d):
        os.rmdir(d)

# collect all merge commits' meta info
def collect_merge_commits(proj_dir):
    print(proj_dir)
    log = sh('git log', cwd=proj_dir)
    lines = log.split('\n')
    commits = []
    
    state = 0
    for line in lines:
        if line.startswith('commit'):
            commit_id = line.split('commit ')[1]
        elif line.startswith('Merge:'):
            commit_parents = (line.split('Merge: ')[1]).split(' ')
            state = 1
        elif line.startswith('Author:'):
            pass
        elif line.startswith('Date:'):
            if state == 1:
                state = 2
        elif line == '':
            if state == 2:
                state = 3
            elif state == 4:
                if len(commit_parents) < 2:
                    print('Warning: Too few parents: %s' % commit_parents)
                    pass
                else:
                    if len(commit_parents) > 2:
                        print('Warning: Too many parents: %s' % commit_parents)
                        commit_parents = commit_parents[:2]

                    # left: developers' change, right: pull requests' change
                    # for pull request: parent1 = left, parent2 = right
                    # base: `git merge-base left right`
                    left = commit_parents[0]
                    right = commit_parents[1]
                    base = sh('git', 'merge-base', left, right, cwd=proj_dir)
                    if not base:
                        print('Warning: Base commit missing: %s' % commit_parents)
                        pass

                    commits.append({
                        'id': commit_id,
                        'msg': commit_message,
                        'base': base,
                        'left': left,
                        'right': right
                    })
                state = 0
        elif state == 3:
            commit_message = line.strip()
            state = 4

    # import pprint
    # pp = pprint.PrettyPrinter(indent=4)
    # pp.pprint(commits)

    return commits

# extract the source code of the commits specified in the meta info
def check_out_repo(proj_dir, dst_dir, commits):
    # remove non-java files
    def clean(f):
        if not os.path.exists(f):
            return

        if os.path.isfile(f):
            if not f.endswith('.java'):
                os.unlink(f)
        else:
            for f1 in os.listdir(f):
                clean(os.path.join(f, f1))
            remove_empty_dir(f)

    # check out the source code of a commit by hash
    def check_out(commit, version, commit_root):
        print('[checkout] %s as %s' % (commit[:8], version))
        git_dir = os.path.join(proj_dir, '.git')
        dst = os.path.join(commit_root, version)
        sh('mkdir', '-p', dst)
        if commit:
            pipe(['git', '--git-dir', git_dir, 'archive', commit], ['tar', '-xC', dst])
        else:
            print('[checkout] ignore empty commit')

        clean(dst)

    # check if files are the same
    def same_files(f1, f2, f3):
        return filecmp.cmp(f1, f2) and filecmp.cmp(f1, f3)

    # remove unchanged files in a commit
    def remove_unchanged_files(commit_root):
        def remove(f, changed_count, unchanged_count):
            base_f = os.path.join(commit_root, 'base', f)
            left_f = os.path.join(commit_root, 'left', f)
            right_f = os.path.join(commit_root, 'right', f)
            expected_f = os.path.join(commit_root, 'expected', f)

            if not os.path.exists(base_f):
                return changed_count, unchanged_count

            if os.path.isfile(base_f):
                if os.path.exists(left_f) and os.path.exists(right_f) and same_files(base_f, left_f, right_f):
                    # remove this
                    unchanged_count += 1

                    os.unlink(base_f)
                    os.unlink(left_f)
                    os.unlink(right_f)
                    if os.path.exists(expected_f):
                        os.unlink(expected_f)
                else:
                    changed_count += 1

            else:
                for f1 in os.listdir(base_f):
                    changed_count, unchanged_count = remove(os.path.join(f, f1), changed_count, unchanged_count)

                remove_empty_dir(base_f)
                remove_empty_dir(left_f)
                remove_empty_dir(right_f)
                remove_empty_dir(expected_f)

            return changed_count, unchanged_count

        changed_count, unchanged_count = remove('', 0, 0)
        print('Changed: %i/%i' % (changed_count, changed_count + unchanged_count))

    # entry
    total = len(commits)

    cnt = 0
    for commit in commits:
        cnt += 1
        print('[%i/%i] %s' % (cnt, total, commit['id'][:8]))
        
        if commit['base'][:7] == commit['left']:
            print('Ignore: base commit = left commit')
            continue
        if commit['base'][:7] == commit['right']:
            print('Ignore: base commit = right commit')
            continue

        commit_root = os.path.join(dst_dir, commit['id'])
        check_out(commit['id'], 'expected', commit_root)
        check_out(commit['base'], 'base', commit_root)
        check_out(commit['left'], 'left', commit_root)
        check_out(commit['right'], 'right', commit_root)

        remove_unchanged_files(commit_root)

# entry of extraction, for one project/repository
def run_on(proj_name):
    print('[run] %s' % proj_name)

    proj_dir = os.path.join(PROJ_ROOT, proj_name)
    dst_dir = os.path.join(ROOT, proj_name)
    os.system('mkdir -p %s' % dst_dir)
    commits = collect_merge_commits(proj_dir)
    check_out_repo(proj_dir, dst_dir, commits)


if __name__ == '__main__':
    if len(sys.argv) <= 1:
        print('Usage: %s repos...' % sys.argv[0])
    else:
        for proj in sys.argv[1:]:
            run_on(proj)
