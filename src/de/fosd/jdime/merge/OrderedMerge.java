/**
 * Copyright (C) 2013-2014 Olaf Lessenich
 * Copyright (C) 2014-2017 University of Passau, Germany
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
import de.fosd.jdime.artifact.ArtifactList;
import de.fosd.jdime.config.merge.MergeContext;
import de.fosd.jdime.config.merge.MergeScenario;
import de.fosd.jdime.config.merge.MergeType;
import de.fosd.jdime.config.merge.Revision;
import de.fosd.jdime.operations.AddOperation;
import de.fosd.jdime.operations.ConflictOperation;
import de.fosd.jdime.operations.MergeOperation;
import javafx.util.Pair;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.fosd.jdime.config.merge.MergeScenario.BASE;

public class OrderedMerge<T extends Artifact<T>> implements MergeInterface<T> {
    private static final Logger LOG = Logger.getLogger(OrderedMerge.class.getCanonicalName());
    private static final Level LOG_LEVEL = Level.FINE;

    private String logprefix;

    public Pair<Pair<ArtifactList<T>, ArtifactList<T>>, T> split(T pivot, ArtifactList<T> list) {
        ArtifactList<T> up = new ArtifactList<>();
        ArtifactList<T> down = new ArtifactList<>();
        Iterator<T> iter = list.iterator();

        T elem;
        T best = null;
        int bestScore = 0;

        while (iter.hasNext()) {
            elem = iter.next();
            int score = elem.matchScoreWith(pivot);
            if (score > bestScore) {
                bestScore = score;
                best = elem;
            }
        }
        if (best != null) {
            LOG.log(LOG_LEVEL, "matched: " + best);
        }

        iter = list.iterator();
        boolean afterBest = false;
        while (iter.hasNext()) {
            elem = iter.next();
            if (elem == best) {
                afterBest = true;
            } else {
                (afterBest ? down : up).add(elem);
            }
        }

        return new Pair<>(new Pair<>(up, down), best);
    }

    public ArtifactList<T> merge2(ArtifactList<T> left, ArtifactList<T> right, Revision l, Revision r,
                                  T target, MergeContext context, boolean reversed) {
        if (LOG.isLoggable(LOG_LEVEL)) {
            LOG.log(LOG_LEVEL, "merge2:");
            System.out.println(String.format("left: %s\nright: %s", left, right));
        }

        if (left.isEmpty()) {
            LOG.log(LOG_LEVEL, "merge2: left is empty");
            return right;
        }

        T pivot = left.head();
        Pair<Pair<ArtifactList<T>, ArtifactList<T>>, T> p = split(pivot, right);
        left.dropHead();

        T matched = p.getValue();
        if (matched == null) {
            LOG.log(LOG_LEVEL, "merge2: right is NOT matched with " + pivot);

            AddOperation<T> addOp = new AddOperation<>(pivot, target, l.getName());
            pivot.setMerged();
            addOp.apply(context);

            LOG.log(LOG_LEVEL, "merge2: add: " + pivot);

            return merge2(left, right, l, r, target, context, reversed);
        }

        LOG.log(LOG_LEVEL, "merge2: right is matched with " + pivot);

        ArtifactList<T> right1 = p.getKey().getKey();
        for (T rightChild : right1) {
            AddOperation<T> addOp = new AddOperation<>(rightChild, target, r.getName());
            rightChild.setMerged();
            addOp.apply(context);

            LOG.log(LOG_LEVEL, "merge2: add: " + rightChild);
        }

        LOG.log(LOG_LEVEL, "normally 2-way merge: " + pivot + " <-> " + matched);
        // merge (pivot, matched)
        T leftT = !reversed ? pivot : matched;
        T rightT = !reversed ? matched : pivot;
        T empty = leftT.createEmptyArtifact(BASE);
        String leftName = !reversed ? l.getName() : r.getName();
        String rightName = !reversed ? r.getName() : l.getName();

        MergeScenario<T> childTriple = new MergeScenario<>(MergeType.TWOWAY, leftT, empty, rightT);
        T targetChild = leftT.copy();
        target.addChild(targetChild);
        if (targetChild != null) {
            assert targetChild.exists();
            targetChild.clearChildren();
        }

        MergeOperation<T> mergeOp = new MergeOperation<>(childTriple, targetChild);
        leftT.setMerged();
        rightT.setMerged();
        mergeOp.apply(context);
        // done

        ArtifactList<T> right2 = p.getKey().getValue();
        return merge2(left, right2, l, r, target, context, reversed);
    }

    public ArtifactList<T> merge3(ArtifactList<T> base, ArtifactList<T> left, ArtifactList<T> right,
                                  Revision l, Revision r, T target, MergeContext context, boolean reversed) {
        if (LOG.isLoggable(LOG_LEVEL)) {
            LOG.log(LOG_LEVEL, "merge3:");
            System.out.println(String.format("base: %s\nleft: %s\nright: %s", base, left, right));
        }

        if (base.isEmpty()) {
            return merge2(left, right, l, r, target, context, reversed);
        }

        T pivot = base.head();
        Pair<Pair<ArtifactList<T>, ArtifactList<T>>, T> pl = split(pivot, left);
        Pair<Pair<ArtifactList<T>, ArtifactList<T>>, T> pr = split(pivot, right);
        base.dropHead();

        T matchedLeft = pl.getValue();
        T matchedRight = pr.getValue();
        if (matchedLeft == null && matchedRight == null) {
            LOG.log(LOG_LEVEL, "merge3: left, right are both NOT matched with " + pivot);
            return merge3(base, left, right, l, r, target, context, reversed);
        }

        ArtifactList<T> left1 = pl.getKey().getKey();
        ArtifactList<T> left2 = pl.getKey().getValue();
        ArtifactList<T> right1 = pr.getKey().getKey();
        ArtifactList<T> right2 = pr.getKey().getValue();
        Revision b = pivot.getRevision();

        if (matchedLeft != null && matchedRight != null && (
                (matchedLeft.hasMatching(matchedRight) && matchedRight.hasMatching(matchedLeft))
                        || !matchedLeft.hasChanges(b) || !matchedRight.hasChanges(b)
        )) {
            // NOTE: even if left is matched with base, and right is matched with base,
            // it is possible that left and right are NOT matched with each other.
            // Therefore, we have to check if left and right are matched with each other,
            // or, either left or right is exactly the same with base.
            // Otherwise, (pivot, matchedLeft, matchedRight) can NOT become a proper merge triple,
            // and we have to ignore `matchedRight`.

            LOG.log(LOG_LEVEL, "merge3: left, right are both matched with " + pivot);

            ArtifactList<T> rightRest = merge2(left1, right1, l, r, target, context, reversed);

            // add rightRest
            for (T rightChild : rightRest) {
                AddOperation<T> addOp = new AddOperation<>(rightChild, target, r.getName());
                rightChild.setMerged();
                addOp.apply(context);

                LOG.log(LOG_LEVEL, "merge3: add: " + rightChild);
            }

            LOG.log(LOG_LEVEL, "normally 3-way merge: " + matchedLeft + " <-> " + matchedRight);
            // merge (pivot, matchedLeft, matchedRight)
            T leftT = !reversed ? matchedLeft : matchedRight;
            T rightT = !reversed ? matchedRight : matchedLeft;
            String leftName = !reversed ? l.getName() : r.getName();
            String rightName = !reversed ? r.getName() : l.getName();

            MergeScenario<T> childTriple = new MergeScenario<>(MergeType.THREEWAY, leftT, pivot, rightT);
            T targetChild = leftT.copy();
            target.addChild(targetChild);
            if (targetChild != null) {
                assert targetChild.exists();
                targetChild.clearChildren();
            }

            MergeOperation<T> mergeOp = new MergeOperation<>(childTriple, targetChild);
            leftT.setMerged();
            rightT.setMerged();
            mergeOp.apply(context);
            // done

            return merge3(base, left2, right2, l, r, target, context, reversed);
        }

        if (matchedLeft != null) {
            LOG.log(LOG_LEVEL, "merge3: only left is matched with " + pivot);
            // Either `matchedRight == null` or (`matchedRight != null` but
            // `matchedLeft.hasMatching(matchedRight) && matchedRight.hasMatching(matchedLeft)`).
            // No matter which one is hold, the whole `right` is remained to be matched.

            return merge3Helper(pivot, matchedLeft, base, left1, left2, right, l, r, target, context, reversed);
        }

        LOG.log(LOG_LEVEL, "merge3: only right is matched with " + pivot);
        assert left2.isEmpty();
        return merge3Helper(pivot, matchedRight, base, right1, right2, left1, r, l, target, context, !reversed);
    }

    public ArtifactList<T> merge3Helper(T pivot, T matchedLeft, ArtifactList<T> baseRest,
                                        ArtifactList<T> left1, ArtifactList<T> left2, ArtifactList<T> right,
                                        Revision l, Revision r, T target, MergeContext context, boolean reversed) {
        ArtifactList<T> rightRest = merge2(left1, right, l, r, target, context, reversed);

        if (matchedLeft.hasChanges(pivot.getRevision())) {
            LOG.log(LOG_LEVEL, "merge3: conflict: " + matchedLeft);

            // conflict (pivot, matchedLeft, ???)
            if (baseRest.isEmpty() && left2.isEmpty()) { // ??? = `rightRest`
                T leftT = target.copy();
                leftT.setRevision(l.concat("tmp"));
                leftT.clearChildren();
                leftT.addChild(matchedLeft);

                T rightT = target.copy();
                rightT.setRevision(r.concat("tmp"));
                rightT.clearChildren(); // also reset astnode's children to avoid recursive conflict children
                rightT.setChildren(rightRest);

                ConflictOperation<T> conflictOp = !reversed ?
                        new ConflictOperation<>(leftT, rightT, target, l.getName(), r.getName()) :
                        new ConflictOperation<>(rightT, leftT, target, r.getName(), l.getName());
                conflictOp.apply(context);

                return baseRest;
            }

            // ??? = empty
            T rightT = matchedLeft.createEmptyArtifact(r);

            ConflictOperation<T> conflictOp = !reversed ?
                    new ConflictOperation<>(matchedLeft, rightT, target, l.getName(), r.getName()) :
                    new ConflictOperation<>(rightT, matchedLeft, target, r.getName(), l.getName());
            conflictOp.apply(context);

            return merge3(baseRest, left2, rightRest, l, r, target, context, reversed);
        }

        LOG.log(LOG_LEVEL, "merge3 helper: no conflict: " + matchedLeft);
        return merge3(baseRest, left2, rightRest, l, r, target, context, reversed);
    }

    @Override
    public void merge(MergeOperation<T> operation, MergeContext context) {
        MergeScenario<T> triple = operation.getMergeScenario();
        T left = triple.getLeft();
        T base = triple.getBase();
        T right = triple.getRight();
        T target = operation.getTarget();

        target.clearChildren();

        this.logprefix = operation.getId() + " - ";

        assert (left.matches(right));
        assert (left.hasMatching(right)) && right.hasMatching(left);

        LOG.finest(() -> {
            String name = getClass().getSimpleName();
            return String.format("%s%s.merge(%s, %s, %s)", prefix(), name, left.getId(), base.getId(), right.getId());
        });

        Revision l = left.getRevision();
        Revision r = right.getRevision();

        ArtifactList<T> leftChildren = left.getChildrenAsArtifactList();
        ArtifactList<T> rightChildren = right.getChildrenAsArtifactList();
        ArtifactList<T> baseChildren = base.getChildrenAsArtifactList();

        /*
        if (leftChildren.size() == 1 && rightChildren.size() == 1 && baseChildren.size() <= 1) {
            T leftChild = leftChildren.head();
            T rightChild = rightChildren.head();

            // trivially 2-way merge if baseChildren.size() == 0
            MergeType childType = MergeType.TWOWAY;
            T baseChild = leftChild.createEmptyArtifact(BASE);

            if (baseChildren.size() == 1) { // trivially 3-way merge
                childType = MergeType.THREEWAY;
                baseChild = baseChildren.head();
            }

            T targetChild = leftChild.copy();
            target.addChild(targetChild);
            if (targetChild != null) {
                assert targetChild.exists();
                targetChild.clearChildren();
            }

            LOG.log(LOG_LEVEL, "trivially ordered merge: " + leftChild + " <-> " + rightChild);
            MergeScenario<T> childTriple = new MergeScenario<>(childType,
                    leftChild, baseChild, rightChild);
            MergeOperation<T> mergeOp = new MergeOperation<>(childTriple, targetChild);
            leftChild.setMerged();
            rightChild.setMerged();
            mergeOp.apply(context);
            return;
        }
        */

        ArtifactList<T> rightRest = merge3(baseChildren, leftChildren, rightChildren,
                l, r, target, context, false);
        LOG.log(LOG_LEVEL, "ordered merge: returned");

        // add rightRest
        for (T rightChild : rightRest) {
            AddOperation<T> addOp = new AddOperation<>(rightChild, target, r.getName());
            rightChild.setMerged();
            addOp.apply(context);

            LOG.log(LOG_LEVEL, "ordered merge: add: " + rightChild);
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