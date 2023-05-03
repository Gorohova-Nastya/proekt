package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.humbleui.jwm.MouseButton;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Rect;
import misc.CoordinateSystem2d;
import misc.CoordinateSystem2i;
import misc.Vector2d;
import misc.Vector2i;
import panels.PanelLog;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

//import static app.Colors.CROSSED_COLOR;
//import static app.Colors.SUBTRACTED_COLOR;

/**
 * Класс задачи
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class Task {
    /**
     * Текст задачи
     */
    public static final String TASK_TEXT = """
            ПОСТАНОВКА ЗАДАЧИ:
            На плоскости задано множество точек. Найти
            из них такие 4 точки, что построенный по ним
            четырёхугольник не является самопересекающимся
            и содержит в себе максимальное количество
            точек множества.""";

    /**
     * Вещественная система координат задачи
     */
    private final CoordinateSystem2d ownCS;
    /**
     * Список точек
     */
    private final ArrayList<Point> points;
    /**
     * Размер точки
     */
    private static final int POINT_SIZE = 3;
    /**
     * Последняя СК окна
     */
    private CoordinateSystem2i lastWindowCS;
    /**
     * Флаг, решена ли задача
     */
    private boolean solved;
    /**
     * Список точек в пересечении
     */
    private final ArrayList<Point> crossed;
    /**
     * Список точек в разности
     */
    private final ArrayList<Point> single;

    /**
     * Задача
     *
     * @param ownCS  СК задачи
     * @param points массив точек
     */
    @JsonCreator
    public Task(@JsonProperty("ownCS") CoordinateSystem2d ownCS, @JsonProperty("points") ArrayList<Point> points) {
        this.ownCS = ownCS;
        this.points = points;
        this.crossed = new ArrayList<>();
        this.single = new ArrayList<>();
    }

    /**
     * Рисование задачи
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        // Сохраняем последнюю СК
        lastWindowCS = windowCS;

        canvas.save();
        try (var paint = new Paint()) {
            for (Point p : points) {
                if (!solved) {
                    paint.setColor(p.getColor());
                }
                Vector2i windowPos = windowCS.getCoords(p.pos.x, p.pos.y, ownCS);
                canvas.drawRect(Rect.makeXYWH(windowPos.x - POINT_SIZE, windowPos.y - POINT_SIZE, POINT_SIZE * 2, POINT_SIZE * 2), paint);}
        }
        canvas.restore();
    }

    public void addPoint(Vector2d pos) {
        solved = false;
        Point newPoint = new Point(pos);
        points.add(newPoint);
        PanelLog.info("точка " + newPoint + " добавлена");
    }

    public void click(Vector2i pos, MouseButton mouseButton) {
        if (lastWindowCS == null) return;
        // получаем положение на экране
        Vector2d taskPos = ownCS.getCoords(pos, lastWindowCS);
        // если левая кнопка мыши, добавляем в первое множество
        if (mouseButton.equals(MouseButton.PRIMARY)) {
            addPoint(taskPos);
        }
    }


    /**
     * Добавить случайные точки
     *
     * @param cnt кол-во случайных точек
     */
    public void addRandomPoints(int cnt) {
        CoordinateSystem2i addGrid = new CoordinateSystem2i(30, 30);
        for (int i = 0; i < cnt; i++) {
            Vector2i gridPos = addGrid.getRandomCoords();
            Vector2d pos = ownCS.getCoords(gridPos, addGrid);
            // сработает примерно в половине случаев
            if (ThreadLocalRandom.current().nextBoolean())
                addPoint(pos);
            else
                addPoint(pos);
        }

    }

    /**
     * Очистить задачу
     */
    public void clear() {
        points.clear();
        solved = false;
    }

    /**
     * Решить задачу
     */
    public void solve() {
        Polygon p = new Polygon(new Vector2d(0, 0), new Vector2d(0, 0), new Vector2d(0, 0), new Vector2d(0, 0));
        for (int i = 0; i < points.size(); i++)
            for (int j = i; j < points.size(); j++)
                for (int k = j; k < points.size(); k++)
                    for (int l = k; l < points.size(); l++)
                    {
                        int c = 0;
                        int n = 0;
                        double x1 = points.get(i).pos.x;
                        double y1 = points.get(i).pos.y;
                        double x2 = points.get(j).pos.x;
                        double y2 = points.get(j).pos.y;
                        double x3 = points.get(k).pos.x;
                        double y3 = points.get(k).pos.y;
                        double x4 = points.get(l).pos.x;
                        double y4 = points.get(l).pos.y;
                        double x = Math.min(Math.min(x1, x2), Math.min(x3, x4));
*                       if ((Math.signum((x1 - x4) * (y2 - y1) - (x2 - x1) * (y1 - y4)) == Math.signum((x2 - x4) * (y3 - y2) - (x3 - x2) * (y2 - y4))) && (Math.signum((x1 - x4) * (y2 - y1) - (x2 - x1) * (y1 - y4)) == Math.signum((x3 - x4) * (y1 - y3) - (x1 - x3) * (y3 - y4))))
                        {
                            n = 1;
                        }
                        if ((Math.signum((x1 - x4) * (y2 - y1) - (x2 - x1) * (y1 - y4)) == Math.signum((x2 - x4) * (y3 - y2) - (x3 - x2) * (y2 - y4))) && (Math.signum((x1 - x4) * (y2 - y1) - (x2 - x1) * (y1 - y4)) == Math.signum((x3 - x4) * (y1 - y3) - (x1 - x3) * (y3 - y4))))
                        {
                            n = 1;
                        }
                        for (int h = 0; h < points.size(); h++)
                        {
                            double x0 = points.get(h).pos.x;
                            double y0 = points.get(h).pos.y;
                            if ((Math.signum((x1 - x0) * (y2 - y1) - (x2 - x1) * (y1 - y0)) == Math.signum((x2 - x0) * (y3 - y2) - (x3 - x2) * (y2 - y0))) && (Math.signum((x1 - x0) * (y2 - y1) - (x2 - x1) * (y1 - y0)) == Math.signum((x3 - x0) * (y1 - y3) - (x1 - x3) * (y3 - y0))))
                            {
                                c = 1;
                            }
                        }

                    }
        solved = true;
    }

    /**
     * Получить тип мира
     *
     * @return тип мира
     */
    public CoordinateSystem2d getOwnCS() {
        return ownCS;
    }

    /**
     * Получить название мира
     *
     * @return название мира
     */
    public ArrayList<Point> getPoints() {
        return points;
    }

    /**
     * Получить список пересечений
     *
     * @return список пересечений
     */
    @JsonIgnore
    public ArrayList<Point> getCrossed() {
        return crossed;
    }

    /**
     * Получить список разности
     *
     * @return список разности
     */
    @JsonIgnore
    public ArrayList<Point> getSingle() {
        return single;
    }

    /**
     * Отмена решения задачи
     */
    public void cancel() {
        solved = false;
    }

    /**
     * проверка, решена ли задача
     *
     * @return флаг
     */
    public boolean isSolved() {
        return solved;
    }
}
