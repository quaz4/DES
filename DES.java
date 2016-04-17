import java.util.Arrays; //used for the toString method
import java.lang.*;

public class DES
{
	//DES uses many tables, declared here so they remain in memory
	//Initial permutation table
    private static final int[] IP =
    { 
	    58, 50, 42, 34, 26, 18, 10, 2,
	    60, 52, 44, 36, 28, 20, 12, 4,
	    62, 54, 46, 38, 30, 22, 14, 6,
	    64, 56, 48, 40, 32, 24, 16, 8,
	    57, 49, 41, 33, 25, 17, 9,  1,
	    59, 51, 43, 35, 27, 19, 11, 3,
	    61, 53, 45, 37, 29, 21, 13, 5,
	    63, 55, 47, 39, 31, 23, 15, 7
	};

	//Inverse Initial permutation table
	private static final int[] IPINVERSE =
	{
        40, 8, 48, 16, 56, 24, 64, 32,
        39, 7, 47, 15, 55, 23, 63, 31,
        38, 6, 46, 14, 54, 22, 62, 30,
        37, 5, 45, 13, 53, 21, 61, 29,
        36, 4, 44, 12, 52, 20, 60, 28,
        35, 3, 43, 11, 51, 19, 59, 27,
        34, 2, 42, 10, 50, 18, 58, 26,
        33, 1, 41, 9, 49, 17, 57, 25
    };

    //Permutation PC1, removes parity bits, changing from 64 to 56
    private static final int[] PC1 =
    {
        57, 49, 41, 33, 25, 17, 9,
        1, 58, 50, 42, 34, 26, 18,
        10, 2, 59, 51, 43, 35, 27,
        19, 11, 3, 60, 52, 44, 36,
        63, 55, 47, 39, 31, 23, 15,
        7, 62, 54, 46, 38, 30, 22,
        14, 6, 61, 53, 45, 37, 29,
        21, 13, 5, 28, 20, 12, 4
    };

    //Permuation PC2
    private static final int[] PC2 =
    {
        14, 17, 11, 24, 1, 5,
        3, 28, 15, 6, 21, 10,
        23, 19, 12, 4, 26, 8,
        16, 7, 27, 20, 13, 2,
        41, 52, 31, 37, 47, 55,
        30, 40, 51, 45, 33, 48,
        44, 49, 39, 56, 34, 53,
        46, 42, 50, 36, 29, 32
    };

    //S-box tables
    private static final int[][] SBOX = 
    {
    	{
	        14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
	        0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
	        4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
	        15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13
    	}, 
    	{
	        15, 1, 8, 14, 6, 11, 3, 4, 9, 7,  2, 13, 12, 0, 5, 10,
	        3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
	        0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6,  9, 3, 2, 15,
	        13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
	    }, 
	    {
	        10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
	        13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
	        13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
	        1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
	    }, 
	    {
	        7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
	        13, 8, 11, 5, 6,  15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
	        10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
	        3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14
	    }, 
	    {
	        2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
	        14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
	        4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
	        11, 8, 12, 7,  1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
	    }, 
	    {
	        12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
	        10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
	        9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
	        4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
	    }, 
	    {
	        4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
	        13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
	        1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
	        6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
	    }, 
	    {
	        13, 2, 8, 4, 6, 15,11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
	        1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
	        7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
	        2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
	    }
	};

	//Expansion table
	private static final int[] E =
	{
        32, 1, 2, 3, 4, 5,
        4, 5, 6, 7, 8, 9,
        8, 9, 10, 11, 12, 13,
        12, 13, 14, 15, 16, 17,
        16, 17, 18, 19, 20, 21,
        20, 21, 22, 23, 24, 25,
        24, 25, 26, 27, 28, 29,
        28, 29, 30, 31, 32, 1
    };

    //Permutation in F function
    private static final int[] P =
    {
        16, 7, 20, 21,
        29, 12, 28, 17,
        1, 15, 23, 26,
        5, 18, 31, 10,
        2, 8, 24, 14,
        32, 27, 3, 9,
        19, 13, 30, 6,
        22, 11, 4, 25
    };

