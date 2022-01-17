package firstiteration;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class MapLocationUtils {
    public static int mapLocationToInt(RobotController rc, MapLocation mapLocation){
        return mapLocation.y * rc.getMapWidth() + mapLocation.x + 1;
    }

    public static MapLocation intToMapLocation(RobotController rc, int location){
        location -=1;
        return new MapLocation(location%rc.getMapWidth(), location/rc.getMapWidth());
    }
}
