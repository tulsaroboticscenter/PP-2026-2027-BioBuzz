package org.firstinspires.ftc.teamcode.opModes.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.classes.Globals;

//@Disabled
@TeleOp(name = "Teleop Test Globals", group = "Test")

public class TeleopTestGlobals extends OpMode {

    public void init() {
        telemetry.addData("position X",Globals.lastKnownPosition.getX(DistanceUnit.INCH));
        telemetry.addData("position Y",Globals.lastKnownPosition.getY(DistanceUnit.INCH));
        telemetry.addData("Heading ",Globals.lastKnownPosition.getHeading(AngleUnit.DEGREES));
        telemetry.addData("Alliance ",Globals.alliance);
        telemetry.addData("field centric ",Globals.fieldCentric);

    }

    public void loop() {

    }
}
