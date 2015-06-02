public class Solution
{
    /*
     * Complete the function below.
     */

    static boolean isBalanced(String input)
    {
        char[] inputChars = input.toCharArray();
        int add = 0;

        for (int i = 0; i < inputChars.length; i++)
        {
            char a = inputChars[i];

            if (a >= '0' && a <= '9')
            {
                continue;
            }
            else if (a == '(')
            {
                add = isXX(inputChars, ')', i + 1);
                if (add == -1)
                {
                    return false;
                }
                else
                {
                    i += add + 1;
                    continue;
                }
            }
            else if (a == ')')
            {
                return false;
            }
            else if (a == '[')
            {
                add = isXX(inputChars, ']', i + 1);
                if (add == -1)
                {
                    return false;
                }
                else
                {
                    i += add + 1;
                    continue;
                }
            }
            else if (a == ']')
            {
                return false;
            }
            else
            {
                continue;
            }
        }

        return true;
    }

    static int isXX(char[] input, char key, int start)
    {
        char a;
        int add;
        for (int i = start; i < input.length; i++)
        {
            a = input[i];
            if (key == a)
            {
                return i - start + 1;
            }
            else if (a == '(')
            {
                add = isXX(input, ')', i + 1);
                if (add > 0)
                {
                    i += add + 1;
                    continue;
                }
                else
                {
                    return -1;
                }
            }
            else if (a == '[')
            {
                add = isXX(input, ']', i + 1);
                if (add > 0)
                {
                    i += add + 1;
                    continue;
                }
                else
                {
                    return -1;
                }
            }
            else if(a == ']' && key == ')')
            {
                return -1;
            }
            else if(a == ')' && key == ']')
            {
                return -1;
            }            
        }
        return -1;
    }

    public static void main(String args[])
    {
//        System.out.println(Solution.isBalanced("([52]12)"));
//        System.out.println(Solution.isBalanced("40[12 23[4 5(12))]]"));
//        System.out.println(Solution.isBalanced("40 12 58 44"));
        System.out.println(Solution.isBalanced("()([)]"));

    }
}
