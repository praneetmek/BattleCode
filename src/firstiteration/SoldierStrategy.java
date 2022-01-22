package firstiteration;

import battlecode.common.*;

import java.awt.*;

public class SoldierStrategy {
    private static boolean damaged = false;
    private static boolean hasActed;
    private static String debugString;
    public static void runSoldier(RobotController rc, RobotInfo[] nearbyEnemies) throws GameActionException {
        debugString = "";

        hasActed = false;
        int archonLocation = rc.readSharedArray(PersonalConstants.INDEX_OF_ARCHON);

        if(rc.getHealth() == rc.getType().health){
            damaged = false;                        //checks whether soldier needs to be healed
        }
        if(rc.getHealth()<25){
            damaged = true;
        }

        if(Communication.isDanger(rc) && Communication.getDangerLocation(rc).distanceSquaredTo(rc.getLocation())<10){
                rc.setIndicatorString("defending archon");
                attackNearbyEnemies(rc, nearbyEnemies, true);
        }
        else{
            if(damaged){
                retreatToArchon(rc);
                hasActed = true;
            }
            else{
                attackNearbyEnemies(rc, nearbyEnemies, false);

                if(archonLocation != 0 && Communication.getAlive(rc, RobotType.SOLDIER)> 10){
                    rc.setIndicatorString("Attacking Archon at" + archonLocation );
                    attackArchon(rc, archonLocation, nearbyEnemies); //go to archon
                    hasActed = true;
                }
                else if(Communication.isFrontLine(rc)){
                    MapLocation frontLineLocation = Communication.getFrontLine(rc);
                    rc.setIndicatorString("THERE IS A FRONT LINE AT " + frontLineLocation.toString() );
                    if(rc.canSenseLocation(frontLineLocation) && rc.canSenseRobotAtLocation(frontLineLocation)){ //if there is a robot at the front line, and we can attack it, then do so
                        //rc.setIndicatorString("Attacking enemy at front line" + frontLineLocation.toString());
                        if(rc.canAttack(frontLineLocation)){
                            rc.attack(frontLineLocation);
                        }
                        PathingUtils.moveDirectlyTowards(rc, frontLineLocation);
                        hasActed = true;


                    }
                    else if(rc.canSenseLocation(frontLineLocation) && !rc.canSenseRobotAtLocation(frontLineLocation)){ //otherwise if the front line is empty, check if there are nearby soldiers
                        for (RobotInfo robotInfo : nearbyEnemies) {
                            if (robotInfo.getType() == RobotType.SOLDIER) {
                                MapLocation sensedSoldierLocation = robotInfo.getLocation();
                                Communication.setFrontLine(rc, sensedSoldierLocation);
                            }
                        }
                    }
                    else if(rc.getLocation().distanceSquaredTo(frontLineLocation) < 25){   //if we cant see that location yet
                        PathingUtils.moveDirectlyTowards(rc, frontLineLocation);
                        hasActed = true;

                    }
                }
            }
            if(!hasActed){
                rc.setIndicatorString("Exploring");
                PathingUtils.smartExplore(rc, RobotPlayer.startLocation);
            }
        }

        rc.setIndicatorString(debugString);
    }

    private static void retreatToArchon(RobotController rc) throws GameActionException {
        PathingUtils.moveDirectlyTowards(rc, Communication.getNearestArchonLocation(rc, rc.getLocation()));
        debugString += "Retreating to "+ Communication.getNearestArchonLocation(rc, rc.getLocation()).toString();
    }

    private static void attackArchon(RobotController rc, int archonLocation, RobotInfo[] nearbyEnemies) throws GameActionException {
        MapLocation archonMapLocation = MapLocationUtils.intToMapLocation(rc, archonLocation);
        if(rc.canSenseLocation(archonMapLocation) && !rc.canSenseRobotAtLocation(archonMapLocation)){
            rc.writeSharedArray(PersonalConstants.INDEX_OF_ARCHON,0);
        }
        else{
            if(rc.canAttack(archonMapLocation)){
                rc.attack(archonMapLocation);
            }
            else{
                if(!PathingUtils.canMoveTowards(rc, archonMapLocation)){
                    for (RobotInfo enemy:nearbyEnemies){
                        if(rc.canAttack(enemy.getLocation())){
                            rc.setIndicatorString("can attack enemy");
                            rc.attack(enemy.getLocation());
                        }
                    }
                }
                else{
                    PathingUtils.moveTowards(rc, archonMapLocation);
                }
            }
        }
    }

    private static void attackNearbyEnemies(RobotController rc, RobotInfo[] nearbyEnemies, boolean chase) throws GameActionException {
        for (RobotInfo enemy:nearbyEnemies){
            if(rc.canAttack(enemy.getLocation())){
                rc.setIndicatorString("can attack enemy");    //attack whatever is nearby
                rc.attack(enemy.getLocation());
                if(chase){
                    PathingUtils.moveDirectlyTowards(rc, enemy.getLocation());
                }
                hasActed = true;
            }
        }
    }
}
