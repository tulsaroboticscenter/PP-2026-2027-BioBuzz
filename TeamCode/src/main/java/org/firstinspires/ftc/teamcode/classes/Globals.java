package org.firstinspires.ftc.teamcode.classes;

import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.opModes.Auto.AutoDriveByEncoder;

public class Globals {
    public static Pose2D lastKnownPosition = null;
    public static boolean fieldCentric = true;
    public enum ALLIANCE {
        BLUE_ALLIANCE,
        RED_ALLIANCE
    }

    public static ALLIANCE alliance = ALLIANCE.RED_ALLIANCE;

}
