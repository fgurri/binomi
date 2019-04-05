package com.creugrogasoft.biphone.utils;

/**
*
* @author fgurri
*/
public class RowItem {
   private String hora;
   private int total;
   private int ateses;
   private int no_ateses;
   private float percentatge_no_ateses;
   
   public RowItem(String hora, int total, int ateses, int no_ateses, float percentatge_no_ateses) {
       this.hora = hora;
       this.total = total;
       this.ateses = ateses;
       this.no_ateses = no_ateses;
       this.percentatge_no_ateses = percentatge_no_ateses;
   }
   
   public String getHora () {
       return this.hora;
   }
   
   public int getTotal () {
       return this.total;
   }
   
   public int getAteses () {
       return this.ateses;
   }
   public int getNoAteses () {
       return this.no_ateses;
   }
   
   public float getPercentatgeNoAteses () {
       return this.percentatge_no_ateses;
   }
}

