package org.firstinspires.ftc.teamcode.opModes.Auto;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Launcher;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Pedro Red Up", group = "Auto",preselectTeleOp = "BioBuzz Teleop")
//@Disabled

public class PedroRedUp extends OpMode {
    private Follower follower;
    private final PoseFactory pf = PoseFactory.degrees();
    private Intake intake = new Intake();
    private Launcher launcher = new Launcher();


    // our poses
    private final Pose startPose = pf.of(55.4396, 8.1868, 270);
    private final Pose park = pf.of(10.0475, 100.6324, 180);
    private final Pose parkCtl = pf.of(13.4028, 32.2695, 0);

    public Path path1() {
        return Paths.curve(startPose, parkCtl, park).linear(startPose, park);
    }

    private double launchDelay = 5; // time allowed for launch

    double intakePower;
    private ElapsedTime opmodeRuntime = new ElapsedTime();
    private ElapsedTime launcherTime = new ElapsedTime();

    enum PATHSTATE {
        LAUNCH_POLLEN,
        FOLLOW_PATH,
        FINISHED
    }
    PATHSTATE pathState = PATHSTATE.LAUNCH_POLLEN;

    public void init(){
    intake.init(hardwareMap);
    launcher.init(hardwareMap);

    follower = Constants.create(hardwareMap);
    follower.setPose(startPose);
    }

    public void init_loop(){

    }

    public void start(){
        opmodeRuntime.reset();
    }

    public void loop(){

        switch (pathState) {
            case LAUNCH_POLLEN:
                launch();
                pathState = PATHSTATE.FOLLOW_PATH;
                launcher.launcher.setVelocity(0);   // turn off launcher


                break;
            case FOLLOW_PATH:
                follower.follow(path1());
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

