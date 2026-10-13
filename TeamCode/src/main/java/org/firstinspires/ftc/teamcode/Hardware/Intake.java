package org.firstinspires.ftc.teamcode.Hardware;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    public DcMotor intake = null;

    //********** Servos ***************************
    public CRServo leftIntakeServo = null;
    public CRServo rightIntakeServo = null;

    public void init(HardwareMap hwMap) {
        intake = hwMap.get(DcMotor.class, "intake");
        leftIntakeServo = hwMap.get(CRServo.class, "left_intake_servo");
        rightIntakeServo = hwMap.get(CRServo.class, "right_intake_servo");

        intake.setZeroPowerBehavior(BRAKE);
        leftIntakeServo.setPower(0);
        rightIntakeServo.setPower(0);

        rightIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);

    }

}
