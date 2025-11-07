package org.firstinspires.ftc.robotcontroller.external.samples.studica;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name="Encoder Auto - Straight and Shoot", group="Linear Opmode")
public class AutoStraightAndShoot extends LinearOpMode {

    // Drive motors
    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightBackDrive = null;

    // Launcher components
    private CRServo flyWheel = null;
    private CRServo backSpin = null;
    private CRServo indexLeft = null;
    private CRServo indexRight = null;

    static final double DRIVE_SPEED = 0.5;       // Forward speed
    static final double FLY_SPEED = 1.0;         // Flywheel speed
    static final double BACKSPIN_SPEED = 1.0;    // Backspin servo speed
    static final double INDEX_SPEED = 0.2;       // Indexing servo speed

    static final double COUNTS_PER_MOTOR_REV = 1120;  // REV HD 40:1 motor (adjust if needed)
    static final double WHEEL_DIAMETER_INCHES = 4.0;  // For calculating circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV) / (WHEEL_DIAMETER_INCHES * 3.1415);

    @Override
    public void runOpMode() {
        // Initialize hardware
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");
        flyWheel = hardwareMap.get(CRServo.class, "flyWheel");
        backSpin = hardwareMap.get(CRServo.class, "backSpin");
        indexLeft = hardwareMap.get(CRServo.class, "leftServo");
        indexRight = hardwareMap.get(CRServo.class, "rightServo");

        // Motor direction setup
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        // Zero power brake for stability
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reset encoders
        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // 1️⃣ Drive forward 24 inches
        driveStraight(24, DRIVE_SPEED);

        // 2️⃣ Shoot rings
        shootRings();

        // 3️⃣ Stop all motion
        stopAllMotors();
    }

    private void driveStraight(double inches, double speed) {
        int moveCounts = (int)(inches * COUNTS_PER_INCH);

        int newLeftFrontTarget = leftFrontDrive.getCurrentPosition() + moveCounts;
        int newRightFrontTarget = rightFrontDrive.getCurrentPosition() + moveCounts;
        int newLeftBackTarget = leftBackDrive.getCurrentPosition() + moveCounts;
        int newRightBackTarget = rightBackDrive.getCurrentPosition() + moveCounts;

        leftFrontDrive.setTargetPosition(newLeftFrontTarget);
        rightFrontDrive.setTargetPosition(newRightFrontTarget);
        leftBackDrive.setTargetPosition(newLeftBackTarget);
        rightBackDrive.setTargetPosition(newRightBackTarget);

        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightBackDrive.setPower(speed);

        while (opModeIsActive() &&
                (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() &&
                        leftBackDrive.isBusy() && rightBackDrive.isBusy())) {
            telemetry.addData("Moving", "Driving forward");
            telemetry.update();
        }

        stopAllMotors();

        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void shootRings() {
        telemetry.addData("Action", "Shooting rings...");
        telemetry.update();

        // Turn on launcher
        flyWheel.setPower(FLY_SPEED);
        backSpin.setPower(BACKSPIN_SPEED);

        sleep(2000); // Spin up time

        // Feed rings into shooter
        indexLeft.setPower(INDEX_SPEED);
        indexRight.setPower(INDEX_SPEED);
        sleep(2500); // Duration of shooting

        // Stop everything
        flyWheel.setPower(0);
        backSpin.setPower(0);
        indexLeft.setPower(0);
        indexRight.setPower(0);
    }

    private void stopAllMotors() {
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }
}
