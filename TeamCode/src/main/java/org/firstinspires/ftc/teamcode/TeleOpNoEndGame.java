/*
 * Copyright 2025 FIRST
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.firstinspires.ftc.robotcontroller.external.samples.studica;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/*
 * This file contains a minimal example of an iterative (Non-Linear) "OpMode". An OpMode is a
 * 'program' that runs in either the autonomous or the TeleOp period of an FTC match. The names
 * of OpModes appear on the menu of the FTC Driver Station. When an selection is made from the
 * menu, the corresponding OpMode class is instantiated on the Robot Controller and executed.
 *
 * Remove the @Disabled annotation on the next line or two (if present) to add this OpMode to the
 * Driver Station OpMode list, or add a @Disabled annotation to prevent this OpMode from being
 * added to the Driver Station.
 */
@TeleOp

public class TeleOpNoEndGame extends OpMode {
    /*static final double FULL_SPEED = 1.0;
    static final double FLY_GOAL_SPEED = 1.0;
    static final double INDEX_GOAL_SPEED = 0.2;*/
    static final double STOP_SPEED = 0.0;
    static final double INDEX_GOAL_SPEED = 1;
    static double flyWheelSpeed = 1;
    static double wheelSpeedMulti = 1;
    static int speedCounter = 0;


    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightBackDrive = null;
    private CRServo flyWheel = null;
    private CRServo backSpin = null;
    private CRServo indexLeft = null;
    private CRServo indexRight = null;
    ////private CRServo foot = null;
    ///private double FOOT_UP_POWER = 1.0;
    ///private double FOOT_DOWN_POWER = -0.85;
    ///private double FOOT_OFF_POWER = 0.0;
    ///private double footPower = FOOT_OFF_POWER;
    ///private enum FootMode {UP, DOWN, BRAKE}
    ///private FootMode footmode;

    //Used for the cycling through the wheel power options
    boolean prevRightBumper1 = false;
    boolean prevLeftBumper1 = false;

    double leftFrontPower;
    double rightFrontPower;
    double leftBackPower;
    double rightBackPower;

    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");
        flyWheel = hardwareMap.get(CRServo.class, "flyWheel");
        backSpin = hardwareMap.get(CRServo.class, "backSpin");
        indexLeft = hardwareMap.get(CRServo.class, "leftServo");
        indexRight = hardwareMap.get(CRServo.class, "rightServo");

        // Incase of wiring into the wrong ports these flags can be switched
        backSpin.setDirection(DcMotor.Direction.FORWARD);
        indexLeft.setDirection(DcMotor.Direction.REVERSE);
        indexRight.setDirection(DcMotor.Direction.FORWARD);

        /*
         * To drive forward, most robots need the motor on one side to be reversed,
         * because the axles point in opposite directions. Pushing the left stick forward
         * MUST make robot go forward. So adjust these two lines based on your first test drive.
         * Note: The settings here assume direct drive on left and right wheels. Gear
         * Reduction or 90 Deg drives may require direction flips
         */
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);
        flyWheel.setDirection(DcMotor.Direction.FORWARD);

        /*
         * Setting zeroPowerBehavior to BRAKE enables a "brake mode". This causes the motor to
         * slow down much faster when it is coasting. This creates a much more controllable
         * drivetrain. As the robot stops much quicker.
         */
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //flyWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x, wheelSpeedMulti);
        ///For controller 1
        //Cycles throught the speed mutipliers
        if (gamepad1.right_bumper && !prevRightBumper1){ //Sees when bumper goes from not pressed -> pressed
            speedCounter ++;
        }
        else if (gamepad1.left_bumper && !prevLeftBumper1){
            speedCounter --;
        }
        // Wrap between 1–3
        if (speedCounter > 3) speedCounter = 1;
        if (speedCounter < 1) speedCounter = 3;
        // Cycle through speed settings
        if (speedCounter == 1) {
            wheelSpeedMulti = 0.5;
        } else if (speedCounter == 2) {
            wheelSpeedMulti = 0.75;
        } else if (speedCounter == 3){
            wheelSpeedMulti = 1;
        }
        // Update telemetry
        telemetry.addData("Wheel Multiplier", wheelSpeedMulti);
        telemetry.update();
        // Remember last button states
        prevRightBumper1 = gamepad1.right_bumper;
        prevLeftBumper1 = gamepad1.left_bumper;

        if (gamepad1.dpad_left){
            turnrobot (0.5,45,true);//Turns 45 degrees left
        }
        if (gamepad1.dpad_right){
            turnrobot(0.5,45,false);//Turns 45 degree right
        }
        if (gamepad1.dpad_down){
            turnrobot(0.5,180,true);//Turns 180 degrees
        }

        ///For controller 2
        //To make the ball shoot
        if (gamepad2.a) {
            setLauncher(flyWheelSpeed,INDEX_GOAL_SPEED);
        } else {
            setLauncher(STOP_SPEED,STOP_SPEED);
        }
        //To change the power of the flyWheel
        if(gamepad2.right_bumper && flyWheelSpeed < 1){
            flyWheelSpeed += 0.1;
            telemetry.addData("Fly Wheel Speed", flyWheelSpeed);
            telemetry.update();

        }
        if (gamepad2.left_bumper && flyWheelSpeed > 0) {
            flyWheelSpeed -= 0.1;
            telemetry.addData("Fly Wheel Speed", flyWheelSpeed);
            telemetry.update();

        }
        /*if (gamepad2.right_bumper || gamepad1.left_bumper){

            if (flyWheelSpeed <= 1 && flyWheelSpeed > 0){
                if (gamepad1.right_bumper)flyWheelSpeed += 0.1;
                else flyWheelSpeed -= 0.1;
            }
            telemetry.addData("Fly Wheel Speed", flyWheelSpeed);
            telemetry.update();
        }*/
        //To move balls back in the shoot
        if (gamepad2.b){
            setBackSpin(1);
        }
        else {
            setBackSpin (STOP_SPEED);
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

    /*
     * This method does the math and sets the power to motors for
     * an arcade drive.
     */
    void mecanumDrive(double forward, double strafe, double rotate, double mutiplier){

        /* the denominator is the largest motor power (absolute value) or 1
         * This ensures all the powers maintain the same ratio,
         * but only if at least one is out of the range [-1, 1]
         */
        double denominator = Math.max(Math.abs(forward) + Math.abs(strafe) + Math.abs(rotate), 1);

        leftFrontPower = ((forward + strafe + rotate) / denominator)*mutiplier;
        rightFrontPower = ((forward - strafe - rotate) / denominator)*mutiplier;
        leftBackPower = ((forward - strafe + rotate) / denominator)*mutiplier;
        rightBackPower = ((forward + strafe - rotate) / denominator)*mutiplier;

        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);

    }

    /*
     * This sets the 1 flywheel motor, 1 back spin CR servo and the 2 index CR servos to the
     * given power.
     */
    public void setLauncher(double flyPower, double indexPower) {
        flyWheel.setPower(flyPower);
        backSpin.setPower(flyPower);
        indexLeft.setPower(indexPower);
        indexRight.setPower(indexPower);
    }
    ///Allows to user to just spin the back spin to push ball back
    public void setBackSpin (double power){
        backSpin.setPower(-power);
        indexLeft.setPower(-power);
        indexRight.setPower(-power);
    }
    ///Makes the bot turn left or write when the right button is pressed
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
