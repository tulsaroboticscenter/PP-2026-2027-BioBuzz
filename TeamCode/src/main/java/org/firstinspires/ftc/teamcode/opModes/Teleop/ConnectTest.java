package org.firstinspires.ftc.teamcode.opModes.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@Disabled
@TeleOp
public class ConnectTest extends OpMode {
    int index = 0;

    public void init(){

    }
    public void loop() {

        if (gamepad1.aWasPressed()){
            index++;
        }
        if (gamepad1.bWasPressed()){
            index--;
        }
        telemetry.addData("index ", index);
    }
}
