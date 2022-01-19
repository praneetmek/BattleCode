package firstiteration;

import battlecode.common.*;

import java.awt.*;

public class SoldierStrategy {
    private static boolean damaged = false;

    public static void runSoldier(RobotController rc, RobotInfo[] nearbyEnemies) throws GameActionException {


        boolean hasActed = false;
        int archonLocation = rc.readSharedArray(PersonalConstants.INDEX_OF_ARCHON);

        if(rc.getHealth() == rc.getType().health){
            damaged = false;                        //checks whether soldier needs to be healed
        }
        if(rc.getHealth()<15){
            damaged = true;
        }

        if(damaged){
            retreatToArchon(rc);
            hasActed = true;
        }
        else{
            for (RobotInfo enemy:nearbyEnemies){
                if(rc.canAttack(enemy.getLocation())){
                    rc.setIndicatorString("can attack enemy");    //attack whatever is nearby
                    rc.attack(enemy.getLocation());
                    hasActed = true;
                }
            }
            if(archonLocation != 0){
                attackArchon(rc, archonLocation, nearbyEnemies); //go to archon
                hasActed = true;
            }

        }
        if(!hasActed){
            PathingUtils.smartExplore(rc, RobotPlayer.startLocation);
        }
    }

    private static void retreatToArchon(RobotController rc) throws GameActionException {
        PathingUtils.moveTowards(rc, RobotPlayer.birthLocation);
        rc.setIndicatorString("moving to" + RobotPlayer.birthLocation.toString());
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
                if(!PathingUtils.moveTowards(rc, archonMapLocation)){
                    for (RobotInfo enemy:nearbyEnemies){
                        if(rc.canAttack(enemy.getLocation())){
                            rc.setIndicatorString("can attack enemy");
                            rc.attack(enemy.getLocation());
                        }
                    }
                }
            }
        }
    }
}
