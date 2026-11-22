package org.firstinspires.ftc.teamcode.opModes.Auto;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Launcher;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "Pedro Auto", group = "Auto", preselectTeleOp = "BioBuzz Teleop")
//@Disabled

public class PedroAuto extends OpMode {
    private Intake intake = new Intake();
    private Launcher launcher = new Launcher();

    private final PoseFactory pf = PoseFactory.degrees();
    // our poses
    private final Pose startPoseUp = pf.of(55.4396, 8.1868, 270);
    private final Pose parkUp = pf.of(10.0475, 100.6324, 180);
    private final Pose parkCtlUp = pf.of(13.4028, 32.2695, 0);

    private final Pose startPoseDown = pf.of(79.3991, 8.3685, 270);
    private final Pose parkDown = pf.of(131.1719, 26.972, 180);
    private final Pose parkCtlDown = pf.of(105.5943, 32.547, 0);

    double intakePower;

    enum InitState {
        GET_ALLIANCE,
        GET_HIVE_POSITION,
        GET_WAIT_AND_LAUNCH,
        CONFIRM_SELECTIONS,
        SET_START_POSE,
        FINISHED
    }
    InitState initState = InitState.GET_ALLIANCE;

    enum ALLIANCE {
        BLUE_ALLIANCE,
        RED_ALLIANCE
    }
        ALLIANCE alliance = ALLIANCE.RED_ALLIANCE;

    enum HIVE_POSITION {
        HIVE_POSITION_UP,
        HIVE_POSITION_DOWN
    }
    HIVE_POSITION hivePosition = HIVE_POSITION.HIVE_POSITION_UP;

    enum PATHSTATE {
        START_PATH,
        WAIT_DELAY,
        LAUNCH_POLLEN,
        FOLLOW_PATH,
        FINISHED
    }

    PATHSTATE pathState = PATHSTATE.START_PATH;

    private Follower follower;
    private int gameDelaySeconds =15;
    private double launchDelay = 5; // time allowed for launch
    private int waitTime = 0;
    private boolean waitAndLaunch = true;
    private ElapsedTime pathRuntime = new ElapsedTime();
    private ElapsedTime opmodeRuntime = new ElapsedTime();
    private ElapsedTime launcherTime = new ElapsedTime();

    public Path path1Up() {
        return Paths.curve(startPoseUp, parkCtlUp, parkUp).linear(startPoseUp, parkUp);
    }

    public Path path1Down() {
        return Paths.curve(startPoseDown, parkCtlDown, parkDown).linear(startPoseDown, parkDown);
    }

    public void init() {
        intake.init(hardwareMap);
        launcher.init(hardwareMap);

        follower = Constants.create(hardwareMap);
    }

    public void init_loop() {
        switch (initState) {
            case GET_ALLIANCE:  // initial state waiting for driver to press A
                telemetry.addLine("Select alliance");
                telemetry.addLine("Press A for Red alliance");
                telemetry.addLine("Press B for Blue alliance");
                if (gamepad1.aWasPressed()) {
                    alliance = ALLIANCE.RED_ALLIANCE;
                    initState = InitState.GET_HIVE_POSITION;
                } else if (gamepad1.bWasPressed()) {
                    alliance = ALLIANCE.BLUE_ALLIANCE;
                    initState = InitState.GET_HIVE_POSITION;
                }
                break;
            case GET_HIVE_POSITION:
                telemetry.addLine("Select hive position");
                telemetry.addLine("Press A for hive up");
                telemetry.addLine("Press B for hive down");
                if (gamepad1.aWasPressed()) {
                    hivePosition = HIVE_POSITION.HIVE_POSITION_UP;
                    initState = InitState.CONFIRM_SELECTIONS;
                } else if (gamepad1.bWasPressed()) {
                    hivePosition = HIVE_POSITION.HIVE_POSITION_DOWN;
                    initState = InitState.GET_WAIT_AND_LAUNCH;
                }
                break;
            case GET_WAIT_AND_LAUNCH:
                telemetry.addLine("Set wait & launch");
                telemetry.addLine("Press A to wait & launch");
                telemetry.addLine("Press B to skip wait & launch");
                if (gamepad1.aWasPressed()) {
                    waitAndLaunch = true;
                    initState = InitState.CONFIRM_SELECTIONS;
                } else if (gamepad1.bWasPressed()) {
                    waitAndLaunch = false;
                    initState = InitState.CONFIRM_SELECTIONS;
                }
                break;
            case CONFIRM_SELECTIONS:
                telemetry.clear();
                telemetry.addData("Alliance = ", alliance);
                telemetry.addData("Hive position = ", hivePosition);
                if (hivePosition == HIVE_POSITION.HIVE_POSITION_DOWN) {
                    telemetry.addData("Wait & Launch = ", waitAndLaunch);
                }
                telemetry.addLine("");
                telemetry.addLine("Press Y to accept");
                telemetry.addLine("Press X to restart");
                if (gamepad1.xWasPressed()) {
                    initState = InitState.GET_ALLIANCE;
                }
                if (gamepad1.yWasPressed()) {
                    initState = InitState.SET_START_POSE;
                }
                break;
            case SET_START_POSE:
                   if (hivePosition == HIVE_POSITION.HIVE_POSITION_UP){
                      follower.setPose(startPoseUp);
                   } else {
                       follower.setPose(startPoseDown);
                   }
                initState = InitState.FINISHED;
                break;
            default: // final state
                telemetry.addLine("state finished");
        }

    }

    public void start() {
        opmodeRuntime.reset();
        if (hivePosition == HIVE_POSITION.HIVE_POSITION_DOWN) {
            waitTime = gameDelaySeconds;
        }
    }

    public void loop() {

        switch (pathState) {
            case START_PATH:     // Determine path start from init choices
                if (hivePosition == HIVE_POSITION.HIVE_POSITION_UP) {
                    launcher.launcher.setVelocity(launcher.LAUNCHER_TARGET_VELOCITY);
                    pathState = PATHSTATE.LAUNCH_POLLEN;                // launch pollen if hive up
                } else if (waitAndLaunch){                              // check wait & launch choice
                    launcher.launcher.setVelocity(launcher.LAUNCHER_TARGET_VELOCITY);
                    pathState = PATHSTATE.WAIT_DELAY;
                } else {
                    pathState = PATHSTATE.FOLLOW_PATH;
                }

                break;

            case WAIT_DELAY:
                if (opmodeRuntime.seconds() > waitTime) {
                    pathState = PATHSTATE.LAUNCH_POLLEN;
                }
                break;
            case LAUNCH_POLLEN:
                launch();
                pathState = PATHSTATE.FOLLOW_PATH;
                launcher.launcher.setVelocity(0);   // turn off launcher
                break;
            case FOLLOW_PATH:
                    if (hivePosition == HIVE_POSITION.HIVE_POSITION_UP){
                        follower.follow(path1Up());
                    } else {
                        follower.follow(path1Down());
                    }

                while (follower.following()) {
                    follower.update();
                }
                break;
            case FINISHED:
                telemetry.addLine("path finished");
                break;
            default: // final state
                telemetry.addLine("path fail");

        }
    }

    void launch() {
        launcherTime.reset();
        while (launcherTime.seconds() < launchDelay) {

            if (launcher.launcher.getVelocity() > launcher.LAUNCHER_MIN_VELOCITY) {
                launcher.windmillServo.setPower(1);
                //  intakePower += 0.5;
                //   telemetry.addData("launcher ", launcher.getVelocity());
            }
        }
        launcher.windmillServo.setPower(0);
        launcher.launcher.setVelocity(0);


    }

}

