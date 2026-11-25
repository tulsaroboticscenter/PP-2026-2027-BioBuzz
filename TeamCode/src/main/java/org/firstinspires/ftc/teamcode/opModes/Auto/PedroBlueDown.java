package org.firstinspires.ftc.teamcode.opModes.Auto;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "Pedro Blue Down", group = "Auto",preselectTeleOp = "BioBuzz Teleop")
@Disabled

public class PedroBlueDown extends OpMode {
    private Follower follower;
    private final PoseFactory pf = PoseFactory.degrees();
    // our poses
    private final Pose startPose = pf.of(79.3991, 8.3685, 270);
    private final Pose park = pf.of(130.9876, 28.446, 180);
    private final Pose parkCtl = pf.of(105.5943, 32.547, 0);

    public Path path1() {
        return Paths.curve(startPose, parkCtl, park).linear(startPose, park);
    }

    public void init(){
    follower = Constants.create(hardwareMap);
    follower.setPose(startPose);
    }

    public void init_loop(){

    }

    public void start(){

    }

    public void loop(){
        follower.follow(path1());
        while (follower.following()){
        follower.update();
         }
    }
}
