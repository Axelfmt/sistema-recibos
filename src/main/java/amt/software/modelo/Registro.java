package amt.software.modelo;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Registro {

    private SimpleIntegerProperty id;
    private SimpleStringProperty cliente;
    private SimpleStringProperty vehiculo;
    private SimpleStringProperty placas;
    private SimpleDoubleProperty tarifa;
    private SimpleIntegerProperty dia;
    private SimpleStringProperty clausula;


    public Registro() {
        this.id = new SimpleIntegerProperty();
        this.cliente = new SimpleStringProperty();
        this.vehiculo = new SimpleStringProperty();
        this.placas = new SimpleStringProperty();
        this.tarifa = new SimpleDoubleProperty();
        this.dia = new SimpleIntegerProperty();
        this.clausula = new SimpleStringProperty();
    }

    public int getId() {return id.get();}
    public void setId(int id) {this.id.set(id);}

    public String getCliente() {return cliente.get();}
    public void setCliente(String cliente) {this.cliente.set(cliente);}

    public String getVehiculo() {return vehiculo.get();}
    public void setVehiculo(String vehiculo) {this.vehiculo.set(vehiculo);}

    public String getPlacas() {return placas.get();}
    public void setPlacas(String placas) {this.placas.set(placas);}

    public double getTarifa() {return tarifa.get();}
    public void setTarifa(double tarifa) {this.tarifa.set(tarifa);}

    public int getDia() {return dia.get();}
    public void setDia(int dia) {this.dia.set(dia);}

    public String getClausula() {return clausula.get();}
    public void setClausula(String clausula) {this.clausula.set(clausula);}


}
