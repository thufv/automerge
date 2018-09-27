#!/usr/bin/env python
# -*- coding: utf-8 -*-
# vim: ts=4 sw=4 expandtab:

import os, sys, json, csv

# Specify the path of the generated `summary.json` file.
PROJECT_ROOT = os.path.abspath('.')
OUTPUTS_DIR = os.path.join(PROJECT_ROOT, 'outputs')
SUMMARY_FILE = os.path.join(OUTPUTS_DIR, 'summary.json')
CONFLICTS_FILE = os.path.join(OUTPUTS_DIR, 'conflicts.json')
MERGED_AST_FILE = os.path.join(OUTPUTS_DIR, 'default.mergedASTData.json')

# Specify the path of output csv files.
EXP1_OUTPUT = os.path.join(OUTPUTS_DIR, 'exp1.csv')
EXP2_OUTPUT = os.path.join(OUTPUTS_DIR, 'exp2.csv')
EXP3_OUTPUT = os.path.join(OUTPUTS_DIR, 'exp3.txt')

def gen_exp1(baseline_conf='default', ps_conf='PS'):
    fieldnames = ['Project', 'Conflict files', 'Holes', 'Resolved holes', 'Resolved Rate', 
                  'Max. k', 'Avg. k', 'P.S.', 'Time (ms)']

    # collect conflict files
    with open(CONFLICTS_FILE, 'r') as f:
        conflicts = json.loads(f.read())
        f.close()

    def dict_inc(d, k, v=1):
        if k in d:
            d[k] += v
        else:
            d[k] = v

    conflict_files_by_project = {}
    for c in conflicts:
        dict_inc(conflict_files_by_project, c['project'])

    projects = sorted(conflict_files_by_project.keys(), key=lambda x: x.lower())

    # collect experiment results
    with open(SUMMARY_FILE, 'r') as f:
        summary = json.loads(f.read())
        f.close()
    
    assert baseline_conf in summary

    data = summary[baseline_conf]
    results_by_project = {}
    for p in projects:
        holes = data['holes by project'][p]
        results_by_project[p] = {
            fieldnames[0]: p,
            fieldnames[1]: conflict_files_by_project[p],
            fieldnames[2]: holes,
            fieldnames[3]: data['resolved holes by project'][p],
            fieldnames[4]: data['resolved holes by project'][p] / float(holes),
            fieldnames[5]: data['max. k by project'][p],
            fieldnames[6]: data['total k by project'][p] / float(holes),
            fieldnames[8]: data['time by project'][p] / float(holes)
        }

    # collect P.S.
    assert ps_conf in summary

    data2 = summary[ps_conf]
    for p in projects:
        results_by_project[p][fieldnames[7]] = data2['total k by project'][p] / float(data2['holes by project'][p])

    # write table
    with open(EXP1_OUTPUT, 'w') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

        writer.writeheader()
        for p in projects:
            writer.writerow(results_by_project[p])  
        total_holes = sum(data['holes by project'].values())
        writer.writerow({
            fieldnames[0]: 'Overall',
            fieldnames[1]: sum(conflict_files_by_project.values()),
            fieldnames[2]: total_holes,
            fieldnames[3]: sum(data['resolved holes by project'].values()),
            fieldnames[4]: sum(data['resolved holes by project'].values()) / float(total_holes),
            fieldnames[5]: max(data['max. k by project'].values()),
            fieldnames[6]: sum(data['total k by project'].values()) / float(total_holes),
            fieldnames[7]: sum(data2['total k by project'].values()) / float(sum(data2['holes by project'].values())),
            fieldnames[8]: sum(data['time by project'].values()) / float(total_holes)
        })

def gen_exp2():
    fieldnames = ['Config', 'Better', 'Worse', 'Same']

    # load configs
    from options import OPTIONS
    configs = [label for label, opt in OPTIONS]

    # collect experiment results
    with open(SUMMARY_FILE, 'r') as f:
        summary = json.loads(f.read())
        f.close()

    # write table
    with open(EXP2_OUTPUT, 'w') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

        writer.writeheader()
        
        for conf in configs:
            assert conf in summary
            writer.writerow({
                fieldnames[0]: conf,
                fieldnames[1]: summary[conf]['better'],
                fieldnames[2]: summary[conf]['worse'],
                fieldnames[3]: summary[conf]['same']
            })

def gen_exp3():
    data = json.load(open(MERGED_AST_FILE, 'r'))
    depths = [d['depth'] for d in data]
    total_holes = len(depths)

    with open(EXP3_OUTPUT, 'w') as f:
        max_depth = max(depths)
        for d in range(2, max_depth + 1):
            c = len([d1 for d1 in depths if d1 >= d])
            per = 100 * c / float(total_holes)
            f.write('Depth >= %i: %i (%.2f%%)\n' % (d, c, per))

        max_by_depth = max(data, key=lambda d: d['depth'])
        f.write('Max. by depth: %s\n' % max_by_depth)
        max_by_size = max(data, key=lambda d: d['size'])
        f.write('Max. by size: %s\n' % max_by_size)

if __name__ == '__main__':
    if len(sys.argv) == 2:
        if sys.argv[1] == 'exp1':
            gen_exp1()
        elif sys.argv[1] == 'exp2':
            gen_exp2()
        elif sys.argv[1] == 'exp3':
            gen_exp3()
        else:
            print('Usage: %s [exp1|exp2|exp3]' % sys.argv[0])
    else:
        gen_exp1()
        gen_exp2()
        gen_exp3()
