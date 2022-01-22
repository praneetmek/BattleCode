package firstiteration;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

import java.awt.*;

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

    static void signalDanger(RobotController rc) throws GameActionException {
        rc.writeSharedArray(PersonalConstants.INDEX_OF_DANGER,MapLocationUtils.mapLocationToInt(rc, rc.getLocation()));
    }
    static void unSignalDanger(RobotController rc) throws GameActionException {
        rc.writeSharedArray(PersonalConstants.INDEX_OF_DANGER,0);
    }
    static MapLocation getDangerLocation(RobotController rc) throws GameActionException {
        return MapLocationUtils.intToMapLocation(rc, rc.readSharedArray(PersonalConstants.INDEX_OF_DANGER));
    }
    static boolean isDanger(RobotController rc) throws GameActionException {
        return rc.readSharedArray(PersonalConstants.INDEX_OF_DANGER)>0;
    }

    static boolean isFrontLine(RobotController rc) throws GameActionException {
        return rc.readSharedArray(PersonalConstants.INDEX_OF_FRONT_LINE) > 0;
    }
    static MapLocation getFrontLine(RobotController rc) throws GameActionException {
        return MapLocationUtils.intToMapLocation(rc, rc.readSharedArray(PersonalConstants.INDEX_OF_FRONT_LINE));
    }

    static void setFrontLine(RobotController rc, MapLocation locOfFrontLine) throws GameActionException {
        rc.writeSharedArray(PersonalConstants.INDEX_OF_FRONT_LINE, MapLocationUtils.mapLocationToInt(rc, locOfFrontLine));
    }

    static MapLocation getNearestArchonLocation(RobotController rc, MapLocation loc) throws GameActionException {
        MapLocation bestLocation = MapLocationUtils.intToMapLocation(rc, rc.readSharedArray(PersonalConstants.INDEX_OF_ARCHON_LOCS));
        int distance = bestLocation.distanceSquaredTo(loc);
        for(int i = 0; i < rc.getArchonCount(); i++){
            MapLocation archonLocation =  MapLocationUtils.intToMapLocation(rc, rc.readSharedArray(i + PersonalConstants.INDEX_OF_ARCHON_LOCS));
            if(archonLocation.distanceSquaredTo(loc) < distance){
                bestLocation = archonLocation;
                distance = archonLocation.distanceSquaredTo(loc);
            }
        }
        return bestLocation;
    }
}
