package firstiteration;

import battlecode.common.*;

public class ArchonStrategy {

    private static boolean inMoveForm = false;
    public static void runArchon(RobotController rc, RobotInfo[] nearbyEnemies) throws GameActionException {
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];

        int numOfSoldiers = Communication.getAlive(rc, RobotType.SOLDIER);
        int numOfMiners = Communication.getAlive(rc, RobotType.MINER);

        int visionRadiusSquared = rc.getType().visionRadiusSquared;
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(visionRadiusSquared, rc.getTeam());

        boolean canCreate = true;



        if(nearbyEnemies.length != 0){
            Communication.signalDanger(rc);
        }
        else if(Communication.getDanger(rc) == MapLocationUtils.mapLocationToInt(rc, rc.getLocation())){
            Communication.unSignalDanger(rc);
        }

        int dangerLocation = Communication.getDanger(rc);
        if(dangerLocation > 0){
            if(dangerLocation == MapLocationUtils.mapLocationToInt(rc, rc.getLocation())){
                rc.setIndicatorString("Danger! Producing soldiers!");
                for(Direction aDir: RobotPlayer.directions) {
                    if (rc.canBuildRobot(RobotType.SOLDIER, aDir)) {
                        rc.buildRobot(RobotType.SOLDIER, aDir);
                    }
                }
            }
            else{
                rc.setIndicatorString("danger! production halted");
                canCreate = false;
            }
        }
        //first try to heal
        healNearbyRobots(rc, nearbyAllies);

        while(rc.senseRubble(rc.getLocation())>0){
            moveToBetterLocation(rc);
        }
        if(inMoveForm){
            if(rc.canTransform()){
                rc.transform();
                inMoveForm = false;
            }
        }
        if(rc.readSharedArray(PersonalConstants.INDEX_OF_ARCHON)!=0 || nearbyEnemies.length!=0){
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
            }
        }
        else if(canCreate){
            if (numOfMiners<numOfSoldiers && numOfMiners < rc.getMapWidth() * rc.getMapHeight() * 0.01) {
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

    private static void moveToBetterLocation(RobotController rc) throws GameActionException {
        if(rc.canTransform() && !inMoveForm){
            rc.transform();
            inMoveForm = true;
        }
        else if(inMoveForm) {
            MapLocation[] allSensibleLocations = rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), rc.getType().visionRadiusSquared);
            MapLocation locToMove = allSensibleLocations[0];
            for (MapLocation loc : allSensibleLocations) {
                if (rc.senseRubble(loc) < rc.senseRubble(locToMove) || (rc.senseRubble(loc) == rc.senseRubble(locToMove) && rc.getLocation().distanceSquaredTo(loc) < rc.getLocation().distanceSquaredTo(locToMove))) {
                    locToMove = loc;
                }
            }
            PathingUtils.moveTowards(rc, locToMove);
            rc.transform();
            inMoveForm = false;
        }
    }
}
