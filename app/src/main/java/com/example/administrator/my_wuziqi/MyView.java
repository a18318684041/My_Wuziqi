package com.example.administrator.my_wuziqi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.MainThread;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MyView extends View {
    private Paint mpaint = new Paint();
    private int mPanelWidth;
    private float mLineHeight;
    //格数
    private int MaxLine = 10;

    //棋子
    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    //轮到谁
    private boolean isWhite = true;

    //棋子占高度的比例
    private float rate = 3 * 1.0f / 4;

    //棋子的集合
    private List<Point> whiteArray = new ArrayList<>();
    private List<Point> BlackArray = new ArrayList<>();

    //逻辑判断
    private boolean mIsGameOver;
    private boolean mIsWhiteWin;

    private int Max_Count_In_Line = 5;

    //获取view中的画板
    private Canvas mcanvas;


    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setAntiAlias(true);
        //是否使用抖动处理
        mpaint.setDither(true);
        mpaint.setColor(Color.BLACK);
        //初始化棋子图片
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthModel = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);
        //如果尺寸为未指定尺寸的时候，可以为其指定一个尺寸，当棋盘被scrollview包裹的时候
        if (widthModel == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightModel == MeasureSpec.UNSPECIFIED) {
            width = widthModel;
        }
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //棋盘的宽度
        mPanelWidth = w;
        //每一行的高度
        mLineHeight = mPanelWidth * 1.0f / MaxLine;

        //确定棋子的宽高
        int pieceWidth = (int) (rate * mLineHeight);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mcanvas = canvas;
        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver(canvas);
    }

    //重新开始
    public void clearqiZi() {
        //点击确定清除棋子
        whiteArray.clear();
        BlackArray.clear();
        //drawBoard(canvas);
        drawPieces(mcanvas);
        mIsGameOver = false;
        invalidate();
    }

    //悔棋
    public void huiqi() {
        if (whiteArray.size() != 0) {
            if (isWhite) {
                int size = BlackArray.size();
                BlackArray.remove(size - 1);
                isWhite = !isWhite;
                invalidate();
            } else {
                int size = whiteArray.size();
                whiteArray.remove(size - 1);
                invalidate();
                isWhite = !isWhite;
            }
        } else {
            Toast.makeText(getContext(), "大侠,你还没落子", Toast.LENGTH_LONG).show();
        }
    }

    private void checkGameOver(final Canvas canvas) {
        boolean whiteWin = checkFiveInLine(whiteArray);
        boolean blackWin = checkFiveInLine(BlackArray);

        if (whiteWin || blackWin) {
            mIsGameOver = true;
            mIsWhiteWin = whiteWin;
            String text = mIsWhiteWin ? "白棋胜利" : "黑棋胜利";
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(getContext());
            normalDialog.setTitle(text);
            normalDialog.setMessage("点击确定重新开始游戏");
            normalDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //点击确定清除棋子
                            whiteArray.clear();
                            BlackArray.clear();
                            //drawBoard(canvas);
                            drawPieces(canvas);
                            mIsGameOver = false;
                            invalidate();
                        }
                    });
            normalDialog.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            // 显示
            normalDialog.show();
        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;

            boolean win = checkHorizontal(x, y, points);
            if (win) return true;
            win = checkVertical(x, y, points);
            if (win) return true;
            win = checkLeft(x, y, points);
            if (win) return true;
            win = checkRight(x, y, points);
            if (win) return true;
        }
        return false;
    }

    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        //左
        for (int i = 1; i < Max_Count_In_Line; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }

        if (count == Max_Count_In_Line) {
            return true;
        }
        //右
        for (int i = 1; i < Max_Count_In_Line; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        return false;
    }

    //垂直胜利
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        //上
        for (int i = 1; i < Max_Count_In_Line; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }

        if (count == Max_Count_In_Line) {
            return true;
        }
        //下
        for (int i = 1; i < Max_Count_In_Line; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        return false;
    }

    private boolean checkLeft(int x, int y, List<Point> points) {
        int count = 1;
        //上
        for (int i = 1; i < Max_Count_In_Line; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }

        if (count == Max_Count_In_Line) {
            return true;
        }
        //下
        for (int i = 1; i < Max_Count_In_Line; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        return false;
    }

    //右斜
    private boolean checkRight(int x, int y, List<Point> points) {
        int count = 1;

        //上
        for (int i = 1; i < Max_Count_In_Line; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }

        if (count == Max_Count_In_Line) {
            return true;
        }
        //下
        for (int i = 1; i < Max_Count_In_Line; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        return false;
    }


    public void drawPieces(Canvas canvas) {
        for (int i = 0; i < whiteArray.size(); i++) {
            Point whitePoint = whiteArray.get(i);
            canvas.drawBitmap(mWhitePiece, (whitePoint.x + (1 - rate) / 2) * mLineHeight, (whitePoint.y + (1 - rate) / 2) * mLineHeight, null);
        }

        for (int i = 0; i < BlackArray.size(); i++) {
            Point blackPoint = BlackArray.get(i);
            canvas.drawBitmap(mBlackPiece, (blackPoint.x + (1 - rate) / 2) * mLineHeight, (blackPoint.y + (1 - rate) / 2) * mLineHeight, null);
        }
    }

    //在切换横竖屏的时候调用的方法
    public void drawPieces() {
        for (int i = 0; i < whiteArray.size(); i++) {
            Point whitePoint = whiteArray.get(i);
            mcanvas.drawBitmap(mWhitePiece, (whitePoint.x + (1 - rate) / 2) * mLineHeight, (whitePoint.y + (1 - rate) / 2) * mLineHeight, null);
        }

        for (int i = 0; i < BlackArray.size(); i++) {
            Point blackPoint = BlackArray.get(i);
            mcanvas.drawBitmap(mBlackPiece, (blackPoint.x + (1 - rate) / 2) * mLineHeight, (blackPoint.y + (1 - rate) / 2) * mLineHeight, null);
        }
    }

    private void drawBoard(Canvas canvas) {
        int width = mPanelWidth;
        float lineHeitht = mLineHeight;
        for (int i = 0; i < MaxLine; i++) {
            //绘制横线
            int startX = (int) (lineHeitht / 2);
            int endX = (int) (width - lineHeitht / 2);
            int Y = (int) ((i + 0.5) * lineHeitht);
            canvas.drawLine(startX, Y, endX, Y, mpaint);

            //绘制纵线
            int X = (int) ((i + 0.5) * lineHeitht);
            int StartY = (int) (lineHeitht / 2);
            int EndY = (int) (width - lineHeitht / 2);
            canvas.drawLine(X, StartY, X, EndY, mpaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //游戏结束后，玩家不允许落子
        if (mIsGameOver) {
            return false;
        }

        int action = event.getAction();
        if (action == event.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x, y);
            if (whiteArray.contains(p) || BlackArray.contains(p)) {
                return false;
            }
            if (isWhite) {
                whiteArray.add(p);
            } else {
                BlackArray.add(p);
            }
            invalidate();
            isWhite = !isWhite;
            return true;
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }
}