    //Subkey shift table
    private static final int[] keyShifts = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };

	public static void main(String[] args)
	{
		/////////////////////////////////////////////TEST INPUT

		int[] test = 
		{
			1,2,3,4,5,6,7,8,
			9,10,11,12,13,14,15,16,
			17,18,19,20,21,22,23,24,
			25,26,27,28,29,30,31,32,
			33,34,35,36,37,38,39,40,
			41,42,43,44,45,46,47,48,
			49,50,51,52,53,54,55,56,
			57,58,59,60,61,62,63,64
		};

		/////////////////////////////////////////////

		//test = permutation(test, IP, 64); //Initial permuation

		System.out.println(test[0]);

		switchFunction(test);

	}

	//Function that implements bit permuations
    //in var is data, table var is where to move bits to
    //outSize is size of output array
    private static int[] permutation(int[] in, int[] table, int outSize)
    {
    	int[] output = new int[outSize];

    	int length = table.length;

    	for(int i = 0; i < length; i++)
    	{
    		//Finds what bit to swap and adds to output array
    		output[i] = (in[table[i]-1]);
    	}

    	return output;
    }

    //Same method as above except implemented with a boolean array
    private static boolean[] permutation(boolean[] in, int[] table, int outSize)
	{
    	boolean[] output = new boolean[outSize];

    	int length = table.length;

    	for(int i = 0; i < length; i++)
    	{
    		//Finds what bit to swap and adds to output array
    		output[i] = (in[table[i]-1]);
    	}

    	return output;
	}

	private static boolean[][] keyGeneration(int[] key)
	{
		key = permutation(key, PC1, 56);

		//Convert to binary array
		boolean[][] converted = new boolean[56][8];

		for(int i = 0; i < 56; i++)
		{
			converted[i] = toBinary(key[i]);
		}

		//split left
		int j = 0;
		int k = 1;
		boolean[] left = new boolean[28];
		boolean[] right = new boolean[28];

		for(int i = 1; k < 29; i++)
		{
			left[k-1] = converted[j][i-1];

			if(i % 8 == 0)
			{
				j++;
				i = 0;
			}

			k++;
		}

		//split right
		k = 1;
		for(int i = 5; k < 29; i++)
		{
			right[k-1] = converted[j][i-1];

			if(i % 8 == 0)
			{
				j++;
				i = 0;
			}

			k++;
		}

		boolean[][] roundKeys = new boolean[16][48];

		for(int i = 0; i < 16; i++)
		{
			//shift left left
			left = shiftLeft(left, keyShifts[i]);

			//shift right left
			right = shiftLeft(left, keyShifts[i]);

			//Combine arrays
			boolean[] combined = new boolean[56];

			System.arraycopy(left, 0, combined, 0, left.length);
			System.arraycopy(right, 0, combined, 28, right.length);

			//compression P
			combined = permutation(combined, PC2, 48);

			//Save
			roundKeys[i] = combined;
		}

		return roundKeys;
	}

	private static boolean[] shiftLeft(boolean[] array, int numShifts)
	{
		boolean temp;

		for(int i = 0; i < numShifts; i++)
		{
			temp = array[0];

			for(int k = 0; k < array.length-1; k++)
			{
				System.out.println("array[" + k + "] = " + array[k]);
				array[k] = array[k+1];
			}

			array[array.length-1] = temp;
		}

		return array;
	}

	private static void switchFunction(int[] input)
	{
		//Convert to binary array
		boolean[][] text = new boolean[64][8];

		for(int i = 0; i < 64; i++)
		{
			text[i] = toBinary(input[i]);
		}

		System.out.println(Arrays.toString(text[0]));

		//Split into two distinct functions
		
	}

	private static int[] Fk(int[] in, int[] roundKey)
	{


		return null;
	}

	private static int[] sBox()
	{


		return null;
	}

	//Converts an int to binary, represented by a boolean array
	private static boolean[] toBinary(int in)
	{
		boolean[] output = new boolean[8];
		int divisor = 128;

		for(int i = 0; i < 8; i++)
		{
			if(in == 0)
			{
				output[i] = false;
			}
			else if(in % divisor == in)
			{
				output[i] = false;
			}
			else
			{
				output[i] = true;
				in = in - divisor;
			}

			divisor = divisor/2;
		}

		return output;
	}
}