/**
 * Copyright (C) 2013-2014 Olaf Lessenich
 * Copyright (C) 2014-2015 University of Passau, Germany
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 * <p>
 * Contributors:
 * Olaf Lessenich <lessenic@fim.uni-passau.de>
 * Georg Seibt <seibt@fim.uni-passau.de>
 */
package de.fosd.jdime.merge;

import de.fosd.jdime.artifact.Artifact;
import de.fosd.jdime.config.merge.MergeContext;
import de.fosd.jdime.config.merge.MergeScenario;
import de.fosd.jdime.config.merge.MergeType;
import de.fosd.jdime.config.merge.Revision;
import de.fosd.jdime.matcher.matching.Matching;
import de.fosd.jdime.operations.AddOperation;
import de.fosd.jdime.operations.ConflictOperation;
import de.fosd.jdime.operations.MergeOperation;
import de.fosd.jdime.strdump.DumpMode;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.fosd.jdime.artifact.Artifacts.root;
import static de.fosd.jdime.config.merge.MergeScenario.BASE;

public class DirectMerge<T extends Artifact<T>> implements MergeInterface<T> {

    private static final Logger LOG = Logger.getLogger(OrderedMerge.class.getCanonicalName());
    private String logprefix;

    private static final Level LOG_LEVEL = Level.FINE;

    /**
     * TODO: this needs high-level documentation. Probably also detailed documentation.
     *
     * @param operation the <code>MergeOperation</code> to perform
     * @param context   the <code>MergeContext</code>
     */
    @Override
    public void merge(MergeOperation<T> operation, MergeContext context) {
        MergeScenario<T> triple = operation.getMergeScenario();
        T left = triple.getLeft();
        T base = triple.getBase();
        T right = triple.getRight();
        T target = operation.getTarget();
        logprefix = operation.getId() + " - ";

        assert (left.matches(right));
        assert (left.hasMatching(right)) && right.hasMatching(left);

        assert (left.getNumChildren() == right.getNumChildren());

        LOG.finest(() -> {
            String name = getClass().getSimpleName();
            return String.format("%s%s.merge(%s, %s, %s)", prefix(), name, left.getId(), base.getId(), right.getId());
        });

        Revision l = left.getRevision();
        Revision b = base.getRevision();
        Revision r = right.getRevision();
        Iterator<T> leftIt = left.getChildren().iterator();
        Iterator<T> rightIt = right.getChildren().iterator();

        boolean done = false;

        T leftChild = null;
        T rightChild = null;

        if (leftIt.hasNext()) {
            leftChild = leftIt.next();
        } else {
            done = true;
        }
        if (rightIt.hasNext()) {
            rightChild = rightIt.next();
        }

        while (!done) {
            Matching<T> mBase = leftChild.getMatching(b);
            T baseChild = mBase == null ? leftChild.createEmptyArtifact(BASE)
                    : mBase.getMatchingArtifact(leftChild);

            if (l.contains(rightChild) && r.contains(leftChild)) {
                LOG.fine(String.format("1-1: (%s) %s and (%s) %s are matched.",
                        leftChild.getId(), leftChild, rightChild.getId(), rightChild));

                // left and right have the artifact. merge it.
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest(prefix(leftChild) + "is in both revisions [" + rightChild.getId() + "]");
                }

                // leftChild is a choice node
                if (leftChild.isChoice()) {
                    T matchedVariant = rightChild.getMatching(l).getMatchingArtifact(rightChild);
                    leftChild.addVariant(r.getName(), matchedVariant);
                    AddOperation<T> addOp = new AddOperation<>(leftChild, target, null);
                    leftChild.setMerged();
                    rightChild.setMerged();
                    addOp.apply(context);
                } else {
                    assert (leftChild.hasMatching(rightChild) && rightChild.hasMatching(leftChild));
                }

                if (!leftChild.isMerged() && !rightChild.isMerged()) {
                    // determine whether the child is 2 or 3-way merged

                    MergeType childType = mBase == null ? MergeType.TWOWAY
                            : MergeType.THREEWAY;
                    T targetChild = target == null ? null : leftChild.copy();
                    if (targetChild != null) {
                        target.addChild(targetChild);

                        assert targetChild.exists();
                        targetChild.clearChildren();
                    }

                    MergeScenario<T> childTriple = new MergeScenario<>(childType,
                            leftChild, baseChild, rightChild);

                    MergeOperation<T> mergeOp = new MergeOperation<>(childTriple, targetChild);
                    leftChild.setMerged();
                    rightChild.setMerged();
                    mergeOp.apply(context);
                }
            } else { // `leftChild` and `rightChild` may conflict
                LOG.log(LOG_LEVEL, String.format("1-1: (%s) %s and (%s) %s are NOT matched.",
                        leftChild.getId(), leftChild, rightChild.getId(), rightChild));

                if (b != null && !leftChild.hasChanges(b)) { // `leftChild` = base, `rightChild` = target
                    LOG.log(LOG_LEVEL, String.format("1-1: left: (%s) %s is NOT changed form base.",
                            leftChild.getId(), leftChild));

                    // add the right change
                    AddOperation<T> addOp = new AddOperation<>(rightChild, target, r.getName());
                    rightChild.setMerged();
                    addOp.apply(context);
                } else if (b != null && !rightChild.hasChanges(b)) { // `rightChild` = base, `leftChild` = target
                    LOG.log(LOG_LEVEL, String.format("1-1: right: (%s) %s is NOT changed form base.",
                            rightChild.getId(), rightChild));

                    // add the left change
                    AddOperation<T> addOp = new AddOperation<>(leftChild, target, r.getName());
                    rightChild.setMerged();
                    addOp.apply(context);
                } else {
                    // either insertion-deletion-conflict or deletion-insertion-conflict
                    ConflictOperation<T> conflictOp = new ConflictOperation<>(
                            leftChild, rightChild, target, l.getName(), r.getName());
                    conflictOp.apply(context);
                }
            }

            if (leftIt.hasNext()) {
                leftChild = leftIt.next();
            } else {
                done = true;
            }
            if (rightIt.hasNext()) {
                rightChild = rightIt.next();
            }

            if (LOG.isLoggable(Level.FINEST) && target != null) {
                LOG.finest(String.format("%s target.dumpTree() after processing child:", prefix()));
                System.out.println(root(target).dump(DumpMode.PLAINTEXT_TREE));
            }
        }
    }

    /**
     * Returns the logging prefix.
     *
     * @return logging prefix
     */
    private String prefix() {
        return logprefix;
    }

    /**
     * Returns the logging prefix.
     *
     * @param artifact artifact that is subject of the logging
     * @return logging prefix
     */
    private String prefix(T artifact) {
        return String.format("%s[%s]", logprefix, (artifact == null) ? "null" : artifact.getId());
    }
}