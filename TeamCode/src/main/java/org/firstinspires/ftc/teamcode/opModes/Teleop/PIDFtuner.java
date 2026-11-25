package org.firstinspires.ftc.teamcode.opModes.Teleop;
/*
adapted from biobuzzTeleop
*/

/*   MIT License
 *   Copyright (c) [2026] [Base 10 Assets, LLC]
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:

 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.

 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */



import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.Hardware.DriveTrain;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Launcher;
import org.firstinspires.ftc.teamcode.Hardware.PinPoint;

/*
 * This teleop class was devived from the GoBilda starter example teleop, modified to be field centric
 *
 */

@TeleOp(name = "PIDF Tuner", group = "Teleop")
//@Disabled
public class PIDFtuner extends OpMode {

    // Declare OpMode members.
    //********** motors ***************************
    private DriveTrain driveTrain = new DriveTrain();
    private Intake intake = new Intake();
    private PinPoint pinpoint = new PinPoint();
    private Launcher launcher = new Launcher();


    // Create a variable to set to the intake.
    double intakePower;

    double curPosRadians;
    boolean fieldCentric = true;

    double PID_P = 0.0;
    double PID_I = 0.0;
    double PID_D = 0.0;
    double PID_F = 0.0;
    PIDFCoefficients pidf = new PIDFCoefficients(PID_P,PID_I,PID_D,PID_F);
    double P_Increment = 0.5;
    double F_Increment = 0.05;
    double test_min_velocity = 1300;  //2580 rpm
    double test_max_velocity = 1340;  //2580 rpm
    double test_target_velocity = 1320; // 2700 rpm

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        driveTrain.init(hardwareMap);
        pinpoint.init(hardwareMap);
        intake.init(hardwareMap);
        launcher.init(hardwareMap);
        fieldCentric = true;

        PIDFCoefficients pidOrig = launcher.launcher.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        PID_P = pidOrig.p; // set new pidf values
        PID_F = pidOrig.f;

        /*
         * Tell the driver that initialization is complete.
         */
        telemetry.addData("Status", "Initialized");
        if (fieldCentric) {
            telemetry.addLine("Field Centric");
        } else {
            telemetry.addLine("Robot Centric");
        }
        telemetry.addLine(" ");
        telemetry.addData("P,F (orig)", "%.0f, %.04f",
                pidOrig.p, pidOrig.f);
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit START
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits START
     */
    @Override
    public void start() {
    }

    /*
     * Code to run REPEATEDLY after the driver hits START but before they hit STOP
     */
    @Override
    public void loop() {


        //  if button A, reset IMU heading
        if (gamepad1.aWasPressed()) {
            pinpoint.pinPoint.resetPosAndIMU();
        }

        // if button X, toggle field centric
        if (gamepad1.xWasPressed()){
            fieldCentric = !fieldCentric;
        }

        if (gamepad1.yWasPressed()) {
            test_min_velocity = test_min_velocity + 5;
            test_target_velocity= test_target_velocity + 5;
        }

        if (gamepad1.bWasPressed()) {
            test_min_velocity = test_min_velocity -5;
            test_target_velocity= test_target_velocity -5;
        }

        if (gamepad1.dpadUpWasPressed()){
            PID_F = PID_F + F_Increment;
            launcher.launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pidf);
            pidf = launcher.launcher.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
            telemetry.addData("P,F (changed)", "%.0f, %.04f",
                    pidf.p, pidf.f);
        }

