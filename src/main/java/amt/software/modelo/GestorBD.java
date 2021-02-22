package amt.software.modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestorBD {

    public static final String NOMBRE_BD = "RegistrosPrueba.db";
    public static final String CADENA_CONEXION =
            "jdbc:sqlite:/home/axelfmt/Software/sistema-de-recibos-O-C/" + NOMBRE_BD;
    private static int CONTADOR_ID = 1;

    public static final String CREAR_ESTACIONAMIENTOS =
            "CREATE TABLE IF NOT EXISTS estacionamientos (" +
                    "id INTEGER NOT NULL UNIQUE, nombre TEXT NOT NULL UNIQUE, " +
                    "PRIMARY KEY(id))";

    public static final String CREAR_REGISTROS =
            "CREATE TABLE IF NOT EXISTS registros " +
                    "(id INTEGER NOT NULL UNIQUE, estacionamiento INTEGER NOT NULL, " +
                    "dia INTEGER NOT NULL, cliente TEXT NOT NULL, " +
                    "vehiculo TEXT NOT NULL, placas TEXT, tarifa REAL NOT NULL, clausula TEXT NOT NULL, " +
                    "PRIMARY KEY(id))";

    public static final String CREAR_SERIES =
            "CREATE TABLE IF NOT EXISTS series (serie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE)";

    public static final String CREAR_MESES =
            "CREATE TABLE IF NOT EXISTS meses (mes TEXT NOT NULL, PRIMARY KEY(mes))";

    public static final String CREAR_CLAUSULAS =
            "CREATE TABLE IF NOT EXISTS clausulas (id TEXT NOT NULL UNIQUE, texto TEXT NOT NULL UNIQUE, PRIMARY KEY(id))";

    public static final String INSERT_MES =
            "INSERT INTO meses (mes) VALUES(?)";

    public static final String LISTAR_MESES =
            "SELECT * FROM meses";

    public static final String LISTA_ESTACIONAMIENTOS =
            "SELECT * FROM estacionamientos";

    public static final String GET_ESTACIONAMIENTO_ID_POR_NOMBRE =
            "SELECT id FROM estacionamientos WHERE nombre = ?";

    public static final String LISTAR_ESTACIONAMIENTOS_NOMBRE =
            "SELECT nombre FROM estacionamientos";

    public static final String LISTAR_REGISTROS_POR_ESTACIONAMIENTO =
            "SELECT id, dia, cliente, vehiculo, placas, tarifa, clausula FROM registros " +
                    "WHERE estacionamiento = ? COLLATE NOCASE " +
                    "ORDER BY dia, cliente";

    public static final String LISTAR_CLAUSULAS_ID =
            "SELECT id FROM clausulas";

    public static final String GET_CLAUSULAS =
            "SELECT texto FROM clausulas WHERE id = ?";

    public static final String GET_MAX_SERIE =
            "SELECT max(serie) FROM series";

    public static final String NUEVO_ESTACIONAMIENTO =
            "INSERT INTO estacionamientos (nombre) VALUES (?)";

    public static final String NUEVO_REGISTRO =
            "INSERT INTO registros (estacionamiento, dia, cliente, vehiculo, placas, tarifa, clausula) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    public static final String INCREMENTA_SERIE =
            "INSERT INTO series (serie) VALUES (?)";

    public static final String NUEVA_CLAUSULA =
            "INSERT INTO clausulas (id, texto) VALUES (?, ?)";

    public static final String ACTUALIZAR_ESTACIONAMIENTO =
            "UPDATE estacionamientos SET nombre = ? WHERE nombre = ?";

    public static final String ACTUALIZAR_REGISTRO_INDIVIDUAL =
            "UPDATE registros SET dia = ?, cliente = ?, vehiculo = ?, placas = ?, tarifa = ?, clausula = ? " +
                    "WHERE id = ?";

    public static final String ACTUALIZAR_CLAUSULA =
            "UPDATE clausulas SET id = ?, texto = ? WHERE id = ?";

    public static final String ACTUALIZAR_TARIFAS =
            "UPDATE registros SET tarifa = ? WHERE tarifa = ? AND estacionamiento = ?";

    public static final String ELIMINAR_ESTACIONAMIENTO =
            "DELETE FROM estacionamientos WHERE nombre = ?";

    public static final String ELIMINAR_REGISTRO =
            "DELETE FROM registros WHERE id = ?";

    public static final String ELIMINAR_CLAUSULA =
            "DELETE FROM clausulas WHERE id = ?";

    private Connection conexion;
    private PreparedStatement listarRegistrosPorEstacionamiento;
    private PreparedStatement nuevoEstacionamiento;
    private PreparedStatement nuevoRegistro;
    private PreparedStatement nuevaClausula;
    private PreparedStatement actualizarEstacionamiento;
    private PreparedStatement actualizarRegistro;
    private PreparedStatement actualizarTarifas;
    private PreparedStatement actualizarClausula;
    private PreparedStatement eliminarEstacionamiento;
    private PreparedStatement eliminarRegistro;
    private PreparedStatement eliminarClausula;
    private PreparedStatement incrementarSerie;
    private PreparedStatement insertMes;
    private PreparedStatement getEstId;
    private PreparedStatement getClausula;

    private static GestorBD instancia = new GestorBD();

    private GestorBD() {
    }

    public static GestorBD getInstancia() {
        return instancia;
    }

    public boolean open() {
        try {
            conexion = DriverManager.getConnection(CADENA_CONEXION);
            crearTablas();
            listarRegistrosPorEstacionamiento = conexion.prepareStatement(LISTAR_REGISTROS_POR_ESTACIONAMIENTO);
            nuevoEstacionamiento = conexion.prepareStatement(NUEVO_ESTACIONAMIENTO);
            nuevoRegistro = conexion.prepareStatement(NUEVO_REGISTRO);
            nuevaClausula = conexion.prepareStatement(NUEVA_CLAUSULA);
            actualizarEstacionamiento = conexion.prepareStatement(ACTUALIZAR_ESTACIONAMIENTO);
            actualizarRegistro = conexion.prepareStatement(ACTUALIZAR_REGISTRO_INDIVIDUAL);
            actualizarClausula = conexion.prepareStatement(ACTUALIZAR_CLAUSULA);
            actualizarTarifas = conexion.prepareStatement(ACTUALIZAR_TARIFAS);
            eliminarEstacionamiento = conexion.prepareStatement(ELIMINAR_ESTACIONAMIENTO);
            eliminarRegistro = conexion.prepareStatement(ELIMINAR_REGISTRO);
            eliminarClausula = conexion.prepareStatement(ELIMINAR_CLAUSULA);
            incrementarSerie = conexion.prepareStatement(INCREMENTA_SERIE);
            insertMes = conexion.prepareStatement(INSERT_MES);
            getEstId = conexion.prepareStatement(GET_ESTACIONAMIENTO_ID_POR_NOMBRE);
            getClausula = conexion.prepareStatement(GET_CLAUSULAS);
            if (getMeses().size() == 0) {
                poblarMeses();
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean close() {
        try {
            if (actualizarRegistro != null) actualizarRegistro.close();
            if (actualizarEstacionamiento != null) actualizarEstacionamiento.close();
            if (actualizarClausula != null) actualizarClausula.close();
            if (nuevoRegistro != null) nuevoRegistro.close();
            if (nuevoEstacionamiento != null) nuevoEstacionamiento.close();
            if (nuevaClausula != null) nuevaClausula.close();
            if (listarRegistrosPorEstacionamiento != null) listarRegistrosPorEstacionamiento.close();
            if (actualizarTarifas != null) actualizarTarifas.close();
            if (eliminarEstacionamiento != null) eliminarEstacionamiento.close();
            if (eliminarRegistro != null) eliminarRegistro.close();
            if (eliminarClausula != null) eliminarClausula.close();
            if (incrementarSerie != null) incrementarSerie.close();
            if (insertMes != null) insertMes.close();
            if (getEstId != null) getEstId.close();
            if (getClausula != null) getClausula.close();
            if (conexion != null) conexion.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void poblarMeses() throws SQLException {
        String meses[] = new String[]{
                "Enero", "Febrero", "Marzo", "Abril",
                "Mayo", "Junio", "Julio", "Agosto",
                "Septiembre", "Octubre", "Noviembre",
                "Diciembre"
        };
        for (String mes : meses) {
            insertMes.setString(1, mes);
            insertMes.execute();
        }
    }

    public void crearTablas() {
        try (Statement declaracion = conexion.createStatement()) {
            declaracion.execute(CREAR_ESTACIONAMIENTOS);
            declaracion.execute(CREAR_REGISTROS);
            declaracion.execute(CREAR_SERIES);
            declaracion.execute(CREAR_MESES);
            declaracion.execute(CREAR_CLAUSULAS);
        } catch (SQLException e) {
        }
    }

    public List<String> getMeses() {
        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(LISTAR_MESES)) {
            List<String> meses = new ArrayList<>();
            while (resultSet.next()) {
                meses.add(resultSet.getString(1));
            }
            return meses;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<String> listarEstacionamientos() {
        try (Statement declaracion = conexion.createStatement();
             ResultSet resultados = declaracion.executeQuery(LISTAR_ESTACIONAMIENTOS_NOMBRE)) {
            List<String> estacionamientos = new ArrayList<>();
            while (resultados.next()) {
                estacionamientos.add(resultados.getString(1));
            }
            return estacionamientos;
        } catch (SQLException e) {
            return null;
        }
    }

    public List<String> listarClausulas() {
        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(LISTAR_CLAUSULAS_ID)) {
            List<String> clausulas = new ArrayList<>();
            while (resultSet.next()) {
                clausulas.add(resultSet.getString(1));
            }
            return clausulas;
        } catch (SQLException e) {
            return null;
        }
    }

    public String getClausulas(String id) {
        try {
            getClausula.setString(1, id);
            ResultSet resultSet = getClausula.executeQuery();
            String texto = resultSet.getString(1);
            return texto;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Registro> listarRegistrosPorEstacionamiento(Integer estacionamiento) {
        try {
            listarRegistrosPorEstacionamiento.setInt(1, estacionamiento);
            ResultSet resultados = listarRegistrosPorEstacionamiento.executeQuery();
            List<Registro> registros = new ArrayList<>();
            while (resultados.next()) {
                Registro registro = new Registro();
                registro.setId(resultados.getInt(1));
                registro.setDia(resultados.getInt(2));
                registro.setCliente(resultados.getString(3));
                registro.setVehiculo(resultados.getString(4));
                registro.setPlacas(resultados.getString(5));
                registro.setTarifa(resultados.getDouble(6));
                registro.setClausula(resultados.getString(7));
                registros.add(registro);
            }
            return registros;
        } catch (SQLException e) {
            return null;
        }
    }

    public void crearEstacionamiento(String nombre) throws SQLException {
        nuevoEstacionamiento.setString(1, nombre);
        nuevoEstacionamiento.execute();
    }

    public void crearRegistro(Integer estacionamiento, int dia, String cliente,
                              String vehiculo, String placas, double tarifa, String clausula) throws SQLException {
        try {
            conexion.setAutoCommit(false);
            nuevoRegistro.setInt(1, estacionamiento);
            nuevoRegistro.setInt(2, dia);
            nuevoRegistro.setString(3, cliente);
            nuevoRegistro.setString(4, vehiculo);
            nuevoRegistro.setString(5, placas);
            nuevoRegistro.setDouble(6, tarifa);
            nuevoRegistro.setString(7, clausula);
            nuevoRegistro.execute();
            conexion.commit();
        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException s) {
                throw new SQLException(s);
            }
            throw new SQLException(e);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                throw new SQLException(e);
            }
        }
    }

    public void crearClausula(String id, String text) throws SQLException {
        try {
            conexion.setAutoCommit(false);
            nuevaClausula.setString(1, id);
            nuevaClausula.setString(2, text);
            nuevaClausula.execute();
            conexion.commit();
        } catch (SQLException e1) {
            try {
                conexion.rollback();
            } catch (SQLException e2) {
                throw new SQLException();
            }
            throw new SQLException();
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e3) {
                throw new SQLException();
            }
        }
    }

    public void actualizarEstacionamiento(String actual, String nuevo) throws SQLException {
        if (nuevo.trim().length() > 0 && actual.trim().length() > 0) {
            actualizarEstacionamiento.setString(1, nuevo);
            actualizarEstacionamiento.setString(2, actual);
            actualizarEstacionamiento.execute();
        }
    }

    public void actualizarRegistroIndividual(int id, int dia, String cliente, String vehiculo,
                                             String placas, double tarifa, String clausula) throws SQLException {
        try {
            conexion.setAutoCommit(false);
            actualizarRegistro.setInt(1, dia);
            actualizarRegistro.setString(2, cliente);
            actualizarRegistro.setString(3, vehiculo);
            actualizarRegistro.setString(4, placas);
            actualizarRegistro.setDouble(5, tarifa);
            actualizarRegistro.setString(6, clausula);
            actualizarRegistro.setInt(7, id);
            actualizarRegistro.execute();
            conexion.commit();
        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException s) {
                throw new SQLException(s);
            }
            throw new SQLException(e);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                throw new SQLException(e);
            }
        }
    }

    public void actualizarClausula(String idNuevo, String texto, String idOriginal) throws SQLException {
        try {
            conexion.setAutoCommit(false);
            actualizarClausula.setString(1, idNuevo);
            actualizarClausula.setString(2, texto);
            actualizarClausula.setString(3, idOriginal);
            actualizarClausula.execute();
            conexion.commit();
        } catch (SQLException e1) {
            try {
                conexion.rollback();
            } catch (SQLException e2) {
                throw new SQLException(e2);
            }
            throw new SQLException(e1);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e3) {
                throw new SQLException(e3);
            }
        }
    }

    /* Para este se revisarán las condiciones en método de dialogo*/
    public void actualizaTarifas(Integer estacionamiento,
                                 double actual, double nueva) throws SQLException {
        actualizarTarifas.setDouble(1, nueva);
        actualizarTarifas.setDouble(2, actual);
        actualizarTarifas.setInt(3, estacionamiento);
        actualizarTarifas.execute();
    }

    public Integer getId(String nombre) {

        try {
            getEstId.setString(1, nombre);
            ResultSet resultSet = getEstId.executeQuery();
            Integer id = resultSet.getInt(1);
            return id;
        } catch (SQLException e) {
            return null;
        }
    }

    public void eliminaEstacionamiento(String estacionamiento) throws SQLException {
        eliminarEstacionamiento.setString(1, estacionamiento);
        eliminarEstacionamiento.execute();
    }

    public void eliminarRegistro(int id) throws SQLException {
        eliminarRegistro.setInt(1, id);
        eliminarRegistro.execute();
    }

    public void eliminarClausula(String id) throws SQLException {
        eliminarClausula.setString(1, id);
        eliminarClausula.execute();
    }

    public void incrementaSerie(int serie) {
        try {
            incrementarSerie.setInt(1, serie);
            incrementarSerie.execute();
        } catch (SQLException e) {
        }
    }

    public Integer getMaxSerie() {
        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_MAX_SERIE)) {
            Integer serie = resultSet.getInt(1);
            return serie;
        } catch (SQLException e) {
            return -1;
        }
    }
}
