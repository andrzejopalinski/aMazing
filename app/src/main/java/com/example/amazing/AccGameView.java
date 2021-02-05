package com.example.amazing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class AccGameView extends View {

    private Cell[][] cells;
    private Cell player, exit;
    private static final int COLS = 5, ROWS = 10;
    private static final int Wall_Thickness = 5;
    private float cellSize, hMargin, vMargin;
    private Paint wallPaint, exitPaint;
    private Random random;



    private Paint ballPaint;
    private static final int radius = 40;

    private int height, width;

    public AccGameView(Context context) {
        super(context);

        ballPaint = new Paint();
        ballPaint.setColor(Color.BLUE);



        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(Wall_Thickness);

        exitPaint = new Paint();
        exitPaint.setColor(Color.GREEN);

        random = new Random();

        createMaze();

    }


    private Cell getNeighbour(Cell cell){
        ArrayList<Cell> neighbours = new ArrayList<Cell>();

        //left
        if(cell.col > 0)
            if(!cells[cell.col - 1][cell.row].visited)
                neighbours.add(cells[cell.col - 1][cell.row]);

        //right cell
        if (cell.col < COLS - 1)
            if (!cells[cell.col + 1][cell.row].visited)
                neighbours.add(cells[cell.col + 1][cell.row]);

        //top cell
        if (cell.row > 0)
            if (!cells[cell.col][cell.row - 1].visited)
                neighbours.add(cells[cell.col][cell.row - 1]);

        //bottom cell
        if (cell.row < ROWS - 1)
            if (!cells[cell.col][cell.row + 1].visited)
                neighbours.add(cells[cell.col][cell.row + 1]);

        if (neighbours.size() > 0) {
            int index = random.nextInt(neighbours.size());
            return neighbours.get(index);
        }
        return null;
    }


    private void removeWall(Cell current, Cell next) {
        if (current.col == next.col && current.row == next.row + 1) {
            current.topWall = false;
            next.bottomWall = false;
        }

        if (current.col == next.col && current.row == next.row - 1) {
            current.bottomWall = false;
            next.topWall = false;
        }

        if (current.col == next.col + 1 && current.row == next.row) {
            current.leftWall = false;
            next.rightWall = false;
        }

        if (current.col == next.col - 1 && current.row == next.row) {
            current.rightWall = false;
            next.leftWall = false;
        }
    }

    private void createMaze() {
        Stack<Cell> stack = new Stack<>();
        Cell current, next;

        cells = new Cell[COLS][ROWS];

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }

        player = cells[0][0];

        exit = cells[COLS - 1][ROWS - 1];

        current = cells[0][0];
        current.visited = true;

        do {
            next = getNeighbour(current);
            if (next != null) {
                removeWall(current, next);
                stack.push(current);
                current = next;
                current.visited = true;
            } else
                current = stack.pop();
        } while (!stack.empty());
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        width = w;
    }

    public void onSensorEvent(SensorEvent event){
        player.col = player.col - (int) event.values[0];
        player.row = player.row + (int) event.values[1];

        if(player.col <= radius){ //ogranicza z lewej
            player.col = radius;
        }
        if(player.col >= (COLS * cellSize) - radius){ //ogranicza z prawej
            player.col = (int) ((COLS * cellSize) - radius);
        }

        if(player.row <= radius){
            player.row = radius;
        }
        if(player.row >= (ROWS * cellSize) - radius){
            player.row = (int) ((ROWS * cellSize) - radius);
        }

        /*for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                if(player.col == cells[x][y].col){
                    player.col = cells[x][y].col;
                }
                if(player.row == cells[x][y].row){
                    player.row = cells[x][y].row;
                }
            }
        }*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(-256); //yellow

        if (width / height < COLS / ROWS) {
            cellSize = width / (COLS + 1);
        } else {
            cellSize = height / (ROWS + 4);
        }

        hMargin = (width - (COLS * (height / (ROWS + 4)))) / 2;
        vMargin = (height - (ROWS * (height / (ROWS + 4)))) / 2;

        canvas.translate(hMargin, vMargin);

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                if (cells[x][y].topWall)
                    canvas.drawLine(
                            x * cellSize,
                            y * cellSize,
                            (x + 1) * cellSize,
                            y * cellSize,
                            wallPaint);

                if (cells[x][y].leftWall)
                    canvas.drawLine(
                            x * cellSize,
                            y * cellSize,
                            x * cellSize,
                            (y + 1) * cellSize,
                            wallPaint);

                if (cells[x][y].rightWall)
                    canvas.drawLine(
                            (x + 1) * cellSize,
                            y * cellSize,
                            (x + 1) * cellSize,
                            (y + 1) * cellSize,
                            wallPaint);

                if (cells[x][y].bottomWall)
                    canvas.drawLine(
                            x * cellSize,
                            (y + 1) * cellSize,
                            (x + 1) * cellSize,
                            (y + 1) * cellSize,
                            wallPaint);

            }
        }


        canvas.drawCircle(player.col, player.row, radius, ballPaint);

        canvas.drawRect(
                exit.col * cellSize + (cellSize/10),
                exit.row * cellSize + (cellSize/10),
                (exit.col + 1) * cellSize - (cellSize/10),
                (exit.row + 1) * cellSize - (cellSize/10),
                exitPaint);
        invalidate();
    }

    private void checkExit() {
        if (player == exit)
            createMaze();
    }

    private class Cell {
        boolean
                topWall = true,
                leftWall = true,
                bottomWall = true,
                rightWall = true,
                visited = false;

        int col, row;

        public Cell(int col, int row) {
            this.col = col;
            this.row = row;
        }
    }
}