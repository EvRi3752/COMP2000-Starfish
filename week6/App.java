package week6;

public class App {

    public static class BarcodePuzzle {
        static int iteration = 4;

        public static void main(String[] args) {
            try {
                star();
            } catch (RuntimeException e) {
                // :)
            }
            System.out.println();
        }

        static void star() {
            System.out.print("* ");
            try {
                pipe();
            } finally {
                System.out.print("* ");
            }
        }

        static void pipe() {
            System.out.print("| ");
            try {
                caret();
            } finally {
                System.out.print("| ");
            }
        }

        static void caret() {
            System.out.print("^ ");
            chuck_a_fit();
        }
    }

    static void chuck_a_fit() {
        throw new RuntimeException("stop");
    }
}

