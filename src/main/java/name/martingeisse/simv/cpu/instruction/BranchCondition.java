package name.martingeisse.simv.cpu.instruction;

public enum BranchCondition {

    EQUAL {
        @Override
        public boolean check(int x, int y) {
            return x == y;
        }
    },

    NOT_EQUAL {
        @Override
        public boolean check(int x, int y) {
            return x != y;
        }
    },

    LESS_THAN {
        @Override
        public boolean check(int x, int y) {
            return x < y;
        }
    },

    GREATER_EQUAL {
        @Override
        public boolean check(int x, int y) {
            return x >= y;
        }
    },

    LESS_THAN_UNSIGNED {
        @Override
        public boolean check(int x, int y) {
            return (x + Integer.MIN_VALUE < y + Integer.MIN_VALUE);
        }
    },

    GREATER_EQUAL_UNSIGNED {
        @Override
        public boolean check(int x, int y) {
            return (x + Integer.MIN_VALUE >= y + Integer.MIN_VALUE);
        }
    };

    public abstract boolean check(int x, int y);

}
