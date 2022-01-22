package firstiteration;

import battlecode.common.*;
import battlecode.common.RobotController;

public class BuilderStrategy {
    private static MapLocation parentLocation = null;
    private static String debugString = "";

    public static void runBuilder(RobotController rc) throws GameActionException {
        debugString = "";

        if(parentLocation == null){
            parentLocation = Communication.getNearestArchonLocation(rc, rc.getLocation());
        }
        int visionRadiusSquared = rc.getType().visionRadiusSquared;

        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(visionRadiusSquared, rc.getTeam());
        int numberOfNearbyBuilders = 0;
        for(RobotInfo robotInfo: nearbyAllies){
            if(robotInfo.getType() == RobotType.BUILDER){
                numberOfNearbyBuilders++;
            }
        }
        debugString += "number of nearby: "+ numberOfNearbyBuilders;
        if(numberOfNearbyBuilders > 3){
            debugString+="Needs to sacrifice";
            sacrifice(rc);
        }
        else{
            if(rc.canRepair(parentLocation) && rc.senseRobotAtLocation(parentLocation).getHealth() < RobotType.ARCHON.health){
                rc.repair(parentLocation);
            }
            PathingUtils.moveWithinCircle(rc, parentLocation, 6);
        }

        rc.setIndicatorString(debugString);
    }

    private static void sacrifice(RobotController rc) throws GameActionException {
        int visionRadiusSquared = rc.getType().visionRadiusSquared;

        MapLocation[] allPotentialSacrificeSpots = rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), visionRadiusSquared);
        MapLocation bestSpot = null;
        int closestDistance = 5000;
        for(MapLocation location: allPotentialSacrificeSpots){
            if(location.distanceSquaredTo(rc.getLocation()) < closestDistance && rc.senseLead(location) == 0){
                bestSpot = location;
                closestDistance = location.distanceSquaredTo(rc.getLocation());
            }
        }
        if(bestSpot.equals(rc.getLocation())){
            rc.disintegrate();
        }
        else{
            PathingUtils.moveDirectlyTowards(rc, bestSpot);
        }
    }

}
