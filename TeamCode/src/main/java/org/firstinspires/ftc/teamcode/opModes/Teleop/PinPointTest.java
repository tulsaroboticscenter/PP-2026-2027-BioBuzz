package org.firstinspires.ftc.teamcode.opModes.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Hardware.PinPoint;
import org.firstinspires.ftc.teamcode.goBilda.GoBildaPinpointDriver;

@Disabled
@TeleOp(name = "Pinpoint test")
public class PinPointTest extends OpMode {
    private PinPoint pinPoint = new PinPoint();

    @Override
    public void init() {
        pinPoint.init(hardwareMap);
        pinPoint.pinPoint.resetPosAndIMU(); // reset heading to 0
     }

    @Override
    public void loop() {
        telemetry.addLine("Push robot to view tracking");
        telemetry.addLine("Press A to reset position");

        if (gamepad1.a){
            pinPoint.pinPoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));
        }

        pinPoint.pinPoint.update();
        Pose2D pose2D = pinPoint.pinPoint.getPosition();

        telemetry.addData("X in inches = ",pose2D.getX(DistanceUnit.INCH));
        telemetry.addData("Y in inches = ",pose2D.getY(DistanceUnit.INCH));
        telemetry.addData("Heading in degrees = ",pose2D.getHeading(AngleUnit.DEGREES));

    }

 }
