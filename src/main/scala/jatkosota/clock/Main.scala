package jatkosota.clock

import java.time.LocalTime
import javafx.util.Duration

import scalafx.Includes._
import scalafx.animation.{Animation, Interpolator, RotateTransition}
import scalafx.application.JFXApp
import scalafx.beans.property.DoubleProperty
import scalafx.scene.{Group, Node, Scene}
import scalafx.scene.layout.{Pane, StackPane}
import scalafx.scene.paint.{Color, CycleMethod, RadialGradient, Stop}
import scalafx.scene.shape._
import scalafx.scene.transform.Rotate
import scalafx.stage.StageStyle

object Main extends JFXApp {

  val r = DoubleProperty(100)

  stage = new JFXApp.PrimaryStage {
    val clockDial = createClockDial
    val hourHand = createHourHand
    val minuteHand = createMinuteHand
    val secondHand = createSecondHand
    val centerPoint = createCenterPoint

    createRotateTransition(
      Duration.seconds(60),
      secondHand,
      angleOfSeconds(LocalTime.now()))
        .play()

    createRotateTransition(
      Duration.minutes(60),
      minuteHand,
      angleOfMinute(LocalTime.now())
    ).play()

    createRotateTransition(
      Duration.hours(12),
      hourHand,
      angleOfHour(LocalTime.now())
    ).play()

    scene = new Scene {
      root = new StackPane {
        children = Seq(
          clockDial,
          hourHand,
          minuteHand,
          secondHand,
          centerPoint
        )
      }
      fill = Color.Transparent
    }

    initStyle(StageStyle.Transparent)
  }

  def createClockDial =
    new Pane {
      children = Seq(createCircle, createTickMarks)
    }

  def createCircle =
    new Circle {
      centerX <== r
      centerY <== r
      radius <== r
      fill = RadialGradient(
        0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NoCycle,
        Stop(0.8, Color.White),
        Stop(0.9, Color.Black),
        Stop(0.95, Color.White),
        Stop(1.0, Color.Black)
      )
    }

  def createTickMark(n: Int) =
    new Line {
      startX <== r
      startY <== (if(n % 5 == 0) r * 0.12 else r * 0.15)
      endX <== r
      endY <== (if(n % 5 == 0) r * 0.2 else r * 0.16)
      transforms = Seq(new Rotate {
        angle = 360 / 60 * n
        pivotX <== r
        pivotY <== r
      })
      strokeWidth = 2
    }

  def createTickMarks = new Group {
    children = (0 to 60).map(createTickMark)
  }

  def createHourOrMinuteHand(stretchRelativeToRim: Double, color: Color) =
    new Path {
      elements = Seq(
        new MoveTo {
          x <== r
          y <== r
        },
        new LineTo{
          x <== r * 0.9
          y <== r * 0.9
        },
        new LineTo {
          x <== r
          y <== r * stretchRelativeToRim
        },
        new LineTo {
          x <== r * 1.1
          y <== r * 0.9
        },
        new LineTo {
          x <== r
          y <== r
        }
      )
      fill = color
      stroke = Color.Transparent
    }

  def createHourHand =
    new Pane {
      children = createHourOrMinuteHand(0.4, Color.Black)
    }

  def createMinuteHand =
    new Pane {
      children = createHourOrMinuteHand(0.2, Color.Black)
    }

  def createSecondHand =
    new Pane {
      prefWidth <== r * 2
      prefHeight <== r * 2
      children = Seq(new Line {
        startX <== r
        startY <== r * 1.1
        endX <== r
        endY <== r * 0.2
      })
    }

  def createCenterPoint =
    new Circle {
      centerX <== r
      centerY <== r
      radius <== r * 0.05
      fill = Color.Black
    }

  def createRotateTransition(d: Duration, n: Node, fa: Double) =
    new RotateTransition { self =>
      duration = d
      node = n
      fromAngle = fa
      byAngle = 360
      cycleCount = Animation.Indefinite
      interpolator = Interpolator.Linear
    }

  def angleOfSeconds(time: LocalTime) =
    time.getSecond * 360 / 60

  def angleOfMinute(time: LocalTime) =
    (time.getMinute + time.getSecond / 60.0) * 360 / 60.0

  def angleOfHour(time: LocalTime) =
    (time.getHour % 12 + time.getMinute / 60.0 + time.getSecond / (60.0 * 60.0)) * 360 / 12
}
