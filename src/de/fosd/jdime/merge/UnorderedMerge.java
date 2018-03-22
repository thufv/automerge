package de.fosd.jdime.merge;

import de.fosd.jdime.Main;
import de.fosd.jdime.artifact.Artifact;
import de.fosd.jdime.config.merge.MergeContext;
import de.fosd.jdime.config.merge.MergeScenario;
import de.fosd.jdime.config.merge.MergeType;
import de.fosd.jdime.config.merge.Revision;
import de.fosd.jdime.matcher.matching.Matching;
import de.fosd.jdime.operations.AddOperation;
import de.fosd.jdime.operations.ConflictOperation;
import de.fosd.jdime.operations.DeleteOperation;
import de.fosd.jdime.operations.MergeOperation;

import java.util.Iterator;
import java.util.logging.Logger;

import static de.fosd.jdime.artifact.Artifacts.copyTree;
import static de.fosd.jdime.config.CommandLineConfigSource.CLI_LIKELIHOOD;
import static de.fosd.jdime.config.merge.MergeScenario.BASE;

/**
 * @param <T> type of artifact
 * @author paul
 */
public class UnorderedMerge<T extends Artifact<T>> implements MergeInterface<T> {

    private static final Logger LOG = Logger.getLogger(UnorderedMerge.class.getCanonicalName());
    private double likelihood;

    public UnorderedMerge() {
        likelihood = Main.config.getDouble(CLI_LIKELIHOOD).orElse(0.2);
    }

    /**
     * Unordered merge for each element `pivot`.
     * Cases:
     * 1) If `pivot` is already merged, then skip.
     * 2) If `pivot` is only in self revision, say no proper match found in the opposing revision,
     * then add the change to target.
     * 3) If `pivot` is in both self revision and base revision,
     * 3-1) when `pivot` has changed from base revision, then this is a insertion-deletion-conflict.
     * 3-2) when `pivot` is consistent with base revision, then delete change from target.
     * 4) If a proper match is found, we apply a normal 3-way or 2-way merging according to the presence of base.
     *
     * @param pivot   current element.
     * @param target  target artifact.
     * @param context merge context.
     * @param self    revision of `pivot`, either left/right.
     * @param other   opposing revision. If `self` is left, then it is `right`, and vice versa.
     * @param base    base revision.
     * @param isLeft  whether `self` is left.
     */
    private void mergeOn(T pivot, T target, MergeContext context,
                         Revision self, Revision other, Revision base, boolean isLeft) {
        if (pivot.isMerged()) { // 1) already merged
            return;
        }

        Matching<T> match = pivot.getMatching(other);
        T matched = (match == null) ? null : match.getMatchingRevision(other);
        if (matched != null && // 4) pivot is matched with some element in the opposing revision
                pivot.matches(matched) && matched.matches(pivot) &&
                match.getPercentage() > likelihood) { // proper match
            Matching<T> mBase = pivot.getMatching(base);

            // determine whether the child is 2 or 3-way merged
            MergeType childType = mBase == null ? MergeType.TWOWAY
                    : MergeType.THREEWAY;
            T baseChild = mBase == null ? pivot.createEmptyArtifact(BASE)
                    : mBase.getMatchingRevision(base);

            T targetChild = pivot.copy();
            target.addChild(targetChild);

            MergeScenario<T> childTriple = new MergeScenario<>(childType,
                    pivot, baseChild, matched);
            MergeOperation<T> mergeOp = new MergeOperation<>(childTriple, targetChild);

            pivot.setMerged();
            matched.setMerged();
            mergeOp.apply(context);

            return;
        }

        // no proper match found
        if (base.contains(pivot) && pivot.hasMatching(base) &&
                pivot.getMatching(base).getPercentage() > likelihood) { // 3) `pivot` in BL
            LOG.fine(() -> String.format("Unordered: %s was deleted by %s", show(pivot), other.getName()));

            if (pivot.hasChanges(base)) {
                // 3-1) insertion-deletion-conflict
                LOG.fine("Unordered: " + show(pivot) + " has changed base.");

                T baseChild = pivot.getMatching(base).getMatchingRevision(base);
                ConflictOperation<T> conflictOp = new ConflictOperation<>(
                        pivot, null, target, self.getName(), other.getName(), baseChild, isLeft);
                conflictOp.apply(context);
            } else {
                // 3-2) can be safely deleted
                DeleteOperation<T> delOp = new DeleteOperation<>(pivot, target, self.getName());
                delOp.apply(context);
            }
        } else { // 2) `pivot` only in L
            // add the change
            LOG.fine(() -> String.format("Unordered: %s is a change", show(pivot)));

            AddOperation<T> addOp = new AddOperation<>(copyTree(pivot), target, self.getName());
            pivot.setMerged();
            addOp.apply(context);
        }
    }

    /**
     * Unordered merge.
     * <p>
     * Merge list L with R, representing left and right versions respectively.
     * The top level procedure:
     * 1) merge on each element of L;
     * 2) merge on each element of R;
     *
     * @param operation the <code>MergeOperation</code> to perform
     * @param context   the <code>MergeContext</code>
     */
    @Override
    public void merge(MergeOperation<T> operation, MergeContext context) {
        assert (operation != null);
        assert (context != null);

        MergeScenario<T> triple = operation.getMergeScenario();
        T left = triple.getLeft();
        T base = triple.getBase();
        T right = triple.getRight();
        T target = operation.getTarget();

        assert (left.matches(right));
        assert (left.hasMatching(right)) && right.hasMatching(left);

        Revision l = left.getRevision();
        Revision b = base.getRevision();
        Revision r = right.getRevision();
        Iterator<T> leftIt = left.getChildren().iterator();
        Iterator<T> rightIt = right.getChildren().iterator();

        while (leftIt.hasNext()) {
            T pivot = leftIt.next();
            mergeOn(pivot, target, context, l, r, b, true);
        }
        while (rightIt.hasNext()) {
            T pivot = rightIt.next();
            mergeOn(pivot, target, context, r, l, b, false);
        }
    }

    /**
     * Returns the logging show.
     *
     * @param artifact artifact that is subject of the logging
     * @return logging show
     */
    private String show(T artifact) {
        return String.format("(%s) %s", (artifact == null) ? "null" : artifact.getId(), artifact);
    }
}
