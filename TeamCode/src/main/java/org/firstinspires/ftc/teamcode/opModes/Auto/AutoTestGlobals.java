package org.firstinspires.ftc.teamcode.opModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.classes.Globals;

@Disabled
@Autonomous(name = "Auto Test Globals", group = "Test",preselectTeleOp = "TeleopTestGlobals")

public class AutoTestGlobals  extends OpMode {
  public void init(){
      Globals.lastKnownPosition = new Pose2D(DistanceUnit.INCH,1.5, 5.5, AngleUnit.DEGREES,180);

  }
  public void loop() {


  }
}
