
package classes;


public class Item {
    private double valor;
    private int peso;
   
    public Item(){
        
    }
    
    public void setValor(double valor){
        this.valor = valor;
    }
    public double getValor(){
        return this.valor;
    } 
    
    public void setPeso(int peso){
        this.peso = peso;
    }
    public int getPeso(){
        return peso;
    }
    public double aresta(){
        if(this.peso == 0){
            return 0;
        }
        else{
            return valor / peso;
        }
    }
}
