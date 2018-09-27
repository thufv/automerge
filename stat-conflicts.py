#!/usr/bin/env python
# -*- coding: utf-8 -*-
# vim: ts=4 sw=4 expandtab:

import os, sys, json, re

PROJECT_ROOT = os.path.abspath('.')
DATA_DIR = os.path.join(PROJECT_ROOT, 'outputs')
CONFLICTS_FILE = os.path.join(DATA_DIR, 'conflicts.json')

def hash_merge_scenario(scenario):
    return '%s:%s:%s:%s' % (scenario['base'], scenario['left'], scenario['right'], scenario['expected'])

def div(x, y):
    if y == 0:
        return 0

    return x / float(y)

def process_log(log_file, filtered_log_file, json_file, merged_AST_data_file):
    records = []
    filtered_lines = []
    new_lines_added = False

    artifact = {}
    syn = {}

    merged_AST_data = []
    AST_data_regex = re.compile(r'depth = (?P<depth>\d+), size = (?P<size>\d+)')

    with open(log_file, 'r') as f:
        for line in f.readlines():
            if line.startswith('['):
                line = line[11:]

            # special lines for merge
            if line.startswith('INFO: Merging:'):
                if artifact: # store
                    records.append(artifact.copy())
                    if new_lines_added:
                        filtered_lines.append('---------------END---------------\n\n')

                artifact = {} # init
                artifact['error'] = False
                artifact['scenario'] = {}
                artifact['syn'] = []
                new_lines_added = False
            elif line.startswith('Left: '):
                artifact['scenario']['left'] = line[6:].strip()
            elif line.startswith('Right: '):
                artifact['scenario']['right'] = line[7:].strip()
            elif line.startswith('Base: '):
                artifact['scenario']['base'] = line[6:].strip()
                if artifact['scenario']['base'].startswith('/tmp'):
                    artifact['scenario']['base'] = ''
            elif line.startswith('INFO: Expected:'):
                artifact['scenario']['expected'] = line[15:].strip()
            elif line.startswith('SEVERE: Exception while merging'):
                artifact['error'] = True

                new_lines_added = True
                filtered_lines.append(line)
            elif line.startswith('The class'):
                artifact['error'] = True

                new_lines_added = True
                filtered_lines.append(line)

            # special lines for synthesis
            elif line.startswith('INFO: Synthesis: Expected'):
                syn = {} # init
                syn['error'] = False

                new_lines_added = True
                filtered_lines.append(line)
            elif line.startswith('INFO: Synthesis: Searched depth: '):
                syn['depth'] = int(line[33:])
            elif line.startswith('INFO: Synthesis: Searched total steps: '):
                syn['steps'] = int(line[39:])
            elif line.startswith('SUCCESS: Synthesis: FOUND'):
                syn['found'] = True
            elif line.startswith('SEVERE: Synthesis: NOT FOUND'):
                syn['found'] = False

                new_lines_added = True
                filtered_lines.append(line)
            elif line.startswith('INFO: Synthesis time: '):
                syn['time'] = int(line[22:].split(' ')[0])
                artifact['syn'].append(syn.copy()) # store
            elif line.startswith('SEVERE: Exception while synthesizing'):
                syn['error'] = True
                artifact['syn'].append(syn.copy()) # store

                new_lines_added = True
                filtered_lines.append(line)

            # additional: extract the depth and size of merged AST
            elif line.startswith('INFO: Synthesis: found a tree with '):
            	m = AST_data_regex.match(line[35:])
            	merged_AST_data.append({
            		'depth': int(m.group('depth')),
            		'size': int(m.group('size'))
            	})

            # ignored lines
            elif line.startswith('Logging configuration file JDimeLogging.properties does not exist. Falling back to defaults.'):
                pass
            elif line.startswith('WARNING: JDime.properties can not be used as a config file as it does not exist.'):
                pass

            # store lines where exception may occur
            else:
                new_lines_added = True
                filtered_lines.append(line)

        f.close()

    if artifact: # store last one
        records.append(artifact.copy())

    # dump filter log
    with open(filtered_log_file, 'w+') as f:
        f.writelines(filtered_lines)
        f.close()

    # write records to json
    with open(json_file, 'w+') as f:
        f.write(json.dumps(records))
        f.close()

    # write merged AST data to json
    with open(merged_AST_data_file, 'w+') as f:
        f.write(json.dumps(merged_AST_data))
        f.close()

    return records

