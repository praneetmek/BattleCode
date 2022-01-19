package firstiteration;

import battlecode.common.*;

public class ArchonStrategy {
    public static void runArchon(RobotController rc, RobotInfo[] nearbyEnemies) throws GameActionException {
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];

        int numOfSoldiers = Communication.getAlive(rc, RobotType.SOLDIER);
        int numOfMiners = Communication.getAlive(rc, RobotType.MINER);

        int visionRadiusSquared = rc.getType().visionRadiusSquared;
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(visionRadiusSquared, rc.getTeam());

        //first try to heal
        healNearbyRobots(rc, nearbyAllies);

        if(rc.readSharedArray(PersonalConstants.INDEX_OF_ARCHON)!=0 || nearbyEnemies.length!=0){
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
            }
        }
        else{
            if (numOfMiners<numOfSoldiers) {
                // Let's try to build a miner.
                rc.setIndicatorString("Trying to build a miner");
                if (rc.canBuildRobot(RobotType.MINER, dir)) {
                    rc.buildRobot(RobotType.MINER, dir);
                }
            } else {
                // Let's try to build a soldier.
                rc.setIndicatorString("Trying to build a soldier");
                if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                }
            }
        }
    }

    private static void healNearbyRobots(RobotController rc, RobotInfo[] nearbyAllies) throws GameActionException {
        for(RobotInfo ally : nearbyAllies){
            while (ally.getHealth()<ally.getType().health && rc.canRepair(ally.getLocation())){
                rc.repair(ally.getLocation());
            }
        }
    }
}
