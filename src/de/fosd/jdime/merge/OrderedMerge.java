/**
 * AutoMerge
 * Copyright (C) 2013-2014 Olaf Lessenich
 * Copyright (C) 2014-2017 University of Passau, Germany
 * Copyright (C) 2018-2019 Fengmin Zhu
 * Copyright (C) 2019-2020 Tsinghua University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 * Olaf Lessenich <lessenic@fim.uni-passau.de>
 * Georg Seibt <seibt@fim.uni-passau.de>
 * Fengmin Zhu <zfm17@mails.tsinghua.edu.cn>
 */
package de.fosd.jdime.merge;

import de.fosd.jdime.artifact.Artifact;
import de.fosd.jdime.artifact.ArtifactList;
import de.fosd.jdime.config.merge.MergeContext;
import de.fosd.jdime.config.merge.MergeScenario;
import de.fosd.jdime.config.merge.MergeType;
import de.fosd.jdime.config.merge.Revision;
import de.fosd.jdime.matcher.matching.Matching;
import de.fosd.jdime.operations.AddOperation;
import de.fosd.jdime.operations.ConflictOperation;
import de.fosd.jdime.operations.MergeOperation;
import javafx.util.Pair;

import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.fosd.jdime.config.merge.MergeScenario.BASE;

public class OrderedMerge<T extends Artifact<T>> implements MergeInterface<T> {
    private static final Logger LOG = Logger.getLogger(OrderedMerge.class.getCanonicalName());
    private static final Level LOG_LEVEL = Level.FINE;

    private String logprefix;

    private Optional<T> bestMatch(T pivot, ArtifactList<T> list) {
        Iterator<T> iter = list.iterator();
        T best = null;
        float bestPercentage = 0;

        while (iter.hasNext()) {
            T elem = iter.next();
            float percentage = pivot.matchPercentageWith(elem);
            if (percentage > bestPercentage) {
                bestPercentage = percentage;
                best = elem;
            }

            if (bestPercentage == 1) { // already overall best percentage
                break;
            }
        }

        if (best == null) {
            return Optional.empty();
        }
        return Optional.of(best);
    }

    public Pair<Pair<ArtifactList<T>, ArtifactList<T>>, T> split(T pivot, ArtifactList<T> list, Revision r) {
        ArtifactList<T> up = new ArtifactList<>();
        ArtifactList<T> down = new ArtifactList<>();

        Matching<T> m = pivot.getMatching(r);
        T match = (m == null) ? null : m.getMatchingRevision(r);
        if (m != null) assert match != null;

        Iterator<T> iter = list.iterator();
        boolean after = false;
        while (iter.hasNext()) {
            T elem = iter.next();
            if (elem == match) {
                after = true;
                LOG.log(LOG_LEVEL, "matched: " + elem.getId());
            } else {
                (after ? down : up).add(elem);
            }
        }

        return new Pair<>(new Pair<>(up, down), after ? match : null);
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
        Pair<Pair<ArtifactList<T>, ArtifactList<T>>, T> p = split(pivot, right, r);
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
        Revision b = pivot.getRevision();
        base.dropHead();

        Pair<Pair<ArtifactList<T>, ArtifactList<T>>, T> pl = split(pivot, left, l);
        T matchedLeft = pl.getValue();

        if (matchedLeft == null) {
            Pair<Pair<ArtifactList<T>, ArtifactList<T>>, T> pr = split(pivot, right, r);
            T matchedRight = pr.getValue();

            if (matchedRight == null) {
                LOG.log(LOG_LEVEL, "merge3: left, right are both NOT matched with " + pivot);
                // pivot will be deleted
                return merge3(base, left, right, l, r, target, context, reversed);
            }

            // matchedRight != null
            LOG.log(LOG_LEVEL, "merge3: only right is matched with " + pivot);

            ArtifactList<T> left1 = pl.getKey().getKey();
            ArtifactList<T> left2 = pl.getKey().getValue();
            ArtifactList<T> right1 = pr.getKey().getKey();
            ArtifactList<T> right2 = pr.getKey().getValue();
            assert left2.isEmpty();
            return merge3Helper(pivot, matchedRight, base, right1, right2, left1, r, l, target, context, !reversed);
        }

        // matchedLeft != null
        Pair<Pair<ArtifactList<T>, ArtifactList<T>>, T> pr = split(matchedLeft, right, r);
        T matchedRight = pr.getValue();
        ArtifactList<T> left1 = pl.getKey().getKey();
        ArtifactList<T> left2 = pl.getKey().getValue();

        if (matchedRight == null) {
            LOG.log(LOG_LEVEL, "merge3: only left is matched with " + pivot);
            return merge3Helper(pivot, matchedLeft, base, left1, left2, right, l, r, target, context, reversed);
        }

        // matchedRight != null
        ArtifactList<T> right1 = pr.getKey().getKey();
        ArtifactList<T> right2 = pr.getKey().getValue();

        LOG.log(LOG_LEVEL, "merge3: left, right are both matched with " + pivot +
                ", and they are matched with each other");

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

    public ArtifactList<T> merge3Helper(T pivot, T matchedLeft, ArtifactList<T> baseRest,
                                        ArtifactList<T> left1, ArtifactList<T> left2, ArtifactList<T> right,
                                        Revision l, Revision r, T target, MergeContext context, boolean reversed) {
        ArtifactList<T> rightRest = merge2(left1, right, l, r, target, context, reversed);

        if (matchedLeft.hasChanges(pivot.getRevision())) {
            LOG.log(LOG_LEVEL, "merge3: conflict: " + matchedLeft);

            // conflict (pivot, matchedLeft, ???)
            if (baseRest.isEmpty() && left2.isEmpty()) { // ??? = `rightRest`
                if (rightRest.size() == 1) { // 1-1 conflict
                    T rightT = rightRest.head();
                    ConflictOperation<T> conflictOp = !reversed ?
                            new ConflictOperation<>(matchedLeft, rightT, target, l.getName(), r.getName(), pivot) :
                            new ConflictOperation<>(rightT, matchedLeft, target, r.getName(), l.getName(), pivot);
                    conflictOp.apply(context);

                    return baseRest;
                }

                // 1-N conflict
                T leftT = target.copy();
                leftT.setRevision(l.concat("tmp"));
                leftT.clearChildren();
                leftT.addChild(matchedLeft);

                T rightT = target.copy();
                rightT.setRevision(r.concat("tmp"));
                rightT.clearChildren(); // also reset astnode's children to avoid recursive conflict children
                rightT.setChildren(rightRest);

                ConflictOperation<T> conflictOp = !reversed ?
                        new ConflictOperation<>(leftT, rightT, target, l.getName(), r.getName(), pivot) :
                        new ConflictOperation<>(rightT, leftT, target, r.getName(), l.getName(), pivot);
                conflictOp.apply(context);

                return baseRest;
            }

            // ??? = empty
            T rightT = matchedLeft.createEmptyArtifact(r);

            ConflictOperation<T> conflictOp = !reversed ?
                    new ConflictOperation<>(matchedLeft, rightT, target, l.getName(), r.getName(), pivot) :
                    new ConflictOperation<>(rightT, matchedLeft, target, r.getName(), l.getName(), pivot);
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
        assert (left.hasMatching(right) && right.hasMatching(left))
                || (base.hasMatching(left) && base.hasMatching(right));

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