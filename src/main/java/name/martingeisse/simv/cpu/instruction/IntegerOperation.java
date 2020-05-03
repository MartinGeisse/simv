package name.martingeisse.simv.cpu.instruction;

public enum IntegerOperation {

    ADD {
        @Override
        public int compute(int x, int y) {
            return x + y;
        }
    },

    SUBTRACT {
        @Override
        public int compute(int x, int y) {
            return x - y;
        }
    },

    MULTIPLY {
        @Override
        public int compute(int x, int y) {
            return x * y;
        }
    },

    DIVIDE {
        @Override
        public int compute(int x, int y) {
            return y == 0 ? -1 : (x / y);
        }
    },

    AND {
        @Override
        public int compute(int x, int y) {
            return x & y;
        }
    },

    OR {
        @Override
        public int compute(int x, int y) {
            return x | y;
        }
    },

    XOR {
        @Override
        public int compute(int x, int y) {
            return x ^ y;
        }
    },

    XNOR {
        @Override
        public int compute(int x, int y) {
            return ~(x ^ y);
        }
    },

    SHIFT_LEFT {
        @Override
        public int compute(int x, int y) {
            return x << y;
        }
    },

    SHIFT_RIGHT_LOGICAL {
        @Override
        public int compute(int x, int y) {
            return x >>> y;
        }
    },

    SHIFT_RIGHT_ARITHMETIC {
        @Override
        public int compute(int x, int y) {
            return x >> y;
        }
    },

    SET_TO_LESS_THAN  {
        @Override
        public int compute(int x, int y) {
            return (x < y ? 1 : 0);
        }
    },

    SET_TO_LESS_THAN_UNSIGNED  {
        @Override
        public int compute(int x, int y) {
            // Explanation why this is correct: Adding MIN_VALUE flips the highest bit. If the highest bits of x
            // and y are equal before flipping, then they are equal afterwards, and the comparison is not changed,
            // so it is a comparison in the lower 31 bits only, which is the same for signed / unsigned.
            // If the highest bits of x and y differ, one of them is greater based on that bit alone, and flipping
            // followed by signed comparison can easily be shown to result in unsigned comparison.
            return (x + Integer.MIN_VALUE < y + Integer.MIN_VALUE ? 1 : 0);
        }
    };

    public abstract int compute(int x, int y);

}
