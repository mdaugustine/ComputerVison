/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invgamma;

import java.util.Scanner;
import Jama.Matrix;

/**
 *
 * @author michael
 */
public class InvGamma {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        int[] RGB = new int[3]; //nonlinear RGB array
        Scanner keyboard = new Scanner(System.in);
        String cont; // to continue loop
        
        do
        {
            System.out.println("Enter R");
            RGB[0] = keyboard.nextInt();
            System.out.println("Enter G");
            RGB[1] = keyboard.nextInt();
            System.out.println("Enter B");
            RGB[2] = keyboard.nextInt();
            
            double[] nlRGB = RGBtoNonlinear(RGB);
            double[] lRGB = InvGamma(nlRGB);
            double[] XYZ = RGBtoXYZ(lRGB);
            double[] xyY = XYZ_xyY(XYZ);
            double[] Luv = XYZtoLuv(XYZ, new double[] {0.95, 1.0, 1.09});
            
            System.out.println("\nNonlinear RGB");
            for (double v : nlRGB)
            {
                System.out.print(v + " ");
            }
            
            System.out.println("\nLinear RGB");
            for (double v : lRGB)
            {
                System.out.print(v + " ");
            }
            
            System.out.println("\nXYZ");
            for (double v : XYZ)
            {
                System.out.print(v + " ");
            }
            
            System.out.println("\nxyY");
            for (double v : xyY)
            {
                System.out.println(v + " ");
            }
            
            System.out.println("\nLuv");
            for (double v : Luv)
            {
                System.out.println(v + " ");
            }
            
            System.out.println("\nContinue? y/n");
            cont = keyboard.next();
        }
        while(!"n".equals(cont));
        
    }
    
    /**
     * This converts RGB to non linear RGB
     * @param RGB
     * @return nlRGB
     */
    public static double[] RGBtoNonlinear(int[] RGB)
    {
        double[] nlRGB = new double[3];
        
        for (int i = 0; i < 3; i++)
        {
            nlRGB[i] = (double)RGB[i] / 255;
        }
        
        return nlRGB;
    }
    
    /**
     * Receives linear RGB and performs Inverse Gamma Correction
     * @param nlRGB
     * @return lRGB
     */
    public static double[] InvGamma(double[] nlRGB)
    {
        double[] lRGB = new double[3]; //linear RGB array
        
        //iterate through array
        for (int i = 0; i < 3; i++)
        {
            if (nlRGB[i] < 0.03928)
            {
                lRGB[i] = nlRGB[i] / 12.92;
            }
            else
            {
                lRGB[i] = Math.pow(((nlRGB[i] + 0.055) / 1.055), 2.4);
            }
        }
        return lRGB; //return linear RGB array
    }
    
    /**
     * This method makes use of matrix operations to convert RGB to XYZ
     * @param lRGB
     * @return XYZ
     */
    public static double[] RGBtoXYZ(double[] lRGB)
    {
        //Create matricies from arrays
        Matrix mRGB = new Matrix(lRGB, 3);
        Matrix colorSpace = new Matrix(new double[][]
        {
            //The sRGB color space
            {0.412453, 0.35758, 0.180423},
            {0.212671, 0.71516, 0.072169},
            {0.019334, 0.119193, 0.950227},
        });
        
        //Return the color space times the linear RGB array
        return colorSpace.times(mRGB).getColumnPackedCopy();
        
    }
    
    /**
     * 
     * @param XYZ
     * @return 
     */
    public static double[] XYZ_xyY(double[] XYZ)
    {
        double[] xyY = new double[3];
        
        if(Double.isNaN(xyY[0] = XYZ[0] / (XYZ[0] + XYZ[1] + XYZ[2])))
        {
            xyY[0] = 0;
        }
        if(Double.isNaN(xyY[1] = XYZ[1] / (XYZ[0] + XYZ[1] + XYZ[2])))
        {
            xyY[1] = 0;
        }
        xyY[2] = XYZ[2];
        
        return xyY;
    }
    
    /**
     * 
     * @param XYZ
     * @param wXYZ
     * @return 
     */
    public static double[] XYZtoLuv(double[] XYZ, double[] wXYZ)
    {
        double Uw;
        double Vw;
        double t = XYZ[1];
        double[] Luv = new double[3];
        double uprime;
        double vprime;
        
        if (Double.isNaN(Uw = (4 * wXYZ[0]) / (wXYZ[0] + (15 * wXYZ[1]) + (3 * wXYZ[2]))))
        {
            Uw = 0;
        }
        if (Double.isNaN(Vw = (9 * wXYZ[0]) / (wXYZ[0] + (15 * wXYZ[1]) + (3 * wXYZ[2]))))
        {
            Vw = 0;
        }
        
        if (t > 0.008856)
        {
            Luv[0] = (116 * Math.pow(t, (1/3))) - 16;
        }
        else
        {
            Luv[0] = 903.3 * t;
        }
        
        double d = XYZ[0] + (15 * XYZ[1]) + (3 * XYZ[2]);
        
        if (Double.isNaN(uprime = (4 * XYZ[0]) / d))
        {
            uprime = 0;
        }
        if (Double.isNaN(vprime = (9 * XYZ[1]) / d))
        {
            vprime = 0;
        }
        
        Luv[1] = 13 * Luv[0] * (uprime - Uw);
        Luv[2] = 13 * Luv[0] * (vprime - Vw);
        
        return Luv;
    }
}
