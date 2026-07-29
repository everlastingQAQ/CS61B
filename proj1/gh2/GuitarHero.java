package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {

    static final double A = 440.0;
    static final double B = 2;
    static final int C = 24;
    static final double D = 12.0;

    public static void main(String[] args) {
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        GuitarString[] strings = new GuitarString[keyboard.length()];

        for (int i = 0; i < strings.length; i++) {
            double frequency = A * Math.pow(B, (i - C) / D);
            strings[i] = new GuitarString(frequency);
        }

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);
                if (index >= 0) {
                    strings[index].pluck();
                }
            }

            double sample = 0.0;

            for (GuitarString string : strings) {
                sample += string.sample();
            }

            StdAudio.play(sample);

            for (GuitarString string : strings) {
                string.tic();
            }
        }
    }
}
