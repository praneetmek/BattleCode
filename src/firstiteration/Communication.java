package firstiteration;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

class Communication {
    private static final int MIN_ENEMY_IDX = 21;
    private static final int NUM_TYPES = 7;

    private static int typeToIndex(RobotType type) {
        switch (type) {
            case ARCHON:     return 0;
            case MINER:      return 1;
            case SOLDIER:    return 2;
            case LABORATORY: return 3;
            case WATCHTOWER: return 4;
            case BUILDER:    return 5;
            case SAGE:       return 6;
            default: throw new RuntimeException("Unknown type: " + type);
        }
    }
    static void reportAlive(RobotController rc) {
        final int typeIdx = typeToIndex(rc.getType());

        try {
            // Zero out in-progress counts if necessary
            if (rc.readSharedArray(0) != rc.getRoundNum()) {
                final int thisRound = rc.getRoundNum() % 2;
                for (int i = 0; i < NUM_TYPES; i++) {
                    if (rc.readSharedArray(thisRound * NUM_TYPES + i + 1) != 0) {
                        rc.writeSharedArray(thisRound * NUM_TYPES + i + 1, 0);
                    }
                }
                rc.writeSharedArray(0, rc.getRoundNum());
            }

            // Increment alive counter
            final int arrayIdx = (rc.getRoundNum() % 2) * NUM_TYPES + typeIdx + 1;
            rc.writeSharedArray(arrayIdx, rc.readSharedArray(arrayIdx) + 1);
        } catch (GameActionException e) {
            e.printStackTrace();
        }
    }

    static int getAlive(RobotController rc, RobotType type) {
        final int typeIdx = typeToIndex(type);

        // Read from previous write cycle
        final int arrayIdx = ((rc.getRoundNum() + 1) % 2) * NUM_TYPES + typeIdx + 1;
        try {
            return rc.readSharedArray(arrayIdx);
        } catch (GameActionException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