def get_project(scenario):
    return scenario['left'].split('commits/')[1].split('/')[0]

def dict_max(d, k, e):
    if k in d:
        if e > d[k]:
            d[k]=e
    else:
        d[k] = e

def dict_inc(d, k, v=1):
    if k in d:
        d[k] += v
    else:
        d[k] = v

def run(configs, baseline_config):
    assert baseline_config in configs

    data = {}

    for conf in configs:
        records = process_log(
            os.path.join(DATA_DIR, '%s.log' % conf),
            os.path.join(DATA_DIR, '%s.filtered.log' % conf),
            os.path.join(DATA_DIR, '%s.json' % conf),
            os.path.join(DATA_DIR, '%s.mergedASTData.json' % conf)
        )

        for rec in records:
            suffix = hash_merge_scenario(rec['scenario'])
            cnt = 0

            for syn in rec['syn']:
                cnt += 1
                hole_id = '%s:%i' % (suffix, cnt)
                if hole_id not in data:
                    data[hole_id] = {}

                is_found = (not syn['error']) and syn['found']
                data[hole_id][conf] = {
                    'found': is_found,
                    'steps': syn['steps'] if 'steps' in syn else 0,
                    'time': syn['time'] if not syn['error'] else 0,
                    'project': get_project(rec['scenario'])
                }

    # summary
    summary = {}

    for conf in configs:
        better_count = 0
        worse_count = 0
        same_count = 0

        found_count = 0
        delta_steps = 0
        total_steps = 0

        steps_by_project = {}
        time_by_project = {}
        holes_by_project = {}
        found_by_project = {}
        max_step_by_project = {}

        for hole_id in data:
            if conf not in data[hole_id]:
                continue
            now = data[hole_id][conf]

            total_steps += now['steps']

            dict_inc(steps_by_project, now['project'], now['steps'])
            dict_inc(time_by_project, now['project'], now['time'])
            dict_inc(holes_by_project, now['project'])
            dict_max(max_step_by_project, now['project'], now['steps'])

            if baseline_config not in data[hole_id]:
                continue
            baseline = data[hole_id][baseline_config]   
            delta = now['steps'] - baseline['steps']
            delta_steps += delta         

            if baseline['found']:
                if now['found']:
                    found_count += 1
                    dict_inc(found_by_project, now['project'])

                    if delta < 0:
                        better_count += 1
                    elif delta > 0:
                        worse_count += 1
                    else:
                        same_count += 1
                else:
                    worse_count += 1
            else:
                if now['found']:
                    found_count += 1
                    dict_inc(found_by_project, now['project'])
                    better_count += 1
                else:
                    same_count += 1

        summary[conf] = {
            'better': better_count,
            'worse': worse_count,
            'same': same_count,

            'total resolved holes': found_count,
            'total k': total_steps,

            'holes by project': holes_by_project,
            'resolved holes by project': found_by_project,
            'total k by project': steps_by_project,
            'max. k by project': max_step_by_project,
            'time by project': time_by_project,
        }

    output_file = os.path.join(DATA_DIR, 'summary.json')
    with open(output_file, 'w+') as f:
        f.write(json.dumps(summary, sort_keys=True, indent=4, separators=(',', ': ')))
        f.close()

if __name__ == '__main__':
    if len(sys.argv) > 2 and sys.argv[1] == '--only':
        opts = sys.argv[2:]
    else:
        others = sys.argv[1:]

        from options import OPTIONS
        opts = [label for (label, _) in OPTIONS]
        opts.extend(others)

    run(opts, opts[0])
