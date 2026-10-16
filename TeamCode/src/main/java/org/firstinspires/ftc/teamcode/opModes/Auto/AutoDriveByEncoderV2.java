/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

/*
 * This OpMode illustrates the concept of driving a path based on encoder counts.

 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: RobotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forward, and causes the encoders to count UP.
 *
 *   The desired path in this example is:
 *   - Drive forward for 6 inches
 *   - Spin right for 24 Inches
 *   - Spin left for 24 Inches
 *   - Drive forward for 24 inches
  *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 *  that performs the actual movement.
 *  This method assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 *  */

@Autonomous(name = "Auto Drive By Encoder V2", group = "Robot",preselectTeleOp = "BioBuzz Teleop")
@Disabled
public class AutoDriveByEncoderV2 extends OpMode {

    // Calculate the COUNTS_PER_INCH for your specific drive train.
    // Go to your motor vendor website to determine your motor's COUNTS_PER_MOTOR_REV
    // For external drive gearing, set DRIVE_GEAR_REDUCTION as needed.
    // For example, use a value of 2.0 for a 12-tooth spur gear driving a 24-tooth spur gear.
    // This is gearing DOWN for less speed and more torque.
    // For gearing UP, use a gear ratio less than 1.0. Note this will affect the direction of wheel rotation.
    static final double PI =3.1415;
    static final double COUNTS_PER_MOTOR_REV = 537.7;    // eg: gobilda 5203 Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 1.0;     // No External Gearing.
    static final double WHEEL_DIAMETER_INCHES = 4.0;     // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * PI);
    static final double DRIVE_SPEED = 0.6;
    static final double TURN_SPEED = 0.5;
    /* Declare OpMode members. */

    private int waitTime = 0;

    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotorEx launcher = null;
    private CRServo windmillServo = null;

    public final int LAUNCHER_TARGET_VELOCITY = 1300; //2678 RPM
    public final int LAUNCHER_MIN_VELOCITY = 1250; //2571 RPM

    private int gameDelaySeconds = 5;
    private double launchDelay = 5;
    private double turn90 = 27.5;
    private double distanceToHive = -10;
    private double distanceToWall = 35;
    private double distanceToParkDown = 12;
    private double distanceToParkUp = 65;
    private double leftTurn90_left = -turn90;
    private double leftTurn90_right = turn90;
    private double rightTurn90_left = turn90;
    private double rightTurn90_right = -turn90;


    private ElapsedTime pathRuntime = new ElapsedTime();
    private ElapsedTime opmodeRuntime = new ElapsedTime();
    private ElapsedTime launcherTime = new ElapsedTime();
    private boolean runningPath = true;

    enum InitState {
        GET_ALLIANCE,
        GET_HIVE_POSITION,
        CONFIRM_SELECTIONS,
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
        ORIENT_LAUNCHER,
        DRIVE_TOWARD_HIVE,
        WAIT_DELAY,
        LAUNCH_POLLEN,
        TURN_TOWARD_WALL,
        DRIVE_TOWARD_WALL,
        TURN_TOWARD_PARK,
        DRIVE_TOWARD_PARK,
        FINISHED
    }
    PATHSTATE pathState = PATHSTATE.ORIENT_LAUNCHER;

