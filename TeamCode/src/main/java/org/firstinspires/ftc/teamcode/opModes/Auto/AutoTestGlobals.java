package org.firstinspires.ftc.teamcode.opModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Hardware.DriveTrain;
import org.firstinspires.ftc.teamcode.Hardware.Imu;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.PinPoint;
import org.firstinspires.ftc.teamcode.classes.Globals;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.classes.Globals;

//@Disabled
@Autonomous(name = "Auto Test Globals", group = "Test",preselectTeleOp = "Teleop Test Globals")

public class AutoTestGlobals  extends OpMode {
    boolean allianceSelected = false;
    boolean fieldCentric = true;
    double curPosRadians = 0.0;

    private DriveTrain driveTrain = new DriveTrain();
    private PinPoint pinPoint = new PinPoint();

  public void init(){
      driveTrain.init(hardwareMap);
      pinPoint.init(hardwareMap);

  }
  public void init_loop() {
      if (allianceSelected == false){
          telemetry.addLine("Select alliance");
          telemetry.addLine("Press A for Red alliance");
          telemetry.addLine("Press B for Blue alliance");
          if (gamepad1.aWasPressed()){
              Globals.alliance = Globals.ALLIANCE.RED_ALLIANCE;
              allianceSelected = true;
          } else if (gamepad1.bWasPressed()) {
              Globals.alliance = Globals.ALLIANCE.BLUE_ALLIANCE;
              allianceSelected = true;
          }
      }
  }
  public void loop() {
      //  if button A, reset IMU heading
      if (gamepad1.aWasPressed()) {
          pinPoint.pinPoint.resetPosAndIMU();
      }


      // if button X, toggle field centric
      if (gamepad1.xWasPressed()){
          fieldCentric = !fieldCentric;
      }

      if (fieldCentric){
          // field centric
          curPosRadians = pinPoint.pinPoint.getHeading();
          driveTrain.driveRobotField(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x, curPosRadians);
      } else {
          // robot centric
          driveTrain.mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
      }

      Globals.lastKnownPosition = pinPoint.pinPoint.getPosition();
      telemetry.addData("position X",Globals.lastKnownPosition.getX(DistanceUnit.INCH));
      telemetry.addData("position Y",Globals.lastKnownPosition.getY(DistanceUnit.INCH));
      telemetry.addData("Heading ",Globals.lastKnownPosition.getHeading(AngleUnit.DEGREES));
      telemetry.addData("Alliance ",Globals.alliance);

  }
}
