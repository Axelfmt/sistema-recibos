package amt.software.modelo;

public class Estacionamiento {

    private Integer id;
    private String nombre;

    public Estacionamiento(Integer id, String nombre) {

        this.id = id;
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