    @Override
    public void init() {

        // Initialize the drive system variables.
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);

        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        windmillServo = hardwareMap.get(CRServo.class, "windmillServo");

        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(40, 0, 0, 12.5));
        windmillServo.setPower(0);
        windmillServo.setDirection(DcMotorSimple.Direction.REVERSE);


        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Starting at", "%7d :%7d",
                leftFrontDrive.getCurrentPosition(),
                rightFrontDrive.getCurrentPosition());
        telemetry.update();
    }

    // initialize loop to get match variables: delay seconds, alliance, etc
    public void init_loop(){

        switch (initState) {
            case GET_ALLIANCE:  // initial state waiting for driver to press A
                telemetry.addLine("Select alliance");
                telemetry.addLine("Press A for Red alliance");
                telemetry.addLine("Press B for Blue alliance");
                if (gamepad1.aWasPressed()){
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
                if (gamepad1.aWasPressed()){
                    hivePosition = HIVE_POSITION.HIVE_POSITION_UP;
                    initState = InitState.CONFIRM_SELECTIONS;
                } else if (gamepad1.bWasPressed()) {
                    hivePosition = HIVE_POSITION.HIVE_POSITION_DOWN;
                    initState = InitState.CONFIRM_SELECTIONS;
                }
                break;
            case CONFIRM_SELECTIONS:
                telemetry.clear();
                telemetry.addData("Alliance = ",alliance);
                telemetry.addData("Hive position = ",hivePosition);
                telemetry.addLine("");
                telemetry.addLine("Press Y to accept");
                telemetry.addLine("Press X to restart");
                if (gamepad1.xWasPressed()){
                    initState = InitState.GET_ALLIANCE;
                }
                if (gamepad1.yWasPressed()){
                    initState = InitState.FINISHED;
                }
                break;
            default: // final state
                telemetry.addLine("state finished");
        }

    }

    // reset elapsed time when driver hits start. used to control delay if needed
    public void start(){

        opmodeRuntime.reset();
        launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
        if (hivePosition == HIVE_POSITION.HIVE_POSITION_DOWN) {
            waitTime = gameDelaySeconds;
        }
    }


    @Override
    public void loop() {


        switch (pathState){
            case ORIENT_LAUNCHER:
                if (hivePosition == HIVE_POSITION.HIVE_POSITION_DOWN) {
                    encoderDrive(TURN_SPEED, leftTurn90_left, leftTurn90_right, 2.0);  // S2: Turn Right 12 Inches with 4 Sec timeout
                } else{
                    encoderDrive(TURN_SPEED, rightTurn90_left, rightTurn90_right, 2.0);  // S2: Turn Rleft 12 Inches with 4 Sec timeout
                }
                if (hivePosition == HIVE_POSITION.HIVE_POSITION_DOWN) {
                    pathState = PATHSTATE.TURN_TOWARD_WALL;

                } else {

                pathState = PATHSTATE.WAIT_DELAY;}
                break;
 //           case Drive_TOWARD_HIVE:
 //               encoderDrive(DRIVE_SPEED, distanceToHive, distanceToHive, 2.0);  // S1: Forward 6 Inches with 5 Sec timeout
 //               pathState = PATHSTATE.WAIT_DELAY;
//                break;
            case WAIT_DELAY:
                if (opmodeRuntime.seconds() > waitTime) {
                    pathState = PATHSTATE.LAUNCH_POLLEN;
                }
                break;
            case LAUNCH_POLLEN:
                launch();
                pathState = PATHSTATE.TURN_TOWARD_WALL;
                break;
            case TURN_TOWARD_WALL:
                if (hivePosition == HIVE_POSITION.HIVE_POSITION_DOWN) {
                    encoderDrive(TURN_SPEED, leftTurn90_left, leftTurn90_right, 2.0);  // S2: Turn Right 12 Inches with 4 Sec timeout
                } else{
                    encoderDrive(TURN_SPEED, rightTurn90_left, rightTurn90_right, 2.0);  // S2: Turn Rleft 12 Inches with 4 Sec timeout
                }

                pathState = PATHSTATE.DRIVE_TOWARD_WALL;
                break;
            case DRIVE_TOWARD_WALL:
                encoderDrive(DRIVE_SPEED, distanceToWall, distanceToWall, 2.0);  // S1: Forward 6 Inches with 5 Sec timeout
                pathState = PATHSTATE.TURN_TOWARD_PARK;
                break;
            case TURN_TOWARD_PARK:
                if (hivePosition == HIVE_POSITION.HIVE_POSITION_DOWN) {
                    encoderDrive(TURN_SPEED, leftTurn90_left, leftTurn90_right, 2.0);  // S2: Turn Right 12 Inches with 4 Sec timeout
                } else{
                    encoderDrive(TURN_SPEED, rightTurn90_left, rightTurn90_right, 2.0);  // S2: Turn Rleft 12 Inches with 4 Sec timeout
                }
                pathState = PATHSTATE.DRIVE_TOWARD_PARK;
                break;
            case DRIVE_TOWARD_PARK:
                if (hivePosition == HIVE_POSITION.HIVE_POSITION_UP) {
                    encoderDrive(DRIVE_SPEED, distanceToParkUp, distanceToParkUp, 2.0);  // S1: Forward 6 Inches with 5 Sec timeout
                } else {
                    encoderDrive(DRIVE_SPEED, distanceToParkDown, distanceToParkDown, 2.0);  // S1: Forward 6 Inches with 5 Sec timeout
                }
                pathState = PATHSTATE.FINISHED;
                break;
            case FINISHED:
                telemetry.addLine("path finished");
                break;
            default: // final state
                telemetry.addLine("path fail");
        }



    }

    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;


        // Determine new target position, and pass to motor controller
        newLeftTarget = leftFrontDrive.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
        newRightTarget = rightFrontDrive.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
        leftFrontDrive.setTargetPosition(newLeftTarget);
        rightFrontDrive.setTargetPosition(newRightTarget);

        // Turn On RUN_TO_POSITION
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // reset the timeout time and start motion.
        pathRuntime.reset();
        leftFrontDrive.setPower(Math.abs(speed));
        rightFrontDrive.setPower(Math.abs(speed));

        // keep looping while we are still active, and there is time left, and both motors are running.
        // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
        // its target position, the motion will stop.  This is "safer" in the event that the robot will
        // always end the motion as soon as possible.
        // However, if you require that BOTH motors have finished their moves before the robot continues
        // onto the next step, use (isBusy() || isBusy()) in the loop test.
        while ((pathRuntime.seconds() < timeoutS) && (leftFrontDrive.isBusy() && rightFrontDrive.isBusy())) {

            // Display it for the driver.
    //        telemetry.addData("Running to", " %7d :%7d", newLeftTarget, newRightTarget);
    //        telemetry.addData("Currently at", " at %7d :%7d",
    //               leftFrontDrive.getCurrentPosition(), rightFrontDrive.getCurrentPosition());
    //        telemetry.update();
        }

        // Stop all motion;
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);

        // Turn off RUN_TO_POSITION
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //sleep(250);   // optional pause after each move.
    }
    void launch() {
        launcherTime.reset();

        while (launcherTime.seconds() < launchDelay) {
            if (launcher.getVelocity() > LAUNCHER_MIN_VELOCITY) {
            windmillServo.setPower(1);
            //  intakePower += 0.5;
            telemetry.addData("launcher ", launcher.getVelocity());
            }
        }
        windmillServo.setPower(0);
        launcher.setVelocity(0);


    }

}
