package cenario;

import javax.media.j3d.*;
import java.util.Enumeration;

public class MorphingBehavior extends Behavior {
    private final Morph morph;
    private final Alpha alpha;

    public MorphingBehavior(Morph morph, Alpha alpha) {
        this.morph = morph;
        this.alpha = alpha;
    }

    @Override
    public void initialize() {
        wakeupOn(new WakeupOnElapsedFrames(10));
    }

    @Override
    public void processStimulus(Enumeration criteria) {
        double alphaValue = alpha.value();
        double[] weights = calculateWeights(alphaValue);

        if (weights != null) {
            morph.setWeights(weights);
        }

        wakeupOn(new WakeupOnElapsedFrames(10));
    }

    private double[] calculateWeights(double alphaValue) {
        int numWeights = morph.getWeights().length;
        double[] weights = new double[numWeights];

        if (numWeights < 2) {
            System.err.println("Morph must have at least 2 weights.");
            return null;
        }

        // Ensure alphaValue is clamped between 0.0 and 1.0
        alphaValue = Math.max(0.0, Math.min(1.0, alphaValue));

        int lowerIndex = (int) (alphaValue * (numWeights - 1));
        int upperIndex = lowerIndex + 1;
        double weightFraction = (alphaValue * (numWeights - 1)) - lowerIndex;

        for (int i = 0; i < numWeights; i++) {
            if (i == lowerIndex) {
                weights[i] = 1.0 - weightFraction;
            } else if (i == upperIndex && i < numWeights) {
                weights[i] = weightFraction;
            } else {
                weights[i] = 0.0;
            }
        }

        return weights;
    }
}