        if (gamepad1.dpadDownWasPressed()){
            PID_F = PID_F - F_Increment;
            launcher.launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pidf);
            pidf = launcher.launcher.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
            telemetry.addData("P,F (changed)", "%.0f, %.04f",
                    pidf.p, pidf.f);
        }

        if (gamepad1.dpadRightWasPressed()){
            PID_P = PID_P + P_Increment;
            launcher.launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pidf);
            pidf = launcher.launcher.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
            telemetry.addData("P,F (changed)", "%.0f, %.04f",
                    pidf.p, pidf.f);
        }

        if (gamepad1.dpadLeftWasPressed()){
            PID_P = PID_P - P_Increment;
            launcher.launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pidf);
            pidf = launcher.launcher.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
            telemetry.addData("P,F (changed)", "%.0f, %.04f",
                    pidf.p, pidf.f);
        }

        pinpoint.pinPoint.update();

        if (fieldCentric){
            // field centric
//            curPosRadians = imu.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            curPosRadians = pinpoint.pinPoint.getHeading();
            driveTrain.driveRobotField(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x, curPosRadians);
        } else {

            // robot centric
            driveTrain.mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        }

        /*
         * Set the intake power variable to equal the right trigger, minus the left trigger.
         * Each trigger outputs a signal from 0-1, with 0 as fully released, and 1 fully depressed.
         * This gives us proportional control of the intake speed. The speed increases as we pull
         * the right trigger further. It's occasionally helpful to be able to reverse the intake,
         * so we also factor in the left trigger. If the left trigger is fully depressed,
         * the intakePower variable will be -1. If the right trigger is fully depressed, the variable
         * will be 1. If the driver pulls both triggers, the intake will remain off.
         * We use this technique (creating a variable, and setting it to our control inputs) to
         * allow us to avoid setting the same motors/servos power more than once per loop. That can
         * create erratic behavior.
         */
        intakePower = gamepad1.right_trigger - gamepad1.left_trigger;

        launch();

        /*
         * Here we set our intake motor and servos to their intake power. The order of operations
         * here is important though. The gamepad triggers define the starting point for the intake
         * power variable in each loop of our code, but inside our launch function we also sometimes
         * change the intake power. So we need to give our launch function a chance to modify the
         * variable before we write it to our motor and servos.
         */
        intake.intake.setPower(intakePower);
        intake.leftIntakeServo.setPower(intakePower);
        intake.rightIntakeServo.setPower(intakePower);

        /*
         * Show motor powers on the Driver Station via telemetry.

        telemetry.addData("field centric ",fieldCentric);
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", driveTrain.leftFrontPower, driveTrain.rightFrontPower);
        telemetry.addData("Triggers", "left (%.2f, right (%.2f)",gamepad1.left_trigger, gamepad1.right_trigger);
        telemetry.addData("Velocity ",launcher.launcher.getVelocity());
*/
   }
    void launch() {
        /*
         * Calling gamepad1.right_bumper returns a boolean which will be true if the bumper is
         * held down, and false if it is not. Notably, this will continue to be true for every
         * cycle of our code that the driver holds down that bumper.
         * The first step of our launch() function is checking to see if the user is currently
         * holding down the right gamepad. If they are, then we want to start spinning up the launcher.
         * Otherwise, we start spinning the launcher down.
         */
        if (gamepad1.right_bumper) {
            launcher.launcher.setVelocity(test_target_velocity);
        } else {
            launcher.launcher.setVelocity(0);
        }

        /*
         * Here we ask if the driver is currently pressing the right bumper, AND the launcher is
         * spinning fast enough to make a successful shot. If it is, then we will turn on the
         * windmill servo to start feeding the elements into the launcher motor. We also
         * add some power to the intake power. This can sometimes help dislodge stuck elements from
         * inside the hopper.
         */
        double velocity = launcher.launcher.getVelocity();
        if (gamepad1.right_bumper  && velocity > test_min_velocity  && velocity <= test_max_velocity ) {
            launcher.windmillServo.setPower(1);
            intakePower += 0.5;
            telemetry.addData("Target","%.4f",test_target_velocity);
            telemetry.addData("Velocity ","%.4f",velocity);
            telemetry.addData("P,F (current)", "%.04f, %.04f",
                    pidf.p, pidf.f);

        } else {
            launcher.windmillServo.setPower(0);
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }




}
