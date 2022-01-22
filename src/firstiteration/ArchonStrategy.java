package firstiteration;

import battlecode.common.*;

public class ArchonStrategy {

    private static boolean inMoveForm = false;
    private static int indexOfLocation = -1;
    private static String s = "";
    public static void runArchon(RobotController rc, RobotInfo[] nearbyEnemies) throws GameActionException {
        s= "";
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];

        int numOfSoldiers = Communication.getAlive(rc, RobotType.SOLDIER);
        int numOfMiners = Communication.getAlive(rc, RobotType.MINER);
        int numOfBuilders = Communication.getAlive(rc, RobotType.BUILDER);

        int visionRadiusSquared = rc.getType().visionRadiusSquared;
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(visionRadiusSquared, rc.getTeam());

        boolean canCreate = true;
        s+="ID number" + rc.getRoundNum()%rc.getArchonCount() + "can build";
        if(rc.getRoundNum()%rc.getArchonCount() != indexOfLocation){
            canCreate = false;
        }

        updateArchonLocation(rc);
        updateDangerStatus(rc, nearbyEnemies);

        if(Communication.isDanger(rc)){
            MapLocation dangerLocation = Communication.getDangerLocation(rc);
            s+="Danger at Archon at" + dangerLocation.toString();
            if(dangerLocation.equals(rc.getLocation())){
                s+= "Producing soldiers to protect";
                MapLocation enemyLocation = nearbyEnemies[0].getLocation();
                Direction directDirectionToEnemy = rc.getLocation().directionTo(enemyLocation);
                for(Direction aDir: MapLocationUtils.getBestDirections(directDirectionToEnemy)) {
                    if (rc.canBuildRobot(RobotType.SOLDIER, aDir)) {
                        rc.buildRobot(RobotType.SOLDIER, aDir);
                    }
                }
            }
            else{
                s+= "Production halted";
            }
            canCreate = false;

        }
        //first try to heal

        while(rc.senseRubble(rc.getLocation())>0){
            moveToBetterLocation(rc);
        }
        if(inMoveForm){
            if(rc.canTransform()){
                rc.transform();
                inMoveForm = false;
            }
        }
        if((rc.readSharedArray(PersonalConstants.INDEX_OF_ARCHON)!=0 && numOfMiners > 10) || nearbyEnemies.length!=0){
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
            }
        }
        healNearbyRobots(rc, nearbyAllies);
        if(canCreate){
            if(rc.getRoundNum()<20){
                if (rc.canBuildRobot(RobotType.MINER, dir)) {
                    rc.buildRobot(RobotType.MINER, dir);
                }
            }
            else if(rc.getRoundNum()< 200){
                for(Direction aDir: RobotPlayer.directions){
                    if(rc.canBuildRobot(RobotType.BUILDER, aDir)){
                        rc.buildRobot(RobotType.BUILDER, aDir);
                    }
                }

            }
            else{
                if (numOfMiners<2* numOfSoldiers && numOfMiners < rc.getMapWidth() * rc.getMapHeight() * 0.03) {
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
        rc.setIndicatorString(s);
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

    private static void updateDangerStatus(RobotController rc, RobotInfo[] nearbyEnemies) throws GameActionException {
        if(nearbyEnemies.length != 0){
            Communication.signalDanger(rc);
        }
        else if(Communication.getDangerLocation(rc).equals(rc.getLocation())){
            Communication.unSignalDanger(rc);
        }
    }

    private static void updateArchonLocation(RobotController rc) throws GameActionException {
        if(indexOfLocation != -1){
            rc.writeSharedArray(indexOfLocation+PersonalConstants.INDEX_OF_ARCHON_LOCS, MapLocationUtils.mapLocationToInt(rc,rc.getLocation()));
        }
        else{
            for(int i=0; i<GameConstants.MAX_STARTING_ARCHONS; i++){
                int currentItem = rc.readSharedArray(i+PersonalConstants.INDEX_OF_ARCHON_LOCS);
                if(rc.readSharedArray(i+PersonalConstants.INDEX_OF_ARCHON_LOCS) == 0){
                    rc.writeSharedArray(i+PersonalConstants.INDEX_OF_ARCHON_LOCS, MapLocationUtils.mapLocationToInt(rc,rc.getLocation()));
                    indexOfLocation = i;
                    break;
                }
            }
        }
    }
}
