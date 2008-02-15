/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007 Karl D.D. Willis
 * 
 * Alchemy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alchemy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 */
package alchemy.create;

import alchemy.*;
import alchemy.ui.AlcSubSlider;
import alchemy.ui.AlcSubToggleButton;
import alchemy.ui.AlcSubToolBarSection;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * SpeedShapes
 * @author Karl D.D. Willis
 */
public class SpeedShapes extends AlcModule implements AlcConstants {

    private Point oldP;
    private int multiplier = 15;
    private AlcSubToolBarSection subToolBarSection;
    private boolean freeform = true;

    public SpeedShapes() {

    }

    protected void setup() {
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

        // Shake/Fatten button
        AlcSubToggleButton lineStyleButton = new AlcSubToggleButton("Style", AlcUtil.getUrlPath("linestyle.png", getClassLoader()));
        lineStyleButton.setToolTipText("Select the style of line");

        lineStyleButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        freeform = !freeform;
                    }
                });
        subToolBarSection.add(lineStyleButton);


        AlcSubSlider speedSlider = new AlcSubSlider("Speed", 0, 35, multiplier);
        speedSlider.setToolTipText("Change the amount cursor movement is sped up by");
        speedSlider.slider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        JSlider source = (JSlider) e.getSource();
                        if (!source.getValueIsAdjusting()) {
                            int value = source.getValue();
                            multiplier = value;
                        //System.out.println(multiplier);
                        }
                    }
                });
        subToolBarSection.add(speedSlider);
    }

    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        canvas.createShapes.add(makeShape(p));
        canvas.redraw();
        oldP = p;
    }

    public void mouseDragged(MouseEvent e) {
        // Need to test if it is null incase the shape has been auto-cleared
        if (canvas.getCurrentCreateShape() != null) {
            Point p = e.getPoint();
            int speed = getCursorSpeed(p, oldP) / 2;

            Point pt = getAngle(p, oldP, speed);
            if (freeform) {
                canvas.getCurrentCreateShape().addCurvePoint(pt);
            } else {
                canvas.getCurrentCreateShape().addLinePoint(pt);
            }

            canvas.redraw();
            oldP = p;
        }
    }

    public void mouseReleased(MouseEvent e) {
        oldP = null;
        canvas.commitShapes();
    }

    private AlcShape makeShape(Point p) {
        // Make a new shape with the globally defined style etc...
        return new AlcShape(p, canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
    }

    private static int getCursorSpeed(Point p1, Point p2) {
        int diffX = Math.abs(p1.x - p2.x);
        int diffY = Math.abs(p1.y - p2.y);
        return diffX + diffY;
    }

    private Point getAngle(Point p1, Point p2, double distance) {
        double adjustedDistance = distance * multiplier;
        // Calculate the angle between the last point and the new point
        //double angle = Math.atan2(p1.y - p2.y, p1.x - p2.x) - root.math.random(TWO_PI);
        double angle = Math.atan2(p1.y - p2.y, p1.x - p2.x);
        //System.out.println(angle);
        // Conver the polar coordinates to cartesian
        double x = p1.x + (adjustedDistance * Math.cos(angle));
        double y = p1.y + (adjustedDistance * Math.sin(angle));

        return new Point((int) x, (int) y);
    }
}
