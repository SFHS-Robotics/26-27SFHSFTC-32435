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

package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp(name = "Mec BioBuzz StarterBot Teleop", group = "StarterBot")

public class BioBuzzStarterbotTeleopMecanum extends OpMode {

    // Drivetrain
    private OmniDrive drive;

    // Other motors and servos
    private DcMotorEx launcher;
    private DcMotor intake;

    private CRServo leftIntakeServo;
    private CRServo rightIntakeServo;
    private CRServo windmillServo;

    // Launcher settings
    public final int LAUNCHER_TARGET_VELOCITY = 1250;
    public final int LAUNCHER_MIN_VELOCITY = 1200;

    // Intake power
    double intakePower;

    @Override
    public void init() {

        // Initialize drivetrain ONCE
        drive = new OmniDrive(this);

        // Initialize intake and launcher
        intake = hardwareMap.get(DcMotor.class, "intake");

        launcher = hardwareMap.get(DcMotorEx.class, "launcher");

        // Initialize servos
        windmillServo = hardwareMap.get(CRServo.class, "windmillServo");

        leftIntakeServo = hardwareMap.get(CRServo.class, "left_intake_servo");

        rightIntakeServo = hardwareMap.get(CRServo.class, "right_intake_servo");

        // Motor settings
        intake.setZeroPowerBehavior(BRAKE);

        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        launcher.setPIDFCoefficients(
                DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(40, 0, 0, 12.5)
        );

        // Initialize servos
        leftIntakeServo.setPower(0);
        rightIntakeServo.setPower(0);
        windmillServo.setPower(0);

        // Servo directions
        rightIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);

        windmillServo.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {

        // --------------------------------
        // DRIVETRAIN
        // --------------------------------

        drive.driveFirstPerson(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                gamepad1.options
        );

        // --------------------------------
        // INTAKE
        // --------------------------------

        intakePower = gamepad1.right_trigger - gamepad1.left_trigger;

        // --------------------------------
        // LAUNCHER
        // --------------------------------

        launch();

        // --------------------------------
        // APPLY INTAKE POWER
        // --------------------------------

        intake.setPower(intakePower);

        leftIntakeServo.setPower(intakePower);

        rightIntakeServo.setPower(intakePower);

        // --------------------------------
        // TELEMETRY
        // --------------------------------

        telemetry.addData("Status", "Running");

        telemetry.addData("Intake Power", intakePower);

        telemetry.addData("Launcher Velocity", "%.0f",
                launcher.getVelocity());

        telemetry.addData("Launcher Target", LAUNCHER_TARGET_VELOCITY);

        telemetry.addData("Left Stick Y", gamepad1.left_stick_y);

        telemetry.addData("Left Stick X", gamepad1.left_stick_x);

        telemetry.addData("Right Stick X", gamepad1.right_stick_x);

        telemetry.update();
    }

    @Override
    public void stop() {

        // Stop everything when OpMode ends
        intake.setPower(0);
        launcher.setVelocity(0);

        leftIntakeServo.setPower(0);
        rightIntakeServo.setPower(0);
        windmillServo.setPower(0);
    }

    void launch() {

        // Start launcher
        if (gamepad2.right_bumper) {

            launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);

        } else {

            launcher.setVelocity(0);
        }

        // Only feed when launcher reaches minimum velocity
        if (gamepad2.right_bumper &&
                launcher.getVelocity() > LAUNCHER_MIN_VELOCITY) {

            windmillServo.setPower(1);



        } else {

            windmillServo.setPower(0);
        }
        if (gamepad2.x ) {

            windmillServo.setPower(1);
        }
        if (gamepad2.left_bumper) {

            intakePower += 0.5;

        }
    }
}