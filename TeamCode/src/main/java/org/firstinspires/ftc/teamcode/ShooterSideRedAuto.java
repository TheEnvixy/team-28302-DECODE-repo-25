package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name="ShooterSideRedAuto", group="Linear Opmode")
public class ShooterSideRedAuto extends LinearOpMode{

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
    static final double FLY_SPEED = 1.0;         // Flywheel speed and Backspin speed
    static final double INDEX_SPEED = 1.0;       // Indexing servo speed

    static final double TICKS_PER_MOTOR_REV = 560; //REV HD Hex 20:1 Motor (Online)
    static final double WHEEL_DIAMETER = 2.99; //Changes depending on the wheel
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV) / (WHEEL_DIAMETER * Math.PI);

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
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);
        flyWheel.setDirection(DcMotor.Direction.FORWARD);
        // Incase of wiring into the wrong ports these flags can be switched
        backSpin.setDirection(DcMotor.Direction.FORWARD);
        indexLeft.setDirection(DcMotor.Direction.REVERSE);
        indexRight.setDirection(DcMotor.Direction.FORWARD);


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

        movement (-8, 0);
        movement (0, -5);
        shootRings();
        stopAllMotors();
    }

    private void movement (double forwardInches, double strafeInches) {
        int xMovement = (int)(strafeInches * TICKS_PER_INCH);
        int yMovement = (int)(forwardInches * TICKS_PER_INCH);

        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        int leftFrontTarget = leftFrontDrive.getCurrentPosition() + yMovement + xMovement;
        int rightFrontTarget = rightFrontDrive.getCurrentPosition() + yMovement - xMovement;
        int leftBackTarget = leftBackDrive.getCurrentPosition() + yMovement - xMovement;
        int rightBackTarget = rightBackDrive.getCurrentPosition() + yMovement + xMovement;

        leftFrontDrive.setTargetPosition(leftFrontTarget);
        rightFrontDrive.setTargetPosition(rightFrontTarget);
        leftBackDrive.setTargetPosition(leftBackTarget);
        rightBackDrive.setTargetPosition(rightBackTarget);

        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setPower(DRIVE_SPEED);
        rightFrontDrive.setPower(DRIVE_SPEED);
        leftBackDrive.setPower(DRIVE_SPEED);
        rightBackDrive.setPower(DRIVE_SPEED);

        while (opModeIsActive() && (leftFrontDrive.isBusy() || rightFrontDrive.isBusy() || leftBackDrive.isBusy() || rightBackDrive.isBusy())) {
            telemetry.addData("Moving", "Forward: %.1f  Strafe: %.1f", forwardInches, strafeInches);
            telemetry.update();
        }
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);

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
        backSpin.setPower(FLY_SPEED);

        sleep(2000); // Spin up time

        // Feed rings into shooter
        indexLeft.setPower(INDEX_SPEED);
        indexRight.setPower(INDEX_SPEED);
        sleep(4000); // Duration of shooting

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

    public void turnrobot (double speed, double degrees, boolean turnLeft){
        //Setting up all the constants
        final double TICKS_PER_MOTOR_REV = 560; //REV HD Hex 20:1 Motor (Online)
        final double WHEEL_DIAMETER = 2.99; //Changes depending on the wheel
        final double ROBOT_DIAMETER = 18; //Changers depending the competiton
        final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV) / (WHEEL_DIAMETER * Math.PI);
        //Calculate distance each wheel travels to turn the given angle
        double CIRCUMFERENCE = Math.PI * ROBOT_DIAMETER;
        double distancePerDegree = CIRCUMFERENCE / 360.0;
        double turnDistance = distancePerDegree * degrees;
        //Determines what direction to turn
        double leftDistance = turnLeft ? turnDistance : -turnDistance; //(boolean ? ifTrue : ifFalse)
        double rightDistance = -leftDistance;
        //Calculate target encoder positions
        int newLeftFrontTarget = leftFrontDrive.getCurrentPosition() + (int)(leftDistance * TICKS_PER_INCH);
        int newLeftBackTarget = leftBackDrive.getCurrentPosition() + (int)(leftDistance * TICKS_PER_INCH);
        int newRightFrontTarget = rightFrontDrive.getCurrentPosition() + (int)(rightDistance * TICKS_PER_INCH);
        int newRightBackTarget = rightBackDrive.getCurrentPosition() + (int)(rightDistance * TICKS_PER_INCH);
        //Sets the target position
        leftFrontDrive.setTargetPosition(newLeftFrontTarget);
        leftBackDrive.setTargetPosition(newLeftBackTarget);
        rightFrontDrive.setTargetPosition(newRightFrontTarget);
        rightBackDrive.setTargetPosition(newRightBackTarget);
        //Changes the mode to run to position
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //Makes the robot start moving
        leftFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        rightBackDrive.setPower(speed);
        //Waits until the robot stops moving
        while (leftFrontDrive.isBusy() && rightFrontDrive.isBusy()) {
            telemetry.addData("Turning", turnLeft ? "Right" : "Left");
            telemetry.update();
        }
        //Sets all the motors back to zero
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightBackDrive.setPower(0);
        //Resets the encoder mode
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
