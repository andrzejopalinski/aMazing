package com.example.amazing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class GameView extends View {
    private Cell[][] cells;
    private Cell player, exit;
    private static final int COLS = 7, ROWS = 10;
    private static final float Wall_Thickness = 5;
    private float cellSize, hMargin, vMargin;
    private Paint wallPaint, playerPaint, exitPaint;
    private Random random;

    public GameView(Context context) {
        super(context);

        wallPaint = new Paint();
        playerPaint = new Paint();
        playerPaint.setColor(Color.BLUE);
        exitPaint = new Paint();
        exitPaint.setColor(Color.GREEN);

        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(Wall_Thickness);

        random = new Random();

        createMaze();
    }


    private Cell getNeighbour(Cell cell) {
        ArrayList<Cell> neighbours = new ArrayList<>();

        //left cell
        if (cell.col > 0)
            if (!cells[cell.col - 1][cell.row].visited)
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

        player = cells[2][0];
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

    private int width;
    private int height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.YELLOW);

        cellSize = height / (ROWS+5);

        hMargin = (width - (COLS * cellSize)) / 2;
        vMargin = (height - (ROWS * cellSize)) / 2;

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

        float margin = cellSize / 10;
        canvas.drawCircle(player.col + (cellSize/2),
                player.row + (cellSize/2),
                cellSize/2 - (margin),
                playerPaint);

        canvas.drawRect(
                exit.col * cellSize + margin,
                exit.row * cellSize + margin,
                (exit.col + 1) * cellSize - margin,
                (exit.row + 1) * cellSize - margin,
                exitPaint);
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_MOVE)
        return super.onTouchEvent(event);
    }*/

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