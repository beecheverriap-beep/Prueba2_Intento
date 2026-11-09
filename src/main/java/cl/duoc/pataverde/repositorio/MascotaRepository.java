package cl.duoc.pataverde.repositorio;

import cl.duoc.pataverde.dao.MascotaDAO;
import cl.duoc.pataverde.dao.MascotaDAOImpl;
import cl.duoc.pataverde.model.Gato;
import cl.duoc.pataverde.model.Mascota;
import cl.duoc.pataverde.model.Perro;

import java.util.ArrayList;
import java.util.List;

public class MascotaRepository {

    private final MascotaDAO mascotaDAO;
    private final List<Mascota> mascotas;

    public MascotaRepository() {
        this.mascotaDAO = new MascotaDAOImpl();
        this.mascotas = new ArrayList<>();
        cargarDesdeArchivos();
    }

    private void cargarDesdeArchivos() {
        mascotas.clear();
        mascotas.addAll(mascotaDAO.cargarPerros());
        mascotas.addAll(mascotaDAO.cargarGatos());
    }

    public List<Mascota> obtenerTodas() {
        return mascotas;
    }

    public void agregarMascota(Mascota mascota) {
        mascotas.add(mascota);
        guardarEnArchivos();
    }

    public void eliminarMascota(Mascota mascota) {
        mascotas.remove(mascota);
        guardarEnArchivos();
    }

    private void guardarEnArchivos() {
        List<Perro> perros = new ArrayList<>();
        List<Gato> gatos = new ArrayList<>();

        for (Mascota m : mascotas) {
            if (m instanceof Perro p) {
                perros.add(p);
            } else if (m instanceof Gato g) {
                gatos.add(g);
            }
        }

        mascotaDAO.guardarPerros(perros);
        mascotaDAO.guardarGatos(gatos);
    }
}