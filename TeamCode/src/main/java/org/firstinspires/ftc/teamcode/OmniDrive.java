package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class OmniDrive {

    private final ElapsedTime runtime = new ElapsedTime();

    private final String LEFT_FRONT_DRIVE = "left_front_drive";
    private final String LEFT_BACK_DRIVE = "left_back_drive";
    private final String RIGHT_FRONT_DRIVE = "right_front_drive";
    private final String RIGHT_BACK_DRIVE = "right_back_drive";

    private final Telemetry telemetry;

    public DcMotor leftFrontDrive;
    public DcMotor leftBackDrive;
    public DcMotor rightFrontDrive;
    public DcMotor rightBackDrive;

    public IMU imu;

    public OmniDrive(BioBuzzStarterbotTeleopMecanum op) {

        telemetry = op.telemetry;

        leftFrontDrive = op.hardwareMap.get(DcMotor.class, LEFT_FRONT_DRIVE);
        leftBackDrive = op.hardwareMap.get(DcMotor.class, LEFT_BACK_DRIVE);
        rightFrontDrive = op.hardwareMap.get(DcMotor.class, RIGHT_FRONT_DRIVE);
        rightBackDrive = op.hardwareMap.get(DcMotor.class, RIGHT_BACK_DRIVE);

        imu = op.hardwareMap.get(IMU.class, "imu");

        // Motor directions
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        // Brake behavior
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // IMU orientation
        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        );

        imu.initialize(parameters);
    }

    public void driveFirstPerson(
            double driveY,
            double driveX,
            double turn,
            boolean resetYaw) {

        // Reset yaw before calculating heading
        if (resetYaw) {
            imu.resetYaw();
        }

        double botHeading = imu.getRobotYawPitchRollAngles()
                .getYaw(AngleUnit.RADIANS);

        // Field-centric transformation
        double rotX = driveX * Math.cos(-botHeading)
                - driveY * Math.sin(-botHeading);

        double rotY = driveX * Math.sin(-botHeading)
                + driveY * Math.cos(-botHeading);

        // Strafing adjustment
        rotX *= 1.1;

        // Calculate wheel powers
        double denominator = Math.max(
                Math.abs(rotY) + Math.abs(rotX) + Math.abs(turn),
                1
        );

        double leftFrontPower = (rotY + rotX + turn) / denominator;
        double rightFrontPower = (rotY - rotX - turn) / denominator;
        double leftBackPower = (rotY - rotX + turn) / denominator;
        double rightBackPower = (rotY + rotX - turn) / denominator;

        // Normalize
        double max = Math.max(
                Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower)),
                Math.max(Math.abs(leftBackPower), Math.abs(rightBackPower))
        );

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        // Apply motor powers
        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);

        // Telemetry
        telemetry.addData("Drive Runtime", runtime.toString());
        telemetry.addData("Front Left/Right", "%.2f, %.2f",
                leftFrontPower, rightFrontPower);
        telemetry.addData("Back Left/Right", "%.2f, %.2f",
                leftBackPower, rightBackPower);
        telemetry.addData("Heading", "%.2f", botHeading);
    }
}