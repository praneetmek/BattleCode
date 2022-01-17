package firstiteration;

import battlecode.common.*;

public class PathingUtils {
    /**
     * Gets the closest location from a list of MapLocations
     * @param currentLocation
     * @param otherLocations
     * @return
     */
    public static MapLocation getClosestLocation(MapLocation currentLocation, MapLocation[] otherLocations){
        int closestLocationDistance = currentLocation.distanceSquaredTo(otherLocations[0]);
        MapLocation bestLocation = otherLocations[0];
        for (MapLocation otherLocation: otherLocations){
            if (currentLocation.distanceSquaredTo(otherLocation) < closestLocationDistance){
                closestLocationDistance = currentLocation.distanceSquaredTo(otherLocation);
                bestLocation = otherLocation;
            }
        }
        return bestLocation;
    }

    public static boolean moveTowards(RobotController rc, MapLocation loc) throws GameActionException {
        MapLocation currLocation = rc.getLocation();
        Direction dirToLoc = currLocation.directionTo(loc);
        if(rc.canMove(dirToLoc)){
            rc.move(dirToLoc);
            return true;
        }
        else if(rc.canSenseRobotAtLocation(currLocation.add(dirToLoc)) && rc.senseRobotAtLocation(currLocation.add(dirToLoc)).getType() == RobotType.SOLDIER){
            Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
            if(rc.canMove(dir)){
                rc.move(dir);
                return true;
            }
        }
        return false;
    }

    public static void smartExplore(RobotController rc, MapLocation startLocation) throws GameActionException{
        boolean hasMoved = false;
        MapLocation currLoc = rc.getLocation();
        if(currLoc.x == 0 || currLoc.x == rc.getMapWidth()-1 || currLoc.y == 0 || currLoc.y == rc.getMapHeight()-1){
            RobotPlayer.startLocation = currLoc;
        }
        while(!hasMoved){
            Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
            MapLocation nextLocation = currLoc.add(dir);
            if(startLocation.distanceSquaredTo(currLoc)<= startLocation.distanceSquaredTo(nextLocation) && rc.canMove(dir)){
                rc.move(dir);
                hasMoved = true;
            }
        }
    }
}
