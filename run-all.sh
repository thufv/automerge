#!/usr/bin/env bash

./run-conflicts.py
./run-conflicts.py PS -PS
./stat-conflicts.py PS
./gen-tables.py

echo "Done. Please check 'outputs/exp1.csv', 'outputs/exp2.csv', and 'outputs/exp3.txt'."