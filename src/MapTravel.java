import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MapTravel
{
    // size of map
    private final static int MAP_SIZE = 10;

    // map array
    private int[][] map = new int[MAP_SIZE][MAP_SIZE];

    // direction result
    private Stack<Direction> result = new Stack<Direction>();

    // tried positions
    List<Integer> listX = new ArrayList<Integer>();
    List<Integer> listY = new ArrayList<Integer>();

    enum Direction
    {
        UP, DOWN, LEFT, RIGHT
    };

    private boolean isPass(int val)
    {
        return val == 1;
    }

    private boolean goUp(int x, int y)
    {
        if (y < 0 || y - 1 < 0)
        {
            return false;
        }

        return isPass(map[x][y - 1]);
    }

    private boolean goLeft(int x, int y)
    {
        if (x < 0 || x - 1 < 0)
        {
            return false;
        }

        return isPass(map[x - 1][y]);
    }

    private boolean goRight(int x, int y)
    {
        if (x + 1 > MAP_SIZE - 1)
        {
            return false;
        }

        return isPass(map[x + 1][y]);
    }

    private boolean goDown(int x, int y)
    {
        if (y + 1 > MAP_SIZE - 1)
        {
            return false;
        }

        return isPass(map[x][y + 1]);
    }

    private boolean isTriedBefore(int x, int y)
    {
        for (int i = 0; i < listX.size(); i++)
        {
            if (x == listX.get(i) && y == listY.get(i))
            {
                return true;
            }
        }
        return false;
    }

    private boolean go(int x, int y)
    {
        boolean ret = false;
        Direction move = Direction.RIGHT;

        if (x == MAP_SIZE - 1 && y == MAP_SIZE - 1)
        {
            return true;
        }

        if (isTriedBefore(x, y))
        {
            return false;
        }
        else
        {
            listX.add(x);
            listY.add(y);
        }

        if (goRight(x, y))
        {
            ret = go(x + 1, y);
        }

        if (!ret && goDown(x, y))
        {
            move = Direction.DOWN;
            ret = go(x, y + 1);
        }

        if (!ret && goLeft(x, y))
        {
            move = Direction.LEFT;
            ret = go(x - 1, y);
        }

        if (!ret && goUp(x, y))
        {
            move = Direction.UP;
            ret = go(x, y - 1);
        }

        if (ret)
        {
            result.push(move);
            return true;
        }

        return false;
    }

    private void makeMap()
    {
        Random rd = new Random();

        for (int i = 0; i < MAP_SIZE; i++)
        {
            for (int j = 0; j < MAP_SIZE; j++)
            {
                map[i][j] = rd.nextInt(3) == 1 ? 0 : 1;
            }
        }

        map[0][0] = 1;
        map[MAP_SIZE - 1][MAP_SIZE - 1] = 1;

        for (int j = 0; j < MAP_SIZE; j++)
        {
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < MAP_SIZE; i++)
            {
                temp.append(map[i][j]);
            }
            System.out.println(temp.toString());
        }
    }

    public void print()
    {
        StringBuilder sb = new StringBuilder();
        while (!result.isEmpty())
            switch (result.pop())
            {
            case RIGHT:
                sb.append("ср");
                break;
            case DOWN:
                sb.append("об");
                break;
            case LEFT:
                sb.append("вС");
                break;
            case UP:
                sb.append("ио");
                break;
            default:
                System.out.println("shit happens!");
                return;
            }
        System.out.println(sb.toString());

    }

    private void startTravel()
    {
        if (!go(0, 0))
        {
            System.out.println("Can't make the way out.");
        }
        else
        {
            print();
        }
    }

    public void start()
    {
        makeMap();
        startTravel();
    }

    public static void main(String args[])
    {
        MapTravel tpt = new MapTravel();
        tpt.start();
    }

}
