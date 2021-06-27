package org.firstinspires.ftc.teamcode.robot2020;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.button.GamepadButton;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

@Config
@TeleOp(name = "driver control v2")
public class DriverControl extends LinearOpMode
{
    Robot robot;
    GamepadButtonManager fullAutoLaunchButton;
    GamepadButtonManager semiAutoLaunchButton;
    GamepadButtonManager pointToZero;
    GamepadButtonManager RPMChange;
    GamepadButtonManager resetPos;
    GamepadButtonManager wobbleGaolDrop;
    GamepadButtonManager autoLaunchPowerShot;
    GamepadButtonManager autoLaunchPowerShot2;
    GamepadButtonManager speedToggle;
    GamepadButtonManager driveModeButton;
    //LK
    GamepadButtonManager pointToGoal;

    double slowSpeed = 0.3;
    boolean useHeadlessMode = false;

    @Override
    public void runOpMode()
    {
        RobotUsage ru = new RobotUsage();
        ru.visionUsage.useVuforia = false;
        ru.useComplexMovement = false;

        robot = new Robot(this, ru);
        robot.positionTracker.positionSettings.startPosMode = 2;

        waitForStart();

        fullAutoLaunchButton = new GamepadButtonManager(gamepad2, GamepadButtons.dpadUP);
        semiAutoLaunchButton = new GamepadButtonManager(gamepad2, GamepadButtons.dpadLEFT);
        pointToZero = new GamepadButtonManager(gamepad2, GamepadButtons.dpadRIGHT);
        RPMChange = new GamepadButtonManager(gamepad2, GamepadButtons.leftBUMPER);
        resetPos = new GamepadButtonManager(gamepad1, GamepadButtons.rightBUMPER);
        wobbleGaolDrop = new GamepadButtonManager(gamepad1, GamepadButtons.A);
        autoLaunchPowerShot = new GamepadButtonManager(gamepad2, GamepadButtons.leftJoyStickBUTTON);
        autoLaunchPowerShot2 = new GamepadButtonManager(gamepad2, GamepadButtons.rightJoyStickBUTTON);
        speedToggle = new GamepadButtonManager(gamepad1, GamepadButtons.leftTRIGGER);
        driveModeButton = new GamepadButtonManager(gamepad1, GamepadButtons.X);
        speedToggle.minSliderVal = 0.3;
        //LK
        pointToGoal = new GamepadButtonManager(gamepad1, GamepadButtons.rightJoyStickBUTTON);

        robot.start(true, false);

        while (opModeIsActive())
        {
            robot.startTelemetry();

            robot.movement.moveForTeleOp(gamepad1, null, useHeadlessMode, true);
            robot.grabber.runForTeleOp(gamepad1, true);
            robot.launcher.runForTeleOp(gamepad2,true);
            robot.positionTracker.drawAllPositions();

            if(fullAutoLaunchButton.getButtonHeld())
                robot.launcher.autoLaunchDiskFromLine();
            else if(semiAutoLaunchButton.getButtonHeld()) {
                robot.launcher.setRPM(robot.launcher.launcherSettings.autoLaunchRPM);
                robot.launcher.goToLine();
                robot.launcher.shutdownWheel = false;
            }
            else if(pointToZero.getButtonHeld())
                robot.movement.turnToAngle(0 , robot.movement.movementSettings.finalPosSettings.toRotAngleSettings());
            else if(wobbleGaolDrop.getButtonHeld())
                robot.grabber.autoDrop();
            else if(autoLaunchPowerShot.getButtonHeld())
                robot.launcher.autoLaunchPowerShots(robot.launcher.launcherSettings.powerShotPos, true);
            else if(autoLaunchPowerShot2.getButtonHeld())
                robot.launcher.autoLaunchPowerShots(robot.launcher.launcherSettings.powerShotPosV2, true);

            //LK demo 06/26/2021
            if(pointToGoal.getButtonPressed())
                robot.movement.movementSettings.teleOpFaceGoal = !robot.movement.movementSettings.teleOpFaceGoal;
            //

            if(RPMChange.getButtonPressed())
            {
              if(robot.launcher.targetWheelRpm == robot.launcher.launcherSettings.autoLaunchRPM){robot.launcher.targetWheelRpm = robot.launcher.launcherSettings.powerShotRPM;}
              else{robot.launcher.targetWheelRpm = robot.launcher.launcherSettings.autoLaunchRPM;}
            }
            if(resetPos.getButtonPressed()) {
                robot.positionTracker.resetAngle();
                if (Math.abs(robot.positionTracker.cameraPosition.R) > 4) {
                    robot.robotUsage.positionUsage.useCamera = false;
                }
                robot.positionTracker.setCurrentPosition(new Position(-26.8, -48.8, 0),true);
                robot.positionTracker.updateLeds();
            }
            if(speedToggle.getButtonHeld()) robot.movement.setSpeedMultiplier(slowSpeed);
            else robot.movement.setSpeedMultiplier(1);
            if(driveModeButton.getButtonPressed()) useHeadlessMode = !useHeadlessMode;

            float[] dist = robot.robotHardware.getDistancesList(robot.robotHardware.distSensors);
            robot.addTelemetry("dist 1", dist[0]);
            robot.addTelemetry("dist 2", dist[1]);
            robot.addTelemetry("dist", robot.positionTracker.distSensorPosition.toString(2));
            robot.addTelemetry("enc", robot.positionTracker.encoderPosition.toString(2));
            robot.addTelemetry("cam", robot.positionTracker.cameraPosition.toString(2));
            robot.addTelemetry("main pos", robot.positionTracker.currentPosition.toString(2));
            //LK
            robot.addTelemetry("facetarget",robot.movement.movementSettings.teleOpFaceGoal);

            robot.sendTelemetry();
        }
    }
}
