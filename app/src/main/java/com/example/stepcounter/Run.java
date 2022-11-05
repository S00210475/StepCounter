package com.example.stepcounter;

import java.io.Serializable;
import java.util.Date;

public class Run implements Serializable {
    int ID;
    int Steps;
    int Seconds;
    double StepsMin;
    Date Date;
    int MetresRan;
    int Calories;
}
