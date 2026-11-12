package org.firstinspires.ftc.teamcode.opModes.Auto;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import static com.pedropathing.api.Paths.*;

import com.pedropathing.paths.Path;
@Autonomous(name = "Pedro Example", group = "Test",preselectTeleOp = "BioBuzz Teleop")
//@Disabled

public class PedroExample extends OpMode {
    private Follower follower;
    private final PoseFactory pf = PoseFactory.degrees();
    // our poses
    private final Pose startPose = pf.of(55.4396, 8.1868, 270);
    private final Pose park = pf.of(10.0475, 100.6324, 180);
    private final Pose parkCtl = pf.of(13.4028, 32.2695, 0);

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
        follower.follow(path1());

    }

    public void loop(){

    }
}
