package org.firstinspires.ftc.teamcode.opModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@Autonomous(name="launcher",group="Auto")
public class LauncherTest extends OpMode {
    private DcMotorEx launcher = null;
    private CRServo windmillServo = null;
    public final int LAUNCHER_TARGET_VELOCITY = 1250; //2678 RPM
    public final int LAUNCHER_MIN_VELOCITY = 1200; //2571 RPM

    private ElapsedTime     runtime = new ElapsedTime();

    @Override
    public void init() {
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        windmillServo = hardwareMap.get(CRServo.class, "windmillServo");

        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(40, 0, 0, 12.5));
        windmillServo.setPower(0);
        windmillServo.setDirection(DcMotorSimple.Direction.REVERSE);

    }

    @Override
    public void start() {
        runtime.reset();
        launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
    }

    @Override
    public void loop() {
        if (runtime.seconds() < 3) {
            launch();
        }
        else {
            launcher.setVelocity(0);
            windmillServo.setPower(0);
        }
    }
    void launch() {

        if (launcher.getVelocity() > LAUNCHER_MIN_VELOCITY) {
            windmillServo.setPower(1);
          //  intakePower += 0.5;
        } else {
            windmillServo.setPower(0);
        }
    }

}
